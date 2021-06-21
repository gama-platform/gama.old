/*******************************************************************************************************
 *
 * msi.gama.common.util.StringUtils.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import msi.gama.common.interfaces.IGamlable;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The class StringUtils.
 *
 * @author drogoul
 * @since 13 d�c. 2011
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class StringUtils {

//	static {
//		DEBUG.ON();
//	}

	final static String strings = "'(?:[^\\\\']+|\\\\.)*'";
	final static String operators = "::|<>|!=|>=|<=|//";
	public final static String ponctuation = "\\p{Punct}";
	public final static String literals = "\\w+\\$\\w+|\\#\\w+|\\d+\\.\\d+|\\w+\\.\\w+|\\w+";
	final static String regex = strings + "|" + literals + "|" + operators + "|" + ponctuation;

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

	static public String toJavaString(final String s) {
		if (s == null) return null;
		final String t = s.trim();
		if (!isGamaString(t)) return s;
		if (t.length() >= 2) return t.substring(1, t.length() - 1);
		return s;
	}

	public static List<String> tokenize(final String expression) {
		if (expression == null) return Collections.EMPTY_LIST;
		final Pattern p = Pattern.compile(regex);
		final List<String> tokens = new ArrayList<>();
		final Matcher m = p.matcher(expression);
		while (m.find()) {
			tokens.add(expression.substring(m.start(), m.end()));
		}
		return tokens;
	}

	/**
	 * Unescape java.
	 *
	 * @param str
	 *            the str
	 *
	 * @return the string
	 */
	static public String unescapeJava(final String str) {
		if (str == null) return null;
		// final String result = unescapeJavaHandMade1(str); // AD: original version
		final String result = unescapeJavaHandMade2(str); // AD: More complete version. See #3095
//		DEBUG.LOG("Input: " + str + " ; unescaped output: " + result);
		return result;
	}

	static private String unescapeJavaHandMade1(final String str) {
		final int sz = str.length();
		final StringBuilder writer = new StringBuilder(str.length());
		boolean hadSlash = false;
		boolean inUnicode = false;
		StringBuilder unicode = null;
		for (int i = 0; i < sz; i++) {
			final char ch = str.charAt(i);
			if (inUnicode) {
				// if in unicode, then we're reading unicode
				// values in somehow
				if (unicode == null) { unicode = new StringBuilder(4); }
				unicode.append(ch);
				if (unicode.length() == 4) {
					final int value = Integer.parseInt(unicode.toString(), 16);
					writer.append((char) value);
					unicode.setLength(0);
					inUnicode = false;
					hadSlash = false;
				}
				continue;
			}
			if (hadSlash) {
				// handle an escaped value
				hadSlash = false;
				switch (ch) {
					case '\\':
						writer.append('\\');
						break;
					case '\'':
						writer.append('\'');
						break;
					case '\"':
						writer.append('"');
						break;
					case '/':
						writer.append('/');
						break;
					case 'r':
						writer.append('\r');
						break;
					case 'f':
						writer.append('\f');
						break;
					case 't':
						writer.append('\t');
						break;
					case 'n':
						writer.append('\n');
						break;
					case 'b':
						writer.append('\b');
						break;
					case 'u': {
						// uh-oh, we're in unicode country....
						inUnicode = true;
						break;
					}
					default:
						// See Issue #3095
						writer.append('\\').append(ch);
						break;
				}
				continue;
			} else if (ch == '\\') {
				hadSlash = true;
				continue;
			}
			writer.append(ch);
		}
		if (hadSlash) {
			// string, let's output it anyway.
			writer.append('\\');
		}
		return writer.toString();
	}

	/*
	 * See https://stackoverflow.com/questions/3537706/how-to-unescape-a-java-string-literal-in-javaw@
	 *
	 */

	public final static String unescapeJavaHandMade2(final String oldstr) {
		final int sz = oldstr.length();
		StringBuffer newstr = new StringBuffer(sz);
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
					if (++i == oldstr.length()) { die("trailing \\c"); }
					cp = oldstr.codePointAt(i);
					/*
					 * don't need to grok surrogates, as next line blows them up
					 */
					if (cp > 0x7f) { die("expected ASCII after \\c"); }
					newstr.append(Character.toChars(cp ^ 64));
					break; /* switch */
				}

				case '8':
				case '9':
					die("illegal octal digit");
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
						die("invalid octal value for \\0 escape");
					}
					newstr.append(Character.toChars(value));
					i += digits - 1;
					break; /* switch */
				} /* end case '0' */

				case 'x': {
					if (i + 2 > oldstr.length()) { die("string too short for \\x escape"); }
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
						if (ch > 127) { die("illegal non-ASCII hex digit in \\x escape"); }

						if (saw_brace && ch == '}') { break; /* for */ }

						if (!(ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F')) {
							die(String.format("illegal hex digit #%d '%c' in \\x", ch, ch));
						}

					}
					if (j == 0) { die("empty braces in \\x{} escape"); }
					int value = 0;
					try {
						value = Integer.parseInt(oldstr.substring(i, i + j), 16);
					} catch (NumberFormatException nfe) {
						die("invalid hex value for \\x escape");
					}
					newstr.append(Character.toChars(value));
					if (saw_brace) { j++; }
					i += j - 1;
					break; /* switch */
				}

				case 'u': {
					if (i + 4 > oldstr.length()) { die("string too short for \\u escape"); }
					i++;
					int j;
					for (j = 0; j < 4; j++) {
						/* this also handles the surrogate issue */
						if (oldstr.charAt(i + j) > 127) { die("illegal non-ASCII hex digit in \\u escape"); }
					}
					int value = 0;
					try {
						value = Integer.parseInt(oldstr.substring(i, i + j), 16);
					} catch (NumberFormatException nfe) {
						die("invalid hex value for \\u escape");
					}
					newstr.append(Character.toChars(value));
					i += j - 1;
					break; /* switch */
				}

				case 'U': {
					if (i + 8 > oldstr.length()) { die("string too short for \\U escape"); }
					i++;
					int j;
					for (j = 0; j < 8; j++) {
						/* this also handles the surrogate issue */
						if (oldstr.charAt(i + j) > 127) { die("illegal non-ASCII hex digit in \\U escape"); }
					}
					int value = 0;
					try {
						value = Integer.parseInt(oldstr.substring(i, i + j), 16);
					} catch (NumberFormatException nfe) {
						die("invalid hex value for \\U escape");
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

	/*
	 * Return a string "U+XX.XXX.XXXX" etc, where each XX set is the xdigits of the logical Unicode code point. No
	 * bloody brain-damaged UTF-16 surrogate crap, just true logical characters.
	 */
	public final static String uniplus(final String s) {
		if (s.length() == 0) return "";
		/* This is just the minimum; sb will grow as needed. */
		StringBuffer sb = new StringBuffer(2 + 3 * s.length());
		sb.append("U+");
		for (int i = 0; i < s.length(); i++) {
			sb.append(String.format("%X", s.codePointAt(i)));
			if (s.codePointAt(i) > Character.MAX_VALUE) {
				i++; /**** WE HATES UTF-16! WE HATES IT FOREVERSES!!! ****/
			}
			if (i + 1 < s.length()) { sb.append("."); }
		}
		return sb.toString();
	}

	private static final void die(final String foa) {
		GAMA.reportAndThrowIfNeeded(null, GamaRuntimeException.warning(foa, null), false);
	}

	static public boolean isGamaString(final String s) {
		if (s == null) return false;
		final int n = s.length();
		if (n == 0 || n == 1) return false;
		if (s.charAt(0) != '\'') return false;
		return s.charAt(n - 1) == '\'';
	}

	public static final DecimalFormat DEFAULT_DECIMAL_FORMAT;
	public static final DecimalFormatSymbols SYMBOLS;

	static {
		SYMBOLS = new DecimalFormatSymbols();
		SYMBOLS.setDecimalSeparator('.');
		SYMBOLS.setInfinity("#infinity");
		SYMBOLS.setNaN("#nan");
		DEFAULT_DECIMAL_FORMAT = new DecimalFormat("##0.0################", SYMBOLS);
	}

	public static String toGaml(final Object val, final boolean includingBuiltIn) {
		if (val == null) return "nil";
		if (val instanceof IGamlable) return ((IGamlable) val).serialize(includingBuiltIn);
		if (val instanceof String) return toGamlString((String) val);
		if (val instanceof Double) return DEFAULT_DECIMAL_FORMAT.format(val);
		if (val instanceof Collection) {
			final IList l = GamaListFactory.create(Types.STRING);
			l.addAll((Collection) val);
			return toGaml(l, includingBuiltIn);
		}
		return String.valueOf(val);
	}

}
