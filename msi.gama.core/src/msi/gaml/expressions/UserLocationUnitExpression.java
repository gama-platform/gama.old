/*********************************************************************************************
 *
 * 'UserLocationUnitExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
	public ILocation _value(final IScope scope) {
		return scope.getGui().getMouseLocationInModel();
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
