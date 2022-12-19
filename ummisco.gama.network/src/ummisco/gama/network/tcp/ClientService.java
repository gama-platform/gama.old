/*******************************************************************************************************
 *
 * ClientService.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
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
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.common.MessageFactory;
import ummisco.gama.network.common.MessageFactory.MessageType;
import ummisco.gama.network.common.socket.SocketService;

/**
 * The Class ClientService.
 */
public class ClientService extends Thread implements SocketService {
	
	/** The socket. */
	protected Socket socket;
	
	/** The server. */
	protected final String server;
	
	/** The port. */
	protected final int port;
	
	/** The receiver. */
	protected BufferedReader receiver;
	
	/** The sender. */
	protected PrintWriter sender;
	
	/** The is alive. */
	protected boolean isAlive;
	
	protected IConnector connector;

	/**
	 * Instantiates a new client service.
	 *
	 * @param server the server
	 * @param port the port
	 * @param connector the connector
	 */
	public ClientService(final String server, final int port, final IConnector connector) {
		// this.modelConnector = connector;
		this.port = port;
		this.server = server;
		this.connector = connector;
	}

	/**
	 * Instantiates a new client service.
	 *
	 * @param server the server
	 * @param port the port
	 * @param connector the connector
	 */
	public ClientService(final Socket sk, final IConnector connector) {
		this.socket=sk;
		this.server = sk.getInetAddress().getHostAddress();
		this.port = sk.getPort();
		this.connector = connector;
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
		if(socket==null) {
			socket = new Socket(this.server, this.port);
		}
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
			
			e.printStackTrace();
		}
	}

	@Override
	public boolean isOnline() {
		return isAlive;
	}

	@Override
	public void run() {
		try {
			while (this.isAlive) {
				receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String msg = receiver.readLine();
				if (msg != null) { //protects against NPE occurring with empty streams
					msg = msg.replaceAll("@n@", "\n");
					msg = msg.replaceAll("@b@@r@", "\b\r");
					receivedMessage(this.socket.toString(), msg);					
				}
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
	public void receivedMessage(final String sender, final String message) {
		final MessageType mte = MessageFactory.identifyMessageType(message);
		if (mte.equals(MessageType.COMMAND_MESSAGE)) {
			//TODO: check if it is really only for tcp
			((TCPConnector)connector).extractAndApplyCommand(sender, message); 
		} else { 
			final String r = connector.isRaw() ? message : MessageFactory.unpackReceiverName(message);
			((Connector)connector).storeMessage(sender, r, message);
		}
	}

	@Override
	public void sendMessage(final String message, final String receiver) throws IOException {
		
		if (socket == null || !isOnline()) return;
		
		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);		
		
		//If raw connection we do not append an end of line nor escape anything
		if (connector.isRaw()) {
			sender.print(message);
			sender.flush();
		}
		else {
			String msg = message.replaceAll("\n", "@n@")
								.replaceAll("\b\r", "@b@@r@")
							+ 	"\n";
			
			//TODO: do we really need to append a '\n' while we already use println ?
			sender.println(msg);
		}
	}
	
	@Override
	public void sendMessage(final String message) throws IOException {
		sendMessage(message, null);
	}

}
