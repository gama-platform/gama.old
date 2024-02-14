/*******************************************************************************************************
 *
 * MatchStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * IfPrototype.
 *
 * @author drogoul 14 nov. 07
 */
@symbol (
		name = { IKeyword.MATCH, IKeyword.MATCH_BETWEEN, IKeyword.MATCH_ONE, IKeyword.MATCH_REGEX },
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		concept = { IConcept.CONDITION },
		with_sequence = true)
@inside (
		symbols = IKeyword.SWITCH)
@facets (
		value = { @facet (
				name = IKeyword.VALUE,
				type = IType.NONE,
				optional = false,
				doc = @doc ("The value or values this statement tries to match")) },
		omissible = IKeyword.VALUE)
@doc (
		value = "In a switch...match structure, the value of each match block is compared to the value in the switch. If they match, the embedded statement set is executed. Four kinds of match can be used, equality, containment, betweenness and regex matching",
		usages = { @usage (
				value = IKeyword.MATCH + " block is executed if the switch value is equals to the value of the match:",
				examples = { @example (
						value = "switch 3 {",
						test = false),
						@example (
								value = "   match 1 {write \"Match 1\"; }",
								test = false),
						@example (
								value = "   match 3 {write \"Match 2\"; }",
								test = false),
						@example (
								value = "}",
								test = false) }),
				@usage (
						value = IKeyword.MATCH_BETWEEN
								+ " block is executed if the switch value is in the interval given in value of the "
								+ IKeyword.MATCH_BETWEEN + ":",
						examples = { @example (
								value = "switch 3 {",
								test = false),
								@example (
										value = "   match_between [1,2] {write \"Match OK between [1,2]\"; }",
										test = false),
								@example (
										value = "   match_between [2,5] {write \"Match OK between [2,5]\"; }",
										test = false),
								@example (
										value = "}",
										test = false) }),
				@usage (
						value = IKeyword.MATCH_ONE
								+ " block is executed if the switch value is equals to one of the values of the "
								+ IKeyword.MATCH_ONE + ":",
						examples = { @example (
								value = "switch 3 {",
								test = false),
								@example (
										value = "   match_one [0,1,2] {write \"Match OK with one of [0,1,2]\"; }",
										test = false),
								@example (
										value = "   match_between [2,3,4,5] {write \"Match OK with one of [2,3,4,5]\"; }",
										test = false),
								@example (
										value = "}",
										test = false) }) },
		see = { IKeyword.SWITCH, IKeyword.DEFAULT })
@SuppressWarnings ({ "rawtypes" })
public class MatchStatement extends AbstractStatementSequence {

	/** The value. */
	final IExpression value;

	/** The constant value. */
	Object constantValue;

	/** The executer. */
	final MatchExecuter executer;

	/**
	 * Instantiates a new match statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public MatchStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
		final String keyword = desc.getKeyword();
		setName(keyword + " " + (value == null ? "" : value.serializeToGaml(false)));
		executer =
				IKeyword.MATCH.equals(keyword) ? new SimpleMatch() : IKeyword.MATCH_ONE.equals(keyword) ? new MatchOne()
						: IKeyword.MATCH_BETWEEN.equals(keyword) ? new MatchBetween()
						: IKeyword.MATCH_REGEX.equals(keyword) ? new MatchRegex() : null;
		if (executer != null) { executer.acceptValue(); }
	}

	/**
	 * Matches.
	 *
	 * @param scope
	 *            the scope
	 * @param switchValue
	 *            the switch value
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public boolean matches(final IScope scope, final Object switchValue) throws GamaRuntimeException {
		if (executer == null) return false;
		return executer.matches(scope, switchValue);
	}

	/**
	 * The Class MatchExecuter.
	 */
	abstract class MatchExecuter {

