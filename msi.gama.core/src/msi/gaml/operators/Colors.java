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
package msi.gaml.operators;

import java.awt.Color;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaColorType;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 * 
 * @todo Description
 * 
 */
public class Colors {

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(value = "a new color resulting from the sum of the two operands, component by component", examples = { "rgb([255, 128, 32]) + rgb('red') 	--:  	rgb([255,128,32])" })
	public static GamaColor add(final GamaColor c1, final GamaColor c2) {
		return new GamaColor(c1.getRed() + c2.getRed(), c1.getGreen() + c2.getGreen(), c1.getBlue() + c2.getBlue(),
			c1.alpha());
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(value = "a new color resulting from the sum of each component of the color with the right operand", examples = { "rgb([255, 128, 32]) + 3 	--:  	rgb([255,131,35])" })
	public static GamaColor add(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() + i, c.getGreen() + i, c.getBlue() + i, c.alpha());
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(value = "a new color resulting from the substraction of each component of the color with the right operand", examples = { "rgb([255, 128, 32]) - 3 	--:  	rgb([252,125,29])" })
	public static GamaColor substract(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() - i, c.getGreen() - i, c.getBlue() - i, c.alpha());
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(value = "a new color resulting from the product of each component of the color with the right operand", examples = { "rgb([255, 128, 32]) * 2 	--:  	rgb([255,255,64])" })
	public static GamaColor multiply(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() * i, c.getGreen() * i, c.getBlue() * i, c.alpha());
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(value = "a new color resulting from the division of each component of the color by the right operand", examples = { "rgb([255, 128, 32]) / 2 	--:  	rgb([127,64,16])" })
	public static GamaColor divide(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() / i, c.getGreen() / i, c.getBlue() / i, c.alpha());
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(value = "a new color resulting from the division of each component of the color by the right operand. The result on each component is then truncated.", examples = { "rgb([255, 128, 32]) / 2.5 	--:  	rgb([102,51,13])" })
	public static GamaColor divide(final GamaColor c, final Double i) {
		return new GamaColor(Maths.round(c.getRed() / i), Maths.round(c.getGreen() / i), Maths.round(c.getBlue() / i),
			c.alpha());
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(value = "a new color resulting from the substraction of the two operands, component by component", examples = { "rgb([255, 128, 32]) - rgb('red')   	--:  	rgb([0,128,32])" })
	public static GamaColor substract(final GamaColor c1, final GamaColor c) {
		return new GamaColor(c1.getRed() - c.getRed(), c1.getGreen() - c.getGreen(), c1.getBlue() - c.getBlue(),
			c1.alpha());
	}

	//
	// @operator(value = "hsb_to_rgb")
	// @doc(value = "Converts hsb value to rgb color", comment =
	// "h=hue, s=saturation, b=brightness. h,s and b components should be floating-point values between 0.0 and 1.0.",
	// examples = "set color <- color hsb_to_rgb ([60,0.5,0]);"
	// +
	// "Hue value Red=[0.0,1.0,1.0], Yellow=[0.16,1.0,1.0], Green=[0.33,1.0,1.0], Cyan=[0.5,1.0,1.0], Blue=[0.66,1.0,1.0], Magenta=[0.83,1.0,1.0]",
	// see = "")
	// public static GamaColor hsbToRgb(final GamaColor c, final GamaList<Double> list) {
	// Color c1 = Color.getHSBColor(list.get(0).floatValue(), list.get(1).floatValue(), list.get(2).floatValue());
	// return new GamaColor(c1.getRed(), c1.getGreen(), c1.getBlue());
	// }

	@operator(value = "hsb")
	@doc(value = "Converts hsb value to Gama color", comment = "h=hue, s=saturation, b=brightness. h,s and b components should be floating-point values between 0.0 and 1.0.", examples = "set color <- hsb (60,0.5,0);"
		+ "Hue value Red=(0.0,1.0,1.0), Yellow=(0.16,1.0,1.0), Green=(0.33,1.0,1.0), Cyan=(0.5,1.0,1.0), Blue=(0.66,1.0,1.0), Magenta=(0.83,1.0,1.0)", see = "")
	public static GamaColor hsb(final Double h, final Double s, final Double b) {
		return new GamaColor(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()));
	}

	@operator(value = "hsb")
	@doc(value = "Converts hsb value to Gama color", comment = "h=hue, s=saturation, b=brightness. h,s and b components should be floating-point values between 0.0 and 1.0. Alpha can be an integer (between 0 and 255) or a float (between 0 and 1)", examples = "set color <- hsb (60,0.5,0);"
		+ "Hue value Red=(0.0,1.0,1.0), Yellow=(0.16,1.0,1.0), Green=(0.33,1.0,1.0), Cyan=(0.5,1.0,1.0), Blue=(0.66,1.0,1.0), Magenta=(0.83,1.0,1.0)", see = "")
	public static GamaColor hsb(final Double h, final Double s, final Double b, final Double a) {
		return new GamaColor(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()), a);
	}

