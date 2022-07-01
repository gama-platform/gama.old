/*******************************************************************************************************
 *
 * HillClimbing.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.kernel.batch.optimization;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.StoppingCriterion;
import msi.gama.kernel.batch.StoppingCriterionMaxIt;
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
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class HillClimbing.
 */
@symbol (
		name = IKeyword.HILL_CLIMBING,
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
						name = HillClimbing.ITER_MAX,
						type = IType.INT,
						optional = true,
						doc = @doc ("number of iterations")),
				@facet (
						name = HillClimbing.INIT_SOL,
						type = IType.MAP,
						optional = true,
						doc = @doc ("init solution: key: name of the variable, value: value of the variable")),
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
						values = { IKeyword.MIN, IKeyword.MAX, "avr" },
						doc = @doc ("the agregation method")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This algorithm is an implementation of the Hill Climbing algorithm. See the wikipedia article and [batch161 the batch dedicated page].",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the `hill_climbing` statement uses `method hill_climbing` instead of the expected `hill_climbing name: id` : ",
				examples = { @example (
						value = "method hill_climbing [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method hill_climbing iter_max: 50 maximize : food_gathered; ",
								isExecutable = false) }) })
public class HillClimbing extends ALocalSearchAlgorithm {

	/** The Constant ITER_MAX. */
	protected static final String ITER_MAX = "iter_max";
	
	/** The stopping criterion. */
	StoppingCriterion stoppingCriterion = null;
	
	/** The max it. */
	int maxIt;

	/**
	 * Instantiates a new hill climbing.
	 *
	 * @param species the species
	 */
	public HillClimbing(final IDescription species) {
		super(species);
		initParams();
		
	}

	/**
	 * Keep sol.
	 *
	 * @param neighborSol the neighbor sol
	 * @param neighborFitness the neighbor fitness
	 * @return true, if successful
	 */
	public boolean keepSol(ParametersSet neighborSol, Double neighborFitness ) {
		if (isMaximize() && neighborFitness.doubleValue() > getBestFitness()
				|| !isMaximize() && neighborFitness.doubleValue() < getBestFitness()) {
			setBestFitness(neighborFitness);
			return true;
		}
		return false;
	}
	
	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		setBestSolution(this.solutionInit);
		double currentFitness = (Double) currentExperiment.launchSimulationsWithSolution(getBestSolution()).get(IKeyword.FITNESS).get(0);
		initializeTestedSolutions();
		testedSolutions.put(getBestSolution(), currentFitness);
		int nbIt = 0;

		final Map<String, Object> endingCritParams = new Hashtable<>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (stoppingCriterion == null || !stoppingCriterion.stopSearchProcess(endingCritParams)) {
			final List<ParametersSet> neighbors = neighborhood.neighbor(scope, getBestSolution());
			if (neighbors.isEmpty()) {
				break;
			}
			setBestFitness(currentFitness);
			ParametersSet bestNeighbor = null;
			
			if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue() && ! currentExperiment.getParametersToExplore().isEmpty()) {
				Map<ParametersSet,Double> result = testSolutions(neighbors);
				for (ParametersSet p : result.keySet()) {
					if (keepSol(p, result.get(p))) {
						bestNeighbor = p;
					}	
				}
			} else {
				for (final ParametersSet neighborSol : neighbors) {
					if (neighborSol == null) {
						continue;
					}
					Double neighborFitness = testedSolutions.get(neighborSol);
					if (neighborFitness == null) {
						neighborFitness = (Double) currentExperiment.launchSimulationsWithSolution(neighborSol).get(IKeyword.FITNESS).get(0);
					}
					testedSolutions.put(neighborSol, neighborFitness);

					if (keepSol(neighborSol, neighborFitness)) {
						bestNeighbor = neighborSol;
					}
					
				}
			}
				
			
			
			if (bestNeighbor != null) {
				setBestSolution(bestNeighbor);
				currentFitness = getBestFitness();
			} else {
				break;
			}
			nbIt++;
			endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		}
		// DEBUG.LOG("Best solution : " + currentSol + " fitness : "
		// + currentFitness);
		return getBestSolution();
	}
	//
	// @Override
	// public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
	// super.initializeFor(scope, agent);
	// }
 
	@Override
	protected void initParams(final IScope scope) {
		final IExpression maxItExp = getFacet(ITER_MAX);
		if (maxItExp != null) {
			maxIt = Cast.asInt(scope, maxItExp.value(scope));
			stoppingCriterion = new StoppingCriterionMaxIt(maxIt);
		}
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(
				new ParameterAdapter("Maximum number of iterations", IExperimentPlan.BATCH_CATEGORY_NAME, IType.INT) {

					@Override
					public Object value() {
						return maxIt;
					}

				});
	}

}
