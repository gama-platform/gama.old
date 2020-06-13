/*******************************************************************************************************
 *
 * msi.gaml.architecture.weighted_tasks.SortedTasksArchitecture.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.weighted_tasks;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * The class SortedTasksArchitecture. In this architecture, the tasks are all executed in the order specified by their
 * weights (biggest first)
 * 
 * @author drogoul
 * @since 22 d�c. 2011
 * 
 */
@skill (
		name = SortedTasksArchitecture.ST,
		concept = { IConcept.ARCHITECTURE, IConcept.BEHAVIOR, IConcept.TASK_BASED },
		doc = @doc ("A control architecture, based on the concept of tasks, which are executed in an order defined by their weight"))
public class SortedTasksArchitecture extends WeightedTasksArchitecture {

	public static final String ST = "sorted_tasks";
	final Map<WeightedTaskStatement, Double> weights = new HashMap<>();
	Comparator<WeightedTaskStatement> sortBlock = (o1, o2) -> weights.get(o1).compareTo(weights.get(o2));

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		// we let a chance to the reflexes, etc. to execute
		super.executeOn(scope);
		// We first compute the weights and cache them in the "weights" map
		for (final Map.Entry<WeightedTaskStatement, Double> entry : weights.entrySet()) {
			entry.setValue(entry.getKey().computeWeight(scope));
		}
		// We then sort the tasks by their respective weight (from the smallest
		// to the biggest)
		Collections.sort(tasks, sortBlock);
		// And we execute all the tasks in the reverse order (beginning by the
		// heaviest)
		Object result = null;
		for (int i = tasks.size() - 1; i >= 0; i--) {
			final ExecutionResult er = scope.execute(tasks.get(i));
			if (!er.passed()) { return result; }
			result = er.getValue();
		}
		return result;
	}

	@Override
	protected WeightedTaskStatement chooseTask(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		super.setChildren(commands);
		for (final WeightedTaskStatement c : tasks) {
			weights.put(c, 0d);
		}
	}

}
