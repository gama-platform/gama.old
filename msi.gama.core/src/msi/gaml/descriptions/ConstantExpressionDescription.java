package msi.gaml.descriptions;

import msi.gama.common.util.GuiUtils;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;

public class ConstantExpressionDescription extends BasicExpressionDescription {

	private ConstantExpressionDescription(Object object) {
		this(object, object == null ? Types.NO_TYPE : Types.get(object.getClass()));
	}

	private ConstantExpressionDescription(Object object, IType type) {
		super(GAML.getExpressionFactory().createConst(object, type));
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public IExpression compile(IDescription context) {
		return expression;
	}

	@Override
	public void setExpression(IExpression expr) {
		GuiUtils.debug("Trying to set a new expression " + expr + " to description " + expression);
	}

	public static IExpressionDescription create(Object object) {
		return new ConstantExpressionDescription(object);
	}

	public static IExpressionDescription create(Integer i) {
		return new ConstantExpressionDescription(i, Types.get(IType.INT));
	}

	public static IExpressionDescription create(Double d) {
		return new ConstantExpressionDescription(d, Types.get(IType.FLOAT));
	}

	public static IExpressionDescription create(Boolean b) {
		return new ConstantExpressionDescription(b, Types.get(IType.BOOL));
	}

}
