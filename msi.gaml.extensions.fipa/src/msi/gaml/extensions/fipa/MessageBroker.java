/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gaml.extensions.fipa;

import java.util.*;

import msi.gama.kernel.simulation.ISchedulerListener;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ScheduledAction;

/**
 * The Class MessageBroker.
 * 
 * @author drogoul
 */
public class MessageBroker implements ISchedulerListener {

	/** The messages to deliver. */
	private final Map<IAgent, List<Message>> messagesToDeliver = new HashMap<IAgent, List<Message>>();
	
	/** Centralized storage of Conversations and Messages to facilitate Garbage Collection */
	private Map<IAgent, ConversationsMessages> conversationsMessages = new HashMap<IAgent, ConversationsMessages>();

	/** The instance. */
	private static MessageBroker instance;

	/**
	 * @throws GamaRuntimeException Deliver message.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	public List<Message> deliverMessagesFor(final IAgent a) throws GamaRuntimeException {
		final List<Message> messagesForA = messagesToDeliver.get(a);
		if (messagesForA == null) { return Collections.EMPTY_LIST; }
		
		List<Message> successfulDeliveries = new GamaList<Message>();
		List<Message> failedDeliveries = new GamaList<Message>();
		
		for (Message m : messagesForA) {
			Conversation conv = m.getConversation();
			try {
				conv.addMessage(m);
			} catch (GamaRuntimeException e) {
				failedDeliveries.add(m);
				failureMessageInReplyTo(m);
				conv.end();
			} finally {
				if (!failedDeliveries.contains(m)) {
					successfulDeliveries.add(m);
				}
			}
		}
 		
		messagesToDeliver.remove(a);
		return successfulDeliveries;
	}

	/**
	 * @throws GamaRuntimeException Deliver failure in reply to.
	 * 
	 * @param m the m
	 * 
	 * @throws GamlException the gaml exception
	 */
	protected Message failureMessageInReplyTo(final Message m) throws GamaRuntimeException {
		if ( m.getPerformative() == FIPAConstants.Performatives.FAILURE ) { return null; }

		final Message f = new Message();
		f.setSender(null);
		final GamaList<IAgent> receivers = new GamaList();
		receivers.add(m.getSender());
		f.setReceivers(receivers);
		f.setPerformative(FIPAConstants.Performatives.FAILURE);
		f.setConversation(m.getConversation());
		f.setContent(m.getContent());
		return f;
	}

	/**
	 * Schedule for delivery.
	 * 
	 * @param m the m
	 */
	public void scheduleForDelivery(final Message m) {
		for ( IAgent a : m.getReceivers() ) {
			scheduleForDelivery(m, a);
		}
	}

	private void scheduleForDelivery(final Message m, final IAgent agent) {
		List<Message> messages = messagesToDeliver.get(agent);
		if ( messages == null ) {
			messages = new ArrayList();
			messagesToDeliver.put(agent, messages);
		}
		messages.add(m);
	}

	/**
	 * @throws GamaRuntimeException Schedule for delivery.
	 * 
	 * @param m the m
	 * @param protocol the protocol
	 * 
	 * @throws UnknownProtocolException the unknown protocol exception
	 * @throws ProtocolErrorException the protocol error exception
	 * @throws GamlException the gaml exception
	 */
	public void scheduleForDelivery(final Message m, final Integer protocol) {
		Conversation conv;
		conv = new Conversation(protocol, m);
		m.setConversation(conv);
		scheduleForDelivery(m);
	}
	

	/**
	 * Gets the single instance of MessageBroker.
	 * 
	 * @param sim the sim
	 * 
	 * @return single instance of MessageBroker
	 */
	public static MessageBroker getInstance() {
		if ( instance == null ) {
			instance = new MessageBroker();

			GAMA.getFrontmostSimulation().getScheduler().insertEndAction(new ScheduledAction() {

				@Override
				public void execute(IScope scope) throws GamaRuntimeException {
					instance.manageConversationsAndMessages();
				}
			});
			GAMA.getFrontmostSimulation().getScheduler().addListener(instance);
		}
		return instance;
		// TODO Il faudrait pouvoir en gérer plusieurs (par simulation)
	}

	public void dispose() {
		messagesToDeliver.clear();
	}
	
	public List<Message> getMessagesFor(IAgent agent) {
		if (!conversationsMessages.containsKey(agent)) {
			ConversationsMessages cm = new ConversationsMessages();
			conversationsMessages.put(agent,  cm);
			return cm.messages;
		}
		
		return conversationsMessages.get(agent).messages;
	}
	
	public List<Conversation> getConversationsFor(IAgent agent) {
		if (!conversationsMessages.containsKey(agent)) {
			ConversationsMessages cm = new ConversationsMessages();
			conversationsMessages.put(agent,  cm);
			return cm.conversations;
		}
		
		return conversationsMessages.get(agent).conversations;
	}
	
	public void addConversation(Conversation c) {
		List<IAgent> members = new GamaList<IAgent>();
		members.add(c.getIntitiator());
		for (IAgent m : (GamaList<IAgent>) c.getParticipants()) {
			members.add(m);
		}
		
		for (IAgent m : members) {
			addConversation(m, c);
		}
	}
	
	private void addConversation(IAgent a, Conversation c) {
		ConversationsMessages cm = new ConversationsMessages();
		cm.conversations.add(c);
		conversationsMessages.put(a, cm);
	}
	
	/**
	 * @throws GamaRuntimeException Removes the already ended conversations.
	 */
	public void manageConversationsAndMessages() throws GamaRuntimeException {
		
		// remove ended conversations
		List<Conversation> conversations;
		List<Conversation> endedConversations = new GamaList<Conversation>();
		for (IAgent a : conversationsMessages.keySet()) {
			
			if (a.dead()) {
				ConversationsMessages cm = conversationsMessages.get(a);
				cm.conversations.clear();
				cm.messages.clear();
				cm.conversations = null;
				cm.messages = null;
				conversationsMessages.remove(a);
			}
			
			conversations = conversationsMessages.get(a).conversations;
			endedConversations.clear();
			
			for (Conversation c : conversations) {
				if (c.isEnded()) {
					endedConversations.add(c);
				}
			}

			for ( final Conversation endedConv : endedConversations ) {
				endedConv.dispose();
			}
			conversations.removeAll(endedConversations);
		}
	}

	class ConversationsMessages {
		List<Conversation> conversations;
		List<Message> messages;
		
		ConversationsMessages() {
			this.conversations = new GamaList<Conversation>();
			this.messages = new GamaList<Message>();
		}
	}

	@Override
	public void schedulerDisposed() {
		messagesToDeliver.clear();
		
		ConversationsMessages cm;
		for (IAgent a : conversationsMessages.keySet()) {
			cm = conversationsMessages.get(a);
			cm.conversations.clear();
			cm.conversations = null;
			cm.messages.clear();
			cm.messages = null;
		}
		conversationsMessages.clear();
		instance = null;
	}
}
