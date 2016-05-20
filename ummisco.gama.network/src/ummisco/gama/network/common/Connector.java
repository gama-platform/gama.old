package ummisco.gama.network.common;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import ummisco.gama.network.skills.GamaNetworkException;
import ummisco.gama.serializer.factory.StreamConverter;

public abstract class Connector implements IConnector{
	
	private String server_URL;
	private String server_port;
	
	protected Connector()
	{
		server_port = "3301";
		server_URL = "localhost";
	}
	
	@Override
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(IScope scope) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	public GamaMap<String, Object> fetchMessageBox(IAgent agt) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public void sendMessage(final IAgent sender,final String receiver, final Object content)
	{
		this.sendMessage(sender,receiver,StreamConverter.convertObjectToStream(sender.getScope(),content));
	}
	
	public Object receiveMessageStream(final IAgent receiver,final String sender, final String content)
	{
		return StreamConverter.convertStreamToObject(receiver.getScope(),receiveMessage(receiver, sender, content));
	}

	
	protected abstract void sendMessage(final IAgent sender,final String receiver, final String content);
	protected abstract String receiveMessage(final IAgent receiver, final String sender, String content);
	
}
