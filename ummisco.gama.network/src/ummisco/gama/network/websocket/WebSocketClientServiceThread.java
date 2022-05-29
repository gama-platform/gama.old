/*******************************************************************************************************
 *
 * ClientServiceThread.java, in ummisco.gama.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.network.websocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.types.Types;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.MessageFactory;

/**
 * The Class ClientServiceThread.
 */
public class WebSocketClientServiceThread extends Thread {
	
	/** The my client socket. */
	private Socket myClientSocket;
	
	/** The closed. */
	private boolean closed = false;
	
	/** The my agent. */
	private IAgent myAgent;

	/**
	 * Instantiates a new client service thread.
	 */
	public WebSocketClientServiceThread() {
		super();
	}

	/**
	 * Instantiates a new client service thread.
	 *
	 * @param a the a
	 * @param s the s
	 */
	@SuppressWarnings ("unchecked")
	public WebSocketClientServiceThread(final IAgent a, final Socket s) {

		myAgent = a;
		myClientSocket = s;
		final IList<String> msgs = GamaListFactory.create(String.class);
		IMap<String, IList<String>> m = (IMap<String, IList<String>>) myAgent.getAttribute("messages");
		if (m == null) {
			m = GamaMapFactory.create(Types.STRING, Types.LIST);
		}
		m.put("" + myClientSocket.toString(), msgs);
		myAgent.setAttribute("messages", m);

	}

	/**
	 * Gets the my client socket.
	 *
	 * @return the my client socket
	 */
	public Socket getMyClientSocket() {
		return myClientSocket;
	}

	@Override
	public void interrupt() {
		closed = true;
		super.interrupt();
	}

	/**
	 * @param myClientSocket
	 *            the myClientSocket to set
	 */
	public void setMyClientSocket(final Socket myClientSocket) {
		this.myClientSocket = myClientSocket;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void run() {
		BufferedReader in = null;

		while (!closed) {
			try { // read incoming stream
				in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
				String clientCommand = in.readLine();

				// DEBUG.LOG("Client Says :" + clientCommand);

				// GamaMap<String, Object> m = (GamaMap<String, Object>)
				// myAgent.getAttribute("messages" + myAgent);
				IList<ConnectorMessage> msgs = (IList<ConnectorMessage>) myAgent.getAttribute("messages" + myAgent);
				if (msgs == null) {
					msgs = GamaListFactory.create(ConnectorMessage.class);
				}
				if (myAgent.dead()) {
					this.interrupt();
				}
				// DEBUG.LOG("\n\n ClientServiceThread
				// "+"messages"+myAgent+"\n\n");
				// GamaList<String> msgs = (GamaList<String>)
				// m.get(myAgent.getScope(), myClientSocket.toString());
				// if (msgs == null) {
				// msgs = (GamaList<String>) myAgent.getAttribute("messages" +
				// myClientSocket.toString());
				// }
				clientCommand = clientCommand.replaceAll("@n@", "\n");
				clientCommand = clientCommand.replaceAll("@b@@r@", "\b\r");
				final ConnectorMessage msg =
						MessageFactory.unPackNetworkMessage(myClientSocket.toString(),myAgent.toString(), clientCommand);

				// NetworkMessage msg=new
				// NetworkMessage(myClientSocket.toString(), clientCommand);
				msgs.addValue(myAgent.getScope(), msg);

				// final GamaMailbox mailbox = (GamaMailbox)
				// myAgent.getAttribute("messaging_skill_mailbox");
				// mailbox.addMessage(myAgent.getScope(), msg);

				// myAgent.setAttribute("messages" +
				// myClientSocket.toString(),msgs);
				// final GamaMap<String, IList<String>> m=(GamaMap<String,
				// IList<String>>) myAgent.getAttribute("messages");
				// m.put(myClientSocket.toString(),msgs);
				// m.put("" + myClientSocket.toString(), msgs);
				myAgent.setAttribute("messages" + myAgent, msgs);
				// myAgent.setAttribute("__clientCommand" +
				// myClientSocket.toString(), msgs);
			} catch (final SocketTimeoutException ste) {
				// DEBUG.LOG("SocketTimeoutException");
				// try {
				// Thread.sleep(1000);
				// } catch(InterruptedException ie){
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} catch (final java.net.SocketException se) {
				closed = true;
			} catch (final Exception e) {
				if (myClientSocket.isClosed() || myClientSocket.isInputShutdown()
						|| myClientSocket.isOutputShutdown()) {
					// myAgent.setAttribute(TCPConnector._TCP_CLIENT +
					// myClientSocket.toString(), null);
					// GamaList<String> l = (GamaList<String>)
					// Cast.asList(myAgent.getScope(),
					// myAgent.getAttribute("clients"));
					// if (l.contains(myClientSocket.toString())) {
					// l.remove(myClientSocket.toString());
					// myAgent.setAttribute("clients", l);
					// }
					this.interrupt();
				} else {
					e.printStackTrace();
				}
			}

		}
		try {
			myClientSocket.close();
			this.interrupt();

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
