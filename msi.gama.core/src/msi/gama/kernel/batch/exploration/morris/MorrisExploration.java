/*******************************************************************************************************
 *
 * MorrisExploration.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration.morris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.batch.exploration.AExplorationAlgorithm;
import msi.gama.kernel.batch.exploration.sampling.MorrisSampling;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;

/**
 *
 * @author tomroy
 *
 */
@symbol (
		name = IKeyword.MORRIS,
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of the method. For internal use only")),
				@facet (
						name = MorrisExploration.SAMPLE_SIZE,
						type = IType.ID,
						optional = false,
						doc = @doc ("The size of the sample for Morris samples")),
				@facet (
						name = MorrisExploration.NB_LEVELS,
						type = IType.ID,
						optional = false,
						doc = @doc ("Number of level for the Morris method, can't be 1")),
				@facet (
						name = IKeyword.BATCH_VAR_OUTPUTS,
						type = IType.LIST,
						of = IType.STRING,
						optional = false,
						doc = @doc ("The list of output variables to analyze through morris method")),
				@facet (
						name = IKeyword.BATCH_REPORT,
						type = IType.STRING,
						optional = true,
						doc = @doc ("The path to the file where the Morris report will be written")),
				@facet (
						name = MorrisExploration.PARAMETER_CSV_PATH,
						type = IType.STRING,
						optional = true,
						doc = @doc ("The path of morris sample .csv file. If don't use, automatic morris sampling will be perform and saved in the corresponding file")),
				@facet (
						name = IKeyword.BATCH_OUTPUT,
						type = IType.STRING,
						optional = true,
						doc = @doc ("The path to the file where the automatic batch report will be written")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This algorithm runs a Morris exploration - it has been built upon the SILAB librairy - disabled the repeat facet of the experiment",
		usages = { @usage (
				value = "For example: ",
				examples = { @example (
						value = "method morris sample_size:100 nb_levels:4 outputs:['my_var'] report:'../path/to/report.txt;",
						isExecutable = false) }) })

public class MorrisExploration extends AExplorationAlgorithm {
	/** The Constant SAMPLE_SIZE */
	protected static final String SAMPLE_SIZE = "sample";

	/** The Constant NB_LEVELS */
	protected static final String NB_LEVELS = "levels";

	/** The Constant PARAMETER_CSV_PATH. */
	protected static final String PARAMETER_CSV_PATH = "csv";

	/** The parameters */
	protected List<Batch> parameters;

	/** The outputs */
	protected IList<String> outputs;

	/** The current parameters space. */
	/* The parameter space defined by the Morris sampling method */
	protected List<ParametersSet> solutions;

	/** The res outputs. */
	/* All the outputs for each simulation */
	protected IMap<ParametersSet, Map<String, List<Object>>> res_outputs;

	/** The Parameters names. */
	protected List<String> ParametersNames;

	/** The sample. */
	private int sample;

	/** The nb levels. */
	private int nb_levels;

	/** The My samples. */
	private List<Map<String, Object>> MySamples;

	/**
	 * Instantiates a new morris exploration.
	 *
	 * @param desc the desc
	 */
	public MorrisExploration(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

	@SuppressWarnings ("unchecked")
	@Override
	public void explore(final IScope scope) {
		this.sample = Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		this.nb_levels = Cast.asInt(scope, getFacet(NB_LEVELS).value(scope));
		if (hasFacet(PARAMETER_CSV_PATH)) {
			IExpression path_facet = getFacet(PARAMETER_CSV_PATH);
			String path = Cast.asString(scope, path_facet.value(scope));
			String new_path = scope.getExperiment().getWorkingPath() + "/" + path;
			List<ParametersSet> solutions = this.solutions == null
					? buildParameterSetsFromCSV(scope, new_path, new ArrayList<>()) : this.solutions;
			this.solutions = solutions;
		} else {
			List<ParametersSet> solutions = buildParameterSets(scope, new ArrayList<>(), 0);
			this.solutions = solutions;
		}
		/* Disable repetitions / repeat argument */
		currentExperiment.setSeeds(new Double[1]);
		currentExperiment.setKeepSimulations(false);
		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
			res_outputs = currentExperiment.launchSimulationsWithSolution(solutions);
		} else {
			res_outputs = GamaMapFactory.create();
			for (ParametersSet sol : solutions) {
				res_outputs.put(sol, currentExperiment.launchSimulationsWithSolution(sol));
			}
		}
		Map<String, List<Double>> rebuilt_output = rebuildOutput(scope, res_outputs);
		List<String> output_names = rebuilt_output.keySet().stream().toList();
		boolean firstime = true;
		if (hasFacet(IKeyword.BATCH_REPORT)) {
			String path_to = Cast.asString(scope, getFacet(IKeyword.BATCH_REPORT).value(scope));
			String new_path = scope.getExperiment().getWorkingPath() + "/" + path_to + "/MorrisResults.txt";
			final File f = new File(new_path);
			final File parent = f.getParentFile();
			if (!parent.exists()) { parent.mkdirs(); }
			if (f.exists()) { f.delete(); }
		}
		for (int i = 0; i < rebuilt_output.size(); i++) {
			String tmp_name = output_names.get(i);
			List<Map<String, Double>> morris_coefficient =
					Morris.MorrisAggregation(nb_levels, rebuilt_output.get(tmp_name), MySamples);
			if (hasFacet(IKeyword.BATCH_REPORT)) {
				String path = Cast.asString(scope, getFacet(IKeyword.BATCH_REPORT).value(scope));
				String new_path = scope.getExperiment().getWorkingPath() + "/" + path + "/MorrisResults.txt";
				Morris.WriteAndTellResult(tmp_name, new_path, firstime, scope, morris_coefficient);
				firstime = false;
			}
		}
		/* Save the simulation values in the provided .csv file (input and corresponding output) */
		if (hasFacet(IKeyword.BATCH_OUTPUT)) {
			String path_to = Cast.asString(scope, getFacet(IKeyword.BATCH_OUTPUT).value(scope));
			final File f = new File(FileUtils.constructAbsoluteFilePath(scope, path_to, false));
			final File parent = f.getParentFile();
			if (!parent.exists()) { parent.mkdirs(); }
			if (f.exists()) { f.delete(); }
			saveSimulation(rebuilt_output, f, scope);
		}
	}

	/**
	 * Here we create samples for simulations with MorrisSampling Class
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public List<ParametersSet> buildParameterSets(final IScope scope, List<ParametersSet> sets, final int index) {
		List<Batch> params = new ArrayList(currentExperiment.getParametersToExplore());
		parameters = parameters == null ? params : parameters;
		List<String> names = new ArrayList<>();
		for (int i = 0; i < parameters.size(); i++) { names.add(parameters.get(i).getName()); }
		this.ParametersNames = names;
		outputs = Cast.asList(scope, getFacet(IKeyword.BATCH_VAR_OUTPUTS).value(scope));
		List<Object> morris_samplings = MorrisSampling.MakeMorrisSampling(nb_levels, this.sample, parameters, scope);
		this.MySamples = Cast.asList(scope, morris_samplings.get(0));
		return Cast.asList(scope, morris_samplings.get(1));
	}

	/**
	 * Convert the output of Gaml so it can be read by the Sobol class
	 *
	 * @param res_outputs
	 *            : output of simulation
	 * @return A map with <br>
	 *         K the name of the output <br>
	 *         V the value of the output
	 */
	private Map<String, List<Double>> rebuildOutput(final IScope scope,
			final IMap<ParametersSet, Map<String, List<Object>>> res_outputs) {
		Map<String, List<Double>> rebuilt_output = new HashMap<>();
		for (String output : outputs) { rebuilt_output.put(output, new ArrayList<>()); }
		for (ParametersSet sol : solutions) {
			for (String output : outputs) {
				rebuilt_output.get(output).add(Cast.asFloat(scope, res_outputs.get(sol).get(output).get(0)));
			}
		}
		return rebuilt_output;
	}

	/**
	 * Save simulation.
	 *
	 * @param rebuilt_output the rebuilt output
	 * @param file the file
	 * @param scope the scope
	 */
	private void saveSimulation(final Map<String, List<Double>> rebuilt_output, final File file, final IScope scope) {
		try {
			FileWriter fw = new FileWriter(file, false);
			fw.write(this.buildSimulationCsv(rebuilt_output));
			fw.close();
		} catch (Exception e) {
			throw GamaRuntimeException.error("File " + file.toString() + " not found", scope);
		}
	}

	/**
	 * Builds the simulation csv.
	 *
	 * @param rebuilt_output the rebuilt output
	 * @return the string
	 */
	private String buildSimulationCsv(final Map<String, List<Double>> rebuilt_output) {
		StringBuilder sb = new StringBuilder();
		String sep = ",";
		// Headers
		for (String sol : ParametersNames) { sb.append(sol).append(sep); }
		for (String output : outputs) { sb.append(output).append(sep); }

		sb.deleteCharAt(sb.length() - 1).append(Strings.LN); // new line

		// Values
		for (ParametersSet ps : res_outputs.keySet().stream().toList()) {
			for (String sol : ParametersNames) {
				sb.append(ps.get(sol)).append(sep); // inputs values
			}
			for (String output : outputs) {
				sb.append(res_outputs.get(ps).get(output)).append(sep); // outputs values
			}
			sb.deleteCharAt(sb.length() - 1).append(Strings.LN); // new line
		}
		return sb.toString();
	}

	/**
	 * Builds the parameter sets from CSV.
	 *
	 * @param scope the scope
	 * @param path the path
	 * @param sets the sets
	 * @return the list
	 */
	public List<ParametersSet> buildParameterSetsFromCSV(final IScope scope, final String path,
			final List<ParametersSet> sets) {
		List<Map<String, Object>> parameters = new ArrayList<>();
		try {
			File file = new File(path);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = " ";
			String[] tempArr;
			List<String> list_name = new ArrayList<>();
			int i = 0;
			while ((line = br.readLine()) != null) {
				tempArr = line.split(",");
				for (String tempStr : tempArr) { if (i == 0) { list_name.add(tempStr); } }
				if (i > 0) {
					Map<String, Object> temp_map = new HashMap<>();
					for (int y = 0; y < tempArr.length; y++) { temp_map.put(list_name.get(y), tempArr[y]); }
					parameters.add(temp_map);
				}
				i++;
			}
			br.close();
		} catch (IOException ioe) {
			throw GamaRuntimeException.error("File " + path + " not found", scope);
		}
		MySamples = parameters;
		// morris_analysis.ParametersNames=parameters.get(0).keySet().stream().toList();
		for (Map<String, Object> parameterSet : parameters) {
			ParametersSet p = new ParametersSet();
			for (String v : parameterSet.keySet()) {
				Object val = parameterSet.get(v);
				p.put(v, val instanceof IExpression ? ((IExpression) val).value(scope) : val);
			}
			sets.add(p);
		}
		return sets;
	}

}
