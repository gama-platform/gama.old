/*********************************************************************************************
 *
 * 'MultiThreadedSocketServer.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.network.udp;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.skills.INetworkSkill;

public class MultiThreadedSocketServer extends Thread {

	static {
		DEBUG.ON();
	}

	private final IAgent myAgent;
	boolean ServerOn = true;
	private ServerSocket myServerSocket;
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

	public MultiThreadedSocketServer(final IAgent a, final ServerSocket ss) {
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
					final GamaList<String> list_net_agents = (GamaList<String>) Cast.asList(myAgent.getScope(),
							myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
					if (list_net_agents != null && !list_net_agents.contains(clientSocket.toString())) {
						list_net_agents.addValue(myAgent.getScope(), clientSocket.toString());
						myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, list_net_agents);
						clientSocket.setSoTimeout(UDPConnector._UDP_SO_TIMEOUT);
						clientSocket.setKeepAlive(true);

						final ClientServiceThread cliThread = new ClientServiceThread(myAgent, clientSocket);
						cliThread.start();

						myAgent.setAttribute(UDPConnector._UDP_CLIENT + clientSocket.toString(), cliThread);
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
			myAgent.setAttribute(UDPConnector._UDP_SERVER + myServerSocket.getLocalPort(), null);
			myServerSocket.close();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}