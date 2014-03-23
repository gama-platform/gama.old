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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 11 d�c. 2010
 * 
 * @todo Description
 * 
 */
public class Points {

	@operator(value = IKeyword.POINT, can_be_const = true)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Double a, final Double b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Integer a, final Double b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Double a, final Integer b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	@doc(value = "internal use only. Use the standard construction {x,y} instead.")
	// "special" operator introduced in the parser for the points
	public static ILocation toPoint(final Integer a, final Integer b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	public static ILocation toPoint(final Double x, final Double y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	public static ILocation toPoint(final Integer x, final Double y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	public static ILocation toPoint(final Integer x, final Integer y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	public static ILocation toPoint(final Integer x, final Integer y, final Integer z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	public static ILocation toPoint(final Double x, final Integer y, final Double z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	public static ILocation toPoint(final Double x, final Integer y, final Integer z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = IKeyword.POINT, can_be_const = true)
	public static ILocation toPoint(final Double x, final Double y, final Integer z) {
		return new GamaPoint(x, y, z);
	}

	@operator(value = "add_z", can_be_const = true)
	@doc(deprecated = "This idiom is deprecated. Use the standard construction {x,y,z} instead. ")
	public static ILocation add_z(final GamaPoint p, final Double z) {
		return new GamaPoint(p.x, p.y, z);
	}

	@operator(value = "add_z", can_be_const = true)
	@doc(deprecated = "This idiom is deprecated. Use the standard construction {x,y,z} instead. ")
	public static ILocation add_z(final GamaPoint p, final Integer z) {
		return new GamaPoint(p.x, p.y, z);
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(value = "Returns a point with coordinates divided by the number", examples = "{5, 7.5} / 2.5 --: {2, 3}")
	public static ILocation divide(final GamaPoint p, final Double d) {
		return new GamaPoint(p.x / d, p.y / d, p.z / d);
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(special_cases = "if the left-hand operator is a point and the right-hand a number, returns a point with coordinates divided by the number", examples = "{2,5} / 4	--:  {0.5;1.25}")
	public static ILocation divide(final GamaPoint p, final Integer d) {
		return new GamaPoint(p.x / d.doubleValue(), p.y / d.doubleValue(), p.z / d.doubleValue());
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(value = "Returns a point with coordinate multiplied by the number.", examples = "{2, 3} + 2.5 --: {5, 7.5}")
	public static ILocation multiply(final GamaPoint p1, final Double d) {
		return new GamaPoint(p1.x * d, p1.y * d, p1.z * d);
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(special_cases = "if the left-hand operator is a point and the right-hand a number, returns a point with coordinates multiplied by the number", examples = "{2,5} * 4 	--: {8.0; 20.0}")
	public static ILocation multiply(final GamaPoint p1, final Integer d) {
		return new GamaPoint(p1.x * d.doubleValue(), p1.y * d.doubleValue(), p1.z / d.doubleValue());
	}

	// ATTENTION: produit scalaire.
	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(value = "Returns the scalar product of two points.", special_cases = "if both operands are points, returns their scalar product", examples = "{2,5} * {4.5, 5} 	--:  34.0")
	public static Double multiply(final GamaPoint p1, final GamaPoint p) {
		return p1.x * p.x + p1.y * p.y + p1.z * p.z;

	}

	@operator(value = "norm", can_be_const = true)
	@doc(value = "the norm of the vector with the coordinnates of the point operand.", examples = "norm({3,4})   --:	  5.0")
	public static Double norm(final GamaPoint p) throws GamaRuntimeException {
		return Maths.sqrt(p.x * p.x + p.y * p.y + p.z * p.z);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(value = "Returns a point with coordinate summing of the two operands.", special_cases = "if both operands are points, returns their sum.", examples = "{1, 2} + {4, 5} 	--:	 {5.0;7.0}")
	public static ILocation add(final GamaPoint p1, final GamaPoint p) {
		return new GamaPoint(p1.x + p.x, p1.y + p.y, p1.z + p.z);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(special_cases = "if left-hand operand is a point and the right-hand a number, returns a new point with each coordinate as the sum of the operand coordinate with this number.", examples = "{1, 2} + 4.5 	--:	 {5.5, 6.5}")
	public static ILocation add(final GamaPoint p1, final Double p) {
		return new GamaPoint(p1.x + p, p1.y + p, p1.z + p);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(examples = "{1, 2} + 4 	--:	 {5.0;6.0}")
	public static ILocation add(final GamaPoint p1, final Integer p) {
		return new GamaPoint(p1.x + p, p1.y + p, p1.z + p);
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(value = "Returns a point with coordinate resulting from the first operand minus the second operand.", special_cases = "if left-hand operand is a point and the right-hand a number, returns a new point with each coordinate as the difference of the operand coordinate with this number.", examples = "{1, 2} - 4.5 	--:	 {-3.5, -2.5}")
	public static ILocation substract(final GamaPoint p1, final Double p) {
		return new GamaPoint(p1.x - p, p1.y - p, p1.z - p);
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(special_cases = "if both operands are points, returns their difference.", examples = "{1, 2} - {4, 5} 	--:	 {-3.0;-3.0}")
	public static ILocation substract(final GamaPoint p1, final GamaPoint p) {
		return new GamaPoint(p1.x - p.x, p1.y - p.y, p1.z - p.z);
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(examples = "{1, 2} - 4 	--:	 {-3.0;-2.0}")
	public static ILocation substract(final GamaPoint p1, final Integer p) {
		return new GamaPoint(p1.x - p, p1.y - p, p1.z - p);
	}

	@operator(value = "with_precision", can_be_const = true)
	@doc(value = "Rounds off the ordinates of the left-hand point to the precision given by the value of right-hand operand", examples = { "point f <- (12345.78943,  12345.78943, 12345.78943) with_precision 2;	// f equals {12345.79, 12345.79, 12345.79]" }, see = "round")
	public static ILocation round(final ILocation v, final Integer precision) {
		return new GamaPoint(Maths.round(v.getX(), precision), Maths.round(v.getY(), precision), Maths.round(v.getZ(),
			precision));
	}

	@operator(value = "round", can_be_const = true)
	@doc(value = "Rounds off the ordinates of the left-hand point to the precision given by the value of right-hand operand", examples = { "point f <- (12345.78943,  12345.78943, 12345.78943) with_precision 2;	// f equals {12345.79, 12345.79, 12345.79]" }, see = "round")
	public static ILocation round(final ILocation v) {
		return new GamaPoint(Maths.round(v.getX()), Maths.round(v.getY()), Maths.round(v.getZ()));
	}

}
