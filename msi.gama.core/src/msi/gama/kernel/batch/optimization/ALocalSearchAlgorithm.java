/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.LocalSearchAlgorithm.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.kernel.batch.optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import msi.gama.kernel.batch.Neighborhood;
import msi.gama.kernel.batch.Neighborhood1Var;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public abstract class ALocalSearchAlgorithm extends AOptimizationAlgorithm {

	protected static final String INIT_SOL = "init_solution";
	
	protected Neighborhood neighborhood;
	protected ParametersSet solutionInit;

	protected IExpression initSolExpression;
	public ALocalSearchAlgorithm(final IDescription species) {
		super(species);
	}
	
	public Map<ParametersSet, Double>  testSolutions(List<ParametersSet> solutions) {
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
		Map<ParametersSet, Double> res = currentExperiment.launchSimulationsWithSolution(solTotest)
				.entrySet().stream().collect(Collectors.toMap(
						e -> e.getKey(), 
						e -> (Double) e.getValue().get(IKeyword.FITNESS).get(0))
						);
		testedSolutions.putAll(res);
		results.putAll(res);
		
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
			Map<String,Object> vals = Cast.asMap(scope, initSolExpression.value(scope), false);
			if (vals != null) {
				initSolution(scope,vals);
			}
		}
	}
	
	public void initSolution(final IScope scope, Map<String, Object> initVals) {
		for (String name : initVals.keySet()) {
			if (solutionInit.containsKey(name)) {
				solutionInit.put(name, initVals.get(name));
			}
		}
	}

}
