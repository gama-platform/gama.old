package ummisco.gama.network.common.socket;

import msi.gama.metamodel.agent.IAgent;

public interface IMessageRooter {
	public void leaveGroup(IAgent agt, String groupName);
	public void registerToGroup(IAgent agt, String groupName);
	public void dropGroup(IAgent agt, String groupName);
}
