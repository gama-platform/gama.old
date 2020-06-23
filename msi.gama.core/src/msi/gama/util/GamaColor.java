/*******************************************************************************************************
 *
 * msi.gama.util.GamaColor.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConstantCategory;
import msi.gama.precompiler.constants.ColorCSS;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaColor. A simple wrapper on an AWT Color.
 *
 * @author drogoul
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

	@constant (
			value = "the set of CSS colors",
			category = IConstantCategory.COLOR_CSS,
			concept = {},
			doc = @doc ("In addition to the previous units, GAML provides a direct access to the 147 named colors defined in CSS (see [http://www.cssportal.com/css3-color-names/]). E.g, {{{rgb my_color <- °teal;}}}")) public final static Object[] array =
					ColorCSS.array;

	public final static Map<String, GamaColor> colors = GamaMapFactory.createUnordered();
	public final static Map<Integer, GamaColor> int_colors = Collections.synchronizedMap(new HashMap<>());

	public static GamaColor getInt(final int rgb) {
		GamaColor result = int_colors.get(rgb);
		if (result == null) {
			result = new GamaColor(rgb);
			int_colors.put(rgb, result);
		}
		return result;
	}

	public static GamaColor getNamed(final String rgb) {
		return colors.get(rgb);
	}

	static {
		for (int i = 0; i < array.length; i += 2) {
			final GamaColor color = new NamedGamaColor((String) array[i], (int[]) array[i + 1]);
			colors.put((String) array[i], color);
			int_colors.put(color.getRGB(), color);
		}
		// A.G add the GAMA Color corresponding to the GAMA1.7 Logo
		final GamaColor orange = new NamedGamaColor("gamaorange", new int[] { 244, 165, 40, 1 });
		colors.put("gamaorange", orange);
		int_colors.put(orange.getRGB(), orange);

		final GamaColor red = new NamedGamaColor("gamared", new int[] { 217, 72, 33, 1 });
		colors.put("gamared", red);
		int_colors.put(red.getRGB(), red);

		final GamaColor blue = new NamedGamaColor("gamablue", new int[] { 22, 94, 147, 1 });
		colors.put("gamablue", blue);
		int_colors.put(blue.getRGB(), blue);

		final GamaColor green = new NamedGamaColor("gamagreen", new int[] { 81, 135, 56, 1 });
		colors.put("gamagreen", green);
		int_colors.put(green.getRGB(), green);
	}

	public static class NamedGamaColor extends GamaColor {

		final String name;

		NamedGamaColor(final String n, final int[] c) {
			// c must be of length 4.
			super(c[0], c[1], c[2], (double) c[3]);
			name = n;
		}

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

	private static int normalize(final int rgbComp) {
		return rgbComp < 0 ? 0 : rgbComp > 255 ? 255 : rgbComp;
	}

	// returns a value between 0 and 255 from a double between 0 and 1
	private static int normalize(final double transp) {
		return (int) (transp < 0 ? 0 : transp > 1 ? 255 : 255 * transp);
	}

	public GamaColor(final Color c) {
		super(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public GamaColor(final Color c, final int alpha) {
		this(c.getRed(), c.getGreen(), c.getBlue(), normalize(alpha));
	}

	public GamaColor(final Color c, final double alpha) {
		this(c.getRed(), c.getGreen(), c.getBlue(), normalize(alpha));
	}

	protected GamaColor(final int awtRGB) {
		super(awtRGB, true);
	}

	public GamaColor(final int r, final int g, final int b) {
		this(normalize(r), normalize(g), normalize(b), 255);

	}

	public GamaColor(final int r, final int g, final int b, final int t) {
		// t between 0 and 255
		super(normalize(r), normalize(g), normalize(b), normalize(t));
	}

	public GamaColor(final double r, final double g, final double b, final double t) {
		// t between 0 and 1
		super(normalize(r), normalize(g), normalize(b), normalize(t));
	}

	public GamaColor(final int r, final int g, final int b, final double t) {
		// t between 0 and 1
		super(normalize(r), normalize(g), normalize(b), normalize(t));
	}

	/**
	 * @param is
	 */
	// public GamaColor(final int[] c) {
	// this(c[0], c[1], c[2], c[3]); // c[3] not considered yet
	// }

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "rgb (" + red() + ", " + green() + ", " + blue() + "," + getAlpha() + ")";
	}

	@Override
	public String stringValue(final IScope scope) {
		return String.valueOf(getRGB());
	}

	@getter (IKeyword.COLOR_RED)
	public Integer red() {
		return super.getRed();
	}

	@getter (IKeyword.COLOR_BLUE)
	public Integer blue() {
		return super.getBlue();
	}

	@getter (IKeyword.COLOR_GREEN)
	public Integer green() {
		return super.getGreen();
	}

	@getter (IKeyword.ALPHA)
	public Integer alpha() {
		return super.getAlpha();
	}

	@getter (IKeyword.BRIGHTER)
	public GamaColor getBrighter() {
		return new GamaColor(super.brighter());
	}

	@getter (IKeyword.DARKER)
	public GamaColor getDarker() {
		return new GamaColor(super.darker());
	}

	@Override
	public GamaColor copy(final IScope scope) {
		return new GamaColor(this);
	}

	public static GamaColor merge(final GamaColor c1, final GamaColor c2) {
		return new GamaColor(c1.getRed() + c2.getRed(), c1.getGreen() + c2.getGreen(), c1.getBlue() + c2.getBlue(),
				c1.getAlpha() + c2.getAlpha());
	}

	public int compareRgbTo(final Color c2) {
		return Integer.signum(getRGB() - c2.getRGB());
	}

	public int compareLuminescenceTo(final Color c2) {
		return Double.compare(this.getRed() * 0.299d + this.getGreen() * 0.587d + this.getBlue() * 0.114d,
				c2.getRed() * 0.299d + c2.getGreen() * 0.587d + c2.getBlue() * 0.114d);
	}

	public int compareBrightnessTo(final Color c2) {
		final float[] hsb = RGBtoHSB(getRed(), getGreen(), getBlue(), null);
		final float[] hsb2 = RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
		return Float.compare(hsb[2], hsb2[2]);
	}

	public int compareLumaTo(final Color c2) {
		return Double.compare(this.getRed() * 0.21d + this.getGreen() * 0.72d + this.getBlue() * 0.07d,
				c2.getRed() * 0.21d + c2.getGreen() * 0.72d + c2.getBlue() * 0.07d);
	}

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
	public IType<?> getGamlType() {
		return Types.COLOR;
	}

	public GamaColor withAlpha(final double d) {
		return new GamaColor(getRed(), getGreen(), getBlue(), d);
	}

}
