package msi.gaml.operators;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;

public class Dates {

	static Pattern model_pattern = Pattern.compile("%[YMNDEhmsz]");

	public static HashMap<String, DateTimeFormatter> FORMATTERS = new HashMap<String, DateTimeFormatter>() {
		{
			put("ISO_LOCAL_DATE", DateTimeFormatter.ISO_LOCAL_DATE);
			put("ISO_LOCAL_DATE_TIME", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			put("ISO_LOCAL_TIME", DateTimeFormatter.ISO_LOCAL_TIME);
			put("ISO_OFFSET_DATE_TIME", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			put("ISO_ZONED_DATE_TIME", DateTimeFormatter.ISO_ZONED_DATE_TIME);
		}
	};

	@operator(value = { IKeyword.MINUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(see = "milliseconds_between", usages = @usage(value = "if both operands are dates, returns the duration in seconds between  date2 and date1. To obtain a more precise duration, in milliseconds, use milliseconds_between(date1, date2)", examples = {
			@example(value = "date1 - date2", equals = "598") }))
	public static double minusDate(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		final Duration duration = Duration.between(date2, date1);
		return duration.getSeconds();

	}

	@operator(value = { IKeyword.PLUS, "plus_seconds", "add_seconds" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(usages = @usage(value = "if one of the operands is a date and the other a number, returns a date corresponding to the date plus the given number as duration (in seconds)", examples = {
			@example(value = "date1 + 200") }))
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final int duration)
			throws GamaRuntimeException {
		return date1.plus(duration, SECONDS);
	}

	@operator(value = { IKeyword.PLUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.TIME, IConcept.DATE })
	@doc("Add a duration to a date. The duration is expressed to be in seconds (so that adding 0.5, for instance, will add 500ms)")
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final double duration)
			throws GamaRuntimeException {
		return date1.plus(duration * 1000, ChronoUnit.MILLIS);
	}

	@operator(value = { IKeyword.MINUS, "minus_seconds", "subtract_seconds" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = {})
	@doc(usages = @usage(value = "if one of the operands is a date and the other a number, returns a date corresponding to the date minus the given number as duration (in seconds)", examples = {
			@example(value = "date1 - 200") }))
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final int duration)
			throws GamaRuntimeException {
		return date1.plus(-duration, SECONDS);
	}

	@operator(value = { IKeyword.MINUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.TIME, IConcept.DATE })
	@doc("Removes a duration from a date. The duration is expected to be in seconds (so that removing 0.5, for instance, will add 500ms) ")
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final double duration)
			throws GamaRuntimeException {
		return date1.plus(-duration * 1000, ChronoUnit.MILLIS);
	}

	@operator(value = { IKeyword.PLUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {})
	@doc(value = "returns the resulting string from the addition of a date and a string")
	public static String ConcatainDate(final IScope scope, final GamaDate date1, final String text)
			throws GamaRuntimeException {
		return date1.toString() + text;
	}

	@operator(value = { "plus_years", "add_years" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of years to a date", examples = { @example(value = "date1 plus_years 3") })
	public static GamaDate addYears(final IScope scope, final GamaDate date1, final int nbYears)
			throws GamaRuntimeException {

		return date1.plus(nbYears, YEARS);

	}

	@operator(value = { "plus_months", "add_months" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of months to a date", examples = { @example(value = "date1 plus_months 5") })
	public static GamaDate addMonths(final IScope scope, final GamaDate date1, final int nbMonths)
			throws GamaRuntimeException {

		return date1.plus(nbMonths, MONTHS);

	}

	@operator(value = { "plus_weeks", "add_weeks" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of weeks to a date", examples = { @example(value = "date1 plus_weeks 15") })
	public static GamaDate addWeeks(final IScope scope, final GamaDate date1, final int nbWeeks)
			throws GamaRuntimeException {
		return date1.plus(nbWeeks, WEEKS);

	}

	@operator(value = { "plus_days", "add_days" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of days to a date", examples = { @example(value = "date1 plus_days 20") })
	public static GamaDate addDays(final IScope scope, final GamaDate date1, final int nbDays)
			throws GamaRuntimeException {
		return date1.plus(nbDays, DAYS);

	}

	@operator(value = { "plus_hours", "add_hours" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of hours to a date", examples = {
			@example(value = "date1 plus_hours 15 // equivalent to date1 + 15 #h") })
	public static GamaDate addHours(final IScope scope, final GamaDate date1, final int nbHours)
			throws GamaRuntimeException {
		return date1.plus(nbHours, HOURS);

	}

	@operator(value = { "plus_minutes", "add_minutes" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of minutes to a date", examples = {
			@example(value = "date1 plus_minutes 5 // equivalent to date1 + 5 #mn") })
	public static GamaDate addMinutes(final IScope scope, final GamaDate date1, final int nbMinutes)
			throws GamaRuntimeException {
		return date1.plus(nbMinutes, MINUTES);

	}

	@operator(value = { "minus_years", "subtract_years" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of year from a date", examples = { @example(value = "date1 minus_years 3") })
	public static GamaDate subtractYears(final IScope scope, final GamaDate date1, final int nbYears)
			throws GamaRuntimeException {
		return date1.plus(-nbYears, YEARS);

	}

	@operator(value = { "minus_months", "subtract_months" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of months from a date", examples = {
			@example(value = "date1 minus_months 5") })
	public static GamaDate subtractMonths(final IScope scope, final GamaDate date1, final int nbMonths)
			throws GamaRuntimeException {
		return date1.plus(-nbMonths, MONTHS);

	}

	@operator(value = { "minus_weeks", "subtract_weeks" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of weeks from a date", examples = {
			@example(value = "date1 minus_weeks 15") })
	public static GamaDate subtractWeeks(final IScope scope, final GamaDate date1, final int nbWeeks)
			throws GamaRuntimeException {
		return date1.plus(-nbWeeks, WEEKS);

	}

	@operator(value = { "minus_days", "subtract_days" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of days from a date", examples = { @example(value = "date1 minus_days 20") })
	public static GamaDate subtractDays(final IScope scope, final GamaDate date1, final int nbDays)
			throws GamaRuntimeException {
		return date1.plus(-nbDays, DAYS);

	}

	@operator(value = { "minus_hours", "subtract_hours" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Remove a given number of hours from a date", examples = {
			@example(value = "date1 minus_hours 15 // equivalent to date1 - 15 #h") })
	public static GamaDate subtractHours(final IScope scope, final GamaDate date1, final int nbHours)
			throws GamaRuntimeException {
		return date1.plus(-nbHours, HOURS);

	}

	@operator(value = { "minus_ms", "subtract_ms" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Remove a given number of milliseconds from a date", examples = {
			@example(value = "date1 minus_ms 15 // equivalent to date1 - 15 #ms") })
	public static GamaDate subtractMs(final IScope scope, final GamaDate date1, final int nbMs)
			throws GamaRuntimeException {
		return date1.plus(-nbMs, ChronoUnit.MILLIS);
	}

	@operator(value = { "plus_ms", "add_ms" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of milliseconds to a date", examples = {
			@example(value = "date1 plus_ms 15 // equivalent to date1 + 15 #ms") })
	public static GamaDate addMs(final IScope scope, final GamaDate date1, final int nbMs) throws GamaRuntimeException {
		return date1.plus(nbMs, ChronoUnit.MILLIS);
	}

	@operator(value = { "minus_minutes", "subtract_minutes" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of minutes from a date", examples = {
			@example(value = "date1 minus_minutes 5 // equivalent to date1 - 5#mn") })
	public static GamaDate subtractMinutes(final IScope scope, final GamaDate date1, final int nbMinutes)
			throws GamaRuntimeException {
		return date1.plus(-nbMinutes, MINUTES);

	}

	@operator(value = { "years_between" }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(value = "Provide the exact number of years between two dates. This number can be positive or negative (if the second operand is smaller than the first one)", examples = {
			@example(value = "years_between(d1, d2) -: 10 ") })
	public static int years_between(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return (int) ChronoUnit.YEARS.between(date1, date2);
	}

	@operator(value = { "milliseconds_between" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Provide the exact number of milliseconds between two dates. This number can be positive or negative (if the second operand is smaller than the first one)", examples = {
			@example(value = "milliseconds_between(d1, d2) -: 10 ") })
	public static double milliseconds_between(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return ChronoUnit.MILLIS.between(date1, date2);
	}

	@operator(value = { "months_between" }, content_type = IType.NONE, category = {
			IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Provide the exact number of months between two dates. This number can be positive or negative (if the second operand is smaller than the first one)", examples = {
			@example(value = "months_between(d1, d2) -: 10 ") })
	public static int months_between(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return (int) ChronoUnit.MONTHS.between(date1, date2);
	}

	@operator(value = { ">" }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(value = "Returns true if the first date is strictly greater than the second one", examples = {
			@example(value = "#now > #now minus_hours 1 :- true") })
	public static boolean greater_than(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isGreaterThan(date2, true);
	}

	@operator(value = { ">=" }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(value = "Returns true if the first date is greater than or equal to the second one", examples = {
			@example(value = "#now >= #now minus_hours 1 :- true") })
	public static boolean greater_than_or_equal(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isGreaterThan(date2, false);
	}

	@operator(value = { "<" }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(value = "Returns true if the first date is strictly smaller than the second one", examples = {
			@example(value = "#now < #now minus_hours 1 :- false") })
	public static boolean smaller_than(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isSmallerThan(date2, true);
	}

	@operator(value = { "<=" }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(value = "Returns true if the first date is smaller than or equal to the second one", examples = {
			@example(value = "#now <= #now minus_hours 1 :- false") })
	public static boolean smaller_than_or_equal(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isSmallerThan(date2, true);
	}

	@operator(value = { "=" }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(value = "Returns true if the two dates are equal (i.e.they represent the same instant in time)", examples = {
			@example(value = "#now = #now minus_hours 1 :- false") })
	public static boolean equal(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.equals(date2);
	}

	@operator(value = { "!=" }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {
			IConcept.DATE })
	@doc(value = "Returns true if the two dates are different  (i.e.they do not represent the same instant in time)", examples = {
			@example(value = "#now != #now minus_hours 1 :- true") })
	public static boolean different(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return !date1.equals(date2);
	}

	public static DateTimeFormatter getFormatter(final String pattern) {
		final DateTimeFormatter formatter = FORMATTERS.get(pattern);
		if (formatter != null)
			return formatter;
		if (!pattern.contains("%")) {
			try {
				final DateTimeFormatter result = DateTimeFormatter.ofPattern(pattern);
				FORMATTERS.put(pattern, result);
				return result;
			} catch (final IllegalArgumentException e) {
				GAMA.reportAndThrowIfNeeded(GAMA.getRuntimeScope(),
						GamaRuntimeException.create(e, GAMA.getRuntimeScope()), false);
				return DateTimeFormatter.ISO_DATE_TIME;
			}
		}
		final DateTimeFormatterBuilder df = new DateTimeFormatterBuilder();
		final List<String> dateList = new ArrayList<>();
		final Matcher m = model_pattern.matcher(pattern);
		int i = 0;
		while (m.find()) {
			final String tmp = m.group();
			if (i != m.start()) {
				dateList.add(pattern.substring(i, m.start()));
			}
			dateList.add(tmp);
			i = m.end();
		}
		if (i != pattern.length()) {
			dateList.add(pattern.substring(i));
		}
		for (i = 0; i < dateList.size(); i++) {
			final String s = dateList.get(i);
			if (s.charAt(0) == '%' && s.length() == 2) {
				final Character c = s.charAt(1);
				switch (c) {
				case 'Y':
					df.appendValue(YEAR, 4);
					break;
				case 'M':
					df.appendValue(MONTH_OF_YEAR, 2);
					break;
				case 'N':
					df.appendText(MONTH_OF_YEAR);
					break;
				case 'D':
					df.appendValue(DAY_OF_MONTH, 2);
					break;
				case 'E':
					df.appendText(DAY_OF_WEEK);
					break;
				case 'h':
					df.appendValue(HOUR_OF_DAY, 2);
					break;
				case 'm':
					df.appendValue(MINUTE_OF_HOUR, 2);
					break;
				case 's':
					df.appendValue(SECOND_OF_MINUTE, 2);
					break;
				case 'z':
					df.appendZoneOrOffsetId();
					break;
				default:
					df.appendLiteral(s);
				}
			} else {
				df.appendLiteral(s);
			}
		}
		final DateTimeFormatter result = df.toFormatter();
		FORMATTERS.put(pattern, result);
		return result;
	}

	public static String asDuration(final Temporal d1, final Temporal d2) {
		final Duration p = Duration.between(d1, d2);
		return DurationFormatter.format(p);
	}

	@operator(value = "date", can_be_const = true, category = { IOperatorCategory.STRING,
			IOperatorCategory.TIME }, concept = { IConcept.STRING, IConcept.CAST, IConcept.TIME })
	@doc(value = "converts a string to a date following a custom pattern. The pattern can use \"%Y %M %N %D %E %h %m %s %z\" for outputting years, months, name of month, days, name of days, hours, minutes, seconds and the time-zone. A null or empty pattern will parse the date using one of the ISO date & time formats (similar to date('...') in that case). The pattern can also follow the pattern definition found here, which gives much more control over what will be parsed: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns ", usages = @usage(value = "", examples = @example(value = "date(\"1999-12-30\", 'yyyy-MM-dd')")))
	public static GamaDate date(final IScope scope, final String value, final String pattern) {
		return new GamaDate(scope, value, pattern);
	}

	@operator(value = "string", can_be_const = true, category = { IOperatorCategory.STRING,
			IOperatorCategory.TIME }, concept = { IConcept.STRING, IConcept.CAST, IConcept.TIME })
	@doc(value = "converts a date to astring following a custom pattern. The pattern can use \"%Y %M %N %D %E %h %m %s %z\" for outputting years, months, name of month, days, name of days, hours, minutes, seconds and the time-zone. A null or empty pattern will return the complete date as defined by the ISO date & time format. The pattern can also follow the pattern definition found here, which gives much more control over the format of the date: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns ", usages = @usage(value = "", examples = @example(value = "format(#now, 'yyyy-MM-dd')")))
	public static String format(final GamaDate time, final String pattern) {
		return time.toString(pattern);
	}

	private static class DurationFormatter implements TemporalAccessor {
		private static final DateTimeFormatter YMDHMS = DateTimeFormatter.ofPattern("y'y' M'm' d'd' HH:mm:ss");
		private static final DateTimeFormatter MDHMS = DateTimeFormatter.ofPattern("M' months' d 'days' HH:mm:ss");
		private static final DateTimeFormatter M1DHMS = DateTimeFormatter.ofPattern("M' month' d 'days' HH:mm:ss");
		private static final DateTimeFormatter M1D1HMS = DateTimeFormatter.ofPattern("M' month' d 'day' HH:mm:ss");
		private static final DateTimeFormatter DHMS = DateTimeFormatter.ofPattern("d 'days' HH:mm:ss");
		private static final DateTimeFormatter D1HMS = DateTimeFormatter.ofPattern("d 'day' HH:mm:ss");
		private static final DateTimeFormatter HMS = DateTimeFormatter.ofPattern("HH:mm:ss");

		static String format(final Duration duration) {
			return new DurationFormatter(duration).toString();
		}

		private Temporal temporal;
		private final Duration duration;

		private DurationFormatter(final Duration duration) {
			this(duration, null);
		}

		private DurationFormatter(final Duration duration, final DateTimeFormatter df) {
			this.duration = duration;
			this.temporal = duration.addTo(GamaDateType.DEFAULT_STARTING_DATE);
			if (duration.toDays() == 0l)
				temporal = LocalTime.from(temporal);
		}

		private DateTimeFormatter getFormatter() {
			if (isSupported(YEAR))
				return YMDHMS;
			if (isSupported(MONTH_OF_YEAR)) {
				if (getLong(MONTH_OF_YEAR) < 2) {
					if (getLong(DAY_OF_MONTH) < 2) {
						return M1D1HMS;
					} else
						return M1DHMS;
				}
				return MDHMS;
			}
			if (isSupported(DAY_OF_MONTH))
				if (getLong(DAY_OF_MONTH) < 2)
					return D1HMS;
				else
					return DHMS;
			return HMS;
		}

		@Override
		public boolean isSupported(final TemporalField field) {
			if (!temporal.isSupported(field))
				return false;
			final long value = temporal.getLong(field) - GamaDateType.DEFAULT_STARTING_DATE.getLong(field);
			return value != 0l;
		}

		@Override
		public long getLong(final TemporalField field) {
			return temporal.getLong(field) - GamaDateType.DEFAULT_STARTING_DATE.getLong(field);
		}

		@Override
		public String toString() {
			return getFormatter().format(this);
		}

	}

}
