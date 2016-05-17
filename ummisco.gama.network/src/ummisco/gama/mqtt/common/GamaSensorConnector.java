package ummisco.gama.mqtt.common;


import java.util.HashMap;
import java.util.Map;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import ummisco.gama.network.common.SimpleMapSerializer;

public class GamaSensorConnector  implements IGamaConnector {
    String boxName =  null;
    UTF8Buffer topic = null;
    FutureConnection connection = null;
    
    
    @Override
	public void connect(String sensorName) throws Exception {
		this.connect(MQTTConnector.DESTINATION_PREFIX+sensorName, MQTTConnector.DEFAULT_HOST, MQTTConnector.DEFAULT_USER, MQTTConnector.DEFAULT_PASSWORD);
	}

	@Override
	public void connect(String sensorName, String server) throws Exception {
		this.connect(MQTTConnector.DESTINATION_PREFIX+sensorName, server, MQTTConnector.DEFAULT_USER, MQTTConnector.DEFAULT_PASSWORD);
	}
    
	@Override
	public void connect(String boxName, String server, String login, String path) throws Exception {
		this.connection = MQTTConnector.connectSender(server, login, path);
		this.boxName = boxName;
	}
	
/*	private UTF8Buffer registerAsBuffer(String name)
	{
		UTF8Buffer tmp = this.buffers.get(name);
		if(tmp == null )
		{
			tmp = new UTF8Buffer(destination+name);
			this.buffers.put(name, tmp);
		}
		return tmp;
	}
	*/
	public void sendData(Map<String, String >  data)
	{
		if(topic == null)
			topic = new UTF8Buffer(boxName);
		//System.out.println("topic :"+topic+": ");
		 connection.publish(topic, new AsciiBuffer(SimpleMapSerializer.map2String(data)), QoS.AT_LEAST_ONCE, false);
	}
	
	public static void main(String [] arg) throws Exception
	{
		GamaSensorConnector tmp = new GamaSensorConnector(); 
		tmp.connect("test","localhost");
	}


}
