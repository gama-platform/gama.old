/*********************************************************************************************
 *
 *
 * 'Colors.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import java.awt.Color;
import org.geotools.brewer.color.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 *
 * @todo Description
 *
 */
public class Colors {

	@operator(value = IKeyword.PLUS, can_be_const = true, category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "A new color resulting from the sum of the two operands, component by component",
		usages = @usage(
			value = "if both operands are colors, returns a new color resulting from the sum of the two operands, component by component",
			examples = { @example(value = "rgb([255, 128, 32]) + rgb('red')", equals = "rgb([255,128,32])") }))
	public static GamaColor add(final GamaColor c1, final GamaColor c2) {
		return new GamaColor(c1.getRed() + c2.getRed(), c1.getGreen() + c2.getGreen(), c1.getBlue() + c2.getBlue(),
			c1.alpha());
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "A new color resulting from the sum of each component of the color with the right operand",
		usages = @usage(
			value = "if one operand is a color and the other an integer, returns a new color resulting from the sum of each component of the color with the right operand",
			examples = { @example(value = "rgb([255, 128, 32]) + 3", equals = "rgb([255,131,35])") }))
	public static GamaColor add(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() + i, c.getGreen() + i, c.getBlue() + i, c.alpha());
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "a new color resulting from the subtraction of each component of the color with the right operand",
		usages = @usage(
			value = "if one operand is a color and the other an integer, returns a new color resulting from the subtraction of each component of the color with the right operand",
			examples = { @example(value = "rgb([255, 128, 32]) - 3", equals = "rgb([252,125,29])") }))
	public static GamaColor subtract(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() - i, c.getGreen() - i, c.getBlue() - i, c.alpha());
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "a new color resulting from the subtraction of the two operands, component by component",
		usages = @usage(
			value = "if both operands are colors, returns a new color resulting from the subtraction of the two operands, component by component",
			examples = { @example(value = "rgb([255, 128, 32]) - rgb('red')", equals = "rgb([0,128,32])") }))
	public static GamaColor subtract(final GamaColor c1, final GamaColor c) {
		return new GamaColor(c1.getRed() - c.getRed(), c1.getGreen() - c.getGreen(), c1.getBlue() - c.getBlue(),
			c1.alpha());
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true, category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "a new color resulting from the product of each component of the color with the right operand",
		usages = @usage(
			value = "if one operand is a color and the other an integer, returns a new color resulting from the product of each component of the color with the right operand (with a maximum value at 255)",
			examples = { @example(value = "rgb([255, 128, 32]) * 2", equals = "rgb([255,255,64])") }))
	public static GamaColor multiply(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() * i, c.getGreen() * i, c.getBlue() * i, c.alpha());
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true, category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "a new color resulting from the division of each component of the color by the right operand",
		usages = @usage(
			value = "if one operand is a color and the other an integer, returns a new color resulting from the division of each component of the color by the right operand",
			examples = { @example(value = "rgb([255, 128, 32]) / 2", equals = "rgb([127,64,16])") }))
	public static GamaColor divide(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() / i, c.getGreen() / i, c.getBlue() / i, c.alpha());
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true, category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(
		value = "a new color resulting from the division of each component of the color by the right operand. The result on each component is then truncated.",
		usages = @usage(
			value = "if one operand is a color and the other a double, returns a new color resulting from the division of each component of the color by the right operand. The result on each component is then truncated.",
			examples = { @example(value = "rgb([255, 128, 32]) / 2.5", equals = "rgb([102,51,13])") }))
	public static GamaColor divide(final GamaColor c, final Double i) {
		return new GamaColor(Maths.round(c.getRed() / i), Maths.round(c.getGreen() / i), Maths.round(c.getBlue() / i),
			c.alpha());
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
	// return new GamaColor(c1.getRed(), c1.getGreen(), c1.getBlue(), 255);
	// }

	@operator(value = "hsb", category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "Converts hsb (h=hue, s=saturation, b=brightness) value to Gama color",
		masterDoc = true,
		comment = "h,s and b components should be floating-point values between 0.0 and 1.0 and when used alpha should be an integer (between 0 and 255) or a float (between 0 and 1) . Examples: Red=(0.0,1.0,1.0), Yellow=(0.16,1.0,1.0), Green=(0.33,1.0,1.0), Cyan=(0.5,1.0,1.0), Blue=(0.66,1.0,1.0), Magenta=(0.83,1.0,1.0)",
		examples = @example(value = "hsb (0.0,1.0,1.0)", equals = "rgb(\"red\")"),
		see = "rgb")
	public static GamaColor hsb(final Double h, final Double s, final Double b) {
		return new GamaColor(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()));
	}

	@operator(value = "hsb", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "Converts hsb (h=hue, s=saturation, b=brightness) value to Gama color",
		examples = @example(value = "hsb (0.5,1.0,1.0,0.0)", equals = "rgb(\"cyan\",0)"))
	public static GamaColor hsb(final Double h, final Double s, final Double b, final Double a) {
		return new GamaColor(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()), a);
	}

	@operator(value = "hsb", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "Converts hsb (h=hue, s=saturation, b=brightness) value to Gama color")
	public static GamaColor hsb(final Double h, final Double s, final Double b, final Integer a) {
		return new GamaColor(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()), a);
	}

	@operator(value = "rgb", category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "Returns a color defined by red, green, blue components and an alpha blending value.",
		masterDoc = true,
		usages = @usage("It can be used with r=red, g=green, b=blue, each between 0 and 255"),
		examples = @example(value = "rgb (255,0,0)", equals = "#red"),
		see = "hsb")
	public static GamaColor rgb(final int r, final int g, final int b) {
		return new GamaColor(r, g, b, 255);
	}

	@operator(value = "rgb", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "rgb color",
		usages = @usage("It can be used with r=red, g=green, b=blue (each between 0 and 255), a=alpha (between 0 and 255)"),
		examples = { @example(value = "rgb (255,0,0,125)", equals = "a light red color", test = false),
			@example(value = "rgb (255,0,0,125).alpha", equals = "125", returnType = IKeyword.INT, isTestOnly = true) },
		see = "hsb")
	public static GamaColor rgb(final int r, final int g, final int b, final int alpha) {
		return new GamaColor(r, g, b, alpha);
	}

	@operator(value = "rgb", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "rgb color",
		usages = @usage("It can be used with r=red, g=green, b=blue (each between 0 and 255), a=alpha (between 0.0 and 1.0)"),
		examples = @example(value = "rgb (255,0,0,0.5)", equals = "a light red color", test = false),
		see = "hsb")
	public static GamaColor rgb(final int r, final int g, final int b, final double alpha) {
		return new GamaColor(r, g, b, alpha);
	}

	@operator(value = "rgb", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "rgb named color",
		usages = @usage("It can be used with a name of color and alpha (between 0 and 255)"),
		examples = @example(value = "rgb (\"red\")", equals = "rgb(255,0,0)"),
		see = "hsb")
	public static GamaColor rgb(final IScope scope, final String s, final int a) {
		return GamaColorType.staticCast(scope, s, a, false);
	}

	// to add
	/*
	 * @operator(value = "rgb", category={IOperatorCategory.COLOR})
	 *
	 * @doc(value = "rgb  named color", usages =
	 *
	 * @usage("It can be used with a name of color and alpha (between 0 and 1)"), examples =
	 *
	 * @example(value="rgb(\"red\",0.2)",equals="rgb([5,0,0])"), see = "hsb")
	 * public static GamaColor rgb(final IScope scope, final String s, final double a) {
	 * return GamaColorType.staticCast(scope, s, a);
	 * }
	 */

	@operator(value = "rgb", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "rgb color",
		usages = @usage("It can be used with a color and an alpha between 0 and 255"),
		examples = @example(value = "rgb(rgb(255,0,0),125)", equals = "a light red color", test = false),
		see = "hsb")
	public static GamaColor rgb(final IScope scope, final GamaColor s, final int a) {
		return GamaColorType.staticCast(scope, s, a, false);
	}

	@operator(value = "rgb", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "rgb color",
		usages = @usage("It can be used with a color and an alpha between 0 and 1"),
		examples = @example(value = "rgb(rgb(255,0,0),0.5)", equals = "a light red color", test = false),
		see = "hsb")
	public static GamaColor rgb(final IScope scope, final GamaColor s, final double a) {
		return GamaColorType.staticCast(scope, s, a, false);
	}

	@operator(value = "grayscale", category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "Converts rgb color to grayscale value",
		comment = "r=red, g=green, b=blue. Between 0 and 255 and gray = 0.299 `*` red + 0.587 `*` green + 0.114 `*` blue (Photoshop value)",
		examples = { @example(value = "grayscale (rgb(255,0,0))", equals = "to a dark grey", isExecutable = false),
			@example(value = "grayscale (rgb(255,0,0))", equals = "rgb(76,76,76)", isTestOnly = true) },
		see = { "rgb", "hsb" })
	public static GamaColor grayscale(final GamaColor c) {
		int grayValue = (int) (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
		return new GamaColor(grayValue, grayValue, grayValue, c.getAlpha());
	}

	@operator(value = "rnd_color", category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR, IConcept.RANDOM_OPERATOR} )
	@doc(value = "rgb color",
		comment = "Return a random color equivalent to rgb(rnd(operand),rnd(operand),rnd(operand))",
		examples = @example(value = "rnd_color(255)",
			equals = "a random color, equivalent to rgb(rnd(255),rnd(255),rnd(255))",
			test = false),
		see = { "rgb", "hsb" })
	public static GamaColor random_color(final IScope scope, final Integer max) {
		final RandomUtils r = scope.getRandom();
		int realMax = CmnFastMath.max(0, CmnFastMath.min(max, 255));
		return new GamaColor(r.between(0, realMax), r.between(0, realMax), r.between(0, realMax), 255);
	}

	@operator(value = "blend", category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "Blend two colors with an optional ratio (c1 `*` r + c2 `*` (1 - r)) between 0 and 1",
		masterDoc = true,
		examples = { @example(value = "blend(#red, #blue, 0.3)", equals = "rgb(76,0,178)", isTestOnly = true),
			@example(value = "blend(#red, #blue, 0.3)",
				equals = "to a color between the purple and the blue",
				isExecutable = false) },
		see = { "rgb", "hsb" })
	public static GamaColor blend(final GamaColor c1, final GamaColor c2, final double r) {
		double ir = 1.0 - r;
		GamaColor color =
			new GamaColor((int) (c1.getRed() * r + c2.getRed() * ir), (int) (c1.getGreen() * r + c2.getGreen() * ir),
				(int) (c1.getBlue() * r + c2.getBlue() * ir), (int) (c1.getAlpha() * r + c2.getAlpha() * ir));
		return color;
	}

	@operator(value = "blend", category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(
		value = "Blend two colors with an optional ratio (c1 `*` r + c2 `*` (1 - r)) between 0 and 1. If the ratio is omitted, an even blend is done",
		usages = @usage(value = "If the ratio is omitted, an even blend is done",
			examples = { @example(value = "blend(#red, #blue)", equals = "rgb(127,0,127)", isTestOnly = true), @example(
				value = "blend(#red, #blue)", equals = "to a color very close to the purple", isExecutable = false) }),
		see = { "rgb", "hsb" })
	public static GamaColor blend(final GamaColor color1, final GamaColor color2) {
		return blend(color1, color2, 0.5);
	}

	@operator(value = "brewer_colors", category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "Build a list of colors of a given type (see website http://colorbrewer2.org/)",
		examples = { @example(value = "list<rgb> colors <- brewer_colors(\"OrRd\");",
			equals = "a list of 6 blue colors",
			isExecutable = false) },
		see = { "brewer_palettes" })
	public static IList<GamaColor> brewerPaletteColors(final String type) {
		IList<GamaColor> colors = GamaListFactory.create(Types.COLOR);
		ColorBrewer brewer = ColorBrewer.instance();
		if ( brewer.hasPalette(type) ) {
			for ( Color col : brewer.getPalette(type).getColors() ) {
				if ( col != null ) {
					colors.add(new GamaColor(col));
				}
			}
		} else {
			throw GamaRuntimeException.error(type + " does not exist", null);
		}
		return colors;
	}

	@operator(value = "brewer_colors", content_type = IType.COLOR, category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(
		value = "Build a list of colors of a given type (see website http://colorbrewer2.org/) with a given number of classes",
		examples = { @example(value = "list<rgb> colors <- brewer_colors(\"Pastel1\", 10);",
			equals = "a list of 10 sequential colors",
			isExecutable = false) },
		see = { "brewer_palettes" })
	public static IList<GamaColor> brewerPaletteColors(final String type, final int nbClasses) {
		IList<GamaColor> cols = brewerPaletteColors(type);
		if ( cols.size() < nbClasses ) {
			throw GamaRuntimeException.error(type + " has less than " + nbClasses + " colors", null);
		} else {
			while (cols.size() > nbClasses) {
				cols.remove(cols.size() - 1);
			}
			return cols;
		}
	}

	@operator(value = "brewer_palettes", content_type = IType.COLOR, category = { IOperatorCategory.COLOR }, concept = {IConcept.COLOR} )
	@doc(value = "returns the list a palette with a given min number of classes and max number of classes)",
		examples = { @example(value = "list<rgb> colors <- brewer_palettes(5,10);",
			equals = "a list of palettes that are composed of a min of 5 colors and a max of 10 colors",
			isExecutable = false) },
		see = { "brewer_colors" })
	public static IList<String> brewerPaletteNames(final int min, final int max) {
		IList<String> palettes = GamaListFactory.create(Types.STRING);
		ColorBrewer brewer = ColorBrewer.instance();
		for ( BrewerPalette p : brewer.getPalettes() ) {
			if ( p.getCount() >= min && p.getCount() <= max ) {
				palettes.add(p.getName());
			}
		}
		return palettes;
	}

	@operator(value = "brewer_palettes", content_type = IType.STRING, category = { IOperatorCategory.COLOR }, concept = {} )
	@doc(value = "returns the list a palette with a given min number of classes and max number of classes)",
		examples = { @example(value = "list<rgb> colors <- brewer_palettes();",
			equals = "a list of palettes that are composed of a min of 5 colors",
			isExecutable = false) },
		see = { "brewer_colors" })
	public static IList<String> brewerPaletteNames(final int min) {
		IList<String> palettes = GamaListFactory.create(Types.STRING);
		ColorBrewer brewer = ColorBrewer.instance();
		for ( BrewerPalette p : brewer.getPalettes() ) {
			if ( p.getCount() >= min ) {
				palettes.add(p.getName());
			}
		}
		return palettes;
	}

}
