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

import java.awt.Color;
import java.util.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(name = IType.COLOR_STR, id = IType.COLOR, wraps = { GamaColor.class, Color.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaColorType extends GamaType<GamaColor> {

	@Override
	public GamaColor cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static GamaColor staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj instanceof GamaColor ) { return (GamaColor) obj; }
		if ( obj instanceof List ) {
			List l = (List) obj;
			if ( l.size() > 2 ) { return new GamaColor(Cast.asInt(scope, l.get(0)), Cast.asInt(
				scope, l.get(1)), Cast.asInt(scope, l.get(2))); }
			/* To allow constructions like rgb [255,255,255] */
		} else if ( obj instanceof Map ) {
			Map m = (Map) obj;
			final int r = Cast.asInt(scope, m.get("r"));
			final int g = Cast.asInt(scope, m.get("g"));
			final int b = Cast.asInt(scope, m.get("b"));
			return new GamaColor(r, g, b);
		}
		if ( obj instanceof IMatrix ) { return staticCast(scope, ((IMatrix) obj).listValue(scope),
			param); }
		if ( obj instanceof String ) {
			String s = (String) obj;
			GamaColor c = GamaColor.colors.get(s);
			if ( c == null ) {
				c = new GamaColor(Color.decode(s));
				GamaColor.colors.put(s, c);
			}
			return c;
		}
		if ( obj instanceof Boolean ) { return (Boolean) obj ? new GamaColor(Color.black)
			: new GamaColor(Color.white); }
		int i = Cast.asInt(scope, obj);
		GamaColor gc = GamaColor.getInt((255 & 0xFF) << 24 | i & 0xFFFFFF << 0);
		return gc;
	}

	@Override
	public GamaColor getDefault() {
		return new GamaColor(Color.white);
	}

	@Override
	public IType defaultContentType() {
		return Types.get(INT);
	}

}
