/*******************************************************************************************************
 *
 * GamaServer.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.network.websocket;
/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.tcp.ServerService;

/**
 * The Class GamaServer.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 26 oct. 2023
 */
public class GamaServer extends WebSocketServer {

	/** The my service. */
	private final ServerService myService;

	/**
	 * Instantiates a new gama server.
	 *
	 * @param port
	 *            the port
	 * @param agt
	 *            the agt
	 * @throws UnknownHostException
	 *             the unknown host exception
	 * @date 26 oct. 2023
	 */
	public GamaServer(final int port, final ServerService agt) throws UnknownHostException {
		super(new InetSocketAddress(port));
		myService = agt;
	}

	@Override
	public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		broadcast(conn + " has left the room!");
		DEBUG.OUT(conn + " has left the room!");
	}

	@Override
	public void onMessage(final WebSocket conn, final String message) {
		// broadcast(message);
		DEBUG.OUT(conn + ": " + message);
		String msg = message;
		if (!myService.getConnector().isRaw()) {
			msg = msg.replace("@n@", "\n");
			msg = msg.replace("@b@@r@", "\b\r");
		}
		myService.receivedMessage(conn.toString(), msg);

	}

	@Override
	public void onMessage(final WebSocket conn, final ByteBuffer message) {
		onMessage(conn, StandardCharsets.UTF_8.decode(message).toString());
	}

	@Override
	public void onError(final WebSocket conn, final Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	@Override
	public void onStart() {

		setConnectionLostTimeout(0);
		setConnectionLostTimeout(100);
	}

}
