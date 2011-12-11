/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.interfaces.IScope;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.MathUtils;

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

	public final static String DIVIDE = "/";
	public final static String PLUS = "+";
	public final static String MINUS = "-";
	public final static String TIMES = "*";

	/**
	 * The Class Units.
	 */

	@operator(value = { "^", "**" }, can_be_const = true)
	public static Integer pow(final Integer a, final Integer b) {
		return pow(a.doubleValue(), b.doubleValue()).intValue();
	}

	@operator(value = { "^", "**" }, can_be_const = true)
	public static Double pow(final Double a, final Integer b) {
		return pow(a, b.doubleValue());
	}

	@operator(value = { "^", "**" }, can_be_const = true)
	public static Double pow(final Integer a, final Double b) {
		return pow(a.doubleValue(), b);
	}

	@operator(value = { "^", "**" }, can_be_const = true)
	public static Double pow(final Double a, final Double b) {
		return MathUtils.pow(a.doubleValue(), b.doubleValue());
	}

	// ==== Operators

	@operator(value = "abs", can_be_const = true)
	public static Double abs(final Double rv) {
		return rv < 0 ? -rv : rv;
	}

	@operator(value = "abs", can_be_const = true)
	public static Integer abs(final Integer rv) {
		return MathUtils.abs(rv);
	}

	@operator(value = "acos", can_be_const = true)
	public static Double acos(final Double rv) {
		return Math.acos(rv) * MathUtils.toDeg;
	}

	@operator(value = "acos", can_be_const = true)
	public static Double acos(final Integer rv) {
		return Math.acos(rv) * MathUtils.toDeg;
	}

	@operator(value = "asin", can_be_const = true)
	public static Double asin(final Double rv) {
		return Math.asin(rv) * MathUtils.toDeg;
	}

	@operator(value = "asin", can_be_const = true)
	public static Double asin(final Integer rv) {
		return Math.asin(rv) * MathUtils.toDeg;
	}

	@operator(value = "atan", can_be_const = true)
	public static Double atan(final Double rv) {
		return Math.atan(rv) * MathUtils.toDeg;
	}

	@operator(value = "atan", can_be_const = true)
	public static Double atan(final Integer rv) {
		return Math.atan(rv) * MathUtils.toDeg;
	}

	@operator(value = "cos", can_be_const = true)
	public static Double cos(final Double rv) {
		return MathUtils.cos((int) rv.doubleValue());
	}

	@operator(value = "cos", can_be_const = true)
	public static Double cos(final Integer rv) {
		return MathUtils.cos(rv.intValue());
	}

	@operator(value = "sin", can_be_const = true)
	public static Double sin(final Double rv) {
		return MathUtils.sin((int) rv.doubleValue());
	}

	@operator(value = "sin", can_be_const = true)
	public static Double sin(final Integer rv) {
		return MathUtils.sin(rv.intValue());
	}

	@operator(value = "even", can_be_const = true)
	public static Boolean even(final Integer rv) {
		return MathUtils.even(rv.intValue());
	}

	@operator(value = "exp", can_be_const = true)
	public static Double exp(final Double rv) {
		return Math.exp(rv);
	}

	@operator(value = "exp", can_be_const = true)
	public static Double exp(final Integer rv) {
		return Math.exp(rv.doubleValue());
	}

	@operator(value = "fact", can_be_const = true)
	public static Integer fact(final Integer n) {
		if ( n < 0 ) { return 0; }
		int product = 1;
		for ( int i = 2; i <= n; i++ ) {
			product *= i;
		}
		return product;
	}

	@operator(value = "ln", can_be_const = true)
	public static Double ln(final Double x) {
		if ( x <= 0 ) {
			GAMA.reportError(new GamaRuntimeException(
				"The ln operator cannot accept negative or null inputs", true));
			return Double.MAX_VALUE; // A compromise...
		}
		return Math.log(x);
	}

	@operator(value = "ln", can_be_const = true)
	public static Double ln(final Integer x) {
		if ( x <= 0 ) {
			GAMA.reportError(new GamaRuntimeException(
				"The ln operator cannot accept negative or null inputs", true));
			return Double.MAX_VALUE; // A compromise...
		}
		return Math.log(x);
	}

	@operator(value = "-", can_be_const = true)
	public static Double negate(final Double x) {
		return -x;
	}

	@operator(value = "-", can_be_const = true)
	public static Integer negate(final Integer x) {
		return -x;
	}

	@operator(value = "round", can_be_const = true)
	public static Integer round(final Double v) {
		return MathUtils.round(v);
	}

	@operator(value = "round", can_be_const = true)
	public static Integer round(final Integer v) {
		return v;
	}

	@operator(value = "sqrt", can_be_const = true)
	public static Double sqrt(final Integer v) throws GamaRuntimeException {
		if ( v < 0 ) { throw new GamaRuntimeException(
			"The sqrt operator cannot accept negative inputs", true); }
		return Math.sqrt(v);
	}

	@operator(value = "sqrt", can_be_const = true)
	public static Double sqrt(final Double v) throws GamaRuntimeException {
		if ( v < 0 ) { throw new GamaRuntimeException(
			"The sqrt operator cannot accept negative inputs", true); }
		return Math.sqrt(v);
	}

	@operator(value = "tan", can_be_const = true)
	public static Double tan(final Double v) {
		return MathUtils.tan(v.intValue());
	}

	@operator(value = "tan", can_be_const = true)
	public static Double tan(final Integer v) {
		return MathUtils.tan(v.intValue());
	}

	@operator(value = DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static Double opDivide(final Integer a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return Double.valueOf(a.doubleValue() / b.doubleValue());
	}

	@operator(value = DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static Double opDivide(final Double a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return a / b.doubleValue();
	}

	@operator(value = DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static Double opDivide(final Double a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return a / b;
	}

	@operator(value = DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static Double opDivide(final Integer a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return a.doubleValue() / b.doubleValue();
	}

	@operator(value = TIMES, priority = IPriority.PRODUCT, can_be_const = true)
	public static Integer opTimes(final Integer a, final Integer b) {
		return a * b;
	}

	@operator(value = TIMES, priority = IPriority.PRODUCT, can_be_const = true)
	public static Double opTimes(final Double a, final Integer b) {
		return Double.valueOf(a * b);
	}

	@operator(value = TIMES, priority = IPriority.PRODUCT, can_be_const = true)
	public static Double opTimes(final Double a, final Double b) {
		return a * b;
	}

	@operator(value = TIMES, priority = IPriority.PRODUCT, can_be_const = true)
	public static Double opTimes(final Integer a, final Double b) {
		return Double.valueOf(a * b);
	}

	@operator(value = PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Integer opPlus(final Integer a, final Integer b) {
		return a + b;
	}

	@operator(value = PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Double opPlus(final Double a, final Integer b) {
		return a + b;
	}

	@operator(value = PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Double opPlus(final Double a, final Double b) {
		return a + b;
	}

	@operator(value = PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Double opPlus(final Integer a, final Double b) {
		return a + b;
	}

	@operator(value = MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Integer opMinus(final Integer a, final Integer b) {
		return a - b;
	}

	@operator(value = MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Double opMinus(final Double a, final Integer b) {
		return a - b;
	}

	@operator(value = MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Double opMinus(final Double a, final Double b) {
		return a - b;
	}

	@operator(value = MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static Double opMinus(final Integer a, final Double b) {
		return a - b;
	}

	@operator(value = "with_precision", can_be_const = true)
	public static Double opTruncate(final Double x, final Integer precision) {
		return MathUtils.truncate(x.doubleValue(), precision.intValue());
	}

	@operator(value = "mod", priority = IPriority.PRODUCT, can_be_const = true)
	public static Integer opMod(final IScope scope, final Integer a, final Integer b) {
		return a % b;
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	public static Integer opDiv(final Integer a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return a / b;
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	public static Integer opDiv(final Double a, final Integer b) throws GamaRuntimeException {
		if ( b == 0 ) { throw new GamaRuntimeException("Division by zero"); }
		return (int) (a / b);
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	public static Integer opDiv(final Integer a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return (int) (a / b);
	}

	@operator(value = "div", priority = IPriority.PRODUCT, can_be_const = true)
	public static Integer opDiv(final Double a, final Double b) throws GamaRuntimeException {
		if ( b.equals(0.0) ) { throw new GamaRuntimeException("Division by zero"); }
		return (int) (a / b);
	}

}
