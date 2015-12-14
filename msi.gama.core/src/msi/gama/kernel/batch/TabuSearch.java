/*********************************************************************************************
 *
 *
 * 'TabuSearch.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.TABU, kind = ISymbolKind.BATCH_METHOD, with_sequence = false)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(
	value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false, internal = true),
		@facet(name = TabuSearch.ITER_MAX, type = IType.INT, optional = true, doc = @doc("number of iterations") ),
		@facet(name = TabuSearch.LIST_SIZE, type = IType.INT, optional = true, doc = @doc("size of the tabu list") ),
		@facet(name = IKeyword.MAXIMIZE,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("the value the algorithm tries to maximize") ),
		@facet(name = IKeyword.MINIMIZE,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("the value the algorithm tries to minimize") ),
		@facet(name = IKeyword.AGGREGATION,
			type = IType.LABEL,
			optional = true,
			values = { IKeyword.MIN, IKeyword.MAX },
			doc = @doc("the agregation method") ) },
	omissible = IKeyword.NAME)
@doc(
	value = "This algorithm is an implementation of the Tabu Search algorithm. See the wikipedia article and [batch161 the batch dedicated page].",
	usages = {
		@usage(
			value = "As other batch methods, the basic syntax of the tabu statement uses `method tabu` instead of the expected `tabu name: id` : ",
			examples = { @example(value = "method tabu [facet: value];", isExecutable = false) }),
		@usage(value = "For example: ",
			examples = { @example(value = "method tabu iter_max: 50 tabu_list_size: 5 maximize: food_gathered;",
				isExecutable = false) }) })
public class TabuSearch extends LocalSearchAlgorithm {

	protected static final String ITER_MAX = "iter_max";
	protected static final String LIST_SIZE = "tabu_list_size";

	private int tabuListSize = 5;
	private StoppingCriterion stoppingCriterion = new StoppingCriterionMaxIt(50);

	public TabuSearch(final IDescription species) {
		super(species);
		initParams();

	}

	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		initializeTestedSolutions();
		final List<ParametersSet> tabuList = new ArrayList<ParametersSet>();
		ParametersSet bestSolutionAlgo = this.solutionInit;
		tabuList.add(bestSolutionAlgo);
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolutionAlgo);
		testedSolutions.put(bestSolutionAlgo, currentFitness);
		setBestSolution(new ParametersSet(bestSolutionAlgo));
		setBestFitness(currentFitness);

		int nbIt = 0;
		double bestFitnessAlgo = currentFitness;
		final Map<String, Object> endingCritParams = new Hashtable<String, Object>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (!stoppingCriterion.stopSearchProcess(endingCritParams)) {
			// GuiUtils.debug("TabuSearch.findBestSolution while stoppingCriterion " + endingCritParams);
			final List<ParametersSet> neighbors = neighborhood.neighbor(scope, bestSolutionAlgo);
			neighbors.removeAll(tabuList);
			if ( neighbors.isEmpty() ) {
				if ( tabuList.isEmpty() ) {
					break;
				}
				neighbors.add(tabuList.get(scope.getRandom().between(0, tabuList.size() - 1)));
			}
			if ( isMaximize() ) {
				bestFitnessAlgo = -Double.MAX_VALUE;
			} else {
				bestFitnessAlgo = Double.MAX_VALUE;
			}
			ParametersSet bestNeighbor = null;

			for ( final ParametersSet neighborSol : neighbors ) {
				// GuiUtils.debug("TabuSearch.findBestSolution for parametersSet " + neighborSol);
				if ( neighborSol == null ) {
					continue;
				}
				Double neighborFitness = testedSolutions.get(neighborSol);
				if ( neighborFitness == null || neighborFitness == Double.MAX_VALUE ) {
					neighborFitness = Double.valueOf(currentExperiment.launchSimulationsWithSolution(neighborSol));
					nbIt++;
				} else {
					continue;
				}
				testedSolutions.put(neighborSol, neighborFitness);

				// GuiUtils.debug("TabuSearch.findBestSolution neighbourFitness = " + neighborFitness +
				// " bestFitnessAlgo = " + bestFitnessAlgo + " bestFitness = " + getBestFitness() +
				// " current fitness = " + currentFitness);
				boolean neighFitnessGreaterThanBest = neighborFitness > bestFitnessAlgo;
				if ( isMaximize() && neighFitnessGreaterThanBest || !isMaximize() && !neighFitnessGreaterThanBest ) {
					bestNeighbor = neighborSol;
					bestFitnessAlgo = neighborFitness;
				}
				boolean curFitnessGreaterThanBest = currentFitness > getBestFitness();

				if ( isMaximize() && curFitnessGreaterThanBest || !isMaximize() && !curFitnessGreaterThanBest ) {
					setBestSolution(new ParametersSet(bestSolutionAlgo));
					setBestFitness(currentFitness);
				}
				if ( nbIt > iterMax ) {
					break;
				}
			}
			if ( bestNeighbor != null ) {
				bestSolutionAlgo = bestNeighbor;
				tabuList.add(bestSolutionAlgo);
				if ( tabuList.size() > tabuListSize ) {
					tabuList.remove(0);
				}
				currentFitness = bestFitnessAlgo;
			} else {
				break;
			}
			endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		}
		// System.out.println("Best solution : " + currentSol + " fitness : "
		// + currentFitness);

		return getBestSolution();
	}

	int iterMax = 50;

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		super.initializeFor(scope, agent);

	}

	public void initParams(final IScope scope) {
		final IExpression maxIt = getFacet(ITER_MAX);
		if ( maxIt != null ) {
			iterMax = Cast.asInt(scope, maxIt.value(scope));
			stoppingCriterion = new StoppingCriterionMaxIt(iterMax);
		}
		final IExpression listsize = getFacet(LIST_SIZE);
		if ( listsize != null ) {
			tabuListSize = Cast.asInt(scope, listsize.value(scope));
		}
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(new ParameterAdapter("Tabu list size", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSize;
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
