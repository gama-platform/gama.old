/*******************************************************************************************************
 *
 * IConnector.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.network.common.socket.SocketService;

/**
 * The Interface IConnector.
 */
public interface IConnector {
	
	SocketService getSocketService();
	/**
	 * Connect.
	 *
	 * @param agent the agent
	 * @throws GamaNetworkException the gama network exception
	 */
	void connect(IAgent agent) throws GamaNetworkException;

	/**
	 * Close.
	 *
	 * @param scope the scope
	 * @throws GamaNetworkException the gama network exception
	 */
	void close(final IScope scope) throws GamaNetworkException;

	/**
	 * Send.
	 *
	 * @param agent the agent
	 * @param dest the dest
	 * @param data the data
	 */
	void send(IAgent agent, String dest, GamaMessage data);

	/**
	 * Fetch message box.
	 *
	 * @param agt the agt
	 * @return the list
	 */
	List<ConnectorMessage> fetchMessageBox(final IAgent agt);

	/**
	 * Configure.
	 *
	 * @param parameterName the parameter name
	 * @param value the value
	 */
	void configure(String parameterName, String value);

	/**
	 * Join A group.
	 *
	 * @param agt the agt
	 * @param groupName the group name
	 */
	void joinAGroup(final IAgent agt, final String groupName);

	/**
	 * Leave the group.
	 *
	 * @param agt the agt
	 * @param groupName the group name
	 */
	void leaveTheGroup(final IAgent agt, final String groupName);

	/**
	 * Fetch all messages.
	 *
	 * @return the map
	 */
	Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages();

	/**
	 * Force network use.
	 *
	 * @param b the b
	 */
	void forceNetworkUse(boolean b);
	
	/**
	 * 
	 * @return true if the connection is raw (no addition message-wrapping), false otherwise
	 */
	boolean isRaw();
	
	/**
	 * set raw
	 * @param isRaw
	 */
	void setRaw(boolean isRaw);

	/** The server url. */
	String SERVER_URL = "SERVER_URL";
	
	/** The server port. */
	String SERVER_PORT = "SERVER_PORT";
	
	/** The local name. */
	String LOCAL_NAME = "LOCAL_NAME";
	
	/** The login. */
	String LOGIN = "LOGIN";
	
	/** The password. */
	String PASSWORD = "PASSWORD";
	
	/** The packet size. */
	String PACKET_SIZE = "PACKET_SIZE";
}
