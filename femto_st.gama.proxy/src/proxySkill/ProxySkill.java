package proxySkill;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
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
		final ProxyAgent proxy = (ProxyAgent) scope.getArg("agentToSetAsDistantAgent");
		
		DEBUG.OUT("Proxy to set to distant : " + proxy);
		DEBUG.OUT("Proxy synchromode : " + proxy.synchroMode);
		proxy.setSynchronizationMode(new DistantSynchronizationMode(proxy.synchroMode.proxiedAgent));	
		
		return;
	}
	
	@action (
			name = "updateProxy",
			args = { @arg (
						name = "ProxyToUpdate",
						type = IType.AGENT,
						optional = true,
						doc = @doc ("ProxyAgent to update")),
					@arg (
							name = "ProxyWithAttributes",
							type = IType.AGENT,
							optional = true,
							doc = @doc ("ProxyAgent to take the attributes from to update the other Proxy"))},
			doc = @doc("Update a ProxyAgent with new attributes"))
		public void updateProxy(final IScope scope)
		{
			ProxyAgent proxyToUpdate = (ProxyAgent) scope.getArg("ProxyToUpdate");
			ProxyAgent proxy = (ProxyAgent) scope.getArg("ProxyWithAttributes");

			DEBUG.OUT("updateProxy ProxyToUpdate" + proxyToUpdate.getOrCreateAttributes());
			DEBUG.OUT("updateProxy ProxyWithAttributes" + proxy.getOrCreateAttributes());
			
			proxyToUpdate.synchroMode.updateAttributes(proxy.getAgent());
			
			return;
		}
	

	@action (
			name = "migrateAgent",
			args = { @arg (
						name = "proxy",
						type = IType.AGENT,
						optional = true,
						doc = @doc ("The agent, or server, to which this message will be sent to")),
					@arg (
							name = "agentType",
							type = IType.STRING,
							optional = true,
							doc = @doc ("The agent, or server, to which this message will be sent to"))},
			doc = @doc(
				value = "Action used to send a message (that can be of any kind of object) to an agent or a server.",
				examples = {@example("do send to: dest contents:\"This message is sent by \" + name + \" to \" + dest;")}))
		public void migrateAgent(final IScope scope)
		{
			// TODO find a way to send the agent
		
			DEBUG.OUT("Class agent : " + scope.getArg("proxy").getClass());
			String agentType = (String) scope.getArg("agentType");
			
			ProxyAgent proxy = (ProxyAgent) scope.getArg("proxy");
			DEBUG.OUT("proxy " + proxy);
			
			DEBUG.OUT("migrateAgent " + proxy.getAgent() + " of type " + agentType);
			//DEBUG.OUT("HASH CODE OF MIGRATE :  " + proxy.getHashCode());
			// TODO setup hashcode here
			
			proxy.getAgent().setAttribute("hashcode", proxy.getHashCode());
			DEBUG.OUT("proxy hashcodep ut : " + proxy.getAgent().getAttribute("hashcode"));
			
			IPopulation<? extends IAgent> popOfNewAgent = scope.getSimulation().getMicroPopulation(agentType);
			DEBUG.OUT("old pop " + popOfNewAgent);
			DEBUG.OUT("attributes to create new agents : " + proxy.getOrCreateAttributes());
			DEBUG.OUT("\n");
			DEBUG.OUT("hashcode of agent : " + proxy.getHashCode());
			
			ProxyAgent existingProxy = ((ProxyPopulation)popOfNewAgent).getProxyFromHashCode(proxy.getHashCode());
			if(existingProxy == null)
			{
				DEBUG.OUT("proxy does not exist : " + existingProxy);
				DEBUG.OUT("population class : " + popOfNewAgent.getClass());
				((ProxyPopulation)popOfNewAgent).createAgentAt(scope, 0, proxy.getOrCreateAttributes(), false, false);
				
				DEBUG.OUT("new pop " + popOfNewAgent);
				for(var auto : popOfNewAgent)
				{
					DEBUG.OUT("auto : " + auto.getOrCreateAttributes());
				}
			}else
			{
				DEBUG.OUT("proxy exist : " + existingProxy);
				existingProxy.setSynchronizationMode(new SynchronizationMode(proxy.getAgent()));
				DEBUG.OUT("existingProxy updated : " + existingProxy);
			}
			
			
			return;
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
		DEBUG.ON();
		ProxyAgent agent = (ProxyAgent) scope.getArg("ProxyAgent");
		int simulationID = (Integer) scope.getArg("SimulationID");
		
		DEBUG.OUT("\n");
		DEBUG.OUT("ProxyAgent hashcode in simulation(" + simulationID + ") : " + agent.getHashCode());
		DEBUG.OUT("\n");
		
		DEBUG.OFF();
	}
}
