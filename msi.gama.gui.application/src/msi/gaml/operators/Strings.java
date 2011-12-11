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

import java.util.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 10 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Strings {

	@operator(value = Maths.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static String opPlus(final String a, final String b) {
		return a + b;
	}

	@operator(value = Maths.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static String opPlus(final String a, final Object b) throws GamaRuntimeException {
		// TODO AJouter le scope ?
		return a + Cast.asString(b);
	}

	@operator(value = "in", can_be_const = true)
	public static Boolean opIn(final String pattern, final String target) {
		return target.contains(pattern);
	}

	@operator(value = "contains", can_be_const = true)
	public static Boolean opContains(final String target, final String pattern) {
		return opIn(pattern, target);
	}

	@operator(value = "contains_any", can_be_const = true)
	public static Boolean opContainsAny(final String target, final List l) {
		for ( Object o : l ) {
			if ( o instanceof String && opContains(target, (String) o) ) { return true; }
		}
		return false;
	}

	@operator(value = "contains_all", can_be_const = true)
	public static Boolean opContainsAll(final String target, final List l) {
		for ( Object o : l ) {
			if ( !(o instanceof String && opContains(target, (String) o)) ) { return false; }
		}
		return true;
	}

	@operator(value = "index_of", can_be_const = true)
	public static Integer opIndexOf(final String target, final String pattern) {
		return target.indexOf(pattern);
	}

	@operator(value = "last_index_of", can_be_const = true)
	public static Integer opLastIndexOf(final String target, final String pattern) {
		return target.lastIndexOf(pattern);
	}

	@operator(value = { "copy_between", "copy" }, can_be_const = true)
	public static String opCopy(final String target, final GamaPoint p) {
		final int beginIndex = p.x < 0 ? 0 : (int) p.x;
		final int endIndex = p.y > target.length() ? target.length() : (int) p.y;
		if ( beginIndex > endIndex ) { return ""; }
		return target.substring(beginIndex, endIndex);
	}

	@operator(value = { "split_with", "tokenize" }, can_be_const = true)
	public static GamaList opTokenize(final String target, final String pattern) {
		final StringTokenizer st = new StringTokenizer(target, pattern);
		return new GamaList(Collections.list(st));
	}

	@operator(value = "is_number", can_be_const = true)
	public static Boolean isGamaNumber(final String s) {
		// copright notice:
		// original code taken from org.apache.commons.lang.NumberUtils.isNumber(String)

		if ( s == null ) { return false; }
		final int length = s.length();
		if ( length == 0 ) { return false; }
		int sz = length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;

		// deal with any possible sign up front
		final int start = s.charAt(0) == '-' ? 1 : 0;
		if ( sz > start + 1 ) {
			if ( s.charAt(start) == '#' ) {
				int i = start + 1;
				if ( i == sz ) { return false; // str == "#"
				}
				// Checking hex (it can't be anything else)
				for ( ; i < length; i++ ) {
					final char c = s.charAt(i);
					if ( (c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F') ) { return false; }
				}

				return true;
			}
		}

		sz--; // Don't want to loop to the last char, check it afterwords for type
				// qualifiers
		int i = start;

		// Loop to the next to last char or to the last char if we need another digit to
		// make a valid number (e.g. chars[0..5] = "1234E")
		while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
			final char c = s.charAt(i);
			if ( c >= '0' && c <= '9' ) {
				foundDigit = true;
				allowSigns = false;
			} else if ( c == '.' ) {
				if ( hasDecPoint || hasExp ) {
					// Two decimal points or dec in exponent
					return false;
				}
				hasDecPoint = true;
			} else if ( c == 'e' || c == 'E' ) {
				// We've already taken care of hex.
				if ( hasExp ) {
					// Two E's
					return false;
				}
				if ( foundDigit ) {
					hasExp = true;
					allowSigns = true;
				} else {
					return false;
				}
			} else if ( c == '-' ) {
				if ( allowSigns ) {
					allowSigns = false;
					foundDigit = false; // We need a digit after the E
				} else {
					return false;
				}
			} else {
				return false;
			}

			i++;
		}

		if ( i < length ) {
			final char c = s.charAt(i);
			if ( c >= '0' && c <= '9' ) {
				return true; // No type qualifier, OK
			} else if ( c == 'e' || c == 'E' ) { return false; // can't have an E at the last byte
			}
		}

		// allowSigns is true iff the val ends in 'E'
		// Found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
		return !allowSigns && foundDigit;
	}

	@operator(value = "reverse", can_be_const = true)
	static public String reverse(final String s) {
		StringBuilder buf = new StringBuilder(s);
		buf.reverse();
		return buf.toString();
	}

	@operator(value = "empty", can_be_const = true)
	static public Boolean isEmpty(final String s) {
		return s != null && s.isEmpty();
	}

	@operator(value = "first", can_be_const = true)
	static public String first(final String s) {
		if ( s == null || s.isEmpty() ) { return ""; }
		return String.valueOf(s.charAt(0));
	}

	@operator(value = "last", can_be_const = true)
	static public String last(final String s) {
		if ( s == null || s.isEmpty() ) { return ""; }
		return String.valueOf(s.charAt(s.length() - 1));
	}

	@operator(value = "length", can_be_const = true)
	static public Integer length(final String s) {
		if ( s == null ) { return 0; }
		return s.length();
	}

	@operator(value = { "at", "@" }, can_be_const = true)
	public static String get(final String lv, final int rv) {
		return rv < lv.length() && rv >= 0 ? lv.substring(rv, rv + 1) : "";
	}
}
