/*******************************************************************************************************
 *
 * FullScreenExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.units;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gaml.types.Types;

/**
 * The Class ZoomUnitExpression.
 */
public class FullScreenExpression extends UnitConstantExpression {

	/**
	 * Instantiates a new zoom unit expression.
	 *
	 * @param name
	 *            the name
	 * @param doc
	 *            the doc
	 */
	public FullScreenExpression(final String name, final String doc) {
		super(1.0, Types.BOOL, name, doc, null);
	}

	@Override
	public Boolean _value(final IScope scope) {
		if (!scope.isGraphics()) return false;
		final IGraphics g = ((IGraphicsScope) scope).getGraphics();
		if (g == null) return false;
		IDisplaySurface surface = g.getSurface();
		if (surface == null) return false;
		LayeredDisplayOutput output = surface.getOutput();
		if (output == null) return false;
		IGamaView.Display view = output.getView();
		if (view == null) return false;
		return view.isFullScreen();
	}

	@Override
	public boolean isConst() { return false; }

	@Override
	public boolean isContextIndependant() { return false; }

	@Override
	public boolean isAllowedInParameters() { return false; }

}
