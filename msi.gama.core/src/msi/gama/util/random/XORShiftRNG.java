/*********************************************************************************************
 *
 *
 * 'XORShiftRNG.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit http://gama-platform.googlecode.com for license information and developers contact.
 *
 *
 **********************************************************************************************/
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
 * Very fast pseudo random number generator. See <a
 * href="http://school.anhb.uwa.edu.au/personalpages/kwessen/shared/Marsaglia03.html">this page</a>
 * for a description. This RNG has a period of about 2^160, which is not as long as the {@link MersenneTwisterRNG} but it is faster.
 * @author Daniel Dyer
 * @since 1.2
 */
public class XORShiftRNG extends GamaRNG {

	private static final int SEED_SIZE_BYTES = 20; // Needs 5 32-bit integers.

	// Previously used an array for state but using separate fields proved to be
	// faster.
	private int state1;
	private int state2;
	private int state3;
	private int state4;
	private int state5;

	private final byte[] seed;

	// Lock to prevent concurrent modification of the RNG's internal state.
	// private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Creates a new RNG and seeds it using the default seeding strategy.
	 */
	// public XORShiftRNG() {
	// this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
	// }

	/**
	 * Seed the RNG using the provided seed generation strategy.
	 * @param seedGenerator The seed generation strategy that will provide the seed value for this
	 *            RNG.
	 * @throws SeedException If there is a problem generating a seed.
	 */
	public XORShiftRNG(final RandomUtils seedGenerator) {
		this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
	}

	/**
	 * Creates an RNG and seeds it with the specified seed data.
	 * @param seed The seed data used to initialise the RNG.
	 */
	public XORShiftRNG(final byte[] seed) {
		if ( seed == null || seed.length != SEED_SIZE_BYTES ) { throw new IllegalArgumentException(
			"XOR shift RNG requires 160 bits of seed data."); }
		this.seed = seed.clone();
		int[] state = convertBytesToInts(seed);
		this.state1 = state[0];
		this.state2 = state[1];
		this.state3 = state[2];
		this.state4 = state[3];
		this.state5 = state[4];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getSeed() {
		return seed.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int next(final int bits) {
		// try
		// {
		// lock.lock();
		int t = state1 ^ state1 >> 7;
		state1 = state2;
		state2 = state3;
		state3 = state4;
		state4 = state5;
		state5 = state5 ^ state5 << 6 ^ t ^ t << 13;
		int value = (state2 + state2 + 1) * state5;
		return value >>> 32 - bits;
		// }
		// finally
		// {
		// lock.unlock();
		// }
	}
}
