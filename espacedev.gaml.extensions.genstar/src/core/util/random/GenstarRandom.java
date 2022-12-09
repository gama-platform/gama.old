/*******************************************************************************************************
 *
 * GenstarRandom.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util.random;

import java.util.Random;

/**
 * The Class GenstarRandom.
 */
public class GenstarRandom {

	/**
	 * Instantiates a new genstar random.
	 */
	private GenstarRandom() {}

	/** The random engine. */
	private static Random randomEngine;

	/**
	 * Gets the single instance of GenstarRandom.
	 *
	 * @return single instance of GenstarRandom
	 */
	public static Random getInstance() {
		if (randomEngine == null) { randomEngine = new Random(); }
		return randomEngine;
	}

	/**
	 * Sets the instance.
	 *
	 * @param random
	 *            the new instance
	 */
	public static void setInstance(final Random random) { randomEngine = random; }

}
