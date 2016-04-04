package msi.gaml.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.field.PreciseDurationField;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.types.IType;

public class Dates {
	

	static Pattern model_pattern = Pattern.compile("%[YMDhms]");
	static Pattern system_pattern = Pattern.compile("%[YMNDEhmsz]");
	private static PeriodFormatterBuilder format;
	private static DateTimeFormatterBuilder systemFormat;
	private static PeriodFormatter dateFormat;
	private static PeriodFormatter timeFormat;
	private static DateTimeFormatter systemDateFormat;
	private static DateTimeFormatter systemTimeFormat;
	private static DateTimeFormatter systemDateTimeFormat;
	private static GamaChronology chronology;
	
	@operator(value = { IKeyword.MINUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(usages = @usage(value = "if both operands are dates, returns the duration in second between from date2 to date1",
			examples = { @example(value = "date1 - date2", equals = "598") }))		
	public static double minusDate(final IScope scope, final GamaDate date1, final GamaDate date2)
		throws GamaRuntimeException {
		Duration duration = new Duration(date2,date1);
		return duration.getStandardSeconds();
		
	}
	
	@operator(value = { IKeyword.PLUS , "add_seconds"}, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(usages = @usage(value = "if one of the operands is a date and the other a number, returns a date corresponding to the date plus the given number as duration (in seconds)",
			examples = { @example(value = "date1 + 200") }))		
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final int duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(duration);
		return nd;
	}
	
	
	@operator(value = { IKeyword.PLUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {})
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final double duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds((int) duration);
		return nd;
	}
	
	@operator(value = { IKeyword.PLUS  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {})
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final int duration,final GamaDate date1)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(duration);
		return nd;
	}
	
	@operator(value = { IKeyword.PLUS  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {})
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final double duration,final GamaDate date1 )
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds((int) duration);
		return nd;
	}
	
