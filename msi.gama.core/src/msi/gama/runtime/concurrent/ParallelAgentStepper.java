/*******************************************************************************************************
 *
 * msi.gama.runtime.concurrent.ParallelAgentStepper.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import java.util.Spliterator;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.MutableResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class ParallelAgentStepper extends ParallelAgentRunner<Boolean> {

	public ParallelAgentStepper(final IScope scope, final Spliterator<IAgent> agents) {
		super(scope, agents);
	}

	@Override
	public Boolean executeOn(final IScope scope) throws GamaRuntimeException {
		final MutableResult result = new MutableResult();
		agents.forEachRemaining(each -> {
			if (result.passed())
				result.accept(scope.step(each));
		});
		return result.passed();
	}

	@Override
	ParallelAgentRunner<Boolean> subTask(final Spliterator<IAgent> sub) {
		return new ParallelAgentStepper(originalScope, sub);
	}

}