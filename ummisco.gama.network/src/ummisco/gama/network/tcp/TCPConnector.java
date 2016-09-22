package ummisco.gama.network.tcp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.GamaNetworkException;

public class TCPConnector extends Connector {
	private boolean is_server = false;
	private IScope myScope;
	public static String _TCP_SERVER = "__tcp_server";
	public static String _TCP_SOCKET= "__tcp_socket";
	public static String _TCP_CLIENT= "__tcp_client";
	public static Integer _TCP_SO_TIMEOUT= 100;

	public static String DEFAULT_HOST = "localhost";
	public static String DEFAULT_PORT = "1988";

	public TCPConnector(final IScope scope, final boolean as_server) {
		is_server = as_server;
		myScope = scope;
	}

	public void openServerSocket(final IAgent agent) throws GamaRuntimeException {
		final Integer port = Cast.asInt(agent.getScope(), this.getConfigurationParameter(SERVER_PORT));
		if (agent.getScope().getSimulation().getAttribute(_TCP_SERVER + port) == null) {
			try {
				final ServerSocket sersock = new ServerSocket(port);
				sersock.setSoTimeout(_TCP_SO_TIMEOUT);
				final MultiThreadedSocketServer ssThread = new MultiThreadedSocketServer(agent, sersock);
				ssThread.start();
				agent.getScope().getSimulation().setAttribute(_TCP_SERVER + port, ssThread);

			} catch (BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (Exception e) {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}
	}

	public void connectToServerSocket(final IAgent agent) throws GamaRuntimeException {
		ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_TCP_SOCKET);
		Socket sock = null;
		if (c != null) {
			sock = (Socket) c.getMyClientSocket();
		}
		if (sock == null) {
			try {

				String server = this.getConfigurationParameter(SERVER_URL);
				String port = this.getConfigurationParameter(SERVER_PORT);
				server = (server == null ? DEFAULT_HOST : server);
				port = (port == null ? DEFAULT_PORT : port);
				sock = new Socket(server, Cast.asInt(agent.getScope(), port));
				sock.setSoTimeout(_TCP_SO_TIMEOUT);

				ClientServiceThread cSock = new ClientServiceThread(agent, sock);
				cSock.start();
				agent.setAttribute(_TCP_SOCKET, cSock);
				// return sock.toString();
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
	
	public void sendToClient(final IAgent agent, final String cli, final String data) throws GamaRuntimeException {
		try {
			ClientServiceThread c = ((ClientServiceThread) agent.getAttribute(_TCP_CLIENT + cli));
			Socket sock = null;
			if (c != null) {
				sock = (Socket) c.getMyClientSocket();
			}
			if (sock == null) {
				return;
			}
			OutputStream ostream = sock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(data);
			pwrite.flush();
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, agent.getScope());
		}
	}

	public void sendToServer(final IAgent agent, String data) throws GamaRuntimeException {
		OutputStream ostream = null;
		ClientServiceThread c = ((ClientServiceThread) agent.getAttribute(_TCP_SOCKET));
		Socket sock = null;
		if (c != null) {
			sock = (Socket) c.getMyClientSocket();
		}
		if (sock == null || sock.isClosed() || sock.isInputShutdown()) {
			return;
		}
		try {
			ostream = sock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(data);
			pwrite.flush();
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, agent.getScope());
		}

	}

//	@Override
//	public void send(final IAgent sender, final String receiver, final GamaMessage content) {
//		if (is_server) {
//			sendToClient(sender, receiver, content.getContents(myScope));
//		} else {
//			sendToServer(sender, content.getContents(myScope));
//		}
//	}

	@Override
	public List<ConnectorMessage> fetchMessageBox(IAgent agent) {
		// TODO Auto-generated method stub
		return super.fetchMessageBox(agent);
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
		final Thread sersock = (Thread) scope.getSimulation().getAttribute(_TCP_SERVER + port);
		final Thread cSock = (Thread) scope.getAgent().getAttribute(_TCP_SOCKET);

		try {
			if (sersock != null) {
				sersock.interrupt();
				scope.getSimulation().setAttribute(_TCP_SERVER + port, null);
			}
			if (cSock != null) {
				cSock.interrupt();
				scope.getAgent().setAttribute(_TCP_SOCKET, null);
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected void sendMessage(IAgent sender, String receiver, String content) throws GamaNetworkException {
		content = content.replaceAll("\b\r", "@b@@r@");
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
}
