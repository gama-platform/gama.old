package proxySkill;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import proxy.ProxyAgent;
import proxyPopulation.ProxyPopulation;
import synchronizationMode.DistantSynchronizationMode;
import synchronizationMode.LocalSynchronizationMode;
import ummisco.gama.dev.utils.DEBUG;

public class ProxyFunctions 
{
	
	static 
	{
		DEBUG.OFF();
	}
	
	/**
	 * Set the agent agentToSetDistant as a distant agent in the current simulation
	 * 
	 * @param scope
	 * @param agentToSetAsDistant
	 */
	public static IAgent setAgentAsDistant(final IScope scope, IAgent agentToSetAsDistant)
	{
		DEBUG.OUT("agentToSetAsDistant.class : " + agentToSetAsDistant.getClass());
		if(agentToSetAsDistant instanceof ProxyAgent)
		{
			final ProxyAgent proxy = (ProxyAgent) agentToSetAsDistant;
			DEBUG.OUT("proxy agentToSetAsDistant : " + proxy);
			DEBUG.OUT("proxy.getSynchroMode().proxiedAgent : " + proxy.getSynchroMode().getAgent());
			proxy.setSynchronizationMode(new DistantSynchronizationMode(scope, proxy.getSynchroMode().getAgent()));
			
			return proxy;
		}else // MinimalAgent
		{
			final IAgent agent = agentToSetAsDistant;
			DEBUG.OUT("minimal agentToSetAsDistant : " + agent);
			String agentType = agent.getGamlType().toString();
			
			IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentType);
			ProxyAgent existingProxy = ((ProxyPopulation)popOfNewAgent).getProxyFromHashCode(((MinimalAgent) agent).hashCode);
			if(existingProxy != null)
			{
				DEBUG.OUT("proxy exist (setDistant) : " + existingProxy);
				existingProxy.setSynchronizationMode(new DistantSynchronizationMode(scope, agent));	
			}

			return existingProxy;
		}
	}
	
	/**
	 * 
	 * create agentToMigrate on this simulation
	 * 
	 * @param scope
	 * @param agentToMigrate
	 * @return
	 */
	public static IAgent migrateAgent(final IScope scope, IAgent agentToMigrate)
	{
		if(agentToMigrate instanceof ProxyAgent)
		{
			DEBUG.OUT("THIS IS A PROXYAGENT");
			ProxyAgent proxy = (ProxyAgent) scope.getArg("agentToMigrate");
			agentToMigrate = proxy.getAgent();
			DEBUG.OUT("agentToCopy proxy : " + agentToMigrate.getOrCreateAttributes());
		}
		
		DEBUG.OUT("agent " + agentToMigrate);
		DEBUG.OUT("type : " + agentToMigrate.getGamlType().toString());
		
		DEBUG.OUT("attributes to create new agents : " + agentToMigrate.getOrCreateAttributes());
		DEBUG.OUT("hashcode of agent : " + ((MinimalAgent)agentToMigrate).hashCode());

		agentToMigrate.setAttribute(IKeyword.LOCATION, agentToMigrate.getLocation());
		agentToMigrate.setAttribute(IKeyword.HASHCODE, ((MinimalAgent)agentToMigrate).hashCode());
	
		DEBUG.OUT("mapAttributes : " + agentToMigrate.getOrCreateAttributes());
		
		ProxyAgent existingProxy = getProxyFromAgent(scope, agentToMigrate);
		if(existingProxy == null) // create the agent with his proxy using the data of agentToMigrate
		{
			DEBUG.OUT("proxy does not exist : " + existingProxy);
			IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentToMigrate.getGamlType().toString());
			ProxyAgent proxyCreated = ((ProxyPopulation)popOfNewAgent).createAgentAt(scope, 0, agentToMigrate.getOrCreateAttributes(), false, false);
			DEBUG.OUT("proxyCreated agent : " + proxyCreated.getAgent().getOrCreateAttributes());
			
			return proxyCreated;
		}else // updating existing proxy
		{
			DEBUG.OUT("proxy exist : " + existingProxy);
			existingProxy.setSynchronizationMode(new LocalSynchronizationMode(agentToMigrate));
			DEBUG.OUT("existingProxy updated : " + existingProxy);
			DEBUG.OUT("existingProxy agent : " + existingProxy.getAgent().getOrCreateAttributes());
			
			return existingProxy;
		}
	}
	
	/**
	 * 
	 * create proxy of agentToCopy on the current simulation
	 * 
	 * @param scope
	 * @param agentToCopy
	 * @return
	 */
	public static IAgent createCopyAgent(final IScope scope, IAgent agentToCopy)
	{
		DEBUG.OUT("createCopyAgentX");
		DEBUG.OUT("agent " + agentToCopy);
		if(agentToCopy instanceof ProxyAgent)
		{
			DEBUG.OUT("THIS IS A PROXYAGENT : " + agentToCopy);
			ProxyAgent proxy = (ProxyAgent) agentToCopy;
			DEBUG.OUT("agentToCopy proxy : " + agentToCopy.getOrCreateAttributes());
			agentToCopy = proxy.getAgent();

			DEBUG.OUT("agentToCopy null?? : " + agentToCopy);
			if(agentToCopy == null)
			{
				return null;
			}
		}
		
		DEBUG.OUT("type : " + agentToCopy.getGamlType().toString());
		
		DEBUG.OUT("attributes to create new agents : " + agentToCopy.getOrCreateAttributes());
		DEBUG.OUT("hashcode of agent : " + ((MinimalAgent)agentToCopy).hashCode());

		agentToCopy.setAttribute(IKeyword.GEOMETRY, agentToCopy.getGeometry());
		agentToCopy.setAttribute(IKeyword.LOCATION, agentToCopy.getLocation());
		agentToCopy.setAttribute(IKeyword.HASHCODE, ((MinimalAgent)agentToCopy).hashCode());
	
		DEBUG.OUT("mapAttributes : " + agentToCopy.getOrCreateAttributes());
		
		ProxyAgent existingProxy = getProxyFromAgent(scope, agentToCopy);
		if(existingProxy == null) // create the agent with his proxy using the data of agentToCopy
		{
			DEBUG.OUT("proxy does not exist : " + existingProxy);
			IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentToCopy.getGamlType().toString());
			ProxyAgent proxyCreated = ((ProxyPopulation)popOfNewAgent).createAgentAt(scope, 0, agentToCopy.getOrCreateAttributes(), false, false);
			DEBUG.OUT("proxyCreated attriv : " + proxyCreated.getAgent().getOrCreateAttributes());
			DEBUG.OUT("proxyCreated agent " + proxyCreated);
			DEBUG.OUT("proxyCreated proxied " + proxyCreated.getAgent());
			
			return proxyCreated;
			
		}else // updating existing proxy
		{
			DEBUG.OUT("proxy exist : " + existingProxy);
			existingProxy.setSynchronizationMode(new LocalSynchronizationMode(agentToCopy));

			return existingProxy;
		}
	}
	
	public static void updateProxy(IScope scope, IAgent agent)
	{
		DEBUG.OUT("updateProxy agent class : " + agent.getClass());
		if(agent instanceof ProxyAgent)
		{
			ProxyAgent proxyWithData = (ProxyAgent) agent;
			DEBUG.OUT("proxyWithData : " + proxyWithData);
			DEBUG.OUT("proxyWithData.getAgent() : " + proxyWithData.getAgent());
			ProxyAgent proxyToUpdate = ProxyFunctions.getProxyFromAgent(scope, proxyWithData.getAgent());
			
			DEBUG.OUT("proxyToUpdate proxy " + proxyToUpdate);
			proxyToUpdate.getSynchroMode().updateAttributes(proxyWithData.getAgent());
		}else
		{
			ProxyAgent proxyToUpdate = ProxyFunctions.getProxyFromAgent(scope, agent);
			
			DEBUG.OUT("proxyToUpdate minimal " + proxyToUpdate);
			proxyToUpdate.getSynchroMode().updateAttributes(agent);
		}
	}
	
	public static void deleteDistant(IScope scope, IAgent agent)
	{
		DEBUG.OUT("updateProxy agent class : " + agent.getClass());
		if(agent instanceof ProxyAgent) ProxyFunctions.removeProxyFromPopulation(scope, (ProxyAgent) agent); else ProxyFunctions.removeProxyFromPopulation(scope, agent);
	}
	
	/**
	 * Get the proxy of the parameter agent
	 * 
	 * @param scope
	 * @param agent
	 * @return
	 */
	public static ProxyAgent getProxyFromAgent(final IScope scope, final IAgent agent)
	{
		DEBUG.OUT("agent.getGamlType().toString() : " + agent.getGamlType().toString());
		IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agent.getGamlType().toString());
		DEBUG.OUT("popOfNewAgent : " + popOfNewAgent);
		DEBUG.OUT("((MinimalAgent)agent).hashCode" + ((MinimalAgent)agent).hashCode);
		return ((ProxyPopulation)popOfNewAgent).getProxyFromHashCode(((MinimalAgent)agent).hashCode);
	}
	
	/**
	 * Remove the proxy from the proxy population 
	 * 
	 * @param scope
	 * @param proxyToDelete
	 */
	public static void removeProxyFromPopulation(final IScope scope, final ProxyAgent proxyToDelete)
	{
		IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(proxyToDelete.getAgent().getGamlType().toString());
		((ProxyPopulation)popOfNewAgent).remove(proxyToDelete);
		((ProxyPopulation)popOfNewAgent).getMapProxyID().remove(proxyToDelete.getHashCode());
	}
	
	/**
	 * Remove the proxy from the proxy population using the agent
	 * 
	 * @param scope
	 * @param agentToDeleteProxy
	 */
	public static void removeProxyFromPopulation(final IScope scope, final IAgent agentToDeleteProxy)
	{
		ProxyAgent proxyToDelete = getProxyFromAgent(scope, agentToDeleteProxy);
		IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentToDeleteProxy.getGamlType().toString());
		((ProxyPopulation)popOfNewAgent).remove(proxyToDelete);
		((ProxyPopulation)popOfNewAgent).getMapProxyID().remove(proxyToDelete.getHashCode());
	}
}
