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
import ummisco.gama.network.skills.INetworkSkill;

class ClientServiceThread extends Thread {
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
//		m.put(myClientSocket.toString(),msgs);
		m.put(""+myClientSocket.toString(), msgs);
		myAgent.setAttribute("messages", m);
//		myAgent.setAttribute("__clientCommand" + myClientSocket.toString(), msgs);

	}
	
	/**
	 * @return the myClientSocket
	 */
	public Socket getMyClientSocket() {
		return myClientSocket;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#interrupt()
	 */
	public void interrupt() {
		// TODO Auto-generated method stub		
		closed = true;
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

				GamaMap<String, Object> m = (GamaMap<String, Object>) myAgent.getAttribute("messages" + myAgent);// GamaMap<String,
																													// IList<String>>
				if (m == null) {
					m = GamaMapFactory.create();
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
				GamaList<String> msgs = (GamaList<String>) GamaListFactory.create(String.class);
				// }

				msgs.addValue(myAgent.getScope(), clientCommand != null ? clientCommand : "");
				// myAgent.setAttribute("messages" +
				// myClientSocket.toString(),msgs);
				// final GamaMap<String, IList<String>> m=(GamaMap<String,
				// IList<String>>) myAgent.getAttribute("messages");
				// m.put(myClientSocket.toString(),msgs);
				m.put("" + myClientSocket.toString(), msgs);
				myAgent.setAttribute("messages" + myAgent, m);
				// myAgent.setAttribute("__clientCommand" +
				// myClientSocket.toString(), msgs);

			} catch(SocketTimeoutException ste){
//				System.out.println("closed ");

			}  catch (Exception e) {
				if (myClientSocket.isClosed() || myClientSocket.isInputShutdown()
						|| myClientSocket.isOutputShutdown()) {
					myAgent.setAttribute("__client" + myClientSocket.toString(), null);
					GamaList<String> l = (GamaList<String>) Cast.asList(myAgent.getScope(),
							myAgent.getAttribute("clients"));
					if (l.contains(myClientSocket.toString())) {
						l.remove(myClientSocket.toString());
						myAgent.setAttribute("clients", l);
					}
					this.interrupt();
				} else {
					e.printStackTrace();
				}
			}

		} 	
		try {
			myClientSocket.close();
			Thread.sleep(100);
	        Thread.currentThread().interrupt();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
