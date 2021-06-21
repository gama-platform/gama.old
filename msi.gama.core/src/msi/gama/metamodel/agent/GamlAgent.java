/*******************************************************************************************************
 *
 * msi.gama.metamodel.agent.GamlAgent.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.MetaPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;

/**
 * The Class GamlAgent. Represents agents that can be manipulated in GAML. They are provided with everything their
 * species defines .
 */
@SuppressWarnings ("unchecked")
public class GamlAgent extends MinimalAgent implements IMacroAgent {

	// hqnghi manipulate micro-models AD put it to null to have lazy
	// initialization (saves some bytes in each agent)
	protected IMap<String, IPopulation<? extends IAgent>> externMicroPopulations;
	// Added to optimize the traversal of "non-minimal" agents that contain
	// micropopulations
	protected IPopulation<? extends IAgent>[] microPopulations;
	static final IPopulation<? extends IAgent>[] NO_POP = new IPopulation[0];

	// end-hqnghi

	/**
	 * @param s
	 *            the population used to prototype the agent.
	 */
	public GamlAgent(final IPopulation<? extends IAgent> s, final int index) {
		super(s, index);
	}

	/**
	 * @param gridPopulation
	 * @param geometry
	 */
	public GamlAgent(final IPopulation<? extends IAgent> gridPopulation, final int index, final IShape geometry) {
		super(gridPopulation, index, geometry);
	}

	private Boolean isPopulation(final String populationName) {
		final IVariable v = getSpecies().getVar(populationName);
		if (v == null) { return false; }
		return v.isMicroPopulation();
	}

	@Override
	public IPopulation<? extends IAgent>[] getMicroPopulations() {
		if (microPopulations == null) {
			final List<IPopulation<?>> pops = new ArrayList<>();
			forEachAttribute((s, o) -> {
				if (isPopulation(s)) {
					pops.add((IPopulation<?>) o);
				}
				return true;
			});
			microPopulations = pops.toArray(new IPopulation[pops.size()]);
			if (microPopulations.length == 0) {
				microPopulations = NO_POP;
			} else {
				Arrays.sort(microPopulations, (p1, p2) -> p1.isGrid() ? p2.isGrid() ? 0 : 1 : p2.isGrid() ? -1 : 0);
			}
		}
		return microPopulations;
	}

	protected boolean initSubPopulations(final IScope scope) {
		for (final IPopulation<? extends IAgent> pop : getMicroPopulations()) {
			if (!scope.init(pop).passed()) { return false; }
		}
		return true;
	}

	@Override
	protected boolean stepSubPopulations(final IScope scope) {
		for (final IPopulation<? extends IAgent> pop : getMicroPopulations()) {
			if (!scope.step(pop).passed()) { return false; }
		}
		return true;
	}

	@Override
	public IList<IAgent> captureMicroAgents(final IScope scope, final ISpecies microSpecies,
			final IList<IAgent> microAgents) throws GamaRuntimeException {
		if (microAgents == null || microAgents.isEmpty() || microSpecies == null
				|| !this.getSpecies().getMicroSpecies().contains(microSpecies)) {
			return GamaListFactory.EMPTY_LIST;
		}

		try (final ICollector<IAgent> candidates = Collector.getList();
				final Collector.AsList<IAgent> capturedAgents = Collector.getList()) {
			for (final IAgent a : microAgents.iterable(scope)) {
				if (this.canCapture(a, microSpecies)) {
					candidates.add(a);
				}
			}
			final IPopulation<? extends IAgent> microSpeciesPopulation = this.getPopulationFor(microSpecies);
			for (final IAgent micro : candidates) {
				final SavedAgent savedMicro = new SavedAgent(scope, micro);
				micro.dispose();
				capturedAgents.add(savedMicro.restoreTo(scope, microSpeciesPopulation));
			}
			return capturedAgents.items();
		}
	}

	@Override
	public IAgent captureMicroAgent(final IScope scope, final ISpecies microSpecies, final IAgent microAgent)
			throws GamaRuntimeException {
		if (this.canCapture(microAgent, microSpecies)) {
			final IPopulation<? extends IAgent> microSpeciesPopulation = this.getMicroPopulation(microSpecies);
			final SavedAgent savedMicro = new SavedAgent(scope, microAgent);
			microAgent.dispose();
			return savedMicro.restoreTo(scope, microSpeciesPopulation);
		}

		return null;
	}

