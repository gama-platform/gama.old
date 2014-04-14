/*********************************************************************************************
 * 
 * 
 * 'UnknownProtocolException.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import msi.gama.runtime.IScope;

/**
 * UnknownProtocolException is thrown when an interaction protocol is not recognized.
 */
public class UnknownProtocolException extends CommunicatingException {

	/** Constant field serialVersionUID. */
	private static final long serialVersionUID = -4342954392232715335L;

	/**
	 * Instantiates a new unknown protocol exception.
	 * 
	 * @param message the message
	 */
	protected UnknownProtocolException(final IScope scope, final Integer message) {
		super(scope, "Unknown protocol: " + message);
	}
}
