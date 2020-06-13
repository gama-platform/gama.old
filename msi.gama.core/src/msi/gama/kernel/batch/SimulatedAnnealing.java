/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.SimulatedAnnealing.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.Collections;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParameterAdapter;
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
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol (
		name = IKeyword.ANNEALING,
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH })
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
						name = SimulatedAnnealing.TEMP_END,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("final temperature")),
				@facet (
						name = SimulatedAnnealing.TEMP_DECREASE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("temperature decrease coefficient")),
				@facet (
						name = SimulatedAnnealing.TEMP_INIT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("initial temperature")),
				@facet (
						name = SimulatedAnnealing.NB_ITER,
						type = IType.INT,
						optional = true,
						doc = @doc ("number of iterations per level of temperature")),
				@facet (
						name = IKeyword.MAXIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to maximize")),
				@facet (
						name = IKeyword.MINIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to minimize")),
				@facet (
						name = IKeyword.AGGREGATION,
						type = IType.LABEL,
						optional = true,
						values = { IKeyword.MIN, IKeyword.MAX },
						doc = @doc ("the agregation method")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This algorithm is an implementation of the Simulated Annealing algorithm. See the wikipedia article and [batch161 the batch dedicated page].",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the annealing statement uses `method annealing` instead of the expected `annealing name: id` : ",
				examples = { @example (
						value = "method annealing [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method annealing temp_init: 100  temp_end: 1 temp_decrease: 0.5 nb_iter_cst_temp: 5 maximize: food_gathered;",
								isExecutable = false) }) })
public class SimulatedAnnealing extends LocalSearchAlgorithm {

	double temperatureEnd = 1;
	double tempDimCoeff = 0.5;
	double temperatureInit = 100;
	int nbIterCstTemp = 5;

	protected static final String TEMP_END = "temp_end";
	protected static final String TEMP_DECREASE = "temp_decrease";
	protected static final String TEMP_INIT = "temp_init";
	protected static final String NB_ITER = "nb_iter_cst_temp";

	public SimulatedAnnealing(final IDescription species) {
		super(species);
		initParams();
	}

	// @Override
	// public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
	// super.initializeFor(scope, agent);
	// }

	// FIXME SimulationScope is normally null at that point. Should be better called from initializeFor()
	@Override
	public void initParams(final IScope scope) {
		final IExpression tempend = getFacet(TEMP_END);
		if (tempend != null) {
			temperatureEnd = Cast.asFloat(scope, tempend.value(scope));
		}
		final IExpression tempdecrease = getFacet(TEMP_DECREASE);
		if (tempdecrease != null) {
			tempDimCoeff = Cast.asFloat(scope, tempdecrease.value(scope));
		}
		final IExpression tempinit = getFacet(TEMP_INIT);
		if (tempinit != null) {
			temperatureInit = Cast.asFloat(scope, tempinit.value(scope));
		}

		final IExpression nbIterCstT = getFacet(NB_ITER);
		if (nbIterCstT != null) {
			nbIterCstTemp = Cast.asInt(scope, nbIterCstT.value(scope));
		}
	}

	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		initializeTestedSolutions();
		setBestSolution(new ParametersSet(this.solutionInit));
		double currentFitness = currentExperiment.launchSimulationsWithSolution(getBestSolution());
		ParametersSet bestSolutionAlgo = this.solutionInit;
		testedSolutions.put(getBestSolution(), getBestFitness());
		setBestFitness(currentFitness);
		double temperature = temperatureInit;

		while (temperature > temperatureEnd) {
			final List<ParametersSet> neighbors = neighborhood.neighbor(scope, bestSolutionAlgo);
			if (neighbors.isEmpty()) {
				break;
			}
			int iter = 0;
			while (iter < nbIterCstTemp) {
				final ParametersSet neighborSol = neighbors.get(scope.getRandom().between(0, neighbors.size() - 1));
				if (neighborSol == null) {
					neighbors.removeAll(Collections.singleton(null));
					if (neighbors.isEmpty()) {
						break;
					}
					continue;
				}
				Double neighborFitness = testedSolutions.get(neighborSol);
				if (neighborFitness == null || neighborFitness == Double.MAX_VALUE) {
					neighborFitness = currentExperiment.launchSimulationsWithSolution(neighborSol);
					testedSolutions.put(neighborSol, neighborFitness);
				}

				if (isMaximize() && (neighborFitness >= currentFitness
						|| scope.getRandom().next() < Math.exp((neighborFitness - currentFitness) / temperature))
						|| !isMaximize() && (neighborFitness <= currentFitness || scope.getRandom().next() < Math
								.exp((currentFitness - neighborFitness) / temperature))) {
					bestSolutionAlgo = neighborSol;
					currentFitness = neighborFitness;
				}
				iter++;
			}
			temperature *= tempDimCoeff;
		}

		return getBestSolution();
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(new ParameterAdapter("Final temperature", IExperimentPlan.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return temperatureEnd;
			}

		});
		params.add(new ParameterAdapter("Initial temperature", IExperimentPlan.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return temperatureInit;
			}

		});
		params.add(new ParameterAdapter("Coefficient of diminution", IExperimentPlan.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return tempDimCoeff;
			}

		});
		params.add(new ParameterAdapter("Number of iterations at constant temperature",
				IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return nbIterCstTemp;
			}

		});
	}

}
