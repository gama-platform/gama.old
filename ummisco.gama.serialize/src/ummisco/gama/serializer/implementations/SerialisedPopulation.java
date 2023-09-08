/*******************************************************************************************************
 *
 * SerialisedPopulation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;

/**
 * The Class SerialisedPopulation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 31 juil. 2023
 */
public record SerialisedPopulation(String speciesName, List<SerialisedAgent> agents) implements ISerialisedPopulation {

	/**
	 * Instantiates a new population proxy. This is where the serialised agents are created
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param pop
	 *            the pop
	 * @date 31 juil. 2023
	 */
	SerialisedPopulation(final IPopulation<IAgent> pop) {
		this(pop.getSpecies().getName(), new ArrayList<>());
		for (IAgent a : pop) { agents.add(new SerialisedAgent(a)); }
	}

}
