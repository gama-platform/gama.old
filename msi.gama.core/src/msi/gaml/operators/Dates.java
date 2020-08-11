/*******************************************************************************************************
 *
 * msi.gaml.operators.Dates.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaDateInterval;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.TimeUnitConstantExpression;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;

public class Dates {

	public static final String ISO_LOCAL_KEY = "ISO_LOCAL_DATE_TIME";
	public static final String ISO_OFFSET_KEY = "ISO_OFFSET_DATE_TIME";
	public static final String ISO_ZONED_KEY = "ISO_ZONED_DATE_TIME";
	public static final String ISO_SIMPLE_KEY = "ISO_SIMPLE";
	public static final String CUSTOM_KEY = "CUSTOM";
	public static String DEFAULT_VALUE = "CUSTOM";
	public static final String DEFAULT_KEY = "DEFAULT";
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String ISO_SIMPLE_FORMAT = "yy-MM-dd HH:mm:ss";
	static final DurationFormatter DURATION_FORMATTER = new DurationFormatter();

	public static HashMap<String, DateTimeFormatter> FORMATTERS = new HashMap<String, DateTimeFormatter>() {
		{
			put(DEFAULT_KEY, DateTimeFormatter.ofPattern(DEFAULT_FORMAT));
			put(ISO_SIMPLE_KEY, DateTimeFormatter.ofPattern(ISO_SIMPLE_FORMAT));
			put(ISO_LOCAL_KEY, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			put(ISO_OFFSET_KEY, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			put(ISO_ZONED_KEY, DateTimeFormatter.ISO_ZONED_DATE_TIME);

		}
	};

	public final static Pref<String> DATES_CUSTOM_FORMATTER = GamaPreferences.create("pref_date_custom_formatter",
			"Custom date pattern (https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)",
			DEFAULT_FORMAT, IType.STRING, true).in(GamaPreferences.External.NAME, GamaPreferences.External.DATES)
			.onChange((e) -> {
				try {
					FORMATTERS.put(CUSTOM_KEY, getFormatter(StringUtils.toJavaString(e), null));
					if (DEFAULT_VALUE.equals(CUSTOM_KEY)) {
						FORMATTERS.put(DEFAULT_KEY, FORMATTERS.get(CUSTOM_KEY));
					}
				} catch (final Exception ex) {
					DEBUG.ERR("Formatter not valid: " + e);
				}
			});

	public final static Pref<String> DATES_DEFAULT_FORMATTER = GamaPreferences
			.create("pref_date_default_formatter", "Default date pattern for writing dates (i.e. string(date1))",
					CUSTOM_KEY, IType.STRING, true)
			.in(GamaPreferences.External.NAME, GamaPreferences.External.DATES)
			.among(ISO_LOCAL_KEY, ISO_OFFSET_KEY, ISO_ZONED_KEY, ISO_SIMPLE_KEY, CUSTOM_KEY).onChange((e) -> {
				DEFAULT_VALUE = e;
				FORMATTERS.put(DEFAULT_KEY, FORMATTERS.get(e));
			});

	public final static Pref<GamaDate> DATES_STARTING_DATE = GamaPreferences
			.create("pref_date_starting_date", "Default starting date of models", GamaDateType.EPOCH, IType.DATE, true)
			.in(GamaPreferences.External.NAME, GamaPreferences.External.DATES);

	public final static Pref<Double> DATES_TIME_STEP =
			GamaPreferences.create("pref_date_time_step", "Default time step of models", 1d, IType.FLOAT, true)
					.in(GamaPreferences.External.NAME, GamaPreferences.External.DATES).between(1d, null);

	static {
		FORMATTERS.put(CUSTOM_KEY, DateTimeFormatter.ofPattern(DATES_CUSTOM_FORMATTER.getValue()));
		FORMATTERS.put(DEFAULT_KEY, FORMATTERS.get(CUSTOM_KEY));
	}

	public static final String APPROXIMATE_TEMPORAL_QUERY = IKeyword.INTERNAL_FUNCTION + "_temporal_query";

	static Pattern model_pattern = Pattern.compile("%[YMNDEhmsz]");

	public static void initialize() {
		// Only here to load the class and its preferences

	}

	@operator (
			value = APPROXIMATE_TEMPORAL_QUERY,
			doc = @doc ("For internal use only"),
			internal = true)
	@no_test
	public static double approximalQuery(final IScope scope, final IExpression left, final IExpression right) {
		final Double arg = Cast.asFloat(scope, left.value(scope));
		if (right instanceof TimeUnitConstantExpression) {
			return scope.getClock().getCurrentDate().getDuration(scope, (TimeUnitConstantExpression) right, arg);
		} else {
			return 0d;
		}

	}

	@operator (
			value = { IKeyword.MINUS },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			see = "milliseconds_between",
			usages = @usage (
					value = "if both operands are dates, returns the duration in seconds between date2 and date1. To obtain a more precise duration, in milliseconds, use milliseconds_between(date1, date2)",
					examples = { @example (
							value = "date('2000-01-02') - date('2000-01-01')",
							equals = "86400") }))
	@test ("date('2000-01-02') - date('2000-01-01') = 86400")

	public static double minusDate(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		final Duration duration = Duration.between(date2, date1);
		return duration.getSeconds();
	}

	@operator (
			value = { "every", "every_cycle" },
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM, IConcept.CYCLE })
	@doc (
			value = "true every operand * cycle, false otherwise",
			comment = "the value of the every operator depends on the cycle. It can be used to do something every x cycle.",
			examples = { @example (
					value = "if every(2#cycle) {write \"the cycle number is even\";}",
					test = false),
					@example (
							value = "	     else {write \"the cycle number is odd\";}",
							test = false) })
	@no_test
	public static Boolean every(final IScope scope, final Integer period) {
		final int time = scope.getClock().getCycle();
		return period > 0 && (time == 0 || time >= period) && time % period == 0;
	}

	@operator (
			value = "every",
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE, IConcept.CYCLE })
	@doc (
			see = { "since", "after" },
			value = "expects a frequency (expressed in seconds of simulated time) as argument. Will return true every time the current_date matches with this frequency",
			comment = "Used to do something at regular intervals of time. Can be used in conjunction with 'since', 'after', 'before', 'until' or 'between', so that this computation only takes place in the temporal segment defined by these operators. In all cases, the starting_date of the model is used as a reference starting point",
			examples = { @example (
					value = "reflex when: every(2#days) since date('2000-01-01') { .. }",
					isExecutable = false),
					@example (
							value = "state a { transition to: b when: every(2#mn);} state b { transition to: a when: every(30#s);} // This oscillatory behavior will use the starting_date of the model as its starting point in time",
							isExecutable = false) })
	@no_test
	public static Boolean every(final IScope scope, final IExpression period) {
		return scope.getClock().getStartingDate().isIntervalReached(scope, period);
	}

	@operator (
			value = "every",
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE, IConcept.CYCLE })
	@doc (
			see = { "to" },
			value = "applies a step to an interval of dates defined by 'date1 to date2'",
			comment = "",
			examples = { @example (
					value = "(date('2000-01-01') to date('2010-01-01')) every (#month) // builds an interval between these two dates which contains all the monthly dates starting from the beginning of the interval",
					isExecutable = false) })
	@test("list((date('2001-01-01') to date('2001-1-02')) every(#day)) collect each = [date ('2001-01-01 00:00:00')]")
	public static IList<GamaDate> every(final IScope scope, final GamaDateInterval interval, final IExpression period) {
		return interval.step(Cast.asFloat(scope, period.value(scope)));
	}

	@operator (
			value = "to",
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE, IConcept.CYCLE })
	@doc (
			see = { "every" },
			value = "builds an interval between two dates (the first inclusive and the second exclusive, which behaves like a read-only list of dates. The default step between two dates is the step of the model",
			comment = "The default step can be overruled by using the every operator applied to this interval",
			examples = { @example (
					value = "date('2000-01-01') to date('2010-01-01') // builds an interval between these two dates",
					isExecutable = false),
					@example (
							value = "(date('2000-01-01') to date('2010-01-01')) every (#month) // builds an interval between these two dates which contains all the monthly dates starting from the beginning of the interval",
							isExecutable = false) })
	@test("list((date('2001-01-01') to date('2001-4-01')) every(#month)) collect each =\n" + 
			"		[date ('2001-01-01 00:00:00'),date ('2001-01-31 00:00:00'),date ('2001-03-02 00:00:00')]")
	public static IList<GamaDate> to(final IScope scope, final GamaDate start, final GamaDate end) {
		return GamaDateInterval.of(start, end);
	}

	@operator (
			value = { "since", "from" },
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the current_date of the model is after (or equal to) the date passed in argument. Synonym of 'current_date >= argument'. Can be used, like 'after', in its composed form with 2 arguments to express the lowest boundary of the computation of a frequency. However, contrary to 'after', there is a subtle difference: the lowest boundary will be tested against the frequency as well ",
			examples = { @example (
					value = "reflex when: since(starting_date) {}  	// this reflex will always be run",
					isExecutable = false),
					@example (
							value = "every(2#days) since (starting_date + 1#day) // the computation will return true 1 day after the starting date and every two days after this reference date",
							isExecutable = false) })
	@test("starting_date <- date([2019,5,9]);since(date([2019,5,10])) = false")
	@test("starting_date <- date([2019,5,9]);since(date([2019,5,9])) = true")
	@test("starting_date <- date([2019,5,9]);since(date([2019,5,8])) = true")
	public static boolean since(final IScope scope, final GamaDate date) {
		return scope.getSimulation().getCurrentDate().isGreaterThan(date, false);
	}

	@operator (
			value = { "after" },
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the current_date of the model is strictly after the date passed in argument. Synonym of 'current_date > argument'. Can be used in its composed form with 2 arguments to express the lower boundary for the computation of a frequency. Note that only dates strictly after this one will be tested against the frequency",
			examples = { @example (
					value = "reflex when: after(starting_date) {} 	// this reflex will always be run after the first step",
					isExecutable = false),
					@example (
							value = "reflex when: false after(starting date + #10days) {} 	// This reflex will not be run after this date. Better to use 'until' or 'before' in that case",
							isExecutable = false),
					@example (
							value = "every(2#days) after (starting_date + 1#day) 	// the computation will return true every two days (using the starting_date of the model as the starting point) only for the dates strictly after this starting_date + 1#day",
							isExecutable = false) })
	@test("starting_date <- date([2019,5,9]);after(date([2019,5,10])) = false")
	@test("starting_date <- date([2019,5,9]);after(date([2019,5,9])) = false")
	@test("starting_date <- date([2019,5,9]);after(date([2019,5,8])) = true")
	public static boolean after(final IScope scope, final GamaDate date) {
		return scope.getSimulation().getCurrentDate().isGreaterThan(date, true);
	}

	@operator (
			value = { "before" },
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the current_date of the model is strictly before the date passed in argument. Synonym of 'current_date < argument'",
			examples = { @example (
					value = "reflex when: before(starting_date) {} 	// this reflex will never be run",
					isExecutable = false) })
	@test("starting_date <- date([2019,5,9]);before(date([2019,5,10])) = true")
	@test("starting_date <- date([2019,5,9]);before(date([2019,5,9])) = false")
	@test("starting_date <- date([2019,5,9]);before(date([2019,5,8])) = false")
	public static boolean before(final IScope scope, final GamaDate date) {
		return scope.getSimulation().getCurrentDate().isSmallerThan(date, true);
	}

	@operator (
			value = { "until", "to" },
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the current_date of the model is before (or equel to) the date passed in argument. Synonym of 'current_date <= argument'",
			examples = { @example (
					value = "reflex when: until(starting_date) {} 	// This reflex will be run only once at the beginning of the simulation",
					isExecutable = false) })
	@test("starting_date <- date([2019,5,9]);until(date([2019,5,10])) = true")
	@test("starting_date <- date([2019,5,9]);until(date([2019,5,9])) = true")
	@test("starting_date <- date([2019,5,9]);until(date([2019,5,8])) = false")
	public static boolean until(final IScope scope, final GamaDate date) {
		return scope.getSimulation().getCurrentDate().isSmallerThan(date, false);
	}

	@operator (
			value = { "since", "from" },
			category = { IOperatorCategory.DATE },
			doc = @doc ("Returns true if the first operand is true and the current date is equal to or after the second operand"),
			concept = { IConcept.DATE })
	@no_test
	public static boolean since(final IScope scope, final IExpression expression, final GamaDate date) {
		return since(scope, date) && Cast.asBool(scope, expression.value(scope));
	}

	@operator (
			value = { "after" },
			doc = @doc ("Returns true if the first operand is true and the current date is situated strictly after the second operand"),
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@no_test
	public static boolean after(final IScope scope, final IExpression expression, final GamaDate date) {
		return after(scope, date) && Cast.asBool(scope, expression.value(scope));
	}

	@operator (
			value = { "before" },
			category = { IOperatorCategory.DATE },
			doc = @doc ("Returns true if the first operand is true and the current date is situated strictly before the second operand"),
			concept = { IConcept.DATE })
	@no_test
	public static boolean before(final IScope scope, final IExpression expression, final GamaDate date) {
		return before(scope, date) && Cast.asBool(scope, expression.value(scope));
	}

	@operator (
			value = { "until", "to" },
			doc = @doc ("Returns true if the first operand is true and the current date is equal to or situated before the second operand"),
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@no_test
	public static boolean until(final IScope scope, final IExpression expression, final GamaDate date) {
		return until(scope, date) && Cast.asBool(scope, expression.value(scope));
	}

	@operator (
			value = { "between" },
			category = { IOperatorCategory.DATE },
			doc = @doc ("Returns true if the first operand is true and the current date is situated strictly after the second operand and before the third one"),
			concept = { IConcept.DATE })
	@no_test
	public static boolean between(final IScope scope, final IExpression expression, final GamaDate start,
			final GamaDate stop) {
		return between(scope, scope.getClock().getCurrentDate(), start, stop) && (boolean) expression.value(scope);
	}

	@operator (
			value = { "between" },
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			usages = @usage (
					value = "returns true if the first operand is between the two dates passed in arguments (both exclusive). Can be combined with 'every' to express a frequency between two dates",
					examples = { @example (
							value = "(date('2016-01-01') between(date('2000-01-01'), date('2020-02-02')))",
							equals = "true"),
							@example (
									value = "// will return true every new day between these two dates, taking the first one as the starting point",
									isExecutable = false),
							@example (
									value = "every(#day between(date('2000-01-01'), date('2020-02-02'))) ",
									isExecutable = false) }))

	public static boolean between(final IScope scope, final GamaDate date, final GamaDate date1, final GamaDate date2) {
		return date.isGreaterThan(date1, true) && date.isSmallerThan(date2, true);
	}

	@operator (
			value = { "between" },
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			usages = @usage (
					value = "With only 2 date operands, it returns true if the current_date is between the 2 date  operands.",
					examples = { @example (
							value = "between(date('2000-01-01'), date('2020-02-02'))",
							equals = "false") }))
	@test("starting_date <- date([2019,5,9]);between((date([2019,5,8])), (date([2019,5,10]))) = true")
	public static boolean between(final IScope scope, final GamaDate date1, final GamaDate date2) {
		return scope.getSimulation().getCurrentDate().isGreaterThan(date1, true)
				&& scope.getSimulation().getCurrentDate().isSmallerThan(date2, true);
	}

	@operator (
			value = { IKeyword.PLUS, "plus_seconds", "add_seconds" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			usages = @usage (
					value = "if one of the operands is a date and the other a number, returns a date corresponding to the date plus the given number as duration (in seconds)",
					examples = { @example (
							value = "date('2000-01-01') + 86400",
							equals = "date('2000-01-02')") }))
	@test ("date('2000-01-01') + 86400 = date('2000-01-02')")
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final int duration)
			throws GamaRuntimeException {
		return date1.plus(duration, SECONDS);
	}

	@operator (
			value = { IKeyword.PLUS },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.TIME, IConcept.DATE })
	@doc (
			value = "Add a duration to a date. The duration is supposed to be in seconds (so that adding 0.5, "
					+ "for instance, will add 500ms)",
			examples = { @example (
					value = "date('2016-01-01 00:00:01') + 86400",
					equals = "date('2016-01-02 00:00:01')"), })
	@test ("date('2016-01-01 00:00:01') + 86400 = date('2016-01-02 00:00:01')")
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final double duration)
			throws GamaRuntimeException {
		return date1.plus(duration * 1000, ChronoUnit.MILLIS);
	}

	@operator (
			value = { IKeyword.MINUS, "minus_seconds", "subtract_seconds" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = {})
	@doc (
			usages = @usage (
					value = "if one of the operands is a date and the other a number, returns a date corresponding to the "
							+ "date minus the given number as duration (in seconds)",
					examples = { @example (
							value = "date('2000-01-01') - 86400",
							equals = "date('1999-12-31')") }))
	@test ("date('2000-01-01') - 86400 = date('1999-12-31')")
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final int duration)
			throws GamaRuntimeException {
		return date1.plus(-duration, SECONDS);
	}

	@operator (
			value = { IKeyword.MINUS },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.TIME, IConcept.DATE })
	@doc (
			value = "Removes a duration from a date. The duration is expected to be in seconds (so that removing 0.5, "
					+ "for instance, will add 500ms) ",
			examples = { @example (
					value = "date('2000-01-01') - 86400",
					equals = "date('1999-12-31')") })
	@test ("date('2000-01-01') - 86400 = date('1999-12-31')")
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final double duration)
			throws GamaRuntimeException {
		return date1.plus(-duration * 1000, ChronoUnit.MILLIS);
	}

	@operator (
			value = { IKeyword.PLUS },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = {})
	@doc (
			value = "returns the resulting string from the addition of a date and a string",
			examples = { @example (
					value = "date('2000-01-01 00:00:00') + '_Test'",
					equals = "'2000-01-01 00:00:00_Test'") })
	@test ("date('2000-01-01 00:00:00') + '_Test' = '2000-01-01 00:00:00_Test'")
	public static String ConcatainDate(final IScope scope, final GamaDate date1, final String text)
			throws GamaRuntimeException {
		return date1.toString() + text;
	}

	@operator (
			value = { "plus_years", "add_years" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Add a given number of years to a date",
			examples = { @example (
					value = "date('2000-01-01') plus_years 15",
					equals = "date('2015-01-01')") })
	@test ("date('2000-01-01') plus_years 15 = date('2015-01-01')")
	public static GamaDate addYears(final IScope scope, final GamaDate date1, final int nbYears)
			throws GamaRuntimeException {

		return date1.plus(nbYears, YEARS);

	}

	@operator (
			value = { "plus_months", "add_months" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Add a given number of months to a date",
			examples = { @example (
					value = "date('2000-01-01') plus_months 5",
					equals = "date('2000-06-01')") })
	@test ("date('2000-01-01') plus_months 5 = date('2000-06-01')")
	public static GamaDate addMonths(final IScope scope, final GamaDate date1, final int nbMonths)
			throws GamaRuntimeException {

		return date1.plus(nbMonths, MONTHS);

	}

	@operator (
			value = { "plus_weeks", "add_weeks" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Add a given number of weeks to a date",
			examples = { @example (
					value = "date('2000-01-01') plus_weeks 15",
					equals = "date('2000-04-15')") })
	@test ("is_error(date('2000-15-01'))")
	public static GamaDate addWeeks(final IScope scope, final GamaDate date1, final int nbWeeks)
			throws GamaRuntimeException {
		return date1.plus(nbWeeks, WEEKS);

	}

	@operator (
			value = { "plus_days", "add_days" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Add a given number of days to a date",
			examples = { @example (
					value = "date('2000-01-01') plus_days 12",
					equals = "date('2000-01-13')") })
	@test ("date('2000-01-01') plus_days 12 = date('2000-01-13')")
	public static GamaDate addDays(final IScope scope, final GamaDate date1, final int nbDays)
			throws GamaRuntimeException {
		return date1.plus(nbDays, DAYS);

	}

	@operator (
			value = { "plus_hours", "add_hours" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Add a given number of hours to a date",
			examples = { @example (
					value = "// equivalent to date1 + 15 #h",
					test = false),
					@example (
							value = "date('2000-01-01') plus_hours 24",
							equals = "date('2000-01-02')") })
	@test ("date('2000-01-01') plus_hours 24  = date('2000-01-02')")
	public static GamaDate addHours(final IScope scope, final GamaDate date1, final int nbHours)
			throws GamaRuntimeException {
		return date1.plus(nbHours, HOURS);

	}

	@operator (
			value = { "plus_minutes", "add_minutes" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Add a given number of minutes to a date",
			examples = { @example (
					value = "// equivalent to date1 + 5 #mn",
					test = false),
					@example (
							value = "date('2000-01-01') plus_minutes 5 ",
							equals = "date('2000-01-01 00:05:00')") })
	@test ("date('2000-01-01') plus_minutes 5  = date('2000-01-01 00:05:00')")
	public static GamaDate addMinutes(final IScope scope, final GamaDate date1, final int nbMinutes)
			throws GamaRuntimeException {
		return date1.plus(nbMinutes, MINUTES);

	}

	@operator (
			value = { "minus_years", "subtract_years" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Subtract a given number of year from a date",
			examples = { @example (
					value = "date('2000-01-01') minus_years 3",
					equals = "date('1997-01-01')") })
	@test ("date('2000-01-01') minus_years 3 = date('1997-01-01')")
	public static GamaDate subtractYears(final IScope scope, final GamaDate date1, final int nbYears)
			throws GamaRuntimeException {
		return date1.plus(-nbYears, YEARS);

	}

	@operator (
			value = { "minus_months", "subtract_months" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Subtract a given number of months from a date",
			examples = { @example (
					value = "date('2000-01-01') minus_months 5",
					equals = "date('1999-08-01')") })
	@test ("date('2000-01-01') minus_months 5 = date('1999-08-01')")
	public static GamaDate subtractMonths(final IScope scope, final GamaDate date1, final int nbMonths)
			throws GamaRuntimeException {
		return date1.plus(-nbMonths, MONTHS);

	}

	@operator (
			value = { "minus_weeks", "subtract_weeks" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Subtract a given number of weeks from a date",
			examples = { @example (
					value = "date('2000-01-01') minus_weeks 15",
					equals = "date('1999-09-18')") })
	@test ("date('2000-01-01') minus_weeks 15 = date('1999-09-18')")
	public static GamaDate subtractWeeks(final IScope scope, final GamaDate date1, final int nbWeeks)
			throws GamaRuntimeException {
		return date1.plus(-nbWeeks, WEEKS);

	}

	@operator (
			value = { "minus_days", "subtract_days" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Subtract a given number of days from a date",
			examples = { @example (
					value = "date('2000-01-01') minus_days 20",
					equals = "date('1999-12-12')") })
	@test ("date('2000-01-01') minus_days 20 = date('1999-12-12')")
	public static GamaDate subtractDays(final IScope scope, final GamaDate date1, final int nbDays)
			throws GamaRuntimeException {
		return date1.plus(-nbDays, DAYS);

	}

	@operator (
			value = { "minus_hours", "subtract_hours" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Remove a given number of hours from a date",
			examples = { @example (
					value = "// equivalent to date1 - 15 #h",
					isExecutable = false),
					@example (
							value = "date('2000-01-01') minus_hours 15 ",
							equals = "date('1999-12-31 09:00:00')") })
	@test ("(date('2000-01-01') minus_hours 15)  = date('1999-12-31 09:00:00')")
	public static GamaDate subtractHours(final IScope scope, final GamaDate date1, final int nbHours)
			throws GamaRuntimeException {
		return date1.plus(-nbHours, HOURS);

	}

	@operator (
			value = { "minus_ms", "subtract_ms" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Remove a given number of milliseconds from a date",
			examples = { @example (
					value = "// equivalent to date1 - 15 #ms",
					isExecutable = false),
					@example (
							value = "date('2000-01-01') minus_ms 1000 ",
							equals = "date('1999-12-31 23:59:59')") })
	@test ("date('2000-01-01') minus_ms 1000  = date('1999-12-31 23:59:59')")
	public static GamaDate subtractMs(final IScope scope, final GamaDate date1, final int nbMs)
			throws GamaRuntimeException {
		return date1.plus(-nbMs, ChronoUnit.MILLIS);
	}

	@operator (
			value = { "plus_ms", "add_ms" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Add a given number of milliseconds to a date",
			examples = { @example (
					value = "// equivalent to date('2000-01-01') + 15 #ms",
					isExecutable = false),
					@example (
							value = "date('2000-01-01') plus_ms 1000 ",
							equals = "date('2000-01-01 00:00:01')") })
	@test ("date('2000-01-01') plus_ms 1000  = date('2000-01-01 00:00:01')")
	public static GamaDate addMs(final IScope scope, final GamaDate date1, final int nbMs) throws GamaRuntimeException {
		return date1.plus(nbMs, ChronoUnit.MILLIS);
	}

	@operator (
			value = { "minus_minutes", "subtract_minutes" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Subtract a given number of minutes from a date",
			examples = { @example (
					value = "// date('2000-01-01') to date1 - 5#mn",
					isExecutable = false),
					@example (
							value = "date('2000-01-01') minus_minutes 5 ",
							equals = "date('1999-12-31 23:55:00')") })
	@test ("date('2000-01-01') minus_minutes 5  = date('1999-12-31 23:55:00')")
	public static GamaDate subtractMinutes(final IScope scope, final GamaDate date1, final int nbMinutes)
			throws GamaRuntimeException {
		return date1.plus(-nbMinutes, MINUTES);

	}

	@operator (
			value = { "years_between" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Provide the exact number of years between two dates. This number can be positive or negative (if the second operand is smaller than the first one)",
			examples = { @example (
					value = "years_between(date('2000-01-01'), date('2010-01-01'))",
					equals = "10") })
	@test ("years_between(date('2000-01-01'), date('2010-01-01')) = 10")
	public static int years_between(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return (int) ChronoUnit.YEARS.between(date1, date2);
	}

	@operator (
			value = { "milliseconds_between" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Provide the exact number of milliseconds between two dates. This number can be positive or negative (if the second operand is smaller than the first one)",
			examples = { @example (
					value = "milliseconds_between(date('2000-01-01'), date('2000-02-01'))",
					equals = "2.6784E9") })
	@test ("milliseconds_between(date('2000-01-01'), date('2000-02-01')) = 2.6784E9")
	public static double milliseconds_between(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return ChronoUnit.MILLIS.between(date1, date2);
	}

	@operator (
			value = { "months_between" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Provide the exact number of months between two dates. This number can be positive or negative (if the second operand is smaller than the first one)",
			examples = { @example (
					value = "months_between(date('2000-01-01'), date('2000-02-01'))",
					equals = "1") })
	@test ("months_between(date('2000-01-01'), date('2000-02-01')) = 1")
	public static int months_between(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return (int) ChronoUnit.MONTHS.between(date1, date2);
	}

	@operator (
			value = { ">" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the first date is strictly greater than the second one",
			examples = { @example (
					value = "(#now > (#now minus_hours 1))",
					equals = "true") })
	@test ("(#now > (#now minus_hours 1)) = true")
	public static boolean greater_than(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isGreaterThan(date2, true);
	}

	@operator (
			value = { ">=" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the first date is greater than or equal to the second one",
			examples = { @example (
					value = "#now >= #now minus_hours 1",
					equals = "true") })
	@test ("(#now >= (#now minus_hours 1)) = true")
	public static boolean greater_than_or_equal(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isGreaterThan(date2, false);
	}

	@operator (
			value = { "<" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the first date is strictly smaller than the second one",
			examples = { @example (
					value = "#now < #now minus_hours 1",
					equals = "false") })
	@test ("(#now < (#now minus_hours 1)) = false")
	public static boolean smaller_than(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isSmallerThan(date2, true);
	}

	@operator (
			value = { "<=" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the first date is smaller than or equal to the second one",
			examples = { @example (
					value = "(#now <= (#now minus_hours 1))",
					equals = "false") })
	@test ("(#now <= (#now minus_hours 1)) = false")
	public static boolean smaller_than_or_equal(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.isSmallerThan(date2, true);
	}

	@operator (
			value = { "=" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the two dates are equal (i.e.they represent the same instant in time)",
			examples = { @example (
					value = "#now = #now minus_hours 1",
					equals = "false") })
	@test ("(#now = (#now minus_hours 1)) = false")
	public static boolean equal(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return date1.equals(date2);
	}

	@operator (
			value = { "!=" },
			content_type = IType.NONE,
			category = { IOperatorCategory.DATE },
			concept = { IConcept.DATE })
	@doc (
			value = "Returns true if the two dates are different  (i.e.they do not represent the same instant in time)",
			examples = { @example (
					value = "#now != #now minus_hours 1",
					equals = "true") })
	@test ("(#now != (#now minus_hours 1)) = true")
	public static boolean different(final IScope scope, final GamaDate date1, final GamaDate date2)
			throws GamaRuntimeException {
		return !date1.equals(date2);
	}

	static Locale getLocale(final String l) {
		if (l == null) { return Locale.getDefault(); }
		final String locale = l.toLowerCase();
		switch (locale) {
			case "us":
				return Locale.US;
			case "fr":
				return Locale.FRANCE;
			case "en":
				return Locale.ENGLISH;
			case "de":
				return Locale.GERMAN;
			case "it":
				return Locale.ITALIAN;
			case "jp":
				return Locale.JAPANESE;
			case "uk":
				return Locale.UK;
			default:
				return new Locale(locale);
		}
	}

	static String getFormatterKey(final String p, final String locale) {
		if (locale == null) { return p; }
		return p + locale;
	}

	public static DateTimeFormatter getFormatter(final String p, final String locale) {

		final String pattern = p;
		// Can happen during initialization
		if (FORMATTERS == null || FORMATTERS.isEmpty()) { return DateTimeFormatter.ofPattern(DEFAULT_FORMAT); }
		if (pattern == null) { return FORMATTERS.get(DEFAULT_KEY); }
		final DateTimeFormatter formatter = FORMATTERS.get(getFormatterKey(pattern, locale));
		if (formatter != null) { return formatter; }
		if (!pattern.contains("%")) {
			try {
				final DateTimeFormatterBuilder df = new DateTimeFormatterBuilder();
				final DateTimeFormatter result =
						df.parseCaseInsensitive().appendPattern(pattern).toFormatter(getLocale(locale));
				FORMATTERS.put(getFormatterKey(pattern, locale), result);
				return result;
			} catch (final IllegalArgumentException e) {
				GAMA.reportAndThrowIfNeeded(GAMA.getRuntimeScope(),
						GamaRuntimeException.create(e, GAMA.getRuntimeScope()), false);
				return FORMATTERS.get(DEFAULT_KEY);
			}
		}
		final DateTimeFormatterBuilder df = new DateTimeFormatterBuilder();
		df.parseCaseInsensitive();
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
		final DateTimeFormatter result = df.toFormatter(getLocale(locale));
		FORMATTERS.put(getFormatterKey(pattern, locale), result);
		return result;
	}

	public static String asDuration(final Temporal d1, final Temporal d2) {
		final Duration p = Duration.between(d1, d2);
		return DurationFormatter.format(p);
	}

	@operator (
			value = "date",
			can_be_const = true,
			category = { IOperatorCategory.STRING, IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.CAST, IConcept.TIME })
	@doc (
			value = "converts a string to a date following a custom pattern. The pattern can use \"%Y %M %N %D %E %h %m %s %z\" for outputting years, months, name of month, days, name of days, hours, minutes, seconds and the time-zone. A null or empty pattern will parse the date using one of the ISO date & time formats (similar to date('...') in that case). The pattern can also follow the pattern definition found here, which gives much more control over what will be parsed: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns. Different patterns are available by default as constant: #iso_local, #iso_simple, #iso_offset, #iso_zoned and #custom, which can be changed in the preferences ",
			masterDoc = true,
			usages = @usage (
					value = "",
					examples = @example (
							value = "date den <- date(\"1999-12-30\", 'yyyy-MM-dd');",
							test = false)))
	@no_test
	public static GamaDate date(final IScope scope, final String value, final String pattern) {
		return new GamaDate(scope, value, pattern);
	}

	@operator (
			value = "date",
			can_be_const = true,
			category = { IOperatorCategory.STRING, IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.CAST, IConcept.TIME })
	@doc (
			value = "converts a string to a date following a custom pattern and a specific locale (e.g. 'fr', 'en'...). The pattern can use \"%Y %M %N %D %E %h %m %s %z\" for parsing years, months, name of month, days, name of days, hours, minutes, seconds and the time-zone. A null or empty pattern will parse the date using one of the ISO date & time formats (similar to date('...') in that case). The pattern can also follow the pattern definition found here, which gives much more control over what will be parsed: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns. Different patterns are available by default as constant: #iso_local, #iso_simple, #iso_offset, #iso_zoned and #custom, which can be changed in the preferences ",
			usages = @usage (
					value = "In addition to the date and  pattern string operands, a specific locale (e.g. 'fr', 'en'...) can be added.",
					examples = @example (
							value = "date d <- date(\"1999-january-30\", 'yyyy-MMMM-dd', 'en');",
							test = false)))
	@test("date('1999-01-30', 'yyyy-MM-dd', 'en') = date('1999-01-30 00:00:00')")
	// @test("date('1999-january-30', 'yyyy-MMMM-dd', 'en') = date('1999-01-30 00:00:00')")
	public static GamaDate date(final IScope scope, final String value, final String pattern, final String locale) {
		return new GamaDate(scope, value, pattern, locale);
	}

	@operator (
			value = "string",
			can_be_const = true,
			category = { IOperatorCategory.STRING, IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.CAST, IConcept.TIME })
	@doc (
			value = "converts a date to astring following a custom pattern. The pattern can use \"%Y %M %N %D %E %h %m %s %z\" for outputting years, months, name of month, days, name of days, hours, minutes, seconds and the time-zone. A null or empty pattern will return the complete date as defined by the ISO date & time format. The pattern can also follow the pattern definition found here, which gives much more control over the format of the date: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns. Different patterns are available by default as constants: #iso_local, #iso_simple, #iso_offset, #iso_zoned and #custom, which can be changed in the preferences",
			masterDoc = true,
			usages = @usage (
					value = "",
					examples = @example (
							value = "string(#now, 'yyyy-MM-dd')",
							isExecutable = false)))
	@test ("string(date('2000-01-02'),'yyyy-MM-dd') = '2000-01-02'")
	@test ("string(date('2000-01-31'),'yyyy-MM-dd') = '2000-01-31'")
	@test ("string(date('2000-01-02'),'yyyy-MM-dd') = '2000-01-02'")
	public static String format(final GamaDate time, final String pattern) {
		return format(time, pattern, null);
	}

	@operator (
			value = "string",
			can_be_const = true,
			category = { IOperatorCategory.STRING, IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.CAST, IConcept.TIME })
	@doc (
			value = "converts a date to astring following a custom pattern and using a specific locale (e.g.: 'fr', 'en', etc.). The pattern can use \"%Y %M %N %D %E %h %m %s %z\" for outputting years, months, name of month, days, name of days, hours, minutes, seconds and the time-zone. A null or empty pattern will return the complete date as defined by the ISO date & time format. "
					+ "The pattern can also follow the pattern definition found here, which gives much more control over the format of the date: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns. Different patterns are available by default as constants: #iso_local, #iso_simple, #iso_offset, #iso_zoned and #custom, which can be changed in the preferences",
			usages = @usage (
					value = "",
					examples = @example (
							value = "string(#now, 'yyyy-MM-dd', 'en')",
							isExecutable = false)))
	@test ("string(date('2000-01-02'),'yyyy-MMMM-dd','en') = '2000-January-02'")

	public static String format(final GamaDate time, final String pattern, final String locale) {
		return time.toString(pattern, locale);
	}

	static class DurationFormatter implements TemporalAccessor {
		private static final DateTimeFormatter YMDHMS = DateTimeFormatter.ofPattern("u'y' M'm' d'd' HH:mm:ss");
		private static final DateTimeFormatter MDHMS = DateTimeFormatter.ofPattern("M' months' d 'days' HH:mm:ss");
		private static final DateTimeFormatter M1DHMS = DateTimeFormatter.ofPattern("M' month' d 'days' HH:mm:ss");
		private static final DateTimeFormatter M1D1HMS = DateTimeFormatter.ofPattern("M' month' d 'day' HH:mm:ss");
		private static final DateTimeFormatter DHMS = DateTimeFormatter.ofPattern("d 'days' HH:mm:ss");
		private static final DateTimeFormatter D1HMS = DateTimeFormatter.ofPattern("d 'day' HH:mm:ss");
		private static final DateTimeFormatter HMS = DateTimeFormatter.ofPattern("HH:mm:ss");

		static String format(final Duration duration) {
			return DURATION_FORMATTER.toString(duration);
		}

		private Temporal temporal;

		private String toString(final Duration duration) {
			this.temporal = duration.addTo(Dates.DATES_STARTING_DATE.getValue())
					.minus(GamaDateType.DEFAULT_OFFSET_IN_SECONDS.getTotalSeconds(), SECONDS);
			// if (duration.toDays() == 0l)
			// temporal = LocalDateTime.(temporal);
			return toString();
		}

		private DateTimeFormatter getFormatter() {
			if (getLong(YEAR) > 0) { return YMDHMS; }
			final long month = getLong(MONTH_OF_YEAR);
			final long day = getLong(DAY_OF_MONTH);
			if (month > 0) {
				if (month < 2) {
					if (day < 2) {
						return M1D1HMS;
					} else {
						return M1DHMS;
					}
				} else {
					return MDHMS;
				}
			}
			if (day > 0) {
				if (day < 2) {
					return D1HMS;
				} else {
					return DHMS;
				}
			}
			return HMS;
		}

		@Override
		public boolean isSupported(final TemporalField field) {
			return temporal.isSupported(field);
		}

		@Override
		public long getLong(final TemporalField field) {
			if (field == SECOND_OF_MINUTE) {
				return temporal.getLong(SECOND_OF_MINUTE);
			} else if (field == MINUTE_OF_HOUR) {
				return temporal.getLong(MINUTE_OF_HOUR);
			} else if (field == HOUR_OF_DAY) {
				return temporal.getLong(HOUR_OF_DAY);
			} else if (field == DAY_OF_MONTH) {
				return temporal.getLong(DAY_OF_MONTH) - 1l;
			} else if (field == MONTH_OF_YEAR) {
				return temporal.getLong(MONTH_OF_YEAR) - 1;
			} else if (field == YEAR) {
				return temporal.getLong(YEAR) - Dates.DATES_STARTING_DATE.getValue().getLong(YEAR);
			}
			return 0;
		}

		@Override
		public String toString() {
			return getFormatter().format(this);
		}

		@SuppressWarnings ("unchecked")
		@Override
		public <R> R query(final TemporalQuery<R> query) {
			if (query == TemporalQueries.precision()) { return (R) SECONDS; }
			if (query == TemporalQueries.chronology()) { return (R) IsoChronology.INSTANCE; }
			if (query == TemporalQueries.zone() || query == TemporalQueries.zoneId()) { return null; }
			return query.queryFrom(this);
		}

	}

}
