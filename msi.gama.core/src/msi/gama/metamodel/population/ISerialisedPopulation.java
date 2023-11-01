/*******************************************************************************************************
 *
 * ISerialisedPopulation.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.population;

import java.util.List;

import msi.gama.metamodel.agent.ISerialisedAgent;

/**
 * The Interface ISerialisedPopulation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 27 août 2023
 */
public interface ISerialisedPopulation {

	/**
	 * Checks if is grid.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is grid
	 * @date 27 août 2023
	 */
	default boolean isGrid() { return false; }

	/**
	 * Gets the agents.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the agents
	 * @date 29 oct. 2023
	 */
	List<ISerialisedAgent> agents();

	/**
	 * Serialize to json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	// @Override
	// default JsonObject serializeToJson(final Json json) {
	// return json.object("population", speciesName(), "agents", json.array(agents()));
	// }

	/**
	 * Species name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @date 29 oct. 2023
	 */
	String speciesName();

}
