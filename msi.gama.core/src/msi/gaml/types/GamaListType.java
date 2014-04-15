/*********************************************************************************************
 * 
 * 
 * 'GamaListType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
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

	@Override
	public boolean canCastToConst() {
		return true;
	}
}
