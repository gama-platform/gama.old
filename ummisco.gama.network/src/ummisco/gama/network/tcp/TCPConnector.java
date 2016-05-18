package ummisco.gama.network.tcp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import ummisco.gama.network.skills.IConnector;

public class TCPConnector implements IConnector{
	private boolean is_server = false;
	private IScope myScope;
	
	public TCPConnector(final IScope scope, final boolean as_server){
		is_server = as_server;		
		myScope = scope;
	}
	
	public boolean isIs_server() {
		return is_server;
	}

	public void setIs_server(boolean is_server) {
		this.is_server = is_server;
	}

	//	@action(name = "open_socket", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primOpenSocket(final IScope scope) throws GamaRuntimeException {
		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		if (scope.getAgentScope().getAttribute("__server" + port) == null) {
			try {
				final ServerSocket sersock = new ServerSocket(port);
				final MultiThreadedSocketServer ssThread = new MultiThreadedSocketServer(scope.getAgentScope(),
						sersock);
				ssThread.start();
				scope.getAgentScope().setAttribute("__server" + port, ssThread);

			} catch (BindException be) {
				throw GamaRuntimeException.create(be, scope);
			} catch (Exception e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}
	}
	
	@Override
	public String connectToServer(IScope scope, String dest, String server) throws Exception {
		// TODO Auto-generated method stub
		ClientServiceThread c = (ClientServiceThread) scope.getAgentScope().getAttribute("__socket");
		Socket sock = null;
		if (c != null) {
			sock = (Socket) c.getMyClientSocket();
		}
		if (sock == null) {
			try {
				final String serverIP = Cast.asString(scope, scope.getAgentScope().getAttribute("ip"));
				final Integer serverPort = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));

				sock = new Socket(serverIP, serverPort);
				ClientServiceThread cSock = new ClientServiceThread(scope.getAgentScope(), sock);
				cSock.start();
				scope.getAgentScope().setAttribute("__socket", cSock);
				return sock.toString();
			} catch (Exception e) {
				throw GamaRuntimeException.create(e, scope);
			}

		}
		return "";
	}

	@Override
	public void sendMessage(IScope scope, String dest, Map<String, String> data) {
		// TODO Auto-generated method stub
		String msg = scope.getStringArg("msg");
		OutputStream ostream = null;
		ClientServiceThread c=((ClientServiceThread)scope.getAgentScope().getAttribute("__socket"));
		Socket sock =null;
		if(c!=null){			
			sock = (Socket) c.getMyClientSocket();		
		}
		if( sock == null){
			return; 
		}
		try {
			ostream = sock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(msg); // sending to server
			pwrite.flush(); // flush the data

		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		} 
	}

	@Override
	public GamaMap<String, String> fetchMessageBox(IAgent agt) {
		GamaMap<String, String>  res= GamaMapFactory.create();
		String receiveMessage = "";
		ClientServiceThread c=((ClientServiceThread)agt.getAttribute("__socket"));
		Socket sock =null;
		if(c!=null){			
			sock = (Socket) c.getMyClientSocket();		
		}
		if( sock == null){
			return res; 
		}
		try {
			GamaMap<String, IList<String>> m=(GamaMap<String, IList<String>>) agt.getAttribute("messages");
			GamaList<String> msgs = (GamaList<String>) m.get(agt.getScope(), sock.toString());
			receiveMessage = msgs.firstValue(agt.getScope());
			
			msgs.remove(receiveMessage);
			m.put(sock.toString(),msgs);
			agt.setAttribute("messages",m);
		
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, agt.getScope());
		}
//		return receiveMessage;
		res.put("message", receiveMessage);
		return res;
	}

	@Override
	public boolean emptyMessageBox(IAgent agt) {
		// TODO Auto-generated method stub
		return false;
	}

}
