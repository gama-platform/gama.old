package ummisco.gama.mqtt.common;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;

public class MQTTConnector {
	public static String DEFAULT_USER = env("ACTIVEMQ_USER", "admin");
	public static String DEFAULT_PASSWORD = env("ACTIVEMQ_PASSWORD", "password");
	public static String DEFAULT_HOST = env("ACTIVEMQ_HOST", "localhost");
	public static int port = Integer.parseInt(env("ACTIVEMQ_PORT", "1883"));
	public static final String DESTINATION_PREFIX = "";//"topic/sensors/";
    
    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }
    
    public static CallbackConnection connectReceiver( String server, String login, String pass) throws URISyntaxException
    {
        MQTT mqtt = new MQTT();
        mqtt.setHost(server, port);
        mqtt.setUserName(login);
        mqtt.setPassword(pass);
        CallbackConnection connection = mqtt.callbackConnection();
        return connection;
    }
    
	public static FutureConnection connectSender( String server, String login, String pass) throws Exception
	{
		MQTT mqtt = new MQTT();
        mqtt.setHost(server, port);
	    mqtt.setUserName(login);
	    mqtt.setPassword(pass);
	    FutureConnection connection = mqtt.futureConnection();
	    connection.connect().await();
	    return connection;
	}
}
