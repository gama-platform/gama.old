package ummisco.gama.network.mqqt;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.types.Types;
import ummisco.gama.mqtt.common.MQTTConnector;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.skills.GamaNetworkException;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.serializer.factory.StreamConverter;




/****@vars({ @var(name = ICommunicatorSkill.SENSOR_NAME, type = IType.STRING, doc = @doc("Net ID of the agent")) })
 * 
 * @author nicolas
 *
 */
public class MQTTConnectorSk implements IConnector{

	//static final String destination = "topic/sensors/";
	
	//IP orderd map
	//protected Map<String, CallbackConnection> receiveConnections;
	//protected CallbackConnection receiveConnections;
	protected MqttClient sendConnection = null;
	
	//Box ordered map
	protected Map<String, ArrayList<IAgent>> boxFollower;
	
	//received Messages
	protected Map<IAgent,LinkedList<Map<String,Object>>> receivedMessage;
   
	protected List<String> topicSuscribingPending ;
	protected boolean isConnected = false;
	
	private Object lock = new Object();
	
	class Callback implements MqttCallback
	{

		@Override
		public void connectionLost(Throwable arg0) {
			System.out.println("connection LOST");
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken arg0) {
			System.out.println("message sended");
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String body =message.toString();
			//System.out.println("message recu " + body);
			@SuppressWarnings("unchecked")
			Map<String,Object> mp = (Map<String,Object>) StreamConverter.convertStreamToObject(GAMA.getSimulation().getScope(), body);
			pushMessageToAgents(topic,mp);
		}
		
	}
	
	public MQTTConnectorSk()
	{
		super();
		//receiveConnections = null;//new HashMap<>();
		boxFollower = new HashMap<>();
		topicSuscribingPending = new ArrayList<String>();
		receivedMessage = new HashMap<>();
		sendConnection = null; 
	}
	
	
	public void pushMessageToAgents(String senderName, Map<String, Object> message)
	{
		
		for(IAgent agt:boxFollower.get(senderName))
		{
			this.receivedMessage.get(agt).add(message);
		}
	}
	
	private  void groupManagment(int action,IAgent agent, String groupName) throws MqttException
	{
	 synchronized(lock)
	 {
		 
		switch(action)
		{
		case 1: {
					ArrayList<IAgent> agentBroadcast = this.boxFollower.get(groupName);
					
					if(agentBroadcast ==null)
					{
						this.topicSuscribingPending.add(groupName);
						groupManagment(2,null, null);
						agentBroadcast = new ArrayList<IAgent>();
						this.boxFollower.put(groupName, agentBroadcast);
					}
					if(!agentBroadcast.contains(agent))
					{
						agentBroadcast.add(agent);
					}	
					break;
				}
		case 2:{
					if(topicSuscribingPending.size()>0)
					{
						//List<Topic> lst = new ArrayList<>();
						String[] topics = new String[topicSuscribingPending.size()];
						int[] qos = new int[topicSuscribingPending.size()];
						int i = 0;
						for(String topic:this.topicSuscribingPending)
						{
							qos[i] = 0;
							topics[i]=topic;
							i++;
						}
						this.sendConnection.subscribe(topics, qos);
						topicSuscribingPending = new ArrayList<>();
					}
					break;
				}
			case 3:{
				this.isConnected = true;
			}
		}
	 }
	}
	
	public void registerToGroup(IAgent agent, String groupName) 
	{
		try {
			groupManagment(1,agent,groupName);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private  void registerToTopic()
	{
		try {
			groupManagment(2,null,null);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 
	public void connectToServer(IAgent agent, String agentName, String server ) throws Exception  {
		IScope scope = agent.getScope();
		if(	this.sendConnection == null) 
		{
			this.sendConnection= MQTTConnector.connectSender(server);
			 MqttConnectOptions connOpts = new MqttConnectOptions();
	         connOpts.setCleanSession(true);
	         sendConnection.setCallback(new Callback());
	         connOpts.setCleanSession(true);
	         connOpts.setKeepAliveInterval(30);
	         connOpts.setUserName(MQTTConnector.DEFAULT_USER);
	         connOpts.setPassword(MQTTConnector.DEFAULT_PASSWORD.toCharArray());
	  		 sendConnection.connect(connOpts);
	        
			System.out.println("creation connection emission");
		}
		if(this.sendConnection == null)
			throw GamaNetworkException.cannotBeConnectedFailure(scope);
		registerToGroup(agent, agentName);
		
		LinkedList<Map<String,Object>> mp = receivedMessage.get(agent);
		if(mp==null )
		{
			this.receivedMessage.put(agent, new LinkedList<Map<String,Object>>());
		}
	}

	public GamaMap<String, Object> fetchMessageBox(IAgent agt) {
		LinkedList<Map<String,Object>> box = this.receivedMessage.get(agt);
		if(box.isEmpty())
			return null;
		
		Map<String,Object> data = box.getFirst();
		box.removeFirst();
		return  GamaMapFactory.createWithoutCasting(Types.STRING, Types.NO_TYPE, data); //	GamaMapFactory.create(). //create(agt.getScope(), Types.STRING, Types.NO_TYPE, data);
	}
	
	public boolean emptyMessageBox(IAgent agt) {
		LinkedList<Map<String,Object>> box = this.receivedMessage.get(agt);
		if(box == null )
			return true;

		return box.isEmpty();

	}
	@Override
	public void sendMessage(IAgent agt, String dest, Object  data) 
	{
		Map<String,Object> message = new HashMap<String, Object>();
		message.put(INetworkSkill.FROM, agt.getAttribute(INetworkSkill.NET_AGENT_NAME));
		message.put(INetworkSkill.TO, dest);
		message.put(INetworkSkill.CONTENT,data);
		String buff = (StreamConverter.convertObjectToStream(agt.getScope(),message));
		MqttMessage mm = new MqttMessage(buff.getBytes());
        
		try {
			sendConnection.publish(dest, mm);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//(new UTF8Buffer(dest), new AsciiBuffer(StreamConverter.convertObjectToStream(agt.getScope(),message)), QoS.AT_LEAST_ONCE, false);
	}


	@Override
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception {
		this.connectToServer(agent, dest, server);
	}


	@Override
	public void close(final IScope scope) throws GamaNetworkException {
		try {
			sendConnection.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaNetworkException.cannotBeDisconnectedFailure(scope);
		}
		boxFollower = new HashMap<>();
		receivedMessage = new HashMap<>();
		sendConnection = null; 
	}


	@Override
	public void leaveTheGroup(IAgent agt, String groupName) {
		ArrayList<IAgent> mygroup = this.boxFollower.get(groupName);
		if(mygroup != null)
			mygroup.remove(agt);
	}

	
}
