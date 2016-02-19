/*********************************************************************************************
 *
 *
 * 'Stats2.java', in plugin 'ummisco.gaml.extensions.stats', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.stats;

import cern.colt.list.DoubleArrayList;
import cern.jet.math.Arithmetic;
import cern.jet.stat.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.operators.Stats;
import msi.gaml.operators.fastmaths.*;
import msi.gaml.types.IType;

public class Stats2 extends Stats {

	private static DoubleArrayList from(final IScope scope, final IContainer values) {
		DoubleArrayList d = new DoubleArrayList(values.length(scope));
		for ( Object o : values.iterable(scope) ) {
			if ( o instanceof Number ) {
				d.add(((Number) o).doubleValue());
			}
		}

		return d;
	}

	public static abstract class DescriptiveStatistics {

		// A list of agents among the left-operand list that are located at a distance <= the right operand from the
		// caller agent (in its topology)

		/**
		 * Returns the auto-correlation of a data sequence.
		 *
		 * @param scope
		 * @param data
		 * @param lag
		 * @param mean
		 * @param variance
		 * @return
		 */
		@operator(value = "auto_correlation",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(value = "Returns the auto-correlation of a data sequence", comment = "", examples = {})
		public static Double opAutoCorrelation(final IScope scope, final IContainer data, final Integer lag) {

			// TODO input parameters validation

			double mean = (Double) Stats.getMean(scope, data);
			double variance = Stats.opVariance(scope, data);

			return Descriptive.autoCorrelation(from(scope, data), lag, mean, variance);
		}

		/**
		 * Returns the correlation of two data sequences.
		 * @see <a href="http://www.mathsisfun.com/data/correlation.html">Correlation</a>
		 *
		 * @param scope
		 * @param data1
		 * @param standardDev1
		 * @param data2
		 * @param stanardDev2
		 * @return
		 */
		@operator(value = "correlation",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(value = "Returns the correlation of two data sequences", comment = "", examples = {})
		public static Double opCorrelation(final IScope scope, final IContainer data1, final IContainer data2) {

			// TODO input parameters validation

			double standardDev1 = Stats.opStDev(scope, data1);
			double standardDev2 = Stats.opStDev(scope, data2);

			return Descriptive.correlation(from(scope, data1), standardDev1, from(scope, data2), standardDev2);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param data1
		 * @param data2
		 * @return
		 */
		@operator(value = "covariance",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(value = "Returns the covariance of two data sequences", comment = "", examples = {})
		public static Double opCovariance(final IScope scope, final IContainer data1, final IContainer data2) {

			// TODO input parameters validation

			return Descriptive.covariance(from(scope, data1), from(scope, data2));
		}

		/**
		 *
		 *
		 * @param scope
		 * @param data
		 * @return
		 */
		@operator(value = "durbin_watson",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(value = "Durbin-Watson computation", comment = "", examples = {})
		public static Double opDurbinWatson(final IScope scope, final IContainer data) {

			// TODO input parameters validation

			return Descriptive.durbinWatson(from(scope, data));
		}

		/**
		 *
		 *
		 * @param scope
		 * @param data
		 * @return
		 */
		@operator(value = "kurtosis",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(value = "Returns the kurtosis (aka excess) of a data sequence", comment = "", examples = {})
		public static Double opKurtosis(final IScope scope, final IContainer data) {

			// TODO input parameters validation

			double mean = (Double) Stats.getMean(scope, data);
			double standardDeviation = Stats.opStDev(scope, data);

			return Descriptive.kurtosis(from(scope, data), mean, standardDeviation);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param moment4
		 * @param standardDeviation
		 * @return
		 */
		@operator(value = "kurtosis",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(value = "Returns the kurtosis (aka excess) of a data sequence", comment = "", examples = {})
		public static Double opKurtosis(final IScope scope, final Double moment4, final Double standardDeviation) {

			// TODO input parameters validation

			return Descriptive.kurtosis(moment4, standardDeviation);
		}

		/**
		 * Returns the moment of k-th order with constant c of a data sequence, which is Sum( (data[i]-c)k ) /
		 * data.size().
		 *
		 * @param scope
		 * @param data
		 * @param k
		 * @param c
		 * @return
		 */
		@operator(value = "moment",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(value = "Returns the moment of k-th order with constant c of a data sequence", comment = "", examples = {})
		public static Double opMoment(final IScope scope, final IContainer data, final Integer k, final Double c) {

			// TODO input parameters validation

			return Descriptive.moment(from(scope, data), k, c);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param data
		 * @param phi
		 * @return
		 */
		@operator(value = "quantile",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(
			value = "Returns the phi-quantile; that is, an element elem for which holds that phi percent of data elements are less than elem. The quantile need not necessarily be contained in the data sequence, it can be a linear interpolation.",
			comment = "",
			examples = {})
		public static Double opQuantile(final IScope scope, final IContainer data, final Double phi) {

			// TODO input parameters validation

			return Descriptive.quantile(from(scope, data), phi);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param data
		 * @param element
		 * @return
		 */
		@operator(value = { "quantile_inverse", "percentile" },
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(
			value = "Returns how many percent of the elements contained in the receiver are <= element. Does linear interpolation if the element is not contained but lies in between two contained elements.",
			comment = "",
			examples = {})
		public static Double opQuantileInverse(final IScope scope, final IContainer data, final Double element) {

			// TODO input parameters validation

			return Descriptive.quantileInverse(from(scope, data), element);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param data
		 * @param element
		 * @return
		 */
		@operator(value = "rank_interpolated",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT })
		@doc(
			value = "Returns the linearly interpolated number of elements in a list less or equal to a given element. The rank is the number of elements <= element. Ranks are of the form {0, 1, 2,..., sortedList.size()}. If no element is <= element, then the rank is zero. If the element lies in between two contained elements, then linear interpolation is used and a non integer value is returned.",
			comment = "",
			examples = {})
		public static Double opRankInterpolated(final IScope scope, final IContainer data, final Double element) {

			// TODO input parameters validation

			return Descriptive.rankInterpolated(from(scope, data), element);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param size
		 * @param sumOfSquares
		 * @return
		 */
		@operator(value = "rms", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the RMS (Root-Mean-Square) of a data sequence. The RMS of data sequence is the square-root of the mean of the squares of the elements in the data sequence. It is a measure of the average size of the elements of a data sequence.",
			comment = "",
			examples = {})
		public static Double opRms(final IScope scope, final Integer size, final Double sumOfSquares) {

			// TODO input parameters validation

			return Descriptive.rms(size, sumOfSquares);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param data
		 * @param mean
		 * @param standardDeviation
		 * @return
		 */
		@operator(value = "skew", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the skew of a data sequence, which is moment(data,3,mean) / standardDeviation3",
			comment = "",
			examples = {})
		public static Double opSkew(final IScope scope, final IContainer data) {

			// TODO input parameters validation

			double mean = (Double) Stats.getMean(scope, data);
			double standardDeviation = Stats.opStDev(scope, data);

			return Descriptive.skew(from(scope, data), mean, standardDeviation);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param moment3
		 * @param standardDeviation
		 * @return
		 */
		@operator(value = "skew", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the skew of a data sequence.", comment = "", examples = {})
		public static Double opSkew(final IScope scope, final Double moment3, final Double standardDeviation) {

			// TODO input parameters validation

			return Descriptive.skew(moment3, standardDeviation);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param variance
		 * @return
		 */
		public static Double opStandardDeviation(final IScope scope, final Double variance) {
			return Descriptive.standardDeviation(variance);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param standardDeviation
		 * @return
		 */
		@operator(value = "variance", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the variance from a standard deviation.", comment = "", examples = {})
		public static Double opVariance(final IScope scope, final Double standardDeviation) {

			// TODO input parameters validation

			return Descriptive.variance(standardDeviation);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param size
		 * @param sum
		 * @param numOfSquares
		 * @return
		 */
		@operator(value = "variance", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the variance of a data sequence. That is (sumOfSquares - mean*sum) / size with mean = sum/size.",
			comment = "",
			examples = {})
		public static Double variance(final IScope scope, final Integer size, final Double sum,
			final Double numOfSquares) {

			// TODO input parameters validation

			return Descriptive.variance(size, sum, numOfSquares);
		}

		// TODO add weightedMean
	}

	/**
	 * Source code of this class is inspired by org.nlogo.extensions.stats.Distributions.java
	 * of the NetLogo Stats extension of Charles Staelin.
	 *
	 * @see <a href="https://github.com/cstaelin/Stats-Extension/releases">NetLogo Stats extension</a>
	 */
	public static abstract class Distributions {

		/**
		 *
		 *
		 * @param scope
		 * @param fstat
		 * @param dfn
		 * @param dfd
		 * @return
		 */
		@operator(value = "pValue_for_fStat", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the P value of F statistic fstat with numerator degrees of freedom dfn and denominator degress of freedom dfd. Uses the incomplete Beta function.",
			comment = "",
			examples = {})
		public static Double opPvalueForFstat(final IScope scope, final Double fstat, final Integer dfn,
			final Integer dfd) { // see Spatial.Punctual.angle_between

			// Returns the P value of F statistic fstat with numerator degrees
			// of freedom dfn and denominator degress of freedom dfd.
			// Uses the incomplete Beta function.

			double x = dfd / (dfd + dfn * fstat);
			try {
				return Gamma.incompleteBeta(dfd / 2.0, dfn / 2.0, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param tstat
		 * @param df
		 * @return
		 */
		@operator(value = "pValue_for_tStat", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the P value of the T statistic tstat with df degrees of freedom. This is a two-tailed test so we just double the right tail which is given by studentT of -|tstat|.",
			comment = "",
			examples = {})
		public static Double opPvalueForTstat(final IScope scope, final Double tstat, final Integer df) {

			// Returns the P value of the T statistic tstat with df degrees of
			// freedom. This is a two-tailed test so we just double the right
			// tail which is given by studentT of -|tstat|.

			double x = FastMath.abs(tstat);
			try {
				double p = Probability.studentT(df, -x);
				return 2.0 * p;
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .studentT reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .studentT reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @param df
		 * @return
		 */
		@operator(value = "student_area", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the area to the left of x in the Student T distribution with the given degrees of freedom.",
			comment = "",
			examples = {})
		public static Double opStudentArea(final IScope scope, final Double x, final Integer df) {

			// Returns the area to the left of x in the Student T distribution
			// with the given degrees of freedom.
			try {
				return Probability.studentT(df, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .studentT reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .studentT reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @param mean
		 * @param sd
		 * @return
		 */
		@operator(value = { "normal_area", "pnorm" }, can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the area to the left of x in the normal distribution with the given mean and standard deviation.",
			comment = "",
			examples = {})
		public static Double opNormalArea(final IScope scope, final Double x, final Double mean, final Double sd) {

			// Returns the area to the left of x in the normal distribution
			// with the given mean and standard deviation.
			try {
				return Probability.normal(mean, sd, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .normal reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .normal reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @param df
		 * @return
		 */
		@operator(value = "student_t_inverse", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the value, t, for which the area under the Student-t probability density function (integrated from minus infinity to t) is equal to x.",
			comment = "",
			examples = {})
		public static Double opStudentTInverse(final IScope scope, final Double x, final Integer df) {

			// Returns the value, t, for which the area under the Student-t
			// probability density function (integrated from minus infinity to t)
			// is equal to x.
			double a = 2.0 * (1.0 - x);
			try {
				return Probability.studentTInverse(a, df);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .studentTInverse reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .studentTInverse reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param area
		 * @param mean
		 * @param sd
		 * @return
		 */
		@operator(value = "normal_inverse", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the x in the normal distribution with the given mean and standard deviation, to the left of which lies the given area. normal.Inverse returns the value in terms of standard deviations from the mean, so we need to adjust it for the given mean and standard deviation.",
			comment = "",
			examples = {})
		public static Double opNormalInverse(final IScope scope, final Double area, final Double mean,
			final Double sd) {

			// Returns the x in the normal distribution with the given mean and
			// standard deviation, to the left of which lies the given area.
			// normal.Inverse returns the value in terms of standard deviations
			// from the mean, so we need to adjust it for the given mean and
			// standard deviation.
			try {
				double x = Probability.normalInverse(area);
				return (x + mean) * sd;
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .normalInverse reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .normalInverse reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @param mean
		 * @param sd
		 * @return
		 */
		@operator(value = { "normal_density", "dnorm" }, can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the probability of x in the normal distribution with the given mean and standard deviation.",
			comment = "",
			examples = {})
		public static Double opNormalDensity(final IScope scope, final Double x, final Double mean, final Double sd) {

			// Returns the probability of x in the normal distribution with the
			// given mean and standard deviation.
			double var = sd * sd;
			double c = 1.0 / FastMath.sqrt(2.0 * CmnFastMath.PI * var);
			double b = (x - mean) * (x - mean) / (2.0 * var);
			return c * FastMath.exp(-b);
		}

		/**
		 *
		 *
		 * @param scope
		 * @param n
		 * @param k
		 * @return
		 */
		@operator(value = "binomial_coeff", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns n choose k as a double. Note the integerization of the double return value.",
			comment = "",
			examples = {})
		public static Double opBinomialCoeff(final IScope scope, final Integer n, final Integer k) {

			// Returns "n choose k" as a double. Note the "integerization" of
			// the double return value.
			try {
				return FastMath.rint(Arithmetic.binomial(n, k));
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .Arithmetic.binomial reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .Arithmetic.binomial reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param n
		 * @param k
		 * @param p
		 * @return
		 */
		@operator(value = { "binomial_sum", "pbinom" }, can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the sum of the terms 0 through k of the Binomial probability density, where n is the number of trials and p is the probability of success in the range 0 to 1.",
			comment = "",
			examples = {})
		public static Double opBinomialSum(final IScope scope, final Integer k, final Integer n, final Double p) {

			// Returns the sum of the terms 0 through k of the Binomial
			// probability density, where n is the number of trials and p is
			// the probability of success in the range 0 to 1.
			try {
				return Probability.binomial(k, n, p);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt Probability.binomial reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt Probability.normal reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param a the paramater a (alpha) of the gamma distribution (shape parameter).
		 * @param b the paramater b (beta, lambda) of the gamma distribution (rate parameter, inverse scale parameter theta).
		 * @param x integration end point.
		 * @return
		 */
		@operator(value = { "gamma_distribution", "pgamma" }, can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the integral from zero to x of the gamma probability density function.",
			comment = "incomplete_gamma(a,x) is equal to pgamma(a,1,x).",
			examples = {})
		public static Double opGamma(final IScope scope, final Double a, final Double b, final Double x) {

			// Returns the integral from zero to x of the gamma probability density function.
			try {
				return Probability.gamma(a, b, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param a the paramater a (alpha) of the gamma distribution.
		 * @param b the paramater b (beta, lambda) of the gamma distribution.
		 * @param x integration end point.
		 * @return
		 */
		@operator(value = "gamma_distribution_complemented", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the integral from x to infinity of the gamma probability density function.",
			comment = "",
			examples = {})
		public static Double opGammaComplemented(final IScope scope, final Double a, final Double b, final Double x) {

			// Returns the integral from x to infinity of the gamma probability density function.
			try {
				return Probability.gammaComplemented(a, b, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param n
		 * @param k
		 * @param p
		 * @return
		 */
		@operator(value = "binomial_complemented", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the sum of the terms k+1 through n of the Binomial probability density, where n is the number of trials and P is the probability of success in the range 0 to 1.",
			comment = "",
			examples = {})
		public static Double opBinomialComplemented(final IScope scope, final Integer n, final Integer k,
			final Double p) {

			// Returns the sum of the terms k+1 through n of the Binomial
			// probability density, where n is the number of trials and P is
			// the probability of success in the range 0 to 1.
			try {
				return Probability.binomialComplemented(k, n, p);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .binomialComplement reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .binomialComplement reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @param df
		 * @return
		 */
		@operator(value = { "chi_square", "pchisq" }, can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the area under the left hand tail (from 0 to x) of the Chi square probability density function with df degrees of freedom.",
			comment = "",
			examples = {})
		public static Double opChiSquare(final IScope scope, final Double x, final Double df) {

			// Returns the area under the left hand tail (from 0 to x) of the
			// Chi square probability density function with df degrees of freedom.
			try {
				return Probability.chiSquare(df, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .chiSquare reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .chiSquare reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @param df
		 * @return
		 */
		@operator(value = "chi_square_complemented", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the area under the right hand tail (from x to infinity) of the Chi square probability density function with df degrees of freedom.",
			comment = "",
			examples = {})
		public static Double opChiSquareComplemented(final IScope scope, final Double x, final Double df) {

			// Returns the area under the right hand tail (from x to infinity)
			// of the Chi square probability density function with df degrees
			// of freedom.
			try {
				return Probability.chiSquareComplemented(df, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .chiSquareComplemented reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .chiSquareComplemented reports: " + ex, scope);
			}
		}
	}

	public static abstract class GammaFunction {

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @return
		 */
		@operator(value = "gamma", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the value of the Gamma function at x.", comment = "", examples = {})
		public static Double opGamma(final IScope scope, final Double x) {

			// Returns the value of the Gamma function at x.
			try {
				return Gamma.gamma(x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param x
		 * @return
		 */
		@operator(value = { "log_gamma", "lgamma" }, can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the log of the value of the Gamma function at x.", comment = "", examples = {})
		public static Double opLogGamma(final IScope scope, final Double x) {

			// Returns the log of the value of the Gamma function at x.
			try {
				return Gamma.logGamma(x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .logGamma reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .logGamma reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param a
		 * @param x
		 * @return
		 */
		@operator(value = "incomplete_gamma", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = " Returns the regularized integral of the Gamma function with argument a to the integration end point x.",
			comment = "",
			examples = {})
		public static Double opIncompleteGamma(final IScope scope, final Double a, final Double x) {

			// Returns the regularized integral of the Gamma function with argument
			// a to the integration end point x.
			try {
				return Gamma.incompleteGamma(a, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .incompleteGamma reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .incompleteGamma reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param a
		 * @param x
		 * @return
		 */
		@operator(value = "incomplete_gamma_complement", can_be_const = true, type = IType.FLOAT)
		@doc(
			value = "Returns the complemented regularized incomplete Gamma function of the argument a and integration start point x.",
			comment = "",
			examples = {})
		public static Double opIncompleteGammaComplement(final IScope scope, final Double a, final Double x) {
			// Returns the complemented regularized incomplete Gamma function of the
			// argument a and integration start point x.
			try {
				return Gamma.incompleteGammaComplement(a, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .incompleteGammaComplement reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .incompleteGammaComplement reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param a
		 * @param b
		 * @return
		 */
		@operator(value = "beta", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the beta function with arguments a, b.", comment = "", examples = {})
		public static Double opBeta(final IScope scope, final Double a, final Double b) {

			// Returns the beta function with arguments a, b.
			try {
				return Gamma.beta(a, b);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .beta reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .beta reports: " + ex, scope);
			}
		}

		/**
		 *
		 *
		 * @param scope
		 * @param a
		 * @param b
		 * @param x
		 * @return
		 */
		@operator(value = "incomplete_beta", can_be_const = true, type = IType.FLOAT)
		@doc(value = "Returns the regularized integral of the beta function with arguments a and b, from zero to x.",
			comment = "",
			examples = {})
		public static Double opIncompleteBeta(final IScope scope, final Double a, final Double b, final Double x) {

			// Returns the regularized integral of the beta function with arguments
			// a and b, from zero to x.
			try {
				return Gamma.incompleteBeta(a, b, x);
			} catch (IllegalArgumentException ex) {
				throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex, scope);
			} catch (ArithmeticException ex) {
				throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex, scope);
			}
		}
	}

}
