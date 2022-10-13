package msi.gama.kernel.batch.exploration.betadistribution;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.batch.exploration.AExplorationAlgorithm;
import msi.gama.kernel.batch.exploration.Exploration;
import msi.gama.kernel.batch.exploration.sampling.LatinhypercubeSampling;
import msi.gama.kernel.batch.exploration.sampling.MorrisSampling;
import msi.gama.kernel.batch.exploration.sampling.OrthogonalSampling;
import msi.gama.kernel.batch.exploration.sampling.RandomSampling;
import msi.gama.kernel.batch.exploration.sampling.SaltelliSampling;
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
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;

/**
 *
 *
 * @author kevinchapuis
 *
 *         Coefficient derived from the work below:
 *
 *         E. Borgonovo, M. Pangallo, J. Rivkin, L. Rizzo, and N. Siggelkow, “Sensitivity analysis of agent-based
 *         models: a new protocol,” Comput. Math. Organ. Theory, vol. 28, no. 1, pp. 52–94, Mar. 2022, doi:
 *         10.1007/s10588-021-09358-5.
 *
 */
@symbol (
		name = IKeyword.BETAD,
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
						name = Exploration.METHODS,
						type = IType.ID,
						optional = false,
						doc = @doc ("The sampling method to build parameters sets - default is uniform")),
				@facet (
						name = IKeyword.BATCH_VAR_OUTPUTS,
						type = IType.LIST,
						of = IType.STRING,
						optional = false,
						doc = @doc ("The list of output variables to analyse")),
				@facet (
						name = Exploration.SAMPLE_SIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of sample required.")),
				@facet (
						name = IKeyword.BATCH_OUTPUT,
						type = IType.STRING,
						optional = true,
						doc = @doc ("The path to the file where the automatic batch report will be written")),
				@facet (
						name = IKeyword.BATCH_REPORT,
						type = IType.STRING,
						optional = true,
						doc = @doc ("The path to the file where the Betad report will be written")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This algorithm runs an exploration with a given sampling to compute BetadKu - see doi: 10.1007/s10588-021-09358-5",
		usages = { @usage (
				value = "For example: ",
				examples = { @example (
						value = "method sobol sample_size:100 outputs:['my_var'] report:'../path/to/report/file.txt'; ",
						isExecutable = false) }) })
public class BetaExploration extends AExplorationAlgorithm {

	/** Theoretical inputs */
	private List<Batch> parameters;
	/** Theoretical outputs */
	private IList<String> outputs;
	/** Actual input / output map */
	protected IMap<ParametersSet, Map<String, List<Object>>> res_outputs;

	public BetaExploration(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

	@SuppressWarnings ("unchecked")
	@Override
	public void explore(final IScope scope) {
		
		// == Parameters ==
		
		List<Batch> params = currentExperiment.getParametersToExplore().stream()
				.filter(p -> p.getMinValue(scope) != null && p.getMaxValue(scope) != null).map(p -> p)
				.collect(Collectors.toList());

		parameters = parameters == null ? params : parameters;
		List<ParametersSet> sets;

		int sample_size = (int) Math.round(Math.pow(params.size(), 2) * 2);
		if (hasFacet(Exploration.SAMPLE_SIZE)) {
			sample_size = Cast.asInt(scope, getFacet(Exploration.SAMPLE_SIZE).value(scope));
		}
		
		// == Build sample of parameter inputs ==
		
		String method = Cast.asString(scope, getFacet(Exploration.METHODS).value(scope));
		sets = switch (method) {
			case IKeyword.MORRIS -> {
				int nbl = hasFacet(Exploration.NB_LEVELS)
						? Cast.asInt(scope, getFacet(Exploration.NB_LEVELS).value(scope)) : 4;
				yield Cast.asList(scope, MorrisSampling.MakeMorrisSampling(nbl, sample_size, parameters, scope).get(1));
			}
			case IKeyword.SALTELLI -> SaltelliSampling.MakeSaltelliSampling(scope, sample_size, parameters);
			case IKeyword.LHS -> LatinhypercubeSampling.LatinHypercubeSamples(sample_size, parameters,
									scope.getRandom().getGenerator(), scope);
			case IKeyword.ORTHOGONAL -> {
				int iterations = hasFacet(Exploration.ITERATIONS)
						? Cast.asInt(scope, getFacet(Exploration.SAMPLE_SIZE).value(scope)) : 5;
				yield OrthogonalSampling.OrthogonalSamples(sample_size, iterations, parameters,
										scope.getRandom().getGenerator(), scope);
			}
			default -> RandomSampling.UniformSampling(scope, sample_size, parameters);
		};

		// == Launch simulations ==
		currentExperiment.setSeeds(new Double[1]);
		currentExperiment.setKeepSimulations(false);
		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
			res_outputs = currentExperiment.launchSimulationsWithSolution(sets);
		} else {
			res_outputs = GamaMapFactory.create();
			for (ParametersSet sol : sets) {
				res_outputs.put(sol, currentExperiment.launchSimulationsWithSolution(sol));
			}
		}

		outputs = Cast.asList(scope, getFacet(IKeyword.BATCH_VAR_OUTPUTS).value(scope));

		Map<String, Map<Batch, Double>> res = new HashMap<>();
		for (String out : outputs) {
			IMap<ParametersSet, List<Object>> sp = GamaMapFactory.create();
			for (ParametersSet ps : res_outputs.keySet()) { sp.put(ps, res_outputs.get(ps).get(out)); }
			Betadistribution bs = new Betadistribution(sp, parameters, out);
			res.put(out, bs.evaluate());
		}

		if (hasFacet(IKeyword.BATCH_REPORT)) {
			String path_to = Cast.asString(scope, getFacet(IKeyword.BATCH_REPORT).value(scope));
			final File f = new File(FileUtils.constructAbsoluteFilePath(scope, path_to, false));
			final File parent = f.getParentFile();
			if (!parent.exists()) { parent.mkdirs(); }
			if (f.exists()) { f.delete(); }
			try {
				FileWriter fw = new FileWriter(f, false);
				fw.write(this.buildReportString(res));
				fw.close();
			} catch (Exception e) {
				throw GamaRuntimeException.error("File " + f.toString() + " not found", scope);
			}
		}

	}

	@Override
	public List<ParametersSet> buildParameterSets(final IScope scope, final List<ParametersSet> sets, final int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildReportString(final Map<String, Map<Batch, Double>> res) {
		StringBuilder sb = new StringBuilder();
		String sep = "; ";
		sb.append("BETA b Kuiper based estimator :\n");
		sb.append("##############################\n");
		sb.append("inputs" + sep + String.join(sep, outputs)).append(Strings.LN);
		String line = "";
		for (Batch param : parameters) {
			line = param.getName();
			for (String output_name : outputs) { line = line + sep + res.get(output_name).get(param).toString(); }
			sb.append(line).append(Strings.LN);
		}
		return sb.toString();
	}

}
