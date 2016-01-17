/*********************************************************************************************
 *
 *
 * 'Points.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 11 d�c. 2010
 *
 * @todo Description
 *
 */
public class Points {

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Double a, final Double b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Integer a, final Double b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Double a, final Integer b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Integer a, final Integer b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	public static ILocation toPoint(final Double x, final Double y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	public static ILocation toPoint(final Integer x, final Double y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	public static ILocation toPoint(final Integer x, final Integer y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	public static ILocation toPoint(final Integer x, final Integer y, final Integer z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	public static ILocation toPoint(final Double x, final Integer y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	public static ILocation toPoint(final Double x, final Integer y, final Integer z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true, category = IOperatorCategory.POINT)
	public static ILocation toPoint(final Double x, final Double y, final Integer z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinates divided by the number",
		usages = @usage(
			value = "if the left operand is a point, returns a new point with coordinates divided by the right operand",
			examples = { @example(value = "{5, 7.5} / 2.5", equals = "{2, 3}"),
				@example(value = "{2,5} / 4", equals = "{0.5,1.25}") }) )
	public static ILocation divide(final GamaPoint p, final Double d) {
		return new GamaPoint(p.x / d, p.y / d, p.z / d);
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinates divided by the number")
	public static ILocation divide(final GamaPoint p, final Integer d) {
		return new GamaPoint(p.x / d.doubleValue(), p.y / d.doubleValue(), p.z / d.doubleValue());
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinates multiplied by a number.")
	public static ILocation multiply(final GamaPoint p1, final Double d) {
		return new GamaPoint(p1.x * d, p1.y * d, p1.z * d);
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinates multiplied by a number.",
		usages = @usage(
			value = "if the left-hand operator is a point and the right-hand a number, returns a point with coordinates multiplied by the number",
			examples = { @example(value = "{2,5} * 4", equals = "{8.0, 20.0}"),
				@example(value = "{2, 4} * 2.5", equals = "{5.0, 10.0}") }) )
	public static ILocation multiply(final GamaPoint p1, final Integer d) {
		return new GamaPoint(p1.x * d.doubleValue(), p1.y * d.doubleValue(), p1.z / d.doubleValue());
	}

	// ATTENTION: produit scalaire.
	@operator(value = IKeyword.MULTIPLY, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns the scalar product of two points.",
		usages = @usage(value = "if both operands are points, returns their scalar product",
			examples = @example(value = "{2,5} * {4.5, 5}", equals = "34.0") ) )
	public static Double multiply(final GamaPoint p1, final GamaPoint p) {
		return p1.x * p.x + p1.y * p.y + p1.z * p.z;
	}

	@operator(value = "norm", can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "the norm of the vector with the coordinnates of the point operand.",
		examples = @example(value = "norm({3,4})", equals = "5.0") )
	public static Double norm(final IScope scope, final GamaPoint p) throws GamaRuntimeException {
		return Maths.sqrt(scope, p.x * p.x + p.y * p.y + p.z * p.z);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinate summing of the two operands.",
		usages = @usage(value = "if both operands are points, returns their sum.",
			examples = @example(value = "{1, 2} + {4, 5}", equals = "{5.0, 7.0}") ) )
	public static ILocation add(final GamaPoint p1, final GamaPoint p) {
		return new GamaPoint(p1.x + p.x, p1.y + p.y, p1.z + p.z);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinate summing of the two operands.",
		usages = @usage(
			value = "if the left-hand operand is a point and the right-hand a number, returns a new point with each coordinate as the sum of the operand coordinate with this number.",
			examples = { @example(value = "{1, 2} + 4", equals = "{5.0, 6.0,4.0}"),
				@example(value = "{1, 2} + 4.5", equals = "{5.5, 6.5,4.5}") }) )
	public static ILocation add(final GamaPoint p1, final Double p) {
		return new GamaPoint(p1.x + p, p1.y + p, p1.z + p);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinate summing of the two operands.")
	public static ILocation add(final GamaPoint p1, final Integer p) {
		return new GamaPoint(p1.x + p, p1.y + p, p1.z + p);
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinate resulting from the first operand minus the second operand.",
		usages = @usage(
			value = "if left-hand operand is a point and the right-hand a number, returns a new point with each coordinate as the difference of the operand coordinate with this number.",
			examples = { @example(value = "{1, 2} - 4.5", equals = "{-3.5, -2.5, -4.5}"),
				@example(value = "{1, 2} - 4", equals = "{-3.0,-2.0,-4.0}") }) )
	public static ILocation substract(final GamaPoint p1, final Double p) {
		return new GamaPoint(p1.x - p, p1.y - p, p1.z - p);
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinate resulting from the first operand minus the second operand.",
		usages = @usage(value = "if both operands are points, returns their difference (coordinates per coordinates).",
			examples = @example(value = "{1, 2} - {4, 5}", equals = "{-3.0, -3.0}") ) )
	public static ILocation substract(final GamaPoint p1, final GamaPoint p) {
		return new GamaPoint(p1.x - p.x, p1.y - p.y, p1.z - p.z);
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, category = IOperatorCategory.POINT)
	@doc(value = "Returns a point with coordinate resulting from the first operand minus the second operand.")
	public static ILocation substract(final GamaPoint p1, final Integer p) {
		return new GamaPoint(p1.x - p, p1.y - p, p1.z - p);
	}

	@operator(value = "with_precision", can_be_const = true)
	@doc(
		value = "Rounds off the ordinates of the left-hand point to the precision given by the value of right-hand operand",
		examples = { @example(value = "{12345.78943, 12345.78943, 12345.78943} with_precision 2 ",
			equals = "{12345.79, 12345.79, 12345.79}") },
		see = "round")
	public static ILocation round(final ILocation v, final Integer precision) {
		return new GamaPoint(Maths.round(v.getX(), precision), Maths.round(v.getY(), precision),
			Maths.round(v.getZ(), precision));
	}

	@operator(value = "round", can_be_const = true)
	@doc(value = "Returns the rounded value of the operand.",
		examples = { @example(value = "{12345.78943,  12345.78943, 12345.78943} with_precision 2",
			equals = "{12345.79,12345.79,12345.79}") },
		see = "round")
	public static ILocation round(final ILocation v) {
		return new GamaPoint(Maths.round(v.getX()), Maths.round(v.getY()), Maths.round(v.getZ()));
	}

}
