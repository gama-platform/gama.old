package msi.gama.headless.runtime;

import static msi.gama.headless.common.Globals.CONSOLE_OUTPUT_FILENAME;
import static msi.gama.headless.common.Globals.OUTPUT_PATH;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.IDescription;
import ummisco.gama.dev.utils.DEBUG;

public class LocalSimulationRuntime extends Observable implements SimulationRuntime, RuntimeContext {

	static {
		DEBUG.ON();
	}
	private final Map<String, ExperimentJob> simulations;
	private final ArrayList<FakeApplication> queue;
	private final ArrayList<FakeApplication> started;
	private final HashMap<String, ArrayList<IModel>> loadedModels;
	private final HashMap<String, ArrayList<IModel>> availableLoadedModels;
	private final int allocatedProcessor;
	private boolean isTraceKept;

	public LocalSimulationRuntime() {
		this(UNDEFINED_QUEUE_SIZE);
	}

	public LocalSimulationRuntime(final int numberOfCoresAsked) {
		simulations = new HashMap<>();
		queue = new ArrayList<>();
		started = new ArrayList<>();
		loadedModels = new HashMap<>();
		availableLoadedModels = new HashMap<>();
		this.allocatedProcessor = getAvailableCores(numberOfCoresAsked);
	}

	private static int getAvailableCores(final int asked) {
		final int max = Runtime.getRuntime().availableProcessors();
		final int cpus = Math.max(1, Math.min(max, asked));
		DEBUG.LOG("Number of cpus used:" + cpus + " (available: " + max + ")");
		return cpus;
	}

	public void listenMe(final Observer v) {
		this.addObserver(v);
	}

	@Override
	public boolean isTraceKept() {
		return this.isTraceKept;
	}

	@Override
	public void keepTrace(final boolean t) {
		this.isTraceKept = t;
	}

	@Override
	public void pushSimulation(final ExperimentJob s) {
		simulations.put(s.getExperimentID(), s);
		final FakeApplication f = new FakeApplication(s, this);
		if (started.size() < allocatedProcessor) {
			this.startSimulation(f);
		} else {
			queue.add(f);
		}
	}

	private void startSimulation(final FakeApplication s) {
		started.add(s);
		s.start();
		this.notifyListener();
	}

	public void closeSimulation(final FakeApplication s) {
		started.remove(s);
		if (queue.size() > 0) {
			final FakeApplication p = queue.get(0);
			queue.remove(p);
			this.startSimulation(p);
		}
		if (!this.isTraceKept) {
			simulations.remove(s.getExperimentJob().getExperimentID());
		}
		this.notifyListener();
	}

	private void notifyListener() {
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public SimulationState getSimulationState(final String id) {
		final ExperimentJob tmp = simulations.get(id);
		if (tmp == null) { return SimulationState.UNDEFINED; }
		/**
		 * TODO AD BUG: ATTENTION !! queue contient des FakeApplication, pas des ExperimentJob
		 */
		if (started.contains(tmp)) { return SimulationState.STARTED; }
		/**
		 * TODO AD BUG: ATTENTION !! queue contient des FakeApplication, pas des ExperimentJob
		 */
		if (queue.contains(tmp)) { return SimulationState.ENQUEUED; }
		return SimulationState.ACHIEVED;
	}

	@Override
	public boolean isPerformingSimulation() {
		return started.size() > 0 || queue.size() > 0;
	}

	public synchronized IModel lockModel(final File fl) throws IOException, GamaHeadlessException {
		IModel mdl;
		final String key = fl.getAbsolutePath();
		ArrayList<IModel> arr = availableLoadedModels.get(fl.getAbsolutePath());
		if (arr == null) {
			arr = new ArrayList<>();
			availableLoadedModels.put(key, arr);
			loadedModels.put(key, new ArrayList<IModel>());
		}
		if (arr.size() == 0) {
			mdl = HeadlessSimulationLoader.loadModel(fl);
			loadedModels.get(key).add(mdl);
		} else {
			mdl = arr.get(0);
			arr.remove(0);
		}
		return mdl;
	}

	@Override
	public synchronized IModel loadModel(final File fl) throws IOException, GamaHeadlessException {
		final List<GamlCompilationError> errors = new ArrayList<>();
		return HeadlessSimulationLoader.loadModel(fl, errors);
	}

	@Override
	public IExperimentPlan buildExperimentPlan(final String expName, final IModel mdl) {
		final IDescription des = mdl.getExperiment(expName).getDescription();
		final IExperimentPlan expp = new ExperimentPlan(des);
		expp.setModel(mdl);
		return expp;
	}

	class FakeApplication extends Thread {// implements Runnable {

		class DebugStream extends FileOutputStream {

			DebugStream() throws FileNotFoundException {
				super(OUTPUT_PATH + "/" + CONSOLE_OUTPUT_FILENAME + "-" + si.getExperimentID() + ".txt");
				DEBUG.REGISTER_LOG_WRITER(this);
			}

			@Override
			public void close() throws IOException {
				super.close();
				DEBUG.UNREGISTER_LOG_WRITER();
			}

		}

		private ExperimentJob si = null;
		private LocalSimulationRuntime runtime = null;

		ExperimentJob getExperimentJob() {
			return si;
		}

		public FakeApplication(final ExperimentJob sim, final LocalSimulationRuntime rn) {
			si = sim;
			this.runtime = rn;
		}

		@Override
		public void run() {
			try (final DebugStream file = new DebugStream()) {
				si.loadAndBuild(this.runtime);
				si.playAndDispose();
			} catch (final Exception e) {
				DEBUG.ERR(e);
			} finally {
				runtime.closeSimulation(this);
			}
		}

	}

	@Override
	public HashMap<String, Double> getSimulationState() {
		final HashMap<String, Double> res = new HashMap<>();
		for (final ExperimentJob exp : simulations.values()) {
			res.put(exp.getExperimentID(), new Double(exp.getStep() / exp.getFinalStep()));
		}
		return res;
	}

}
