/*******************************************************************************************************
 *
 * ServerService.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.common.MessageFactory;
import ummisco.gama.network.common.MessageFactory.MessageType;
import ummisco.gama.network.common.socket.AbstractProtocol;
import ummisco.gama.network.common.socket.IListener;
import ummisco.gama.network.common.socket.SocketService;
import ummisco.gama.network.skills.INetworkSkill;

/**
 * The Class ServerService.
 */
public class ServerService extends Thread implements SocketService, IListener {

	/** The server socket. */
	protected ServerSocket serverSocket;

	/** The my agent. */
	protected final IAgent myAgent;

	/** The port. */
	protected final int port;

	/** The is alive. */
	protected boolean isAlive;

	/** The is online. */
	protected boolean isOnline;

	/** The sender. */
	protected PrintWriter sender;

	/** The receiver. */
	BufferedReader receiver = null;

	/** The connector. */
	protected IConnector connector;

	/**
	 * Instantiates a new server service.
	 *
	 * @param agent
	 *
	 * @param port
	 *            the port
	 */
	public ServerService(final IAgent agent, final int port, final IConnector conn) {
		this.port = port;
		this.isAlive = false;
		this.isOnline = false;
		this.connector = conn;
		this.myAgent = agent;
	}

	@Override
	public String getRemoteAddress() {
		if (serverSocket == null) return null;
		return this.serverSocket.getInetAddress() + ":" + this.port;
	}

	@Override
	public String getLocalAddress() {
		if (serverSocket == null) return null;
		return this.serverSocket.getLocalSocketAddress() + ":" + this.port;
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
				// DEBUG.OUT("before accept wait...");
				// currentSocket = this.serverSocket.accept();
				// String msg = "";
				// do {
				// DEBUG.OUT("wait message ...........");
				// receiver = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
				// msg = receiver.readLine();
				// if (msg != null) {
				// msg = msg.replace("@n@", "\n");
				// msg = msg.replace("@b@@r@", "\b\r");
				// receivedMessage(this.currentSocket.getInetAddress() + ":" + this.port, msg);
				// }
				// DEBUG.OUT("fin traitement message ..." + this.isOnline);
				// } while (isOnline);

				// Accept incoming connections.
				if (myAgent.dead()) {
					isOnline = false;
					this.interrupt();
					return;
				}
				// DEBUG.OUT(myServerSocket+" server waiting for connection");

				final Socket clientSocket = this.serverSocket.accept();
				DEBUG.OUT(clientSocket + " connected");

				if (!clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
					final IList<String> list_net_agents =
							Cast.asList(myAgent.getScope(), myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
					if (list_net_agents != null && !list_net_agents.contains(clientSocket.toString())) {
						list_net_agents.addValue(myAgent.getScope(), clientSocket.toString());
						myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, list_net_agents);
						// clientSocket.setSoTimeout(TCPConnector._TCP_SO_TIMEOUT);
						// clientSocket.setKeepAlive(true);

						final ClientService cliThread = new ClientService(clientSocket, connector);
						cliThread.startService();

						myAgent.setAttribute(TCPConnector._TCP_CLIENT + clientSocket.toString(), cliThread);
					}
				}

			} catch (final SocketTimeoutException e) {
				DEBUG.LOG("Socket timeout");
			} catch (final SocketException e) {
				DEBUG.LOG("Socket closed");
			} catch (final IOException e1) {
				DEBUG.LOG("Socket error" + e1);
				/// isOnline = false;
			} catch (final Exception e) {
				DEBUG.LOG("Exception occured in socket " + e.getMessage());
				if (serverSocket.isClosed()) { isOnline = false; }
			}
		}
		// DEBUG.OUT("closed ");
		try {
			myAgent.setAttribute(TCPConnector._TCP_SERVER + serverSocket.getLocalPort(), null);
			serverSocket.close();
		} catch (final Exception e) {
			
			e.printStackTrace();
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
			if (serverSocket != null) { serverSocket.close(); }
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.interrupt();
	}

	@Override
	public void sendMessage(final String msg, final String receiver) throws IOException {
		// IList<String> groups = Cast.asList(myAgent.getScope(), myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
		final ClientService cliThread = (ClientService) myAgent.getAttribute(TCPConnector._TCP_CLIENT + receiver);

		String message = msg;
		if (cliThread == null || cliThread.socket == null || !isOnline()) return;
	
		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(cliThread.socket.getOutputStream())), true);

		//If raw connection we do not append an end of line nor escape anything
		if (connector.isRaw()) {
			sender.print(message);
			sender.flush();
		}
		else {

			message = message.replace("\n", "@n@");
			message = message.replace("\b\r", "@b@@r@");
			//TODO: do we really need to append a '\n' while we already use println and the printwriter is in auto flush ?
			sender.println(message);// +"\n"
			sender.flush();
		}

		// DataOutputStream outToServer = new DataOutputStream(currentSocket.getOutputStream());
		// outToServer.writeUTF(message +"\n");
		// outToServer.flush();
	}

	@Override
	public void sendMessage(final String msg) throws IOException {
		// String message = msg;
		// if (currentSocket == null || !isOnline()) return;
		// message = message.replace("\n", "@n@");
		// message = message.replace("\b\r", "@b@@r@");
		// sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(currentSocket.getOutputStream())), true);
		// sender.println(message );//+"\n"
		// sender.flush();

		// DataOutputStream outToServer = new DataOutputStream(currentSocket.getOutputStream());
		// outToServer.writeUTF(message +"\n");
		// outToServer.flush();
	}

	@Override
	public void receivedMessage(final String sender, final String message) {
		final MessageType mte = MessageFactory.identifyMessageType(message);
		if (MessageType.COMMAND_MESSAGE.equals(mte)) {
			((TCPConnector) connector).extractAndApplyCommand(sender, message);
		} else {
			final String r = ((TCPConnector) connector).isRaw() ? message : MessageFactory.unpackReceiverName(message);
			((TCPConnector) connector).storeMessage(sender, r, message);
		}
	}

	@Override
	public void onOpen(final AbstractProtocol conn) {
		

	}

	@Override
	public void onClose(final AbstractProtocol conn, final int code, final String reason, final boolean remote) {
		

	}

	@Override
	public void onMessage(final AbstractProtocol conn, final String message) {
		

	}

	@Override
	public void onMessage(final AbstractProtocol conn, final ByteBuffer message) {
		

	}

	@Override
	public void onError(final AbstractProtocol conn, final Exception ex) {
		

	}

	@Override
	public void onStart() {
		

	}
}
