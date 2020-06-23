/*******************************************************************************************************
 *
 * msi.gama.util.random.JavaRNG.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package msi.gama.util.random;

import msi.gama.common.util.RandomUtils;

/**
 * <p>
 * This is the default {@link Random JDK RNG} extended to implement the
 * {@link RepeatableRNG} interface (for consistency with the other RNGs in this
 * package).
 * </p>
 *
 * <p>
 * The {@link MersenneTwisterRNG} should be used in preference to this class
 * because it is statistically more random and performs slightly better.
 * </p>
 *
 * @author Daniel Dyer
 */
public class JavaRNG extends GamaRNG {

	private static final int SEED_SIZE_BYTES = 8;

	private final byte[] seed;

	/**
	 * Creates a new RNG and seeds it using the default seeding strategy.
	 */
	// public JavaRNG() {
	// this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
	// }

	/**
	 * Seed the RNG using the provided seed generation strategy.
	 * 
	 * @param seedGenerator
	 *            The seed generation strategy that will provide the seed value
	 *            for this RNG.
	 * @throws SeedException
	 *             If there is a problem generating a seed.
	 */
	public JavaRNG(final RandomUtils seedGenerator) {
		this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
	}

	/**
	 * Creates an RNG and seeds it with the specified seed data.
	 * 
	 * @param seed
	 *            The seed data used to initialise the RNG.
	 */
	public JavaRNG(final byte[] seed) {
		super(createLongSeed(seed));
		this.seed = seed.clone();
	}

	@Override
	public int next(final int bits) {
		usage++;
		return super.next(bits);
	}

	/**
	 * Helper method to convert seed bytes into the long value required by the
	 * super class.
	 */
	private static long createLongSeed(final byte[] seed) {
		if (seed == null || seed.length != SEED_SIZE_BYTES) {
			throw new IllegalArgumentException("Java RNG requires a 64-bit (8-byte) seed.");
		}
		long value = 0;
		for (int i = 0; i < 0 + 8; i++) {
			final byte b = seed[i];
			value <<= 8;
			value += b & 0xff;
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getSeed() {
		return seed.clone();
	}

}
