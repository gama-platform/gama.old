/*********************************************************************************************
 * 
 * 
 * 'InvalidConversationException.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
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
 * The Class InvalidConversationException.
 */
public class InvalidConversationException extends CommunicatingException {

	/** Constant field serialVersionUID. */
	private static final long serialVersionUID = -1332296136464201236L;

	/**
	 * Instantiates a new invalid conversation exception.
	 * 
	 * @param s the s
	 */
	protected InvalidConversationException(final IScope scope, final String s) {
		super(scope, s);
	}
}
