/*********************************************************************************************
 *
 * 'INetworkSkill.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.network.skills;

public interface INetworkSkill {
	String CONNECT_TOPIC = "connect";
	String SERVER_URL = "to";
	String LOGIN = "login";
	String PASSWORD = "password";
	String WITHNAME = "with_name";
	String PROTOCOL = "protocol";
	String PORT = "port";

	// Agent Data
	String NET_AGENT_NAME = "network_name";
	String NET_AGENT_GROUPS = "network_groups";
	String NET_AGENT_SERVER = "network_server";

	// CONNECTION PROTOCOL
	String UDP_SERVER = "udp_server";
	String UDP_CLIENT = "udp_emitter";
	String TCP_SERVER = "tcp_server";
	String TCP_CLIENT = "tcp_client";

	///// SKILL NETWORK
	String NETWORK_SKILL = "network";
	String FETCH_MESSAGE = "fetch_message";
	String HAS_MORE_MESSAGE_IN_BOX = "has_more_message";
	String FORCE_NETWORK_USE = "force_network_use";

	///// GROUP MANAGEMENT
	String REGISTER_TO_GROUP = "join_group";
	String LEAVE_THE_GROUP = "leave_group";
	String[] DEFAULT_GROUP = { "ALL" };

	// SKILL TEST
	String SIMULATE_STEP = "simulate_step";

	// UDP data packet max size
	String MAX_DATA_PACKET_SIZE = "size_packet";
}
