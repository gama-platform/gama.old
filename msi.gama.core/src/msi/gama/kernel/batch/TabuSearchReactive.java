/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.TabuSearchReactive.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		name = IKeyword.REACTIVE_TABU,
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
				internal = true),
				@facet (
						name = TabuSearchReactive.ITER_MAX,
						type = IType.INT,
						optional = true,
						doc = @doc ("number of iterations")),
				@facet (
						name = TabuSearchReactive.LIST_SIZE_INIT,
						type = IType.INT,
						optional = true,
						doc = @doc ("initial size of the tabu list")),
				@facet (
						name = TabuSearchReactive.LIST_SIZE_MAX,
						type = IType.INT,
						optional = true,
						doc = @doc ("maximal size of the tabu list")),
				@facet (
						name = TabuSearchReactive.LIST_SIZE_MIN,
						type = IType.INT,
						optional = true,
						doc = @doc ("minimal size of the tabu list")),
				@facet (
						name = TabuSearchReactive.NB_TESTS_MAX,
						type = IType.INT,
						optional = true,
						doc = @doc ("number of movements without collision before shortening the tabu list")),
				@facet (
						name = TabuSearchReactive.CYCLE_SIZE_MAX,
						type = IType.INT,
						optional = true,
						doc = @doc ("minimal size of the considered cycles")),
				@facet (
						name = TabuSearchReactive.CYCLE_SIZE_MIN,
						type = IType.INT,
						optional = true,
						doc = @doc ("maximal size of the considered cycles")),
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
		value = "This algorithm is a simple implementation of the Reactive Tabu Search algorithm ((Battiti et al., 1993)). This Reactive Tabu Search is an enhance version of the Tabu search. It adds two new elements to the classic Tabu Search. The first one concerns the size of the tabu list: in the Reactive Tabu Search, this one is not constant anymore but it dynamically evolves according to the context. Thus, when the exploration process visits too often the same solutions, the tabu list is extended in order to favor the diversification of the search process. On the other hand, when the process has not visited an already known solution for a high number of iterations, the tabu list is shortened in order to favor the intensification of the search process. The second new element concerns the adding of cycle detection capacities. Thus, when a cycle is detected, the process applies random movements in order to break the cycle. See [batch161 the batch dedicated page].",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the reactive_tabu statement uses `method reactive_tabu` instead of the expected `reactive_tabu name: id` : ",
				examples = { @example (
						value = "method reactive_tabu [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method reactive_tabu iter_max: 50 tabu_list_size_init: 5 tabu_list_size_min: 2 tabu_list_size_max: 10 nb_tests_wthout_col_max: 20 cycle_size_min: 2 cycle_size_max: 20 maximize: food_gathered;",
								isExecutable = false) }) })
public class TabuSearchReactive extends LocalSearchAlgorithm {

	int tabuListSizeInit = 5;
	int tabuListSizeMax = 2;
	int tabuListSizeMin = 10;
	int nbTestWithoutCollisionMax = 20;
	int cycleSizeMax = 20;
	int cycleSizeMin = 2;
	StoppingCriterion stoppingCriterion = new StoppingCriterionMaxIt(100);

	protected static final String ITER_MAX = "iter_max";
	protected static final String LIST_SIZE_INIT = "tabu_list_size_init";
	protected static final String LIST_SIZE_MAX = "tabu_list_size_max";
	protected static final String LIST_SIZE_MIN = "tabu_list_size_min";
	protected static final String NB_TESTS_MAX = "nb_tests_wthout_col_max";
	protected static final String CYCLE_SIZE_MAX = "cycle_size_max";
	protected static final String CYCLE_SIZE_MIN = "cycle_size_min";

	public TabuSearchReactive(final IDescription species) {
		super(species);
		initParams();

	}

	int iterMax = 100;

	// @Override
	// public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
	// super.initializeFor(scope, agent);
	// }

