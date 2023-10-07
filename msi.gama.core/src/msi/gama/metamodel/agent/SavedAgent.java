/*******************************************************************************************************
 *
 * SavedAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.GamaShapeFactory;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;

/**
 * A helper class to save agent and restore/recreate agent as a member of a population.
 */
@SuppressWarnings ("unchecked")
public class SavedAgent extends GamaMap<String, Object> implements ISerialisedAgent {

	/** Variables which are not saved during the capture and release process. */
	static final List<String> UNSAVABLE_VARIABLES = Arrays.asList(IKeyword.PEERS, IKeyword.AGENTS, IKeyword.HOST,
			IKeyword.TOPOLOGY, IKeyword.MEMBERS, "populations", IKeyword.INDEX);

	/** The index. */
	int index;

	/** The inner populations. */
	Map<String, List<SavedAgent>> innerPopulations;

	@Override
	public final SavedAgent clone() {
		final SavedAgent result = new SavedAgent();
		result.putAll(this);
		result.innerPopulations = innerPopulations;
		result.index = index;
		return result;
	}

	/**
	 * Instantiates a new saved agent.
	 */
	protected SavedAgent() {
		super(11, Types.STRING, Types.NO_TYPE);
	}

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public SavedAgent(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		this();
		index = agent.getIndex();
		saveAttributes(scope, agent);
		if (agent instanceof IMacroAgent) { saveMicroAgents(scope, (IMacroAgent) agent); }
	}

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param ind
	 *            the ind
	 * @param v
	 *            the v
	 * @param inPop
	 *            the in pop
	 */
	public SavedAgent(final int ind, final Map<String, Object> v, final Map<String, List<SavedAgent>> inPop) {
		this(v, inPop);
		index = ind;
	}

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param v
	 *            the v
	 * @param inPop
	 *            the in pop
	 */
	public SavedAgent(final Map<String, Object> v, final Map<String, List<SavedAgent>> inPop) {
		super(v.size(), Types.STRING, Types.NO_TYPE);
		putAll(v);
		innerPopulations = inPop;
	}

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param map
	 *            the map
	 */
	public SavedAgent(final IMap<String, Object> map) {
		this();
		putAll(map);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @param attrName
	 *            the attr name
	 * @return the attribute value
	 */
	@Override
	public Object getAttributeValue(final String attrName) {
		return get(attrName);
	}

	@Override
	public void setAttributeValue(final String attrName, final Object val) {
		put(attrName, val);
	}

	/**
	 * Gets the variables.
	 *
	 * @return the variables
	 */
	public Map<String, Object> getVariables() { return this; }

	/**
	 * Gets the inner populations.
	 *
	 * @return the inner populations
	 */
	public Map<String, List<SavedAgent>> getInnerPopulations() { return innerPopulations; }

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	@Override
	public int getIndex() { return index; }

	/**
	 * Saves agent's attributes to a map.
	 *
	 * @param agent
	 * @throws GamaRuntimeException
	 */
	private void saveAttributes(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final ISpecies species = agent.getSpecies();
		for (final String specVar : species.getVarNames()) {
			if (UNSAVABLE_VARIABLES.contains(specVar)
					|| species.getVar(specVar).value(scope, agent) instanceof IPopulation) {
				continue;
			}

			if (IKeyword.SHAPE.equals(specVar)) {
				// variables.put(specVar, geometry.copy());
				// Changed 3/2/12: is it necessary to make the things below ?
				// variables.put(specVar,
				// GamaShapeFactory.createFrom(agent.getGeometry()));
				// Changed 26/5/16: additional variables exist in the shape. To
				// keep them in the SavedAgent, we add them by hand in the
				// geometry.
				// We cannot keep all the GamaShape, because it contains
				// populations too.
				final GamaShape shape = GamaShapeFactory
						.createFrom(((IShape) species.getVar(specVar).value(scope, agent)).getInnerGeometry());

				// if (agent.getAttributes() != null) {}

				agent.forEachAttribute((attrName, val) -> {
					if (UNSAVABLE_VARIABLES.contains(attrName) || species.getVarNames().contains(attrName)
							|| val instanceof IPopulation)
						return true;
					shape.setAttribute(attrName, val);
					return true;
				});
				// for (final String keyAttr : agent.getAttributes().keySet()) {
				// final String attrName = keyAttr;
				// if (UNSAVABLE_VARIABLES.contains(attrName)) {
				// continue;
				// }
				// if (species.getVarNames().contains(attrName)) {
				// continue;
				// }
				//
				// if (agent.getAttribute(keyAttr) instanceof IPopulation) {
				// continue;
				// }
				// shape.setAttribute(attrName, agent.getAttribute(keyAttr));
				// }

				put(specVar, shape);

				continue;
			}
			put(specVar, species.getVar(specVar).value(scope, agent));
		}
	}

	/**
	 * Recursively save micro-agents of an agent.
	 *
	 * @param agent
	 *            The agent having micro-agents to be saved.
	 * @throws GamaRuntimeException
	 */
	private void saveMicroAgents(final IScope scope, final IMacroAgent agent) throws GamaRuntimeException {
		innerPopulations = GamaMapFactory.create();

		for (final IPopulation<? extends IAgent> microPop : agent.getMicroPopulations()) {
			final List<SavedAgent> savedAgents = new ArrayList<>();
			final Iterator<? extends IAgent> it = microPop.iterator();
			while (it.hasNext()) { savedAgents.add(new SavedAgent(scope, it.next())); }

			innerPopulations.put(microPop.getSpecies().getName(), savedAgents);
		}
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
	public IAgent restoreTo(final IScope scope, final IPopulation<? extends IAgent> targetPopulation)
			throws GamaRuntimeException {
		final List<Map<String, Object>> agentAttrs = new ArrayList<>();
		agentAttrs.add(this);
		final List<? extends IAgent> restoredAgents = targetPopulation.createAgents(scope, 1, agentAttrs, true, true);
		restoreMicroAgents(scope, restoredAgents.get(0));

		return restoredAgents.get(0);
	}

	/**
	 *
	 *
	 * @param host
	 * @throws GamaRuntimeException
	 */
	public void restoreMicroAgents(final IScope scope, final IAgent host) throws GamaRuntimeException {
		if (innerPopulations != null) {
			for (final String microPopName : innerPopulations.keySet()) {
				final IPopulation<? extends IAgent> microPop = ((IMacroAgent) host).getMicroPopulation(microPopName);

				if (microPop != null) {
					final List<SavedAgent> savedMicros = innerPopulations.get(microPopName);
					final List<Map<String, Object>> microAttrs = new ArrayList<>();
					microAttrs.addAll(savedMicros);

					final List<? extends IAgent> microAgents =
							microPop.createAgents(scope, savedMicros.size(), microAttrs, true, true);

					for (int i = 0; i < microAgents.size(); i++) {
						savedMicros.get(i).restoreMicroAgents(scope, microAgents.get(i));
					}
				}
			}
		}
	}
}
