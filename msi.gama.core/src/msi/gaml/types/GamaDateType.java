/*********************************************************************************************
 *
 *
 * 'GamaColorType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import org.joda.time.MutableDateTime;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;

/**
 * Written by Patrick Tallandier
 *
 * @todo Description
 *
 */
@type(name = "date", id = IType.DATE, wraps = { GamaDate.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaDateType extends GamaType<GamaDate> {

	@Override
	public GamaDate cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	public static GamaDate staticCast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		if ( obj == null ) { return null; }
		if ( obj instanceof GamaDate ) {
			if ( copy ) { return new GamaDate((GamaDate) obj); }
			return (GamaDate) obj;
		}
		if ( obj instanceof IList ) { return new GamaDate((IList) obj); }
		if ( obj instanceof IContainer ) { return staticCast(scope,
			((IContainer) obj).listValue(scope, Types.NO_TYPE, false), param, copy); }

		if ( obj instanceof String ) {
			if ( "now".equals(obj.toString()) ) { return new GamaDate(MutableDateTime.now()); }
			return new GamaDate((String) obj);
		}
		if ( obj instanceof Boolean ) { return new GamaDate(); }
		int i = Cast.asInt(scope, obj);
		return new GamaDate(i);
	}

	@Override
	public GamaDate getDefault() {
		return null;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
