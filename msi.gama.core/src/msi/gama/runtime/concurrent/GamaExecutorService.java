/*********************************************************************************************
 *
 * 'GamaExecutorService.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.runtime.concurrent;

import static msi.gama.common.preferences.GamaPreferences.create;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import com.google.common.util.concurrent.MoreExecutors;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.IPreferenceChangeListener;
import msi.gama.common.preferences.Pref;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;;

public abstract class GamaExecutorService {

	public static ForkJoinPool AGENT_PARALLEL_EXECUTOR;
	public static ExecutorService SIMULATION_PARALLEL_EXECUTOR;
	public static final ExecutorService SAME_THREAD_EXECUTOR = MoreExecutors.sameThreadExecutor();

	public static final Pref<Boolean> CONCURRENCY_SIMULATIONS =
			create("pref_parallel_simulations", "Make experiments run simulations in parallel", true, IType.BOOL)
					.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Boolean> CONCURRENCY_GRID =
			create("pref_parallel_grids", "Make grids schedule their agents in parallel", false, IType.BOOL)
					.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Boolean> CONCURRENCY_SPECIES =
			create("pref_parallel_species", "Make species schedule their agents in parallel", false, IType.BOOL)
					.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Integer> CONCURRENCY_THRESHOLD =
			create("pref_parallel_threshold", "Number under which agents are executed sequentially", 20, IType.INT)
					.between(1, null).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);
	public static final Pref<Integer> CONCURRENCY_THREADS_NUMBER =
			create("pref_parallel_threads",
					"Max. number of threads to use (available processors: " + Runtime.getRuntime().availableProcessors()
							+ ")",
					4, IType.INT).between(1, null).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY)
							.addChangeListener(new IPreferenceChangeListener<Integer>() {

								@Override
								public boolean beforeValueChange(final Integer newValue) {
									return true;
								}

								@Override
								public void afterValueChange(final Integer newValue) {
									setConcurrencyLevel(newValue);
									System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
											String.valueOf(newValue));
								}
							});

	public static void startUp() {
		// Called by the activator to init the preferences and executor services
		setConcurrencyLevel(CONCURRENCY_THREADS_NUMBER.getValue());
	}

	public static void setConcurrencyLevel(final int nb) {
		if (AGENT_PARALLEL_EXECUTOR != null)
			AGENT_PARALLEL_EXECUTOR.shutdown();
		AGENT_PARALLEL_EXECUTOR = new ForkJoinPool(nb);
		if (SIMULATION_PARALLEL_EXECUTOR != null)
			SIMULATION_PARALLEL_EXECUTOR.shutdown();
		SIMULATION_PARALLEL_EXECUTOR = Executors.newFixedThreadPool(nb);
	}

	public static enum Caller {
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
		if (concurrency == null) {
			switch (caller) {
				case SIMULATION:
					if (CONCURRENCY_SIMULATIONS.getValue())
						return CONCURRENCY_THREADS_NUMBER.getValue();
					else
						return 0;
				case SPECIES:
					if (CONCURRENCY_SPECIES.getValue()) {
						return CONCURRENCY_THRESHOLD.getValue();
					} else {
						return 0;
					}
				case GRID:
					if (CONCURRENCY_GRID.getValue()) {
						return CONCURRENCY_THRESHOLD.getValue();
					} else {
						return 0;
					}
				default:
					return 0;
			}
		} else {
			final Object o = concurrency.value(scope);
			if (o instanceof Boolean) {
				if (o.equals(Boolean.FALSE))
					return 0;
				if (o.equals(Boolean.TRUE)) {
					if (caller == Caller.SIMULATION)
						return CONCURRENCY_THREADS_NUMBER.getValue();
					return CONCURRENCY_THRESHOLD.getValue();
				}
			} else if (o instanceof Integer) {
				final Integer i = Math.abs((Integer) o);
				return i;
			} else {
				return getParallelism(scope, null, caller);
			}
		}
		return 0;
	}

	public static void executeThreaded(final Runnable r) {
		AGENT_PARALLEL_EXECUTOR.invoke(ForkJoinTask.adapt(r));
	}

	public static Boolean step(final IScope scope, final List<? extends IAgent> pop, final ISpecies species)
			throws GamaRuntimeException {
		final IExpression schedule = species.getSchedule();
		final List<? extends IAgent> agents = schedule == null ? pop : Cast.asList(scope, schedule.value(scope));
		final int threshold =
				getParallelism(scope, species.getConcurrency(), species.isGrid() ? Caller.GRID : Caller.SPECIES);
		return doStep(scope, agents.toArray(new IAgent[0]), threshold);
	}

	public static <A extends IShape> Boolean step(final IScope scope, final A[] array, final ISpecies species)
			throws GamaRuntimeException {
		final IExpression schedule = species.getSchedule();
		final IShape[] scheduledAgents;
		if (schedule == null) {
			scheduledAgents = array;
		} else {
			final List<IShape> agents = Cast.asList(scope, schedule.value(scope));
			scheduledAgents = agents.toArray(new IShape[0]);
		}
		final int threshold =
				getParallelism(scope, species.getConcurrency(), species.isGrid() ? Caller.GRID : Caller.SPECIES);
		return doStep(scope, scheduledAgents, threshold);
	}

	private static <A extends IShape> Boolean doStep(final IScope scope, final A[] array, final int threshold) {
		int concurrency = threshold;
		if (array.length <= threshold)
			concurrency = 0;
		switch (concurrency) {
			case 0:
				for (final A agent : array) {
					if (!scope.step((IAgent) agent).passed())
						return false;
				}
				return true;
			case 1:
				for (final A agent : array) {
					executeThreaded(() -> scope.step((IAgent) agent));
				}
				return true;
			default:
				return ParallelAgentRunner.step(scope, array, threshold);
		}
	}

	public static <A extends IShape> void execute(final IScope scope, final IExecutable executable, final A[] array,
			final IExpression parallel) throws GamaRuntimeException {
		int threshold = getParallelism(scope, parallel, Caller.NONE);
		if (array.length <= threshold)
			threshold = 0;
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
		execute(scope, executable, list.toArray(new IAgent[0]), parallel);
	}

}
