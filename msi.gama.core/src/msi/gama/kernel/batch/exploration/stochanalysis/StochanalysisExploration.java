/*******************************************************************************************************
 *
 * StochanalysisExploration.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration.stochanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.exploration.AExplorationAlgorithm;
import msi.gama.kernel.batch.exploration.Exploration;
import msi.gama.kernel.batch.exploration.sampling.LatinhypercubeSampling;
import msi.gama.kernel.batch.exploration.sampling.OrthogonalSampling;
import msi.gama.kernel.batch.exploration.sampling.RandomSampling;
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
import msi.gaml.types.IType;

/**
 * The Class StochanalysisExploration.
 */
@symbol (
		name = { IKeyword.STO },
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
						optional = true,
						doc = @doc ("The sampling method to build parameters sets. Available methods are: "
								+ IKeyword.LHS + ", " + IKeyword.ORTHOGONAL)),
				@facet (
						name = IKeyword.BATCH_VAR_OUTPUTS,
						type = IType.LIST,
						optional = false,
						doc = @doc ("The list of output variables to analyse")),
				@facet (
						name = Exploration.SAMPLE_SIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of sample required , 10 by default")),
				@facet (
						name = Exploration.SAMPLE_FACTORIAL,
						type = IType.LIST,
						of = IType.INT,
						optional = true,
						doc = @doc ("The number of sample required.")),
				@facet (
						name = IKeyword.BATCH_OUTPUT,
						type = IType.STRING,
						optional = true,
						doc = @doc ("The path to the file where the automatic batch report will be written")),
				@facet (
						name = Exploration.NB_LEVELS,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of level required, 4 by default")),
				@facet (
						name = StochanalysisExploration.THRESHOLD,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("The threshold for the stochanalysis, will perform an automatic analysis by default"))

		},
		omissible = IKeyword.NAME)
@doc (
		value = "This algorithm runs an exploration with a given sampling to compute a Stochasticity Analysis",
		usages = { @usage (
				value = "For example: ",
				examples = { @example (
						value = "method stochanalyse sampling:'latinhypercube' outputs:['my_var'] replicat:10 results:'../path/to/report/file.txt'; ",
						isExecutable = false) }) })
public class StochanalysisExploration extends AExplorationAlgorithm {

	/** The Constant THRESHOLD. */
	public static final String THRESHOLD = "threshold";

	/** The method. */
	public String method = IKeyword.LHS;

	/** The sample size. */
	public int sample_size = 10;

	/** The threshold. */
	public double threshold = -1;
	/** Theoretical inputs */
	private List<Batch> parameters;
	/** Theoretical outputs */
	private IList<Object> outputs;
	/** Actual input / output map */
	protected IMap<ParametersSet, Map<String, List<Object>>> res_outputs;

	/**
	 * Instantiates a new stochanalysis exploration.
	 *
	 * @param desc
	 *            the desc
	 */
	public StochanalysisExploration(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {

		List<Batch> params = new ArrayList<>(currentExperiment.getParametersToExplore());

		parameters = parameters == null ? params : parameters;

		List<ParametersSet> sets;

		if (hasFacet(Exploration.SAMPLE_SIZE)) {
			this.sample_size = Cast.asInt(scope, getFacet(Exploration.SAMPLE_SIZE).value(scope));
		}
		if (hasFacet(StochanalysisExploration.THRESHOLD)) {
			this.threshold = Cast.asFloat(scope, getFacet(StochanalysisExploration.THRESHOLD).value(scope));
		}
		if (hasFacet(Exploration.METHODS)) {
			method = Cast.asString(scope, getFacet(Exploration.METHODS).value(scope));
		}
		sets = switch (method) {
			case IKeyword.LHS -> LatinhypercubeSampling.LatinHypercubeSamples(sample_size, parameters,
					scope.getRandom().getGenerator(), scope);
			case IKeyword.ORTHOGONAL -> {
				int iterations = hasFacet(Exploration.ITERATIONS)
						? Cast.asInt(scope, getFacet(Exploration.ITERATIONS).value(scope)) : 5;
				yield OrthogonalSampling.OrthogonalSamples(sample_size, iterations, parameters,
						scope.getRandom().getGenerator(), scope);
			}
			case IKeyword.FACTORIAL -> {
				List<ParametersSet> ps = null;
				if (hasFacet(Exploration.SAMPLE_FACTORIAL)) {
					@SuppressWarnings("unchecked")
					int[] factors = Cast.asList(scope, getFacet(Exploration.SAMPLE_FACTORIAL).value(scope))
						.stream().mapToInt(o -> Integer.valueOf(o.toString()))
						.toArray();
					ps = RandomSampling.FactorialUniformSampling(scope, factors, params);
				} else {
					ps = RandomSampling.FactorialUniformSampling(scope, sample_size, params);
				}
				
				yield ps;
			}
			default -> RandomSampling.UniformSampling(scope, sample_size, params);
		};

		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
			res_outputs = currentExperiment.launchSimulationsWithSolution(sets);
		} else {
			res_outputs = GamaMapFactory.create();
			for (ParametersSet sol : sets) {
				res_outputs.put(sol, currentExperiment.launchSimulationsWithSolution(sol));
			}
		}

		IExpression outputFacet = getFacet(IKeyword.BATCH_VAR_OUTPUTS);
		outputs = Cast.asList(scope, scope.evaluate(outputFacet, currentExperiment).getValue());
		int res = 0;
		Map<String, Map<Double, List<Object>>> MapOutput = new LinkedHashMap<>();
		for (Object out : outputs) {
			Map<Double, List<Object>> res_val = new HashMap<>(Map.of(0.05, Collections.emptyList(), 0.01,
					Collections.emptyList(), 0.001, Collections.emptyList(), 90.0, Collections.emptyList(), 95.0,
					Collections.emptyList(), 99.0, Collections.emptyList(), -1.0, Collections.emptyList()));
			IMap<ParametersSet, List<Object>> sp = GamaMapFactory.create();
			for (ParametersSet ps : res_outputs.keySet()) { sp.put(ps, res_outputs.get(ps).get(out.toString())); }
			if (threshold == -1) {
				List<Double> keys = res_val.keySet().stream().toList();
				for (Double thresh : keys) {
					if (res_val.get(thresh).isEmpty()) {
						res_val.replace(thresh, Stochanalysis.StochasticityAnalysis(sp, thresh, scope));
					}
				}
				MapOutput.put(out.toString(), res_val);
			} else {
				res = Cast.asInt(scope, Stochanalysis.StochasticityAnalysis(sp, threshold, scope).get(0));
			}
		}
		if (threshold == -1) {
			if (hasFacet(IKeyword.BATCH_OUTPUT)) {
				String path = Cast.asString(scope, getFacet(IKeyword.BATCH_OUTPUT).value(scope));
				String new_path = scope.getExperiment().getWorkingPath() + "/" + path;
				Stochanalysis.WriteAndTellResultList(new_path, MapOutput, scope);
			}
		} else {
			res = res / outputs.size();
			if (hasFacet(IKeyword.BATCH_OUTPUT)) {
				String path = Cast.asString(scope, getFacet(IKeyword.BATCH_OUTPUT).value(scope));
				String new_path = scope.getExperiment().getWorkingPath() + "/" + path;
				Stochanalysis.WriteAndTellResult(new_path, res, scope);
			}
		}
	}

	@Override
	public List<ParametersSet> buildParameterSets(final IScope scope, final List<ParametersSet> sets, final int index) {
		
		return null;
	}
}
