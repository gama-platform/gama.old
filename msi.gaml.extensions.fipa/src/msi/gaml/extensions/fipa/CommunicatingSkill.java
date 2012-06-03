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

import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.*;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.types.*;

/**
 * Agents capable of communicate are equipped with this skill. The CommunicatingSkill supplies the
 * communicating agents with primitives to manipulate the Conversation and Message objects.
 */

@skill(name = "communicating")
@vars({
	@var(name = "conversations", type = IType.LIST_STR, of = Message.SPECIES_NAME, init = "[]"),
	@var(name = "messages", type = IType.LIST_STR, of = Message.SPECIES_NAME, init = "[]"),
	@var(name = "accept_proposals", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "agrees", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "cancels", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "cfps", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "failures", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "informs", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "proposes", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "queries", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "refuses", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "reject_proposals", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "requests", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "requestWhens", type = IType.LIST_STR, of = Message.SPECIES_NAME),
	@var(name = "subscribes", type = IType.LIST_STR, of = Message.SPECIES_NAME) })
// @uses( { Message.class, Conversation.class })
public class CommunicatingSkill extends Skill {

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

	/** A list conversations. */
	// private final GamaList<Conversation> conversations = new GamaList<Conversation>();

	/** The in box. */
	// private final GamaList<Message> inBox = new GamaList();

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
	@action(name = "send")
	@args(names = { Message.SPECIES_NAME, "receivers", "content", "performative", "protocol",
		"conversation" })
	public Message primSendMessage(final IScope scope) throws GamaRuntimeException {
		final Message m =
			(Message) Types.get(Message.SPECIES_NAME).cast(scope,
				scope.getArg(Message.SPECIES_NAME, Types.get(Message.SPECIES_NAME).id()), null);
		Message message;
		message = m == null ? new Message() : m;

		List receivers = Cast.asList(scope, scope.getArg("receivers", IType.LIST));
		if ( receivers == null || receivers.isEmpty() || receivers.contains(null) ) {
			// scope.setStatus(CommandStatus.failure);
		}
		message.setReceivers(new GamaList(receivers));

		message.setSender(getCurrentAgent(scope));

		final List content = Cast.asList(scope, scope.getArg("content", IType.LIST));
		if ( content != null ) {
			message.setContent(new GamaList(content));
		}

		final String performative =
			Cast.asString(scope, scope.getArg("performative", IType.STRING));

		if ( performative != null ) {
			message.setPerformative(performativeIndexes.get(performative));
		}
		if ( message.getPerformative() == -1 ) {
			scope.setStatus(ExecutionStatus.failure);
			return message;
		}

		Conversation conv = message.getConversation();
		if ( conv == null ) {
			conv = (Conversation) Types.get(ConversationType.CONV_ID).cast(scope, message, null);
		}
		if ( conv != null ) { // The message belongs to a conversation
			message.setConversation(conv);
			MessageBroker.getInstance().scheduleForDelivery(message);
		} else { // This is the start of a new conversation
			final String protocol = Cast.asString(scope, scope.getArg("protocol", IType.STRING));
			if ( protocol == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return message;
			}
			MessageBroker.getInstance().scheduleForDelivery(message, protocolIndexes.get(protocol));
		}
		scope.setStatus(ExecutionStatus.skipped);
		return message;
	}

