/*********************************************************************************************
 * 
 * 
 * 'GamaStringType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * 
 * 
 * 
 * Written by drogoul Modified on 3 juin 2010
 * 
 * @todo Description
 * 
 */
@type(name = IKeyword.STRING, id = IType.STRING, wraps = { String.class }, kind = ISymbolKind.Variable.REGULAR,
concept = { IConcept.TYPE, IConcept.STRING })
public class GamaStringType extends GamaType<String> {

	@Override
	public String cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		return staticCast(scope, obj, copy);
	}

	public static String staticCast(final IScope scope, final Object obj, final boolean copy)
		throws GamaRuntimeException {
		if ( obj == null ) { return null; }
		if ( obj instanceof IValue ) { return ((IValue) obj).stringValue(scope); }
		return obj.toString();
	}

	@Override
	public String getDefault() {
		return null;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
