/*********************************************************************************************
 * 
 * 
 * 'Conversation.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.*;

/**
 * This class represents the notion of a Conversation which is comprised of several Messages, the
 * order of which follow a certain interaction protocol (e.g. FIPA-Request, FIPA-Subscribe,...). The
 * methods it provides allow retrieval of information about the conversation.
 * 
 * This is the base class of all the classes that implement the concrete interaction protocols. The
 * protocol models are defined in the corresponding sub-classes.
 */

@vars({ @var(name = Conversation.MESSAGES, type = IType.LIST, of = MessageType.MESSAGE_ID),
	@var(name = Conversation.PROTOCOL, type = IType.STRING), @var(name = Conversation.INITIATOR, type = IType.AGENT),
	@var(name = Conversation.PARTICIPANTS, type = IType.LIST, of = IType.AGENT),
	@var(name = Conversation.ENDED, type = IType.BOOL, init = "false") })
public class Conversation extends GamaList<Message> {

	/** The protocol. */
	private FIPAProtocol protocol;

	public final static String PROTOCOL = "protocol";
	public final static String INITIATOR = "initiator";
	public final static String PARTICIPANTS = "participants";
	public final static String ENDED = "ended";
	public final static String MESSAGES = "messages";

	/** The owner of this conversation. */
	private IAgent initiator;

	/** Other Agent in the conversations. */
	private final IList<IAgent> participants = GamaListFactory.create(Types.AGENT);

	/** The protocol node participant map. */
	private final Map<IAgent, ProtocolNode> protocolNodeParticipantMap = new HashMap<IAgent, ProtocolNode>();

	/** The no protocol node participant map. */
	private final Map<IAgent, Message> noProtocolNodeParticipantMap = new HashMap<IAgent, Message>();

	/** The current node in the protocol tree. */
	// private ProtocolNode currentNode;
	/**
	 * Plays the role of a mailbox, contains all the messages sent by the other agent in this
	 * Conversation.
	 */
	private final IList<Message> messages = GamaListFactory.create(Types.get(MessageType.MESSAGE_ID));

	/** The ended. */
	private boolean ended = false;

	/**
	 * @throws GamaRuntimeException Method to dynamically load a Conversation instance which follows
	 *             the given protocol and belongs to the given Agent.
	 * 
	 * @param sim the sim
	 * @param p the protocol id
	 * @param message the message
	 * 
	 * @return The appropriate instance of Conversation for the protocol given
	 * 
	 * @throws UnknownProtocolException the unknown protocol exception
	 * @throws ProtocolErrorException the protocol error exception
	 * @throws GamlException the gaml exception
	 * 
	 * @exception UnknownProtocolException Thrown if a Conversation class cannot be loaded for the
	 *                given class
	 */
	protected Conversation(final IScope scope, final Integer p, final Message message) throws GamaRuntimeException {
		super(0, Types.get(MessageType.MESSAGE_ID));
		final int proto = p == null ? FIPAConstants.Protocols.NO_PROTOCOL : p;
		protocol = FIPAProtocol.named(proto);
		if ( protocol == null ) { throw new UnknownProtocolException(scope, proto); }
		initiator = message.getSender();

		
		participants.addAll(message.getReceivers());
		if ( participants == null || participants.isEmpty() || participants.contains(null) ) { throw new ProtocolErrorException(
			scope, "The message : " + message.toString() + " has no receivers."); }

		MessageBroker.getInstance().addConversation(this);
	}

