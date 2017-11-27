package msi.gama.runtime.exceptions;

import msi.gama.runtime.IScope;

public class GamaTestException extends GamaRuntimeException {

	public GamaTestException(final IScope scope, final String s, final boolean warning) {
		super(scope, s, warning);
	}

}
