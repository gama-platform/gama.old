/*******************************************************************************************************
 *
 * SerialisedPopulation.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.population;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.ISerialisedAgent;
import msi.gama.metamodel.agent.SerialisedAgent;
import msi.gama.runtime.IScope;
import one.util.streamex.StreamEx;

/**
 * The Class SerialisedPopulation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 31 juil. 2023
 */
public record SerialisedPopulation(String speciesName, List<ISerialisedAgent> agents) implements ISerialisedPopulation {

	/**
	 * Instantiates a new population proxy. This is where the serialised agents are created
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param pop
	 *            the pop
	 * @date 31 juil. 2023
	 */
	public SerialisedPopulation(final IPopulation<? extends IAgent> pop) {
		this(pop.getSpecies().getName(), new ArrayList<>());
		for (IAgent a : pop) { agents.add(SerialisedAgent.of(a, true)); }
	}

	/**
	 * Restore as.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param pop
	 *            the pop
	 * @date 31 oct. 2023
	 */
	public void restoreAs(final IScope scope, final IPopulation<? extends IAgent> pop) {

		Map<Integer, IAgent> agents = StreamEx.of(pop).toMap(IAgent::getIndex, each -> each);
		Map<Integer, ISerialisedAgent> images = StreamEx.of(agents()).toMap(ISerialisedAgent::getIndex, each -> each);
		for (Map.Entry<Integer, ISerialisedAgent> entry : images.entrySet()) {
			int index = entry.getKey();
			// We gather the corresponding agent and remove it from this temp map
			IAgent agent = agents.remove(index);
			// If the agent is not found we create a new one
			if (agent == null) { agent = pop.getOrCreateAgent(scope, index); }
			entry.getValue().restoreAs(scope, agent);
		}
		// The remaining agents in the map are killed
		agents.forEach((i, a) -> { a.primDie(scope); });
		scope.getAndClearDeathStatus();

	}

}
