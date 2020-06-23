/*******************************************************************************************************
 *
 * msi.gaml.statements.SwitchStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.IStatement.Breakable;
import msi.gaml.statements.SwitchStatement.SwitchValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * IfPrototype.
 *
 * @author drogoul 14 nov. 07
 */
@symbol (
		name = IKeyword.SWITCH,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		concept = { IConcept.CONDITION })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets (
		value = { @facet (
				name = IKeyword.VALUE,
				type = IType.NONE,
				optional = false,
				doc = @doc ("an expression")) },
		omissible = IKeyword.VALUE)
@doc (
		value = "The \"switch... match\" statement is a powerful replacement for imbricated \"if ... else ...\" constructs. All the blocks that match are executed in the order they are defined. The block prefixed by default is executed only if none have matched (otherwise it is not).",
		usages = { @usage (
				value = "The prototypical syntax is as follows:",
				examples = { @example (
						value = "switch an_expression {",
						isExecutable = false),
						@example (
								value = "        match value1 {...}",
								isExecutable = false),
						@example (
								value = "        match_one [value1, value2, value3] {...}",
								isExecutable = false),
						@example (
								value = "        match_between [value1, value2] {...}",
								isExecutable = false),
						@example (
								value = "        default {...}",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "Example:",
						examples = { @example (
								value = "switch 3 {",
								test = false),
								@example (
										value = "   match 1 {write \"Match 1\"; }",
										test = false),
								@example (
										value = "   match 2 {write \"Match 2\"; }",
										test = false),
								@example (
										value = "   match 3 {write \"Match 3\"; }",
										test = false),
								@example (
										value = "   match_one [4,4,6,3,7]  {write \"Match one_of\"; }",
										test = false),
								@example (
										value = "   match_between [2, 4] {write \"Match between\"; }",
										test = false),
								@example (
										value = "   default {write \"Match Default\"; }",
										test = false),
								@example (
										value = "}",
										test = false),
								@example (
										value = "string val1 <- \"\";",
										test = false,
										isTestOnly = true),
								@example (
										value = "switch 1 {",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match 1 {val1 <- val1 + \"1\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match 2 {val1 <- val1 + \"2\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match_one [1,1,6,4,7]  {val1 <- val1 + \"One_of\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match_between [2, 4] {val1 <- val1 + \"Between\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   default {val1 <- val1 + \"Default\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "}",
										test = false,
										isTestOnly = true),
								@example (
										var = "val1",
										equals = "'1One_of'",
										isTestOnly = true),
								@example (
										value = "string val2 <- \"\";",
										test = false,
										isTestOnly = true),
								@example (
										value = "switch 2 {",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match 1 {val2 <- val2 + \"1\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match 2 {val2 <- val2 + \"2\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match_one [1,1,6,4,7]  {val2 <- val2 + \"One_of\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match_between [2, 4] {val2 <- val2 + \"Between\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   default {val2 <- val2 + \"Default\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "}",
										test = false,
										isTestOnly = true),
								@example (
										var = "val2",
										equals = "'2Between'",
										isTestOnly = true),
								@example (
										value = "string val10 <- \"\";",
										test = false,
										isTestOnly = true),
								@example (
										value = "switch 10 {",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match 1 {val10 <- val10 + \"1\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match 2 {val10 <- val10 + \"2\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match_one [1,1,6,4,7]  {val10 <- val10 + \"One_of\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   match_between [2, 4] {val10 <- val10 + \"Between\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "   default {val10 <- val10 + \"Default\"; }",
										test = false,
										isTestOnly = true),
								@example (
										value = "}",
										test = false,
										isTestOnly = true),
								@example (
										var = "val10",
										equals = "'Default'",
										isTestOnly = true) }) },
		see = { IKeyword.MATCH, IKeyword.DEFAULT, IKeyword.IF })
@validator (SwitchValidator.class)
@SuppressWarnings ({ "rawtypes" })
public class SwitchStatement extends AbstractStatementSequence implements Breakable {

	public static class SwitchValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {

			// FIXME This assertion only verifies the case of "match" (not
			// match_one or match_between)
			final Iterable<IDescription> matches = desc.getChildrenWithKeyword(MATCH);
			final IExpression switchValue = desc.getFacetExpr(VALUE);
			if (switchValue == null) { return; }
			final IType switchType = switchValue.getGamlType();
			if (switchType.equals(Types.NO_TYPE)) { return; }
			for (final IDescription match : matches) {
				final IExpression value = match.getFacetExpr(VALUE);
				if (value == null) {
					continue;
				}
				final IType<?> matchType = value.getGamlType();
				// AD : special case introduced for ints and floats (a warning
				// is emitted)
				if (Types.intFloatCase(matchType, switchType)) {
					match.warning(
							"The value " + value.serialize(false) + " of type " + matchType
									+ " is compared to a value of type " + switchType + ", which will never match ",
							IGamlIssue.SHOULD_CAST, IKeyword.VALUE, switchType.toString());
					continue;
				}

				if (matchType.isTranslatableInto(switchType)) {
					continue;
				}
				match.warning(
						"The value " + value.serialize(false) + " of type " + matchType
								+ " is compared to a value of type " + switchType + ", which will never match ",
						IGamlIssue.SHOULD_CAST, IKeyword.VALUE, switchType.toString());
			}

		}

	}

	public MatchStatement[] matches;
	public MatchStatement defaultMatch;
	final IExpression value;

	/**
	 * The Constructor.
	 *
	 * @param sim
	 *            the sim
	 */
	public SwitchStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
		setName("switch" + value.serialize(false));

	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		final List<MatchStatement> cases = new ArrayList<>();
		for (final ISymbol c : commands) {
			if (c instanceof MatchStatement) {
				if (((MatchStatement) c).getKeyword().equals(IKeyword.DEFAULT)) {
					defaultMatch = (MatchStatement) c;
				} else {
					cases.add((MatchStatement) c);
				}
			}
		}
		matches = cases.toArray(new MatchStatement[cases.size()]);
		super.setChildren(Iterables.filter(commands, each -> each != defaultMatch || !cases.contains(each)));
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		boolean hasMatched = false;
		final Object switchValue = value.value(scope);
		Object lastResult = null;
		for (final MatchStatement matche : matches) {
			if (scope.interrupted()) { return lastResult; }
			if (matche.matches(scope, switchValue)) {
				final ExecutionResult er = scope.execute(matche);
				if (!er.passed()) { return lastResult; }
				lastResult = er.getValue();
				hasMatched = true;
			}
		}
		if (!hasMatched && defaultMatch != null) { return scope.execute(defaultMatch).getValue(); }
		return lastResult;
	}

	@Override
	public void leaveScope(final IScope scope) {
		// Clears any _loop_halted status
		scope.popLoop();
		super.leaveScope(scope);
	}
}