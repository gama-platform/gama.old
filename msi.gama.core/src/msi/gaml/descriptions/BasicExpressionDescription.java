/*********************************************************************************************
 *
 *
 * 'BasicExpressionDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.*;
import org.eclipse.emf.ecore.EObject;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlProperties;
import msi.gama.util.GAML;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

public class BasicExpressionDescription implements IExpressionDescription {

	protected IExpression expression;
	protected EObject target;

	public BasicExpressionDescription(final IExpression expr) {
		expression = expr;
	}

	public BasicExpressionDescription(final EObject object) {
		target = object;
	}

	@Override
	public String toString() {
		return serialize(false);
	}

	public String toOwnString() {
		return target.toString();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return expression == null ? toOwnString() : expression.serialize(includingBuiltIn);
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		if ( expression != null ) {
			expression.collectMetaInformation(meta);
		}
	}

	@Override
	public boolean equals(final Object c) {
		if ( c == null ) { return false; }
		if ( c == this ) { return true; }
		if ( c instanceof IExpressionDescription ) { return ((IExpressionDescription) c).equalsString(toString()); }
		return false;
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

	@Override
	public void dispose() {
		expression = null;
		target = null;
	}

	@Override
	public void setExpression(final IExpression expr) {
		expression = expr;
	}

	@Override
	public IExpression compile(final IDescription context) {
		if ( expression == null ) {
			expression = GAML.getExpressionFactory().createExpr(this, context);
		}
		return expression;
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#compileAsLabel()
	 */
	@Override
	public IExpressionDescription compileAsLabel() {
		IExpressionDescription newEd = LabelExpressionDescription.create(StringUtils.toJavaString(toString()));
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
	public EObject getTarget() {
		return target;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void setTarget(final EObject newTarget) {
		if ( target == null ) {
			target = newTarget;
		}
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		return Collections.EMPTY_SET;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		IExpressionDescription result = new BasicExpressionDescription(expression);
		result.setTarget(target);
		return result;
	}

	@Override
	public IType getDenotedType(final IDescription context) {
		compile(context);
		if ( expression instanceof TypeExpression ) { return expression.getType(); }
		if ( expression instanceof ConstantExpression ) { return context.getTypeNamed(expression.literalValue()); }
		return Types.NO_TYPE;
	}

}
