package ummisco.gama.network.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttException;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import ummisco.gama.serializer.factory.StreamConverter;

public abstract class Connector /*implements IConnector*/{
	
	private final static int REGISTER_GROUP_THREAD_SAFE_ACTION = 0;
	private final static int SUBSCRIBE_GROUP_THREAD_SAFE_ACTION = 1;
	private final static int CONNECTION_THREAD_SAFE_ACTION = 2;
	private final static int REGISTER_USER_THREAD_SAFE_ACTION = 3;
	
	
	protected String server_URL;
	protected int server_port;
	
	//Box ordered map
	protected Map<String, ArrayList<IAgent>> boxFollower;
	
	//Received messages
	protected Map<IAgent,LinkedList<Message>> receivedMessage;
   
	protected List<String> topicSuscribingPending ;
	protected boolean isConnected = false;

	Object lockGroupManagment = new Object();
	
	
	
	protected Connector()
	{
		super();
		server_port = 3301;
		server_URL = "localhost";
		boxFollower = new HashMap<>();
		topicSuscribingPending = new ArrayList<String>();
		receivedMessage = new HashMap<>();
	}
	
	protected void pushMessage(Message msg)
	{
		ArrayList<IAgent> receivers = boxFollower.get("AAAAA"); ////ha modifier
		for(IAgent rec:receivers)
		{
			LinkedList<Message> messages  = receivedMessage.get(rec);
			if(messages != null)
				messages.add(msg);
		}
	}
	
	public List<Message> fetchMessageBox(IAgent agent)
	{
		List<Message> currentMessage = receivedMessage.get(agent);
		this.receivedMessage.put(agent,new LinkedList<Message>());
		return currentMessage;
	}
	
	
	public void register(IAgent agt,String groupName)
	{
		
	}
	
	public void connectToServer(IAgent agent, String myName, String server, int port) throws Exception {
		server_URL = server;
		server_port = port;
		

	}

	public void close(IScope scope) throws GamaNetworkException {
		releaseConnection(scope);
		topicSuscribingPending.clear();
		boxFollower.clear();
		receivedMessage.clear();
		isConnected = false;
		

	}
	
	private  void groupManagment(int action,IAgent agent, String groupName) throws MqttException
	{
	 synchronized(lockGroupManagment)
	 {
		 
		switch(action)
		{
		case REGISTER_GROUP_THREAD_SAFE_ACTION: {
					ArrayList<IAgent> agentBroadcast = this.boxFollower.get(groupName);
					
					if(agentBroadcast ==null)
					{
						this.topicSuscribingPending.add(groupName);
						groupManagment(SUBSCRIBE_GROUP_THREAD_SAFE_ACTION,null, null);
						agentBroadcast = new ArrayList<IAgent>();
						this.boxFollower.put(groupName, agentBroadcast);
					}
					if(!agentBroadcast.contains(agent))
					{
						agentBroadcast.add(agent);
					}	
					break;
				}
		case SUBSCRIBE_GROUP_THREAD_SAFE_ACTION:{
					if(topicSuscribingPending.size()>0)
					{
						for(String topic:this.topicSuscribingPending)
							this.subscribeToBox(agent, topic);
						topicSuscribingPending = new ArrayList<>();
					}
					break;
				}
		case CONNECTION_THREAD_SAFE_ACTION : {
				this.isConnected = true;
				break;
				}
		case REGISTER_USER_THREAD_SAFE_ACTION: {
				
			}
		
		}
	 }
	}

	
	public void sendMessage(final IAgent sender,final String receiver, final Object content)
	{
		this.sendMessage(sender,receiver,StreamConverter.convertObjectToStream(sender.getScope(),content));
	}
	
	protected Message messageArrived(final String receiver , String content) throws GamaNetworkException
	{
		//Message mm = new Message();
		return null;
	}
	
	
	protected abstract void subscribeToBox(IAgent agt, String boxName) throws GamaNetworkException;
	protected abstract void releaseConnection(final IScope scope) throws GamaNetworkException;
	protected abstract void sendMessage(final IAgent sender,final String receiver, final String content) throws GamaNetworkException;
	
}
