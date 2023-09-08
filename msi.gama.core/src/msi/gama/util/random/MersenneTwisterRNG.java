/*******************************************************************************************************
 *
 * MersenneTwisterRNG.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.random;

import msi.gama.common.util.RandomUtils;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Random number generator based on the
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html" target= "_top">Mersenne Twister</a> algorithm
 * developed by Makoto Matsumoto and Takuji Nishimura. This is a very fast random number generator with good statistical
 * properties (it passes the full DIEHARD suite). This is the best RNG for most experiments. If a non-linear generator
 * is required, use the slower {@link AESCounterRNG} RNG. This PRNG is deterministic, which can be advantageous for
 * testing purposes since the output is repeatable. If multiple instances of this class are created with the same seed
 * they will all have identical output. This code is translated from the original C version and assumes that we will
 * always seed from an array of bytes. I don't pretend to know the meanings of the magic numbers or how it works, it
 * just does.
 *
 * @author Makoto Matsumoto and Takuji Nishimura (original C version)
 * @author Daniel Dyer (Java port)
 */
public class MersenneTwisterRNG extends GamaRNG {

	static {
		DEBUG.OFF();
	}

	/** The bitwise byte to int. */
	// Mask for casting a byte to an int, bit-by-bit (with bitwise AND) with no special consideration for the sign bit.
	int BITWISE_BYTE_TO_INT = 0x000000FF;

	/** The Constant N. */
	private static final int N = 624;

	/** The Constant M. */
	private static final int M = 397;

	/** The Constant MAG01. */
	private static final int[] MAG01 = { 0, 0x9908b0df };

	/** The Constant UPPER_MASK. */
	private static final int UPPER_MASK = 0x80000000;

	/** The Constant LOWER_MASK. */
	private static final int LOWER_MASK = 0x7fffffff;

	/** The Constant BOOTSTRAP_SEED. */
	private static final int BOOTSTRAP_SEED = 19650218;

	/** The Constant BOOTSTRAP_FACTOR. */
	private static final int BOOTSTRAP_FACTOR = 1812433253;

	/** The Constant SEED_FACTOR1. */
	private static final int SEED_FACTOR1 = 1664525;

	/** The Constant SEED_FACTOR2. */
	private static final int SEED_FACTOR2 = 1566083941;

	/** The Constant GENERATE_MASK1. */
	private static final int GENERATE_MASK1 = 0x9d2c5680;

	/** The Constant GENERATE_MASK2. */
	private static final int GENERATE_MASK2 = 0xefc60000;

	/** The mt. */
	private final int[] mt = new int[N]; // State vector.

	/** The mt index. */
	private int mtIndex = 0; // Index into state vector.

	/**
	 * Seed the RNG using the provided seed generation strategy.
	 *
	 * @param seedGenerator
	 *            The seed generation strategy that will provide the seed value for this RNG.
	 */
	public MersenneTwisterRNG(final RandomUtils seedGenerator) {
		this(seedGenerator.generateSeed(16));
	}

	/**
	 * Creates an RNG and seeds it with the specified seed data.
	 *
	 * @param seed
	 *            The seed data used to initialise the RNG.
	 */
	protected MersenneTwisterRNG(final byte[] seed) {
		super(seed);
		final int[] seedInts = convertBytesToInts(seed);
		mt[0] = BOOTSTRAP_SEED;
		for (mtIndex = 1; mtIndex < N; mtIndex++) {
			mt[mtIndex] = BOOTSTRAP_FACTOR * (mt[mtIndex - 1] ^ mt[mtIndex - 1] >>> 30) + mtIndex;
		}
		int i = 1;
		int j = 0;
		for (int k = Math.max(N, seedInts.length); k > 0; k--) {
			mt[i] = (mt[i] ^ (mt[i - 1] ^ mt[i - 1] >>> 30) * SEED_FACTOR1) + seedInts[j] + j;
			i++;
			j++;
			if (i >= N) {
				mt[0] = mt[N - 1];
				i = 1;
			}
			if (j >= seedInts.length) { j = 0; }
		}
		for (int k = N - 1; k > 0; k--) {
			mt[i] = (mt[i] ^ (mt[i - 1] ^ mt[i - 1] >>> 30) * SEED_FACTOR2) - i;
			i++;
			if (i >= N) {
				mt[0] = mt[N - 1];
				i = 1;
			}
		}
		mt[0] = UPPER_MASK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int internalNext(final int bits) {
		int y;
		if (mtIndex >= N) // Generate N ints at a time.
		{
			int kk;
			for (kk = 0; kk < N - M; kk++) {
				y = mt[kk] & UPPER_MASK | mt[kk + 1] & LOWER_MASK;
				mt[kk] = mt[kk + M] ^ y >>> 1 ^ MAG01[y & 0x1];
			}
			for (; kk < N - 1; kk++) {
				y = mt[kk] & UPPER_MASK | mt[kk + 1] & LOWER_MASK;
				mt[kk] = mt[kk + M - N] ^ y >>> 1 ^ MAG01[y & 0x1];
			}
			y = mt[N - 1] & UPPER_MASK | mt[0] & LOWER_MASK;
			mt[N - 1] = mt[M - 1] ^ y >>> 1 ^ MAG01[y & 0x1];

			mtIndex = 0;
		}
		try {
			y = mt[mtIndex++];
		} catch (Exception e) {
			// DEBUG.OUT(e);
			throw e;
		}

		// Tempering
		y ^= y >>> 11;
		y ^= y << 7 & GENERATE_MASK1;
		y ^= y << 15 & GENERATE_MASK2;
		y ^= y >>> 18;

		return y >>> 32 - bits;
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
	int[] convertBytesToInts(final byte[] bytes) {
		if (bytes.length % 4 != 0) throw new IllegalArgumentException("Number of input bytes must be a multiple of 4.");
		final int[] ints = new int[bytes.length / 4];
		for (int i = 0; i < ints.length; i++) {
			final int offset = i * 4;
			ints[i] = BITWISE_BYTE_TO_INT & bytes[offset + 3] | (BITWISE_BYTE_TO_INT & bytes[offset + 2]) << 8
					| (BITWISE_BYTE_TO_INT & bytes[offset + 1]) << 16 | (BITWISE_BYTE_TO_INT & bytes[offset]) << 24;
		}
		return ints;
	}

}
