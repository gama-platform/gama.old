/*******************************************************************************************************
 *
 * SerialisedAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.ISerialisedPopulation;
import msi.gama.metamodel.population.SerialisedGrid;
import msi.gama.metamodel.population.SerialisedPopulation;
import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.metamodel.topology.grid.IGridAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonObject;
import msi.gama.util.file.json.JsonValue;

/**
 * The Class SerialisedAgent.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 31 juil. 2023
 */
public record SerialisedAgent(int index, String species, Map<String, Object> attributes,
		Map<String, ISerialisedPopulation> innerPopulations) implements ISerialisedAgent {

	/** The Constant KEY. */
	public static final String HISTORY_KEY = "**history**";

	/** The node key. */
	public static final String SERIALISE_HISTORY = "**serialise_history**";

	/**
	 * Instantiates a new agent proxy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param target
	 *            the target
	 * @date 31 juil. 2023
	 */
	public SerialisedAgent(final IAgent target) {
		this(target.getIndex(), target.getSpeciesName(),
				filterAttributes(target, target instanceof IGridAgent, target.getAttributes(true)),
				filterPopulations(target, target instanceof IGridAgent, target.getAttributes(true)));
	}

	/**
	 * Filter populations.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param target
	 *            the target
	 * @param b
	 *            the b
	 * @param m
	 *            the m
	 * @return the map
	 * @date 29 oct. 2023
	 */
	private static Map<String, ISerialisedPopulation> filterPopulations(final IAgent target, final boolean b,
			final Map<String, Object> m) {
		Map<String, ISerialisedPopulation> map = new HashMap<>();
		for (Map.Entry<String, Object> entry : m.entrySet()) {
			Object v = entry.getValue();
			if (v instanceof IPopulation p) {
				map.put(entry.getKey(),
						p.isGrid() ? new SerialisedGrid((GridPopulation) p) : new SerialisedPopulation(p));
			}
		}
		return map;
	}

	/**
	 * Gets the index.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the index
	 * @date 6 août 2023
	 */
	@Override
	public int getIndex() { return index; }

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
			Object v = entry.getValue();
			if (Json.NON_SERIALISABLE.contains(k) || isGrid && Json.GRID_NON_SERIALISABLE.contains(k)
					|| v instanceof IPopulation) {
				continue;
			}
			map.put(k, v);
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

	/**
	 * @param scope
	 *            Restores the saved agent as a member of the target population.
	 *
	 * @param targetPopulation
	 *            The population that the saved agent will be restored to.
	 * @return
	 * @throws GamaRuntimeException
	 */
	@Override
	public IAgent restoreInto(final IScope scope, final IPopulation<? extends IAgent> targetPopulation)
			throws GamaRuntimeException {
		final List<Map<String, Object>> agentAttrs = new ArrayList<>();
		agentAttrs.add(attributes);
		final List<? extends IAgent> restoredAgents = targetPopulation.createAgents(scope, 1, agentAttrs, true, true);
		IAgent result = restoredAgents.get(0);
		restoreAs(scope, result);
		return result;
	}

	/**
	 * Restore as.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @return the i agent
	 * @date 31 oct. 2023
	 */
	@Override
	public void restoreAs(final IScope scope, final IAgent agent) {
		// Update attributes and micropopulations
		attributes().forEach((name, v) -> { agent.setDirectVarValue(scope, name, v); });
		if (agent instanceof IMacroAgent host && innerPopulations != null) {
			innerPopulations.forEach((name, v) -> {
				IPopulation<? extends IAgent> pop = host.getMicroPopulation(name);
				if (pop != null) {
					if (v instanceof SerialisedGrid sg) {
						sg.restoreAs(scope, pop);
					} else if (v instanceof SerialisedPopulation sp) { sp.restoreAs(scope, pop); }
				}
			});
			// Update simulation-specific variables
			if (agent instanceof SimulationAgent sim) {
				final Map<String, Object> attr = attributes();
				Double seedValue = (Double) attr.remove(IKeyword.SEED);
				String rngValue = (String) attr.remove(IKeyword.RNG);
				Integer usageValue = (Integer) attr.remove(SimulationAgent.USAGE);
				sim.setRandomGenerator(new RandomUtils(seedValue, rngValue));
				sim.setUsage(usageValue);
				// Update Clock
				final Integer cycle = (Integer) sim.getAttribute(SimulationAgent.CYCLE);
				sim.getClock().setCycleNoCheck(cycle);

			}
		}
	}

	@Override
	public JsonValue serializeToJson(final Json json) {
		JsonObject obj = json.agent(species).add("index", index).add("attributes", attributes);
		if (innerPopulations != null && !innerPopulations.isEmpty()) { obj.add("populations", innerPopulations); }
		return obj;
	}

	/**
	 * Recreate in.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return the i agent
	 * @date 30 oct. 2023
	 */
	public IAgent recreateIn(final IScope scope) {
		IPopulation p = scope.getSimulation().getPopulationFor(species);
		if (p == null)
			throw GamaRuntimeException.error("No population named" + species + " exist in this simulation", scope);
		return restoreInto(scope, p);
	}

	/**
	 * Should serialize history.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @return true, if successful
	 * @date 22 oct. 2023
	 */
	static boolean shouldSerializeHistory(final SimulationAgent sim) {
		return sim.hasAttribute(SERIALISE_HISTORY) && (Boolean) sim.getAttribute(SERIALISE_HISTORY);
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
		if (!shouldSerializeHistory(sim)) { result.attributes().remove(HISTORY_KEY); }
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

}
