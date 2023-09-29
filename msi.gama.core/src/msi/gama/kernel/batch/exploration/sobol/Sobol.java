/*******************************************************************************************************
 *
 * Sobol.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration.sobol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.moeaframework.util.sequence.Saltelli;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Strings;

/**
 * The Class Sobol.
 */
public class Sobol {

	/** The list of Saltelli indices [0; 1] */
	private double[][] saltelli;

	/** The sample. */
	private int sample;

	/** The sample. */
	private int _sample;

	/** The resample. */
	protected int _resample = 1000; // Bootstraping for confidence interval

	/** Map describing the problem : pair of min and max value for each parameters */
	private Map<String, List<Object>> problem;

	/** Map giving the values of each parameters */
	private final Map<String, List<Object>> parameters;

	/** Output name */
	private final List<String> output_names;

	/** Map of outputs values */
	private Map<String, List<Object>> outputs;

	/**
	 * sobol indexes for each variable <br>
	 * - K1 the output name <br>
	 * - K2 the variable name <br>
	 * - V list of first order index, first order confidence, second order index, second order confidence
	 */
	private final Map<String, Map<String, List<Double>>> sobol_analysis = new HashMap<>();

	/**
	 * Scope for GamaRunTimeException
	 */
	private final IScope scope;

	/**
	 * Build a Sobol element corresponding to the problem
	 *
	 * @param problem
	 *            : A map with K the name of the parameter and V a list containing the max and min values of the param
	 *            (or a list of possible values for categorical parameters)
	 * @param output
	 *            : The name of the output variable
	 * @param sample
	 *            : The number of sample
	 */
	public Sobol(final LinkedHashMap<String, List<Object>> problem, final List<String> output_names, final int sample,
			final IScope scope) {
		this.scope = scope;
		this.problem = problem;
		this.parameters = new LinkedHashMap<>();
		problem.keySet().stream().forEach(p -> this.parameters.put(p, new ArrayList<>()));
		this.output_names = output_names;
		this.outputs = new HashMap<>();

		this.sample = sample;
		this._sample = sample * (2 * parameters.size() + 2);
	}

