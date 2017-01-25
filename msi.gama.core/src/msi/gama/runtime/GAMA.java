/*********************************************************************************************
 *
 * 'GAMA.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gaml.compilation.kernel.GamaMetaModel;

/**
 * Written by drogoul Modified on 23 nov. 2009
 *
 * In GUI Mode, for the moment, only one controller allowed at a time (controllers[0])
 *
 * @todo Description
 */
public class GAMA {

	public final static String VERSION = "GAMA 1.7";
	public static final String _WARNINGS = "warnings";
	public static PlatformAgent agent;

	// hqnghi: add several controllers to have multi-thread experiments
	private final static List<IExperimentController> controllers = new ArrayList<>();

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
	 * 
	 * @param id
	 * @param model
	 */
	public static void runGuiExperiment(final String id, final IModel model) {
		// System.out.println("Launching experiment " + id + " of model " +
		// model.getFilePath());
		final IExperimentPlan newExperiment = model.getExperiment(id);
		if (newExperiment == null) {
			// System.out.println("No experiment " + id + " in model " +
			// model.getFilePath());
			return;
		}
		IExperimentController controller = getFrontmostController();
		if (controller != null) {
			final IExperimentPlan existingExperiment = controller.getExperiment();
			if (existingExperiment != null) {
				controller.getScheduler().pause();
				if (!getGui().confirmClose(existingExperiment)) { return; }
			}
		}
		controller = newExperiment.getController();
		if (controllers.size() > 0) {
			closeAllExperiments(false, false);
		}

		if (getGui().openSimulationPerspective(model, id, true)) {
			controllers.add(controller);
			controller.userOpen();
		} else {
			// we are unable to launch the perspective.
			System.out.println("Unable to launch simulation perspective for experiment " + id + " of model "
					+ model.getFilePath());
			// getGui().openModelingPerspective(true);
		}

	}

	/**
	 * Add a sub-experiment to the current GUI experiment
	 * 
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
	 * 
	 * @param id
	 * @param model
	 */
	public static synchronized IExperimentPlan addHeadlessExperiment(final IModel model, final String expName,
			final ParametersSet params, final Double seed) {

		final ExperimentPlan currentExperiment = (ExperimentPlan) model.getExperiment(expName);

		if (currentExperiment == null) { throw GamaRuntimeException
				.error("Experiment " + expName + " cannot be created", getRuntimeScope()); }
		currentExperiment.setHeadless(true);
		for (final Map.Entry<String, Object> entry : params.entrySet()) {

			currentExperiment.setParameterValueByTitle(currentExperiment.getExperimentScope(), entry.getKey(),
					entry.getValue());
		}
		currentExperiment.open();
		if (seed != null) {
			currentExperiment.getAgent().setSeed(seed);
		}

		currentExperiment.getAgent().createSimulation(new ParametersSet(), true);

		controllers.add(currentExperiment.getController());
		return currentExperiment;

	}

