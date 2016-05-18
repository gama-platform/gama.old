package ummisco.gama.network.tcp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import ummisco.gama.network.skills.IConnector;

public class TCPConnector implements IConnector{
	private boolean is_server = false;
	private IScope myScope;
	
	public TCPConnector(final IScope scope, final boolean as_server){
		is_server = as_server;		
		myScope = scope;
		if(is_server){
			primOpenSocket(scope);
		}
			
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
	public void connectToServer(IScope scope, String dest, String server) throws Exception {
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
//				return sock.toString();
			} catch (Exception e) {
				throw GamaRuntimeException.create(e, scope);
			}

		}
//		return "";
	}

	@Override
	public void sendMessage(IScope scope, String dest, Map<String, String> data) {
		if(is_server){
			primSendToClient(scope,dest,data.get("message"));
		}else{
			primSendToServer(scope, data.get("message"));
		}
	}

	@Override
	public GamaMap<String, String> fetchMessageBox(IAgent agt) {
		return null;
	}
	
	public GamaMap<String, String> fetchMessage(IAgent agt, final String cli) {
		GamaMap<String, String> res= GamaMapFactory.create();
		if(is_server){
			res.put("message", primGetFromClient(agt.getScope(), cli));
		}else{
			res.put("message", primGetFromServer(agt.getScope()));
		}
		return res;
	}

	@Override
	public boolean emptyMessageBox(IAgent agt) {
		// TODO Auto-generated method stub
		return false;
	}



	
	public String primConnectSocket(final IScope scope) throws GamaRuntimeException {
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
		return null;
	}

	public Boolean primIs_Closed(final IScope scope) throws GamaRuntimeException {
		String cli = scope.getStringArg("cID");

		final Socket sock = (Socket) ((ClientServiceThread)scope.getAgentScope().getAttribute("__client"+cli)).getMyClientSocket();
		if (sock == null || sock.isClosed() || sock.isInputShutdown() || sock.isOutputShutdown()) {
			return true;
		}
		return false; 
	}


	public String primGetFromClient(final IScope scope,final String cli) throws GamaRuntimeException {
//		String cli = scope.getStringArg("cID");
		String receiveMessage = "";
			
		GamaMap<String, IList<String>> m=(GamaMap<String, IList<String>>) scope.getAgentScope().getAttribute("messages");
		GamaList<String> msgs = (GamaList<String>) m.get(scope, cli);

		receiveMessage = msgs.firstValue(scope);
		
		msgs.remove(receiveMessage);
		m.put(cli,msgs);
		scope.getAgentScope().setAttribute("messages",m);
			
		return receiveMessage; 
	}


	public void primSendToClient(final IScope scope,final String cli, final String msg) throws GamaRuntimeException {
//		String cli = scope.getStringArg("cID");
//		String msg = scope.getStringArg("msg");

		try {
			ClientServiceThread c = ((ClientServiceThread)scope.getAgentScope().getAttribute("__client"+cli));
			Socket sock = null;
			if (c != null) {
				sock = (Socket) c.getMyClientSocket();
			}	
			if(sock == null) {return;}
				OutputStream ostream = sock.getOutputStream();
				PrintWriter pwrite = new PrintWriter(ostream, true);
				pwrite.println(msg);
				pwrite.flush();
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}


	
	public String primGetFromServer(final IScope scope) throws GamaRuntimeException {
		
		String receiveMessage = "";
		ClientServiceThread c=((ClientServiceThread)scope.getAgentScope().getAttribute("__socket"));
		Socket sock =null;
		if(c!=null){			
			sock = (Socket) c.getMyClientSocket();		
		}
		if( sock == null){
			return receiveMessage; 
		}
		try {
			GamaMap<String, IList<String>> m=(GamaMap<String, IList<String>>) scope.getAgentScope().getAttribute("messages");
			GamaList<String> msgs = (GamaList<String>) m.get(scope, sock.toString());
			receiveMessage = msgs.firstValue(scope);
			
			msgs.remove(receiveMessage);
			m.put(sock.toString(),msgs);
			scope.getAgentScope().setAttribute("messages",m);
		
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
		return receiveMessage; 	
	}

	

	
	public void primSendToServer(final IScope scope, final String msg) throws GamaRuntimeException {
//		String msg = scope.getStringArg("msg");
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


	private void closeSocket(final IScope scope) {
		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		final Thread sersock = (Thread) scope.getAgentScope().getAttribute("__server" + port);
		if (sersock != null ){
			sersock.interrupt();
		}
	}

	@Override
	public void connectToServer(IAgent agent, String dest, String server) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(IAgent agent, String dest, Map<String, String> data) {
		// TODO Auto-generated method stub
		
	}


}
