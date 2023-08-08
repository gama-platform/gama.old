/*******************************************************************************************************
 *
 * SerialisedAgentFactory.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import one.util.streamex.StreamEx;

/**
 * A factory for creating SerialisedAgent objects.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisedAgentFactory {

	/**
	 * Restore agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the agent
	 * @param image
	 *            the image
	 * @date 6 août 2023
	 */
	public static void restoreAgent(final IScope scope, final IAgent agent, final SerialisedAgent image) {
		// DEBUG.OUT("Restoring " + agent.getName() + " from " + agent.getOrCreateAttributes() + " to "
		// + image.attributes());
		image.attributes().forEach((name, v) -> {
			if (agent instanceof IMacroAgent host && v instanceof SerialisedPopulation sp) {
				IPopulation<? extends IAgent> pop = host.getMicroPopulation(name);
				if (pop != null) { restorePopulation(scope, pop, sp); }
			} else {
				agent.setDirectVarValue(scope, name, v);
			}
		});
	}

	/**
	 * Restore population.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param pop
	 *            the pop
	 * @param sp
	 *            the sp
	 * @date 6 août 2023
	 */
	public static void restorePopulation(final IScope scope, final IPopulation<? extends IAgent> pop,
			final SerialisedPopulation sp) {
		Map<Integer, IAgent> agents = StreamEx.of(pop).toMap(IAgent::getIndex, each -> each);
		Map<Integer, SerialisedAgent> images = StreamEx.of(sp.agents()).toMap(SerialisedAgent::getIndex, each -> each);
		Set<Entry<Integer, SerialisedAgent>> imagesEntries = images.entrySet();
		for (Map.Entry<Integer, SerialisedAgent> entry : imagesEntries) {
			int index = entry.getKey();
			// We gather the corresponding agent and remove it from this temp map
			IAgent agent = agents.remove(index);
			// If the agent is not found we create a new one
			if (agent == null) { agent = pop.getOrCreateAgent(scope, index); }
			restoreAgent(scope, agent, entry.getValue());
		}
		// The remaining agents in the map are killed
		agents.forEach((i, a) -> { a.primDie(scope); });
	}

	/**
	 * Restore simulation. Take care of restoring seed, rnd, usage, and cycle
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param sim
	 *            the sim
	 * @param image
	 *            the image
	 * @date 8 août 2023
	 */
	public static void restoreSimulation(final IScope scope, final SimulationAgent sim, final SerialisedAgent image) {
		final Map<String, Object> attr = image.attributes();
		Double seedValue = (Double) attr.remove(IKeyword.SEED);
		String rngValue = (String) attr.remove(IKeyword.RNG);
		Integer usageValue = (Integer) attr.remove(SimulationAgent.USAGE);
		// Update Attributes and micropopulations
		SerialisedAgentFactory.restoreAgent(scope, sim, image);
		// Update RNG
		sim.setRandomGenerator(new RandomUtils(seedValue, rngValue));
		sim.setUsage(usageValue);
		// Update Clock
		final Integer cycle = (Integer) sim.getAttribute(SimulationAgent.CYCLE);
		sim.getClock().setCycle(cycle);
	}

}
