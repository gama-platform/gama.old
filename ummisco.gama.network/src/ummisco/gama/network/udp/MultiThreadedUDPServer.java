package ummisco.gama.network.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Cast;
import ummisco.gama.network.skills.INetworkSkill;

public class MultiThreadedUDPServer extends Thread {

	private IAgent myAgent;
	boolean ServerOn = true;
	private DatagramSocket myUDPServerSocket;
	/**
	 * @return the myServerSocket
	 */
	public DatagramSocket getMyServerSocket() {
		return myUDPServerSocket;
	}

	/**
	 * @param myServerSocket the myServerSocket to set
	 */
	public void setMyServerSocket(DatagramSocket u) {
		this.myUDPServerSocket = u;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		super.interrupt();
		try {
			myAgent.setAttribute("__UDPserver"+ myUDPServerSocket.getLocalPort(), null);
			myUDPServerSocket.close();				

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public MultiThreadedUDPServer(final IAgent a, final DatagramSocket ss) {
		myAgent = a;
		myUDPServerSocket = ss;
	}

	public void run() {
		// Successfully created Server Socket. Now wait for connections.
		byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
		while (!myUDPServerSocket.isClosed()) {
			try {
				// Accept incoming connections.
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				myUDPServerSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
//				System.out.println("RECEIVED: " + sentence);

				GamaMap<String, Object> m=(GamaMap<String, Object>) myAgent.getAttribute("messages"+myAgent);//GamaMap<String, IList<String>>
				if(m==null){
					m=GamaMapFactory.create();
				}
				if(myAgent.dead()){
					this.interrupt();
				}
//				System.out.println("\n\n ClientServiceThread "+"messages"+myAgent+"\n\n");
//				GamaList<String> msgs = (GamaList<String>) m.get(myAgent.getScope(), myClientSocket.toString());
//				if (msgs == null) {				
//					msgs = (GamaList<String>) myAgent.getAttribute("messages" + myClientSocket.toString());					
				GamaList<String> msgs = (GamaList<String>) GamaListFactory.create(String.class);
//				}
				
				msgs.addValue(myAgent.getScope(),sentence != null ? sentence : "");
//				myAgent.setAttribute("messages" + myClientSocket.toString(),msgs);
//				final GamaMap<String, IList<String>> m=(GamaMap<String, IList<String>>) myAgent.getAttribute("messages");
//					m.put(myClientSocket.toString(),msgs);
				m.put(""+myUDPServerSocket.toString(), msgs);
				myAgent.setAttribute("messages"+myAgent, m);
				
				
//				if (!clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
//					GamaList<String> l = (GamaList<String>) Cast.asList(myAgent.getScope(),
//							myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
//					if (l!=null && !l.contains(clientSocket.toString())) {
//						l.addValue(myAgent.getScope(), clientSocket.toString());
//						myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, l);
						
//						final ClientServiceThread cliThread = new ClientServiceThread(myAgent, clientSocket);
//						cliThread.start();

//						myAgent.setAttribute("__client"+clientSocket.toString(), cliThread);
//					}
//				}

			} catch (Exception ioe) {

				if (myUDPServerSocket.isClosed()) {
					this.interrupt();
				} else {
					ioe.printStackTrace();
				}
			}

		}
	}

}