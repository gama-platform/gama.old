/*********************************************************************************************
 *
 * 'ZoomUnitExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.Types;

public class CurrentErrorUnitExpression extends UnitConstantExpression {

	public CurrentErrorUnitExpression(final String doc) {
		super("", Types.STRING, "current_error", doc, null);
	}

	@Override
	public String _value(final IScope scope) {
		final GamaRuntimeException e = scope.getCurrentError();
		if (e == null) { return "nil"; }
		return e.getMessage();
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
