/*********************************************************************************************
 * 
 * 
 * 'ProbabilisticTasksArchitecture.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.weighted_tasks;

import java.util.*;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * The class ProbabilisticTasksArchitecture. Contrary to its parent, this class uses the weights
 * as a support for making a weighted probabilistic choice among the different tasks. If all tasks
 * have the same weight, one is randomly chosen each step.
 * 
 * @author drogoul
 * @since 22 dec. 2011
 * 
 */
@skill(name = ProbabilisticTasksArchitecture.PT,
concept = { IConcept.ARCHITECTURE, IConcept.BEHAVIOR, IConcept.TASK_BASED })
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
		Double choice = scope.getRandom().between(0d, sum);
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
