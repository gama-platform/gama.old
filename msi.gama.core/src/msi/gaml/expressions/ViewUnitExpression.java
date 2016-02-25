/*********************************************************************************************
 *
 *
 * 'DisplayHeightUnitExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class ViewUnitExpression extends UnitConstantExpression {

	public ViewUnitExpression(final String unit, final String doc) {
		super(0.0, Types.FLOAT, unit, doc, null);
	}

	@Override
	public Double value(final IScope scope) {
		IGraphics g = scope.getGraphics();
		if ( g == null ) { return 0d; }
		switch (name) {
			case "view_x":
				return -g.getXOffsetInPixels() / g.getxRatioBetweenPixelsAndModelUnits();
			case "view_y":
				return -g.getYOffsetInPixels() / g.getyRatioBetweenPixelsAndModelUnits();
			case "view_width":
				return g.getDisplayWidth() / g.getxRatioBetweenPixelsAndModelUnits();
			case "view_height":
				return g.getDisplayHeight() / g.getyRatioBetweenPixelsAndModelUnits();
		}
		return 0d;
	}

	@Override
	public boolean isConst() {
		return false;

	}

}
