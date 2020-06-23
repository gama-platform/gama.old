/*********************************************************************************************
 *
 *
 * 'Message.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class FIPAMessage extends GamaMessage {

	/** The message. */
	private MessageData data;

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
		super(scope, null, null, null);
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
		super(scope, m.getData().getSender(), null, m.getData().getContent());
		getData().setContent(m.getData().getContent());
		getData().setConversation(m.getConversation());
		getData().setReceivers(m.getData().getReceivers());
		getData().setPerformativeName(m.getData().getPerformativeName());
		getData().setSender(m.getData().getSender());

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
	public FIPAMessage(final IAgent sender, final IList<IAgent> receivers, final IList content,
			final Performative performative, final Conversation conversation) throws GamaRuntimeException {
		super(sender.getScope(), sender, null, content);
		setSender(sender);
		getData().setReceivers(receivers);
		getData().setPerformative(performative);
		getData().setConversation(conversation);
	}

	public Conversation getConversation() {
		return getData().getConversation();
	}

	@Override
	public IList<IAgent> getReceivers() {
		return getData().getReceivers();
	}

	// @Override
	// public FIPAMessage clone() {
	// final FIPAMessage m = new FIPAMessage(getSender(), getReceivers(),
	// (IList<IAgent>) getContents(null),
	// getPerformative(), getConversation());
	// return m;
	// }

	/**
	 * Gets the contents of the message.
	 *
	 * @return the contents
	 */
	@Override
	@getter (GamaMessage.CONTENTS)
	public Object getContents(final IScope scope) {
		setUnread(false);
		return getData().getContent();
	}

	/**
	 * Sets the contents of the message.
	 *
	 * @param content
	 *            the content
	 */
	@Override
	@setter (GamaMessage.CONTENTS)
	public void setContents(final Object content) {
		getData().setContent((IList) content);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#getMessage()
	 */
	public MessageData getData() {
		if (data == null)
			data = new MessageData();
		return data;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#getSender()
	 */
	@Override
	@getter (FIPAMessage.SENDER)
	public IAgent getSender() {
		return getData().getSender();
	}

	/**
	 * Sets the sender.
	 *
	 * @param sender
	 *            the sender
	 */
	@setter (FIPAMessage.SENDER)
	public void setSender(final IAgent sender) {
		if (getData() != null)
			getData().setSender(sender);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#getPerformative()
	 */
	public Performative getPerformative() {
		return getData().getPerformative();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#setPerformative(int)
	 */
	public void setPerformative(final Performative performative) {
		getData().setPerformative(performative);
	}

	@Override
	public String toString() {
		return "Proxy on " + getData();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return StringUtils.toGaml(getData().getContent(), includingBuiltIn);
	}

	//
	// @Override
	// public IType type() {
	// return Types.get(MessageType.MESSAGE_ID);
	// }

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return "message[sender: " + getData().getSender() + "; receivers: " + getData().getReceivers()
				+ "; performative: " + getData().getPerformativeName() + "; content: " + getData().getContent()
				+ "; content" + "]";
	}

	@Override
	public FIPAMessage copy(final IScope scope) throws GamaRuntimeException {
		final FIPAMessage m = new FIPAMessage(getSender(), getReceivers(), (IList<IAgent>) getContents(scope),
				getPerformative(), getConversation());
		return m;
	}

	public void setReceivers(final IList receivers) {
		getData().setReceivers(receivers);
	}

	public String getPerformativeName() {
		return getData().getPerformativeName();
	}

	public void setConversation(final Conversation conversation2) {
		getData().setConversation(conversation2);

	}

}
