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

public class DisplayHeightUnitExpression extends UnitConstantExpression {

	public DisplayHeightUnitExpression(final String doc) {
		super(0.0, Types.FLOAT, "display_height", doc, null);
	}

	@Override
	public Double value(final IScope scope) {
		IGraphics g = scope.getGraphics();
		if ( g == null ) { return 0d; }
		return (double) g.getDisplayHeight();
		// return (double) g.getEnvironmentHeight();
	}

	@Override
	public boolean isConst() {
		return false;

	}

}
