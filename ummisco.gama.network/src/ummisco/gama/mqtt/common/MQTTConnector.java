package ummisco.gama.mqtt.common;

import java.net.URISyntaxException;
import java.util.Calendar;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public final class MQTTConnector {
	//public static String DEFAULT_USER = env("ACTIVEMQ_USER", "admin");
	//public static String DEFAULT_PASSWORD = env("ACTIVEMQ_PASSWORD", "password");
	public static String DEFAULT_USER = env("ACTIVEMQ_USER", "guest");
	public static String DEFAULT_PASSWORD = env("ACTIVEMQ_PASSWORD", "guest");
	public static String DEFAULT_HOST = env("ACTIVEMQ_HOST", "localhost");
	public static int port = Integer.parseInt(env("ACTIVEMQ_PORT", "1883"));
	public static final String DESTINATION_PREFIX = "";//"topic/sensors/";
    
    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }
    
    public static MqttClient connectReceiver( String server) throws MqttException  
    {
    	MqttClient sampleClient = new MqttClient("tcp://"+server+":"+port, "gama"+Calendar.getInstance().getTimeInMillis()+"@"+server, new MemoryPersistence());
    	return sampleClient;
    }
    
	public static MqttClient connectSender( String server) throws Exception
	{
		return connectReceiver(  server) ;
/*		MQTT mqtt = new MQTT();
		System.out.println(mqtt.isCleanSession());
        mqtt.setHost(server, port);
	    mqtt.setUserName(login);
	    mqtt.setPassword(pass);
	    FutureConnection connection = mqtt.futureConnection();
	    connection.connect().await();
	    return connection;*/
	}
	
	
}
