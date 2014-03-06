package msi.gaml.descriptions;

import msi.gama.common.util.GuiUtils;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;

public class ConstantExpressionDescription extends BasicExpressionDescription {

	private ConstantExpressionDescription(final Object object) {
		this(object, object == null ? Types.NO_TYPE : Types.get(object.getClass()));
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
		GuiUtils.debug("Trying to set a new expression " + expr + " to description " + expression);
	}

	@Override
	public IExpressionDescription cleanCopy() {
		// TODO Could also return this, as these expressions are not supposed to depend on the model
		IExpressionDescription copy = new ConstantExpressionDescription(expression);
		copy.setTarget(target);
		return copy;
	}

	public static IExpressionDescription create(final Object object) {
		return new ConstantExpressionDescription(object);
	}

	public static IExpressionDescription create(final Integer i) {
		return new ConstantExpressionDescription(i, Types.get(IType.INT));
	}

	public static IExpressionDescription create(final Double d) {
		return new ConstantExpressionDescription(d, Types.get(IType.FLOAT));
	}

	public static IExpressionDescription create(final Boolean b) {
		return new ConstantExpressionDescription(b, Types.get(IType.BOOL));
	}

	@Override
	public IType getDenotedType(final IDescription context) {
		return context.getTypeNamed(expression.literalValue());
	}

}
