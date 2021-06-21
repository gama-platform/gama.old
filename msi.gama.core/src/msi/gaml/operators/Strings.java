/*******************************************************************************************************
 *
 * msi.gaml.operators.Strings.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import java.util.StringTokenizer;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "rawtypes" })
public class Strings {

//	static {
//		DEBUG.ON();
//	}

	public static final String LN = java.lang.System.getProperty("line.separator");
	public static final String TAB = "\t";

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING },
			doc = @doc ("Concatenates the two string operands"))
	@doc (
			usages = @usage (
					value = "if the left-hand and right-hand operand are a string, returns the concatenation of the two operands",
					examples = @example (
							value = "\"hello \" + \"World\"",
							equals = "\"hello World\"")))

	@test ("'a'+'b'='ab'")
	@test ("''+'' = ''")
	@test ("string a <- 'a'; a + '' = a")
	public static String opPlus(final String a, final String b) {
		return a + b;
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if the left-hand operand is a string, returns the concatenation of the two operands (the left-hand one beind casted into a string)",
					examples = @example (
							value = "\"hello \" + 12",
							equals = "\"hello 12\"")))
	public static String opPlus(final IScope scope, final String a, final Object b) throws GamaRuntimeException {
		return a + Cast.asString(scope, b);
	}

	@operator (
			value = "in",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if both operands are strings, returns true if the left-hand operand patterns is included in to the right-hand string;"),
			examples = @example (
					value = " 'bc' in 'abcded'",
					equals = "true"))
	public static Boolean opIn(final String pattern, final String target) {
		return target.contains(pattern);
	}

	@operator (
			value = "contains",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if both operands are strings, returns true if the right-hand operand contains the right-hand pattern;"),
			examples = @example (
					value = "'abcded' contains 'bc'",
					equals = "true"))
	public static Boolean opContains(final String target, final String pattern) {
		return opIn(pattern, target);
	}

	@operator (
			value = "contains_any",
			can_be_const = true,
			expected_content_type = { IType.STRING },
			concept = { IConcept.STRING })
	@doc (
			examples = @example (
					value = "\"abcabcabc\" contains_any [\"ca\",\"xy\"]",
					equals = "true"))
	public static Boolean opContainsAny(final String target, final IList l) {
		for (final Object o : l) {
			if (o instanceof String && opContains(target, (String) o)) return true;
		}
		return false;
	}

	@operator (
			value = "contains_all",
			can_be_const = true,
			expected_content_type = { IType.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if the left-operand is a string, test whether the string contains all the element of the list;",
					examples = @example (
							value = "\"abcabcabc\" contains_all [\"ca\",\"xy\"]",
							equals = "false")))
	public static Boolean opContainsAll(final String target, final IList l) {
		for (final Object o : l) {
			if (!(o instanceof String && opContains(target, (String) o))) return false;
		}
		return true;
	}

	@operator (
			value = "index_of",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if both operands are strings, returns the index within the left-hand string of the first occurrence of the given right-hand string",
					examples = @example (
							value = " \"abcabcabc\" index_of \"ca\"",
							equals = "2")))
	public static Integer opIndexOf(final String target, final String pattern) {
		return target.indexOf(pattern);
	}

	@operator (
			value = "last_index_of",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if both operands are strings, returns the index within the left-hand string of the rightmost occurrence of the given right-hand string",
					examples = @example (
							value = "\"abcabcabc\" last_index_of \"ca\"",
							equals = "5")))
	public static Integer opLastIndexOf(final String target, final String pattern) {
		return target.lastIndexOf(pattern);
	}

	@operator (
			value = { "copy_between" /* , "copy" */ },
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			examples = @example (
					value = "copy_between(\"abcabcabc\", 2,6)",
					equals = "\"cabc\""))
	public static String opCopy(final String target, final Integer beginIndex, final Integer endIndex) {
		final int bIndex = beginIndex < 0 ? 0 : beginIndex;
		final int eIndex = endIndex > target.length() ? target.length() : endIndex;
		if (bIndex >= eIndex) return "";
		return target.substring(bIndex, eIndex);
	}

	@operator (
			value = { "split_with", "tokenize" },
			content_type = IType.STRING,
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "Returns a list containing the sub-strings (tokens) of the left-hand operand delimited by each of the characters of the right-hand operand.",
			masterDoc = true,
			comment = "Delimiters themselves are excluded from the resulting list.",
			examples = @example (
					value = "'to be or not to be,that is the question' split_with ' ,'",
					equals = "['to','be','or','not','to','be','that','is','the','question']"))
	public static IList opTokenize(final IScope scope, final String target, final String pattern) {
		return opTokenize(scope, target, pattern, false);
	}

	@operator (
			value = { "split_with", "tokenize" },
			content_type = IType.STRING,
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "Returns a list containing the sub-strings (tokens) of the left-hand operand delimited either by each of the characters of the right-hand operand (false) or by the whole right-hand operand (true).",
			usages = @usage (
					value = "when used  with an  additional boolean operand, it returns a list containing the sub-strings (tokens) of the left-hand operand delimited either by each of the characters of the right-hand operand (false) or by the whole right-hand operand (true)."),
			examples = { @example (
					value = "'aa::bb:cc' split_with ('::', true)",
					equals = "['aa','bb:cc']"),
					@example (
							value = "'aa::bb:cc' split_with ('::', false)",
							equals = "['aa','bb','cc']") })
	public static IList opTokenize(final IScope scope, final String target, final String pattern,
			final Boolean completeSep) {
		if (completeSep) return GamaListFactory.create(scope, Types.STRING, target.split(pattern));
		final StringTokenizer st = new StringTokenizer(target, pattern);
		return GamaListFactory.create(scope, Types.STRING, st);
	}

	@operator (
			value = { "replace" },
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "Returns the String resulting by replacing for the first operand all the sub-strings corresponding the second operand by the third operand",
			examples = @example (
					value = "replace('to be or not to be,that is the question','to', 'do')",
					equals = "'do be or not do be,that is the question'"),
			see = { "replace_regex" })
	public static String opReplace(final String target, final String pattern, final String replacement) {
		return target.replace(pattern, replacement);
	}

	@operator (
			value = { "replace_regex" },
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "Returns the String resulting by replacing for the first operand all the sub-strings corresponding to the regular expression given in the second operand by the third operand",
			examples = @example (
					value = "replace_regex(\"colour, color\", \"colou?r\", \"col\")",
					equals = "'col, col'"),
			see = { "replace" })
	public static String opReplaceRegex(final String target, final String pattern, final String replacement) {
//		DEBUG.OUT("String pattern = " + pattern);
		return target.replaceAll(pattern, replacement);
	}

	@operator (
			value = "is_number",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "tests whether the operand represents a numerical value",
			comment = "Note that the symbol . should be used for a float value (a string with , will not be considered as a numeric value). "
					+ "Symbols e and E are also accepted. A hexadecimal value should begin with #.",
			examples = { @example (
					value = "is_number(\"test\")",
					equals = "false"),
					@example (
							value = "is_number(\"123.56\")",
							equals = "true"),
					@example (
							value = "is_number(\"-1.2e5\")",
							equals = "true"),
					@example (
							value = "is_number(\"1,2\")",
							equals = "false"),
					@example (
							value = "is_number(\"#12FA\")",
							equals = "true") })
	public static Boolean isGamaNumber(final String s) {
		// copright notice:
		// original code taken from
		// org.apache.commons.lang.NumberUtils.isNumber(String)

		if (s == null) return false;
		final int length = s.length();
		if (length == 0) return false;
		int sz = length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;

		// deal with any possible sign up front
		final int start = s.charAt(0) == '-' ? 1 : 0;
		if (sz > start + 1) {
			if (s.charAt(start) == '#') {
				int i = start + 1;
				if (i == sz) return false; // str == "#"
				// Checking hex (it can't be anything else)
				for (; i < length; i++) {
					final char c = s.charAt(i);
					if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) return false;
				}

				return true;
			}
		}

		sz--; // Don't want to loop to the last char, check it afterwords for
				// type
		// qualifiers
		int i = start;

		// Loop to the next to last char or to the last char if we need another
		// digit to
		// make a valid number (e.g. chars[0..5] = "1234E")
		while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
			final char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				foundDigit = true;
				allowSigns = false;
			} else if (c == '.') {
				if (hasDecPoint || hasExp) // Two decimal points or dec in exponent
					return false;
				hasDecPoint = true;
			} else if (c == 'e' || c == 'E') {
				// We've already taken care of hex.
				if (hasExp) // Two E's
					return false;
				if (foundDigit) {
					hasExp = true;
					allowSigns = true;
				} else
					return false;
			} else if (c == '-') {
				if (allowSigns) {
					allowSigns = false;
					foundDigit = false; // We need a digit after the E
				} else
					return false;
			} else
				return false;

			i++;
		}

		if (i < length) {
			final char c = s.charAt(i);
			if (c >= '0' && c <= '9')
				return true; // No type qualifier, OK
			else if (c == 'e' || c == 'E') return false; // can't have an E at the last byte
		}

		// allowSigns is true iff the val ends in 'E'
		// Found digit it to make sure weird stuff like '.' and '1E-' doesn't
		// pass
		return !allowSigns && foundDigit;
	}

	@operator (
			value = "reverse",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if it is a string, reverse returns a new string with characters in the reversed order",
					examples = @example (
							value = "reverse ('abcd')",
							equals = "'dcba'")))
	static public String reverse(final String s) {
		final StringBuilder buf = new StringBuilder(s);
		buf.reverse();
		return buf.toString();
	}

	@operator (
			value = "empty",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if it is a string, empty returns true if the string does not contain any character, and false otherwise",
					examples = @example (
							value = "empty ('abced')",
							equals = "false")))
	static public Boolean isEmpty(final String s) {
		return s != null && s.isEmpty();
	}

	@operator (
			value = "first",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if it is a string, first returns a string composed of its first character",
					examples = @example (
							value = "first ('abce')",
							equals = "'a'")))
	static public String first(final String s) {
		if (s == null || s.isEmpty()) return "";
		return String.valueOf(s.charAt(0));
	}

	@operator (
			value = "last",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if it is a string, last returns a string composed of its last character, or an empty string if the operand is empty",
					examples = @example (
							value = "last ('abce')",
							equals = "'e'")))
	static public String last(final String s) {
		if (s == null || s.isEmpty()) return "";
		return String.valueOf(s.charAt(s.length() - 1));
	}

	@operator (
			value = "length",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "if it is a string, length returns the number of characters",
					examples = @example (
							value = "length ('I am an agent')",
							equals = "13")))
	static public Integer length(final String s) {
		if (s == null) return 0;
		return s.length();
	}

	@operator (
			value = { IKeyword.AT, "@" },
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			examples = @example (
					value = "'abcdef' at 0",
					equals = "'a'"))
	public static String get(final String lv, final int rv) {
		return rv < lv.length() && rv >= 0 ? lv.substring(rv, rv + 1) : "";
	}

	@operator (
			value = "char",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			usages = @usage (
					value = "converts ACSII integer value to character",
					examples = @example (
							value = "char (34)",
							equals = "'\"'")))
	static public String asChar(final Integer s) {
		if (s == null) return "";
		return Character.toString((char) s.intValue());
	}

	@operator (
			value = "indented_by",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "Converts a (possibly multiline) string by indenting it by a number -- specified by the second operand -- of tabulations to the right",
			examples = @example (
					value = "\"my\" + indented_by(\"text\", 1)",
					equals = "\"my	text\""))
	static public String indent(final String s, final int nb) {
		if (nb <= 0) return s;
		final StringBuilder sb = new StringBuilder(nb);
		for (int i = 0; i < nb; i++) {
			sb.append(TAB);
		}
		final String t = sb.toString();
		return s.replaceAll("(?m)^", t);
	}

	@operator (
			value = "lower_case",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "Converts all of the characters in the string operand to lower case",
			examples = @example (
					value = "lower_case(\"Abc\")",
					equals = "'abc'"),
			see = { "upper_case" })
	static public String toLowerCase(final String s) {
		if (s == null) return s;
		return s.toLowerCase();
	}

	@operator (
			value = "upper_case",
			can_be_const = true,
			category = { IOperatorCategory.STRING },
			concept = { IConcept.STRING })
	@doc (
			value = "Converts all of the characters in the string operand to upper case",
			examples = @example (
					value = "upper_case(\"Abc\")",
					equals = "'ABC'"),
			see = { "lower_case" })
	static public String toUpperCase(final String s) {
		if (s == null) return s;
		return s.toUpperCase();
	}

}
