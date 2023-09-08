/*******************************************************************************************************
 *
 * GamaColor.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.constants.ColorCSS;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaColor. A simple wrapper on an AWT Color.
 *
 * @author drogoul
 */

/**
 * The Class GamaColor.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 20 août 2023
 */

/**
 * The Class GamaColor.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 20 août 2023
 */
@vars ({ @variable (
		name = IKeyword.COLOR_RED,
		type = IType.INT,
		doc = { @doc ("Returns the red component of the color (between 0 and 255)") }),
		@variable (
				name = IKeyword.COLOR_GREEN,
				type = IType.INT,
				doc = { @doc ("Returns the green component of the color (between 0 and 255)") }),
		@variable (
				name = IKeyword.COLOR_BLUE,
				type = IType.INT,
				doc = { @doc ("Returns the blue component of the color (between 0 and 255)") }),
		@variable (
				name = IKeyword.ALPHA,
				type = IType.INT,
				doc = { @doc ("Returns the alpha component (transparency) of the color (between 0 for transparent and 255 for opaque)") }),
		@variable (
				name = IKeyword.BRIGHTER,
				type = IType.COLOR,
				doc = { @doc ("Returns a lighter color (with increased luminance)") }),
		@variable (
				name = IKeyword.DARKER,
				type = IType.COLOR,
				doc = { @doc ("Returns a darker color (with decreased luminance)") }) })
public class GamaColor extends Color implements IValue, Comparable<Color>/* implements IContainer<Integer, Integer> */ {

	/** The Constant array. */
	public final static Object[] array = ColorCSS.array;

	/** The Constant colors. */
	public final static Map<String, GamaColor> colors = GamaMapFactory.createUnordered();

	/** The Constant int_colors. */
	public final static Map<Integer, GamaColor> int_colors = Collections.synchronizedMap(new HashMap<>());

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param rgb
	 *            the rgb
	 * @return the gama color
	 * @date 20 août 2023
	 */

