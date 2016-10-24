package ummisco.gama.mqtt.external.connector;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.network.common.CompositeGamaMessage;
import ummisco.gama.network.common.NetworkMessage;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.serializer.factory.StreamConverter;


public class MQTTConnector {
	//public static String DEFAULT_USER = env("ACTIVEMQ_USER", "admin");
	//public static String DEFAULT_PASSWORD = env("ACTIVEMQ_PASSWORD", "password");
	public static String DEFAULT_USER = env("ACTIVEMQ_USER", "admin");
	public static String DEFAULT_PASSWORD = env("ACTIVEMQ_PASSWORD", "password");
	public static String DEFAULT_HOST = env("ACTIVEMQ_HOST", "localhost");
	public static int port = Integer.parseInt(env("ACTIVEMQ_PORT", "1883"));
	public static final String DESTINATION_PREFIX = "";//"topic/sensors/";
    
    private String boxName =  null;
    private MqttClient connection = null;
    private String topic = null;
	
    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }
    
    public static MQTTConnector connectSender( String server, String boxN) throws MqttException  
    {
    	return connectSender(server, boxN, DEFAULT_USER, DEFAULT_PASSWORD);
    }
    
    
	public void sendPlainData(String data)
	{
		//System.out.println("topic :"+topic+": ");
		 try {
			connection.publish(boxName, data.getBytes(), 1, false);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
     
	
    public static MQTTConnector connectSender( String server, String boxN, String userN, String pass) throws MqttException  
    {
    	MQTTConnector res = new MQTTConnector();
    	res.connection = new MqttClient("tcp://"+server+":"+port, "gama"+Calendar.getInstance().getTimeInMillis()+"@"+server, new MemoryPersistence());
		MqttConnectOptions connOpts = new MqttConnectOptions();
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
