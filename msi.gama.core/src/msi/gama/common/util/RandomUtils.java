/*******************************************************************************************************
 *
 * RandomUtils.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gama.util.random.IGamaRNG;
import msi.gama.util.random.JavaRNG;
import msi.gama.util.random.MersenneTwisterRNG;
import msi.gama.util.random.ParallelMersenneTwisterRNG;
import msi.gama.util.random.ThreadLocalRNG;
import msi.gaml.operators.Maths;

/**
 * The Class RandomUtils.
 */

/**
 * The Class RandomUtils.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class RandomUtils {

	/** The Constant DOC. */
	public static final String DOC =
			"The random number generator to use. Four different ones are at the disposal of the modeler: " + "'"
					+ IKeyword.MERSENNE + "'"
					+ " represents the default generator, based on the Mersenne-Twister algorithm. Very reliable, fast and deterministic (that is, using the same seed and the same sequence of calls, it will return the same stream of pseudo-random numbers). This algorithm is however not safe to use in simulations where agents can behave in parallel; "
					+ "'threaded'"
					+ " is a very fast generator, based on the DotMix algorithm, that can be safely used in parallel simulations as it creates one instance per thread. However, determinism cannot be guaranteed and this algorithm does not accept a seed as each instance will compute its own;"
					+ "'" + IKeyword.PARALLEL + "'"
					+ " is a version of the Mersenne-Twister algorithm that can be safely used in parallel simulations by preventing a concurrent access to its internal state. Determinism is guaranteed (in terms of generation, but not in terms of execution, as the sequence in which the threads will access it cannot be determined) and it performs a bit slower than its base version."
					+ "'" + IKeyword.JAVA + "'"
					+ " invokes the standard generator provided by the JDK, deterministic and thread-safe, albeit slower than all the other ones";

	/**
	 * The Enum GeneratorNames.
	 */
	public enum Generators {
		/** The mersenne. */
		MERSENNE(IKeyword.MERSENNE,
				" represents the default generator, based on the Mersenne-Twister algorithm. Very reliable, fast and deterministic (that is, using the same seed and the same sequence of calls, it will return the same stream of pseudo-random numbers). This algorithm is however not safe to use in simulations where agents can behave in parallel; "),
		/** The parallel. */
		PARALLEL(IKeyword.PARALLEL,
				" is a version of the Mersenne-Twister algorithm that can be safely used in parallel simulations by preventing a concurrent access to its internal state. Determinism is guaranteed (in terms of generation, but not in terms of execution, as the sequence in which the threads will access it cannot be determined) and it performs a bit slower than its base version; "),
		/** The java. */
		JAVA(IKeyword.JAVA,
				" invokes the standard generator provided by the JDK, deterministic and thread-safe, albeit slower than all the other ones; "),
		/** The threaded. */
		THREADED("threaded",
				" is a very fast generator, based on the DotMix algorithm, that can be safely used in parallel simulations as it creates one instance per thread. However, determinism cannot be guaranteed and this algorithm does not accept a seed as each instance will compute its own; ");

		/** The name. */
		private String name;

		/**
		 * Environment.
		 *
		 * @param envUrl
		 *            the env url
		 */
		Generators(final String name, final String doc) {
			this.name = name;
		}

		/**
		 * Gets the url.
		 *
		 * @return the url
		 */
		public String getName() { return name; }

		// ****** Reverse Lookup ************//

		/**
		 * Gets the
		 *
		 * @param url
		 *            the url
		 * @return the optional
		 */
		public static Generators get(final String url) {
			return Arrays.stream(values()).filter(env -> env.name.equals(url)).findFirst().orElse(null);
		}

		/**
		 * Names.
		 *
		 * @return the list
		 */
		public static List<String> names() {
			return Arrays.stream(values()).map(e -> e.name).toList();
		}
	}

	/** The Constant SEED_SOURCE. */
	private static final SecureRandom SEED_SOURCE = new SecureRandom();

	/** The seed. */
	protected Double seed;
	/** The generator name. */
	private String generatorName;
	/** The generator. */
	private IGamaRNG generator;

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
	 * Inits the generator.
	 */
	private void initGenerator() {
		generator = switch (Generators.get(generatorName)) {
			case JAVA -> new JavaRNG(this);
			case THREADED -> new ThreadLocalRNG(this);
			case PARALLEL -> new ParallelMersenneTwisterRNG(this);
			default -> new MersenneTwisterRNG(this);
		};

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
	 * Creates the seed.
	 *
	 * @param s
	 *            the s
	 * @param length
	 *            the length
	 * @return the byte[]
	 */
	public byte[] generateSeed(final int length) {
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
	 * Gets the generator.
	 *
	 * @return the generator
	 */
	public Random getGenerator() { return generator.getRandomGenerator(); }

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
