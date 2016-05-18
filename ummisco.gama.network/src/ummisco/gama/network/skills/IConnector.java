package ummisco.gama.network.skills;

import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;

public interface IConnector {
	public void connectToServer(IAgent agent, String dest, String server, int port) throws Exception;
	public void close(final IScope scope) throws GamaNetworkException;
	public void sendMessage(IAgent agent,String dest, Object  data) ;
	public GamaMap<String, Object> fetchMessageBox(final IAgent agt);
	public boolean emptyMessageBox(IAgent agt);
}
