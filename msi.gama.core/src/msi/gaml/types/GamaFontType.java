/*******************************************************************************************************
 *
 * msi.gaml.types.GamaFontType.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import java.awt.Font;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.FONT,
		id = IType.FONT,
		wraps = { GamaFont.class },
		kind = ISymbolKind.Variable.REGULAR,
		doc = { @doc ("Represents font objects that can be passed directly as arguments to draw statements and text layers. A font is identified by its face name (e.g. 'Helvetica'), its size in points (e.g. 12) and its style (i.e., #bold, #italic, or an addition of the 2") },
		concept = { IConcept.TYPE, IConcept.TEXT, IConcept.DISPLAY })
public class GamaFontType extends GamaType<GamaFont> {

	public static Pref<GamaFont> DEFAULT_DISPLAY_FONT = GamaPreferences
			.create("pref_display_default_font", "Default font to use in 'draw'",
					() -> new GamaFont("Helvetica", Font.PLAIN, 12), IType.FONT, true)
			.in(GamaPreferences.Displays.NAME, GamaPreferences.Displays.DRAWING);

	@doc(value="Cast any object as a font", 
			usages = {
				@usage(value = "if the operand is a number, returns with the operand value as font size and the default display font style", 		
					examples = {
						@example("font f <- font(12);")
					}),
				@usage(value = "if the operand is a string, returns a font with this font name", 		
					examples = {
						@example("font f <- font(12);")
					}),				
			})
	@Override
	public GamaFont cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, copy);
	}

	public static GamaFont staticCast(final IScope scope, final Object obj, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof Number) {
			final Number size = (Number) obj;
			final GamaFont font = DEFAULT_DISPLAY_FONT.getValue();
			return new GamaFont(font.getName(), font.getStyle(), size.intValue());
		}
		if (obj instanceof GamaFont) {
			if (copy) {
				return new GamaFont((Font) obj);
			} else {
				return (GamaFont) obj;
			}
		}
		if (obj instanceof String) { return new GamaFont(Font.decode((String) obj)); }
		return DEFAULT_DISPLAY_FONT.getValue();
	}

	@Override
	public GamaFont getDefault() {
		return DEFAULT_DISPLAY_FONT.getValue();
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
