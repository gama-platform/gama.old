package ummisco.gama.network.skills;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class GamaNetworkException extends GamaRuntimeException {

	public static String CONNECTION_FAILURE = "Network cannot be reach!";
	public static String DISCONNECTION_FAILURE = "Cannot be disconnected!";
	
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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
