/*********************************************************************************************
 *
 * 'UDPConnector.java, in plugin ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.network.udp;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Cast;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.GamaNetworkException;
import ummisco.gama.network.skills.INetworkSkill;

public class UDPConnector extends Connector {

	public static String _UDP_SERVER = "__udp_server";
	public static String _UDP_SOCKET = "__udp_socket";
	public static String _UDP_CLIENT = "__udp_client";

	private boolean is_server = false;
	private final IScope myScope;

	public UDPConnector(final IScope scope, final boolean as_server) {
		is_server = as_server;
		myScope = scope;
	}

	@Override
	public List<ConnectorMessage> fetchMessageBox(final IAgent agent) {
		// TODO Auto-generated method stub
		return super.fetchMessageBox(agent);
	}

	@SuppressWarnings("unchecked")
	public GamaMap<String, Object> fetchMessageBox_old(final IAgent agt) {
		GamaMap<String, Object> m = GamaMapFactory.create();
		// if(is_server){
		final String cli;
		final String receiveMessage = "";
		// System.out.println("\n\n primGetFromClient "+"messages"+agt+"\n\n");

		m = (GamaMap<String, Object>) agt.getAttribute("messages" + agt);
		agt.setAttribute("messages", null);
		// }else{
		// try {
		// byte[] sendData = new byte[1024];
		// byte[] receiveData = new byte[1024];
		// DatagramSocket clientSocket = new DatagramSocket();
		//
		// DatagramPacket receivePacket = new DatagramPacket(receiveData,
		// receiveData.length);
		//
		// clientSocket.receive(receivePacket);
		// String modifiedSentence = new String(receivePacket.getData());
		//
		//
		// GamaList<String> msgs = (GamaList<String>)
		// GamaListFactory.create(String.class);
		//
		// msgs.addValue(agt.getScope(),modifiedSentence != null ?
		// modifiedSentence : "");
		// m.put(""+clientSocket.toString(), msgs);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		return m;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages() {
		for (final IAgent agt : this.receivedMessage.keySet()) {
			// IScope scope = agt.getScope();
			GamaList<ConnectorMessage> m = null;

			m = (GamaList<ConnectorMessage>) agt.getAttribute("messages" + agt);
			// receivedMessage.get(agt).addAll(m);
			for (final ConnectorMessage cm : m) {
				receivedMessage.get(agt).add(cm);
			}
			m.clear();
			agt.setAttribute("message" + agt, m);
			// scope.getAgentScope().setAttribute("messages" +
			// scope.getAgentScope(), null);
		}
		return super.fetchAllMessages();
	}

	public void openServerSocket(final IAgent agent) {
		final Integer port = Cast.asInt(agent.getScope(), this.getConfigurationParameter(SERVER_PORT));

		if (agent.getScope().getSimulation().getAttribute(_UDP_SERVER + port) == null) {
			try {
				final DatagramSocket sersock = new DatagramSocket(port);
				sersock.setSoTimeout(10);
				final MultiThreadedUDPServer ssThread = new MultiThreadedUDPServer(agent, sersock);
				ssThread.start();
				agent.getScope().getSimulation().setAttribute(_UDP_SERVER + port, ssThread);

			} catch (final BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (final Exception e) {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}
	}

	public void connectToServerSocket(final IAgent agent) {

		// InetAddress IPAddress = InetAddress.getByName("localhost");
		//
		// byte[] sendData = new byte[1024];
		// byte[] receiveData = new byte[1024];
		// sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,
		// 9876);
		final Integer port = Cast.asInt(agent.getScope(), this.getConfigurationParameter(SERVER_PORT));

		if (agent.getAttribute(_UDP_CLIENT + port) == null) {

			try {
				final DatagramSocket sersock = new DatagramSocket();
				sersock.setSoTimeout(10);
				final MultiThreadedUDPServer ssThread = new MultiThreadedUDPServer(agent, sersock);
				ssThread.OnServer = false;
				ssThread.start();
				agent.setAttribute(_UDP_CLIENT + port, ssThread);

			} catch (final BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (final Exception e) {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}
	}

	@Override
	protected void connectToServer(final IAgent agent) throws GamaNetworkException {
		if (is_server) {
			openServerSocket(agent);
		} else {
			connectToServerSocket(agent);
		}
	}

	public void sendToClient(final IAgent agent, final String cli, final Object data) throws GamaRuntimeException {
		// int port = (int) agent.getAttribute("port");
		final Integer port = Cast.asInt(agent.getScope(), this.getConfigurationParameter(SERVER_PORT));

		final MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getScope().getSimulation()
				.getAttribute(_UDP_SERVER + port);
		final InetAddress IPAddress = (InetAddress) agent.getAttribute("replyIP");
		final int replyport = Cast.asInt(agent.getScope(), agent.getAttribute("replyPort"));
		if (ssThread == null || IPAddress == null) {
			return;
		}
		final byte[] sendData = ((String) data).getBytes();
		final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, replyport);
		try {
			ssThread.setSendPacket(sendPacket);
			// System.out.println("SENT: "+replyport);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendToServer(final IAgent agent, final Object data) throws GamaRuntimeException {
		final String sport = this.getConfigurationParameter(SERVER_PORT);
		final Integer port = Cast.asInt(agent.getScope(), sport);
		final MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getAttribute(_UDP_CLIENT + port);
		if (ssThread == null) {
			return;
		}
		try {
			final DatagramSocket clientSocket = ssThread.getMyServerSocket();
			final InetAddress IPAddress = InetAddress.getByName((String) agent.getAttribute(INetworkSkill.SERVER_URL));
			final byte[] sendData = ((String) data).getBytes();
			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			sendPacket.setData(sendData);
			clientSocket.send(sendPacket);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void send(final IAgent sender, final String receiver, final GamaMessage content) {
		if (is_server) {
			sendToClient(sender, receiver, content.getContents(myScope));
		} else {
			sendToServer(sender, content.getContents(myScope));
		}
	}

	public void sendMessage(final IAgent agent, final String dest, final Object data) {
		if (is_server) {

		} else {

		}
	}

	@Override
	protected void subscribeToGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unsubscribeGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void releaseConnection(final IScope scope) throws GamaNetworkException {
		final String server = this.getConfigurationParameter(SERVER_URL);
		final String sport = this.getConfigurationParameter(SERVER_PORT);
		final Integer port = Cast.asInt(scope, sport);
		final Thread UDPsersock = (Thread) scope.getSimulation().getAttribute(_UDP_SERVER + port);
		try {
			if (UDPsersock != null) {
				UDPsersock.interrupt();
				scope.getSimulation().setAttribute(_UDP_SERVER + port, null);
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected void sendMessage(final IAgent sender, final String receiver, final String content)
			throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

}
