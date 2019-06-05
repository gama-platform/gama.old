package ummisco.gama.network.common.socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;

public class MessageRooter implements IMessageRooter {
	private final Map<IAgent, List<String>> groupsByAgent;
	private final Map<String, List<IAgent>> agentsByGroup;

	public MessageRooter() {
		groupsByAgent = new HashMap<>();
		agentsByGroup = new HashMap<>();
	}

	@Override
	public void leaveGroup(final IAgent agt, final String groupName) {
		final List<String> glist = groupsByAgent.get(agt);
		if (glist != null) {
			glist.remove(groupName);
		}
		final List<IAgent> alist = agentsByGroup.get(groupName);
		if (alist != null) {
			alist.remove(agt);
		}
	}

	@Override
	public void registerToGroup(final IAgent agt, final String groupName) {
		List<String> glist = groupsByAgent.get(agt);
		if (glist == null) {
			glist = new ArrayList<>();
			this.groupsByAgent.put(agt, glist);
		}
		List<IAgent> alist = agentsByGroup.get(groupName);
		if (alist == null) {
			alist = new ArrayList<>();
			this.agentsByGroup.put(groupName, alist);
		}
		if (!glist.contains(groupName)) {
			glist.add(groupName);
		}
		if (!alist.contains(agt)) {
			alist.add(agt);
		}
	}

	@Override
	public void dropGroup(final IAgent agt, final String groupName) {
		final List<IAgent> alist = agentsByGroup.get(groupName);
		if (alist != null) {
			for (@SuppressWarnings ("unused") final IAgent a : alist) {
				groupsByAgent.get(agt).remove(groupName);
			}
			agentsByGroup.remove(groupName);
		}

	}

}
