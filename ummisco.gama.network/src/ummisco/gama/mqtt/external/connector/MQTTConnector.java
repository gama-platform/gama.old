/*********************************************************************************************
 *
 * 'MQTTConnector.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.mqtt.external.connector;

import java.util.Calendar;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTConnector {
	// public static String DEFAULT_USER = env("ACTIVEMQ_USER", "admin");
	// public static String DEFAULT_PASSWORD = env("ACTIVEMQ_PASSWORD", "password");
	public static String DEFAULT_USER = env("ACTIVEMQ_USER", "admin");
	public static String DEFAULT_PASSWORD = env("ACTIVEMQ_PASSWORD", "password");
	public static String DEFAULT_HOST = env("ACTIVEMQ_HOST", "localhost");
	public static int port = Integer.parseInt(env("ACTIVEMQ_PORT", "1883"));
	public static final String DESTINATION_PREFIX = "";// "topic/sensors/";

	private String boxName = null;
	private MqttClient connection = null;
	// private String topic = null;

	private static String env(final String key, final String defaultValue) {
		final String rc = System.getenv(key);
		if (rc == null) { return defaultValue; }
		return rc;
	}

	public static MQTTConnector connectSender(final String server, final String boxN) throws MqttException {
		return connectSender(server, boxN, DEFAULT_USER, DEFAULT_PASSWORD);
	}

	public void sendPlainData(final String data) {
		// System.out.println("topic :"+topic+": ");
		try {
			connection.publish(boxName, data.getBytes(), 1, false);
		} catch (final MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static MQTTConnector connectSender(final String server, final String boxN, final String userN,
			final String pass) throws MqttException {
		final MQTTConnector res = new MQTTConnector();
		res.connection = new MqttClient("tcp://" + server + ":" + port,
				"gama" + Calendar.getInstance().getTimeInMillis() + "@" + server, new MemoryPersistence());
		final MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		connOpts.setCleanSession(true);
		connOpts.setKeepAliveInterval(30);
		connOpts.setUserName(userN);
		connOpts.setPassword(pass.toCharArray());
		res.connection.connect(connOpts);
		res.boxName = boxN;
		return res;
	}

}
