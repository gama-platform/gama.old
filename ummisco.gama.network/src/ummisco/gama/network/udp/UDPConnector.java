package ummisco.gama.network.udp;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Cast;
import ummisco.gama.network.skills.GamaNetworkException;
import ummisco.gama.network.skills.IConnector;
import ummisco.gama.network.tcp.MultiThreadedSocketServer;

public class UDPConnector implements IConnector{

	private boolean is_server = false;
	private IScope myScope;

	public UDPConnector(final IScope scope, final boolean as_server){
		is_server = as_server;			
		myScope = scope;
	}
	
	@Override
	public GamaMap<String, Object> fetchMessageBox(IAgent agt) {
		final String cli;
		String receiveMessage = "";
//		System.out.println("\n\n primGetFromClient "+"messages"+agt+"\n\n");

		GamaMap<String, Object> m=(GamaMap<String, Object>) agt.getAttribute("messages"+agt);
		agt.setAttribute("messages",GamaMapFactory.EMPTY_MAP);

		return m;
	}

	@Override
	public boolean emptyMessageBox(IAgent agt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception {
//		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		if (agent.getScope().getAgentScope().getAttribute("__UDPserver" + port) == null) {
			try {
				final DatagramSocket sersock = new DatagramSocket(port);
				final MultiThreadedUDPServer ssThread = new MultiThreadedUDPServer(agent,
						sersock);
				ssThread.start();
				agent.setAttribute("__UDPserver" + port, ssThread);

			} catch (BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (Exception e) {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}		
	}

	@Override
	public void sendMessage(IAgent agent, String dest, Object data) {
		final Integer port = Cast.asInt(agent.getScope(), agent.getAttribute("port"));

		MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getAttribute("__UDPserver" + port);		
	}

	@Override
	public void close(final IScope scope) throws GamaNetworkException {
		
	
	}

	@Override
	public void registerToGroup(IAgent agt, String groupName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leaveTheGroup(IAgent agt, String groupName) {
		// TODO Auto-generated method stub
		
	}

}
