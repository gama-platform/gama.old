/*********************************************************************************************
 *
 * 'UserOnlyControlArchitecture.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.architecture.user;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@skill (
		name = IKeyword.USER_ONLY,
		concept = { IConcept.GUI, IConcept.ARCHITECTURE },
		doc = @doc ("A control architecture, based on FSM, where the user is being given complete control of the agents"))
// @vars({ @var(name = IKeyword.STATE, type = IType.STRING),
// @var(name = IKeyword.STATES, type = IType.LIST, constant = true) })
public class UserOnlyControlArchitecture extends UserControlArchitecture {

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executeCurrentState(scope);
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		if (initPanel != null) {
			initPanel.executeOn(scope);
		}
		return true;
	}
}