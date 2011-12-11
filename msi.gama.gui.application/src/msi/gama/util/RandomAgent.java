/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import org.uncommons.maths.binary.BinaryUtils;
import org.uncommons.maths.random.*;

public class RandomAgent implements SeedGenerator {

	/** The seed. */
	private Long seed = null;

	public static RandomAgent defaultRandom = new RandomAgent((Long) null);

	public static RandomAgent getDefault() {
		return defaultRandom;
	}

	/** The generator name. */
	private String generatorName = ISymbol.XOR;

	/** The generator. */
	private Random generator;
	private ContinuousUniformGenerator uniform;

	public RandomAgent(final Long seed) {
		setSeed(seed);
	}

	public RandomAgent(final Double seed) {
		setSeed(seed);
	}

	public void initDefaultDistributions() {
		uniform = createUniform(0., 1.);
	}

	public static final GamaList<String> GENERATOR_NAMES = GamaList.with(ISymbol.CELLULAR,
		ISymbol.XOR, ISymbol.JAVA, ISymbol.MERSENNE);

	/**
	 * Inits the generator.
	 */
	private void initGenerator() {
		try {
			if ( generatorName.equals(ISymbol.CELLULAR) ) {
				generator = createCAGenerator();
			} else if ( generatorName.equals(ISymbol.XOR) ) {
				generator = createXORShiftGenerator();
			} else if ( generatorName.equals(ISymbol.JAVA) ) {
				generator = createJavaGenerator();
			} else if ( generatorName.equals(ISymbol.MERSENNE) ) {
				generator = createMersenneTwisterGenerator();
			}
		} catch (SeedException e) {
			generator = new Random();
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
		byte[] retVal = new byte[8];

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
		} catch (SeedException e) {
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
		// GUI.debug("New seed for RandomAgent: " + newSeed);
		Long oldSeed = seed;
		seed = newSeed == null || newSeed.equals(0L) ? seed : newSeed;
		if ( seed == null || !seed.equals(oldSeed) ) {
			initGenerator();
			initDefaultDistributions();
		}
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
		if ( newGen == null || generatorName.equals(newGen) ) { return; }
		generatorName = newGen;
		initGenerator();
		initDefaultDistributions();
	}

	public long getSeed() {
		if ( seed == null ) { return BinaryUtils.convertBytesToLong(DefaultSeedGenerator
			.getInstance().generateSeed(8), 0); }
		return seed;
	}

	public String getGeneratorName() {
		return generatorName;
	}

	public Random getGenerator() {
		return generator;
	}

	public void shuffle(final List list) {
		for ( int i = list.size(); i > 1; i-- ) {
			swap(list, i - 1, between(0, i - 1));
		}
	}

	public String shuffle(final String string) {
		char[] c = string.toCharArray();
		shuffle(c);
		return String.copyValueOf(c);
	}

	public IMatrix shuffle(final IMatrix matrix) throws GamaRuntimeException {
		IMatrix matrix2 = (IMatrix) matrix.copy();
		matrix2.shuffleWith(this);
		return matrix2;
	}

	public <T> T[] shuffle(final T[] array) {
		T[] copy = array.clone();
		for ( int i = array.length; i > 1; i-- ) {
			int i1 = i - 1;
			int j = between(0, i - 1);
			T tmp = copy[i1];
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

	private static void swap(final List list, final int i, final int j) {
		Object tmp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, tmp);
	}

	/**
	 * @param matrix
	 * @return
	 */
	public double[] shuffle(final double[] array) {
		for ( int i = array.length; i > 1; i-- ) {
			int i1 = i - 1;
			int j = between(0, i - 1);
			double tmp = array[i1];
			array[i1] = array[j];
			array[j] = tmp;
		}
		return array;
	}

	public int[] shuffle(final int[] array) {
		for ( int i = array.length; i > 1; i-- ) {
			int i1 = i - 1;
			int j = between(0, i - 1);
			int tmp = array[i1];
			array[i1] = array[j];
			array[j] = tmp;
		}
		return array;
	}

	public char[] shuffle(final char[] array) {
		for ( int i = array.length; i > 1; i-- ) {
			int i1 = i - 1;
			int j = between(0, i - 1);
			char tmp = array[i1];
			array[i1] = array[j];
			array[j] = tmp;
		}
		return array;
	}

}