	/**
	 * @throws GamaRuntimeException Retrieves a list of currently active conversations.
	 * 
	 * @return a list of currently active conversations.
	 */
	@getter("conversations")
	public List getConversations(final IAgent agent) throws GamaRuntimeException {
		List<Conversation> conversations =
			(List<Conversation>) agent.getDirectVarValue("conversations");
		return conversations;
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
	private Object replyMessage(final IScope scope, final IList<Message> originals,
		final int performative, final IList content) throws GamaRuntimeException {
		for ( final Message original : originals ) {
			original.setUnread(false);
			final IAgent receiver = original.getSender();
			final GamaList<IAgent> receivers = new GamaList<IAgent>();
			receivers.add(receiver);
			final Conversation conv = original.getConversation();
			Message message;

			message = new Message(getCurrentAgent(scope), receivers, content, performative, conv);
			MessageBroker.getInstance().scheduleForDelivery(message);

		}
		return originals;
	}

	private IList getContentArg(final IScope scope) {
		return Cast.asList(scope, scope.getArg(Message.CONTENT, IType.LIST));
	}

	private IList<Message> getMessageArg(final IScope scope) {
		return Cast.asList(scope, scope.getArg(Message.SPECIES_NAME, IType.LIST));
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
	@action(name = "reply")
	@args(names = { Message.SPECIES_NAME, "performative", "content" })
	public Object primReplyToMessage(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		final String performative =
			Cast.asString(scope, scope.getArg("performative", IType.STRING));
		if ( performative == null ) {
			scope.setStatus(ExecutionStatus.failure);
		}
		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, performativeIndexes.get(performative),
			getContentArg(scope));

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
	@action(name = "accept_proposal")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primAcceptProposal(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}
		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "agree")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primAgree(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "cancel")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primCancel(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "cfp")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primCfp(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "end")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primEnd(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, FIPAConstants.Performatives.END_CONVERSATION,
			getContentArg(scope));

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
	@action(name = "failure")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primFailure(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "inform")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primInform(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "propose")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primPropose(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "query")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primQuery(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "refuse")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primRefuse(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "reject_proposal")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primRejectProposal(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "request")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primRequest(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
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
	@action(name = "subscribe")
	@args(names = { Message.SPECIES_NAME, "content" })
	public Object primSubscribe(final IScope scope) throws GamaRuntimeException {
		final IList originals = getMessageArg(scope);
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, SUBSCRIBE, getContentArg(scope));

	}

	/**
	 * Gets the messages.
	 * 
	 * @return the messages
	 */
	@getter("messages")
	public List getMessages(final IAgent agent) {
		List<Message> result = (List) agent.getAttribute("inBox");
		List<Message> received = MessageBroker.getInstance().deliverMessagesFor(agent);
		result.addAll(received);
		for ( Iterator<Message> it = result.iterator(); it.hasNext(); ) {
			Message m = it.next();
			if ( !m.isUnread() || m.getConversation().isEnded() ) {
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
	public List getAcceptProposalMsgs(final IAgent agent) {
		return filter(agent, ACCEPT_PROPOSAL);
	}

	/**
	 * Gets the agree msgs.
	 * 
	 * @return the agree msgs
	 */
	@getter("agrees")
	public List getAgreeMsgs(final IAgent agent) {
		return filter(agent, AGREE);
	}

	/**
	 * Gets the cancel msgs.
	 * 
	 * @return the cancel msgs
	 */
	@getter("cancels")
	public List getCancelMsgs(final IAgent agent) {
		return filter(agent, CANCEL);
	}

	/**
	 * Gets the cfp msgs.
	 * 
	 * @return the cfp msgs
	 */
	@getter("cfps")
	public List getCfpMsgs(final IAgent agent) {
		final List cfps = filter(agent, CFP);
		return cfps;
	}

	/**
	 * Gets the failure msgs.
	 * 
	 * @return the failure msgs
	 */
	@getter("failures")
	public List getFailureMsgs(final IAgent agent) {
		return filter(agent, FAILURE);
	}

	/**
	 * Gets the inform msgs.
	 * 
	 * @return the inform msgs
	 */
	@getter("informs")
	public List getInformMsgs(final IAgent agent) {
		final List informs = filter(agent, INFORM);
		return informs;
	}

	/**
	 * Gets the propose msgs.
	 * 
	 * @return the propose msgs
	 */
	@getter("proposes")
	public List getProposeMsgs(final IAgent agent) {
		return filter(agent, PROPOSE);
	}

	/**
	 * Gets the query msgs.
	 * 
	 * @return the query msgs
	 */
	@getter("queries")
	public List getQueryMsgs(final IAgent agent) {
		return filter(agent, QUERY);
	}

	/**
	 * Gets the refuses msgs.
	 * 
	 * @return the refuses msgs
	 */
	@getter("refuses")
	public List getRefusesMsgs(final IAgent agent) {
		return filter(agent, REFUSE);
	}

	/**
	 * Gets the reject proposal msgs.
	 * 
	 * @return the reject proposal msgs
	 */
	@getter("reject_proposals")
	public List getRejectProposalMsgs(final IAgent agent) {
		return filter(agent, REJECT_PROPOSAL);
	}

	/**
	 * Gets the request msgs.
	 * 
	 * @return the request msgs
	 */
	@getter("requests")
	public List getRequestMsgs(final IAgent agent) {
		final List requests = filter(agent, REQUEST);
		return requests;
	}

	/**
	 * Gets the request when msgs.
	 * 
	 * @return the request when msgs
	 */
	@getter("requestWhens")
	public List getRequestWhenMsgs(final IAgent agent) {
		return filter(agent, REQUEST_WHEN);
	}

	/**
	 * Gets the subscribe msgs.
	 * 
	 * @return the subscribe msgs
	 */
	@getter("subscribes")
	public List getSubscribeMsgs(final IAgent agent) {
		return filter(agent, SUBSCRIBE);
	}

	/**
	 * @throws GamaRuntimeException Receive message.
	 * 
	 * @param message the message
	 * 
	 * @throws GamlException the gaml exception
	 */
	protected void receiveMessage(final IAgent agent, final Message message)
		throws GamaRuntimeException {
		Message messageToAdd;
		messageToAdd = new Message(message);
		getMessages(agent).add(messageToAdd);
	}

	//
	// @Override
	// public void initializeFor(final IScope scope) {
	// scope.getSimulationScope().getScheduler()
	// .insertEndAction(this, "manageConversationsAndMessages");
	// }

	/**
	 * Filter.
	 * 
	 * @param performative the performative
	 * 
	 * @return the gama list< i message>
	 */
	private List<Message> filter(final IAgent agent, final int performative) {
		List<Message> inBox = getMessages(agent);
		if ( inBox.isEmpty() ) { return Collections.EMPTY_LIST; }
		final GamaList<Message> result = new GamaList();
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
	protected void addConversation(final IAgent agent, final Conversation conv)
		throws GamaRuntimeException {
		List<Conversation> conversations = getConversations(agent);
		conversations.add(conv);
	}

	/**
	 * @throws GamaRuntimeException Removes the already ended conversations.
	 */

	public void manageConversationsAndMessages(final IAgent agent) throws GamaRuntimeException {

		// remove ended conversations
		List<Conversation> conversations = getConversations(agent);
		final List<Conversation> endedConversations = new GamaList<Conversation>();
		for ( final Conversation conv : conversations ) {
			if ( conv.isEnded() ) {
				endedConversations.add(conv);
			}
		}
		for ( final Conversation endedConv : endedConversations ) {
			endedConv.dispose();
		}
		conversations.removeAll(endedConversations);

		// remove obsolete messages
		// List<Message> inBox = getMessages(agent);
		// final List<Message> messagesToRemove = new GamaList<Message>();
	}

}