	@operator(value = {  IKeyword.MINUS,  "subtract_seconds"}, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {})
	@doc(usages = @usage(value = "if one of the operands is a date and the other a number, returns a date corresponding to the date minus the given number as duration (in seconds)",
			examples = { @example(value = "date1 - 200") }))		
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final int duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(- duration);
		return nd;
	}
	
	@operator(value = {  IKeyword.MINUS}, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {})
	@doc("Minus a duration from a date")
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final double duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(- (int) duration);
		return nd;
	}
	
	
	@operator(value = { IKeyword.PLUS}, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = {})
	@doc(value="returns the resulting string from the addition of a date and a string")
	public static String ConcatainDate(final IScope scope, final GamaDate date1, final String text )
		throws GamaRuntimeException {
		return date1.toString() + text;
	}
	
	@operator(value = { "add_years"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of year to a date", examples = { @example(value = "date1 add_years 3") })
	public static GamaDate addYears(final IScope scope, final GamaDate date1, final int nbYears)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addYears(nbYears);
		return nd;
	}
	
	@operator(value = { "add_months"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of months to a date", examples = { @example(value = "date1 add_months 5") })
	public static GamaDate addMonths(final IScope scope, final GamaDate date1, final int nbMonths)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addMonths(nbMonths);
		return nd;
	}
	
	@operator(value = { "add_weeks"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of weeks to a date", examples = { @example(value = "date1 add_weeks 15") })
	public static GamaDate addWeeks(final IScope scope, final GamaDate date1, final int nbWeeks)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addWeeks(nbWeeks);
		return nd;
	}
	
	@operator(value = { "add_days"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of days to a date", examples = { @example(value = "date1 add_days 20") })
	public static GamaDate addDays(final IScope scope, final GamaDate date1, final int nbDays)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addDays(nbDays);
		return nd;
	}
	
	@operator(value = { "add_hours"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of hours to a date", examples = { @example(value = "date1 add_hours 15") })
	public static GamaDate addHours(final IScope scope, final GamaDate date1, final int nbHours)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addHours(nbHours);
		return nd;
	}
	
	@operator(value = { "add_minutes"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of minutes to a date", examples = { @example(value = "date1 add_minutes 5") })
	public static GamaDate addMinutes(final IScope scope, final GamaDate date1, final int nbMinutes)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addMinutes(nbMinutes);
		return nd;
	}
	
	@operator(value = { "subtract_years"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of year from a date", examples = { @example(value = "date1 subtract_years 3") })
	public static GamaDate subtractYears(final IScope scope, final GamaDate date1, final int nbYears)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addYears(- nbYears);
		return nd;
	}
	
	@operator(value = { "subtract_months"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of months from a date", examples = { @example(value = "date1 subtract_months 5") })
	public static GamaDate subtractMonths(final IScope scope, final GamaDate date1, final int nbMonths)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addMonths(- nbMonths);
		return nd;
	}
	
	@operator(value = { "subtract_weeks"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of weeks from a date", examples = { @example(value = "date1 subtract_weeks 15") })
	public static GamaDate subtractWeeks(final IScope scope, final GamaDate date1, final int nbWeeks)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addWeeks(- nbWeeks);
		return nd;
	}
	
	@operator(value = { "subtract_days"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of days from a date", examples = { @example(value = "date1 subtract_days 20") })
	public static GamaDate subtractDays(final IScope scope, final GamaDate date1, final int nbDays)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addDays(- nbDays);
		return nd;
	}
	
	@operator(value = { "subtract_hours"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Add a given number of hours from a date", examples = { @example(value = "date1 subtract_hours 15") })
	public static GamaDate subtractHours(final IScope scope, final GamaDate date1, final int nbHours)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addHours( - nbHours);
		return nd;
	}
	
	@operator(value = { "subtract_minutes"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE }, concept = { IConcept.DATE })
	@doc(value = "Subtract a given number of minutes from a date", examples = { @example(value = "date1 subtract_minutes 5") })
	public static GamaDate subtractMinutes(final IScope scope, final GamaDate date1, final int nbMinutes)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addMinutes(- nbMinutes);
		return nd;
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

	@operator(value = "as_date", can_be_const = true, category = { IOperatorCategory.STRING, IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.DATE })
	@doc(value = "converts a number of seconds in the model (for instance, the 'time' variable) into a string that represents the period elapsed since the beginning of the simulation using year, month, day, hour, minutes and seconds following a given pattern (right-hand operand). GAMA uses a special calendar for internal model times, where months have 30 days and years 12 months. ",
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
		fillCustomFormat(pattern);
		PeriodFormatter pf = getCustomFormat().toFormatter();
		PeriodType pt = PeriodType.yearMonthDayTime();
		return pf.print(new Period(new Duration((long) time * 1000), getChronology()).normalizedStandard(pt));

	}
	
	private static void fillCustomFormat(final String pattern) {
		getCustomFormat().clear();
		List<String> dateList = new ArrayList();
		final Matcher m = model_pattern.matcher(pattern);
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

	}
	
	public static String asDate(GamaDate d1, GamaDate d2, final String pattern) {
		Period p = new Period(d1,d2);
		if (pattern == null) {
			String date = getDateFormat().print(p);
			 PeriodType pt = PeriodType.yearMonthDayTime();
			 String time= getTimeFormat().print(p.normalizedStandard(pt));
			return date + " " + time; 
		}
		fillCustomFormat(pattern);
		PeriodFormatter pf = getCustomFormat().toFormatter();
		PeriodType pt = PeriodType.yearMonthDayTime();
		return pf.print(p.normalizedStandard(pt));
	}

	@operator(value = "as_system_date", can_be_const = true, category = { IOperatorCategory.STRING,
		IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.TIME, IConcept.DATE })
	@doc(value = "converts a number of milliseconds in the system (for instance, the 'machine_time' variable) into a string that represents the current date represented by these milliseconds  using year, month, day, hour, minutes, seconds and time-zone offset following a given pattern (right-hand operand) ",
		masterDoc = true,
		usages = @usage(value = "Pattern should include : \"%Y %M %N %D %E %h %m %s %z\" for outputting years, months, name of month, days, name of days, hours, minutes, seconds and the time-zone. A null or empty pattern will return the complete date as defined by the ISO 8601 standard yyyy-MM-ddThh:mm:ss w/o the time-zone offset. Names are defined using the locale of the system",
			examples = @example(value = "2147483647 as_date \" %D %Y %M / %h:%m:%s %z\"",
				equals = "\"06 2015 05 / 23:58:57 +07\"")),
		see = { "as_system_time" })
	public static
		String asSystemDate(final double time, final String pattern) {
		// Pattern should include : "%Y %M %D %h %m %s" for outputting years, months, days, hours,
		// minutes, seconds
		if ( pattern == null || pattern.isEmpty() ) { return asSystemDate(time) + "T" + asSystemTime(time); }
		getSystemFormat().clear();
		List<String> dateList = new ArrayList();
		final Matcher m = system_pattern.matcher(pattern);
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
						getSystemFormat().appendYear(4, 4);
						break;
					case 'M':
						getSystemFormat().appendMonthOfYear(2);
						break;
					case 'N':
						getSystemFormat().appendMonthOfYearText();
						break;
					case 'D':
						getSystemFormat().appendDayOfMonth(2);
						break;
					case 'E':
						getSystemFormat().appendDayOfWeekText();
						break;
					case 'h':
						getSystemFormat().appendHourOfDay(2);
						break;
					case 'm':
						getSystemFormat().appendMinuteOfHour(2);
						break;
					case 's':
						getSystemFormat().appendSecondOfMinute(2);
						break;
					case 'z':
						getSystemFormat().appendTimeZoneOffset(null, false, 1, 2);
						break;
					default:
						getSystemFormat().appendLiteral(s);
				}
			} else {
				getSystemFormat().appendLiteral(s);
			}
		}

		DateTimeFormatter pf = getSystemFormat().toFormatter();
		return pf.print((long) time);

	}

	@operator(value = "as_date", can_be_const = true, category = { IOperatorCategory.STRING, IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.CAST, IConcept.TIME })
	@doc(value = "converts a number of seconds in the model (for example, the value of the 'time' variable) into a string that represents the period elapsed since the beginning of the simulation with years, months and days following a standard pattern. GAMA uses a special calendar for internal model times, where months have 30 days and years 12 months. ",
		usages = @usage(value = "used as an unary operator, it uses a defined pattern with years, months, days",
			examples = @example(value = "as_date(22324234)", equals = "\"8 months, 18 days\"")))
	public static
		String asDate(final double time) {
		PeriodType pt = PeriodType.yearMonthDayTime();
		return getDateFormat().print(
			new Period(new Duration((long) time * 1000), getChronology()).normalizedStandard(pt));
	}
	
	public static
	String asDate(final GamaDate date) {
		return getSystemDateTimeFormat().print(date);
	}

	@operator(value = "as_time", can_be_const = true, category = { IOperatorCategory.STRING, IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.TIME })
	@doc(value = "converts  a number of seconds in the model  (for example, the value of the 'time' variable)  into a string that represents the current number of hours, minutes and seconds of the period elapsed since the beginning of the simulation. As GAMA has no conception of time zones, the time is expressed as if the model was at GMT+00",
		comment = "as_time operator is a particular case (using a particular pattern) of the as_date operator.",
		examples = @example(value = "as_time(22324234)", equals = "\"09:10:34\""),
		see = "as_date")
	public static
		String asTime(final double time) {
		PeriodType pt = PeriodType.yearMonthDayTime();
		return getTimeFormat().print(
			new Period(new Duration((long) time * 1000), getChronology()).normalizedStandard(pt));
	}
	
	

	@operator(value = "as_system_time", can_be_const = true, category = { IOperatorCategory.STRING,
		IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.TIME })
	@doc(value = "converts  a value of milliseconds in the system  (for example, the value of the 'machine_time' variable)  into a string representing the current hours, minutes and seconds in the current time zone of the machine and the current Locale. This representation follows the ISO 8601 standard hh:mm:ss",
		comment = "as_system_time operator is a particular case (using a particular pattern) of the as_system_date operator.",
		examples = @example(value = "as_system_time(2147483647)", equals = "\"23:58:57\""),
		see = "as_system_date")
	public static
		String asSystemTime(final double time) {
		return getSystemTimeFormat().print((long) time);
	}

	@operator(value = "as_system_date", can_be_const = true, category = { IOperatorCategory.STRING,
		IOperatorCategory.TIME },
			concept = { IConcept.STRING, IConcept.DATE, IConcept.TIME })
	@doc(value = "converts  a value of milliseconds in the system (for example, the value of the 'machine_time' variable)  into a string representing the current year, month and day in the current time zone of the machine and the current Locale. This representation follows the ISO 8601 standard yyyy-mm-dd",
		comment = "as_system_date operator is a particular case (using a particular pattern) of the as_system_date operator.",
		examples = @example(value = "as_system_date(2147483647)", equals = "\"2015-05-06\""),
		see = "as_system_date")
	public static
		String asSystemDate(final double time) {
		return getSystemDateFormat().print((long) time);
	}
	
	static PeriodFormatterBuilder getCustomFormat() {
		if ( format == null ) {
			format = new PeriodFormatterBuilder();
		}
		return format;
	}

	static DateTimeFormatterBuilder getSystemFormat() {
		if ( systemFormat == null ) {
			systemFormat = new DateTimeFormatterBuilder();
		}
		return systemFormat;
	}

	static PeriodFormatter getTimeFormat() {
		if ( timeFormat == null ) {
			timeFormat =
				new PeriodFormatterBuilder().printZeroAlways().minimumPrintedDigits(2).appendHours().appendLiteral(":")
					.appendMinutes().appendLiteral(":").appendSeconds().toFormatter();
		}
		return timeFormat;
	}

	static DateTimeFormatter getSystemTimeFormat() {
		if ( systemTimeFormat == null ) {
			systemTimeFormat = DateTimeFormat.forPattern("HH:mm:ss");
		}
		return systemTimeFormat;
	}

	static DateTimeFormatter getSystemDateFormat() {
		if ( systemDateFormat == null ) {
			systemDateFormat = ISODateTimeFormat.yearMonthDay();
		}
		return systemDateFormat;
	}
	
	static public DateTimeFormatter getSystemDateTimeFormat() {
		if ( systemDateTimeFormat == null ) {
			systemDateTimeFormat = ISODateTimeFormat.dateTimeNoMillis();
		}
		return systemDateTimeFormat;
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

	public static GamaChronology getChronology() {
		if ( chronology == null ) {
			chronology = new GamaChronology(GregorianChronology.getInstanceUTC());
		}
		return chronology;
	}
	
}
