package ummisco.gama.network.common.socket;

import msi.gama.metamodel.agent.IAgent;

public class MessageBox {
	private IAgent agent;
	private String boxName;
	private INetworkClient client;
	
	public MessageBox(IAgent agent, String boxName, INetworkClient client) {
		super();
		this.agent = agent;
		this.boxName = boxName;
		this.client = client;
	}
	public IAgent getAgent() {
		return agent;
	}
	public String getBoxName() {
		return boxName;
	}
	public INetworkClient getClient() {
		return client;
	}
	
	

}
