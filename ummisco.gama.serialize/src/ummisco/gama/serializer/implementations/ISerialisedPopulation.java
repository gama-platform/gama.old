/*******************************************************************************************************
 *
 * ISerialisedPopulation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

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

}
