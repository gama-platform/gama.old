/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.batch;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.REACTIVE_TABU, kind = ISymbolKind.BATCH_METHOD, with_sequence = false)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = TabuSearchReactive.ITER_MAX, type = IType.INT, optional = true),
	@facet(name = TabuSearchReactive.LIST_SIZE_INIT, type = IType.INT, optional = true),
	@facet(name = TabuSearchReactive.LIST_SIZE_MAX, type = IType.INT, optional = true),
	@facet(name = TabuSearchReactive.LIST_SIZE_MIN, type = IType.INT, optional = true),
	@facet(name = TabuSearchReactive.NB_TESTS_MAX, type = IType.INT, optional = true),
	@facet(name = TabuSearchReactive.CYCLE_SIZE_MAX, type = IType.INT, optional = true),
	@facet(name = TabuSearchReactive.CYCLE_SIZE_MIN, type = IType.INT, optional = true),
	@facet(name = IKeyword.MAXIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.MINIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.AGGREGATION, type = IType.LABEL, optional = true, values = { IKeyword.MIN, IKeyword.MAX }) }, omissible = IKeyword.NAME)
public class TabuSearchReactive extends LocalSearchAlgorithm {

	private int tabuListSizeInit = 5;
	private int tabuListSizeMax = 2;
	private int tabuListSizeMin = 10;
	private int nbTestWithoutCollisionMax = 20;
	private int cycleSizeMax = 20;
	private int cycleSizeMin = 2;
	private StoppingCriterion stoppingCriterion = new StoppingCriterionMaxIt(100);

	protected static final String ITER_MAX = "iter_max";
	protected static final String LIST_SIZE_INIT = "tabu_list_size_init";
	protected static final String LIST_SIZE_MAX = "tabu_list_size_max";
	protected static final String LIST_SIZE_MIN = "tabu_list_size_min";
	protected static final String NB_TESTS_MAX = "nb_tests_wthout_col_max";
	protected static final String CYCLE_SIZE_MAX = "cycle_size_max";
	protected static final String CYCLE_SIZE_MIN = "cycle_size_min";

	public TabuSearchReactive(final IDescription species) {
		super(species);

	}

	int iterMax = 100;

