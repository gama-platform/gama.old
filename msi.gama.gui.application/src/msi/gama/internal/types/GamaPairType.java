/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(value = IType.PAIR_STR, id = IType.PAIR, wraps = { GamaPair.class })
public class GamaPairType extends GamaType<GamaPair> {

	@Override
	public GamaPair cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj == null ) { return getDefault(); }
		if ( obj instanceof GamaPair ) { return (GamaPair) obj; }
		if ( obj instanceof GamaPoint ) { return new GamaPair(((GamaPoint) obj).x,
			((GamaPoint) obj).y); }
		if ( obj instanceof GamaDynamicLink ) { return new GamaPair(
			((GamaDynamicLink) obj).getSource(), ((GamaDynamicLink) obj).getTarget()); }
		if ( obj instanceof Map ) {
			Map m = (Map) obj;
			return new GamaPair(new GamaList(m.keySet()), new GamaList(m.values()));
		}
		if ( obj instanceof List ) {
			List l = (List) obj;
			if ( l.size() == 0 ) { return new GamaPair(null, null); }
			if ( l.size() >= 1 ) { return new GamaPair(l.get(0), l.get(1)); }
		}
		return new GamaPair(Cast.asString(obj), obj);
	}

	@Override
	public GamaPair getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

}
