/*********************************************************************************************
 *
 *
 * 'AgentVariableExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.Set;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

public class AgentVariableExpression extends VariableExpression implements IVarExpression.Agent {

	protected AgentVariableExpression(final String n, final IType type, final boolean notModifiable,
		final IDescription def) {
		super(n, type, notModifiable, def);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return scope.getAgentVarValue(scope.getAgentScope(), getName());
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		if ( isNotModifiable ) { return; }
		scope.setAgentVarValue(scope.getAgentScope(), getName(), v);
	}

	@Override
	public String getDocumentation() {
		IDescription desc = getDefinitionDescription();
		return "Type " + type.getTitle() + (desc == null ? "<br>Built In" : "<br>Defined in " + desc.getTitle());
	}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectPlugins(final Set<String> plugins) {}

}
