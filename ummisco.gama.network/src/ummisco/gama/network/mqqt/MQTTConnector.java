package ummisco.gama.network.mqqt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.GamaNetworkException;
import ummisco.gama.serializer.factory.StreamConverter;

public class MQTTConnector extends Connector{
	protected MqttClient sendConnection = null;
	
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
			String body =message.toString();
			
		}
		
	}

	
	
	
	
	@Override
	protected void subscribeToBox(IAgent agt, String boxName) {
		try {
			sendConnection.subscribe(boxName);
		} catch (MqttException e) {
			throw GamaNetworkException.cannotSubscribeToTopic(agt.getScope());
		}
		
	}

	@Override
	protected void releaseConnection(IScope scope) {
		try {
			sendConnection.disconnect();
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

}
