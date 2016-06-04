package ummisco.gama.network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Deque;

import msi.gama.extensions.messaging.GamaMailbox;
import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;
import ummisco.gama.network.common.ConnectorMessage;
import ummisco.gama.network.common.NetworkMessage;
import ummisco.gama.network.skills.INetworkSkill;

public class ClientServiceThread extends Thread {
	private Socket myClientSocket;
	private boolean closed = false;
	boolean m_bRunThread = true;
	boolean ServerOn = true;
	private IAgent myAgent;

	public ClientServiceThread() {
		super();
	}

	public ClientServiceThread(final IAgent a, final Socket s) {
		
		myAgent = a;
		myClientSocket = s;
		GamaList<String> msgs =  (GamaList<String>) GamaListFactory.create(String.class);
		GamaMap<String, IList<String>> m=(GamaMap<String, IList<String>>) myAgent.getAttribute("messages");
		if(m == null) {
			m = GamaMapFactory.create(Types.STRING, Types.LIST);
		}
		m.put(""+myClientSocket.toString(), msgs);
		myAgent.setAttribute("messages", m);

	}
	
	public Socket getMyClientSocket() {
		return myClientSocket;
	}

	public void interrupt() {
		closed = true;
		super.interrupt();
	}

	/**
	 * @param myClientSocket the myClientSocket to set
	 */
	public void setMyClientSocket(Socket myClientSocket) {
		this.myClientSocket = myClientSocket;
	}

	public void run() {
		BufferedReader in = null;
		
		while (!closed) {
			try { // read incoming stream
				in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
				String clientCommand = in.readLine();

				// System.out.println("Client Says :" + clientCommand);

//				GamaMap<String, Object> m = (GamaMap<String, Object>) myAgent.getAttribute("messages" + myAgent);
				GamaList<ConnectorMessage> msgs =(GamaList<ConnectorMessage>) myAgent.getAttribute("messages" + myAgent);
				if (msgs == null) {
					msgs =  (GamaList<ConnectorMessage>) GamaListFactory.create(ConnectorMessage.class);
				}
				if (myAgent.dead()) {
					this.interrupt();
				}
				// System.out.println("\n\n ClientServiceThread
				// "+"messages"+myAgent+"\n\n");
				// GamaList<String> msgs = (GamaList<String>)
				// m.get(myAgent.getScope(), myClientSocket.toString());
				// if (msgs == null) {
				// msgs = (GamaList<String>) myAgent.getAttribute("messages" +
				// myClientSocket.toString());
				// }

				NetworkMessage msg=new NetworkMessage(myClientSocket.toString(), clientCommand);
				msgs.addValue(myAgent.getScope(), msg);
				
//				final GamaMailbox mailbox = (GamaMailbox) myAgent.getAttribute("messaging_skill_mailbox");
//				mailbox.addMessage(myAgent.getScope(), msg);
				
				// myAgent.setAttribute("messages" +
				// myClientSocket.toString(),msgs);
				// final GamaMap<String, IList<String>> m=(GamaMap<String,
				// IList<String>>) myAgent.getAttribute("messages");
				// m.put(myClientSocket.toString(),msgs);
//				m.put("" + myClientSocket.toString(), msgs);
				myAgent.setAttribute("messages" + myAgent, msgs);
				// myAgent.setAttribute("__clientCommand" +
				// myClientSocket.toString(), msgs);
			}catch(SocketTimeoutException ste){
//				System.out.println("SocketTimeoutException");
//				try {
//					Thread.sleep(1000);
//				} catch(InterruptedException ie){				
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			} catch(java.net.SocketException se){
				closed = true;
			}catch (Exception e) {
				if (myClientSocket.isClosed() || myClientSocket.isInputShutdown()
						|| myClientSocket.isOutputShutdown()) {
//					myAgent.setAttribute(TCPConnector._TCP_CLIENT + myClientSocket.toString(), null);
//					GamaList<String> l = (GamaList<String>) Cast.asList(myAgent.getScope(),
//							myAgent.getAttribute("clients"));
//					if (l.contains(myClientSocket.toString())) {
//						l.remove(myClientSocket.toString());
//						myAgent.setAttribute("clients", l);
//					}
					this.interrupt();
				} else {
					e.printStackTrace();
				}
			}

		} 	
		try {
			myClientSocket.close();
			this.interrupt();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
