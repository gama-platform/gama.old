/*******************************************************************************************************
 *
 * msi.gaml.expressions.CameraTargetUnitExpression.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class CameraTargetUnitExpression extends UnitConstantExpression {

	public CameraTargetUnitExpression(final String doc) {
		super(GamaPoint.NULL_POINT, Types.POINT, "camera_target", doc, null);
	}

	@Override
	public ILocation _value(final IScope scope) {
		if (scope == null) { return GamaPoint.NULL_POINT; }
		final IGraphics g = scope.getGraphics();
		if (g == null || g.is2D()) { return GamaPoint.NULL_POINT; }
		return ((IGraphics.ThreeD) g).getCameraTarget();
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
