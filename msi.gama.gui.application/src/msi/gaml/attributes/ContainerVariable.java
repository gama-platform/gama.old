/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.attributes;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gama.util.matrix.*;

@facets({ @facet(name = ISymbol.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = ISymbol.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = ISymbol.INIT, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.VALUE, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.CONST, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.SIZE, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.OF, type = IType.TYPE_ID, optional = true),
	@facet(name = ISymbol.FILL_WITH, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.DEPENDS_ON, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.INITER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.GETTER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.SETTER, type = IType.LABEL, optional = true) })
@symbol(name = { IType.LIST_STR, IType.MATRIX_STR, IType.FILE_STR, IType.STRING_STR,
	IType.CONTAINER_STR, IType.COLOR_STR, IType.PAIR_STR, IType.MAP_STR, IType.POINT_STR,
	IType.GRAPH_STR }, kind = ISymbolKind.VARIABLE)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT })
public class ContainerVariable extends Variable {

	private GamaPoint size;

	public ContainerVariable(final IDescription sd) throws GamlException, GamaRuntimeException {
		super(sd);
	}

	@Override
	public void initializeWith(final IScope scope, final IAgent owner, final Object v)
		throws GamaRuntimeException {
		super.initializeWith(scope, owner, v);
		final IExpression size = getFacet(ISymbol.SIZE);
		if ( size != null ) {
			setSize(scope, owner, scope.evaluate(size, owner));
		}
		final IExpression with = getFacet(ISymbol.FILL_WITH);
		if ( with != null ) {
			setInitialValues(scope, owner, with);
		}
	}

	private void setInitialValues(final IScope scope, final IAgent owner, final IExpression initial)
		throws GamaRuntimeException {
		final Object val = value(owner);
		if ( val == null ) { return; }
		if ( !(val instanceof IGamaContainer) ) { return; }
		Object o = scope.evaluate(initial, owner);
		((IGamaContainer) val).putAll(o, null);
	}

	private void setSize(final IScope scope, final IAgent owner, final Object value)
		throws GamaRuntimeException {
		IGamaContainer result = null;
		size = value instanceof GamaPoint ? (GamaPoint) value : new GamaPoint(Cast.asInt(value), 1);
		switch (this.type().id()) {
			case IType.MATRIX: {
				Object v = value(owner);
				if ( !(v instanceof IMatrix) ) {
					v = null;
				}
				switch (this.getContentType().id()) {

					case IType.FLOAT:
						result =
							v == null ? new GamaFloatMatrix(size) : GamaFloatMatrix.from(
								(int) size.x, (int) size.y, (IMatrix) v);
						break;
					case IType.INT:
						result =
							v == null ? new GamaIntMatrix(size) : GamaIntMatrix.from((int) size.x,
								(int) size.y, (IMatrix) v);
						break;
					default:
						result =
							v == null ? new GamaObjectMatrix(size) : GamaObjectMatrix.from(
								(int) size.x, (int) size.y, (IMatrix) v);
				}
				break;
			}
			case IType.LIST: {
				result = new GamaList<Object>((int) size.x);
				Object v = value(owner);
				if ( v instanceof GamaList ) {
					if ( ((GamaList) v).size() < size.x ) {
						((GamaList) result).addAll((GamaList) v);
					} else {
						((GamaList) result).addAll(((GamaList) v).subList(0, (int) size.x));
					}
				}
			}
		}
		if ( result == null ) { return; }
		_setVal(owner, scope, result);
	}

	@Override
	protected Object coerce(final IAgent agent, final IScope scope, final Object v)
		throws GamaRuntimeException {
		Object result = type.cast(scope, v, size);
		return result;
	}

}
