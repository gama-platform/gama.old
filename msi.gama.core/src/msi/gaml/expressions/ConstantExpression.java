/*********************************************************************************************
 *
 *
 * 'ConstantExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.Set;
import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gaml.types.*;

/**
 * ConstantValueExpr.
 *
 * @author drogoul 22 août 07
 */

public class ConstantExpression extends AbstractExpression {

	Object value;

	public ConstantExpression(final Object val, final IType t, final String name) {
		value = val;
		type = t;
		setName(name);
	}

	public ConstantExpression(final Object val, final IType t) {
		this(val, t, val == null ? "nil" : val.toString());
	}

	public ConstantExpression(final Object val) {
		this(val, GamaType.of(val));
	}

	@Override
	public Object value(final IScope scope) {
		return value;
	}

	@Override
	public boolean isConst() {
		return true;
	}

	@Override
	public String toString() {
		return value == null ? "nil" : value.toString();
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return StringUtils.toGaml(value, includingBuiltIn);
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "Literal expression of type " + getType().getTitle();
	}

	@Override
	public String getTitle() {
		return literalValue();
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectPlugins(final Set<String> plugins) {

	}

}
