/*******************************************************************************************************
 *
 * SuperExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class SuperExpression.
 */
public class SuperExpression extends VariableExpression {

	/**
	 * Instantiates a new super expression.
	 *
	 * @param type
	 *            the type
	 */
	public SuperExpression(final IType<?> type) {
		super(IKeyword.SUPER, type, true, null);
	}

	@Override
	public Object _value(final IScope scope) {
		return scope.getAgent();
	}

	@Override
	public String getTitle() { return "pseudo-variable super of type " + getGamlType().getTitle(); }

	@Override
	public Doc getDocumentation() {
		return new ConstantDoc("Represents the current agent, instance of species " + type.getTitle()
				+ ", indicating a redirection to the parent species in case of calling an action");
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) {}

	@Override
	public boolean isConst() { return false; }

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

}
