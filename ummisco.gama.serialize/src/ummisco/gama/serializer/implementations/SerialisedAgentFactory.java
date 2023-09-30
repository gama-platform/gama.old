/*******************************************************************************************************
 *
 * SerialisedAgentFactory.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;

/**
 * A factory for creating SerialisedAgent objects.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisedAgentFactory implements ISerialisationConstants {

	static {
		DEBUG.ON();
	}

	/**
	 * Save simulation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @return the serialised agent
	 * @date 8 août 2023
	 */
	public static SerialisedAgent createFor(final SimulationAgent sim) {
		SerialisedAgent result = new SerialisedAgent(sim);
		result.attributes().put(HEADER_KEY, new SerialisedSimulationHeader(sim));
		if (sim.serializeHistory()) {
			result.attributes().put(HISTORY_KEY, sim.getHistory());
			result.attributes().put(NODE_KEY, sim.getCurrentHistoryNode());
		}
		return result;
	}

	/**
	 * Creates a new SerialisedAgent object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the agent
	 * @return the serialised agent
	 * @date 8 août 2023
	 */
	public static SerialisedAgent createFor(final IAgent agent) {
		if (agent instanceof SimulationAgent sa) return createFor(sa);
		return new SerialisedAgent(agent);
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
		for (Map.Entry<Integer, SerialisedAgent> entry : images.entrySet()) {
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
	 * Restore grid.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param pop
	 *            the pop
	 * @param sp
	 *            the sp
	 * @date 27 août 2023
	 */
	public static void restoreGrid(final IScope scope, final IPopulation<? extends IAgent> pop,
			final SerialisedGrid sp) {
		GridPopulation grid = (GridPopulation) pop;
		grid.setGrid(sp.matrix());
		for (SerialisedAgent a : sp.agents()) {
			IAgent agent = pop.getAgent(a.getIndex());
			a.attributes().forEach((name, v) -> {
				// Object o = agent.getDirectVarValue(scope, name);
				// if (!Objects.equal(o, v)) { DEBUG.OUT("Difference found in " + a.getIndex()); }
				agent.setDirectVarValue(scope, name, v);

			});
		}

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
	@SuppressWarnings ("unchecked")
	public static void restoreAgent(final IScope scope, final IAgent agent, final SerialisedAgent image) {

		// Update attributes and micropopulations
		image.attributes().forEach((name, v) -> {
			if (agent instanceof IMacroAgent host && v instanceof ISerialisedPopulation sp) {
				IPopulation<? extends IAgent> pop = host.getMicroPopulation(name);
				if (pop != null) {
					if (sp.isGrid()) {
						restoreGrid(scope, pop, (SerialisedGrid) sp);
					} else {
						restorePopulation(scope, pop, (SerialisedPopulation) sp);
					}
				}
			} else {
				agent.setDirectVarValue(scope, name, v);
			}
		});
		// Update simulation-specific variables
		if (agent instanceof SimulationAgent sim) {
			final Map<String, Object> attr = image.attributes();
			Double seedValue = (Double) attr.remove(IKeyword.SEED);
			String rngValue = (String) attr.remove(IKeyword.RNG);
			Integer usageValue = (Integer) attr.remove(SimulationAgent.USAGE);
			sim.setRandomGenerator(new RandomUtils(seedValue, rngValue));
			sim.setUsage(usageValue);
			// Update Clock
			final Integer cycle = (Integer) sim.getAttribute(SimulationAgent.CYCLE);
			sim.getClock().setCycleNoCheck(cycle);
			// Retrieve history
			if (attr.containsKey(HISTORY_KEY)) {
				sim.setHistory((GamaTree<byte[]>) attr.remove(HISTORY_KEY));
				sim.setCurrentHistoryNode((GamaNode<byte[]>) attr.remove(NODE_KEY));
			}

		}
	}

}
