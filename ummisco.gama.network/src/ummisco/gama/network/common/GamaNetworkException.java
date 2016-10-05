package ummisco.gama.network.common;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GamaNetworkException extends GamaRuntimeException {

	public static String CONNECTION_FAILURE = "Network cannot be reach!";
	public static String DISCONNECTION_FAILURE = "Cannot be disconnected!";
	public static String SUBSCRIBE_FAILURE = "Cannot subscribe to the expected topic!";
	public static String SENDING_FAILURE = "Cannot send the message to agent!";
	public static String UNSUSCRIBE_FAILURE = "Cannot unsuscribe to topic!";

	protected GamaNetworkException(final IScope scope, final String s, final boolean warning) {
		super(scope, s, warning);

	}

	public static GamaNetworkException cannotBeDisconnectedFailure(final IScope s) {
		return new GamaNetworkException(s, DISCONNECTION_FAILURE, false);
	}

	public static GamaNetworkException cannotBeConnectedFailure(final IScope s) {
		return new GamaNetworkException(s, CONNECTION_FAILURE, false);
	}

	public static GamaNetworkException cannotSubscribeToTopic(final IScope s, final String text) {
		return new GamaNetworkException(s, SUBSCRIBE_FAILURE + "\n" + text, false);
	}

	public static GamaNetworkException cannotSendMessage(final IScope s, final String destName) {
		return new GamaNetworkException(s, SENDING_FAILURE + " to " + destName, false);
	}

	public static GamaNetworkException cannotUnsuscribeToTopic(final IScope s, final String destName) {
		return new GamaNetworkException(s, SENDING_FAILURE + " to " + destName, false);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
