/*******************************************************************************************************
 *
 * Stochanalysis.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration.stochanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.compress.utils.FileNameUtils;

//import com.google.common.math.Stats;

import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.operators.Cast;

/**
 *
 * @author Tom ROY
 *
 */
/**
 *
 * This class perform a Stochastic Analysis to determinate the minimum size of repeat. This class use 3 different
 * methods: - Coefficient Variation method use a threshold - Standard Error method use a threshold (with a percent) -
 * Student law method
 *
 * This class write the result in a batch report.
 *
 */

public class Stochanalysis {

	/**
	 * We decided to select alpha = 95%
	 * https://en.wikipedia.org/wiki/Student%27s_t-distribution
	 */
	final static double[] Talpha = { 6.314, 2.920, 2.353, 2.132, 2.015, 1.943, 1.895, 1.860, 1.833, 1.812, 1.796, 1.782,
			1.771, 1.761, 1.753, 1.746, 1.740, 1.734, 1.729, 1.725, 1.721, 1.717, 1.714, 1.711, 1.708, 1.706, 1.703,
			1.701, 1.699, 1.697, 1.684, 1.676, 1.671, 1.664, 1.660, 1.658, 1.645 };

	/** The Constant Tbeta. */
	final static double[] Tbeta = { 3.078, 1.886, 1.638, 1.533, 1.476, 1.440, 1.415, 1.397, 1.383, 1.372, 1.363, 1.356,
			1.350, 1.345, 1.341, 1.337, 1.333, 1.330, 1.328, 1.325, 1.323, 1.321, 1.319, 1.318, 1.316, 1.315, 1.314,
			1.313, 1.311, 1.310, 1.303, 1.299, 1.296, 1.292, 1.290, 1.289, 1.282 };

	// Statistical arbitrary indicators
	final static public String CV = "Coefficient of variation";
	final static public String SE = "Standard error";
	final static double[] STOCHThresholds = {0.05,0.01,0.001};
	// Critical size effect TODO : to be refined
	final static public String ES = "Critical effect size";
	final static double[] ESPower = {0.5,0.8,0.9};
	// List of methods
	final static protected List<String> SA = List.of(CV,SE,ES); 
	
	// UTILS 
	final static String SEP = ",";
	final static String RL = "\n";
	
	/**
	 * Find the median value of a list (Skipping -1 values)
	 *
	 * @param list
	 *            : List of value (N minimum for each point)
	 * @param scope
	 * @return the median value
	 */
	private static int findMedian(final List<Integer> list, final IScope scope) {
		List<Integer> list_t = new ArrayList<>();
		for (int o : list) { if (o > 0) { list_t.add(o); } }
		Arrays.sort(list_t.toArray());
		double median;
		if (list_t.size() % 2 == 0) {
			median = ((double) list_t.get(list_t.size() / 2) + (double) list_t.get(list_t.size() / 2 - 1)) / 2;
		} else {
			median = list_t.get(list.size() / 2);
		}
		return (int) Math.ceil(median);
	}

	/**
	 * Find the max value of a list of Integer
	 *
	 * @param list
	 * @param scope
	 * @return the max value
	 */

	private static int findMax(final List<Integer> list, final IScope scope) {
		int max = 0;
		for (Integer element : list) { if (element > 0 && max < element) { max = element; } }
		return max;
	}

