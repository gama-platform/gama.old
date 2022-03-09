package msi.gama.headless.runtime;
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

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import msi.gama.headless.core.GamaHeadlessException;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
interface Endpoint {
	void onOpen(WebSocket socket);
	void onMessage(GamaWebSocketServer server, WebSocket socket, String message);
	// add other event handlers here
}

public class GamaWebSocketServer extends WebSocketServer {
	Map<String, Endpoint> endpoints = Collections.synchronizedMap(new HashMap<>());
	Map<WebSocket, Endpoint> saved_endpoints = Collections.synchronizedMap(new HashMap<>());

	private Application app;

	public GamaWebSocketServer(int port, Application ap) throws UnknownHostException {
		super(new InetSocketAddress(port));
		app = ap;
	}
	public Application getDefaultApp() {
		return app;
	}
	public GamaWebSocketServer(InetSocketAddress address) {
		super(address);
	}

	public GamaWebSocketServer(int port, Draft_6455 draft) {
		super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " to the server!"); // This
																													// method
																													// sends
																													// a
																													// message
																													// to
																													// the
																													// new
																													// client
		broadcast("new connection: " + handshake.getResourceDescriptor()); // This method sends a message to all clients
																			// connected
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
		
		

		String path = URI.create(handshake.getResourceDescriptor()).getPath();
		Endpoint endpoint = endpoints.get(path);
		if(endpoint != null) {
			saved_endpoints.put(conn, endpoint);
			endpoint.onOpen(conn);
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		broadcast(conn + " has left the room!");
		System.out.println(conn + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		saved_endpoints.get(conn).onMessage(this,conn,message);
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		broadcast(message.array());
		System.out.println(conn + ": " + message);
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		int port = 8887; // 843 flash policy port
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception ex) {
		}
		GamaWebSocketServer s = new GamaWebSocketServer(new InetSocketAddress(port));
		s.start();
		System.out.println("ChatServer started on port: " + s.getPort());

		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String in = sysin.readLine();
			s.broadcast(in);
			if (in.equals("exit")) {
				s.stop(1000);
				break;
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(100);
	}

}
