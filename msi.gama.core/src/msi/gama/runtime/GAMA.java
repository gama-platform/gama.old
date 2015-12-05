/*********************************************************************************************
 *
 *
 * 'GAMA.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.runtime;

import java.util.*;
import com.google.common.collect.Lists;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 23 nov. 2009
 *
 * @todo Description
 */
public class GAMA {

	private static GAMA instance;

	public final static String VERSION = "GAMA 1.7";
	// public static final String _FATAL = "fatal";
	public static final String _WARNINGS = "warnings";
	// Minimum duration of a cycle in seconds
	private static double CYCLE_DELAY = 0d;

	public final static FrontEndController controller = new FrontEndController(new FrontEndScheduler());

	// hqnghi: add several controllers to have multi-thread experiments
	private final static Map<String, FrontEndController> controllers =
		new TOrderedHashMap<String, FrontEndController>();

	public static FrontEndController getController(final String ctrlName) {
		return controllers.get(ctrlName);
	}

	public static Map<String, FrontEndController> getControllers() {
		return controllers;
	}

	public static void addController(final String ctrlName, final FrontEndController fec) {
		controllers.put(ctrlName, fec);
	}

	// end-hqnghi
	/**
	 *
	 * Access to experiments and their components
	 *
	 */

	public static SimulationAgent getSimulation() {
		if ( controller.getExperiment() == null ) { return null; }
		return controller.getExperiment().getCurrentSimulation();
	}

	public static List<IPopulation> getModelPopulations() {
		final SimulationAgent sim = getSimulation();
		if ( sim == null ) { return Collections.EMPTY_LIST; }
		final List<ISpecies> species = new ArrayList(getModel().getAllSpecies().values());
		final List<IPopulation> populations = Lists.newArrayList();
		for ( final ISpecies s : species ) {
			if ( !s.getDescription().isBuiltIn() ) {
				final IPopulation p = sim.getPopulationFor(s);
				if ( p != null ) { // Multiple scale population
					populations.add(p);
				}
			}
		}
		// Collections.sort(populations);
		return populations;

	}

	public static IExperimentPlan getExperiment() {
		return controller.getExperiment();
	}

	public static SimulationClock getClock() {
		final IScope scope = getRuntimeScope();
		if ( scope == null ) { return new SimulationClock(); }
		return scope.getClock();
	}

	// public static RandomUtils getRandom() {
	// if ( controller.getExperiment() == null || controller.getExperiment().getAgent() == null ) { return RandomUtils
	// .getDefault(); }
	// return controller.getExperiment().getAgent().getRandomGenerator();
	// }

	public static IModel getModel() {
		if ( controller.getExperiment() == null ) { return null; }
		return controller.getExperiment().getModel();
	}

	/**
	 *
	 * Exception and life-cycle related utilities
	 *
	 */

	public static boolean reportError(final IScope scope, final GamaRuntimeException g,
		final boolean shouldStopSimulation) {
		// Returns whether or not to continue
		if ( scope != null && !scope.reportErrors() ) {
			g.printStackTrace();
			return true;
		}
		GuiUtils.runtimeError(g);
		if ( controller.getExperiment() == null || controller.getExperiment().getAgent() == null ) { return false; }
		boolean isError = !g.isWarning() || controller.getExperiment().getAgent().getWarningsAsErrors();
		boolean shouldStop = isError && shouldStopSimulation && GamaPreferences.CORE_REVEAL_AND_STOP.getValue();
		// if ( shouldStop ) {
		// controller.userPause();
		// return false;
		// }
		return !shouldStop;
	}

	public static void reportAndThrowIfNeeded(final IScope scope, final GamaRuntimeException g,
		final boolean shouldStopSimulation) throws GamaRuntimeException {
		if ( scope != null && !scope.reportErrors() ) {
			g.printStackTrace();
			return;
		}
		if ( scope != null && scope.getAgentScope() != null ) {
			String name = scope.getAgentScope().getName();
			if ( !g.getAgentsNames().contains(name) ) {
				g.addAgent(name);
			}
		}
		boolean shouldStop = !reportError(scope, g, shouldStopSimulation);
		if ( shouldStop ) {
			controller.userPause();
			throw g;
		}
	}

	public static void shutdown() {
		controller.shutdown();
	}

	public static boolean isPaused() {
		return controller.getScheduler().paused;

	}

	/**
	 *
	 * Scoping utilities
	 *
	 */

	public static void releaseScope(final IScope scope) {
		if ( scope != null ) {
			scope.clear();
		}
	}

	public static IScope obtainNewScope() {
		final IScope scope = getRuntimeScope();
		if ( scope != null ) { return scope.copy(); }
		return null;
	}

	public static IScope getRuntimeScope() {
		if ( controller.getExperiment() == null ) { return null; }
		final ExperimentAgent a = controller.getExperiment().getAgent();
		if ( a == null || a.dead() ) { return controller.getExperiment().getExperimentScope(); }
		final SimulationAgent s = (SimulationAgent) a.getSimulation();
		if ( s == null || s.dead() ) { return a.getScope(); }
		return s.getScope();
	}

	public static interface InScope<T> {

		public abstract static class Void implements InScope {

			@Override
			public Object run(final IScope scope) {
				process(scope);
				return null;
			}

			public abstract void process(IScope scope);
		}

		T run(IScope scope);
	}

	public static <T> T run(final InScope<T> r) {
		final IScope scope = obtainNewScope();
		// if ( scope == null ) { return null; }
		// if ( scope == null ) { throw GamaRuntimeException.error("Impossible to obtain a scope"); } // Exception?
		try {
			final T result = r.run(scope);
			return result;
		} finally {
			releaseScope(scope);
		}
	}

	public static double getDelayInMilliseconds() {
		return CYCLE_DELAY * 1000;
	}

	public static void setDelayFromUI(final double newDelayInMilliseconds) {
		CYCLE_DELAY = newDelayInMilliseconds / 1000;
		// getExperiment().getAgent().setMinimumDuration(CYCLE_DELAY);
	}

	public static void setDelayFromExperiment(final double newDelayInSeconds) {
		CYCLE_DELAY = newDelayInSeconds;
		GuiUtils.updateSpeedDisplay(CYCLE_DELAY * 1000, false);
	}
}
