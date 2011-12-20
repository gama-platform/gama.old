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
package msi.gama.util;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.*;

/**
 * The Class GamaColor. A simple wrapper on an AWT Color.
 * 
 * @author drogoul
 */
@vars({ @var(name = IKeyword.COLOR_RED, type = IType.INT_STR),
	@var(name = IKeyword.COLOR_GREEN, type = IType.INT_STR),
	@var(name = IKeyword.COLOR_BLUE, type = IType.INT_STR),
	@var(name = IKeyword.BRIGHTER, type = IType.COLOR_STR),
	@var(name = IKeyword.DARKER, type = IType.COLOR_STR) })
public class GamaColor extends Color implements IValue/* implements IContainer<Integer, Integer> */{

	public final static Map<String, GamaColor> colors = new HashMap();
	public final static Map<Integer, GamaColor> int_colors = new HashMap();

	public static GamaColor getInt(final int rgb) {
		GamaColor result = int_colors.get(rgb);
		if ( result == null ) {
			result = new GamaColor(rgb);
			int_colors.put(rgb, result);
		}
		return result;
	}

	static {
		final Field[] colorFields = Color.class.getDeclaredFields();
		for ( final Field f : colorFields ) {
			try {
				final Color color = (Color) f.get((Object) null);
				final GamaColor gc = new GamaColor(color);
				colors.put(f.getName(), gc);
				int_colors.put(gc.getRGB(), gc);
			} catch (final IllegalAccessException iae) {} catch (final ClassCastException cce) {}
		}
	}

	private static int normalize(final int rgbComp) {
		return rgbComp < 0 ? 0 : rgbComp > 255 ? 255 : rgbComp;
	}

	public GamaColor(final Color c) {
		super(c.getRGB());
	}

	protected GamaColor(final int awtRGB) {
		super(awtRGB);
	}

	public GamaColor(final int r, final int g, final int b) {
		super(normalize(r), normalize(g), normalize(b));
	}

	@Override
	public String toString() {
		return "GamaColor[" + getRGB() /* + ";" + swtValue */+ "]";
	}

	@Override
	public String toGaml() {
		return "rgb (" + getRGB() + ")";
	}

	@Override
	public String stringValue() {
		return String.valueOf(getRGB());
	}

	@Override
	public IType type() {
		return Types.get(IType.COLOR);
	}

	@getter(var = IKeyword.COLOR_RED)
	public Integer red() {
		return super.getRed();
	}

	@getter(var = IKeyword.COLOR_BLUE)
	public Integer blue() {
		return super.getBlue();
	}

	@getter(var = IKeyword.COLOR_GREEN)
	public Integer green() {
		return super.getGreen();
	}

	@getter(var = IKeyword.BRIGHTER)
	public GamaColor getBrighter() {
		return new GamaColor(super.brighter());
	}

	@getter(var = IKeyword.DARKER)
	public GamaColor getDarker() {
		return new GamaColor(super.darker());
	}

	@Override
	public GamaColor copy() {
		return this;
	}

}
