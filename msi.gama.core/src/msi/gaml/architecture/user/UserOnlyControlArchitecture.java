package msi.gaml.architecture.user;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@skill(name = IKeyword.USER_ONLY)
// @vars({ @var(name = IKeyword.STATE, type = IType.STRING_STR),
// @var(name = IKeyword.STATES, type = IType.LIST_STR, constant = true) })
public class UserOnlyControlArchitecture extends UserControlArchitecture {

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executeCurrentState(scope);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		if ( initPanel != null ) {
			initPanel.executeOn(scope);
		}
	}
}