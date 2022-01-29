/*******************************************************************************************************
 *
 * DisplayHeightUnitExpression.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions.units;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gaml.types.Types;

/**
 * The Class DisplayHeightUnitExpression.
 */
public class DisplayHeightUnitExpression extends UnitConstantExpression {

	/**
	 * Instantiates a new display height unit expression.
	 *
	 * @param doc the doc
	 */
	public DisplayHeightUnitExpression(final String doc) {
		super(0.0, Types.FLOAT, "display_height", doc, null);
	}

	@Override
	public Double _value(final IScope sc) {
		if (sc == null || !sc.isGraphics()) {
			IDisplaySurface surface = GAMA.getGui().getFrontmostDisplaySurface();
			if (surface != null) return surface.getDisplayHeight();
			return 0d;
		}
		IGraphicsScope scope = (IGraphicsScope) sc;
		final IGraphics g = scope.getGraphics();
		if (g == null) return 0d;
		return (double) g.getDisplayHeight();
	}

	@Override
	public boolean isConst() {
		return false;

	}

	@Override
	public boolean isContextIndependant() { return false; }

}
