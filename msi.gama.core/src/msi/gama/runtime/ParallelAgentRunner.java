package msi.gama.runtime;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.IExecutable;

public abstract class ParallelAgentRunner<T> extends RecursiveTask<T> implements IExecutable {

	public static class ParallelAgentStepper extends ParallelAgentRunner<Boolean> {

		public <A extends IShape> ParallelAgentStepper(final IScope scope, final A[] agents, final int begin,
				final int end) {
			super(scope, agents, begin, end, GamaPreferences.SEQUENTIAL_THRESHOLD.getValue());
		}

		@Override
		public Boolean executeOn(final IScope scope) throws GamaRuntimeException {
			// Triggers the creation of a new scope if necessary in this thread
			for (int i = begin; i < end; ++i) {
				if (!scope.step((IAgent) agents[i]))
					return false;
			}
			return true;
		}

		@Override
		ParallelAgentStepper subTask(final int begin, final int end) {
			return new ParallelAgentStepper(originalScope, agents, begin, end);
		}

	}

	public static class ParallelAgentExecuter extends ParallelAgentRunner<Object> {

		final IExecutable executable;

		public <A extends IShape> ParallelAgentExecuter(final IScope scope, final IExecutable executable,
				final A[] agents, final int begin, final int end, final int threshold) {
			super(scope, agents, begin, end, threshold);
			this.executable = executable;
		}

		@Override
		public Object executeOn(final IScope scope) throws GamaRuntimeException {
			// Triggers the creation of a new scope if necessary in this thread
			final Object[] result = new Object[1];
			for (int i = begin; i < end; ++i) {
				if (!scope.execute(executable, (IAgent) agents[i], null, result))
					return null;
			}
			return result[0];
		}

		@Override
		ParallelAgentExecuter subTask(final int begin, final int end) {
			return new ParallelAgentExecuter(originalScope, executable, agents, begin, end, sequentialThreshold);
		}

	}

	final IShape[] agents;
	final int begin, end;
	final IScope originalScope;
	final int sequentialThreshold;

	public static <A extends IShape> ParallelAgentRunner<Boolean> step(final IScope scope, final A[] array) {
		if (array == null)
			return null;
		return new ParallelAgentStepper(scope, array, 0, array.length);
	}

	public static <A extends IShape> ParallelAgentExecuter execute(final IScope scope, final IExecutable executable,
			final A[] array, final int threshold) {
		if (array == null || executable == null)
			return null;
		return new ParallelAgentExecuter(scope, executable, array, 0, array.length, threshold);
	}

	public static ParallelAgentExecuter execute(final IExecutable executable, final IPopulation<? extends IAgent> pop,
			final int threshold) {
		if (pop == null || pop.isEmpty())
			return null;
		return new ParallelAgentExecuter(pop.getHost().getScope(), executable, pop.toArray(), 0, pop.size(), threshold);
	}

	public static ParallelAgentExecuter execute(final IScope scope, final IExecutable executable,
			final List<IAgent> list, final int threshold) {
		if (list == null || list.isEmpty())
			return null;
		return new ParallelAgentExecuter(scope, executable, list.toArray(new IAgent[list.size()]), 0, list.size(),
				threshold);
	}

	public static ParallelAgentRunner<Boolean> step(final IPopulation<? extends IAgent> pop) {
		if (pop == null || pop.isEmpty())
			return null;
		return new ParallelAgentStepper(pop.getHost().getScope(), pop.toArray(), 0, pop.size());
	}

	public static ParallelAgentRunner<Boolean> step(final IScope scope, final List<IAgent> list) {
		if (list == null || list.isEmpty())
			return null;
		return new ParallelAgentStepper(scope, list.toArray(new IAgent[list.size()]), 0, list.size());
	}

	public <A extends IShape> ParallelAgentRunner(final IScope scope, final A[] agents, final int begin, final int end,
			final int threshold) {
		this.agents = agents;
		this.begin = begin;
		this.end = end;
		this.originalScope = scope.copy(" - forked - ");
		this.sequentialThreshold = threshold;
	}

	abstract ParallelAgentRunner<T> subTask(final int begin, final int end);

	@Override
	protected T compute() throws GamaRuntimeException {
		// We execute the agents if the size of the array is below the given
		// threshold
		if (end - begin <= sequentialThreshold) {
			return executeOn(originalScope);
			// Otherwise, we divide the array in two, compute the left part and
			// fork the right part
		} else {
			final int mid = begin + (end - begin) / 2;
			final ParallelAgentRunner<T> left = subTask(begin, mid);
			final ParallelAgentRunner<T> right = subTask(mid, end);
			left.fork();
			final T firstPart = right.compute();
			if (firstPart instanceof Boolean && ((Boolean) firstPart).equals(false))
				return null;
			return left.join();
		}
	}

	// Called for each subtask or can be called directly if no parallelism is
	// allowed
	@Override
	public abstract T executeOn(IScope scope) throws GamaRuntimeException;

}
