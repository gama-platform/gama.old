/*********************************************************************************************
 * 
 *
 * 'PixelUnitExpression.java', in plugin 'msi.gama.core', is part of the source code of the 
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
import msi.gaml.types.*;

public class PixelUnitExpression extends ConstantExpression {

	public PixelUnitExpression() {
		super("px", Types.get(IType.FLOAT));
	}

	@Override
	public Double value(final IScope scope) {
		IGraphics g = scope.getGraphics();
		if ( g == null ) { return 0d; }
		Double v = (double) g.getEnvironmentHeight() / g.getDisplayHeightInPixels();
		// GuiUtils.debug("PixelUnitExpression.value" + v);
		return v;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String toGaml() {
		return "°px";
	}

}
