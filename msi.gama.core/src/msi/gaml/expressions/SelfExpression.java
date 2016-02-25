/*********************************************************************************************
 *
 *
 * 'SelfExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.Set;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

public class SelfExpression extends VariableExpression {

	protected SelfExpression(final IType type) {
		super(IKeyword.SELF, type, true, null);
	}

	@Override
	public Object value(final IScope scope) {
		return scope.getAgentScope();
	}

	@Override
	public String getTitle() {
		return "pseudo-variable self of type " + getType().getTitle();
	}

	@Override
	public String getDocumentation() {
		return "Represents the current agent, instance of species " + type.getTitle();
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectMetaInformation(final Set<String> plugins) {}

}
