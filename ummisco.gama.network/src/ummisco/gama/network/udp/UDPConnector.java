/*********************************************************************************************
 *
 * 'UDPConnector.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.network.udp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import ummisco.gama.network.udp.ClientServiceThread;

public class UDPConnector extends Connector {

	public static String _UDP_SERVER = "__udp_server";
	public static String _UDP_SOCKET = "__udp_socket";
	public static String _UDP_CLIENT = "__udp_client";
	public static Integer _UDP_SO_TIMEOUT = 10000;

	public static String DEFAULT_HOST = "localhost";
	public static String DEFAULT_PORT = "1988";
	
	private boolean is_server = false;
	// private final IScope myScope;

	public UDPConnector(final IScope scope, final boolean as_server) {
		is_server = as_server;
		// myScope = scope;
	}

	@Override
	public List<ConnectorMessage> fetchMessageBox(final IAgent agent) {
		// TODO Auto-generated method stub
		return super.fetchMessageBox(agent);
	}

	@SuppressWarnings ("unchecked")
	public GamaMap<String, Object> fetchMessageBox_old(final IAgent agt) {
		GamaMap<String, Object> m = GamaMapFactory.create();
		// if(is_server){
		// final String cli;
		// final String receiveMessage = "";
		// DEBUG.LOG("\n\n primGetFromClient "+"messages"+agt+"\n\n");

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

	@SuppressWarnings ("unchecked")
	@Override
	public Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages() {
		for (final IAgent agt : this.receivedMessage.keySet()) {
			// IScope scope = agt.getScope();
			GamaList<ConnectorMessage> m = null;

			m = (GamaList<ConnectorMessage>) agt.getAttribute("messages" + agt);
			if(m != null) {
				// receivedMessage.get(agt).addAll(m);
				for (final ConnectorMessage cm : m) {
					receivedMessage.get(agt).add(cm);
				}
				m.clear();
				agt.setAttribute("message" + agt, m);
				// scope.getAgentScope().setAttribute("messages" +
				// scope.getAgentScope(), null);
			}
		}
		return super.fetchAllMessages();
	}

	public void openServerSocket(final IAgent agent) {
		final Integer port = Cast.asInt(agent.getScope(), this.getConfigurationParameter(SERVER_PORT));

		if (agent.getScope().getSimulation().getAttribute(_UDP_SERVER + port) == null) {
			try {
				final ServerSocket sersock = new ServerSocket(port);
				sersock.setSoTimeout(_UDP_SO_TIMEOUT);
				final MultiThreadedSocketServer ssThread = new MultiThreadedSocketServer(agent, sersock);
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
		final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_UDP_SOCKET);
		Socket sock = null;
		if (c != null) {
			sock = c.getMyClientSocket();
		}
		if (sock == null) {
			try {

				String server = this.getConfigurationParameter(SERVER_URL);
				String port = this.getConfigurationParameter(SERVER_PORT);
				server = server == null ? DEFAULT_HOST : server;
				port = port == null ? DEFAULT_PORT : port;
				sock = new Socket(server, Cast.asInt(agent.getScope(), port));
				sock.setSoTimeout(_UDP_SO_TIMEOUT);

				final ClientServiceThread cSock = new ClientServiceThread(agent, sock);
				cSock.start();
				agent.setAttribute(_UDP_SOCKET, cSock);
				// return sock.toString();
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

	public void sendToClient(final IAgent agent, final String cli, final String data) throws GamaRuntimeException {
		try {
			final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_UDP_CLIENT + cli);
			Socket sock = null;
			if (c != null) {
				sock = c.getMyClientSocket();
			}
			if (sock == null) { return; }
			final OutputStream ostream = sock.getOutputStream();
			final PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(data);
			pwrite.flush();
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, agent.getScope());
		}
	}

	public void sendToServer(final IAgent agent, final String data) throws GamaRuntimeException {
		OutputStream ostream = null;
		final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_UDP_SOCKET);
		Socket sock = null;
		if (c != null) {
			sock = c.getMyClientSocket();
		}
		if (sock == null || sock.isClosed() || sock.isInputShutdown()) { return; }
		try {
			ostream = sock.getOutputStream();
			final PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(data);
			pwrite.flush();
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, agent.getScope());
		}

	}	
	
//	public void sendToServer(final IAgent agent, final Object data) throws GamaRuntimeException {
//		final String sport = this.getConfigurationParameter(SERVER_PORT);
//		final Integer port = Cast.asInt(agent.getScope(), sport);
//		final MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getAttribute(_UDP_CLIENT + port);
//		if (ssThread == null) { return; }
//		try {
//			DatagramSocket clientSocket = ssThread.getMyServerSocket();
//			final InetAddress IPAddress = InetAddress.getByName((String) agent.getAttribute(INetworkSkill.SERVER_URL));
//			final byte[] sendData = ((String) data).getBytes();
//			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
//			sendPacket.setData(sendData);
//			
//			if(clientSocket.isClosed()) {
//				clientSocket = new DatagramSocket();
//				clientSocket.setSoTimeout(_UDP_SO_TIMEOUT);
//				ssThread.setMyServerSocket(clientSocket);
//				ssThread.setClosed(false);
//			}					
//			
//			clientSocket.send(sendPacket);
//		} catch (final Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	@Override
	protected void sendMessage(final IAgent sender, final String receiver, final String cont)
			throws GamaNetworkException {
		String content = cont.replaceAll("\b\r", "@b@@r@");
		content = content.replaceAll("\n", "@n@");
		if (is_server) {
			sendToClient(sender, receiver, content);
		} else {
			sendToServer(sender, content);
		}
		// if(is_server){
		// primSendToClient(sender, receiver, content);
		// }else{
		// primSendToServer(sender, content);
		// }
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
		// final String server = this.getConfigurationParameter(SERVER_URL);
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

}
