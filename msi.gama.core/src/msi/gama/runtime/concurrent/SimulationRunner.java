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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.runtime.concurrent.GamaExecutorService.Caller;

public class SimulationRunner {

	final SimulationPopulation population;
	final Map<SimulationAgent, Callable<Boolean>> runnables;
	static final Function<SimulationAgent, Boolean> STEP = each -> each.getScope().step(each).passed();
	final int concurrency;

	private int activeThreads;

	public SimulationRunner(final SimulationPopulation pop) {
		population = pop;
		runnables = new LinkedHashMap<>();
		final IExperimentPlan plan = population.getHost().getSpecies();
		if (plan.isHeadless()) {
			concurrency = 1;
		} else {
			concurrency = GamaExecutorService.getParallelism(population.getHost().getScope(), plan.getConcurrency(),
					Caller.SIMULATION);
		}

	}

	public void remove(final SimulationAgent agent) {
		runnables.remove(agent);
	}

	public void add(final SimulationAgent agent) {
		runnables.put(agent, () -> {
			activeThreads = computeNumberOfThreads();
			return STEP.apply(agent);
		});
	}

	public void step() {
		try {
			getExecutor().invokeAll(runnables.values());
		} catch (final InterruptedException e) {}

	}

	private int computeNumberOfThreads() {
		final ExecutorService executor = getExecutor();
		if (executor instanceof ForkJoinPool) {
			// getActiveThreadCount() always overestimates the number of threads
			return Math.min(concurrency, ((ForkJoinPool) executor).getActiveThreadCount());
		}
		if (executor instanceof ThreadPoolExecutor) {
			return Math.min(concurrency, ((ThreadPoolExecutor) executor).getActiveCount());
		}
		return 1;
	}

	protected ExecutorService getExecutor() {
		return concurrency == 0 ? GamaExecutorService.SAME_THREAD_EXECUTOR
				: GamaExecutorService.SIMULATION_PARALLEL_EXECUTOR;
	}

	public void dispose() {
		runnables.clear();
	}

	public int getActiveThreads() {
		return activeThreads;
	}

	public boolean hasSimulations() {
		return runnables.size() > 0;
	}

}
