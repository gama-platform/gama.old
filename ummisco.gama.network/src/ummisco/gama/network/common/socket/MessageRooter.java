package ummisco.gama.network.common.socket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gaml.expressions.IVarExpression.Agent;

public class MessageRooter implements IMessageRooter{
	private Map<IAgent, List<String>> groupsByAgent;
	private Map<String, List<IAgent>> agentsByGroup;
	
	
	
	
	public MessageRooter()
	{
		groupsByAgent = new HashMap<IAgent, List<String>>();
		agentsByGroup = new HashMap<String, List<IAgent>>();
	}
	
	@Override
	public void leaveGroup(IAgent agt, String groupName) {
		List<String> glist = groupsByAgent.get(agt);
		if(glist != null){
			glist.remove(groupName);
		}
		List<IAgent> alist = agentsByGroup.get(groupName);
		if(alist != null){
			alist.remove(agt);
		}
	}

	@Override
	public void registerToGroup(IAgent agt, String groupName) {
		List<String> glist = groupsByAgent.get(agt);
		if(glist == null){
			glist = new ArrayList<String>();
			this.groupsByAgent.put(agt,glist);
		}
		List<IAgent> alist = agentsByGroup.get(groupName);
		if(alist == null){
			alist = new ArrayList<IAgent>();
			this.agentsByGroup.put(groupName,alist);
		}
		if(!glist.contains(groupName))
			glist.add(groupName);
		if(!alist.contains(agt))
			alist.add(agt);
	}

	@Override
	public void dropGroup(IAgent agt, String groupName) {
		List<IAgent> alist = agentsByGroup.get(groupName);
		if(alist == null){
			for(IAgent a:alist)
				groupsByAgent.get(agt).remove(groupName);
			agentsByGroup.remove(groupName);
		}
		
		
	}
	
	
	
	

}
