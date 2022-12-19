/*******************************************************************************************************
 *
 * ParallelAgentExecuter.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import java.util.Spliterator;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.IExecutable;

/**
 * The Class ParallelAgentExecuter.
 */
public class ParallelAgentExecuter extends ParallelAgentRunner<Object> {

	/** The executable. */
	final IExecutable executable;

	/**
	 * Instantiates a new parallel agent executer.
	 *
	 * @param scope the scope
	 * @param executable the executable
	 * @param agents the agents
	 */
	public ParallelAgentExecuter(final IScope scope, final IExecutable executable, final Spliterator<IAgent> agents) {
		super(scope, agents);
		this.executable = executable;
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		final Boolean[] mutableBoolean = { Boolean.TRUE };
		// final AccumulatingExecutionResult result = new AccumulatingExecutionResult();
		agents.forEachRemaining(each -> {
			if (mutableBoolean[0].booleanValue()) {
				// if (result.passed()) {
				mutableBoolean[0] = scope.execute(executable, each, null).passed();
				// result.accept(scope.execute(executable, each, null));
			}
		});
		return mutableBoolean[0];
		// return result.passed() ? result.getValue() : null;
	}

	@Override
	ParallelAgentExecuter subTask(final Spliterator<IAgent> sub) {
		return new ParallelAgentExecuter(originalScope, executable, sub);
	}

}