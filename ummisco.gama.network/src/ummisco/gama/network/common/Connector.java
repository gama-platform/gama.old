/*********************************************************************************************
 *
 * 'Connector.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

public abstract class Connector implements IConnector {

	// private final static int REGISTER_GROUP_THREAD_SAFE_ACTION = 0;
	// private final static int SUBSCRIBE_GROUP_THREAD_SAFE_ACTION = 1;
	// private final static int CONNECTION_THREAD_SAFE_ACTION = 2;
	// private final static int REGISTER_USER_THREAD_SAFE_ACTION = 3;
	// private final static int UNSUBSCRIBE_GROUP_THREAD_SAFE_ACTION = 4;

	private final static int FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION = 5;
	private final static int PUSCH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION = 8;

	// connector Configuration data
	protected Map<String, String> connectionParameter;

	// Box ordered map
	protected Map<String, ArrayList<IAgent>> boxFollower;

	// Received messages
	protected Map<IAgent, LinkedList<ConnectorMessage>> receivedMessage;

	protected Map<String, IAgent> localMemberNames;

	protected List<String> topicSuscribingPending;
	protected boolean isConnected = false;

	Object lockGroupManagment = new Object();

	protected Connector() {
		super();
		boxFollower = new HashMap<>();
		topicSuscribingPending = Collections.synchronizedList(new ArrayList<String>());
		connectionParameter = new HashMap<>();
		receivedMessage = new HashMap<>();
		localMemberNames = new HashMap<String,IAgent>();
	}

	@Override
	public void configure(final String parameterName, final String value) {
		this.connectionParameter.put(parameterName, value);
	}

	protected String getConfigurationParameter(final String name) {
		return this.connectionParameter.get(name);
	}

	protected void setConnected() {
		this.isConnected = true;
	}

	@Override
	public List<ConnectorMessage> fetchMessageBox(final IAgent agent) {
		final List<ConnectorMessage> currentMessage = receivedMessage.get(agent);
		this.receivedMessage.put(agent, new LinkedList<ConnectorMessage>());
		return currentMessage;
	}

	protected void storeMessage(final String topic, final String content) throws GamaNetworkException {
		// final ArrayList<IAgent> bb = this.boxFollower.get(receiver);
		final ConnectorMessage msg = MessageFactory.unPackNetworkMessage(topic, content);
		pushAndFetchthreadSafe(Connector.PUSCH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION, msg.getReceiver(), msg);
	}

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
				case PUSCH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION: {
					final ArrayList<IAgent> bb = this.boxFollower.get(groupName);
					for (final IAgent agt : bb) {
						final LinkedList<ConnectorMessage> messages = receivedMessage.get(agt);
						if (messages != null) {
							messages.add(message);
						}
					}
					break;
				}
			}
		}
		return null;
	}

	@Override
	public void send(final IAgent sender, final String receiver, final GamaMessage content) {
		if (this.boxFollower.containsKey(receiver)) {
			List<IAgent> dests = boxFollower.get(receiver);
			for(IAgent dest:dests)
				this.receivedMessage.get(dest).push(
						new LocalMessage((String) sender.getAttribute(INetworkSkill.NET_AGENT_NAME), receiver, content));
		}
		
		if (!this.localMemberNames.containsKey(receiver))
		{
			final CompositeGamaMessage cmsg = new CompositeGamaMessage(sender.getScope(), content);
			if (cmsg.getSender() instanceof IAgent) {
				cmsg.setSender(sender.getAttribute(INetworkSkill.NET_AGENT_NAME));
			}
			// final String mss = StreamConverter.convertObjectToStream(sender.getScope(), cmsg);
			final NetworkMessage msg = MessageFactory.buildNetworkMessage((String) sender.getAttribute(INetworkSkill.NET_AGENT_NAME),
					receiver, StreamConverter.convertObjectToStream(sender.getScope(), cmsg));
			this.sendMessage(sender, receiver, MessageFactory.packMessage(msg));
		}
	}

	@Override
	public Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages() {
		return pushAndFetchthreadSafe(FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION, null, null);
	}

	@Override
	public void close(final IScope scope) throws GamaNetworkException {
		System.out.println("close connexion ");
		releaseConnection(scope);
		topicSuscribingPending.clear();
		boxFollower.clear();
		receivedMessage.clear();
		isConnected = false;
	}

	@Override
	public void leaveTheGroup(final IAgent agt, final String groupName) {
		this.unsubscribeGroup(agt, groupName);
		ArrayList<IAgent> members = this.boxFollower.get(groupName);
		if(members != null)
			members.remove(agt);
		if(members.size()==0)
			this.boxFollower.remove(groupName);
	}

	@Override
	public void joinAGroup(final IAgent agt, final String groupName) {
		if (!this.receivedMessage.keySet().contains(agt)) {
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
		if(!(this.localMemberNames.containsKey(netAgent)))
		{
			this.localMemberNames.put(netAgent,agent);
		}
		if (!this.isConnected) 
			connectToServer(agent);
		
		if (this.receivedMessage.get(agent) == null)
			joinAGroup(agent, netAgent);
	}

	protected abstract void connectToServer(IAgent agent) throws GamaNetworkException;

	protected abstract boolean isAlive(final IAgent agent) throws GamaNetworkException;

	protected abstract void subscribeToGroup(IAgent agt, String boxName) throws GamaNetworkException;

	protected abstract void unsubscribeGroup(IAgent agt, String boxName) throws GamaNetworkException;

	protected abstract void releaseConnection(final IScope scope) throws GamaNetworkException;

	protected abstract void sendMessage(final IAgent sender, final String receiver, final String content)
			throws GamaNetworkException;

}
