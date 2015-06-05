/*********************************************************************************************
 *
 *
 * 'Random.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 *
 * @todo Description
 *
 */
public class Random {

	public static RandomUtils RANDOM(final IScope scope) {
		RandomUtils r = scope.getRandom();
		if ( r == null ) {
			r = new RandomUtils();
		}
		return r;
	}

	@operator(value = { "truncated_gauss", "TGauss" }, category = { IOperatorCategory.RANDOM })
	@doc(value = "A random value from a normally distributed random variable in the interval ]mean - standardDeviation; mean + standardDeviation[.",
		usages = { @usage(value = "when the operand is a point, it is read as {mean, standardDeviation}") },
		examples = { @example(value = "truncated_gauss ({0, 0.3})",
			equals = "an float between -0.3 and 0.3",
			test = false) }, see = { "gauss" })
	public static
		Double opTGauss(final IScope scope, final GamaPoint p) {
		return opTGauss(scope, GamaListFactory.createWithoutCasting(Types.FLOAT, p.x, p.y));
	}

	@operator(value = { "truncated_gauss", "TGauss" }, category = { IOperatorCategory.RANDOM })
	@doc(usages = {
		@usage(value = "if the operand is a list, only the two first elements are taken into account as [mean, standardDeviation]"),
		@usage(value = "when truncated_gauss is called with a list of only one element mean, it will always return 0.0") },
		examples = { @example(value = "truncated_gauss ([0.5, 0.0])", equals = "0.5") })
	public static
		Double opTGauss(final IScope scope, final IList list) {
		if ( list.size() < 2 ) { return 0d; }
		final double mean = Cast.asFloat(scope, list.get(0));
		final double range = Cast.asFloat(scope, list.get(1));
		/*
		 * We want to have a real gamma like distribution though it s truncated one to do so we set
		 * that 2 stdDevation = deviation which means we will have 95% of the random generated
		 * number within ]mean - deviation; mean + deviation[ , thus in 5% of the time we will redo
		 * regenerate the number
		 */
		// double internalRange = bound / 2;
		double tmpResult = 0;
		// final GaussianGenerator gen = RANDOM(scope).createGaussian(mean, range / 2);
		// 'do while' does the truncature

		do {
			// we use bound / 2 as a standard deviation because we want to have
			// stdDeviation = 2 * bound
			tmpResult = RANDOM(scope).createGaussian(mean, range / 2);
		} while (tmpResult > mean + range || tmpResult < mean - range);
		return tmpResult;

	}

	@operator(value = "gauss", category = { IOperatorCategory.RANDOM })
	@doc(value = "A value from a normally distributed random variable with expected value (mean) and variance (standardDeviation). The probability density function of such a variable is a Gaussian.",
		usages = { @usage(value = "when the operand is a point, it is read as {mean, standardDeviation}"),
			@usage(value = "when standardDeviation value is 0.0, it always returns the mean value") },
		examples = { @example(value = "gauss({0,0.3})", equals = "0.22354", test = false),
			@example(value = "gauss({0,0.3})", equals = "-0.1357", test = false) },
		see = { "truncated_gauss", "poisson" })
	public static
		Double opGauss(final IScope scope, final GamaPoint point) {
		final double mean = point.x;
		final double sd = point.y;
		return RANDOM(scope).createGaussian(mean, sd);
	}

	@operator(value = "gauss", category = { IOperatorCategory.RANDOM })
	@doc(value = "A value from a normally distributed random variable with expected value (mean) and variance (standardDeviation). The probability density function of such a variable is a Gaussian.",
		usages = { @usage(value = "when the operand is a point, it is read as {mean, standardDeviation}"),
			@usage(value = "when standardDeviation value is 0.0, it always returns the mean value") },
		examples = { @example(value = "gauss(0,0.3)", equals = "0.22354", test = false),
			@example(value = "gauss(0,0.3)", equals = "-0.1357", test = false) },
		see = { "truncated_gauss", "poisson" })
	public static
		Double opGauss(final IScope scope, final double mean, final double sd) {
		return RANDOM(scope).createGaussian(mean, sd);
	}

	@operator(value = "poisson", category = { IOperatorCategory.RANDOM })
	@doc(value = "A value from a random variable following a Poisson distribution (with the positive expected number of occurence lambda as operand).",
		comment = "The Poisson distribution is a discrete probability distribution that expresses the probability of a given number of events occurring in a fixed interval of time and/or space if these events occur with a known average rate and independently of the time since the last event, cf. Poisson distribution on Wikipedia.",
		examples = { @example(value = "poisson(3.5)", equals = "a random positive integer", test = false) },
		see = { "binomial", "gauss" })
	public static
		Integer opPoisson(final IScope scope, final Double mean) {
		return RANDOM(scope).createPoisson(mean);
	}

