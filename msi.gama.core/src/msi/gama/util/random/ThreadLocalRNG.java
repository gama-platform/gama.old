/*******************************************************************************************************
 *
 * ThreadLocalRNG.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.random;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import msi.gama.common.util.RandomUtils;

/**
 * An implementation that derives all access to ThreadLocalRandom instances. Not deterministic but fast and thread-safe
 */
public class ThreadLocalRNG implements IGamaRNG {

	/**
	 * Seed the RNG using the provided seed generation strategy. Not applicable for this class of RNG
	 *
	 * @param seedGenerator
	 *            The seed generation strategy that will provide the seed value for this RNG.
	 */
	public ThreadLocalRNG(final RandomUtils seedGenerator) {
		// Not used as the seed cannot be set
	}

	@Override
	public int getUsage() { return 0; }

	@Override
	public int nextInt() {
		return ThreadLocalRandom.current().nextInt();
	}

	@Override
	public double nextDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	@Override
	public double nextGaussian() {
		return ThreadLocalRandom.current().nextGaussian();
	}

	@Override
	public Random getRandomGenerator() { return ThreadLocalRandom.current(); }

}
