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
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@doc("k")
@vars({ @var(name = "msg", type = IType.STRING, doc = @doc("the message")),
		@var(name = "port", type = IType.INT, doc = @doc("the port")),
		@var(name = "ip", type = IType.STRING, doc = @doc("the IP")) })
@skill(name = "socket", concept = { IConcept.SKILL, IConcept.NETWORK })
public class SocketSkill extends Skill {

	// private Integer myPort;
	// private String myIP = "127.0.0.1";
	// private String sendMessage;
	// private String recMessage;

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

	// private ServerSocket sersock = null;
	// private Socket sock = null;

	private void openSocket(final IScope scope, final Integer socket) {
		if (scope.getAgentScope().getAttribute("__server" + socket) == null) {
			try {
				final ServerSocket sersock = new ServerSocket(socket);
				scope.getAgentScope().setAttribute("__server" + socket, sersock);

			} catch (Exception e) {
				scope.getGui().errorStatus(e.getMessage());
			}
		}
	}

	private void closeSocket(final IScope scope) {
		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		final ServerSocket sersock = (ServerSocket) scope.getAgentScope().getAttribute("__server" + port);
		if (sersock == null || sersock.isClosed()) {
			try {
				sersock.close();
			} catch (Exception e) {
				scope.getGui().errorStatus(e.getMessage());
			}
		}
	}

	@action(name = "close_socket", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primCloseSocket(final IScope scope) throws GamaRuntimeException {
		closeSocket(scope);
	}

	@action(name = "open_socket", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primOpenSocket(final IScope scope) throws GamaRuntimeException {
		final Integer myPort = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		openSocket(scope, myPort);
	}

	@action(name = "listen_client", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primListenClient(final IScope scope) throws GamaRuntimeException {

		try {
			final Integer myPort = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
			final ServerSocket sersock = (ServerSocket) scope.getAgentScope().getAttribute("__server" + myPort);

			if (scope.getAgentScope().getAttribute("__socket") == null) {
				while (true) {
					final Socket sock = sersock.accept();
					if (sock != null) {
						scope.getAgentScope().setAttribute("__socket", sock);
						break;
					}
				}
			} else {
				final Socket sock = (Socket) scope.getAgentScope().getAttribute("__socket");
				InputStream istream = sock.getInputStream();
				BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
				String receiveMessage;
				if ((receiveMessage = receiveRead.readLine()) != null) {
					scope.getAgentScope().setAttribute("message", receiveMessage);
				}
			}
		} catch (Exception e) {
			scope.getGui().errorStatus(e.getMessage());
		}
	}

	@action(name = "send_to_client", args = {
			@arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)")) }, doc = @doc(examples = {
					@example("tA;") }, value = "M."))
	public void primSendToClient(final IScope scope) throws GamaRuntimeException {
		String msg = scope.getStringArg("msg");

		try {
			final Socket sock = (Socket) scope.getAgentScope().getAttribute("__socket");
			if (sock == null) {
				return;
			}
			OutputStream ostream = sock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(msg);
			pwrite.flush();

		} catch (Exception e) {
			scope.getGui().errorStatus(e.getMessage());
		}
	}

	private Socket cSock = null;

	@action(name = "listen_server", doc = @doc(examples = { @example("d;") }, value = "."))
	public void primListenServer(final IScope scope) throws GamaRuntimeException {

		try {
			InputStream istream = cSock.getInputStream();
			BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
			String receiveMessage;
			if ((receiveMessage = receiveRead.readLine()) != null) {
				scope.getAgentScope().setAttribute("message", receiveMessage);
			}
		} catch (Exception e) {
			scope.getGui().errorStatus(e.getMessage());
		}
	}

	@action(name = "send_to_server", args = {
			@arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)")) }, doc = @doc(examples = {
					@example("tA;") }, value = "M."))
	public void primSendToServer(final IScope scope) throws GamaRuntimeException {
		String msg = scope.getStringArg("msg");
		if (cSock == null) {
			try {
				final String serverIP = Cast.asString(scope, scope.getAgentScope().getAttribute("ip"));

				final Integer serverPort = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));

				cSock = new Socket(serverIP, serverPort);
			} catch (Exception e) {
				scope.getGui().errorStatus(e.getMessage());
			}

		}
		try {
			OutputStream ostream = cSock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(msg); // sending to server
			pwrite.flush(); // flush the data

		} catch (Exception e) {
			scope.getGui().errorStatus(e.getMessage());
		}
	}

}
