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
	public boolean isPlainMessage() {
		return false;
	}
	
	@Override
	public GamaMessage getContent(IScope scope)
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

}
