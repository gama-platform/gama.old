package msi.gama.metamodel.population;

import java.util.Collection;

import msi.gama.metamodel.agent.IAgent;

/**
 * Listeners have to care on efficiency.
 *  
 * @author Samuel Thiriot
 *
 */
public interface IPopulationListener {

	public void notifyAgentRemoved(IPopulation pop, IAgent agent);
	public void notifyAgentAdded(IPopulation pop, IAgent agent);
	public void notifyAgentsAdded(IPopulation pop, Collection agents);
	public void notifyAgentsRemoved(IPopulation pop, Collection agents);
	public void notifyPopulationCleared(IPopulation pop);
	
}
