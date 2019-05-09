/*********************************************************************************************
 *
 * 'LocalMessage.java, in plugin ummisco.gama.network, is part of the source code of the
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

public class LocalMessage implements ConnectorMessage {
	private Object internalMessage;
	private String receiver;
	private String sender;
	
	
	public LocalMessage(String sender, String receiver, Object ct)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.internalMessage = ct;
	}
	
	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public String getReceiver() {
		return receiver;
	}
	
	@Override
	public String getPlainContents() {
		return this.internalMessage.toString();
	}

	@Override
	public boolean isPlainMessage() {
		return false;
	}
	
	@Override
	public GamaMessage getContents(IScope scope)
	{
		GamaMessage message = null;
		if(internalMessage instanceof GamaMessage) {
			message = (GamaMessage) internalMessage;
		}
		else
			message = new GamaMessage(scope, sender, receiver, internalMessage);
		message.hasBeenReceived(scope);
		return message;
	}

	@Override
	public boolean isCommandMessage() {
		// TODO Auto-generated method stub
		return false;
	}

}
