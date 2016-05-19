package ummisco.gama.network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;
import ummisco.gama.network.skills.INetworkSkill;

public class MultiThreadedSocketServer extends Thread {

	private IAgent myAgent;
	boolean ServerOn = true;
	private ServerSocket myServerSocket;
	private boolean closed = false;
	/**
	 * @return the myServerSocket
	 */
	public ServerSocket getMyServerSocket() {
		return myServerSocket;
	}

	/**
	 * @param myServerSocket the myServerSocket to set
	 */
	public void setMyServerSocket(ServerSocket myServerSocket) {
		this.myServerSocket = myServerSocket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#interrupt()
	 */

	public void interrupt() {
		closed= true;
//
//		try {
//			myAgent.setAttribute("__server"+ myServerSocket.getLocalPort(), null);
//			myServerSocket.close();				
//			Thread.sleep(100);
//			
//	        Thread.currentThread().interrupt();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}


	public MultiThreadedSocketServer(final IAgent a, final ServerSocket ss) {
		myAgent = a;
		myServerSocket = ss;
	}

	public void run() {
		// Successfully created Server Socket. Now wait for connections.
		while (!closed) {
			try {
				// Accept incoming connections.
				if(myAgent.dead()){ 
					this.interrupt();
				}
				Socket clientSocket = myServerSocket.accept();

				// accept() will block until a client connects to the server.
				// If execution reaches this point, then it means that a client
				// socket has been accepted.

				// For each client, we will start a service thread to
				// service the client requests. This is to demonstrate a
				// Multi-Threaded server. Starting a thread also lets our
				// MultiThreadedSocketServer accept multiple connections
				// simultaneously.

				// Start a Service thread

				if (!clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
					GamaList<String> l = (GamaList<String>) Cast.asList(myAgent.getScope(),
							myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
					if (l!=null && !l.contains(clientSocket.toString())) {
						l.addValue(myAgent.getScope(), clientSocket.toString());
						myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, l);
						
						final ClientServiceThread cliThread = new ClientServiceThread(myAgent, clientSocket);
						cliThread.start();

						myAgent.setAttribute("__client"+clientSocket.toString(), cliThread);
					}
				}

			} catch(SocketTimeoutException ste){
//				System.out.println("closed ");

			} catch (Exception ioe) {

				if (myServerSocket.isClosed()) {
					this.interrupt();
				} else {
					ioe.printStackTrace();
				}
			}
		}
//		System.out.println("closed ");
		try {
			myAgent.setAttribute("__server"+ myServerSocket.getLocalPort(), null);
			myServerSocket.close();				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}