/*******************************************************************************************************
 *
 * Colors.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.ecore.EObject;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.compilation.IOperatorValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaColorType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class Colors.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 20 août 2023
 */
public class Colors {

	/**
	 * Adds the.
	 *
	 * @param c1
	 *            the c 1
	 * @param c2
	 *            the c 2
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "A new color resulting from the sum of the two operands, component by component. The alpha is the one of the left rgb color.",
			usages = @usage (
					value = "if both operands are colors, returns a new color resulting from the sum of the two operands, component by component",
					examples = { @example (
							value = "rgb([255, 128, 32]) + rgb('red')",
							equals = "rgb([255,128,32])") }))
	@test ("rgb([255, 128, 32]) + rgb('red') = rgb([255,128,32])")
	public static GamaColor add(final GamaColor c1, final GamaColor c2) {
		return GamaColor.get(c1.getRed() + c2.getRed(), c1.getGreen() + c2.getGreen(), c1.getBlue() + c2.getBlue(),
				c1.alpha());
	}

	/**
	 * Adds the.
	 *
	 * @param c
	 *            the c
	 * @param i
	 *            the i
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "A new color resulting from the sum of each component of the color with the right int operand. This value is directly added to the 0-255 values of r, g and b. The alpha component remains untouched",
			usages = @usage (
					value = "if one operand is a color and the other an integer, returns a new color resulting from the sum of each component of the color with the right operand",
					examples = { @example (
							value = "rgb([255, 128, 32]) + 3",
							equals = "rgb([255,131,35])") }))
	@test ("rgb([255, 128, 32]) + 3 = rgb([255,131,35]) ")
	public static GamaColor add(final GamaColor c, final Integer i) {
		return GamaColor.get(c.getRed() + i, c.getGreen() + i, c.getBlue() + i, c.alpha());
	}

	/**
	 * Subtract.
	 *
	 * @param c
	 *            the c
	 * @param i
	 *            the i
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "a new color resulting from the subtraction of each component of the color with the right operand",
			usages = @usage (
					value = "if one operand is a color and the other an integer, returns a new color resulting from the subtraction of each component of the color with the right operand",
					examples = { @example (
							value = "rgb([255, 128, 32]) - 3",
							equals = "rgb([252,125,29])") }))
	@test ("rgb([255, 128, 32]) - 3 = rgb([252,125,29]) ")
	public static GamaColor subtract(final GamaColor c, final Integer i) {
		return GamaColor.get(c.getRed() - i, c.getGreen() - i, c.getBlue() - i, c.alpha());
	}

	/**
	 * Subtract.
	 *
	 * @param c1
	 *            the c 1
	 * @param c
	 *            the c
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "a new color resulting from the subtraction of the two operands, component by component",
			usages = @usage (
					value = "if both operands are colors, returns a new color resulting from the subtraction of the two operands, component by component",
					examples = { @example (
							value = "rgb([255, 128, 32]) - rgb('red')",
							equals = "rgb([0,128,32])") }))
	@test ("rgb([255, 128, 32]) - rgb('red') = rgb([0,128,32])")
	public static GamaColor subtract(final GamaColor c1, final GamaColor c) {
		return GamaColor.get(c1.getRed() - c.getRed(), c1.getGreen() - c.getGreen(), c1.getBlue() - c.getBlue(),
				c1.alpha());
	}

	/**
	 * Multiply.
	 *
	 * @param c
	 *            the c
	 * @param i
	 *            the i
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "a new color resulting from the product of each component of the color with the right operand",
			usages = @usage (
					value = "if one operand is a color and the other an integer, returns a new color resulting from the product of each component of the color with the right operand (with a maximum value at 255)",
					examples = { @example (
							value = "rgb([255, 128, 32]) * 2",
							equals = "rgb([255,255,64])") }))
	@test ("rgb([255, 128, 32]) * 2 = rgb([255,255,64])")
	public static GamaColor multiply(final GamaColor c, final Integer i) {
		return GamaColor.get(c.getRed() * i, c.getGreen() * i, c.getBlue() * i, c.alpha());
	}

	/**
	 * Multiply.
	 *
	 * @param c
	 *            the c
	 * @param i
	 *            the i
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "a new color resulting from the product of each component of the color with the right operand",
			usages = @usage (
					value = "if one operand is a color and the other a float, returns a new color resulting from the product of each component of the color with the right operand (with a maximum value at 255)",
					examples = { @example (
							value = "rgb([255, 128, 32]) * 2.0",
							equals = "rgb([255,255,64])") }))
	@test ("rgb([255, 128, 32]) * 2.0 = rgb([255,255,64])")
	public static GamaColor multiply(final GamaColor c, final Double i) {
		return GamaColor.get((int) (c.getRed() * i), (int) (c.getGreen() * i), (int) (c.getBlue() * i), c.alpha());
	}

	/**
	 * Divide.
	 *
	 * @param c
	 *            the c
	 * @param i
	 *            the i
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "a new color resulting from the division of each component of the color by the right operand",
			usages = @usage (
					value = "if one operand is a color and the other an integer, returns a new color resulting from the division of each component of the color by the right operand",
					examples = { @example (
							value = "rgb([255, 128, 32]) / 2",
							equals = "rgb([127,64,16])") }))
	@test ("rgb([255, 128, 32]) / 2 = rgb([127,64,16])")
	public static GamaColor divide(final GamaColor c, final Integer i) {
		return GamaColor.get(c.getRed() / i, c.getGreen() / i, c.getBlue() / i, c.alpha());
	}

	/**
	 * Divide.
	 *
	 * @param c
	 *            the c
	 * @param i
	 *            the i
	 * @return the gama color
	 */
	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "a new color resulting from the division of each component of the color by the right operand. "
					+ "The result on each component is then truncated.",
			usages = @usage (
					value = "if one operand is a color and the other a double, returns a new color resulting "
							+ "from the division of each component of the color by the right operand. The result on each component is then truncated.",
					examples = { @example (
							value = "rgb([255, 128, 32]) / 2.5",
							equals = "rgb([102,51,13])") }))
	@test ("rgb([255, 128, 32]) / 2.5 = rgb([102,51,13])")
	public static GamaColor divide(final GamaColor c, final Double i) {
		return GamaColor.get(Maths.round(c.getRed() / i), Maths.round(c.getGreen() / i), Maths.round(c.getBlue() / i),
				c.alpha());
	}

	/**
	 * Hsb.
	 *
	 * @param h
	 *            the h
	 * @param s
	 *            the s
	 * @param b
	 *            the b
	 * @return the gama color
	 */
	@operator (
			value = "hsb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "Converts hsb (h=hue, s=saturation, b=brightness) value to Gama color",
			masterDoc = true,
			comment = "h,s and b components should be floating-point values between 0.0 and 1.0 and when used alpha should be an integer (between 0 and 255) or a float (between 0 and 1) . Examples: Red=(0.0,1.0,1.0), Yellow=(0.16,1.0,1.0), Green=(0.33,1.0,1.0), Cyan=(0.5,1.0,1.0), Blue=(0.66,1.0,1.0), Magenta=(0.83,1.0,1.0)",
			examples = @example (
					value = "hsb (0.0,1.0,1.0)",
					equals = "rgb(\"red\")"),
			see = "rgb")
	@test ("hsb (0.0,1.0,1.0) = rgb('red') ")
	public static GamaColor hsb(final Double h, final Double s, final Double b) {
		return GamaColor.get(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()));
	}

	/**
	 * Hsb.
	 *
	 * @param h
	 *            the h
	 * @param s
	 *            the s
	 * @param b
	 *            the b
	 * @param a
	 *            the a
	 * @return the gama color
	 */
	@operator (
			value = "hsb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "Converts hsb (h=hue, s=saturation, b=brightness) value to Gama color",
			examples = @example (
					value = "hsb (0.5,1.0,1.0,0.0)",
					equals = "rgb(\"cyan\",0)"))
	@test ("hsb (0.5,1.0,1.0,0.0) = rgb('cyan',0) ")
	public static GamaColor hsb(final Double h, final Double s, final Double b, final Double a) {
		return GamaColor.get(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()), a);
	}

	/**
	 * To HSB.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @return the i list
	 * @date 20 août 2023
	 */
	@operator (
			value = "to_hsb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "Converts a Gama color to hsb (h=hue, s=saturation, b=brightness) value",
			examples = @example (
					value = "to_hsb (#cyan)",
					equals = "[0.5,1.0,1.0]"))
	@test ("[0.5,1.0,1.0] = to_hsb(rgb('cyan',0)) ")
	public static IList<Double> toHSB(final GamaColor c) {
		IList<Double> hsb = GamaListFactory.create();
		float[] v = Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), null);
		hsb.add(Float.valueOf(v[0]).doubleValue());
		hsb.add(Float.valueOf(v[1]).doubleValue());
		hsb.add(Float.valueOf(v[2]).doubleValue());
		return hsb;
	}

	/**
	 * Hsb.
	 *
	 * @param h
	 *            the h
	 * @param s
	 *            the s
	 * @param b
	 *            the b
	 * @param a
	 *            the a
	 * @return the gama color
	 */
	@operator (
			value = "hsb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "Converts hsb (h=hue, s=saturation, b=brightness) value to Gama color")
	@test ("int(hsb(200,40, 90)) = -526409")
	public static GamaColor hsb(final Double h, final Double s, final Double b, final Integer a) {
		return GamaColor.get(Color.getHSBColor(h.floatValue(), s.floatValue(), b.floatValue()), a);
	}

	/**
	 * Rgb.
	 *
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @return the gama color
	 */
	@operator (
			value = "rgb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "Returns a color defined by red, green, blue components and an alpha blending value.",
			masterDoc = true,
			usages = @usage ("It can be used with r=red, g=green, b=blue, each between 0 and 255"),
			examples = @example (
					value = "rgb (255,0,0)",
					equals = "#red"),
			see = "hsb")
	@test ("rgb (255,0,0) = #red")
	public static GamaColor rgb(final int r, final int g, final int b) {
		return GamaColor.get(r, g, b, 255);
	}

	/**
	 * Rgb.
	 *
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @param alpha
	 *            the alpha
	 * @return the gama color
	 */
	@operator (
			value = "rgb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "rgb color",
			usages = @usage ("It can be used with r=red, g=green, b=blue (each between 0 and 255), a=alpha (between 0 and 255)"),
			examples = { @example (
					value = "rgb (255,0,0,125)",
					equals = "a light red color",
					test = false) },
			see = "hsb")
	@test ("rgb (255,0,0,125).alpha = 125")
	public static GamaColor rgb(final int r, final int g, final int b, final int alpha) {
		return GamaColor.get(r, g, b, alpha);
	}

	/**
	 * Rgb.
	 *
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @param alpha
	 *            the alpha
	 * @return the gama color
	 */
	@operator (
			value = "rgb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "rgb color",
			usages = @usage ("It can be used with r=red, g=green, b=blue (each between 0 and 255), a=alpha (between 0.0 and 1.0)"),
			examples = @example (
					value = "rgb (255,0,0,0.5)",
					equals = "a light red color",
					test = false),
			see = "hsb")
	@test (" int(rgb (255,0,0,0.5)) = 2147418112")
	public static GamaColor rgb(final int r, final int g, final int b, final double alpha) {
		return GamaColor.getWithDoubleAlpha(r, g, b, alpha);
	}

	/**
	 * Rgb.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param a
	 *            the a
	 * @return the gama color
	 */
	@operator (
			value = "rgb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "rgb named color",
			usages = @usage ("It can be used with a name of color and alpha (between 0 and 255)"),
			examples = @example (
					value = "rgb (\"red\")",
					equals = "rgb(255,0,0)"),
			see = "hsb")
	@test ("rgb ('red') = rgb(255,0,0) ")
	public static GamaColor rgb(final IScope scope, final String s, final int a) {
		return GamaColorType.staticCast(scope, s, a, false);
	}

	/**
	 * Rgb.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param a
	 *            the a
	 * @return the gama color
	 */
	@operator (
			value = "rgb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "rgb color",
			usages = @usage ("It can be used with a color and an alpha between 0 and 255"),
			examples = @example (
					value = "rgb(rgb(255,0,0),125)",
					equals = "a light red color",
					test = false),
			see = "hsb")
	@test ("int(rgb(rgb(255,0,0),125)) = 2113863680")
	public static GamaColor rgb(final IScope scope, final GamaColor s, final int a) {
		return GamaColorType.staticCast(scope, s, a, false);
	}

	/**
	 * Rgb.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param a
	 *            the a
	 * @return the gama color
	 */
	@operator (
			value = "rgb",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "rgb color",
			usages = @usage ("It can be used with a color and an alpha between 0 and 1"),
			examples = @example (
					value = "rgb(rgb(255,0,0),0.5)",
					equals = "a light red color",
					test = false),
			see = "hsb")
	@test ("int(rgb(rgb(255,0,0),0.5)) = 2147418112")
	public static GamaColor rgb(final IScope scope, final GamaColor s, final double a) {
		return GamaColor.get(s, a);
	}

	/**
	 * Grayscale.
	 *
	 * @param c
	 *            the c
	 * @return the gama color
	 */
	@operator (
			value = "grayscale",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "Converts rgb color to grayscale value",
			comment = "r=red, g=green, b=blue. Between 0 and 255 and gray = 0.299 `*` red + 0.587 `*` green + 0.114 `*` blue (Photoshop value)",
			examples = { @example (
					value = "grayscale (rgb(255,0,0))",
					equals = "to a dark grey",
					isExecutable = false), },
			see = { "rgb", "hsb" })
	@test ("int(grayscale (rgb(255,0,0))) = -11776948")
	@test ("grayscale (rgb(255,0,0)) = rgb(76,76,76)")
	public static GamaColor grayscale(final GamaColor c) {
		final int grayValue = (int) (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
		return GamaColor.get(grayValue, grayValue, grayValue, c.getAlpha());
	}

	/**
	 * Random color.
	 *
	 * @param scope
	 *            the scope
	 * @param max
	 *            the max
	 * @return the gama color
	 */
	@operator (
			value = "rnd_color",
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR, IConcept.RANDOM })
	@doc (
			value = "rgb color",
			comment = "Return a random color equivalent to rgb(rnd(operand),rnd(operand),rnd(operand))",
			examples = @example (
					value = "rnd_color(255)",
					equals = "a random color, equivalent to rgb(rnd(255),rnd(255),rnd(255))",
					test = false),
			see = { "rgb", "hsb" })
	@test ("seed <- 1.0; int(rnd_color(255)) = -3749758")
	public static GamaColor random_color(final IScope scope, final Integer max) {
		final RandomUtils r = scope.getRandom();
		final int realMax = Math.max(0, Math.min(max, 255));
		return GamaColor.get(r.between(0, realMax), r.between(0, realMax), r.between(0, realMax), 255);
	}

	/**
	 * Random color.
	 *
	 * @param scope
	 *            the scope
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the gama color
	 */
	@operator (
			value = "rnd_color",
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR, IConcept.RANDOM })
	@doc (
			value = "Return a random color equivalent to rgb(rnd(first_op, last_op),rnd(first_op, last_op),rnd(first_op, last_op))",
			comment = "",
			examples = @example (
					value = "rnd_color(100, 200)",
					equals = "a random color, equivalent to rgb(rnd(100, 200),rnd(100, 200),rnd(100, 200))",
					test = false),
			see = { "rgb", "hsb" })
	@test ("seed <- 1.0; int(rnd_color(100, 200)) = -5065833")
	public static GamaColor random_color(final IScope scope, final Integer min, final Integer max) {
		final RandomUtils r = scope.getRandom();
		final int realMax = Math.max(0, Math.min(max, 255));
		final int realMin = Math.max(0, Math.min(min, realMax));
		return GamaColor.get(r.between(realMin, realMax), r.between(realMin, realMax), r.between(realMin, realMax),
				255);
	}

	/**
	 * Blend.
	 *
	 * @param c1
	 *            the c 1
	 * @param c2
	 *            the c 2
	 * @param r
	 *            the r
	 * @return the gama color
	 */
	@operator (
			value = "blend",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "Blend two colors with an optional ratio (c1 `*` r + c2 `*` (1 - r)) between 0 and 1",
			masterDoc = true,
			examples = { @example (
					value = "blend(#red, #blue, 0.3)",
					equals = "to a color between the purple and the blue",
					isExecutable = false) },
			see = { "rgb", "hsb" })
	@test ("blend(#red, #blue, 0.3) = rgb(76,0,178)")
	public static GamaColor blend(final GamaColor c1, final GamaColor c2, final double r) {
		final double ir = 1.0 - r;
		return GamaColor.get((int) (c1.getRed() * r + c2.getRed() * ir), (int) (c1.getGreen() * r + c2.getGreen() * ir),
				(int) (c1.getBlue() * r + c2.getBlue() * ir), (int) (c1.getAlpha() * r + c2.getAlpha() * ir));
	}

	/**
	 * Blend.
	 *
	 * @param color1
	 *            the color 1
	 * @param color2
	 *            the color 2
	 * @return the gama color
	 */
	@operator (
			value = "blend",
			can_be_const = true,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "Blend two colors with an optional ratio (c1 `*` r + c2 `*` (1 - r)) between 0 and 1. If the ratio is omitted, an even blend is done",
			usages = @usage (
					value = "If the ratio is omitted, an even blend is done",
					examples = { @example (
							value = "blend(#red, #blue)",
							equals = "to a color very close to the purple",
							isExecutable = false) }),
			see = { "rgb", "hsb" })
	@test ("blend(#red, #blue) = rgb(127,0,127)")
	public static GamaColor blend(final GamaColor color1, final GamaColor color2) {
		return blend(color1, color2, 0.5);
	}

	/**
	 * The Class BrewerValidator.
	 */
	public static class BrewerValidator implements IOperatorValidator {

		@Override
		public boolean validate(final IDescription context, final EObject emfContext, final IExpression... arguments) {
			if (arguments[0].isConst()) {
				final Object palette = arguments[0].getConstValue();
				if (palette instanceof final String p) {
					if (!BREWER.hasPalette(p)) {
						context.error(
								"Palette " + p + " does not exist. Available palette names are: "
										+ Arrays.toString(BREWER.getPaletteNames()),
								UNKNOWN_ARGUMENT, getArg(emfContext, 1));
						return false;
					}
					if (arguments.length > 1) {
						final IExpression exp = arguments[1];
						if (exp.isConst()) {
							final Object number = exp.getConstValue();
							if (number instanceof Integer) {
								final BrewerPalette pal = BREWER.getPalette(p);
								if (pal.getCount() < (Integer) number) {
									context.warning("Palette " + p + " has only " + pal.getCount() + " colors.",
											IGamlIssue.WRONG_VALUE, getArg(emfContext, 1),
											String.valueOf(pal.getCount()));
								}
							}
						}
					}
				}
			}
			return true;
		}
	}

	/** The Constant BREWER. */
	static final ColorBrewer BREWER = ColorBrewer.instance();

	/** The Constant BREWER_CACHE. */
	static final LoadingCache<String, GamaPalette> BREWER_CACHE =
			CacheBuilder.newBuilder().build(new CacheLoader<String, GamaPalette>() {

				@Override
				public GamaPalette load(final String name) throws Exception {
					IList<GamaColor> colors = GamaListFactory.create(Types.COLOR);
					 BrewerPalette  p= BREWER.getPalette(name);
					for (final Color col : p.getColors()) { if (col != null) { colors.add(GamaColor.get(col)); } }
					return new GamaPalette(colors);
				}
			});

	/**
	 * Brewer palette colors.
	 *
	 * @param scope
	 *            the scope
	 * @param type
	 *            the type
	 * @return the gama palette
	 */
	@validator (BrewerValidator.class)
	@operator (
			value = "brewer_colors",
			type = IType.LIST,
			content_type = IType.COLOR,
			can_be_const = false,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "Build a list of colors of a given type (see website http://colorbrewer2.org/). The list of palettes can be obtained by calling brewer_palettes. "
					+ "This list can be safely modified afterwards (adding or removing colors)",
			examples = { @example (
					value = "list<rgb> colors <- brewer_colors(\"OrRd\");",
					equals = "a list of 6 blue colors",
					isExecutable = false) },
			see = { "brewer_palettes" })
	@no_test
	public static GamaPalette brewerPaletteColors(final IScope scope, final String type) {
		if (!BREWER.hasPalette(type)) throw GamaRuntimeException.error(type + " does not exist", scope);
		try {
			return new GamaPalette(BREWER_CACHE.get(type));
		} catch (ExecutionException e) {
			throw GamaRuntimeException.error(type + " cannot be retrieved", scope);
		}
	}

	/**
	 * Brewer palette colors.
	 *
	 * @param scope
	 *            the scope
	 * @param type
	 *            the type
	 * @param nbClasses
	 *            the nb classes
	 * @return the gama palette
	 */
	@validator (BrewerValidator.class)
	@operator (
			value = "brewer_colors",
			can_be_const = false,
			type = IType.LIST,
			content_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "Build a list of colors of a given type (see website http://colorbrewer2.org/) with a given number of classes",
			examples = { @example (
					value = "list<rgb> colors <- brewer_colors(\"Pastel1\", 5);",
					equals = "a list of 5 sequential colors in the palette named 'Pastel1'. The list of palettes can be obtained by calling brewer_palettes",
					isExecutable = false) },
			see = { "brewer_palettes" })
	@no_test
	public static GamaPalette brewerPaletteColors(final IScope scope, final String type, final int nbClasses) {
		final GamaPalette cols = brewerPaletteColors(scope, type);
		if (cols.size() < nbClasses)
			throw GamaRuntimeException.error(type + " has less than " + nbClasses + " colors", scope);
		final IList<GamaColor> colors = GamaListFactory.create(Types.COLOR);
		for (int i = 0; i < nbClasses; i++) { colors.add(cols.get(i)); }
		return new GamaPalette(colors);
	}

	/**
	 * Brewer palette names.
	 *
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the i list
	 */
	@operator (
			value = "brewer_palettes",
			can_be_const = false,
			content_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "returns the list a palette with a given min number of classes and max number of classes)",
			examples = { @example (
					value = "list<string> palettes <- brewer_palettes(5,10);",
					equals = "a list of palettes that are composed of a min of 5 colors and a max of 10 colors",
					isExecutable = false) },
			see = { "brewer_colors" })
	@no_test
	public static IList<String> brewerPaletteNames(final int min, final int max) {
		final IList<String> palettes = GamaListFactory.create(Types.STRING);
		for (final BrewerPalette p : BREWER.getPalettes()) {
			if (p.getCount() >= min && p.getCount() <= max) { palettes.add(p.getName()); }
		}
		return palettes;
	}

	/**
	 * Brewer palette names.
	 *
	 * @param min
	 *            the min
	 * @return the i list
	 */
	@operator (
			value = "brewer_palettes",
			can_be_const = false,
			content_type = IType.STRING,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "returns the list a palette with a given min number of classes)",
			examples = { @example (
					value = "list<string> palettes <- brewer_palettes(3);",
					equals = "a list of palettes that are composed of a min of 3 colors",
					isExecutable = false) },
			see = { "brewer_colors" })
	@no_test
	public static IList<String> brewerPaletteNames(final int min) {
		final IList<String> palettes = GamaListFactory.create(Types.STRING);
		for (final BrewerPalette p : BREWER.getPalettes()) { if (p.getCount() >= min) { palettes.add(p.getName()); } }
		return palettes;
	}

	/**
	 * The Class GamaGradient.
	 */
	@SuppressWarnings ("unchecked")
	public static class GamaGradient extends GamaMap<GamaColor, Double> {

		/**
		 * Instantiates a new gama gradient.
		 */
		protected GamaGradient() {
			super(5, Types.COLOR, Types.FLOAT);
		}

		/**
		 * Sets the.
		 *
		 * @param scope
		 *            the scope
		 * @param values
		 *            the values
		 */
		public void set(final IScope scope, final IMap<Object, Object> values) {
			for (Map.Entry<Object, Object> entry : values.entrySet()) {
				this.put(Cast.asColor(scope, entry.getKey()), Cast.asFloat(scope, entry.getValue()));
			}
		}

	}

	/**
	 * The Class GamaScale.
	 */
	@SuppressWarnings ("unchecked")
	public static class GamaScale extends GamaMap<Double, GamaColor> {

		/**
		 * Instantiates a new gama scale.
		 *
		 * @param scope
		 *            the scope
		 * @param values
		 *            the values
		 */
		public GamaScale(final IScope scope, final IMap<Double, GamaColor> values) {
			super(values.size(), Types.FLOAT, Types.COLOR);
			sort(values);
		}

		/**
		 * Sort.
		 *
		 * @param values
		 *            the values
		 */
		void sort(final Map<Double, GamaColor> values) {
			List<Map.Entry<Double, GamaColor>> entries = new ArrayList(values.entrySet());
			Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<Double, GamaColor> entry : entries) { put(entry.getKey(), entry.getValue()); }
		}

	}

	/**
	 * The Class GamaPalette.
	 */
	public static class GamaPalette extends GamaList<GamaColor> {

		/**
		 * Instantiates a new gama palette.
		 *
		 * @param colors
		 *            the colors
		 */
		GamaPalette(final IList<GamaColor> colors) {
			super(100, Types.COLOR);
			addAll(colors);
		}
	}

	/**
	 * Gradient.
	 *
	 * @param start
	 *            the start
	 * @param stop
	 *            the stop
	 * @return the gama gradient
	 */
	@operator (
			value = "gradient",
			can_be_const = true,
			type = IType.MAP,
			content_type = IType.FLOAT,
			index_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "returns the definition of a linear gradient between two colors, represented internally as a color map [start::0.0,stop::1.0]")
	@no_test
	public static GamaGradient gradient(final GamaColor start, final GamaColor stop) {
		GamaGradient cm = new GamaGradient();
		cm.put(start, 0d);
		cm.put(stop, 1d);
		return cm;
	}

	/**
	 * Gradient.
	 *
	 * @param start
	 *            the start
	 * @param stop
	 *            the stop
	 * @param r
	 *            the r
	 * @return the gama gradient
	 */
	@operator (
			value = "gradient",
			can_be_const = true,
			type = IType.MAP,
			content_type = IType.FLOAT,
			index_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "returns the definition of a linear gradient between two colors, with a ratio (between 0 and 1, otherwise clamped) represented internally as a color map [start::0.0,(start*r+stop*(1-r))::r, stop::1.0]")
	@no_test
	public static GamaGradient gradient(final GamaColor start, final GamaColor stop, final Double r) {
		double val = r < 0 ? 0 : r > 1 ? 1 : r;
		GamaGradient cm = new GamaGradient();
		cm.put(start, 0d);
		cm.put(blend(start, stop, val), val);
		cm.put(stop, 1d);
		return cm;
	}

	/**
	 * Gradient.
	 *
	 * @param colors
	 *            the colors
	 * @return the gama gradient
	 */
	@operator (
			value = "gradient",
			can_be_const = true,
			type = IType.MAP,
			content_type = IType.FLOAT,
			index_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "returns the definition of a linear gradient between n colors, represented internally as a color map [c1::1/n,c2::1/n, ... cn::1/n]")
	@no_test
	public static GamaGradient gradient(final IList<GamaColor> colors) {
		GamaGradient cm = new GamaGradient();
		int nb = colors.size();
		for (GamaColor c : colors) { cm.put(c, 1d / nb); }
		return cm;
	}

	/**
	 * Gradient.
	 *
	 * @param colors
	 *            the colors
	 * @return the gama gradient
	 */
	@operator (
			value = "gradient",
			can_be_const = true,
			type = IType.MAP,
			expected_content_type = IType.FLOAT,
			content_type = IType.FLOAT,
			index_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "returns the definition of a linear gradient between n colors provided with their positions on a scale between 0 and 1. "
					+ "A similar color map is returned, in the same color order, with all the positions normalized (so that they are shifted and scaled to fit between 0 and 1). Throws an error if the number of colors is less than 2 or if the positions are not strictly ordered")
	@no_test
	public static GamaGradient gradient(final IScope scope, final IMap<GamaColor, Number> colors) {
		if (colors.size() < 2) throw GamaRuntimeException.error("A gradient must at least propose 2 colors", scope);

		GamaGradient cm = new GamaGradient();
		Double min = Double.MAX_VALUE;
		Double max = Double.MIN_VALUE;
		double previous = -Double.MIN_VALUE;
		for (Number n : colors.values()) {
			double v = n.doubleValue();
			if (v <= previous) throw GamaRuntimeException.error(
					"The positions of the colors in the gradient must be provided in a stricly increasing order",
					scope);
			if (v < min) {
				min = v;
			} else if (v > max) { max = v; }
		}
		double low = min;
		double div = max - min;
		colors.forEach((c, f) -> cm.put(c, (f.doubleValue() + low) / div));
		return cm;
	}

	/**
	 * Scale.
	 *
	 * @param scope
	 *            the scope
	 * @param colors
	 *            the colors
	 * @return the gama scale
	 */
	@operator (
			value = "scale",
			can_be_const = true,
			type = IType.MAP,
			content_type = IType.FLOAT,
			index_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			see = "gradient",
			value = "Similar to gradient(map<rgb, float>) but reorders the colors based on their weight and does not normalize them, so as to effectively represent a color scale (i.e. a correspondance between a range of value and a color that implicitly begins with the lowest value)"
					+ "For instance scale([#red::10, #green::0, #blue::30]) would produce the reverse map and associate #green to the interval 0-10, #red to 10-30, and #blue above 30. The main difference in usages is that, for instance in the definition of a "
					+ "mesh to display, a gradient will produce interpolated colors to accomodate for the intermediary values, while a scale will stick to the colors defined.")
	@no_test
	public static GamaScale scale(final IScope scope, final IMap<GamaColor, Object> colors) {
		IMap<Double, GamaColor> map = GamaMapFactory.createOrdered();
		colors.forEach((c, f) -> map.put(Cast.asFloat(scope, f), c));
		return new GamaScale(scope, map);
	}

	/**
	 * Scale.
	 *
	 * @param scope
	 *            the scope
	 * @param colors
	 *            the colors
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the gama scale
	 */
	@operator (
			value = "scale",
			can_be_const = true,

			type = IType.MAP,
			content_type = IType.COLOR,
			index_type = IType.FLOAT,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			see = "gradient",
			value = "Expects a gradient, i.e. a map<rgb,float>, where values represent the different stops of the colors. "
					+ "First normalizes the passed gradient, and then applies the resulting weights to the interval represented by min and max, so as to return a scale (i.e. absolute values instead of the stops")
	@no_test
	public static GamaScale scale(final IScope scope, final IMap<GamaColor, Object> colors, final double min,
			final double max) {
		double sum = 0d;
		for (Map.Entry<GamaColor, Object> entry : colors.entrySet()) {
			// To make sure ?
			double d = Cast.asFloat(scope, entry.getValue());
			entry.setValue(d);
			sum += d;
		}
		double div = sum;
		IMap<Double, GamaColor> map = GamaMapFactory.createOrdered();
		colors.forEach((c, f) -> map.put(min + (max - min) * Cast.asFloat(scope, f) / div, c));
		return new GamaScale(scope, map);
	}

	/**
	 * Palette.
	 *
	 * @param scope
	 *            the scope
	 * @param colors
	 *            the colors
	 * @param nb
	 *            the nb
	 * @return the gama palette
	 */
	@operator (
			value = "palette",
			can_be_const = true,
			type = IType.LIST,
			content_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = {})
	@doc (
			value = "returns a list of n colors chosen in the gradient provided. Colors are chosen by interpolating the stops of the gradient (the colors) using their weight, in the order described in the gradient. In case the map<rgb, float> passed in argument is not a gradient but a scale, the colors will be chosen in the set of colors and might appear duplicated in the palette")
	@no_test
	public static GamaPalette palette(final IScope scope, final IMap<GamaColor, Number> colors, final int nb) {
		// var cm = gradient(scope, colors); // to make sure it is normalized
		// Not yet ready...
		return null;
	}

	/**
	 * Palette.
	 *
	 * @param scope
	 *            the scope
	 * @param colors
	 *            the colors
	 * @return the gama palette
	 */
	@operator (
			value = "palette",
			can_be_const = true, 
			type = IType.LIST,
			expected_content_type = IType.COLOR,
			content_type = IType.COLOR,
			category = { IOperatorCategory.COLOR },
			concept = { IConcept.COLOR })
	@doc (
			value = "transforms a list of n colors into a palette (necessary for some layers)")
	@no_test
	public static GamaPalette palette(final IScope scope, final IList<GamaColor> colors) {
		return new GamaPalette(colors);
	}

}
