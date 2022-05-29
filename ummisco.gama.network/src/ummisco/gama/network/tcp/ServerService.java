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
import java.io.DataOutputStream;
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
import ummisco.gama.network.common.socket.IListener;
import ummisco.gama.network.common.socket.SocketService;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.network.tcp.TCPConnector.GamaClientService;

/**
 * The Class ServerService.
 */
public abstract class ServerService extends Thread implements SocketService, IListener {

	/** The server socket. */
	private ServerSocket serverSocket;

	/** The my agent. */
	private final IAgent myAgent;
	
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

	private IConnector modelConnector;
	/**
	 * Instantiates a new server service.
	 * @param agent 
	 *
	 * @param port
	 *            the port
	 */
	public ServerService(final IAgent agent, final int port, final IConnector conn) {
		this.port = port;
		this.isAlive = false;
		this.isOnline = false;
		this.modelConnector=conn;
		this.myAgent=agent;
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
//				DEBUG.OUT("before accept wait...");
//				currentSocket = this.serverSocket.accept();
//				String msg = "";
//				do {
//					DEBUG.OUT("wait message ...........");
//					receiver = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
//					msg = receiver.readLine();
//					if (msg != null) {
//						msg = msg.replace("@n@", "\n");
//						msg = msg.replace("@b@@r@", "\b\r");
//						receivedMessage(this.currentSocket.getInetAddress() + ":" + this.port, msg);
//					}
//					DEBUG.OUT("fin traitement message ..." + this.isOnline);
//				} while (isOnline);
				
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
//						clientSocket.setSoTimeout(TCPConnector._TCP_SO_TIMEOUT);
//						clientSocket.setKeepAlive(true);

						final ClientService cliThread =((TCPConnector) modelConnector).new GamaClientService(clientSocket);
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
				DEBUG.LOG("Exception occured in socket "+e.getMessage());
				if (serverSocket.isClosed()) {
					isOnline = false;
				}  
			}
		}
		// DEBUG.OUT("closed ");
		try {
			myAgent.setAttribute(TCPConnector._TCP_SERVER + serverSocket.getLocalPort(), null);
			serverSocket.close();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
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
			if (currentSocket != null) { currentSocket.close(); }
			if (serverSocket != null) { serverSocket.close(); }
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.interrupt();
	}

	@Override
	public void sendMessage(final String msg, final String receiver) throws IOException {
		IList<String> groups = Cast.asList(myAgent.getScope(), myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
		final ClientService cliThread =(ClientService) myAgent.getAttribute(TCPConnector._TCP_CLIENT + receiver);

		String message = msg;
		if (cliThread==null || cliThread.socket == null || !isOnline()) return;
		message = message.replace("\n", "@n@");
		message = message.replace("\b\r", "@b@@r@");
		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(cliThread.socket.getOutputStream())), true);
		sender.println(message );//+"\n" 
		sender.flush();
		
//		DataOutputStream outToServer = new DataOutputStream(currentSocket.getOutputStream());  
//		outToServer.writeUTF(message +"\n");
//		outToServer.flush();
	}
	
	@Override
	public void sendMessage(final String msg) throws IOException {
//		String message = msg;
//		if (currentSocket == null || !isOnline()) return;
//		message = message.replace("\n", "@n@");
//		message = message.replace("\b\r", "@b@@r@");
//		sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(currentSocket.getOutputStream())), true);
//		sender.println(message );//+"\n" 
//		sender.flush();
		
//		DataOutputStream outToServer = new DataOutputStream(currentSocket.getOutputStream());  
//		outToServer.writeUTF(message +"\n");
//		outToServer.flush();
	}

}
