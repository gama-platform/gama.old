package ummisco.gama.mqtt.common;

import java.util.Map;

public interface IGamaConnector {
	public void connect(String sensorName) throws Exception;
	public void connect(String sensorName, String server, String login, String path) throws Exception;
	public void connect(String sensorName, String server) throws Exception;
	public void sendData(Map<String, String >  data);
	
}
