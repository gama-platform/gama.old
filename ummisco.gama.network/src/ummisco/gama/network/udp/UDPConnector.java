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
	public static String _UDP_SOCKET= "__udp_socket";
	public static String _UDP_CLIENT= "__udp_client";
	
	private boolean is_server = false;
	private IScope myScope;

	public UDPConnector(final IScope scope, final boolean as_server) {
		is_server = as_server;
		myScope = scope;
	}

	@Override
	public List<ConnectorMessage> fetchMessageBox(IAgent agent) {
		// TODO Auto-generated method stub
		return super.fetchMessageBox(agent);
	}

	public GamaMap<String, Object> fetchMessageBox_old(IAgent agt) {
		GamaMap<String, Object> m = GamaMapFactory.create();
		// if(is_server){
		final String cli;
		String receiveMessage = "";
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

	@Override
	public Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages() {
		for (IAgent agt : this.receivedMessage.keySet()) {
			// IScope scope = agt.getScope();
			GamaList<ConnectorMessage> m = null;

			m = (GamaList<ConnectorMessage>) agt.getAttribute("messages" + agt);
			// receivedMessage.get(agt).addAll(m);
			for (ConnectorMessage cm : m) {
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

		if (agent.getScope().getSimulationScope().getAttribute(_UDP_SERVER + port) == null) {
			try {
				final DatagramSocket sersock = new DatagramSocket(port);
				sersock.setSoTimeout(10);
				final MultiThreadedUDPServer ssThread = new MultiThreadedUDPServer(agent, sersock);
				ssThread.start();
				agent.getScope().getSimulationScope().setAttribute(_UDP_SERVER + port, ssThread);

			} catch (BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (Exception e) {
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

			} catch (BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (Exception e) {
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
//		int port = (int) agent.getAttribute("port");
		final Integer port = Cast.asInt(agent.getScope(), this.getConfigurationParameter(SERVER_PORT));

		MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getScope().getSimulationScope().getAttribute(_UDP_SERVER + port);
		InetAddress IPAddress = (InetAddress) agent.getAttribute("replyIP");
		int replyport = Cast.asInt(agent.getScope(), agent.getAttribute("replyPort"));
		if (ssThread == null || IPAddress == null) {
			return;
		}
		byte[] sendData = ((String) data).getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, replyport);
		try {
			ssThread.setSendPacket(sendPacket);
			// System.out.println("SENT: "+replyport);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendToServer(final IAgent agent, Object data) throws GamaRuntimeException {
		final String sport = this.getConfigurationParameter(SERVER_PORT);
		final Integer port = Cast.asInt(agent.getScope(), sport);		
		MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getAttribute(_UDP_CLIENT + port);
		if (ssThread == null) {
			return;
		}
		try {
			DatagramSocket clientSocket = ssThread.getMyServerSocket();
			InetAddress IPAddress = InetAddress.getByName((String) agent.getAttribute(INetworkSkill.SERVER_URL));
			byte[] sendData = ((String) data).getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			sendPacket.setData(sendData);
			clientSocket.send(sendPacket);
		} catch (Exception e) {
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

	public void sendMessage(IAgent agent, String dest, Object data) {
		if (is_server) {
	
		} else {
			
		}
	}

	@Override
	protected void subscribeToGroup(IAgent agt, String boxName) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unsubscribeGroup(IAgent agt, String boxName) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void releaseConnection(IScope scope) throws GamaNetworkException {
		String server = this.getConfigurationParameter(SERVER_URL);
		String sport = this.getConfigurationParameter(SERVER_PORT);
		final Integer port = Cast.asInt(scope, sport);
		final Thread UDPsersock = (Thread) scope.getSimulationScope().getAttribute(_UDP_SERVER + port);
		try {
			if (UDPsersock != null) {
				UDPsersock.interrupt();
				scope.getSimulationScope().setAttribute(_UDP_SERVER + port, null);
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected void sendMessage(IAgent sender, String receiver, String content) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

}
