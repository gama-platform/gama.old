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

import msi.gama.common.util.StringUtils;
import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;

/**
 * The Class MessageProxy.
 *
 * @author drogoul
 */

public class FIPAMessage extends GamaMessage implements Cloneable {

	/** The message. */
	private final MessageData data = new MessageData();

	/** The unread. */

	private Conversation conversation;

	/**
	 * @throws GamaRuntimeException
	 *             Instantiates a new message proxy.
	 *
	 * @param sim
	 *            the sim
	 * @param s
	 *            the s
	 *
	 * @throws GamlException
	 *             the gaml exception
	 */
	public FIPAMessage(final IScope scope) {
		super(scope, null, null);
	}

	/**
	 * @throws GamaRuntimeException
	 *             Instantiates a new message proxy.
	 *
	 * @param sim
	 *            the sim
	 * @param m
	 *            the m
	 *
	 * @throws GamlException
	 *             the gaml exception
	 */
	public FIPAMessage(final IScope scope, final FIPAMessage m) throws GamaRuntimeException {
		super(scope, m.data.getSender(), m.data.getContent());
		data.setContent(m.data.getContent());
		data.setConversation(m.getConversation());
		data.setReceivers(m.data.getReceivers());
		data.setPerformativeName(m.data.getPerformativeName());
		data.setSender(m.data.getSender());

	}

	/**
	 * @throws GamaRuntimeException
	 *             Instantiates a new message proxy.
	 *
	 * @param sim
	 *            the sim
	 * @param sender
	 *            the sender
	 * @param receivers
	 *            the receivers
	 * @param content
	 *            the content
	 * @param performative
	 *            the performative
	 * @param conversation
	 *            the conversation
	 *
	 * @throws GamlException
	 *             the gaml exception
	 */
	public FIPAMessage(final IAgent sender, final IList<IAgent> receivers, final IList content, final int performative,
			final Conversation conversation) throws GamaRuntimeException {
		super(sender.getScope(), sender, content);
		this.conversation = conversation;
		setSender(sender);
		data.setReceivers(receivers);
		data.setPerformative(performative);
	}

	public Conversation getConversation() {
		return conversation;
	}

	public IList<IAgent> getReceivers() {
		return data.getReceivers();
	}

	@Override
	public FIPAMessage clone() {
		return new FIPAMessage(getSender(), data.getReceivers(), (IList<IAgent>) getContents(), getPerformative(),
				conversation);
	}

	/**
	 * Gets the contents of the message.
	 *
	 * @return the contents
	 */
	@Override
	@getter(GamaMessage.CONTENTS)
	public Object getContents() {
		setUnread(false);
		return data.getContent();
	}

	/**
	 * Sets the contents of the message.
	 *
	 * @param content
	 *            the content
	 */
	@Override
	@setter(GamaMessage.CONTENTS)
	public void setContents(final Object content) {
		if (data != null)
			data.setContent((IList) content);
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
	@Override
	@getter(FIPAMessage.SENDER)
	public IAgent getSender() {
		return data.getSender();
	}

	/**
	 * Sets the sender.
	 *
	 * @param sender
	 *            the sender
	 */
	@setter(FIPAMessage.SENDER)
	public void setSender(final IAgent sender) {
		if (data != null)
			data.setSender(sender);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#getPerformative()
	 */
	public int getPerformative() {
		return data.getPerformative();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#setPerformative(int)
	 */
	public void setPerformative(final int performative) {
		data.setPerformative(performative);
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
		return "message[sender: " + data.getSender() + "; receivers: " + data.getReceivers() + "; performative: "
				+ data.getPerformativeName() + "; content: " + data.getContent() + "; content" + "]";
	}

	@Override
	public FIPAMessage copy(final IScope scope) throws GamaRuntimeException {
		return this.clone();
	}

	public void setReceivers(final IList receivers) {
		data.setReceivers(receivers);
	}

	public String getPerformativeName() {
		return data.getPerformativeName();
	}

	public void setConversation(final Conversation conversation2) {
		data.setConversation(conversation2);

	}

}
