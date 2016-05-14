package dream.gama.opengis.operators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;

class ClientServiceThread extends Thread {
	Socket myClientSocket;
	boolean m_bRunThread = true;
	boolean ServerOn = true;
	private IAgent myAgent;

	public ClientServiceThread() {
		super();
	}

	ClientServiceThread(final IAgent a, final Socket s) {
		myAgent = a;
		myClientSocket = s;
	}

	public void run() {
		// Obtain the input stream and the output stream for the socket
		// A good practice is to encapsulate them with a BufferedReader
		// and a PrintWriter as shown below.
		BufferedReader in = null;
		// PrintWriter out = null;

		// Print out details of this connection
		// System.out.println("Accepted Client Address - " +
		// myClientSocket.getInetAddress().getHostName());

		try {
			in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
			// out = new PrintWriter(new
			// OutputStreamWriter(myClientSocket.getOutputStream()));

			// At this point, we can read for input and reply with appropriate
			// output.

			// Run in a loop until m_bRunThread is set to false
			while (m_bRunThread) {
				// read incoming stream
				String clientCommand = in.readLine();
				// System.out.println("Client Says :" + clientCommand);

				myAgent.setAttribute("__clientCommand" + myClientSocket.toString(), clientCommand);

//				if (!ServerOn) {
//					// Special command. Quit this thread
//					// System.out.print("Server has already stopped");
//					// out.println("Server has already stopped");
//					// out.flush();
//					m_bRunThread = false;
//
//				}

//				if (clientCommand.equalsIgnoreCase("quit")) {
//					// Special command. Quit this thread
//					m_bRunThread = false;
//					// System.out.print("Stopping client thread for client : ");
//				} else if (clientCommand.equalsIgnoreCase("end")) {
//					// Special command. Quit this thread and Stop the Server
//					m_bRunThread = false;
//					// System.out.print("Stopping client thread for client : ");
//					ServerOn = false;
//				} else {
//					// Process it
//					// out.println("Server Says : " + clientCommand);
//					// out.flush();
//				}
			}
		} catch (Exception e) {
//			if (myClientSocket.isClosed() || myClientSocket.isInputShutdown() || myClientSocket.isOutputShutdown()) {
				myAgent.setAttribute("__client"+myClientSocket.toString(),null);
				GamaList<String> l = (GamaList<String>) Cast.asList(myAgent.getScope(),
						myAgent.getAttribute("clientID"));
				if (l.contains(myClientSocket.toString())) {
					l.remove(myClientSocket.toString());
					myAgent.setAttribute("clientID", l);
				}
				this.interrupt();
//			} else {
//				e.printStackTrace();
//			}
		} 
	}

}
