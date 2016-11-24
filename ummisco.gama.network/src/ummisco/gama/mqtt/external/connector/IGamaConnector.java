/*********************************************************************************************
 *
 * 'IGamaConnector.java, in plugin ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.mqtt.external.connector;

import java.util.Map;

public interface IGamaConnector {
	public void connect(String sensorName) throws Exception;
	public void connect(String sensorName, String server) throws Exception;
	public void sendData(Map<String, String >  data);
	
}
