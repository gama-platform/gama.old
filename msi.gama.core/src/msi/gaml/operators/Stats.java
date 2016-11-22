/*********************************************************************************************
 *
 * 'Stats.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.DBSCANClusterer;
import org.apache.commons.math3.stat.clustering.EuclideanDoublePoint;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaRegression;
import msi.gama.util.IContainer;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import rcaller.RCaller;
import rcaller.RCode;
import rcaller.exception.ExecutionException;
import rcaller.exception.ParseException;

/**
 * Written by drogoul Modified on 15 janv. 2011
 *
 * @todo Description
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Stats {

	public static class Instance extends EuclideanDoublePoint {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int id;

		public Instance(final int id, final double[] point) {
			super(point);
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setId(final int id) {
			this.id = id;
		}

	}

	private static class DataSet {

		private static final int DEFAULT_CAPACITY = 50;
		private static final double GROWTH_RATE = 1.5d;

		private double[] dataSet;
		private int dataSetSize = 0;

		private double total = 0;
		private double product = 1;
		private double reciprocalSum = 0;
		private double minimum = Double.MAX_VALUE;
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
		 *            The initial capacity for the data set (this number of
		 *            values will be able to be added without needing to resize
		 *            the internal data storage).
		 */
		public DataSet(final int capacity) {
			this.dataSet = new double[capacity];
			this.dataSetSize = 0;
		}

		//
		// /**
		// * Creates a data set and populates it with the specified values.
		// * @param dataSet The values to add to this data set.
		// */
		// public DataSet(final double[] dataSet) {
		// this.dataSet = dataSet.clone();
		// this.dataSetSize = dataSet.length;
		// for ( double value : this.dataSet ) {
		// updateStatsWithNewValue(value);
		// }
		// }

		/**
		 * Adds a single value to the data set and updates any statistics that
		 * are calculated cumulatively.
		 * 
		 * @param value
		 *            The value to add.
		 */
		public void addValue(final double value) {
			if (dataSetSize == dataSet.length) {
				// Increase the capacity of the array.
				final int newLength = (int) (GROWTH_RATE * dataSetSize);
				final double[] newDataSet = new double[newLength];
				java.lang.System.arraycopy(dataSet, 0, newDataSet, 0, dataSetSize);
				dataSet = newDataSet;
			}
			dataSet[dataSetSize] = value;
			updateStatsWithNewValue(value);
			++dataSetSize;
		}

		private void updateStatsWithNewValue(final double value) {
			total += value;
			product *= value;
			reciprocalSum += 1 / value;
			minimum = FastMath.min(minimum, value);
			maximum = FastMath.max(maximum, value);
		}

		/**
		 * Returns the number of values in this data set.
		 * 
		 * @return The size of the data set.
		 */
		public final int getSize() {
			return dataSetSize;
		}

		// /**
		// * @return The smallest value in the data set.
		// * @throws EmptyDataSetException If the data set is empty.
		// * @since 1.0.1
		// */
		// public final double getMinimum() {
		// return minimum;
		// }
		//
		// /**
		// * @return The biggest value in the data set.
		// * @throws EmptyDataSetException If the data set is empty.
		// * @since 1.0.1
		// */
		// public final double getMaximum() {
		// return maximum;
		// }

		/**
		 * Determines the median value of the data set.
		 * 
		 * @return If the number of elements is odd, returns the middle element.
		 *         If the number of elements is even, returns the midpoint of
		 *         the two middle elements.
		 * @since 1.0.1
		 */
		public final double getMedian() {

			// Sort the data (take a copy to do this).
			final double[] dataCopy = new double[getSize()];
			java.lang.System.arraycopy(dataSet, 0, dataCopy, 0, dataCopy.length);
			Arrays.sort(dataCopy);
			final int midPoint = dataCopy.length / 2;
			if (dataCopy.length % 2 != 0) {
				return dataCopy[midPoint];
			}

			return dataCopy[midPoint - 1] + (dataCopy[midPoint] - dataCopy[midPoint - 1]) / 2;

		}

		/**
		 * @return The sum of all values.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getAggregate() {
			return total;
		}

		/**
		 * @return The product of all values.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getProduct() {
			return product;
		}

		/**
		 * The arithemthic mean of an n-element set is the sum of all the
		 * elements divided by n. The arithmetic mean is often referred to
		 * simply as the "mean" or "average" of a data set.
		 * 
		 * @see #getGeometricMean()
		 * @return The arithmetic mean of all elements in the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getArithmeticMean() {
			return total / dataSetSize;
		}

		/**
		 * The geometric mean of an n-element set is the nth-root of the product
		 * of all the elements. The geometric mean is used for finding the
		 * average factor (e.g. an average interest rate).
		 * 
		 * @see #getArithmeticMean()
		 * @see #getHarmonicMean()
		 * @return The geometric mean of all elements in the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getGeometricMean() {
			return FastMath.pow(product, 1.0d / dataSetSize);
		}

		/**
		 * The harmonic mean of an n-element set is {@literal n} divided by the
		 * sum of the reciprocals of the values (where the reciprocal of a value
		 * {@literal x} is 1/x). The harmonic mean is used to calculate an
		 * average rate (e.g. an average speed).
		 * 
		 * @see #getArithmeticMean()
		 * @see #getGeometricMean()
		 * @since 1.1
		 * @return The harmonic mean of all the elements in the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getHarmonicMean() {
			return dataSetSize / reciprocalSum;
		}

		/**
		 * Calculates the mean absolute deviation of the data set. This is the
		 * average (absolute) amount that a single value deviates from the
		 * arithmetic mean.
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
			for (int i = 0; i < dataSetSize; i++) {
				diffs += FastMath.abs(mean - dataSet[i]);
			}
			return diffs / dataSetSize;
		}

		/**
		 * Calculates the variance (a measure of statistical dispersion) of the
		 * data set. There are different measures of variance depending on
		 * whether the data set is itself a finite population or is a sample
		 * from some larger population. For large data sets the difference is
		 * negligible. This method calculates the population variance.
		 * 
		 * @see #getSampleVariance()
		 * @see #getStandardDeviation()
		 * @see #getMeanDeviation()
		 * @return The population variance of the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getVariance() {
			return sumSquaredDiffs() / getSize();
		}

		/**
		 * Helper method for variance calculations.
		 * 
		 * @return The sum of the squares of the differences between each value
		 *         and the arithmetic mean.
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
		 * The standard deviation is the square root of the variance. This
		 * method calculates the population standard deviation as opposed to the
		 * sample standard deviation. For large data sets the difference is
		 * negligible.
		 * 
		 * @see #getSampleStandardDeviation()
		 * @see #getVariance()
		 * @see #getMeanDeviation()
		 * @return The standard deviation of the population.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		public final double getStandardDeviation() {
			return FastMath.sqrt(getVariance());
		}

		/**
		 * Calculates the variance (a measure of statistical dispersion) of the
		 * data set. There are different measures of variance depending on
		 * whether the data set is itself a finite population or is a sample
		 * from some larger population. For large data sets the difference is
		 * negligible. This method calculates the sample variance.
		 * 
		 * @see #getVariance()
		 * @see #getSampleStandardDeviation()
		 * @see #getMeanDeviation()
		 * @return The sample variance of the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		// public final double getSampleVariance() {
		// return sumSquaredDiffs() / (getSize() - 1);
		// }

		/**
		 * The sample standard deviation is the square root of the sample
		 * variance. For large data sets the difference between sample standard
		 * deviation and population standard deviation is negligible.
		 * 
		 * @see #getStandardDeviation()
		 * @see #getSampleVariance()
		 * @see #getMeanDeviation()
		 * @return The sample standard deviation of the data set.
		 * @throws EmptyDataSetException
		 *             If the data set is empty.
		 */
		// public final double getSampleStandardDeviation() {
		// return FastMath.sqrt(getSampleVariance());
		// }
	}

	private static DataSet from(final IScope scope, final IContainer values) {
		final DataSet d = new DataSet(values.length(scope));
		for (final Object o : values.iterable(scope)) {
			if (o instanceof Number) {
				d.addValue(((Number) o).doubleValue());
			}
		}
		return d;
	}

	@operator(value = "max", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
			IType.INT, IType.FLOAT, IType.POINT }, category = { IOperatorCategory.STATISTICAL,
					IOperatorCategory.CONTAINER }, concept = { IConcept.STATISTIC })
	@doc(value = "the maximum element found in the operand", masterDoc = true, comment = "the max operator behavior depends on the nature of the operand", usages = {
			@usage(value = "if it is a list of int of float, max returns the maximum of all the elements", examples = {
					@example(value = "max ([100, 23.2, 34.5])", equals = "100.0") }),
			@usage(value = "if it is a list of points: max returns the maximum of all points as a point (i.e. the point with the greatest coordinate on the x-axis, in case of equality the point with the greatest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )", examples = {
					@example(value = "max([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}])", equals = "{9.0,1.0}") }),
			@usage("if it is a population of a list of other type: max transforms all elements into integer and returns the maximum of them"),
			@usage("if it is a map, max returns the maximum among the list of all elements value"),
			@usage("if it is a file, max returns the maximum of the content of the file (that is also a container)"),
			@usage("if it is a graph, max returns the maximum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)"),
			@usage("if it is a matrix of int, float or object, max returns the maximum of all the numerical elements (thus all elements for integer and float matrices)"),
			@usage("if it is a matrix of geometry, max returns the maximum of the list of the geometries"),
			@usage("if it is a matrix of another type, max returns the maximum of the elements transformed into float") }, see = {
					"min" })
	public static Object max(final IScope scope, final IContainer l) {
		Number maxNum = null;
		ILocation maxPoint = null;
		for (final Object o : l.iterable(scope)) {
			if (o instanceof ILocation && maxNum == null) {
				if (maxPoint == null || ((ILocation) o).compareTo(maxPoint) > 0) {
					maxPoint = (ILocation) o;
				}
			} else if (o instanceof Number && maxPoint == null
					&& (maxNum == null || ((Number) o).doubleValue() > maxNum.doubleValue())) {
				maxNum = (Number) o;
			} else {
				final Double d = Cast.asFloat(scope, o);
				if (maxNum == null || d > maxNum.doubleValue()) {
					maxNum = d;
				}
			}
		}
		return maxNum == null ? maxPoint : maxNum;
	}

	@operator(value = "min", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
			IType.INT, IType.FLOAT, IType.POINT }, category = { IOperatorCategory.STATISTICAL,
					IOperatorCategory.CONTAINER }, concept = { IConcept.STATISTIC })
	@doc(value = "the minimum element found in the operand.", masterDoc = true, comment = "the min operator behavior depends on the nature of the operand", usages = {
			@usage(value = "if it is a list of int or float: min returns the minimum of all the elements", examples = {
					@example(value = "min ([100, 23.2, 34.5])", equals = "23.2") }),
			@usage(value = "if it is a list of points: min returns the minimum of all points as a point (i.e. the point with the smallest coordinate on the x-axis, in case of equality the point with the smallest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )"),
			@usage(value = "if it is a population of a list of other types: min transforms all elements into integer and returns the minimum of them"),
			@usage(value = "if it is a map, min returns the minimum among the list of all elements value"),
			@usage(value = "if it is a file, min returns the minimum of the content of the file (that is also a container)"),
			@usage(value = "if it is a graph, min returns the minimum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)"),
			@usage(value = "if it is a matrix of int, float or object, min returns the minimum of all the numerical elements (thus all elements for integer and float matrices)"),
			@usage(value = "if it is a matrix of geometry, min returns the minimum of the list of the geometries"),
			@usage(value = "if it is a matrix of another type, min returns the minimum of the elements transformed into float") }, see = {
					"max" })
	public static Object min(final IScope scope, final IContainer l) {
		Number minNum = null;
		ILocation minPoint = null;
		for (final Object o : l.iterable(scope)) {
			if (o instanceof ILocation && minNum == null) {
				if (minPoint == null || ((ILocation) o).compareTo(minPoint) < 0) {
					minPoint = (ILocation) o;
				}
			} else if (o instanceof Number && minPoint == null
					&& (minNum == null || ((Number) o).doubleValue() < minNum.doubleValue())) {
				minNum = (Number) o;
			} else {
				final Double d = Cast.asFloat(scope, o);
				if (minNum == null || d < minNum.doubleValue()) {
					minNum = d;
				}
			}
		}
		return minNum == null ? minPoint : minNum;
	}

	@SuppressWarnings("null")
	@operator(value = { "mul",
			"product" }, can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
					IType.INT, IType.FLOAT, IType.POINT }, category = { IOperatorCategory.STATISTICAL,
							IOperatorCategory.CONTAINER }, concept = { IConcept.STATISTIC })
	@doc(value = "the product of all the elements of the operand", masterDoc = true, comment = "the mul operator behavior depends on the nature of the operand", usages = {
			@usage(value = "if it is a list of int or float: mul returns the product of all the elements", examples = {
					@example(value = "mul ([100, 23.2, 34.5])", equals = "80040.0") }),
			@usage(value = "if it is a list of points: mul returns the product of all points as a point (each coordinate is the product of the corresponding coordinate of each element)"),
			@usage(value = "if it is a list of other types: mul transforms all elements into integer and multiplies them"),
			@usage(value = "if it is a map, mul returns the product of the value of all elements"),
			@usage(value = "if it is a file, mul returns the product of the content of the file (that is also a container)"),
			@usage(value = "if it is a graph, mul returns the product of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)"),
			@usage(value = "if it is a matrix of int, float or object, mul returns the product of all the numerical elements (thus all elements for integer and float matrices)"),
			@usage(value = "if it is a matrix of geometry, mul returns the product of the list of the geometries"),
			@usage(value = "if it is a matrix of other types: mul transforms all elements into float and multiplies them") }, see = {
					"sum" })
	public static Object product(final IScope scope, final IContainer l) {
		final DataSet x = new DataSet();
		DataSet y = null, z = null;
		for (final Object o : l.iterable(scope)) {
			if (o instanceof ILocation) {
				if (y == null) {
					y = new DataSet();
					z = new DataSet();
				}
				final ILocation p = (ILocation) o;
				x.addValue(p.getX());
				y.addValue(p.getY());
				z.addValue(p.getZ());
			} else {
				x.addValue(Cast.asFloat(scope, o));
			}
		}
		if (x.getSize() == 0) {
			if (y == null) {
				return 0.0;
			}
			return new GamaPoint(0, 0, 0);
		}
		if (y == null) {
			return x.getProduct();
		}
		return new GamaPoint(x.getProduct(), y.getProduct(), z.getProduct());
	}

	// TODO Penser a faire ces calculs sur les points, egalement (et les entiers
	// ?)

	@operator(value = "median", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
			IType.INT, IType.FLOAT, IType.POINT, IType.COLOR }, category = { IOperatorCategory.STATISTICAL,
					IOperatorCategory.CONTAINER,
					IOperatorCategory.COLOR }, concept = { IConcept.STATISTIC, IConcept.COLOR })
	@doc(value = "the median of all the elements of the operand.", special_cases = {
			"if the container contains points, the result will be a point. If the container contains rgb values, the result will be a rgb color" }, examples = {
					@example(value = "median ([4.5, 3.5, 5.5, 3.4, 7.0])", equals = "5.0") }, see = { "mean" })
	public static Object opMedian(final IScope scope, final IContainer values) {

		final IType contentType = values.getType().getContentType();
		if (values.length(scope) == 0) {
			return contentType.cast(scope, 0d, null, false);
		}
		switch (contentType.id()) {
		case IType.INT:
		case IType.FLOAT:
			final DataSet d2 = new DataSet();
			for (final Object o : values.iterable(scope)) {
				d2.addValue(Cast.asFloat(scope, o));
			}
			final Number result = d2.getSize() == 0 ? 0.0 : d2.getMedian();
			return contentType.cast(scope, result, null, false);
		case IType.POINT:
			final DataSet x = new DataSet();
			final DataSet y = new DataSet();
			final DataSet z = new DataSet();
			for (final Object o : values.iterable(scope)) {
				final ILocation p = (ILocation) o;
				x.addValue(p.getX());
				y.addValue(p.getY());
				z.addValue(p.getZ());
			}
			if (x.getSize() == 0) {
				return new GamaPoint(0, 0, 0);
			}
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
			if (r.getSize() == 0) {
				return new GamaColor(0, 0, 0, 0);
			}
			return new GamaColor(r.getMedian(), g.getMedian(), b.getMedian(), 0);
		default:
			final DataSet d = new DataSet();
			for (final Object o : values.iterable(scope)) {
				d.addValue(Cast.asFloat(scope, o));
			}
			final Number n = d.getSize() == 0 ? 0.0 : d.getMedian();
			return Cast.asFloat(scope, n);

		}
	}

	@operator(value = "standard_deviation", can_be_const = true, expected_content_type = { IType.INT,
			IType.FLOAT }, category = { IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "the standard deviation on the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Standard_deviation\">Standard_deviation</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", special_cases = {
			"" }, examples = {
					@example(value = "standard_deviation ([4.5, 3.5, 5.5, 7.0])", equals = "1.2930100540985752") }, see = {
							"mean", "mean_deviation" })
	public static Double opStDev(final IScope scope, final IContainer values) {
		final DataSet d = from(scope, values);
		return d.getStandardDeviation();
	}

	@operator(value = "geometric_mean", can_be_const = true, expected_content_type = { IType.INT,
			IType.FLOAT }, category = { IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "the geometric mean of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Geometric_mean\">Geometric_mean</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", special_cases = {
			"" }, examples = {
					@example(value = "geometric_mean ([4.5, 3.5, 5.5, 7.0])", equals = "4.962326343467649") }, see = {
							"mean", "median", "harmonic_mean" })
	public static Double opGeomMean(final IScope scope, final IContainer values) {
		final DataSet d = from(scope, values);
		return d.getGeometricMean();
	}

	@operator(value = "harmonic_mean", can_be_const = true, expected_content_type = { IType.INT,
			IType.FLOAT }, category = { IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "the harmonic mean of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Harmonic_mean\">Harmonic_mean</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", special_cases = {
			"" }, examples = {
					@example(value = "harmonic_mean ([4.5, 3.5, 5.5, 7.0])", equals = "4.804159445407279") }, see = {
							"mean", "median", "geometric_mean" })
	public static Double opHarmonicMean(final IScope scope, final IContainer values) {
		final DataSet d = from(scope, values);
		return d.getHarmonicMean();
	}

	@operator(value = "variance", can_be_const = true, expected_content_type = { IType.INT, IType.FLOAT }, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "the variance of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Variance\">Variance</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded. ", examples = {
			@example(value = "variance ([4.5, 3.5, 5.5, 7.0])", equals = "1.671875") }, see = { "mean", "median" })
	public static Double opVariance(final IScope scope, final IContainer values) {
		final DataSet d = from(scope, values);
		return d.getVariance();
	}

	@operator(value = "mean_deviation", can_be_const = true, expected_content_type = { IType.INT,
			IType.FLOAT }, category = { IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "the deviation from the mean of all the elements of the operand. See <A href= \"http://en.wikipedia.org/wiki/Absolute_deviation\" >Mean_deviation</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", examples = {
			@example(value = "mean_deviation ([4.5, 3.5, 5.5, 7.0])", equals = "1.125") }, see = { "mean",
					"standard_deviation" })
	public static Double opMeanDeviation(final IScope scope, final IContainer values) {
		final DataSet d = from(scope, values);
		return d.getMeanDeviation();
	}

	@operator(value = {
			"frequency_of" }, can_be_const = true, iterator = true, index_type = ITypeProvider.SECOND_CONTENT_TYPE, content_type = IType.INT, category = {
					IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "Returns a map with keys equal to the application of the right-hand argument (like collect) and values equal to the frequency of this key (i.e. how many times it has been obtained)", comment = "", examples = {
			@example(value = "[ag1, ag2, ag3, ag4] frequency_of each.size", equals = "the different sizes as keys and the number of agents of this size as values", isExecutable = false) }, see = "as_map")
	public static GamaMap frequencyOf(final IScope scope, final IContainer original, final IExpression filter)
			throws GamaRuntimeException {
		if (original == null) {
			return GamaMapFactory.create(Types.NO_TYPE, Types.INT);
		}
		final GamaMap<Object, Integer> result = GamaMapFactory.create(original.getType().getContentType(), Types.INT);
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

	@operator(value = { "corR", "R_correlation" }, can_be_const = false, type = IType.FLOAT, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "returns the Pearson correlation coefficient of two given vectors (right-hand operands) in given variable  (left-hand operand).", special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0", examples = {
			@example("list X <- [1, 2, 3];"), @example("list Y <- [1, 2, 4];"),
			@example(value = "corR(X, Y)", equals = "0.981980506061966") })
	public static Object getCorrelationR(final IScope scope, final IContainer l1, final IContainer l2)
			throws GamaRuntimeException, ParseException, ExecutionException {
		if (l1.length(scope) == 0 || l2.length(scope) == 0) {
			return Double.valueOf(0d);
		}

		if (l1.length(scope) != l2.length(scope)) {
			return Double.valueOf(0d);
		}

		final RCaller caller = new RCaller();
		final RCode code = new RCode();

		final String RPath = GamaPreferences.LIB_R.value(scope).getPath(scope);
		caller.setRscriptExecutable(RPath);
		// caller.setRscriptExecutable("\"" + RPath + "\"");
		// if ( java.lang.System.getProperty("os.name").startsWith("Mac") ) {
		// caller.setRscriptExecutable(RPath);
		// }

		final double[] vectorX = new double[l1.length(scope)];
		final double[] vectorY = new double[l2.length(scope)];

		int i = 0;
		for (final Object o : l1.iterable(scope)) {
			vectorX[i++] = Double.parseDouble(o.toString());
		}

		i = 0;
		for (final Object o : l2.iterable(scope)) {
			vectorY[i++] = Double.parseDouble(o.toString());
		}

		code.addDoubleArray("vectorX", vectorX);
		code.addDoubleArray("vectorY", vectorY);

		code.addRCode("corCoef<-cor(vectorX, vectorY, method='pearson')");
		caller.setRCode(code);
		caller.runAndReturnResult("corCoef");

		double[] results;
		try {
			results = caller.getParser().getAsDoubleArray("corCoef");
		} catch (final Exception ex) {
			return 0.0;
		}

		return results[0];
	}

	@operator(value = { "meanR", "R_mean" }, can_be_const = false, type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC })
	@doc(value = "returns the mean value of given vector (right-hand operand) in given variable  (left-hand operand).", examples = {
			@example("list<int> X <- [2, 3, 1];"),
			@example(value = "meanR(X)", equals = "2", returnType = IKeyword.INT) })
	public static Object getMeanR(final IScope scope, final IContainer l)
			throws GamaRuntimeException, ParseException, ExecutionException {
		if (l.length(scope) == 0) {
			return Double.valueOf(0d);
		}

		double[] results;
		final RCaller caller = new RCaller();
		final RCode code = new RCode();

		final String RPath = GamaPreferences.LIB_R.value(scope).getPath(scope);
		caller.setRscriptExecutable(RPath);
		// caller.setRscriptExecutable("\"" + RPath + "\"");
		// if ( java.lang.System.getProperty("os.name").startsWith("Mac") ) {
		// caller.setRscriptExecutable(RPath);
		// }

		final double[] data = new double[l.length(scope)];
		int i = 0;
		for (final Object o : l.iterable(scope)) {
			data[i++] = Double.parseDouble(o.toString());
		}

		code.addDoubleArray("data", data);
		code.addRCode("mean<-mean(data)");
		caller.setRCode(code);
		caller.runAndReturnResult("mean");
		results = caller.getParser().getAsDoubleArray("mean");
		return results[0];
	}

	@operator(value = "dbscan", can_be_const = false, type = IType.LIST, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc(value = "returns the list of clusters (list of instance indices) computed with the dbscan (density-based spatial clustering of applications with noise) algorithm from the first operand data according to the maximum radius of the neighborhood to be considered (eps) and the minimum number of points needed for a cluster (minPts). Usage: dbscan(data,eps,minPoints)", special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0", examples = {
			@example("dbscan ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],10,2)") })
	public static GamaList<GamaList> DBscanApache(final IScope scope, final GamaList data, final Double eps,
			final Integer minPts) throws GamaRuntimeException {

		final DBSCANClusterer<EuclideanDoublePoint> dbscan = new DBSCANClusterer(eps, minPts);
		final List<EuclideanDoublePoint> instances = new ArrayList<EuclideanDoublePoint>();
		for (int i = 0; i < data.size(); i++) {
			final GamaList d = (GamaList) data.get(i);
			final double point[] = new double[d.size()];
			for (int j = 0; j < d.size(); j++) {
				point[j] = Cast.asFloat(scope, d.get(j));
			}
			instances.add(new Instance(i, point));
		}
		final List<Cluster<EuclideanDoublePoint>> clusters = dbscan.cluster(instances);
		final GamaList results = (GamaList) GamaListFactory.create();
		for (final Cluster<EuclideanDoublePoint> cl : clusters) {
			final GamaList clG = (GamaList) GamaListFactory.create();
			for (final EuclideanDoublePoint pt : cl.getPoints()) {
				clG.addValue(scope, ((Instance) pt).getId());
			}
			results.addValue(scope, clG);
		}
		return results;
	}

	@operator(value = "kmeans", can_be_const = false, type = IType.LIST, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc(value = "returns the list of clusters (list of instance indices) computed with the kmeans++ algorithm from the first operand data according to the number of clusters to split the data into (k) and the maximum number of iterations to run the algorithm for (If negative, no maximum will be used) (maxIt). Usage: kmeans(data,k,maxit)", special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0", examples = {
			@example(value = "kmeans ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],2,10)", isExecutable = false) })
	public static GamaList<GamaList> KMeansPlusplusApache(final IScope scope, final GamaList data, final Integer k,
			final Integer maxIt) throws GamaRuntimeException {
		final Random rand = new Random(scope.getRandom().getSeed().longValue());
		final KMeansPlusPlusClusterer<EuclideanDoublePoint> kmeans = new KMeansPlusPlusClusterer<EuclideanDoublePoint>(
				rand);

		final List<EuclideanDoublePoint> instances = new ArrayList<EuclideanDoublePoint>();
		for (int i = 0; i < data.size(); i++) {
			final GamaList d = (GamaList) data.get(i);
			final double point[] = new double[d.size()];
			for (int j = 0; j < d.size(); j++) {
				point[j] = Cast.asFloat(scope, d.get(j));
			}
			instances.add(new Instance(i, point));
		}
		final List<Cluster<EuclideanDoublePoint>> clusters = kmeans.cluster(instances, k, maxIt);
		final GamaList results = (GamaList) GamaListFactory.create();
		for (final Cluster<EuclideanDoublePoint> cl : clusters) {
			final GamaList clG = (GamaList) GamaListFactory.create();
			for (final EuclideanDoublePoint pt : cl.getPoints()) {
				clG.addValue(scope, ((Instance) pt).getId());
			}
			results.addValue(scope, clG);
		}
		return results;
	}

	@operator(value = "gamma_rnd", can_be_const = false, type = IType.LIST, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc(value = "returns a random value from a gamma distribution with specified values of the shape and scale parameters", examples = {
			@example("gamma_rnd(10.0,5.0)") })
	public static Double OpGammaDist(final IScope scope, final Double shape, final Double scale ) throws GamaRuntimeException {
		final GammaDistribution dist = new GammaDistribution(scope.getRandom().getGenerator(),shape,scale,GammaDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
		return dist.sample();
	}
	
	@operator(value = "skewness", can_be_const = false, type = IType.LIST, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc(value = "returns skewness value computed from the operand list of values", special_cases = "if the length of the list is lower than 3, returns NaN", examples = {
			@example("skewness ([1,2,3,4,5])") })
	public static Double skewness(final IScope scope, final GamaList data) throws GamaRuntimeException {
		final Skewness sk = new Skewness();
		final double[] values = new double[data.length(scope)];
		for (int i = 0; i < values.length; i++) {
			values[i] = Cast.asFloat(scope, data.get(i));
		}
		return sk.evaluate(values);
	}

	@operator(value = "kurtosis", can_be_const = false, type = IType.LIST, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc(value = "returns kurtosis value computed from the operand list of values", special_cases = "if the length of the list is lower than 3, returns NaN", examples = {
			@example("kurtosis ([1,2,3,4,5])") })
	public static Double kurtosis(final IScope scope, final GamaList data) throws GamaRuntimeException {
		final Kurtosis k = new Kurtosis();
		final double[] values = new double[data.length(scope)];
		for (int i = 0; i < values.length; i++) {
			values[i] = Cast.asFloat(scope, data.get(i));
		}
		return k.evaluate(values);
	}

	@operator(value = "kmeans", can_be_const = false, type = IType.LIST, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.CLUSTERING })
	@doc(value = "returns the list of clusters (list of instance indices) computed with the kmeans++ algorithm from the first operand data according to the number of clusters to split the data into (k). Usage: kmeans(data,k)", special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0", examples = {
			@example("kmeans ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],2)") })
	public static GamaList<GamaList> KMeansPlusplusApache(final IScope scope, final GamaList data, final Integer k)
			throws GamaRuntimeException {
		return KMeansPlusplusApache(scope, data, k, -1);
	}

	@operator(value = "build", can_be_const = false, type = IType.REGRESSION, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.REGRESSION })
	@doc(value = "returns the regression build from the matrix data (a row = an instance, the last value of each line is the y value) while using the given method (\"GLS\" or \"OLS\"). Usage: build(data,method)", examples = {
			@example("build(matrix([[1,2,3,4],[2,3,4,2]]),\"GLS\")") })
	public static GamaRegression buildRegression(final IScope scope, final GamaFloatMatrix data, final String method)
			throws GamaRuntimeException {
		try {
			return new GamaRegression(scope, data, method);
		} catch (final Exception e) {
			throw GamaRuntimeException.error("The GLS operator is not usable for these data", scope);
		}
	}

	@operator(value = "build", can_be_const = false, type = IType.REGRESSION, category = {
			IOperatorCategory.STATISTICAL }, concept = {})
	@doc(value = "returns the regression build from the matrix data (a row = an instance, the last value of each line is the y value) while using the given ordinary least squares method. Usage: build(data)", examples = {
			@example("matrix([[1,2,3,4],[2,3,4,2]])") })
	public static GamaRegression buildRegression(final IScope scope, final GamaFloatMatrix data)
			throws GamaRuntimeException {
		try {

			return new GamaRegression(scope, data, "OSL");
		} catch (final Exception e) {
			throw GamaRuntimeException.error("The GLS operator is not usable for these data", scope);
		}
	}

	@operator(value = "predict", can_be_const = false, type = IType.FLOAT, category = {
			IOperatorCategory.STATISTICAL }, concept = { IConcept.STATISTIC, IConcept.REGRESSION })
	@doc(value = "returns the value predict by the regression parameters for a given instance. Usage: predict(regression, instance)", examples = {
			@example("predict(my_regression, [1,2,3]") })
	public static Double predictFromRegression(final IScope scope, final GamaRegression regression,
			final GamaList<Double> instance) throws GamaRuntimeException {
		return regression.predict(scope, instance);
	}

}
