package proxySkill;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import proxy.ProxyAgent;
import synchronizationMode.LocalSynchronizationMode;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.network.skills.NetworkSkill;

@skill (name = "ProxySkill",
		doc = @doc ("Skill to test ProxyAgent behavior"))
public class ProxySkill extends NetworkSkill 
{
	static 
	{
		DEBUG.OFF();
	}
	
	@SuppressWarnings("unchecked")
	@action (
		name = "setAgentsAsDistant",
		args = {
			@arg (
				name = "agentsToSetAsDistantAgent",
				type = IType.LIST,
				optional = false,
				doc = @doc ("Set the list of agents as distant agents, their proxyAgent will now update their attributes according to the politic of their proxy"))})
	public List<IAgent> setAgentsAsDistantAction(final IScope scope)
	{
		DEBUG.OUT(scope.getArg("agentsToSetAsDistantAgent"));
		List<IAgent> agentsToSetAsDistant = (List<IAgent>)scope.getArg("agentsToSetAsDistantAgent");
		for(var agentToSetAsDistant : agentsToSetAsDistant)
		{			
			ProxyFunctions.setAgentAsDistant(scope, agentToSetAsDistant);
		}
		return agentsToSetAsDistant;
	}
	
	@action (
		name = "setAgentAsDistant",
		args = {
			@arg (
				name = "agentToSetAsDistantAgent",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("Set this agent as a distant agent, his proxyAgent will now update his attributes according to the politic of this proxy"))})
	public IAgent setAgentAsDistantAction(final IScope scope)
	{
		IAgent agentToSetAsDistant = (IAgent) scope.getArg("agentToSetAsDistantAgent");
		ProxyFunctions.setAgentAsDistant(scope, agentToSetAsDistant);
		return agentToSetAsDistant;
	}
	
	@action (
		name = "updateProxys",
		args = { @arg (
					name = "AgentsWithData",
					type = IType.LIST,
					optional = false,
					doc = @doc ("list of ProxyAgent to update"))})
	public void updateProxysAction(final IScope scope)
	{
		DEBUG.OUT("agentsToSetAsDistantAgent : " + scope.getArg("agentsToSetAsDistantAgent"));
		List<IAgent> agentsWithData = (List<IAgent>)scope.getArg("AgentsWithData");
		DEBUG.OUT("agentsWithData : " + agentsWithData);
		
		for(var agentWithData : agentsWithData)
		{			
			ProxyFunctions.updateProxy(scope, agentWithData);
		}
		return;
	}
	
	@action (
		name = "updateProxy",
		args = { @arg (
					name = "AgentWithData",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("ProxyAgent to update"))})
	public void updateProxyAction(final IScope scope)
	{
		ProxyFunctions.updateProxy(scope, (IAgent) scope.getArg("AgentWithData"));
	}

	@SuppressWarnings("unchecked")
	@action (
			name = "createCopyAgents",
			args = { @arg (
						name = "agentToCopy",
						type = IType.LIST,
						optional = false,
						doc = @doc ("Agents to create in current simulation"))},
			doc = @doc("create copy of the agents to this simulation"))
	public List<IAgent> createCopyAgentsAction(final IScope scope)
	{
		DEBUG.OUT("createCopyAgentsAction" + scope.getArg("agentToCopy"));
		List<IAgent> agentsToCopy = (List<IAgent>) scope.getArg("agentToCopy");
		DEBUG.OUT("agentsToCopy" + agentsToCopy);
		
		List<IAgent> newAgents = new ArrayList<IAgent>();
		for(var agentToCopy : agentsToCopy)
		{
			IAgent agent = ProxyFunctions.createCopyAgent(scope, agentToCopy);
			newAgents.add(agent);
		}
		DEBUG.OUT("newAgents" + newAgents);
		
		return newAgents;
	}
	
	@action (
		name = "createCopyAgent",
		args = { @arg (
					name = "agentToCopy",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("Agent to migrate to another simulation"))},
		doc = @doc("create copy of the agents to this simulation"))
	public IAgent createCopyAgentAction(final IScope scope)
	{
		return ProxyFunctions.createCopyAgent(scope, (IAgent) scope.getArg("agentToCopy"));
	}
	
	@action (name = "deleteDistant",
		args = { @arg (
				name = "distantToDelete",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("Agent to check if he is proxy"))})
	public void deleteDistant(IScope scope)
	{
		ProxyFunctions.deleteDistant(scope, (IAgent) scope.getArg("distantToDelete"));
	}
	
