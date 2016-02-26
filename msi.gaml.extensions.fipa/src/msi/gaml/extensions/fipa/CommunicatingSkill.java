/*********************************************************************************************
 *
 *
 * 'CommunicatingSkill.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.*;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.types.*;

/**
 * Agents capable of communicate are equipped with this skill. The CommunicatingSkill supplies the
 * communicating agents with primitives to manipulate the Conversation and Message objects.
 */
@doc(
	value = "The fipa skill offers some primitives and built-in variables which enable agent to communicate with each other using the FIPA interaction protocol. ")
@skill(name = "fipa", concept = { IConcept.FIPA, IConcept.SKILL, IConcept.COMMUNICATION })
@vars({
	@var(name = "conversations",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		init = "[]",
		doc = @doc("A list containing the current conversations of agent. Ended conversations are automatically removed from this list.")),
	@var(name = "messages",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		init = "[]",
		doc = @doc("The mailbox of the agent, a list of messages of all types of performatives.")),
	@var(name = "accept_proposals",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'accept_proposal' performative messages of the agent's mailbox having .")),
	@var(name = "agrees",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'accept_proposal' performative messages.")),
	@var(name = "cancels",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'cancel' performative messages.")),
	@var(name = "cfps",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'cfp' (call for proposal) performative messages.")),
	@var(name = "failures",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'failure' performative messages.")),
	@var(name = "informs",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'inform' performative messages.")),
	@var(name = "proposes",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'propose' performative messages .")),
	@var(name = "queries",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'query' performative messages.")),
	@var(name = "refuses",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'propose' performative messages.")),
	@var(name = "reject_proposals",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'reject_proposals' performative messages.")),
	@var(name = "requests",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'request' performative messages.")),
	@var(name = "requestWhens",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'request-when' performative messages.")),
	@var(name = "subscribes",
		type = IType.LIST,
		of = MessageType.MESSAGE_ID,
		doc = @doc("A list of 'subscribe' performative messages.")), })
public class CommunicatingSkill extends Skill {

	@doc(deprecated = "Use the keyword 'fipa' instead")
	@skill(name = "communicating", concept = { IConcept.FIPA, IConcept.SKILL, IConcept.COMMUNICATION })
	public static class Deprecated extends CommunicatingSkill {}

	/** The protocol indexes. */
	private static Map<String, Integer> protocolIndexes = new HashMap();

	/** The performative indexes. */
	protected static Map<String, Integer> performativeIndexes = new HashMap();
	static {
		int i = 0;
		for ( final String name : FIPAConstants.protocolNames ) {
			protocolIndexes.put(name, i);
			i++;
		}

		i = 0;
		for ( final String name : FIPAConstants.performativeNames ) {
			performativeIndexes.put(name, i);
			i++;
		}

	}

	/**
	 * @throws GamaRuntimeException Primitive sendMessage. Reads the input arguments, creates an
	 *             instance of Message then sends it.
	 *
	 * @param args contains the properties of the message.
	 *
	 * @return the Action.CommandStatus indicating the success or failure in executing the
	 *         primitive.
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "start_conversation",
		args = { @arg(name = "receivers", type = IType.LIST, optional = false, doc = @doc("A list of receiver agents")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the message. A list of any GAML type")),
			@arg(name = "performative",
				type = IType.STRING,
				optional = true,
				doc = @doc("A string, representing the message performative")),
			@arg(name = "protocol",
				type = IType.STRING,
				optional = true,
				doc = @doc("A string representing the name of interaction protocol")) },
		doc = @doc(value = "Starts a conversation/interaction protocol."))
	public Message primStartConversation(final IScope scope) throws GamaRuntimeException {

		Message message = new Message();

		final IList receivers = Cast.asList(scope, scope.getArg("receivers", IType.LIST));
		if ( receivers == null || receivers.isEmpty() || receivers
			.contains(null) ) { throw GamaRuntimeException.error("receivers can not be empty or null", scope); }
		message.setReceivers(receivers);

		message.setSender(getCurrentAgent(scope));

		final IList content = Cast.asList(scope, scope.getArg("content", IType.LIST));
		if ( content != null ) {
			message.setContent(content);
		}

		final String performative = Cast.asString(scope, scope.getArg("performative", IType.STRING));

		if ( performative != null ) {
			message.setPerformative(performativeIndexes.get(performative));
		} else {
			throw GamaRuntimeException.error("performative can can null", scope);
		}

		if ( message.getPerformative() == -1 ) { throw GamaRuntimeException
			.error(performative + " performative is unknown", scope); }

		String protocol = Cast.asString(scope, scope.getArg("protocol", IType.STRING));
		if ( protocol == null ) {
			protocol = FIPAConstants.Protocols.NO_PROTOCOL_STR;
		}

		MessageBroker.getInstance().scheduleForDelivery(scope, message, protocolIndexes.get(protocol));

		return message;
	}

	@action(name = "send",
		args = { @arg(name = "receivers", type = IType.LIST, optional = false, doc = @doc("A list of receiver agents")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the message. A list of any GAML type")),
			@arg(name = "performative",
				type = IType.STRING,
				optional = true,
				doc = @doc("A string, representing the message performative")),
			@arg(name = "protocol",
				type = IType.STRING,
				optional = true,
				doc = @doc("A string representing the name of interaction protocol")) },
		doc = @doc(deprecated = "It is preferable to use 'start_conversation' instead to start a conversation",
			value = "Starts a conversation/interaction protocol."))
	public Message primSendMessage(final IScope scope) throws GamaRuntimeException {
		return primStartConversation(scope);
	}

	/**
	 * @throws GamaRuntimeException Primitive reply. Replies a message. Retrieves the conversation
	 *             specified by the conversationID input argument, the have this conversation handle
	 *             the replying process.
	 *
	 * @param args contains the conversationID, performative and the content of the replied message.
	 *
	 * @return the Action.CommandStatus indicating the success or failure of the primitive.
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "reply",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "performative",
				type = IType.STRING,
				optional = false,
				doc = @doc("The performative of the replying message")),
			@arg(name = "content",
				type = IType.LIST,
				optional = true,
				doc = @doc("The content of the replying message")) },
		doc = @doc(
			value = "Replies a message. This action should be only used to reply a message in a 'no-protocol' conversation and with a 'user defined performative'. For performatives supported by GAMA (i.e., standard FIPA performatives), please use the 'action' with the same name of 'performative'. For example, to reply a message with a 'request' performative message, the modeller should use the 'request' action."))
	public Object primReplyToMessage(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		final String performative = Cast.asString(scope, scope.getArg("performative", IType.STRING));
		if ( performative == null ) { throw GamaRuntimeException.error("'performative' argument is mandatory", scope); }

		return replyMessage(scope, originals, performativeIndexes.get(performative), getContentArg(scope));
	}

	/**
	 * @throws GamaRuntimeException Retrieves a list of currently active conversations.
	 *
	 * @return a list of currently active conversations.
	 */
	@getter("conversations")
	public List<Conversation> getConversations(final IAgent agent) throws GamaRuntimeException {
		return MessageBroker.getInstance().getConversationsFor(agent);
	}

	/**
	 * @throws GamaRuntimeException
	 * @throws GamaRuntimeException Reply message.
	 *
	 * @param originals the originals
	 * @param performative the performative
	 * @param content the content
	 *
	 * @throws GamlException the gaml exception
	 */
	private Object replyMessage(final IScope scope, final IList<Message> originals, final int performative,
		final IList content) throws GamaRuntimeException {
		for ( final Message original : originals ) {
			original.setUnread(false);
			final IAgent receiver = original.getSender();
			final IList<IAgent> receivers = GamaListFactory.create(Types.AGENT);
			receivers.add(receiver);
			final Conversation conv = original.getConversation();
			Message message;

			message = new Message(getCurrentAgent(scope), receivers, content, performative, conv);
			MessageBroker.getInstance().scheduleForDelivery(scope, message);

		}
		return originals;
	}

	private IList getContentArg(final IScope scope) {
		return Cast.asList(scope, scope.getArg(Message.CONTENT, IType.LIST));
	}

	private IList<Message> getMessageArg(final IScope scope) {
		return Cast.asList(scope, scope.getArg(MessageType.MESSAGE_STR, IType.LIST));
	}

	/**
	 * Prim accept proposal.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "accept_proposal",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with an 'accept_proposal' performative message."))
	public Object primAcceptProposal(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, ACCEPT_PROPOSAL, getContentArg(scope));
	}

	/**
	 * Prim agree.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "agree",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with an 'agree' performative message."))
	public Object primAgree(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {}

		return replyMessage(scope, originals, AGREE, getContentArg(scope));
	}

	/**
	 * Prim cancel.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "cancel",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'cancel' peformative message."))
	public Object primCancel(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, CANCEL, getContentArg(scope));
	}

	/**
	 * Prim cfp.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "cfp",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'cfp' performative message."))
	public Object primCfp(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, CFP, getContentArg(scope));
	}

	/**
	 * Prim end.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "end_conversation",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Reply a message with an 'end_conversation' peprformative message. This message marks the end of a conversation. In a 'no-protocol' conversation, it is the responsible of the modeler to explicitly send this message to mark the end of a conversation/interaction protocol."))
	public Object primEndConversation(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, FIPAConstants.Performatives.END_CONVERSATION, getContentArg(scope));
	}

	/**
	 * Prim failure.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "failure",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'failure' performative message."))
	public Object primFailure(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, FAILURE, getContentArg(scope));
	}

	/**
	 * Prim inform.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "inform",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with an 'inform' performative message."))
	public Object primInform(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, INFORM, getContentArg(scope));
	}

	/**
	 * Prim propose.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "propose",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'propose' performative message."))
	public Object primPropose(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, PROPOSE, getContentArg(scope));
	}

	/**
	 * Prim query.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "query",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'query' performative message."))
	public Object primQuery(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, QUERY, getContentArg(scope));
	}

	/**
	 * Prim refuse.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "refuse",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'refuse' performative message."))
	public Object primRefuse(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, REFUSE, getContentArg(scope));
	}

	/**
	 * Prim reject proposal.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "reject_proposal",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'reject_proposal' performative message."))
	public Object primRejectProposal(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, REJECT_PROPOSAL, getContentArg(scope));
	}

	/**
	 * Prim request.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "request",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'request' performative message."))
	public Object primRequest(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, REQUEST, getContentArg(scope));
	}

	/**
	 * Prim subscribe.
	 *
	 * @param args the args
	 *
	 * @return the command status
	 *
	 * @throws GamlException the gaml exception
	 */
	@action(name = "subscribe",
		args = {
			@arg(name = MessageType.MESSAGE_STR,
				type = MessageType.MESSAGE_ID,
				optional = false,
				doc = @doc("The message to be replied")),
			@arg(name = "content",
				type = IType.LIST,
				optional = false,
				doc = @doc("The content of the replying message")) },
		doc = @doc("Replies a message with a 'subscribe' performative message."))
	public Object primSubscribe(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null ||
			originals.size() == 0 ) { throw GamaRuntimeException.error("No message to reply", scope); }

		return replyMessage(scope, originals, SUBSCRIBE, getContentArg(scope));

	}

	/**
	 * Gets the messages.
	 *
	 * @return the messages
	 */
	@getter("messages")
	public IList<Message> getMessages(final IScope scope, final IAgent agent) {
		final IList<Message> result = MessageBroker.getInstance().getMessagesFor(agent);
		final List<Message> received = MessageBroker.getInstance().deliverMessagesFor(scope, agent);
		result.addAll(received);
		for ( final Iterator<Message> it = result.iterator(); it.hasNext(); ) {
			final Message m = it.next();
			if ( !m.isUnread() ) {
				it.remove();
			}
		}

		return result;
	}

	/**
	 * Gets the accept proposal msgs.
	 *
	 * @return the accept proposal msgs
	 */
	@getter("accept_proposals")
	public IList<Message> getAcceptProposalMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, ACCEPT_PROPOSAL);
	}

	/**
	 * Gets the agree msgs.
	 *
	 * @return the agree msgs
	 */
	@getter("agrees")
	public IList<Message> getAgreeMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, AGREE);
	}

	/**
	 * Gets the cancel msgs.
	 *
	 * @return the cancel msgs
	 */
	@getter("cancels")
	public IList<Message> getCancelMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, CANCEL);
	}

	/**
	 * Gets the cfp msgs.
	 *
	 * @return the cfp msgs
	 */
	@getter("cfps")
	public IList<Message> getCfpMsgs(final IScope scope, final IAgent agent) {
		final IList cfps = filter(scope, agent, CFP);
		return cfps;
	}

	/**
	 * Gets the failure msgs.
	 *
	 * @return the failure msgs
	 */
	@getter("failures")
	public IList<Message> getFailureMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, FAILURE);
	}

	/**
	 * Gets the inform msgs.
	 *
	 * @return the inform msgs
	 */
	@getter("informs")
	public IList<Message> getInformMsgs(final IScope scope, final IAgent agent) {
		final IList informs = filter(scope, agent, INFORM);
		return informs;
	}

	/**
	 * Gets the propose msgs.
	 *
	 * @return the propose msgs
	 */
	@getter("proposes")
	public IList<Message> getProposeMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, PROPOSE);
	}

	/**
	 * Gets the query msgs.
	 *
	 * @return the query msgs
	 */
	@getter("queries")
	public IList<Message> getQueryMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, QUERY);
	}

	/**
	 * Gets the refuses msgs.
	 *
	 * @return the refuses msgs
	 */
	@getter("refuses")
	public IList<Message> getRefusesMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, REFUSE);
	}

	/**
	 * Gets the reject proposal msgs.
	 *
	 * @return the reject proposal msgs
	 */
	@getter("reject_proposals")
	public IList<Message> getRejectProposalMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, REJECT_PROPOSAL);
	}

	/**
	 * Gets the request msgs.
	 *
	 * @return the request msgs
	 */
	@getter("requests")
	public IList<Message> getRequestMsgs(final IScope scope, final IAgent agent) {
		final IList requests = filter(scope, agent, REQUEST);
		return requests;
	}

	/**
	 * Gets the request when msgs.
	 *
	 * @return the request when msgs
	 */
	@getter("requestWhens")
	public IList<Message> getRequestWhenMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, REQUEST_WHEN);
	}

	/**
	 * Gets the subscribe msgs.
	 *
	 * @return the subscribe msgs
	 */
	@getter("subscribes")
	public IList<Message> getSubscribeMsgs(final IScope scope, final IAgent agent) {
		return filter(scope, agent, SUBSCRIBE);
	}

	/**
	 * @throws GamaRuntimeException Receive message.
	 *
	 * @param message the message
	 *
	 * @throws GamlException the gaml exception
	 */
	protected void receiveMessage(final IScope scope, final IAgent agent, final Message message)
		throws GamaRuntimeException {
		Message messageToAdd;
		messageToAdd = new Message(message);
		getMessages(scope, agent).add(messageToAdd);
	}

	/**
	 * Filter.
	 *
	 * @param performative the performative
	 *
	 * @return the gama list< i message>
	 */
	private IList<Message> filter(final IScope scope, final IAgent agent, final int performative) {
		final IList<Message> inBox = getMessages(scope, agent);
		if ( inBox.isEmpty() ) { return GamaListFactory.EMPTY_LIST; }
		final IList<Message> result =
			GamaListFactory.create(scope.getModelContext().getTypeNamed(MessageType.MESSAGE_STR));
		for ( final Message m : inBox ) {
			final boolean unread = m.isUnread();
			final int mperf = m.getPerformative();
			if ( unread && mperf == performative ) {
				result.add(m);
			}
		}
		return result;
	}

	/**
	 * @throws GamaRuntimeException Adds a conversation to the conversation list.
	 *
	 * @param conv the conv
	 */
	protected void addConversation(final IAgent agent, final Conversation conv) throws GamaRuntimeException {
		final List<Conversation> conversations = getConversations(agent);
		conversations.add(conv);
	}
}
