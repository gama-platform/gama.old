package ummisco.gama.network.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.serializer.factory.StreamConverter;

public abstract class Connector implements IConnector{
	
	private final static int REGISTER_GROUP_THREAD_SAFE_ACTION = 0;
	private final static int SUBSCRIBE_GROUP_THREAD_SAFE_ACTION = 1;
	private final static int CONNECTION_THREAD_SAFE_ACTION = 2;
	private final static int REGISTER_USER_THREAD_SAFE_ACTION = 3;
	private final static int UNSUBSCRIBE_GROUP_THREAD_SAFE_ACTION = 4;
	
	private final static int FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION = 5;
	private final static int PUSCH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION = 8;
	
	
	
	//connector Configuration data
	protected Map<String, String> connectionParameter;
	
	//Box ordered map
	protected Map<String, ArrayList<IAgent>> boxFollower;
	
	//Received messages 
	protected Map<IAgent,LinkedList<ConnectorMessage>> receivedMessage;
	
	protected List<String> LocalMemberNames;
	   
	protected List<String> topicSuscribingPending ;
	protected boolean isConnected = false;

	Object lockGroupManagment = new Object();
	
	protected Connector()
	{
		super();
		boxFollower = new HashMap<>();
		topicSuscribingPending = Collections.synchronizedList(new ArrayList<String>());
		connectionParameter = new HashMap<String,String>();
		receivedMessage = new HashMap<IAgent,LinkedList<ConnectorMessage>>();
		LocalMemberNames = new ArrayList<String>();
	}
	
	public void configure(String parameterName,String value)
	{
		this.connectionParameter.put(parameterName, value);
	}
	
	protected String getConfigurationParameter(String name)
	{
		return this.connectionParameter.get(name);
	}
		
	protected void setConnected() {
		this.isConnected = true;
	}
	
	public List<ConnectorMessage> fetchMessageBox(IAgent agent)
	{
		List<ConnectorMessage> currentMessage = receivedMessage.get(agent);
		this.receivedMessage.put(agent,new LinkedList<ConnectorMessage>());
		return currentMessage;
	}
	
	protected void storeMessage(String receiver, String content) throws GamaNetworkException
	{
		ArrayList<IAgent> bb = this.boxFollower.get(receiver);
		ConnectorMessage msg = NetworkMessage.unPackMessage(receiver,content);
		pushAndFetchthreadSafe(Connector.PUSCH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION,receiver,msg);
	}
	
	private  Map<IAgent,LinkedList<ConnectorMessage>> pushAndFetchthreadSafe(int action, String groupName,ConnectorMessage message)
	{
		synchronized(lockGroupManagment)
		{
			switch(action)
			{
				case FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION: {
					Map<IAgent,LinkedList<ConnectorMessage>> newBox = new HashMap<IAgent,LinkedList<ConnectorMessage>>();
					for(IAgent agt:this.receivedMessage.keySet())
						newBox.put(agt, new LinkedList<ConnectorMessage>());
					Map<IAgent,LinkedList<ConnectorMessage>> allMessage = this.receivedMessage;
					this.receivedMessage = newBox;
					return allMessage;
				}
				case PUSCH_RECEIVED_MESSAGE_THREAD_SAFE_ACTION: {
					ArrayList<IAgent> bb = this.boxFollower.get(groupName);
					for(IAgent agt:bb)
					{
						LinkedList<ConnectorMessage> messages  = receivedMessage.get(agt);
						if(messages != null)
							messages.add(message);
					}
					break;
				}
			}
		}
		return null;
	}

	
	public void send(final IAgent sender,final String receiver, final GamaMessage content)
	{
		if(this.LocalMemberNames.contains(receiver))
			this.receivedMessage.get(receiver).push(new LocalMessage((String)sender.getAttribute(INetworkSkill.NET_AGENT_NAME), receiver, content));
		else {
			CompositeGamaMessage cmsg = new CompositeGamaMessage(sender.getScope(),content);
			if(cmsg.getSender() instanceof IAgent)
				cmsg.setSender(sender.getAttribute(INetworkSkill.NET_AGENT_NAME));
			NetworkMessage msg = new NetworkMessage((String)(sender.getAttribute(INetworkSkill.NET_AGENT_NAME)),receiver,StreamConverter.convertObjectToStream(sender.getScope(),cmsg));
			this.sendMessage(sender,receiver,NetworkMessage.packMessage(msg));
		}
	}
	
	public Map<IAgent,LinkedList<ConnectorMessage>>  fetchAllMessages()
	{
		return pushAndFetchthreadSafe(FETCH_ALL_MESSAGE_THREAD_SAFE_ACTION, null,null);
	}
	
	public void close(IScope scope) throws GamaNetworkException {
		releaseConnection(scope);
		topicSuscribingPending.clear();
		boxFollower.clear();
		receivedMessage.clear();
		isConnected = false;
	}
	
	@Override
	public void leaveTheGroup(IAgent agt, String groupName) {
		this.unsubscribeGroup(agt, groupName);
		this.boxFollower.remove(groupName);
	}

	@Override
	public void joinAGroup(IAgent agt, String groupName) {
		if(!this.receivedMessage.keySet().contains(agt))
			this.receivedMessage.put(agt, new LinkedList<ConnectorMessage>());
		
		ArrayList<IAgent> agentBroadcast = this.boxFollower.get(groupName);
		
		if(agentBroadcast ==null)
		{
			this.subscribeToGroup(agt, groupName);
			agentBroadcast = new ArrayList<IAgent>();
			this.boxFollower.put(groupName, agentBroadcast);
		}
		if(!agentBroadcast.contains(agt))
		{
			agentBroadcast.add(agt);
			this.subscribeToGroup(agt,groupName);
		}			
	}
	
	public void connect(IAgent agent) throws GamaNetworkException
	{
		String netAgent = (String)agent.getAttribute(INetworkSkill.NET_AGENT_NAME);
		if(!this.isConnected)
			connectToServer(agent);
		if(this.receivedMessage.get(netAgent) == null)
			joinAGroup(agent,netAgent);
		
	}
	
	protected abstract void connectToServer(IAgent agent) throws GamaNetworkException;
	protected abstract void subscribeToGroup(IAgent agt, String boxName) throws GamaNetworkException;
	protected abstract void unsubscribeGroup(IAgent agt, String boxName) throws GamaNetworkException;
	protected abstract void releaseConnection(final IScope scope) throws GamaNetworkException;
	protected abstract void sendMessage(final IAgent sender,final String receiver, final String content) throws GamaNetworkException;
	
}
