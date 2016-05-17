/*********************************************************************************************
 *
 *
 * 'MovingSkill.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.network.skills;

import java.io.*;
import java.net.*;

import ummisco.gama.network.skills.ClientServiceThread;
import ummisco.gama.network.skills.MultiThreadedSocketServer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@doc("k")
@vars({ 
		@var(name = "messages", type = IType.MAP, doc = @doc("the list of messages")),
		@var(name = "clients", type = IType.LIST, doc = @doc("the list of clients")),
		@var(name = "port", type = IType.INT, doc = @doc("the port")),
		@var(name = "ip", type = IType.STRING, doc = @doc("the IP")) })
@skill(name = "socket", concept = { IConcept.SKILL, IConcept.NETWORK })
public class SocketSkill extends Skill {
	
	@getter("clients")
	public IList getListClient(final IAgent agent) {
		return Cast.asList(agent.getScope(), agent.getAttribute("clients"));
	}

	@setter("clients")
	public void setListClient(final IAgent agent, final IList s) {
		agent.setAttribute("clients", s);
	}

	@getter("messages")
	public GamaMap<String, IList<String>> getMessage(final IAgent agent) {
		return (GamaMap<String, IList<String>>) agent.getAttribute("messages");
	}

	@setter("messages")
	public void setMesage(final IAgent agent, final GamaMap<String, IList<String>> s) {
		agent.setAttribute("message", s);
	}

	@getter("ip")
	public String getIP(final IAgent agent) {
		return Cast.asString(agent.getScope(), agent.getAttribute("ip"));
	}

	@setter("ip")
	public void setIP(final IAgent agent, final String myip) {
		agent.setAttribute("ip", myip);
	}

	@getter("port")
	public Integer getPort(final IAgent agent) {
		return Cast.asInt(agent.getScope(), agent.getAttribute("port"));
	}

	@setter("port")
	public void setPort(final IAgent agent, final Integer p) {
		agent.setAttribute("port", p);
	}


	private void closeSocket(final IScope scope) {
		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		final Thread sersock = (Thread) scope.getAgentScope().getAttribute("__server" + port);
		if (sersock != null ){
			sersock.interrupt();
		}
	}

	@action(name = "close_socket", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primCloseSocket(final IScope scope) throws GamaRuntimeException {
		closeSocket(scope);
	}


	@action(name = "open_socket", doc = @doc(examples = { @example("d;") }, value = "."))
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
	
	@action(name = "connect_server", doc = @doc(examples = { @example("d;") }, value = "."))
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

	@action(name = "is_closed",args = {
			@arg(name = "cID", type = IType.STRING, optional = false, doc = @doc("td)")) 
	}, doc = @doc(examples = { @example("d;") }, value = "."))
	public Boolean primIs_Closed(final IScope scope) throws GamaRuntimeException {
		String cli = scope.getStringArg("cID");

		final Socket sock = (Socket) ((ClientServiceThread)scope.getAgentScope().getAttribute("__client"+cli)).getMyClientSocket();
		if (sock == null || sock.isClosed() || sock.isInputShutdown() || sock.isOutputShutdown()) {
			return true;
		}
		return false; 
	}

	@action(name = "get_from_client",args = {
			@arg(name = "cID", type = IType.STRING, optional = false, doc = @doc("td)")) 
	}, doc = @doc(examples = { @example("d;") }, value = "."))
	public String primGetFromClient(final IScope scope) throws GamaRuntimeException {
		String cli = scope.getStringArg("cID");
		String receiveMessage = "";
			
		GamaMap<String, IList<String>> m=(GamaMap<String, IList<String>>) scope.getAgentScope().getAttribute("messages");
		GamaList<String> msgs = (GamaList<String>) m.get(scope, cli);

		receiveMessage = msgs.firstValue(scope);
		
		msgs.remove(receiveMessage);
		m.put(cli,msgs);
		scope.getAgentScope().setAttribute("messages",m);
			
		return receiveMessage; 
	}

	@action(name = "send_to_client", args = {
			@arg(name = "cID", type = IType.STRING, optional = false, doc = @doc("td)")) ,
			@arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)")) 
			
	
	}, doc = @doc(examples = {
					@example("tA;") }, value = "M."))
	public void primSendToClient(final IScope scope) throws GamaRuntimeException {
		String cli = scope.getStringArg("cID");
		String msg = scope.getStringArg("msg");

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


	@action(name = "listen_server", doc = @doc(examples = { @example("d;") }, value = "."))
	public String primListenServer(final IScope scope) throws GamaRuntimeException {
		
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

	@action(name = "send_to_server", args = {
			@arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)")) }, doc = @doc(examples = {
					@example("tA;") }, value = "M."))
	public void primSendToServer(final IScope scope) throws GamaRuntimeException {
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

}
