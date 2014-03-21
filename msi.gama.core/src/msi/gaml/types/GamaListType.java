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
package msi.gaml.types;

import java.awt.Color;
import java.util.Collection;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.expressions.IExpression;

@type(name = IKeyword.LIST, id = IType.LIST, wraps = { IList.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaListType extends GamaContainerType<IList> {

	@Override
	public IList cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentsType) throws GamaRuntimeException {
		IList list = staticCast(scope, obj, contentsType);
		if ( contentsType != null && contentsType != Types.NO_TYPE ) {
			for ( int i = 0; i < list.size(); i++ ) {
				list.set(i, toType(scope, list.get(i), contentsType));
			}
		}
		return list;
	}

	public static IList staticCast(final IScope scope, final Object obj, final IType contentsType)
		throws GamaRuntimeException {
		if ( obj == null ) { return new GamaList(); }
		// if ( obj instanceof IList ) { return (IList) obj; }
		if ( obj instanceof IContainer ) { return ((IContainer) obj).listValue(scope, contentsType); }
		if ( obj instanceof Collection ) { return new GamaList((Collection) obj).listValue(scope, contentsType); }
		if ( obj instanceof Color ) {
			final Color c = (Color) obj;
			return GamaList.with(c.getRed(), c.getGreen(), c.getBlue()).listValue(scope, contentsType);
		}
		if ( obj instanceof GamaPoint ) {
			GamaPoint point = (GamaPoint) obj;
			return GamaList.with(point.x, point.y, point.z).listValue(scope, contentsType);
		}
		if ( obj instanceof String ) { return new GamaList(StringUtils.tokenize((String) obj)).listValue(scope,
			contentsType); }
		return GamaList.with(obj).listValue(scope, contentsType);
	}

	@Override
	public IType getKeyType() {
		return Types.get(INT);
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression expr) {
		switch (expr.getType().id()) {
			case COLOR:
				return Types.get(INT);
			case POINT:
				return Types.get(FLOAT);
		}
		return super.contentsTypeIfCasting(expr);
	}

	public static GamaList with(final IScope scope, final IExpression fillExpr, final Integer size) {
		final Object[] contents = new Object[size];
		if ( fillExpr != null ) {
			// 10/01/14. Cannot use Arrays.fill() everywhere: see Issue 778.
			for ( int i = 0; i < contents.length; i++ ) {
				contents[i] = fillExpr.value(scope);
			}
		}
		return new GamaList(contents);
	}
}
