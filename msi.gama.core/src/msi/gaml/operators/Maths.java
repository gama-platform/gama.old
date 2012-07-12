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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class GamaMath.
 */
public class Maths {

	// public static void main(final String[] args) {
	// GUI.debug("Various arithmetic tests");
	// GUI.debug("Infinity to int:" + (int) Double.POSITIVE_INFINITY);
	// GUI.debug("NaN to int:" + (int) Double.NaN);
	// GUI.debug("(int) (1.0/0.0):" + (int) (1.0 / 0.0));
	// GUI.debug("(int) (1.0/0):" + (int) (1.0 / 0));
	// GUI.debug("(int) (1.0/0d):" + (int) (1 / 0d));
	// GUI.debug("(int) (1/0):" + 1 / 0);
	// }

	/**
	 * The Class Units.
	 */

	@operator(value = { "^", "**" }, priority = IPriority.PRODUCT, can_be_const = true)
	@doc(
		value = "the left-hand operand raised to the power of the right-hand operand.",
		special_cases = {
			"if the right-hand operand is equal to 0, returns 1", 
			"if it is equal to 1, returns the left-hand operand."},
		examples = "",
		see = {"*", "sqrt"})
	public static Integer pow(final Integer a, final Integer b) {
		return pow(a.doubleValue(), b.doubleValue()).intValue();
	}

