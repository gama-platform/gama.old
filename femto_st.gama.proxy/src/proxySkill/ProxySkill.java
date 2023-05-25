package proxySkill;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import proxy.ProxyAgent;
import proxyPopulation.ProxyPopulation;
import synchronizationMode.DistantSynchronizationMode;
import synchronizationMode.SynchronizationMode;
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
	
	@action (
		name = "setAgentAsDistant",
		args = {
			@arg (
				name = "agentToSetAsDistantAgent",
				type = IType.AGENT,
				optional = false,
				doc = @doc ("Set this agent as a distant agent, his proxyAgent will now update his attributes according to the politic of this proxy"))})
	public void setAgentAsDistant(final IScope scope)
	{
		DEBUG.OUT("setAgentAsDistant agent class : " + scope.getArg("agentToSetAsDistantAgent"));
		if(scope.getArg("agentToSetAsDistantAgent") instanceof ProxyAgent)
		{
			final ProxyAgent proxy = (ProxyAgent) scope.getArg("agentToSetAsDistantAgent");
			proxy.setSynchronizationMode(new DistantSynchronizationMode(proxy.synchroMode.proxiedAgent));	
		}else
		{
			final IAgent agent = (IAgent) scope.getArg("agentToSetAsDistantAgent");
			String agentType = agent.getGamlType().toString();
			
			IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentType);
			ProxyAgent existingProxy = ((ProxyPopulation)popOfNewAgent).getProxyFromHashCode(((MinimalAgent) agent).hashCode);
			if(existingProxy != null)
			{
				DEBUG.OUT("proxy exist (setDistant) : " + existingProxy);
				existingProxy.setSynchronizationMode(new DistantSynchronizationMode(agent));	
			}
		}
		
		return;
	}
	
	@action (
		name = "updateProxy",
		args = { @arg (
					name = "AgentWithData",
					type = IType.AGENT,
					doc = @doc ("ProxyAgent to update"))})
	public void updateProxy(final IScope scope)
	{

		DEBUG.OUT("updateProxy agent class : " + scope.getArg("AgentWithData").getClass());
		if(scope.getArg("AgentWithData") instanceof ProxyAgent)
		{
			ProxyAgent proxyWithData = (ProxyAgent) scope.getArg("AgentWithData");
			DEBUG.OUT("proxyWithData.getAgent() : " + proxyWithData.getAgent());
			ProxyAgent proxyToUpdate = getProxyFromAgent(scope, proxyWithData.getAgent());
			
			DEBUG.OUT("proxyToUpdate proxy " + proxyToUpdate);
			proxyToUpdate.synchroMode.updateAttributes(proxyWithData.getAgent());
		}else
		{
			IAgent agentWithData = (IAgent) scope.getArg("AgentWithData");
			ProxyAgent proxyToUpdate = getProxyFromAgent(scope, agentWithData);
			
			DEBUG.OUT("proxyToUpdate minimal " + proxyToUpdate);
			proxyToUpdate.synchroMode.updateAttributes(agentWithData);
		}
		
		return;
	}
	
	@action (
		name = "migrateAgent",
		args = { @arg (
					name = "agentToMigrate",
					type = IType.AGENT,
					optional = true,
					doc = @doc ("Agent to migrate to another simulation"))},
		doc = @doc("Migrate an agent to another simulation"))
	public IAgent migrateAgent(final IScope scope)
	{

		DEBUG.OUT("Class agentToMigrate : " + scope.getArg("agentToMigrate").getClass());
		IAgent agentToMigrate;
		if(scope.getArg("agentToMigrate") instanceof ProxyAgent)
		{
			ProxyAgent proxy = (ProxyAgent) scope.getArg("agentToMigrate");
			agentToMigrate = proxy.getAgent();
		}else
		{
			agentToMigrate = (IAgent) scope.getArg("agentToMigrate");
		}
		
		DEBUG.OUT("agent " + agentToMigrate);
		DEBUG.OUT("type : " + agentToMigrate.getGamlType().toString());
		
		DEBUG.OUT("attributes to create new agents : " + agentToMigrate.getOrCreateAttributes());
		DEBUG.OUT("hashcode of agent : " + ((MinimalAgent)agentToMigrate).hashCode());

		agentToMigrate.setAttribute(IKeyword.LOCATION, agentToMigrate.getLocation());
		agentToMigrate.setAttribute(IKeyword.HASHCODE, ((MinimalAgent)agentToMigrate).hashCode());
	
		DEBUG.OUT("mapAttributes : " + agentToMigrate.getOrCreateAttributes());
		
		ProxyAgent existingProxy = getProxyFromAgent(scope, agentToMigrate);
		if(existingProxy == null)
		{
			DEBUG.OUT("proxy does not exist : " + existingProxy);
			IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentToMigrate.getGamlType().toString());
			
			for(var auto : ((ProxyPopulation)popOfNewAgent))
			{
				DEBUG.OUT("in pop " + auto);
			}
			ProxyAgent proxyCreated = ((ProxyPopulation)popOfNewAgent).createAgentAt(scope, 0, agentToMigrate.getOrCreateAttributes(), false, false);
			for(var auto : ((ProxyPopulation)popOfNewAgent))
			{
				DEBUG.OUT("in pop after " + auto);
			}
			return proxyCreated;
		}else
		{
			DEBUG.OUT("proxy exist : " + existingProxy);
			existingProxy.setSynchronizationMode(new SynchronizationMode(agentToMigrate));
			DEBUG.OUT("existingProxy updated : " + existingProxy);
			return existingProxy;
		}
	}

	@action (name = "checkHashCode",
		args = { @arg (
				name = "ProxyAgent",
				type = IType.AGENT,
				doc = @doc ("Agent to check HashCode")),
				@arg (
					name = "SimulationID",
					type = IType.INT,
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
	}
	
	@action (name = "isProxy",
		args = { @arg (
				name = "testProxy",
				type = IType.AGENT,
				doc = @doc ("Agent to check if he is proxy"))})
	public String isProxy(IScope scope)
	{
		String agentName = ((IAgent)scope.getArg("testProxy")).getName();
		DEBUG.OUT("ISPROXY : " + "agent(" + agentName + ") : " + scope.getArg("testProxy").getClass());
		return "agent(" + agentName + ") : " + scope.getArg("testProxy").getClass();
	}
	
	@action (name = "deleteDistant",
		args = { @arg (
				name = "distantToDelete",
				type = IType.AGENT,
				doc = @doc ("Agent to check if he is proxy"))})
	public void deleteDistant(IScope scope)
	{
		DEBUG.OUT("updateProxy agent class : " + scope.getArg("distantToDelete").getClass());
		if(scope.getArg("AgentWithData") instanceof ProxyAgent)
		{
			ProxyAgent proxyToDelete = (ProxyAgent) scope.getArg("distantToDelete");
			removeProxyFromPopulation(scope, proxyToDelete);
		}else{
			IAgent distantToDelete = (IAgent) scope.getArg("distantToDelete");
			removeProxyFromPopulation(scope, distantToDelete);
		}
	}
	
	ProxyAgent getProxyFromAgent(IScope scope, IAgent agent)
	{
		IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agent.getGamlType().toString());
		return ((ProxyPopulation)popOfNewAgent).getProxyFromHashCode(((MinimalAgent)agent).hashCode);
	}
	
	void removeProxyFromPopulation(IScope scope, ProxyAgent proxyToDelete)
	{
		IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(proxyToDelete.getAgent().getGamlType().toString());
		((ProxyPopulation)popOfNewAgent).remove(proxyToDelete);
		((ProxyPopulation)popOfNewAgent).getMapProxyID().remove(proxyToDelete.getHashCode());
	}
	
	void removeProxyFromPopulation(IScope scope, IAgent agentToDeleteProxy)
	{
		ProxyAgent proxyToDelete = getProxyFromAgent(scope, agentToDeleteProxy);
		IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentToDeleteProxy.getGamlType().toString());
		((ProxyPopulation)popOfNewAgent).remove(proxyToDelete);
		((ProxyPopulation)popOfNewAgent).getMapProxyID().remove(proxyToDelete.getHashCode());
	}
}
