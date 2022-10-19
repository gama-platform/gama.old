/*******************************************************************************************************
 *
 * Stats.java, in ummisco.gaml.extensions.stats, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.stats;

import static msi.gaml.operators.Containers.collect;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.inference.TTest;

import com.google.common.collect.Ordering;

import cern.colt.list.DoubleArrayList;
import cern.jet.math.Arithmetic;
import cern.jet.stat.Descriptive;
import cern.jet.stat.Gamma;
import cern.jet.stat.Probability;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.batch.exploration.morris.Morris;
import msi.gama.kernel.batch.exploration.sobol.Sobol;
import msi.gama.kernel.batch.exploration.stochanalysis.Stochanalysis;
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
import msi.gama.util.Collector;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.matrix.GamaField;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Containers;
import msi.gaml.operators.Containers.ComparableValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 15 janv. 2011
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class Stats {

	/**
	 * The Class DataSet.
	 */
	private static class DataSet {

		/** The Constant DEFAULT_CAPACITY. */
		private static final int DEFAULT_CAPACITY = 50;

		/** The Constant GROWTH_RATE. */
		private static final double GROWTH_RATE = 1.5d;

		/** The data set. */
		double[] dataSet;

		/** The data set size. */
		int dataSetSize = 0;

		/** The total. */
		private double total = 0;

		/** The product. */
		private double product = 1;

		/** The reciprocal sum. */
		private double reciprocalSum = 0;

		/** The minimum. */
		private double minimum = Double.MAX_VALUE;

		/** The maximum. */
		private double maximum = Double.MIN_VALUE;

		/**
		 * Creates an empty data set with a default initial capacity.
		 */
		public DataSet() {
			this(DEFAULT_CAPACITY);
		}

		/**
		 * Creates an empty data set with the specified initial capacity.
		 *
		 * @param capacity
		 *            The initial capacity for the data set (this number of values will be able to be added without
		 *            needing to resize the internal data storage).
		 */
		public DataSet(final int capacity) {
			this.dataSet = new double[capacity];
			this.dataSetSize = 0;
		}

		/**
		 * Adds a single value to the data set and updates any statistics that are calculated cumulatively.
		 *
		 * @param value
		 *            The value to add.
		 */
		public void addValue(final double value) {
			if (dataSetSize == dataSet.length) {
				// Increase the capacity of the array.
				final int newLength = (int) Math.round(GROWTH_RATE * dataSetSize);
				final double[] newDataSet = new double[newLength];
				java.lang.System.arraycopy(dataSet, 0, newDataSet, 0, dataSetSize);
				dataSet = newDataSet;
			}
			dataSet[dataSetSize] = value;
			updateStatsWithNewValue(value);
			++dataSetSize;
		}

		/**
		 * The arithemthic mean of an n-element set is the sum of all the elements divided by n. The arithmetic mean is
		 * often referred to simply as the "mean" or "average" of a data set.
		 *
		 * @see #getGeometricMean()
		 * @return The arithmetic mean of all elements in the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getArithmeticMean() { return total / dataSetSize; }

		/**
		 * The geometric mean of an n-element set is the nth-root of the product of all the elements. The geometric mean
		 * is used for finding the average factor (e.g. an average interest rate).
		 *
		 * @see #getArithmeticMean()
		 * @see #getHarmonicMean()
		 * @return The geometric mean of all elements in the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getGeometricMean() { return Math.pow(product, 1.0d / dataSetSize); }

		/**
		 * The harmonic mean of an n-element set is {@literal n} divided by the sum of the reciprocals of the values
		 * (where the reciprocal of a value {@literal x} is 1/x). The harmonic mean is used to calculate an average rate
		 * (e.g. an average speed).
		 *
		 * @see #getArithmeticMean()
		 * @see #getGeometricMean()
		 * @since 1.1
		 * @return The harmonic mean of all the elements in the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getHarmonicMean() { return dataSetSize / reciprocalSum; }

		/**
		 * Calculates the mean absolute deviation of the data set. This is the average (absolute) amount that a single
		 * value deviates from the arithmetic mean.
		 *
		 * @see #getArithmeticMean()
		 * @see #getVariance()
		 * @see #getStandardDeviation()
		 * @return The mean absolute deviation of the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getMeanDeviation() {
			final double mean = getArithmeticMean();
			double diffs = 0;
			for (int i = 0; i < dataSetSize; i++) { diffs += Math.abs(mean - dataSet[i]); }
			return diffs / dataSetSize;
		}

		/**
		 * Determines the median value of the data set.
		 *
		 * @return If the number of elements is odd, returns the middle element. If the number of elements is even,
		 *         returns the midpoint of the two middle elements.
		 * @since 1.0.1
		 */
		public final double getMedian() {

			// Sort the data (take a copy to do this).
			final double[] dataCopy = new double[getSize()];
			java.lang.System.arraycopy(dataSet, 0, dataCopy, 0, dataCopy.length);
			Arrays.sort(dataCopy);
			final int midPoint = dataCopy.length / 2;
			if (dataCopy.length % 2 != 0) return dataCopy[midPoint];

			return dataCopy[midPoint - 1] + (dataCopy[midPoint] - dataCopy[midPoint - 1]) / 2;
		}

		/**
		 * @return The product of all values.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getProduct() { return product; }

		/**
		 * Returns the number of values in this data set.
		 *
		 * @return The size of the data set.
		 */
		public final int getSize() { return dataSetSize; }

		/**
		 * The standard deviation is the square root of the variance. This method calculates the population standard
		 * deviation as opposed to the sample standard deviation. For large data sets the difference is negligible.
		 *
		 * @see #getSampleStandardDeviation()
		 * @see #getVariance()
		 * @see #getMeanDeviation()
		 * @return The standard deviation of the population.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getStandardDeviation() { return Math.sqrt(getVariance()); }

		/**
		 * Gets the stops.
		 *
		 * @param nb
		 *            the nb
		 * @return the stops
		 */
		public double[] getStops(final int nb) {
			final double interval = (maximum - minimum) / nb;
			final double[] result = new double[nb - 1];
			for (int i = 1; i < nb; i++) { result[i - 1] = minimum + i * interval; }
			return result;
		}

		/**
		 * Calculates the variance (a measure of statistical dispersion) of the data set. There are different measures
		 * of variance depending on whether the data set is itself a finite population or is a sample from some larger
		 * population. For large data sets the difference is negligible. This method calculates the population variance.
		 *
		 * @see #getSampleVariance()
		 * @see #getStandardDeviation()
		 * @see #getMeanDeviation()
		 * @return The population variance of the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getVariance() { return sumSquaredDiffs() / getSize(); }

		/**
		 * Helper method for variance calculations.
		 *
		 * @return The sum of the squares of the differences between each value and the arithmetic mean.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		private double sumSquaredDiffs() {
			final double mean = getArithmeticMean();
			double squaredDiffs = 0;
			for (int i = 0; i < getSize(); i++) {
				final double diff = mean - dataSet[i];
				squaredDiffs += diff * diff;
			}
			return squaredDiffs;
		}

		/**
		 * Update stats with new value.
		 *
		 * @param value
		 *            the value
		 */
		private void updateStatsWithNewValue(final double value) {
			total += value;
			product *= value;
			reciprocalSum += 1 / value;
			minimum = Math.min(minimum, value);
			maximum = Math.max(maximum, value);
		}

	}

	/**
	 * The Class Instance.
	 */
	public static class Instance extends DoublePoint {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/** The id. */
		int id;

		/**
		 * Instantiates a new instance.
		 *
		 * @param id
		 *            the id
		 * @param point
		 *            the point
		 */
		public Instance(final int id, final double[] point) {
			super(point);
			this.id = id;
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public int getId() { return id; }

		/**
		 * Sets the id.
		 *
		 * @param id
		 *            the new id
		 */
		public void setId(final int id) { this.id = id; }

	}

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
	@operator (
			value = "auto_correlation",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the auto-correlation of a data sequence given some lag",
			comment = "",
			examples = { @example (
					value = "auto_correlation([1,0,1,0,1,0],2)",
					equals = "1"),
					@example (
							value = "auto_correlation([1,0,1,0,1,0],1)",
							equals = "-1") })
	public static Double opAutoCorrelation(final IScope scope, final IContainer data, final Integer lag) {

		// TODO input parameters validation

		final double mean = (Double) Containers.opMean(scope, data);
		final double variance = Stats.opVariance(scope, data);

		return Descriptive.autoCorrelation(toDoubleArrayList(scope, data), lag, mean, variance);
	}

	/**
	 *
	 *
	 * @param scope
	 * @param a
	 * @param b
	 * @return
	 */
	@operator (
			value = "beta",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the beta function with arguments a, b.",
			comment = "Checked on R. beta(4,5)",
			examples = { @example (
					value = "beta(4,5) with_precision(4)",
					equals = "0.0036") })

	public static Double opBeta(final IScope scope, final Double a, final Double b) {

		// Returns the beta function with arguments a, b.
		try {
			return Gamma.beta(a, b);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .beta reports: " + ex, scope);
		}
	}

	/**
	 *
	 *
	 * @param scope
	 * @param n
	 * @param k
	 * @return
	 */
	@operator (
			value = "binomial_coeff",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns n choose k as a double. Note the integerization of the double return value.",
			comment = "",
			examples = { @example (
					value = "binomial_coeff(10,2)",
					equals = "45") })

	public static Double opBinomialCoeff(final IScope scope, final Integer n, final Integer k) {

		// Returns "n choose k" as a double. Note the "integerization" of
		// the double return value.
		try {
			return Math.rint(Arithmetic.binomial(n, k));
		} catch (final IllegalArgumentException | ArithmeticException ex) {
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
	@operator (
			value = "binomial_complemented",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the sum of the terms k+1 through n of the Binomial probability density, "
					+ "where n is the number of trials and P is the probability of success in the range 0 to 1.",
			comment = "",
			examples = { @example (
					value = "binomial_complemented(10,5,0.5) with_precision(2)",
					equals = "0.38") })

	public static Double opBinomialComplemented(final IScope scope, final Integer n, final Integer k, final Double p) {

		// Returns the sum of the terms k+1 through n of the Binomial
		// probability density, where n is the number of trials and P is
		// the probability of success in the range 0 to 1.
		try {
			return Probability.binomialComplemented(k, n, p);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .binomialComplement reports: " + ex, scope);
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
	@operator (
			value = { "binomial_sum", "pbinom" },
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the sum of the terms 0 through k of the Binomial probability density, where "
					+ "n is the number of trials and p is the probability of success in the range 0 to 1.",
			comment = "",
			examples = { @example (
					value = "binomial_sum(5,10,0.5) with_precision(2)",
					equals = "0.62") })

	public static Double opBinomialSum(final IScope scope, final Integer k, final Integer n, final Double p) {

		// Returns the sum of the terms 0 through k of the Binomial
		// probability density, where n is the number of trials and p is
		// the probability of success in the range 0 to 1.
		try {
			return Probability.binomial(k, n, p);
		} catch (final IllegalArgumentException ex) {
			throw GamaRuntimeException.error("colt Probability.binomial reports: " + ex, scope);
		} catch (final ArithmeticException ex) {
			throw GamaRuntimeException.error("colt Probability.normal reports: " + ex, scope);
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
	@operator (
			value = { "chi_square", "pchisq" },
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the area under the left hand tail (from 0 to x) of the Chi square probability density function with df degrees of freedom.",
			comment = "",
			examples = { @example (
					value = "chi_square(20.0,10) with_precision(3)",
					equals = "0.971") }

	)

	public static Double opChiSquare(final IScope scope, final Double x, final Double df) {

		// Returns the area under the left hand tail (from 0 to x) of the
		// Chi square probability density function with df degrees of
		// freedom.
		try {
			return Probability.chiSquare(df, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
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
	@operator (
			value = "chi_square_complemented",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the area under the right hand tail (from x to infinity) of the Chi square probability density function with df degrees of freedom.",
			comment = "",
			examples = {

					@example (
							value = "chi_square_complemented(2,10) with_precision(3)",
							equals = "0.996")

			})

	public static Double opChiSquareComplemented(final IScope scope, final Double x, final Double df) {

		// Returns the area under the right hand tail (from x to infinity)
		// of the Chi square probability density function with df degrees
		// of freedom.
		try {
			return Probability.chiSquareComplemented(df, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .chiSquareComplemented reports: " + ex, scope);
		}
	}

	/**
	 * Returns the correlation of two data sequences.
	 *
	 * @see <a href="http://www.mathsisfun.com/data/correlation.html"> Correlation</a>
	 *
	 * @param scope
	 * @param data1
	 * @param standardDev1
	 * @param data2
	 * @param stanardDev2
	 * @return
	 */
	@operator (
			value = "correlation",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the correlation of two data sequences (having the same size)",
			comment = "",
			examples = { @example (
					value = "correlation([1,2,1,3,1,2], [1,2,1,3,1,2]) with_precision(4)",
					equals = "1.2"),
					@example (
							value = "correlation([13,2,1,4,1,2], [1,2,1,3,1,2]) with_precision(2)",
							equals = "-0.21") })
	public static Double opCorrelation(final IScope scope, final IContainer data1, final IContainer data2) {

		// TODO input parameters validation

		final double standardDev1 = Stats.opStandardDeviation(scope, data1);
		final double standardDev2 = Stats.opStandardDeviation(scope, data2);

		return Descriptive.correlation(toDoubleArrayList(scope, data1), standardDev1, toDoubleArrayList(scope, data2),
				standardDev2);
	}

	/**
	 *
	 *
	 * @param scope
	 * @param data1
	 * @param data2
	 * @return
	 */
	@operator (
			value = "covariance",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the covariance of two data sequences",
			comment = "",
			examples = { @example (
					value = "covariance([13,2,1,4,1,2], [1,2,1,3,1,2]) with_precision(2)",
					equals = "-0.67") })
	public static Double opCovariance(final IScope scope, final IContainer data1, final IContainer data2) {

		// TODO input parameters validation

		return Descriptive.covariance(toDoubleArrayList(scope, data1), toDoubleArrayList(scope, data2));
	}

	// TODO Penser a faire ces calculs sur les points, egalement (et les entiers
	// ?)

	/**
	 * D bscan apache.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @param eps
	 *            the eps
	 * @param minPts
	 *            the min pts
	 * @return the i list
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "dbscan",
			can_be_const = false,
			type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc (
			value = "returns the list of clusters (list of instance indices) computed with the dbscan"
					+ " (density-based spatial clustering of applications with noise) algorithm from the "
					+ "first operand data according to the maximum radius of the neighborhood to be considered (eps) "
					+ "and the minimum number of points needed for a cluster (minPts). Usage: dbscan(data,eps,minPoints)",
			special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0",
			examples = { @example (
					value = "dbscan ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],10,2)",
					equals = "[[0,1,2,3]]") })
	public static IList<IList> opDBScan(final IScope scope, final IList data, final Double eps, final Integer minPts)
			throws GamaRuntimeException {
		final IList<Integer> remainingData = GamaListFactory.create(Types.INT);
		final DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer(eps, minPts);
		final List<DoublePoint> instances = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			final IList d = (IList) data.get(i);
			final double point[] = new double[d.size()];
			for (int j = 0; j < d.size(); j++) { point[j] = Cast.asFloat(scope, d.get(j)); }
			remainingData.add(i);
			instances.add(new Instance(i, point));
		}
		final List<Cluster<DoublePoint>> clusters = dbscan.cluster(instances);

		try (final Collector.AsList results = Collector.getList()) {
			for (final Cluster<DoublePoint> cl : clusters) {
				final IList clG = GamaListFactory.create();
				for (final DoublePoint pt : cl.getPoints()) {
					final Integer id = ((Instance) pt).getId();
					clG.addValue(scope, id);
					remainingData.remove(id);
				}
				results.add(clG);
			}
			for (final Integer id : remainingData) {
				final IList clG = GamaListFactory.create();
				clG.add(id);
				results.add(clG);
			}
			return results.items();
		}
	}

	/**
	 *
	 *
	 * @param scope
	 * @param data
	 * @return
	 */
	@operator (
			value = "durbin_watson",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Durbin-Watson computation",
			comment = "",
			examples = { @example (
					value = "durbin_watson([13,2,1,4,1,2]) with_precision(4)",
					equals = "0.7231") })
	public static Double opDurbinWatson(final IScope scope, final IContainer data) {

		// TODO input parameters validation

		return Descriptive.durbinWatson(toDoubleArrayList(scope, data));
	}

	/**
	 * Op dynamic time warping.
	 *
	 * @param scope
	 *            the scope
	 * @param vals1
	 *            the vals 1
	 * @param vals2
	 *            the vals 2
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "dtw",
			can_be_const = false,
			// type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "returns the dynamic time warping between the two series of values (step pattern used: symetric1)",
			examples = { @example (
					value = "dtw([32.0,5.0,1.0,3.0],[1.0,10.0,5.0,1.0])",
					equals = "38.0") })
	public static Double opDynamicTimeWarping(final IScope scope, final IList vals1, final IList vals2)
			throws GamaRuntimeException {
		final int n1 = vals1.size();
		final int n2 = vals2.size();
		final double[][] table = new double[2][n2 + 1];

		table[0][0] = 0;

		for (int i = 1; i <= n2; i++) { table[0][i] = Double.POSITIVE_INFINITY; }

		for (int i = 1; i <= n1; i++) {
			table[1][0] = Double.POSITIVE_INFINITY;

			for (int j = 1; j <= n2; j++) {
				final double cost =
						Math.abs(Cast.asFloat(scope, vals1.get(i - 1)) - Cast.asFloat(scope, vals2.get(j - 1)));

				double min = table[0][j - 1];

				if (min > table[0][j]) { min = table[0][j]; }

				if (min > table[1][j - 1]) { min = table[1][j - 1]; }

				table[1][j] = cost + min;
			}

			final double[] swap = table[0];
			table[0] = table[1];
			table[1] = swap;
		}

		return table[0][n2];
	}

	/**
	 * Op dynamic time warping.
	 *
	 * @param scope
	 *            the scope
	 * @param vals1
	 *            the vals 1
	 * @param vals2
	 *            the vals 2
	 * @param radius
	 *            the radius
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "dtw",
			can_be_const = false,
			// type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "returns the dynamic time warping between the two series of values"
					+ " (step pattern used: symetric1) with Sakoe-Chiba band (radius: the window width of Sakoe-Chiba band)",
			examples = { @example (
					value = "dtw([10.0,5.0,1.0, 3.0],[1.0,10.0,5.0,1.0], 2)",
					equals = "11.0") })
	public static Double opDynamicTimeWarping(final IScope scope, final IList vals1, final IList vals2,
			final int radius) throws GamaRuntimeException {
		final int n1 = vals1.size();
		final int n2 = vals2.size();
		final double[][] table = new double[2][n2 + 1];

		table[0][0] = 0;

		for (int i = 1; i <= n2; i++) { table[0][i] = Double.POSITIVE_INFINITY; }

		for (int i = 1; i <= n1; i++) {
			final int start = Math.max(1, i - radius);
			final int end = Math.min(n2, i + radius);

			table[1][start - 1] = Double.POSITIVE_INFINITY;
			if (end < n2) { table[1][end + 1] = Double.POSITIVE_INFINITY; }

			for (int j = start; j <= end; j++) {
				final double cost =
						Math.abs(Cast.asFloat(scope, vals1.get(i - 1)) - Cast.asFloat(scope, vals2.get(j - 1)));

				double min = table[0][j - 1];

				if (min > table[0][j]) { min = table[0][j]; }

				if (min > table[1][j - 1]) { min = table[1][j - 1]; }

				table[1][j] = cost + min;
			}

			final double[] swap = table[0];
			table[0] = table[1];
			table[1] = swap;
		}

		return table[0][n2];

	}

	/**
	 * Frequency of.
	 *
	 * @param scope
	 *            the scope
	 * @param original
	 *            the original
	 * @param filter
	 *            the filter
	 * @return the i map
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = { "frequency_of" },
			can_be_const = true,
			iterator = true,
			index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			content_type = IType.INT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns a map with keys equal to the application of the right-hand argument (like collect) "
					+ "and values equal to the frequency of this key (i.e. how many times it has been obtained)",
			comment = "",
			examples = { @example (
					value = "[1, 2, 3, 3, 4, 4, 5, 3, 3, 4] frequency_of each",
					equals = "map([1::1,2::1,3::4,4::3,5::1])") })

	public static IMap opFrequencyOf(final IScope scope, final IContainer original, final IExpression filter)
			throws GamaRuntimeException {
		if (original == null) return GamaMapFactory.create(Types.NO_TYPE, Types.INT);
		final IMap<Object, Integer> result = GamaMapFactory.create(original.getGamlType().getContentType(), Types.INT);
		for (final Object each : original.iterable(scope)) {
			scope.setEach(each);
			final Object key = filter.value(scope);
			if (!result.containsKey(key)) {
				result.put(key, 1);
			} else {
				result.put(key, result.get(key) + 1);
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param scope
	 * @param x
	 * @return
	 */
	@operator (
			value = "gamma",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the value of the Gamma function at x.",
			comment = "",
			examples = { @example (
					value = "gamma(5)",
					equals = "24.0") }

	)
	public static Double opGamma(final IScope scope, final Double x) {

		// Returns the value of the Gamma function at x.
		try {
			return Gamma.gamma(x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
		}
	}

	/**
	 *
	 *
	 * @param scope
	 * @param a
	 *            the paramater a (alpha) of the gamma distribution (shape parameter).
	 * @param b
	 *            the paramater b (beta, lambda) of the gamma distribution (rate parameter, inverse scale parameter
	 *            theta).
	 * @param x
	 *            integration end point.
	 * @return
	 */
	@operator (
			value = { "gamma_distribution", "pgamma" },
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the integral from zero to x of the gamma probability density function.",
			comment = "incomplete_gamma(a,x) is equal to pgamma(a,1,x).",
			examples = { @example (
					value = "gamma_distribution(2,3,0.9) with_precision(3)",
					equals = "0.269") })

	public static Double opGamma(final IScope scope, final Double a, final Double b, final Double x) {

		// Returns the integral from zero to x of the gamma probability
		// density function.
		try {
			return Probability.gamma(a, b, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
		}
	}

	// @operator (
	// value = { "corR", "R_correlation" },
	// can_be_const = false,
	// type = IType.FLOAT,
	// category = { IOperatorCategory.STATISTICAL },
	// concept = { IConcept.STATISTIC })
	// @doc (
	// value = "returns the Pearson correlation coefficient of two given vectors (right-hand operands)"
	// + " in given variable (left-hand operand).",
	// special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0",
	// examples = { @example (
	// value = "list X <- [1, 2, 3];",
	// isExecutable = false),
	// @example (
	// value = "list Y <- [1, 2, 4];",
	// isExecutable = false),
	// @example (
	// value = "corR(X, Y)",
	// equals = "0.981980506061966",
	// isExecutable = false) })
	// @no_test // because require R to be installed.
	//
	// public static Object getCorrelationR(final IScope scope, final IContainer l1, final IContainer l2)
	// throws GamaRuntimeException, ParseException, ExecutionException {
	// if (l1.length(scope) == 0 || l2.length(scope) == 0) return Double.valueOf(0d);
	//
	// if (l1.length(scope) != l2.length(scope)) return Double.valueOf(0d);
	//
	// final RCaller caller = new RCaller();
	// final RCode code = new RCode();
	//
	// final String RPath = GamaPreferences.External.LIB_R.value(scope).getPath(scope);
	// caller.setRscriptExecutable(RPath);
	// // caller.setRscriptExecutable("\"" + RPath + "\"");
	// // if ( java.lang.System.getProperty("os.name").startsWith("Mac") ) {
	// // caller.setRscriptExecutable(RPath);
	// // }
	//
	// final double[] vectorX = new double[l1.length(scope)];
	// final double[] vectorY = new double[l2.length(scope)];
	//
	// int i = 0;
	// for (final Object o : l1.iterable(scope)) {
	// vectorX[i++] = Double.parseDouble(o.toString());
	// }
	//
	// i = 0;
	// for (final Object o : l2.iterable(scope)) {
	// vectorY[i++] = Double.parseDouble(o.toString());
	// }
	//
	// code.addDoubleArray("vectorX", vectorX);
	// code.addDoubleArray("vectorY", vectorY);
	//
	// code.addRCode("corCoef<-cor(vectorX, vectorY, method='pearson')");
	// caller.setRCode(code);
	// caller.runAndReturnResult("corCoef");
	//
	// double[] results;
	// try {
	// results = caller.getParser().getAsDoubleArray("corCoef");
	// } catch (final Exception ex) {
	// return 0.0;
	// }
	//
	// return results[0];
	// }

	// @operator (
	// value = { "meanR", "R_mean" },
	// can_be_const = false,
	// type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
	// category = { IOperatorCategory.STATISTICAL },
	// concept = { IConcept.STATISTIC })
	// @doc (
	// value = "returns the mean value of given vector (right-hand operand) in given variable (left-hand operand).",
	// examples = { @example (
	// value = "list<int> X <- [2, 3, 1];",
	// isExecutable = false),
	// @example (
	// value = "meanR(X)",
	// equals = "2",
	// returnType = IKeyword.INT,
	// isExecutable = false),
	// @example (
	// value = "meanR([2, 3, 1])",
	// equals = "2",
	// isExecutable = false) })
	// @no_test
	// public static Object getMeanR(final IScope scope, final IContainer l)
	// throws GamaRuntimeException, ParseException, ExecutionException {
	// if (l.length(scope) == 0) return Double.valueOf(0d);
	//
	// double[] results;
	// final RCaller caller = new RCaller();
	// final RCode code = new RCode();
	//
	// final String RPath = GamaPreferences.External.LIB_R.value(scope).getPath(scope);
	// caller.setRscriptExecutable(RPath);
	// // caller.setRscriptExecutable("\"" + RPath + "\"");
	// // if ( java.lang.System.getProperty("os.name").startsWith("Mac") ) {
	// // caller.setRscriptExecutable(RPath);
	// // }
	//
	// final double[] data = new double[l.length(scope)];
	// int i = 0;
	// for (final Object o : l.iterable(scope)) {
	// data[i++] = Double.parseDouble(o.toString());
	// }
	//
	// code.addDoubleArray("data", data);
	// code.addRCode("mean<-mean(data)");
	// caller.setRCode(code);
	// caller.runAndReturnResult("mean");
	// results = caller.getParser().getAsDoubleArray("mean");
	// return results[0];
	// }

	/**
	 *
	 *
	 * @param scope
	 * @param a
	 *            the paramater a (alpha) of the gamma distribution.
	 * @param b
	 *            the paramater b (beta, lambda) of the gamma distribution.
	 * @param x
	 *            integration end point.
	 * @return
	 */
	@operator (
			value = "gamma_distribution_complemented",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the integral from x to infinity of the gamma probability density function.",
			comment = "",
			examples = { @example (
					value = "gamma_distribution_complemented(2,3,0.9) with_precision(3)",
					equals = "0.731") })

	public static Double opGammaComplemented(final IScope scope, final Double a, final Double b, final Double x) {

		// Returns the integral from x to infinity of the gamma probability
		// density function.
		try {
			return Probability.gammaComplemented(a, b, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .gamma reports: " + ex, scope);
		}
	}

	/**
	 * Op geom mean.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the double
	 */
	@operator (
			value = "geometric_mean",
			can_be_const = true,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the geometric mean of the elements of the operand. "
					+ "See <a href=\"http://en.wikipedia.org/wiki/Geometric_mean\">Geometric_mean</a> for more details.",
			comment = "The operator casts all the numerical element of the list into float. "
					+ "The elements that are not numerical are discarded.",
			special_cases = { "" },
			examples = { @example (
					value = "geometric_mean ([4.5, 3.5, 5.5, 7.0])",
					equals = "4.962326343467649") },
			see = { "mean", "median", "harmonic_mean" })
	public static Double opGeometricMean(final IScope scope, final IContainer values) {
		final DataSet d = toDataSet(scope, values);
		return d.getGeometricMean();
	}

	/**
	 * Gini index.
	 *
	 * @param scope
	 *            the scope
	 * @param vals
	 *            the vals
	 * @return the double
	 */
	@operator (
			value = "gini",
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.STATISTICAL },
			concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
	@doc (
			usages = { @usage (
					value = "return the Gini Index of the given list of values (list of floats)",
					examples = { @example (
							value = "gini([1.0, 0.5, 2.0])",
							equals = "the gini index computed i.e. 0.2857143",
							test = false) }) })

	@test ("(gini([1.0, 0.5, 2.0]) with_precision 4) = 0.2857")

	public static double opGini(final IScope scope, final IList<Double> vals) {
		final int N = vals.size();
		double G = 0.0;
		double sumXi = 0.0;
		for (int i = 0; i < N; i++) {
			final double xi = vals.get(i);
			sumXi += xi;
			for (int j = 0; j < N; j++) {
				final double yi = vals.get(j);
				G += Math.abs(xi - yi);
			}
		}
		G /= 2 * N * sumXi;
		return G;
	}

	/**
	 * Op harmonic mean.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the double
	 */
	@operator (
			value = "harmonic_mean",
			can_be_const = true,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the harmonic mean of the elements of the operand. "
					+ "See <a href=\"http://en.wikipedia.org/wiki/Harmonic_mean\">Harmonic_mean</a> for more details.",
			comment = "The operator casts all the numerical element of the list into float."
					+ " The elements that are not numerical are discarded.",
			special_cases = { "" },
			examples = { @example (
					value = "harmonic_mean ([4.5, 3.5, 5.5, 7.0])",
					equals = "4.804159445407279") },
			see = { "mean", "median", "geometric_mean" })
	public static Double opHarmonicMean(final IScope scope, final IContainer values) {
		final DataSet d = toDataSet(scope, values);
		return d.getHarmonicMean();
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
	@operator (
			value = "incomplete_beta",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the regularized integral of the beta function with arguments a and b, from zero to x.",
			comment = "",
			examples = { @example (
					value = "incomplete_beta(2,3,0.9) with_precision(3)",
					equals = "0.996") })

	public static Double opIncompleteBeta(final IScope scope, final Double a, final Double b, final Double x) {

		// Returns the regularized integral of the beta function with
		// arguments
		// a and b, from zero to x.
		try {
			return Gamma.incompleteBeta(a, b, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .incompleteBeta reports: " + ex, scope);
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
	@operator (
			value = "incomplete_gamma",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = " Returns the regularized integral of the Gamma function with argument a to the integration end point x.",
			comment = "",
			examples = { @example (
					value = "incomplete_gamma(1,5.3) with_precision(3)",
					equals = "0.995")

			})

	public static Double opIncompleteGamma(final IScope scope, final Double a, final Double x) {

		// Returns the regularized integral of the Gamma function with
		// argument
		// a to the integration end point x.
		try {
			return Gamma.incompleteGamma(a, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
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
	@operator (
			value = "incomplete_gamma_complement",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the complemented regularized incomplete Gamma function of the argument a and integration start point x.",
			comment = "Is the complement to 1 of incomplete_gamma.",
			examples = { @example (
					value = "incomplete_gamma_complement(1,5.3) with_precision(3)",
					equals = "0.005")

			})

	public static Double opIncompleteGammaComplement(final IScope scope, final Double a, final Double x) {
		// Returns the complemented regularized incomplete Gamma function of
		// the
		// argument a and integration start point x.
		try {
			return Gamma.incompleteGammaComplement(a, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .incompleteGammaComplement reports: " + ex, scope);
		}
	}

	/**
	 * K means plusplus apache.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @param k
	 *            the k
	 * @return the i list
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "kmeans",
			can_be_const = false,
			type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc (
			value = "returns the list of clusters (list of instance indices) computed with the kmeans++ "
					+ "algorithm from the first operand data according to the number of clusters to split"
					+ " the data into (k). Usage: kmeans(data,k)",
			// special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0",
			usages = { @usage (
					value = "The maximum number of (third operand) can be omitted.",
					examples = { @example (
							value = "kmeans ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],2)",
							equals = "[[0,2,3],[1]]") }) })
	public static IList<IList> opKMeans(final IScope scope, final IList data, final Integer k)
			throws GamaRuntimeException {
		return opKMeans(scope, data, k, -1);
	}

	/**
	 * K means plusplus apache.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @param k
	 *            the k
	 * @param maxIt
	 *            the max it
	 * @return the i list
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "kmeans",
			can_be_const = false,
			type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc (
			value = "returns the list of clusters (list of instance indices) computed with the kmeans++ "
					+ "algorithm from the first operand data according to the number of clusters to split"
					+ " the data into (k) and the maximum number of iterations to run the algorithm."
					+ "(If negative, no maximum will be used) (maxIt). Usage: kmeans(data,k,maxit)",
			// special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0",
			masterDoc = true,
			examples = { @example (
					value = "kmeans ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],2,10)",
					equals = "[[0,2,3],[1]]") })
	public static IList<IList> opKMeans(final IScope scope, final IList data, final Integer k, final Integer maxIt)
			throws GamaRuntimeException {
		// AD 04/21 : Is it ok to use an additional generator here ?
		final MersenneTwister rand = new MersenneTwister(scope.getRandom().getSeed().longValue());

		final List<DoublePoint> instances = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			final IList d = (IList) data.get(i);
			final double point[] = new double[d.size()];
			for (int j = 0; j < d.size(); j++) { point[j] = Cast.asFloat(scope, d.get(j)); }
			instances.add(new Instance(i, point));
		}
		final KMeansPlusPlusClusterer<DoublePoint> kmeans =
				new KMeansPlusPlusClusterer<>(k, maxIt, new EuclideanDistance(), rand);
		final List<CentroidCluster<DoublePoint>> clusters = kmeans.cluster(instances);
		try (final Collector.AsList results = Collector.getList()) {
			for (final Cluster<DoublePoint> cl : clusters) {
				final IList clG = GamaListFactory.create();
				for (final DoublePoint pt : cl.getPoints()) { clG.addValue(scope, ((Instance) pt).getId()); }
				results.add(clG);
			}
			return results.items();
		}
	}

	/**
	 *
	 *
	 * @param scope
	 * @param moment4
	 * @param standardDeviation
	 * @return
	 */
	@operator (
			value = "kurtosis",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the kurtosis from a moment and a standard deviation",
			comment = "",
			examples = { @example (
					value = "kurtosis(3,12) with_precision(4)",
					equals = "-2.9999") })
	public static Double opKurtosis(final IScope scope, final Double moment4, final Double standardDeviation) {
		// TODO input parameters validation
		return Descriptive.kurtosis(moment4, standardDeviation);
	}

	// /**
	// *
	// *
	// * @param scope
	// * @param data
	// * @return
	// */
	// @operator (
	// value = "kurtosis",
	// can_be_const = true,
	// type = IType.FLOAT,
	// expected_content_type = { IType.INT, IType.FLOAT },
	// category = { IOperatorCategory.STATISTICAL },
	// concept = { IConcept.STATISTIC })
	// @doc (
	// value = "Returns the kurtosis (aka excess) of a data sequence",
	// comment = "",
	// examples = { @example (
	// value = "kurtosis([13,2,1,4,1,2]) with_precision(4)",
	// equals = "4.8083") })
	// public static Double opKurtosis(final IScope scope, final IContainer data) {
	//
	// // TODO input parameters validation
	//
	// final double mean = (Double) Containers.opMean(scope, data);
	// final double standardDeviation = Stats.opStandardDeviation(scope, data);
	//
	// return Descriptive.kurtosis(toDoubleArrayList(scope, data), mean, standardDeviation);
	// }

	/**
	 * Kurtosis.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "kurtosis",
			can_be_const = true,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc (
			value = "Returns the kurtosis (aka excess) of a list of values "
					+ "(kurtosis = { [n(n+1) / (n -1)(n - 2)(n-3)] sum[(x_i - mean)^4] / std^4 } - [3(n-1)^2 / (n-2)(n-3)])",
			special_cases = "if the length of the list is lower than 3, returns NaN",
			examples = { @example (
					value = "kurtosis ([1,2,3,4,5])",
					equals = "-1.200000000000002"),
					@example (
							value = "kurtosis([13,2,1,4,1,2]) with_precision(4)",
							equals = "4.8083") })
	public static Double opKurtosis(final IScope scope, final IList data) throws GamaRuntimeException {
		final Kurtosis k = new Kurtosis();
		final double[] values = new double[data.length(scope)];
		for (int i = 0; i < values.length; i++) { values[i] = Cast.asFloat(scope, data.get(i)); }
		return k.evaluate(values);
	}

	/**
	 *
	 *
	 * @param scope
	 * @param x
	 * @return
	 */
	@operator (
			value = { "log_gamma", "lgamma" },
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the log of the value of the Gamma function at x.",
			comment = "",
			examples = { @example (
					value = "log_gamma(0.6) with_precision(4)",
					equals = "0.3982") })

	public static Double opLogGamma(final IScope scope, final Double x) {

		// Returns the log of the value of the Gamma function at x.
		try {
			return Gamma.logGamma(x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .logGamma reports: " + ex, scope);
		}
	}

	/**
	 * Max.
	 *
	 * @param scope
	 *            the scope
	 * @param l
	 *            the l
	 * @return the object
	 */
	@operator (
			value = "max",
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the maximum element found in the operand",
			masterDoc = true,
			comment = "the max operator behavior depends on the nature of the operand",
			usages = {

					@usage (
							value = "if it is a list of int of float, max returns the maximum of all the elements",
							examples = { @example (
									value = "max ([100, 23.2, 34.5])",
									equals = "100.0") }),
					@usage (
							value = "if it is a list of points: max returns the maximum of all points as a point "
									+ "(i.e. the point with the greatest coordinate on the x-axis, in case of equality "
									+ "the point with the greatest coordinate on the y-axis is chosen. "
									+ "If all the points are equal, the first one is returned. )",
							examples = { @example (
									value = "max([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}])",
									equals = "{9.0,1.0}") }),
					@usage ("if it is a population of a list of other type: max transforms all elements into "
							+ "integer and returns the maximum of them"),
					@usage ("if it is a map, max returns the maximum among the list of all elements value"),
					@usage ("if it is a file, max returns the maximum of the content of the file (that is "
							+ "also a container)"),
					@usage ("if it is a graph, max returns the maximum of the list of the elements of the graph"
							+ " (that can be the list of edges or vertexes depending on the graph)"),
					@usage ("if it is a matrix of int, float or object, max returns the maximum of all "
							+ "the numerical elements (thus all elements for integer and float matrices)"),
					@usage ("if it is a matrix of geometry, max returns the maximum of the list of the geometries"),
					@usage ("if it is a matrix of another type, max returns the maximum of the elements "
							+ "transformed into float") },
			see = { "min" })
	public static Object opMax(final IScope scope, final IContainer l) {
		if (l instanceof GamaField) return ((GamaField) l).getMinMax(null)[1];
		Number maxNum = null;
		GamaPoint maxPoint = null;
		for (final Object o : l.iterable(scope)) {
			if (o instanceof GamaPoint && maxNum == null) {
				if (maxPoint == null || ((GamaPoint) o).compareTo(maxPoint) > 0) { maxPoint = (GamaPoint) o; }
			} else if (o instanceof Number && maxPoint == null
					&& (maxNum == null || ((Number) o).doubleValue() > maxNum.doubleValue())) {
				maxNum = (Number) o;
			} else {
				final Double d = Cast.asFloat(scope, o);
				if (maxNum == null || d > maxNum.doubleValue()) { maxNum = d; }
			}
		}
		return maxNum == null ? maxPoint : maxNum;
	}

	/**
	 * Op mean deviation.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the double
	 */
	@operator (
			value = "mean_deviation",
			can_be_const = true,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the deviation from the mean of all the elements of the operand. "
					+ "See <a href= \"http://en.wikipedia.org/wiki/Absolute_deviation\" >Mean_deviation</a> for more details.",
			comment = "The operator casts all the numerical element of the list into float."
					+ " The elements that are not numerical are discarded.",
			examples = { @example (
					value = "mean_deviation ([4.5, 3.5, 5.5, 7.0])",
					equals = "1.125") },
			see = { "mean", "standard_deviation" })
	public static Double opMeanDeviation(final IScope scope, final IContainer values) {
		final DataSet d = toDataSet(scope, values);
		return d.getMeanDeviation();
	}

	/**
	 * Op median.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the object
	 */
	@operator (
			value = "median",
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT, IType.COLOR },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER, IOperatorCategory.COLOR },
			concept = { IConcept.STATISTIC, IConcept.COLOR })
	@doc (
			value = "the median of all the elements of the operand.",
			special_cases = {
					"if the container contains points, the result will be a point. If the container contains rgb values, "
							+ "the result will be a rgb color" },
			examples = { @example (
					value = "median ([4.5, 3.5, 5.5, 3.4, 7.0])",
					equals = "4.5") },
			see = { "mean" })
	public static Object opMedian(final IScope scope, final IContainer values) {

		final IType contentType = values.getGamlType().getContentType();
		if (values.length(scope) == 0) return contentType.cast(scope, 0d, null, false);
		switch (contentType.id()) {
			case IType.INT:
			case IType.FLOAT:
				final DataSet d2 = new DataSet();
				for (final Object o : values.iterable(scope)) { d2.addValue(Cast.asFloat(scope, o)); }
				final Number result = d2.getSize() == 0 ? 0.0 : d2.getMedian();
				return contentType.cast(scope, result, null, false);
			case IType.POINT:
				final DataSet x = new DataSet();
				final DataSet y = new DataSet();
				final DataSet z = new DataSet();
				for (final Object o : values.iterable(scope)) {
					final GamaPoint p = (GamaPoint) o;
					x.addValue(p.getX());
					y.addValue(p.getY());
					z.addValue(p.getZ());
				}
				if (x.getSize() == 0) return new GamaPoint(0, 0, 0);
				return new GamaPoint(x.getMedian(), y.getMedian(), z.getMedian());
			case IType.COLOR:
				final DataSet r = new DataSet();
				final DataSet g = new DataSet();
				final DataSet b = new DataSet();
				for (final Object o : values.iterable(scope)) {
					final GamaColor p = (GamaColor) o;
					r.addValue(p.getRed());
					g.addValue(p.getGreen());
					b.addValue(p.getBlue());
				}
				if (r.getSize() == 0) return new GamaColor(0, 0, 0, 0);
				return new GamaColor((int) r.getMedian(), (int) g.getMedian(), (int) b.getMedian(), 0);
			default:
				final DataSet d = new DataSet();
				for (final Object o : values.iterable(scope)) { d.addValue(Cast.asFloat(scope, o)); }
				final Number n = d.getSize() == 0 ? 0.0 : d.getMedian();
				return Cast.asFloat(scope, n);

		}
	}

	/**
	 * Min.
	 *
	 * @param scope
	 *            the scope
	 * @param l
	 *            the l
	 * @return the object
	 */
	@operator (
			value = "min",
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the minimum element found in the operand.",
			masterDoc = true,
			comment = "the min operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list of int or float: min returns the minimum of all the elements",
					examples = { @example (
							value = "min ([100, 23.2, 34.5])",
							equals = "23.2") }),
					@usage (
							value = "if it is a list of points: min returns the minimum of all points as a point "
									+ "(i.e. the point with the smallest coordinate on the x-axis, in case of "
									+ "equality the point with the smallest coordinate on the y-axis is chosen."
									+ " If all the points are equal, the first one is returned. )"),
					@usage (
							value = "if it is a population of a list of other types: min transforms all elements "
									+ "into integer and returns the minimum of them"),
					@usage (
							value = "if it is a map, min returns the minimum among the list of all elements value"),
					@usage (
							value = "if it is a file, min returns the minimum of the content of the file (that is"
									+ " also a container)"),
					@usage (
							value = "if it is a graph, min returns the minimum of the list of the elements of "
									+ "the graph (that can be the list of edges or vertexes depending on the graph)"),
					@usage (
							value = "if it is a matrix of int, float or object, min returns the minimum of all the "
									+ "numerical elements (thus all elements for integer and float matrices)"),
					@usage (
							value = "if it is a matrix of geometry, min returns the minimum of the list of the geometries"),
					@usage (
							value = "if it is a matrix of another type, min returns the minimum of the elements"
									+ " transformed into float") },
			see = { "max" })
	public static Object opMin(final IScope scope, final IContainer l) {
		if (l instanceof GamaField) return ((GamaField) l).getMinMax(null)[0];
		Number minNum = null;
		GamaPoint minPoint = null;
		for (final Object o : l.iterable(scope)) {
			if (o instanceof GamaPoint && minNum == null) {
				if (minPoint == null || ((GamaPoint) o).compareTo(minPoint) < 0) { minPoint = (GamaPoint) o; }
			} else if (o instanceof Number && minPoint == null
					&& (minNum == null || ((Number) o).doubleValue() < minNum.doubleValue())) {
				minNum = (Number) o;
			} else {
				final Double d = Cast.asFloat(scope, o);
				if (minNum == null || d < minNum.doubleValue()) { minNum = d; }
			}
		}
		return minNum == null ? minPoint : minNum;
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
	@operator (
			value = "moment",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the moment of k-th order with constant c of a data sequence",
			comment = "",
			examples = { @example (
					value = "moment([13,2,1,4,1,2], 2, 1.2) with_precision(4)",
					equals = "24.74") })
	public static Double opMoment(final IScope scope, final IContainer data, final Integer k, final Double c) {

		// TODO input parameters validation

		return Descriptive.moment(toDoubleArrayList(scope, data), k, c);
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
	@operator (
			value = { "normal_area", "pnorm" },
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the area to the left of x in the normal distribution with the given mean and standard deviation.",
			comment = "",
			examples = { @example (
					value = "normal_area(0.9,0,1) with_precision(3)",
					equals = "0.816") })

	public static Double opNormalArea(final IScope scope, final Double x, final Double mean, final Double sd) {

		// Returns the area to the left of x in the normal distribution
		// with the given mean and standard deviation.
		try {
			return Probability.normal(mean, sd, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .normal reports: " + ex, scope);
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
	@operator (
			value = { "normal_density", "dnorm" },
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the probability of x in the normal distribution with the given mean and standard deviation.",
			comment = "",
			examples = { @example (
					value = "(normal_density(2,1,1)*100) with_precision 2",
					equals = "24.2") })

	public static Double opNormalDensity(final IScope scope, final Double x, final Double mean, final Double sd) {

		// Returns the probability of x in the normal distribution with the
		// given mean and standard deviation.
		final double var = sd * sd;
		final double c = 1.0 / Math.sqrt(2.0 * Math.PI * var);
		final double b = (x - mean) * (x - mean) / (2.0 * var);
		return c * Math.exp(-b);
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
	@operator (
			value = "normal_inverse",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the x in the normal distribution with the given mean and standard deviation, to the left of which lies the given area. normal.",
			comment = "",
			examples = { @example (
					value = "normal_inverse(0.98,0,1) with_precision(2)",
					equals = "2.05") })
	public static Double opNormalInverse(final IScope scope, final Double area, final Double mean, final Double sd) {

		// Returns the x in the normal distribution with the given mean and
		// standard deviation, to the left of which lies the given area.
		// normal.Inverse returns the value in terms of standard deviations
		// from the mean, so we need to adjust it for the given mean and
		// standard deviation.
		try {
			final double x = Probability.normalInverse(area);
			return (x + mean) * sd;
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .normalInverse reports: " + ex, scope);
		}
	}

	/**
	 * Predict from regression.
	 *
	 * @param scope
	 *            the scope
	 * @param regression
	 *            the regression
	 * @param instance
	 *            the instance
	 * @return the double
	 */
	@operator (
			value = "predict",
			can_be_const = false,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.REGRESSION })
	@doc (
			value = "returns the value predicted by the regression parameters for a given instance. "
					+ "Usage: predict(regression, instance)",
			examples = { @example (
					value = "predict(my_regression, [1,2,3])",
					isExecutable = false) })
	@test ("predict(build(matrix([[1.0,2.0,3.0,4.0],[2.0,3.0,4.0,2.0]])),[1,2,3,2] ) = 2.1818181818181817")
	public static Double opPredict(final IScope scope, final GamaRegression regression, final IList instance) {
		return regression.predict(scope, instance);
	}

	/**
	 * Product.
	 *
	 * @param scope
	 *            the scope
	 * @param l
	 *            the l
	 * @return the object
	 */
	@SuppressWarnings ("null")
	@operator (
			value = { "mul", "product" },
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the product of all the elements of the operand",
			masterDoc = true,
			comment = "the mul operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list of int or float: mul returns the product of all the elements",
					examples = { @example (
							value = "mul ([100, 23.2, 34.5])",
							equals = "80040.0") }),
					@usage (
							value = "if it is a list of points: mul returns the product of all points as a point"
									+ " (each coordinate is the product of the corresponding coordinate of each element)"),
					@usage (
							value = "if it is a list of other types: mul transforms all elements into integer and "
									+ "multiplies them"),
					@usage (
							value = "if it is a map, mul returns the product of the value of all elements"),
					@usage (
							value = "if it is a file, mul returns the product of the content of the file (that is"
									+ " also a container)"),
					@usage (
							value = "if it is a graph, mul returns the product of the list of the elements of the graph"
									+ " (that can be the list of edges or vertexes depending on the graph)"),
					@usage (
							value = "if it is a matrix of int, float or object, mul returns the product of all the numerical "
									+ "elements (thus all elements for integer and float matrices)"),
					@usage (
							value = "if it is a matrix of geometry, mul returns the product of the list of the geometries"),
					@usage (
							value = "if it is a matrix of other types: mul transforms all elements into float and "
									+ "multiplies them") },
			see = { "sum" })
	public static Object opProduct(final IScope scope, final IContainer l) {
		final DataSet x = new DataSet();
		DataSet y = null, z = null;
		for (final Object o : l.iterable(scope)) {
			if (o instanceof GamaPoint) {
				if (y == null) {
					y = new DataSet();
					z = new DataSet();
				}
				final GamaPoint p = (GamaPoint) o;
				x.addValue(p.getX());
				y.addValue(p.getY());
				z.addValue(p.getZ());
			} else {
				x.addValue(Cast.asFloat(scope, o));
			}
		}
		if (x.getSize() == 0) {
			if (y == null) return 0.0;
			return new GamaPoint(0, 0, 0);
		}
		if (y == null) return x.getProduct();
		return new GamaPoint(x.getProduct(), y.getProduct(), z.getProduct());
	}

	/**
	 * Product of.
	 *
	 * @param scope
	 *            the scope
	 * @param container
	 *            the container
	 * @param filter
	 *            the filter
	 * @return the object
	 */
	@operator (
			value = { "product_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the product of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage (
					value = "if the left-operand is a map, the keyword each will contain each value",
					examples = { @example (
							value = "[1::2, 3::4, 5::6] product_of (each)",
							equals = "48") }) },
			examples = { @example (
					value = "[1,2] product_of (each * 10 )",
					equals = "200") },
			see = { "min_of", "max_of", "sum_of", "mean_of" })
	@test ("[3,4] product_of (each *2) = 48")
	public static Object opProductOf(final IScope scope, final IContainer container, final IExpression filter) {
		return opProduct(scope, collect(scope, container, filter));
	}

	/**
	 *
	 *
	 * @param scope
	 * @param fstat
	 * @param dfn
	 * @param dfd
	 * @return
	 */
	@operator (
			value = "pValue_for_fStat",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the P value of F statistic fstat with numerator degrees of freedom dfn and denominator degress of freedom dfd. Uses the incomplete Beta function.",
			comment = "",
			examples = { @example (
					value = "pValue_for_fStat(1.9,10,12) with_precision(3)",
					equals = "0.145") })

	public static Double opPvalueForFstat(final IScope scope, final Double fstat, final Integer dfn,
			final Integer dfd) { // see Spatial.Punctual.angle_between

		// Returns the P value of F statistic fstat with numerator degrees
		// of freedom dfn and denominator degrees of freedom dfd.
		// Uses the incomplete Beta function.

		final double x = dfd / (dfd + dfn * fstat);
		try {
			return Gamma.incompleteBeta(dfd / 2.0, dfn / 2.0, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
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
	@operator (
			value = "pValue_for_tStat",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the P value of the T statistic tstat with df degrees of freedom. "
					+ "This is a two-tailed test so we just double the right tail which is given by studentT of -|tstat|.",
			comment = "",
			examples = { @example (
					value = "pValue_for_tStat(0.9,10) with_precision(3)",
					equals = "0.389") })

	public static Double opPvalueForTstat(final IScope scope, final Double tstat, final Integer df) {

		// Returns the P value of the T statistic tstat with df degrees of
		// freedom. This is a two-tailed test so we just double the right
		// tail which is given by studentT of -|tstat|.

		final double x = Math.abs(tstat);
		try {
			final double p = Probability.studentT(df, -x);
			return 2.0 * p;
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .studentT reports: " + ex, scope);
		}
	}

	/**
	 *
	 *
	 * @param scope
	 * @param data
	 * @param phi
	 * @return
	 */
	@operator (
			value = "quantile",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the phi-quantile; that is, an element elem for which holds that phi percent of data elements are less than elem. The quantile does not need necessarily to be contained in the data sequence, it can be a linear interpolation. Note that the container holding the values must be sorted first",
			comment = "",
			examples = { @example (
					value = "quantile([1,3,5,6,9,11,12,13,19,21,22,32,35,36,45,44,55,68,79,80,81,88,90,91,92,100], 0.5)",
					equals = "35.5") })
	public static Double opQuantile(final IScope scope, final IContainer data, final Double phi) {

		// TODO input parameters validation

		return Descriptive.quantile(toDoubleArrayList(scope, data), phi);
	}

	/**
	 *
	 *
	 * @param scope
	 * @param data
	 * @param element
	 * @return
	 */
	@operator (
			value = { "quantile_inverse", "percentile" },
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns how many percent of the elements contained in the receiver are <= element. Does linear interpolation if the element is not contained but lies in between two contained elements. Note that the container holding the values must be sorted first",
			comment = "",
			examples = { @example (
					value = "quantile_inverse([1,3,5,6,9,11,12,13,19,21,22,32,35,36,45,44,55,68,79,80,81,88,90,91,92,100], 35.5) with_precision(2)",
					equals = "0.52") })
	public static Double opQuantileInverse(final IScope scope, final IContainer data, final Double element) {

		// TODO input parameters validation

		return Descriptive.quantileInverse(toDoubleArrayList(scope, data), element);
	}

	/**
	 *
	 *
	 * @param scope
	 * @param data
	 * @param element
	 * @return
	 */
	@operator (
			value = "rank_interpolated",
			can_be_const = true,
			type = IType.FLOAT,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the linearly interpolated number of elements in a list less or equal to a given element. The rank is the number of elements <= element. Ranks are of the form {0, 1, 2,..., sortedList.size()}. If no element is <= element, then the rank is zero. If the element lies in between two contained elements, then linear interpolation is used and a non integer value is returned. Note that the container holding the values must be sorted first",
			comment = "",
			examples = { @example (
					value = "rank_interpolated([1,3,5,6,9,11,12,13,19,21,22,32,35,36,45,44,55,68,79,80,81,88,90,91,92,100], 35)",
					equals = "13.0") })
	public static Double opRankInterpolated(final IScope scope, final IContainer data, final Double element) {

		// TODO input parameters validation

		return Descriptive.rankInterpolated(toDoubleArrayList(scope, data), element);
	}

	/**
	 * Builds the regression.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the gama regression
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "build",
			can_be_const = false,
			type = IType.REGRESSION,
			category = { IOperatorCategory.STATISTICAL },
			concept = {})
	@doc (
			value = "returns the regression build from the matrix data (a row = an instance, "
					+ "the first value of each line is the y value) while using the given ordinary "
					+ "least squares method. Usage: build(data)",
			examples = { @example (
					value = "build(matrix([[1.0,2.0,3.0,4.0],[2.0,3.0,4.0,2.0]]))",
					isExecutable = false) })
	@test ("build(matrix([[1.0,2.0,3.0,4.0],[2.0,3.0,4.0,2.0],[5.0,1.0,3.0,5.0],[3.0,4.0,5.0,1.0]])).parameters collect (each with_precision 5) = [0.5,2.5,0.0,-1.5]")
	public static GamaRegression opRegression(final IScope scope, final GamaMatrix data) throws GamaRuntimeException {
		try {
			return new GamaRegression(scope, data);
		} catch (final Exception e) {
			throw GamaRuntimeException.error("The build operator is not usable for these data", scope);
		}
	}

	/**
	 * Compute adjusted R²
	 *
	 * @param scope
	 *            the scope
	 * @param regression
	 *            the regression
	 * @return the adjusted R²
	 */
	@operator (
			value = "rSquare",
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.REGRESSION })
	@doc (
			value = "Return the value of the adjusted R square for a given regression model",
			examples = { @example (
					value = "rSquare(my_regression)",
					isExecutable = false) })
	@test ("rSquare(build(matrix([[4.0,1.0,2.0,3.0],[4.0,2.0,3.0,4.0]]))) = 0.8363636363636364")
	public static Double rSquare(final IScope scope, final GamaRegression regression) {
		return regression.getRSquare();
	}

	/**
	 * Compute the residuals for the regression
	 *
	 * @param scope
	 *            the scope
	 * @param regression
	 *            the regression
	 * @return the list of residuals
	 */
	@operator (
			value = "residuals",
			type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.REGRESSION })
	@doc (
			value = "Return the list of residuals for a given regression model",
			examples = { @example (
					value = "residuals(my_regression)",
					isExecutable = false) })
	@no_test
	public static IList<Double> residuals(final IScope scope, final GamaRegression regression) {
		return regression.getResiduals();
	}

	/**
	 *
	 * @param scope
	 * @param path
	 *            path of the input csv file
	 * @param report_path
	 *            path to save the sobol_report.txt file
	 * @param nb_parameters
	 *            number of parameters in the model
	 * @return
	 */
	@operator (
			value = "sobolAnalysis",
			type = IType.STRING,
			can_be_const = true,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC },
			expected_content_type = { IType.STRING, IType.INT })
	@doc (
			value = "Return a string containing the Report of the sobol analysis for the corresponding .csv file and save this report in a txt file.")
	@no_test
	public static String sobolAnalysis(final IScope scope, final String path, final String report_path,
			final int nb_parameters) {
		final File f = new File(FileUtils.constructAbsoluteFilePath(scope, path, false));
		final File f_report = new File(FileUtils.constructAbsoluteFilePath(scope, report_path, false));
		Sobol sob = new Sobol(f, nb_parameters, scope);
		sob.evaluate();
		sob.saveResult(f_report);
		return sob.buildReportString();
	}

	/**
	 * Add by Tom Return the morris analysis
	 *
	 * @param scope
	 * @param path
	 *            : path to csv file
	 * @param nb_levels
	 *            : the number of level
	 * @param id_firstOutput
	 *            : the id of the first output
	 * @return the result of a morris analysis based on data in a CSV file
	 *
	 */
	@operator (
			value = "morrisAnalysis",
			type = IType.STRING,
			can_be_const = true,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC },
			expected_content_type = { IType.STRING, IType.INT })
	@doc (
			value = "Return a string containing the Report of the morris analysis for the corresponding CSV file")
	@no_test
	public static String morrisAnalysis(final IScope scope, final String path, final int nb_levels,
			final int id_firstOutput) {

		String new_path = scope.getExperiment().getWorkingPath() + "/" + path;
		List<Object> morris_simulation = Morris.readSimulation(new_path, id_firstOutput, scope);
		List<Map<String, Object>> MySamples = Cast.asList(scope, morris_simulation.get(0));
		// True or false ? Have to be tested
		Map<String, List<Double>> output = Cast.asMap(scope, morris_simulation, false);
		List<String> OutputsNames = output.keySet().stream().toList();
		boolean temp = true;
		StringBuilder s = new StringBuilder();

		for (String name : OutputsNames) {
			List<Map<String, Double>> morris_coefficient =
					Morris.MorrisAggregation_CSV(nb_levels, output.get(name), MySamples);
			Map<String, Double> mu = morris_coefficient.get(0);
			Map<String, Double> mu_star = morris_coefficient.get(1);
			Map<String, Double> sigma = morris_coefficient.get(2);
			s.append(Morris.buildResultTxt(name, temp, mu, mu_star, sigma));
		}

		return s.toString();

	}

	/**
	 * Stochanalyse.
	 *
	 * @param replicat
	 *            the replicat
	 * @param threshold
	 *            the threshold
	 * @param path
	 *            the path
	 * @param id_firstOutput
	 *            the id first output
	 * @param scope
	 *            the scope
	 * @return the string
	 */
	public static String Stochanalyse(final int replicat, final int threshold, final String path,
			final int id_firstOutput, final IScope scope) {
		String new_path = scope.getExperiment().getWorkingPath() + "/" + path;
		// Stochanalysis sto = new Stochanalysis();
		return Stochanalysis.StochasticityAnalysis_From_CSV(replicat, threshold, new_path, id_firstOutput, scope);

	}

	/**
	 *
	 *
	 * @param scope
	 * @param size
	 * @param sumOfSquares
	 * @return
	 */
	@operator (
			value = "rms",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the RMS (Root-Mean-Square) of a data sequence. "
					+ "The RMS of data sequence is the square-root of the mean of the squares "
					+ "of the elements in the data sequence. It is a measure of the average size of "
					+ "the elements of a data sequence.",
			comment = "",
			examples = { @example (" list<float> data_sequence <- [6.0, 7.0, 8.0, 9.0]; "),
					@example (" list<float> squares <- data_sequence collect (each*each); "), @example (
							value = " rms(length(data_sequence),sum(squares)) with_precision(4) ",
							equals = "7.5829") })
	public static Double opRms(final IScope scope, final Integer size, final Double sumOfSquares) {

		// TODO input parameters validation

		return Descriptive.rms(size, sumOfSquares);
	}

	// /**
	// *
	// *
	// * @param scope
	// * @param moment3
	// * @param standardDeviation
	// * @return
	// */
	// @operator (
	// value = { "skew", "skewness" },
	// can_be_const = true,
	// type = IType.FLOAT,
	// category = { IOperatorCategory.STATISTICAL },
	// concept = { IConcept.STATISTIC })
	// @doc (
	// value = "Returns the skew of a data sequence when the 3rd moment has already been computed.",
	// comment = "In R moment(c(1, 3, 5, 6, 9, 11, 12, 13), order=3,center=TRUE) is -10.125 and
	// sd(c(1,3,5,6,9,11,12,13)) = 4.407785"
	// + "The value of the skewness tested here is different because there are different types of estimator"
	// + "Joanes and Gill (1998) discuss three methods for estimating skewness:"
	// + "Type 1: g_1 = m_3 / m_2^(3/2). This is the typical definition used in many older textbooks."
	// + "Type 2: G_1 = g_1 * sqrt(n(n-1)) / (n-2). Used in SAS and SPSS."
	// + "Type 3: b_1 = m_3 / s^3 = g_1 ((n-1)/n)^(3/2). Used in MINITAB and BMDP."
	// + "In R skewness(c(1, 3, 5, 6, 9, 11, 12, 13),type=3) is -0.1182316",
	// examples = { @example (
	// value = "skew(-10.125,4.407785) with_precision(2)",
	// equals = "-0.12") })
	// public static Double opSkew(final IScope scope, final Double moment3, final Double standardDeviation) {
	//
	// // TODO input parameters validation
	//
	// return Descriptive.skew(moment3, standardDeviation);
	// }

	// /**
	// *
	// *
	// * @param scope
	// * @param data
	// * @param mean
	// * @param standardDeviation
	// * @return
	// */
	// @operator (
	// value = "skew",
	// can_be_const = true,
	// type = IType.FLOAT,
	// category = { IOperatorCategory.STATISTICAL },
	// concept = { IConcept.STATISTIC })
	// @doc (
	// value = "Returns the skew of a data sequence, which is moment(data,3,mean) / standardDeviation3",
	// comment = "",
	// examples = { @example (
	// value = "skew([1,3,5,6,9,11,12,13]) with_precision(2)",
	// equals = "-0.14") })
	// public static Double opSkew(final IScope scope, final IContainer data) {
	//
	// // TODO input parameters validation
	//
	// final double mean = (Double) Containers.opMean(scope, data);
	// final double standardDeviation = Stats.opStandardDeviation(scope, data);
	//
	// return Descriptive.skew(toDoubleArrayList(scope, data), mean, standardDeviation);
	// }

	/**
	 * Skewness.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = { "skewness", "skew" },
			can_be_const = false,
			// type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc (
			value = "returns skewness value computed from the operand list of values",
			special_cases = "if the length of the list is lower than 3, returns NaN",
			examples = { @example (
					value = "skewness ([1,2,3,4,5])",
					equals = "0.0") })
	public static Double opSkewness(final IScope scope, final IList data) throws GamaRuntimeException {
		final Skewness sk = new Skewness();
		final double[] values = new double[data.length(scope)];
		for (int i = 0; i < values.length; i++) { values[i] = Cast.asFloat(scope, data.get(i)); }
		return sk.evaluate(values);
	}

	/**
	 * Split.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param list
	 *            the list
	 * @return the i list
	 */
	@operator (
			value = "split",
			can_be_const = true,
			content_type = IType.LIST,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			see = { "split_in", "split_using" },
			value = "Splits a list of numbers into n=(1+3.3*log10(elements)) bins. The splitting is strict "
					+ "(i.e. elements are in the ith bin if they are strictly smaller than the ith bound)",
			examples = { @example (
					value = "split([1.0,2.0,1.0,3.0,1.0,2.0])",
					equals = "[[1.0,1.0,1.0],[2.0,2.0],[3.0]]")

			})

	public static <T extends Number> IList<IList<T>> opSplit(final IScope scope, final IList<T> list) {
		final int nb = (int) (1 + 3.3 * Math.log10(list.size()));
		return opSplitIn(scope, list, nb);
	}

	/**
	 * Split in.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param list
	 *            the list
	 * @param nb
	 *            the nb
	 * @return the i list
	 */
	@operator (
			value = "split_in",
			can_be_const = true,
			content_type = IType.LIST,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			see = { "split", "split_using" },
			value = "Splits a list of numbers into n bins defined by n-1 bounds between the minimum "
					+ "and maximum values found in the first argument. The splitting is strict "
					+ "(i.e. elements are in the ith bin if they are strictly smaller than the ith bound)",
			examples = { @example ("list<float> li <- [1.0,3.1,5.2,6.0,9.2,11.1,12.0,13.0,19.9,35.9,40.0];"), @example (
					value = "split_in(li,3)",
					equals = "[[1.0,3.1,5.2,6.0,9.2,11.1,12.0,13.0],[19.9],[35.9,40.0]]") })

	public static <T extends Number> IList<IList<T>> opSplitIn(final IScope scope, final IList<T> list, final int nb) {
		return opSplitIn(scope, list, nb, true);
	}

	/**
	 * Split in.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param list
	 *            the list
	 * @param nb
	 *            the nb
	 * @param strict
	 *            the strict
	 * @return the i list
	 */
	@operator (
			value = "split_in",
			can_be_const = true,
			content_type = IType.LIST,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			see = { "split", "split_using" },
			value = "Splits a list of numbers into n bins defined by n-1 bounds between the minimum and maximum values"
					+ " found in the first argument. The boolean argument controls whether or not the splitting is "
					+ " strict (if true, elements are in the ith bin if they are strictly smaller than the ith bound)",
			examples = { @example ("list<float> l <- [1.0,3.1,5.2,6.0,9.2,11.1,12.0,13.0,19.9,35.9,40.0];"), @example (
					value = "split_in(l,3, true)",
					equals = "[[1.0,3.1,5.2,6.0,9.2,11.1,12.0,13.0],[19.9],[35.9,40.0]]") })

	public static <T extends Number> IList<IList<T>> opSplitIn(final IScope scope, final IList<T> list, final int nb,
			final boolean strict) {
		if (nb <= 1) {
			final IList<IList<T>> result = GamaListFactory.create(Types.LIST.of(list.getGamlType().getContentType()));
			result.add(list);
			return result;
		}
		final DataSet d = toDataSet(scope, list);
		final IList<Double> stops = GamaListFactory.create(scope, Types.FLOAT, d.getStops(nb));
		return opSplitUsing(scope, list, stops);
	}

	/**
	 * Split using.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param list
	 *            the list
	 * @param stops
	 *            the stops
	 * @return the i list
	 */
	@operator (
			value = "split_using",
			can_be_const = true,
			content_type = IType.LIST,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			see = { "split", "split_in" },
			value = "Splits a list of numbers into n+1 bins using a set of n bounds passed as the second argument. "
					+ "The splitting is strict (i.e. elements are in the ith bin if they are strictly smaller "
					+ "than the ith bound), when no boolean attribute is specified.",
			masterDoc = true,
			examples = { @example ("list<float> li <- [1.0,3.1,5.2,6.0,9.2,11.1,12.0,13.0,19.9,35.9,40.0];"), @example (
					value = "split_using(li,[1.0,3.0,4.2])",
					equals = "[[],[1.0],[3.1],[5.2,6.0,9.2,11.1,12.0,13.0,19.9,35.9,40.0]]") })
	public static <T extends Number> IList<IList<T>> opSplitUsing(final IScope scope, final IList<T> list,
			final IList<? extends Comparable> stops) {
		return opSplitUsing(scope, list, stops, true);
	}

	/**
	 * Split using.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param list
	 *            the list
	 * @param stops
	 *            the stops
	 * @param strict
	 *            the strict
	 * @return the i list
	 */
	@operator (
			value = "split_using",
			can_be_const = true,
			content_type = IType.LIST,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER },
			concept = { IConcept.STATISTIC })
	@doc (
			see = { "split", "split_in" },
			value = "Splits a list of numbers into n+1 bins using a set of n bounds passed as the second argument."
					+ " The boolean argument controls whether or not the splitting is strict "
					+ "(if true, elements are in the ith bin if they are strictly smaller than the ith bound",
			examples = { @example ("list<float> l <- [1.0,3.1,5.2,6.0,9.2,11.1,12.0,13.0,19.9,35.9,40.0];"), @example (
					value = "split_using(l,[1.0,3.0,4.2], true)",
					equals = "[[],[1.0],[3.1],[5.2,6.0,9.2,11.1,12.0,13.0,19.9,35.9,40.0]]") })
	public static <T extends Number> IList<IList<T>> opSplitUsing(final IScope scope, final IList<T> list,
			final IList<? extends Comparable> stops, final boolean strict) {
		if (stops.size() == 0) {
			final IList<IList<T>> result = GamaListFactory.create(Types.LIST.of(list.getGamlType().getContentType()));
			result.add(list);
			return result;
		}
		if (!Ordering.<Comparable> natural().isStrictlyOrdered(stops)) throw GamaRuntimeException
				.error("The list " + Cast.toGaml(stops) + " should be ordered and cannot contain duplicates", scope);
		final DataSet d = toDataSet(scope, stops);
		d.addValue(Double.MAX_VALUE);
		final IType numberType = list.getGamlType().getContentType();
		final IList<IList<T>> result = GamaListFactory.createWithoutCasting(Types.LIST.of(numberType));
		for (int i = 0; i < d.dataSetSize; i++) { result.add(GamaListFactory.createWithoutCasting(numberType)); }
		for (final T o : list) {
			for (int i = 0; i < d.dataSetSize; i++) {
				if (strict ? o.doubleValue() < d.dataSet[i] : o.doubleValue() <= d.dataSet[i]) {
					result.get(i).add((T) numberType.cast(scope, o, null, false));
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Op st dev.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the double
	 */
	@operator (
			value = "standard_deviation",
			can_be_const = true,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the standard deviation on the elements of the operand. "
					+ "See <a href=\"http://en.wikipedia.org/wiki/Standard_deviation\">Standard_deviation</a> "
					+ "for more details.",
			comment = "The operator casts all the numerical element of the list into float. "
					+ "The elements that are not numerical are discarded.",
			special_cases = { "" },
			examples = { @example (
					value = "standard_deviation ([4.5, 3.5, 5.5, 7.0])",
					equals = "1.2930100540985752") },
			see = { "mean", "mean_deviation" })
	public static Double opStandardDeviation(final IScope scope, final IContainer values) {
		final DataSet d = toDataSet(scope, values);
		return d.getStandardDeviation();
	}

	/**
	 *
	 *
	 * @param scope
	 * @param x
	 * @param df
	 * @return
	 */
	@operator (
			value = "student_area",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the area to the left of x in the Student T distribution with the given degrees of freedom.",
			comment = "",
			examples = { @example (
					value = "student_area(1.64,3) with_precision(2)",
					equals = "0.9") })

	public static Double opStudentArea(final IScope scope, final Double x, final Integer df) {

		// Returns the area to the left of x in the Student T distribution
		// with the given degrees of freedom.
		try {
			return Probability.studentT(df, x);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
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
	@operator (
			value = "student_t_inverse",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the value, t, for which the area under the Student-t probability density function "
					+ "(integrated from minus infinity to t) is equal to x.",
			comment = "",

			examples = { @example (
					value = "student_t_inverse(0.9,3) with_precision(2)",
					equals = "1.64") })

	public static Double opStudentTInverse(final IScope scope, final Double x, final Integer df) {

		// Returns the value, t, for which the area under the Student-t
		// probability density function (integrated from minus infinity to
		// t)
		// is equal to x.
		final double a = 2.0 * (1.0 - x);
		try {
			return Probability.studentTInverse(a, df);
		} catch (final IllegalArgumentException | ArithmeticException ex) {
			throw GamaRuntimeException.error("colt .studentTInverse reports: " + ex, scope);
		}
	}

	/**
	 * T test P value.
	 *
	 * @param scope
	 *            the scope
	 * @param seq1
	 *            the seq 1
	 * @param seq2
	 *            the seq 2
	 * @return the double
	 */
	@operator (
			value = "t_test",
			can_be_const = false,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the observed significance level, or p-value, associated with a two-sample, "
					+ "two-tailed t-test comparing the means of the two input lists."
					+ "The number returned is the smallest significance level at which one can reject the null hypothesis",
			examples = { @example (
					value = "t_test([10.0,5.0,1.0, 3.0],[1.0,10.0,5.0,1.0])",
					equals = "0.01") })
	public static Double opTTest(final IScope scope, final IList seq1, final IList seq2) {
		TTest t = new TTest();

		double[] s1 = new double[seq1.length(scope)];
		for (int i = 0; i < seq1.length(scope); i++) { s1[i] = Cast.asFloat(scope, seq1.get(i)); }

		double[] s2 = new double[seq2.length(scope)];
		for (int i = 0; i < seq2.length(scope); i++) { s2[i] = Cast.asFloat(scope, seq2.get(i)); }

		return t.tTest(s1, s2);
	}

	/**
	 * Op variance.
	 *
	 * @param scope
	 *            the scope
	 * @param standardDeviation
	 *            the standard deviation
	 * @return the double
	 */
	@operator (
			value = "variance",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the variance from a standard deviation.",
			comment = "",
			examples = { @example (
					value = "int(variance([1,3,5,6,9,11,12,13]))",
					equals = "17",
					returnType = "int") })

	// @test ("int(variance([1,3,5,6,9,11,12,13])) = 17")
	public static Double opVariance(final IScope scope, final Double standardDeviation) {

		// TODO input parameters validation

		return Descriptive.variance(standardDeviation);
	}

	/**
	 * Op variance.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the double
	 */
	@operator (
			value = "variance",
			can_be_const = true,
			expected_content_type = { IType.INT, IType.FLOAT },
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "the variance of the elements of the operand. "
					+ "See <a href=\"http://en.wikipedia.org/wiki/Variance\">Variance</a> for more details.",
			comment = "The operator casts all the numerical element of the list into float."
					+ " The elements that are not numerical are discarded. ",
			examples = { @example (
					value = "variance ([4.5, 3.5, 5.5, 7.0])",
					equals = "1.671875") },
			see = { "mean", "median" })
	public static Double opVariance(final IScope scope, final IContainer values) {
		final DataSet d = toDataSet(scope, values);
		return d.getVariance();
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
	@operator (
			value = "variance",
			can_be_const = true,
			type = IType.FLOAT,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc (
			value = "Returns the variance of a data sequence. That is (sumOfSquares - mean*sum) / size with mean = sum/size.",
			comment = "In the example we consider variance of [1,3,5,7]. The size is 4, the sum is 1+3+5+7=16 and the sum of squares is 84."
					+ "The variance is (84- 16^2/4)/4. CQFD.",
			examples = { @example (
					value = "int(variance(4,16,84))",
					equals = "5",
					returnType = "int") })
	public static Double opVariance(final IScope scope, final Integer size, final Double sum,
			final Double numOfSquares) {

		// TODO input parameters validation

		return Descriptive.variance(size, sum, numOfSquares);
	}

	/**
	 * Variance of.
	 *
	 * @param scope
	 *            the scope
	 * @param container
	 *            the container
	 * @param filter
	 *            the filter
	 * @return the object
	 */
	@operator (
			value = { "variance_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the variance of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			see = { "min_of", "max_of", "sum_of", "product_of" },
			examples = { @example (
					value = "[1,2,3,4,5,6] variance_of each with_precision 2",
					equals = "2.92",
					returnType = "float") })

	@test ("[1,2,3,4,5,6] variance_of each with_precision 2 = 2.92")
	public static Object opVarianceOf(final IScope scope, final IContainer container, final IExpression filter) {
		return opVariance(scope, collect(scope, container, filter));
	}

	/**
	 * From.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the data set
	 */
	private static DataSet toDataSet(final IScope scope, final IContainer values) {
		final DataSet d = new DataSet(values.length(scope));
		for (final Object o : values.iterable(scope)) {
			if (o instanceof Number) { d.addValue(((Number) o).doubleValue()); }
		}
		return d;
	}

	/**
	 * From.
	 *
	 * @param scope
	 *            the scope
	 * @param values
	 *            the values
	 * @return the double array list
	 */
	static DoubleArrayList toDoubleArrayList(final IScope scope, final IContainer values) {
		final DoubleArrayList d = new DoubleArrayList(values.length(scope));
		for (final Object o : values.iterable(scope)) {
			if (o instanceof Number) { d.add(((Number) o).doubleValue()); }
		}

		return d;
	}

	/**
	 * Mean of.
	 *
	 * @param scope
	 *            the scope
	 * @param container
	 *            the container
	 * @param filter
	 *            the filter
	 * @return the object
	 */
	@operator (
			value = { "mean_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2 + ITypeProvider.FLOAT_IN_CASE_OF_INT,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the mean of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage (
					value = "if the left-operand is a map, the keyword each will contain each value",
					examples = { @example (
							value = "[1::2, 3::4, 5::6] mean_of (each)",
							equals = "4") }) },
			examples = { @example (
					value = "[1,2] mean_of (each * 10 )",
					equals = "15") },
			see = { "min_of", "max_of", "sum_of", "product_of" })
	@test ("[1,2] mean_of (each * 10 ) = 15")
	@test ("[1,2] mean_of (each * 10 ) = 15")
	@test ("[1,2] mean_of (each * 10 ) = 15")
	public static Object opMeanOf(final IScope scope, final IContainer container, final IExpression filter) {
		return Containers.opMean(scope, collect(scope, container, filter));
	}

	/**
	 * Min of.
	 *
	 * @param scope
	 *            the scope
	 * @param c
	 *            the c
	 * @param filter
	 *            the filter
	 * @return the object
	 */
	@operator (
			value = { "min_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the minimum value of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage ("if the left-hand operand is nil or empty, min_of throws an error"), @usage (
					value = "if the left-operand is a map, the keyword each will contain each value",
					examples = { @example (
							value = "[1::2, 3::4, 5::6] min_of (each + 3)",
							equals = "5") }) },
			examples = {
					// @example (value = "graph([]) min_of([])", raises = "error", isTestOnly = true),
					@example (
							value = "[1,2,4,3,5,7,6,8] min_of (each * 100 )",
							equals = "100"),
					@example (
							value = "graph g2 <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);"),
					@example (
							value = "g2 min_of (length(g2 out_edges_of each) )",
							equals = "0"),
					@example (
							value = "(list(node) min_of (round(node(each).location.x))",
							equals = "4",
							isExecutable = false) },
			see = { "max_of" })
	@test ("[1,2,4,3,5,7,6,8] min_of (each * 100 ) = 100")
	@validator (ComparableValidator.class)
	public static Object opMinOf(final IScope scope, final IContainer c, final IExpression filter) {
		return Containers.stream(scope, c).map(Containers.with(scope, filter)).minBy(Function.identity()).orElse(null);
	}

	/**
	 * Max of.
	 *
	 * @param scope
	 *            the scope
	 * @param c
	 *            the c
	 * @param filter
	 *            the filter
	 * @return the object
	 */
	@operator (
			value = { "max_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the maximum value of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage ("As of GAMA 1.6, if the left-hand operand is nil or empty, max_of throws an error"),
					@usage (
							value = "if the left-operand is a map, the keyword each will contain each value",
							examples = { @example (
									value = "[1::2, 3::4, 5::6] max_of (each + 3)",
									equals = "9") }) },
			examples = {
					// @example ( value = "graph([]) max_of([])", raises = "error", isTestOnly = true),
					@example (
							value = "[1,2,4,3,5,7,6,8] max_of (each * 100 )",
							equals = "800"),
					@example (
							value = "graph g2 <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);"),
					@example (
							value = "g2.vertices max_of (g2 degree_of( each ))",
							equals = "2"),
					@example (
							value = "(list(node) max_of (round(node(each).location.x))",
							equals = "96",
							isExecutable = false) },
			see = { "min_of" })
	@test ("[1,2,4,3,5,7,6,8] max_of (each * 100 ) = 800")
	@validator (ComparableValidator.class)
	public static Object opMaxOf(final IScope scope, final IContainer c, final IExpression filter) {
		return Containers.stream(scope, c).map(Containers.with(scope, filter)).maxBy(Function.identity()).orElse(null);
	}

}