	@operator(value = "binomial", category = { IOperatorCategory.RANDOM })
	@doc(deprecated = " Use binomial(int, float) instead",
		value = "A value from a random variable following a binomial distribution. The operand {n,p} represents the number of experiments n and the success probability p.",
		comment = "The binomial distribution is the discrete probability distribution of the number of successes in a sequence of n independent yes/no experiments, each of which yields success with probability p, cf. Binomial distribution on Wikipedia.",
		examples = { @example(value = "binomial({15,0.6})", equals = "a random positive integer", test = false) },
		see = { "poisson", "gauss" })
	public static
		Integer opBinomial(final IScope scope, final GamaPoint point) {
		final Integer n = (int) point.x;
		final Double p = point.y;
		return opBinomial(scope, n, p);
	}

	@operator(value = "binomial", category = { IOperatorCategory.RANDOM })
	@doc(value = "A value from a random variable following a binomial distribution. The operands represent the number of experiments n and the success probability p.",
		comment = "The binomial distribution is the discrete probability distribution of the number of successes in a sequence of n independent yes/no experiments, each of which yields success with probability p, cf. Binomial distribution on Wikipedia.",
		examples = { @example(value = "binomial(15,0.6)", equals = "a random positive integer", test = false) },
		see = { "poisson", "gauss" })
	public static
		Integer opBinomial(final IScope scope, final Integer n, final Double p) {
		return RANDOM(scope).createBinomial(n, p);
	}

