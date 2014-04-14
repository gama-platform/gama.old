/*********************************************************************************************
 * 
 * 
 * 'ConversationFinishedException.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
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
 * ConversationFinishedException is thrown when a message is added to an already finished
 * conversation.
 */
public class ConversationFinishedException extends CommunicatingException {

	/** Constant field serialVersionUID. */
	private static final long serialVersionUID = -4582844650106730326L;

	/**
	 * Instantiates a new conversation finished exception.
	 * 
	 * @param message the message
	 */
	protected ConversationFinishedException(final IScope scope, final String message) {
		super(scope, message);
	}
}
