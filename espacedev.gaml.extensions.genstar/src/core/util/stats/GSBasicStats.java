/*******************************************************************************************************
 *
 * GSBasicStats.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import core.metamodel.entity.IEntity;

/**
 * Intended to compute statistic on various list of number. To compute statistics on arrays
 * {@see GSBasicStats#transpose(...)} static methods
 *
 * TODO: move to a more generic form that can handle collection of {@link IEntity}, in order to operate statistic on the
 * whole generation process of genstar (from population to localisation and even network)
 *
 * @author kevinchapuis
 *
 * @param <T>
 */
public class GSBasicStats<T extends Number> {

	/** The map. */
	private EnumMap<GSEnumStats, Double> map;

	/** The float precision. */
	private double floatPrecision = 0.0001;

	/** The nb no data. */
	private int nbNoData = 0;

	/**
	 * Instantiates a new GS basic stats.
	 *
	 * @param list
	 *            the list
	 */
	public GSBasicStats(final List<T> list) {
		List<T> tempList = new ArrayList<>(list);
		this.map = new EnumMap<>(GSEnumStats.class);
		DoubleSummaryStatistics stats = list.parallelStream().mapToDouble(Number::doubleValue).collect(
				DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine);
		map.put(GSEnumStats.av, Math.round(stats.getAverage() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.min, Math.round(stats.getMin() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.max, Math.round(stats.getMax() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.sum, Math.round(stats.getSum() / floatPrecision) * floatPrecision);

		tempList.sort(Comparator.comparing(T::doubleValue));
		map.put(GSEnumStats.med, Math.round(
				list.get(list.size() % 2 == 0 ? list.size() / 2 : (list.size() + 1) / 2).doubleValue() / floatPrecision)
				* floatPrecision);

		int rest = tempList.size() % 5;
		int quartil = tempList.size() % 5 == 0 ? tempList.size() / 5 : (tempList.size() - rest) / 5;
		map.put(GSEnumStats.q_one, Math.round(tempList.get(quartil).doubleValue() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.q_two,
				Math.round(tempList.get(quartil * 2).doubleValue() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.q_three,
				Math.round(tempList.get(quartil * 3).doubleValue() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.q_four,
				Math.round(tempList.get(quartil * 4).doubleValue() / floatPrecision) * floatPrecision);
	}

	/**
	 * Instantiates a new GS basic stats.
	 *
	 * @param list
	 *            the list
	 * @param regexData
	 *            the regex data
	 */
	public GSBasicStats(final List<T> list, final List<T> regexData) {
		this(list.parallelStream().filter(val -> !regexData.contains(val)).toList());
		this.nbNoData = (int) list.parallelStream().filter(regexData::contains).count();
	}

	// ----------------------------------------------------- //

	/**
	 * Set the calculation precision
	 *
	 * @param floatPrecision
	 */
	public void setFloatingPrecision(final double floatPrecision) { this.floatPrecision = floatPrecision; }

	/**
	 * Retrieve pre calculated statistics on init list. The statistics are sorted accordind to input {@link GSEnumStats}
	 * order
	 *
	 * @param stats
	 * @return
	 */
	public double[] getStat(final GSEnumStats... stats) {
		double[] output = new double[stats.length];
		IntStream.range(0, stats.length).forEach(i -> output[i] = map.get(stats[i]));
		return output;
	}

	/**
	 * Get a synthetic overview of available statistics
	 *
	 * @return
	 */
	public String getStatReport() {
		StringBuilder head = new StringBuilder();
		StringBuilder report = new StringBuilder();
		for (Entry<GSEnumStats, Double> entry : map.entrySet()) {
			if (!head.isEmpty()) { head.append("\t"); }
			head.append(entry.getKey());
			if (!report.isEmpty()) { report.append("\t"); }
			report.append(entry.getValue());
		}
		return head + "\n" + report;

	}

	/**
	 * Retrieve the number of NoData in the init list.
	 *
	 * @return
	 */
	public long getNoDataCount() { return nbNoData; }

	/////////////////////////////////////////////////////////////
	// ---------------------- UTILITIES ---------------------- //
	/////////////////////////////////////////////////////////////

	/**
	 * Transpose.
	 *
	 * @param intArray
	 *            the int array
	 * @return the list
	 */
	public static List<Integer> transpose(final int[] intArray) {
		return IntStream.of(intArray).boxed().toList();
	}

	/**
	 * Transpose.
	 *
	 * @param doubleArray
	 *            the double array
	 * @return the list
	 */
	public static List<Double> transpose(final double[] doubleArray) {
		return DoubleStream.of(doubleArray).boxed().toList();
	}

	/**
	 * Transpose.
	 *
	 * @param floatArray
	 *            the float array
	 * @return the list
	 */
	public static List<Double> transpose(final float[] floatArray) {
		return IntStream.range(0, floatArray.length).mapToDouble(i -> (double) floatArray[i]).boxed().toList();
	}

	/**
	 * Transpose.
	 *
	 * @param intMatrix
	 *            the int matrix
	 * @return the list
	 */
	public static List<Integer> transpose(final int[][] intMatrix) {
		return Arrays.stream(intMatrix).parallel().flatMapToInt(Arrays::stream).boxed().toList();
	}

	/**
	 * Transpose.
	 *
	 * @param doubleMatrix
	 *            the double matrix
	 * @return the list
	 */
	public static List<Double> transpose(final double[][] doubleMatrix) {
		return Arrays.stream(doubleMatrix).parallel().flatMapToDouble(Arrays::stream).boxed().toList();
	}

	/**
	 * Transpose.
	 *
	 * @param floatMatrix
	 *            the float matrix
	 * @return the list
	 */
	public static List<Double> transpose(final float[][] floatMatrix) {
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < floatMatrix.length; i++) {
			int idx = i;
			list.addAll(IntStream.range(0, floatMatrix[idx].length).parallel().mapToDouble(j -> floatMatrix[idx][j])
					.boxed().toList());
		}
		return list;
	}

}
