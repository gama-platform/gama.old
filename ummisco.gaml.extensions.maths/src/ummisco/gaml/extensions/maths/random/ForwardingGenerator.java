package ummisco.gaml.extensions.maths.random;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import msi.gama.util.random.GamaRNG;

public class ForwardingGenerator implements RandomGenerator {

	private final GamaRNG target;

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