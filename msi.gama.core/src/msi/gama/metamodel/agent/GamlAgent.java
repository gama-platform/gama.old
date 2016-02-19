/*********************************************************************************************
 *
 *
 * 'GamlAgent.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.*;
import com.google.common.collect.Iterables;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;

/**
 * The Class GamlAgent. Represents agents that can be manipulated in GAML. They are provided with
 * everything their species defines .
 */

public class GamlAgent extends MinimalAgent implements IMacroAgent {

	// hqnghi manipulate micro-models AD put it to null to have lazy initialization (saves some bytes in each agent)
	protected GamaMap<String, IPopulation> externMicroPopulations;

	// end-hqnghi

	/**
	 * @param s the population used to prototype the agent.
	 */
	public GamlAgent(final IPopulation s) {
		super(s);
	}

	/**
	 * @param gridPopulation
	 * @param geometry
	 */
	public GamlAgent(final IPopulation pop, final IShape geometry) {
		super(pop, geometry);
	}

	@Override
	protected Object stepSubPopulations(final IScope scope) {
		if ( getAttributes() == null ) { return this; }
		for ( Object pop : getAttributes().values().toArray() ) {
			if ( pop instanceof IPopulation ) {
				scope.step((IPopulation) pop);
			}
		}
		return this;
	}

	@Override
	public IList<IAgent> captureMicroAgents(final IScope scope, final ISpecies microSpecies,
		final IList<IAgent> microAgents) throws GamaRuntimeException {
		if ( microAgents == null || microAgents.isEmpty() || microSpecies == null ||
			!this.getSpecies().getMicroSpecies().contains(microSpecies) ) { return GamaListFactory.EMPTY_LIST; }

		final List<IAgent> candidates = GamaListFactory.create(Types.AGENT);
		for ( final IAgent a : microAgents.iterable(scope) ) {
			if ( this.canCapture(a, microSpecies) ) {
				candidates.add(a);
			}
		}
		final IList<IAgent> capturedAgents = GamaListFactory.create(Types.AGENT);
		final IPopulation microSpeciesPopulation = this.getPopulationFor(microSpecies);
		for ( final IAgent micro : candidates ) {
			final SavedAgent savedMicro = new SavedAgent(scope, micro);
			micro.dispose();
			capturedAgents.add(savedMicro.restoreTo(scope, microSpeciesPopulation));
		}
		return capturedAgents;
	}

	@Override
	public IAgent captureMicroAgent(final IScope scope, final ISpecies microSpecies, final IAgent microAgent)
		throws GamaRuntimeException {
		if ( this.canCapture(microAgent, microSpecies) ) {
			final IPopulation microSpeciesPopulation = this.getMicroPopulation(microSpecies);
			final SavedAgent savedMicro = new SavedAgent(scope, microAgent);
			microAgent.dispose();
			return savedMicro.restoreTo(scope, microSpeciesPopulation);
		}

		return null;
	}

	@Override
	public IList<IAgent> releaseMicroAgents(final IScope scope, final IList<IAgent> microAgents)
		throws GamaRuntimeException {
		IPopulation originalSpeciesPopulation;
		final IList<IAgent> releasedAgents = GamaListFactory.create(Types.AGENT);

		for ( final IAgent micro : microAgents.iterable(scope) ) {
			final SavedAgent savedMicro = new SavedAgent(scope, micro);
			originalSpeciesPopulation = micro.getPopulationFor(micro.getSpecies().getParentSpecies());
			micro.dispose();
			releasedAgents.add(savedMicro.restoreTo(scope, originalSpeciesPopulation));
		}
		return releasedAgents;
	}

	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's
	 * species.
	 *
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	@Override
	public IList<IAgent> migrateMicroAgents(final IScope scope, final IList<IAgent> microAgents,
		final ISpecies newMicroSpecies) {
		final List<IAgent> immigrantCandidates = GamaListFactory.create(Types.AGENT);

		for ( final IAgent m : microAgents.iterable(scope) ) {
			if ( m.getSpecies().isPeer(newMicroSpecies) ) {
				immigrantCandidates.add(m);
			}
		}

		final IList<IAgent> immigrants = GamaListFactory.create(Types.AGENT);
		if ( !immigrantCandidates.isEmpty() ) {
			final IPopulation microSpeciesPopulation = this.getPopulationFor(newMicroSpecies);
			for ( final IAgent micro : immigrantCandidates ) {
				final SavedAgent savedMicro = new SavedAgent(scope, micro);
				micro.dispose();
				immigrants.add(savedMicro.restoreTo(scope, microSpeciesPopulation));
			}
		}

		return immigrants;
	}

