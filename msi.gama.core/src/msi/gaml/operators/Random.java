/*******************************************************************************************************
 *
 * Random.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.operators;

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
import msi.gaml.operators.noise.SimplexNoise2;
import msi.gaml.types.GamaFieldType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 10 dec. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class Random {

	/**
	 * Random.
	 *
	 * @param scope the scope
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
	 * @param scope the scope
	 * @param p the p
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
	 * @param scope the scope
	 * @param list the list
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
	 * @param scope the scope
	 * @param point the point
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
	 * @param scope the scope
	 * @param mean the mean
	 * @param sd the sd
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
	 * @param scope the scope
	 * @param min the min
	 * @param max the max
	 * @param skew the skew
	 * @param bias the bias
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
	 * @param scope the scope
	 * @param mean the mean
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
		return RANDOM(scope).createPoisson(mean);
	}

	/**
	 * Op binomial.
	 *
	 * @param scope the scope
	 * @param n the n
	 * @param p the p
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
		return RANDOM(scope).createBinomial(n, p);
	}

	/**
	 * Op shuffle.
	 *
	 * @param scope the scope
	 * @param target the target
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
	 * @param scope the scope
	 * @param target the target
	 * @return the i matrix
	 * @throws GamaRuntimeException the gama runtime exception
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
	 * @param scope the scope
	 * @param target the target
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
	 * @param scope the scope
	 * @param max the max
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
	 * @param scope the scope
	 * @param min the min
	 * @param max the max
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
	 * @param scope the scope
	 * @param min the min
	 * @param max the max
	 * @param step the step
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
	 * @param scope the scope
	 * @param min the min
	 * @param max the max
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
	 * @param scope the scope
	 * @param min the min
	 * @param max the max
	 * @param step the step
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
	 * @param scope the scope
	 * @param min the min
	 * @param max the max
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
	 * @param scope the scope
	 * @param min the min
	 * @param max the max
	 * @param step the step
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
	 * @param scope the scope
	 * @param max the max
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
	 * @param scope the scope
	 * @param max the max
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
	 * @param scope the scope
	 * @param probability the probability
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
	 * @param scope the scope
	 * @param distribution the distribution
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
		Double sumElt = 0.0;

		for (final Object eltDistrib : distribution) {
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
			if (randomValue <= 0) return i;
		}

		return -1;
	}

	/**
	 * Op rnd coice.
	 *
	 * @param <T> the generic type
	 * @param scope the scope
	 * @param distribution the distribution
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
		Double sumElt = 0.0;

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
	 * @param scope the scope
	 * @param x the x
	 * @param nb the nb
	 * @param replacement the replacement
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
	 * @param scope the scope
	 * @param x the x
	 * @param nb the nb
	 * @param replacement the replacement
	 * @param weights the weights
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

	// @operator (
	// value = "simplex_generator",
	// category = { IOperatorCategory.RANDOM },
	// concept = {})
	// @doc (
	// value = "take a x, y and a bias parameters and gives a value",
	// examples = { @example (
	// value = "simplex_generator(2,3,253)",
	// equals = "0.0976676931220678",
	// test = false) })
	// @test ("simplex_generator(2,3,253) = 0.0976676931220678")
	// public static Double simplex_generator(final IScope scope, final double x, final double y, final double biais) {
	// return SimplexNoise.noise(x, y, biais);
	// }

	/**
	 * Generate terrain.
	 *
	 * @param scope the scope
	 * @param seed the seed
	 * @param width the width
	 * @param height the height
	 * @param details the details
	 * @param smoothness the smoothness
	 * @param scattering the scattering
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

		SimplexNoise2 simplex = new SimplexNoise2(seed);

		// smoothness between 0 (very rough) to 1 (very smooth)
		double roughness = smoothness <= 0 ? 1 : smoothness >= 1 ? 0 : 1 - smoothness;

		// scattering between 0 (in one piece) to 1 (completely scattered)
		double scale = scattering <= 0 ? 0.0001 : scattering >= 1 ? 0.01 : scattering / 100d;

		// details between 0 (1 octave) and 1 (10 octaves)
		int octaves = details < 0.1 ? 1 : details >= 1 ? 10 : (int) (details * 10d);

		GamaField result = (GamaField) GamaFieldType.buildField(scope, width, height);
		double[] totalNoise = result.getMatrix();

		double layerWeight = 1d;
		double weightSum = 0d;

		for (int octave = 0; octave < octaves; octave++) {
			// Calculate single layer/octave of simplex noise, then add it to total noise
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					totalNoise[y * width + x] += simplex.noise(x * scale, y * scale) * layerWeight;
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
