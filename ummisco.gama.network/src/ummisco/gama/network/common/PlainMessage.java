/*********************************************************************************************
 *
 * 'PlainMessage.java, in plugin ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
