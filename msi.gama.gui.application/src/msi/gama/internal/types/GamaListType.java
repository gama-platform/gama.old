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
import msi.gama.util.GamaList;

@type(value = IType.LIST_STR, id = IType.LIST, wraps = { GamaList.class, List.class,
	ArrayList.class })
public class GamaListType extends GamaType<GamaList> {

	@Override
	public GamaList cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static GamaList staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj == null ) { return new GamaList(); }
		if ( obj instanceof ISpecies ) { return ((ISpecies) obj).listValue(scope); }
		if ( obj instanceof IGamaContainer ) { return ((IGamaContainer) obj).listValue(scope); }
		if ( obj instanceof Collection ) { return new GamaList((Collection) obj); }
		if ( obj instanceof String ) { return GamaStringType.tokenize((String) obj); }
		return GamaList.with(obj);
	}

	@Override
	public GamaList getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

}
