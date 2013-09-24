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
package msi.gaml.operators;

import java.util.Random;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.apache.commons.math3.util.FastMath;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * The Class GamaMath.
 */
public class Maths {

	public static void main(final String[] args) {
		java.lang.System.out.println("Various arithmetic tests");
		// java.lang.System.out.println("3.0 = 3" + (3d == 3));
		// java.lang.System.out.println("3.0 != 3" + (3d != 3));
		java.lang.System.out.println("floor and ceil 2.7 " + floor(2.7) + " and " + ceil(2.7));
		java.lang.System.out.println("floor and ceil -2.7 " + floor(-2.7) + " and " + ceil(-2.7));
		java.lang.System.out.println("floor and ceil -2 " + floor(-2) + " and " + ceil(-2));
		java.lang.System.out.println("floor and ceil 3 " + floor(3) + " and " + ceil(3));
		double atan2diff = 0;
		double atan2diff2 = 0;
		Random rand = new Random();
		long s1 = 0;
		long t1 = 0;
		long t2 = 0;
		long t3 = 0;
		for ( int i = 0; i < 10000000; i++ ) {
			double x = rand.nextDouble();
			double y = rand.nextDouble();
			s1 = java.lang.System.currentTimeMillis();
			double a1 = Math.atan2(x, y);
			t1 += java.lang.System.currentTimeMillis() - s1;
			s1 = java.lang.System.currentTimeMillis();
			double a2 = FastMath.atan2(x, y);
			t2 += java.lang.System.currentTimeMillis() - s1;
			s1 = java.lang.System.currentTimeMillis();
			double a3 = Maths.atan2Opt2(x, y);
			t3 += java.lang.System.currentTimeMillis() - s1;

			atan2diff += Math.abs(a1 - a2);
			atan2diff2 += Math.abs(a1 - a3);
		}
		java.lang.System.out.println("atan2diff : " + atan2diff + "  atan2diff2 : " + atan2diff2 + " t1 : " + t1 +
			" t2 : " + t2 + " t3 : " + t3);

		long t4 = 0;
		long t5 = 0;
		long t6 = 0;
		double distDiff1 = 0;
		double distDiff2 = 0;
		for ( int i = 0; i < 1000000; i++ ) {
			double x1 = rand.nextDouble();
			double y1 = rand.nextDouble();
			double x2 = rand.nextDouble();
			double y2 = rand.nextDouble();
			Coordinate c1 = new Coordinate(x1, y1);
			Coordinate c2 = new Coordinate(x2, y2);

			s1 = java.lang.System.currentTimeMillis();
			double a1 = Math.hypot(x2 - x1, y2 - y1);
			t4 += java.lang.System.currentTimeMillis() - s1;
			s1 = java.lang.System.currentTimeMillis();
			double a2 = FastMath.hypot(x2 - x1, y2 - y1);
			t5 += java.lang.System.currentTimeMillis() - s1;
			s1 = java.lang.System.currentTimeMillis();
			double a3 = c1.distance(c2);
			t6 += java.lang.System.currentTimeMillis() - s1;
			distDiff1 += Math.abs(a1 - a2);
			distDiff2 += Math.abs(a1 - a3);
		}
		java.lang.System.out.println("distDiff1 : " + distDiff1 + "  distDiff2 : " + distDiff2 + " t4 : " + t4 +
			" t5 : " + t5 + " t6 : " + t6);

		long t7 = 0;
		long t8 = 0;
		distDiff1 = 0;

		for ( int i = 0; i < 1000000; i++ ) {
			double a1, a2;
			double x1 = rand.nextDouble();
			double x2 = rand.nextDouble();
			double y1 = rand.nextDouble();
			double y2 = rand.nextDouble();
			double z1 = 0.0;
			double z2 = 0.0;
			GamaPoint c2 = new GamaPoint(x2, y2, z2);

			s1 = java.lang.System.currentTimeMillis();
			if ( z1 == 0d && c2.getZ() == 0d ) {
				a1 = hypot(x1, x2, y1, y2);
			} else {
				a1 = hypot(x1, x2, y1, y2, z1, z2);
			}
			t7 += java.lang.System.currentTimeMillis() - s1;
			s1 = java.lang.System.currentTimeMillis();
			a2 = hypot(x1, x2, y1, y2, z1, c2.getZ());
			t8 += java.lang.System.currentTimeMillis() - s1;
			distDiff1 += Math.abs(a1 - a2);
		}
		java.lang.System.out.println("with 0.0 check : " + t7 + "  with direct 3 parameters call : " + t8 +
			" distance : " + distDiff1);
		// java.lang.System.out.println("Infinity to int:" + (int) Double.POSITIVE_INFINITY);
		// java.lang.System.out.println("NaN to int:" + (int) Double.NaN);
		// GuiUtils.debug("(int) (1.0/0.0):" + (int) (1.0 / 0.0));
		// GuiUtils.debug("(int) (1.0/0):" + (int) (1.0 / 0));
		// GuiUtils.debug("(int) (1.0/0d):" + (int) (1 / 0d));
		// GuiUtils.debug("(int) (1/0):" + 1 / 0);
	}

