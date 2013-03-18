package msi.gaml.descriptions;

import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.GAMA;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;

public class ConstantExpressionDescription extends BasicExpressionDescription {

	public ConstantExpressionDescription(Object object) {
		super(GAMA.getExpressionFactory().createConst(object,
			object == null ? Types.NO_TYPE : Types.get(object.getClass())));
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	public static IExpressionDescription create(Object object) {
		return new ConstantExpressionDescription(object);
	}

	@Override
	public IExpression compile(IDescription context) {
		return expression;
	}

	@Override
	public void setExpression(IExpression expr) {
		GuiUtils.debug("Trying to set a new expression " + expr + " to description " + expression);
	}

}
