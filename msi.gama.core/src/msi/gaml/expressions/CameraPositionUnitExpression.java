/*********************************************************************************************
 *
 * 'CameraPositionUnitExpression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class CameraPositionUnitExpression extends UnitConstantExpression {

	public CameraPositionUnitExpression(final String doc) {
		super(GamaPoint.NULL_POINT, Types.POINT, "camera_location", doc, null);
	}

	@Override
	public ILocation value(final IScope scope) {
		if (scope == null)
			return GamaPoint.NULL_POINT;
		final IGraphics g = scope.getGraphics();
		if (g == null) {
			return GamaPoint.NULL_POINT;
		}
		return g.getCameraPos();
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
