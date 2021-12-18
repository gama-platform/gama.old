/*******************************************************************************************************
 *
 * RandomUtils.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.random.CellularAutomatonRNG;
import msi.gama.util.random.GamaRNG;
import msi.gama.util.random.JavaRNG;
import msi.gama.util.random.MersenneTwisterRNG;
import msi.gaml.operators.Maths;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class RandomUtils.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class RandomUtils {

	/** The seed. */
	protected Double seed;

	/** The Constant SEED_SOURCE. */
	private static final SecureRandom SEED_SOURCE = new SecureRandom();
	/** The generator name. */
	private String generatorName;
	/** The generator. */
	private GamaRNG generator;

	/**
	 * The Class State.
	 */
	public static class State {

		/**
		 * Instantiates a new state.
		 *
		 * @param seed
		 *            the seed
		 * @param generatorName
		 *            the generator name
		 * @param usage
		 *            the usage
		 */
		public State(final Double seed, final String generatorName, final int usage) {
			this.seed = seed;
			this.generatorName = generatorName;
			this.usage = usage;
		}

		/** The seed. */
		Double seed;

		/** The generator name. */
		String generatorName;

		/** The usage. */
		int usage;

	}

	/**
	 * Instantiates a new random utils.
	 *
	 * @param state
	 *            the state
	 */
	public RandomUtils(final State state) {
		setState(state);
	}

	/**
	 * Instantiates a new random utils.
	 *
	 * @param seed
	 *            the seed.
	 * @param rng
	 *            the rng
	 */
	public RandomUtils(final Double seed, final String rng) {
		setSeed(seed, false);
		setGenerator(rng, true);
	}

	/**
	 * Instantiates a new random utils.
	 *
	 * @param rng
	 *            the rng
	 */
	public RandomUtils(final String rng) {
		this(GamaPreferences.External.CORE_SEED_DEFINED.getValue() ? GamaPreferences.External.CORE_SEED.getValue()
				: null, rng);
	}

	/**
	 * Instantiates a new random utils.
	 */
	public RandomUtils() {
		this(GamaPreferences.External.CORE_RNG.getValue());
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public State getState() { return new State(seed, generatorName, generator.getUsage()); }

	/**
	 * Sets the state.
	 *
	 * @param state
	 *            the new state
	 */
	public void setState(final State state) {
		setSeed(state.seed, false);
		setGenerator(state.generatorName, true);
		generator.setUsage(state.usage);
	}

	/**
	 * Inits the generator.
	 */
	private void initGenerator() {
		if (IKeyword.CELLULAR.equals(generatorName)) {
			generator = new CellularAutomatonRNG(this);
		} else if (IKeyword.JAVA.equals(generatorName)) {
			generator = new JavaRNG(this);
		} else {
			/* By default */
			generator = new MersenneTwisterRNG(this);
		}
	}

	/**
	 * Sets the usage.
	 *
	 * @param usage
	 *            the new usage
	 */
	public void setUsage(final Integer usage) {
		generator.setUsage(usage);
	}

	/**
	 * Gets the usage.
	 *
	 * @return the usage
	 */
	public Integer getUsage() { return generator.getUsage(); }

	/**
	 * Creates a new Gaussian Generator object.
	 *
	 * @param mean
	 *            the mean
	 * @param stdv
	 *            the stdv
	 *
	 * @return the gaussian generator
	 */
	public double createGaussian(final double mean, final double stdv) {
		return generator.nextGaussian() * stdv + mean;
	}

	/**
	 * Creates a new Binomial Generator object.
	 *
	 * @param n
	 *            the n
	 * @param p
	 *            the p
	 *
	 * @return the binomial generator
	 */
	public int createBinomial(final int n, final double p) {

		double value = p;
		final StringBuilder bits = new StringBuilder(64);
		double bitValue = 0.5d;
		while (value > 0) {
			if (value >= bitValue) {
				bits.append('1');
				value -= bitValue;
			} else {
				bits.append('0');
			}
			bitValue /= 2;
		}
		final BitString pBits = new BitString(bits.toString());

		int trials = n;
		int totalSuccesses = 0;
		int pIndex = pBits.getLength() - 1;
		while (trials > 0 && pIndex >= 0) {
			final BitString bs = new BitString(trials, generator);
			final int successes = bs.countSetBits();
			trials -= successes;
			if (pBits.getBit(pIndex)) { totalSuccesses += successes; }
			--pIndex;
		}
		return totalSuccesses;

	}

	/**
	 * Creates a new Poisson Generator object.
	 *
	 * @param mean
	 *            the mean
	 *
	 * @return the poisson generator
	 */
	public int createPoisson(final double mean) {

		int x = 0;
		double t = 0.0;
		while (true) {
			t -= Math.log(next()) / mean;
			if (t > 1.0) { break; }
			++x;
		}
		return x;

	}

	/**
	 * Creates the seed.
	 *
	 * @param s
	 *            the s
	 * @param length
	 *            the length
	 * @return the byte[]
	 */
	private byte[] createSeed(final Double s, final int length) {
		this.seed = s;
		Double realSeed = seed;
		if (realSeed < 0) { realSeed *= -1; }
		if (realSeed < 1) { realSeed *= Long.MAX_VALUE; }
		long l = Double.doubleToRawLongBits(realSeed);
		final byte[] result = new byte[length];
		switch (length) {
			case 4:
				for (int i1 = 0; i1 < 4; i1++) {
					result[i1] = (byte) (l & 0xff);
					l >>= 8;
				}
				break;
			case 8:
				for (int i = 0; i < 8; i++) {
					result[i] = (byte) l;
					l >>= 8;
				}
				break;
			case 16:
				for (int i = 0; i < 8; i++) {
					result[i] = result[i + 8] = (byte) (l & 0xff);
					l >>= 8;
				}
		}
		return result;
	}

	/**
	 * Generate seed.
	 *
	 * @param length
	 *            the length
	 * @return the byte[]
	 */
	public byte[] generateSeed(final int length) {
		return createSeed(seed, length);
	}

	/**
	 * Sets the seed.
	 *
	 * @param newSeed
	 *            the new seed
	 * @param init
	 *            the init
	 */
	public void setSeed(final Double newSeed, final boolean init) {
		seed = newSeed;
		if (seed == null) { seed = SEED_SOURCE.nextDouble(); }
		if (init) { initGenerator(); }
	}

	/**
	 * Sets the generator.
	 *
	 * @param newGen
	 *            the new generator
	 */
	public void setGenerator(final String newGen, final boolean init) {
		generatorName = newGen;
		if (init) { initGenerator(); }
	}

	/**
	 * Shuffle in place.
	 *
	 * @param list
	 *            the list
	 */
	public void shuffleInPlace(final Collection list) {
		if (list == null) return;
		final int size = list.size();
		if (size < 2) return;
		final Object[] a = list.toArray(new Object[size]);
		list.clear();
		shuffleInPlace(a);
		list.addAll(Arrays.asList(a));
	}

	/**
	 * Shuffle in place.
	 *
	 * @param <T>
	 *            the generic type
	 * @param a
	 *            the a
	 */
	public <T> void shuffleInPlace(final T[] a) {
		for (int i = 0; i < a.length; i++) {
			final int change = between(i, a.length - 1);
			final T helper = a[i];
			a[i] = a[change];
			a[change] = helper;
		}
	}

	/**
	 * Shuffle in place.
	 *
	 * @param a
	 *            the a
	 */
	public void shuffleInPlace(final double[] a) {
		for (int i = 0; i < a.length; i++) {
			final int change = between(i, a.length - 1);
			final double helper = a[i];
			a[i] = a[change];
			a[change] = helper;
		}
	}

	/**
	 * Shuffle in place.
	 *
	 * @param a
	 *            the a
	 */
	public void shuffleInPlace(final int[] a) {
		for (int i = 0; i < a.length; i++) {
			final int change = between(i, a.length - 1);
			final int helper = a[i];
			a[i] = a[change];
			a[change] = helper;
		}
	}

	/**
	 * Shuffle in place.
	 *
	 * @param a
	 *            the a
	 */
	public void shuffleInPlace(final short[] a) {
		for (int i = 0; i < a.length; i++) {
			final int change = between(i, a.length - 1);
			final short helper = a[i];
			a[i] = a[change];
			a[change] = helper;
		}
	}

	/**
	 * Shuffle in place.
	 *
	 * @param a
	 *            the a
	 */
	public void shuffleInPlace(final char[] a) {
		for (int i = 0; i < a.length; i++) {
			final int change = between(i, a.length - 1);
			final char helper = a[i];
			a[i] = a[change];
			a[change] = helper;
		}
	}

	/**
	 * Shuffle in place.
	 *
	 * @param list
	 *            the list
	 */
	public void shuffleInPlace(final List list) {
		for (int i = list.size(); i > 1; i--) {
			final int i1 = i - 1;
			final int j = between(0, i - 1);
			final Object tmp = list.get(i1);
			list.set(i1, list.get(j));
			list.set(j, tmp);
		}
	}

	/**
	 * Shuffle.
	 *
	 * @param string
	 *            the string
	 * @return the string
	 */
	public String shuffle(final String string) {
		final char[] c = string.toCharArray();
		shuffleInPlace(c);
		return String.copyValueOf(c);
	}

	/**
	 * @return an uniformly distributed int random number in [from, to]
	 */
	public int between(final int min, final int max) {
		return (int) (min + (long) ((1L + max - min) * next()));
	}

	/**
	 * Between.
	 *
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the double
	 */
	public double between(final double min, final double max) {
		// uniformly distributed double random number in [min, max]
		return min + (max + Double.MIN_VALUE - min) * next();
	}

	/**
	 * @return an uniformly distributed int random number in [min, max] respecting the step
	 */
	public int between(final int min, final int max, final int step) {
		final int nbSteps = (max - min) / step;
		return min + between(0, nbSteps) * step;
	}

	/**
	 * Between.
	 *
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @return the double
	 */
	public double between(final double min, final double max, final double step) {
		// uniformly distributed double random number in [min, max] respecting
		// the step
		final double val = between(min, max);
		final int nbStep = (int) ((val - min) / step);
		final double valSup = Math.min(max, min + (nbStep + 1.0) * step);
		final double valMin = min + nbStep * step;
		final int precision = BigDecimal.valueOf(step).scale() + 5;

		final double high = Maths.round(valSup, precision);
		final double low = Maths.round(valMin, precision);
		return val - low < high - val ? low : high;
	}

	/**
	 * Next.
	 *
	 * @return the double
	 */
	public double next() {
		return generator.nextDouble();
	}

	/**
	 * @return
	 */
	public Double getSeed() { return seed; }

	/**
	 * @return
	 */
	public String getRngName() { return generatorName; }

	/**
	 * Test draw random values.
	 *
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 */
	public static void testDrawRandomValues(final double min, final double max, final double step) {
		DEBUG.LOG("Drawing 100 double between " + min + " and " + max + " step " + step);
		final RandomUtils r = new RandomUtils(100.0, "mersenne");
		for (int i = 0; i < 100; i++) {
			final double val = r.between(min, max);
			final int nbStep = (int) ((val - min) / step);
			final double high = (int) (Math.min(max, min + (nbStep + 1.0) * step) * 1000000) / 1000000.0;
			final double low = (int) ((min + nbStep * step) * 1000000) / 1000000.0;
			DEBUG.LOG(val - low < high - val ? low : high, false);
			DEBUG.LOG(" | ", false);
		}
	}

	/**
	 * Test draw random values.
	 *
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 */
	public static void testDrawRandomValues(final int min, final int max, final int step) {
		DEBUG.LOG("Drawing 100 int between " + min + " and " + max + " step " + step);
		final RandomUtils r = new RandomUtils(100.0, "mersenne");
		final int nbSteps = (max - min) / step;
		for (int i = 0; i < 100; i++) {
			final int val = min + r.between(0, nbSteps) * step;
			DEBUG.LOG(val + " | ", false);
		}
	}

	/**
	 * The Class BitString.
	 */
	private static class BitString {

		/** The Constant WORD_LENGTH. */
		private static final int WORD_LENGTH = 32;

		/** The length. */
		private final int length;

		/**
		 * Store the bits packed in an array of 32-bit ints. This field cannot be declared final because it must be
		 * cloneable.
		 */
		private final int[] data;

		/**
		 * Creates a bit string of the specified length with all bits initially set to zero (off).
		 *
		 * @param length
		 *            The number of bits.
		 */
		public BitString(final int length) {
			if (length < 0) throw new IllegalArgumentException("Length must be non-negative.");
			this.length = length;
			this.data = new int[(length + WORD_LENGTH - 1) / WORD_LENGTH];
		}

		/**
		 * Creates a bit string of the specified length with each bit set randomly (the distribution of bits is uniform
		 * so long as the output from the provided RNG is also uniform). Using this constructor is more efficient than
		 * creating a bit string and then randomly setting each bit individually.
		 *
		 * @param length
		 *            The number of bits.
		 * @param rng
		 *            A source of randomness.
		 */
		public BitString(final int length, final Random rng) {
			this(length);
			// We can set bits 32 at a time rather than calling
			// rng.nextBoolean()
			// and setting each one individually.
			for (int i = 0; i < data.length; i++) { data[i] = rng.nextInt(); }
			// If the last word is not fully utilised, zero any out-of-bounds
			// bits.
			// This is necessary because the countSetBits() methods will count
			// out-of-bounds bits.
			final int bitsUsed = length % WORD_LENGTH;
			if (bitsUsed < WORD_LENGTH) {
				final int unusedBits = WORD_LENGTH - bitsUsed;
				final int mask = 0xFFFFFFFF >>> unusedBits;
				data[data.length - 1] &= mask;
			}
		}

		/**
		 * Initialises the bit string from a character string of 1s and 0s in big-endian order.
		 *
		 * @param value
		 *            A character string of ones and zeros.
		 */
		public BitString(final String value) {
			this(value.length());
			for (int i = 0; i < value.length(); i++) {
				if (value.charAt(i) == '1') {
					setBit(value.length() - (i + 1), true);
				} else if (value.charAt(i) != '0')
					throw new IllegalArgumentException("Illegal character at position " + i);
			}
		}

		/**
		 * @return The length of this bit string.
		 */
		public int getLength() { return length; }

		/**
		 * Returns the bit at the specified index.
		 *
		 * @param index
		 *            The index of the bit to look-up (0 is the least-significant bit).
		 * @return A boolean indicating whether the bit is set or not.
		 * @throws IndexOutOfBoundsException
		 *             If the specified index is not a bit position in this bit string.
		 */
		public boolean getBit(final int index) {
			assertValidIndex(index);
			final int word = index / WORD_LENGTH;
			final int offset = index % WORD_LENGTH;
			return (data[word] & 1 << offset) != 0;
		}

		/**
		 * Sets the bit at the specified index.
		 *
		 * @param index
		 *            The index of the bit to set (0 is the least-significant bit).
		 * @param set
		 *            A boolean indicating whether the bit should be set or not.
		 * @throws IndexOutOfBoundsException
		 *             If the specified index is not a bit position in this bit string.
		 */
		public void setBit(final int index, final boolean set) {
			assertValidIndex(index);
			final int word = index / WORD_LENGTH;
			final int offset = index % WORD_LENGTH;
			if (set) {
				data[word] |= 1 << offset;
			} else // Unset the bit.
			{
				data[word] &= ~(1 << offset);
			}
		}

		/**
		 * Helper method to check whether a bit index is valid or not.
		 *
		 * @param index
		 *            The index to check.
		 * @throws IndexOutOfBoundsException
		 *             If the index is not valid.
		 */
		private void assertValidIndex(final int index) {
			if (index >= length || index < 0)
				throw new IndexOutOfBoundsException("Invalid index: " + index + " (length: " + length + ")");
		}

		/**
		 * @return The number of bits that are 1s rather than 0s.
		 */
		public int countSetBits() {
			int count = 0;
			for (int x : data) {
				while (x != 0) {
					x &= x - 1; // Unsets the least significant on bit.
					++count; // Count how many times we have to unset a bit
								// before x equals zero.
				}
			}
			return count;
		}

	}

	/**
	 * Gets the generator.
	 *
	 * @return the generator
	 */
	public GamaRNG getGenerator() { return generator; }

	/**
	 * One of.
	 *
	 * @param <K>
	 *            the key type
	 * @param c
	 *            the c
	 * @return the k
	 */
	public <K> K oneOf(final Collection<K> c) {
		if (c == null || c.isEmpty()) return null;
		return (K) oneOf(c.toArray());
	}

	/**
	 * One of.
	 *
	 * @param <K>
	 *            the key type
	 * @param c
	 *            the c
	 * @return the k
	 */
	public <K> K oneOf(final List<K> c) {
		if (c == null || c.isEmpty()) return null;
		return c.get(between(0, c.size() - 1));
	}

	/**
	 * One of.
	 *
	 * @param <K>
	 *            the key type
	 * @param c
	 *            the c
	 * @return the k
	 */
	public <K> K oneOf(final K[] c) {
		if (c == null || c.length == 0) return null;
		return c[between(0, c.length - 1)];

	}

	/**
	 * One of.
	 *
	 * @param c
	 *            the c
	 * @return the int
	 */
	public int oneOf(final int[] c) {
		if (c == null || c.length == 0) return -1;
		return c[between(0, c.length - 1)];
	}

	/**
	 * One of.
	 *
	 * @param c
	 *            the c
	 * @return the double
	 */
	public double oneOf(final double[] c) {
		if (c == null || c.length == 0) return -1;
		return c[between(0, c.length - 1)];
	}

	/**
	 * One of.
	 *
	 * @param c
	 *            the c
	 * @return true, if successful
	 */
	public boolean oneOf(final boolean[] c) {
		if (c == null || c.length == 0) return false;
		return c[between(0, c.length - 1)];
	}

	/**
	 * Between.
	 *
	 * @param pMin
	 *            the min
	 * @param pMax
	 *            the max
	 * @param pStep
	 *            the step
	 * @return the gama point
	 */
	public GamaPoint between(final GamaPoint pMin, final GamaPoint pMax, final GamaPoint pStep) {
		double x = between(pMin.x, pMax.x, pStep.x);
		double y = between(pMin.y, pMax.y, pStep.y);
		double z = between(pMin.z, pMax.z, pStep.z);
		return new GamaPoint(x, y, z);
	}

}
