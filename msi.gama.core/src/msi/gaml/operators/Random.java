/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.*;
import org.uncommons.maths.number.NumberGenerator;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 * 
 * @todo Description
 * 
 */
public class Random {

	private static RandomUtils RANDOM(final IScope scope) {
		return GAMA.getRandom();
	}

	@operator(value = { "truncated_gauss", "TGauss" }, category={IOperatorCategory.RANDOM})
	@doc(value = "A random value from a normally distributed random variable in the interval ]mean - standardDeviation; mean + standardDeviation[.", usages = { @usage(value="when the operand is a point, it is read as {mean, standardDeviation}") }, examples = { @example(value="truncated_gauss ({0, 0.3})  --:  an float between -0.3 and 0.3") }, see = { "gauss" })
	public static Double opTGauss(final IScope scope, final GamaPoint p) {
		return opTGauss(scope, GamaList.with(p.x, p.y));
	}

	@operator(value = { "truncated_gauss", "TGauss" }, category={IOperatorCategory.RANDOM})
	@doc(usages = {
		@usage(value="if the operand is a list, only the two first elements are taken into account as [mean, standardDeviation]"),
		@usage(value="when truncated_gauss is called with a list of only one element mean, it will always return 0.0") }, examples = { @example(value="truncated_gauss ([0.5, 0.0])  --:  0.5 (always)") })
	public static Double opTGauss(final IScope scope, final IList list) {
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
		final NumberGenerator<Double> gen = RANDOM(scope).createGaussian(mean, range / 2);
		// 'do while' does the truncature

		do {
			// we use bound / 2 as a standard deviation because we want to have
			// stdDeviation = 2 * bound
			tmpResult = gen.nextValue();
		} while (tmpResult > mean + range || tmpResult < mean - range);
		return tmpResult;

	}

	@operator(value = "gauss", category={IOperatorCategory.RANDOM})
	@doc(value = "A value from a normally distributed random variable with expected value (mean) and variance (standardDeviation). The probability density function of such a variable is a Gaussian.", usages = {
		@usage(value="when the operand is a point, it is read as {mean, standardDeviation}"),
		@usage(value="when standardDeviation value is 0.0, it always returns the mean value") }, examples = {
		@example(value="gauss({0,0.3})  --:  0.22354"), @example(value="gauss({0,0.3})  --:  -0.1357") }, see = { "truncated_gauss", "poisson" })
	public static Double opGauss(final IScope scope, final GamaPoint point) {
		final double mean = point.x;
		final double sd = point.y;
		return RANDOM(scope).createGaussian(mean, sd).nextValue();
	}

	@operator(value = "gauss", category={IOperatorCategory.RANDOM})
	@doc(value = "A value from a normally distributed random variable with expected value (mean) and variance (standardDeviation). The probability density function of such a variable is a Gaussian.", usages = {
		@usage(value="when the operand is a point, it is read as {mean, standardDeviation}"),
		@usage(value="when standardDeviation value is 0.0, it always returns the mean value") }, examples = {
		@example(value="gauss(0,0.3)  --:  0.22354"), @example(value="gauss(0,0.3)  --:  -0.1357") }, see = { "truncated_gauss", "poisson" })
	public static Double opGauss(final IScope scope, final double mean, final double sd) {
		return RANDOM(scope).createGaussian(mean, sd).nextValue();
	}

	@operator(value = "poisson", category={IOperatorCategory.RANDOM})
	@doc(value = "A value from a random variable following a Poisson distribution (with the positive expected number of occurence lambda as operand).", comment = "The Poisson distribution is a discrete probability distribution that expresses the probability of a given number of events occurring in a fixed interval of time and/or space if these events occur with a known average rate and independently of the time since the last event, cf. Poisson distribution on Wikipedia.", examples = { @example(value="poisson(3.5) --: a random positive integer") }, see = {
		"binomial", "gauss" })
	public static Integer opPoisson(final IScope scope, final Double mean) {
		return RANDOM(scope).createPoisson(mean).nextValue();
	}

