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

import java.security.SecureRandom;
import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.random.*;

public class RandomUtils {

	/** The seed. */
	protected Double seed;

	// private Double seed = null;
	private static final SecureRandom SEED_SOURCE = new SecureRandom();

	/** The generator name. */
	private String generatorName;

	/** The generator. */
	private Random generator;
	private ContinuousUniformGenerator uniform;

	public RandomUtils(final Double seed, final String rng) {
		setSeed(seed, false);
		setGenerator(rng, true);
	}

	public RandomUtils(final String rng) {
		this(GamaPreferences.CORE_SEED_DEFINED.getValue() ? GamaPreferences.CORE_SEED.getValue() : (Double) null, rng);
	}

	public RandomUtils() {
		this(GamaPreferences.CORE_RNG.getValue());
	}

	/**
	 * Inits the generator.
	 */
	private void initGenerator() {
		if ( generatorName.equals(IKeyword.CELLULAR) ) {
			generator = new CellularAutomatonRNG(this);
		} else if ( generatorName.equals(IKeyword.XOR) ) {
			generator = new XORShiftRNG(this);
		} else if ( generatorName.equals(IKeyword.JAVA) ) {
			generator = new JavaRNG(this);
		} else {
			/* By default */
			generator = new MersenneTwisterRNG(this);
		}
		uniform = createUniform(0., 1.);
	}

	/**
	 * Creates a new Discrete Uniform Generator object.
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
	 * Creates a new Continuous Uniform Generator object.
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
	 * Creates a new Gaussian Generator object.
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
	 * Creates a new Binomial Generator object.
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
	 * Creates a new Poisson Generator object.
	 *
	 * @param mean the mean
	 *
	 * @return the poisson generator
	 */
	public PoissonGenerator createPoisson(final double mean) {
		return new PoissonGenerator(mean, generator);
	}

	private byte[] createSeed(final Double seed, final int length) {
		this.seed = seed;
		Double realSeed = seed;
		if ( realSeed < 0 ) {
			realSeed *= -1;
		}
		if ( realSeed < 1 ) {
			realSeed *= Long.MAX_VALUE;
		}
		long l = realSeed.longValue();
		System.out.println("Initial seed: " + seed + "; normalized seed: " + l);

		final byte[] result = new byte[length];
		switch (length) {
			case 4:
				for ( int i1 = 0; i1 < 4; i1++ ) {
					result[i1] = (byte) (l & 0xff);
					l >>= 8;
				}
				break;
			case 8:
				for ( int i = 0; i < 8; i++ ) {
					result[i] = (byte) l;
					l >>= 8;
				}
				break;
			case 16:
				for ( int i = 0; i < 8; i++ ) {
					result[i] = result[i + 8] = (byte) (l & 0xff);
					l >>= 8;
				}
		}
		return result;
	}

	public void dispose() {
		seed = null;
		generator = null;
		uniform = null;
	}

	public byte[] generateSeed(final int length) {
		byte[] result;
		return createSeed(seed, length);
	}

	public void setSeed(final Double newSeed, final boolean init) {
		seed = newSeed;
		if ( seed == null ) {
			seed = SEED_SOURCE.nextDouble();
			// byte[] s = SEED_SOURCE.generateSeed(length);
		}
		if ( init ) {
			initGenerator();
		}
	}

	/**
	 * Sets the generator.
	 *
	 * @param newGen the new generator
	 */
	public void setGenerator(final String newGen, final boolean init) {
		generatorName = newGen;
		if ( init ) {
			initGenerator();
		}
	}

	// public String getGeneratorName() {
	// return generatorName;
	// }
	//
	// public Random getGenerator() {
	// return generator;
	// }

	// public void shuffle(final Set list) {
	// final Object[] copy = list.toArray(new Object[list.size()]);
	// list.clear();
	// for ( int i = copy.length; i > 1; i-- ) {
	// final int i1 = i - 1;
	// final int j = between(0, i - 1);
	// final Object tmp = copy[i1];
	// copy[i1] = copy[j];
	// copy[j] = tmp;
	// }
	// list.addAll(Arrays.asList(copy));
	//
	// }

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
	public int between(final int min, final int max) {
		return (int) (min + (long) ((1L + max - min) * next()));
	}

	public double between(final double min, final double max) {
		// uniformly distributed double random number in [min, max]
		return min + (max + Double.MIN_VALUE - min) * next();
	}

	/**
	 * @return an uniformly distributed int random number in [min, max] respecting the step
	 */
	public int between(final int min, final int max, final int step) {
		int nbSteps = (max - min) / step;
		return min + between(0, nbSteps) * step;
	}

	public double between(final double min, final double max, final double step) {
		// uniformly distributed double random number in [min, max] respecting the step
		double val = between(min, max);
		final int nbStep = (int) ((val - min) / step);
		final double high = (int) (Math.min(max, min + (nbStep + 1.0) * step) * 1000000) / 1000000.0;
		final double low = (int) ((min + nbStep * step) * 1000000) / 1000000.0;
		return val - low < high - val ? low : high;
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

	/**
	 * @return
	 */
	public Double getSeed() {
		return seed;
	}

	/**
	 * @return
	 */
	public String getRngName() {
		return generatorName;
	}

	public static void drawRandomValues(final double min, final double max, final double step) {
		System.out.println("Drawing 100 double between " + min + " and " + max + " step " + step);
		RandomUtils r = new RandomUtils(100.0, "mersenne");
		for ( int i = 0; i < 100; i++ ) {
			final double val = r.between(min, max);
			final int nbStep = (int) ((val - min) / step);
			final double high = (int) (Math.min(max, min + (nbStep + 1.0) * step) * 1000000) / 1000000.0;
			final double low = (int) ((min + nbStep * step) * 1000000) / 1000000.0;
			System.out.print(val - low < high - val ? low : high);
			System.out.print(" | ");
		}
		System.out.println();
	}

	public static void drawRandomValues(final int min, final int max, final int step) {
		System.out.println("Drawing 100 int between " + min + " and " + max + " step " + step);
		RandomUtils r = new RandomUtils(100.0, "mersenne");
		int nbSteps = (max - min) / step;
		for ( int i = 0; i < 100; i++ ) {
			final int val = min + r.between(0, nbSteps) * step;
			System.out.print(val);
			System.out.print(" | ");
		}
		System.out.println();
	}

	public static void main(final String[] args) {
		drawRandomValues(-0.2, 0.2, 0.1);
		drawRandomValues(4., 5., 0.2);
		drawRandomValues(0, 100, 3);
		drawRandomValues(-5, 5, 3);
		RandomUtils r = new RandomUtils(100.0, "mersenne");
		for ( int i = 0; i < 10000000; i++ ) {
			double d = 0.0;
			if ( r.between(0.0, 0.1) == 0.0 ) {
				System.out.println("0.0 !");
			}
		}
		System.out.println("Finished");
	}

}
