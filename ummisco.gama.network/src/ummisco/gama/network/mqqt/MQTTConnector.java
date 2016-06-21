package ummisco.gama.network.mqqt;

import java.util.Calendar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.GamaNetworkException;

public final class MQTTConnector extends Connector{
	public static String DEFAULT_USER = "admin";
	public static String DEFAULT_LOCAL_NAME = "gama-"+Calendar.getInstance().getTimeInMillis()+"@";
	public static String DEFAULT_PASSWORD = "password";
	public static String DEFAULT_HOST =  "localhost";
	public static String DEFAULT_PORT =  "1883";
	
	protected MqttClient sendConnection = null;
	protected IScope simulationScope;
	
	public MQTTConnector(IScope scope)
	{
		this.simulationScope = scope;
	}
	
	class Callback implements MqttCallback
	{
		@Override
		public void connectionLost(Throwable arg0) {
			throw GamaNetworkException.cannotBeConnectedFailure(GAMA.getSimulation().getScope());
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken arg0) {
			System.out.println("message sended");
		}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String body = message.toString();
			storeMessage(topic,body);
		}
	}
	

	@Override
	protected void releaseConnection(IScope scope) {
		try {
			sendConnection.disconnect();
			sendConnection = null;
		} catch (MqttException e) {
			throw GamaNetworkException.cannotBeDisconnectedFailure(scope);
		}	
	}

	@Override
	protected void sendMessage(IAgent sender, String receiver, String content) {
			MqttMessage mm = new MqttMessage(content.getBytes());
			try {
				sendConnection.publish(receiver, mm);
			} catch (MqttException e) {
				throw GamaNetworkException.cannotSendMessage(sender.getScope(),receiver);
			}
	}

	@Override
	protected void subscribeToGroup(IAgent agt, String boxName) {
		try {
			sendConnection.subscribe(boxName);
		} catch (MqttException e) {
			e.printStackTrace();
			throw GamaNetworkException.cannotSubscribeToTopic(agt.getScope(),e.toString());
		}
		
	}
	
	@Override
	public void unsubscribeGroup(IAgent agt, String boxName) throws GamaNetworkException {
		try {
			sendConnection.unsubscribe(boxName);
		} catch (MqttException e) {
			throw GamaNetworkException.cannotUnsuscribeToTopic(simulationScope,boxName);
		}
	}

	@Override
	protected void connectToServer(IAgent agent) throws GamaNetworkException {
		if(sendConnection == null)
		{
			String server = this.getConfigurationParameter(SERVER_URL);
			String port = this.getConfigurationParameter(SERVER_PORT);
			String userName = this.getConfigurationParameter(LOGIN);
			String password = this.getConfigurationParameter(PASSWORD);
			String localName = this.getConfigurationParameter(LOCAL_NAME);
			
			server = (server==null?DEFAULT_HOST:server);
			port = (port==null?DEFAULT_PORT:port);
			userName = (userName==null?DEFAULT_USER:userName);
			password = (password==null?DEFAULT_PASSWORD:userName);
			localName = (localName==null?DEFAULT_LOCAL_NAME+server:localName);
			
			try {
				sendConnection = new MqttClient("tcp://"+server+":"+port, localName, new MemoryPersistence());
				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
		        sendConnection.setCallback(new Callback());
		        connOpts.setCleanSession(true);
		        connOpts.setKeepAliveInterval(30);
		        connOpts.setUserName(userName);
		        connOpts.setPassword(password.toCharArray());
		  		sendConnection.connect(connOpts);
			} catch (MqttException e) {
				e.printStackTrace();
			}

		}
    	
		
	}
}
