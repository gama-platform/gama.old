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
		if ( obj instanceof IContainer ) { return ((IContainer) obj).listValue(scope); }
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
