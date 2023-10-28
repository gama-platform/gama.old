/*******************************************************************************************************
 *
 * LetStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

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
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.LetStatement.LetSerializer;
import msi.gaml.statements.LetStatement.LetValidator;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 * @todo Description
 *
 */

@facets (
		value = { /* @facet(name = IKeyword.VAR, type = IType.NEW_TEMP_ID, optional = true), */
				@facet (
						name = IKeyword.NAME,
						type = IType.NEW_TEMP_ID,
						optional = false,
						doc = @doc ("The name of the variable declared ")),
				@facet (
						name = IKeyword.VALUE,
						type = { IType.NONE },
						optional = /* AD change false */true,
						doc = @doc ("The value assigned to this variable")),

				@facet (
						name = IKeyword.OF,
						type = { IType.TYPE_ID },
						optional = true,
						doc = @doc ("The type of the contents if this declaration concerns a container")),
				@facet (
						name = IKeyword.INDEX,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("The type of the index if this declaration concerns a container")),
				@facet (
						name = IKeyword.TYPE,
						type = { IType.TYPE_ID },
						optional = true,
						doc = @doc ("The type of the variable")) },
		omissible = IKeyword.NAME)
@symbol (
		name = { IKeyword.LET },
		kind = ISymbolKind.SINGLE_STATEMENT,
		concept = { IConcept.SYSTEM },
		with_sequence = false,
		doc = @doc ("Allows to declare a temporary variable and to initialize it with a value. The type can be provided, otherwise it is inferred from the right-hand expression. "))
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@validator (LetValidator.class)
@serializer (LetSerializer.class)
@doc ("Allows to declare a temporary variable of the specified type and to initialize it with a value")
public class LetStatement extends SetStatement {

	/**
	 * The Class LetSerializer.
	 */
	public static class LetSerializer extends AssignmentSerializer {

		@Override
		protected void serialize(final SymbolDescription desc, final StringBuilder sb, final boolean includingBuiltIn) {
			sb.append(desc.getGamlType().serializeToGaml(includingBuiltIn)).append(" ");
			super.serialize(desc, sb, includingBuiltIn);

		}

	}

	/**
	 * The Class LetValidator.
	 */
	public static class LetValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
			if (Assert.nameIsValid(cd)) {
				final IExpressionDescription receiver = cd.getFacet(NAME);
				final IExpression expr = receiver.getExpression();
				if (!(expr instanceof IVarExpression var)) {
					cd.error("The expression " + cd.getLitteral(NAME) + " is not a reference to a variable ", NAME);
					return;
				}
				final IExpressionDescription assigned = cd.getFacet(VALUE);
				if (assigned != null) {
					Assert.typesAreCompatibleForAssignment(VALUE, cd, Cast.toGaml(expr), expr.getGamlType(), assigned);
				}

			}
		}
	}

	/**
	 * Instantiates a new let statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public LetStatement(final IDescription desc) {
		super(desc);
		setName(IKeyword.LET + getVarName());
	}

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final Object val = value.value(scope);
		varExpr.setVal(scope, val, true);
		return val;
	}

}
