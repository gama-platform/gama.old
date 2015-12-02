/*********************************************************************************************
 * 
 * 
 * 'Message.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.types.*;

/**
 * The Class MessageProxy.
 * 
 * @author drogoul
 */

@vars({ @var(name = Message.SENDER, type = IType.AGENT), @var(name = Message.RECEIVERS, type = IType.LIST),
	@var(name = Message.PERFORMATIVE, type = IType.STRING), @var(name = Message.CONTENT, type = IType.LIST),
	@var(name = Message.UNREAD, type = IType.BOOL, init = IKeyword.TRUE),
	@var(name = Message.CONVERSATION, type = ConversationType.CONV_ID),
	@var(name = Message.PROTOCOL, type = IType.STRING, depends_on = Message.CONVERSATION),
	@var(name = Message.TIMESTAMP, type = IType.STRING) })
public class Message implements IValue, Cloneable {

	public final static String CONVERSATION = "current_conversation";
	public final static String CONTENT = "content";
	public final static String UNREAD = "unread";
	public final static String PROTOCOL = "protocol";
	public final static String TIMESTAMP = "timestamp";
	public final static String RECEIVERS = "receivers";
	public final static String PERFORMATIVE = "performative";
	public final static String SENDER = "sender";

	/** The message. */
	private final MessageData data;

	/** The unread. */
	private boolean unread = true;

	/**
	 * @throws GamaRuntimeException Instantiates a new message proxy.
	 * 
	 * @param sim the sim
	 * @param s the s
	 * 
	 * @throws GamlException the gaml exception
	 */
	public Message() {
		data = new MessageData();
	}

	/**
	 * @throws GamaRuntimeException Instantiates a new message proxy.
	 * 
	 * @param sim the sim
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	public Message(final Message m) throws GamaRuntimeException {
		data = m.getData();
	}

	/**
	 * @throws GamaRuntimeException Instantiates a new message proxy.
	 * 
	 * @param sim the sim
	 * @param sender the sender
	 * @param receivers the receivers
	 * @param content the content
	 * @param performative the performative
	 * @param conversation the conversation
	 * 
	 * @throws GamlException the gaml exception
	 */
	public Message(final IAgent sender, final IList<IAgent> receivers, final IList content, final int performative,
		final Conversation conversation) throws GamaRuntimeException {
		data = new MessageData();
		setSender(sender);
		setReceivers(receivers);
		setContent(content);
		setPerformative(performative);
		setConversation(conversation);
	}

	@Override
	public Message clone() {
		return new Message(getSender(), getReceivers(), getContent(), getPerformative(), getConversation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#getMessage()
	 */
	public MessageData getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#getSender()
	 */
	@getter(Message.SENDER)
	public IAgent getSender() {
		return data.getSender();
	}

	/**
	 * Sets the sender.
	 * 
	 * @param sender the sender
	 */
	@setter(Message.SENDER)
	public void setSender(final IAgent sender) {
		data.setSender(sender);
	}

	/**
	 * Gets the receivers.
	 * 
	 * @return the receivers a list of the receivers' name.
	 */
	@getter(Message.RECEIVERS)
	public IList<IAgent> getReceivers() {
		return data.getReceivers();
	}

	/**
	 * Sets the intended receivers.
	 * 
	 * @param receivers the list of the receivers' name.
	 */
	@setter(Message.RECEIVERS)
	public void setReceivers(final IList receivers) {
		data.setReceivers(receivers);
	}

	/**
	 * Gets the contents of the message.
	 * 
	 * @return the contents
	 */
	@getter(Message.CONTENT)
	public IList getContent() {
		// OutputManager.debug("Message " + getName() + " is read.");
		setUnread(false);
		return data.getContent();
	}

	/**
	 * Sets the contents of the message.
	 * 
	 * @param content the content
	 */
	@setter(Message.CONTENT)
	public void setContent(final IList content) {
		data.setContent(content);
	}

	/**
	 * Gets the performative.
	 * 
	 * @return the performative
	 */
	@getter(Message.PERFORMATIVE)
	public String getPerformativeName() {
		return FIPAConstants.performativeNames[data.getPerformative()];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#getPerformative()
	 */
	public int getPerformative() {
		return data.getPerformative();
	}

	/**
	 * Sets the performative.
	 * 
	 * @param performative the performative to set
	 */
	@setter(Message.PERFORMATIVE)
	public void setPerformativeName(final String performative) {
		data.setPerformative(CommunicatingSkill.performativeIndexes.get(performative));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#setPerformative(int)
	 */
	public void setPerformative(final int performative) {
		data.setPerformative(performative);
	}

	/**
	 * Gets the conversation.
	 * 
	 * @return the conversationID
	 */
	@getter(Message.CONVERSATION)
	public Conversation getConversation() {
		return data.getConversation();
	}

	/**
	 * Sets the conversation.
	 * 
	 * @param conv the conv
	 */
	@setter(Message.CONVERSATION)
	public void setConversation(final Conversation conv) {
		data.setConversation(conv);
	}

	/**
	 * Checks if is unread.
	 * 
	 * @return true, if is unread
	 */
	@getter(Message.UNREAD)
	public boolean isUnread() {
		return unread;
	}

	/**
	 * Sets the unread.
	 * 
	 * @param unread the new unread
	 */
	@setter(Message.UNREAD)
	public void setUnread(final boolean unread) {
		this.unread = unread;
	}

	/**
	 * Gets the protocol name.
	 * 
	 * @return the protocol name
	 */
	@getter(Message.PROTOCOL)
	public String getProtocolName() {
		if ( getConversation() == null ) { return null; }
		return getConversation().getProtocolName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#getTimestamp()
	 */
	@getter(Message.TIMESTAMP)
	public String getTimestamp() {
		return data.getTimestamp();
	}

	@Override
	public String toString() {
		return "Proxy on " + data;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return StringUtils.toGaml(data.getContent(), includingBuiltIn);
	}

	//
	// @Override
	// public IType type() {
	// return Types.get(MessageType.MESSAGE_ID);
	// }

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return "message[sender: " + data.getSender() + "; receivers: " + data.getReceivers() + "; performative: " +
			data.getPerformativeName() + "; content: " + data.getContent() + "; content" + "]";
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return this.clone();
	}

	/**
	 * Method getType()
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType getType() {
		return Types.get(MessageType.MESSAGE_ID);
	}

}
