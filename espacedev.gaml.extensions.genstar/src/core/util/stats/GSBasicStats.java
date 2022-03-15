package core.util.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import core.metamodel.entity.IEntity;

/**
 * Intended to compute statistic on various list of number. To compute statistics on arrays {@see GSBasicStats#transpose(...)} static methods
 * 
 * TODO: move to a more generic form that can handle collection of {@link IEntity}, 
 * in order to operate statistic on the whole generation process of genstar (from population to localisation and even network)  
 * 
 * @author kevinchapuis
 *
 * @param <T>
 */
public class GSBasicStats<T extends Number> {
	
	private EnumMap<GSEnumStats, Double> map;
	private double floatPrecision = 0.0001;
	private int nbNoData = 0;
	
	public GSBasicStats(List<T> list){
		List<T> tempList = new ArrayList<>(list);
		this.map = new EnumMap<>(GSEnumStats.class);
		DoubleSummaryStatistics stats = list.parallelStream().mapToDouble(Number::doubleValue)
				.collect(DoubleSummaryStatistics::new, 
				DoubleSummaryStatistics::accept, 
				DoubleSummaryStatistics::combine);
		map.put(GSEnumStats.av, Math.round(stats.getAverage() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.min, Math.round(stats.getMin() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.max, Math.round(stats.getMax() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.sum, Math.round(stats.getSum() / floatPrecision) * floatPrecision);
		
		tempList.sort((n1, n2) -> Double.valueOf(n1.doubleValue()).compareTo(Double.valueOf(n2.doubleValue())));
		map.put(GSEnumStats.med, Math.round(list.get(list.size() % 2 == 0 ? list.size() / 2 : 
			(list.size() + 1) / 2).doubleValue() / floatPrecision) * floatPrecision);
		
		int rest = tempList.size() % 5;
		int quartil = tempList.size() % 5 == 0 ? tempList.size() / 5 : (tempList.size() - rest) / 5;
		map.put(GSEnumStats.q_one, Math.round(tempList.get(quartil).doubleValue() / floatPrecision) * floatPrecision); 
		map.put(GSEnumStats.q_two, Math.round(tempList.get(quartil*2).doubleValue() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.q_three, Math.round(tempList.get(quartil*3).doubleValue() / floatPrecision) * floatPrecision);
		map.put(GSEnumStats.q_four, Math.round(tempList.get(quartil*4).doubleValue() / floatPrecision) * floatPrecision);
	}
	
	public GSBasicStats(List<T> list, List<T> regexData){
		this(list.parallelStream()
				.filter(val -> !regexData.contains(val))
				.collect(Collectors.toList()));
		this.nbNoData  = (int) list.parallelStream()
				.filter(val -> regexData.contains(val)).count();
	}
	
	// ----------------------------------------------------- //
	
	/**
	 * Set the calculation precision
	 * 
	 * @param floatPrecision
	 */
	public void setFloatingPrecision(double floatPrecision){
		this.floatPrecision = floatPrecision;
	}
	
	/**
	 * Retrieve pre calculated statistics on init list. The statistics
	 * are sorted accordind to input {@link GSEnumStats} order
	 * 
	 * @param stats
	 * @return
	 */
	public double[] getStat(GSEnumStats... stats){
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
		String head = "";
		String report = "";
		for(GSEnumStats stat : map.keySet()){
			head = head.isEmpty() ? stat.toString() : head+"\t"+stat.toString();
			report = report.isEmpty() ? map.get(stat).toString() : report+"\t"+map.get(stat).toString(); 
		}
		return head+"\n"+report;
	}
	
	/**
	 * Retrieve the number of NoData in the init list.
	 * 
	 * @return
	 */
	public long getNoDataCount() {
		return nbNoData;
	}

	
	/////////////////////////////////////////////////////////////
	// ---------------------- UTILITIES ---------------------- //
	/////////////////////////////////////////////////////////////
	
	public static List<Integer> transpose(int[] intArray){
		return IntStream.of(intArray).boxed().collect(Collectors.toList());
	}
	
	public static List<Double> transpose(double[] doubleArray){
		return DoubleStream.of(doubleArray).boxed().collect(Collectors.toList());
	}
	
	public static List<Double> transpose(float[] floatArray){
		return IntStream.range(0, floatArray.length).mapToDouble(i -> (double) floatArray[i]).boxed().collect(Collectors.toList());
	}
	
	public static List<Integer> transpose(int[][] intMatrix){
		return Arrays.stream(intMatrix).parallel().flatMapToInt(Arrays::stream).boxed().collect(Collectors.toList());
	}
	
	public static List<Double> transpose(double[][] doubleMatrix){
		return Arrays.stream(doubleMatrix).parallel().flatMapToDouble(Arrays::stream).boxed().collect(Collectors.toList());
	}
	
	public static List<Double> transpose(float[][] floatMatrix){
		List<Double> list = new ArrayList<>();
		for(int i = 0; i < floatMatrix.length; i++){
			int idx = i;
			list.addAll(IntStream.range(0, floatMatrix[idx].length)
					.parallel().mapToDouble(j -> floatMatrix[idx][j]).boxed().collect(Collectors.toList()));
		}
		return list;
	}
	
}