	/**
	 * The Class Units.
	 */

	@operator(value = { "^", "**" }, can_be_const = true)
	@doc(value = "Returns the value of the left operand raised to the power of the right operand.", special_cases = {
		"if the right-hand operand is equal to 0, returns 1", "if it is equal to 1, returns the left-hand operand." }, examples = "", see = {
		"*", "sqrt" })
	public static Integer pow(final Integer a, final Integer b) {
		return pow(a.doubleValue(), b.doubleValue()).intValue();
	}

	@operator(value = { "^", "**" }, can_be_const = true)
	@doc(value = "Returns the value of the left operand raised to the power of the right operand.", examples = { " 2 ^ 3 --: 8" })
	public static Double pow(final Double a, final Integer b) {
		return pow(a, b.doubleValue());
	}

	@operator(value = { "^", "**" }, can_be_const = true)
	@doc(value = "Returns the value of the left operand raised to the power of the right operand.", examples = { " 2 ^ 3 --: 8" })
	public static Double pow(final Integer a, final Double b) {
		return pow(a.doubleValue(), b);
	}

	@operator(value = { "^", "**" }, can_be_const = true)
	@doc(value = "Returns the value of the left operand raised to the power of the right operand.", examples = { " 2 ^ 3 --: 8" })
	public static Double pow(final Double a, final Double b) {
		return Math.pow(a, b);
	}

	// ==== Operators

	@operator(value = "abs", can_be_const = true)
	@doc(value = "the absolute value of the operand (so a positive int or float depending on the type of the operand).", examples = "abs (200 * -1 + 0.5) --: 200.5")
	public static Double abs(final Double rv) {
		return rv < 0 ? -rv : rv;
	}

	@operator(value = "abs", can_be_const = true)
	@doc(value = "Returns the absolute value of the operand.", examples = { " abs (-10) --: 10 ", "abs (10) --: 10" })
	public static Integer abs(final Integer rv) {
		return (rv ^ rv >> 31) - (rv >> 31);
	}

	@operator(value = "acos", can_be_const = true)
	@doc(value = "the arccos of the operand (which has to be expressed in decimal degrees).", examples = "acos (0) 	--: 	1", see = {
		"asin", "atan" })
	public static Double acos(final Double rv) {
		return Math.acos(rv) * toDeg;
	}

	@operator(value = "acos", can_be_const = true)
	@doc(value = "the arccos of the operand ")
	public static Double acos(final Integer rv) {
		return Math.acos(rv) * toDeg;
	}

	@operator(value = "asin", can_be_const = true)
	@doc(value = "the arcsin of the operand (which has to be expressed in decimal degrees).", examples = "asin (90) --: 1", see = {
		"acos", "atan" })
	public static Double asin(final Double rv) {
		return Math.asin(rv) * toDeg;
	}

	@operator(value = "asin", can_be_const = true)
	@doc(value = "the arcsin of the operand", examples = "asin (90) --: 1", see = { "acos", "atan" })
	public static Double asin(final Integer rv) {
		return Math.asin(rv) * toDeg;
	}

