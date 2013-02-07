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

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

/**
 * Written by drogoul Modified on 10 dec. 2010
 * 
 * @todo Description
 * 
 */
public class Comparison {

	public final static String GT = ">";
	public final static String LT = "<";
	public final static String GTE = ">=";
	public final static String LTE = "<=";

	@operator(value = "between", can_be_const = true)
	public static Boolean between(final Integer a, final Integer inf, final Integer sup) {
		if ( inf > sup ) { return false; }
		return a >= sup ? false : a > inf;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(value = "true if the left-hand operand is greater than the right-hand operand, false otherwise.", special_cases = { "if one of the operands is nil, returns false" }, examples = { "3 > 7  --:  false" })
	public static Boolean greater(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3 > 2.5  --: true" })
	public static Boolean greater(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3.5 > 7  --: false" })
	public static Boolean greater(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3.5 > 7.6  --: false" })
	public static Boolean greater(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a > b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(value = "true if the left-hand operand is less than the right-hand operand, false otherwise.", special_cases = { "if one of the operands is nil, returns false" }, examples = { "3 < 7  --:  true" })
	public static Boolean less(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3 < 2.5  --: false" })
	public static Boolean less(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3.5 < 7  --: true" })
	public static Boolean less(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3.5 < 7.6  --: true" })
	public static Boolean less(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a < b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(value = "true if the left-hand operand is greater or equal than the right-hand operand, false otherwise.", special_cases = { "if one of the operands is nil, returns false" }, examples = { "3 >= 7  --:  false" })
	public static Boolean greaterOrEqual(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a >= b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR)
	@doc(examples = { "3 >= 2.5  --: true" })
	public static Boolean greaterOrEqual(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a >= b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3.5 >= 7  --: false" })
	public static Boolean greaterOrEqual(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a >= b;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3.5 >= 3.5  --: true" })
	public static Boolean greaterOrEqual(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return !(a < b);
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(value = "true if the left-hand operand is less or equal than the right-hand operand, false otherwise.", special_cases = { "if one of the operands is nil, returns false" }, examples = { "3 <= 7  --:  true" })
	public static Boolean opLessThanOrEqual(final Integer a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a <= b;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3 <= 2.5  --: false" })
	public static Boolean lessOrEqual(final Integer a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return a <= b;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "7.0 <= 7  --: true" })
	public static Boolean lessOrEqual(final Double a, final Integer b) {
		if ( a == null || b == null ) { return false; }
		return a <= b;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(examples = { "3.5 <= 3.5  --: true" })
	public static Boolean lessOrEqual(final Double a, final Double b) {
		if ( a == null || b == null ) { return false; }
		return !(a > b);
	}

	@operator(value = { "=" }, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(value = "true if both operands are equal, false otherwise", examples = {
		"3 = 3    	--: true", "4.5 = 4.7  	--:  false" }, see = { "!=" })
	public static Boolean equal(final Double a, final Double b) {
		return a == null ? b == null : IntervalSize.isZeroWidth(a, b);
		// return !(a < b) && !(a > b);
	}

	@operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(value = "true if both operands are different, false otherwise", examples = { "4.5 = 4.7  	--:  false" }, see = { "=" })
	public static Boolean different(final Double a, final Double b) {
		if ( a == null ) { return b != null; }
		if ( b == null ) { return false; }
		return !IntervalSize.isZeroWidth(a, b);
		// return a < b || a > b;
	}

	// @operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	// public static Boolean different(final Integer a, final Double b) {
	// if ( a == null ) { return b != null; }
	// if ( b == null ) { return false; }
	// return true;
	// }
	//
	// @operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	// public static Boolean different(final Double a, final Integer b) {
	// if ( a == null ) { return b != null; }
	// if ( b == null ) { return false; }
	// return true;
	// }
	//
	// @operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	// @doc(examples = {"3 != 3    	--: false"})
	// public static Boolean different(final Integer a, final Integer b) {
	// if ( a == null ) { return b != null; }
	// if ( b == null ) { return false; }
	// return a.intValue() != b.intValue();
	// }

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if the operands are strings, a lexicographic comparison is performed" }, examples = { "abc <= aeb  --: true" })
	public static Boolean lessOrEqual(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i <= 0;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if the operands are strings, a lexicographic comparison is performed" }, examples = {
		"abc >= aeb  --: false", "abc >= abc  --: true" })
	public static Boolean greaterOrEqual(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i >= 0;
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if the operands are strings, a lexicographic comparison is performed" }, examples = { "abc < aeb  --: true" })
	public static Boolean less(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i < 0;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if the operands are strings, a lexicographic comparison is performed" }, examples = { "abc > aeb  --: false" })
	public static Boolean greater(final String a, final String b) {
		if ( a == null ) { return false; }
		int i = a.compareTo(b);
		return i > 0;
	}

	@operator(value = { "=" }, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(comment = "this operator will return true if the two operands are identical (i.e., the same object) or equal. Comparisons between nil values are permitted.", examples = {
		"3.0 = 3  	--:  true", "[2,3] = [2,3] --: true" })
	public static Boolean equal(final Object a, final Object b) {
		return a == null ? b == null : a.equals(b);
	}

	@operator(value = { "!=", "<>" }, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(comment = " this operator will return false if the two operands are identical (i.e., the same object) or equal. Comparisons between nil values are permitted.", examples = {
		"[2,3] != [2,3] --: false", "[2,4] != [2,3] --: true" })
	public static Boolean different(final Object a, final Object b) {
		return a == null ? b != null : !a.equals(b);
	}

	@operator(value = LT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if both operands are points, returns true if only if left component (x) of the left operand if less than or equal to x of the right one and if the right component (y) of the left operand is greater than or equal to y of the right one." }, examples = {
		"{5,7} < {4,6}  --: false", "{5,7} < {4,8}  --: false" })
	public static Boolean less(final GamaPoint p1, final GamaPoint p) {
		return p1.x < p.x && p1.y < p.y;
	}

	@operator(value = GT, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if both operands are points, returns true if only if left component (x) of the left operand if greater than x of the right one and if the right component (y) of the left operand is greater than y of the right one." }, examples = {
		"{5,7} > {4,6}  --: true", "{5,7} > {4,8}  --: false" })
	public static Boolean greater(final GamaPoint p1, final GamaPoint p) {
		return p1.x > p.x && p1.y > p.y;
	}

	@operator(value = LTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if both operands are points, returns true if only if left component (x) of the left operand if less than or equal to x of the right one and if the right component (y) of the left operand is greater than or equal to y of the right one." }, examples = {
		"{5,7} <= {4,6}  --: false", "{5,7} <= {4,8}  --: false" })
	public static Boolean lessOrEqual(final GamaPoint p1, final GamaPoint p) {
		return p1.x <= p.x && p1.y <= p.y;
	}

	@operator(value = GTE, priority = IPriority.COMPARATOR, can_be_const = true)
	@doc(special_cases = { "if both operands are points, returns true if only if left component (x) of the left operand if greater than or equal to x of the right one and if the right component (y) of the left operand is greater than or equal to y of the right one." }, examples = {
		"{5,7} >= {4,6}  --: true", "{5,7} >= {4,8}  --: false" })
	public static Boolean greaterOrEqual(final GamaPoint p1, final GamaPoint p) {
		return p1.x >= p.x && p1.y >= p.y;
	}

}
