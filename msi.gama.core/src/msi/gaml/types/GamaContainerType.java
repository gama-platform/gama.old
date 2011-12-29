/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import msi.gama.common.interfaces.*;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.precompiler.GamlAnnotations.type;

/**
 * Written by drogoul
 * Modified on 11 nov. 2011
 * 
 * A generic type for containers. Tentative.
 * 
 */
@type(value = IType.CONTAINER_STR, id = IType.CONTAINER, wraps = { IContainer.class })
public class GamaContainerType extends GamaType<IContainer> {

	public static IContainer staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return obj instanceof IContainer ? (IContainer) obj : GamaListType.staticCast(scope, obj,
			param);
		// reverts by default to a list (most generic type)
	}

	@Override
	public IContainer cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	@Override
	public IContainer getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

}
