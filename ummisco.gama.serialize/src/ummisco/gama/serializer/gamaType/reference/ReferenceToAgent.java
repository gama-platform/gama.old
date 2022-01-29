/*******************************************************************************************************
 *
 * ReferenceToAgent.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;
import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;

/**
 * The Class ReferenceToAgent.
 */
public class ReferenceToAgent {
	
	/** The species. */
	List<String> species;
	
	/** The index. */
	List<Integer> index;

	/**
	 * Instantiates a new reference to agent.
	 */
	private ReferenceToAgent() {
		species = new ArrayList<>();
		index = new ArrayList<>();
	}

	/**
	 * Instantiates a new reference to agent.
	 *
	 * @param agt the agt
	 */
	public ReferenceToAgent(final IAgent agt) {
		this();
		if (agt != null) {
			species.add(agt.getSpeciesName());
			index.add(agt.getIndex());

			IAgent host = agt.getHost();

			while (host != null && !(host instanceof SimulationAgent)) {
				species.add(host.getSpeciesName());
				index.add(host.getIndex());
				host = host.getHost();
			}
		}
	}

	@Override
	public String toString() {
		String res = "";

		for (int i = 0; i < species.size(); i++) {
			res = "/" + species.get(i) + index.get(i);
		}
		return res;
	}

	/**
	 * Gets the referenced agent.
	 *
	 * @param sim the sim
	 * @return the referenced agent
	 */
	public IAgent getReferencedAgent(final SimulationAgent sim) {

		IPopulation<? extends IAgent> pop = sim.getPopulationFor(species.get(species.size() - 1));
		IAgent referencedAgt = pop.getAgent(index.get(index.size() - 1));

		for (int i = index.size() - 2; i >= 0; i--) {
			pop = sim.getPopulationFor(species.get(i));
			referencedAgt = pop.get(index.get(i));
		}

		return referencedAgt;
	}

}