	/**
	 * Sets the protocol.
	 * 
	 * @param protocol the protocol to set
	 */
	public void setProtocol(final FIPAProtocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * Adds a message to the conversation.
	 * 
	 * @param message The Message to be added
	 * @param receiver The agent who receive the message
	 * 
	 * @throws ProtocolErrorException the protocol error exception
	 * @throws InvalidConversationException the invalid conversation exception
	 * @throws ConversationFinishedException the conversation finished exception
	 * 
	 * @exception ProtocolErrorException Thrown when the message to be added doesn't follow the
	 *                correct protocol
	 * @exception InvalidConversationException Thrown when the conversation ID of the ACLMessage is
	 *                different to that of the conversation
	 * @exception ConversationFinishedException Thrown when the conversation has already finished
	 */
	protected void addMessage(final IScope scope, final Message message, final IAgent receiver) throws ProtocolErrorException,
		InvalidConversationException, ConversationFinishedException {

		// OutputManager.debug(name + " adds message " + message);

		// Check if the message belongs to this conversation
		final Conversation msgConv = message.getConversation();
		if ( msgConv == null || msgConv != this ) { throw new InvalidConversationException(scope,
			"Conversation is invalid or not specified"); }

		if ( protocol.hasProtocol() ) {
			/** we use a protocol for this Conversation */
			// check the validity of this Message with the protocol model.
			// Raises an exception if the message is not valid.
			final boolean senderIsInitiator = message.getSender().equals(initiator);
			ProtocolNode currentNode;

			if ( senderIsInitiator ) {
				if (message.getReceivers().contains(receiver)) {
					if ( protocolNodeParticipantMap.containsKey(receiver) ) {
						currentNode = protocolNodeParticipantMap.remove(receiver);
						protocolNodeParticipantMap
							.put(receiver, protocol.getNode(scope, message, currentNode, message.getPerformative(),
								senderIsInitiator));
					} else {
						currentNode =
							protocol.getNode(scope, message, null, message.getPerformative(), senderIsInitiator);

						if ( currentNode != null ) {
							protocolNodeParticipantMap.put(receiver, currentNode);
						}
					}
				} else {
					throw new CommunicatingException(scope, "Receiver " + receiver.getName() + " is not in the available message's receivers.");
				}

				
				
			} else if ( participants.contains(message.getSender()) ) {
				if ( protocolNodeParticipantMap.containsKey(message.getSender()) ) {
					currentNode = protocolNodeParticipantMap.remove(message.getSender());
					protocolNodeParticipantMap.put(message.getSender(),
						protocol.getNode(scope, message, currentNode, message.getPerformative(), senderIsInitiator));
				} else {
					currentNode = protocol.getNode(scope, message, null, message.getPerformative(), senderIsInitiator);

					if ( currentNode != null ) {
						protocolNodeParticipantMap.put(message.getSender(), currentNode);
					}
				}
			}
		} else { // we use NoProtocol
			final boolean senderIsInitiator = message.getSender().equals(initiator);
			Message currentMessage;

			if ( senderIsInitiator ) {
				currentMessage = noProtocolNodeParticipantMap.get(receiver);

				if ( currentMessage != null &&
					currentMessage.getPerformative() == FIPAConstants.Performatives.END_CONVERSATION ) { throw new ConversationFinishedException(
					scope, "Message received in conversation which has already ended." + message + this); }

				if ( currentMessage != null ) {
					noProtocolNodeParticipantMap.remove(receiver);
				}
				noProtocolNodeParticipantMap.put(receiver, message);
				
			} else if ( participants.contains(message.getSender()) ) {
				currentMessage = noProtocolNodeParticipantMap.get(message.getSender());

				if ( currentMessage != null &&
					currentMessage.getPerformative() == FIPAConstants.Performatives.END_CONVERSATION ) { throw new ConversationFinishedException(
					scope, "Message received in conversation which has already ended." + message + this); }

				if ( currentMessage != null ) {
					noProtocolNodeParticipantMap.remove(message.getSender());
				}
				noProtocolNodeParticipantMap.put(message.getSender(), message);
			}
		}

		messages.add(message);
	}

	/**
	 * Retrieves all the messages kept by this role of the conversation. Note: There are two roles
	 * in a conversation : initiator and participant.
	 * 
	 * @return the messages
	 */
	@getter(MESSAGES)
	public IList<Message> getMessages() {
		return messages;
	}

	/**
	 * Gets the intitiator.
	 * 
	 * @return the intitiator
	 */
	@getter(INITIATOR)
	public IAgent getIntitiator() {
		return initiator;
	}

	/**
	 * Gets the participants.
	 * 
	 * @return the participants
	 */
	@getter(PARTICIPANTS)
	public IList getParticipants() {
		return participants;
	}

	/**
	 * Gets the protocol name.
	 * 
	 * @return the protocol name
	 */
	@getter(PROTOCOL)
	public String getProtocolName() {
		if ( protocol == null ) { return null; }
		return FIPAConstants.protocolNames[protocol.getIndex()];
	}

	/**
	 * Checks if is ended.
	 * 
	 * @return true, if is ended
	 */
	@getter(ENDED)
	public boolean isEnded() {
		return ended || areAllNodeEnded();
	}

	public boolean areMessagesRead() {
		for ( Message m : messages ) {
			if ( m.isUnread() ) { return false; }
		}

		return true;
	}

	/**
	 * Are all node ended.
	 * 
	 * @return true, if successful
	 */
	private boolean areAllNodeEnded() {
		if ( protocol != null && protocol.hasProtocol() ) {
			final Collection<ProtocolNode> protocolNodes = protocolNodeParticipantMap.values();
			if ( protocolNodes.isEmpty() ) { return false; }
			for ( final ProtocolNode node : protocolNodes ) {
				if ( !(node != null && node.isTerminal()) ) { return false; }
			}
			return true;
		}
		final Collection<Message> finalMsgs = noProtocolNodeParticipantMap.values();
		if ( finalMsgs.isEmpty() ) { return false; }
		for ( final Message finalMsg : finalMsgs ) {
			if ( finalMsg.getPerformative() != FIPAConstants.Performatives.END_CONVERSATION ) { return false; }
		}
		return true;
	}

	/**
	 * End.
	 */
	protected void end() {
		ended = true;
	}

	public synchronized void dispose() {
		end();
		protocolNodeParticipantMap.clear();
		noProtocolNodeParticipantMap.clear();
		participants.clear();
		initiator = null;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "Conversation between initiator: " + this.getIntitiator() + " and participants: " +
			this.getParticipants();
	}

	//
	// @Override
	// public IType type() {
	// return Types.get(ConversationType.CONV_ID);
	// }

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "Conversation between initiator: " + this.getIntitiator() + " and participants: " +
			this.getParticipants();
	}

	@Override
	public String toString() {
		return "Conversation between initiator: " + this.getIntitiator() + " and participants: " +
			this.getParticipants();
	}
}
