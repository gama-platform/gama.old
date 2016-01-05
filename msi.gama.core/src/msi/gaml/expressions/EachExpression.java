/*********************************************************************************************
 *
 *
 * 'EachExpression.java', in plugin 'msi.gama.core', is part of the source code of the
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

public class EachExpression extends VariableExpression {

	public EachExpression(final IType type) {
		super(IKeyword.EACH, type, true, null);
	}

	@Override
	public Object value(final IScope scope) {
		return scope.getEach();
	}

	@Override
	public String getTitle() {
		return "pseudo-variable each of type " + getType().getTitle();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "Represents the current object, of type " + type.getTitle() + ", in the iteration";
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectPlugins(final Set<String> plugins) {}

}
