package msi.gaml.architecture.user;

import java.util.ArrayList;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.*;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.finite_state_machine.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

@vars(@var(name = IKeyword.USER_CONTROLLED, init = IKeyword.TRUE, type = IType.BOOL_STR))
public abstract class UserControlArchitecture extends FsmArchitecture {

	UserInitPanelStatement initPanel;

	@Override
	public void verifyBehaviors(final ISpecies context) {
		super.verifyBehaviors(context);
		if ( initialState == null && states.size() == 1 ) {
			initialState = new ArrayList<FsmStateStatement>(states.values()).get(0);
			context.getVar(IKeyword.STATE).setValue(initialState.getName());
		}
		for ( final FsmStateStatement s : states.values() ) {
			if ( s instanceof UserInitPanelStatement ) {
				initPanel = (UserInitPanelStatement) s;
			}
		}
	}

	@getter( IKeyword.USER_CONTROLLED)
	public Boolean isUserControlled(final IAgent agent) {
		return (Boolean) agent.getAttribute(IKeyword.USER_CONTROLLED);
	}

	@setter(IKeyword.USER_CONTROLLED)
	public void setUserControlled(final IAgent agent, final Boolean b) {
		agent.setAttribute(IKeyword.USER_CONTROLLED, b);
	}

	@Override
	protected Object executeCurrentState(final IScope scope) throws GamaRuntimeException {
		IGamlAgent agent = getCurrentAgent(scope);
		if ( agent.dead() || !isUserControlled(agent) ) { return null; }
		return super.executeCurrentState(scope);
	}

}
