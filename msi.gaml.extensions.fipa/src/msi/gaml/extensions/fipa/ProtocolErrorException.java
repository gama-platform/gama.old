/*********************************************************************************************
 * 
 * 
 * 'ProtocolErrorException.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
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
 * ProtocolErrorException is thrown when a message (which is added to a
 * conversation) doesn't follow the interaction protocol employed by the
 * conversation.
 */
public class ProtocolErrorException extends CommunicatingException {

	/** Constant field serialVersionUID. */
	private static final long serialVersionUID = -1817154936864364995L;

	/**
	 * Instantiates a new protocol error exception.
	 * 
	 * @param message
	 *            the message
	 */
	public ProtocolErrorException(final IScope scope, final String message) {
		super(scope, message);
	}
}
