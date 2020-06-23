/*******************************************************************************************************
 *
 * msi.gaml.architecture.finite_state_machine.FsmTransitionStatement.java, in plugin msi.gama.core, is part of the
 * source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.finite_state_machine;

import java.util.Arrays;
import java.util.List;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.finite_state_machine.FsmTransitionStatement.TransitionSerializer;
import msi.gaml.architecture.finite_state_machine.FsmTransitionStatement.TransitionValidator;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@symbol (
		name = FsmTransitionStatement.TRANSITION,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true)
@inside (
		kinds = { ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.BEHAVIOR })
@facets (
		value = { @facet (
				name = IKeyword.WHEN,
				type = IType.BOOL,
				optional = true,
				doc = @doc ("a condition to be fulfilled to have a transition to another given state")),
				@facet (
						name = FsmTransitionStatement.TO,
						type = IType.ID,
						optional = false,
						doc = @doc ("the identifier of the next state")) },
		omissible = IKeyword.WHEN)
@validator (TransitionValidator.class)
@serializer (TransitionSerializer.class)
@doc (
		value = "In an FSM architecture, `" + FsmTransitionStatement.TRANSITION
				+ "` specifies the next state of the life cycle. The transition occurs when the condition is fulfilled. The embedded statements are executed when the transition is triggered.",
		usages = { @usage (
				value = "In the following example, the transition is executed when after 2 steps:",
				examples = { @example (
						value = "	state s_init initial: true {",
						isExecutable = false),
						@example (
								value = "		write state;",
								isExecutable = false),
						@example (
								value = "		transition to: s1 when: (cycle > 2) {",
								isExecutable = false),
						@example (
								value = "			write \"transition s_init -> s1\";",
								isExecutable = false),
						@example (
								value = "		}",
								isExecutable = false),
						@example (
								value = "	}",
								isExecutable = false) }) },
		see = { FsmStateStatement.ENTER, FsmStateStatement.STATE, FsmStateStatement.EXIT })
public class FsmTransitionStatement extends AbstractStatementSequence {

	static final List<String> states = Arrays.asList(FsmStateStatement.STATE, IKeyword.USER_PANEL);

	public static class TransitionSerializer extends SymbolSerializer<SymbolDescription> {

		static String[] MY_FACETS = new String[] { TO, WHEN };

		@Override
		protected void serializeFacets(final SymbolDescription s, final StringBuilder sb,
				final boolean includingBuiltIn) {
			for (final String key : MY_FACETS) {

				final String expr = serializeFacetValue(s, key, includingBuiltIn);
				if (expr != null) {
					sb.append(serializeFacetKey(s, key, includingBuiltIn)).append(expr).append(" ");
				}
			}

		}
	}

	public static class TransitionValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
			final IDescription sup = desc.getEnclosingDescription();
			final String keyword = sup.getKeyword();
			if (!states.contains(keyword)) {
				desc.error("Transitions cannot be declared inside  " + keyword, IGamlIssue.WRONG_PARENT);
				return;
			}
			final IExpression expr = sup.getFacetExpr(FsmStateStatement.FINAL);
			if (IExpressionFactory.TRUE_EXPR.equals(expr)) {
				desc.error("Transitions are not accepted in final states", IGamlIssue.WRONG_PARENT);
				return;
			}
			final String behavior = desc.getLitteral(TO);
			final SpeciesDescription sd = desc.getSpeciesContext();
			if (!sd.hasBehavior(behavior)) {
				desc.error("Behavior " + behavior + " does not exist in " + sd.getName(), IGamlIssue.UNKNOWN_BEHAVIOR,
						TO, behavior, sd.getName());
			}

		}

	}

	final IExpression when;

	/** Constant field TRANSITION. */
	public static final String TRANSITION = "transition";

	protected static final String TO = "to";

	public FsmTransitionStatement(final IDescription desc) {
		super(desc);
		final String stateName = getLiteral(TO);
		setName(stateName);
		if (getFacet(IKeyword.WHEN) != null) {
			when = getFacet(IKeyword.WHEN);
		} else {
			when = IExpressionFactory.TRUE_EXPR;
		}
	}

	public boolean evaluatesTrueOn(final IScope scope) throws GamaRuntimeException {
		return Cast.asBool(scope, when.value(scope));
		// Normally, the agent is still in the "currentState" scope.
	}

}
