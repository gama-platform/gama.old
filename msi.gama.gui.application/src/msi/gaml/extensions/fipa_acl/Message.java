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

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.GamaList;
import msi.gaml.agents.GamlAgent;

/**
 * The Class MessageProxy.
 * 
 * @author drogoul
 */
@species("message")
@vars({ @var(name = "sender", type = IType.AGENT_STR),
	@var(name = "receivers", type = IType.LIST_STR),
	@var(name = "performative", type = IType.STRING_STR),
	@var(name = "content", type = IType.LIST_STR),
	@var(name = "unread", type = IType.BOOL_STR, init = "true"),
	@var(name = "conversation", type = IType.AGENT_STR, species = "conversation"),
	@var(name = "protocol", type = IType.STRING_STR, depends_on = "conversation"),
	@var(name = "timestamp", type = IType.STRING_STR) })
public class Message extends GamlAgent {

	/** The message. */
	private MessageData data;

	/** The unread. */
	private boolean unread = true;

	public static final String SPECIES_NAME = "message";

	/**
	 * @throws GamaRuntimeException Instantiates a new message proxy.
	 * 
	 * @param sim the sim
	 * @param s the s
	 * 
	 * @throws GamlException the gaml exception
	 */
	public Message(final ISimulation sim, final IPopulation s) throws GamaRuntimeException {
		super(sim, s);
		data = new MessageData();
		// schedule(sim);
		// TODO Vérifier que l'on ne doit pas initialiser l'espèce
	}

	/**
	 * @throws GamaRuntimeException Instantiates a new message proxy.
	 * 
	 * @param sim the sim
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	public Message(final ISimulation sim, final Message m) throws GamaRuntimeException {
		super(sim, sim.getWorld().getPopulationFor(Message.SPECIES_NAME));
		data = m.getData();
		// schedule(sim);
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
	public Message(final ISimulation sim, final IAgent sender, final GamaList<IAgent> receivers,
		final GamaList content, final int performative, final Conversation conversation)
		throws GamaRuntimeException {
		super(sim, sim.getWorld().getPopulationFor(Message.SPECIES_NAME));
		data = new MessageData();
		setSender(sender);
		setReceivers(receivers);
		setContent(content);
		setPerformative(performative);
		setConversation(conversation);
		// OutputManager.debug("Message " + name + " created (sender " +
		// sender.getName()+")" + "(receivers " + receivers + ")");
		// schedule(sim);
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
	@getter(var = "sender")
	public IAgent getSender() {
		return data.getSender();
	}

	/**
	 * Sets the sender.
	 * 
	 * @param sender the sender
	 */
	@setter("sender")
	public void setSender(final IAgent sender) {
		data.setSender(sender);
	}

	/**
	 * Gets the receivers.
	 * 
	 * @return the receivers a list of the receivers' name.
	 */
	@getter(var = "receivers")
	public GamaList getReceivers() {
		return data.getReceivers();
	}

	/**
	 * Sets the intended receivers.
	 * 
	 * @param receivers the list of the receivers' name.
	 */
	@setter("receivers")
	public void setReceivers(final GamaList receivers) {
		data.setReceivers(receivers);
	}

	/**
	 * Gets the contents of the message.
	 * 
	 * @return the contents
	 */
	@getter(var = "content")
	public GamaList getContent() {
		// OutputManager.debug("Message " + getName() + " is read.");
		setUnread(false);
		return data.getContent();
	}

	/**
	 * Sets the contents of the message.
	 * 
	 * @param content the content
	 */
	@setter("content")
	public void setContent(final GamaList content) {
		data.setContent(content);
	}

	/**
	 * Gets the performative.
	 * 
	 * @return the performative
	 */
	@getter(var = "performative")
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
	@setter("performative")
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
	@getter(var = "conversation")
	public IAgent getConversation() {
		return data.getConversation();
	}

	/**
	 * Sets the conversation.
	 * 
	 * @param conv the conv
	 */
	@setter("conversation")
	public void setConversation(final IAgent conv) {
		data.setConversation(conv);
	}

	/**
	 * Checks if is unread.
	 * 
	 * @return true, if is unread
	 */
	@getter(var = "unread")
	public boolean isUnread() {
		return unread;
	}

	/**
	 * Sets the unread.
	 * 
	 * @param unread the new unread
	 */
	@setter("unread")
	public void setUnread(final boolean unread) {
		this.unread = unread;
	}

	/**
	 * Gets the protocol name.
	 * 
	 * @return the protocol name
	 */
	@getter(var = "protocol")
	public String getProtocolName() {
		if ( getConversation() == null ) { return null; }
		return ((Conversation) getConversation()).getProtocolName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.extensions.fipa.IMessage#getTimestamp()
	 */
	@getter(var = "timestamp")
	public String getTimestamp() {
		return data.getTimestamp();
	}

	@Override
	public String toString() {
		return "Proxy on " + data;
	}

	@Override
	public void dispose() {
		super.dispose();
		// OutputManager.debug("Message " + name + " disposed");
		data = null;
	}
}
