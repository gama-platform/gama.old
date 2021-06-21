package msi.gama.headless.runtime;

import static msi.gama.headless.common.Globals.CONSOLE_OUTPUT_FILENAME;
import static msi.gama.headless.common.Globals.OUTPUT_PATH;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import msi.gama.headless.job.ExperimentJob;
import ummisco.gama.dev.utils.DEBUG;

public class LocalSimulationRuntime /* extends Observable */ implements SimulationRuntime {

	static {
		DEBUG.ON();
	}
	private final Map<String, ExperimentJobThread> simulations;
	private final ArrayList<ExperimentJobThread> queue;
	private final ArrayList<ExperimentJobThread> started;
	private final int allocatedProcessor;
	private boolean isTraceKept;

	public LocalSimulationRuntime() {
		this(UNDEFINED_QUEUE_SIZE);
	}

	public LocalSimulationRuntime(final int numberOfCoresAsked) {
		simulations = new HashMap<>();
		queue = new ArrayList<>();
		started = new ArrayList<>();
		// loadedModels = new HashMap<>();
		// availableLoadedModels = new HashMap<>();
		this.allocatedProcessor = getAvailableCores(numberOfCoresAsked);
	}

	private static int getAvailableCores(final int asked) {
		final int max = Runtime.getRuntime().availableProcessors();
		final int cpus = Math.max(1, Math.min(max, asked));
		DEBUG.LOG("Number of cpus used:" + cpus + " (available: " + max + ")");
		return cpus;
	}

	@Override
	public void pushSimulation(final ExperimentJob s) {
		final ExperimentJobThread f = new ExperimentJobThread(s);
		simulations.put(s.getExperimentID(), f);
		if (started.size() < allocatedProcessor) {
			this.startSimulation(f);
		} else {
			queue.add(f);
		}
	}

	private void startSimulation(final ExperimentJobThread s) {
		started.add(s);
		s.start();
		// this.notifyListener();
	}

	public void closeSimulation(final ExperimentJobThread s) {
		started.remove(s);
		if (queue.size() > 0) {
			final ExperimentJobThread p = queue.get(0);
			queue.remove(p);
			this.startSimulation(p);
		}
		if (!this.isTraceKept) { simulations.remove(s.getExperimentJob().getExperimentID()); }
		// this.notifyListener();
	}

	@Override
	public boolean isPerformingSimulation() {
		return started.size() > 0 || queue.size() > 0;
	}

	class ExperimentJobThread extends Thread {

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

		ExperimentJob getExperimentJob() {
			return si;
		}

		public ExperimentJobThread(final ExperimentJob sim) {
			si = sim;
		}

		@Override
		public void run() {
			try (final DebugStream file = new DebugStream()) {
				si.loadAndBuild();
				si.playAndDispose();
			} catch (final Exception e) {
				DEBUG.ERR(e);
			} finally {
				closeSimulation(this);
			}
		}

	}

}

// private final HashMap<String, ArrayList<IModel>> loadedModels;
// private final HashMap<String, ArrayList<IModel>> availableLoadedModels;

// @Override
// public HashMap<String, Double> getSimulationState() {
// final HashMap<String, Double> res = new HashMap<>();
// for (final ExperimentJobThread app : simulations.values()) {
// ExperimentJob exp = app.getExperimentJob();
// res.put(exp.getExperimentID(), new Double(exp.getStep() / exp.getFinalStep()));
// }
// return res;
// }

// private void notifyListener() {
// this.setChanged();
// this.notifyObservers();
// }
//
// @Override
// public SimulationState getSimulationState(final String id) {
// final FakeApplication tmp = simulations.get(id);
// if (tmp == null) return SimulationState.UNDEFINED;
// if (started.contains(tmp)) return SimulationState.STARTED;
// if (queue.contains(tmp)) return SimulationState.ENQUEUED;
// return SimulationState.ACHIEVED;
// }

// public void listenMe(final Observer v) {
// this.addObserver(v);
// }

// @Override
// public boolean isTraceKept() {
// return this.isTraceKept;
// }

// @Override
// public void keepTrace(final boolean t) {
// this.isTraceKept = t;
// }

// public synchronized IModel lockModel(final File fl) throws IOException, GamaHeadlessException {
// IModel mdl;
// final String key = fl.getAbsolutePath();
// ArrayList<IModel> arr = availableLoadedModels.get(fl.getAbsolutePath());
// if (arr == null) {
// arr = new ArrayList<>();
// availableLoadedModels.put(key, arr);
// loadedModels.put(key, new ArrayList<IModel>());
// }
// if (arr.size() == 0) {
// mdl = HeadlessSimulationLoader.loadModel(fl);
// loadedModels.get(key).add(mdl);
// } else {
// mdl = arr.get(0);
// arr.remove(0);
// }
// return mdl;
// }

// @Override
// public synchronized IModel loadModel(final File fl) throws IOException, GamaHeadlessException {
// final List<GamlCompilationError> errors = new ArrayList<>();
// return HeadlessSimulationLoader.loadModel(fl, errors);
// }

// @Override
// public IExperimentPlan buildExperimentPlan(final String expName, final IModel mdl) {
// final IDescription des = mdl.getExperiment(expName).getDescription();
// final IExperimentPlan expp = new ExperimentPlan(des);
// expp.setModel(mdl);
// return expp;
// }
