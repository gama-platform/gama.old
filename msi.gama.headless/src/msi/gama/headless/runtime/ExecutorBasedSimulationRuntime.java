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
	final ThreadPoolExecutor executor;

	/**
	 * Instantiates a new local simulation runtime.
	 */
	public ExecutorBasedSimulationRuntime() {
		this(UNDEFINED_QUEUE_SIZE);
	}

	/**
	 * Sets the number of threads.
	 *
	 * @param n
	 *            the new number of threads
	 */
	@Override
	public void setNumberOfThreads(final int n) {
		executor.setMaximumPoolSize(n);
	}

	/**
	 * Gets the number of threads.
	 *
	 * @return the number of threads
	 */
	public int getNumberOfThreads() { return executor.getMaximumPoolSize(); }

	/**
	 * Instantiates a new local simulation runtime.
	 *
	 * @param n
	 *            the number of cores asked
	 */
	public ExecutorBasedSimulationRuntime(final int n) {
		executor = new ThreadPoolExecutor(n, n, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		executor.setRejectedExecutionHandler((r, executor) -> {
			if (r instanceof OwnRunnable or) {
				DEBUG.ERR("The execution of " + or.sim.getExperimentID() + " has been rejected");
			}
		});
	}

	@Override
	public void pushSimulation(final IExperimentJob s) {
		executor.execute(new OwnRunnable(s));
	}

	@Override
	public boolean isPerformingSimulation() { return executor.getActiveCount() > 0; }

}
