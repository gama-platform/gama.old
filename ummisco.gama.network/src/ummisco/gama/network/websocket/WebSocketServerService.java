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
package ummisco.gama.network.websocket;

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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.common.MessageFactory;
import ummisco.gama.network.common.MessageFactory.MessageType;
import ummisco.gama.network.common.socket.SocketService;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.network.tcp.ClientService;
import ummisco.gama.network.tcp.ServerService;
import ummisco.gama.network.tcp.TCPConnector;

/**
 * The Class ServerService.
 */
public class WebSocketServerService extends ServerService {

	/** The server socket. */
	protected GamaServer serverSocket;

	public WebSocketServerService(IAgent agent, int port, IConnector conn) {
		super(agent, port, conn);
	}

	@Override
	public void startService() throws UnknownHostException, IOException {
		this.serverSocket = new GamaServer(port, this);
		this.isAlive = true;
		this.isOnline = true;
		this.start();
		this.serverSocket.start();
	}

	@Override
	public void run() {
		while (this.isAlive) {
			isOnline = true;

		}
		// DEBUG.OUT("closed ");
		try {
			myAgent.setAttribute(TCPConnector._TCP_SERVER + serverSocket.getPort(), null);
		} catch (final Exception e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(final String msg) throws IOException {
		String message = msg;
		if (serverSocket == null || !isOnline()) return;
		message = message.replace("\n", "@n@");
		message = message.replace("\b\r", "@b@@r@");
		serverSocket.broadcast(message);
//		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())), true);
//		sender.println(message );//+"\n" 
//		sender.flush();
		
//		DataOutputStream outToServer = new DataOutputStream(currentSocket.getOutputStream());  
//		outToServer.writeUTF(message +"\n");
//		outToServer.flush();
	}

	@Override
	public void receivedMessage(final String sender, final String message) {
		final MessageType mte = MessageFactory.identifyMessageType(message);
		if (mte.equals(MessageType.COMMAND_MESSAGE)) {
			((WebSocketConnector)connector).extractAndApplyCommand(sender, message);
		} else { 
			final String r = ((WebSocketConnector)connector).isRaw() ? message : MessageFactory.unpackReceiverName(message);
			((WebSocketConnector)connector).storeMessage(sender, r, message);
		}
	}
	@Override
	public void stopService() {
		isOnline = false;
		isAlive = false;

		if (sender != null) {
			sender.close();
		}
		if (serverSocket != null) {
			try {
				serverSocket.stop(1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		this.interrupt();
	}

}
