package ummisco.gama.network.common;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.DenotedActionExpression;

public class GamaNetworkException extends GamaRuntimeException {

	public static String CONNECTION_FAILURE = "Network cannot be reach!";
	public static String DISCONNECTION_FAILURE = "Cannot be disconnected!";
	public static String SUBSCRIBE_FAILURE = "Cannot subscribe to the expected topic!";
	public static String SENDING_FAILURE = "Cannot send the message to agent!";
	public static String UNSUSCRIBE_FAILURE = "Cannot unsuscribe to topic!";
	
	protected GamaNetworkException(IScope scope, String s, boolean warning) {
		super(scope, s, warning);
		
	}
	
	public static GamaNetworkException cannotBeDisconnectedFailure(IScope s)
	{
		return new GamaNetworkException(s, DISCONNECTION_FAILURE, false);
	}
	public static GamaNetworkException cannotBeConnectedFailure(IScope s)
	{
		return new GamaNetworkException(s, CONNECTION_FAILURE, false);
	}
	public static GamaNetworkException cannotSubscribeToTopic(IScope s, String text)
	{
		return new GamaNetworkException(s, SUBSCRIBE_FAILURE + "\n"+text, false);
	}
	public static GamaNetworkException cannotSendMessage(IScope s, String destName)
	{
		return new GamaNetworkException(s, SENDING_FAILURE+" to "+destName, false);
	}
	public static GamaNetworkException cannotUnsuscribeToTopic(IScope s, String destName)
	{
		return new GamaNetworkException(s, SENDING_FAILURE+" to "+destName, false);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
