/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.variables;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import msi.gaml.variables.ContainerVariable.ContainerVarValidator;

@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.VALUE, type = IType.NONE, optional = true, doc = @doc(value = "", deprecated = "Use 'update' instead")),
	@facet(name = IKeyword.UPDATE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.FUNCTION, type = IType.NONE, optional = true),
	@facet(name = IKeyword.CONST, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.SIZE, type = { IType.INT, IType.POINT }, optional = true, doc = @doc(value = "", deprecated = "Use the operator matrix_with(size, fill_with) or list_with(size, fill_with) instead")),
	@facet(name = IKeyword.OF, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INDEX, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.FILL_WITH, type = IType.NONE, optional = true, doc = @doc(value = "", deprecated = "Use the operator matrix_with(size, fill_with) or list_with(size, fill_with) instead")) }, omissible = IKeyword.NAME)
@symbol(kind = ISymbolKind.Variable.CONTAINER, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@validator(ContainerVarValidator.class)
public class ContainerVariable extends Variable {

	public static class ContainerVarValidator extends VarValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription vd) {
			Facets ff = vd.getFacets();
			// Replaces the size: and fill_with: facets with an operator depending on the type of the container
			if ( ff.containsKey(SIZE) ) {
				IExpression size = ff.getExpr(SIZE);
				IExpression fill = ff.getExpr(FILL_WITH);
				if ( fill == null ) {
					fill = IExpressionFactory.NIL_EXPR;
				}
				IType type = vd.getType();
				switch (type.id()) {
					case IType.LIST:
						if ( size.getType().id() != IType.INT ) {
							vd.error("Facet 'size:' must be of type int", IGamlIssue.WRONG_TYPE, SIZE, "int");
							return;
						}
						IExpression init =
							GAML.getExpressionFactory().createOperator("list_with", vd, null, size, fill);
						ff.put(INIT, init);
						break;
					case IType.MATRIX:
						if ( size.getType().id() != IType.POINT ) {
							vd.error("Facet 'size:' must be of type point", IGamlIssue.WRONG_TYPE, SIZE, "point");
							return;
						}

						init = GAML.getExpressionFactory().createOperator("matrix_with", vd, null, size, fill);
						ff.put(INIT, init);
						break;
					default:
						vd.error("Facet 'size:' can only be used for lists and matrices", IGamlIssue.UNKNOWN_FACET,
							SIZE);
						return;
				}
			} else if ( ff.containsKey(FILL_WITH) ) {
				vd.error("Facet 'size:' missing. A container cannot be filled if no size is provided",
					IGamlIssue.MISSING_FACET, FILL_WITH);
				return;
			}
			super.validate(vd);
		}
	}

	// private GamaPoint size;
	// private final IExpression sizeExpr;
	// private final IExpression fillExpr;

	public ContainerVariable(final IDescription sd) {
		super(sd);
		// sizeExpr = getFacet(IKeyword.SIZE);
		// fillExpr = getFacet(IKeyword.FILL_WITH);
	}

	// @Override
	// public void initializeWith(final IScope scope, final IAgent owner, final Object v) throws GamaRuntimeException {
	// super.initializeWith(scope, owner, v);
	// if ( sizeExpr != null ) {
	// setSize(scope, owner, scope.evaluate(sizeExpr, owner));
	// }
	//
	// }
	//
	// private void setSize(final IScope scope, final IAgent owner, final Object value) throws GamaRuntimeException {
	// IContainer result = null;
	// size = value instanceof ILocation ? (GamaPoint) value : new GamaPoint(Cast.asInt(scope, value), 1);
	// switch (type.id()) {
	// case IType.MATRIX:
	// result = GamaMatrixType.with(scope, fillExpr, size);
	// break;
	// case IType.LIST: {
	// result = GamaListType.with(scope, fillExpr, Cast.asInt(scope, value));
	// }
	// }
	// if ( result == null ) { return; }
	// _setVal(owner, scope, result);
	// }
	//
	// @Override
	// protected Object coerce(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
	// final Object result = type.cast(scope, v, size);
	// return result;
	// }

}
