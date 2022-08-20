/*******************************************************************************************************
 *
 * GamaExecutorService.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gama.runtime.FlowStatus;
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

/**
 * The Class GamaExecutorService.
 */
public abstract class GamaExecutorService {

	/**
	 * The Class GamaMemoryExceptionHandler.
	 */
	static class GamaMemoryExceptionHandler implements UncaughtExceptionHandler {

		/** The last warning time stamp. */
		long lastWarningTimeStamp = 0l;

		@Override
		public void uncaughtException(final Thread t, final Throwable e) {
			if (e instanceof OutOfMemoryError) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastWarningTimeStamp > 60000l) {
					// 1 minute between warnings
					lastWarningTimeStamp = currentTime;
					String msg = e.getMessage();
					msg = msg == null ? "" : msg.toLowerCase();
					if (GamaPreferences.Runtime.CORE_MEMORY_ACTION.getValue() && GAMA.getExperiment() != null
							&& msg.contains("heap")) {
						GAMA.getGui().tell(
								"GAMA is out of memory. Experiment will be closed now. Please consult: https://gama-platform.org/wiki/Troubleshooting#memory-problems");
						GAMA.closeAllExperiments(true, true);
					} else {
						if (GAMA.getExperiment() != null && !msg.contains("heap")) {
							GAMA.getGui().tell(
									"GAMA cannot allocate more memory for displaying this experiment. The platform will exit now. Please try to quit other applications and relaunch it");
						} else {
							GAMA.getGui().tell(
									"Your system is running out of memory. GAMA will exit now. Please try to quit other applications and relaunch it");
						}
						System.exit(0);
					}
				}
			} else {
				e.printStackTrace();
			}
		}

	}

	/** The Constant EXCEPTION_HANDLER. */
	public static final UncaughtExceptionHandler EXCEPTION_HANDLER = new GamaMemoryExceptionHandler();

	/** The agent parallel executor. */
	public static volatile ForkJoinPool AGENT_PARALLEL_EXECUTOR;

	/** The Constant CONCURRENCY_SIMULATIONS. */
	public static final Pref<Boolean> CONCURRENCY_SIMULATIONS =
			create("pref_parallel_simulations", "Make experiments run simulations in parallel", true, IType.BOOL, true)
					.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);

	/** The Constant CONCURRENCY_SIMULATIONS_ALL. */
	public static final Pref<Boolean> CONCURRENCY_SIMULATIONS_ALL = create("pref_parallel_simulations_all",
			"In batch mode, allow to run simulations with different parameter sets in parallel", false, IType.BOOL,
			true).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);

	/** The Constant CONCURRENCY_GRID. */
	public static final Pref<Boolean> CONCURRENCY_GRID = create("pref_parallel_grids",
			"Make grids schedule their agents in parallel (beware that setting this to true no longer allows GAMA to ensure the reproducibility of simulations)",
			false, IType.BOOL, true).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);

	/** The Constant CONCURRENCY_SPECIES. */
	public static final Pref<Boolean> CONCURRENCY_SPECIES = create("pref_parallel_species",
			"Make species schedule their agents in parallel (beware that setting this to true no longer allows GAMA to ensure the reproducibility of simulations)",
			false, IType.BOOL, true).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);

	/** The Constant CONCURRENCY_THRESHOLD. */
	public static final Pref<Integer> CONCURRENCY_THRESHOLD =
			create("pref_parallel_threshold", "Number under which agents are executed sequentially", 20, IType.INT,
					true).between(1, null).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.CONCURRENCY);

	/** The Constant THREADS_NUMBER. */
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

	/**
	 * Reset.
	 */
	public static void reset() {
		// Called by the activator to init the preferences and executor services
		setConcurrencyLevel(THREADS_NUMBER.getValue());
	}

	/**
	 * Sets the concurrency level.
	 *
	 * @param nb
	 *            the new concurrency level
	 */
	public static void setConcurrencyLevel(final int nb) {
		if (AGENT_PARALLEL_EXECUTOR != null) { AGENT_PARALLEL_EXECUTOR.shutdown(); }
		AGENT_PARALLEL_EXECUTOR = new ForkJoinPool(nb) {
			@Override
			public UncaughtExceptionHandler getUncaughtExceptionHandler() { return EXCEPTION_HANDLER; }
		};

	}

	/**
	 * The Enum Caller.
	 */
	public enum Caller {

		/** The species. */
		SPECIES,
		/** The grid. */
		GRID,
		/** The none. */
		NONE,
		/** The simulation. */
		SIMULATION
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

	/**
	 * Execute threaded.
	 *
	 * @param r
	 *            the r
	 */
	public static void executeThreaded(final Runnable r) {
		AGENT_PARALLEL_EXECUTOR.invoke(ForkJoinTask.adapt(r));
	}

	/**
	 * Step.
	 *
	 * @param <A>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param pop
	 *            the pop
	 * @param species
	 *            the species
	 * @return the boolean
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static <A extends IAgent> Boolean step(final IScope scope, final IList<A> pop, final ISpecies species)
			throws GamaRuntimeException {
		final IExpression schedule = species.getSchedule();
		final IList<? extends IAgent> agents = schedule == null ? pop : Cast.asList(scope, schedule.value(scope));
		final int threshold =
				getParallelism(scope, species.getConcurrency(), species.isGrid() ? Caller.GRID : Caller.SPECIES);
		return doStep(scope, agents.toArray(new IAgent[agents.size()]), threshold, species);
	}

	/**
	 * Step.
	 *
	 * @param <A>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param array
	 *            the array
	 * @param species
	 *            the species
	 * @return the boolean
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
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

	/**
	 * Do step.
	 *
	 * @param <A>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param array
	 *            the array
	 * @param threshold
	 *            the threshold
	 * @param species
	 *            the species
	 * @return the boolean
	 */
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
					for (final A agent : array) { executeThreaded(() -> scope.step((IAgent) agent)); }
					break;
				default:
					ParallelAgentRunner.step(scope, array, threshold);
			}
		}
		return true;
	}

	/**
	 * Execute.
	 *
	 * @param <A>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param executable
	 *            the executable
	 * @param array
	 *            the array
	 * @param parallel
	 *            the parallel
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static <A extends IShape> void execute(final IScope scope, final IExecutable executable, final A[] array,
			final IExpression parallel) throws GamaRuntimeException {
		int threshold = getParallelism(scope, parallel, Caller.NONE);
		if (array.length <= threshold) { threshold = 0; }
		switch (threshold) {
			case 0:
				for (final A agent : array) {
					scope.execute(executable, (IAgent) agent, null);
					if (scope.getAndClearBreakStatus() == FlowStatus.BREAK) { break; }
				}
				return;
			// Break doesnt really make sense for parallel execution
			case 1:
				for (final A agent : array) { executeThreaded(() -> scope.execute(executable, (IAgent) agent, null)); }
				return;
			default:
				ParallelAgentRunner.execute(scope, executable, array, threshold);
		}
	}

	/**
	 * Execute.
	 *
	 * @param scope
	 *            the scope
	 * @param executable
	 *            the executable
	 * @param list
	 *            the list
	 * @param parallel
	 *            the parallel
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static void execute(final IScope scope, final IExecutable executable, final List<? extends IAgent> list,
			final IExpression parallel) throws GamaRuntimeException {
		execute(scope, executable, list.toArray(new IAgent[list.size()]), parallel);
	}

}
