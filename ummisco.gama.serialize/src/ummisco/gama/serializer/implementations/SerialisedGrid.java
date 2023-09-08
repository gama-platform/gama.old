/*******************************************************************************************************
 *
 * SerialisedGrid.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
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
import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.metamodel.topology.grid.IGrid;

/**
 * The SerialisedGrid.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 27 août 2023
 */
public record SerialisedGrid(String speciesName, List<SerialisedAgent> agents, IGrid matrix)
		implements ISerialisedPopulation {

	/**
	 * Instantiates a new serialised grid.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param pop
	 *            the pop
	 * @date 27 août 2023
	 */
	SerialisedGrid(final GridPopulation pop) {
		this(pop.getSpecies().getName(), new ArrayList<>(), pop.getTopology().getPlaces());
		for (IAgent a : pop) { agents.add(new SerialisedAgent(a)); }
	}

	/**
	 * Checks if is grid.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is grid
	 * @date 27 août 2023
	 */
	@Override
	public boolean isGrid() { return true; }
}
