/*******************************************************************************************************
 *
 * msi.gaml.variables.ContainerVariable.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.variables;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.types.IType;
import msi.gaml.variables.ContainerVariable.ContainerVarValidator;

@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.NEW_VAR_ID,
				optional = false,
				doc = @doc ("The name of the attribute")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("The type of the attribute")),
				@facet (
						name = IKeyword.INIT,
						// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
						type = IType.NONE,
						optional = true,
						doc = @doc ("The initial value of the attribute")),
				@facet (
						name = IKeyword.VALUE,
						type = IType.NONE,
						optional = true,
						doc = @doc (
								value = "",
								deprecated = "Use 'update' instead")),
				@facet (
						name = IKeyword.UPDATE,
						// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
						type = IType.NONE,
						optional = true,
						doc = @doc ("An expression that will be evaluated each cycle to compute a new value for the attribute")),
				@facet (
						name = IKeyword.FUNCTION,
						// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
						type = IType.NONE,
						optional = true,
						doc = @doc ("Used to specify an expression that will be evaluated each time the attribute is accessed. This facet is incompatible with both 'init:' and 'update:'")),
				@facet (
						name = IKeyword.CONST,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates whether this attribute can be subsequently modified or not")),
				@facet (
						name = IKeyword.CATEGORY,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("Soon to be deprecated. Declare the parameter in an experiment instead")),
				@facet (
						name = IKeyword.PARAMETER,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("Soon to be deprecated. Declare the parameter in an experiment instead")),
				@facet (
						name = IKeyword.SIZE,
						type = { IType.INT, IType.POINT },
						optional = true,
						doc = @doc (
								value = "",
								deprecated = "Use the operator matrix_with(size, fill_with) or list_with(size, fill_with) instead")),
				@facet (
						name = IKeyword.ON_CHANGE,
						type = IType.NONE,
						optional = true,
						doc = @doc ("Provides a block of statements that will be executed whenever the value of the attribute changes")),

				@facet (
						name = IKeyword.OF,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("The type of the contents of this container attribute")),
				@facet (
						name = IKeyword.INDEX,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("The type of the key used to retrieve the contents of this attribute")),
				@facet (
						name = IKeyword.FILL_WITH,
						type = IType.NONE,
						optional = true,
						doc = @doc (
								value = "",
								deprecated = "Use the operator matrix_with(size, fill_with) or list_with(size, fill_with) instead")) },
		omissible = IKeyword.NAME)
@symbol (
		kind = ISymbolKind.Variable.CONTAINER,
		with_sequence = false,
		concept = { IConcept.CONTAINER })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@doc ("Allows to declare an attribute of a species or an experiment")
@validator (ContainerVarValidator.class)
public class ContainerVariable extends Variable {

	public static class ContainerVarValidator extends VarValidator {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription vd) {
			// Replaces the size: and fill_with: facets with an operator
			// depending on the type of the container
			if (vd.hasFacet(SIZE)) {
				final IExpression size = vd.getFacetExpr(SIZE);
				IExpression fill = vd.getFacetExpr(FILL_WITH);
				if (fill == null) {
					fill = IExpressionFactory.NIL_EXPR;
				}
				final IType<?> type = vd.getGamlType();
				switch (type.id()) {
					case IType.LIST:
						if (size.getGamlType().id() != IType.INT) {
							vd.error("Facet 'size:' must be of type int", IGamlIssue.WRONG_TYPE, SIZE, "int");
							return;
						}
						IExpression init =
								GAML.getExpressionFactory().createOperator("list_with", vd, null, size, fill);
						vd.setFacet(INIT, init);
						break;
					case IType.MATRIX:
						if (size.getGamlType().id() != IType.POINT) {
							vd.error("Facet 'size:' must be of type point", IGamlIssue.WRONG_TYPE, SIZE, "point");
							return;
						}

						init = GAML.getExpressionFactory().createOperator("matrix_with", vd, null, size, fill);
						vd.setFacet(INIT, init);
						break;
					default:
						vd.error("Facet 'size:' can only be used for lists and matrices", IGamlIssue.UNKNOWN_FACET,
								SIZE);
						return;
				}
			} else if (vd.hasFacet(FILL_WITH)) {
				vd.error("Facet 'size:' missing. A container cannot be filled if no size is provided",
						IGamlIssue.MISSING_FACET, vd.getUnderlyingElement(), SIZE, "0");
				return;
			}
			super.validate(vd);
		}
	}

	public ContainerVariable(final IDescription sd) {
		super(sd);
	}

}
