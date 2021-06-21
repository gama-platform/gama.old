/*******************************************************************************************************
 *
 * msi.gaml.expressions.CameraPositionUnitExpression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class CameraPositionUnitExpression extends UnitConstantExpression {

	public CameraPositionUnitExpression(final String doc) {
		super(new GamaPoint(), Types.POINT, "camera_location", doc, null);
	}

	@Override
	public ILocation _value(final IScope scope) {
		final IGraphics g = scope.getGraphics();
		if (g == null) {
			Iterable<IDisplaySurface> surfaces = scope.getGui().getAllDisplaySurfaces();
			// Returns a clone to avoid any side effect
			if (Iterables.size(surfaces) == 1) return Iterables.get(surfaces, 0).getData().getCameraPos().clone();
		} else if (!g.is2D()) return ((IGraphics.ThreeD) g).getCameraPos().copy(scope);
		return null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
