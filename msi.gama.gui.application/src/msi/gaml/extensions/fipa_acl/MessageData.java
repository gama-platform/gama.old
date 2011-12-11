/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.fipa_acl;

import msi.gama.interfaces.IAgent;
import msi.gama.util.GamaList;

/**
 * The Message class represents the piece of information transfered between agents capable of
 * communicating.
 */
public class MessageData {

	/** The name of sender. */
	private IAgent sender;

	/** The name of all receivers. */
	private GamaList<IAgent> receivers;

	/** The content of the message. */
	private GamaList content;

	/** The performative of the message (defined by the FIPA). */
	private int performative;

	/** The associated conversation. */
	private Conversation conversation;

	/** The timestamp. */
	private final String timestamp;

	/**
	 * Instantiates a new message.
	 */
	public MessageData() {
		timestamp = Long.toString(System.currentTimeMillis());
	}

	public void dispose() {
		conversation = null;
		content.clear();
		receivers.clear();
		sender = null;
	}

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
	 * @see msi.misc.current_development.IMessage#setSender(msi.gama.metamodel.agent
	 * .interfaces.BasicEntity)
	 */
	public void setSender(final IAgent sender) {
		this.sender = sender;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#getReceivers()
	 */
	public GamaList getReceivers() {
		return receivers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#setReceivers(java.util.List)
	 */
	public void setReceivers(final GamaList receivers) {
		this.receivers = receivers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#getContent()
	 */
	public GamaList getContent() {
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#setContent(java.lang.String)
	 */
	public void setContent(final GamaList content) {
		if ( content != null ) {
			this.content = content;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#getPerformative()
	 */
	public String getPerformativeName() {
		return FIPAConstants.performativeNames[performative];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#getPerformative()
	 */
	public int getPerformative() {
		return performative;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#setPerformative(java.lang.String)
	 */
	public void setPerformativeName(final String performative) {
		this.performative = CommunicatingSkill.performativeIndexes.get(performative);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#setPerformative(int)
	 */
	public void setPerformative(final int performative) {
		this.performative = performative;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#getConversation()
	 */
	public IAgent getConversation() {
		return conversation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.misc.current_development.IMessage#setConversation(msi.gama.metamodel
	 * .agent.interfaces.BasicEntity)
	 */
	public void setConversation(final IAgent conv) {
		conversation = (Conversation) conv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer retVal = new StringBuffer();
		retVal.append("Message[sender : " + sender + ", receivers : " + receivers + /*
																					 * ", content : "
																					 * + content +
																					 */
		", conversation : " + conversation + ", performative : " +
			FIPAConstants.performativeNames[performative] + "]");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#getTimestamp()
	 */
	public String getTimestamp() {
		return timestamp;
	}

}
