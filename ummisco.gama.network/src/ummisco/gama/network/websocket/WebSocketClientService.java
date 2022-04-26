/*******************************************************************************************************
 *
 * ClientService.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.websocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.common.socket.SocketService;

/**
 * The Class ClientService.
 */
public abstract class WebSocketClientService extends Thread implements SocketService {
	
	/** The socket. */
	private Socket socket;
	
	/** The server. */
	private final String server;
	
	/** The port. */
	private final int port;
	
	/** The receiver. */
	private BufferedReader receiver;
	
	/** The sender. */
	private PrintWriter sender;
	
	/** The is alive. */
	private boolean isAlive;
	// private IConnector modelConnector;

	/**
	 * Instantiates a new client service.
	 *
	 * @param server the server
	 * @param port the port
	 * @param connector the connector
	 */
	public WebSocketClientService(final String server, final int port, final IConnector connector) {
		// this.modelConnector = connector;
		this.port = port;
		this.server = server;
	}

	@Override
	public String getRemoteAddress() {
		if (socket == null) return null;
		return this.socket.getInetAddress() + ":" + this.port;
	}

	@Override
	public String getLocalAddress() {
		if (socket == null) return null;
		return this.socket.getLocalAddress() + ":" + this.port;
	}

	@Override
	public void startService() throws UnknownHostException, IOException {
		socket = new Socket(this.server, this.port);

		isAlive = true;

		this.start();

	}

	@Override
	public void stopService() {
		this.isAlive = false;
		if (sender != null) { sender.close(); }
		try {
			if (receiver != null) { receiver.close(); }
			socket.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isOnline() {
		return isAlive;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void run() {
		try {
			while (this.isAlive) {
				receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String msg = receiver.readLine();
				msg = msg.replaceAll("@n@", "\n");
				msg = msg.replaceAll("@b@@r@", "\b\r");
				receivedMessage(this.socket.getInetAddress() + ":" + this.port, msg);
			}
		} catch (final SocketTimeoutException e) {
			DEBUG.LOG("Socket timeout");
		} catch (final SocketException e) {
			DEBUG.LOG("Socket closed");
		} catch (final IOException e1) {
			DEBUG.LOG("Socket error" + e1);
		}

	}

	@Override
	public void sendMessage(final String message) throws IOException {
		if (socket == null || !isOnline()) return;
		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		final String msg = message.replaceAll("\n", "@n@").replaceAll("\b\r", "@b@@r@");
		sender.println(msg + "\n");
		sender.flush();
	}

}
