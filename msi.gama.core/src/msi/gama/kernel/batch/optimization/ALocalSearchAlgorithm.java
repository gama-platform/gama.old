/*******************************************************************************************************
 *
 * ALocalSearchAlgorithm.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.kernel.batch.optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import msi.gama.kernel.batch.Neighborhood;
import msi.gama.kernel.batch.Neighborhood1Var;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class ALocalSearchAlgorithm.
 */
public abstract class ALocalSearchAlgorithm extends AOptimizationAlgorithm {

	/** The Constant INIT_SOL. */
	protected static final String INIT_SOL = "init_solution";

	/** The neighborhood. */
	protected Neighborhood neighborhood;

	/** The solution init. */
	protected ParametersSet solutionInit;

	/** The init sol expression. */
	protected IExpression initSolExpression;

	/**
	 * Instantiates a new a local search algorithm.
	 *
	 * @param species
	 *            the species
	 */
	public ALocalSearchAlgorithm(final IDescription species) {
		super(species);
	}

	/**
	 * Test solutions.
	 *
	 * @param solutions
	 *            the solutions
	 * @return the map
	 */
	public Map<ParametersSet, Double> testSolutions(final List<ParametersSet> solutions) {
		Map<ParametersSet, Double> results = GamaMapFactory.create();
		solutions.removeIf(a -> a == null);
		List<ParametersSet> solTotest = new ArrayList<>();
		for (ParametersSet sol : solutions) {
			if (testedSolutions.containsKey(sol)) {
				results.put(sol, testedSolutions.get(sol));
			} else {
				solTotest.add(sol);
			}
		}
		BatchAgent agent = getCurrentExperiment();
		if (agent != null) {
			Map<ParametersSet, Double> res = agent.launchSimulationsWithSolution(solTotest).entrySet().stream()
					.collect(Collectors.toMap(Entry::getKey, e -> getFirstFitness(e.getValue())));
			testedSolutions.putAll(res);
			results.putAll(res);
		}
		return results;
	}

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		super.initializeFor(scope, agent);
		final List<IParameter.Batch> v = agent.getParametersToExplore();
		neighborhood = new Neighborhood1Var(v);
		solutionInit = new ParametersSet(scope, v, true);
		initSolExpression = getFacet(INIT_SOL);
		if (initSolExpression != null) {
			Map<String, Object> vals = Cast.asMap(scope, initSolExpression.value(scope), false);
			if (vals != null) { initSolution(scope, vals); }
		}
	}

	/**
	 * Inits the solution.
	 *
	 * @param scope
	 *            the scope
	 * @param initVals
	 *            the init vals
	 */
	public void initSolution(final IScope scope, final Map<String, Object> initVals) {
		for (String name : initVals.keySet()) {
			if (solutionInit.containsKey(name)) { solutionInit.put(name, initVals.get(name)); }
		}
	}

}