	@operator(value = "atan", can_be_const = true)
	@doc(value = "the arctan of the operand (which has to be expressed in decimal degrees).", examples = "atan (45) --: 1", see = {
		"acos", "asin" })
	public static Double atan(final Double rv) {
		return Math.atan(rv) * toDeg;
	}

	@operator(value = "atan", can_be_const = true)
	@doc(value = "the arctan of the operand", examples = "atan (45) --: 1", see = { "acos", "asin" })
	public static Double atan(final Integer rv) {
		return Math.atan(rv) * toDeg;
	}

	@operator(value = "tanh", can_be_const = true)
	@doc(value = "the hyperbolic tangent of the operand (which has to be expressed in decimal degrees).", examples = {
		"tanh(0)  	--: 0.0", "tanh(1)  	--: 0.7615941559557649", "tanh(10) 	--: 0.9999999958776927" })
	public static Double tanh(final Double rv) {
		return Math.tanh(rv);
	}

	@operator(value = "the hyperbolic tangent of the operand ", can_be_const = true)
	@doc(value = "the hyperbolic tangent of the operand (which has to be expressed in decimal degrees).", examples = {
		"tanh(0)  	--: 0.0", "tanh(1)  	--: 0.7615941559557649", "tanh(10) 	--: 0.9999999958776927" })
	public static Double tanh(final Integer rv) {
		return Math.tanh(rv);
	}

	@operator(value = "cos", can_be_const = true)
	@doc(value = "the cosinus of the operand (in decimal degrees).", special_cases = "the argument is casted to an int before being evaluated. Integers outside the range [0-359] are normalized.", examples = "cos (0) --: 1", see = {
		"sin", "tan" })
	public static Double cos(final Double rv) {
		double rad = toRad * rv;
		return Math.cos(rad);
	}

	@operator(value = "cos", can_be_const = true)
	@doc(value = "the cosinus of the operand.", special_cases = "Integers outside the range [0-359] are normalized.", examples = "cos (0) --: 1", see = {
		"sin", "tan" })
	public static Double cos(final Integer rv) {
		double rad = toRad * rv;
		return Math.cos(rad);
	}

	@operator(value = "sin", can_be_const = true)
	@doc(value = "the sinus of the operand (in decimal degrees).", special_cases = "the argument is casted to an int before being evaluated. Integers outside the range [0-359] are normalized.", examples = "cos (0) --: 0", see = {
		"cos", "tan" })
	public static Double sin(final Double rv) {
		double rad = toRad * rv;
		return Math.sin(rad);
	}

	@operator(value = "sin", can_be_const = true)
	@doc(value = "the sinus of the operand (in decimal degrees).", special_cases = "Integers outside the range [0-359] are normalized.", examples = "cos (0) --: 0", see = {
		"cos", "tan" })
	public static Double sin(final Integer rv) {
		double rad = toRad * rv;
		return Math.sin(rad);
	}

	@operator(value = "even", can_be_const = true)
	@doc(value = "true if the operand is even and false if it is odd.", special_cases = "if the operand is equal to 0, it returns true.", examples = {
		"even (3) 	--:   false", "even (-12)   --:  true" })
	public static Boolean even(final Integer rv) {
		return rv.intValue() % 2 == 0;
	}

	@operator(value = "exp", can_be_const = true)
	@doc(value = "returns Euler's number e raised to the power of the operand.", special_cases = "the operand is casted to a float before being evaluated.", examples = "exp (0) 	--:	 1", see = "ln")
	public static Double exp(final Double rv) {
		return Math.exp(rv);
	}

	@operator(value = "exp", can_be_const = true)
	@doc(value = "returns Euler's number e raised to the power of the operand.", special_cases = "the operand is casted to a float before being evaluated.", examples = "exp (0) 	--:	 1", see = "ln")
	public static Double exp(final Integer rv) {
		return Math.exp(rv.doubleValue());
	}

	@operator(value = "fact", can_be_const = true)
	@doc(value = "the factorial of the operand.", special_cases = "if the operand is less than 0, fact returns 0.", examples = "fact (4) 	--:	 24")
	public static Integer fact(final Integer n) {
		if ( n < 0 ) { return 0; }
		int product = 1;
		for ( int i = 2; i <= n; i++ ) {
			product *= i;
		}
		return product;
	}

