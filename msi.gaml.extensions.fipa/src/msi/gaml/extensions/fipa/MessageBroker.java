/*********************************************************************************************
 *
 *
 * 'MessageBroker.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class MessageBroker.
 * 
 * TODO Provide this class with a copy of the scope with which it is created to
 * simplify the API by removing the scope in the parameters
 *
 * @author drogoul
 */
public class MessageBroker {

	/** The messages to deliver. */
	private final Map<IAgent, List<FIPAMessage>> messagesToDeliver = new HashMap<IAgent, List<FIPAMessage>>();

	/**
	 * Centralized storage of Conversations and Messages to facilitate Garbage
	 * Collection
	 */
	private final Map<IAgent, ConversationsMessages> conversationsMessages = new HashMap<IAgent, ConversationsMessages>();

	/** The instance. */
	private static Map<IScope, MessageBroker> instances = new HashMap();

	/**
	 * @throws GamaRuntimeException
	 *             Deliver message.
	 *
	 * @param m
	 *            the m
	 *
	 * @throws GamlException
	 *             the gaml exception
	 */
	public IList<FIPAMessage> deliverMessagesFor(final IScope scope, final IAgent a) throws GamaRuntimeException {
		final List<FIPAMessage> messagesForA = messagesToDeliver.get(a);
		if (messagesForA == null) {
			return GamaListFactory.create();
		}

		final IList<FIPAMessage> successfulDeliveries = GamaListFactory.create(Types.get(IType.MESSAGE));
		final IList<FIPAMessage> failedDeliveries = GamaListFactory.create(Types.get(IType.MESSAGE));

		for (final FIPAMessage m : messagesForA) {
			final Conversation conv = m.getConversation();
			try {
				conv.addMessage(scope, m, a);
			} catch (final GamaRuntimeException e) {
				failedDeliveries.add(m);
				failureMessageInReplyTo(scope, m);
				conv.end();
				throw e;
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
	 * @throws GamaRuntimeException
	 *             Deliver failure in reply to.
	 *
	 * @param m
	 *            the m
	 *
	 * @throws GamlException
	 *             the gaml exception
	 */
	protected FIPAMessage failureMessageInReplyTo(final IScope scope, final FIPAMessage m) throws GamaRuntimeException {
		if (m.getPerformative() == FIPAConstants.Performatives.FAILURE) {
			return null;
		}

		final FIPAMessage f = new FIPAMessage(scope);
		f.setSender(null);
		final IList<IAgent> receivers = GamaListFactory.create(Types.AGENT);
		receivers.add(m.getSender());
		f.setReceivers(receivers);
		f.setPerformative(FIPAConstants.Performatives.FAILURE);
		f.setConversation(m.getConversation());
		f.setContents(m.getContents());
		return f;
	}

	/**
	 * Schedule for delivery.
	 *
	 * @param m
	 *            the m
	 */
	public void scheduleForDelivery(final IScope scope, final FIPAMessage m) {
		for (final IAgent a : m.getReceivers().iterable(scope)) {
			scheduleForDelivery(m.clone(), a);
		}
	}

	private void scheduleForDelivery(final FIPAMessage m, final IAgent agent) {
		List<FIPAMessage> messages = messagesToDeliver.get(agent);
		if (messages == null) {
			messages = new ArrayList();
			messagesToDeliver.put(agent, messages);
		}
		messages.add(m);
	}

	/**
	 * @throws GamaRuntimeException
	 *             Schedule for delivery.
	 *
	 * @param m
	 *            the m
	 * @param protocol
	 *            the protocol
	 *
	 * @throws UnknownProtocolException
	 *             the unknown protocol exception
	 * @throws ProtocolErrorException
	 *             the protocol error exception
	 * @throws GamlException
	 *             the gaml exception
	 */
	public void scheduleForDelivery(final IScope scope, final FIPAMessage m, final Integer protocol) {
		Conversation conv;
		conv = new Conversation(scope, protocol, m);
		m.setConversation(conv);
		scheduleForDelivery(scope, m);
	}

	/**
	 * Gets the single instance of MessageBroker.
	 *
	 * @param sim
	 *            the sim
	 *
	 * @return single instance of MessageBroker
	 */
	public static MessageBroker getInstance(final IScope scope) {
		MessageBroker instance = instances.get(scope);
		if (instance == null) {
			instance = new MessageBroker();
			instances.put(scope, instance);

			scope.getSimulationScope().postEndAction(new IExecutable() {

				@Override
				public Object executeOn(final IScope scope) throws GamaRuntimeException {
					instances.get(scope).manageConversationsAndMessages();
					return null;
				}
			});
			scope.getSimulationScope().postDisposeAction(new IExecutable() {

				@Override
				public Object executeOn(final IScope scope) throws GamaRuntimeException {
					if (instances.get(scope) != null)
						instances.get(scope).schedulerDisposed();
					instances.remove(scope);
					return null;
				}
			});
		}
		return instance;
	}

	public void dispose() {
		messagesToDeliver.clear();
	}

	public IList<FIPAMessage> getMessagesFor(final IAgent agent) {
		if (!conversationsMessages.containsKey(agent)) {
			return GamaListFactory.create();
		}

		return conversationsMessages.get(agent).messages;
	}

	public List<Conversation> getConversationsFor(final IAgent agent) {
		if (!conversationsMessages.containsKey(agent)) {
			return GamaListFactory.create();
		}

		return conversationsMessages.get(agent).conversations;
	}

	public void addConversation(final Conversation c) {
		final List<IAgent> members = GamaListFactory.create(Types.AGENT);
		members.add(c.getIntitiator());
		for (final IAgent m : (GamaList<IAgent>) c.getParticipants()) {
			members.add(m);
		}

		for (final IAgent m : members) {
			addConversation(m, c);
		}
	}

	private void addConversation(final IAgent a, final Conversation c) {
		ConversationsMessages cm = conversationsMessages.get(a);
		if (cm == null) {
			cm = new ConversationsMessages();
			conversationsMessages.put(a, cm);
		}

		cm.conversations.add(c);
	}

	/**
	 * @throws GamaRuntimeException
	 *             Removes the already ended conversations.
	 */
	public void manageConversationsAndMessages() throws GamaRuntimeException {

		// remove ended conversations
		List<Conversation> conversations;
		final List<Conversation> endedConversations = GamaListFactory.create(Types.get(ConversationType.CONV_ID));
		for (final IAgent a : conversationsMessages.keySet()) {
			if (a.dead()) {
				final ConversationsMessages cm = conversationsMessages.get(a);
				cm.conversations.clear();
				cm.messages.clear();
				cm.conversations = null;
				cm.messages = null;
				conversationsMessages.remove(a);
				return;
			}
			conversations = conversationsMessages.get(a).conversations;
			endedConversations.clear();

			for (final Conversation c : conversations) {
				if (c.isEnded() && c.areMessagesRead()) {
					endedConversations.add(c);
				}
			}

			for (final Conversation endedConv : endedConversations) {
				endedConv.dispose();
			}

			final List<FIPAMessage> alreadyReadMessages = GamaListFactory.create(Types.get(IType.MESSAGE));
			for (final FIPAMessage m : conversationsMessages.get(a).messages) {
				if (!m.isUnread()) {
					alreadyReadMessages.add(m);
				}
			}
			conversationsMessages.get(a).messages.removeAll(alreadyReadMessages);

			conversations.removeAll(endedConversations);
		}
	}

	class ConversationsMessages {

		IList<Conversation> conversations;

		// agent mailbox : all un-read messages of an agent
		IList<FIPAMessage> messages;

		ConversationsMessages() {
			this.conversations = GamaListFactory.create(Types.get(ConversationType.CONV_ID));
			this.messages = GamaListFactory.create(Types.get(IType.MESSAGE));
		}
	}

	public void schedulerDisposed() {
		messagesToDeliver.clear();

		ConversationsMessages cm;
		for (final IAgent a : conversationsMessages.keySet()) {
			cm = conversationsMessages.get(a);
			cm.conversations.clear();
			cm.conversations = null;
			cm.messages.clear();
			cm.messages = null;
		}
		conversationsMessages.clear();
	}
}
