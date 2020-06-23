/*******************************************************************************************************
 *
 * msi.gama.runtime.concurrent.ParallelAgentRunner.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import java.util.Spliterator;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.IExecutable;

public abstract class ParallelAgentRunner<T> extends RecursiveTask<T> implements IExecutable {

	final Spliterator<IAgent> agents;
	final IScope originalScope;

	public static <T> T execute(final ForkJoinTask<T> task) throws GamaRuntimeException {
		if (task == null) { return null; }
		return GamaExecutorService.AGENT_PARALLEL_EXECUTOR.invoke(task);
	}

	public static <A extends IShape> Boolean step(final IScope scope, final A[] array, final int threshold)
			throws GamaRuntimeException {
		final ParallelAgentStepper runner = from(scope, array, threshold);
		if (array.length <= threshold) { return runner.executeOn(scope); }
		return execute(runner);
	}

	public static <A extends IShape> void execute(final IScope scope, final IExecutable executable, final A[] array,
			final int threshold) throws GamaRuntimeException {
		final ParallelAgentRunner<?> runner = from(scope, executable, array, threshold);
		if (array.length <= threshold) {
			runner.executeOn(scope);
		} else {
			execute(runner);
		}
	}

	private static <A extends IShape> ParallelAgentStepper from(final IScope scope, final A[] array,
			final int threshold) {
		return new ParallelAgentStepper(scope, AgentSpliterator.of(array, threshold));
	}

	private static <A extends IShape> ParallelAgentExecuter from(final IScope scope, final IExecutable executable,
			final A[] array, final int threshold) {
		return new ParallelAgentExecuter(scope, executable, AgentSpliterator.of(array, threshold));
	}

	protected <A extends IShape> ParallelAgentRunner(final IScope scope, final Spliterator<IAgent> agents) {
		this.agents = agents;
		this.originalScope = scope.copy(" - forked - ");
	}

	abstract ParallelAgentRunner<T> subTask(Spliterator<IAgent> sub);

	@Override
	protected T compute() throws GamaRuntimeException {
		final Spliterator<IAgent> sub = agents.trySplit();
		T result;
		if (sub == null) {
			result = executeOn(originalScope);
		} else {
			final ParallelAgentRunner<T> left = subTask(sub);
			left.fork();
			result = compute();
			left.join();
		}
		return result;
	}

	// Called for each subtask or can be called directly if no parallelism is
	// allowed
	@Override
	public abstract T executeOn(IScope scope) throws GamaRuntimeException;

}
