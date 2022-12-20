/*******************************************************************************************************
 *
 * Exploration.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.exploration.sampling.LatinhypercubeSampling;
import msi.gama.kernel.batch.exploration.sampling.MorrisSampling;
import msi.gama.kernel.batch.exploration.sampling.OrthogonalSampling;
import msi.gama.kernel.batch.exploration.sampling.RandomSampling;
import msi.gama.kernel.batch.exploration.sampling.SaltelliSampling;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Random;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.GamaFloatType;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.IType;

/**
 * The Class ExhaustiveSearch.
 */
@symbol (
		name = { IKeyword.EXPLORATION },
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
						type = IType.STRING,
						optional = true,
						doc = @doc ("The name of the method (among saltelli/morris/latinhypercube/orthogonal/uniform/factorial)")),
				@facet (
						name = IKeyword.FROM,
						type = IType.STRING,
						optional = true,
						doc = @doc ("a path to a file where each lines correspond to one parameter set and each colon a parameter")),
				@facet (
						name = IKeyword.WITH,
						type = IType.LIST,
						of = IType.MAP,
						optional = true,
						doc = @doc ("the list of parameter sets to explore; a parameter set is defined by a map: key: name of the variable, value: expression for the value of the variable")),
				@facet (
						name = Exploration.SAMPLE_SIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of sample required, 132 by default")),
				@facet (
						name = Exploration.SAMPLE_FACTORIAL,
						type = IType.LIST,
						of = IType.INT,
						optional = true,
						doc = @doc ("The number of sample required.")),
				@facet (
						name = Exploration.NB_LEVELS,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of levels for morris sampling, 4 by default")),
				@facet (
						name = Exploration.ITERATIONS,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of iteration for orthogonal sampling, 5 by default"))

		},
		omissible = IKeyword.NAME)
@doc (
		value = "This is the standard batch method. The exhaustive mode is defined by default when there is no method element present in the batch section. It explores all the combination of parameter values in a sequential way. You can also choose a sampling method for the exploration. See [batch161 the batch dedicated page].",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the exhaustive statement uses `method exhaustive` instead of the expected `exhaustive name: id` : ",
				examples = { @example (
						value = "method exhaustive [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "Simplest example: ",
						examples = { @example (
								value = "method exploration;",
								isExecutable = false) }),
				@usage (
						value = "Using sampling facet: ",
						examples = { @example (
								value = "method exploration sampling:latinhypercube sample:100; ",
								isExecutable = false) }),
				@usage (
						value = "Using from facet: ",
						examples = { @example (
								value = "method exploration from:\"../path/to/my/exploration/plan.csv\"; ",
								isExecutable = false) }),
				@usage (
						value = "Using with facet: ",
						examples = { @example (
								value = "method exploration with:[[\"a\"::0.5, \"b\"::10],[\"a\"::0.1, \"b\"::100]]; ",
								isExecutable = false) }) })
public class Exploration extends AExplorationAlgorithm {
	/** The Constant Method */
	public static final String METHODS = "sampling";

	/** The Constant SAMPLE_SIZE */
	public static final String SAMPLE_SIZE = "sample";

	/** The factorial sampling */
	public static final String SAMPLE_FACTORIAL = "factorial";

	/** The Constant NB_LEVELS */
	public static final String NB_LEVELS = "levels";

	/** The Constant ITERATIONS */
	public static final String ITERATIONS = "iterations";

	/** The Constant FROM_FILE. */
	public static final String FROM_FILE = "FROMFILE";

	/** The Constant FROM_LIST. */
	public static final String FROM_LIST = "FROMLIST";

	/** The default step factor. */
	private final int __default_step_factor = 10;

	/** The sample size. */
	private int sample_size = 132;

	/** The nb levels. */
	private int nb_levels = 4;

	/** The iterations. */
	private int iterations = 5;

	/** The parameters. */
	private List<Batch> parameters;

	/**
	 * Instantiates a new exhaustive search.
	 *
	 * @param desc
	 *            the desc
	 */
	public Exploration(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {

		List<Batch> params = currentExperiment.getParametersToExplore();

		parameters = parameters == null ? params : parameters;
		List<ParametersSet> sets;

		if (hasFacet(Exploration.SAMPLE_SIZE)) {
			this.sample_size = Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		}

		if (hasFacet(Exploration.ITERATIONS)) {
			this.iterations = Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		}

		if (hasFacet(Exploration.NB_LEVELS)) { this.nb_levels = Cast.asInt(scope, getFacet(NB_LEVELS).value(scope)); }

		String method = hasFacet(Exploration.METHODS) ? Cast.asString(scope, getFacet(METHODS).value(scope)) : "";

		if (hasFacet(IKeyword.FROM)) {
			method = FROM_FILE;
		} else if (hasFacet(IKeyword.WITH)) { method = FROM_LIST; }

		sets = switch (method) {
			case IKeyword.MORRIS -> MorrisSampling.MakeMorrisSamplingOnly(nb_levels, sample_size, parameters, scope);
			case IKeyword.SALTELLI -> SaltelliSampling.MakeSaltelliSampling(scope, sample_size, parameters);
			case IKeyword.LHS -> LatinhypercubeSampling.LatinHypercubeSamples(sample_size, parameters,
					scope.getRandom().getGenerator(), scope);
			case IKeyword.ORTHOGONAL -> OrthogonalSampling.OrthogonalSamples(sample_size, iterations, parameters,
					scope.getRandom().getGenerator(), scope);
			case IKeyword.UNIFORM -> RandomSampling.UniformSampling(scope, sample_size, parameters);
			case IKeyword.FACTORIAL -> {
				List<ParametersSet> ps = null;
				if (hasFacet(Exploration.SAMPLE_FACTORIAL)) {
					@SuppressWarnings ("unchecked") int[] factors =
							Cast.asList(scope, getFacet(Exploration.SAMPLE_FACTORIAL).value(scope)).stream()
									.mapToInt(o -> Integer.parseInt(o.toString())).toArray();
					ps = RandomSampling.FactorialUniformSampling(scope, factors, params);
				} else {
					ps = RandomSampling.FactorialUniformSampling(scope, sample_size, params);
				}
				yield ps;
			}

			case FROM_LIST -> buildParameterFromMap(scope, new ArrayList<>(), 0);
			case FROM_FILE -> buildParametersFromCSV(scope, Cast.asString(scope, getFacet(IKeyword.FROM).value(scope)),
					new ArrayList<>());
			default -> buildParameterSets(scope, new ArrayList<>(), 0);
		};
		if (sets.isEmpty()) { sets.add(new ParametersSet()); }

		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
			currentExperiment.launchSimulationsWithSolution(sets);
		} else {
			for (ParametersSet sol : sets) { currentExperiment.launchSimulationsWithSolution(sol); }
		}

	}

	@Override
	public List<ParametersSet> buildParameterSets(final IScope scope, final List<ParametersSet> sets, final int index) {

		if (sets == null) throw GamaRuntimeException.error("Cannot build a sample with empty parameter set", scope);
		final List<Batch> variables = currentExperiment.getParametersToExplore();
		List<ParametersSet> sets2 = new ArrayList<>();
		if (variables.isEmpty()) return sets2;
		if (sets.isEmpty()) { sets.add(new ParametersSet()); }
		final IParameter.Batch var = variables.get(index);
		for (ParametersSet solution : sets) {
			@SuppressWarnings ("rawtypes") List vals =
					var.getAmongValue(scope) != null ? var.getAmongValue(scope) : getParameterSwip(scope, var);
			for (final Object val : vals) {
				ParametersSet ps = new ParametersSet(solution);
				ps.put(var.getName(), val);
				sets2.add(ps);
			}
		}
		if (index == variables.size() - 1) return sets2;
		return buildParameterSets(scope, sets2, index + 1);
	}

	/**
	 * Build a parameter set (a sample of the parameter space) based on explicit point given either with a gaml map or
	 * written in a file
	 *
	 * @param scope
	 * @param sets
	 * @param index
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private List<ParametersSet> buildParameterFromMap(final IScope scope, final List<ParametersSet> sets,
			final int index) {
		IExpression psexp = getFacet(IKeyword.WITH);
		List<Map<String, Object>> parameterSets = Cast.asList(scope, psexp.value(scope));

		for (Map<String, Object> parameterSet : parameterSets) {
			ParametersSet p = new ParametersSet();
			for (String v : parameterSet.keySet()) {
				Object val = parameterSet.get(v);
				p.put(v, val instanceof IExpression ? ((IExpression) val).value(scope) : val);
			}
			sets.add(p);
		}
		return sets;

	}

	/**
	 * Create a List of Parameters Set with values contains in a CSV file
	 *
	 * @param scope
	 * @param path
	 * @param sets
	 * @return
	 */
	private List<ParametersSet> buildParametersFromCSV(final IScope scope, final String path,
			final List<ParametersSet> sets) {
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
						Map<String, Object> temp_map = new HashMap<>();
						for (int y = 0; y < tempArr.length; y++) { temp_map.put(list_name.get(y), tempArr[y]); }
						parameters.add(temp_map);
					}
					i++;
				}
			}
		} catch (FileNotFoundException nfe) {
			throw GamaRuntimeException.error("CSV file not found: " + path, scope);
		} catch (IOException ioe) {
			throw GamaRuntimeException.error("Error during the reading of the CSV file", scope);
		}

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

	// ##################### Methods to determine possible values based on exhaustive ######################

	/**
	 * Return all the possible value of a parameter based on
	 *
	 * @param scope
	 * @param var
	 * @return
	 */
	private List<Object> getParameterSwip(final IScope scope, final Batch var) {
		List<Object> res = new ArrayList<>();
		switch (var.getType().id()) {
			case IType.INT:
				int minValue = Cast.asInt(scope, var.getMinValue(scope));
				int maxValue = Cast.asInt(scope, var.getMaxValue(scope));
				double stepValue = 1;
				if (hasFacet(IKeyword.STEP)) {
					stepValue = Cast.asInt(scope, var.getStepValue(scope));
				} else if (maxValue - minValue > __default_step_factor) {
					stepValue = (maxValue - minValue) / __default_step_factor;
				}

				while (minValue <= maxValue) {
					res.add(minValue);
					minValue = minValue + (int) stepValue + (Random.opFlip(scope, stepValue - (int) stepValue) ? 1 : 0);
				}
				break;
			case IType.FLOAT:
				double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
				double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
				double stepFloatValue = 0.1;
				if (hasFacet(IKeyword.STEP)) {
					stepFloatValue = Cast.asFloat(scope, var.getStepValue(scope));
				} else {
					stepFloatValue = (maxFloatValue - floatValue) / __default_step_factor;
				}

				while (floatValue <= maxFloatValue) {
					res.add(floatValue);
					floatValue = floatValue + stepFloatValue;
				}
				break;
			case IType.DATE:
				GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
				GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
				while (dateValue.isSmallerThan(maxDateValue, false)) {
					res.add(dateValue);
					dateValue = dateValue.plus(Cast.asFloat(scope, var.getStepValue(scope)), ChronoUnit.SECONDS);
				}
				break;
			case IType.POINT:
				GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
				GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
				GamaPoint increment = new GamaPoint();
				if (hasFacet(IKeyword.STEP)) {
					increment = GamaPointType.staticCast(scope, var.getStepValue(scope), true);
					if (increment == null) {
						Double d = GamaFloatType.staticCast(scope, var.getStepValue(scope), null, false);
						if (d == null) {
							GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.error("Cannot retrieve steps "
									+ var.getStepValue(scope) + " of paramter " + var.getName(), scope), true);
						} else {
							increment = new GamaPoint(d, d, d);
						}
					}
				} else {

				}
				while (pointValue.smallerThanOrEqualTo(maxPointValue)) {
					res.add(pointValue);
					pointValue = pointValue.plus(Cast.asPoint(scope, increment));
				}
				break;
			default:
				double varValue = Cast.asFloat(scope, var.getMinValue(scope));
				double maxVarValue = Cast.asFloat(scope, var.getMaxValue(scope));
				double floatcrement = 1;
				if (hasFacet(IKeyword.STEP)) {
					floatcrement = Cast.asFloat(scope, var.getStepValue(scope));
				} else {
					floatcrement = (maxVarValue - varValue) / __default_step_factor;
				}
				while (varValue <= Cast.asFloat(scope, var.getMaxValue(scope))) {

					if (var.getType().id() == IType.INT) {
						res.add((int) varValue);
					} else if (var.getType().id() == IType.FLOAT) {
						res.add(varValue);
					} else {
						continue;
					}

					varValue = varValue + floatcrement;
				}
		}
		return res;
	}

}
