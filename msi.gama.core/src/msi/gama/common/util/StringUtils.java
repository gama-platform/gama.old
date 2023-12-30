/*******************************************************************************************************
 *
 * StringUtils.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gaml.interfaces.IGamlable;
import msi.gaml.types.Types;

/**
 * The class StringUtils.
 *
 * @author drogoul
 * @since 13 d�c. 2011
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class StringUtils {

	// static {
	// DEBUG.ON();
	// }

	/** The Constant DEFAULT_DECIMAL_FORMAT. */
	public static final DecimalFormat DEFAULT_DECIMAL_FORMAT;

	/** The Constant SYMBOLS. */
	public static final DecimalFormatSymbols SYMBOLS;

	static {
		SYMBOLS = new DecimalFormatSymbols();
		SYMBOLS.setDecimalSeparator('.');
		SYMBOLS.setInfinity("#infinity");
		SYMBOLS.setNaN("#nan");
		DEFAULT_DECIMAL_FORMAT = new DecimalFormat("##0.0################", SYMBOLS);
	}

	/** The gama string pattern. */
	static Predicate<String> GAMA_STRING_PATTERN = Pattern.compile("^'.*'$").asMatchPredicate();

	/**
	 * To gaml string.
	 *
	 * @param s
	 *            the s
	 * @return the string
	 */
	static public String toGamlString(final String s) {
		if (s == null) return null;
		final int length = s.length();
		final StringBuilder sb = new StringBuilder(length);
		sb.append('\'');
		for (int i = 0; i < length; i++) {
			final char c = s.charAt(i);
			switch (c) {
				case '"':
				case '\'':
				case '\\':
					// Commented on purpose. See issue #2988
					// case '/':
					sb.append('\\');
					// $FALL-THROUGH$
				default:
					sb.append(c);
			}
		}
		sb.append('\'');
		return sb.toString();
	}

	/**
	 * To java string.
	 *
	 * @param s
	 *            the s
	 * @return the string
	 */
	static public String toJavaString(final String s) {
		if (s == null) return null;
		final String t = s.trim();
		if (!isGamaString(t)) return s;
		return t.substring(1, t.length() - 1);
	}

	/*
	 * See https://stackoverflow.com/questions/3537706/how-to-unescape-a-java-string-literal-in-javaw@
	 *
	 */

	/**
	 * Unescape java hand made 2.
	 *
	 * @param oldstr
	 *            the oldstr
	 * @return the string
	 */
	public final static String unescapeJava(final String oldstr) {
		if (oldstr == null) return null;
		final int sz = oldstr.length();
		StringBuilder newstr = new StringBuilder(sz);
		boolean saw_backslash = false;
		for (int i = 0; i < sz; i++) {
			int cp = oldstr.codePointAt(i);
			if (oldstr.codePointAt(i) > Character.MAX_VALUE) { i++; }
			if (!saw_backslash) {
				if (cp == '\\') {
					saw_backslash = true;
				} else {
					newstr.append(Character.toChars(cp));
				}
				continue; /* switch */
			}

			if (cp == '\\') {
				saw_backslash = false;
				newstr.append('\\');
				newstr.append('\\');
				continue; /* switch */
			}

			switch (cp) {

				case 'r':
					newstr.append('\r');
					break; /* switch */

				case 'n':
					newstr.append('\n');
					break; /* switch */

				case 'f':
					newstr.append('\f');
					break; /* switch */

				/* PASS a \b THROUGH!! */
				case 'b':
					newstr.append("\\b");
					break; /* switch */

				case 't':
					newstr.append('\t');
					break; /* switch */

				case 'a':
					newstr.append('\007');
					break; /* switch */

				case 'e':
					newstr.append('\033');
					break; /* switch */

				/*
				 * A "control" character is what you get when you xor its codepoint with '@'==64. This only makes sense
				 * for ASCII, and may not yield a "control" character after all.
				 *
				 * Strange but true: "\c{" is ";", "\c}" is "=", etc.
				 */
				case 'c': {
					if (++i == oldstr.length()) { emitError("trailing \\c"); }
					cp = oldstr.codePointAt(i);
					/*
					 * don't need to grok surrogates, as next line blows them up
					 */
					if (cp > 0x7f) { emitError("expected ASCII after \\c"); }
					newstr.append(Character.toChars(cp ^ 64));
					break; /* switch */
				}

				case '8':
				case '9':
					emitError("illegal octal digit");
					/* NOTREACHED */

					/*
					 * may be 0 to 2 octal digits following this one so back up one for fallthrough to next case; unread
					 * this digit and fall through to next case.
					 */
					//$FALL-THROUGH$
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
					--i;
					/* FALLTHROUGH */

					/*
					 * Can have 0, 1, or 2 octal digits following a 0 this permits larger values than octal 377, up to
					 * octal 777.
					 */
					//$FALL-THROUGH$
				case '0': {
					if (i + 1 == oldstr.length()) {
						/* found \0 at end of string */
						newstr.append(Character.toChars(0));
						break; /* switch */
					}
					i++;
					int digits = 0;
					int j;
					for (j = 0; j <= 2; j++) {
						if (i + j == oldstr.length()) {
							break; /* for */
						}
						/* safe because will unread surrogate */
						int ch = oldstr.charAt(i + j);
						if (ch < '0' || ch > '7') {
							break; /* for */
						}
						digits++;
					}
					if (digits == 0) {
						--i;
						newstr.append('\0');
						break; /* switch */
					}
					int value = 0;
					try {
						value = Integer.parseInt(oldstr.substring(i, i + digits), 8);
					} catch (NumberFormatException nfe) {
						emitError("invalid octal value for \\0 escape");
					}
					newstr.append(Character.toChars(value));
					i += digits - 1;
					break; /* switch */
				} /* end case '0' */

				case 'x': {
					if (i + 2 > oldstr.length()) { emitError("string too short for \\x escape"); }
					i++;
					boolean saw_brace = false;
					if (oldstr.charAt(i) == '{') {
						/* ^^^^^^ ok to ignore surrogates here */
						i++;
						saw_brace = true;
					}
					int j;
					for (j = 0; j < 8; j++) {

						if (!saw_brace && j == 2) {
							break; /* for */
						}

						/*
						 * ASCII test also catches surrogates
						 */
						int ch = oldstr.charAt(i + j);
						if (ch > 127) { emitError("illegal non-ASCII hex digit in \\x escape"); }

						if (saw_brace && ch == '}') { break; /* for */ }

						if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'f') && (ch < 'A' || ch > 'F')) {
							emitError(String.format("illegal hex digit #%d '%c' in \\x", ch, ch));
						}

					}
					if (j == 0) { emitError("empty braces in \\x{} escape"); }
					int value = 0;
					try {
						value = Integer.parseInt(oldstr.substring(i, i + j), 16);
					} catch (NumberFormatException nfe) {
						emitError("invalid hex value for \\x escape");
					}
					newstr.append(Character.toChars(value));
					if (saw_brace) { j++; }
					i += j - 1;
					break; /* switch */
				}

				case 'u': {
					if (i + 4 > oldstr.length()) { emitError("string too short for \\u escape"); }
					i++;
					int j;
					for (j = 0; j < 4; j++) {
						/* this also handles the surrogate issue */
						if (oldstr.charAt(i + j) > 127) { emitError("illegal non-ASCII hex digit in \\u escape"); }
					}
					int value = 0;
					try {
						value = Integer.parseInt(oldstr.substring(i, i + j), 16);
					} catch (NumberFormatException nfe) {
						emitError("invalid hex value for \\u escape");
					}
					newstr.append(Character.toChars(value));
					i += j - 1;
					break; /* switch */
				}

				case 'U': {
					if (i + 8 > oldstr.length()) { emitError("string too short for \\U escape"); }
					i++;
					int j;
					for (j = 0; j < 8; j++) {
						/* this also handles the surrogate issue */
						if (oldstr.charAt(i + j) > 127) { emitError("illegal non-ASCII hex digit in \\U escape"); }
					}
					int value = 0;
					try {
						value = Integer.parseInt(oldstr.substring(i, i + j), 16);
					} catch (NumberFormatException nfe) {
						emitError("invalid hex value for \\U escape");
					}
					newstr.append(Character.toChars(value));
					i += j - 1;
					break; /* switch */
				}

				default:
					newstr.append('\\');
					newstr.append(Character.toChars(cp));
					break; /* switch */

			}
			saw_backslash = false;
		}

		if (saw_backslash) { newstr.append('\\'); }

		return newstr.toString();
	}

	/**
	 * Die.
	 *
	 * @param foa
	 *            the foa
	 */
	private static final void emitError(final String foa) {
		GAMA.reportAndThrowIfNeeded(null, GamaRuntimeException.warning(foa, null), false);
	}

	/**
	 * Checks if is gama string.
	 *
	 * @param s
	 *            the s
	 * @return true, if is gama string
	 */
	static public boolean isGamaString(final String s) {
		if (s == null) return false;
		int length = s.length();
		if (length < 2 || s.charAt(0) != '\'' || s.charAt(length - 1) != '\'') return false;
		return true;

		// return s != null && GAMA_STRING_PATTERN.test(s);
	}

	/**
	 * To gaml.
	 *
	 * @param val
	 *            the val
	 * @param includingBuiltIn
	 *            the including built in
	 * @return the string
	 */
	public static String toGaml(final Object val, final boolean includingBuiltIn) {
		if (val == null) return "nil";
		if (val instanceof IGamlable g) return g.serializeToGaml(includingBuiltIn);
		if (val instanceof String s) return toGamlString(s);
		if (val instanceof Double d) return DEFAULT_DECIMAL_FORMAT.format(d);
		if (val instanceof Collection c) return toGaml(GamaListFactory.wrap(Types.STRING, c), includingBuiltIn);
		if (val instanceof Map m) return toGaml(GamaMapFactory.wrap(Types.NO_TYPE, Types.NO_TYPE, m), includingBuiltIn);
		return String.valueOf(val);
	}

}
