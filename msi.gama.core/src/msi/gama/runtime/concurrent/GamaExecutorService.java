/*******************************************************************************************************
 *
 * msi.gama.runtime.concurrent.GamaExecutorService.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import static msi.gama.common.preferences.GamaPreferences.create;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.benchmark.StopWatch;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;;

public abstract class GamaExecutorService {

	public static final UncaughtExceptionHandler EXCEPTION_HANDLER = (t, e) -> {

		if (e instanceof OutOfMemoryError) {
			if (GamaPreferences.Runtime.CORE_MEMORY_ACTION.getValue()) {
				GAMA.getGui().tell(
						"GAMA is out of memory. Experiment will be closed now. Increase the memory allocated to the platform in the preferences.");
				GAMA.closeAllExperiments(true, true);
			} else {
				GAMA.getGui().tell(
						"GAMA is out of memory. The platform will exit now. Relaunch it and increase the memory allocated in the preferences.");
				System.exit(0);
			}
		} else {
			e.printStackTrace();
		}

	};

	public static volatile ForkJoinPool AGENT_PARALLEL_EXECUTOR;

	public static final Pref<Boolean> CONCURRENCY_SIMULATIONS =
			create("pref_parallel_simulations", "Make experiments run simulations in parallel", true, IType.BOOL, true)
					.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Boolean> CONCURRENCY_GRID =
			create("pref_parallel_grids", "Make grids schedule their agents in parallel", false, IType.BOOL, true)
					.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Boolean> CONCURRENCY_SPECIES =
			create("pref_parallel_species", "Make species schedule their agents in parallel", false, IType.BOOL, true)
					.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Integer> CONCURRENCY_THRESHOLD =
			create("pref_parallel_threshold", "Number under which agents are executed sequentially", 20, IType.INT,
					true).between(1, null).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Integer> THREADS_NUMBER =
			create("pref_parallel_threads",
					"Max. number of threads to use (available processors: " + Runtime.getRuntime().availableProcessors()
							+ ")",
					4, IType.INT, true).between(1, null)
							.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY)
							.onChange(newValue -> {
								reset();
								System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
										String.valueOf(newValue));
							});

	public static void reset() {
		// Called by the activator to init the preferences and executor services
		setConcurrencyLevel(THREADS_NUMBER.getValue());
	}

	public static void setConcurrencyLevel(final int nb) {
		if (AGENT_PARALLEL_EXECUTOR != null) { AGENT_PARALLEL_EXECUTOR.shutdown(); }
		AGENT_PARALLEL_EXECUTOR = new ForkJoinPool(nb) {
			@Override
			public UncaughtExceptionHandler getUncaughtExceptionHandler() {
				return EXCEPTION_HANDLER;
			}
		};

	}

	public enum Caller {
		SPECIES, GRID, NONE, SIMULATION
	}

	/**
	 * Returns the level of parallelism from the expression passed and the preferences
	 *
	 * @param concurrency
	 *            The facet passed to the statement or species
	 * @param forSpecies
	 *            whether it is for species or not
	 * @return 0 for no parallelism, 1 for complete parallelism (i.e. each agent on its own), n for parallelism with a
	 *         threshold of n
	 */
	public static int getParallelism(final IScope scope, final IExpression concurrency, final Caller caller) {
		if (concurrency != null) {
			final Object o = concurrency.value(scope);
			if (o instanceof Boolean) {
				if (o.equals(Boolean.FALSE)) return 0;
				if (caller == Caller.SIMULATION) return THREADS_NUMBER.getValue();
				return CONCURRENCY_THRESHOLD.getValue();
			}
			if (o instanceof Integer) return Math.abs((Integer) o);
			return getParallelism(scope, null, caller);
		}
		switch (caller) {
			case SIMULATION:
				if (CONCURRENCY_SIMULATIONS.getValue())
					return THREADS_NUMBER.getValue();
				else
					return 0;
			case SPECIES:
				if (CONCURRENCY_SPECIES.getValue())
					return CONCURRENCY_THRESHOLD.getValue();
				else
					return 0;
			case GRID:
				if (CONCURRENCY_GRID.getValue())
					return CONCURRENCY_THRESHOLD.getValue();
				else
					return 0;
			default:
				return 0;
		}
	}

	public static void executeThreaded(final Runnable r) {
		AGENT_PARALLEL_EXECUTOR.invoke(ForkJoinTask.adapt(r));
	}

	public static <A extends IAgent> Boolean step(final IScope scope, final IList<A> pop, final ISpecies species)
			throws GamaRuntimeException {
		final IExpression schedule = species.getSchedule();
		final IList<? extends IAgent> agents = schedule == null ? pop : Cast.asList(scope, schedule.value(scope));
		final int threshold =
				getParallelism(scope, species.getConcurrency(), species.isGrid() ? Caller.GRID : Caller.SPECIES);
		return doStep(scope, agents.toArray(new IAgent[agents.size()]), threshold, species);
	}

	public static <A extends IShape> Boolean step(final IScope scope, final A[] array, final ISpecies species)
			throws GamaRuntimeException {
		final IExpression schedule = species.getSchedule();
		final IShape[] scheduledAgents;
		if (schedule == null) {
			scheduledAgents = array;
		} else {
			final List<IAgent> agents = Cast.asList(scope, schedule.value(scope));
			scheduledAgents = agents.toArray(new IAgent[agents.size()]);
		}
		final int threshold =
				getParallelism(scope, species.getConcurrency(), species.isGrid() ? Caller.GRID : Caller.SPECIES);
		return doStep(scope, scheduledAgents, threshold, species);
	}

	private static <A extends IShape> Boolean doStep(final IScope scope, final A[] array, final int threshold,
			final ISpecies species) {
		try (final StopWatch w = GAMA.benchmark(scope, species)) {
			int concurrency = threshold;
			if (array.length <= threshold) { concurrency = 0; }
			switch (concurrency) {
				case 0:
					for (final A aa : array) {
						final IAgent agent = (IAgent) aa;
						if (agent.dead()) {
							continue; // add this condition to avoid the activation of dead agents
						}
						if (!scope.step(agent).passed()) return false;
					}
					break;
				case 1:
					for (final A agent : array) {
						executeThreaded(() -> scope.step((IAgent) agent));
					}
					break;
				default:
					ParallelAgentRunner.step(scope, array, threshold);
			}
		}
		return true;
	}

	public static <A extends IShape> void execute(final IScope scope, final IExecutable executable, final A[] array,
			final IExpression parallel) throws GamaRuntimeException {
		int threshold = getParallelism(scope, parallel, Caller.NONE);
		if (array.length <= threshold) { threshold = 0; }
		switch (threshold) {
			case 0:
				for (final A agent : array) {
					scope.execute(executable, (IAgent) agent, null);
				}
				return;
			case 1:
				for (final A agent : array) {
					executeThreaded(() -> scope.execute(executable, (IAgent) agent, null));
				}
				return;
			default:
				ParallelAgentRunner.execute(scope, executable, array, threshold);
		}
	}

	public static void execute(final IScope scope, final IExecutable executable, final List<? extends IAgent> list,
			final IExpression parallel) throws GamaRuntimeException {
		execute(scope, executable, list.toArray(new IAgent[list.size()]), parallel);
	}

}
