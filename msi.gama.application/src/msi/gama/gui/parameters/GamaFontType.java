/*********************************************************************************************
 * 
 * 
 * 'GamaColorType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.gui.swt.SwtGui;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;
import org.eclipse.swt.graphics.FontData;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 * 
 * @todo Description
 * 
 */
@type(name = IKeyword.FONT, id = IType.FONT, wraps = { FontData.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaFontType extends GamaType<FontData> {

	@Override
	public FontData cast(final IScope scope, final Object obj, final Object param, final boolean copy)
		throws GamaRuntimeException {
		return staticCast(scope, obj, copy);
	}

	public static FontData staticCast(final IScope scope, final Object obj, final boolean copy)
		throws GamaRuntimeException {
		if ( obj instanceof FontData ) {
			FontData col = (FontData) obj;
			return col;
		}
		if ( obj instanceof String ) {
			FontData col = new FontData((String) obj);
			return col;
		}
		return null;
	}

	@Override
	public FontData getDefault() {
		return SwtGui.getLabelfont().getFontData()[0];
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
