package ummisco.gama.network.common;

import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;

public interface IConnector {
	public void connectToServer(IAgent agent, String dest, String server, IScope scope) throws Exception;
	public void sendMessage(String dest, Map<String, String >  data) ;
	public GamaMap<String, String> fetchMessageBox(final IAgent agt);
	public boolean emptyMessageBox(IAgent agt);
}
