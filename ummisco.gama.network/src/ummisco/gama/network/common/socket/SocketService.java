/*******************************************************************************************************
 *
 * SocketService.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.common.socket;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * The Interface SocketService.
 */
public interface SocketService {

	/**
	 * Start service.
	 *
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void startService() throws UnknownHostException, IOException;

	/**
	 * Stop service.
	 */
	void stopService();

	/**
	 * Received message.
	 *
	 * @param sender the sender
	 * @param message the message
	 */
	void receivedMessage(String sender, String message);

	/**
	 * Send message.
	 *
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void sendMessage(String message) throws IOException;

	/**
	 * Checks if is online.
	 *
	 * @return true, if is online
	 */
	boolean isOnline();

	/**
	 * Gets the remote address.
	 *
	 * @return the remote address
	 */
	String getRemoteAddress();

	/**
	 * Gets the local address.
	 *
	 * @return the local address
	 */
	String getLocalAddress();

	/** The end. */
	String END = "END";
	
	/** The gama start. */
	String GAMA_START = "(((GAMA)))";

}
