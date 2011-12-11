/**
 * 
 */
package msi.gama.internal.types;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;

/**
 * Written by drogoul
 * Modified on 11 nov. 2011
 * 
 * A generic type for containers. Tentative.
 * 
 */
@type(value = IType.CONTAINER_STR, id = IType.CONTAINER, wraps = { IGamaContainer.class })
public class GamaContainerType extends GamaType<IGamaContainer> {

	public static IGamaContainer staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return obj instanceof IGamaContainer ? (IGamaContainer) obj : GamaListType.staticCast(
			scope, obj, param);
		// reverts by default to a list (most generic type)
	}

	@Override
	public IGamaContainer cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	@Override
	public IGamaContainer getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

}