	@operator(value = "ln", can_be_const = true)
	@doc(value = "returns the natural logarithm (base e) of the operand.", special_cases = "an exception is raised if the operand is less than zero.", examples = "ln(1) 	--:	 0.0", see = "exp")
	public static Double ln(final Double x) {
		if ( x <= 0 ) { throw GamaRuntimeException.warning("The ln operator cannot accept negative or null inputs");
		// return Double.MAX_VALUE; // A compromise...
		}
		return Math.log(x);
	}

	@operator(value = "ln", can_be_const = true)
	@doc(value = "returns the natural logarithm (base e) of the operand.", special_cases = "an exception is raised if the operand is less than zero.", examples = "ln(1) 	--:	 0.0", see = "exp")
	public static Double ln(final Integer x) {
		if ( x <= 0 ) { throw GamaRuntimeException.warning("The ln operator cannot accept negative or null inputs");
		// return Double.MAX_VALUE; // A compromise...
		}
		return Math.log(x);
	}

	@operator(value = "log", can_be_const = true)
	@doc(value = "returns the logarithm (base 10) of the operand.", special_cases = "an exception is raised if the operand is less than zero.", examples = "log(10) 	--:	 1.0", see = "ln")
	public static Double log(final Double x) {
		if ( x <= 0 ) { throw GamaRuntimeException.warning("The ln operator cannot accept negative or null inputs");
		// return Double.MAX_VALUE; // A compromise...
		}
		return Math.log10(x);
	}

	@operator(value = "log", can_be_const = true)
	@doc(value = "returns the logarithm (base 10) of the operand.", special_cases = "an exception is raised if the operand is less than zero.", examples = "log(10) 	--:	 1.0", see = "ln")
	public static Double log(final Integer x) {
		if ( x <= 0 ) { throw GamaRuntimeException.warning("The ln operator cannot accept negative or null inputs");
		// return Double.MAX_VALUE; // A compromise...
		}
		return Math.log10(x);
	}

	@operator(value = "-", can_be_const = true)
	@doc(value = "Returns the opposite or the operand.", examples = "- (-56) 	--:	 56")
	public static Double negate(final Double x) {
		return -x;
	}

	@operator(value = "-", can_be_const = true)
	@doc(value = "Returns the opposite or the operand.", examples = "- (-56) 	--:	 56")
	public static Integer negate(final Integer x) {
		return -x;
	}

	@operator(value = "round", can_be_const = true)
	@doc(value = "Returns the rounded value of the operand.", examples = { "round (0.51) 	--:	 1",
		"round (100.2) 	--: 	 100" }, see = { "int", "with_precision" })
	public static Integer round(final Double v) {
		int i;
		if ( v >= 0 ) {
			i = (int) (v + .5);
		} else {
			i = (int) (v - .5);
		}
		return i;
	}

	@operator(value = "round", can_be_const = true)
	@doc(special_cases = "if the operand is an int, round returns it")
	public static Integer round(final Integer v) {
		return v;
	}

	@operator(value = "sqrt", can_be_const = true)
	@doc(value = "Returns the square root of the operand.", special_cases = "if the operand is negative, an exception is raised", examples = "sqrt(4) 	--:	 2.0")
	public static Double sqrt(final Integer v) throws GamaRuntimeException {
		if ( v < 0 ) { throw GamaRuntimeException.warning("The sqrt operator cannot accept negative inputs"); }
		return Math.sqrt(v);
	}

	@operator(value = "sqrt", can_be_const = true)
	@doc(value = "Returns the square root of the operand.", special_cases = "if the operand is negative, an exception is raised", examples = "sqrt(4) 	--:	 2.0")
	public static Double sqrt(final Double v) throws GamaRuntimeException {
		if ( v < 0 ) { throw GamaRuntimeException.warning("The sqrt operator cannot accept negative inputs"); }
		return Math.sqrt(v);
	}

	@operator(value = "tan", can_be_const = true)
	@doc(value = "the trigonometic tangent of the operand (in decimal degrees).", special_cases = "the argument is casted to an int before being evaluated. Integers outside the range [0-359] are normalized.", examples = "cos (180) --: 0", see = {
		"cos", "sin" })
	public static Double tan(final Double v) {
		double rad = toRad * v;
		return Math.tan(rad);
	}