	public static void closeFrontmostExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) { return; }
		controller.close();
		controllers.remove(controller);
	}

	public static void closeExperiment(final IExperimentPlan experiment) {
		if (experiment == null) { return; }
		final IExperimentController controller = experiment.getController();
		if (controller == null) { return; }
		controller.close();
		controllers.remove(controller);
	}

	public static void closeAllExperiments(final boolean andOpenModelingPerspective, final boolean immediately) {
		for (final IExperimentController controller : controllers) {
			controller.close();
		}
		controllers.clear();
		getGui().closeSimulationViews(andOpenModelingPerspective, immediately);
	}

	/**
	 *
	 * Access to experiments and their components
	 *
	 */

	public static SimulationAgent getSimulation() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) { return null; }
		return controller.getExperiment().getCurrentSimulation();
	}

	public static IExperimentPlan getExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null) { return null; }
		return controller.getExperiment();
	}

	public static IModel getModel() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null
				|| controller.getExperiment() == null) { return GamaMetaModel.INSTANCE.getAbstractModelSpecies(); }
		return controller.getExperiment().getModel();
	}

	/**
	 *
	 * Exception and life-cycle related utilities
	 *
	 */

	public static boolean reportError(final IScope scope, final GamaRuntimeException g,
			final boolean shouldStopSimulation) {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null || controller.isDisposing()
				|| controller.getExperiment().getAgent() == null) { return false; }
		// Returns whether or not to continue
		if (!(g instanceof GamaRuntimeFileException) && scope != null && !scope.reportErrors()) {
			// AD: we still throw exceptions related to files (Issue #1281)
			g.printStackTrace();
			return true;
		}
		if (scope != null && scope.getGui() != null)
			scope.getGui().runtimeError(g);
		g.setReported();

		final boolean isError = !g.isWarning() || controller.getExperiment().getAgent().getWarningsAsErrors();
		final boolean shouldStop =
				isError && shouldStopSimulation && GamaPreferences.Runtime.CORE_REVEAL_AND_STOP.getValue();
		return !shouldStop;
	}

	public static void reportAndThrowIfNeeded(final IScope scope, final GamaRuntimeException g,
			final boolean shouldStopSimulation) throws GamaRuntimeException {
		if (getExperiment() == null) {
			if (!(g instanceof GamaRuntimeFileException) && scope != null && !scope.reportErrors()) {
				// AD: we still throw exceptions related to files (Issue #1281)
				g.printStackTrace();
				return;
			}
		}
		// System.out.println("reportAndThrowIfNeeded : " + g.getMessage());
		if (scope != null && scope.getAgent() != null) {
			final String name = scope.getAgent().getName();
			if (!g.getAgentsNames().contains(name)) {
				g.addAgent(name);
			}
		}
		final boolean shouldStop = !reportError(scope, g, shouldStopSimulation);
		if (shouldStop) {
			final IExperimentController controller = getFrontmostController();
			if (controller == null || controller.isDisposing()) { return; }
			controller.userPause();
			throw g;
		}
	}

	public static void startPauseFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.startPause();
		}
	}

	public static void stepFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.userStep();
		}
	}

	public static void stepBackFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.stepBack();
		}
	}

	public static void pauseFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.directPause();
		}
	}

	public static void reloadFrontmostExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller != null) {
			controller.userReload();
		}
	}

	public static void startFrontmostExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller != null) {
			controller.userStart();
		}
	}

	public static boolean isPaused() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) { return true; }
		return controller.getScheduler().paused;

	}

	/**
	 *
	 * Scoping utilities
	 *
	 */

	public static void releaseScope(final IScope scope) {
		if (scope != null) {
			scope.clear();
		}
	}

	private static IScope copyRuntimeScope(final String additionalName) {
		final IScope scope = getRuntimeScope();
		if (scope != null) { return scope.copy(additionalName); }
		return null;
	}

	public static IScope getRuntimeScope() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) { return getPlatformAgent().getScope(); }
		final ExperimentAgent a = controller.getExperiment().getAgent();
		if (a == null || a.dead()) { return controller.getExperiment().getExperimentScope(); }
		final SimulationAgent s = a.getSimulation();
		if (s == null || s.dead()) { return a.getScope(); }
		return s.getScope();
	}

	public static interface InScope<T> {

		public abstract static class Void implements InScope<Object> {

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
		try (IScope scope = copyRuntimeScope(" in temporary scope block")) {
			final T result = r.run(scope);
			return result;
		}
	}

	/**
	 * Allows to update all outputs after running an experiment
	 * 
	 * @param r
	 */
	public static final void runAndUpdateAll(final Runnable r) {
		r.run();
		getExperiment().refreshAllOutputs();
	}

	/**
	 *
	 * Simulation state related utilities for Eclipse GUI
	 *
	 */

	static IGui regularGui;
	static IGui headlessGui = new HeadlessListener();

	/**
	 * @return
	 */
	public static IGui getGui() {
		// either a headless listener or a fully configured gui
		if (isInHeadlessMode || regularGui == null) {
			return headlessGui;
		} else {
			return regularGui;
		}
	}

	public static IGui getHeadlessGui() {
		return headlessGui;
	}

	public static IGui getRegularGui() {
		return regularGui;
	}

	/**
	 * @param IGui
	 *            gui
	 */
	public static void setHeadlessGui(final IGui g) {
		headlessGui = g;
	}

	public static void setRegularGui(final IGui g) {
		regularGui = g;
	}

	static boolean isInHeadlessMode;

	/**
	 * @return
	 */
	public static boolean isInHeadLessMode() {
		return isInHeadlessMode;
	}

	/**
	 *
	 */
	public static void setHeadLessMode() {
		isInHeadlessMode = true;
	}

	public static void relaunchFrontmostExperiment() {
		// Needs to be done: recompile the model and runs the previous
		// experiment if any

	}

	public static PlatformAgent getPlatformAgent() {
		if (agent == null)
			agent = new PlatformAgent();
		return agent;
	}

}
