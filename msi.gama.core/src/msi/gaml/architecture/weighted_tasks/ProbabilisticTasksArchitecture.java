/**
 * Created by drogoul, 22 déc. 2011
 * 
 */
package msi.gaml.architecture.weighted_tasks;

import java.util.*;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * The class ProbabilisticTasksArchitecture. Contrary to its parent, this class uses the weights
 * as a support for making a weighted probabilistic choice among the different tasks. If all tasks
 * have the same weight, one is randomly chosen each step.
 * 
 * @author drogoul
 * @since 22 déc. 2011
 * 
 */
@skill(name = ProbabilisticTasksArchitecture.PT)
public class ProbabilisticTasksArchitecture extends WeightedTasksArchitecture {

	public final static String PT = "probabilistic_tasks";
	double[] weights;

	@Override
	protected WeightedTaskStatement chooseTask(final IScope scope) throws GamaRuntimeException {
		Double sum = 0d;
		for ( int i = 0; i < weights.length; i++ ) {
			double weight = tasks.get(i).computeWeight(scope);
			sum += weight;
			weights[i] = weight;
		}
		Double choice = GAMA.getRandom().between(0d, sum);
		sum = 0d;
		for ( int i = 0; i < weights.length; i++ ) {
			double weight = weights[i];
			if ( choice > sum && choice <= sum + weight ) { return tasks.get(i); }
			sum += weight;
		}
		return null;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		super.setChildren(commands);
		weights = new double[tasks.size()];
		Arrays.fill(weights, 0d);
	}

}
