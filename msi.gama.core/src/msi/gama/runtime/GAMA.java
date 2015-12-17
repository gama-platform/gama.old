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
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 23 nov. 2009
 *
 * In GUI Mode, for the moment, only one controller allowed at a time (controllers[0])
 *
 * @todo Description
 */
public class GAMA {

	public final static String VERSION = "GAMA 1.7";
	// public static final String _FATAL = "fatal";
	public static final String _WARNINGS = "warnings";

	// private final static ExperimentController controller = new ExperimentController(new FrontEndScheduler());

	// hqnghi: add several controllers to have multi-thread experiments
	private final static List<IExperimentController> controllers = new ArrayList<IExperimentController>();

	// Needed by RCP for displaying the simulation state
	public static ISimulationStateProvider state = null;
	public final static String PAUSED = "STOPPED";
	public final static String RUNNING = "RUNNING";
	public final static String NOTREADY = "NOTREADY";
	public final static String NONE = "NONE";

	public static List<IExperimentController> getControllers() {
		return controllers;
	}

	public static IExperimentController getFrontmostController() {
		return controllers.isEmpty() ? null : controllers.get(0);
	}

	/**
	 * New control architecture
	 */

	/**
	 * Create a GUI experiment that replaces the current one (if any)
	 * @param id
	 * @param model
	 */
	public static void runGuiExperiment(final String id, final IModel model) {
		final IExperimentPlan newExperiment = model.getExperiment(id);
		if ( newExperiment == null ) { return; }
		IExperimentController controller = getFrontmostController();
		if ( controller != null ) {
			IExperimentPlan existingExperiment = controller.getExperiment();
			if ( existingExperiment != null ) {
				controller.getScheduler().pause();
				if ( !GuiUtils.confirmClose(existingExperiment) ) { return; }
			}
		}
		controller = newExperiment.getController();
		if ( controllers.size() > 0 ) {
			closeAllExperiments(false);
		}

		GuiUtils.openSimulationPerspective(true);

		controllers.add(controller);

		controller.userOpen();

	}

	/**
	 * Add a sub-experiment to the current GUI experiment
	 * @param id
	 * @param model
	 */
	public static void addGuiExperiment(final IExperimentPlan experiment) {

	}

	public static void openExperiment(final IExperimentPlan experiment) {
		experiment.getController().directOpenExperiment();
	}

	/**
	 * Add an experiment
	 * @param id
	 * @param model
	 */
	public static synchronized IExperimentPlan addHeadlessExperiment(final IModel model, final String expName,
		final ParametersSet params, final Long seed) {

		ExperimentPlan currentExperiment = (ExperimentPlan) model.getExperiment(expName);
		if ( currentExperiment == null ) { throw GamaRuntimeException
			.error("Experiment " + expName + " cannot be created"); }
		for ( Map.Entry<String, Object> entry : params.entrySet() ) {
			currentExperiment.setParameterValue(currentExperiment.getExperimentScope(), entry.getKey(),
				entry.getValue());
		}
		currentExperiment.createAgent();
		if ( seed != null ) {
			currentExperiment.getAgent().setSeed(Double.longBitsToDouble(seed));
		}

		currentExperiment.getAgent().createSimulation(new ParametersSet(), true);

		controllers.add(currentExperiment.getController());
		/// FIXME ADD IT SOMEWHERE
		return currentExperiment;

	}

	public static void closeFrontmostExperiment() {
		IExperimentController controller = getFrontmostController();
		if ( controller == null || controller.getExperiment() == null ) { return; }
		controller.close();
		controllers.remove(controller);
	}

	public static void closeExperiment(final IExperimentPlan experiment) {
		if ( experiment == null ) { return; }
		IExperimentController controller = experiment.getController();
		if ( controller == null ) { return; }
		controller.close();
		controllers.remove(controller);
	}

	public static void closeAllExperiments(final boolean andOpenModelingPerspective) {
		for ( IExperimentController controller : controllers ) {
			controller.close();
		}
		controllers.clear();
		GuiUtils.closeSimulationViews(andOpenModelingPerspective);
		// GuiUtils.wipeExperiments();
	}

	/**
	 *
	 * Access to experiments and their components
	 *
	 */

	public static SimulationAgent getSimulation() {
		IExperimentController controller = getFrontmostController();
		if ( controller == null || controller.getExperiment() == null ) { return null; }
		return controller.getExperiment().getCurrentSimulation();
	}