	@Override
	public void initializeFor(final BatchAgent agent) throws GamaRuntimeException {
		super.initializeFor(agent);
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				final IExpression maxIt = getFacet(ITER_MAX);
				if ( maxIt != null ) {
					iterMax = Cast.asInt(scope, maxIt.value(scope));
					stoppingCriterion = new StoppingCriterionMaxIt(iterMax);
				}
				final IExpression listSizeInit = getFacet(LIST_SIZE_INIT);
				if ( listSizeInit != null ) {
					tabuListSizeInit = Cast.asInt(scope, listSizeInit.value(scope));
				}
				final IExpression listSizeMax = getFacet(LIST_SIZE_MAX);
				if ( listSizeMax != null ) {
					tabuListSizeMax = Cast.asInt(scope, listSizeMax.value(scope));
				}
				final IExpression listSizeMin = getFacet(LIST_SIZE_MIN);
				if ( listSizeMin != null ) {
					tabuListSizeMin = Cast.asInt(scope, listSizeMin.value(scope));
				}
				final IExpression nbTestWtoutColMax = getFacet(NB_TESTS_MAX);
				if ( nbTestWtoutColMax != null ) {
					nbTestWithoutCollisionMax = Cast.asInt(scope, nbTestWtoutColMax.value(scope));
				}
				final IExpression cycleMax = getFacet(CYCLE_SIZE_MAX);
				if ( cycleMax != null ) {
					cycleSizeMax = Cast.asInt(scope, cycleMax.value(scope));
				}
				final IExpression cycleMin = getFacet(CYCLE_SIZE_MIN);
				if ( cycleMin != null ) {
					cycleSizeMin = Cast.asInt(scope, cycleMin.value(scope));
				}

			}
		});
	}

	@Override
	public ParametersSet findBestSolution() throws GamaRuntimeException {
		testedSolutions = new Hashtable<ParametersSet, Double>();
		final List<ParametersSet> tabuList = new ArrayList<ParametersSet>();
		int tabuListSize = tabuListSizeInit;
		tabuList.add(bestSolution);
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolution);
		double bestFitnessAlgo = currentFitness;
		ParametersSet bestSolutionAlgo = this.solutionInit;
		bestSolution = new ParametersSet(bestSolutionAlgo);
		bestFitness = currentFitness;

		testedSolutions.put(bestSolution, new Double(currentFitness));
		int nbIt = 0;
		int nbTestWithoutCollision = 0;
		int currentCycleSize = 0;
		int cycleSize = 0;
		ParametersSet startingCycle = null;
		final Map<String, Object> endingCritParams = new Hashtable<String, Object>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (!stoppingCriterion.stopSearchProcess(endingCritParams)) {
			nbTestWithoutCollision++;
			final List<ParametersSet> neighbors = neighborhood.neighbor(bestSolution);
			neighbors.removeAll(tabuList);
			if ( neighbors.isEmpty() ) {
				break;
			}
			if ( isMaximize() ) {
				bestFitnessAlgo = Double.MIN_VALUE;
			} else {
				bestFitnessAlgo = Double.MAX_VALUE;
			}
			ParametersSet bestNeighbor = null;

			for ( final ParametersSet neighborSol : neighbors ) {
				if ( neighborSol == null ) {
					continue;
				}
				if ( testedSolutions.containsKey(neighborSol) ) {
					nbTestWithoutCollision = 0;
					if ( tabuListSize < tabuListSizeMax ) {
						tabuListSize++;
					}
				}
				Double neighborFitness = testedSolutions.get(neighborSol);
				if ( neighborFitness == null ) {
					neighborFitness = Double.valueOf(currentExperiment.launchSimulationsWithSolution(neighborSol));
				}
				testedSolutions.put(neighborSol, neighborFitness);

				if ( isMaximize() && neighborFitness.doubleValue() > bestFitnessAlgo || !isMaximize() &&
					neighborFitness.doubleValue() < bestFitnessAlgo ) {
					bestNeighbor = neighborSol;
					bestFitnessAlgo = neighborFitness.doubleValue();
				}
				if ( isMaximize() && currentFitness > bestFitness || !isMaximize() && currentFitness < bestFitness ) {
					bestSolution = new ParametersSet(bestSolutionAlgo);
					bestFitness = currentFitness;
				}
				nbIt++;
				if ( nbIt > iterMax ) {
					break;
				}
			}
			if ( bestNeighbor == null ) {
				break;
			}
			if ( this.testedSolutions.keySet().contains(bestNeighbor) ) {
				currentCycleSize++;
			} else {
				startingCycle = null;
				currentCycleSize = 0;
				cycleSize = 0;
			}
			if ( currentCycleSize == cycleSizeMin ) {
				startingCycle = bestNeighbor;
			} else if ( currentCycleSize > cycleSizeMin && currentCycleSize <= cycleSizeMax ) {
				if ( startingCycle != null && !startingCycle.equals(bestNeighbor) ) {
					cycleSize++;
				} else {
					final int depl = (int) (1 + GAMA.getRandom().next() * cycleSize / 2.0);
					for ( int i = 0; i < depl; i++ ) {
						final List<ParametersSet> neighborsAlea = neighborhood.neighbor(bestSolutionAlgo);
						neighborsAlea.removeAll(tabuList);
						if ( neighborsAlea.isEmpty() ) {
							break;
						}
						bestSolutionAlgo = neighborsAlea.get(GAMA.getRandom().between(0, neighborsAlea.size() - 1));
						if ( tabuList.size() == tabuListSize ) {
							tabuList.remove(0);
						}
						tabuList.add(bestSolutionAlgo);
					}
					currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolutionAlgo);
					testedSolutions.put(bestSolutionAlgo, new Double(currentFitness));
					if ( isMaximize() && currentFitness > bestFitness || !isMaximize() && currentFitness < bestFitness ) {
						bestSolution = new ParametersSet(bestSolutionAlgo);
						bestFitness = currentFitness;
					}
					if ( nbIt > iterMax ) {
						break;
					}
				}
			}
			bestSolutionAlgo = bestNeighbor;
			tabuList.add(bestSolutionAlgo);
			if ( tabuList.size() > tabuListSize ) {
				tabuList.remove(0);
			}
			currentFitness = bestFitnessAlgo;

			if ( nbTestWithoutCollision == nbTestWithoutCollisionMax ) {
				nbTestWithoutCollision = 0;
				if ( tabuListSize > this.tabuListSizeMin ) {
					tabuListSize--;
				}
			}

			nbIt++;
			endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		}
		// System.out.println("Best solution : " + currentSol + "  fitness : "
		// + currentFitness);

		return bestSolution;
	}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(new ParameterAdapter("Tabu list initial size", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSizeInit;
			}

		});
		params.add(new ParameterAdapter("Tabu list maximum size", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSizeMax;
			}

		});
		params.add(new ParameterAdapter("Tabu list minimum size", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSizeMin;
			}

		});
		params.add(new ParameterAdapter("Maximum number of tests without collision",
			IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return nbTestWithoutCollisionMax;
			}

		});
		params.add(new ParameterAdapter("Maximum cycle size", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return cycleSizeMax;
			}

		});
		params.add(new ParameterAdapter("Minimum cycle size", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return cycleSizeMin;
			}

		});
		params.add(new ParameterAdapter("Maximum number of iterations", IExperimentSpecies.BATCH_CATEGORY_NAME,
			IType.FLOAT) {

			@Override
			public Object value() {
				return iterMax;
			}

		});
	}

}
