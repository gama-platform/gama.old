package ummisco.gama.network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.MessageFactory;
import ummisco.gama.network.common.NetworkMessage;

public class MultiThreadedUDPSocketServer extends Thread {

	static {
		DEBUG.ON();
	}
	
	private final IAgent myAgent;
	private volatile boolean closed = false;
	private DatagramSocket myUDPServerSocket;

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

	public MultiThreadedUDPSocketServer(final IAgent a, final DatagramSocket ss) {
		myAgent = a;
		myUDPServerSocket = ss;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// Successfully created Server Socket. Now wait for connections.
		while (!closed) {
			try {
				if (myAgent.dead()) {
					this.interrupt();
				}

				final byte[] receiveData = new byte[1024];
				// Accept incoming connections.
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				myUDPServerSocket.receive(receivePacket);
				final String sentence = new String(receivePacket.getData());

				GamaList<ConnectorMessage> msgs = (GamaList<ConnectorMessage>) myAgent
						.getAttribute("messages" + myAgent);
				if (msgs == null) {
					msgs = (GamaList<ConnectorMessage>) GamaListFactory.create(ConnectorMessage.class);
				}
				if (myAgent.dead()) {
					this.interrupt();
				}

				final NetworkMessage msg = MessageFactory.buildNetworkMessage(myUDPServerSocket.toString(), sentence);
				msgs.addValue(myAgent.getScope(), msg);

				myAgent.setAttribute("messages" + myAgent, msgs);

			} catch (final SocketTimeoutException ste) {
				closed = true;
				// ste.printStackTrace();
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
	}
}	