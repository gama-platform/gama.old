/*********************************************************************************************
 * 
 *
 * 'DataSet.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit http://gama-platform.googlecode.com for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package org.uncommons.maths.statistics;

import java.util.Arrays;

/**
 * Utility class for calculating statistics for a finite data set.
 * @author Daniel Dyer
 * @see <a href="http://betterexplained.com/articles/how-to-analyze-data-using-the-average"> How To
 *      Analyze Data Using the Average</a>
 */
public class DataSet {

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
	 * @param capacity The initial capacity for the data set (this number of values will be able to
	 *            be added without needing to resize the internal data storage).
	 */
	public DataSet(final int capacity) {
		this.dataSet = new double[capacity];
		this.dataSetSize = 0;
	}

	/**
	 * Creates a data set and populates it with the specified values.
	 * @param dataSet The values to add to this data set.
	 */
	public DataSet(final double[] dataSet) {
		this.dataSet = dataSet.clone();
		this.dataSetSize = dataSet.length;
		for ( double value : this.dataSet ) {
			updateStatsWithNewValue(value);
		}
	}

	/**
	 * Adds a single value to the data set and updates any statistics that are calculated
	 * cumulatively.
	 * @param value The value to add.
	 */
	public void addValue(final double value) {
		if ( dataSetSize == dataSet.length ) {
			// Increase the capacity of the array.
			int newLength = (int) (GROWTH_RATE * dataSetSize);
			double[] newDataSet = new double[newLength];
			System.arraycopy(dataSet, 0, newDataSet, 0, dataSetSize);
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
		minimum = Math.min(minimum, value);
		maximum = Math.max(maximum, value);
	}

	private void assertNotEmpty() {
		if ( getSize() == 0 ) { throw new EmptyDataSetException(); }
	}

	/**
	 * Returns the number of values in this data set.
	 * @return The size of the data set.
	 */
	public final int getSize() {
		return dataSetSize;
	}

	/**
	 * @return The smallest value in the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 * @since 1.0.1
	 */
	public final double getMinimum() {
		assertNotEmpty();
		return minimum;
	}

	/**
	 * @return The biggest value in the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 * @since 1.0.1
	 */
	public final double getMaximum() {
		assertNotEmpty();
		return maximum;
	}

	/**
	 * Determines the median value of the data set.
	 * @return If the number of elements is odd, returns the middle element. If the number of
	 *         elements is even, returns the midpoint of the two middle elements.
	 * @since 1.0.1
	 */
	public final double getMedian() {
		assertNotEmpty();
		// Sort the data (take a copy to do this).
		double[] dataCopy = new double[getSize()];
		System.arraycopy(dataSet, 0, dataCopy, 0, dataCopy.length);
		Arrays.sort(dataCopy);
		int midPoint = dataCopy.length / 2;
		if ( dataCopy.length % 2 != 0 ) { return dataCopy[midPoint]; }

		return dataCopy[midPoint - 1] + (dataCopy[midPoint] - dataCopy[midPoint - 1]) / 2;

	}

	/**
	 * @return The sum of all values.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getAggregate() {
		assertNotEmpty();
		return total;
	}

	/**
	 * @return The product of all values.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getProduct() {
		assertNotEmpty();
		return product;
	}

	/**
	 * The arithemthic mean of an n-element set is the sum of all the elements divided by n. The
	 * arithmetic mean is often referred to simply as the "mean" or "average" of a data set.
	 * @see #getGeometricMean()
	 * @return The arithmetic mean of all elements in the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getArithmeticMean() {
		assertNotEmpty();
		return total / dataSetSize;
	}

	/**
	 * The geometric mean of an n-element set is the nth-root of the product of all the elements.
	 * The geometric mean is used for finding the average factor (e.g. an average interest rate).
	 * @see #getArithmeticMean()
	 * @see #getHarmonicMean()
	 * @return The geometric mean of all elements in the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getGeometricMean() {
		assertNotEmpty();
		return Math.pow(product, 1.0d / dataSetSize);
	}

	/**
	 * The harmonic mean of an n-element set is {@literal n} divided by the sum of the reciprocals
	 * of the values (where the reciprocal of a value {@literal x} is 1/x). The harmonic mean is
	 * used to calculate an average rate (e.g. an average speed).
	 * @see #getArithmeticMean()
	 * @see #getGeometricMean()
	 * @since 1.1
	 * @return The harmonic mean of all the elements in the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getHarmonicMean() {
		assertNotEmpty();
		return dataSetSize / reciprocalSum;
	}

	/**
	 * Calculates the mean absolute deviation of the data set. This is the average (absolute) amount
	 * that a single value deviates from the arithmetic mean.
	 * @see #getArithmeticMean()
	 * @see #getVariance()
	 * @see #getStandardDeviation()
	 * @return The mean absolute deviation of the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getMeanDeviation() {
		double mean = getArithmeticMean();
		double diffs = 0;
		for ( int i = 0; i < dataSetSize; i++ ) {
			diffs += Math.abs(mean - dataSet[i]);
		}
		return diffs / dataSetSize;
	}

	/**
	 * Calculates the variance (a measure of statistical dispersion) of the data set. There are
	 * different measures of variance depending on whether the data set is itself a finite
	 * population or is a sample from some larger population. For large data sets the difference is
	 * negligible. This method calculates the population variance.
	 * @see #getSampleVariance()
	 * @see #getStandardDeviation()
	 * @see #getMeanDeviation()
	 * @return The population variance of the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getVariance() {
		return sumSquaredDiffs() / getSize();
	}

	/**
	 * Helper method for variance calculations.
	 * @return The sum of the squares of the differences between each value and the arithmetic mean.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	private double sumSquaredDiffs() {
		double mean = getArithmeticMean();
		double squaredDiffs = 0;
		for ( int i = 0; i < getSize(); i++ ) {
			double diff = mean - dataSet[i];
			squaredDiffs += diff * diff;
		}
		return squaredDiffs;
	}

	/**
	 * The standard deviation is the square root of the variance. This method calculates the
	 * population standard deviation as opposed to the sample standard deviation. For large data
	 * sets the difference is negligible.
	 * @see #getSampleStandardDeviation()
	 * @see #getVariance()
	 * @see #getMeanDeviation()
	 * @return The standard deviation of the population.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}

	/**
	 * Calculates the variance (a measure of statistical dispersion) of the data set. There are
	 * different measures of variance depending on whether the data set is itself a finite
	 * population or is a sample from some larger population. For large data sets the difference is
	 * negligible. This method calculates the sample variance.
	 * @see #getVariance()
	 * @see #getSampleStandardDeviation()
	 * @see #getMeanDeviation()
	 * @return The sample variance of the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getSampleVariance() {
		return sumSquaredDiffs() / (getSize() - 1);
	}

	/**
	 * The sample standard deviation is the square root of the sample variance. For large data sets
	 * the difference between sample standard deviation and population standard deviation is
	 * negligible.
	 * @see #getStandardDeviation()
	 * @see #getSampleVariance()
	 * @see #getMeanDeviation()
	 * @return The sample standard deviation of the data set.
	 * @throws EmptyDataSetException If the data set is empty.
	 */
	public final double getSampleStandardDeviation() {
		return Math.sqrt(getSampleVariance());
	}
}
