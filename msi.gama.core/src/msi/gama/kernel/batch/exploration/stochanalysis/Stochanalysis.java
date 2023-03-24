/*******************************************************************************************************
 *
 * Stochanalysis.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
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

	/** The Constant CI. */
	// 90, 95 and 99% CI interval
	final static double[] CI = { 1.645, 1.980, 2.576 };

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
		for (int o : list) { if (o != -1) { list_t.add(o); } }
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
		for (Integer element : list) { if (element != -1 && max < element) { max = element; } }
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
	private static String buildResultMap(final Map<String, Map<Double, List<Object>>> Out, final IScope scope) {
		StringBuilder sb = new StringBuilder();
		sb.append("== STOCHASTICITY ANALYSIS: \n");
		sb.append("\n");
		sb.append("--- Summary of required number of replications ---\n");
		List<Integer> all_vals = new ArrayList<>();
		for (String i : Out.keySet()) {
			for (List<Object> l : Out.get(i).values()) {
				all_vals.addAll(Cast.asList(scope, l.get(2)).stream().mapToInt(o -> Integer.parseInt(o.toString()))
						.filter(d -> d > 0).boxed().toList());
			}
		}
		sb.append("On average requires: " + all_vals.stream().mapToDouble(i -> i * 1d).average().orElse(0.0));
		sb.append("\tHighest requirements: " + Collections.max(all_vals));
		sb.append("\tLowest requirements: " + Collections.min(all_vals));
		sb.append("--- End of summary ---\n\n");
		for (String outputs : Out.keySet()) {
			Map<Double, List<Object>> tmp_map = Out.get(outputs);
			sb.append("## Output : ");
			sb.append(outputs);
			sb.append("\n");
			sb.append("\n");
			for (int i = 0; i < tmp_map.size(); i++) {
				// Method to write
				double val = tmp_map.keySet().stream().sorted().toList().get(i);
				// Minimum size to print
				int n_min = Cast.asInt(scope, tmp_map.get(val).get(0));
				// Number of point failed. (Nb_min > Nb repeat used)
				int nb_failed = Cast.asInt(scope, tmp_map.get(val).get(1));
				List<Integer> nb_val = Cast.asList(scope, tmp_map.get(val).get(2));
				if (0 < val && val < 1) {
					sb.append(" CV method - Threshold : ");
					sb.append(val);
					sb.append("\n");
					sb.append(
							"Nb minimum replicat found \\ Nb median \\ Nb max replicat found \\ Nb failed (> Nb replicat )");
					sb.append("\n");

					if (nb_failed >= nb_val.size() / 2) {
						sb.append("Failed");
						sb.append(" \\ ");
						sb.append("Failed");
					} else {
						sb.append(n_min);
						sb.append(" \\ ");
						sb.append(findMedian(nb_val, scope));
					}
					sb.append(" \\ ");
					int max = findMax(nb_val, scope);
					if (max != 0) {
						sb.append(findMax(nb_val, scope));
					} else {
						sb.append("Failed");
					}
					sb.append(" \\ ");
					if (nb_failed != 0) {
						sb.append(nb_failed);
						sb.append(" (");
						sb.append(Cast.asFloat(scope, nb_failed) / Cast.asFloat(scope, nb_val.size()) * 100);
						sb.append("%)");
					}
					sb.append("\n");
					sb.append("All values for each points :");
					sb.append("\n");
					sb.append(nb_val);
					sb.append("\n");
					sb.append("\n");
				} else if (val == -1) {
					sb.append(" Student method: ");
					sb.append("\n");
					sb.append("Nb minimum replicat found \\ Nb median \\ Nb max replicat found");
					sb.append("\n");

					if (nb_failed >= nb_val.size() / 2) {
						sb.append("Failed");
						sb.append(" \\ ");
						sb.append("Failed");
					} else {
						sb.append(n_min);
						sb.append(" \\ ");
						sb.append(findMedian(nb_val, scope));
					}
					sb.append(" \\ ");
					int max = findMax(nb_val, scope);
					if (max != 0) {
						sb.append(findMax(nb_val, scope));
					} else {
						sb.append("Failed");
					}
					sb.append(" \\ ");
					sb.append("\n");
					sb.append("All values for each points:");
					sb.append("\n");
					sb.append(nb_val);
					sb.append("\n");
					sb.append("\n");
				} else if (val >= 1) {
					sb.append(" SE method - Percent : ");
					sb.append(val);
					sb.append("% \n");
					sb.append(
							"Nb minimum replicat found \\ Nb median \\ Nb max replicat found \\ Nb failed (> Nb replicat )");
					sb.append("\n");

					if (nb_failed >= nb_val.size() / 2) {
						sb.append("Failed");
						sb.append(" \\ ");
						sb.append("Failed");
					} else {
						sb.append(n_min);
						sb.append(" \\ ");
						sb.append(findMedian(nb_val, scope));
					}
					sb.append(" \\ ");
					int max = findMax(nb_val, scope);
					if (max != 0) {
						sb.append(findMax(nb_val, scope));
					} else {
						sb.append("Failed");
					}
					sb.append(" \\ ");
					if (nb_failed != 0) {
						sb.append(nb_failed);
						sb.append(" (");
						sb.append(Cast.asFloat(scope, nb_failed) / Cast.asFloat(scope, nb_val.size()) * 100);
						sb.append("%)");
					}
					sb.append("\n");
					sb.append("All values for each points :");
					sb.append("\n");
					sb.append(nb_val);
					sb.append("\n");
					sb.append("\n");
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
	public static void WriteAndTellReport(final File f, final Map<String, Map<Double, List<Object>>> outputs,
			final IScope scope) {

		try {
			try (FileWriter fw = new FileWriter(f, false)) {
				fw.write(buildResultMap(outputs, scope));
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
		String sep = CSVInfo.DELIMITER;
		
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
				for (Object pvalue : ps.values()) { sb.append(pvalue); }
				for (String output : res.keySet()) { sb.append(res.get(output).get(r)); }
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
				while (remainings.size() < combinationSize - 1) {
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
					id_sample = i + 1;
				}
			}
		}
		if (!thresh_ok) return -1;
		return id_sample;
	}

	/**
	 * Compute the Standard Error of a List
	 *
	 * @param STD
	 *            : the Standard deviation for each number of replicates
	 * @return the standard error for each number of replicates
	 */
	private static List<Double> computeSE(final List<Double> STD, final double z) {
		List<Double> SE = new ArrayList<>();
		for (int i = 1; i < STD.size(); i++) { SE.add(z * STD.get(i) / Math.sqrt(i + 1)); }
		return SE;
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
	 * Compute the absolute difference in means of a list
	 *
	 * @param val
	 *            : list of data
	 * @param mean
	 *            : the mean of the list
	 * @param scope
	 * @return return the absolute difference in means of the list
	 */
	private static double computeDelta(final List<Object> val, final double mean, final IScope scope) {
		double tmp_delta = 0;
		for (Object element : val) {
			double tmp_val = Cast.asFloat(scope, element);
			tmp_delta += Math.abs(tmp_val - mean);
		}
		return tmp_delta / val.size();
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
			if (60 < sample_size && sample_size <= 80)
				return Talpha[33];
			else if (80 < sample_size && sample_size <= 100)
				return Talpha[34];
			else if (100 < sample_size && sample_size <= 120)
				return Talpha[35];
			else
				return Talpha[36];
		}
		if (sample_size <= 30) return Tbeta[sample_size - 1];
		if (30 < sample_size && sample_size <= 40) return Tbeta[30];
		if (40 < sample_size && sample_size <= 50) return Tbeta[31];
		if (50 < sample_size && sample_size <= 60) return Tbeta[32];
		if (60 < sample_size && sample_size <= 80)
			return Tbeta[33];
		else if (80 < sample_size && sample_size <= 100)
			return Tbeta[34];
		else if (100 < sample_size && sample_size <= 120)
			return Tbeta[35];
		else
			return Tbeta[36];
	}

	/**
	 * Compute the minimum number of replicate with the student method
	 *
	 * @param S
	 *            : Standard deviation
	 * @param delta
	 *            : absolute difference in mean
	 * @param Ta
	 *            : Student t alpha t statistic
	 * @param Tb
	 *            : Student t beta t statistic
	 * @return minimum number of replicate found
	 */
	private static int Student(final double S, final double delta, final double Ta, final double Tb) {
		return (int) Math.ceil(2 * (Math.pow(S, 2) / Math.pow(delta, 2)) * Math.pow(Ta + Tb, 2));
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
	 */
	public static List<Object> StochasticityAnalysis(final IMap<ParametersSet, List<Object>> sample,
			final double threshold, final IScope scope) {
		int tmp_replicat = 0;
		if (0 < threshold && threshold < 1) {
			// Min nb replicat with CV method
			int compteur_failed = 0;
			List<Integer> n_min_list = new ArrayList<>();
			for (ParametersSet ps : sample.keySet()) {
				List<Object> currentXp = sample.get(ps);
				Collections.shuffle(currentXp);
				List<Double> mean = computeMeanCombination(currentXp, scope);
				List<Double> std = computeSTD(mean, sample.get(ps), scope);
				List<Double> cv = computeCV(std, mean);
				int n_tmp = FindWithThreshold(cv, threshold);
				if (n_tmp == -1) {
					compteur_failed++;
					tmp_replicat = tmp_replicat + mean.size();
					n_min_list.add(-1);
				} else {
					n_min_list.add(n_tmp);
					tmp_replicat = tmp_replicat + n_tmp;
				}
			}
			int min_replicat = tmp_replicat / sample.size();
			if (min_replicat == 0) { min_replicat = 1; }
			return Arrays.asList(min_replicat, compteur_failed, n_min_list);
		}
		if (threshold == -1) {
			// Min nb replicat with Student method
			int compteur_failed = 0;
			List<Integer> n_min_list = new ArrayList<>();
			for (ParametersSet ps : sample.keySet()) {
				List<Double> mean = computeMean(sample.get(ps), scope);
				double val_mean = mean.get(mean.size() - 1);
				double s = computeS(sample.get(ps), val_mean, scope);
				double delta = computeDelta(sample.get(ps), val_mean, scope);
				double t1 = computeT(true, mean.size());
				double t2 = computeT(false, mean.size());
				int nb_tmp = Student(s, delta, t1, t2);
				n_min_list.add(nb_tmp);
				tmp_replicat += nb_tmp;
			}
			int min_replicat = tmp_replicat / sample.size();
			if (min_replicat == 0) { min_replicat = -1; }
			return Arrays.asList(min_replicat, compteur_failed, n_min_list);

		}
		if (threshold < 1) throw GamaRuntimeException.error("Wrong value for threshold", scope);
		// Min nb replicat with SE method
		// hypothesis of normally distributed samples around the mean
		// ==> means that a SE have to fit into a given confident interval
		int compteur_failed = 0;
		List<Integer> n_min_list = new ArrayList<>();
		for (ParametersSet ps : sample.keySet()) {
			List<Object> currentXp = sample.get(ps);
			Collections.shuffle(currentXp);
			List<Double> mean = computeMeanCombination(currentXp, scope);
			List<Double> std = computeSTD(mean, currentXp, scope);
			double ci = threshold == 90 ? CI[0] : threshold == 95 ? CI[1] : CI[2];
			double new_threshold = ci * std.get(std.size() - 1) / Math.sqrt(std.size());
			List<Double> se = computeSE(std, ci);
			boolean first = true;
			int tmp = 0;
			for (int i = 0; i < se.size(); i++) {
				if (first && se.get(i) < new_threshold) {
					tmp = i + 1;
					first = false;
				}
			}
			if (first) {
				compteur_failed++;
				tmp_replicat = tmp_replicat + mean.size();
				n_min_list.add(-1);
			} else {
				tmp_replicat = tmp_replicat + tmp;
				n_min_list.add(tmp);
			}
		}
		int min_replicat = tmp_replicat / sample.size();
		if (min_replicat == 0) { min_replicat = 1; }
		return Arrays.asList(min_replicat, compteur_failed, n_min_list);
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
