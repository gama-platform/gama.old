/*******************************************************************************************************
 *
 * msi.gaml.statements.ActionStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import static msi.gama.util.Collector.getOrderedSet;
import static msi.gaml.compilation.GAML.getExpressionFactory;
import static msi.gaml.types.Types.intFloatCase;

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
import msi.gama.util.ICollector;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer.StatementSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.statements.ActionStatement.ActionSerializer;
import msi.gaml.statements.ActionStatement.ActionValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class ActionCommand.
 *
 * @author drogoul
 */
@symbol (
		name = IKeyword.ACTION,
		kind = ISymbolKind.ACTION,
		with_sequence = true,
		with_args = true,
		unique_name = true,
		concept = { IConcept.SPECIES, IConcept.ACTION })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				doc = @doc ("identifier of the action")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("the action returned type"),
						internal = true),
				@facet (
						name = IKeyword.OF,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("if the action returns a container, the type of its elements"),
						internal = true),
				@facet (
						name = IKeyword.INDEX,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("if the action returns a map, the type of its keys"),
						internal = true),
				@facet (
						name = IKeyword.VIRTUAL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("whether the action is virtual (defined without a set of instructions) (false by default)")), },
		omissible = IKeyword.NAME)
@doc (
		value = "Allows to define in a species, model or experiment a new action that can be called elsewhere.",
		usages = { @usage (
				value = "The simplest syntax to define an action that does not take any parameter and does not return anything is:",
				examples = { @example (
						value = "action simple_action {",
						isExecutable = false),
						@example (
								value = "   // [set of statements]",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "If the action needs some parameters, they can be specified betwee, braquets after the identifier of the action:",
						examples = { @example (
								value = "action action_parameters(int i, string s){",
								isExecutable = false),
								@example (
										value = "   // [set of statements using i and s]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "If the action returns any value, the returned type should be used instead of the \"action\" keyword. A return statement inside the body of the action statement is mandatory.",
						examples = { @example (
								value = "int action_return_val(int i, string s){",
								isExecutable = false),
								@example (
										value = "   // [set of statements using i and s]",
										isExecutable = false),
								@example (
										value = "   return i + i;",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "If virtual: is true, then the action is abstract, which means that the action is defined without body. A species containing at least one abstract action is abstract. Agents of this species cannot be created. The common use of an abstract action is to define an action that can be used by all its sub-species, which should redefine all abstract actions and implements its body.",
						examples = { @example (
								value = "species parent_species {",
								isExecutable = false),
								@example (
										value = "   int virtual_action(int i, string s);",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false),
								@example (
										value = "",
										isExecutable = false),
								@example (
										value = "species children parent: parent_species {",
										isExecutable = false),
								@example (
										value = "   int virtual_action(int i, string s) {",
										isExecutable = false),
								@example (
										value = "      return i + i;",
										isExecutable = false),
								@example (
										value = "   }",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) },
		see = { "do" })
@validator (ActionValidator.class)
@serializer (ActionSerializer.class)
@SuppressWarnings ({ "rawtypes" })
public class ActionStatement extends AbstractStatementSequenceWithArgs {

	public static class ActionSerializer extends StatementSerializer {

		@Override
		protected String serializeFacetValue(final SymbolDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(TYPE)) return null;
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

		@Override
		protected void serializeArg(final IDescription desc, final IDescription arg, final StringBuilder sb,
				final boolean includingBuiltIn) {
			final String name = arg.getLitteral(NAME);
			final IExpressionDescription type = arg.getFacet(TYPE);
			final IExpressionDescription def = arg.getFacet(DEFAULT);

			sb.append(type == null ? "unknown" : type.serialize(includingBuiltIn)).append(" ").append(name);
			if (def != null) { sb.append(" <- ").append(def.serialize(includingBuiltIn)); }
		}

		@Override
		protected void serializeKeyword(final SymbolDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			String type = desc.getGamlType().serialize(includingBuiltIn);
			if (type.equals(UNKNOWN)) { type = ACTION; }
			sb.append(type).append(" ");
		}

	}

	public static class ActionValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			if (Assert.nameIsValid(description)) { assertReturnedValueIsOk((ActionDescription) description); }

		}

		private void assertReturnedValueIsOk(final ActionDescription cd) {
			final IType at = cd.getGamlType();
			if (at == Types.NO_TYPE) return;
			try (final ICollector<StatementDescription> returns = getOrderedSet()) {
				final DescriptionVisitor<IDescription> finder = (desc) -> {
					if (desc.getKeyword().equals(RETURN)) { returns.add((StatementDescription) desc); }
					return true;
				};
				cd.visitOwnChildrenRecursively(finder);
				if (returns.isEmpty()) {
					if (!cd.isAbstract()) {
						cd.error("Action " + cd.getName() + " must return a result of type " + at,
								IGamlIssue.MISSING_RETURN);
						return;
					}
				}
				for (final StatementDescription ret : returns) {
					final IExpression ie = ret.getFacetExpr(VALUE);
					if (ie == null) { continue; }
					if (ie.equals(IExpressionFactory.NIL_EXPR)) {
						if (at.getDefault() != null) {
							ret.error(
									"'nil' is not an acceptable return value. A valid " + at + " is expected instead.",
									IGamlIssue.WRONG_TYPE, VALUE);
						} else {
							continue;
						}
					} else {
						final IType<?> rt = ie.getGamlType();
						if (!rt.isTranslatableInto(at)) {
							ret.error("Action " + cd.getName() + " must return a result of type " + at + " (and not "
									+ rt + ")", IGamlIssue.SHOULD_CAST, VALUE, at.toString());
						} else if (intFloatCase(rt, at) || intFloatCase(rt.getContentType(), at.getContentType())) {
							// See Issue #3059
							ret.warning("The returned value (of type " + rt + ") will be casted to " + at,
									IGamlIssue.WRONG_TYPE, VALUE);
							ret.setFacet(VALUE, getExpressionFactory().createAs(cd.getSpeciesContext(), ie,
									getExpressionFactory().createTypeExpression(at)));
						}
					}
				}
			}
			// FIXME This assertion is still simple (i.e. the tree is not
			// verified to ensure that every
			// branch returns something)
		}

	}

	Arguments formalArgs = new Arguments();

	/**
	 * The Constructor.
	 *
	 * @param actionDesc
	 *            the action desc
	 * @param sim
	 *            the sim
	 */
	public ActionStatement(final IDescription desc) {
		super(desc);
		if (hasFacet(IKeyword.NAME)) { name = getLiteral(IKeyword.NAME); }

	}

	@Override
	public void leaveScope(final IScope scope) {
		// Clears any _action_halted status present
		scope.popAction();
		super.leaveScope(scope);
	}

	@Override
	public void setRuntimeArgs(final IScope scope, final Arguments args) {
		super.setRuntimeArgs(scope, args);
		actualArgs.get().complementWith(formalArgs);
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		formalArgs.putAll(args);
	}
}