	/**
	 * Migrates some micro-agents from one micro-species to another micro-species of this agent's
	 * species.
	 *
	 * @param microAgent
	 * @param newMicroSpecies
	 * @return
	 */
	@Override
	public IList<IAgent> migrateMicroAgents(final IScope scope, final ISpecies oldMicroSpecies,
		final ISpecies newMicroSpecies) {
		final IPopulation oldMicroPop = this.getPopulationFor(oldMicroSpecies);

		final IPopulation newMicroPop = this.getPopulationFor(newMicroSpecies);
		final IList<IAgent> immigrants = GamaListFactory.create(Types.AGENT);
		// final Iterator<IAgent> it = oldMicroPop.iterator();
		while (!oldMicroPop.isEmpty()) {
			// while (it.hasNext()) {
			IAgent m = oldMicroPop.get(0);
			final SavedAgent savedMicro = new SavedAgent(scope, m);
			m.dispose();
			immigrants.add(savedMicro.restoreTo(scope, newMicroPop));

		}

		return immigrants;
	}

	@Override
	public void initializeMicroPopulation(final IScope scope, final String name) {
		final ISpecies microSpec = getModel().getSpecies(name);
		final IPopulation microPop = GamaPopulation.createPopulation(scope, this, microSpec);
		// System.out.println("Micro-pop added to attributes: " + name);
		setAttribute(microSpec.getName(), microPop);
		microPop.initializeFor(scope);
	}

	@Override
	public void dispose() {
		if ( dead() ) { return; }
		// scope.getGui().debug(this.getClass().getSimpleName() + " " + getName() + " .dispose (in GamlAgent)");
		try {
			// acquireLock();
			if ( getAttributes() != null ) {
				for ( final Map.Entry<Object, Object> entry : getAttributes().entrySet() ) {
					if ( entry.getValue() instanceof IPopulation ) {
						final IPopulation microPop = (IPopulation) entry.getValue();
						// microPop.killMembers();
						microPop.dispose();
					}
				}
			}
			final GamaGraph graph = (GamaGraph) getAttribute("attached_graph");
			if ( graph != null ) {

				final Set edgesToModify = graph.edgesOf(this);
				graph.removeVertex(this);

				for ( final Object obj : edgesToModify ) {
					if ( obj instanceof IAgent ) {
						((IAgent) obj).dispose();
					}
				}
			}
		} finally {
			// releaseLock();
		}
		super.dispose();
	}

	// @Override
	// public void hostChangesShape() {
	// setLocation(new GamaPoint(getLocation()));
	// }

	static IPopulation[] NO_POP = new IPopulation[0];

	@Override
	public IPopulation[] getMicroPopulations() {
		if ( getAttributes() == null ) { return NO_POP; }
		Iterable<IPopulation> it = Iterables.filter(getAttributes().values(), IPopulation.class);
		IPopulation[] pops = Iterables.toArray(it, IPopulation.class);
		return pops;
	}

	@Override
	public synchronized IPopulation getMicroPopulation(final String microSpeciesName) {
		if ( getAttributes() == null ) { return null; }
		return (IPopulation) getAttributes().get(microSpeciesName);
	}

	@Override
	public IPopulation getMicroPopulation(final ISpecies microSpecies) {
		if ( getAttributes() == null ) { return null; }
		return (IPopulation) getAttributes().get(microSpecies.getName());
	}

