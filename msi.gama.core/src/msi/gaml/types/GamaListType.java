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

@type(name = IKeyword.LIST, id = IType.LIST, wraps = { IList.class }, kind = ISymbolKind.Variable.CONTAINER,
concept = { IConcept.TYPE, IConcept.CONTAINER, IConcept.LIST })
public class GamaListType extends GamaContainerType<IList> {

	@Override
	public IList cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentsType, final boolean copy) throws GamaRuntimeException {
		IList list = staticCast(scope, obj, contentsType, copy);
		return list;
	}

	public static IList staticCast(final IScope scope, final Object obj, final IType ct, final boolean copy)
		throws GamaRuntimeException {
		IType contentsType = ct == null ? Types.NO_TYPE : ct;
		if ( obj == null ) { return GamaListFactory.create(Types.NO_TYPE, 0); }
		// if ( obj instanceof IList ) { return (IList) obj; }
		if ( obj instanceof IContainer ) { return ((IContainer) obj).listValue(scope, contentsType, copy); }
		// Dont copy twice the collection
		if ( obj instanceof Collection ) { return GamaListFactory.create(scope, contentsType, (Collection) obj); }
		if ( obj instanceof Color ) {
			final Color c = (Color) obj;
			return GamaListFactory.create(scope, contentsType, new int[] { c.getRed(), c.getGreen(), c.getBlue() });
		}
		if ( obj instanceof GamaPoint ) {
			GamaPoint point = (GamaPoint) obj;
			return GamaListFactory.create(scope, contentsType, new double[] { point.x, point.y, point.z });
		}
		if ( obj instanceof String ) { return GamaListFactory.create(scope, contentsType,
			StringUtils.tokenize((String) obj)); }
		return GamaListFactory.create(scope, contentsType, new Object[] { obj });
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

	@Override
	public boolean canCastToConst() {
		return true;
	}
}
