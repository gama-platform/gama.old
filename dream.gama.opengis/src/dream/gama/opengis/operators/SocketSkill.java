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
package dream.gama.opengis.operators;

import java.io.*;
import java.net.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@doc("k")
@vars({ @var(name = "msg", type = IType.STRING, doc = @doc("the message")),
		@var(name = "clientID", type = IType.LIST, doc = @doc("the list of clients")),
		@var(name = "port", type = IType.INT, doc = @doc("the port")),
		@var(name = "ip", type = IType.STRING, doc = @doc("the IP")) })
@skill(name = "socket", concept = { IConcept.SKILL, IConcept.NETWORK })
public class SocketSkill extends Skill {

	// private Integer myPort;
	// private String myIP = "127.0.0.1";
	// private String sendMessage;
	// private String recMessage;
	// private ServerSocket sersock = null;
	// private Socket sock = null;
	// private Socket cSock = null;
	
	@getter("clientID")
	public IList getListClient(final IAgent agent) {
		return Cast.asList(agent.getScope(), agent.getAttribute("clientID"));
	}

	@setter("clientID")
	public void setListClient(final IAgent agent, final IList s) {
		agent.setAttribute("clientID", s);
	}

	@getter("msg")
	public String getMessage(final IAgent agent) {
		return Cast.asString(agent.getScope(), agent.getAttribute("message"));
	}

	@setter("msg")
	public void setMesage(final IAgent agent, final String s) {
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

	private void openSocket(final IScope scope) {
		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		if (scope.getAgentScope().getAttribute("__server" + port) == null) {
			try {
				final ServerSocket sersock = new ServerSocket(port);
				final MultiThreadedSocketServer ssThread = new MultiThreadedSocketServer(scope.getAgentScope(), sersock);
				ssThread.start();
				scope.getAgentScope().setAttribute("__server" + port, sersock);

			}catch(BindException be){
				throw GamaRuntimeException.create(be, scope);
			}
			catch (Exception e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}
	}
	
	private void closeSocket(final IScope scope) {
		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		final ServerSocket sersock = (ServerSocket) scope.getAgentScope().getAttribute("__server" + port);
		if (sersock != null && !sersock.isClosed()) {
			try {
				sersock.close();
				scope.getAgentScope().setAttribute("__server"+ port, null);
			} catch (Exception e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}
	}

	@action(name = "close_socket", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primCloseSocket(final IScope scope) throws GamaRuntimeException {
		closeSocket(scope);
	}


	@action(name = "open_socket", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primOpenSocket(final IScope scope) throws GamaRuntimeException {
//		final Integer myPort = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		openSocket(scope);
	}

//	@action(name = "listen", doc = @doc(examples = { @example("d;") }, value = "."))
//	public String primListen(final IScope scope) throws GamaRuntimeException {
//		final Integer myPort = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
//		final ServerSocket sersock = (ServerSocket) scope.getAgentScope().getAttribute("__server" + myPort);
//		Socket sock = null;
//
//		if(sersock == null){
//			openSocket(scope);
//			
//		}
//		while (true) {
//			try {
//				sock = sersock.accept();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
////			System.out.println(sock.getInetAddress());
////            ClientServiceThread cliThread = new ClientServiceThread(sock);
////            cliThread.start(); 
//
//			if (sock != null && !sock.isClosed()) {
//				scope.getAgentScope().setAttribute("__client"+sock.toString(), sock);
//				break;
//			}
////			if(sock.isClosed()){
////				scope.getAgentScope().setAttribute("__client"+sock.toString(),null);
////				return "";
////			}
//			
//		}
//		return sock.toString(); 
//	}

	@action(name = "get_from_client",args = {
			@arg(name = "clientID", type = IType.STRING, optional = false, doc = @doc("td)")) 
	}, doc = @doc(examples = { @example("d;") }, value = "."))
	public String primGetFromClient(final IScope scope) throws GamaRuntimeException {
		String cli = scope.getStringArg("clientID");

		final Socket sock = (Socket) scope.getAgentScope().getAttribute("__client"+cli);
		String receiveMessage = "";
		if(sock.isClosed() || sock.isInputShutdown()){			
			GamaList<String> l=(GamaList<String>) Cast.asList(scope, scope.getAgentScope().getAttribute("clientID"));
			if(l.contains(sock.toString())){
				l.remove(sock.toString());
				scope.getAgentScope().setAttribute("clientID", l);
			}
			return receiveMessage;
		}
		try {

			receiveMessage = (String) scope.getAgentScope().getAttribute("__clientCommand" + sock.toString());
			scope.getAgentScope().setAttribute("message", receiveMessage);
//			if (scope.getAgentScope().getAttribute("__client") == null) {
	
		
//			} else {
//				final Socket sock = (Socket) scope.getAgentScope().getAttribute("__client");
				
//				InputStream istream = sock.getInputStream();
//				BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
//				if ((receiveMessage = receiveRead.readLine()) != null) {
//					scope.getAgentScope().setAttribute("message", receiveMessage);
//				}
//			}
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
		return receiveMessage; 
	}

	@action(name = "send_to_client", args = {
			@arg(name = "clientID", type = IType.STRING, optional = false, doc = @doc("td)")) ,
			@arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)")) 
			
	
	}, doc = @doc(examples = {
					@example("tA;") }, value = "M."))
	public void primSendToClient(final IScope scope) throws GamaRuntimeException {
		String cli = scope.getStringArg("clientID");
		String msg = scope.getStringArg("msg");

		try {
			final Socket sock = (Socket) scope.getAgentScope().getAttribute("__client"+cli);
			if (sock == null || sock.isClosed() || sock.isOutputShutdown()) {
				return;
			}

			OutputStream ostream = sock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(msg);
			pwrite.flush();

		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}


	@action(name = "listen_server", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primListenServer(final IScope scope) throws GamaRuntimeException {

		try {
			final Socket cSock = (Socket) scope.getAgentScope().getAttribute("__socket");
			if (cSock == null) {
				return;
			}

//			String receiveMessage = (String) scope.getAgentScope().getAttribute("__clientCommand" + cSock.toString());
//			scope.getAgentScope().setAttribute("message", receiveMessage);
			String receiveMessage;
			InputStream istream = cSock.getInputStream();
			BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
			if ((receiveMessage = receiveRead.readLine()) != null) {
				scope.getAgentScope().setAttribute("message", receiveMessage);
			}
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@action(name = "send_to_server", args = {
			@arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)")) }, doc = @doc(examples = {
					@example("tA;") }, value = "M."))
	public void primSendToServer(final IScope scope) throws GamaRuntimeException {
		String msg = scope.getStringArg("msg");
		Socket cSock = (Socket) scope.getAgentScope().getAttribute("__socket");		
		OutputStream ostream = null;
		if (cSock == null) {
			try {
				final String serverIP = Cast.asString(scope, scope.getAgentScope().getAttribute("ip"));
				final Integer serverPort = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));

				cSock = new Socket(serverIP, serverPort);
				scope.getAgentScope().setAttribute("__socket",cSock);		
			} catch (Exception e) {
				throw GamaRuntimeException.create(e, scope);
			}

		}
		try {
			ostream = cSock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(msg); // sending to server
			pwrite.flush(); // flush the data

		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		} finally 
        { 
            // Clean up 
//            try 
//            {                    
//            	if(ostream != null) {ostream.close(); }
//            } 
//            catch(IOException ioe) 
//            { 
//                ioe.printStackTrace(); 
//            } 
        } 
	}

}