	@Override
	public void initParams(final IScope scope) {
		final IExpression maxIt = getFacet(ITER_MAX);
		if (maxIt != null) {
			iterMax = Cast.asInt(scope, maxIt.value(scope));
			stoppingCriterion = new StoppingCriterionMaxIt(iterMax);
		}
		final IExpression listSizeInit = getFacet(LIST_SIZE_INIT);
		if (listSizeInit != null) {
			tabuListSizeInit = Cast.asInt(scope, listSizeInit.value(scope));
		}
		final IExpression listSizeMax = getFacet(LIST_SIZE_MAX);
		if (listSizeMax != null) {
			tabuListSizeMax = Cast.asInt(scope, listSizeMax.value(scope));
		}
		final IExpression listSizeMin = getFacet(LIST_SIZE_MIN);
		if (listSizeMin != null) {
			tabuListSizeMin = Cast.asInt(scope, listSizeMin.value(scope));
		}
		final IExpression nbTestWtoutColMax = getFacet(NB_TESTS_MAX);
		if (nbTestWtoutColMax != null) {
			nbTestWithoutCollisionMax = Cast.asInt(scope, nbTestWtoutColMax.value(scope));
		}
		final IExpression cycleMax = getFacet(CYCLE_SIZE_MAX);
		if (cycleMax != null) {
			cycleSizeMax = Cast.asInt(scope, cycleMax.value(scope));
		}
		final IExpression cycleMin = getFacet(CYCLE_SIZE_MIN);
		if (cycleMin != null) {
			cycleSizeMin = Cast.asInt(scope, cycleMin.value(scope));
		}
	}

	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		initializeTestedSolutions();
		final List<ParametersSet> tabuList = new ArrayList<>();
		int tabuListSize = tabuListSizeInit;
		ParametersSet bestSolutionAlgo = this.solutionInit;
		tabuList.add(bestSolutionAlgo);
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolutionAlgo);
		testedSolutions.put(bestSolutionAlgo, currentFitness);

		double bestFitnessAlgo = currentFitness;

		setBestSolution(new ParametersSet(bestSolutionAlgo));
		setBestFitness(currentFitness);

		testedSolutions.put(getBestSolution(), currentFitness);
		int nbIt = 0;
		int nbTestWithoutCollision = 0;
		int currentCycleSize = 0;
		int cycleSize = 0;
		ParametersSet startingCycle = null;
		final Map<String, Object> endingCritParams = new HashMap<>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (!stoppingCriterion.stopSearchProcess(endingCritParams)) {
			nbTestWithoutCollision++;
			final List<ParametersSet> neighbors = neighborhood.neighbor(scope, getBestSolution());
			neighbors.removeAll(tabuList);
			if (neighbors.isEmpty()) {
				break;
			}
			if (isMaximize()) {
				bestFitnessAlgo = Double.MIN_VALUE;
			} else {
				bestFitnessAlgo = Double.MAX_VALUE;
			}
			ParametersSet bestNeighbor = null;

			for (final ParametersSet neighborSol : neighbors) {
				if (neighborSol == null) {
					continue;
				}
				if (testedSolutions.containsKey(neighborSol)) {
					nbTestWithoutCollision = 0;
					if (tabuListSize < tabuListSizeMax) {
						tabuListSize++;
					}
				}
				Double neighborFitness = testedSolutions.get(neighborSol);
				if (neighborFitness == null || neighborFitness == Double.MAX_VALUE) {
					neighborFitness = currentExperiment.launchSimulationsWithSolution(neighborSol);
				}
				testedSolutions.put(neighborSol, neighborFitness);

				if (isMaximize() && neighborFitness > bestFitnessAlgo
						|| !isMaximize() && neighborFitness < bestFitnessAlgo) {
					bestNeighbor = neighborSol;
					bestFitnessAlgo = neighborFitness;
				}
				nbIt++;
				if (nbIt > iterMax) {
					break;
				}
			}
			if (bestNeighbor == null) {
				break;
			}
			if (this.testedSolutions.containsKey(bestNeighbor)) {
				currentCycleSize++;
			} else {
				startingCycle = null;
				currentCycleSize = 0;
				cycleSize = 0;
			}
			if (currentCycleSize == cycleSizeMin) {
				startingCycle = bestNeighbor;
			} else if (currentCycleSize > cycleSizeMin && currentCycleSize <= cycleSizeMax) {
				if (startingCycle != null && !startingCycle.equals(bestNeighbor)) {
					cycleSize++;
				} else {
					final int depl = (int) (1 + scope.getRandom().next() * cycleSize / 2.0);
					for (int i = 0; i < depl; i++) {
						final List<ParametersSet> neighborsAlea = neighborhood.neighbor(scope, bestSolutionAlgo);
						neighborsAlea.removeAll(tabuList);
						if (neighborsAlea.isEmpty()) {
							break;
						}
						bestSolutionAlgo = neighborsAlea.get(scope.getRandom().between(0, neighborsAlea.size() - 1));
						if (tabuList.size() == tabuListSize) {
							tabuList.remove(0);
						}
						tabuList.add(bestSolutionAlgo);
					}
					currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolutionAlgo);
					testedSolutions.put(bestSolutionAlgo, currentFitness);
					if (nbIt > iterMax) {
						break;
					}
				}
			}
			bestSolutionAlgo = bestNeighbor;
			tabuList.add(bestSolutionAlgo);
			if (tabuList.size() > tabuListSize) {
				tabuList.remove(0);
			}
			// currentFitness = bestFitnessAlgo;

			if (nbTestWithoutCollision == nbTestWithoutCollisionMax) {
				nbTestWithoutCollision = 0;
				if (tabuListSize > this.tabuListSizeMin) {
					tabuListSize--;
				}
			}

			nbIt++;
			endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		}
		// DEBUG.LOG("Best solution : " + currentSol + " fitness : "
		// + currentFitness);

		return getBestSolution();
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(new ParameterAdapter("Tabu list initial size", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSizeInit;
			}

		});
		params.add(new ParameterAdapter("Tabu list maximum size", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSizeMax;
			}

		});
		params.add(new ParameterAdapter("Tabu list minimum size", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSizeMin;
			}

		});
		params.add(new ParameterAdapter("Maximum number of tests without collision",
				IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return nbTestWithoutCollisionMax;
			}

		});
		params.add(new ParameterAdapter("Maximum cycle size", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return cycleSizeMax;
			}

		});
		params.add(new ParameterAdapter("Minimum cycle size", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return cycleSizeMin;
			}

		});
		params.add(
				new ParameterAdapter("Maximum number of iterations", IExperimentPlan.BATCH_CATEGORY_NAME, IType.FLOAT) {

					@Override
					public Object value() {
						return iterMax;
					}

				});
	}

}
