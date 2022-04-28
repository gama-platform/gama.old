package ummisco.gama.network.skills;


import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;


@skill (
		name = IRawNetworkSkill.TCP_SKILL_NAME,
		concept = { IConcept.NETWORK, IConcept.COMMUNICATION, IConcept.SKILL },
		internal = true,
		doc = @doc ("The " + IRawNetworkSkill.TCP_SKILL_NAME + " skill provides new features to let agents use raw tcp functions.")
		)
public class TcpSkill extends Skill{

	
	private GamaMap<String, ServerSocket> 	servers;
	private GamaMap<String, Socket> 		sockets;
	
	
	public TcpSkill() {
		super();
		servers 	= (GamaMap<String, ServerSocket>)	GamaMapFactory.create(Types.STRING,Types.NO_TYPE);
		sockets 	= (GamaMap<String, Socket>)			GamaMapFactory.create(Types.STRING,Types.NO_TYPE);
	}
	
	
	@action(
			name = IRawNetworkSkill.WAIT_FOR_CONNECTION,
			args = {
					@arg(
							name	= IRawNetworkSkill.SERVER_NAME,
							optional= false,
							type 	= IType.STRING,
							doc		= @doc("Identifier of the server that should be waiting for the connection"))
				},
			doc	= @doc(
					value 	= "Function that blocks the execution until a client connects to the server",
					returns	= "The identifier of the socket for communicating afterward")
			)
	public String waitForconnection(final IScope scope) {
		
		String ret 		= null;
		String server 	= (String) scope.getArg(IRawNetworkSkill.SERVER_NAME, IType.STRING);
		if (server != null && servers.containsKey(server)) {
			var servSocket = servers.get(server);
			try {
				
				var socket = servSocket.accept();
				var sock_desc = socket.toString();
				sockets.put(sock_desc, socket);
				ret = sock_desc;
				
			} catch (IOException e) {
				var error_mess = e.getMessage();
				scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(),  new GamaColor(GamaColor.red));
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	@action(
			name = IRawNetworkSkill.CREATE_SERVER,
			args = {
				@arg(
						name	= IRawNetworkSkill.PORT,
						optional= true,
						type	= IType.INT,
						doc		= @doc("The server's port. Will be assigned automatically to the next available if nil")),
				},
			doc	= @doc(
					value	= "Creates a tcp server on given port",
					returns	= "The server's identifier")
			)
	public String createServer(final IScope scope) {
		
		final Integer 	port 		= (Integer) scope.getArg(IRawNetworkSkill.PORT, IType.INT);
		
		ServerSocket 	sock = null;
		String			desc = null;
		
		try {
			sock = new ServerSocket(port);
		} catch (IOException e) {

			var error_mess = e.getMessage();
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			e.printStackTrace();
		}
		
		if (sock != null) {
			desc = sock.toString();
			servers.put(desc, sock);			
		}
		
		return desc;
	}
	
	@action(
			name = IRawNetworkSkill.CONNECT_TO_SERVER,
			args = {
					@arg(
							name	= IRawNetworkSkill.SERVER_URL,
							optional= false,
							type	= IType.STRING,
							doc		= @doc("The url to connect to")),
					@arg(
							name	= IRawNetworkSkill.PORT,
							optional= false,
							type	= IType.INT,
							doc		= @doc("The connection port"))
				},
			doc	= @doc(
					value	= "Creates a connection to a given server",
					returns = "The identifier of the connection's socket, nil if there was an error")
			
			)
	public String connectToServer(final IScope scope) {
		
		final String 	server_url 	= (String)	scope.getArg(IRawNetworkSkill.SERVER_URL, 	IType.STRING);
		final Integer 	port 		= (Integer) scope.getArg(IRawNetworkSkill.PORT, 		IType.INT);

		try {
			
			Socket s	= new Socket(server_url,port);
			sockets.put(s.toString(), s);
			return s.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
			var error_mess = "Unable to connect to server with url '" + server_url +"' and port '" + port + "' : " + e.toString();
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			DEBUG.OUT(error_mess);
			return null;
		}  

		
	}
	
	
	@action(
				name	= IRawNetworkSkill.SEND,
				args	= {
							@arg(	name	= IRawNetworkSkill.TO,
									optional= false,
									type	= IType.STRING,
									doc		= @doc("Identifier of the socket used to send the message")),
							@arg(	name	= IRawNetworkSkill.CONTENT,
									optional= false,
									type	= IType.STRING,
									doc		= @doc("Message to send")),
						},
				doc		= @doc(
							value 	= "Sends a message via tcp through the given socket",
							returns	= "True if everything went well, false otherwise"))
	public boolean send(IScope scope) {

		final String to_desc = (String)	scope.getArg(IRawNetworkSkill.TO, 		IType.STRING);
		final String content = (String)	scope.getArg(IRawNetworkSkill.CONTENT, 	IType.STRING);
		
		if (to_desc == null || ! sockets.containsKey(scope, to_desc)) {
			var error_mess = "unknown client value for the 'to' facet : '" + to_desc + "'";
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			DEBUG.OUT(error_mess);			
			return false;
		}
		if (content == null) {
			var error_mess = "content cannot be nil";
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			DEBUG.OUT(error_mess);			
			return false;
		}
		
		byte[] content_bytes = content.getBytes();	
		
		var sock_to = sockets.get(to_desc);
		try {

			sock_to.getOutputStream().write(content_bytes);

		} catch (IOException e) {
			var error_mess = e.getMessage();
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			return false;
		}
		
		return true;
	}
	
	@action(
				name = IRawNetworkSkill.READ_LINE,
				args = {
						@arg(
								name 	= IRawNetworkSkill.FROM,
								optional= false,
								type	= IType.STRING,
								doc		= @doc("Identifier of the socket used to read")
								)
						},
				doc	= @doc(
						value 	= "Reads a line from the given socket",
						returns	= "The line read"
						)
			)
	public String readLine(IScope scope) {

		final String from_desc = (String)	scope.getArg(IRawNetworkSkill.FROM, 		IType.STRING);

		if (from_desc == null || ! sockets.containsKey(scope, from_desc)) {
			var error_mess = "unknown connection value for the 'from' facet : '" + from_desc + "'";
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);			
			return null;
		}
		
		DEBUG.OUT("reading one line from " + from_desc);

		return readUntil(scope, sockets.get(from_desc), "\n");
			
	}
	
	
	@action(
			name = IRawNetworkSkill.READ_ALL,
			args = {
					@arg(
							name 	= IRawNetworkSkill.FROM,
							optional= false,
							type	= IType.STRING,
							doc		= @doc("Identifier of the socket used to read")
							)
					},
			doc	= @doc(
					value 	= "Reads all bytes from the given socket",
					returns	= "The data read, converted into a string"
					)
			)
	public String readAll(IScope scope) {

		
		final String from_desc = (String)	scope.getArg(IRawNetworkSkill.FROM, 		IType.STRING);

		if (from_desc == null || ! sockets.containsKey(scope, from_desc)) {
			var error_mess = "unknown connection value for the 'from' facet : '" + from_desc + "'";
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(),  new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);			
			return null;
		}
		
		DEBUG.OUT("reading all bytes from " + from_desc);
		
		try {
			
			Socket from_sock	= sockets.get(from_desc);
			String line 		= from_sock.getInputStream().readAllBytes().toString();
			
			DEBUG.OUT("line read from '" + from_desc + "' : " + line);
			return line;
			
		} catch (IOException e) {
			e.printStackTrace();
			var error_mess = "impossible to read line from '" + from_desc + "' : " + e.toString();
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(),  new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);
			return null;
		}

	}
	
	
	@action(
			name = IRawNetworkSkill.READ_BYTES,
			args = {
					@arg(
							name 	= IRawNetworkSkill.FROM,
							optional= false,
							type	= IType.STRING,
							doc		= @doc("Identifier of the socket used to read")
							),
					@arg(
							name 	= IRawNetworkSkill.LENGTH,
							optional= false,
							type	= IType.INT,
							doc		= @doc("How many bytes should be read")
							),
					},
			doc	= @doc(
					value 	= "Reads a given number of bytes from the given socket",
					returns	= "The data read, converted into a string"
					)
			)
	public String readBytes(IScope scope) {

		
		final String 	from_desc 	= (String)	scope.getArg(IRawNetworkSkill.FROM, 		IType.STRING);
		final int		length		= (Integer)	scope.getArg(IRawNetworkSkill.LENGTH, 		IType.INT);
		
		DEBUG.OUT("reading " + length + " bytes from " + from_desc);
		
		if (from_desc == null || ! sockets.containsKey(scope, from_desc)) {
			var error_mess = "unknown connection value for the 'from' facet : '" + from_desc + "'";
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(),  new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);			
			return null;
		}
		
		try {
			
			Socket from_sock	= sockets.get(from_desc);
			String line 		= new String(from_sock.getInputStream().readNBytes(length));
			
			DEBUG.OUT("line read from '" + from_desc + "' : " + line);
			return line;
			
		} catch (IOException e) {
			e.printStackTrace();
			var error_mess = "impossible to read line from '" + from_desc + "' : " + e.toString();
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);
			return null;
		}

	}
	

	@action(
			name = IRawNetworkSkill.READ_UNTIL,
			args = {
					@arg(
							name 	= IRawNetworkSkill.FROM,
							optional= false,
							type	= IType.STRING,
							doc		= @doc("Identifier of the socket used to read")
							),
					@arg(
							name 	= IRawNetworkSkill.END_STRING,
							optional= false,
							type	= IType.STRING,
							doc		= @doc("Characters used as a 'stop reading' command")
							),
				},
			doc	= @doc(
					value 	= "Reads from the given socket until the given string is met",
					returns	= "The data read, converted into a string"
					)
			)
	public String readUntil(IScope scope) {

		
		final String 	from_desc 	= (String)	scope.getArg(IRawNetworkSkill.FROM, 		IType.STRING);
		final String	end_string	= (String)	scope.getArg(IRawNetworkSkill.END_STRING, 		IType.STRING);
		
		DEBUG.OUT("reading bytes from " + from_desc + " until the string '" + end_string + "' is met");
		
		if (from_desc == null || ! sockets.containsKey(scope, from_desc)) {
			var error_mess = "unknown connection value for the 'from' facet : '" + from_desc + "'";
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);			
			return null;
		}
		
		if (end_string == null || end_string.length() == 0) {
			var error_mess = IRawNetworkSkill.END_STRING + " must be at least one caracter long";
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(), new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);
			return null;
		}
		
		Socket from_sock	= sockets.get(from_desc);
		return readUntil(scope, from_sock, end_string);
			

	}
	
	
	private String readUntil(IScope scope, Socket from, String endString) {

		try {
	
			var inStream		= from.getInputStream();
			var tmp_read 		= inStream.readNBytes(endString.length());
			String current_buff = new String(tmp_read);
			String read			= current_buff;
			while ( ! current_buff.equals(endString)) {
				int current 	= inStream.read();
				if (current == -1) { //The stream is closed
					break;
				}
				
				char current_c	= (char)current;
				read 			+= current_c;
				current_buff 	= current_buff.substring(1) + current_c;
			}
			read = read.substring(0, read.length()-endString.length());
			DEBUG.OUT("string read from '" + from.toString() + "' : " + read);
			return read;

		} catch (IOException e) {
			e.printStackTrace();
			var error_mess = "impossible to read line from '" + from.toString() + "' : " + e.toString();
			scope.getGui().getConsole().informConsole(error_mess, scope.getRoot(),  new GamaColor(GamaColor.red));
			DEBUG.ERR(error_mess);
			return null;
		}
		
	}
	
	
}
