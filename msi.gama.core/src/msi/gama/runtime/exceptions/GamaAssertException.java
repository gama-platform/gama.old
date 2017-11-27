package msi.gama.runtime.exceptions;

import msi.gama.runtime.IScope;

public class GamaAssertException extends GamaRuntimeException {

	public GamaAssertException(final IScope scope, final String s, final boolean warning) {
		super(scope, s, warning);
	}

}
