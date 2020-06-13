/*******************************************************************************************************
 *
 * msi.gaml.expressions.DisplayWidthUnitExpression.java, in plugin msi.gama.core,
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

public class DisplayWidthUnitExpression extends UnitConstantExpression {

	public DisplayWidthUnitExpression(final String doc) {
		super(0.0, Types.FLOAT, "display_width", doc, null);
	}

	@Override
	public Double _value(final IScope scope) {
		final IGraphics g = scope.getGraphics();
		if (g == null) { return 0d; }
		return (double) g.getDisplayWidth();
		// return (double) g.getEnvironmentWidth();
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
