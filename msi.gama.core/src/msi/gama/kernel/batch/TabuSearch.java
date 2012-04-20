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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.TABU, kind = ISymbolKind.BATCH_METHOD)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = TabuSearch.ITER_MAX, type = IType.INT_STR, optional = true),
	@facet(name = TabuSearch.LIST_SIZE, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.MAXIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.MINIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.AGGREGATION, type = IType.LABEL, optional = true, values = {
		IKeyword.MIN, IKeyword.MAX }) }, omissible = IKeyword.NAME)
public class TabuSearch extends LocalSearchAlgorithm {

	protected static final String ITER_MAX = "iter_max";
	protected static final String LIST_SIZE = "tabu_list_size";

	private int tabuListSize = 5;
	private StoppingCriterion stoppingCriterion = new StoppingCriterionMaxIt(50);

	public TabuSearch(final IDescription species) {
		super(species);
		verifyFacetType(IKeyword.MAXIMIZE);
		verifyFacetType(IKeyword.MINIMIZE);
		verifyFacetType(IKeyword.AGGREGATION);
		verifyFacetType(ITER_MAX);
		verifyFacetType(LIST_SIZE);

	}

	@Override
	public ParametersSet findBestSolution() throws GamaRuntimeException {
		testedSolutions = new Hashtable<ParametersSet, Double>();
		final List<ParametersSet> tabuList = new ArrayList<ParametersSet>();
		ParametersSet bestSolutionAlgo = this.solutionInit;
		tabuList.add(bestSolutionAlgo);
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolutionAlgo);
		testedSolutions.put(bestSolutionAlgo, new Double(currentFitness));
		bestSolution = new ParametersSet(bestSolutionAlgo);
		bestFitness = currentFitness;

		int nbIt = 0;
		double bestFitnessAlgo = currentFitness;
		final Map<String, Object> endingCritParams = new Hashtable<String, Object>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (!stoppingCriterion.stopSearchProcess(endingCritParams)) {
			final List<ParametersSet> neighbors = neighborhood.neighbor(bestSolutionAlgo);
			neighbors.removeAll(tabuList);
			if ( neighbors.isEmpty() ) {
				if ( tabuList.isEmpty() ) {
					break;
				}
				neighbors.add(tabuList.get(GAMA.getRandom().between(0, tabuList.size() - 1)));
			}
			if ( isMaximize() ) {
				bestFitnessAlgo = -Double.MAX_VALUE;
			} else {
				bestFitnessAlgo = Double.MAX_VALUE;
			}
			ParametersSet bestNeighbor = null;

			for ( final ParametersSet neighborSol : neighbors ) {
				if ( neighborSol == null ) {
					continue;
				}
				Double neighborFitness = testedSolutions.get(neighborSol);
				if ( neighborFitness == null ) {
					neighborFitness =
						Double
							.valueOf(currentExperiment.launchSimulationsWithSolution(neighborSol));
					nbIt++;
				}
				testedSolutions.put(neighborSol, neighborFitness);

				if ( isMaximize() && neighborFitness.doubleValue() > bestFitnessAlgo ||
					!isMaximize() && neighborFitness.doubleValue() < bestFitnessAlgo ) {
					bestNeighbor = neighborSol;
					bestFitnessAlgo = neighborFitness.doubleValue();
				}

				if ( isMaximize() && currentFitness > bestFitness || !isMaximize() &&
					currentFitness < bestFitness ) {
					bestSolution = new ParametersSet(bestSolutionAlgo);
					bestFitness = currentFitness;
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
		// System.out.println("Best solution : " + currentSol + "  fitness : "
		// + currentFitness);

		return bestSolution;
	}

	int iterMax = 50;

	@Override
	public void initializeFor(final BatchExperiment f) throws GamaRuntimeException {
		super.initializeFor(f);
		final IExpression maxIt = getFacet(ITER_MAX);
		if ( maxIt != null ) {
			iterMax = Cast.asInt(GAMA.getDefaultScope(), maxIt.value(GAMA.getDefaultScope()));
			stoppingCriterion = new StoppingCriterionMaxIt(iterMax);
		}
		final IExpression listsize = getFacet(LIST_SIZE);
		if ( listsize != null ) {
			tabuListSize =
				Cast.asInt(GAMA.getDefaultScope(), listsize.value(GAMA.getDefaultScope()));
		}
	}

	@Override
	public void addParametersTo(final BatchExperiment exp) {
		super.addParametersTo(exp);
		exp.addMethodParameter(new ParameterAdapter("Tabu list size",
			IExperiment.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return tabuListSize;
			}

		});
		exp.addMethodParameter(new ParameterAdapter("Maximum number of iterations",
			IExperiment.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return iterMax;
			}

		});
	}

}
