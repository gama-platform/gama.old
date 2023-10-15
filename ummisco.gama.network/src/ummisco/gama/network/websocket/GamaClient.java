/*******************************************************************************************************
 *
 * GamaClient.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation platform
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

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import ummisco.gama.network.tcp.ClientService;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class GamaClient extends WebSocketClient {

	/**
	 * Instantiates a new gama client.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param serverUri
	 *            the server uri
	 * @param cs
	 *            the cs
	 * @date 15 oct. 2023
	 */
	public GamaClient(final URI serverUri, final ClientService cs) {
		super(serverUri);
		myService = cs;
	}

	/** The my service. */
	private final ClientService myService;
	// private IAgent myAgent;
	// public GamaClient(int port, IAgent agt) throws UnknownHostException {
	// super(new InetSocketAddress(port));
	// myAgent=agt;
	// }

	// public GamaClient(int port, Draft_6455 draft) {
	// super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
	// }

	/**
	 * On close.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param arg0
	 *            the arg 0
	 * @param arg1
	 *            the arg 1
	 * @param arg2
	 *            the arg 2
	 * @date 15 oct. 2023
	 */
	@Override
	public void onClose(final int arg0, final String arg1, final boolean arg2) {

	}

	/**
	 * On error.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param arg0
	 *            the arg 0
	 * @date 15 oct. 2023
	 */
	@Override
	public void onError(final Exception arg0) {

	}

	/**
	 * On message.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param message
	 *            the message
	 * @date 15 oct. 2023
	 */
	@Override
	public void onMessage(final String message) {
		System.out.println(": " + message);
		String msg = message;
		msg = msg.replace("@n@", "\n");
		msg = msg.replace("@b@@r@", "\b\r");
		myService.receivedMessage("", msg);

	}

	/**
	 * On open.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param arg0
	 *            the arg 0
	 * @date 15 oct. 2023
	 */
	@Override
	public void onOpen(final ServerHandshake arg0) {

	}

}