	@operator(value = "shuffle", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
		IOperatorCategory.RANDOM, IOperatorCategory.CONTAINER })
	@doc(value = "The elements of the operand in random order.",
		usages = { @usage(value = "if the operand is empty, returns an empty list (or string, matrix)") },
		examples = { @example(value = "shuffle ([12, 13, 14])", equals = "[14,12,13] (for example)", test = false) },
		see = { "reverse" })
	public static IList opShuffle(final IScope scope, final IContainer target) {
		if ( target == null || target.isEmpty(scope) ) { return GamaListFactory.create(target == null ? Types.NO_TYPE
			: target.getType().getContentType()); }
		final IList list = (IList) target.listValue(scope, target.getType().getContentType(), false).copy(scope);
		RANDOM(scope).shuffle(list);
		return list;
	}

	// @operator(value = "shuffle", content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	// @doc(examples = { "shuffle (bug) --:  shuffle the list of all agents of the `bug` species" })
	// public static IList opShuffle(final IScope scope, final ISpecies target)
	// throws GamaRuntimeException {
	// return opShuffle(scope, scope.getAgentScope().getPopulationFor(target).getAgentsList());
	// }

	@operator(value = "shuffle", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
		IOperatorCategory.RANDOM, IOperatorCategory.MATRIX })
	@doc(examples = { @example(value = "shuffle (matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]))",
		equals = "matrix([[\"c12\",\"c21\",\"c11\"],[\"c13\",\"c22\",\"c23\"]]) (for example)",
		test = false) })
	public static IMatrix opShuffle(final IScope scope, final IMatrix target) throws GamaRuntimeException {
		final IMatrix matrix2 = target.copy(scope);
		matrix2.shuffleWith(RANDOM(scope));
		return matrix2;
	}

	@operator(value = "shuffle", content_type = IType.STRING, category = { IOperatorCategory.RANDOM,
		IOperatorCategory.STRING })
	@doc(examples = { @example(value = "shuffle ('abc')", equals = "'bac' (for example)", test = false) })
	public static String opShuffle(final IScope scope, final String target) {
		return RANDOM(scope).shuffle(target);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(value = "a random integer in the interval [0, operand]",
		masterDoc = true,
		comment = "to obtain a probability between 0 and 1, use the expression (rnd n) / n, where n is used to indicate the precision",
		usages = {},
		examples = {
			@example(value = "rnd (2)", equals = "0, 1 or 2", test = false),
			@example(value = "rnd (1000) / 1000",
				returnType = IKeyword.FLOAT,
				equals = "a float between 0 and 1 with a precision of 0.001",
				test = false) },
		see = { "flip" })
	public static
		Integer opRnd(final IScope scope, final Integer max) {
		return opRnd(scope, 0, max);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(value = "a random integer in the interval [first operand, second operand]",
		examples = { @example(value = "rnd (2, 4)", equals = "2, 3 or 4", test = false) },
		see = {})
	public static Integer opRnd(final IScope scope, final Integer min, final Integer max) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(value = "a random integer in the interval [first operand, second operand], constrained by a step given by the last operand",
		examples = { @example(value = "rnd (2, 12, 4)", equals = "2, 6 or 10", test = false) },
		see = {})
	public static
		Integer opRnd(final IScope scope, final Integer min, final Integer max, final Integer step) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max, step);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(value = "a random float in the interval [first operand, second operand]",
		examples = { @example(value = "rnd (2.0, 4.0)", equals = "a float number between 2.0 and 4.0", test = false) },
		see = {})
	public static Double opRnd(final IScope scope, final Double min, final Double max) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(value = "a random float in the interval [first operand, second operand] constrained by the last operand (step)",
		examples = { @example(value = "rnd (2.0, 4.0, 0.5)",
			equals = "a float number between 2.0 and 4.0 every 0.5",
			test = false) }, see = {})
	public static
		Double opRnd(final IScope scope, final Double min, final Double max, final Double step) {
		final RandomUtils r = RANDOM(scope);
		return r.between(min, max, step);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(value = "a random point in the interval [first operand, second operand]",
		examples = { @example(value = "rnd ({2.0, 4.0}, {2.0, 5.0, 10.0})",
			equals = "a point with x = 2.0, y between 2.0 and 4.0 and z between 0.0 and 10.0",
			test = false) }, see = {})
	public static GamaPoint opRnd(final IScope scope, final GamaPoint min, final GamaPoint max) {
		final double x = opRnd(scope, min.x, max.x);
		final double y = opRnd(scope, min.y, max.y);
		final double z = opRnd(scope, min.z, max.z);
		return new GamaPoint(x, y, z);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(value = "a random point in the interval [first operand, second operand], constained by the step provided by the last operand",
		examples = { @example(value = "rnd ({2.0, 4.0}, {2.0, 5.0, 10.0}, 1)",
			equals = "a point with x = 2.0, y equal to 2.0, 3.0 or 4.0 and z between 0.0 and 10.0 every 1.0",
			test = false) }, see = {})
	public static
		GamaPoint opRnd(final IScope scope, final GamaPoint min, final GamaPoint max, final Double step) {
		final double x = opRnd(scope, min.x, max.x, step);
		final double y = opRnd(scope, min.y, max.y, step);
		final double z = opRnd(scope, min.z, max.z, step);
		return new GamaPoint(x, y, z);
	}

	static GamaPoint NULL_POINT = new GamaPoint(0, 0, 0);

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(usages = { @usage(value = "if the operand is a point, returns a point with three random float ordinates, each in the interval [0, ordinate of argument]") },
		examples = { @example(value = "rnd ({2.5,3, 0.0})",
			equals = "{x,y} with x in [0.0,2.0], y in [0.0,3.0], z = 0.0",
			test = false) })
	public static
		ILocation opRnd(final IScope scope, final GamaPoint max) {
		return opRnd(scope, NULL_POINT, max);
	}

	@operator(value = "rnd", category = { IOperatorCategory.RANDOM })
	@doc(usages = { @usage(value = "if the operand is a float, returns an uniformly distributed float random number in [0.0, to]") },
		examples = { @example(value = "rnd(3.4)", equals = "a random float between 0.0 and 3.4", test = false) })
	public static
		Double opRnd(final IScope scope, final Double max) {
		return opRnd(scope, 0.0, max);
	}

	@Deprecated
	@operator(value = "rnd_float")
	@doc(deprecated = "Use rnd instead with a float argument", examples = { @example(value = "rnd_float(3)",
		equals = "a random float between 0.0 and 3.0",
		test = false) }, see = { "rnd" })
	public static Double opRndFloat(final IScope scope, final Double max) {
		return opRnd(scope, max);
	}

	@operator(value = "flip", category = { IOperatorCategory.RANDOM })
	@doc(value = "true or false given the probability represented by the operand",
		usages = { @usage(value = "flip 0 always returns false, flip 1 true") },
		examples = { @example(value = "flip (0.66666)", equals = "2/3 chances to return true.", test = false) },
		see = { "rnd" })
	public static Boolean opFlip(final IScope scope, final Double probability) {
		return probability > RANDOM(scope).between(0., 1.);
	}

	@operator(value = "rnd_choice")
	@doc(value = "returns an index of the given list with a probability following the (normalized) distribution described in the list (a form of lottery)",
		examples = { @example(value = "rnd_choice([0.2,0.5,0.3])",
			equals = "2/10 chances to return 0, 5/10 chances to return 1, 3/10 chances to return 2",
			test = false) }, see = { "rnd" })
	public static
		Integer opRndChoice(final IScope scope, final IList distribution) {
		final IList<Double> normalizedDistribution = GamaListFactory.create(Types.FLOAT);
		Double sumElt = 0.0;

		for ( Object eltDistrib : distribution ) {
			Double elt = Cast.asFloat(scope, eltDistrib);
			if ( elt < 0.0 ) { throw GamaRuntimeException.create(new RuntimeException(
				"Distribution elements should be positive."), scope); }
			normalizedDistribution.add(elt);
			sumElt = sumElt + elt;
		}
		if ( sumElt == 0.0 ) { throw GamaRuntimeException.create(new RuntimeException(
			"Distribution elements should not be all equal to 0"), scope); }

		for ( int i = 0; i < normalizedDistribution.size(); i++ ) {
			normalizedDistribution.set(i, normalizedDistribution.get(i) / sumElt);
		}

		double randomValue = RANDOM(scope).between(0., 1.);

		for ( int i = 0; i < distribution.size(); i++ ) {
			randomValue = randomValue - normalizedDistribution.get(i);
			if ( randomValue <= 0 ) { return i; }
		}

		return -1;
	}

}
