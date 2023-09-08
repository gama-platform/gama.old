/*******************************************************************************************************
 *
 * GAMA.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ISnapshotMaker;
import msi.gama.common.interfaces.ITopLevelAgentChangeListener;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.PoolUtils;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.benchmark.Benchmark;
import msi.gama.runtime.benchmark.StopWatch;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.compilation.kernel.GamaMetaModel;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 23 nov. 2009
 *
 * In GUI Mode, for the moment, only one controller allowed at a time (controllers[0])
 *
 * Description
 */
public class GAMA {

	static {
		DEBUG.OFF();
	}

	/** The Constant VERSION_NUMBER. */
	public static final String VERSION_NUMBER = "1.9.3";

	/** The Constant VERSION. */
	public static final String VERSION = "GAMA " + VERSION_NUMBER;

	/** The Constant _WARNINGS. */
	// public static final String _WARNINGS = "warnings";

	/** The agent. */
	private static volatile PlatformAgent agent;

	/** The snapshot agent. */
	private static ISnapshotMaker snapshotAgent;

	/** The benchmark agent. */
	private static Benchmark benchmarkAgent;

	/** The is in headless mode. */
	private static boolean isInHeadlessMode;

	/** The is in headless mode. */
	private static boolean isInServerMode;

	/** The is synchronized. */
	private static volatile boolean isSynchronized;

	/** The regular gui. */
	private static IGui regularGui;

	/** The headless gui. */
	private static IGui headlessGui;

	/** The current top level agent. */
	private static ITopLevelAgent currentTopLevelAgent;

	/** The top level agent listeners. */
	private static List<ITopLevelAgentChangeListener> topLevelAgentListeners = new CopyOnWriteArrayList<>();

	/** The Constant controllers. */
	// hqnghi: add several controllers to have multi-thread experiments
	private static final List<IExperimentController> controllers = new CopyOnWriteArrayList<>();

	/**
	 * Gets the controllers.
	 *
	 * @return the controllers
	 */
	public static List<IExperimentController> getControllers() { return controllers; }

	/**
	 * Gets the frontmost controller.
	 *
	 * @return the frontmost controller
	 */
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
		// DEBUG.OUT("Launching experiment " + id + " of model " + model.getFilePath());
		final IExperimentPlan newExperiment = model.getExperiment(id);
		if (newExperiment == null) // DEBUG.OUT("No experiment " + id + " in model " + model.getFilePath());
			return;
		IExperimentController controller = getFrontmostController();
		if (controller != null) {
			final IExperimentPlan existingExperiment = controller.getExperiment();
			if (existingExperiment != null) {
				controller.directPause();
				if (!getGui().confirmClose(existingExperiment)) return;
			}
		}
		controller = newExperiment.getController();
		if (!controllers.isEmpty()) { closeAllExperiments(false, false); }

