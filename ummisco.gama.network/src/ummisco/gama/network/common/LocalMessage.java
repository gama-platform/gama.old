/*******************************************************************************************************
 *
 * LocalMessage.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.common;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.runtime.IScope;

/**
 * The Class LocalMessage.
 */
public class LocalMessage implements ConnectorMessage {
	
	/** The internal message. */
	private Object internalMessage;
	
	/** The receiver. */
	private String receiver;
	
	/** The sender. */
	private String sender;
	
	
	/**
	 * Instantiates a new local message.
	 *
	 * @param sender the sender
	 * @param receiver the receiver
	 * @param ct the ct
	 */
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
		
		return false;
	}

}
