/*********************************************************************************************
 * 
 * 
 * 'Strings.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.operators;

import java.util.*;
import java.util.regex.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.IType;
import org.joda.time.*;
import org.joda.time.chrono.*;
import org.joda.time.field.PreciseDurationField;
import org.joda.time.format.*;

/**
 * Written by drogoul Modified on 10 d�c. 2010
 * 
 * @todo Description
 * 
 */
public class Strings {

	public static final String LN = java.lang.System.getProperty("line.separator");
	public static final String TAB = "\t";

	static Pattern p = Pattern.compile("%[YMDhms]");
	private static PeriodFormatterBuilder format;
	private static PeriodFormatter dateFormat;
	private static PeriodFormatter timeFormat;
	private static GamaChronology chronology;

	static PeriodFormatterBuilder getCustomFormat() {
		if ( format == null ) {
			format = new PeriodFormatterBuilder();
		}
		return format;
	}

	static PeriodFormatter getTimeFormat() {
		if ( timeFormat == null ) {
			timeFormat =
				new PeriodFormatterBuilder().printZeroAlways().minimumPrintedDigits(2).appendHours().appendLiteral(":")
					.appendMinutes().appendLiteral(":").appendSeconds().toFormatter();
		}
		return timeFormat;
	}

	static PeriodFormatter getDateFormat() {
		if ( dateFormat == null ) {
			dateFormat =
				new PeriodFormatterBuilder().appendYears().appendSuffix(" year", " years").appendSeparator(", ")
					.appendMonths().appendSuffix(" month", " months").appendSeparator(", ").appendWeeks()
					.appendSuffix(" week", " weeks").appendSeparator(", ").appendDays().appendSuffix(" day", " days")
					.appendSeparator(" ").toFormatter();
		}
		return dateFormat;
	}

