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
	@Override
	public void interrupt() {
		closed= true;
		super.interrupt();
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
					closed=true;
					this.interrupt();
					return;
				}
//				System.out.println(myServerSocket+" server waiting for connection");

				Socket clientSocket = myServerSocket.accept();
				System.out.println(clientSocket+" connected");

				if (!clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
					GamaList<String> list_net_agents = (GamaList<String>) Cast.asList(myAgent.getScope(),
							myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
					if (list_net_agents!=null && !list_net_agents.contains(clientSocket.toString())) {
						list_net_agents.addValue(myAgent.getScope(), clientSocket.toString());
						myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, list_net_agents);
						clientSocket.setSoTimeout(10);
						clientSocket.setKeepAlive(true);				

						final ClientServiceThread cliThread = new ClientServiceThread(myAgent, clientSocket);
						cliThread.start();

						myAgent.setAttribute(TCPConnector._TCP_CLIENT+clientSocket.toString(), cliThread);
					}
				}

			} catch(SocketTimeoutException ste){
//				System.out.println("server waiting time out ");
//				try {
//					Thread.sleep(1000);
//				} catch(InterruptedException ie){
//				}
//				catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			} catch (Exception ioe) {

				if (myServerSocket.isClosed()) {
					closed=true;
				} else {
					ioe.printStackTrace();
				}
			}
		}
//		System.out.println("closed ");
		try {
			myAgent.setAttribute(TCPConnector._TCP_SERVER+ myServerSocket.getLocalPort(), null);
			myServerSocket.close();				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}