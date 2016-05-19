package ummisco.gama.network.mqqt;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import msi.gaml.types.GamaNoType;
import msi.gaml.types.Types;
import ummisco.gama.mqtt.common.MQTTConnector;
import ummisco.gama.network.skills.GamaNetworkException;
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
	//protected Map<String, CallbackConnection> receiveConnections;
	protected CallbackConnection receiveConnections;
	protected FutureConnection sendConnection = null;
	
	//Box ordered map
	protected Map<String, ArrayList<IAgent>> boxFollower;
	
	//received Messages
	protected Map<IAgent,LinkedList<Map<String,Object>>> receivedMessage;
   
	protected List<String> topicSuscribingPending ;
	protected boolean isConnected = false;
	
	
	class MQTTConnecterListener implements Callback<Void>
	{
		
		@Override
		public void onFailure(Throwable arg0) {
			throw GamaNetworkException.cannotBeDisconnectedFailure(GAMA.getSimulation().getScope());
		}

		@Override
		public void onSuccess(Void arg0) {
			isConnected = true;
			registerToTopic();
		}
	}
	
	class MQTTListener implements org.fusesource.mqtt.client.Listener
	{
		@Override
		public void onConnected() {
		}

		@Override
		public void onDisconnected() {
		}

		@Override
		public void onFailure(Throwable arg0) {
			throw GamaNetworkException.cannotBeDisconnectedFailure(null);
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
		receiveConnections = null;//new HashMap<>();
		boxFollower = new HashMap<>();
		topicSuscribingPending = new ArrayList<String>();
		receivedMessage = new HashMap<>();
		sendConnection = null; 
	}
	
	
	public void pushMessageToAgents(String senderName, Map<String, Object> message)
	{
		
		for(IAgent agt:boxFollower.get(senderName))
		{
			//message.put(INetworkSkill.FROM, senderName);
			this.receivedMessage.get(agt).add(message);
		}
	}
	
	public void registerAgentToGroup(IAgent agent, String groupName)
	{
		ArrayList<IAgent> agentBroadcast = this.boxFollower.get(groupName);
		
		if(agentBroadcast ==null)
		{
			this.topicSuscribingPending.add(groupName);
			registerToTopic();
			agentBroadcast = new ArrayList<IAgent>();
			this.boxFollower.put(groupName, agentBroadcast);
		}
		if(!agentBroadcast.contains(agent))
		{
			agentBroadcast.add(agent);
		}		
	}
	
	private void registerToTopic()
	{
		if(this.isConnected)
		{
			for(String topic:this.topicSuscribingPending)
			{
				Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};
		        this.receiveConnections.subscribe(topics, new Callback<byte[]>() {
		        public void onSuccess(byte[] qoses) {}
		        public void onFailure(Throwable value) {
		        	throw GamaNetworkException.cannotBeConnectedFailure(GAMA.getSimulation().getScope());
		        }
		        });
			}
		}

	}
	
	 
	public void connectToServer(IAgent agent, String agentName, String server ) throws Exception  {
		IScope scope = agent.getScope();
		if(	this.sendConnection == null) 
			this.sendConnection= MQTTConnector.connectSender(server, MQTTConnector.DEFAULT_USER, MQTTConnector.DEFAULT_PASSWORD);
		if(this.sendConnection == null)
			throw GamaNetworkException.cannotBeConnectedFailure(scope);

		//CallbackConnection connection =  receiveConnections;//.get(server);
		if(receiveConnections == null)
		{
			receiveConnections =  MQTTConnector.connectReceiver(server, MQTTConnector.DEFAULT_USER, MQTTConnector.DEFAULT_PASSWORD);
			if(receiveConnections == null)
				throw GamaNetworkException.cannotBeConnectedFailure(scope);
			receiveConnections.listener(new MQTTListener());
			receiveConnections.connect(new MQTTConnecterListener());
		}

		registerAgentToGroup(agent, agentName);
		
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
		sendConnection.publish(new UTF8Buffer(dest), new AsciiBuffer(StreamConverter.convertObjectToStream(agt.getScope(),message)), QoS.AT_LEAST_ONCE, false);
	}


	@Override
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception {
		this.connectToServer(agent, dest, server);
	}


	@Override
	public void close(final IScope scope) throws GamaNetworkException {
		MQTTCallBack listener = new MQTTCallBack(scope);
		//Close listening connection
		receiveConnections.disconnect(listener);
		receiveConnections = null;
		sendConnection.disconnect();
		boxFollower = new HashMap<>();
		receivedMessage = new HashMap<>();
		sendConnection = null; 
	}


	 class MQTTCallBack implements Callback<Void>
	{
		private IScope scope;
		public MQTTCallBack(IScope s)
		{
			scope = s;
		}
		@Override
		public void onFailure(Throwable arg0) {
			throw GamaNetworkException.cannotBeDisconnectedFailure(scope);
		}

		@Override
		public void onSuccess(Void arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}


	@Override
	public void registerToGroup(IAgent agt, String groupName) {
		
		
	}


	@Override
	public void leaveTheGroup(IAgent agt, String groupName) {
		// TODO Auto-generated method stub
		
	}

	
}