	/**
	 * Build a sobol problem from a .csv file of format : <br>
	 * |param_1, ..., param_P, output_1, ..., output_X <br>
	 * |val_11, ..., val_1P, eval_11, ... , eval_1X <br>
	 * | . <br>
	 * | . <br>
	 * |val_N1, ..., val_NP, eval_N1, ... , eval_NX <br>
	 *
	 * @param path
	 *            : path to a .csv file
	 * @param nb_parameters
	 *            : number of parameters of the problem
	 * @param output
	 *            : name of the output columns in the .csv file
	 */
	public Sobol(final File f, final int nb_parameters, final IScope scope) {
		this.scope = scope;
		this.parameters = new LinkedHashMap<>();
		this.output_names = new ArrayList<>();
		this.outputs = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line = br.readLine();

			// parse first line
			String columns_names[] = line.split(",");
			// parameters names
			for (int i = 0; i < nb_parameters; i++) { parameters.put(columns_names[i], new ArrayList<>()); }
			// output variable
			for (int i = nb_parameters; i < columns_names.length; i++) {
				output_names.add(columns_names[i]);
				this.outputs.put(columns_names[i], new ArrayList<>());
			}

			// parse values and count the number of samples
			this._sample = 0;
			while ((line = br.readLine()) != null) {
				this._sample++;
				String values[] = line.split(",");
				for (int i = 0; i < nb_parameters; i++) {
					this.parameters.get(columns_names[i]).add(Double.parseDouble(values[i]));
				}
				for (int i = nb_parameters; i < columns_names.length; i++) {
					this.outputs.get(columns_names[i]).add(Double.parseDouble(values[i]));

				}
			}
			if (_sample % (2 * nb_parameters + 2) != 0) throw new IllegalArgumentException(
					"Number of sample in the file doesn't match the number of parameters");
			sample = _sample / (2 * nb_parameters + 2);

		} catch (IOException e) {
			throw GamaRuntimeException.error("File " + f.toString() + " not found", scope);
		} catch (OutOfRangeException e) {
			throw GamaRuntimeException.error(
					"The number of parameters provided doesn't match the number of parameters in the file", scope);
		} catch (Exception e) {
			throw GamaRuntimeException.error(e.toString(), scope);
		}
	}

	/**
	 * Generate a sample using a random Saltelli sampling
	 */
	public void setRandomSaltelliSampling() {
		saltelli = new Saltelli().generate(_sample, parameters.size());
		sample();
	}

	/**
	 * Generate a sample using the provided .csv file containing a matrix of size N x P <br>
	 * - N the number of _sample <br>
	 * - P the number of parameters
	 *
	 * @param file
	 *            : .csv file
	 */
	public void setSaltelliSamplingFromCsv(final File file) {
		parseSaltelli(file);
		sample();
	}

	/**
	 * Save the Saltelli sample of this Sobol object in a .cvs file
	 *
	 * @param file
	 *            : .csv file
	 */
	public void saveSaltelliSample(final File file) {
		try (FileWriter fw = new FileWriter(file, false)) {
			fw.write(buildSaltelliReport());
		} catch (IOException e) {
			throw GamaRuntimeException.error("File " + file.toString() + " not found", scope);
		}
	}

	/**
	 * Return the parameters of the simulation and it's values
	 *
	 * @return A map with <br>
	 *         - K the name of the parameter, <br>
	 *         - V the list of values of this parameter
	 */
	public Map<String, List<Object>> getParametersValues() { return this.parameters; }

	/**
	 * Set the output of corresponding to each parameter set
	 *
	 * @param outputs
	 *            : a map with : <br>
	 *            - K the name of the output, <br>
	 *            - V the list of outputs in the same order as parameters inputs
	 */
	public void setOutputs(final Map<String, List<Object>> outputs) {
		for (String output : outputs.keySet()) {
			if (outputs.get(output).size() != _sample) throw GamaRuntimeException.error(
					"This size of the output " + output + " doesn't match the number of samples in the parameters",
					scope);
		}
		this.outputs = outputs;
	}

	/**
	 * Evaluate the sobol indices
	 *
	 * @return A map of map with : <br>
	 *         - K1 the output name, <br>
	 *         - K2 the variable name, <br>
	 *         - V list of first order index, first order confidence, second order index, second order confidence
	 */
	public Map<String, Map<String, List<Double>>> evaluate() {
		if (outputs.isEmpty()) { System.err.println("no output porivded call setOutputs before calling evaluate"); }

		/* INIT MOEA FRAMEWORK SOBOL SEQUENCE */
		A = new double[sample];
		B = new double[sample];
		C_A = new double[sample][this.parameters.size()];
		C_B = new double[sample][this.parameters.size()];

		for (String output : outputs.keySet()) {
			Iterator<Object> it = outputs.get(output).iterator();
			Map<String, List<Double>> sobolIndexes_output = new HashMap<>();

			for (int i = 0; i < sample; i++) {
				// TODO : does it has to be continuous output variables ? How to check for int for example ?
				A[i] = Double.parseDouble(it.next().toString());

				for (int j = 0; j < this.parameters.size(); j++) {
					C_A[i][j] = Double.parseDouble(it.next().toString());
				}

				for (int j = 0; j < this.parameters.size(); j++) {
					C_B[i][j] = Double.parseDouble(it.next().toString());
				}

				B[i] = Double.parseDouble(it.next().toString());
			}

			int j = 0;
			for (String param : parameters.keySet()) {
				List<Double> sobolIndexes = new ArrayList<>();
				double[] a0 = new double[sample];
				double[] a1 = new double[sample];
				double[] a2 = new double[sample];

				for (int i = 0; i < sample; i++) {
					a0[i] = A[i];
					a1[i] = C_A[i][j];
					a2[i] = B[i];
				}

				// First order
				sobolIndexes.add(computeFirstOrder(a0, a1, a2, sample));
				sobolIndexes.add(computeFirstOrderConfidence(a0, a1, a2, sample, _resample));
				// Total order
				sobolIndexes.add(computeTotalOrder(a0, a1, a2, sample));
				sobolIndexes.add(computeTotalOrderConfidence(a0, a1, a2, sample, _resample));

				sobolIndexes_output.put(param, sobolIndexes);
				j++;
			}
			sobol_analysis.put(output, sobolIndexes_output);
		}

		return sobol_analysis;
	}

	/**
	 * Save the simulation data (input and output of the model for each sample) in a .csv file
	 *
	 * @param file
	 *            : .csv file
	 */
	public void saveSimulation(final File file) {
		try (FileWriter fw = new FileWriter(file, false)) {
			fw.write(this.buildSimulationCsv());
		} catch (Exception e) {
			throw GamaRuntimeException.error("File " + file.toString() + " not found", scope);
		}
	}

	/**
	 * Save the report of the Sobol analysis (sobol indexes) in a .csv file
	 *
	 * @param file
	 *            : .csv file
	 */
	public void saveResult(final File file) {
		try (FileWriter fw = new FileWriter(file, false)) {
			fw.write(this.buildReportString(FileNameUtils.getExtension(file.getPath())));
		} catch (Exception e) {
			throw GamaRuntimeException.error("File " + file.toString() + " not found", scope);
		}
	}

	/**
	 * Build the string that contains the report of the Sobol analysis
	 */
	public String buildReportString(String extension) {
		
		StringBuilder sb = new StringBuilder();
		char sep = ',';
		
		if (extension.equalsIgnoreCase("csv")) {
			// Build header
			sb.append("output").append(sep);
			sb.append("parameter").append(sep);
			sb.append("first order").append(sep);
			sb.append("first order confidence").append(sep);
			sb.append("Total order").append(sep);
			sb.append("Total order confidence").append(Strings.LN);
			for (String output_name : sobol_analysis.keySet()) {
				for (String param : sobol_analysis.get(output_name).keySet()) {
					// The output & parameter
					sb.append(output_name).append(sep);
					sb.append(param);
					for (Double indices : sobol_analysis.get(output_name).get(param)) {
						// The Sobol indices
						sb.append(sep).append(indices);
					}
					sb.append(Strings.LN);
				}
			}	
		} else {
			sb.append("SOBOL ANALYSIS:\n");
			for (String output_name : sobol_analysis.keySet()) {
				sb.append("##############################\n");
				sb.append("output variable : " + output_name).append(Strings.LN);
				sb.append("-------------------").append(Strings.LN);
				for (String param : sobol_analysis.get(output_name).keySet()) {
					sb.append(param + " : \n");
					sb.append("first order : ");
					sb.append(sobol_analysis.get(output_name).get(param).get(0)).append(Strings.LN);
					sb.append("first order confidence : ");
					sb.append(sobol_analysis.get(output_name).get(param).get(1)).append(Strings.LN);
					sb.append("Total order : ");
					sb.append(sobol_analysis.get(output_name).get(param).get(2)).append(Strings.LN);
					sb.append("Total order confidence : ");
					sb.append(sobol_analysis.get(output_name).get(param).get(3)).append(Strings.LN);
					sb.append("-------------------").append(Strings.LN);
				}
			}
		}
		
		return sb.toString();
	}

	/*******************************************************/
	/************************* UTILS *************************/
	/*******************************************************/

	/*
	 * Compute the parameters values for each sample using the Saltelli indices matrix
	 */
	private void sample() {
		for (int i = 0; i < _sample; i++) {
			int j = 0;
			for (String param : parameters.keySet()) {
				roll(param, saltelli[i][j], problem.get(param));
				j++;
			}
		}
	}

	/**
	 * Build the string that contains the input and output for each sample
	 *
	 * @return the string
	 */
	private String buildSimulationCsv() {
		StringBuilder sb = new StringBuilder();
		String sep = ",";
		int j = 0;
		for (String param : parameters.keySet()) { sb.append(param).append(sep); }
		for (String output : output_names) {
			j++;
			sb.append(output).append(j == output_names.size() ? Strings.LN : sep);
		}

		for (int i = 0; i < _sample; i++) {
			j = 0;
			for (String param : parameters.keySet()) { sb.append(parameters.get(param).get(i)).append(sep); }
			for (String output : output_names) {
				j++;
				sb.append(outputs.get(output).get(i)).append(j == output_names.size() ? Strings.LN : sep);
			}
		}

		return sb.toString();
	}

	/**
	 * Build the string that contains the Saltelli indices matrix
	 *
	 * @return the string
	 */
	private String buildSaltelliReport() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this._sample; i++) {
			for (int j = 0; j < parameters.size(); j++) {
				sb.append(saltelli[i][j]);
				if (j != parameters.size() - 1) { sb.append(", "); }
			}
			if (i != this._sample - 1) { sb.append(System.lineSeparator()); }
		}
		return sb.toString();
	}

	/**
	 * Read a .csv file which format is : _______________________________________ |val_11, val_12, ..., val_1D, | . | .
	 * | . |val_N1, val_N2, ..., val_ND |______________________________________
	 *
	 * and return the corresponding saltelli sample.
	 *
	 * @param scope
	 * @param File
	 *            : .csv file
	 * @return a saltelli matrix of size N x D * N the number of sample points * D the dimension of each sample point
	 *         (number of parameters)
	 */
	private void parseSaltelli(final File file) {
		saltelli = new double[this._sample][parameters.size()];

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;

			// Parse rest of the file with values of the parameters
			int sample = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				for (int i = 0; i < values.length; i++) { saltelli[sample][i] = Double.parseDouble(values[i]); }
				sample++;
			}

			br.close();
		} catch (IOException e) {
			throw GamaRuntimeException.error("File " + file.toString() + "not found", scope);
		} catch (IndexOutOfBoundsException e) {
			throw GamaRuntimeException.error(
					"Format of the provided saltelli file doesn't match to the number of sample or number of variable of the experiment",
					scope);
		} catch (Exception e) {
			throw GamaRuntimeException.error(e.toString(), scope);
		}
	}

	/**
	 * Roll the value of the parameter using the corresponding Saltelli indice
	 *
	 * @param param
	 *            : the name of the parameter
	 * @param saltelli
	 *            : the value of the Saltelli indice
	 * @param info
	 *            : a list containing <br>
	 *            - the min and max value for int / double <br>
	 *            - true and false for boolean <br>
	 *            - the possible values for discrete variables <br>
	 */
	private void roll(final String param, final Double saltelli, final List<Object> info) {
		Object val = null;
		// Double
		if (info.stream().allMatch(p -> p instanceof Double)) {
			Double min = (Double) info.get(0);
			Double max = (Double) info.get(1);
			val = min + saltelli * (max - min);
		}
		// Intege
		else if (info.stream().allMatch(p -> p instanceof Integer)) {
			int min = (int) info.get(0);
			int max = (int) info.get(1);
			val = (int) Math.floor(min + saltelli * (max - min));
		}
		// Boolean
		else if (info.stream().allMatch(p -> p instanceof Boolean)) {
			val = saltelli > 0.5;
		}
		// Discrete variable
		else if (info.size() > 2) {
			int n = (int) Math.floor(saltelli * info.size());
			val = info.get(n);
		} else
			throw GamaRuntimeException.error("Uknown type for " + param + " : " + info.toString(), scope);
		parameters.get(param).add(val);
	}

	// ------------------------------------------------------------------- //
	// COPY PAST FROM MOEAFRAMEWORK //
	// ------------------------------------------------------------------- //

	/**
	 * Output from the original parameters.
	 */
	private double[] A;

	/**
	 * Output from the resampled parameters.
	 */
	private double[] B;

	/**
	 * Output from the original samples where the j-th parameter is replaced by the corresponding resampled parameter.
	 */
	private double[][] C_A;

	/**
	 * Output from the resampled samples where the j-th parameter is replaced by the corresponding original parameter.
	 */
	private double[][] C_B;

	/**
	 * Returns the first-order confidence interval of the i-th parameter. The arguments to this method mirror the
	 * arguments to {@link #computeFirstOrder}.
	 *
	 * @param a0
	 *            the output from the first independent samples
	 * @param a1
	 *            the output from the samples produced by swapping the i-th parameter in the first independent samples
	 *            with the i-th parameter from the second independent samples
	 * @param a2
	 *            the output from the second independent samples
	 * @param nsample
	 *            the number of samples
	 * @param nresample
	 *            the number of resamples used when calculating the confidence interval
	 * @return the first-order confidence interval of the i-th parameter
	 */
	private double computeFirstOrderConfidence(final double[] a0, final double[] a1, final double[] a2,
			final int nsample, final int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = scope.getRandom().getGenerator().nextInt(nsample);

				b0[j] = a0[index];
				b1[j] = a1[index];
				b2[j] = a2[index];
			}

			s[i] = computeFirstOrder(b0, b1, b2, nsample);
		}

		double ss = Arrays.stream(s).sum() / nresample;
		double sss = 0.0;

		for (int i = 0; i < nresample; i++) { sss += Math.pow(s[i] - ss, 2.0); }

		return 1.96 * Math.sqrt(sss / (nresample - 1));
	}

	/**
	 * Returns the first-order sensitivity of the i-th parameter. Note how the contents of the array {@code a1} specify
	 * the parameter being analyzed.
	 *
	 * @param a0
	 *            the output from the first independent samples
	 * @param a1
	 *            the output from the samples produced by swapping the i-th parameter in the first independent samples
	 *            with the i-th parameter from the second independent samples
	 * @param a2
	 *            the output from the second independent samples
	 * @param nsample
	 *            the number of samples
	 * @return the first-order sensitivity of the i-th parameter
	 */
	private double computeFirstOrder(final double[] a0, final double[] a1, final double[] a2, final int nsample) {
		double c = 0.0;
		for (int i = 0; i < nsample; i++) { c += a0[i]; }
		c /= nsample;

		double tmp1 = 0.0;
		double tmp2 = 0.0;
		double tmp3 = 0.0;
		double EY2 = 0.0;

		for (int i = 0; i < nsample; i++) {
			EY2 += (a0[i] - c) * (a2[i] - c);
			tmp1 += (a2[i] - c) * (a2[i] - c);
			tmp2 += a2[i] - c;
			tmp3 += (a1[i] - c) * (a2[i] - c);
		}

		EY2 /= nsample;

		double V = tmp1 / (nsample - 1) - Math.pow(tmp2 / nsample, 2.0);
		double U = tmp3 / (nsample - 1);

		return (U - EY2) / V;
	}

	/**
	 * Returns the total-order sensitivity of the i-th parameter. Note how the contents of the array {@code a1} specify
	 * the parameter being analyzed.
	 *
	 * @param a0
	 *            the output from the first independent samples
	 * @param a1
	 *            the output from the samples produced by swapping the i-th parameter in the first independent samples
	 *            with the i-th parameter from the second independent samples
	 * @param a2
	 *            the output from the second independent samples
	 * @param nsample
	 *            the number of samples
	 * @return the total-order sensitivity of the i-th parameter
	 */
	private double computeTotalOrder(final double[] a0, final double[] a1, final double[] a2, final int nsample) {
		double c = 0.0;

		for (int i = 0; i < nsample; i++) { c += a0[i]; }

		c /= nsample;

		double tmp1 = 0.0;
		double tmp2 = 0.0;
		double tmp3 = 0.0;

		for (int i = 0; i < nsample; i++) {
			tmp1 += (a0[i] - c) * (a0[i] - c);
			tmp2 += (a0[i] - c) * (a1[i] - c);
			tmp3 += a0[i] - c;
		}

		double EY2 = Math.pow(tmp3 / nsample, 2.0);
		double V = tmp1 / (nsample - 1) - EY2;
		double U = tmp2 / (nsample - 1);

		return 1.0 - (U - EY2) / V;
	}

	/**
	 * Returns the total-order confidence interval of the i-th parameter. The arguments to this method mirror the
	 * arguments to {@link #computeTotalOrder}.
	 *
	 * @param a0
	 *            the output from the first independent samples
	 * @param a1
	 *            the output from the samples produced by swapping the i-th parameter in the first independent samples
	 *            with the i-th parameter from the second independent samples
	 * @param a2
	 *            the output from the second independent samples
	 * @param nsample
	 *            the number of samples
	 * @param nresample
	 *            the number of resamples used when calculating the confidence interval
	 * @return the total-order confidence interval of the i-th parameter
	 */
	private double computeTotalOrderConfidence(final double[] a0, final double[] a1, final double[] a2,
			final int nsample, final int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = scope.getRandom().getGenerator().nextInt(nsample);

				b0[j] = a0[index];
				b1[j] = a1[index];
				b2[j] = a2[index];
			}

			s[i] = computeTotalOrder(b0, b1, b2, nsample);
		}

		double ss = Arrays.stream(s).sum() / nresample;
		double sss = 0.0;

		for (int i = 0; i < nresample; i++) { sss += Math.pow(s[i] - ss, 2.0); }

		return 1.96 * Math.sqrt(sss / (nresample - 1));
	}

	/**
	 * Returns the second-order sensitivity of the i-th and j-th parameters. Note how the contents of the arrays
	 * {@code a1}, {@code a2}, and {@code a3} specify the two parameters being analyzed.
	 *
	 * @param a0
	 *            the output from the first independent samples
	 * @param a1
	 *            the output from the samples produced by swapping the i-th parameter in the second independent samples
	 *            with the i-th parameter from the first independent samples
	 * @param a2
	 *            the output from the samples produced by swapping the j-th parameter in the first independent samples
	 *            with the j-th parameter from the second independent samples
	 * @param a3
	 *            the output from the samples produced by swapping the i-th parameter in the first independent samples
	 *            with the i-th parameter from the second independent samples
	 * @param a4
	 *            the output from the second independent samples
	 * @param nsample
	 *            the number of samples
	 * @param nresample
	 *            the number of resamples used when calculating the confidence interval
	 * @return the second-order sensitivity of the i-th and j-th parameters
	 */
	private double computeSecondOrder(final double[] a0, final double[] a1, final double[] a2, final double[] a3,
			final double[] a4, final int nsample) {
		double c = 0.0;

		for (int i = 0; i < nsample; i++) { c += a0[i]; }

		c /= nsample;

		double EY = 0.0;
		double EY2 = 0.0;
		double tmp1 = 0.0;
		double tmp2 = 0.0;
		double tmp3 = 0.0;
		double tmp4 = 0.0;
		double tmp5 = 0.0;

		for (int i = 0; i < nsample; i++) {
			EY += (a0[i] - c) * (a4[i] - c);
			EY2 += (a1[i] - c) * (a3[i] - c);
			tmp1 += (a1[i] - c) * (a1[i] - c);
			tmp2 += a1[i] - c;
			tmp3 += (a1[i] - c) * (a2[i] - c);
			tmp4 += (a2[i] - c) * (a4[i] - c);
			tmp5 += (a3[i] - c) * (a4[i] - c);
		}

		EY /= nsample;
		EY2 /= nsample;

		double V = tmp1 / (nsample - 1) - Math.pow(tmp2 / nsample, 2.0);
		double Vij = tmp3 / (nsample - 1) - EY2;
		double Vi = tmp4 / (nsample - 1) - EY;
		double Vj = tmp5 / (nsample - 1) - EY2;

		return (Vij - Vi - Vj) / V;
	}

	/**
	 * Returns the second-order confidence interval of the i-th and j-th parameters. The arguments to this method mirror
	 * the arguments to {@link #computeSecondOrder}.
	 *
	 * @param a0
	 *            the output from the first independent samples
	 * @param a1
	 *            the output from the samples produced by swapping the i-th parameter in the second independent samples
	 *            with the i-th parameter from the first independent samples
	 * @param a2
	 *            the output from the samples produced by swapping the j-th parameter in the first independent samples
	 *            with the j-th parameter from the second independent samples
	 * @param a3
	 *            the output from the samples produced by swapping the i-th parameter in the first independent samples
	 *            with the i-th parameter from the second independent samples
	 * @param a4
	 *            the output from the second independent samples
	 * @param nsample
	 *            the number of samples
	 * @return the second-order confidence interval of the i-th and j-th parameters
	 */
	@SuppressWarnings ("unused")
	private double computeSecondOrderConfidence(final double[] a0, final double[] a1, final double[] a2,
			final double[] a3, final double[] a4, final int nsample, final int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] b3 = new double[nsample];
		double[] b4 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = scope.getRandom().getGenerator().nextInt(nsample);

				b0[j] = a0[index];
				b1[j] = a1[index];
				b2[j] = a2[index];
				b3[j] = a3[index];
				b4[j] = a4[index];
			}

			s[i] = computeSecondOrder(b0, b1, b2, b3, b4, nsample);
		}

		double ss = Arrays.stream(s).sum() / nresample;
		double sss = 0.0;

		for (int i = 0; i < nresample; i++) { sss += Math.pow(s[i] - ss, 2.0); }

		return 1.96 * Math.sqrt(sss / (nresample - 1));
	}
}
