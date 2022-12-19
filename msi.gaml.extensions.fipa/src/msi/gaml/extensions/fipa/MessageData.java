/*******************************************************************************************************
 *
 * MessageData.java, in msi.gaml.extensions.fipa, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

	/**
	 * Gets the name of sender.
	 *
	 * @return the name of sender
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getSender()
	 */
	public IAgent getSender() {
		return sender;
	}

	/**
	 * Sets the name of sender.
	 *
	 * @param sender the new name of sender
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setSender(msi.gama.metamodel.agent .interfaces.BasicEntity)
	 */
	public void setSender(final IAgent sender) {
		this.sender = sender;
	}

	/**
	 * Gets the name of all receivers.
	 *
	 * @return the name of all receivers
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getReceivers()
	 */
	public IList getReceivers() {
		return receivers;
	}

	/**
	 * Sets the name of all receivers.
	 *
	 * @param receivers the new name of all receivers
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setReceivers(java.util.List)
	 */
	public void setReceivers(final IList receivers) {
		this.receivers = receivers;
	}

	/**
	 * Gets the content of the message.
	 *
	 * @return the content of the message
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getContent()
	 */
	public IList getContent() {
		return content;
	}

	/**
	 * Sets the content of the message.
	 *
	 * @param content the new content of the message
	 */
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

	/**
	 * Gets the performative name.
	 *
	 * @return the performative name
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getPerformative()
	 */
	public String getPerformativeName() {
		return performative.name();
	}

	/**
	 * Gets the performative of the message (defined by the FIPA).
	 *
	 * @return the performative of the message (defined by the FIPA)
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#getPerformative()
	 */
	public Performative getPerformative() {
		return performative;
	}

	/**
	 * Sets the performative name.
	 *
	 * @param performative the new performative name
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#setPerformative(java.lang.String)
	 */
	public void setPerformativeName(final String performative) {
		this.performative = Performative.valueOf(performative);
	}

	/**
	 * Sets the performative of the message (defined by the FIPA).
	 *
	 * @param performative the new performative of the message (defined by the FIPA)
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IMessage#setPerformative(int)
	 */
	public void setPerformative(final Performative performative) {
		this.performative = performative;
	}

	/**
	 * Gets the associated conversation.
	 *
	 * @return the associated conversation
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getConversation()
	 */
	public Conversation getConversation() {
		return conversation;
	}

	/**
	 * Sets the associated conversation.
	 *
	 * @param conv the new associated conversation
	 */
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

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.IMessage#getMessage()
	 */
	public MessageData getMessage() {
		return this;
	}

}
