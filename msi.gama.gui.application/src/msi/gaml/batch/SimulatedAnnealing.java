/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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

@symbol(name = IBatch.ANNEALING, kind = ISymbolKind.BATCH_METHOD)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets({
	@facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = SimulatedAnnealing.TEMP_END, type = IType.FLOAT_STR, optional = true),
	@facet(name = SimulatedAnnealing.TEMP_DECREASE, type = IType.FLOAT_STR, optional = true),
	@facet(name = SimulatedAnnealing.TEMP_INIT, type = IType.FLOAT_STR, optional = true),
	@facet(name = SimulatedAnnealing.NB_ITER, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.MAXIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.MINIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.AGGREGATION, type = IType.LABEL, optional = true, values = { ISymbol.MIN,
		ISymbol.MAX }) })
public class SimulatedAnnealing extends LocalSearchAlgorithm {

	private double					temperatureEnd	= 1;
	private double					tempDimCoeff	= 0.5;
	private double					temperatureInit	= 100;
	private int						nbIterCstTemp	= 5;

	protected static final String	TEMP_END		= "temp_end";
	protected static final String	TEMP_DECREASE	= "temp_decrease";
	protected static final String	TEMP_INIT		= "temp_init";
	protected static final String	NB_ITER			= "nb_iter_cst_temp";

	public SimulatedAnnealing(final IDescription species) {
		super(species);
	}

	@Override
	public void initializeFor(final BatchExperiment f) throws GamaRuntimeException {
		super.initializeFor(f);

		final IExpression tempend = getFacet(TEMP_END);
		if ( tempend != null ) {
			temperatureEnd = Cast.asFloat(tempend.value(GAMA.getDefaultScope()));
		}
		final IExpression tempdecrease = getFacet(TEMP_DECREASE);
		if ( tempdecrease != null ) {
			tempDimCoeff = Cast.asFloat(tempdecrease.value(GAMA.getDefaultScope()));
		}
		final IExpression tempinit = getFacet(TEMP_INIT);
		if ( tempinit != null ) {
			temperatureInit = Cast.asFloat(tempinit.value(GAMA.getDefaultScope()));
		}

		final IExpression nbIterCstT = getFacet(NB_ITER);
		if ( nbIterCstT != null ) {
			nbIterCstTemp = Cast.asInt(nbIterCstT.value(GAMA.getDefaultScope()));
		}

	}

	@Override
	public Solution findBestSolution() throws GamaRuntimeException {
		testedSolutions = new Hashtable<Solution, Double>();
		bestSolution = new Solution(this.solutionInit);
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolution);
		Solution bestSolutionAlgo = this.solutionInit;
		testedSolutions.put(bestSolution, bestFitness);
		int nbIt = 0;
		double temperature = temperatureInit;

		final Map<String, Object> endingCritParams = new Hashtable<String, Object>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (temperature > temperatureEnd) {
			final List<Solution> neighbors = neighborhood.neighbor(bestSolutionAlgo);
			if ( neighbors.isEmpty() ) {
				break;
			}
			int iter = 0;
			while (iter < nbIterCstTemp) {
				final Solution neighborSol =
					neighbors.get(GAMA.getRandom().between(0, neighbors.size() - 1));
				if ( neighborSol == null ) {
					neighbors.removeAll(Collections.singleton(null));
					if ( neighbors.isEmpty() ) {
						break;
					}
					continue;
				}
				Double neighborFitness = testedSolutions.get(neighborSol);
				if ( neighborFitness == null ) {
					neighborFitness =
						Double
							.valueOf(currentExperiment.launchSimulationsWithSolution(neighborSol));
				}
				testedSolutions.put(neighborSol, neighborFitness);

				if ( isMaximize() &&
					(neighborFitness.doubleValue() >= currentFitness || GAMA.getRandom().next() < Math
						.exp((neighborFitness.doubleValue() - currentFitness) / temperature)) ||
					!isMaximize() &&
					(neighborFitness.doubleValue() <= currentFitness || GAMA.getRandom().next() < Math
						.exp((currentFitness - neighborFitness.doubleValue()) / temperature)) ) {
					bestSolutionAlgo = neighborSol;
					currentFitness = neighborFitness.doubleValue();
				}
				if ( isMaximize() && currentFitness > bestFitness || !isMaximize() &&
					currentFitness < bestFitness ) {
					bestSolution = new Solution(bestSolutionAlgo);
					bestFitness = currentFitness;
				}
				iter++;
			}
			temperature *= tempDimCoeff;
			nbIt++;
			endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		}
		// System.out.println("Best solution : " + currentSol + "  fitness : "
		// + currentFitness);

		return bestSolution;
	}

	@Override
	public void addParametersTo(final BatchExperiment exp) {
		super.addParametersTo(exp);
		exp.addMethodParameter(new ParameterAdapter("Final temperature",
			IExperiment.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return temperatureEnd;
			}

		});
		exp.addMethodParameter(new ParameterAdapter("Initial temperature",
			IExperiment.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return temperatureInit;
			}

		});
		exp.addMethodParameter(new ParameterAdapter("Coefficient of diminution",
			IExperiment.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return tempDimCoeff;
			}

		});
		exp.addMethodParameter(new ParameterAdapter("Number of iterations at constant temperature",
			IExperiment.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return temperatureEnd;
			}

		});
	}

}
