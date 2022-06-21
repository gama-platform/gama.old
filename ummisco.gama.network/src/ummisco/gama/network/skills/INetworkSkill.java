/*******************************************************************************************************
 *
 * INetworkSkill.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.skills;

/**
 * The Interface INetworkSkill.
 */
public interface INetworkSkill {
	
	/** The connect topic. */
	String CONNECT_TOPIC = "connect";
	
	/** The server url. */
	String SERVER_URL = "to";
	
	/** The login. */
	String LOGIN = "login";
	
	/** The password. */
	String PASSWORD = "password";
	
	/** The withname. */
	String WITHNAME = "with_name";
	
	/** The protocol. */
	String PROTOCOL = "protocol";
	
	/** The port. */
	String PORT = "port";

	/** The net agent name. */
	// Agent Data
	String NET_AGENT_NAME = "network_name";
	
	/** The net agent groups. */
	String NET_AGENT_GROUPS = "network_groups";
	
	/** The net agent server. */
	String NET_AGENT_SERVER = "network_server";

	/** The websocket server. */
	// CONNECTION PROTOCOL
	String WEBSOCKET_SERVER = "websocket_server";
	
	/** The websocket client. */
	String WEBSOCKET_CLIENT = "websocket_client";

	/** The udp server. */
	// CONNECTION PROTOCOL
	String UDP_SERVER = "udp_server";
	
	/** The udp client. */
	String UDP_CLIENT = "udp_emitter";
	
	/** The tcp server. */
	String TCP_SERVER = "tcp_server";
	
	/** The tcp client. */
	String TCP_CLIENT = "tcp_client";

	/** For HTTP requests. */
	String HTTP_REQUEST = "http";

	/** The network skill. */
	///// SKILL NETWORK
	String NETWORK_SKILL = "network";
	
	/** The fetch message. */
	String FETCH_MESSAGE = "fetch_message";
	
	/** The has more message in box. */
	String HAS_MORE_MESSAGE_IN_BOX = "has_more_message";
	
	/** The force network use. */
	String FORCE_NETWORK_USE = "force_network_use";

	/** The register to group. */
	///// GROUP MANAGEMENT
	String REGISTER_TO_GROUP = "join_group";
	
	/** The leave the group. */
	String LEAVE_THE_GROUP = "leave_group";
	
	/** The default group. */
	String[] DEFAULT_GROUP = { "ALL" };

	/** The simulate step. */
	// SKILL TEST
	String FETCH_MESSAGE_FROM_NETWORK = "fetch_message_from_network";

	/** The max data packet size. */
	// UDP data packet max size
	String MAX_DATA_PACKET_SIZE = "size_packet";
}