	@operator(value = { "^", "**" }, priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Double pow(final Double a, final Integer b) {

		return pow(a, b.doubleValue());
	}

	@operator(value = { "^", "**" }, priority = IPriority.PRODUCT, can_be_const = true)
	@doc(
		special_cases = "",
		examples = "")
	public static Double pow(final Integer a, final Double b) {
		return pow(a.doubleValue(), b);
	}

	@operator(value = { "^", "**" }, priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Double pow(final Double a, final Double b) {
		return Math.pow(a, b);
		// Based on the Taylor series approximation.
		// double a1 = a.doubleValue();
		// double b1 = b.doubleValue();
		// int oc = -1; // used to alternate math symbol (+,-)
		// int iter = 20; // number of iterations
		// double p, x, x2, sumX, sumY; // is exponent a whole number?
		// if ( b1 - floor(b1) == 0 ) { // return base^exponent
		// double x1 = a1;
		// int y = (int) b1;
		// switch (y) {
		// case -3:
		// return 1 / (x1 * x1 * x1);
		// case -2:
		// return 1 / (x1 * x1);
		// case -1:
		// return 1 / x1;
		// case 0:
		// return 1d;
		// case 1:
		// return x1;
		// case 2:
		// return x1 * x1;
		// case 3:
		// return x1 * x1 * x1;
		// case 4:
		// return x1 * x1 * x1 * x1;
		// default:
		// if ( y > 0 ) {
		// double z = 1;
		// do {
		// if ( (y & 1) != 0 ) {
		// z *= x1;
		// }
		// x1 *= x1;
		// y >>= 1;
		// } while (y != 0);
		// return z;
		// }
		// y = -y;
		// double z = 1;
		// do {
		// if ( (y & 1) != 0 ) {
		// z /= x1;
		// }
		// x1 *= x1;
		// y >>= 1;
		// } while (y != 0);
		// return z;
		// }
		// }
		// // true if base is greater
		// // than 1
		// boolean gt1 = Math.sqrt((a1 - 1) * (a1 - 1)) <= 1 ? false : true;
		// x = gt1 ? a1 / (a1 - 1) : // base is greater than 1
		// a1 - 1; // base is 1 or less
		// sumX = gt1 ? 1 / x : // base is greater than 1
		// x; // base is 1 or less
		// for ( int i = 2; i < iter; i++ ) { // find x^iteration
		// p = x;
		// for ( int j = 1; j < i; j++ ) {
		// p *= x;
		// }
		// double xTemp = gt1 ? 1 / (i * p) : // base is greater than 1
		// p / i; // base is 1 or less
		// sumX = gt1 ? sumX + xTemp : // base is greater than 1
		// sumX + xTemp * oc; // base is 1 or less
		// oc *= -1; // change math symbol (+,-)
		// }
		// x2 = b1 * sumX;
		// sumY = 1 + x2; // our estimate
		// for ( int i = 2; i <= iter; i++ ) { // find x2^iteration
		// p = x2;
		// for ( int j = 1; j < i; j++ ) {
		// p *= x2; // multiply iterations (ex: 3 iterations = 3*2*1)
		// }
		// int yTemp = 2;
		// for ( int j = i; j > 2; j-- ) {
		// yTemp *= j; // add to estimate (ex: 3rd iteration => (x2^3)/(3*2*1) )
		// }
		// sumY += p / yTemp;
		// }
		// return sumY; // return our estimate
	}

	// ==== Operators

	@operator(value = "abs", can_be_const = true)
	@doc(
		value = "the absolute value of the operand (so a positive int or float depending on the type of the operand).",
		examples = "abs (200 * -1 + 0.5) → 200.5")
	public static Double abs(final Double rv) {
		return rv < 0 ? -rv : rv;
	}

	@operator(value = "abs", can_be_const = true)
	@doc()	
	public static Integer abs(final Integer rv) {
		return (rv ^ rv >> 31) - (rv >> 31);
	}

	@operator(value = "acos", can_be_const = true)
	@doc(
		value = "the arccos of the operand (which has to be expressed in decimal degrees).",
		examples = "acos (90) → 0",
		see = {"asin", "atan"})
	public static Double acos(final Double rv) {
		return Math.acos(rv) * toDeg;
	}

	@operator(value = "acos", can_be_const = true)
	@doc()
	public static Double acos(final Integer rv) {
		return Math.acos(rv) * toDeg;
	}

	@operator(value = "asin", can_be_const = true)
	@doc(
		value = "the arcsin of the operand (which has to be expressed in decimal degrees).",
		examples = "acos (90) → 1",
		see = {"acos", "atan"})
	public static Double asin(final Double rv) {
		return Math.asin(rv) * toDeg;
	}

	@operator(value = "asin", can_be_const = true)
	@doc()
	public static Double asin(final Integer rv) {
		return Math.asin(rv) * toDeg;
	}

	@operator(value = "atan", can_be_const = true)
	@doc(
		value = "the arctan of the operand (which has to be expressed in decimal degrees).",
		examples = "atan (45) → 1",
		see = {"acos", "asin"})	
	public static Double atan(final Double rv) {
		return Math.atan(rv) * toDeg;
	}

	@operator(value = "atan", can_be_const = true)
	@doc()
	public static Double atan(final Integer rv) {
		return Math.atan(rv) * toDeg;
	}

	@operator(value = "tanh", can_be_const = true)
	@doc(
		value = "the hyperbolic tangent of the operand (which has to be expressed in decimal degrees).",
		examples = {
			"tanh(0)  	--: 0.0",
			"tanh(1)  	--: 0.7615941559557649",
			"tanh(10) 	--: 0.9999999958776927"})		
	public static Double tanh(final Double rv) {
		return Math.tanh(rv);
	}

	@operator(value = "tanh", can_be_const = true)
	@doc()
	public static Double tanh(final Integer rv) {
		return Math.tanh(rv);
	}

	@operator(value = "cos", can_be_const = true)
	@doc(
		value = "the cosinus of the operand (in decimal degrees).",
		special_cases = "the argument is casted to an int before being evaluated. Integers outside the range [0-359] are normalized.",
		examples = "cos (0) → 1",
		see = {"sin", "tan"})		
	public static Double cos(final Double rv) {
		return cosTable[(int) rv.doubleValue()];
	}

	@operator(value = "cos", can_be_const = true)
	@doc()
	public static Double cos(final Integer rv) {
		return cosTable[rv.intValue()];
	}

	@operator(value = "sin", can_be_const = true)
	@doc(
		value = "the sinus of the operand (in decimal degrees).",
		special_cases = "the argument is casted to an int before being evaluated. Integers outside the range [0-359] are normalized.",
		examples = "cos (0) → 0",
		see = {"cos", "tan"})		
	public static Double sin(final Double rv) {
		return sinTable[rv.intValue()];
	}

	@operator(value = "sin", can_be_const = true)
	@doc()
	public static Double sin(final Integer rv) {
		return sinTable[rv.intValue()];
	}

	@operator(value = "even", can_be_const = true)
	@doc(
		value = "true if the operand is even and false if it is odd.",
		special_cases = "if the operand is equal to 0, it returns true.",
		examples = {"even (3) 	--:   false", "even (-12)   --:  true"})	
	public static Boolean even(final Integer rv) {
		return rv.intValue() % 2 == 0;
	}

	@operator(value = "exp", can_be_const = true)
	@doc(
		value = "returns Euler's number e raised to the power of the operand.",
		special_cases = "the operand is casted to a float before being evaluated.",
		examples = "exp (0) 	--:	 1",
		see = "ln")
	public static Double exp(final Double rv) {
		return Math.exp(rv);
	}

	@operator(value = "exp", can_be_const = true)
	@doc()
	public static Double exp(final Integer rv) {
		return Math.exp(rv.doubleValue());
	}

	@operator(value = "fact", can_be_const = true)
	@doc(
		value = "the factorial of the operand.",
		special_cases = "if the operand is less than 0, fact returns 0.",
		examples = "fact (4) 	--:	 24")
	public static Integer fact(final Integer n) {
		if ( n < 0 ) { return 0; }
		int product = 1;
		for ( int i = 2; i <= n; i++ ) {
			product *= i;
		}
		return product;
	}

	@operator(value = "ln", can_be_const = true)
	@doc(
		value = "returns the natural logarithm (base e) of the operand.",
		special_cases = "an exception is raised if the operand is less than zero.",
		examples = "ln(1) 	--:	 0.0",
		see = "exp")
	public static Double ln(final Double x) {
		if ( x <= 0 ) {
			GAMA.reportError(new GamaRuntimeException(
				"The ln operator cannot accept negative or null inputs", true));
			return Double.MAX_VALUE; // A compromise...
		}
		return Math.log(x);
	}

	@operator(value = "ln", can_be_const = true)
	@doc()
	public static Double ln(final Integer x) {
		if ( x <= 0 ) {
			GAMA.reportError(new GamaRuntimeException(
				"The ln operator cannot accept negative or null inputs", true));
			return Double.MAX_VALUE; // A compromise...
		}
		return Math.log(x);
	}

	@operator(value = "-", can_be_const = true)
	@doc(
		special_cases = "when it is used as an unary operator, - returns the opposite or the operand.",
		examples = "- (-56) 	--:	 56")
	public static Double negate(final Double x) {
		return -x;
	}

	@operator(value = "-", can_be_const = true)
	@doc()
	public static Integer negate(final Integer x) {
		return -x;
	}

	@operator(value = "round", can_be_const = true)
	@doc(
		value = "the rounded value of the operand.",
		examples = {"round (0.51) 	--:	 1", "round (100.2) 	--: 	 100"},
		see = {"int","with_precision"})
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
	@doc(
		value = "returns the square root of the operand.",
		special_cases = "if the operand is negative, an exception is raised",
		examples = "sqrt(4) 	--:	 2.0")	
	public static Double sqrt(final Integer v) throws GamaRuntimeException {
		if ( v < 0 ) { throw new GamaRuntimeException(
			"The sqrt operator cannot accept negative inputs", true); }
		return Math.sqrt(v);
	}

	@operator(value = "sqrt", can_be_const = true)
	@doc()
	public static Double sqrt(final Double v) throws GamaRuntimeException {
		if ( v < 0 ) { throw new GamaRuntimeException(
			"The sqrt operator cannot accept negative inputs", true); }
		return Math.sqrt(v);
	}

	@operator(value = "tan", can_be_const = true)
	@doc(
		value = "the trigonometic tangent of the operand (in decimal degrees).",
		special_cases = "the argument is casted to an int before being evaluated. Integers outside the range [0-359] are normalized.",
		examples = "cos (180) → 0",
		see = {"cos", "sin"})		
	public static Double tan(final Double v) {
		return tanTable[v.intValue()];
	}

	@operator(value = "tan", can_be_const = true)
	@doc()
	public static Double tan(final Integer v) {
		return tanTable[v.intValue()];
	}

	@operator(value = IKeyword.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	@doc(
		value = "a float, equal to the division of the left-hand operand by the rigth-hand operand.",
		special_cases = "if the right-hand operand is equal to zero, raises a \"Division by zero\" exception",
		examples = "",
		see = "*")	
	public static Double opDivide(final Integer a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return Double.valueOf(a.doubleValue() / b.doubleValue());
	}

	@operator(value = IKeyword.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	@doc(examples = "")
	public static Double opDivide(final Double a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return a / b.doubleValue();
	}

	@operator(value = IKeyword.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Double opDivide(final Double a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return a / b;
	}

	@operator(value = IKeyword.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Double opDivide(final Integer a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return a.doubleValue() / b.doubleValue();
	}

	@operator(value = IKeyword.MULTIPLY, priority = IPriority.PRODUCT, can_be_const = true)
	@doc(
		value = "the product of the two operands",
		special_cases = "if both operands are int, returns the product as an int",
		examples = "",
		see = "/")
	public static Integer opTimes(final Integer a, final Integer b) {
		return a * b;
	}

	@operator(value = IKeyword.MULTIPLY, priority = IPriority.PRODUCT, can_be_const = true)
	@doc(examples = "")
	public static Double opTimes(final Double a, final Integer b) {
		return Double.valueOf(a * b);
	}

	@operator(value = IKeyword.MULTIPLY, priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Double opTimes(final Double a, final Double b) {
		return a * b;
	}

	@operator(value = IKeyword.MULTIPLY, priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Double opTimes(final Integer a, final Double b) {
		return Double.valueOf(a * b);
	}

	@operator(value = IKeyword.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc(
		value = "the sum, union or concatenation of the two operands.",
		special_cases = "if both operands are numbers (float or int), performs a normal arithmetic sum and returns a float if one of them is a float.",
		examples = "1 + 1 	--:	 2",
		see = "-")
	public static Integer opPlus(final Integer a, final Integer b) {
		return a + b;
	}

	@operator(value = IKeyword.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc(examples = "1.0 + 1 → 2.0")
	public static Double opPlus(final Double a, final Integer b) {
		return a + b;
	}

	@operator(value = IKeyword.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc()
	public static Double opPlus(final Double a, final Double b) {
		return a + b;
	}

	@operator(value = IKeyword.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc()
	public static Double opPlus(final Integer a, final Double b) {
		return a + b;
	}

	@operator(value = IKeyword.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc(
		value= "the difference of the two operands",
		special_cases = "if both operands are numbers, performs a normal arithmetic difference and returns a float if one of them is a float.",
		examples = "1 - 1 	--:	 0")
	public static Integer opMinus(final Integer a, final Integer b) {
		return a - b;
	}

	@operator(value = IKeyword.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc(examples = "1.0 - 1 → 0.0")
	public static Double opMinus(final Double a, final Integer b) {
		return a - b;
	}

	@operator(value = IKeyword.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc()
	public static Double opMinus(final Double a, final Double b) {
		return a - b;
	}

	@operator(value = IKeyword.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	@doc()
	public static Double opMinus(final Integer a, final Double b) {
		return a - b;
	}

	@operator(value = "with_precision", can_be_const = true)
	@doc(
		value = "round off the value of left-hand operand to the precision given by the value of right-hand operand",
		examples = {"12345.78943 with_precision 2 	--:	 12345.79", "123 with_precision 2 	--:	 123.00"},
		see = "round")
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

	@operator(value = "floor", can_be_const = true)
	@doc(
		value = "maps the operand to the largest previous following integer.",
		comment = "More precisely, floor(x) is the largest integer not greater than x.",
		examples = {
			"floor(3) 		--:  3.0",
			"floor(3.5) 	--:  3.0",
			"floor(-4.7) 	--:  -5.0"},
		see = {"ceil", "round"})
	public static final double floor(final double d) {
		int i;
		if ( d >= 0 ) {
			i = (int) d;
		} else {
			i = -((int) -d) - 1;
		}
		return i;
	}

	@operator(value = "ceil", can_be_const = true)
	@doc(
		value = "maps the operand to the smallest following integer.",
		comment = "More precisely, ceiling(x) is the smallest integer not less than x.",
		examples = {
			"ceil(3) 		--:  4.0",
			"ceil(3.5) 		--:  4.0",
			"ceil(-4.7) 	--:  -4.0"},
		see = {"floor", "round"})
	public static final double ceil(final double d) {
		int i;
		if ( d >= 0 ) {
			i = -((int) -d) + 1;
		} else {
			i = (int) d;
		}
		return i;
	}

	@operator(value = "mod", priority = IPriority.PRODUCT, can_be_const = true)
	@doc(
		value = "an int, equal to the remainder of the integer division of the left-hand operand by the rigth-hand operand.",
		special_cases = "if the right-hand operand is equal to zero, raises an exception.",
		examples = {"40 mod 3 		--:  1","40 mod 4		--:  0"},
		see = "div")	
	public static Integer opMod(final IScope scope, final Integer a, final Integer b) {
		return a % b;
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	@doc(
		value = "an int, equal to the truncation of the division of the left-hand operand by the rigth-hand operand.",
		special_cases = "if the right-hand operand is equal to zero, raises an exception.",
		examples = "40 div 3 	--:  13",
		see = "mod")
	public static Integer div(final Integer a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return a / b;
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Integer div(final Double a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return (int) (a / b);
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	@doc(examples = "40 div 4.1		--:  9")
	public static Integer div(final Integer a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return (int) (a / b);
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	@doc()
	public static Integer div(final Double a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return (int) (a / b);
	}

	/** Constant field PI. */
	public static final double PI = java.lang.Math.PI;
	/** Constant field PI_4. */
	public final static double PI_4 = PI / 4d;
	/** Constant field PRECISION. */
	public final static int PRECISION = 360;
	/** Constant field cosTable. */
	public final static double[] cosTable = new double[PRECISION];
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

	/** Constant field RAD_SLICE. */
	public final static double RAD_SLICE = PI_2 / PRECISION;
	/** Constant field sinTable. */
	public final static double[] sinTable = new double[PRECISION];
	public static final double SQRT2 = Math.sqrt(2);
	/** Constant field tanTable. */
	public final static double[] tanTable = new double[PRECISION];
	/** Constant field toDeg. */
	public static final double toDeg = 180 / Math.PI;
	/** Constant field toRad. */
	public static final double toRad = Math.PI / 180;

	static {
		double rad = 0;
		for ( int i = 0; i < PRECISION; i++ ) {
			rad = i * RAD_SLICE;
			sinTable[i] = java.lang.Math.sin(rad);
			cosTable[i] = java.lang.Math.cos(rad);
			tanTable[i] = java.lang.Math.tan(rad);
		}
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

	public static double hypot(final double x1, final double x2, final double y1, final double y2) {
		final double dx = x2 - x1;
		final double dy = y2 - y1;
		double a = dx * dx + dy * dy;
		final long x = Double.doubleToLongBits(a) >> 32;
		double y = Double.longBitsToDouble(x + 1072632448 << 31);
		// repeat the following line for more precision
		y = (y + a / y) * 0.5;
		y = (y + a / y) * 0.5;
		return y;
	}

	/**
	 * Rad to index.
	 * 
	 * @param radians the radians
	 * 
	 * @return the int
	 */
	public static final int radToIndex(final double radians) {
		return (int) (radians * PI_2_OVER1_P) & PREC_MIN_1;
	}

}
