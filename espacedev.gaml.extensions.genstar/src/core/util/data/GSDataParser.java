/*******************************************************************************************************
 *
 * GSDataParser.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util.data;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.metamodel.value.numeric.template.GSRangeTemplate;
import core.util.exception.GSIllegalRangedData;

/**
 *
 * Object that can read data and:
 * <p>
 * <ul>
 * <li>{@link #getValueType(String)} give the implicit parsed data type ( {@link GSEnumDataType} )
 * <li>{@link #getRangedDoubleData(String, boolean)} or {@link #getRangedIntegerData(String, boolean)}
 * </ul>
 * <p>
 * Can give explicit values from string ranged value data representation
 *
 * @author kevinchapuis
 *
 */
public class GSDataParser {

	/** The Constant DOES_NOT_REPRESENT_ANY_VALUE. */
	private static final String DOES_NOT_REPRESENT_ANY_VALUE = " does not represent any value";

	/** The Constant THE_STRING_RANGED_DATA. */
	private static final String THE_STRING_RANGED_DATA = "The string ranged data ";

	/** The Constant DEFAULT_NUM_MATCH. */
	public static final String DEFAULT_NUM_MATCH = "#";

	/** The split operator. */
	private static String SPLIT_OPERATOR = " ";

	/**
	 * The Enum NumMatcher.
	 */
	public enum NumMatcher {

		/** The double match eng. */
		DOUBLE_MATCH_ENG("(\\-)?(\\d+\\.\\d+)(E(\\-)?\\d+)?"),

		/** The double positif match eng. */
		DOUBLE_POSITIF_MATCH_ENG("(^\\d+\\.\\d+)(E\\-\\d+)?"),

		/** The double match fr. */
		DOUBLE_MATCH_FR("(\\-)?(\\d+\\,\\d+)(E(\\-)?\\d+)?"),

		/** The double positif match fr. */
		DOUBLE_POSITIF_MATCH_FR("(^\\d+\\,\\d+)(E\\-\\d+)?"),

		/** The int positif match. */
		INT_POSITIF_MATCH("\\d+"),

		/** The int match. */
		INT_MATCH("-?\\d+");

		/** The match. */
		private final String match;

		/**
		 * Instantiates a new num matcher.
		 *
		 * @param match
		 *            the match
		 */
		NumMatcher(final String match) {
			this.match = match;
		}

		/**
		 * Gives the string matcher
		 *
		 * @return
		 */
		public String getMatch() { return match; }

		/**
		 * Default numeric value String matcher which look for positive integer range values
		 *
		 * @return
		 */
		public static NumMatcher getDefault() { return NumMatcher.INT_POSITIF_MATCH; }

	}

	/**
	 * Methods that retrieve value type ({@link GSEnumDataType}) through string parsing <br/>
	 * Default type is {@value GSEnumDataType#STRING}
	 *
	 * @param value
	 * @return
	 */
	public GSEnumDataType getValueType(String value) {
		value = value.trim();
		if (value.matches(NumMatcher.INT_MATCH.getMatch())) return GSEnumDataType.Integer;
		if (value.matches(NumMatcher.DOUBLE_MATCH_ENG.getMatch())
				|| value.matches(NumMatcher.DOUBLE_MATCH_FR.getMatch()))
			return GSEnumDataType.Continue;
		if (Boolean.TRUE.toString().equalsIgnoreCase(value) || Boolean.FALSE.toString().equalsIgnoreCase(value))
			return GSEnumDataType.Boolean;
		try {
			this.getRangedDoubleData(value, NumMatcher.DOUBLE_MATCH_ENG);
			return GSEnumDataType.Range;
		} catch (Exception e) {
			return GSEnumDataType.Nominal;
		}
	}

