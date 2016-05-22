package ummisco.gama.network.common;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class PlainMessage extends GamaMessage {

	public PlainMessage(IScope scope, Object sender, Object receivers, Object content) throws GamaRuntimeException {
		super(scope, sender, receivers, content);
		// TODO Auto-generated constructor stub
	}
	
}
