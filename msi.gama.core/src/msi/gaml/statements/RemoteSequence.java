/**
 * Created by drogoul, 11 mai 2014
 * 
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

public class RemoteSequence extends AbstractStatementSequence {

	IAgent myself;

	public RemoteSequence(final IDescription desc) {
		super(desc);
	}

	public IAgent getMyself(){
		return myself;
	}
	
	public void setMyself(final IAgent agent) {
		myself = agent;
	}

	@Override
	public void leaveScope(final IScope scope) {
		myself = null;
		super.leaveScope(scope);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		scope.addVarWithValue(IKeyword.MYSELF, myself);
		final Object result = super.privateExecuteIn(scope);
		return result;
	}
}