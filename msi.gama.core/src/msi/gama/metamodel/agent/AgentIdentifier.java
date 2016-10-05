package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.List;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;

/**
 * Uniquely identifies an agent using a path from the simulation consisting of a
 * set of species names and index in these species of the different hosts and
 * the agent
 * 
 * @author drogoul
 *
 */

public class AgentIdentifier {

	final String[] species;
	final Integer[] indexes;

	/**
	 * Builds an agent identifier from an agent
	 * 
	 * @param agent
	 */

	private AgentIdentifier(final IAgent agent) {
		// agent is never null (see AgentIdentifier#of())
		IAgent currentAgent = agent;
		final IScope scope = agent.getScope();
		final ITopLevelAgent root = scope.getRoot();
		final List<String> specs = new ArrayList<>();
		final List<Integer> inds = new ArrayList<>();
		while (true) {
			specs.add(currentAgent.getSpeciesName());
			inds.add(currentAgent.getIndex());
			if (currentAgent == root)
				break;
			currentAgent = currentAgent.getPopulation().getHost();
		}
		species = specs.toArray(new String[0]);
		indexes = inds.toArray(new Integer[0]);

	}

	/**
	 * Returns the agent identified by this identifier
	 * 
	 * @param scope
	 * @return
	 */
	public IAgent getAgent(final IScope scope) {
		final ITopLevelAgent root = scope.getRoot();
		IPopulation pop = root.getPopulation();
		IAgent currentAgent = pop.getAgent(indexes[indexes.length - 1]);
		if (species.length == 1)
			return currentAgent;

		for (int i = species.length - 2; i >= 0; i--) {
			pop = currentAgent.getPopulationFor(species[i]);
			currentAgent = pop.getAgent(indexes[i]);
		}
		return currentAgent;

	}

	public static AgentIdentifier of(final IAgent agent) {
		if (agent == null)
			return null;
		return new AgentIdentifier(agent);
	}

	public Integer getIndex() {
		return indexes[0];
	}

}
