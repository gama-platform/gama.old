/*********************************************************************************************
 * 
 *
 * 'ICommunicatorSkill.java', in plugin 'ummisco.gama.communicator', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.network.skills;

import java.util.Observer;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;

public interface INetworkSkill  {
	public final static String CONNECT_TOPIC = "connect";
	public final static String SERVER_URL = "to";
	public final static String GROUP = "with_group";
	public final static String WITHNAME = "with_name";
	public final static String PROTOCOL = "protocol";
	public final static String PORT = "port";
	
	//Agent Data
	public final static String NET_AGENT_NAME = "network_name";
	public final static String NET_AGENT_GROUPS = "network_groups";
	public final static String NET_AGENT_SERVER = "network_server";
	
	//MESSAGE CONTENTS
	public final static String TO = "to";
	public final static String FROM = "from";
	public final static String CONTENT = "content";
	public final static String BROADCAST = "all";
	
	
	// CONNECTION PROTOCOL
	public final static String UDP_SERVER = "udp_server";
	public final static String UDP_CLIENT = "udp_client";
	public final static String TCP_SERVER = "tcp_server";
	public final static String TCP_CLIENT = "tcp_client";
	public final static String MQTT = "mqtt";
	
	
	///// SKILL NETWORK
	public static String NETWORK_SKILL = "network";
	public static String SEND_MESSAGE = "send_message";
	public static String FETCH_MESSAGE = "fetch_message";
	public static String LISTEN = "listen";
	public static String HAS_MORE_MESSAGE_IN_BOX="has_received_message";
	
	// SKILL SENSING
	public static String SENSING_SKILL  = "sensor";
	public static String SENSING_CONNECT = "connect_sensor";
	public static String SENSOR_NAME = "sensor_name";
	public static String FETCH_DATA = "fetch_data";
	public static String EMPTY_BUFFER="has_received_data";
	
	
	
	
	
}
