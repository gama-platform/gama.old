/*******************************************************************************************************
 *
 * Connector.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.network.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.serializer.factory.StreamConverter;

/**
 * The Class Connector.
 */
public abstract class Connector implements IConnector {

	// private final static int REGISTER_GROUP_THREAD_SAFE_ACTION = 0;
	// private final static int SUBSCRIBE_GROUP_THREAD_SAFE_ACTION = 1;
	// private final static int CONNECTION_THREAD_SAFE_ACTION = 2;
	// private final static int REGISTER_USER_THREAD_SAFE_ACTION = 3;
	// private final static int UNSUBSCRIBE_GROUP_THREAD_SAFE_ACTION = 4;

	/** The Constant FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION. */
	private final static int FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION = 5;

	/** The Constant PUSH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION. */
	private final static int PUSH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION = 8;

	/** The connection parameter. */
	// connector Configuration data
	protected Map<String, String> connectionParameter;

	/** The box follower. */
	// Box ordered map
	protected Map<String, ArrayList<IAgent>> boxFollower;

	/** The received message. */
	// Received messages
	protected Map<IAgent, LinkedList<ConnectorMessage>> receivedMessage;

	/** The local member names. */
	protected Map<String, IAgent> localMemberNames;

	/** The topic suscribing pending. */
	protected List<String> topicSuscribingPending;

	/** The is connected. */
	protected boolean isConnected = false;

	/** The lock group managment. */
	Object lockGroupManagment = new Object();

	/** The force network use. */
	boolean forceNetworkUse = false;

	/** message is raw or composite. */
	private boolean isRaw = false;

	/**
	 * Instantiates a new connector.
	 */
	protected Connector() {
		boxFollower = new HashMap<>();
		topicSuscribingPending = Collections.synchronizedList(new ArrayList<String>());
		connectionParameter = new HashMap<>();
		receivedMessage = new HashMap<>();
		localMemberNames = new HashMap<>();
		forceNetworkUse = false;
	}

	@Override
	public void forceNetworkUse(final boolean b) {
		this.forceNetworkUse = b;
	}

	@Override
	public void configure(final String parameterName, final String value) {
		this.connectionParameter.put(parameterName, value);
	}

	/**
	 * Gets the configuration parameter.
	 *
	 * @param name
	 *            the name
	 * @return the configuration parameter
	 */
	protected String getConfigurationParameter(final String name) {
		return this.connectionParameter.get(name);
	}

	/**
	 * Sets the connected.
	 */
	protected void setConnected() {
		this.isConnected = true;
	}

	@Override
	public List<ConnectorMessage> fetchMessageBox(final IAgent agent) {
		final List<ConnectorMessage> currentMessage = receivedMessage.get(agent);
		this.receivedMessage.put(agent, new LinkedList<ConnectorMessage>());
		return currentMessage;
	}

	/**
	 * Store message.
	 *
	 * @param topic
	 *            the topic
	 * @param content
	 *            the content
	 * @throws GamaNetworkException
	 *             the gama network exception
	 */
	public void storeMessage(final String sender, final String topic, final String content) throws GamaNetworkException {
		// final ArrayList<IAgent> bb = this.boxFollower.get(receiver);
		final ConnectorMessage msg = MessageFactory.unPackNetworkMessage(sender, topic, content);
		if (!this.localMemberNames.containsKey(msg.getSender())) {
			pushAndFetchthreadSafe(Connector.PUSH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION, msg.getReceiver(), msg);
		}
	}

	/**
	 * Push and fetch thread safe.
	 *
	 * @param action
	 *            the action
	 * @param groupName
	 *            the group name
	 * @param message
	 *            the message
	 * @return the map
	 */
	private Map<IAgent, LinkedList<ConnectorMessage>> pushAndFetchthreadSafe(final int action, final String groupName,
			final ConnectorMessage message) {
		synchronized (lockGroupManagment) {
			switch (action) {
				case FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION: {
					final Map<IAgent, LinkedList<ConnectorMessage>> newBox = new HashMap<>();
					for (final IAgent agt : this.receivedMessage.keySet()) {
						newBox.put(agt, new LinkedList<ConnectorMessage>());
					}
					final Map<IAgent, LinkedList<ConnectorMessage>> allMessage = this.receivedMessage;
					this.receivedMessage = newBox;
					return allMessage;
				}
				case PUSH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION: {
					final ArrayList<IAgent> bb = this.boxFollower.get(groupName)==null? this.boxFollower.get("ALL"): this.boxFollower.get(groupName);
					for (final IAgent agt : bb) {
						final LinkedList<ConnectorMessage> messages = receivedMessage.get(agt);
						if (messages != null) { messages.add(message); }
					}
					break;
				}
			}
		}
		return null;
	}

