/*******************************************************************************************************
 *
 * SimulationRuntime.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.runtime;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import msi.gama.headless.job.IExperimentJob;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class LocalSimulationRuntime.
 */
public class SimulationRuntime extends ThreadPoolExecutor implements RejectedExecutionHandler {

	/** The undefined queue size. */
	static int DEFAULT_NB_THREADS = 32;

	/**
	 * Instantiates a new executor based simulation runtime.
	 */
	public SimulationRuntime() {
		super(DEFAULT_NB_THREADS, DEFAULT_NB_THREADS, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		setRejectedExecutionHandler(this);
	}

	/**
	 * Sets the number of threads.
	 *
	 * @param n
	 *            the new number of threads
	 */
	public void setNumberOfThreads(final int n) {
		int oldN = getCorePoolSize();
		if (n == oldN) return;
		if (n < oldN) {
			setCorePoolSize(n);
			setMaximumPoolSize(n);
		} else {
			setMaximumPoolSize(n);
			setCorePoolSize(n);
		}
	}

	/**
	 * Checks if is performing simulation.
	 *
	 * @return true, if is performing simulation
	 */
	public boolean isPerformingSimulation() { return getActiveCount() > 0; }

	/**
	 * Rejected execution.
	 *
	 * @param r
	 *            the r
	 * @param executor
	 *            the executor
	 */
	@Override
	public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
		if (r instanceof IExperimentJob or) {
			DEBUG.ON();
			DEBUG.ERR("The execution of  " + or.getExperimentID() + " has been rejected");
		}
	}

}