	@Override
	public IList<IAgent> releaseMicroAgents(final IScope scope, final IList<IAgent> microAgents)
			throws GamaRuntimeException {
		IPopulation<? extends IAgent> originalSpeciesPopulation;
		final IList<IAgent> releasedAgents = GamaListFactory.create(Types.AGENT);

		for (final IAgent micro : microAgents.iterable(scope)) {
			final SavedAgent savedMicro = new SavedAgent(scope, micro);
			originalSpeciesPopulation = micro.getPopulationFor(micro.getSpecies().getParentSpecies());
			micro.dispose();
			releasedAgents.add(savedMicro.restoreTo(scope, originalSpeciesPopulation));
		}
		return releasedAgents;
	}

	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's species.
	 *
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	@Override
	public IList<IAgent> migrateMicroAgents(final IScope scope, final IList<IAgent> microAgents,
			final ISpecies newMicroSpecies) {
		final List<IAgent> immigrantCandidates = GamaListFactory.create(Types.AGENT);

		for (final IAgent m : microAgents.iterable(scope)) {
			if (m.getSpecies().isPeer(newMicroSpecies)) {
				immigrantCandidates.add(m);
			}
		}

		final IList<IAgent> immigrants = GamaListFactory.create(Types.AGENT);
		if (!immigrantCandidates.isEmpty()) {
			final IPopulation<? extends IAgent> microSpeciesPopulation = this.getPopulationFor(newMicroSpecies);
			for (final IAgent micro : immigrantCandidates) {
				final SavedAgent savedMicro = new SavedAgent(scope, micro);
				micro.dispose();
				immigrants.add(savedMicro.restoreTo(scope, microSpeciesPopulation));
			}
		}

		return immigrants;
	}

	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's species.
	 *
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	@Override
	public IList<IAgent> migrateMicroAgents(final IScope scope, final ISpecies oldMicroSpecies,
			final ISpecies newMicroSpecies) {
		final IPopulation<? extends IAgent> oldMicroPop = this.getPopulationFor(oldMicroSpecies);

		final IPopulation<? extends IAgent> newMicroPop = this.getPopulationFor(newMicroSpecies);
		final IList<IAgent> immigrants = GamaListFactory.create(Types.AGENT);
		// final Iterator<IAgent> it = oldMicroPop.iterator();
		while (!oldMicroPop.isEmpty()) {
			// while (it.hasNext()) {
			final IAgent m = oldMicroPop.get(0);
			final SavedAgent savedMicro = new SavedAgent(scope, m);
			m.dispose();
			immigrants.add(savedMicro.restoreTo(scope, newMicroPop));

		}

		return immigrants;
	}

	@Override
	public void initializeMicroPopulation(final IScope scope, final String name) {
		final ISpecies microSpec = getModel().getSpecies(name);
		final IPopulation<? extends IAgent> microPop = GamaPopulation.createPopulation(scope, this, microSpec);
		registerMicropopulation(scope, microSpec, microPop);
	}
	
