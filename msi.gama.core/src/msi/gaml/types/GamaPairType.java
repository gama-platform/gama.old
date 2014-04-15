/*********************************************************************************************
 * 
 * 
 * 'GamaPairType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.types;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaDynamicLink;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 * 
 * @todo Description
 * 
 */
@type(name = IKeyword.PAIR, id = IType.PAIR, wraps = { GamaPair.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaPairType extends GamaContainerType<GamaPair> {

	@Override
	public GamaPair cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentsType) {
		GamaPair p = staticCast(scope, obj, keyType, contentsType);
		return p;
	}

	public static GamaPair staticCast(final IScope scope, final Object obj, final IType keyType,
		final IType contentsType) throws GamaRuntimeException {
		GamaPair result = null;
		if ( obj instanceof GamaPair ) {
			result = (GamaPair) obj;
		} else
		// 8/01/14: No more automatic casting between points and pairs (as points can now have 3 coordinates
		// if ( obj instanceof ILocation ) { return new GamaPair(((GamaPoint) obj).x, ((GamaPoint) obj).y); }
		if ( obj instanceof GamaDynamicLink ) {
			result = new GamaPair(((GamaDynamicLink) obj).getSource(), ((GamaDynamicLink) obj).getTarget());
		} else if ( obj instanceof Map ) {
			Map m = (Map) obj;
			result = new GamaPair(new GamaList(m.keySet()), new GamaList(m.values()));
		} else if ( obj instanceof List ) {
			List l = (List) obj;
			switch (l.size()) {
				case 0:
					result = new GamaPair(null, null);
					break;
				case 1:
					result = new GamaPair(l.get(0), l.get(0));
					break;
				case 2:
					result = new GamaPair(l.get(0), l.get(1));
					break;
				default:
					result = new GamaPair(l, l);
			}

		} else {
			// 8/01/14 : Change of behavior for the default pair: now returns a pair object::object
			result = new GamaPair(obj, obj);
			// return new GamaPair(Cast.asString(scope, obj), obj);

		}
		return new GamaPair(toType(scope, result.key, keyType), toType(scope, result.value, contentsType));
	}

	@Override
	public GamaPair getDefault() {
		return new GamaPair(null, null);
	}

	@Override
	public IType getContentType() {
		return Types.get(NONE);
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}
	//
	// @Override
	// public boolean hasContents() {
	// return true;
	// }

}
