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

import static msi.gaml.extensions.fipa_acl.FIPAConstants.Performatives.*;
import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.skills.Skill;
import msi.gama.util.*;

/**
 * Agents capable of communicate are equipped with this skill. The CommunicatingSkill supplies the
 * communicating agents with primitives to manipulate the Conversation and Message objects.
 */

@skill("communicating")
// // TODO : A REVOIR COMPLETEMENT MAINTENANT QUE LES SKILLS SONT STATELESS
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
	@action("send")
	@args({ Message.SPECIES_NAME, "receivers", "content", "performative", "protocol",
		"conversation" })
	public IAgent primSendMessage(final IScope scope) throws GamaRuntimeException {
		final Message m = (Message) Cast.asAgent(scope.getArg(Message.SPECIES_NAME));
		Message message;
		message =
			m == null ? new Message(scope.getSimulationScope(), scope.getAgentScope()
				.getPopulationFor(Message.SPECIES_NAME)) : m;

		List receivers = Cast.asList(scope.getArg("receivers"));
		if ( receivers == null || receivers.isEmpty() || receivers.contains(null) ) {
			// scope.setStatus(CommandStatus.failure);
		}
		message.setReceivers(new GamaList(receivers));

		message.setSender(getCurrentAgent(scope));

		final List content = Cast.asList(scope.getArg("content"));
		if ( content != null ) {
			message.setContent(new GamaList(content));
		}

		final String performative = Cast.asString(scope.getArg("performative"));

		if ( performative != null ) {
			message.setPerformative(performativeIndexes.get(performative));
		}
		if ( message.getPerformative() == -1 ) {
			scope.setStatus(ExecutionStatus.failure);
			return message;
		}

		IAgent conv = message.getConversation();
		if ( conv == null ) {
			conv = Cast.asAgent(scope.getArg("conversation"));
		}
		if ( conv != null ) { // The message belongs to a conversation
			message.setConversation(conv);
			MessageBroker.getInstance(scope.getSimulationScope()).scheduleForDelivery(message);
		} else { // This is the start of a new conversation
			final String protocol = Cast.asString(scope.getArg("protocol"));
			if ( protocol == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return message;
			}
			MessageBroker.getInstance(scope.getSimulationScope()).scheduleForDelivery(message,
				protocolIndexes.get(protocol));
		}
		scope.setStatus(ExecutionStatus.skipped);
		return message;
	}

	/**
	 * @throws GamaRuntimeException Retrieves a list of currently active conversations.
	 * 
	 * @return a list of currently active conversations.
	 */
	@getter(var = "conversations")
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
	private Object replyMessage(final IScope scope, final List<Message> originals,
		final int performative, final GamaList content) throws GamaRuntimeException {
		for ( final Message original : originals ) {
			original.setUnread(false);
			final IAgent receiver = original.getSender();
			final GamaList<IAgent> receivers = new GamaList<IAgent>();
			receivers.add(receiver);
			final Conversation conv = (Conversation) original.getConversation();
			Message message;

			message =
				new Message(scope.getSimulationScope(), getCurrentAgent(scope), receivers, content,
					performative, conv);
			MessageBroker.getInstance(scope.getSimulationScope()).scheduleForDelivery(message);

		}
		return originals;
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
	@action("reply")
	@args({ Message.SPECIES_NAME, "performative", "content" })
	public Object primReplyToMessage(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		final String performative = Cast.asString(scope.getArg("performative"));
		if ( performative == null ) {
			scope.setStatus(ExecutionStatus.failure);
		}
		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, performativeIndexes.get(performative),
			Cast.asList(scope.getArg("content")));

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
	@action("accept_proposal")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primAcceptProposal(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}
		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, ACCEPT_PROPOSAL, Cast.asList(scope.getArg("content")));

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
	@action("agree")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primAgree(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, AGREE, Cast.asList(scope.getArg("content")));

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
	@action("cancel")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primCancel(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, CANCEL, Cast.asList(scope.getArg("content")));

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
	@action("cfp")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primCfp(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, CFP, Cast.asList(scope.getArg("content")));

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
	@action("end")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primEnd(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, FIPAConstants.Performatives.END_CONVERSATION,
			Cast.asList(scope.getArg("content")));

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
	@action("failure")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primFailure(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, FAILURE, Cast.asList(scope.getArg("content")));

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
	@action("inform")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primInform(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, INFORM, Cast.asList(scope.getArg("content")));

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
	@action("propose")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primPropose(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, PROPOSE, Cast.asList(scope.getArg("content")));

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
	@action("query")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primQuery(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, QUERY, Cast.asList(scope.getArg("content")));

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
	@action("refuse")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primRefuse(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, REFUSE, Cast.asList(scope.getArg("content")));

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
	@action("reject_proposal")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primRejectProposal(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, REJECT_PROPOSAL, Cast.asList(scope.getArg("content")));

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
	@action("request")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primRequest(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, REQUEST, Cast.asList(scope.getArg("content")));

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
	@action("subscribe")
	@args({ Message.SPECIES_NAME, "content" })
	public Object primSubscribe(final IScope scope) throws GamaRuntimeException {
		final List originals = Cast.asList(scope.getArg(Message.SPECIES_NAME));
		if ( originals == null || originals.size() == 0 ) {
			scope.setStatus(ExecutionStatus.failure);
		}

		scope.setStatus(ExecutionStatus.skipped);
		return replyMessage(scope, originals, SUBSCRIBE, Cast.asList(scope.getArg("content")));

	}

	/**
	 * Gets the messages.
	 * 
	 * @return the messages
	 */
	@getter(var = "messages")
	public List getMessages(final IAgent agent) {
		return (List) agent.getAttribute("inBox");
	}

	/**
	 * Gets the accept proposal msgs.
	 * 
	 * @return the accept proposal msgs
	 */
	@getter(var = "accept_proposals")
	public List getAcceptProposalMsgs(final IAgent agent) {
		return filter(agent, ACCEPT_PROPOSAL);
	}

	/**
	 * Gets the agree msgs.
	 * 
	 * @return the agree msgs
	 */
	@getter(var = "agrees")
	public List getAgreeMsgs(final IAgent agent) {
		return filter(agent, AGREE);
	}

	/**
	 * Gets the cancel msgs.
	 * 
	 * @return the cancel msgs
	 */
	@getter(var = "cancels")
	public List getCancelMsgs(final IAgent agent) {
		return filter(agent, CANCEL);
	}

	/**
	 * Gets the cfp msgs.
	 * 
	 * @return the cfp msgs
	 */
	@getter(var = "cfps")
	public List getCfpMsgs(final IAgent agent) {
		final List cfps = filter(agent, CFP);
		return cfps;
	}

	/**
	 * Gets the failure msgs.
	 * 
	 * @return the failure msgs
	 */
	@getter(var = "failures")
	public List getFailureMsgs(final IAgent agent) {
		return filter(agent, FAILURE);
	}

	/**
	 * Gets the inform msgs.
	 * 
	 * @return the inform msgs
	 */
	@getter(var = "informs")
	public List getInformMsgs(final IAgent agent) {
		final List informs = filter(agent, INFORM);
		return informs;
	}

	/**
	 * Gets the propose msgs.
	 * 
	 * @return the propose msgs
	 */
	@getter(var = "proposes")
	public List getProposeMsgs(final IAgent agent) {
		return filter(agent, PROPOSE);
	}

	/**
	 * Gets the query msgs.
	 * 
	 * @return the query msgs
	 */
	@getter(var = "queries")
	public List getQueryMsgs(final IAgent agent) {
		return filter(agent, QUERY);
	}

	/**
	 * Gets the refuses msgs.
	 * 
	 * @return the refuses msgs
	 */
	@getter(var = "refuses")
	public List getRefusesMsgs(final IAgent agent) {
		return filter(agent, REFUSE);
	}

	/**
	 * Gets the reject proposal msgs.
	 * 
	 * @return the reject proposal msgs
	 */
	@getter(var = "reject_proposals")
	public List getRejectProposalMsgs(final IAgent agent) {
		return filter(agent, REJECT_PROPOSAL);
	}

	/**
	 * Gets the request msgs.
	 * 
	 * @return the request msgs
	 */
	@getter(var = "requests")
	public List getRequestMsgs(final IAgent agent) {
		final List requests = filter(agent, REQUEST);
		return requests;
	}

	/**
	 * Gets the request when msgs.
	 * 
	 * @return the request when msgs
	 */
	@getter(var = "requestWhens")
	public List getRequestWhenMsgs(final IAgent agent) {
		return filter(agent, REQUEST_WHEN);
	}

	/**
	 * Gets the subscribe msgs.
	 * 
	 * @return the subscribe msgs
	 */
	@getter(var = "subscribes")
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
		messageToAdd = new Message(agent.getSimulation(), message);
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
		List<Message> inBox = getMessages(agent);
		final List<Message> messagesToRemove = new GamaList<Message>();
		for ( final Message m : inBox ) {
			if ( !m.isUnread() || ((Conversation) m.getConversation()).isEnded() ) {
				messagesToRemove.add(m);
			}
		}
		for ( Message m : messagesToRemove ) {
			m.dispose();
		}
		inBox.removeAll(messagesToRemove);
	}

}
