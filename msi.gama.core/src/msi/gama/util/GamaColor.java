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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.awt.Color;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

/**
 * The Class GamaColor. A simple wrapper on an AWT Color.
 * 
 * @author drogoul
 */
@vars({ @var(name = IKeyword.COLOR_RED, type = IType.INT), @var(name = IKeyword.COLOR_GREEN, type = IType.INT),
	@var(name = IKeyword.COLOR_BLUE, type = IType.INT), @var(name = IKeyword.ALPHA, type = IType.INT),
	@var(name = IKeyword.BRIGHTER, type = IType.COLOR), @var(name = IKeyword.DARKER, type = IType.COLOR) })
public class GamaColor extends Color implements IValue/* implements IContainer<Integer, Integer> */{

	public final static Object[] array = new Object[] { "transparent", new int[] { 0, 0, 0, 0 }, "aliceblue",
		new int[] { 240, 248, 255, 1 }, "antiquewhite", new int[] { 250, 235, 215, 1 }, "aqua",
		new int[] { 0, 255, 255, 1 }, "aquamarine", new int[] { 127, 255, 212, 1 }, "azure",
		new int[] { 240, 255, 255, 1 }, "beige", new int[] { 245, 245, 220, 1 }, "bisque",
		new int[] { 255, 228, 196, 1 }, "black", new int[] { 0, 0, 0, 1 }, "blanchedalmond",
		new int[] { 255, 235, 205, 1 }, "blue", new int[] { 0, 0, 255, 1 }, "blueviolet",
		new int[] { 138, 43, 226, 1 }, "brown", new int[] { 165, 42, 42, 1 }, "burlywood",
		new int[] { 222, 184, 135, 1 }, "cadetblue", new int[] { 95, 158, 160, 1 }, "chartreuse",
		new int[] { 127, 255, 0, 1 }, "chocolate", new int[] { 210, 105, 30, 1 }, "coral",
		new int[] { 255, 127, 80, 1 }, "cornflowerblue", new int[] { 100, 149, 237, 1 }, "cornsilk",
		new int[] { 255, 248, 220, 1 }, "crimson", new int[] { 220, 20, 60, 1 }, "cyan", new int[] { 0, 255, 255, 1 },
		"darkblue", new int[] { 0, 0, 139, 1 }, "darkcyan", new int[] { 0, 139, 139, 1 }, "darkgoldenrod",
		new int[] { 184, 134, 11, 1 }, "darkgray", new int[] { 169, 169, 169, 1 }, "darkgreen",
		new int[] { 0, 100, 0, 1 }, "darkgrey", new int[] { 169, 169, 169, 1 }, "darkkhaki",
		new int[] { 189, 183, 107, 1 }, "darkmagenta", new int[] { 139, 0, 139, 1 }, "darkolivegreen",
		new int[] { 85, 107, 47, 1 }, "darkorange", new int[] { 255, 140, 0, 1 }, "darkorchid",
		new int[] { 153, 50, 204, 1 }, "darkred", new int[] { 139, 0, 0, 1 }, "darksalmon",
		new int[] { 233, 150, 122, 1 }, "darkseagreen", new int[] { 143, 188, 143, 1 }, "darkslateblue",
		new int[] { 72, 61, 139, 1 }, "darkslategray", new int[] { 47, 79, 79, 1 }, "darkslategrey",
		new int[] { 47, 79, 79, 1 }, "darkturquoise", new int[] { 0, 206, 209, 1 }, "darkviolet",
		new int[] { 148, 0, 211, 1 }, "deeppink", new int[] { 255, 20, 147, 1 }, "deepskyblue",
		new int[] { 0, 191, 255, 1 }, "dimgray", new int[] { 105, 105, 105, 1 }, "dimgrey",
		new int[] { 105, 105, 105, 1 }, "dodgerblue", new int[] { 30, 144, 255, 1 }, "firebrick",
		new int[] { 178, 34, 34, 1 }, "floralwhite", new int[] { 255, 250, 240, 1 }, "forestgreen",
		new int[] { 34, 139, 34, 1 }, "fuchsia", new int[] { 255, 0, 255, 1 }, "gainsboro",
		new int[] { 220, 220, 220, 1 }, "ghostwhite", new int[] { 248, 248, 255, 1 }, "gold",
		new int[] { 255, 215, 0, 1 }, "goldenrod", new int[] { 218, 165, 32, 1 }, "gray",
		new int[] { 128, 128, 128, 1 }, "green", new int[] { 0, 128, 0, 1 }, "greenyellow",
		new int[] { 173, 255, 47, 1 }, "grey", new int[] { 128, 128, 128, 1 }, "honeydew",
		new int[] { 240, 255, 240, 1 }, "hotpink", new int[] { 255, 105, 180, 1 }, "indianred",
		new int[] { 205, 92, 92, 1 }, "indigo", new int[] { 75, 0, 130, 1 }, "ivory", new int[] { 255, 255, 240, 1 },
		"khaki", new int[] { 240, 230, 140, 1 }, "lavender", new int[] { 230, 230, 250, 1 }, "lavenderblush",
		new int[] { 255, 240, 245, 1 }, "lawngreen", new int[] { 124, 252, 0, 1 }, "lemonchiffon",
		new int[] { 255, 250, 205, 1 }, "lightblue", new int[] { 173, 216, 230, 1 }, "lightcoral",
		new int[] { 240, 128, 128, 1 }, "lightcyan", new int[] { 224, 255, 255, 1 }, "lightgoldenrodyellow",
		new int[] { 250, 250, 210, 1 }, "lightgray", new int[] { 211, 211, 211, 1 }, "lightgreen",
		new int[] { 144, 238, 144, 1 }, "lightgrey", new int[] { 211, 211, 211, 1 }, "lightpink",
		new int[] { 255, 182, 193, 1 }, "lightsalmon", new int[] { 255, 160, 122, 1 }, "lightseagreen",
		new int[] { 32, 178, 170, 1 }, "lightskyblue", new int[] { 135, 206, 250, 1 }, "lightslategray",
		new int[] { 119, 136, 153, 1 }, "lightslategrey", new int[] { 119, 136, 153, 1 }, "lightsteelblue",
		new int[] { 176, 196, 222, 1 }, "lightyellow", new int[] { 255, 255, 224, 1 }, "lime",
		new int[] { 0, 255, 0, 1 }, "limegreen", new int[] { 50, 205, 50, 1 }, "linen", new int[] { 250, 240, 230, 1 },
		"magenta", new int[] { 255, 0, 255, 1 }, "maroon", new int[] { 128, 0, 0, 1 }, "mediumaquamarine",
		new int[] { 102, 205, 170, 1 }, "mediumblue", new int[] { 0, 0, 205, 1 }, "mediumorchid",
		new int[] { 186, 85, 211, 1 }, "mediumpurple", new int[] { 147, 112, 219, 1 }, "mediumseagreen",
		new int[] { 60, 179, 113, 1 }, "mediumslateblue", new int[] { 123, 104, 238, 1 }, "mediumspringgreen",
		new int[] { 0, 250, 154, 1 }, "mediumturquoise", new int[] { 72, 209, 204, 1 }, "mediumvioletred",
		new int[] { 199, 21, 133, 1 }, "midnightblue", new int[] { 25, 25, 112, 1 }, "mintcream",
		new int[] { 245, 255, 250, 1 }, "mistyrose", new int[] { 255, 228, 225, 1 }, "moccasin",
		new int[] { 255, 228, 181, 1 }, "navajowhite", new int[] { 255, 222, 173, 1 }, "navy",
		new int[] { 0, 0, 128, 1 }, "oldlace", new int[] { 253, 245, 230, 1 }, "olive", new int[] { 128, 128, 0, 1 },
		"olivedrab", new int[] { 107, 142, 35, 1 }, "orange", new int[] { 255, 165, 0, 1 }, "orangered",
		new int[] { 255, 69, 0, 1 }, "orchid", new int[] { 218, 112, 214, 1 }, "palegoldenrod",
		new int[] { 238, 232, 170, 1 }, "palegreen", new int[] { 152, 251, 152, 1 }, "paleturquoise",
		new int[] { 175, 238, 238, 1 }, "palevioletred", new int[] { 219, 112, 147, 1 }, "papayawhip",
		new int[] { 255, 239, 213, 1 }, "peachpuff", new int[] { 255, 218, 185, 1 }, "peru",
		new int[] { 205, 133, 63, 1 }, "pink", new int[] { 255, 192, 203, 1 }, "plum", new int[] { 221, 160, 221, 1 },
		"powderblue", new int[] { 176, 224, 230, 1 }, "purple", new int[] { 128, 0, 128, 1 }, "red",
		new int[] { 255, 0, 0, 1 }, "rosybrown", new int[] { 188, 143, 143, 1 }, "royalblue",
		new int[] { 65, 105, 225, 1 }, "saddlebrown", new int[] { 139, 69, 19, 1 }, "salmon",
		new int[] { 250, 128, 114, 1 }, "sandybrown", new int[] { 244, 164, 96, 1 }, "seagreen",
		new int[] { 46, 139, 87, 1 }, "seashell", new int[] { 255, 245, 238, 1 }, "sienna",
		new int[] { 160, 82, 45, 1 }, "silver", new int[] { 192, 192, 192, 1 }, "skyblue",
		new int[] { 135, 206, 235, 1 }, "slateblue", new int[] { 106, 90, 205, 1 }, "slategray",
		new int[] { 112, 128, 144, 1 }, "slategrey", new int[] { 112, 128, 144, 1 }, "snow",
		new int[] { 255, 250, 250, 1 }, "springgreen", new int[] { 0, 255, 127, 1 }, "steelblue",
		new int[] { 70, 130, 180, 1 }, "tan", new int[] { 210, 180, 140, 1 }, "teal", new int[] { 0, 128, 128, 1 },
		"thistle", new int[] { 216, 191, 216, 1 }, "tomato", new int[] { 255, 99, 71, 1 }, "turquoise",
		new int[] { 64, 224, 208, 1 }, "violet", new int[] { 238, 130, 238, 1 }, "wheat",
		new int[] { 245, 222, 179, 1 }, "white", new int[] { 255, 255, 255, 1 }, "whitesmoke",
		new int[] { 245, 245, 245, 1 }, "yellow", new int[] { 255, 255, 0, 1 }, "yellowgreen",
		new int[] { 154, 205, 50, 1 } };

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
		for ( int i = 0; i < array.length; i += 2 ) {
			GamaColor color = new NamedGamaColor((String) array[i], (int[]) array[i + 1]);
			colors.put((String) array[i], color);
			int_colors.put(color.getRGB(), color);
		}
	}

	public static class NamedGamaColor extends GamaColor {

		final String name;

		private NamedGamaColor(final String n, final int[] c) {
			// c must be of length 4.
			super(c[0], c[1], c[2], (double) c[3]);
			name = n;
		}

		@Override
		public String toString() {
			return "color[" + name + "]";
		}

		@Override
		public String toGaml() {
			return "°" + name;
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
		super(awtRGB);
	}

	// public GamaColor(final int r, final int g, final int b) {
	// this(normalize(r), normalize(g), normalize(b), 255);
	//
	// }

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
	public GamaColor(final int[] c) {
		this(c[0], c[1], c[2], c[3]); // c[3] not considered yet
	}

	@Override
	public String toString() {
		return "color[" + getRGB() /* + ";" + swtValue */+ "]";
	}

	@Override
	public String toGaml() {
		return "rgb (" + red() + ", " + green() + ", " + blue() + "," + getAlpha() + ")";
	}

	@Override
	public String stringValue(final IScope scope) {
		return String.valueOf(getRGB());
	}

	@getter(IKeyword.COLOR_RED)
	public Integer red() {
		return super.getRed();
	}

	@getter(IKeyword.COLOR_BLUE)
	public Integer blue() {
		return super.getBlue();
	}

	@getter(IKeyword.COLOR_GREEN)
	public Integer green() {
		return super.getGreen();
	}

	@getter(IKeyword.ALPHA)
	public Integer alpha() {
		return super.getAlpha();
	}

	@getter(IKeyword.BRIGHTER)
	public GamaColor getBrighter() {
		return new GamaColor(super.brighter());
	}

	@getter(IKeyword.DARKER)
	public GamaColor getDarker() {
		return new GamaColor(super.darker());
	}

	@Override
	public GamaColor copy(final IScope scope) {
		return new GamaColor(this);
	}

}
