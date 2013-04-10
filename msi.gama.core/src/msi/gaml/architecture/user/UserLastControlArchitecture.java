package msi.gaml.architecture.user;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@skill(name = IKeyword.USER_LAST)
// @vars({ @var(name = IKeyword.STATE, type = IType.STRING),
// @var(name = IKeyword.STATES, type = IType.LIST, constant = true) })
public class UserLastControlArchitecture extends UserControlArchitecture {

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		executeReflexes(scope);
		return executeCurrentState(scope);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		super.init(scope);
		if ( initPanel != null ) {
			initPanel.executeOn(scope);
		}
	}
}