/*******************************************************************************************************
 *
 * msi.gaml.architecture.weighted_tasks.ProbabilisticTasksArchitecture.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.weighted_tasks;

import java.util.Arrays;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * The class ProbabilisticTasksArchitecture. Contrary to its parent, this class uses the weights as a support for making
 * a weighted probabilistic choice among the different tasks. If all tasks have the same weight, one is randomly chosen
 * each step.
 * 
 * @author drogoul
 * @since 22 dec. 2011
 * 
 */
@skill (
		name = ProbabilisticTasksArchitecture.PT,
		concept = { IConcept.ARCHITECTURE, IConcept.BEHAVIOR, IConcept.TASK_BASED },
		doc = @doc ("A control architecture, based on the concept of tasks, which are executed with a probability depending on their weight"))
public class ProbabilisticTasksArchitecture extends WeightedTasksArchitecture {

	public final static String PT = "probabilistic_tasks";
	double[] weights;

	@Override
	protected WeightedTaskStatement chooseTask(final IScope scope) throws GamaRuntimeException {
		Double sum = 0d;
		for (int i = 0; i < weights.length; i++) {
			final double weight = tasks.get(i).computeWeight(scope);
			sum += weight;
			weights[i] = weight;
		}
		final Double choice = scope.getRandom().between(0d, sum);
		sum = 0d;
		for (int i = 0; i < weights.length; i++) {
			final double weight = weights[i];
			if (choice > sum && choice <= sum + weight) { return tasks.get(i); }
			sum += weight;
		}
		return null;
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		super.setChildren(commands);
		weights = new double[tasks.size()];
		Arrays.fill(weights, 0d);
	}

}
