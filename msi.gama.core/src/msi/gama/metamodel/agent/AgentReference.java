/*******************************************************************************************************
 *
 * AgentReference.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.file.json.IJsonConstants;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonValue;
import msi.gaml.interfaces.IJsonable;
import one.util.streamex.StreamEx;

/**
 * The Class AgentReference. A unique way to reference agents inside experiments.
 *
 * The reference of an agent will be :
 *
 * - "simulation[n]" if it is a simulation and its index is n
 *
 * - "simulation[n].species_name[m]" if it is instance of species_name, its index is m and it belongs to the simulation
 * with index n. Nested species follow the same pattern, e.g. "simulation[n].specie_name[m].nested_species_name[x]"
 *
 * Assumes (1) the experiment is unique in the scope; (2) the first species name (the simulation) is not relevant (can
 * be called "simulation" if needed)
 */
public record AgentReference(String[] species, Integer[] index, String cached_ref) implements IJsonable {

	/**
	 * Of.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agt
	 *            the agt
	 * @return the agent reference
	 * @date 1 nov. 2023
	 */
	public static AgentReference of(final IAgent agt) {
		return of(buildSpeciesArray(agt), buildIndicesArray(agt));
	}

	/**
	 * Of.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param ref
	 *            the ref
	 * @return the agent reference
	 * @date 5 nov. 2023
	 */
	public static AgentReference of(final String ref) {
		String[] tokens = ref.split("[\\[\\]\\.]");
		tokens = StreamEx.of(tokens).filter(s -> !s.isEmpty()).toArray(String.class);
		int size = tokens.length / 2;
		String[] species = new String[size];
		Integer[] index = new Integer[size];
		for (int i = 0; i < size; i++) {
			species[i] = tokens[i * 2];
			index[i] = Integer.decode(tokens[i * 2 + 1]);
		}
		return new AgentReference(species, index, ref);
	}

	/**
	 * Of.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param species
	 *            the species
	 * @param index
	 *            the index
	 * @return the agent reference
	 * @date 1 nov. 2023
	 */
	public static AgentReference of(final String[] species, final Integer[] index) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < species.length; i++) {
			sb.append(species[i]).append('[').append(index[i]).append(']').append('.');
		}
		sb.setLength(sb.length() - 1);
		String ref = sb.toString();
		return new AgentReference(species, index, ref);
	}

	@Override
	public String toString() {
		return cached_ref;
	}

	/**
	 * Gets the referenced agent.
	 *
	 * @param sim
	 *            the sim
	 * @return the referenced agent
	 */
	public IAgent getReferencedAgent(final IScope scope) {
		if (scope == null) return null;
		IExperimentAgent sim = scope.getExperiment();
		if (sim == null) return null;
		IPopulation<? extends IAgent> pop = sim.getSimulationPopulation();
		IAgent referencedAgt = pop.getOrCreateAgent(scope, index[0]);
		for (int i = 1; i < index.length; i++) {
			pop = referencedAgt.getPopulationFor(species[i]);
			if (pop == null) return null;
			referencedAgt = pop.getOrCreateAgent(scope, index[i]);
		}
		return referencedAgt;
	}

	/**
	 * Builds the species array. simulation > species1 > nested_species > ...
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param a
	 *            the a
	 * @return the string[]
	 * @date 7 août 2023
	 */
	static String[] buildSpeciesArray(final IAgent a) {
		List<String> species = new LinkedList<>();
		species.add(a instanceof SimulationAgent sim ? IKeyword.SIMULATION : a.getSpeciesName());
		IAgent host = a.getHost();
		while (host != null && !(host instanceof IExperimentAgent)) {
			species.add(0, host.getSpeciesName());
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
		List<Integer> species = new LinkedList<>();
		species.add(a.getIndex());
		IAgent host = a.getHost();
		while (host != null && !(host instanceof IExperimentAgent)) {
			species.add(0, host.getIndex());
			host = host.getHost();
		}
		return species.toArray(new Integer[0]);
	}

	@Override
	public JsonValue serializeToJson(final Json json) {
		return json.object(IJsonConstants.AGENT_REFERENCE_LABEL, toString());
	}

}