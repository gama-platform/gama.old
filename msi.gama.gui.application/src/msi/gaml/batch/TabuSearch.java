/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.batch;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

@symbol(name = IBatch.TABU, kind = ISymbolKind.BATCH_METHOD)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets({
	@facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = TabuSearch.ITER_MAX, type = IType.INT_STR, optional = true),
	@facet(name = TabuSearch.LIST_SIZE, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.MAXIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.MINIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.AGGREGATION, type = IType.LABEL, optional = true, values = { ISymbol.MIN,
		ISymbol.MAX }) })
public class TabuSearch extends LocalSearchAlgorithm {

	protected static final String ITER_MAX = "iter_max";
	protected static final String LIST_SIZE = "tabu_list_size";

	private int tabuListSize = 5;
	private StoppingCriterion stoppingCriterion = new StoppingCriterionMaxIt(50);

	public TabuSearch(final IDescription species) {
		super(species);
	}

	@Override
	public Solution findBestSolution() throws GamaRuntimeException {
		testedSolutions = new Hashtable<Solution, Double>();
		final List<Solution> tabuList = new ArrayList<Solution>();
		Solution bestSolutionAlgo = this.solutionInit;
		tabuList.add(bestSolutionAlgo);
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolutionAlgo);
		testedSolutions.put(bestSolutionAlgo, new Double(currentFitness));
		bestSolution = new Solution(bestSolutionAlgo);
		bestFitness = currentFitness;
		
		int nbIt = 0;
		double bestFitnessAlgo = currentFitness;
		final Map<String, Object> endingCritParams = new Hashtable<String, Object>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (!stoppingCriterion.stopSearchProcess(endingCritParams)) {
			final List<Solution> neighbors = neighborhood.neighbor(bestSolutionAlgo);
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
			Solution bestNeighbor = null;

			for ( final Solution neighborSol : neighbors ) {
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

				if ( isMaximize() && neighborFitness.doubleValue() > bestFitnessAlgo || !isMaximize() &&
					neighborFitness.doubleValue() < bestFitnessAlgo ) {
					bestNeighbor = neighborSol;
					bestFitnessAlgo = neighborFitness.doubleValue();
				}

				if ( isMaximize() && currentFitness > bestFitness || !isMaximize() &&
					currentFitness < bestFitness ) {
					bestSolution = new Solution(bestSolutionAlgo);
					bestFitness = currentFitness;
				}
				if (nbIt > iterMax)
					break;
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
			iterMax = Cast.asInt(maxIt.value(GAMA.getDefaultScope()));
			stoppingCriterion = new StoppingCriterionMaxIt(iterMax);
		}
		final IExpression listsize = getFacet(LIST_SIZE);
		if ( listsize != null ) {
			tabuListSize = Cast.asInt(listsize.value(GAMA.getDefaultScope()));
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
