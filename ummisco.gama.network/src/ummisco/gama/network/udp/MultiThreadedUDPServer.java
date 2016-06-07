package ummisco.gama.network.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Cast;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.NetworkMessage;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.network.tcp.ClientServiceThread;
import ummisco.gama.network.tcp.TCPConnector;

public class MultiThreadedUDPServer extends Thread {

	private IAgent myAgent;
	public boolean OnServer = true;
	private boolean closed = false;
	private DatagramSocket myUDPServerSocket;
	private DatagramPacket sendPacket = null;

	public DatagramPacket getSendPacket() {
		return sendPacket;
	}

	public void setSendPacket(DatagramPacket sendPacket) {
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
	public void setMyServerSocket(DatagramSocket u) {
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
				// System.out.println("client ");
				// }
				byte[] receiveData = new byte[1024];
//				byte[] sendData = new byte[1024];
				// Accept incoming connections.
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				myUDPServerSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				myAgent.setAttribute("replyIP", IPAddress);
				myAgent.setAttribute("replyPort", port);

				GamaList<String> list_net_agents = (GamaList<String>) Cast.asList(myAgent.getScope(),
						myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
				if (list_net_agents!=null && !list_net_agents.contains(IPAddress.toString()+"_"+port)) {
					list_net_agents.addValue(myAgent.getScope(), IPAddress.toString()+"_"+port);
					myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, list_net_agents);
				}
				// System.out.println("RECEIVED: "+IPAddress+" "
				// +port+"\n"+myUDPServerSocket.getLocalPort());
				GamaList<ConnectorMessage> msgs = (GamaList<ConnectorMessage>) myAgent
						.getAttribute("messages" + myAgent);
				if (msgs == null) {
					msgs = (GamaList<ConnectorMessage>) GamaListFactory.create(ConnectorMessage.class);
				}
				if (myAgent.dead()) {
					this.interrupt();
				}

				NetworkMessage msg = new NetworkMessage(myUDPServerSocket.toString(), sentence != null ? sentence : "");
				msgs.addValue(myAgent.getScope(), msg);

				myAgent.setAttribute("messages" + myAgent, msgs);


			} catch (SocketTimeoutException ste) {
				// System.out.println("closed ");

			} catch (Exception ioe) {

				if (myUDPServerSocket.isClosed()) {
					this.interrupt();
				} else {
					ioe.printStackTrace();
				}
			}

		}

		try {
			myAgent.getScope().getSimulationScope().setAttribute(UDPConnector._UDP_SERVER + myUDPServerSocket.getLocalPort(), null);
			myAgent.getScope().getSimulationScope().setAttribute(UDPConnector._UDP_CLIENT + myUDPServerSocket.getLocalPort(), null);
			myUDPServerSocket.close();
			Thread.sleep(100);

			Thread.currentThread().interrupt();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}