	static GamaChronology getChronology() {
		if ( chronology == null ) {
			chronology = new GamaChronology(GregorianChronology.getInstanceUTC());
		}
		return chronology;
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, category = { IOperatorCategory.STRING })
	public static
		String opPlus(final String a, final String b) {
		return a + b;
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if the left-hand operand is a string, returns the concatenation of the two operands (the left-hand one beind casted into a string)",
		examples = @example(value = "\"hello \" + 12", equals = "\"hello 12\"")))
	public static String opPlus(final IScope scope, final String a, final Object b) throws GamaRuntimeException {
		return a + Cast.asString(scope, b);
	}

	@operator(value = "in", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if both operands are strings, returns true if the left-hand operand patterns is included in to the right-hand string;"),
		examples = @example(value = " 'bc' in 'abcded'", equals = "true"))
	public static
		Boolean opIn(final String pattern, final String target) {
		return target.contains(pattern);
	}

	@operator(value = "contains", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if both operands are strings, returns true if the right-hand operand contains the right-hand pattern;"),
		examples = @example(value = "'abcded' contains 'bc'", equals = "true"))
	public static
		Boolean opContains(final String target, final String pattern) {
		return opIn(pattern, target);
	}

	@operator(value = "contains_any", can_be_const = true, expected_content_type = { IType.STRING })
	@doc(examples = @example(value = "\"abcabcabc\" contains_any [\"ca\",\"xy\"]", equals = "true"))
	public static Boolean opContainsAny(final String target, final List l) {
		for ( Object o : l ) {
			if ( o instanceof String && opContains(target, (String) o) ) { return true; }
		}
		return false;
	}

	@operator(value = "contains_all", can_be_const = true, expected_content_type = { IType.STRING })
	@doc(usages = @usage(value = "if the left-operand is a string, test whether the string contains all the element of the list;",
		examples = @example(value = "\"abcabcabc\" contains_all [\"ca\",\"xy\"]", equals = "false")))
	public static
		Boolean opContainsAll(final String target, final List l) {
		for ( Object o : l ) {
			if ( !(o instanceof String && opContains(target, (String) o)) ) { return false; }
		}
		return true;
	}

	@operator(value = "index_of", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if both operands are strings, returns the index within the left-hand string of the first occurrence of the given right-hand string",
		examples = @example(value = " \"abcabcabc\" index_of \"ca\"", equals = "2")))
	public static
		Integer opIndexOf(final String target, final String pattern) {
		return target.indexOf(pattern);
	}

	@operator(value = "last_index_of", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if both operands are strings, returns the index within the left-hand string of the rightmost occurrence of the given right-hand string",
		examples = @example(value = "\"abcabcabc\" last_index_of \"ca\"", equals = "5")))
	public static
		Integer opLastIndexOf(final String target, final String pattern) {
		return target.lastIndexOf(pattern);
	}

	@operator(value = { "copy_between" /* , "copy" */}, can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(deprecated = "Deprecated. Use copy_between(string, int, int) instead")
	public static String opCopy(final String target, final GamaPoint p) {
		final int beginIndex = (int) p.x;
		final int endIndex = (int) p.y;
		return opCopy(target, beginIndex, endIndex);
	}

	@operator(value = { "copy_between" /* , "copy" */}, can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(examples = @example(value = "copy_between(\"abcabcabc\", 2,6)", equals = "\"cabc\""))
	public static String opCopy(final String target, final Integer beginIndex, final Integer endIndex) {
		int bIndex = beginIndex < 0 ? 0 : beginIndex;
		int eIndex = endIndex > target.length() ? target.length() : endIndex;
		if ( bIndex >= eIndex ) { return ""; }
		return target.substring(bIndex, eIndex);
	}

	@operator(value = { "split_with", "tokenize" },
		content_type = IType.STRING,
		can_be_const = true,
		category = { IOperatorCategory.STRING })
	@doc(value = "Returns a list containing the sub-strings (tokens) of the left-hand operand delimited by each of the characters of the right-hand operand.",
		comment = "Delimiters themselves are excluded from the resulting list.",
		examples = @example(value = "'to be or not to be,that is the question' split_with ' ,'",
			equals = "['to','be','or','not','to','be','that','is','the','question']"))
	public static
		IList opTokenize(final String target, final String pattern) {
		final StringTokenizer st = new StringTokenizer(target, pattern);
		return new GamaList(Collections.list(st));
	}

	@operator(value = { "replace" }, can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(value = "Returns the String resulting by replacing for the first operand all the sub-strings corresponding the the second operand by the thrid operand",
		examples = @example(value = "replace('to be or not to be,that is the question','to', 'do')",
			equals = "'do be or not do be,that is the question'"))
	public static
		String opReplace(final String target, final String pattern, final String replacement) {
		return target.replaceAll(pattern, replacement);
	}

	@operator(value = "is_number", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(value = "tests whether the operand represents a numerical value",
		comment = "Note that the symbol . should be used for a float value (a string with , will not be considered as a numeric value). "
			+ "Symbols e and E are also accepted. A hexadecimal value should begin with #.",
		examples = { @example(value = "is_number(\"test\")", equals = "false"),
			@example(value = "is_number(\"123.56\")", equals = "true"),
			@example(value = "is_number(\"-1.2e5\")", equals = "true"),
			@example(value = "is_number(\"1,2\")", equals = "false"),
			@example(value = "is_number(\"#12FA\")", equals = "true") })
	public static
		Boolean isGamaNumber(final String s) {
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

	@operator(value = "reverse", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if it is a string, reverse returns a new string with caracters in the reversed order",
		examples = @example(value = "reverse ('abcd')", equals = "'dcba'")))
	static public
		String reverse(final String s) {
		StringBuilder buf = new StringBuilder(s);
		buf.reverse();
		return buf.toString();
	}

	@operator(value = "empty", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if it is a string, empty returns true if the string does not contain any character, and false otherwise",
		examples = @example(value = "empty ('abced')", equals = "false")))
	static public
		Boolean isEmpty(final String s) {
		return s != null && s.isEmpty();
	}

	@operator(value = "first", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if it is a string, first returns a string composed of its first character",
		examples = @example(value = "first ('abce')", equals = "'a'")))
	static public String first(final String s) {
		if ( s == null || s.isEmpty() ) { return ""; }
		return String.valueOf(s.charAt(0));
	}

	@operator(value = "last", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if it is a string, last returns a string composed of its last character, or an empty string if the operand is empty",
		examples = @example(value = "last ('abce')", equals = "'e'")))
	static public
		String last(final String s) {
		if ( s == null || s.isEmpty() ) { return ""; }
		return String.valueOf(s.charAt(s.length() - 1));
	}

	@operator(value = "length", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "if it is a string, length returns the number of characters",
		examples = @example(value = "length ('I am an agent')", equals = "13")))
	static public Integer length(final String s) {
		if ( s == null ) { return 0; }
		return s.length();
	}

	@operator(value = { IKeyword.AT, "@" }, can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(examples = @example(value = "'abcdef' at 0", equals = "'a'"))
	public static String get(final String lv, final int rv) {
		return rv < lv.length() && rv >= 0 ? lv.substring(rv, rv + 1) : "";
	}

	@operator(value = "char", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(usages = @usage(value = "converts ACSII integer value to character", examples = @example(value = "char (34)",
		equals = "'\"'")))
	static public String asChar(final Integer s) {
		if ( s == null ) { return ""; }
		return Character.toString((char) s.intValue());
	}

	@operator(value = "toChar", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc(deprecated = "Use 'char' instead",
		special_cases = { "convert ACSII integer value to character" },
		examples = { @example(value = "toChar (34)", equals = "'\"'") })
	static public String toChar(final Integer s) {
		return asChar(s);
	}

	@operator(value = "indented_by", can_be_const = true, category = { IOperatorCategory.STRING })
	@doc("Converts a (possibly multiline) string by indenting it by a number -- specified by the second operand -- of tabulations to the right")
	static public
		String indent(final String s, final int nb) {
		if ( nb <= 0 ) { return s; }
		StringBuilder sb = new StringBuilder(nb);
		for ( int i = 0; i < nb; i++ ) {
			sb.append(TAB);
		}
		String t = sb.toString();
		String indented = s.replaceAll("(?m)^", t);
		return indented;
	}

	private static final class GamaChronology extends AssembledChronology {

		private GamaChronology(final Chronology base) {
			super(base, null);
		}

		@Override
		protected void assemble(final AssembledChronology.Fields fields) {
			fields.months = new PreciseDurationField(DurationFieldType.months(), (long) IUnits.month * 1000);
			fields.years = new PreciseDurationField(DurationFieldType.years(), (long) IUnits.year * 1000);
		}

		@Override
		public Chronology withUTC() {
			return this;
		}

		@Override
		public Chronology withZone(final DateTimeZone zone) {
			return this;
		}

		@Override
		public String toString() {
			return "GAMA Chronology : 1 yr = 12 months ; 1 month = 30 days ";
		}

	}

	@operator(value = "as_date", can_be_const = true, category = { IOperatorCategory.STRING, IOperatorCategory.TIME })
	@doc(value = "converts a number into a string with year, month, day, hour, minutes, second following a given pattern (right-hand operand)",
		masterDoc = true,
		usages = @usage(value = "Pattern should include : \"%Y %M %D %h %m %s\" for outputting years, months, days, hours, minutes, seconds",
			examples = @example(value = "22324234 as_date \"%M m %D d %h h %m m %s seconds\"",
				equals = "\"8 m 18 d 9 h 10 m 34 seconds\"")),
		see = { "as_time" })
	public static
		String asDate(final double time, final String pattern) {
		// Pattern should include : "%Y %M %D %h %m %s" for outputting years, months, days, hours,
		// minutes, seconds
		if ( pattern == null || pattern.isEmpty() ) { return asDate(time) + " " + asTime(time); }
		getCustomFormat().clear();
		List<String> dateList = new ArrayList();
		final Matcher m = p.matcher(pattern);
		int i = 0;
		while (m.find()) {
			String tmp = m.group();
			if ( i != m.start() ) {
				dateList.add(pattern.substring(i, m.start()));
			}
			dateList.add(tmp);
			i = m.end();
		}
		if ( i != pattern.length() ) {
			dateList.add(pattern.substring(i));
		}
		for ( i = 0; i < dateList.size(); i++ ) {
			String s = dateList.get(i);
			if ( s.charAt(0) == '%' && s.length() == 2 ) {
				Character c = s.charAt(1);
				switch (c) {
					case 'Y':
						getCustomFormat().appendYears();
						break;
					case 'M':
						getCustomFormat().appendMonths();
						break;
					case 'D':
						getCustomFormat().appendDays();
						break;
					case 'h':
						getCustomFormat().appendHours();
						break;
					case 'm':
						getCustomFormat().appendMinutes();
						break;
					case 's':
						getCustomFormat().appendSeconds();
						break;
					default:
						getCustomFormat().appendLiteral(s);
				}
			} else {
				getCustomFormat().appendLiteral(s);
			}
		}

		PeriodFormatter pf = getCustomFormat().toFormatter();
		PeriodType pt = PeriodType.yearMonthDayTime();
		return pf.print(new Period(new Duration((long) time * 1000), getChronology()).normalizedStandard(pt));

	}

	@operator(value = "as_date", can_be_const = true, category = { IOperatorCategory.STRING, IOperatorCategory.TIME })
	@doc(value = "converts a number into a string with year, month, day, hour, minutes, second following a standard pattern",
		usages = @usage(value = "used as an unary operator, it uses a defined pattern with years, months, days",
			examples = @example(value = "as_date(22324234)", equals = "\"8 months, 18 days\"")))
	public static
		String asDate(final double time) {
		PeriodType pt = PeriodType.yearMonthDayTime();
		return getDateFormat().print(
			new Period(new Duration((long) time * 1000), getChronology()).normalizedStandard(pt));
	}

	@operator(value = "as_time", can_be_const = true, category = { IOperatorCategory.STRING, IOperatorCategory.TIME })
	@doc(value = "converts the given number into a string with hours, minutes and seconds",
		comment = "as_time operator is a particular case (using a particular pattern) of the as_date operator.",
		examples = @example(value = "as_time(22324234)", equals = "\"09:10:34\""),
		see = "as_date")
	public static String asTime(final double cycles) {
		PeriodType pt = PeriodType.yearMonthDayTime();
		return getTimeFormat().print(
			new Period(new Duration((long) cycles * 1000), getChronology()).normalizedStandard(pt));
	}

}
