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
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.HILL_CLIMBING, kind = ISymbolKind.BATCH_METHOD)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = HillClimbing.ITER_MAX, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.MAXIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.MINIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.AGGREGATION, type = IType.LABEL, optional = true, values = {
		IKeyword.MIN, IKeyword.MAX }) }, omissible = IKeyword.NAME)
public class HillClimbing extends LocalSearchAlgorithm {

	protected static final String ITER_MAX = "iter_max";
	private StoppingCriterion stoppingCriterion = null;
	private int maxIt;

	public HillClimbing(final IDescription species) {
		super(species);
	}

	@Override
	public ParametersSet findBestSolution() throws GamaRuntimeException {
		bestSolution = this.solutionInit;
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolution);
		testedSolutions = new Hashtable<ParametersSet, Double>();
		testedSolutions.put(bestSolution, new Double(currentFitness));
		int nbIt = 0;

		final Map<String, Object> endingCritParams = new Hashtable<String, Object>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (stoppingCriterion == null || !stoppingCriterion.stopSearchProcess(endingCritParams)) {
			final List<ParametersSet> neighbors = neighborhood.neighbor(bestSolution);
			if ( neighbors.isEmpty() ) {
				break;
			}
			bestFitness = currentFitness;
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
				}
				testedSolutions.put(neighborSol, neighborFitness);

				if ( isMaximize() && neighborFitness.doubleValue() > bestFitness || !isMaximize() &&
					neighborFitness.doubleValue() < bestFitness ) {
					bestNeighbor = neighborSol;
					bestFitness = neighborFitness.doubleValue();
				}
			}
			if ( bestNeighbor != null ) {
				bestSolution = bestNeighbor;
				currentFitness = bestFitness;
			} else {
				break;
			}
			nbIt++;
			endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		}
		// System.out.println("Best solution : " + currentSol + "  fitness : "
		// + currentFitness);
		return bestSolution;
	}

	@Override
	public void initializeFor(final BatchExperiment f) throws GamaRuntimeException {
		super.initializeFor(f);
		final IExpression maxItExp = getFacet("iter_max");
		if ( maxItExp != null ) {
			maxIt = Cast.asInt(GAMA.getDefaultScope(), maxItExp.value(GAMA.getDefaultScope()));
			stoppingCriterion = new StoppingCriterionMaxIt(maxIt);
		}

	}

	@Override
	public void addParametersTo(final BatchExperiment exp) {
		super.addParametersTo(exp);
		exp.addMethodParameter(new ParameterAdapter("Maximum number of iterations",
			IExperiment.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return maxIt;
			}

		});
	}

}
