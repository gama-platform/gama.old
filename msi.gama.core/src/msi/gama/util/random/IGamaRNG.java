/*******************************************************************************************************
 *
 * IGamaRNG.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.random;

import java.util.Random;

/**
 * The Class IGamaRNG.
 */
public interface IGamaRNG {

	/**
	 * Gets the usage.
	 *
	 * @return the usage
	 */
	int getUsage();

	/**
	 * Sets the usage.
	 *
	 * @param usage
	 *            the new usage
	 */
	default void setUsage(final int usage) {
		for (long i = 0; i < usage; i++) { nextInt(); }
	}

	/**
	 * Next int.
	 *
	 * @return the int
	 */
	int nextInt();

	/**
	 * Next double.
	 *
	 * @see java.util.Random#nextDouble()
	 *
	 * @return the double
	 */
	double nextDouble();

	/**
	 * Next gaussian.
	 *
	 * @see java.util.Random#nextGaussian()
	 *
	 * @return the double
	 */
	double nextGaussian();

	/**
	 * Gets the random generator.
	 *
	 * @return the random generator
	 */
	Random getRandomGenerator();

}
