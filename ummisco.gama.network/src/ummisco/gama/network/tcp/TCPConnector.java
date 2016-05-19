package ummisco.gama.network.tcp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
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
import ummisco.gama.network.skills.GamaNetworkException;
import ummisco.gama.network.skills.IConnector;
import ummisco.gama.network.skills.INetworkSkill;

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
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception {

		if(is_server){
			primOpenSocket(agent.getScope());
		} else {
			ClientServiceThread c = (ClientServiceThread) agent.getAttribute("__socket");
			Socket sock = null;
			if (c != null) {
				sock = (Socket) c.getMyClientSocket();
			}
			if (sock == null) {
				try {
//					final String serverIP = Cast.asString(agent.getScope(), agent.getAttribute("ip"));
//					final Integer serverPort = Cast.asInt(agent.getScope(), agent.getAttribute("port"));
					
					sock = new Socket(server, port);
					ClientServiceThread cSock = new ClientServiceThread(agent, sock);
					cSock.start();
					agent.setAttribute("__socket", cSock);
					// return sock.toString();
				} catch (Exception e) {
					throw GamaRuntimeException.create(e, agent.getScope());
				}

			}
		}
//		return "";
	}

	@Override
	public void sendMessage(IAgent agent, String dest, Object data) {
		if(is_server){
			primSendToClient(agent, dest, data);
		}else{
			primSendToServer(agent, data);
		}
	}

	@Override
	public GamaMap<String, Object> fetchMessageBox(IAgent agt) {
		if(is_server){
			return primGetFromClient(agt.getScope());
		}else{
			return primGetFromServer(agt.getScope());
		}
	}

	@Override
	public boolean emptyMessageBox(IAgent agt) {
//		GamaMap<String, Object> m=GamaMapFactory.create();
//		agt.setAttribute("messages",m);

		return true;
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
		String sender = (String) scope.getAgentScope().getAttribute(INetworkSkill.NET_AGENT_NAME);

//		String cli = scope.getStringArg("cID");

		final Socket sock = (Socket) ((ClientServiceThread)scope.getAgentScope().getAttribute("__client"+sender)).getMyClientSocket();
		if (sock == null || sock.isClosed() || sock.isInputShutdown() || sock.isOutputShutdown()) {
			return true;
		}
		return false; 
	}


	public GamaMap<String, Object> primGetFromClient(final IScope scope) throws GamaRuntimeException {
//		String cli = scope.getStringArg("cID");
		final String cli;
		String receiveMessage = "";
//		System.out.println("\n\n primGetFromClient "+"messages"+scope.getAgentScope()+"\n\n");

		GamaMap<String, Object> m=(GamaMap<String, Object>) scope.getAgentScope().getAttribute("messages"+scope.getAgentScope());//(GamaMap<String, IList<String>>)
		scope.getAgentScope().setAttribute("messages"+scope.getAgentScope(),GamaMapFactory.EMPTY_MAP);
//		GamaList<String> msgs = (GamaList<String>) m.get(scope, cli);
//
//		receiveMessage = msgs.firstValue(scope);
//		
//		msgs.remove(receiveMessage);
//		m.put(cli,msgs);
//		scope.getAgentScope().setAttribute("messages",m);
//			
//		return receiveMessage; 
		return m;
	}


	public void primSendToClient(final IAgent agent, final String cli, final Object data) throws GamaRuntimeException {
//		String cli = scope.getStringArg("cID");
//		String msg = scope.getStringArg("msg");
		String msg = "";
//		String cli = "";
		if (data instanceof HashMap){
			msg = ""+((HashMap<String, Object>) data).get(INetworkSkill.CONTENT);
		}else{
			msg = ""+data;
		}
		try {
			ClientServiceThread c = ((ClientServiceThread)agent.getAttribute("__client"+cli));
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
			throw GamaRuntimeException.create(e, agent.getScope());
		}
	}


	
	public  GamaMap<String, Object>  primGetFromServer(final IScope scope) throws GamaRuntimeException {
		GamaMap<String, Object> m = null;//(GamaMap<String, Object>) scope.getAgentScope().getAttribute("messages");//(GamaMap<String, IList<String>>)

		String receiveMessage = "";
		ClientServiceThread c=((ClientServiceThread)scope.getAgentScope().getAttribute("__socket"));
		Socket sock =null;
		if(c!=null){			
			sock = (Socket) c.getMyClientSocket();		
		}
		if( sock == null){
			return GamaMapFactory.EMPTY_MAP; 
		}
		try {
//			System.out.println("\n\n primGetFromServer "+"messages"+scope.getAgentScope()+"\n\n");
			m=(GamaMap<String, Object>) scope.getAgentScope().getAttribute("messages"+scope.getAgentScope());//GamaMap<String, IList<String>> 
		
//			if (msgs != null) {				
//				receiveMessage = msgs.firstValue(scope);
//				msgs.remove(0);
//			}
//			m.put(sock.toString(),msgs);
//			scope.getAgentScope().setAttribute("messages"+scope.getAgentScope(),GamaMapFactory.EMPTY_MAP);

		
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
		return m; 	
	}

	

	
	public void primSendToServer(final IAgent agent, Object data) throws GamaRuntimeException {
//		String msg = scope.getStringArg("msg");
		String msg = "";
		if (data instanceof HashMap){
			msg = ""+((HashMap<String, Object>) data).get(INetworkSkill.CONTENT);
		}else{
			msg = ""+data;
		}
		OutputStream ostream = null;
		ClientServiceThread c=((ClientServiceThread)agent.getAttribute("__socket"));
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
			throw GamaRuntimeException.create(e, agent.getScope());
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
	public void close(final IScope scope) throws GamaNetworkException {
	// TODO Auto-generated method stub
		
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
