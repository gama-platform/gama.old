/*******************************************************************************************************
 *
 * msi.gama.runtime.concurrent.SimulationRunner.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static msi.gama.runtime.concurrent.GamaExecutorService.EXCEPTION_HANDLER;
import static msi.gama.runtime.concurrent.GamaExecutorService.THREADS_NUMBER;
import static msi.gama.runtime.concurrent.GamaExecutorService.getParallelism;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import msi.gama.common.interfaces.IScopedStepable;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.runtime.concurrent.GamaExecutorService.Caller;

public class SimulationRunner {
	public volatile ExecutorService executor;
	final Map<IScopedStepable, Callable<Boolean>> runnables;
	final int concurrency;
	volatile int activeThreads;

	public static SimulationRunner of(final SimulationPopulation pop) {
		int concurrency = 0;
		final IExperimentPlan plan = pop.getHost().getSpecies();
		if (plan.isHeadless() && !plan.isBatch()) {
			concurrency = 1;
		} else {
			concurrency = getParallelism(pop.getHost().getScope(), plan.getConcurrency(), Caller.SIMULATION);
		}
		return withConcurrency(concurrency);
	}

	public static SimulationRunner withConcurrency(final int concurrency) {
		return new SimulationRunner(concurrency < 0 ? 1 : concurrency);
	}

	private SimulationRunner(final int concurrency) {
		this.concurrency = concurrency;
		runnables = new LinkedHashMap<>();
	}

	public void remove(final IScopedStepable agent) {
		runnables.remove(agent);
	}

	public void add(final IScopedStepable agent) {
		add(agent, () -> {
			activeThreads = computeNumberOfThreads();
			return agent.step();
		});
	}

	public void add(final IScopedStepable agent, final Callable<Boolean> callable) {
		runnables.put(agent, callable);
	}

	public void step() {
		try {
			getExecutor().invokeAll(runnables.values());
		} catch (final InterruptedException e) {}

	}

	private int computeNumberOfThreads() {
		final ExecutorService executor = getExecutor();
		if (executor instanceof ThreadPoolExecutor)
			return Math.min(concurrency, ((ThreadPoolExecutor) executor).getActiveCount());
		return 1;
	}

	protected ExecutorService getExecutor() {
		if (executor == null) {
			executor = concurrency == 0 ? newSingleThreadExecutor() : new Executor(THREADS_NUMBER.getValue());
		}
		return executor;
	}

	public void dispose() {
		runnables.clear();
		if (executor != null) { executor.shutdownNow(); }
	}

	public int getActiveThreads() {
		return activeThreads;
	}

	public boolean hasSimulations() {
		return runnables.size() > 0;
	}

	static class Executor extends ThreadPoolExecutor {
		Executor(final int nb) {
			super(nb, nb, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		}

		@Override
		protected void afterExecute(final Runnable r, final Throwable exception) {
			Throwable t = exception;
			super.afterExecute(r, t);
			if (t == null && r instanceof Future<?>) {
				try {
					final Future<?> future = (Future<?>) r;
					if (future.isDone()) { future.get(); }
				} catch (final CancellationException ce) {
					t = ce;
				} catch (final ExecutionException ee) {
					t = ee.getCause();
				} catch (final InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
			}
			if (t != null) { EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), t); }
		}

	}

}
