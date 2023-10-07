/*******************************************************************************************************
 *
 * SerialisedAgent.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.ISerialisedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.metamodel.topology.grid.IGridAgent;

/**
 * The Class SerialisedAgent.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 31 juil. 2023
 */
public record SerialisedAgent(/* AgentReference ref */ int index, Map<String, Object> attributes)
		implements ISerialisedAgent {

	/** The non serialisable. */
	static Set<String> NON_SERIALISABLE = Set.of(IKeyword.MEMBERS, IKeyword.AGENTS, IKeyword.LOCATION, IKeyword.HOST,
			IKeyword.PEERS, IKeyword.EXPERIMENT, IKeyword.WORLD_AGENT_NAME, SimulationAgent.TIME,
			PlatformAgent.MACHINE_TIME, SimulationAgent.DURATION, SimulationAgent.AVERAGE_DURATION,
			SimulationAgent.TOTAL_DURATION, IKeyword.INDEX);

	/** The grid non serialisable. */
	static Set<String> GRID_NON_SERIALISABLE = Set.of(IKeyword.GRID_X, IKeyword.GRID_Y, IKeyword.NEIGHBORS);

	/**
	 * Instantiates a new agent proxy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param target
	 *            the target
	 * @date 31 juil. 2023
	 */
	public SerialisedAgent(final IAgent target) {
		this(/* new AgentReference(target), */target.getIndex(),
				filterAttributes(target, target instanceof IGridAgent, target.getAttributes(true)));
	}

	/**
	 * Gets the index.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the index
	 * @date 6 août 2023
	 */
	@Override
	public int getIndex() { return /* ref.getLastIndex(); */ index; }

	/**
	 * Filter map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param m
	 *            the m
	 * @return the object
	 * @date 31 juil. 2023
	 */
	@SuppressWarnings ("unchecked")
	public static Map<String, Object> filterAttributes(final IAgent agent, final boolean isGrid,
			final Map<String, Object> m) {
		Map<String, Object> map = new HashMap<>();
		for (Map.Entry<String, Object> entry : m.entrySet()) {
			String k = entry.getKey();
			if (NON_SERIALISABLE.contains(k) || isGrid && GRID_NON_SERIALISABLE.contains(k)) { continue; }
			Object v = entry.getValue();
			map.put(k, v instanceof IPopulation p
					? p.isGrid() ? new SerialisedGrid((GridPopulation) p) : new SerialisedPopulation(p) : v);
		}
		boolean isSim = agent instanceof SimulationAgent;
		if (isSim) {
			SimulationAgent sim = (SimulationAgent) agent;
			map.put(IKeyword.SEED, sim.getSeed());
			map.put(IKeyword.RNG, sim.getRng());
			map.put(SimulationAgent.USAGE, sim.getUsage());
			map.put(SimulationAgent.CYCLE, sim.getClock().getCycle());
		}
		if (!isGrid) { map.put(IKeyword.SHAPE, agent.getGeometry()); }
		map.put(IKeyword.NAME, agent.getName());
		return map;
	}

	@Override
	public Object getAttributeValue(final String var) {
		return attributes.get(var);
	}

	@Override
	public void setAttributeValue(final String var, final Object val) {
		attributes.put(var, val);
	}

}
