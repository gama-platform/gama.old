/*******************************************************************************************************
 *
 * ISyntheticGosplPopGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.generator;

import gospl.GosplPopulation;

/**
 * Light and unprescriptive super interface for synthetic population generator: only need to be able to create a
 * {@link GosplPopulation} whith n individual entity
 *
 * @author kevinchapuis
 *
 */
public interface ISyntheticGosplPopGenerator {

	/**
	 * Generate a synthetic population of type {@link GosplPopulation} with parametric number of individual entity
	 *
	 * @param numberOfIndividual
	 * @return
	 */
	GosplPopulation generate(int numberOfIndividual);

}