		/**
		 * Matches.
		 *
		 * @param scope
		 *            the scope
		 * @param switchValue
		 *            the switch value
		 * @return true, if successful
		 * @throws GamaRuntimeException
		 *             the gama runtime exception
		 */
		abstract boolean matches(IScope scope, Object switchValue) throws GamaRuntimeException;

		/**
		 * Accept value.
		 */
		void acceptValue() {
			if (value.isConst()) { constantValue = value.getConstValue(); }
		}

		/**
		 * Gets the value.
		 *
		 * @param scope
		 *            the scope
		 * @return the value
		 * @throws GamaRuntimeException
		 *             the gama runtime exception
		 */
		Object getValue(final IScope scope) throws GamaRuntimeException {
			return constantValue == null ? value.value(scope) : constantValue;
		}
	}

	/**
	 * The Class SimpleMatch.
	 */
	class SimpleMatch extends MatchExecuter {

		@Override
		public boolean matches(final IScope scope, final Object switchValue) throws GamaRuntimeException {
			final Object val = getValue(scope);
			return val == null ? switchValue == null : val.equals(switchValue);
		}

	}

	/**
	 * The Class MatchOne.
	 */
	class MatchOne extends MatchExecuter {

		@Override
		public boolean matches(final IScope scope, final Object switchValue) throws GamaRuntimeException {
			final Object val = getValue(scope);
			if (val instanceof IContainer) return ((IContainer) val).contains(scope, switchValue);
			return Cast.asList(scope, val).contains(switchValue);
		}

		@Override
		public void acceptValue() {
			super.acceptValue();
			if (constantValue != null && !(constantValue instanceof IContainer)
					&& !(constantValue instanceof GamaPoint)) {
				constantValue = Types.LIST.cast(null, constantValue, null, false);
			}
		}
	}

	/**
	 * The Class MatchRegex.
	 */
	class MatchRegex extends MatchExecuter {

		@Override
		public boolean matches(final IScope scope, final Object switchValue) throws GamaRuntimeException {
			final Object val = getValue(scope);
			if (!(switchValue instanceof String)) throw GamaRuntimeException.error(
					"Can only match strings against a regular expression. " + switchValue + " is not a string", scope);
			if (!(val instanceof String)) throw GamaRuntimeException
					.error("Can only match strings against a regular expression. " + val + " is not a string", scope);
			String target = (String) switchValue;
			String pattern = (String) val;
			if (pattern.isEmpty()) return false;
			try {
				Pattern p = Pattern.compile(pattern);
				return p.matcher(target).find();
			} catch (PatternSyntaxException e) {
				// throw GamaRuntimeException.error(
				// "The syntax of " + pattern + " is not a correct regular expression", scope);
				return target.contains(pattern);
			}
		}

		@Override
		public void acceptValue() {
			super.acceptValue();
			if (constantValue != null && !(constantValue instanceof String)) {
				constantValue = Types.STRING.cast(null, constantValue, null, false);
			}
		}
	}

	/**
	 * The Class MatchBetween.
	 */
	class MatchBetween extends MatchExecuter {

		@Override
		public boolean matches(final IScope scope, final Object switchValue) throws GamaRuntimeException {
			if (!(switchValue instanceof Number)) throw GamaRuntimeException
					.error("Can only match if a number is in an interval. " + switchValue + " is not a number", scope);
			Object val = value.value(scope);
			if (!(val instanceof GamaPoint)) { val = Cast.asPoint(scope, val); }
			final double min = ((GamaPoint) val).getX();
			final double max = ((GamaPoint) val).getY();
			final double in = ((Number) switchValue).doubleValue();
			return in >= min && in <= max;
		}

		/**
		 * @see msi.gaml.commands.MatchCommand.MatchExecuter#acceptValue()
		 */
		@Override
		public void acceptValue() {
			super.acceptValue();
			if (constantValue != null && !(constantValue instanceof GamaPoint)) {
				constantValue = Types.POINT.cast(null, constantValue, null, false);
			}

		}
	}

}