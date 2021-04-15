/*******************************************************************************************************
 *
 * msi.gama.util.random.GamaRNG.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
public abstract class GamaRNG extends Random {

	// Mask for casting a byte to an int, bit-by-bit (with
	// bitwise AND) with no special consideration for the sign bit.
	public static final int BITWISE_BYTE_TO_INT = 0x000000FF;

	/**
	 * @return The seed data used to initialise this pseudo-random number generator.
	 */
	abstract byte[] getSeed();

	/** number of times the generator has been asked to draw a random number */
	int usage = 0;

	/**
	 * @param createLongSeed
	 */
	public GamaRNG(final long seed) {
		super(seed);
	}

	public GamaRNG() {
		super();
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(final int usage) {
		for (long i = 0; i < usage; i++) {
			next(32);
		}
	}

	/**
	 * Take four bytes from the specified position in the specified block and convert them into a 32-bit int, using the
	 * big-endian convention.
	 *
	 * @param bytes
	 *            The data to read from.
	 * @param offset
	 *            The position to start reading the 4-byte int from.
	 * @return The 32-bit integer represented by the four bytes.
	 */
	public static int convertBytesToInt(final byte[] bytes, final int offset) {
		return BITWISE_BYTE_TO_INT & bytes[offset + 3] | (BITWISE_BYTE_TO_INT & bytes[offset + 2]) << 8
				| (BITWISE_BYTE_TO_INT & bytes[offset + 1]) << 16 | (BITWISE_BYTE_TO_INT & bytes[offset]) << 24;
	}

	/**
	 * Convert an array of bytes into an array of ints. 4 bytes from the input data map to a single int in the output
	 * data.
	 *
	 * @param bytes
	 *            The data to read from.
	 * @return An array of 32-bit integers constructed from the data.
	 * @since 1.1
	 */
	public static int[] convertBytesToInts(final byte[] bytes) {
		if (bytes.length % 4 != 0) throw new IllegalArgumentException("Number of input bytes must be a multiple of 4.");
		final int[] ints = new int[bytes.length / 4];
		for (int i = 0; i < ints.length; i++) {
			ints[i] = convertBytesToInt(bytes, i * 4);
		}
		return ints;
	}

}
