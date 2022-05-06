/*******************************************************************************************************
 *
 * ExecutorBasedSimulationRuntime.java, in msi.gama.headless, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.runtime;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import msi.gama.headless.job.IExperimentJob;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class LocalSimulationRuntime.
 */
public class ExecutorBasedSimulationRuntime implements SimulationRuntime {

	static {
		DEBUG.ON();
	}

	/**
	 * The Class OwnRunnable.
	 */
	static class OwnRunnable implements Runnable {

		/** The sim. */
		final IExperimentJob sim;

		/**
		 * Instantiates a new own runnable.
		 *
		 * @param s
		 *            the s
		 */
		OwnRunnable(final IExperimentJob s) {
			sim = s;
		}

		/**
		 * Run.
		 */
		@Override
		public void run() {
			try (final DebugStream file = new DebugStream(sim)) {
				sim.loadAndBuild();
				sim.playAndDispose();
			} catch (final Exception e) {
				DEBUG.ERR(e);
			}
		}
	}

	/** The executor. */
	ThreadPoolExecutor executor;
	int numberOfThreads = UNDEFINED_QUEUE_SIZE;

	/**
	 * Sets the number of threads.
	 *
	 * @param n
	 *            the new number of threads
	 */
	@Override
	public void setNumberOfThreads(final int n) {
		numberOfThreads = n;
		if (executor != null && n != executor.getMaximumPoolSize()) {
			executor.shutdown();
			executor = null;
		}
	}

	/**
	 * Gets the number of threads.
	 *
	 * @return the number of threads
	 */
	public int getNumberOfThreads() { return numberOfThreads; }

	/**
	 * Instantiates a new local simulation runtime.
	 *
	 * @param n
	 *            the number of cores asked
	 */

	private void createNewExecutor() {
		executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		executor.setRejectedExecutionHandler((r, executor) -> {
			if (r instanceof OwnRunnable or) {
				DEBUG.ERR("The execution of " + or.sim.getExperimentID() + " has been rejected");
			}
		});
	}

	@Override
	public void execute(final Runnable r) {
		getExecutor().execute(r);
	}

	@Override
	public void pushSimulation(final IExperimentJob s) {
		getExecutor().execute(new OwnRunnable(s));
	}

	@Override
	public boolean isPerformingSimulation() { return getExecutor().getActiveCount() > 0; }

	ThreadPoolExecutor getExecutor() {
		if (executor == null) { createNewExecutor(); }
		return executor;
	}

}
