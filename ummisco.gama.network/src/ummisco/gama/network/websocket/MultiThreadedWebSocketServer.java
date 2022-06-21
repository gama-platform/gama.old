/*******************************************************************************************************
 *
 * MultiThreadedSocketServer.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.websocket;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.skills.INetworkSkill;

/**
 * The Class MultiThreadedSocketServer.
 */
public class MultiThreadedWebSocketServer extends Thread {

	static {
		DEBUG.ON();
	}

	/** The my agent. */
	private final IAgent myAgent;
	
	/** The my server socket. */
	private ServerSocket myServerSocket;
	
	/** The closed. */
	private boolean closed = false;

	/**
	 * @return the myServerSocket
	 */
	public ServerSocket getMyServerSocket() {
		return myServerSocket;
	}

	/**
	 * @param myServerSocket
	 *            the myServerSocket to set
	 */
	public void setMyServerSocket(final ServerSocket myServerSocket) {
		this.myServerSocket = myServerSocket;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		closed = true;
		super.interrupt();
	}

	/**
	 * Instantiates a new multi threaded socket server.
	 *
	 * @param a the a
	 * @param ss the ss
	 */
	public MultiThreadedWebSocketServer(final IAgent a, final ServerSocket ss) {
		myAgent = a;
		myServerSocket = ss;
	}

	@Override
	public void run() {
		// Successfully created Server Socket. Now wait for connections.
		while (!closed) {
			try {
				// Accept incoming connections.
				if (myAgent.dead()) {
					closed = true;
					this.interrupt();
					return;
				}
				// DEBUG.OUT(myServerSocket+" server waiting for connection");

				final Socket clientSocket = myServerSocket.accept();
				DEBUG.OUT(clientSocket + " connected");

				if (!clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
					final IList<String> list_net_agents =
							Cast.asList(myAgent.getScope(), myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
					if (list_net_agents != null && !list_net_agents.contains(clientSocket.toString())) {
						list_net_agents.addValue(myAgent.getScope(), clientSocket.toString());
						myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, list_net_agents);
//						clientSocket.setSoTimeout(TCPConnectorOld._TCP_SO_TIMEOUT);
						clientSocket.setKeepAlive(true);

						final WebSocketClientServiceThread cliThread = new WebSocketClientServiceThread(myAgent, clientSocket);
						cliThread.start();

						myAgent.setAttribute(WebSocketConnector._WEBSOCKET_CLIENT + clientSocket.toString(), cliThread);
					}
				}

			} catch (final SocketTimeoutException ste) {
				// DEBUG.OUT("server waiting time out ");
				// try {
				// Thread.sleep(1000);
				// } catch(InterruptedException ie){
				// }
				// catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} catch (final Exception ioe) {

				if (myServerSocket.isClosed()) {
					closed = true;
				} else {
					ioe.printStackTrace();
				}
			}
		}
		// DEBUG.OUT("closed ");
		try {
			myAgent.setAttribute(WebSocketConnector._WEBSOCKET_SERVER + myServerSocket.getLocalPort(), null);
			myServerSocket.close();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}