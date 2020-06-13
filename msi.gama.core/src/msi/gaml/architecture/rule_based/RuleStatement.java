/*******************************************************************************************************
 *
 * msi.gaml.architecture.weighted_tasks.WeightedTaskStatement.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.rule_based;

import static msi.gama.common.interfaces.IKeyword.WHEN;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

/**
 * The Class RuleStatement. A simple definition of a rule (set of statements which execution depend on a condition and a
 * priority).
 *
 * @author drogoul
 */

@symbol (
		name = RuleStatement.RULE,
		kind = ISymbolKind.BEHAVIOR,
		with_sequence = true,
		unique_name = true,
		concept = { IConcept.BEHAVIOR, IConcept.ARCHITECTURE })
@inside (
		symbols = { RuleBasedArchitecture.RULES },
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = WHEN,
				type = IType.BOOL,
				optional = false,
				doc = @doc ("The condition to fulfill in order to execute the statements embedded in the rule. when: true makes the rule always activable")),
				@facet (
						name = RuleStatement.PRIORITY,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("An optional priority for the rule, which is used to sort activable rules and run them in that order ")),
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						optional = false,
						doc = @doc ("the identifier of the rule")) },
		omissible = IKeyword.NAME)
@doc ("A simple definition of a rule (set of statements which execution depend on a condition and a priority).")
public class RuleStatement extends AbstractStatementSequence {

	protected static final String PRIORITY = "priority";
	protected static final String RULE = "rule";
	protected final IExpression priority, condition;

	public RuleStatement(final IDescription desc) {
		super(desc);
		setName(desc.getName());
		priority = getFacet(PRIORITY);
		condition = getFacet(WHEN);
	}

	public Double computePriority(final IScope scope) throws GamaRuntimeException {
		return priority == null ? 0d : Cast.asFloat(scope, priority.value(scope));
	}

	public Boolean computeCondition(final IScope scope) throws GamaRuntimeException {
		return Cast.asBool(scope, condition.value(scope));
	}

}
