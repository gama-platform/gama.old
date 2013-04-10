package msi.gaml.architecture.user;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

// @vars({ @var(name = IKeyword.STATE, type = IType.STRING),
// @var(name = IKeyword.STATES, type = IType.LIST, constant = true) })
@skill(name = IKeyword.USER_FIRST)
public class UserFirstControlArchitecture extends UserControlArchitecture {

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		executeCurrentState(scope);
		return executeReflexes(scope);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		if ( initPanel != null ) {
			initPanel.executeOn(scope);
		}
		super.init(scope);
	}
}