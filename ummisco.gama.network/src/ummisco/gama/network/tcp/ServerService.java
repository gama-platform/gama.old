/*******************************************************************************************************
 *
 * ServerService.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.network.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.socket.SocketService;

/**
 * The Class ServerService.
 */
public abstract class ServerService extends Thread implements SocketService {

	/** The server socket. */
	private ServerSocket serverSocket;

	/** The port. */
	private final int port;

	/** The is alive. */
	private boolean isAlive;

	/** The is online. */
	private boolean isOnline;

	/** The sender. */
	private PrintWriter sender;

	/** The current socket. */
	private Socket currentSocket;

	/** The receiver. */
	BufferedReader receiver = null;

	/**
	 * Instantiates a new server service.
	 *
	 * @param port
	 *            the port
	 */
	public ServerService(final int port) {
		this.port = port;
		this.isAlive = false;
		this.isOnline = false;
	}

	@Override
	public String getRemoteAddress() {
		if (currentSocket == null) return null;
		return this.currentSocket.getInetAddress() + ":" + this.port;
	}

	@Override
	public String getLocalAddress() {
		if (currentSocket == null) return null;
		return this.currentSocket.getLocalAddress() + ":" + this.port;
	}

	@Override
	public void startService() throws UnknownHostException, IOException {
		this.serverSocket = new ServerSocket(port);
		this.isAlive = true;
		this.isOnline = true;
		this.start();
	}

	@Override
	public void run() {
		while (this.isAlive) {
			isOnline = true;
			try {
				DEBUG.OUT("before accept wait...");
				currentSocket = this.serverSocket.accept();
				String msg = "";
				do {
					DEBUG.OUT("wait message ...........");
					receiver = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
					msg = receiver.readLine();
					if (msg != null) {
						msg = msg.replace("@n@", "\n");
						msg = msg.replace("@b@@r@", "\b\r");
						receivedMessage(this.currentSocket.getInetAddress() + ":" + this.port, msg);
					}
					DEBUG.OUT("fin traitement message ..." + this.isOnline);
				} while (isOnline);

			} catch (final SocketTimeoutException e) {
				DEBUG.LOG("Socket timeout");
			} catch (final SocketException e) {
				DEBUG.LOG("Socket closed");
			} catch (final IOException e1) {
				DEBUG.LOG("Socket error" + e1);
				/// isOnline = false;
			} catch (final Exception e) {
				DEBUG.LOG("Exception occured in socket");
			}
		}
	}

	@Override
	public boolean isOnline() { return isAlive && isOnline; }

	@Override
	public void stopService() {
		isOnline = false;
		isAlive = false;

		if (sender != null) { sender.close(); }
		try {
			if (receiver != null) { receiver.close(); }
			if (currentSocket != null) { currentSocket.close(); }
			if (serverSocket != null) { serverSocket.close(); }
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.interrupt();
	}

	@Override
	public void sendMessage(final String msg) throws IOException {
		String message = msg;
		if (currentSocket == null || !isOnline()) return;
		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(currentSocket.getOutputStream())), true);
		message = message.replace("\n", "@n@");
		message = message.replace("\b\r", "@b@@r@");
		sender.println(message + "\n");
		sender.flush();

	}

}
