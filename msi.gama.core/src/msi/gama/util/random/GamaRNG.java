/*******************************************************************************************************
 *
 * GamaRNG.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.random;

import java.util.Random;

/**
 * Class GamaRNG.
 *
 * @author drogoul
 * @since 4 juin 2015
 * @modified april 2021 for removing the dependency towards Apache Commons Maths
 *
 */
public class GamaRNG extends Random implements IGamaRNG {

	/** number of times the generator has been asked to draw a random number */
	int usage = 0;

	/**
	 * @param createLongSeed
	 */
	public GamaRNG(final byte[] seed) {
		long value = 0;
		for (int i = 0; i < 8; i++) {
			final byte b = seed[i];
			value <<= 8;
			value += b & 0xff;
		}
		super.setSeed(value);
	}

	/**
	 * Gets the number of times the generator has been asked to draw a random number.
	 *
	 * @return the number of times the generator has been asked to draw a random number
	 */
	@Override
	public int getUsage() { return usage; }

	@Override
	public final int next(final int bits) {
		usage++;
		return internalNext(bits);
	}

	/**
	 * Internal next. Should be redefined for specific RNG -- returns the legacy Random.next() value by default
	 *
	 * @param bits
	 *            the bits
	 * @return the int
	 */
	protected int internalNext(final int bits) {
		return super.next(bits);
	}

	@Override
	public Random getRandomGenerator() { return this; }

}