	@operator(value = "binomial", category={IOperatorCategory.RANDOM})
	@doc(value = "A value from a random variable following a binomial distribution. The operand {n,p} represents the number of experiments n and the success probability p.", comment = "The binomial distribution is the discrete probability distribution of the number of successes in a sequence of n independent yes/no experiments, each of which yields success with probability p, cf. Binomial distribution on Wikipedia.", examples = { @example(value="binomial({15,0.6})  --:  a random positive integer") }, see = {
		"poisson", "gauss" })
	public static Integer opBinomial(final IScope scope, final GamaPoint point) {
		final int n = (int) point.x;
		final double p = point.y;
		return RANDOM(scope).createBinomial(n, p).nextValue();
	}

	@operator(value = "shuffle", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.RANDOM})
	@doc(value = "The elements of the operand in random order.", usages = { @usage(value="if the operand is empty, returns an empty list (or string, matrix)") }, examples = { @example(value="shuffle ([12, 13, 14]) --: [14,12,13];") }, see = { "reverse" })
	public static IList opShuffle(final IScope scope, final IContainer target) {
		if ( target == null || target.isEmpty(scope) ) { return new GamaList(); }
		final IList list = (IList) target.listValue(scope, Types.NO_TYPE).copy(scope);
		RANDOM(scope).shuffle(list);
		return list;
	}

	// @operator(value = "shuffle", content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	// @doc(examples = { "shuffle (bug) --:  shuffle the list of all agents of the `bug` species" })
	// public static IList opShuffle(final IScope scope, final ISpecies target)
	// throws GamaRuntimeException {
	// return opShuffle(scope, scope.getAgentScope().getPopulationFor(target).getAgentsList());
	// }

	@operator(value = "shuffle", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.RANDOM})
	@doc(examples = { @example(value="shuffle ([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]) --: [[\"c12\",\"c21\",\"c11\"],[\"c13\",\"c22\",\"c23\"]]") })
	public static IMatrix opShuffle(final IScope scope, final IMatrix target) throws GamaRuntimeException {
		final IMatrix matrix2 = (IMatrix) target.copy(scope);
		matrix2.shuffleWith(RANDOM(scope));
		return matrix2;
	}

	@operator(value = "shuffle", content_type = IType.STRING, category={IOperatorCategory.RANDOM})
	@doc(examples = { @example(value="shuffle ('abc') --: 'bac'") })
	public static String opShuffle(final IScope scope, final String target) {
		return RANDOM(scope).shuffle(target);
	}

	@operator(value = "rnd", category={IOperatorCategory.RANDOM})
	@doc(value = "a random integer in the interval [0, operand]", masterDoc= true, comment = "to obtain a probability between 0 and 1, use the expression (rnd n) / n, where n is used to indicate the precision", usages = { }, examples = {
		@example(value="rnd (2) --: 0, 1 or 2"), @example(value="rnd (1000) / 1000 --: a float between 0 and 1 with a precision of 0.001") }, see = { "flip" })
	public static Integer opRnd(final IScope scope, final Integer max) {
		final RandomUtils r = RANDOM(scope);
		return r.between(0, max);
	}

	@operator(value = "rnd", category={IOperatorCategory.RANDOM})
	@doc(usages = { @usage(value="if the operand is a float, it is casted to an int before being evaluated") }, examples = { @example(value="rnd (2.5) --: 0, 1 or 2") })
	public static Integer opRnd(final IScope scope, final Double max) {
		return RANDOM(scope).between(0, max.intValue());
	}

	@operator(value = "rnd", category={IOperatorCategory.RANDOM})
	@doc(usages = { @usage(value="if the operand is a point, returns a point with two random integers in the interval [0, operand]") }, examples = { @example(value="rnd ({2.5,3}) --: {x,y} with x in [0,2] and y in [0,3]") })
	public static ILocation opRnd(final IScope scope, final GamaPoint max) {
		final Integer x = RANDOM(scope).between(0, (int) max.x);
		final Integer y = RANDOM(scope).between(0, (int) max.y);
		return new GamaPoint(x, y);
	}

	@operator(value = "flip", category={IOperatorCategory.RANDOM})
	@doc(value = "true or false given the probability represented by the operand", usages = { @usage(value="flip 0 always returns false, flip 1 true") }, examples = { @example(value="flip (0.66666) --: 2/3 chances to return true.") }, see = { "rnd" })
	public static Boolean opFlip(final IScope scope, final Double probability) {
		return probability > RANDOM(scope).between(0., 1.);
	}

}