		if (getGui().openSimulationPerspective(model, id)) {
			controllers.add(controller);
			startBenchmark(newExperiment);
			controller.userOpen();
		} else {
			// we are unable to launch the perspective.
			DEBUG.ERR("Unable to launch simulation perspective for experiment " + id + " of model "
					+ model.getFilePath());
		}
	}

	// /**
	// * Add a sub-experiment to the current GUI experiment
	// *
	// * @param id
	// * @param model
	// */
	// public static void addGuiExperiment(final IExperimentPlan experiment) {
	//
	// }

	/**
	 * Open experiment from gaml file.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void openExperimentFromGamlFile(final IExperimentPlan experiment) {
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

		if (currentExperiment == null) throw GamaRuntimeException
				.error("Experiment " + expName + " does not exist. Please check its name.", getRuntimeScope());
		currentExperiment.setHeadless(true);
		for (final Map.Entry<String, Object> entry : params.entrySet()) {

			final IParameter.Batch v = currentExperiment.getParameterByTitle(entry.getKey());
			if (v != null) {
				currentExperiment.setParameterValueByTitle(currentExperiment.getExperimentScope(), entry.getKey(),
						entry.getValue());
			} else {
				currentExperiment.setParameterValue(currentExperiment.getExperimentScope(), entry.getKey(),
						entry.getValue());
			}

		}
		currentExperiment.open(seed);
		controllers.add(currentExperiment.getController());
		return currentExperiment;

	}

	/**
	 * Close experiment.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void closeExperiment(final IExperimentPlan experiment) {
		if (experiment == null) return;
		closeController(experiment.getController());
		changeCurrentTopLevelAgent(getPlatformAgent(), false);
	}

	/**
	 * Close all experiments.
	 *
	 * @param andOpenModelingPerspective
	 *            the and open modeling perspective
	 * @param immediately
	 *            the immediately
	 */
	public static void closeAllExperiments(final boolean andOpenModelingPerspective, final boolean immediately) {
		for (final IExperimentController controller : new ArrayList<>(controllers)) { closeController(controller); }
		getGui().closeSimulationViews(null, andOpenModelingPerspective, immediately);
		PoolUtils.WriteStats();
		changeCurrentTopLevelAgent(getPlatformAgent(), false);
	}

	/**
	 * Close controller.
	 *
	 * @param controller
	 *            the controller
	 */
	private static void closeController(final IExperimentController controller) {
		if (controller == null) return;
		stopBenchmark(controller.getExperiment());
		desynchronizeFrontmostExperiment();
		controller.close();
		controllers.remove(controller);
	}

	/**
	 *
	 * Access to experiments and their components
	 *
	 */

	public static SimulationAgent getSimulation() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) return null;
		return controller.getExperiment().getCurrentSimulation();
	}

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	public static IExperimentPlan getExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null) return null;
		return controller.getExperiment();
	}

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public static IModel getModel() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null)
			return GamaMetaModel.INSTANCE.getAbstractModelSpecies();
		return controller.getExperiment().getModel();
	}

	/**
	 *
	 * Exception and life-cycle related utilities
	 *
	 */

	/**
	 * Report Error: tries to report (on the UI) and returns true if the simulation should continue
	 *
	 * @param scope
	 * @param g
	 * @param shouldStopSimulation
	 * @return
	 */
	public static boolean reportError(final IScope scope, final GamaRuntimeException g,
			final boolean shouldStopSimulation) {
		final boolean shouldStop = (g.isWarning() && GamaPreferences.Runtime.CORE_WARNINGS.getValue()
				|| !g.isWarning() && shouldStopSimulation) && GamaPreferences.Runtime.CORE_REVEAL_AND_STOP.getValue();

		if (g.isReported()) return !shouldStop;
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null || controller.isDisposing()
				|| controller.getExperiment().getAgent() == null)
			return false;
		// DEBUG.LOG("report error : " + g.getMessage());
		// Returns whether or not to continue
		if (!(g instanceof GamaRuntimeFileException) && scope != null && !scope.reportErrors()) {
			// AD: we still throw exceptions related to files (Issue #1281)
			g.printStackTrace();
			return true;
		}
		if (scope != null && scope.getGui() != null) { scope.getGui().runtimeError(scope, g); }
		g.setReported();

		return !shouldStop;
	}

	/**
	 * Report and throw if needed.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @param shouldStopSimulation
	 *            the should stop simulation
	 */
	public static void reportAndThrowIfNeeded(final IScope scope, final GamaRuntimeException g,
			final boolean shouldStopSimulation) {
		// See #3641 -- move this sentence to reportError(): if (g.isReported()) return;
		if (getExperiment() == null && !(g instanceof GamaRuntimeFileException) && scope != null
				&& !scope.reportErrors()) {
			// AD: we still throw exceptions related to files (Issue #1281)
			g.printStackTrace();
			return;
		}

		// DEBUG.LOG("reportAndThrowIfNeeded : " + g.getMessage());
		if (scope != null) {
			if (scope.getAgent() != null) {
				final String name = scope.getAgent().getName();
				if (!g.getAgentsNames().contains(name)) { g.addAgent(name); }
			}
			scope.setCurrentError(g);
			if (scope.isInTryMode()) throw g;
		}
		final boolean shouldStop = !reportError(scope, g, shouldStopSimulation);
		if (shouldStop) {
			if (isInHeadLessMode() && !isInServerMode()) throw g;
			pauseFrontmostExperiment();
			throw g;
		}
	}

	/**
	 * Start pause frontmost experiment.
	 */
	public static void startPauseFrontmostExperiment() {
		for (final IExperimentController controller : controllers) { controller.startPause(); }
	}

	/**
	 * Step frontmost experiment.
	 */
	public static void stepFrontmostExperiment() {
		for (final IExperimentController controller : controllers) { controller.userStep(); }
	}

	/**
	 * Step back frontmost experiment.
	 */
	public static void stepBackFrontmostExperiment() {
		for (final IExperimentController controller : controllers) { controller.userStepBack(); }
	}

	/**
	 * Pause frontmost experiment.
	 */
	public static void pauseFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			// Dont block display threads (see #
			if (getGui().isInDisplayThread()) {
				controller.userPause();
			} else {
				controller.directPause();
			}
		}
	}

	/**
	 * Resume frontmost experiment.
	 */
	public static void resumeFrontmostExperiment() {
		for (final IExperimentController controller : controllers) { controller.userStart(); }
	}

	/**
	 * Reload frontmost experiment.
	 */
	public static void reloadFrontmostExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller != null) { controller.userReload(); }
	}

	/**
	 * Start frontmost experiment.
	 */
	public static void startFrontmostExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller != null) { controller.userStart(); }
	}

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	public static boolean isPaused() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) return true;
		return controller.isPaused();

	}

	/**
	 *
	 * Scoping utilities
	 *
	 */

	public static void releaseScope(final IScope scope) {
		if (scope != null) { scope.clear(); }
	}

	/**
	 * Copy runtime scope.
	 *
	 * @param additionalName
	 *            the additional name
	 * @return the i scope
	 */
	private static IScope copyRuntimeScope(final String additionalName) {
		final IScope scope = getRuntimeScope();
		if (scope != null) return scope.copy(additionalName);
		return null;
	}

	/**
	 * Gets the runtime scope.
	 *
	 * @return the runtime scope
	 */
	public static IScope getRuntimeScope() {
		// If GAMA has not yet been loaded, we return null
		if (!GamaBundleLoader.LOADED) return null;
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) return getPlatformAgent().getScope();
		final ExperimentAgent a = controller.getExperiment().getAgent();
		if (a == null || a.dead()) return controller.getExperiment().getExperimentScope();
		final SimulationAgent s = a.getSimulation();
		if (s == null || s.dead()) return a.getScope();
		return s.getScope();
	}

	/**
	 * Gets the current random.
	 *
	 * @return the current random
	 */
	public static RandomUtils getCurrentRandom() {
		final IScope scope = getRuntimeScope();
		if (scope == null) return new RandomUtils();
		return scope.getRandom();
	}

	/**
	 * The Interface InScope.
	 *
	 * @param <T>
	 *            the generic type
	 */
	public interface InScope<T> {

		/**
		 * The Class Void.
		 */
		public abstract static class Void implements InScope<Object> {

			@Override
			public Object run(final IScope scope) {
				process(scope);
				return null;
			}

			/**
			 * Process.
			 *
			 * @param scope
			 *            the scope
			 */
			public abstract void process(IScope scope);
		}

		/**
		 * Run.
		 *
		 * @param scope
		 *            the scope
		 * @return the t
		 */
		T run(IScope scope);
	}

	/**
	 * Run.
	 *
	 * @param <T>
	 *            the generic type
	 * @param r
	 *            the r
	 * @return the t
	 */
	public static <T> T run(final InScope<T> r) {
		try (IScope scope = copyRuntimeScope(" in temporary scope block")) {
			return r.run(scope);
		}
	}

	/**
	 * Allows to update all outputs after running an experiment
	 *
	 * @param r
	 */
	public static final void runAndUpdateAll(final Runnable r) {
		r.run();
		IExperimentPlan exp = getExperiment();
		if (exp != null) { exp.refreshAllOutputs(); }
	}

	/**
	 * Gets the gui.
	 *
	 * @return the gui
	 */
	public static IGui getGui() {
		// either a headless listener or a fully configured gui
		if (isInHeadlessMode || regularGui == null) return getHeadlessGui();
		return regularGui;
	}

	/**
	 * Gets the headless gui.
	 *
	 * @return the headless gui
	 */
	public static IGui getHeadlessGui() {
		if (headlessGui == null) { headlessGui = new NullGuiHandler(); }
		return headlessGui;
	}

	/**
	 * Gets the regular gui.
	 *
	 * @return the regular gui
	 */
	public static IGui getRegularGui() { return regularGui; }

	/**
	 * @param IGui
	 *            gui
	 */
	public static void setHeadlessGui(final IGui g) { headlessGui = g; }

	/**
	 * Sets the regular gui.
	 *
	 * @param g
	 *            the new regular gui
	 */
	public static void setRegularGui(final IGui g) { regularGui = g; }

	/**
	 * @return
	 */
	public static boolean isInHeadLessMode() { return isInHeadlessMode; }

	/**
	 * Checks if is in server mode.
	 *
	 * @return true, if is in server mode
	 */
	public static boolean isInServerMode() { return isInServerMode; }

	/**
	 *
	 */
	public static IGui setHeadLessMode(final boolean isServer, final IGui guiHandler) {
		isInHeadlessMode = true;
		isInServerMode = isServer;
		setHeadlessGui(guiHandler);
		return guiHandler;
	}

	/**
	 * Relaunch frontmost experiment.
	 */
	public static void relaunchFrontmostExperiment() {
		// Needs to be done: recompile the model and runs the previous
		// experiment if any

	}

	/**
	 * Register top level agent change listener.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param listener
	 *            the listener
	 * @date 14 août 2023
	 */
	public static void registerTopLevelAgentChangeListener(final ITopLevelAgentChangeListener listener) {
		if (!topLevelAgentListeners.contains(listener)) { topLevelAgentListeners.add(listener); }
	}

	/**
	 * Register top level agent change listener.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param listener
	 *            the listener
	 * @date 14 août 2023
	 */
	public static void removeTopLevelAgentChangeListener(final ITopLevelAgentChangeListener listener) {
		topLevelAgentListeners.remove(listener);
	}

	/**
	 * Access to the one and only 'gama' agent
	 *
	 * @return the platform agent, or creates it if it doesn't exist
	 */
	public static PlatformAgent getPlatformAgent() {
		if (agent == null) { agent = new PlatformAgent(); }
		return agent;
	}

	/**
	 * Gets the current top level agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the current top level agent
	 * @date 14 août 2023
	 */
	public static ITopLevelAgent getCurrentTopLevelAgent() {
		if (currentTopLevelAgent == null || currentTopLevelAgent.dead() || currentTopLevelAgent.getScope().isClosed()) {
			currentTopLevelAgent = computeCurrentTopLevelAgent();
		}
		return currentTopLevelAgent;
	}

	/**
	 * Change current top level agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the agent
	 * @date 14 août 2023
	 */
	public static void changeCurrentTopLevelAgent(final ITopLevelAgent current, final boolean force) {
		if (currentTopLevelAgent == current && !force) return;
		currentTopLevelAgent = current;
		for (ITopLevelAgentChangeListener listener : topLevelAgentListeners) { listener.topLevelAgentChanged(current); }
	}

	/**
	 * Compute current top level agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the i top level agent
	 * @date 14 août 2023
	 */
	private static ITopLevelAgent computeCurrentTopLevelAgent() {
		IExperimentPlan plan = getExperiment();
		if (plan == null) return getPlatformAgent();
		IExperimentAgent exp = plan.getAgent();
		if (exp == null || exp.dead()) return getPlatformAgent();
		SimulationAgent sim = exp.getSimulation();
		if (sim == null || sim.dead()) return exp;
		return sim;
	}

	/**
	 *
	 * Benchmarking utilities
	 *
	 */
	public static StopWatch benchmark(final IScope scope, final Object symbol) {
		if (benchmarkAgent == null || symbol == null || scope == null) return StopWatch.NULL;
		if (symbol instanceof IBenchmarkable ib) return benchmarkAgent.record(scope, ib);
		if (symbol instanceof ISymbol is) return benchmarkAgent.record(scope, is.getDescription());
		return StopWatch.NULL;
	}

	/**
	 * Start benchmark.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void startBenchmark(final IExperimentPlan experiment) {
		if (experiment.shouldBeBenchmarked()) { benchmarkAgent = new Benchmark(experiment); }
	}

	/**
	 * Stop benchmark.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void stopBenchmark(final IExperimentPlan experiment) {
		if (benchmarkAgent != null) { benchmarkAgent.saveAndDispose(experiment); }
		benchmarkAgent = null;
	}

	/**
	 * Toggle sync frontmost experiment.
	 */
	public static void desynchronizeFrontmostExperiment() {
		isSynchronized = false;
	}

	/**
	 * Checks if is synchronized.
	 *
	 * @return true, if is synchronized
	 */
	public static boolean isSynchronized() { return isSynchronized; }

	/**
	 * Synchronize experiment.
	 */
	public static void synchronizeFrontmostExperiment() {
		isSynchronized = true;
	}

	/**
	 * Sets the snapshot maker.
	 *
	 * @param instance
	 *            the new snapshot maker
	 */
	public static void setSnapshotMaker(final ISnapshotMaker instance) {
		if (instance != null) { snapshotAgent = instance; }
	}

	/**
	 * Gets the snapshot maker.
	 *
	 * @return the snapshot maker
	 */
	public static ISnapshotMaker getSnapshotMaker() {
		if (snapshotAgent == null) return IGui.NULL_SNAPSHOT_MAKER;
		return snapshotAgent;
	}
}
