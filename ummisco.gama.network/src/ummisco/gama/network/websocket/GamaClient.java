package ummisco.gama.network.websocket;
/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.network.tcp.ClientService;
import ummisco.gama.network.tcp.TCPConnector;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class GamaClient extends WebSocketClient {
	public GamaClient(URI serverUri, ClientService cs) {
		super(serverUri);
		myService=cs;
	}
	private ClientService myService;
//	private IAgent myAgent;
//	public GamaClient(int port, IAgent agt) throws UnknownHostException {
//		super(new InetSocketAddress(port));
//		myAgent=agt;
//	}

//	public GamaClient(int port, Draft_6455 draft) {
//		super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
//	}
  
	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String message) { 
		System.out.println( ": " + message);
		String msg = message;
		msg = msg.replaceAll("@n@", "\n");
		msg = msg.replaceAll("@b@@r@", "\b\r");
		myService.receivedMessage("", msg);
		
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		// TODO Auto-generated method stub
		
	}

}
