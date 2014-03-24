package ummisco.gaml.extensions.stats;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.operators.Stats;
import msi.gaml.types.IType;
import cern.colt.list.DoubleArrayList;
import cern.jet.math.Arithmetic;
import cern.jet.stat.Descriptive;
import cern.jet.stat.Gamma;
import cern.jet.stat.Probability;

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
		
		// A list of agents among the left-operand list that are located at a distance <= the right operand from the caller agent (in its topology)
		
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
		@operator(value = "auto_correlation", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "The auto-correlation ", comment = "", examples = "")
		public static Double opAutoCorrelation(final IScope scope, final IContainer data, final Integer lag) {
			
			// TODO input parameters validation
			
			double mean = (Double) Stats.getMean(scope, data);
			double variance = (Double) Stats.opVariance(scope, data);
			
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
		@operator(value = "correlation", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "covariance", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "durbin_watson", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "kurtosis_1", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "kurtosis_2", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
		public static Double opKurtosis(final IScope scope, final Double moment4, final Double standardDeviation) {
			
			// TODO input parameters validation
			
			return Descriptive.kurtosis(moment4, standardDeviation);
		}
		
		
		/**
		 * Returns the moment of k-th order with constant c of a data sequence, which is Sum( (data[i]-c)k ) / data.size().
		 * 
		 * @param scope
		 * @param data
		 * @param k
		 * @param c
		 * @return
		 */
		@operator(value = "moment", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "quantile", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = { "quantile_inverse", "percentile" }, can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "rank_interpolated", can_be_const = true, type = IType.FLOAT, expected_content_type = {
				IType.INT, IType.FLOAT })
		@doc(value = "", comment = "", examples = "")
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
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "skew_1", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double opSkew(final IScope scope, final IContainer data) {
			
			// TODO input parameters validation
			
			double mean = (Double) Stats.getMean(scope, data);
			double standardDeviation = (Double) Stats.opStDev(scope, data);

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
		@operator(value = "skew_2", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "variance1", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
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
		@operator(value = "variance2", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double variance(final IScope scope, final Integer size, final Double sum, final Double numOfSquares) {
			
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
		@doc(value = "", comment = "", examples = "")
		public static Double opPvalueForFstat(final IScope scope, final Double fstat, final Integer dfn, final Integer dfd) { // see Spatial.Punctual.angle_between
			
		    // Returns the P value of F statistic fstat with numerator degrees
		    // of freedom dfn and denominator degress of freedom dfd.
		    // Uses the incomplete Beta function.
		
		    double x = dfd / (dfd + dfn * fstat);
		    try {
		      return Gamma.incompleteBeta(dfd / 2.0, dfn / 2.0, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opPvalueForTstat(final IScope scope, final Double tstat, final Integer df) {
			
		    // Returns the P value of the T statistic tstat with df degrees of
		    // freedom. This is a two-tailed test so we just double the right
		    // tail which is given by studentT of -|tstat|.

		    double x = Math.abs(tstat);
		    try {
		      double p = Probability.studentT((double) df, -x);
		      return 2.0 * p;
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .studentT reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .studentT reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opStudentArea(final IScope scope, final Double x, final Integer df) {

		    // Returns the area to the left of x in the Student T distribution
		    // with the given degrees of freedom.
		    try {
		      return Probability.studentT((double) df, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .studentT reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .studentT reports: " + ex);
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
		@operator(value = "normal_area", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double opNormalArea(final IScope scope, final Double x, final Double mean, final Double sd) {
			
		    // Returns the area to the left of x in the normal distribution
		    // with the given mean and standard deviation.
		    try {
		      return Probability.normal(mean, sd, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .normal reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .normal reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opStudentTInverse(final IScope scope, final Double x, final Integer df) {
			
		    // Returns the value, t, for which the area under the Student-t 
		    // probability density function (integrated from minus infinity to t) 
		    // is equal to x.
		    double a = 2.0 * (1.0 - x);
		    try {
		      return Probability.studentTInverse(a, df);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .studentTInverse reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .studentTInverse reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opNormalInverse(final IScope scope, final Double area, final Double mean, final Double sd) {

		    // Returns the x in the normal distribution with the given mean and
		    // standard deviation, to the left of which lies the given area.
		    // normal.Inverse returns the value in terms of standard deviations
		    // from the mean, so we need to adjust it for the given mean and 
		    // standard deviation.
		    try {
		      double x = Probability.normalInverse(area);
		      return (x + mean) * sd;
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .normalInverse reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .normalInverse reports: " + ex);
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
		@operator(value = "normal_density", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double opNormalDensity(final IScope scope, final Double x, final Double mean, final Double sd) {

		    // Returns the probability of x in the normal distribution with the 
		    // given mean and standard deviation.
		    double var = sd * sd;
		    double c = 1.0 / Math.sqrt(2.0 * Math.PI * var);
		    double b = ((x - mean) * (x - mean)) / (2.0 * var);
		    return c * Math.exp(-b);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opBinomialCoeff(final IScope scope, final Integer n, final Integer k) {

		    // Returns "n choose k" as a double. Note the "integerization" of
		    // the double return value.
		    try {
		    return Math.rint(Arithmetic.binomial((long) n, (long) k));
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .Arithmetic.binomial reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .Arithmetic.binomial reports: " + ex);
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
		@operator(value = "binomial_sum", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double opBinomialSum(final IScope scope, final Integer n, final Integer k, final Double p) {

		    // Returns the sum of the terms 0 through k of the Binomial 
		    // probability density, where n is the number of trials and p is 
		    // the probability of success in the range 0 to 1.
		    try {
		      return Probability.binomial(k, n, p);
		    } catch (IllegalArgumentException ex) {
		    	throw GamaRuntimeException.error("colt Probability.binomial reports: " + ex);
		    } catch (ArithmeticException ex) {
		    	throw GamaRuntimeException.error("colt Probability.normal reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opBinomialComplemented(final IScope scope, final Integer n, final Integer k, final Double p) {

		    // Returns the sum of the terms k+1 through n of the Binomial 
		    // probability density, where n is the number of trials and P is
		    // the probability of success in the range 0 to 1.
		    try {
		      return Probability.binomialComplemented(k, n, p);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .binomialComplement reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .binomialComplement reports: " + ex);
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
		@operator(value = "chi_square", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double opChiSquare(final IScope scope, final Double x, final Double df) {

		    // Returns the area under the left hand tail (from 0 to x) of the 
		    // Chi square probability density function with df degrees of freedom.
		    try {
		      return Probability.chiSquare(df, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .chiSquare reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .chiSquare reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opChiSquareComplemented(final IScope scope, final Double x, final Double df) {
			
		    // Returns the area under the right hand tail (from x to infinity) 
		    // of the Chi square probability density function with df degrees 
		    // of freedom.
		    try {
		      return Probability.chiSquareComplemented(df, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .chiSquareComplemented reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .chiSquareComplemented reports: " + ex);
		    }
		}
		
		
		/**
		 * 
		 * 
		 * @param scope
		 * @param x
		 * @return
		 */
		@operator(value = "gamma", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double opGamma(final IScope scope, final Double x) {

		    // Returns the value of the Gamma function at x.
		    try {
		      return Gamma.gamma(x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .gamma reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .gamma reports: " + ex);
		    }
		}
		
		
		/**
		 * 
		 * 
		 * @param scope
		 * @param x
		 * @return
		 */
		@operator(value = "log_gamma", can_be_const = true, type = IType.FLOAT)
		@doc(value = "", comment = "", examples = "")
		public static Double opLogGamma(final IScope scope, final Double x) {

		    // Returns the log of the value of the Gamma function at x.
		    try {
		      return Gamma.logGamma(x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .logGamma reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .logGamma reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opIncompleteGamma(final IScope scope, final Double a, final Double x) {

		    // Returns the regularized integral of the Gamma function with argument
		    // a to the integration end point x.
		    try {
		      return Gamma.incompleteGamma(a, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .incompleteGamma reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .incompleteGamma reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opIncompleteGammaComplement(final IScope scope, final Double a, final Double x) {
		    // Returns the complemented regularized incomplete Gamma function of the 
		    // argument a and integration start point x.
		    try {
		      return Gamma.incompleteGammaComplement(a, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .incompleteGammaComplement reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .incompleteGammaComplement reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opBeta(final IScope scope, final Double a, final Double b) {

		    // Returns the beta function with arguments a, b.
		    try {
		      return Gamma.beta(a, b);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .beta reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .beta reports: " + ex);
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
		@doc(value = "", comment = "", examples = "")
		public static Double opIncompleteBeta(final IScope scope, final Double a, final Double b, final Double x) {

		    // Returns the regularized integral of the beta function with arguments
		    // a and b, from zero to x.
		    try {
		      return Gamma.incompleteBeta(a, b, x);
		    } catch (IllegalArgumentException ex) {
		      throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex);
		    } catch (ArithmeticException ex) {
		      throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex);
		    }
		}
	}
}
