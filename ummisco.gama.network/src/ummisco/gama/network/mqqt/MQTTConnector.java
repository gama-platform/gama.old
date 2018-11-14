/*********************************************************************************************
 *
 * 'MQTTConnector.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.GamaNetworkException;

public final class MQTTConnector extends Connector {

	static {
		DEBUG.OFF();
	}
	public static String DEFAULT_USER = "gama_demo";
	public static String DEFAULT_LOCAL_NAME = "gama-" + Calendar.getInstance().getTimeInMillis() + "@";
	public static String DEFAULT_PASSWORD = "gama_demo";
	public static String DEFAULT_HOST = "vmpams.ird.fr";
	public static String DEFAULT_PORT = "1935";

	protected MqttClient sendConnection = null;
	protected IScope simulationScope;

	public MQTTConnector(final IScope scope) {
		this.simulationScope = scope;
	}

	class Callback implements MqttCallback {
		@Override
		public void connectionLost(final Throwable arg0) {
			throw GamaNetworkException.cannotBeConnectedFailure(GAMA.getSimulation().getScope());
		}

		@Override
		public void deliveryComplete(final IMqttDeliveryToken arg0) {
			DEBUG.OUT("message sended");
		}

		@Override
		public void messageArrived(final String topic, final MqttMessage message) throws Exception {
			final String body = message.toString();
			storeMessage(topic, body);
		}
	}

	@Override
	protected void releaseConnection(final IScope scope) {
		try {
			if( (sendConnection != null) && (sendConnection.isConnected()) ) {
				sendConnection.disconnect();
				sendConnection = null;
			}
		} catch (final MqttException e) {
			throw GamaNetworkException.cannotBeDisconnectedFailure(scope);
		}
	}

	@Override
	protected void sendMessage(final IAgent sender, final String receiver, final String content) {
		final MqttMessage mm = new MqttMessage(content.getBytes());
		try {
			sendConnection.publish(receiver, mm);
		} catch (final MqttException e) {
			throw GamaNetworkException.cannotSendMessage(sender.getScope(), receiver);
		}
	}

	@Override
	protected void subscribeToGroup(final IAgent agt, final String boxName) {
		try {
			sendConnection.subscribe(boxName);
		} catch (final MqttException e) {
			e.printStackTrace();
			throw GamaNetworkException.cannotSubscribeToTopic(agt.getScope(), e.toString());
		}

	}

	@Override
	public void unsubscribeGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		try {
			sendConnection.unsubscribe(boxName);
		} catch (final MqttException e) {
			throw GamaNetworkException.cannotUnsuscribeToTopic(simulationScope, boxName);
		}
	}

	@Override
	protected void connectToServer(final IAgent agent) throws GamaNetworkException {
		if (sendConnection == null) {
			String server = this.getConfigurationParameter(SERVER_URL);
			String port = this.getConfigurationParameter(SERVER_PORT);
			String userName = this.getConfigurationParameter(LOGIN);
			String password = this.getConfigurationParameter(PASSWORD);
			String localName = this.getConfigurationParameter(LOCAL_NAME);

			server = server == null ? DEFAULT_HOST : server;
			port = port == null ? DEFAULT_PORT : port;
			userName = userName == null ? DEFAULT_USER : userName;
			password = password == null ? DEFAULT_PASSWORD : userName;
			localName = localName == null ? DEFAULT_LOCAL_NAME + server : localName;

			System.out.println("url "+ "tcp://" + server + ":" + port);
			
			try {
				sendConnection = new MqttClient("tcp://" + server + ":" + port, localName, new MemoryPersistence());
				final MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
				sendConnection.setCallback(new Callback());
				connOpts.setCleanSession(true);
				connOpts.setKeepAliveInterval(30);
				connOpts.setUserName(userName);
				connOpts.setPassword(password.toCharArray());
				sendConnection.connect(connOpts);
			} catch (final MqttException e) {
				throw GamaNetworkException.cannotBeConnectedFailure(simulationScope);
			}

		}

	}
}
