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

import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.ACCEPT_PROPOSAL;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.AGREE;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.CANCEL;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.CFP;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.FAILURE;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.INFORM;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.PROPOSE;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.QUERY;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.REFUSE;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.REJECT_PROPOSAL;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.REQUEST;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.REQUEST_WHEN;
import static msi.gaml.extensions.fipa.FIPAConstants.Performatives.SUBSCRIBE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.ExecutionStatus;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Agents capable of communicate are equipped with this skill. The CommunicatingSkill supplies the
 * communicating agents with primitives to manipulate the Conversation and Message objects.
 */

@skill(name = "communicating")
@vars({
	@var(name = "conversations", type = IType.LIST_STR, of = MessageType.MESSAGE_STR, init = "[]"),
	@var(name = "messages", type = IType.LIST_STR, of = MessageType.MESSAGE_STR, init = "[]"),
	@var(name = "accept_proposals", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "agrees", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "cancels", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "cfps", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "failures", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "informs", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "proposes", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "queries", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "refuses", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "reject_proposals", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "requests", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "requestWhens", type = IType.LIST_STR, of = MessageType.MESSAGE_STR),
	@var(name = "subscribes", type = IType.LIST_STR, of = MessageType.MESSAGE_STR) })
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
	@action(name = "send",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = IType.STRING_STR, optional = false, doc = @doc("to be described")),
			@arg(name = "receivers", type = IType.LIST_STR, optional = false, doc = @doc("to be described")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be described")),
			@arg(name = "performative", type = IType.STRING_STR, optional = true, doc = @doc("to be described")),
			@arg(name = "protocol", type = IType.STRING_STR, optional = true, doc = @doc("to be described")),
			@arg(name = "conversation", type = ConversationType.CONVERSATION_STR, optional = true, doc = @doc("to be described"))			
		}
	)
	public Message primSendMessage(final IScope scope) throws GamaRuntimeException {
		final Message m =
			(Message) Types.get(MessageType.MESSAGE_STR).cast(scope,
				scope.getArg(MessageType.MESSAGE_STR, Types.get(MessageType.MESSAGE_STR).id()), null);
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
		return Cast.asList(scope, scope.getArg(MessageType.MESSAGE_STR, IType.LIST));
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
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "performative", type = IType.STRING_STR, optional = false, doc = @doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = true, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "accept_proposal",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "agree",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "cancel",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "cfp",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "end",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "failure",
			args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "inform",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "propose",
			args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "query",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "refuse",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "reject_proposal",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "request",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	@action(name = "subscribe",
		args = {
			@arg(name = MessageType.MESSAGE_STR, type = MessageType.MESSAGE_STR, optional = false, doc=@doc("to be documented")),
			@arg(name = "content", type = IType.LIST_STR, optional = false, doc = @doc("to be documented"))
		}
	)
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
	public IList<Message> getMessages(final IAgent agent) {
		IList<Message> result = MessageBroker.getInstance().getMessagesFor(agent);
		
		List<Message> received = MessageBroker.getInstance().deliverMessagesFor(agent);
		result.addAll(received);
		for ( Iterator<Message> it = result.iterator(); it.hasNext(); ) {
			Message m = it.next();
//			if ( !m.isUnread() || m.getConversation().isEnded() ) {
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
	public IList<Message> getAcceptProposalMsgs(final IAgent agent) {
		return filter(agent, ACCEPT_PROPOSAL);
	}

	/**
	 * Gets the agree msgs.
	 * 
	 * @return the agree msgs
	 */
	@getter("agrees")
	public IList<Message> getAgreeMsgs(final IAgent agent) {
		return filter(agent, AGREE);
	}

	/**
	 * Gets the cancel msgs.
	 * 
	 * @return the cancel msgs
	 */
	@getter("cancels")
	public IList<Message> getCancelMsgs(final IAgent agent) {
		return filter(agent, CANCEL);
	}

	/**
	 * Gets the cfp msgs.
	 * 
	 * @return the cfp msgs
	 */
	@getter("cfps")
	public IList<Message> getCfpMsgs(final IAgent agent) {
		final IList cfps = filter(agent, CFP);
		return cfps;
	}

	/**
	 * Gets the failure msgs.
	 * 
	 * @return the failure msgs
	 */
	@getter("failures")
	public IList<Message> getFailureMsgs(final IAgent agent) {
		return filter(agent, FAILURE);
	}

	/**
	 * Gets the inform msgs.
	 * 
	 * @return the inform msgs
	 */
	@getter("informs")
	public IList<Message> getInformMsgs(final IAgent agent) {
		final IList informs = filter(agent, INFORM);
		return informs;
	}

	/**
	 * Gets the propose msgs.
	 * 
	 * @return the propose msgs
	 */
	@getter("proposes")
	public IList<Message> getProposeMsgs(final IAgent agent) {
		return filter(agent, PROPOSE);
	}

	/**
	 * Gets the query msgs.
	 * 
	 * @return the query msgs
	 */
	@getter("queries")
	public IList<Message> getQueryMsgs(final IAgent agent) {
		return filter(agent, QUERY);
	}

	/**
	 * Gets the refuses msgs.
	 * 
	 * @return the refuses msgs
	 */
	@getter("refuses")
	public IList<Message> getRefusesMsgs(final IAgent agent) {
		return filter(agent, REFUSE);
	}

	/**
	 * Gets the reject proposal msgs.
	 * 
	 * @return the reject proposal msgs
	 */
	@getter("reject_proposals")
	public IList<Message> getRejectProposalMsgs(final IAgent agent) {
		return filter(agent, REJECT_PROPOSAL);
	}

	/**
	 * Gets the request msgs.
	 * 
	 * @return the request msgs
	 */
	@getter("requests")
	public IList<Message> getRequestMsgs(final IAgent agent) {
		final IList requests = filter(agent, REQUEST);
		return requests;
	}

	/**
	 * Gets the request when msgs.
	 * 
	 * @return the request when msgs
	 */
	@getter("requestWhens")
	public IList<Message> getRequestWhenMsgs(final IAgent agent) {
		return filter(agent, REQUEST_WHEN);
	}

	/**
	 * Gets the subscribe msgs.
	 * 
	 * @return the subscribe msgs
	 */
	@getter("subscribes")
	public IList<Message> getSubscribeMsgs(final IAgent agent) {
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

	/**
	 * Filter.
	 * 
	 * @param performative the performative
	 * 
	 * @return the gama list< i message>
	 */
	private IList<Message> filter(final IAgent agent, final int performative) {
		List<Message> inBox = getMessages(agent);
		if ( inBox.isEmpty() ) { return GamaList.EMPTY_LIST; }
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
}
