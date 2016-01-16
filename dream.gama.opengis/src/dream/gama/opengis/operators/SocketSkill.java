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
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@doc("k")
@vars({ @var(name = "msg", type = IType.STRING), @var(name = "port", type = IType.INT, doc = @doc("t)") ),
	@var(name = "ip", type = IType.STRING, doc = @doc("cs") ) })
@skill(name = "socket")
public class SocketSkill extends Skill {

	private Integer myPort;
	private String myIP = "127.0.0.1";
	private String sendMessage;
	private String recMessage;

	@getter("msg")
	public String getMessage(final IAgent agent) {
		return recMessage;
	}

	@setter("msg")
	public void setMesage(final IAgent agent, final String s) {
		recMessage = s;
	}

	@getter("ip")
	public String getIP(final IAgent agent) {
		return myIP;
	}

	@setter("ip")
	public void setIP(final IAgent agent, final String myip) {
		myIP = myip;
	}

	@getter("port")
	// @getter(value = IKeyword.LOCATION, initializer = true)
	public Integer getPort(final IAgent agent) {
		return myPort;
	}

	@setter("port")
	public void setPort(final IAgent agent, final Integer p) {
		myPort = p;
	}

	private ServerSocket sersock = null;
	private Socket sock = null;

	private void openSocket(final IScope scope, final Integer socket) {
		if ( sersock == null || sersock.isClosed() ) {
			try {
				sersock = new ServerSocket(socket);
				sock = sersock.accept();
			} catch (Exception e) {
				scope.getGui().errorStatus(e.getMessage());
			}
		}
	}

	@action(name = "open_socket", doc = @doc(examples = { @example("d;") }, value = ".") )
	public void primOpenSocket(final IScope scope) throws GamaRuntimeException {
		// return null;
		// ServerSocket sersock = null;
		// Socket sock = null;
		openSocket(scope, myPort);
	}

	@action(name = "listen_client", doc = @doc(examples = { @example("d;") }, value = ".") )
	public void primListenClient(final IScope scope) throws GamaRuntimeException {

		try {
			// BufferedReader keyRead = new BufferedReader(new InputStreamReader(
			// java.lang.System.in));
			// sending to client (pwrite object)

			InputStream istream = sock.getInputStream();
			BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
			String receiveMessage;
			if ( (receiveMessage = receiveRead.readLine()) != null ) {
				// scope.getGui().informConsole(receiveMessage);
				recMessage = receiveMessage;
			}
		} catch (Exception e) {
			scope.getGui().errorStatus(e.getMessage());
		}
	}

	@action(name = "send_to_client",
		args = { @arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)") ) },
		doc = @doc(examples = { @example("tA;") }, value = "M.") )
	public void primSendToClient(final IScope scope) throws GamaRuntimeException {
		String msg = scope.getStringArg("msg");

		try {
			OutputStream ostream = sock.getOutputStream();
			PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(msg);
			pwrite.flush();

		} catch (Exception e) {
			scope.getGui().errorStatus(e.getMessage());
		}
	}

	private Socket cSock = null;

	@action(name = "listen_server", doc = @doc(examples = { @example("d;") }, value = ".") )
	public void primListenServer(final IScope scope) throws GamaRuntimeException {

		try {
			// BufferedReader keyRead = new BufferedReader(new InputStreamReader(
			// java.lang.System.in));
			// sending to client (pwrite object)

			InputStream istream = cSock.getInputStream();
			BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
			String receiveMessage;
			if ( (receiveMessage = receiveRead.readLine()) != null ) {
				// scope.getGui().informConsole(receiveMessage); // displaying at DOS prompt
				recMessage = receiveMessage;
			}
		} catch (Exception e) {
			scope.getGui().errorStatus(e.getMessage());
		}
	}

	@action(name = "send_to_server",
		args = { @arg(name = "msg", type = IType.STRING, optional = false, doc = @doc("td)") ) },
		doc = @doc(examples = { @example("tA;") }, value = "M.") )
	public void primSendToServer(final IScope scope) throws GamaRuntimeException {
		String msg = scope.getStringArg("msg");
		if ( cSock == null ) {
			try {
				cSock = new Socket(myIP, myPort);
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
