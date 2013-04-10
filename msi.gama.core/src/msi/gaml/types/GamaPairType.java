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
package msi.gaml.types;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(name = IKeyword.PAIR, id = IType.PAIR, wraps = { GamaPair.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaPairType extends GamaType<GamaPair> {

	@Override
	public GamaPair cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj == null ) { return getDefault(); }
		if ( obj instanceof GamaPair ) { return (GamaPair) obj; }
		if ( obj instanceof ILocation ) { return new GamaPair(((GamaPoint) obj).x,
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
		return new GamaPair(Cast.asString(scope, obj), obj);
	}

	@Override
	public GamaPair getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

	@Override
	public boolean hasContents() {
		return true;
	}

}
