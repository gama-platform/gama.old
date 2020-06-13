/*******************************************************************************************************
 *
 * msi.gaml.architecture.weighted_tasks.WeightedTasksArchitecture.java, in plugin msi.gama.core, is part of the source
 * code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.rule_based;

import static one.util.streamex.StreamEx.of;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.statements.IStatement;

/**
 * The class RuleBasedArchitecture. A simple architecture of competing rules. Conditions and priorities of the rules are
 * computed every step and the rules executed are the ones which fulfil their condition, in the order of their
 * priorities
 *
 * task t1 weight: a_float { ... } task t2 weight: another_float {...}
 *
 * @author drogoul
 * @since 21 dec. 2011
 *
 */
@skill (
		name = RuleBasedArchitecture.RULES,
		concept = { IConcept.ARCHITECTURE, IConcept.BEHAVIOR },
		doc = @doc ("A control architecture based on the concept of rules"))
public class RuleBasedArchitecture extends ReflexArchitecture {

	public static final String RULES = "rules";
	List<RuleStatement> rules = new ArrayList<>();

	@Override
	protected void clearBehaviors() {
		super.clearBehaviors();
		rules.clear();
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		// we let a chance to the reflexes, etc. to execute
		super.executeOn(scope);
		final Map<RuleStatement, Double> priorities = of(rules).toMap(r -> r.computePriority(scope));
		final List<RuleStatement> rulesToRun = of(rules).filter(r -> r.computeCondition(scope))
				.reverseSorted((o1, o2) -> priorities.get(o1).compareTo(priorities.get(o2))).toList();
		Object result = null;
		for (final RuleStatement rule : rulesToRun) {
			final ExecutionResult er = scope.execute(rule);
			if (!er.passed()) { return result; }
			result = er.getValue();
		}
		return result;
	}

	@Override
	public void addBehavior(final IStatement c) {
		if (c instanceof RuleStatement) {
			rules.add((RuleStatement) c);
		} else {
			super.addBehavior(c);
		}
	}

}
