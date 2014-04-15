/*********************************************************************************************
 * 
 * 
 * 'RandomUtils.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.common.util;

import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import org.uncommons.maths.binary.BinaryUtils;
import org.uncommons.maths.random.*;

public class RandomUtils implements SeedGenerator {

	/** The seed. */
	private Long seed = null;

	// private static RandomUtils defaultRandom = null;

	public static RandomUtils getDefault() {
		// if ( defaultRandom == null ) {
		Double doubleSeed =
			GamaPreferences.CORE_SEED_DEFINED.getValue() ? GamaPreferences.CORE_SEED.getValue() : (Double) null;
		RandomUtils defaultRandom = new RandomUtils(doubleSeed == null ? null : doubleSeed.longValue());
		// }
		return defaultRandom;
	}

	/** The generator name. */
	private String generatorName = GamaPreferences.CORE_RNG.getValue();

	/** The generator. */
	private Random generator;
	private ContinuousUniformGenerator uniform;

	public RandomUtils(final Long seed) {
		setSeed(seed);
	}

	public RandomUtils(final Double seed, final String rng) {
		setSeed(seed);
		setGenerator(rng);
	}

	public void initDefaultDistributions() {
		uniform = createUniform(0., 1.);
	}

	/**
	 * Inits the generator.
	 */
	private void initGenerator() {
		// GuiUtils.debug("RandomUtils.initGenerator: " + generatorName);
		try {
			if ( generatorName.equals(IKeyword.CELLULAR) ) {
				generator = createCAGenerator();
			} else if ( generatorName.equals(IKeyword.XOR) ) {
				generator = createXORShiftGenerator();
			} else if ( generatorName.equals(IKeyword.JAVA) ) {
				generator = createJavaGenerator();
			} else if ( generatorName.equals(IKeyword.MERSENNE) ) {
				generator = createMersenneTwisterGenerator();
			}
		} catch (final SeedException e) {
			generator = new Random(seed);
		}
	}

	// Distributions

	/**
	 * Creates a new Random object.
	 * 
	 * @param min the min
	 * @param max the max
	 * 
	 * @return the discrete uniform generator
	 */
	public DiscreteUniformGenerator createUniform(final int min, final int max) {
		return new DiscreteUniformGenerator(min, max, generator);
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @param min the min
	 * @param max the max
	 * 
	 * @return the continuous uniform generator
	 */
	public ContinuousUniformGenerator createUniform(final double min, final double max) {
		return new ContinuousUniformGenerator(min, max, generator);
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @param mean the mean
	 * @param stdv the stdv
	 * 
	 * @return the gaussian generator
	 */
	public GaussianGenerator createGaussian(final double mean, final double stdv) {
		return new GaussianGenerator(mean, stdv, generator);
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @param n the n
	 * @param p the p
	 * 
	 * @return the binomial generator
	 */
	public BinomialGenerator createBinomial(final int n, final double p) {
		return new BinomialGenerator(n, p, generator);
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @param mean the mean
	 * 
	 * @return the poisson generator
	 */
	public PoissonGenerator createPoisson(final double mean) {
		return new PoissonGenerator(mean, generator);
	}

	// Random numbers generators

	/**
	 * Creates a new Random object.
	 * 
	 * @return the random
	 * 
	 * @throws SeedException the seed exception
	 */
	public Random createCAGenerator() throws SeedException {
		return new CellularAutomatonRNG(this);
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @return the random
	 * 
	 * @throws SeedException the seed exception
	 */
	public Random createJavaGenerator() throws SeedException {
		return new JavaRNG(this);
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @return the random
	 * 
	 * @throws SeedException the seed exception
	 */
	public Random createMersenneTwisterGenerator() throws SeedException {
		return new MersenneTwisterRNG(this);
	}

	public Random createXORShiftGenerator() throws SeedException {
		return new XORShiftRNG(this);
	}

	// Random number generator seed

	private static byte[] create8BytesSeed(final long seed) {
		long l = seed;
		final byte[] retVal = new byte[8];

		for ( int i = 0; i < 8; i++ ) {
			retVal[i] = (byte) l;
			l >>= 8;
		}

		return retVal;
	}

	private static byte[] createSeed(final Long seed, final int length) throws SeedException {
		switch (length) {
			case 4:
				return create4BytesSeed(seed);
			case 8:
				return create8BytesSeed(seed);
			case 16:
				return create16BytesSeed(seed);
			default:
				throw new SeedException("cannot generate a seed of length " + length);
		}
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @param seed the seed
	 * 
	 * @return the byte[]
	 */
	private static byte[] create16BytesSeed(final long seed) {
		final byte s[] = new byte[16];
		long v = seed;
		for ( int i = 0; i < 8; i++ ) {
			s[i] = s[i + 8] = (byte) (v & 0xff);
			v >>= 8;
		}
		return s;
	}

	/**
	 * Creates a new Random object.
	 * 
	 * @param seed the seed
	 * 
	 * @return the byte[]
	 */
	private static byte[] create4BytesSeed(final long seed) {
		final byte s[] = new byte[4];
		long v = seed;
		for ( int i = 0; i < 4; i++ ) {
			s[i] = (byte) (v & 0xff);
			v >>= 8;
		}
		return s;
	}

	public void dispose() {
		seed = null;
		generator = null;
		uniform = null;
	}

	@Override
	public byte[] generateSeed(final int length) {
		byte[] result;
		if ( seed == null ) { return DefaultSeedGenerator.getInstance().generateSeed(length); }
		try {
			result = createSeed(seed, length);
		} catch (final SeedException e) {
			result = DefaultSeedGenerator.getInstance().generateSeed(length);
		}
		return result;
	}

	/**
	 * Sets the seed.
	 * 
	 * @param newSeed the new seed
	 */
	public void setSeed(final Long newSeed) {
		// GuiUtils.debug("New seed for RandomAgent: " + newSeed);
		// final Long oldSeed = seed;
		seed = newSeed;
		// if ( seed == null || !seed.equals(oldSeed) ) {
		initGenerator();
		initDefaultDistributions();
		// }
	}

	public void setSeed(final Double newSeed) {
		if ( newSeed == null ) {
			setSeed((Long) null);
		} else {
			setSeed(Math.round(newSeed));
		}
	}

	/**
	 * Sets the generator.
	 * 
	 * @param newGen the new generator
	 */
	public void setGenerator(final String newGen) {
		// GuiUtils.debug("RandomUtils.setGenerator " + newGen);
		// if ( newGen == null || generatorName.equals(newGen) ) { return; }
		generatorName = newGen;
		initGenerator();
		initDefaultDistributions();
	}

	public long getSeed() {
		if ( seed == null ) {
			Double s =
				GamaPreferences.CORE_SEED_DEFINED.getValue() ? GamaPreferences.CORE_SEED.getValue() : (Double) null;
			if ( s == null ) {
				seed = BinaryUtils.convertBytesToLong(DefaultSeedGenerator.getInstance().generateSeed(8), 0);
			} else {
				seed = Math.round(s);
			}
		}
		return seed;
	}

	public String getGeneratorName() {
		return generatorName;
	}

	public Random getGenerator() {
		return generator;
	}

	public void shuffle(final Set list) {
		final Object[] copy = list.toArray(new Object[list.size()]);
		list.clear();
		for ( int i = copy.length; i > 1; i-- ) {
			final int i1 = i - 1;
			final int j = between(0, i - 1);
			final Object tmp = copy[i1];
			copy[i1] = copy[j];
			copy[j] = tmp;
		}
		list.addAll(Arrays.asList(copy));

	}

	public void shuffle2(final Set list) {
		int size = list.size();
		if ( size < 2 ) { return; }
		final Object[] a = list.toArray(new Object[size]);
		list.clear();
		for ( int i = 0; i < size; i++ ) {
			int change = between(i, size - 1);
			Object helper = a[i];
			a[i] = a[change];
			a[change] = helper;
			list.add(a[i]);
		}
	}

	public List shuffle(final List list) {
		for ( int i = list.size(); i > 1; i-- ) {
			final int i1 = i - 1;
			final int j = between(0, i - 1);
			final Object tmp = list.get(i1);
			list.set(i1, list.get(j));
			list.set(j, tmp);
		}
		return list;
	}

	public String shuffle(final String string) {
		final char[] c = string.toCharArray();
		shuffle(c);
		return String.copyValueOf(c);
	}

	public <T> T[] shuffle(final T[] array) {
		final T[] copy = array.clone();
		for ( int i = array.length; i > 1; i-- ) {
			final int i1 = i - 1;
			final int j = between(0, i - 1);
			final T tmp = copy[i1];
			copy[i1] = copy[j];
			copy[j] = tmp;
		}
		return copy;
	}

	/**
	 * @return an uniformly distributed int random number in [from, to]
	 */
	public int between(final int from, final int to) {
		return (int) (from + (long) ((1L + to - from) * uniform.nextValue()));
	}

	public double between(final double from, final double to) {
		// uniformly distributed double random number in ]from, to[
		return from + (to - from) * uniform.nextValue();
	}

	public double next() {
		return uniform.nextValue();
	}

	/**
	 * @param matrix
	 * @return
	 */
	public double[] shuffle(final double[] array) {
		for ( int i = array.length; i > 1; i-- ) {
			final int i1 = i - 1;
			final int j = between(0, i - 1);
			final double tmp = array[i1];
			array[i1] = array[j];
			array[j] = tmp;
		}
		return array;
	}

	public int[] shuffle(final int[] array) {
		for ( int i = array.length; i > 1; i-- ) {
			final int i1 = i - 1;
			final int j = between(0, i - 1);
			final int tmp = array[i1];
			array[i1] = array[j];
			array[j] = tmp;
		}
		return array;
	}

	public char[] shuffle(final char[] array) {
		for ( int i = array.length; i > 1; i-- ) {
			final int i1 = i - 1;
			final int j = between(0, i - 1);
			final char tmp = array[i1];
			array[i1] = array[j];
			array[j] = tmp;
		}
		return array;
	}

}