	protected void registerMicropopulation(IScope scope, ISpecies microSpec, IPopulation<? extends IAgent> microPop) {
		setAttribute(microSpec.getName(), microPop);
		microPop.initializeFor(scope);
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public void dispose() {
		if (dead) { return; }
		final IPopulation[] microPops = getMicroPopulations();
		for (final IPopulation pop : microPops) {
			pop.dispose();
		}

		final Object graph = getAttribute("attached_graph");
		if (graph instanceof GamaGraph) {
			((GamaGraph) graph).disposeVertex(this);

		}
		super.dispose();
	}

	@Override
	public synchronized IPopulation<? extends IAgent> getMicroPopulation(final String microSpeciesName) {
		final Object o = getAttribute(microSpeciesName);
		if (o instanceof IPopulation) { return (IPopulation<? extends IAgent>) o; }
		return null;
	}

	@Override
	public IPopulation<? extends IAgent> getMicroPopulation(final ISpecies microSpecies) {
		final Object o = getAttribute(microSpecies.getName());
		return o instanceof IPopulation ? (IPopulation<IAgent>) o : null;
	}

	@Override
	public boolean hasMembers() {
		if (dead()) { return false; }
		for (final IPopulation pop : getMicroPopulations()) {
			if (pop.size() > 0) { return true; }
		}
		return false;
	}

	@Override
	public IContainer<?, IAgent> getMembers(final IScope scope) {
		if (dead()) { return GamaListFactory.EMPTY_LIST; }
		final MetaPopulation mp = new MetaPopulation(getMicroPopulations());
		// forEachAttribute((s, pop) -> {
		// if (pop instanceof IPopulation && ((IPopulation) pop).size() > 0) {
		// mp.addPopulation((IPopulation<? extends IAgent>) pop);
		// }
		// return true;
		// });

		return mp;
	}

	@Override
	public void setMembers(final IList<IAgent> newMembers) {
		// Directly changing "members" not supported
	}

	/*
	 * Returns the number of agents for which this agent is the direct host
	 */
	@Override
	public int getMembersSize(final IScope scope) {
		return getMembers(scope).length(scope);
		// final int[] result = { 0 };
		// forEachAttribute((s, pop) -> {
		// if (pop instanceof IPopulation) {
		// result[0] += ((IPopulation<? extends IAgent>) pop).length(scope);
		// }
		// return true;
		// });
		// return result[0];
	}

	@Override
	public void setAgents(final IList<IAgent> agents) {
		// "agents" is read-only attribute
	}

	@Override
	public IList<IAgent> getAgents(final IScope scope) {
		if (!hasMembers()) { return GamaListFactory.EMPTY_LIST; }

		final IContainer<?, IAgent> members = getMembers(scope);
		final IList<IAgent> agents = GamaListFactory.create(Types.AGENT);
		agents.addAll(members.listValue(scope, Types.NO_TYPE, false));
		for (final IAgent m : members.iterable(scope)) {
			if (m instanceof IMacroAgent) {
				agents.addAll(((IMacroAgent) m).getAgents(scope));
			}
		}

		return agents;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final ISpecies species) {
		// hqnghi adjust to get population for species which come from main as
		// well micro models
		final ModelDescription micro = species.getDescription().getModelDescription();
		final ModelDescription main = (ModelDescription) this.getModel().getDescription();
		IPopulation<? extends IAgent> microPopulation = null;
		if (main.getMicroModel(micro.getAlias()) == null) {
			microPopulation = this.getMicroPopulation(species);
			if (microPopulation == null && getHost() != null) {
				microPopulation = getHost().getPopulationFor(species);
			}
		} else {
			microPopulation = this.getScope().getSimulation()
					.getExternMicroPopulationFor(micro.getAlias() + "." + species.getName());
		}
		// end-hqnghi
		return microPopulation;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final String speciesName) {
		final IPopulation<? extends IAgent> microPopulation = this.getMicroPopulation(speciesName);
		if (microPopulation == null && getHost() != null) { return getHost().getPopulationFor(speciesName); }
		return microPopulation;
	}

	/**
	 * Verifies if this agent can capture other agent as newSpecies.
	 *
	 * @return true if the following conditions are correct: 1. newSpecies is one micro-species of this agent's species;
	 *         2. newSpecies is a sub-species of this agent's species or other species is a sub-species of this agent's
	 *         species; 3. the "other" agent is not macro-agent of this agent; 4. the "other" agent is not a micro-agent
	 *         of this agent.
	 */
	@Override
	public boolean canCapture(final IAgent other, final ISpecies newSpecies) {
		if (other == null || other.dead() || newSpecies == null || !this.getSpecies().containMicroSpecies(newSpecies)) {
			return false;
		}
		if (this.getMacroAgents().contains(other)) { return false; }
		return !other.getHost().equals(this);
	}

	@Override
	public void addExternMicroPopulation(final String expName, final IPopulation<? extends IAgent> pop) {
		if (externMicroPopulations == null) {
			externMicroPopulations = GamaMapFactory.create(Types.STRING, Types.LIST.of(Types.AGENT));
		}
		externMicroPopulations.put(expName, pop);
	}

	@Override
	public IPopulation<? extends IAgent> getExternMicroPopulationFor(final String expName) {
		if (externMicroPopulations != null) { return externMicroPopulations.get(expName); }
		return null;
	}
	//
	// @Override
	// public GamaMap<String, IPopulation<? extends IAgent>>
	// getExternMicroPopulations() {
	// if (externMicroPopulations == null) {
	// return GamaMapFactory.create();
	// }
	// return externMicroPopulations;
	// }

	// @Override
	// public int getNbAgents() {
	// return nbSubAgents;
	// }
	//
	// @Override
	// public void addSubAgents(final int nb) {
	// nbSubAgents += nb;
	// }
	//
	// @Override
	// public void removeAgent() {
	// nbSubAgents--;
	// }

}
