/*******************************************************************************************************
 *
 * Random.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.matrix.GamaField;
import msi.gama.util.matrix.IField;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.GamaFieldType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.IntStreamEx;

/**
 * Written by drogoul Modified on 10 dec. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class Random {

	/**
	 * The Class BitString.
	 */
	private static class BitString {

		/** The Constant WORD_LENGTH. */
		private static final int WORD_LENGTH = 32;

		/** The length. */
		private final int length;

		/**
		 * Store the bits packed in an array of 32-bit ints.
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
		public BitString(final int length, final java.util.Random rng) {
			this(length);
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
	 * Random.
	 *
	 * @param scope
	 *            the scope
	 * @return the random utils
	 */
	public static RandomUtils RANDOM(final IScope scope) {
		RandomUtils r = scope.getRandom();
		if (r == null) { r = new RandomUtils(); }
		return r;
	}

	/**
	 * Op T gauss.
	 *
	 * @param scope
	 *            the scope
	 * @param p
	 *            the p
	 * @return the double
	 */
	@operator (
			value = { "truncated_gauss", "TGauss" },
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "A random value from a normally distributed random variable in the interval ]mean - standardDeviation; mean + standardDeviation[.",
			usages = { @usage (
					value = "when the operand is a point, it is read as {mean, standardDeviation}") },
			examples = { @example (
					value = "truncated_gauss ({0, 0.3})",
					equals = "a float between -0.3 and 0.3",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "rnd", "skew_gauss",
					"weibull_rnd", "gamma_trunc_rnd", "weibull_trunc_rnd", "lognormal_trunc_rnd" })
	@test ("seed <- 1.0; TGauss({0,0.3}) = 0.10073201959421514")
	public static Double opTGauss(final IScope scope, final GamaPoint p) {
		return opTGauss(scope, GamaListFactory.wrap(Types.FLOAT, p.x, p.y));
	}

	/**
	 * Op T gauss.
	 *
	 * @param scope
	 *            the scope
	 * @param list
	 *            the list
	 * @return the double
	 */
	@operator (
			value = { "truncated_gauss", "TGauss" },
			category = { IOperatorCategory.RANDOM },
			concept = {})
	@doc (
			usages = { @usage (
					value = "if the operand is a list, only the two first elements are taken into account as [mean, standardDeviation]"),
					@usage (
							value = "when truncated_gauss is called with a list of only one element mean, it will always return 0.0") },
			examples = { @example (
					value = "truncated_gauss ([0.5, 0.0])",
					equals = "0.5") },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "rnd", "skew_gauss",
					"weibull_rnd", "gamma_trunc_rnd", "weibull_trunc_rnd", "lognormal_trunc_rnd" })
	@test ("seed <- 1.0; truncated_gauss ([0.5, 0.2]) = 0.5671546797294768")
	public static Double opTGauss(final IScope scope, final IList list) {
		if (list.size() < 2) return 0d;
		final double mean = Cast.asFloat(scope, list.get(0));
		final double range = Cast.asFloat(scope, list.get(1));
		/*
		 * We want to have a real gamma like distribution though it s truncated one to do so we set that 2 stdDevation =
		 * deviation which means we will have 95% of the random generated number within ]mean - deviation; mean +
		 * deviation[ , thus in 5% of the time we will redo regenerate the number
		 */
		// double internalRange = bound / 2;
		double tmpResult = 0;
		// final GaussianGenerator gen = RANDOM(scope).createGaussian(mean,
		// range / 2);
		// 'do while' does the truncature

		do {
			// we use bound / 2 as a standard deviation because we want to have
			// stdDeviation = 2 * bound
			tmpResult = RANDOM(scope).createGaussian(mean, range / 2);
		} while (tmpResult > mean + range || tmpResult < mean - range);
		return tmpResult;

	}

	/**
	 * Op gauss.
	 *
	 * @param scope
	 *            the scope
	 * @param point
	 *            the point
	 * @return the double
	 */
	@operator (
			value = { "gauss", "gauss_rnd" },
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "The operator can be used with an operand of type point {meand,standardDeviation}.",
			usages = { @usage (
					value = "when the operand is a point, it is read as {mean, standardDeviation}") },
			examples = { @example (
					value = "gauss({0,0.3})",
					equals = "0.22354",
					test = false) },
			see = { "binomial", "gamma_rnd", "lognormal_rnd", "poisson", "rnd", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; gauss({0.5, 0.2}) = 0.6343093594589535")
	public static Double opGauss(final IScope scope, final GamaPoint point) {
		final double mean = point.x;
		final double sd = point.y;
		return RANDOM(scope).createGaussian(mean, sd);
	}

	/**
	 * Op gauss.
	 *
	 * @param scope
	 *            the scope
	 * @param mean
	 *            the mean
	 * @param sd
	 *            the sd
	 * @return the double
	 */
	@operator (
			value = { "gauss", "gauss_rnd" },
			category = { IOperatorCategory.RANDOM },
			concept = {})
	@doc (
			value = "A value from a normally distributed random variable with expected value (mean as first operand) and variance (standardDeviation as second operand). The probability density function of such a variable is a Gaussian.",
			usages = { @usage (
					value = "when standardDeviation value is 0.0, it always returns the mean value") },
			examples = { @example (
					value = "gauss(0,0.3)",
					equals = "0.22354",
					test = false) },
			see = { "binomial", "gamma_rnd", "lognormal_rnd", "poisson", "rnd", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; gauss(0.5, 0.2) = 0.6343093594589535")
	public static Double opGauss(final IScope scope, final double mean, final double sd) {
		return RANDOM(scope).createGaussian(mean, sd);
	}

	/**
	 * Op gauss.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param skew
	 *            the skew
	 * @param bias
	 *            the bias
	 * @return the double
	 */
	@operator (
			value = "skew_gauss",
			category = { IOperatorCategory.RANDOM },
			concept = {})
	@doc (
			value = "A value from a skew normally distributed random variable with min value (the minimum skewed value possible), max value (the maximum skewed value possible), skew (the degree to which the values cluster around the mode of the distribution; higher values mean tighter clustering) and bias (the tendency of the mode to approach the min, max or midpoint value; positive values bias toward max, negative values toward min)."
					+ "The algorithm was taken from http://stackoverflow.com/questions/5853187/skewing-java-random-number-generation-toward-a-certain-number",
			examples = { @example (
					value = "skew_gauss(0.0, 1.0, 0.7,0.1)",
					equals = "0.1729218460343077",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "rnd", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; skew_gauss(0.0, 1.0, 0.7,0.1) = 0.7425668006838585")
	public static Double opGauss(final IScope scope, final double min, final double max, final double skew,
			final double bias) {
		final double range = max - min;
		final double mid = min + range / 2.0;
		final double unitGaussian = RANDOM(scope).createGaussian(0.0, 1.0);
		final double biasFactor = Math.exp(bias);
		return mid + range * (biasFactor / (biasFactor + Math.exp(-unitGaussian / skew)) - 0.5);
	}

	/**
	 * Op poisson.
	 *
	 * @param scope
	 *            the scope
	 * @param mean
	 *            the mean
	 * @return the integer
	 */
	@operator (
			value = "poisson",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "A value from a random variable following a Poisson distribution (with the positive expected number of occurence lambda as operand).",
			comment = "The Poisson distribution is a discrete probability distribution that expresses the probability of a given number of events occurring in a fixed interval of time and/or space if these events occur with a known average rate and independently of the time since the last event, cf. Poisson distribution on Wikipedia.",
			examples = { @example (
					value = "poisson(3.5)",
					equals = "a random positive integer",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "rnd", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; poisson(3.5) = 6")
	public static Integer opPoisson(final IScope scope, final Double mean) {
		RandomUtils ru = RANDOM(scope);
		int x = 0;
		double t = 0.0;
		while (true) {
			t -= Math.log(ru.next()) / mean;
			if (t > 1.0) { break; }
			++x;
		}
		return x;
	}

	/**
	 * Op binomial.
	 *
	 * @param scope
	 *            the scope
	 * @param n
	 *            the n
	 * @param p
	 *            the p
	 * @return the integer
	 */
	@operator (
			value = "binomial",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "A value from a random variable following a binomial distribution. The operands represent the number of experiments n and the success probability p.",
			comment = "The binomial distribution is the discrete probability distribution of the number of successes in a sequence of n independent yes/no experiments, each of which yields success with probability p, cf. Binomial distribution on Wikipedia.",
			examples = { @example (
					value = "binomial(15,0.6)",
					equals = "a random positive integer",
					test = false) },
			see = { "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "rnd", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; binomial(15,0.6) = 9")
	public static Integer opBinomial(final IScope scope, final Integer n, final Double p) {
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
		RandomUtils ru = RANDOM(scope);
		int trials = n;
		int totalSuccesses = 0;
		int pIndex = pBits.getLength() - 1;
		while (trials > 0 && pIndex >= 0) {
			final BitString bs = new BitString(trials, ru.getGenerator());
			final int successes = bs.countSetBits();
			trials -= successes;
			if (pBits.getBit(pIndex)) { totalSuccesses += successes; }
			--pIndex;
		}
		return totalSuccesses;
	}

	/**
	 * Op shuffle.
	 *
	 * @param scope
	 *            the scope
	 * @param target
	 *            the target
	 * @return the i list
	 */
	@operator (
			value = "shuffle",
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.RANDOM, IOperatorCategory.CONTAINER },
			concept = { IConcept.RANDOM })
	@doc (
			value = "The elements of the operand in random order.",
			usages = { @usage (
					value = "if the operand is empty, returns an empty list (or string, matrix)") },
			examples = { @example (
					value = "shuffle ([12, 13, 14])",
					equals = "[14,12,13] (for example)",
					test = false) },
			see = { "reverse" })
	@test ("seed <- 1.0; shuffle ([12, 13, 14]) = [12,13,14]")
	public static IList opShuffle(final IScope scope, final IContainer target) {
		if (target == null || target.isEmpty(scope))
			return GamaListFactory.create(target == null ? Types.NO_TYPE : target.getGamlType().getContentType());
		final IList list = target.listValue(scope, target.getGamlType().getContentType(), false).copy(scope);
		RANDOM(scope).shuffleInPlace(list);
		return list;
	}

	// @operator(value = "shuffle", content_type =
	// ITypeProvider.CONTENT_TYPE_AT_INDEX + 1)
	// @doc(examples = { "shuffle (bug) --: shuffle the list of all agents of
	// the `bug` species" })
	// public static IList opShuffle(final IScope scope, final ISpecies target)
	// throws GamaRuntimeException {
	// return opShuffle(scope,
	// scope.getAgentScope().getPopulationFor(target).getAgentsList());
	// }

	/**
	 * Op shuffle.
	 *
	 * @param scope
	 *            the scope
	 * @param target
	 *            the target
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "shuffle",
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.RANDOM, IOperatorCategory.MATRIX },
			concept = { IConcept.RANDOM })
	@doc (
			examples = { @example (
					value = "shuffle (matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]))",
					equals = "matrix([[\"c12\",\"c21\",\"c11\"],[\"c13\",\"c22\",\"c23\"]]) (for example)",
					test = false) })
	@test ("seed <- 1.0; shuffle (matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]])) = matrix([[\"c13\",\"c21\",\"c22\"],[\"c11\",\"c23\",\"c12\"]])")
	public static IMatrix opShuffle(final IScope scope, final IMatrix target) throws GamaRuntimeException {
		final IMatrix matrix2 = target.copy(scope);
		matrix2.shuffleWith(RANDOM(scope));
		return matrix2;
	}

	/**
	 * Op shuffle.
	 *
	 * @param scope
	 *            the scope
	 * @param target
	 *            the target
	 * @return the string
	 */
	@operator (
			value = "shuffle",
			content_type = IType.STRING,
			category = { IOperatorCategory.RANDOM, IOperatorCategory.STRING },
			concept = { IConcept.RANDOM })
	@doc (
			examples = { @example (
					value = "shuffle ('abc')",
					equals = "'bac' (for example)",
					test = false) })
	@no_test
	// @test ("seed <- 1.0; shuffle ('abc') = 'abc'")
	public static String opShuffle(final IScope scope, final String target) {
		return RANDOM(scope).shuffle(target);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param max
	 *            the max
	 * @return the integer
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "returns a random value in a range (the type value depends on the operand type): when called with an integer, it returns a random integer in the interval [0, operand]",
			masterDoc = true,
			comment = "to obtain a probability between 0 and 1, use the expression (rnd n) / n, where n is used to indicate the precision",
			usages = {},
			examples = { @example (
					value = "rnd (2)",
					equals = "0, 1 or 2",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd(10) = 8")
	public static Integer opRnd(final IScope scope, final Integer max) {
		return opRnd(scope, 0, max);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the integer
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "a random integer in the interval [first operand, second operand]",
			examples = { @example (
					value = "rnd (2, 4)",
					equals = "2, 3 or 4",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd(1,5) = 4")
	public static Integer opRnd(final IScope scope, final Integer min, final Integer max) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @return the integer
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "a random integer in the interval [first operand, second operand], constrained by a step given by the last operand",
			examples = { @example (
					value = "rnd (2, 12, 4)",
					equals = "2, 6 or 10",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd (2, 12, 4) = 10")
	public static Integer opRnd(final IScope scope, final Integer min, final Integer max, final Integer step) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max, step);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the double
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "a random float in the interval [first operand, second operand]",
			examples = { @example (
					value = "rnd (2.0, 4.0)",
					equals = "a float number between 2.0 and 4.0",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd (2.0, 4.0) = 3.548024306042759")
	public static Double opRnd(final IScope scope, final Double min, final Double max) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @return the double
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "a random float in the interval [first operand, second operand] constrained by the last operand (step)",
			examples = { @example (
					value = "rnd (2.0, 4.0, 0.5)",
					equals = "a float number between 2.0 and 4.0 every 0.5",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd (2.0, 4.0, 0.5) = 3.5")
	public static Double opRnd(final IScope scope, final Double min, final Double max, final Double step) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max, step);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the gama point
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "a random point in the interval [first operand, second operand]",
			examples = { @example (
					value = "rnd ({2.0, 4.0}, {2.0, 5.0, 10.0})",
					equals = "a point with x = 2.0, y between 2.0 and 4.0 and z between 0.0 and 10.0",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd ({2.0, 4.0}, {2.0, 5.0, 10.0}) = {2.0,4.785039740667429,5.087825199078746}")
	public static GamaPoint opRnd(final IScope scope, final GamaPoint min, final GamaPoint max) {
		final double x = opRnd(scope, min.x, max.x);
		final double y = opRnd(scope, min.y, max.y);
		final double z = opRnd(scope, min.z, max.z);
		return new GamaPoint(x, y, z);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @return the gama point
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "a random point in the interval [first operand, second operand], constained by the step provided by the last operand",
			examples = { @example (
					value = "rnd ({2.0, 4.0}, {2.0, 5.0, 10.0}, 1)",
					equals = "a point with x = 2.0, y equal to 2.0, 3.0 or 4.0 and z between 0.0 and 10.0 every 1.0",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd ({2.0, 4.0}, {2.0, 5.0, 10.0},1) = {2.0,5.0,5.0}")
	public static GamaPoint opRnd(final IScope scope, final GamaPoint min, final GamaPoint max, final Double step) {
		final double x = opRnd(scope, min.x, max.x, step);
		final double y = opRnd(scope, min.y, max.y, step);
		final double z = opRnd(scope, min.z, max.z, step);
		return new GamaPoint(x, y, z);
	}

	/** The null point. */
	static GamaPoint NULL_POINT = new GamaPoint(0, 0, 0);

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param max
	 *            the max
	 * @return the gama point
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = {})
	@doc (
			usages = { @usage (
					value = "if the operand is a point, returns a point with three random float ordinates, each in the interval [0, ordinate of argument]") },
			examples = { @example (
					value = "rnd ({2.5,3, 0.0})",
					equals = "{x,y} with x in [0.0,2.0], y in [0.0,3.0], z = 0.0",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test ("seed <- 1.0; rnd ({2.5,3, 1.0}) = {1.935030382553449,2.3551192220022856,0.5087825199078746}")
	public static GamaPoint opRnd(final IScope scope, final GamaPoint max) {
		return opRnd(scope, NULL_POINT, max);
	}

	/**
	 * Op rnd.
	 *
	 * @param scope
	 *            the scope
	 * @param max
	 *            the max
	 * @return the double
	 */
	@operator (
			value = "rnd",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			usages = { @usage (
					value = "if the operand is a float, returns an uniformly distributed float random number in [0.0, to]") },
			examples = { @example (
					value = "rnd(3.4)",
					equals = "a random float between 0.0 and 3.4",
					test = false) },
			see = { "binomial", "gamma_rnd", "gauss_rnd", "lognormal_rnd", "poisson", "skew_gauss", "truncated_gauss",
					"weibull_rnd" })
	@test (" seed <- 1.0; rnd(100) = 78")
	public static Double opRnd(final IScope scope, final Double max) {
		return opRnd(scope, 0.0, max);
	}

	/**
	 * Op flip.
	 *
	 * @param scope
	 *            the scope
	 * @param probability
	 *            the probability
	 * @return the boolean
	 */
	@operator (
			value = "flip",
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "true or false given the probability represented by the operand",
			usages = { @usage (
					value = "flip 0 always returns false, flip 1 true") },
			examples = { @example (
					value = "flip (0.66666)",
					equals = "2/3 chances to return true.",
					test = false) },
			see = { "rnd" })
	@test ("flip(0) = false and flip(1) = true")
	public static Boolean opFlip(final IScope scope, final Double probability) {
		return probability > RANDOM(scope).between(0., 1.);
	}

	/**
	 * Op rnd choice.
	 *
	 * @param scope
	 *            the scope
	 * @param distribution
	 *            the distribution
	 * @return the integer
	 */
	@operator (
			value = "rnd_choice",
			concept = { IConcept.RANDOM })
	@doc (
			value = "returns an index of the given list with a probability following the (normalized) distribution described in the list (a form of lottery)",
			examples = { @example (
					value = "rnd_choice([0.2,0.5,0.3])",
					equals = "2/10 chances to return 0, 5/10 chances to return 1, 3/10 chances to return 2",
					test = false) },
			see = { "rnd" })
	@test ("seed <- 1.0; rnd_choice([0.2,0.5,0.3]) = 2")
	public static Integer opRndChoice(final IScope scope, final IList distribution) {
		final IList<Double> normalizedDistribution = GamaListFactory.create(Types.FLOAT);
		double sumElt = 0.0;
		Double minVal = 0.0;
		for (final Object eltDistrib : distribution) {
			final Double elt = Cast.asFloat(scope, eltDistrib);
			if (elt < 0.0) { minVal = Math.max(minVal, Math.abs(elt)); }
			// throw GamaRuntimeException.create(new RuntimeException("Distribution elements should be positive."),
			// scope);
			normalizedDistribution.add(elt);
			sumElt = sumElt + elt;
		}
		int nb = normalizedDistribution.size();
		if (minVal > 0) { sumElt += minVal * nb; }
		if (sumElt == 0.0) throw GamaRuntimeException
				.create(new RuntimeException("Distribution elements should not be all equal to 0"), scope);
		for (int i = 0; i < nb; i++) {
			normalizedDistribution.set(i, (normalizedDistribution.get(i) + minVal) / sumElt);
		}

		double randomValue = RANDOM(scope).between(0., 1.);

		for (int i = 0; i < distribution.size(); i++) {
			randomValue = randomValue - normalizedDistribution.get(i);
			if (randomValue <= 0) return i;
		}

		return -1;
	}

	/**
	 * Op rnd coice.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param distribution
	 *            the distribution
	 * @return the t
	 */
	@operator (
			value = "rnd_choice",
			concept = { IConcept.RANDOM },
			type = ITypeProvider.KEY_TYPE_AT_INDEX + 1)
	@doc (
			value = "returns a key from the map with a probability following the (normalized) distribution described in map values (a form of lottery)",
			examples = { @example (
					value = "rnd_choice([\"toto\"::0.2,\\\"tata\\\"::0.5,\\\"tonton\\\"::0.3])",
					equals = "2/10 chances to return \"toto\", 5/10 chances to return \"tata\", 3/10 chances to return \"tonton\"",
					test = false) },
			see = { "rnd" })
	@test ("seed <- 1.0; rnd_choice([\"toto\"::0.2,\"tata\"::0.5,\"tonton\"::0.3]) = \"tonton\"")
	public static <T> T opRndCoice(final IScope scope, final IMap<T, ?> distribution) {
		final IList<T> key = distribution.getKeys();
		final IList<Double> normalizedDistribution = GamaListFactory.create(Types.FLOAT);
		double sumElt = 0.0;

		for (final T k : key) {
			Object eltDistrib = distribution.get(k);
			final Double elt = Cast.asFloat(scope, eltDistrib);
			if (elt < 0.0) throw GamaRuntimeException
					.create(new RuntimeException("Distribution elements should be positive."), scope);
			normalizedDistribution.add(elt);
			sumElt = sumElt + elt;
		}
		if (sumElt == 0.0) throw GamaRuntimeException
				.create(new RuntimeException("Distribution elements should not be all equal to 0"), scope);

		for (int i = 0; i < normalizedDistribution.size(); i++) {
			normalizedDistribution.set(i, normalizedDistribution.get(i) / sumElt);
		}

		double randomValue = RANDOM(scope).between(0., 1.);

		for (int i = 0; i < distribution.size(); i++) {
			randomValue = randomValue - normalizedDistribution.get(i);
			if (randomValue <= 0) return key.get(i);
		}

		throw GamaRuntimeException.create(new RuntimeException("Malformed distribution"), scope);
	}

	/**
	 * Op sample.
	 *
	 * @param scope
	 *            the scope
	 * @param x
	 *            the x
	 * @param nb
	 *            the nb
	 * @param replacement
	 *            the replacement
	 * @return the i list
	 */
	@operator (
			value = "sample",
			type = ITypeProvider.TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.RANDOM },
			concept = { IConcept.RANDOM })
	@doc (
			value = "takes a sample of the specified size from the elements of x using either with or without replacement",
			examples = { @example (
					value = "sample([2,10,1],2,false)",
					equals = "[10,1]",
					test = false) })
	@test ("seed <- 1.0; " + "list l1 <- sample([2,10,1],2,false);\r\n" + "		list l2 <-  [1,10];" + "l1 = l2")
	public static IList opSample(final IScope scope, final IList x, final int nb, final boolean replacement) {
		if (nb < 0.0) throw GamaRuntimeException
				.create(new RuntimeException("The number of elements of the sample should be positive."), scope);
		final IList result = GamaListFactory.create(x.getGamlType());
		final IList source = replacement ? x : x.copy(scope);
		while (result.size() < nb && !source.isEmpty()) {
			final int i = scope.getRandom().between(0, source.size() - 1);
			if (replacement) {
				result.add(source.get(i));
			} else {
				result.add(source.remove(i));
			}
		}
		return result;
	}

	/**
	 * Op sample.
	 *
	 * @param scope
	 *            the scope
	 * @param x
	 *            the x
	 * @param nb
	 *            the nb
	 * @param replacement
	 *            the replacement
	 * @param weights
	 *            the weights
	 * @return the i list
	 */
	@operator (
			value = "sample",
			type = ITypeProvider.TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.RANDOM },
			concept = {})
	@doc (
			value = "takes a sample of the specified size from the elements of x using either with or without replacement with given weights",
			examples = { @example (
					value = "sample([2,10,1],2,false,[0.1,0.7,0.2])",
					equals = "[10,2]",
					test = false) })
	@test ("seed <- 1.0;\r\n" + "		list l1 <- sample([2,10,1],2,false,[0.1,0.7,0.2]);\r\n"
			+ "		list l2 <-  [10,1];\r\n" + "		l1 = l2 ")
	public static IList opSample(final IScope scope, final IList x, final int nb, final boolean replacement,
			final IList weights) {
		if (weights == null) return opSample(scope, x, nb, replacement);
		if (nb < 0.0) throw GamaRuntimeException
				.create(new RuntimeException("The number of elements of the sample should be positive."), scope);
		if (weights.size() != x.size()) throw GamaRuntimeException.create(
				new RuntimeException("The number of weights should be equal to the number of elements of the source."),
				scope);
		final IList result = GamaListFactory.create(x.getGamlType());
		final IList source = replacement ? x : x.copy(scope);
		final IList weights_s = replacement ? weights : weights.copy(scope);
		while (result.size() < nb && !source.isEmpty()) {
			final int i = opRndChoice(scope, weights_s);
			if (replacement) {
				result.add(source.get(i));
			} else {
				result.add(source.remove(i));
				weights_s.remove(i);
			}
		}
		return result;
	}

	/** The supply. */
	// this contains all the numbers between 0 and 255, these are put in a random order depending upon the seed
	private final static short SUPPLY[] = IntStreamEx.rangeClosed(0, 255).toShortArray();

	/** The gradient. */
	private final static int GRADIENT[] =
			{ 1, 1, -1, 1, 1, -1, -1, -1, 1, 0, -1, 0, 1, 0, -1, 0, 0, 1, 0, -1, 0, 1, 0, -1 };

	/** The Constant F2. */
	// Skewing and unskewing factors for 2, 3, and 4 dimensions
	private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);

	/** The Constant G2. */
	private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

	/**
	 * Generate terrain.
	 *
	 * @param scope
	 *            the scope
	 * @param seed
	 *            the seed
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param details
	 *            the details
	 * @param smoothness
	 *            the smoothness
	 * @param scattering
	 *            the scattering
	 * @return the i field
	 */
	@operator (
			value = "generate_terrain")
	@doc ("This operator allows to generate a pseudo-terrain using a simplex noise generator. Its usage is kept simple: it takes first a seed (random or not), then the dimensions "
			+ "(width and height) of the field to generate, then a level (between 0 and 1) of details (which actually determines the number of passes to make)"
			+ ", then the value (between 0 and 1) of smoothess, with 0 being completely rought and 1 super smooth, and finally the value (between 0 and 1) of "
			+ "scattering, with 0 building maps in 'one piece' and 1 completely scattered ones.")
	@no_test
	public static IField generateTerrain(final IScope scope, final int seed, final int width, final int height,
			final double details, final double smoothness, final double scattering) {

		RandomUtils rand = new RandomUtils((double) seed, IKeyword.MERSENNE);
		final short p[] = SUPPLY.clone();
		rand.shuffleInPlace(p);

		// Permutations
		final short perm[] = new short[512];
		final short permMod12[] = new short[512];

		for (int i = 0; i < 512; i++) {
			short s = p[i & 255];
			perm[i] = s;
			permMod12[i] = (short) (s % 12);
		}
		// smoothness between 0 (very rough) to 1 (very smooth)
		double roughness = smoothness <= 0 ? 1 : smoothness >= 1 ? 0 : 1 - smoothness;
		// scattering between 0 (in one piece) to 1 (completely scattered)
		double scale = scattering <= 0 ? 0.0001 : scattering >= 1 ? 0.01 : scattering / 100d;
		// details between 0 (1 octave) and 1 (10 octaves)
		int octaves = details < 0.1 ? 1 : details >= 1 ? 10 : (int) (details * 10);
		GamaField result = (GamaField) GamaFieldType.buildField(scope, width, height);
		double[] totalNoise = result.getMatrix();
		double layerWeight = 1d;
		double weightSum = 0d;
		double n0, n1, n2; // Noise contributions from the three corners
		int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
		for (int octave = 0; octave < octaves; octave++) {
			// Calculate single layer/octave of simplex noise, then add it to total noise
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					double xin = x * scale, yin = y * scale;
					// Skew the input space to determine which simplex cell we're in
					double s = (xin + yin) * F2; // Hairy factor for 2D
					int i = Maths.floor(xin + s), j = Maths.floor(yin + s);
					double t = (i + j) * G2, X0 = i - t, Y0 = j - t, x0 = xin - X0, y0 = yin - Y0;
					// For the 2D case, the simplex shape is an equilateral triangle.
					// Determine which simplex we are in.
					if (x0 > y0) {
						i1 = 1;
						j1 = 0;
					} // lower triangle, XY order: (0,0)->(1,0)->(1,1)
					else {
						i1 = 0;
						j1 = 1;
					} // upper triangle, YX order: (0,0)->(0,1)->(1,1)
					double x1 = x0 - i1 + G2, y1 = y0 - j1 + G2, x2 = x0 - 1.0 + 2.0 * G2, y2 = y0 - 1.0 + 2.0 * G2;
					// Work out the hashed gradient indices of the three simplex corners
					int ii = i & 255, jj = j & 255, gi0 = permMod12[ii + perm[jj]],
							gi1 = permMod12[ii + i1 + perm[jj + j1]], gi2 = permMod12[ii + 1 + perm[jj + 1]];
					// Calculate the contribution from the three corners
					double t0 = 0.5 - x0 * x0 - y0 * y0;
					if (t0 < 0) {
						n0 = 0.0;
					} else {
						t0 *= t0;
						n0 = t0 * t0 * (GRADIENT[gi0] * x0 + GRADIENT[gi0 + 1] * y0);
					}
					double t1 = 0.5 - x1 * x1 - y1 * y1;
					if (t1 < 0) {
						n1 = 0.0;
					} else {
						t1 *= t1;
						n1 = t1 * t1 * (GRADIENT[gi1] * x1 + GRADIENT[gi1 + 1] * y1);
					}
					double t2 = 0.5 - x2 * x2 - y2 * y2;
					if (t2 < 0) {
						n2 = 0.0;
					} else {
						t2 *= t2;
						n2 = t2 * t2 * (GRADIENT[gi2] * x2 + GRADIENT[gi2 + 1] * y2);
					}
					// Add contributions from each corner to get the final noise value.
					// The result is scaled to return values in the interval [-1,1].
					totalNoise[y * width + x] += 70.0 * (n0 + n1 + n2) * layerWeight;
				}
			}
			// Increase variables with each incrementing octave
			scale *= 2;
			weightSum += layerWeight;
			layerWeight *= roughness;
		}
		for (int x = 0; x < totalNoise.length; x++) { totalNoise[x] /= weightSum; }
		return result;
	}

}