	/**
	 * Build the report with result for each method and each output
	 *
	 * @param Out
	 *            : Value to print.
	 * @param scope
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private static String buildResultMap(final Map<String, Map<ParametersSet, Map<String, List<Double>>>> Out, 
			final int nbsample, final int nbreplicates, final IScope scope) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("== STOCHASTICITY ANALYSIS ==");
		sb.append(RL);
		sb.append(Out.size()+" outputs | "+nbsample+" samples | "+nbreplicates+" max replications");
		sb.append("Thresholds: ").append(STOCHThresholds).append(RL);
		sb.append("Meaning: threshold represent the delta of marginal decrease of the minimum value of concerned statistic to decide on the number of replicates").append(RL);
		sb.append("Exemple: focusing on ").append(SE).append(" the corresponding number of replicates is defined when marginal decrease is lower than arg_min(standard error) * threshold").append(RL);
		sb.append(RL).append(RL);

		for (String outputs : Out.keySet()) {
			Map<ParametersSet, Map<String, List<Double>>> pso = Out.get(outputs);
			sb.append("## Output : ");
			sb.append(outputs);
			sb.append(RL);
			
			for (String method : SA) {
				if (pso.values().stream().noneMatch(m -> m.containsKey(method))) { continue; }
				
				if (method != ES) {
					// Compute all given nb of replicate required to accept a threshold hypothesis
					IMap<Double, List<Integer>> res = GamaMapFactory.create();
					for (Double thresh : STOCHThresholds) {
						List<Integer> lres = new ArrayList<>();
						for(ParametersSet ps : pso.keySet()) { lres.add(FindWithRelativeThreshold(pso.get(ps).get(method), thresh)); }
						res.put(thresh, lres);
					}
					sb.append(method).append(RL);
					for (Double threshold : STOCHThresholds) {
						sb.append(threshold).append(" : ");
						sb.append("min = ").append(Collections.min(res.get(threshold))).append(" | ");
						sb.append("max = ").append(Collections.max(res.get(threshold))).append(" | ");
						sb.append("avr = ").append(Math.round(res.get(threshold).stream().mapToInt(i -> i).average().getAsDouble())).append(RL);
					}
				} else {
					// TODO
				}
			}
			sb.append(RL).append(RL);
		}
		return sb.toString();

	}

	/**
	 * 
	 * @param Out
	 * @param nbsample
	 * @param nbreplicates
	 * @param scope
	 * @return
	 */
	private static String buildStochMap(final Map<String, Map<ParametersSet, Map<String, List<Double>>>> Out, 
			final int nbsample, final int nbreplicates, final IScope scope) {
		StringBuilder sb = new StringBuilder();
		// Header of the csv
		sb.append("Outputs").append(SEP);
		
		// Parameters
		IList<String> ph = Out.get(Out.keySet().stream().findAny().get()).keySet().stream().findAny().get().getKeys();
		sb.append(ph.stream().collect(Collectors.joining(","))).append(SEP);
		
		sb.append("Indicator").append(SEP);
		sb.append(IntStream.range(1,nbreplicates).boxed().map(i -> String.valueOf(i)).collect(Collectors.joining(SEP)));
		sb.append(RL);
		
		for (String o : Out.keySet()) {
			Map<ParametersSet, Map<String, List<Double>>> om = Out.get(o);
			for (ParametersSet p : om.keySet()) {
				String lineP = ph.stream().map(head -> p.get(head).toString()).collect(Collectors.joining(SEP));
				Map<String,List<Double>> cr = om.get(p);
				for (String m : cr.keySet()) {
					sb.append(o).append(SEP);
					sb.append(lineP).append(SEP);
					sb.append(m).append(SEP);
					sb.append(cr.get(m).stream().skip(1).map(d -> String.valueOf(d)).collect(Collectors.joining(SEP)));
					sb.append(RL);
				}
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Write and tell report.
	 *
	 * @param path
	 *            the path
	 * @param Outputs
	 *            the outputs
	 * @param scope
	 *            the scope
	 */
	public static void WriteAndTellReport(final File f, final Map<String, Map<ParametersSet, Map<String, List<Double>>>> outputs,
			final int nbsample, final int nbreplicates, final IScope scope) {

		try {
			try (FileWriter fw = new FileWriter(f, false)) {
				fw.write( FileNameUtils.getExtension(f.getPath()).equalsIgnoreCase("txt") ? 
						buildResultMap(outputs, nbsample, nbreplicates, scope) : 
							buildStochMap(outputs, nbsample, nbreplicates, scope));
				
			}
		} catch (IOException e) {
			throw GamaRuntimeException.error("File " + f.toString() + " not found", scope);
		}
	}
	
	/**
	 * Rebuild simulations ouptuts to be written in a file
	 * 
	 * @param Outputs
	 * @param scope
	 * @return
	 */
	public static String buildSimulationCsv(final IMap<ParametersSet, Map<String, List<Object>>> outputs, IScope scope) {
		StringBuilder sb = new StringBuilder();
		String sep = ";";
		String linesep = "\n";
		
		// Write the header
		for (String param : outputs.keySet().stream().findFirst().get().keySet()) { sb.append(param).append(sep); }
		for (String output : outputs.anyValue(scope).keySet()) { sb.append(output).append(sep); }
		
		// Find results and append to global string
		for (ParametersSet ps : outputs.keySet()) {
			Map<String, List<Object>> res = outputs.get(ps);
			int nbr = res.values().stream().findAny().get().size();
			if (!res.values().stream().allMatch(r -> r.size()==nbr)) { 
				GamaRuntimeException.warning("Not all sample of stochastic analysis have the same number of replicates", scope); 
			}
			for (int r = 0; r < nbr; r++) {
				sb.append(linesep);
				for (Object pvalue : ps.values()) { sb.append(pvalue).append(sep); }
				for (String output : res.keySet()) { sb.append(res.get(output).get(r)).append(sep); }
			}
		}

		return sb.toString();
	}
	
	/**
	 * Write and tell row results from simulations
	 * 
	 * @param path
	 * @param Outputs
	 * @param scope
	 */
	public static void WriteAndTellResult(final File f, final IMap<ParametersSet, Map<String, List<Object>>> outputs,
			final IScope scope) {
		try (FileWriter fw = new FileWriter(f, false)) {
			fw.write(buildSimulationCsv(outputs, scope));
		} catch (Exception e) {
			throw GamaRuntimeException.error("File " + f.toString() + " not found", scope);
		}
	}

	/**
	 * Compute the mean of a List of object
	 *
	 * @param val
	 *            : List of value (data of each replicates)
	 * @param scope
	 * @return return the mean for each number of replicates
	 */
	private static List<Double> computeMean(final List<Object> val, final IScope scope) {
		List<Double> mean = new ArrayList<>();
		double tmp_mean = 0;
		for (int i = 0; i < val.size(); i++) {
			double tmp_val = Cast.asFloat(scope, val.get(i));
			tmp_mean = tmp_mean + tmp_val;
			mean.add(tmp_mean / (i + 1));
		}
		return mean;
	}


	/*
	 * Compute all possible means of combinationSize elements of the list
	 *
	 * @param val : List of value (data of each replicates)
	 *
	 * @param scope
	 *
	 * @param combinationSize : size of the local mean to assess
	 *
	 * @return the mean for each number of replicates
	 */
	private static List<Double> computeMeanCombination(final List<Object> val, final IScope scope) {
		List<Double> mean = new ArrayList<>();
		final double rm = val.stream().mapToDouble(o -> Cast.asFloat(scope, o)).boxed()
				.collect(Collectors.averagingDouble(Double::doubleValue));
		for (int i = 1; i < val.size(); i++) {
			Double localMean = 0.0;
			int combinationSize = i + 1;
			for (Object r : val) {
				List<Object> remainings = new ArrayList<>(val);
				remainings.remove(r);
				List<Double> currentMean = new ArrayList<>();
				while (remainings.size() > combinationSize - 1) {
					Collections.shuffle(remainings);
					List<Object> sample = remainings.stream().limit(combinationSize - 1).toList();
					currentMean.add(sample.stream().mapToDouble(e -> Cast.asFloat(scope, e).doubleValue()).boxed()
							.collect(Collectors.averagingDouble(Double::doubleValue)));
					remainings.removeAll(sample);
				}
				double ct = -1.0;
				for (Double cm : currentMean) {
					double lt = Math.abs(cm - rm);
					if (lt > ct) {
						localMean = cm;
						ct = lt;
					}
				}
			}
			mean.add(localMean);
		}
		return mean;
	}

	/**
	 * Compute the Standard Deviation of a list
	 *
	 * @param mean
	 *            : the mean for each number of replicates
	 * @param val
	 *            : List of value (data of each replicates)
	 * @param scope
	 * @return return the standard deviation for each number of replicates (Always 0 for 1).
	 */
	private static List<Double> computeSTD(final List<Double> mean, final List<Object> val, final IScope scope) {
		List<Double> STD = new ArrayList<>();
		for (int i = 0; i < mean.size(); i++) {
			double sum = 0;
			for (int y = 0; y < i; y++) {
				double tmp_val = Cast.asFloat(scope, val.get(y));
				sum = sum + Math.pow(tmp_val - mean.get(i), 2);
			}
			STD.add(Math.sqrt(sum / (i + 1)));
		}
		return STD;
	}

	/**
	 * Compute the Coefficient of Variation of a list
	 *
	 * @param STD
	 *            : the Standard deviation for each number of replicates
	 * @param mean
	 *            : the mean for each number of replicates
	 * @return the coefficient of variation for each number of replicates to 2 at replicate max size
	 */
	private static List<Double> computeCV(final List<Double> STD, final List<Double> mean) {
		List<Double> CV = new ArrayList<>();
		for (int i = 1; i < mean.size(); i++) { CV.add(STD.get(i) / mean.get(i)); }
		return CV;
	}

	/**
	 * Find the minimum replicates size depending of a threshold, when CV[i]-CV[i+1] < threshold, we keep the id "i".
	 *
	 * @param CV
	 *            : the coefficient of variation for each number of replicates
	 * @return the minimum replicates size (or -1 if the threshold is not reached)
	 */
	private static int FindWithThreshold(final List<Double> CV, final double threshold) {
		boolean thresh_ok = false;
		int id_sample = 0;
		for (int i = 0; i < CV.size() - 2; i++) {
			for (int y = i + 1; y < CV.size(); y++) {
				double tmp_val = Math.abs(CV.get(i) - CV.get(y));
				if (tmp_val <= threshold && !thresh_ok) {
					thresh_ok = true;
					id_sample = (1 + i + y) / 2;
				}
			}
		}
		if (!thresh_ok) return -1;
		return id_sample;
	}
	
	/**
	 * Find the minimum replicates size depending on a threshold, 
	 * when CV[i-1] - CV[i] >= 0 and CV[i-1] - CV[i] <= min_arg(CV) * threshold, 
	 * we keep the number of replicates "i".
	 *
	 * @param Stat
	 *            : the statistic given to assess replicates effectiveness
	 * @return the minimum replicates size to reach a given threshold of marginal benefit adding a new replicates
	 */
	private static int FindWithRelativeThreshold(final List<Double> Stat, final double threshold) {
		Double th = Collections.min(Stat) * threshold;
		for (int i = 2; i < Stat.size(); i++) {
			double delta = Stat.get(i-1) - Stat.get(i);
			if (delta >= 0 &&  delta <= th) { return i; }
		}
		return Stat.size();
	}

	/**
	 * Compute the standard deviation of a list.
	 *
	 * @param val
	 *            : list of data
	 * @param mean
	 *            : the mean of the list
	 * @param scope
	 * @return return the standard deviation of the list
	 */
	private static double computeS(final List<Object> val, final double mean, final IScope scope) {
		double tmp_s = 0;
		for (Object element : val) {
			double tmp_val = Cast.asFloat(scope, element);
			tmp_s = tmp_s + (Math.pow(tmp_val, 2) - Math.pow(mean, 2));
		}
		return Math.sqrt(tmp_s / val.size());
	}

	/**
	 * Find the right Student t value depending of the sample size
	 *
	 * @param isAlpha
	 *            : Is alpha ? No - Then is beta.
	 * @param sample_size
	 *            : the number of repeat.
	 * @return return the right alpha or beta value
	 */
	private static double computeT(final boolean isAlpha, final int sample_size) {
		if (isAlpha) {
			if (sample_size <= 30) return Talpha[sample_size - 1];
			if (30 < sample_size && sample_size <= 40) return Talpha[30];
			if (40 < sample_size && sample_size <= 50) return Talpha[31];
			if (50 < sample_size && sample_size <= 60) return Talpha[32];
			if (60 < sample_size && sample_size <= 80) return Talpha[33];
			if (80 < sample_size && sample_size <= 100) return Talpha[34];
			if (100 < sample_size && sample_size <= 120) return Talpha[35];
			return Talpha[36];
		}
		if (sample_size <= 30) return Tbeta[sample_size - 1];
		if (30 < sample_size && sample_size <= 40) return Tbeta[30];
		if (40 < sample_size && sample_size <= 50) return Tbeta[31];
		if (50 < sample_size && sample_size <= 60) return Tbeta[32];
		if (60 < sample_size && sample_size <= 80) return Tbeta[33];
		if (80 < sample_size && sample_size <= 100) return Tbeta[34];
		if (100 < sample_size && sample_size <= 120) return Tbeta[35];
		return Tbeta[36];
	}


	/**
	 * Main method for the Stochastic Analysis
	 *
	 * @param sample
	 *            : The sample with all replicates for each points with results
	 * @param threshold
	 *            : Threshold for all method, the value will allow to choose the method
	 * @param scope
	 * @return return a List with 0: The n minimum found // 1: The number of failed (if n_minimum > repeat size) //2:
	 *         the result for each point of the space
	 *         
	 * TODO : also export the raw result of stochasticity measures
	 *         
	 */
	public static IMap<ParametersSet, List<Double>> StochasticityAnalysis(final IMap<ParametersSet, List<Object>> sample,
			final String method, final IScope scope) {
		
		IMap<ParametersSet,List<Double>> res = GamaMapFactory.create();
		switch (method) {
		case CV: {
			for (ParametersSet ps : sample.keySet()) {
				List<Object> currentXp = new ArrayList<>(sample.get(ps));
				Collections.shuffle(currentXp);
				res.put(ps, coefficientOfVariance(currentXp, scope));
			}
			break;
		}
		case SE: {
			for (ParametersSet ps : sample.keySet()) {
				List<Object> currentXp = new ArrayList<>(sample.get(ps));
				Collections.shuffle(currentXp);
				res.put(ps, standardError(currentXp, scope));
			}	
			break;
		}
		case ES: {
			for (ParametersSet ps : sample.keySet()) {
				List<Object> currentXp = new ArrayList<>(sample.get(ps));
				Collections.shuffle(currentXp);
				res.put(ps, List.of((double)criticalEffectSize(currentXp, scope)));
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + method);
		}
		return res;
		
	}
	
	// ################# ACTUAL METHODS ################# //
	
	/**
	 * Return the coefficient of variance over a set of replicates
	 * 
	 * @param aSample
	 * @return
	 */
	private static List<Double> coefficientOfVariance(List<Object> aSample, final IScope scope) {
		List<Double> mean = computeMean(aSample, scope);
		List<Double> std = computeSTD(mean, aSample, scope);
		List<Double> cv = computeCV(std, mean);
		return cv;
	}
	
	/**
	 * 
	 * TODO review methods and adapt the indicator to be out ready
	 * 
	 * @see https://www.jasss.org/18/4/4.html
	 * @see https://www.ncbi.nlm.nih.gov/pmc/articles/PMC7745163/
	 * 
	 * @param aSample
	 * @param scope
	 * @return
	 */
	private static int criticalEffectSize(List<Object> aSample, final IScope scope) {
		List<Double> mean = computeMean(aSample, scope);
		double val_mean = mean.get(mean.size() - 1);
		double s = computeS(aSample, val_mean, scope);
		// 1.96 is the normal distribution hypothesis 95% CI
		// std is the standard deviation of the sample
		// sqrt(n) is the square root of the sample size
		// std / sqrt(n) is the standard error btw
		double delta = 1.96 * s / Math.sqrt(aSample.size());
		double t1 = computeT(true, aSample.size());
		double t2 = computeT(false, aSample.size());
		return (int) Math.ceil(2 * (Math.pow(s, 2) / Math.pow(delta, 2)) * Math.pow(t1 + t2, 2));
	}
	
	/**
	 * 
	 * @param aSample
	 * @param scope
	 * @return
	 */
	private static List<Double> standardError(List<Object> aSample, final IScope scope) {
		List<Double> mean = computeMean(aSample, scope);
		List<Double> std = computeSTD(mean, aSample, scope);
		List<Double> SE = new ArrayList<>();
		for (int i = 1; i < std.size(); i++) { SE.add(std.get(i) / Math.sqrt(i + 1)); }
		return SE;
	}
	
	/*
	 * "#################################################################################################"
	 * "#################################################################################################"
	 * ############################# Method for the statistical function ################################"
	 * "#################################################################################################"
	 * "#################################################################################################"
	 */
	// Need to be tested

	/**
	 * Read simulation.
	 *
	 * @param path
	 *            the path
	 * @param idOutput
	 *            the id output
	 * @param scope
	 *            the scope
	 * @return the list
	 */
	public static List<Object> readSimulation(final String path, final int idOutput, final IScope scope) {
		List<Map<String, Object>> parameters = new ArrayList<>();
		try {
			File file = new File(path);
			try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
				String line = " ";
				String[] tempArr;
				List<String> list_name = new ArrayList<>();
				int i = 0;
				while ((line = br.readLine()) != null) {
					tempArr = line.split(",");
					for (String tempStr : tempArr) { if (i == 0) { list_name.add(tempStr); } }
					if (i > 0) {
						Map<String, Object> temp_map = new LinkedHashMap<>();
						for (int y = 0; y < tempArr.length; y++) { temp_map.put(list_name.get(y), tempArr[y]); }
						parameters.add(temp_map);
					}
					i++;
				}
			}
		} catch (IOException ioe) {
			throw GamaRuntimeException.error("File " + path + " not found", scope);
		}
		Map<String, List<Double>> new_Outputs = new LinkedHashMap<>();
		List<String> tmpNames = parameters.get(0).keySet().stream().toList();
		IntStream.range(0, parameters.size()).forEach(i -> {
			for (int y = idOutput; y < tmpNames.size(); y++) {
				List<Double> tmpList;
				try {
					tmpList = new ArrayList<>(new_Outputs.get(tmpNames.get(y)));
					double val = Double.parseDouble((String) parameters.get(i).get(tmpNames.get(y)));
					tmpList.add(val);
					new_Outputs.replace(tmpNames.get(y), tmpList);
				} catch (Exception ignored) {
					tmpList = new ArrayList<>();
					double val = Double.parseDouble((String) parameters.get(i).get(tmpNames.get(y)));
					tmpList.add(val);
					new_Outputs.put(tmpNames.get(y), tmpList);
				}
				parameters.get(i).remove(tmpNames.get(y));
			}
		});
		List<Object> simulation_morris = new ArrayList<>();
		simulation_morris.add(parameters);
		simulation_morris.add(new_Outputs);
		return simulation_morris;
	}

	/**
	 * Builds the string.
	 *
	 * @param s
	 *            the s
	 * @return the string
	 */
	private static String BuildString(final Map<String, Object> s) {
		StringBuilder txt = new StringBuilder();
		for (String name : s.keySet()) { txt.append(s.get(name).toString()).append("_"); }
		return txt.toString();
	}

	/**
	 * Stochasticity analysis from CSV.
	 *
	 * @param replicat
	 *            the replicat
	 * @param threshold
	 *            the threshold
	 * @param path_to_data
	 *            the path to data
	 * @param id_output
	 *            the id output
	 * @param scope
	 *            the scope
	 * @return the string
	 */
	// Need to be tested and change like the main method if it works
	@SuppressWarnings ("unchecked")
	public static String StochasticityAnalysis_From_CSV(final int replicat, final double threshold,
			final String path_to_data, final int id_output, final IScope scope) {
		List<Object> STO_simu = readSimulation(path_to_data, id_output, scope);
		List<Map<String, Object>> MySample = Cast.asList(scope, STO_simu.get(0));
		Map<String, List<Double>> Outputs = Cast.asMap(scope, STO_simu.get(1), false);
		int min_replicat = 1;
		for (String name : Outputs.keySet()) {
			Map<String, List<Object>> sample = new HashedMap<>();
			for (Map<String, Object> m : MySample) {
				String s = BuildString(m);
				if (sample.containsKey(s)) {
					List<Object> tmp_l = sample.get(s);
					tmp_l.add(Outputs.get(name));
					m.replace(s, tmp_l);
				} else {
					List<Object> tmp_l = new ArrayList<>();
					tmp_l.add(Outputs.get(name));
					m.put(s, tmp_l);
				}
			}
			int tmp_replicat = 0;
			for (String ps : sample.keySet()) {
				List<Double> mean = computeMean(sample.get(ps), scope);
				List<Double> std = computeSTD(mean, sample.get(ps), scope);
				List<Double> cv = computeCV(std, mean);
				tmp_replicat = tmp_replicat + FindWithThreshold(cv, threshold);
			}
			min_replicat = tmp_replicat / sample.size();
		}
		min_replicat = min_replicat / Outputs.size();
		return Cast.asString(scope, min_replicat);
	}
}