	/**
	 * Can extract the template of the range data in given string representation
	 *
	 * @see GSRangeTemplate
	 *
	 *      WARNING: untested FIXME: only get template with integer based range data
	 *
	 * @param range
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public GSRangeTemplate getRangeTemplate(final List<String> ranges, final String match, final NumMatcher numMatcher)
			throws GSIllegalRangedData {
		List<Integer> rangeInt = new ArrayList<>();
		for (String range : ranges) { rangeInt.addAll(this.getRangedIntegerData(range, numMatcher)); }
		Collections.sort(rangeInt);
		String lowerBound = "";
		String upperBound = "";
		String middle = "";
		for (String range : ranges) {
			List<Integer> ints = this.getRangedIntegerData(range, numMatcher);
			if (ints.size() == 1) {
				if (ints.get(0).equals(rangeInt.get(0))) {
					lowerBound = range.replaceAll(rangeInt.get(0).toString(), match);
				} else if (ints.get(0).equals(rangeInt.get(rangeInt.size() - 1))) {
					upperBound = range.replaceAll(rangeInt.get(rangeInt.size() - 1).toString(), match);
				}
			} else if (middle.isEmpty()) {
				middle = range.replaceAll(numMatcher.getMatch(), match);
			} else {
				String newMiddle = range.replaceAll(numMatcher.getMatch(), match);
				if (!newMiddle.equalsIgnoreCase(middle))
					throw new GSIllegalRangedData("Range template has more than 3 range format");
			}
		}
		if (lowerBound.isEmpty()) {
			int lb = Integer.MAX_VALUE;
			for (String range : ranges) {
				List<Integer> ints = this.getRangedIntegerData(range, numMatcher);
				if (ints.size() == 2 && Math.min(ints.get(0), ints.get(1)) < lb) {
					lb = Math.min(ints.get(0), ints.get(1));
					lowerBound = range;// .replaceAll(ints.get(1).toString(), match);
				}
			}
		}
		if (upperBound.isEmpty()) {
			int ub = -1 * Integer.MAX_VALUE;
			for (String range : ranges) {
				List<Integer> ints = this.getRangedIntegerData(range, numMatcher);
				if (ints.size() == 2 && Math.max(ints.get(0), ints.get(1)) > ub) {
					ub = Math.max(ints.get(0), ints.get(1));
					upperBound = range;// .replaceAll(ints.get(1).toString(), match);
				}

			}
		}
		return new GSRangeTemplate(lowerBound, middle, upperBound, match, numMatcher);
	}

	/**
	 * Extract range template from a list of ranges and given number matcher
	 *
	 * @param ranges
	 * @param numMatcher
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public GSRangeTemplate getRangeTemplate(final List<String> ranges, final NumMatcher numMatcher)
			throws GSIllegalRangedData {
		return getRangeTemplate(ranges, DEFAULT_NUM_MATCH, numMatcher);
	}

	/**
	 * default replacement of match {@link NumMatcher}
	 *
	 * @see #getRangeTemplate(List, String, NumMatcher)
	 *
	 * @param ranges
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public GSRangeTemplate getRangeTemplate(final List<String> ranges) throws GSIllegalRangedData {
		return getRangeTemplate(ranges, DEFAULT_NUM_MATCH, NumMatcher.getDefault());
	}

	/**
	 * Parses double range values from string representation. There is no need for specifying <br/>
	 * any delimiter, although the method rely on proper {@link Double} values string encoding. <br/>
	 * If negative value is true delimiter can't be the null "-" symbol
	 *
	 * @param range
	 * @return {@link List} of min and max double values based on {@code range} string representation
	 * @throws GSIllegalRangedData
	 */
	public List<Double> getRangedDoubleData(final String range, final NumMatcher numMatcher)
			throws GSIllegalRangedData {
		List<Double> list = new ArrayList<>();
		List<String> stringRange = this.getNumbers(range, numMatcher.getMatch());
		stringRange.stream().forEach(String::trim);
		stringRange = stringRange.stream().filter(s -> !s.isEmpty()).toList();
		if (stringRange.isEmpty())
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + DOES_NOT_REPRESENT_ANY_VALUE);
		if (stringRange.size() > 2)
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + " has more than 2 (lower / upper) values");
		for (String i : stringRange) { list.add(Double.valueOf(i)); }
		return list;
	}

	/**
	 * {@link #getRandedDoubleData(String, boolean)} for specification. Also this method allow for {@code minVal} <br/>
	 * {@code maxVal} forced value: this is intended to encoded ranged value from "min implicit double value" (e.g. age
	 * = 0) <br/>
	 * to ranged parsed integer value or from ranged parsed to "max implicit double value"
	 *
	 * @param range
	 * @param nullValue
	 * @param minVal
	 * @deprecated
	 * @return {@link List} of min and max double values based on given {@code minVal} and parsed max {@code range}
	 * @throws GSIllegalRangedData
	 */
	@Deprecated (
			forRemoval = true)
	public List<Double> getRangedData(String range, final NumMatcher numMatcher, final Double minVal,
			final Double maxVal) throws GSIllegalRangedData {
		List<Double> list = new ArrayList<>();
		range = range.replaceAll(numMatcher.getMatch(), SPLIT_OPERATOR);
		List<String> stringRange = Arrays.asList(range.trim().split(SPLIT_OPERATOR));
		stringRange.stream().forEach(String::trim);
		stringRange = stringRange.stream().filter(s -> !s.isEmpty()).toList();
		if (stringRange.isEmpty())
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + DOES_NOT_REPRESENT_ANY_VALUE);
		if (stringRange.size() > 2)
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + " has more than 2 (min / max) values");
		if (stringRange.size() == 1) {
			if (minVal == null && maxVal == null) throw new GSIllegalRangedData(
					"for implicit bounded values, either min or max value in argument must be set to a concret value !");
			if (maxVal == null) {
				stringRange.add(0, String.valueOf(minVal));
			} else if (minVal == null) { stringRange.add(String.valueOf(maxVal)); }
			if (minVal != null && maxVal != null
					&& Double.valueOf(stringRange.get(0)) - minVal <= maxVal - Double.valueOf(stringRange.get(0))) {
				stringRange.add(0, String.valueOf(minVal));
			} else {
				stringRange.add(String.valueOf(maxVal));
			}
		}
		for (String i : stringRange) { list.add(Double.valueOf(i)); }
		return list;
	}

	/**
	 * Parses int range values from string representation. There is no need for specifying <br/>
	 * any delimiter, although the method rely on proper {@link Integer} values string encoding. <br/>
	 * If null value is true delimiter can't be the null "-" symbol
	 *
	 * @param range
	 * @return {@link List} of min and max integer values based on {@code range} string representation
	 * @throws GSIllegalRangedData
	 */
	public List<Integer> getRangedIntegerData(final String range, final NumMatcher numMatcher)
			throws GSIllegalRangedData {
		List<Integer> list = new ArrayList<>();
		List<String> stringRange = this.getNumbers(range, numMatcher.getMatch());
		stringRange.stream().forEach(String::trim);
		stringRange = stringRange.stream().filter(s -> !s.isEmpty()).toList();
		if (stringRange.isEmpty())
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + DOES_NOT_REPRESENT_ANY_VALUE);
		if (stringRange.size() > 2)
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + " has more than 2 (lower / upper) values");
		for (String i : stringRange) { list.add(Integer.valueOf(i)); }
		return list;
	}

	/**
	 * {@link #getRangedIntegerData(String, boolean)} for specification. Also this method allow for {@code minVal} <br/>
	 * {@code maxVal} forced value: this is intended to encode ranged value from "min implicit integer value" (e.g. age
	 * = 0) <br/>
	 * to ranged parsed integer value or from ranged parsed to "max implicit integer value"
	 *
	 * @param range
	 * @param nullValue
	 * @param minVal
	 * @return {@link List} of min and max values
	 * @throws GSIllegalRangedData
	 */

	/**
	 * Gets the ranged data.
	 *
	 * @param range
	 *            the range
	 * @param numMatcher
	 *            the num matcher
	 * @param minVal
	 *            the min val
	 * @param maxVal
	 *            the max val
	 * @deprecated
	 * @return the ranged data
	 * @throws GSIllegalRangedData
	 *             the GS illegal ranged data
	 */
	@Deprecated (
			forRemoval = true)
	public List<Integer> getRangedData(String range, final NumMatcher numMatcher, final Integer minVal,
			final Integer maxVal) throws GSIllegalRangedData {
		List<Integer> list = new ArrayList<>();
		range = range.replaceAll(numMatcher.getMatch(), SPLIT_OPERATOR);
		List<String> stringRange = Arrays.asList(range.trim().split(SPLIT_OPERATOR));
		stringRange.stream().forEach(String::trim);
		stringRange = stringRange.stream().filter(s -> !s.isEmpty()).toList();
		if (stringRange.isEmpty())
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + DOES_NOT_REPRESENT_ANY_VALUE);
		if (stringRange.size() > 2)
			throw new GSIllegalRangedData(THE_STRING_RANGED_DATA + range + " has more than 2 (min / max) values");
		if (stringRange.size() == 1) {
			if (minVal == null && maxVal == null) throw new GSIllegalRangedData(
					"for implicit bounded values, either min or max value in argument must be set to a concret value !");
			if (maxVal == null || minVal != null
					&& Integer.valueOf(stringRange.get(0)) - minVal <= maxVal - Integer.valueOf(stringRange.get(0))) {
				stringRange.add(0, String.valueOf(minVal));
			} else {
				stringRange.add(String.valueOf(maxVal));
			}
		}
		for (String i : stringRange) { list.add(Integer.valueOf(i)); }
		return list;
	}

	/**
	 * Parse a {@link String} that represents a double value either with ',' or '.' <br/>
	 * decimal value separator given the {@link Locale#getDefault()} category
	 *
	 * @see http://stackoverflow.com/questions/4323599/best-way-to-parsedouble-with-comma-as-decimal-separator
	 *
	 * @param value
	 * @return double value
	 */
	public Double getDouble(final String value) {
		if (value == null || value.isEmpty()) throw new NumberFormatException(value);

		try {
			return Double.valueOf(value);
		} catch (NumberFormatException e) {
			Locale theLocale = Locale.getDefault();
			NumberFormat numberFormat = NumberFormat.getInstance(theLocale);
			try {
				return numberFormat.parse(value).doubleValue();
			} catch (ParseException e1) {
				String valueWithDot = value.replace(',', '.');
				return Double.valueOf(valueWithDot);
			}
		}
	}

	/**
	 * Parse a {@link String} and retrieve any numerical match independently
	 *
	 * @param trim
	 * @return
	 */
	public List<String> getNumber(final String string) {
		return this.getNumbers(string, NumMatcher.getDefault().getMatch());
	}

	/**
	 * Parse a {@link String} and retrieves numerical values
	 *
	 * @param string
	 * @param matcher
	 * @return
	 */
	public List<Number> getNumbers(final String string, final NumMatcher matcher) {
		return this.getNumbers(string, matcher.getMatch()).stream().map(this::parseNumbers).toList();
	}

	/**
	 * Parse a string to return a Number either double or integer
	 *
	 * @param stringVal
	 * @return
	 */
	public Number parseNumbers(final String stringVal) {
		return switch (this.getValueType(stringVal)) {
			case Continue -> Double.valueOf(getNumbers(stringVal, NumMatcher.DOUBLE_MATCH_ENG.getMatch()).get(0));
			case Integer -> Integer.valueOf(getNumbers(stringVal, NumMatcher.INT_MATCH.getMatch()).get(0));
			default -> Double.NaN;
		};
	}

	/**
	 * Gets the numbers.
	 *
	 * @param string
	 *            the string
	 * @param match
	 *            the match
	 * @return the numbers
	 */
	/*
	 *
	 */
	private List<String> getNumbers(final String string, final String match) {
		List<String> numbers = new ArrayList<>();
		Pattern p = Pattern.compile(match);
		Matcher m = p.matcher(string);
		while (m.find()) { numbers.add(m.group()); }
		return numbers;
	}

}
