/*******************************************************************************************************
 *
 * ParallelMersenneTwisterRNG.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.random;

import java.util.concurrent.locks.ReentrantLock;

import msi.gama.common.util.RandomUtils;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Random number generator based on the
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html" target= "_top">Mersenne Twister</a> algorithm
 * developed by Makoto Matsumoto and Takuji Nishimura. Includes a reentrant lock to prevent concurrent modification of
 * the internal array
 *
 */
public class ParallelMersenneTwisterRNG extends MersenneTwisterRNG {

	static {
		DEBUG.OFF();
	}

	/** The lock. */
	// Lock to prevent concurrent modification of the RNG's internal state.
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Seed the RNG using the provided seed generation strategy.
	 *
	 * @param seedGenerator
	 *            The seed generation strategy that will provide the seed value for this RNG.
	 * @throws SeedException
	 *             If there is a problem generating a seed.
	 */
	public ParallelMersenneTwisterRNG(final RandomUtils seedGenerator) {
		this(seedGenerator.generateSeed(16));
	}

	/**
	 * Creates an RNG and seeds it with the specified seed data.
	 *
	 * @param seed
	 *            The seed data used to initialise the RNG.
	 */
	private ParallelMersenneTwisterRNG(final byte[] seed) {
		super(seed);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int internalNext(final int bits) {
		lock.lock();
		try {
			return super.internalNext(bits);
		} catch (Exception e) {
			// DEBUG.OUT(e);
			throw e;
		} finally {
			lock.unlock();
		}
	}
}
