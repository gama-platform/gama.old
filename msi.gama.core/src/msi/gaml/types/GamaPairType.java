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
@type(name = IKeyword.PAIR, id = IType.PAIR, wraps = { GamaPair.class }, kind = ISymbolKind.Variable.REGULAR,
concept = { IConcept.TYPE, IConcept.CONTAINER })
public class GamaPairType extends GamaContainerType<GamaPair> {

	@Override
	public GamaPair cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentsType, final boolean copy) {
		GamaPair p = staticCast(scope, obj, keyType, contentsType, copy);
		return p;
	}

	public static GamaPair staticCast(final IScope scope, final Object obj, final IType keyType,
		final IType contentsType, final boolean copy) throws GamaRuntimeException {
		Object key, value;
		if ( obj instanceof GamaPair ) {
			key = ((GamaPair) obj).key;
			value = ((GamaPair) obj).value;
		} else
		// 8/01/14: No more automatic casting between points and pairs (as points can now have 3 coordinates
		// if ( obj instanceof ILocation ) { return new GamaPair(((GamaPoint) obj).x, ((GamaPoint) obj).y); }
		if ( obj instanceof GamaDynamicLink ) {
			key = ((GamaDynamicLink) obj).getSource();
			value = ((GamaDynamicLink) obj).getTarget();
		} else if ( obj instanceof GamaMap ) {
			GamaMap m = (GamaMap) obj;
			key = GamaListFactory.create(scope, m.getType().getKeyType(), m.keySet());
			value = GamaListFactory.create(scope, m.getType().getContentType(), m.values());
		} else if ( obj instanceof IList ) {
			IList l = (IList) obj;
			switch (l.size()) {
				case 0:
					key = null;
					value = null;
					break;
				case 1:
					key = l.get(0);
					value = l.get(0);
					break;
				case 2:
					key = l.get(0);
					value = l.get(1);
					break;
				default:
					key = l;
					value = l;
			}

		} else {
			// 8/01/14 : Change of behavior for the default pair: now returns a pair object::object
			key = obj;
			value = obj;
		}
		IType kt = keyType == null || keyType == Types.NO_TYPE ? GamaType.of(key) : keyType;
		IType ct = contentsType == null || contentsType == Types.NO_TYPE ? GamaType.of(value) : contentsType;
		return new GamaPair(toType(scope, key, kt, copy), toType(scope, value, ct, copy), kt, ct);
	}

	@Override
	public GamaPair getDefault() {
		return new GamaPair(null, null, Types.NO_TYPE, Types.NO_TYPE);
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
