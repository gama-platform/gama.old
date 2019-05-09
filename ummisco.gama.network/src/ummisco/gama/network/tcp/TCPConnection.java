package ummisco.gama.network.tcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.internal.MessageCatalog;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Cast;
import ummisco.gama.network.common.CommandMessage;
import ummisco.gama.network.common.CommandMessage.CommandType;
import ummisco.gama.network.common.Connector;
import ummisco.gama.network.common.GamaNetworkException;
import ummisco.gama.network.common.MessageFactory;
import ummisco.gama.network.common.MessageFactory.MessageType;
import ummisco.gama.network.common.socket.SocketService;

public class TCPConnection extends Connector {

	private SocketService socket;
	protected IScope simulationScope;
	private boolean isServer;
	
	private ArrayList<String> remoteBoxName;

	public TCPConnection(IScope scope, boolean isServer) {
		this.simulationScope = scope;
		this.isServer = isServer;
		this.remoteBoxName = new ArrayList<String>();
	}
	
	protected void extractAndApplyCommand(String sender, String message)
	{
		CommandMessage mm = MessageFactory.unPackCommandMessage(sender, message);
		String sttr= mm.getPlainContents();
		if(mm.getCommand().equals(CommandType.NEW_GROUP))
			this.remoteBoxName.add(sttr);
		
		if(mm.getCommand().equals(CommandType.REMOVE_GROUP))
			this.remoteBoxName.remove(sttr);
		
			//String str = (mm.getContents(this.simulationScope).getContents(this.simulationScope);
			//this.remoteBoxName.add();
	}
	
	@Override
	protected void connectToServer(IAgent agent) throws GamaNetworkException {
		if(isConnected ) return;
		
		String server = this.getConfigurationParameter(SERVER_URL);
		int port = Integer.valueOf(this.getConfigurationParameter(SERVER_PORT)).intValue();
		if(this.isServer) 
			socket = new ServerService(port) {
				@Override
				public void receivedMessage(String sender,String message) {
					MessageType mte = MessageFactory.identifyMessageType(message);
					if(mte.equals(MessageType.COMMAND_MESSAGE)){
						extractAndApplyCommand(sender,message);
					}
					else
						storeMessage(sender, message);
				}
			};
			else
				socket = new ClientService(server,port,this) {
					@Override
					public void receivedMessage(String sender,String message) {
						MessageType mte = MessageFactory.identifyMessageType(message);
						if(mte.equals(MessageType.COMMAND_MESSAGE)){
							extractAndApplyCommand(sender,message);
						}
						else
							storeMessage(sender, message);
					}
				};
			try {
				socket.startService();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.setConnected();
	}

	@Override
	protected boolean isAlive(IAgent agent) throws GamaNetworkException {
		return socket.isOnline();
	}

	@Override
	protected void subscribeToGroup(IAgent agt, String boxName) throws GamaNetworkException {
		if(!this.localMemberNames.containsKey(boxName))
			this.remoteBoxName.add(boxName);
		
		CommandMessage cmd = MessageFactory.buildCommandMessage(socket.getLocalAddress(),
				socket.getRemoteAddress(), CommandType.NEW_GROUP, boxName);
		this.sendMessage(agt, socket.getRemoteAddress(),MessageFactory.packMessage(cmd) );
	}

	@Override
	protected void unsubscribeGroup(IAgent agt, String boxName) throws GamaNetworkException {
		this.remoteBoxName.remove(boxName);
		CommandMessage cmd = MessageFactory.buildCommandMessage(socket.getLocalAddress(),
				socket.getRemoteAddress(), CommandType.REMOVE_GROUP, boxName);
		this.sendMessage(agt, socket.getRemoteAddress(),MessageFactory.packMessage(cmd) );
	}

	@Override
	protected void releaseConnection(IScope scope) throws GamaNetworkException {
		socket.stopService();
		socket = null;
		this.isConnected = false;
	}

	@Override
	protected void sendMessage(IAgent sender, String receiver, String content) throws GamaNetworkException {
		try {
			if(socket != null)
				socket.sendMessage(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
