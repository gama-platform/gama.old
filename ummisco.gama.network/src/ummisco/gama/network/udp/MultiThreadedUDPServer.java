/*********************************************************************************************
 *
 * 'MultiThreadedUDPServer.java, in plugin ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gaml.operators.Cast;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.NetworkMessage;
import ummisco.gama.network.skills.INetworkSkill;

public class MultiThreadedUDPServer extends Thread {

	private final IAgent myAgent;
	public boolean OnServer = true;
	private volatile boolean closed = false;
	private DatagramSocket myUDPServerSocket;
	private DatagramPacket sendPacket = null;

	public DatagramPacket getSendPacket() {
		return sendPacket;
	}

	public void setSendPacket(final DatagramPacket sendPacket) {
		this.sendPacket = sendPacket;
	}

	/**
	 * @return the myServerSocket
	 */
	public DatagramSocket getMyServerSocket() {
		return myUDPServerSocket;
	}

	/**
	 * @param myServerSocket
	 *            the myServerSocket to set
	 */
	public void setMyServerSocket(final DatagramSocket u) {
		this.myUDPServerSocket = u;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#interrupt()
	 */
	// public void interrupt() {
	// closed =true;
	// }

	public MultiThreadedUDPServer(final IAgent a, final DatagramSocket ss) {
		myAgent = a;
		myUDPServerSocket = ss;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// Successfully created Server Socket. Now wait for connections.
		while (!closed) {
			try {
				if (sendPacket != null) {
					myUDPServerSocket.send(sendPacket);
					sendPacket = null;
				}
				if (myAgent.dead()) {
					this.interrupt();
				}
				// if(!OnServer){
				// DEBUG.LOG("client ");
				// }
				final byte[] receiveData = new byte[1024];
				// byte[] sendData = new byte[1024];
				// Accept incoming connections.
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				myUDPServerSocket.receive(receivePacket);
				final String sentence = new String(receivePacket.getData());
				final InetAddress IPAddress = receivePacket.getAddress();
				final int port = receivePacket.getPort();
				myAgent.setAttribute("replyIP", IPAddress);
				myAgent.setAttribute("replyPort", port);

				final GamaList<String> list_net_agents = (GamaList<String>) Cast.asList(myAgent.getScope(),
						myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
				if (list_net_agents != null && !list_net_agents.contains(IPAddress.toString() + "_" + port)) {
					list_net_agents.addValue(myAgent.getScope(), IPAddress.toString() + "_" + port);
					myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, list_net_agents);
				}
				// DEBUG.LOG("RECEIVED: "+IPAddress+" "
				// +port+"\n"+myUDPServerSocket.getLocalPort());
				GamaList<ConnectorMessage> msgs = (GamaList<ConnectorMessage>) myAgent
						.getAttribute("messages" + myAgent);
				if (msgs == null) {
					msgs = (GamaList<ConnectorMessage>) GamaListFactory.create(ConnectorMessage.class);
				}
				if (myAgent.dead()) {
					this.interrupt();
				}

				final NetworkMessage msg = new NetworkMessage(myUDPServerSocket.toString(), sentence);
				msgs.addValue(myAgent.getScope(), msg);

				myAgent.setAttribute("messages" + myAgent, msgs);

			} catch (final SocketTimeoutException ste) {
				closed = true;
				// DEBUG.LOG("closed ");

			} catch (final Exception ioe) {

				if (myUDPServerSocket.isClosed()) {
					closed = true;
					this.interrupt();
				} else {
					ioe.printStackTrace();
				}
			}

		}

		try {
			myAgent.getScope().getSimulation().setAttribute(UDPConnector._UDP_SERVER + myUDPServerSocket.getLocalPort(),
					null);
			myAgent.getScope().getSimulation().setAttribute(UDPConnector._UDP_CLIENT + myUDPServerSocket.getLocalPort(),
					null);
			myUDPServerSocket.close();
			Thread.sleep(100);

			Thread.currentThread().interrupt();

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}