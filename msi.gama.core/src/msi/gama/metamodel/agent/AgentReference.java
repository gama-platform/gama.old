/*******************************************************************************************************
 *
 * AgentReference.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonValue;
import msi.gaml.interfaces.IJsonable;

/**
 * The Class AgentReference.
 */
public record AgentReference(String[] species, Integer[] index) implements IJsonable {

	/**
	 * Instantiates a new reference to agent.
	 *
	 * @param agt
	 *            the agt
	 */
	public AgentReference(final IAgent agt) {
		this(buildSpeciesArray(agt), buildIndicesArray(agt));
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < species.length; i++) { res = "/" + species[i] + index[i]; }
		return res;
	}

	/**
	 * Gets the referenced agent.
	 *
	 * @param sim
	 *            the sim
	 * @return the referenced agent
	 */
	public IAgent getReferencedAgent(final IScope scope) {
		SimulationAgent sim = scope.getSimulation();
		IPopulation<? extends IAgent> pop = sim.getPopulationFor(species[species.length - 1]);
		if (pop == null) { pop = sim.getPopulation(); }
		IAgent referencedAgt = pop.getOrCreateAgent(scope, index[index.length - 1]);

		for (int i = index.length - 2; i >= 0; i--) {
			pop = sim.getPopulationFor(species[i]);
			referencedAgt = pop.getOrCreateAgent(scope, index[i]);
		}
		return referencedAgt;
	}

	/**
	 * Gets the last index.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the last index
	 * @date 6 août 2023
	 */
	public Integer getLastIndex() { return index[index.length - 1]; }

	/**
	 * Builds the species array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param a
	 *            the a
	 * @return the string[]
	 * @date 7 août 2023
	 */
	static String[] buildSpeciesArray(final IAgent a) {
		List<String> species = new ArrayList<>();
		species.add(a.getSpeciesName());
		IAgent host = a.getHost();
		while (host != null && !(host instanceof SimulationAgent)) {
			species.add(host.getSpeciesName());
			host = host.getHost();
		}
		return species.toArray(new String[0]);
	}

	/**
	 * Builds the species array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param a
	 *            the a
	 * @return the int[]
	 * @date 7 août 2023
	 */
	static Integer[] buildIndicesArray(final IAgent a) {
		List<Integer> species = new ArrayList<>();
		species.add(a.getIndex());
		IAgent host = a.getHost();
		while (host != null && !(host instanceof SimulationAgent)) {
			species.add(host.getIndex());
			host = host.getHost();
		}
		return species.toArray(new Integer[0]);
	}

	@Override
	public JsonValue serializeToJson(final Json json) {
		return json.object("ref", species[0], "populations", json.array(species), "indices", json.array(index));
	}

}