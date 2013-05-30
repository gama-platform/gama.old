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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.variables;

import java.util.Arrays;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@facets(value = { @facet(name = IKeyword.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.VALUE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.UPDATE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.FUNCTION, type = IType.NONE, optional = true),
	@facet(name = IKeyword.CONST, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.SIZE, type = { IType.INT, IType.POINT }, optional = true),
	@facet(name = IKeyword.OF, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INDEX, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.FILL_WITH, type = IType.NONE, optional = true) }, omissible = IKeyword.NAME)
@symbol(kind = ISymbolKind.Variable.CONTAINER, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
public class ContainerVariable extends Variable {

	private GamaPoint size;
	private final IExpression sizeExpr;
	private final IExpression fillExpr;

	public ContainerVariable(final IDescription sd) {
		super(sd);
		sizeExpr = getFacet(IKeyword.SIZE);
		fillExpr = getFacet(IKeyword.FILL_WITH);
	}

	@Override
	public void initializeWith(final IScope scope, final IAgent owner, final Object v) throws GamaRuntimeException {
		super.initializeWith(scope, owner, v);
		if ( sizeExpr != null ) {
			setSize(scope, owner, scope.evaluate(sizeExpr, owner));
		}

	}

	private void setSize(final IScope scope, final IAgent owner, final Object value) throws GamaRuntimeException {
		IContainer result = null;
		size = value instanceof ILocation ? (GamaPoint) value : new GamaPoint(Cast.asInt(scope, value), 1);
		switch (this.getType().id()) {
			case IType.MATRIX: {
				Object v = value(scope, owner);
				if ( !(v instanceof IMatrix) ) {
					v = null;
				}
				switch (description.getContentType().id()) {

					case IType.FLOAT:
						result =
							v == null ? new GamaFloatMatrix(scope, size) : GamaFloatMatrix.from(scope, (int) size.x,
								(int) size.y, (IMatrix) v);
						break;
					case IType.INT:
						result =
							v == null ? new GamaIntMatrix(scope, size) : GamaIntMatrix.from(scope, (int) size.x,
								(int) size.y, (IMatrix) v);
						break;
					default:
						result =
							v == null ? new GamaObjectMatrix(scope, size) : GamaObjectMatrix.from(scope, (int) size.x,
								(int) size.y, (IMatrix) v);
				}
				final Object o =
					fillExpr == null ? description.getContentType().getDefault() : scope.evaluate(fillExpr, owner);
				((IMatrix) result).add(scope, null, o, null, true, false);
				break;
			}
			case IType.LIST: {
				final Object[] contents = new Object[Cast.asInt(scope, value)];
				final Object o =
					fillExpr == null ? description.getContentType().getDefault() : scope.evaluate(fillExpr, owner);
				Arrays.fill(contents, o);
				result = new GamaList(contents);
			}
		}
		if ( result == null ) { return; }
		_setVal(owner, scope, result);
	}

	@Override
	protected Object coerce(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		final Object result = type.cast(scope, v, size);
		return result;
	}

}