	@operator(value = "tan", can_be_const = true)
	@doc(value = "the trigonometic tangent of the operand.", special_cases = "the argument is casted to an int before being evaluated. Integers outside the range [0-359] are normalized.", examples = "cos (180) --: 0", see = {
		"cos", "sin" })
	public static Double tan(final Integer v) {
		double rad = toRad * v;
		return Math.tan(rad);
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(value = "Returns a float, equal to the division of the left-hand operand by the rigth-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises a \"Division by zero\" exception", examples = "", see = "*")
	public static Double opDivide(final Integer a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw GamaRuntimeException.error("Division by zero"); }
		return Double.valueOf(a.doubleValue() / b.doubleValue());
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(value = "Returns a float, equal to the division of the left-hand operand by the rigth-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises a \"Division by zero\" exception", examples = "", see = "*")
	public static Double opDivide(final Double a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw GamaRuntimeException.error("Division by zero"); }
		return a / b.doubleValue();
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(value = "Returns a float, equal to the division of the left-hand operand by the rigth-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises a \"Division by zero\" exception", examples = "", see = "*")
	public static Double opDivide(final Double a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw GamaRuntimeException.error("Division by zero"); }
		return a / b;
	}

	@operator(value = IKeyword.DIVIDE, can_be_const = true)
	@doc(value = "Returns a float, equal to the division of the left-hand operand by the rigth-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises a \"Division by zero\" exception", examples = "", see = "*")
	public static Double opDivide(final Integer a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw GamaRuntimeException.error("Division by zero"); }
		return a.doubleValue() / b.doubleValue();
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(value = "Returns the product of the two operands", special_cases = "if both operands are int, returns the product as an int", examples = "", see = "/")
	public static Integer opTimes(final Integer a, final Integer b) {
		return a * b;
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(value = "Returns the product of the two operands", examples = "", see = "/")
	public static Double opTimes(final Double a, final Integer b) {
		return Double.valueOf(a * b);
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(value = "Returns the product of the two operands", examples = "", see = "/")
	public static Double opTimes(final Double a, final Double b) {
		return a * b;
	}

	@operator(value = IKeyword.MULTIPLY, can_be_const = true)
	@doc(value = "Returns the product of the two operands", examples = "", see = "/")
	public static Double opTimes(final Integer a, final Double b) {
		return Double.valueOf(a * b);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(value = "the sum, union or concatenation of the two operands.", special_cases = "if both operands are numbers (float or int), performs a normal arithmetic sum and returns a float if one of them is a float.", examples = "1 + 1 	--:	 2", see = "-")
	public static Integer opPlus(final Integer a, final Integer b) {
		return a + b;
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(value = "the sum, union or concatenation of the two operands.", examples = "1.0 + 1 --: 2.0")
	public static Double opPlus(final Double a, final Integer b) {
		return a + b;
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(value = "the sum, union or concatenation of the two operands.", examples = "1.0 + 2.5 --: 3.5")
	public static Double opPlus(final Double a, final Double b) {
		return a + b;
	}

	@operator(value = IKeyword.PLUS, can_be_const = true)
	@doc(value = "the sum, union or concatenation of the two operands.", examples = "2 + 2.5 --: 4.5")
	public static Double opPlus(final Integer a, final Double b) {
		return a + b;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(value = "the difference of the two operands", special_cases = "if both operands are numbers, performs a normal arithmetic difference and returns a float if one of them is a float.", examples = "1 - 1 	--:	 0")
	public static Integer opMinus(final Integer a, final Integer b) {
		return a - b;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(value = "the difference of the two operands", examples = "1.0 - 1 --: 0.0")
	public static Double opMinus(final Double a, final Integer b) {
		return a - b;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(value = "the difference of the two operands", examples = "3.7 - 1.2 --: 2.5")
	public static Double opMinus(final Double a, final Double b) {
		return a - b;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true)
	@doc(value = "the difference of the two operands", examples = "3 - 1.2 --: 1.8")
	public static Double opMinus(final Integer a, final Double b) {
		return a - b;
	}

	// @operator(value = "with_precision", can_be_const = true)
	// @doc(value =
	// "round off the value of left-hand operand to the precision given by the value of right-hand operand",
	// examples = {
	// "12345.78943 with_precision 2 	--:	 12345.79", "123 with_precision 2 	--:	 123.00" }, see =
	// "round")
	public static Double opTruncate(final Double x, final Integer precision) {
		double x1 = x.doubleValue();
		int precision1 = precision.intValue();
		double fract;
		double whole;
		double mult;
		if ( x1 > 0 ) {
			whole = floor(x1);
			mult = pow(10.0, precision1);
			fract = floor((x1 - whole) * mult) / mult;
		} else {
			whole = ceil(x1);
			mult = pow(10, precision1);
			fract = ceil((x1 - whole) * mult) / mult;
		}
		return whole + fract;
	}

	@operator(value = "with_precision", can_be_const = true)
	@doc(value = "round off the value of left-hand operand to the precision given by the value of right-hand operand", examples = {
		"12345.78943 with_precision 2 	--:	 12345.79", "123 with_precision 2 	--:	 123.00" }, see = "round")
	public static double round(final Double v, final Integer precision) {
		long t = TENS[precision]; // contains powers of ten.
		return (double) (long) (v > 0 ? v * t + 0.5 : v * t - 0.5) / t;
	}

	@operator(value = "floor", can_be_const = true)
	@doc(value = "maps the operand to the largest previous following integer.", comment = "More precisely, floor(x) is the largest integer not greater than x.", examples = {
		"floor(3) 		--:  3.0", "floor(3.5) 	--:  3.0", "floor(-4.7) 	--:  -5.0" }, see = { "ceil", "round" })
	public static final double floor(final double d) {
		return Math.floor(d);
	}

	@operator(value = "ceil", can_be_const = true)
	@doc(value = "maps the operand to the smallest following integer.", comment = "More precisely, ceiling(x) is the smallest integer not less than x.", examples = {
		"ceil(3) 		--:  4.0", "ceil(3.5) 		--:  4.0", "ceil(-4.7) 	--:  -4.0" }, see = { "floor", "round" })
	public static final double ceil(final double d) {
		return Math.ceil(d);
	}

	@operator(value = "mod", can_be_const = true)
	@doc(value = "an int, equal to the remainder of the integer division of the left-hand operand by the rigth-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises an exception.", examples = {
		"40 mod 3 		--:  1", "40 mod 4		--:  0" }, see = "div")
	public static Integer opMod(final IScope scope, final Integer a, final Integer b) {
		return a % b;
	}

	@operator(value = "div", can_be_const = true)
	@doc(value = "an int, equal to the truncation of the division of the left-hand operand by the right-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises an exception.", examples = "40 div 3 	--:  13", see = "mod")
	public static Integer div(final Integer a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw GamaRuntimeException.error("Division by zero"); }
		return a / b;
	}

	@operator(value = "div", can_be_const = true)
	@doc(value = "an int, equal to the truncation of the division of the left-hand operand by the right-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises an exception.", examples = "40.5 div 3 	--:  13", see = "mod")
	public static Integer div(final Double a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw GamaRuntimeException.error("Division by zero"); }
		return (int) (a / b);
	}

	@operator(value = "div", can_be_const = true)
	@doc(value = "an int, equal to the truncation of the division of the left-hand operand by the right-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises an exception.", examples = "40 div 4.1		--:  9")
	public static Integer div(final Integer a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw GamaRuntimeException.error("Division by zero"); }
		return (int) (a / b);
	}

	@operator(value = "div", can_be_const = true)
	@doc(value = "an int, equal to the truncation of the division of the left-hand operand by the right-hand operand.", special_cases = "if the right-hand operand is equal to zero, raises an exception.", examples = "40.1 div 4.1		--:  9")
	public static Integer div(final Double a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw GamaRuntimeException.error("Division by zero"); }
		return (int) (a / b);
	}

	/** Constant field PI. */
	public static final double PI = java.lang.Math.PI;
	/** Constant field PI_4. */
	public final static double PI_4 = PI / 4d;
	/** Constant field PRECISION. */
	public final static int PRECISION = 360;
	/** Constant field PI_2. */
	public static final double PI_2 = PI * 2;
	/** Constant field PI_2_OVER1. */
	public final static double PI_2_OVER1 = 1f / PI_2;
	/** Constant field PI_2_OVER1_P. */
	public final static double PI_2_OVER1_P = PI_2_OVER1 * PRECISION;
	/** Constant field PI_34. */
	public final static double PI_34 = PI_4 * 3d;
	/** Constant field PREC_MIN_1. */
	public final static int PREC_MIN_1 = PRECISION - 1;

	public static final double SQRT2 = Math.sqrt(2);
	/** Constant field toDeg. */
	public static final double toDeg = 180 / Math.PI;
	/** Constant field toRad. */
	public static final double toRad = Math.PI / 180;
	public static final long[] TENS = new long[100];

	static {
		for ( int i = 0; i < TENS.length; i++ ) {
			TENS[i] = (long) Math.pow(10, i);
		}
	}

	@operator(value = "atan2", can_be_const = true)
	public static double atan2(final double y, final double x) {
		return Math.atan2(y, x) * toDeg;
	}

	public static double aTan2(final double y, final double x) {
		final double abs_y = Math.abs(y);
		double angle;
		if ( x >= 0d ) {
			final double r = (x - abs_y) / (x + abs_y);
			angle = PI_4 - PI_4 * r;
		} else {
			final double r = (x + abs_y) / (abs_y - x);
			angle = PI_34 - PI_4 * r;
		}
		return y < 0d ? -angle : angle;
	}

	/**
	 * Check heading : keep it in the 0 - 360 degrees interval.
	 * 
	 * @param newHeading the new heading
	 * 
	 * @return the integer
	 */
	public static int checkHeading(final int newHeading) {
		int result = newHeading;
		while (result < 0) {
			result += PRECISION;
		}
		return result % PRECISION;
	}

	@operator(value = "hypot", can_be_const = true)
	@doc(value = "Returns sqrt(x2 +y2) without intermediate overflow or underflow.", special_cases = "If either argument is infinite, then the result is positive infinity. If either argument is NaN and neither argument is infinite, then the result is NaN.")
	public static double hypot(final double x1, final double x2, final double y1, final double y2) {
		// return Math.hypot(x2 - x1, y2 - y1); VERY SLOW !
		final double dx = x2 - x1;
		final double dy = y2 - y1;
		return sqrt(dx * dx + dy * dy);
	}

	public static double hypot(final double x1, final double x2, final double y1, final double y2, final double z1,
		final double z2) {
		final double dx = x2 - x1;
		final double dy = y2 - y1;
		final double dz = z2 - z1;
		return sqrt(dx * dx + dy * dy + dz * dz);
	}

	private static final int ATAN2_BITS = 7;

	private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
	private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	private static final int ATAN2_COUNT = ATAN2_MASK + 1;
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);

	private static final double INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

	private static final double[] atan2 = new double[ATAN2_COUNT];

	static {
		for ( int i = 0; i < ATAN2_DIM; i++ ) {
			for ( int j = 0; j < ATAN2_DIM; j++ ) {
				double x0 = (double) i / ATAN2_DIM;
				double y0 = (double) j / ATAN2_DIM;

				atan2[j * ATAN2_DIM + i] = Math.atan2(y0, x0);
			}
		}
	}

	public static final double atan2Opt(final double y, final double x) {
		return atan2Opt2(y, x) * toDeg;
	}

	public static final double atan2Opt2(double y, double x) {
		double add, mul;

		if ( x < 0.0f ) {
			if ( y < 0.0f ) {
				x = -x;
				y = -y;

				mul = 1.0f;
			} else {
				x = -x;
				mul = -1.0f;
			}

			add = -3.141592653f;
		} else {
			if ( y < 0.0f ) {
				y = -y;
				mul = -1.0f;
			} else {
				mul = 1.0f;
			}

			add = 0.0f;
		}

		double invDiv = 1.0f / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);

		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);

		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}

}
