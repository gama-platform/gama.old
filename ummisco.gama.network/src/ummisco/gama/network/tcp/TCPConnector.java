/*********************************************************************************************
 *
 * 'TCPConnector.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.network.tcp;

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
import msi.gaml.operators.Cast;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.GamaNetworkException;

@SuppressWarnings ({ "unchecked" })
public class TCPConnector extends Connector {
	private boolean is_server = false;
	// private final IScope myScope;
	public static String _TCP_SERVER = "__tcp_server";
	public static String _TCP_SOCKET = "__tcp_socket";
	public static String _TCP_CLIENT = "__tcp_client";
	public static Integer _TCP_SO_TIMEOUT = 100;

	public static String DEFAULT_HOST = "localhost";
	public static String DEFAULT_PORT = "1988";

	public TCPConnector(final IScope scope, final boolean as_server) {
		is_server = as_server;
		// myScope = scope;
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

			} catch (final BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (final Exception e) {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}
		
	}

	public void connectToServerSocket(final IAgent agent) throws GamaRuntimeException {
		final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_TCP_SOCKET);
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
				sock.setSoTimeout(_TCP_SO_TIMEOUT);

				final ClientServiceThread cSock = new ClientServiceThread(agent, sock);
				cSock.start();
				agent.setAttribute(_TCP_SOCKET, cSock);
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
			final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_TCP_CLIENT + cli);
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
		final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_TCP_SOCKET);
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

	// @Override
	// public void send(final IAgent sender, final String receiver, final
	// GamaMessage content) {
	// if (is_server) {
	// sendToClient(sender, receiver, content.getContents(myScope));
	// } else {
	// sendToServer(sender, content.getContents(myScope));
	// }
	// }

	@Override
	public List<ConnectorMessage> fetchMessageBox(final IAgent agent) {
		// TODO Auto-generated method stub
		return super.fetchMessageBox(agent);
	}

	@Override
	public Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages() {
		for (final IAgent agt : this.receivedMessage.keySet()) {
			// IScope scope = agt.getScope();
			GamaList<ConnectorMessage> m = null;

			m = (GamaList<ConnectorMessage>) agt.getAttribute("messages" + agt);
			if (m != null) {
				// receivedMessage.get(agt).addAll(m);
				for (final ConnectorMessage cm : m) {
					receivedMessage.get(agt).add(cm);
				}
				m.clear();
				agt.setAttribute("message" + agt, m);
			}
		}
		return super.fetchAllMessages();
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
	protected boolean isAlive(final IAgent agent) throws GamaNetworkException {
		final String sport = this.getConfigurationParameter(SERVER_PORT);
		final Integer port = Cast.asInt(agent.getScope(), sport);
		final Thread sersock = (Thread) agent.getScope().getSimulation().getAttribute(_TCP_SERVER + port);
		if(sersock != null && sersock.isAlive()) return true;
		
		final Thread cSock = (Thread) agent.getScope().getAgent().getAttribute(_TCP_SOCKET);
		if(sersock != null && cSock.isAlive()) return true;
		
		return false;
	}

	@Override
	protected void releaseConnection(final IScope scope) throws GamaNetworkException {
		// final String server = this.getConfigurationParameter(SERVER_URL);
		final String sport = this.getConfigurationParameter(SERVER_PORT);
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
}
