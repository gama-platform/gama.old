/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.runtime;

import java.util.*;
import msi.gama.common.util.*;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import com.google.common.collect.Lists;

/**
 * Written by drogoul Modified on 23 nov. 2009
 * 
 * @todo Description
 */
public class GAMA {

	public final static String VERSION = "GAMA 1.6";
	public static final String _FATAL = "fatal";
	public static final String _WARNINGS = "warnings";
	public static boolean REVEAL_ERRORS_IN_EDITOR = true;
	public static boolean TREAT_WARNINGS_AS_ERRORS = false;

	public final static FrontEndController controller = new FrontEndController(new FrontEndScheduler());
	public static final int NUMBER_OF_AGENTS_IN_MENUS = 50;

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

	public static IExperimentSpecies getExperiment() {
		return controller.getExperiment();
	}

	public static SimulationClock getClock() {
		final IScope scope = getDefaultScope();
		if ( scope == null ) { return new SimulationClock(); }
		return scope.getClock();
	}

	public static RandomUtils getRandom() {
		if ( controller.getExperiment() == null || controller.getExperiment().getAgent() == null ) { return RandomUtils
			.getDefault(); }
		return controller.getExperiment().getAgent().getRandomGenerator();
	}

	public static IModel getModel() {
		if ( controller.getExperiment() == null ) { return null; }
		return controller.getExperiment().getModel();
	}

	/**
	 * 
	 * Exception and life-cycle related utilities
	 * 
	 */

	public static boolean reportError(final GamaRuntimeException g, final boolean shouldStopSimulation) {
		// Returns whether or not to continue
		GuiUtils.runtimeError(g);
		if ( controller.getExperiment() == null ) { return false; }
		boolean isError = !g.isWarning() || TREAT_WARNINGS_AS_ERRORS;
		boolean shouldStop = isError && shouldStopSimulation && REVEAL_ERRORS_IN_EDITOR;
		// if ( shouldStop ) {
		// controller.userPause();
		// return false;
		// }
		return !shouldStop;
	}

	public static void reportAndThrowIfNeeded(final IScope scope, final GamaRuntimeException g,
		final boolean shouldStopSimulation) throws GamaRuntimeException {
		if ( scope != null ) {
			String name = scope.getAgentScope().getName();
			if ( !g.getAgentsNames().contains(name) ) {
				g.addAgent(name);
			}
		}
		boolean shouldStop = !reportError(g, shouldStopSimulation);
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
		final IScope scope = getDefaultScope();
		if ( scope != null ) { return scope.copy(); }
		return null;
	}

	private static IScope getDefaultScope() {
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
		if ( scope == null ) { return null; }
		// if ( scope == null ) { throw GamaRuntimeException.error("Impossible to obtain a scope"); } // Exception?
		try {
			final T result = r.run(scope);
			return result;
		} finally {
			releaseScope(scope);
		}
	}

}
