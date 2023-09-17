/*******************************************************************************************************
 *
 * BasicExpressionDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.util.StringUtils;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.types.TypeExpression;
import msi.gaml.types.IType;
import msi.gaml.types.ITypesManager;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class BasicExpressionDescription.
 */
public class BasicExpressionDescription implements IExpressionDescription {

	static {
		DEBUG.OFF();
	}

	/** The expression. */
	protected IExpression expression;

	/** The target. */
	protected EObject target;

	/**
	 * Instantiates a new basic expression description.
	 *
	 * @param expr
	 *            the expr
	 */
	public BasicExpressionDescription(final IExpression expr) {
		expression = expr;
	}

	/**
	 * Instantiates a new basic expression description.
	 *
	 * @param object
	 *            the object
	 */
	public BasicExpressionDescription(final EObject object) {
		target = object;
	}

	@Override
	public String toString() {
		return serialize(false);
	}

	/**
	 * To own string.
	 *
	 * @return the string
	 */
	public String toOwnString() {
		return target.toString();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return expression == null ? toOwnString() : expression.serialize(includingBuiltIn);
	}

	@Override
	public boolean equals(final Object c) {
		if (c == null) return false;
		if (c == this) return true;
		if (c instanceof IExpressionDescription) return ((IExpressionDescription) c).equalsString(toString());
		return false;
	}

	@Override
	public IExpression getExpression() { return expression; }

	@Override
	public void dispose() {
		expression = null;
		target = null;
	}

	@Override
	public void setExpression(final IExpression expr) { expression = expr; }

	@Override
	public IExpression compile(final IDescription context) {
		if (expression == null) { expression = GAML.getExpressionFactory().createExpr(this, context); }
		return expression;
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#compileAsLabel()
	 */
	@Override
	public IExpressionDescription compileAsLabel() {
		final IExpressionDescription newEd = LabelExpressionDescription.create(StringUtils.toJavaString(toString()));
		newEd.setTarget(getTarget());
		return newEd;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#equalsString(java.lang.String)
	 */
	@Override
	public boolean equalsString(final String o) {
		return o == null ? false : o.equals(toString());
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
	 */
	@Override
	public EObject getTarget() { return target; }

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void setTarget(final EObject newTarget) {
		if (target == null) { target = newTarget; }
	}

	@Override
	public boolean isConst() { return false; }

	@Override
	public Collection<String> getStrings(final IDescription context, final boolean skills) {
		return Collections.EMPTY_SET;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		final IExpressionDescription result = new BasicExpressionDescription(expression);
		result.setTarget(target);
		return result;
	}

	@Override
	public IType<?> getDenotedType(final IDescription context) {
		compile(context);
		if (expression == null) return Types.NO_TYPE;
		if (expression instanceof TypeExpression) return ((TypeExpression) expression).getDenotedType();
		IType type = expression.getGamlType();
		// if (type == Types.STRING && expression.isConst())
		// return context.getTypeNamed(GamaStringType.staticCast(null, expression.getConstValue(), true));

		ModelDescription md = context.getModelDescription();
		if (md != null) {
			final ITypesManager tm = md.getTypesManager();
			final String s = expression.literalValue();
			if (tm.containsType(s))
				return tm.get(s);
		}
		return type;
	}

}
