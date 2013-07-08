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

@symbol(name = IKeyword.ANNEALING, kind = ISymbolKind.BATCH_METHOD, with_sequence = false)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = SimulatedAnnealing.TEMP_END, type = IType.FLOAT, optional = true),
	@facet(name = SimulatedAnnealing.TEMP_DECREASE, type = IType.FLOAT, optional = true),
	@facet(name = SimulatedAnnealing.TEMP_INIT, type = IType.FLOAT, optional = true),
	@facet(name = SimulatedAnnealing.NB_ITER, type = IType.INT, optional = true),
	@facet(name = IKeyword.MAXIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.MINIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.AGGREGATION, type = IType.LABEL, optional = true, values = { IKeyword.MIN, IKeyword.MAX }) }, omissible = IKeyword.NAME)
public class SimulatedAnnealing extends LocalSearchAlgorithm {

	private double temperatureEnd = 1;
	private double tempDimCoeff = 0.5;
	private double temperatureInit = 100;
	private int nbIterCstTemp = 5;

	protected static final String TEMP_END = "temp_end";
	protected static final String TEMP_DECREASE = "temp_decrease";
	protected static final String TEMP_INIT = "temp_init";
	protected static final String NB_ITER = "nb_iter_cst_temp";

	public SimulatedAnnealing(final IDescription species) {
		super(species);
	}

	@Override
	public void initializeFor(final BatchAgent agent) throws GamaRuntimeException {
		super.initializeFor(agent);
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				final IExpression tempend = getFacet(TEMP_END);
				if ( tempend != null ) {
					temperatureEnd = Cast.asFloat(scope, tempend.value(scope));
				}
				final IExpression tempdecrease = getFacet(TEMP_DECREASE);
				if ( tempdecrease != null ) {
					tempDimCoeff = Cast.asFloat(scope, tempdecrease.value(scope));
				}
				final IExpression tempinit = getFacet(TEMP_INIT);
				if ( tempinit != null ) {
					temperatureInit = Cast.asFloat(scope, tempinit.value(scope));
				}

				final IExpression nbIterCstT = getFacet(NB_ITER);
				if ( nbIterCstT != null ) {
					nbIterCstTemp = Cast.asInt(scope, nbIterCstT.value(scope));
				}

			}
		});

	}

	@Override
	public ParametersSet findBestSolution() throws GamaRuntimeException {
		testedSolutions = new Hashtable<ParametersSet, Double>();
		bestSolution = new ParametersSet(this.solutionInit);
		double currentFitness = currentExperiment.launchSimulationsWithSolution(bestSolution);
		ParametersSet bestSolutionAlgo = this.solutionInit;
		testedSolutions.put(bestSolution, bestFitness);
		int nbIt = 0;
		double temperature = temperatureInit;

		final Map<String, Object> endingCritParams = new Hashtable<String, Object>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (temperature > temperatureEnd) {
			final List<ParametersSet> neighbors = neighborhood.neighbor(bestSolutionAlgo);
			if ( neighbors.isEmpty() ) {
				break;
			}
			int iter = 0;
			while (iter < nbIterCstTemp) {
				final ParametersSet neighborSol = neighbors.get(GAMA.getRandom().between(0, neighbors.size() - 1));
				if ( neighborSol == null ) {
					neighbors.removeAll(Collections.singleton(null));
					if ( neighbors.isEmpty() ) {
						break;
					}
					continue;
				}
				Double neighborFitness = testedSolutions.get(neighborSol);
				if ( neighborFitness == null ) {
					neighborFitness = Double.valueOf(currentExperiment.launchSimulationsWithSolution(neighborSol));
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
				if ( isMaximize() && currentFitness > bestFitness || !isMaximize() && currentFitness < bestFitness ) {
					bestSolution = new ParametersSet(bestSolutionAlgo);
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
	public void addParametersTo(final List<IParameter.Batch> params, BatchAgent agent) {
		super.addParametersTo(params, agent);
		params.add(new ParameterAdapter("Final temperature", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return temperatureEnd;
			}

		});
		params.add(new ParameterAdapter("Initial temperature", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.FLOAT) {

			@Override
			public Object value() {
				return temperatureInit;
			}

		});
		params.add(new ParameterAdapter("Coefficient of diminution", IExperimentSpecies.BATCH_CATEGORY_NAME,
			IType.FLOAT) {

			@Override
			public Object value() {
				return tempDimCoeff;
			}

		});
		params.add(new ParameterAdapter("Number of iterations at constant temperature",
			IExperimentSpecies.BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return temperatureEnd;
			}

		});
	}

}
