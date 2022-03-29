/*******************************************************************************************************
 *
 * LocalSimulationRuntime.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import msi.gama.headless.job.IExperimentJob;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class LocalSimulationRuntime.
 */
public class LocalSimulationRuntime /* extends Observable */ implements SimulationRuntime {

	static {
		DEBUG.ON();
	}

	/** The simulations. */
	private final Map<String, IExperimentJobThread> simulations;

	/** The queue. */
	private final ArrayList<IExperimentJobThread> queue;

	/** The started. */
	private final ArrayList<IExperimentJobThread> started;

	/** The allocated processor. */
	private int allocatedProcessor;

	/** The is trace kept. */
	private boolean isTraceKept;

	/**
	 * Instantiates a new local simulation runtime.
	 */
	public LocalSimulationRuntime() {
		this(UNDEFINED_QUEUE_SIZE);
	}

	/**
	 * Instantiates a new local simulation runtime.
	 *
	 * @param numberOfCoresAsked
	 *            the number of cores asked
	 */
	public LocalSimulationRuntime(final int numberOfCoresAsked) {
		simulations = new HashMap<>();
		queue = new ArrayList<>();
		started = new ArrayList<>();
		// loadedModels = new HashMap<>();
		// availableLoadedModels = new HashMap<>();
		this.allocatedProcessor = getAvailableCores(numberOfCoresAsked);
	}

	/**
	 * Gets the available cores.
	 *
	 * @param asked
	 *            the asked
	 * @return the available cores
	 */
	private static int getAvailableCores(final int asked) {
		final int max = Runtime.getRuntime().availableProcessors();
		final int cpus = Math.max(1, Math.min(max, asked));
		DEBUG.LOG("Number of cpus used:" + cpus + " (available: " + max + ")");
		return cpus;
	}

	@Override
	public void pushSimulation(final IExperimentJob s) {
		final IExperimentJobThread f = new IExperimentJobThread(s);
		simulations.put(s.getExperimentID(), f);
		if (started.size() < allocatedProcessor) {
			this.startSimulation(f);
		} else {
			queue.add(f);
		}
	}

	/**
	 * Start simulation.
	 *
	 * @param s
	 *            the s
	 */
	private void startSimulation(final IExperimentJobThread s) {
		started.add(s);
		s.start();
		// this.notifyListener();
	}

	/**
	 * Close simulation.
	 *
	 * @param s
	 *            the s
	 */
	public void closeSimulation(final IExperimentJobThread s) {
		started.remove(s);
		if (queue.size() > 0) {
			final IExperimentJobThread p = queue.get(0);
			queue.remove(p);
			this.startSimulation(p);
		}
		if (!this.isTraceKept) { simulations.remove(s.getIExperimentJob().getExperimentID()); }
		// this.notifyListener();
	}

	@Override
	public boolean isPerformingSimulation() { return started.size() > 0 || queue.size() > 0; }

	/**
	 * The Class IExperimentJobThread.
	 */
	class IExperimentJobThread extends Thread {

		/** The si. */
		private IExperimentJob si = null;

		/**
		 * Gets the experiment job.
		 *
		 * @return the experiment job
		 */
		IExperimentJob getIExperimentJob() { return si; }

		/**
		 * Instantiates a new experiment job thread.
		 *
		 * @param sim
		 *            the sim
		 */
		public IExperimentJobThread(final IExperimentJob sim) {
			si = sim;
		}

		@Override
		public void run() {
			try (final DebugStream file = new DebugStream(si)) {
				si.loadAndBuild();
				si.playAndDispose();
			} catch (final Exception e) {
				DEBUG.ERR(e);
			} finally {
				closeSimulation(this);
			}
		}

	}

	@Override
	public void setNumberOfThreads(final int numberOfThread) {
		this.allocatedProcessor = numberOfThread;

	}

	@Override
	public void execute(Runnable r) {
		// TODO Auto-generated method stub
		
	}

}