	public static GamaColor get(final int rgb) {
		// rgba value expected
		GamaColor result = int_colors.get(rgb);
		if (result == null) {
			result = new GamaColor(rgb);
			int_colors.put(rgb, result);
		}
		return result;
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param rgb
	 *            the rgb
	 * @param alpha
	 *            the alpha
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor get(final int rgb, final int alpha) {
		GamaColor c = get(rgb);
		return get(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor get(final int r, final int g, final int b) {
		return get(r, g, b, 255);
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @param a
	 *            the a
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor get(final int r, final int g, final int b, final int a) {
		// rgb in 3 components + alpha
		return get((normalize(a) & 0xFF) << 24 | (normalize(r) & 0xFF) << 16 | (normalize(g) & 0xFF) << 8
				| (normalize(b) & 0xFF) << 0);

	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @param t
	 *            the t
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor getWithDoubleAlpha(final int r, final int g, final int b, final double t) {
		return get(r, g, b, normalize(t));
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @param t
	 *            the t
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor getWithDoubles(final double r, final double g, final double b, final double t) {
		return get(normalize(r), normalize(g), normalize(b), normalize(t));
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param t
	 *            the t
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor get(final Color c, final double t) {
		return get(c, normalize(t));
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @param t
	 *            the t
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor get(final Color c, final int t) {
		return get(c.getRed(), c.getGreen(), c.getBlue(), t);
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @date 20 août 2023
	 */
	public static GamaColor get(final Color c) {
		return get(c.getRGB());
	}

	/**
	 * Gets the named.
	 *
	 * @param rgb
	 *            the rgb
	 * @return the named
	 */
	public static GamaColor get(final String rgb) {
		return colors.get(rgb);
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the rgb
	 * @return the gama color
	 * @date 20 août 2023
	 */
	public static GamaColor get(final String name, final int... t) {
		GamaColor c = colors.get(name);
		if (c == null) { colors.put(name, new NamedGamaColor(name, t[0], t[1], t[2], t[3])); }
		return colors.get(name);
	}

	static {
		for (int i = 0; i < array.length; i += 2) {
			final GamaColor color = GamaColor.get((String) array[i], (int[]) array[i + 1]);
			colors.put((String) array[i], color);
			int_colors.put(color.getRGB(), color);
		}
		// A.G add the GAMA Color corresponding to the GAMA 1.9 Logo
		final GamaColor orange = GamaColor.get("gamaorange", 244, 165, 40, 255);
		colors.put("gamaorange", orange);
		int_colors.put(orange.getRGB(), orange);

		final GamaColor red = GamaColor.get("gamared", 217, 72, 33, 255);
		colors.put("gamared", red);
		int_colors.put(red.getRGB(), red);

		final GamaColor blue = GamaColor.get("gamablue", 22, 94, 147, 255);
		colors.put("gamablue", blue);
		int_colors.put(blue.getRGB(), blue);

		final GamaColor green = GamaColor.get("gamagreen", 81, 135, 56, 255);
		colors.put("gamagreen", green);
		int_colors.put(green.getRGB(), green);
	}

	/**
	 * The Class NamedGamaColor.
	 */
	public static class NamedGamaColor extends GamaColor {

		/**
		 * Instantiates a new named gama color.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param name
		 *            the name.
		 * @param rgba
		 *            the rgba
		 * @date 20 août 2023
		 */
		NamedGamaColor(final String name, final int... rgba) {
			super(rgba[0], rgba[1], rgba[2], rgba[3]);
			this.name = name;
		}

		/** The name. */
		final String name;

		@Override
		public String toString() {
			return "color[" + name + "]";
		}

		@Override
		public String serialize(final boolean includingBuiltIn) {
			return "#" + name;
		}

		@Override
		public String stringValue(final IScope scope) {
			return name;
		}

	}

	/**
	 * Normalize.
	 *
	 * @param number
	 *            the rgb comp
	 * @return the int
	 */
	private static int normalize(final int number) {
		return number < 0 ? 0 : number > 255 ? 255 : number;
	}

	/**
	 * Normalize.
	 *
	 * @param number
	 *            the transp
	 * @return the int
	 */
	// returns a value between 0 and 255 from a double between 0 and 1
	private static int normalize(final double number) {
		return (int) (number < 0 ? 0 : number > 1 ? 255 : 255 * number);
	}

	/**
	 * Instantiates a new gama color.
	 *
	 * @param awtRGB
	 *            the awt RGB
	 */
	protected GamaColor(final int awtRGB) {
		super(awtRGB, true);
	}

	/**
	 * Instantiates a new gama color.
	 *
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @param t
	 *            the t
	 */
	protected GamaColor(final int r, final int g, final int b, final int t) {
		// t between 0 and 255
		super(normalize(r), normalize(g), normalize(b), normalize(t));
	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "rgb (" + red() + ", " + green() + ", " + blue() + ", " + getAlpha() + ")";
	}

	@Override
	public String stringValue(final IScope scope) {
		return toString();
	}

	/**
	 * Red.
	 *
	 * @return the integer
	 */
	@getter (IKeyword.COLOR_RED)
	public Integer red() {
		return super.getRed();
	}

	/**
	 * Blue.
	 *
	 * @return the integer
	 */
	@getter (IKeyword.COLOR_BLUE)
	public Integer blue() {
		return super.getBlue();
	}

	/**
	 * Green.
	 *
	 * @return the integer
	 */
	@getter (IKeyword.COLOR_GREEN)
	public Integer green() {
		return super.getGreen();
	}

	/**
	 * Alpha.
	 *
	 * @return the integer
	 */
	@getter (IKeyword.ALPHA)
	public Integer alpha() {
		return super.getAlpha();
	}

	/** The brightness factor. */
	static float BRIGHTNESS_FACTOR = 0.7f;

	/**
	 * Gets the brighter.
	 *
	 * @return the brighter
	 */
	@Override
	@getter (IKeyword.BRIGHTER)
	public GamaColor brighter() {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		int alpha = getAlpha();

		/*
		 * From 2D group: 1. black.brighter() should return grey 2. applying brighter to blue will always return blue,
		 * brighter 3. non pure color (non zero rgb) will eventually return white
		 */
		int i = (int) (1.0 / (1.0 - BRIGHTNESS_FACTOR));
		if (r == 0 && g == 0 && b == 0) return GamaColor.get(i, i, i, alpha);
		if (r > 0 && r < i) { r = i; }
		if (g > 0 && g < i) { g = i; }
		if (b > 0 && b < i) { b = i; }

		return GamaColor.get(Math.min((int) (r / BRIGHTNESS_FACTOR), 255), Math.min((int) (g / BRIGHTNESS_FACTOR), 255),
				Math.min((int) (b / BRIGHTNESS_FACTOR), 255), alpha);
	}

	/**
	 * Gets the darker.
	 *
	 * @return the darker
	 */
	@Override
	@getter (IKeyword.DARKER)
	public GamaColor darker() {
		return GamaColor.get(Math.max((int) (getRed() * BRIGHTNESS_FACTOR), 0),
				Math.max((int) (getGreen() * BRIGHTNESS_FACTOR), 0), Math.max((int) (getBlue() * BRIGHTNESS_FACTOR), 0),
				getAlpha());
	}

	@Override
	public GamaColor copy(final IScope scope) {
		return GamaColor.get(this);
	}

	/**
	 * Merge.
	 *
	 * @param c1
	 *            the c 1
	 * @param c2
	 *            the c 2
	 * @return the gama color
	 */
	public static GamaColor merge(final GamaColor c1, final GamaColor c2) {
		return GamaColor.get(c1.getRed() + c2.getRed(), c1.getGreen() + c2.getGreen(), c1.getBlue() + c2.getBlue(),
				c1.getAlpha() + c2.getAlpha());
	}

	/**
	 * Compare rgb to.
	 *
	 * @param c2
	 *            the c 2
	 * @return the int
	 */
	public int compareRgbTo(final Color c2) {
		return Integer.signum(getRGB() - c2.getRGB());
	}

	/**
	 * Compare luminescence to.
	 *
	 * @param c2
	 *            the c 2
	 * @return the int
	 */
	public int compareLuminescenceTo(final Color c2) {
		return Double.compare(this.getRed() * 0.299d + this.getGreen() * 0.587d + this.getBlue() * 0.114d,
				c2.getRed() * 0.299d + c2.getGreen() * 0.587d + c2.getBlue() * 0.114d);
	}

	/**
	 * Compare brightness to.
	 *
	 * @param c2
	 *            the c 2
	 * @return the int
	 */
	public int compareBrightnessTo(final Color c2) {
		final float[] hsb = RGBtoHSB(getRed(), getGreen(), getBlue(), null);
		final float[] hsb2 = RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
		return Float.compare(hsb[2], hsb2[2]);
	}

	/**
	 * Compare luma to.
	 *
	 * @param c2
	 *            the c 2
	 * @return the int
	 */
	public int compareLumaTo(final Color c2) {
		return Double.compare(this.getRed() * 0.21d + this.getGreen() * 0.72d + this.getBlue() * 0.07d,
				c2.getRed() * 0.21d + c2.getGreen() * 0.72d + c2.getBlue() * 0.07d);
	}

	/**
	 * Compare to.
	 *
	 * @param c2
	 *            the c 2
	 * @return the int
	 */
	@Override
	public int compareTo(final Color c2) {
		return compareRgbTo(c2);
	}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.common.interfaces.ITyped#getGamlType()
	 */
	@Override
	public IType<?> getGamlType() { return Types.COLOR; }

	/**
	 * With alpha.
	 *
	 * @param d
	 *            the d
	 * @return the gama color
	 */
	public GamaColor withAlpha(final double d) {
		return getWithDoubleAlpha(getRed(), getGreen(), getBlue(), d);
	}

	/**
	 * Checks if is zero.
	 *
	 * @return true, if is zero
	 */
	public boolean isZero() { return getRed() == 0 && getGreen() == 0 && getBlue() == 0; }

	@Override
	public int intValue(final IScope scope) {
		return super.getRGB();
	}

}
