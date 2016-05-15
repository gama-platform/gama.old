package dream.gama.opengis.operators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
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

class ClientServiceThread extends Thread {
	private Socket myClientSocket;

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
		m.put(myClientSocket.toString(),msgs);
		myAgent.setAttribute("messages", m);
		myAgent.setAttribute("__clientCommand" + myClientSocket.toString(), msgs);

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
	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		super.interrupt();

		try {
			myClientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param myClientSocket the myClientSocket to set
	 */
	public void setMyClientSocket(Socket myClientSocket) {
		this.myClientSocket = myClientSocket;
	}

	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
			while (m_bRunThread) {
				// read incoming stream
				String clientCommand = in.readLine();
				// System.out.println("Client Says :" + clientCommand);
				GamaList<String> msgs = (GamaList<String>) myAgent.getAttribute("__clientCommand" + myClientSocket.toString());
				if (msgs != null) {
					msgs.addValue(myAgent.getScope(),clientCommand != null ? clientCommand : "");
					final GamaMap<String, IList<String>> m=(GamaMap<String, IList<String>>) myAgent.getAttribute("messages");
					m.put(myClientSocket.toString(),msgs);
					myAgent.setAttribute("messages", m);
					myAgent.setAttribute("__clientCommand" + myClientSocket.toString(), msgs);
				}

			}
		} catch (Exception e) {
			if (myClientSocket.isClosed() || myClientSocket.isInputShutdown() || myClientSocket.isOutputShutdown()) {
				myAgent.setAttribute("__client"+myClientSocket.toString(),null);
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

}
