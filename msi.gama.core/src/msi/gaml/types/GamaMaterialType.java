/*********************************************************************************************
 *
 *
 * 'GamaPointType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMaterial;

/**
 * Written by mazarsju
 *
 * @todo Description
 *
 */
@type(name = IKeyword.MATERIAL,
id = IType.MATERIAL,
wraps = { GamaMaterial.class },
kind = ISymbolKind.Variable.REGULAR,
concept = { /*TODO*/ })
public class GamaMaterialType extends GamaType<GamaMaterial> {
	
	public static GamaMaterial DEFAULT_MATERIAL = new GamaMaterial(0,0);

	@Override
	public GamaMaterial cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		return staticCast(scope, obj, copy);
	}

	public static GamaMaterial staticCast(final IScope scope, final Object obj, final boolean copy) {
		if ( obj instanceof GamaMaterial ) { return (GamaMaterial) obj; }
		return null;
	}

	@Override
	public GamaMaterial getDefault() {
		return null;
	}

	@Override
	public IType getContentType() {
		return Types.get(FLOAT);
	}

	@Override
	public IType getKeyType() {
		return Types.get(INT);
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

}
