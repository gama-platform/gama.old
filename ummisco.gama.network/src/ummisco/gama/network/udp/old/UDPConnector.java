package ummisco.gama.network.udp.old;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Cast;
import ummisco.gama.network.common.IConnector;
import ummisco.gama.network.skills.GamaNetworkException;
import ummisco.gama.network.skills.INetworkSkill;
import ummisco.gama.network.tcp.MultiThreadedSocketServer;
import ummisco.gama.network.udp.MultiThreadedUDPServer;

public class UDPConnector implements IConnector{

	private boolean is_server = false;
	private IScope myScope;

	public UDPConnector(final IScope scope, final boolean as_server){
		is_server = as_server;			
		myScope = scope;
	}
	
	@Override
	public GamaMap<String, Object> fetchMessageBox(IAgent agt) {
		GamaMap<String, Object> m = GamaMapFactory.create();
//		if(is_server){			
				final String cli;
				String receiveMessage = "";
	//		System.out.println("\n\n primGetFromClient "+"messages"+agt+"\n\n");
				
				m=(GamaMap<String, Object>) agt.getAttribute("messages"+agt);
				agt.setAttribute("messages",null);
//		}else{
//			try {
//				byte[] sendData = new byte[1024];
//				byte[] receiveData = new byte[1024];
//				DatagramSocket clientSocket = new DatagramSocket();
//
//				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//				
//				clientSocket.receive(receivePacket);
//				String modifiedSentence = new String(receivePacket.getData());
//				
//		
//				GamaList<String> msgs = (GamaList<String>) GamaListFactory.create(String.class);
//				
//				msgs.addValue(agt.getScope(),modifiedSentence != null ? modifiedSentence : "");
//				m.put(""+clientSocket.toString(), msgs);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

		return m;
	}

	@Override
	public boolean emptyMessageBox(IAgent agt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception {
//		final Integer port = Cast.asInt(scope, scope.getAgentScope().getAttribute("port"));
		if(is_server){

			if (agent.getScope().getAgentScope().getAttribute("__UDPserver" + port) == null) {
				try {
					final DatagramSocket sersock = new DatagramSocket(port);
					sersock.setSoTimeout(10);
					final MultiThreadedUDPServer ssThread = new MultiThreadedUDPServer(agent,
							sersock);
					ssThread.start();
					agent.setAttribute("__UDPserver" + port, ssThread);
	
				} catch (BindException be) {
					throw GamaRuntimeException.create(be, agent.getScope());
				} catch (Exception e) {
					throw GamaRuntimeException.create(e, agent.getScope());
				}
			}
		}
			else{

//			InetAddress IPAddress = InetAddress.getByName("localhost");
//
//			byte[] sendData = new byte[1024];
//			byte[] receiveData = new byte[1024];
//			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			
			if (agent.getScope().getAgentScope().getAttribute("__UDPclient" + port) == null) {

				try {
					final DatagramSocket sersock = new DatagramSocket();
					sersock.setSoTimeout(10);
					final MultiThreadedUDPServer ssThread = new MultiThreadedUDPServer(agent, sersock);
					ssThread.OnServer = false;
					ssThread.start();
					agent.setAttribute("__UDPclient" + port, ssThread);

				} catch (BindException be) {
					throw GamaRuntimeException.create(be, agent.getScope());
				} catch (Exception e) {
					throw GamaRuntimeException.create(e, agent.getScope());
				}
			}
		}
	}
	@Override
	public void sendMessage(IAgent agent, String dest, Object data) {
		if(is_server){
			int port = 		(int) agent.getAttribute("port");

			MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getAttribute("__UDPserver" + port);
			InetAddress IPAddress =(InetAddress) agent.getAttribute("replyIP");
			int replyport =Cast.asInt(agent.getScope(), agent.getAttribute("replyPort"));
			if(ssThread==null ||  IPAddress == null ) {return;}
              
				byte[] sendData = new byte[1024];
				sendData = ((String)data).getBytes();
              DatagramPacket sendPacket =
              new DatagramPacket(sendData, sendData.length, IPAddress, replyport);
              try {
				ssThread.setSendPacket(sendPacket);
//  				System.out.println("SENT: "+replyport);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{
				
			final Integer port = Cast.asInt(agent.getScope(), agent.getAttribute("port"));
	
			MultiThreadedUDPServer ssThread = (MultiThreadedUDPServer) agent.getAttribute("__UDPclient" + port);
			if(ssThread==null ) {return;}

			try {
	
				DatagramSocket clientSocket = ssThread.getMyServerSocket();
				InetAddress IPAddress = InetAddress.getByName((String) agent.getAttribute(INetworkSkill.SERVER_URL));
				
				byte[] sendData = new byte[1024];
				byte[] receiveData = new byte[1024];
	
				DatagramPacket sendPacket  = new DatagramPacket(sendData, sendData.length, IPAddress, port);
							
							
				
				sendData = ((String)data).getBytes();
				
				sendPacket.setData(sendData);
				
				clientSocket.send(sendPacket);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close(final IScope scope) throws GamaNetworkException {
		
	
	}

	@Override
	public void registerToGroup(IAgent agt, String groupName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leaveTheGroup(IAgent agt, String groupName) {
		// TODO Auto-generated method stub
		
	}

}