	@action (name = "deleteDistants",
			args = { @arg (
				name = "distantsToDelete",
				type = IType.LIST,
				optional = false,
				doc = @doc ("Agent to check if he is proxy"))})
	public void deleteDistants(IScope scope)
	{
		DEBUG.OUT("distantsToDelete " + scope.getArg("distantsToDelete"));
		List<IAgent> agentsToDelete = (List<IAgent>) scope.getArg("distantsToDelete");
		DEBUG.OUT(agentsToDelete);
		
		for(var agentToDelete : agentsToDelete)
		{
			ProxyFunctions.deleteDistant(scope, agentToDelete);
		}
	}
	
	@action (name = "addDistantAgentsToUpdate",
			args = { @arg (
					name = "ProxyAgents",
					type = IType.LIST,
					optional = false,
					doc = @doc ("Agent to add distant agent")),
					@arg (
						name = "SimulationID",
						type = IType.INT,
						optional = false,
						doc = @doc ("The simulation's ID where the agent to update is located"))
			},
		doc = @doc("Add SimulationID to procsWithDistantAgent of a local ProxyAgent"))
		public void addDistantAgentsToUpdate(IScope scope)
		{
			List<ProxyAgent> proxys = (List<ProxyAgent>) scope.getArg("ProxyAgents");
			DEBUG.OUT("addDistantAgentsToUpdate : " + proxys);
			int simulationID = (Integer) scope.getArg("SimulationID");
			
			for(var agentToAddAsDistant : proxys)
			{
				if(agentToAddAsDistant.getSynchroMode() instanceof LocalSynchronizationMode)
				{
					DEBUG.OUT("Adding " + simulationID + " to " + agentToAddAsDistant.getName());
					((LocalSynchronizationMode)agentToAddAsDistant.getSynchroMode()).addProcs(simulationID);
				}
			}
		}

	@action (name = "checkHashCode",
		args = { @arg (
				name = "ProxyAgent",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("Agent to check HashCode")),
				@arg (
					name = "SimulationID",
					type = IType.INT,
					optional = false,
					doc = @doc ("The simulation's ID where the agent is from"))
		},
	doc = @doc("Display the HashCode of a ProxyAgent in the Eclipse console"))
	public void checkHashCode(IScope scope)
	{
		ProxyAgent proxy = (ProxyAgent) scope.getArg("ProxyAgent");
		int simulationID = (Integer) scope.getArg("SimulationID");
		
		DEBUG.OUT("\n");
		DEBUG.OUT("ProxyAgent hashcode in simulation(" + simulationID + ") : " + proxy.getHashCode());
		DEBUG.OUT("\n");
		
		var agent = ProxyFunctions.getProxyFromAgent(scope, proxy.getAgent());
		DEBUG.OUT("agentagentagentagent : " + agent);
	}
	
	@action (name = "isProxy",
		args = { @arg (
				name = "testProxy",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("Agent to check if he is proxy"))})
	public String isProxy(IScope scope)
	{
		String agentName = ((IAgent)scope.getArg("testProxy")).getName();
		DEBUG.OUT("ISPROXY : " + "agent(" + agentName + ") : " + scope.getArg("testProxy").getClass());
		return "agent(" + agentName + ") : " + scope.getArg("testProxy").getClass();
	}
	
	@action (name = "printPopulationState")
	public String printPopulationState(IScope scope)
	{
		String ret = "";
		var pops = scope.getSimulation().getMicroPopulations();
		ret = ret + "pops : " + pops + "\n";
		DEBUG.OUT("pops : " + pops);
		for(var pop : pops)
		{
			ret = ret + "pop : " + pop + "\n";
			DEBUG.OUT("pop : " + pop);
			for(var agent : pop)	
			{
				if(agent instanceof ProxyAgent)
				{
					ret = ret + "agent : " + agent + " :: " + ((ProxyAgent)agent).getHashCode() + "\n";
					DEBUG.OUT("agent : " + agent + " :: " + ((ProxyAgent)agent).getHashCode());
				}else
				{
					ret = ret + "agent : " + agent + "\n";
					DEBUG.OUT("agent : " + agent);
				}
			}
		}
		
		return ret;
	}
	@action (name = "testGetPopulationFor",
			args = { @arg (
					name = "agent",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("Agent to check if he is proxy"))})
	public IPopulation<? extends IAgent> testGetPopulationFor(IScope scope)
	{
		IAgent agent = (IAgent) scope.getArg("agent");
		final IPopulation<? extends IAgent> microPopulation = scope.getSimulation().getPopulationFor(agent.getGamlType().toString());
		return microPopulation;
	}
}
