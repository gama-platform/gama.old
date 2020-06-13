/*********************************************************************************************
 *
 *
 * 'MessageData.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.IList;

/**
 * The Message class represents the piece of information transfered between agents capable of communicating.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class MessageData {

	/** The name of sender. */
	private IAgent sender;

	/** The name of all receivers. */
	private IList<IAgent> receivers;

	/** The content of the message. */
	private IList content;

	/** The performative of the message (defined by the FIPA). */
	private Performative performative;

	/** The associated conversation. */
	private Conversation conversation;

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getSender()
	 */
	public IAgent getSender() {
		return sender;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setSender(msi.gama.metamodel.agent .interfaces.BasicEntity)
	 */
	public void setSender(final IAgent sender) {
		this.sender = sender;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getReceivers()
	 */
	public IList getReceivers() {
		return receivers;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setReceivers(java.util.List)
	 */
	public void setReceivers(final IList receivers) {
		this.receivers = receivers;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getContent()
	 */
	public IList getContent() {
		return content;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setContent(java.lang.String)
	 */
	public void setContent(final IList content) {
		if (content != null) {
			this.content = content;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getPerformative()
	 */
	public String getPerformativeName() {
		return performative.name();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#getPerformative()
	 */
	public Performative getPerformative() {
		return performative;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setPerformative(java.lang.String)
	 */
	public void setPerformativeName(final String performative) {
		this.performative = Performative.valueOf(performative);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#setPerformative(int)
	 */
	public void setPerformative(final Performative performative) {
		this.performative = performative;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getConversation()
	 */
	public Conversation getConversation() {
		return conversation;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setConversation(msi.gama.metamodel .agent.interfaces.BasicEntity)
	 */
	public void setConversation(final Conversation conv) {
		conversation = conv;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer retVal = new StringBuffer();
		retVal.append("Message[sender : " + sender + ", receivers : " + receivers + ", conversation : " + conversation
				+ ", performative : " + performative + "]");
		return retVal.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getMessage()
	 */
	public MessageData getMessage() {
		return this;
	}

}
