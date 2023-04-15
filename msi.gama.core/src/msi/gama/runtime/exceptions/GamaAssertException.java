/*******************************************************************************************************
 *
 * GamaAssertException.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.exceptions;

import msi.gama.runtime.IScope;

/**
 * The Class GamaAssertException.
 */
public class GamaAssertException extends GamaRuntimeException {

	/**
	 * Instantiates a new gama assert exception.
	 *
	 * @param scope the scope
	 * @param s the s
	 * @param warning the warning
	 */
	public GamaAssertException(final IScope scope, final String s, final boolean warning) {
		super(scope, s, warning);
	}

}
