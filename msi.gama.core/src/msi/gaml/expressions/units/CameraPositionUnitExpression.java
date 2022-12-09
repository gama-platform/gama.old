/*******************************************************************************************************
 *
 * CameraPositionUnitExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.units;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gaml.types.Types;

/**
 * The Class CameraPositionUnitExpression.
 */
public class CameraPositionUnitExpression extends UnitConstantExpression {

	/**
	 * Instantiates a new camera position unit expression.
	 *
	 * @param doc
	 *            the doc
	 */
	public CameraPositionUnitExpression(final String doc) {
		super(new GamaPoint(), Types.POINT, "camera_location", doc, null);
	}

	@Override
	public GamaPoint _value(final IScope sc) {
		if (sc == null || !sc.isGraphics()) {
			IDisplaySurface surface = GAMA.getGui().getFrontmostDisplaySurface();
			if (surface != null) return surface.getData().getCameraPos().yNegated();
			return null;
		}
		IGraphicsScope scope = (IGraphicsScope) sc;
		final IGraphics g = scope.getGraphics();
		if (g.is2D()) return null;
		return ((IGraphics.ThreeD) g).getCameraPos().yNegated();

	}

	@Override
	public boolean isConst() { return false; }

}
