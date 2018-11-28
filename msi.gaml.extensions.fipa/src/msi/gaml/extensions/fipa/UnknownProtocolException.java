/*******************************************************************************************************
 *
 * msi.gaml.extensions.fipa.UnknownProtocolException.java, in plugin msi.gaml.extensions.fipa,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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
	 * @param message
	 *            the message
	 */
	protected UnknownProtocolException(final IScope scope, final String message) {
		super(scope, "Unknown protocol: " + message);
	}
}
