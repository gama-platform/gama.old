/*********************************************************************************************
 * 
 * 
 * 'CommunicatingException.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The CommunicatingException.
 */
public class CommunicatingException extends GamaRuntimeException {

	/** Constant field serialVersionUID. */
	private static final long serialVersionUID = -3158933849501997451L;

	/**
	 * Instantiates a new communicating exception.
	 * 
	 * @param message the message
	 */
	protected CommunicatingException(final IScope scope, final String message) {
		super(scope, message, true);
	}

}
