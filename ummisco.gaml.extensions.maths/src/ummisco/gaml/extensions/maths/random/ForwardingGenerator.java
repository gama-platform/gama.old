/*******************************************************************************************************
 *
 * ForwardingGenerator.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.random;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import msi.gama.util.random.GamaRNG;

/**
 * The Class ForwardingGenerator.
 */
public class ForwardingGenerator implements RandomGenerator {

	/** The target. */
	private final GamaRNG target;

	/**
	 * Instantiates a new forwarding generator.
	 *
	 * @param target the target
	 */
	ForwardingGenerator(final GamaRNG target) {
		this.target = target;
	}

	@Override
	public boolean nextBoolean() {
		return target.nextBoolean();
	}

	@Override
	public void nextBytes(final byte[] arg0) {
		target.nextBytes(arg0);
	}

	@Override
	public double nextDouble() {
		return target.nextDouble();
	}

	@Override
	public float nextFloat() {
		return target.nextFloat();
	}

	@Override
	public double nextGaussian() {
		return target.nextGaussian();
	}

	@Override
	public int nextInt() {
		return target.nextInt();
	}

	@Override
	public int nextInt(final int arg0) {
		return target.nextInt(arg0);
	}

	@Override
	public long nextLong() {
		return target.nextLong();
	}

	@Override
	public void setSeed(final int arg0) {
		target.setSeed(Integer.toUnsignedLong(arg0));
	}

	@Override
	public void setSeed(final int[] arg0) {
		target.setSeed(RandomGeneratorFactory.convertToLong(arg0));
	}

	@Override
	public void setSeed(final long arg0) {
		target.setSeed(arg0);
	}

}