	@Override
	public void send(final IAgent sender, final String receiver, final GamaMessage content) {
		if (!this.forceNetworkUse && this.boxFollower.containsKey(receiver)) {
			final ConnectorMessage msg =
					new LocalMessage((String) sender.getAttribute(INetworkSkill.NET_AGENT_NAME), receiver, content);
			this.pushAndFetchthreadSafe(PUSH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION, receiver, msg);
			// Fix for #3335
			return;
		}

		if (!this.localMemberNames.containsKey(receiver)) {
			if (!isRaw()) {
				final CompositeGamaMessage cmsg = new CompositeGamaMessage(sender.getScope(), content);
				if (cmsg.getSender() instanceof IAgent) {
					cmsg.setSender(sender.getAttribute(INetworkSkill.NET_AGENT_NAME));
				}
				final NetworkMessage msg =
						MessageFactory.buildNetworkMessage((String) sender.getAttribute(INetworkSkill.NET_AGENT_NAME),
								receiver, StreamConverter.convertObjectToStream(sender.getScope(), cmsg));
				this.sendMessage(sender, receiver, MessageFactory.packMessage(msg));
			} else {
				this.sendMessage(sender, receiver, content.getContents(sender.getScope()).toString());
			}
		}
	}

	@Override
	public Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages() {
		return pushAndFetchthreadSafe(FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION, null, null);
	}

	@Override
	public void close(final IScope scope) throws GamaNetworkException {
		releaseConnection(scope);
		topicSuscribingPending.clear();
		boxFollower.clear();
		receivedMessage.clear();
		isConnected = false;
	}

	@Override
	public void leaveTheGroup(final IAgent agt, final String groupName) {
		this.unsubscribeGroup(agt, groupName);
		final ArrayList<IAgent> members = this.boxFollower.get(groupName);
		if (members != null) {
			members.remove(agt);
			if (members.size() == 0) { this.boxFollower.remove(groupName); }
		}
	}

	@Override
	public void joinAGroup(final IAgent agt, final String groupName) {
//		if (isRaw) return;
		if (!this.receivedMessage.containsKey(agt)) {
			this.receivedMessage.put(agt, new LinkedList<ConnectorMessage>());
		}

		ArrayList<IAgent> agentBroadcast = this.boxFollower.get(groupName);

		if (agentBroadcast == null) {
			this.subscribeToGroup(agt, groupName);
			agentBroadcast = new ArrayList<>();
			this.boxFollower.put(groupName, agentBroadcast);
		}
		if (!agentBroadcast.contains(agt)) {
			agentBroadcast.add(agt);
			this.subscribeToGroup(agt, groupName);
		}
	}

	@Override
	public void connect(final IAgent agent) throws GamaNetworkException {
		final String netAgent = (String) agent.getAttribute(INetworkSkill.NET_AGENT_NAME);
		if (!this.forceNetworkUse && !this.localMemberNames.containsKey(netAgent)) {
			this.localMemberNames.put(netAgent, agent);
		}
		if (!this.isConnected) { connectToServer(agent); }

		if (this.receivedMessage.get(agent) == null && !isRaw()) { joinAGroup(agent, netAgent); }
	}
	
	@Override
	public boolean isRaw() {
		return isRaw;
	}
	
	@Override
	public void setRaw(boolean isRaw) {
		this.isRaw = isRaw;
	}

	/**
	 * Connect to server.
	 *
	 * @param agent
	 *            the agent
	 * @throws GamaNetworkException
	 *             the gama network exception
	 */
	protected abstract void connectToServer(IAgent agent) throws GamaNetworkException;

	/**
	 * Checks if is alive.
	 *
	 * @param agent
	 *            the agent
	 * @return true, if is alive
	 * @throws GamaNetworkException
	 *             the gama network exception
	 */
	protected abstract boolean isAlive(final IAgent agent) throws GamaNetworkException;

	/**
	 * Subscribe to group.
	 *
	 * @param agt
	 *            the agt
	 * @param boxName
	 *            the box name
	 * @throws GamaNetworkException
	 *             the gama network exception
	 */
	protected abstract void subscribeToGroup(IAgent agt, String boxName) throws GamaNetworkException;

	/**
	 * Unsubscribe group.
	 *
	 * @param agt
	 *            the agt
	 * @param boxName
	 *            the box name
	 * @throws GamaNetworkException
	 *             the gama network exception
	 */
	protected abstract void unsubscribeGroup(IAgent agt, String boxName) throws GamaNetworkException;

	/**
	 * Release connection.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaNetworkException
	 *             the gama network exception
	 */
	protected abstract void releaseConnection(final IScope scope) throws GamaNetworkException;

	/**
	 * Send message.
	 *
	 * @param sender
	 *            the sender
	 * @param receiver
	 *            the receiver
	 * @param content
	 *            the content
	 * @throws GamaNetworkException
	 *             the gama network exception
	 */
	protected abstract void sendMessage(final IAgent sender, final String receiver, final String content)
			throws GamaNetworkException;


}
