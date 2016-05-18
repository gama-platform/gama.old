package ummisco.gama.network.mqqt;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.types.Types;
import ummisco.gama.mqtt.common.MQTTConnector;
import ummisco.gama.network.skills.IConnector;
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
	protected Map<String, CallbackConnection> receiveConnections;
	protected FutureConnection sendConnection = null;
	
	//Box ordered map
	protected Map<String, ArrayList<IAgent>> boxFollower;
	
	//received Messages
	protected Map<IAgent,LinkedList<Map<String,Object>>> receivedMessage;
   
	
	class MQTTConnecterListener implements Callback<Void>
	{
		CallbackConnection connection;
		String serverName;
		String mqttDest;
		
		MQTTConnecterListener(CallbackConnection lst, String server,String destination)
		{
			super();
			this.connection = lst;
			this.serverName = server;
			this.mqttDest = destination;
		}
		
		@Override
		public void onFailure(Throwable arg0) {
			System.out.println("Connection to MQTT failure!");
		}

		@Override
		public void onSuccess(Void arg0) {
			System.out.println("Connection to MQTT server passed!");
			receiveConnections.put(serverName, connection);
			
            Topic[] topics = {new Topic(mqttDest, QoS.AT_LEAST_ONCE)};
            connection.subscribe(topics, new Callback<byte[]>() {
                public void onSuccess(byte[] qoses) {
                }
                public void onFailure(Throwable value) {
                    value.printStackTrace();
                    System.exit(-2);
                }
            });
		}
	}
	
	class MQTTListener implements org.fusesource.mqtt.client.Listener
	{
		private String server;
		private CallbackConnection connection;
		private MQTTConnectorSk parentSkill;
		
		MQTTListener(String serverName,CallbackConnection connect,MQTTConnectorSk agentSkill) {
			this.server= serverName;
			this.connection=connect;
			this.parentSkill = agentSkill;
		}
		
		@Override
		public void onConnected() {
		}

		@Override
		public void onDisconnected() {}

		@Override
		public void onFailure(Throwable arg0) {
		}

		@Override
		public void onPublish(UTF8Buffer topic, Buffer msg, Runnable ack) {
			String topicName = topic.utf8().toString();
			String body = msg.utf8().toString();
			@SuppressWarnings("unchecked")
			Map<String,Object> mp = (Map<String,Object>) StreamConverter.convertStreamToObject(GAMA.getSimulation().getScope(), body);
			pushMessageToAgents(topicName,mp);
			ack.run();
		}
	}
	
	
	public MQTTConnectorSk()
	{
		super();
		receiveConnections = new HashMap<>();
		boxFollower = new HashMap<>();
		receivedMessage = new HashMap<>();
		sendConnection = null; 
	}
	
	
	public void pushMessageToAgents(String senderName, Map<String, Object> message)
	{
		
		for(IAgent agt:boxFollower.get(senderName))
		{
			message.put(INetworkSkill.FROM, senderName);
			this.receivedMessage.get(agt).add(message);
		}
	}
	
	 
	public void connectToServer(IAgent agent, String agentName, String server ) throws Exception  {
		IScope scope = agent.getScope();
		if(	sendConnection == null) 
			sendConnection= MQTTConnector.connectSender(server, MQTTConnector.DEFAULT_USER, MQTTConnector.DEFAULT_PASSWORD);
		CallbackConnection connection =  receiveConnections.get(server);
			if(connection == null)
			{
				try {
					connection = MQTTConnector.connectReceiver(server, MQTTConnector.DEFAULT_USER, MQTTConnector.DEFAULT_PASSWORD);
					connection.listener(new MQTTListener(server,connection,this));
					connection.connect(new MQTTConnecterListener(connection, server,agentName));
					receiveConnections.put(server, connection);
					
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} 
			}
			ArrayList<IAgent> agentBroadcast = this.boxFollower.get(agentName);
			if(agentBroadcast == null)
			{
				if(!(this.boxFollower.keySet().contains(agentName)))
				{
					Topic [] tps = {new Topic(agentName, QoS.AT_LEAST_ONCE)};
					connection.subscribe( tps,new Callback<byte[]>() {
	                    public void onSuccess(byte[] qoses) {
	                    }
	                    public void onFailure(Throwable value) {
	                        value.printStackTrace();
	                        System.exit(-2);
	                    }
	                });
				}
				agentBroadcast = new ArrayList<IAgent>();
				this.boxFollower.put(agentName, agentBroadcast);
			}
			if(!agentBroadcast.contains(scope.getAgentScope()))
			{
				agentBroadcast.add(scope.getAgentScope());
			}
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
		System.out.println(message);
		System.out.println(StreamConverter.convertObjectToStream(agt.getScope(),message));
		
		sendConnection.publish(new UTF8Buffer(dest), new AsciiBuffer(StreamConverter.convertObjectToStream(agt.getScope(),message)), QoS.AT_LEAST_ONCE, false);
	}


	@Override
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception {
		// TODO Auto-generated method stub
		
	}




	
}
