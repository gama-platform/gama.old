/**
 * Created by drogoul, 22 déc. 2011
 * 
 */
package msi.gaml.architecture.weighted_tasks;

import java.util.*;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * The class SortedTasksArchitecture. In this architecture, the tasks are all executed in
 * the order specified by their weights (biggest first)
 * 
 * @author drogoul
 * @since 22 déc. 2011
 * 
 */
@skill(SortedTasksArchitecture.ST)
public class SortedTasksArchitecture extends WeightedTasksArchitecture {

	public static final String ST = "sorted_tasks";
	final Map<WeightedTaskCommand, Double> weights = new HashMap();
	Comparator<WeightedTaskCommand> sortBlock = new Comparator<WeightedTaskCommand>() {

		@Override
		public int compare(final WeightedTaskCommand o1, final WeightedTaskCommand o2) {
			return weights.get(o1).compareTo(weights.get(o2));
		}

	};

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		// we let a chance to the reflexes, etc. to execute
		super.executeOn(scope);
		// We first compute the weights and cache them in the "weights" map
		for ( Map.Entry<WeightedTaskCommand, Double> entry : weights.entrySet() ) {
			entry.setValue(entry.getKey().computeWeight(scope));
		}
		// We then sort the tasks by their respective weight (from the smallest to the biggest)
		Collections.sort(tasks, sortBlock);
		// And we execute all the tasks in the reverse order (beginning by the heaviest)
		Object result = null;
		for ( int i = tasks.size() - 1; i >= 0; i-- ) {
			result = tasks.get(i).executeOn(scope);
		}
		return result;
	}

	@Override
	protected WeightedTaskCommand chooseTask(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		super.setChildren(commands);
		for ( WeightedTaskCommand c : tasks ) {
			weights.put(c, 0d);
		}
	}

}
