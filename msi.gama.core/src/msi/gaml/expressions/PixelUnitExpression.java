/*******************************************************************************************************
 *
 * msi.gaml.expressions.PixelUnitExpression.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class PixelUnitExpression extends UnitConstantExpression {

	public PixelUnitExpression(final String name, final String doc) {
		super(1.0, Types.FLOAT, name, doc, new String[] { "pixels", "px" });
	}

	@Override
	public Double _value(final IScope scope) {
		if (scope == null) { return 1d; }
		final IGraphics g = scope.getGraphics();
		if (g == null) { return 1d; }
		double ratio;
		if (scope.isHorizontalPixelContext()) {
			ratio = g.getxRatioBetweenPixelsAndModelUnits();
		} else {
			ratio = g.getyRatioBetweenPixelsAndModelUnits();
		}
		if (ratio == 0d) { return 1d; }
		final Double v = 1d / ratio;
		return v;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isContextIndependant() {
		return false;
	}

}
