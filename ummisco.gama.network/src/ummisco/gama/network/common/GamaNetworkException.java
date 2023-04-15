/*******************************************************************************************************
 *
 * GamaNetworkException.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.common;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class GamaNetworkException.
 */
public class GamaNetworkException extends GamaRuntimeException {

	/** The connection failure. */
	public static String CONNECTION_FAILURE = "Network cannot be reached! Check that your server is connected.";
	
	/** The disconnection failure. */
	public static String DISCONNECTION_FAILURE = "Cannot be disconnected!";
	
	/** The subscribe failure. */
	public static String SUBSCRIBE_FAILURE = "Cannot subscribe to the expected topic!";
	
	/** The sending failure. */
	public static String SENDING_FAILURE = "Cannot send the message to agent!";

	/**
	 * Instantiates a new gama network exception.
	 *
	 * @param scope the scope
	 * @param s the s
	 * @param warning the warning
	 */
	protected GamaNetworkException(final IScope scope, final String s, final boolean warning) {
		super(scope, s, warning);

	}

	/**
	 * Cannot be disconnected failure.
	 *
	 * @param s the s
	 * @return the gama network exception
	 */
	public static GamaNetworkException cannotBeDisconnectedFailure(final IScope s) {
		return new GamaNetworkException(s, DISCONNECTION_FAILURE, false);
	}

	/**
	 * Cannot be connected failure.
	 *
	 * @param s the s
	 * @return the gama network exception
	 */
	public static GamaNetworkException cannotBeConnectedFailure(final IScope s) {
		return new GamaNetworkException(s, CONNECTION_FAILURE, false);
	}

	/**
	 * Cannot subscribe to topic.
	 *
	 * @param s the s
	 * @param text the text
	 * @return the gama network exception
	 */
	public static GamaNetworkException cannotSubscribeToTopic(final IScope s, final String text) {
		return new GamaNetworkException(s, SUBSCRIBE_FAILURE + "\n" + text, false);
	}

	/**
	 * Cannot send message.
	 *
	 * @param s the s
	 * @param destName the dest name
	 * @return the gama network exception
	 */
	public static GamaNetworkException cannotSendMessage(final IScope s, final String destName) {
		return new GamaNetworkException(s, SENDING_FAILURE + " to " + destName, false);
	}

	/**
	 * Cannot unsuscribe to topic.
	 *
	 * @param s the s
	 * @param destName the dest name
	 * @return the gama network exception
	 */
	public static GamaNetworkException cannotUnsuscribeToTopic(final IScope s, final String destName) {
		return new GamaNetworkException(s, SENDING_FAILURE + " to " + destName, false);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
