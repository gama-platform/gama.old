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

import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.GamaPoint;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

/**
 * Written by drogoul Modified on 10 dŽc. 2010
 * 
 * @todo Description
 * 
 */
public class Comparison {

	public final static String GT = ">";
	public final static String LT = "<";
	public final static String GTE = ">=";
	public final static String LTE = "<=";

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greater(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greater(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greater(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greater(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean less(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean less(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean less(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean less(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greaterOrEqual(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a >= b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR)
	public static Boolean greaterOrEqual(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a >= b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greaterOrEqual(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a >= b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greaterOrEqual(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return !(a < b);
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean opLessThanOrEqual(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a <= b;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean lessOrEqual(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a <= b;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean lessOrEqual(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a <= b;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean lessOrEqual(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return !(a > b);
	}

	@operator(value = { "=" }, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean equal(final Double a, final Double b) {
		return a == null ? b == null : IntervalSize.isZeroWidth(a, b);
		// return !(a < b) && !(a > b);
	}

	@operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean different(final Double a, final Double b) {
		if ( a == null ) { return b != null; }
		if ( b == null ) { return false; }
		return !IntervalSize.isZeroWidth(a, b);
		// return a < b || a > b;
	}

	@operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean different(final Integer a, final Double b) {
		if ( a == null ) { return b != null; }
		if ( b == null ) { return false; }
		return true;
	}

	@operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean different(final Double a, final Integer b) {
		if ( a == null ) { return b != null; }
		if ( b == null ) { return false; }
		return true;
	}

	@operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean different(final Integer a, final Integer b) {
		if ( a == null ) { return b != null; }
		if ( b == null ) { return false; }
		return a.intValue() != b.intValue();
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean lessOrEqual(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i <= 0;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greaterOrEqual(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i >= 0;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean less(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i < 0;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greater(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i > 0;
	}

	@operator(value = { "=" }, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean equal(final Object a, final Object b) {
		return a == null ? b == null : a.equals(b);
	}

	@operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean different(final Object a, final Object b) {
		return a == null ? b != null : !a.equals(b);
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean less(final GamaPoint p1, final GamaPoint p) {
		return p1.x < p.x && p1.y < p.y;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greater(final GamaPoint p1, final GamaPoint p) {
		return p1.x > p.x && p1.y > p.y;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean lessOrEqual(final GamaPoint p1, final GamaPoint p) {
		return p1.x <= p.x && p1.y <= p.y;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	public static Boolean greaterOrEqual(final GamaPoint p1, final GamaPoint p) {
		return p1.x >= p.x && p1.y >= p.y;
	}

}
