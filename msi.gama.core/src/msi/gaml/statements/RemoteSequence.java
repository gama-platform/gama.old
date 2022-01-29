/*******************************************************************************************************
 *
 * RemoteSequence.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

/**
 * The Class RemoteSequence.
 */
public class RemoteSequence extends AbstractStatementSequence {

	/** The myself. */
	// AD: adding ThreadLocal for multi-threaded simulations
	final ThreadLocal<IAgent> myself = new ThreadLocal<>();

	/**
	 * Instantiates a new remote sequence.
	 *
	 * @param desc the desc
	 */
	public RemoteSequence(final IDescription desc) {
		super(desc);
	}

	/**
	 * Gets the myself.
	 *
	 * @return the myself
	 */
	public IAgent getMyself() {
		return myself.get();
	}

	public void setMyself(final IAgent agent) {
		myself.set(agent);
	}

	@Override
	public void leaveScope(final IScope scope) {
		myself.set(null);
		super.leaveScope(scope);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		scope.addVarWithValue(IKeyword.MYSELF, myself.get());
		final Object result = super.privateExecuteIn(scope);
		return result;
	}
}