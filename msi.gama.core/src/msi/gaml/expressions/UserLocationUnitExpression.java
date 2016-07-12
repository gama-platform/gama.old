package msi.gaml.expressions;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class UserLocationUnitExpression extends UnitConstantExpression {

	public UserLocationUnitExpression(final String doc) {
		super(GamaPoint.NULL_POINT, Types.POINT, "user_location", doc, null);
	}

	@Override
	public ILocation value(final IScope scope) {
		return scope.getGui().getMouseLocationInModel();
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
