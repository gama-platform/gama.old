/*********************************************************************************************
 * 
 * 
 * 'ConstantExpressionDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.descriptions;

import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;

public class ConstantExpressionDescription extends BasicExpressionDescription {

	private ConstantExpressionDescription(final Object object) {
		this(object, GamaType.of(object));
	}

	private ConstantExpressionDescription(final Object object, final IType type) {
		this(GAML.getExpressionFactory().createConst(object, type));
	}

	private ConstantExpressionDescription(final IExpression expr) {
		super(expr);
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public IExpression compile(final IDescription context) {
		return expression;
	}

	@Override
	public void setExpression(final IExpression expr) {
		// scope.getGui().debug("Trying to set a new expression " + expr + " to description " + expression);
	}

	@Override
	public IExpressionDescription cleanCopy() {
		IExpressionDescription copy = new ConstantExpressionDescription(expression);
		copy.setTarget(target);
		return copy;
	}

	public static IExpressionDescription create(final Object object) {
		return new ConstantExpressionDescription(object);
	}

	public static IExpressionDescription create(final Integer i) {
		return new ConstantExpressionDescription(i, Types.INT);
	}

	public static IExpressionDescription create(final Double d) {
		return new ConstantExpressionDescription(d, Types.FLOAT);
	}

	public static IExpressionDescription create(final Boolean b) {
		return new ConstantExpressionDescription(b, Types.BOOL);
	}

	@Override
	public IType getDenotedType(final IDescription context) {
		return context.getTypeNamed(expression.literalValue());
	}

}