	@Override
	public boolean hasMembers() {
		if ( dead() || getAttributes() == null ) { return false; }
		for ( final Object pop : getAttributes().getRawValues() ) {
			if ( pop instanceof IPopulation && ((IPopulation) pop).size() > 0 ) { return true; }
		}
		return false;
	}

	@Override
	public IContainer<?, IAgent> getMembers(final IScope scope) {
		if ( dead() || getAttributes() == null ) { return GamaListFactory.EMPTY_LIST; }
		MetaPopulation mp = new MetaPopulation();
		for ( final Object pop : getAttributes().values() ) {
			if ( pop instanceof IPopulation && ((IPopulation) pop).size() > 0 ) {
				mp.addPopulation((IPopulation) pop);
			}
		}
		return mp;
	}

	@Override
	public void setMembers(final IList<IAgent> newMembers) {
		// Directly changing "members" not supported
	}

	@Override
	public void setAgents(final IList<IAgent> agents) {
		// "agents" is read-only attribute
	}

	@Override
	public IList<IAgent> getAgents(final IScope scope) {
		if ( !hasMembers() ) { return GamaListFactory.EMPTY_LIST; }

		final IContainer<?, IAgent> members = getMembers(scope);
		final IList<IAgent> agents = GamaListFactory.create(Types.AGENT);
		agents.addAll(members.listValue(scope, Types.NO_TYPE, false));
		for ( final IAgent m : members.iterable(scope) ) {
			if ( m != null && m instanceof IMacroAgent ) {
				agents.addAll(((IMacroAgent) m).getAgents(scope));
			}
		}

		return agents;
	}

	@Override
	public IPopulation getPopulationFor(final ISpecies species) {
		// hqnghi adjust to get population for species which come from main as well micro models
		ModelDescription micro = species.getDescription().getModelDescription();
		ModelDescription main = (ModelDescription) this.getModel().getDescription();
		IPopulation microPopulation = null;
		if ( main.getMicroModel(micro.getAlias()) == null ) {
			microPopulation = this.getMicroPopulation(species);
			if ( microPopulation == null && getHost() != null ) {
				microPopulation = getHost().getPopulationFor(species);
			}
		} else {
			microPopulation = this.getExternMicroPopulationFor(species.getName());
		}
		// end-hqnghi
		return microPopulation;
	}

	@Override
	public IPopulation getPopulationFor(final String speciesName) {
		final IPopulation microPopulation = this.getMicroPopulation(speciesName);
		if ( microPopulation == null && getHost() != null ) { return getHost().getPopulationFor(speciesName); }
		return microPopulation;
	}

	/**
	 * Verifies if this agent can capture other agent as newSpecies.
	 *
	 * @return true if the following conditions are correct:
	 *         1. newSpecies is one micro-species of this agent's species;
	 *         2. newSpecies is a sub-species of this agent's species or other species is a
	 *         sub-species of this agent's species;
	 *         3. the "other" agent is not macro-agent of this agent;
	 *         4. the "other" agent is not a micro-agent of this agent.
	 */
	@Override
	public boolean canCapture(final IAgent other, final ISpecies newSpecies) {
		if ( other == null || other.dead() || newSpecies == null ||
			!this.getSpecies().containMicroSpecies(newSpecies) ) { return false; }
		if ( this.getMacroAgents().contains(other) ) { return false; }
		if ( other.getHost().equals(this) ) { return false; }
		return true;
	}

	@Override
	public void addExternMicroPopulation(final String expName, final IPopulation pop) {
		if ( externMicroPopulations == null ) {
			externMicroPopulations = GamaMapFactory.create(Types.STRING, Types.LIST.of(Types.AGENT));
		}
		externMicroPopulations.put(expName, pop);
	}

	@Override
	public IPopulation getExternMicroPopulationFor(final String expName) {
		if ( externMicroPopulations != null ) { return externMicroPopulations.get(expName); }
		return null;
	}

	@Override
	public GamaMap<String, IPopulation> getExternMicroPopulations() {
		if ( externMicroPopulations == null ) { return GamaMapFactory.EMPTY_MAP; }
		return externMicroPopulations;
	}

}
