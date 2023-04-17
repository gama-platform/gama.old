/*******************************************************************************************************
 *
 * ParallelAgentStepper.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import java.util.Spliterator;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class ParallelAgentStepper.
 */
public class ParallelAgentStepper extends ParallelAgentRunner<Boolean> {

	/**
	 * Instantiates a new parallel agent stepper.
	 *
	 * @param scope the scope
	 * @param agents the agents
	 */
	public ParallelAgentStepper(final IScope scope, final Spliterator<IAgent> agents) {
		super(scope, agents);
	}

	@Override
	public Boolean executeOn(final IScope scope) throws GamaRuntimeException {
		final Boolean[] mutableBoolean = { Boolean.TRUE };
		agents.forEachRemaining(each -> {
			if (mutableBoolean[0].booleanValue()) {
				mutableBoolean[0] = Boolean.valueOf(scope.step(each).passed());
			}
		});
		return mutableBoolean[0];
	}

	@Override
	ParallelAgentRunner<Boolean> subTask(final Spliterator<IAgent> sub) {
		return new ParallelAgentStepper(originalScope, sub);
	}

}