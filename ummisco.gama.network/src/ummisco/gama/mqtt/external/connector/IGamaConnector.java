package ummisco.gama.mqtt.external.connector;

import java.util.Map;

public interface IGamaConnector {
	public void connect(String sensorName) throws Exception;
	public void connect(String sensorName, String server) throws Exception;
	public void sendData(Map<String, String >  data);
	
}