	// public static List<IPopulation> getModelPopulations() {
	// final SimulationAgent sim = getSimulation();
	// if ( sim == null ) { return Collections.EMPTY_LIST; }
	// final List<ISpecies> species = new ArrayList(getModel().getAllSpecies().values());
	// final List<IPopulation> populations = Lists.newArrayList();
	// for ( final ISpecies s : species ) {
	// if ( !s.getDescription().isBuiltIn() ) {
	// final IPopulation p = sim.getPopulationFor(s);
	// if ( p != null ) { // Multiple scale population
	// populations.add(p);
	// }
	// }
	// }
	// // Collections.sort(populations);
	// return populations;
	//
	// }

	public static IExperimentPlan getExperiment() {
		IExperimentController controller = getFrontmostController();
		if ( controller == null ) { return null; }
		return controller.getExperiment();
	}

	// public static SimulationClock getClock() {
	// final IScope scope = getRuntimeScope();
	// if ( scope == null ) { return null; }
	// // if ( scope == null ) { return new SimulationClock(); }
	// return scope.getClock();
	// }

	// public static RandomUtils getRandom() {
	// if ( controller.getExperiment() == null || controller.getExperiment().getAgent() == null ) { return RandomUtils
	// .getDefault(); }
	// return controller.getExperiment().getAgent().getRandomGenerator();
	// }

	public static IModel getModel() {
		IExperimentController controller = getFrontmostController();
		if ( controller == null || controller.getExperiment() == null ) { return null; }
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
		IExperimentController controller = getFrontmostController();
		if ( controller == null || controller.getExperiment() == null ||
			controller.getExperiment().getAgent() == null ) { return false; }
		boolean isError = !g.isWarning() || controller.getExperiment().getAgent().getWarningsAsErrors();
		boolean shouldStop = isError && shouldStopSimulation && GamaPreferences.CORE_REVEAL_AND_STOP.getValue();
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
			IExperimentController controller = getFrontmostController();
			if ( controller == null ) { return; }
			controller.userPause();
			throw g;
		}
	}

	// public static void shutdownAllExperiments() {
	// for ( IExperimentController controller : controllers ) {
	// controller.close();
	// }
	// controllers.clear();
	// }

	public static void startPauseFrontmostExperiment() {
		for ( IExperimentController controller : controllers ) {
			controller.startPause();
		}
	}

	public static void stepFrontmostExperiment() {
		for ( IExperimentController controller : controllers ) {
			controller.userStep();
		}
	}

	public static void pauseFrontmostExperiment() {
		for ( IExperimentController controller : controllers ) {
			controller.directPause();
		}
	}

	public static void reloadFrontmostExperiment() {
		IExperimentController controller = getFrontmostController();
		if ( controller != null ) {
			controller.userReload();
		}
	}

	public static void startFrontmostExperiment() {
		IExperimentController controller = getFrontmostController();
		if ( controller != null ) {
			controller.userStart();
		}
	}

	public static boolean isPaused() {
		IExperimentController controller = getFrontmostController();
		if ( controller == null || controller.getExperiment() == null ) { return true; }
		return controller.getScheduler().paused;

	}

	/**
	 *
	 */
	// public static void InterruptFrontmostExperiment() {
	// IExperimentController controller = getFrontmostController();
	// if ( controller != null ) {
	// controller.close();
	// // controller.userInterrupt();
	// }
	// controllers.remove(controller);
	// }

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
		IExperimentController controller = getFrontmostController();
		if ( controller == null || controller.getExperiment() == null ) { return new TemporaryScope(); }
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
		try {
			final T result = r.run(scope);
			return result;
		} finally {
			releaseScope(scope);
		}
	}

	/**
	 *
	 * Simulation state related utilities for Eclipse GUI
	 *
	 */

	public static String getFrontmostSimulationState() {
		IExperimentController controller = getFrontmostController();
		if ( controller == null ) {
			return NONE;
		} else if ( controller.getScheduler().paused ) { return PAUSED; }
		return RUNNING;
	}

	public static void updateSimulationState(final String forcedState) {
		if ( state != null ) {
			GuiUtils.run(new Runnable() {

				@Override
				public void run() {
					state.updateStateTo(forcedState);
				}
			});
		}
	}

	public static void updateSimulationState() {
		updateSimulationState(getFrontmostSimulationState());
	}

}
