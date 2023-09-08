/*******************************************************************************************************
 *
 * GamaColorType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.awt.Color;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.IContainer;
import msi.gaml.operators.Cast;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@type (
		name = IKeyword.RGB,
		id = IType.COLOR,
		wraps = { GamaColor.class, Color.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.COLOR },
		doc = @doc ("The type rgb represents colors in GAML, with their three red, green, blue components and, optionally, a fourth alpha component "))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaColorType extends GamaType<GamaColor> {

	@Override
	@doc ("Transforms the parameter into a rgb color. A second parameter can be used to express the transparency of the color, either an int (between 0 and 255) or a float (between 0 and 1)")
	public GamaColor cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	/**
	 * Static cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param copy
	 *            the copy
	 * @return the gama color
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static GamaColor staticCast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		// param can contain the alpha value
		if (obj == null) return null;
		if (obj instanceof final GamaColor col) {
			if (param instanceof Integer a) return GamaColor.get(col.getRed(), col.getGreen(), col.getBlue(), a);
			if (param instanceof Double a)
				return GamaColor.getWithDoubleAlpha(col.getRed(), col.getGreen(), col.getBlue(), a);
			return (GamaColor) obj;
		}
		if (obj instanceof final List l) {
			final int size = l.size();
			return switch (size) {
				case 0 -> GamaColor.get(Color.black);
				case 1, 2 -> staticCast(scope, ((List) obj).get(0), param, copy);
				case 3 -> GamaColor.get(Cast.asInt(scope, l.get(0)), Cast.asInt(scope, l.get(1)),
						Cast.asInt(scope, l.get(2)), 255);
				default -> GamaColor.get(Cast.asInt(scope, l.get(0)), Cast.asInt(scope, l.get(1)),
						Cast.asInt(scope, l.get(2)), Cast.asInt(scope, l.get(3)));
			};
		}
		if (obj instanceof IContainer)
			return staticCast(scope, ((IContainer) obj).listValue(scope, Types.NO_TYPE, false), param, copy);
		if (obj instanceof String) {
			final String s = ((String) obj).toLowerCase();
			GamaColor c = GamaColor.colors.get(s);
			if (c == null) {
				try {
					c = GamaColor.get(Color.decode(s));
				} catch (final NumberFormatException e) {
					c = null;
					if (s != null && s.contains("rgb")) {
						String sClean = s.replace(" ", "").replace("rgb", "").replace("(", "").replace(")", "");
						String[] sval = sClean.split(",");
						if (sval.length >= 3) {
							Integer r = Integer.valueOf(sval[0]);
							Integer g = Integer.valueOf(sval[1]);
							Integer b = Integer.valueOf(sval[2]);
							Integer alpha = sval.length == 4 ? Integer.valueOf(sval[3]) : null;
							if (r != null && b != null && g != null) {
								c = GamaColor.get(r, g, b, alpha == null ? 255 : alpha);
							}
						}
					}
					if (c == null) {
						final GamaRuntimeException ex =
								GamaRuntimeException.error("'" + s + "' is not a valid color name", scope);
						throw ex;
					}
				}
				GamaColor.colors.put(s, c);
			}
			if (param == null) return c;
			if (param instanceof Integer i) return GamaColor.get(c, i);
			if (param instanceof Double d) return GamaColor.get(c, d);
		}
		if (obj instanceof Boolean cond) return cond ? GamaColor.get(Color.black) : GamaColor.get(Color.white);
		final int i = Cast.asInt(scope, obj);
		if (param instanceof Integer in) return GamaColor.get(i, in);
		if (param instanceof Double d) return GamaColor.get(i, Double.valueOf(d * 255).intValue());
		return GamaColor.get(i);
	}

	@Override
	public GamaColor getDefault() {
		return null; // new GamaColor(Color.black);
	}

	@Override
	public IType getContentType() { return Types.get(INT); }

	@Override
	public IType getKeyType() { return Types.get(INT); }

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isCompoundType() { return true; }

}