	@operator(value = "hsb")
	@doc(value = "Converts hsb value to Gama color", comment = "h=hue, s=saturation, b=brightness. h,s and b components should be floating-point values between 0.0 and 1.0. Alpha can be an integer (between 0 and 255) or a float (between 0 and 1)", examples = "set color <- hsb (60,0.5,0);"
		+ "Hue value Red=(0.0,1.0,1.0), Yellow=(0.16,1.0,1.0), Green=(0.33,1.0,1.0), Cyan=(0.5,1.0,1.0), Blue=(0.66,1.0,1.0), Magenta=(0.83,1.0,1.0)", see = "")
	public static GamaColor hsb(final Double h, final Double s, final Double b, final Integer a) {
		return new GamaColor(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()), a);
	}

	@operator(value = "rgb")
	@doc(value = "rgb color", comment = "r=red, g=greeb, b=blue. Between 0 and 255", examples = "set color <- rgb (255,0,0);", see = "hsb")
	public static GamaColor rgb(final int r, final int g, final int b) {
		return new GamaColor(r, g, b, 255);
	}

	@operator(value = "rgb")
	@doc(value = "rgb color", comment = "r=red, g=greeb, b=blue, a=alpha. Each between 0 and 255", examples = "set color <- rgb (255,0,0);", see = "hsb")
	public static GamaColor rgb(final int r, final int g, final int b, final int alpha) {
		return new GamaColor(r, g, b, alpha);
	}

	@operator(value = "rgb")
	@doc(value = "rgb named color", comment = "Alpha must be between 0 and 255", examples = "set color <- rgb (255,0,0);", see = "hsb")
	public static GamaColor rgb(final IScope scope, final String s, final int a) {
		return GamaColorType.staticCast(scope, s, a);
	}

	@operator(value = "rgb")
	@doc(value = "rgb  named color", comment = "Alpha must be between 0 and 1", examples = "set color <- rgb (255,0,0);", see = "hsb")
	public static GamaColor rgb(final IScope scope, final String s, final double a) {
		return GamaColorType.staticCast(scope, s, a);
	}

	@operator(value = "rgb")
	@doc(value = "rgb color", comment = "Alpha must be between 0 and 255", examples = "set color <- rgb (255,0,0);", see = "hsb")
	public static GamaColor rgb(final IScope scope, final GamaColor s, final int a) {
		return GamaColorType.staticCast(scope, s, a);
	}

	@operator(value = "rgb")
	@doc(value = "rgb color", comment = "Alpha must be between 0 and 1", examples = "set color <- rgb (255,0,0);", see = "hsb")
	public static GamaColor rgb(final IScope scope, final GamaColor s, final double a) {
		return GamaColorType.staticCast(scope, s, a);
	}

	@operator(value = "grayscale")
	@doc(value = "Converts rgb color to grayscale value", comment = "r=red, g=greeb, b=blue. Between 0 and 255 and gray = 0.299 * red + 0.587 * green + 0.114 * blue (Photoshop value)", examples = "set grayscale_color <- grayscale (color);", see = "rgb,hsb")
	public static GamaColor grayscale(final GamaColor c) {
		int grayValue = (int) (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
		return new GamaColor(grayValue, grayValue, grayValue, c.getAlpha());
	}

	@operator(value = "blend")
	@doc(value = "Blend two colors with an optional ratio (c1 * r + c2 * (1 - r)) between 0 and 1", comment = "", examples = "rgb blended <- blend(°red, °blue, 0.3)", see = "rgb,hsb")
	public static GamaColor blend(final GamaColor c1, final GamaColor c2, final double r) {
		double ir = 1.0 - r;
		GamaColor color =
			new GamaColor((int) (c1.getRed() * r + c2.getRed() * ir), (int) (c1.getGreen() * r + c2.getGreen() * ir),
				(int) (c1.getBlue() * r + c2.getBlue() * ir), (int) (c1.getAlpha() * r + c2.getAlpha() * ir));
		return color;
	}

	/**
	 * Make an even blend between two colors.
	 * 
	 * @param c1 First color to blend.
	 * @param c2 Second color to blend.
	 * @return Blended color.
	 */
	@operator(value = "blend")
	@doc(value = "Blend two colors with an optional ratio (c1 * r + c2 * (1 - r)) between 0 and 1. If the ratio is ommitted, an even blend is done", comment = "", examples = "rgb blended <- blend(°red, °blue)", see = "rgb,hsb")
	public static GamaColor blend(final GamaColor color1, final GamaColor color2) {
		return blend(color1, color2, 0.5);
	}

}
