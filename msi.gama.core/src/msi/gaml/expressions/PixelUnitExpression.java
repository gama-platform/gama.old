/*********************************************************************************************
 *
 * 'PixelUnitExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
		final double ratio = g.getyRatioBetweenPixelsAndModelUnits();
		if (ratio == 0d) { return 1d; }
		final Double v = 1d / ratio;
		return v;
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
