/*********************************************************************************************
 *
 *
 * 'GamaColor.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;

import org.apache.commons.lang.StringUtils;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Dates;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaDate. Immutable class that holds a date (based on JSR-310)
 *
 * @author Taillandier
 * @author Alexis Drogoul
 */
@vars({ @var(name = "day_of_week", type = IType.INT, doc = {
		@doc("Returns the index of the day of the week (with Monday being 1)") }),
		@var(name = "leap", type = IType.BOOL, doc = { @doc("Returns true if the year is a leap year") }),
		@var(name = "days_in_month", type = IType.INT, doc = {
				@doc("Returns the number of days of the month of this date") }),
		@var(name = "days_in_year", type = IType.INT, doc = {
				@doc("Returns the number of days of the year of this date") }),
		@var(name = "week_of_year", type = IType.INT, doc = { @doc("Returns the week of the year") }),
		@var(name = "second", type = IType.INT, doc = { @doc("Returns the second of minute of this date") }),
		@var(name = "second_of_day", type = IType.INT, doc = { @doc("Returns the second of day of this date") }),
		@var(name = "minute", type = IType.INT, doc = { @doc("Returns the minute of hour of this date") }),
		@var(name = "minute_of_day", type = IType.INT, doc = { @doc("Returns the minute of day of this date") }),
		@var(name = "hour", type = IType.INT, doc = { @doc("Returns the hour") }),
		@var(name = "day", type = IType.INT, doc = { @doc("Returns the day") }),
		@var(name = "month", type = IType.INT, doc = { @doc("Returns the month") }),
		@var(name = "year", type = IType.INT, doc = { @doc("Returns the year") }) })
public class GamaDate implements IValue, Temporal {

	final Temporal dateTime;

	public static GamaDate absolute(final Temporal t) {
		return new GamaDate(t);
	}

	private GamaDate(final Temporal t) {
		this(null, t);
	}

	public GamaDate(final IScope scope, final GamaDate other) {
		this(scope, LocalDateTime.from(other));
	}

	public GamaDate(final IScope scope, final Temporal d) {
		if (!d.isSupported(MINUTE_OF_HOUR))
			dateTime = LocalDateTime.of(LocalDate.from(d), LocalTime.of(0, 0));
		else if (!d.isSupported(DAY_OF_MONTH))
			dateTime = LocalDateTime.of(LocalDate
					.from(scope == null ? GamaDateType.DEFAULT_STARTING_DATE : scope.getSimulation().getStartingDate()),
					LocalTime.from(d));
		else
			dateTime = d;
	}

	public GamaDate(final IScope scope, final String dateStr) {
		this(scope, parse(scope, dateStr, null));
	}

	public GamaDate(final IScope scope, final String dateStr, final String pattern) {
		this(scope, parse(scope, dateStr, Dates.getFormatter(pattern)));
	}

	public GamaDate(final IScope scope, final double val) {
		this(scope, scope.getSimulation().getStartingDate().plus(val * 1000, ChronoUnit.MILLIS));
	}

	public GamaDate(final IScope scope, final IList<?> vals) {
		this(scope, computeFromList(scope, vals));

	}

	private static Temporal parse(final IScope scope, final String original, final DateTimeFormatter df) {
		if (original == null || original.isEmpty() || original.equals("now"))
			return LocalDateTime.now();
		Temporal result = null;

		if (df != null) {
			try {
				final TemporalAccessor ta = df.parse(original);
				if (ta instanceof Temporal)
					return (Temporal) ta;
				if (ta.isSupported(ChronoField.HOUR_OF_DAY))
					return LocalTime.from(ta);
				return LocalDate.from(ta);
			} catch (final DateTimeParseException e) {
			}
			GAMA.reportAndThrowIfNeeded(scope,
					GamaRuntimeException.warning(
							"The date " + original + " can not correctly be parsed by the pattern provided", scope),
					false);
			return parse(scope, original, null);
		}

		String dateStr;
		try {
			// We first make sure all date fields have the correct length and
			// the string is correctly formatted
			String string = original;
			if (!original.contains("T") && original.contains(" ")) {
				string = StringUtils.replaceOnce(original, " ", "T");
			}
			final String[] base = string.split("T");
			final String[] date = base[0].split("-");
			String other;
			if (base.length == 1) {
				other = "00:00:00";
			} else
				other = base[1];
			String year, month, day;
			if (date.length == 1) {
				// ISO basic date format
				year = date[0].substring(0, 4);
				month = date[0].substring(4, 6);
				day = date[0].substring(6, 8);
			} else {
				year = date[0];
				month = date[1];
				day = date[2];
			}
			if (year.length() == 2)
				year = "20" + year;
			if (month.length() == 1)
				month = '0' + month;
			if (day.length() == 1)
				day = '0' + day;
			dateStr = year + "-" + month + "-" + day + "T" + other;
		} catch (final Exception e1) {
			throw GamaRuntimeException.error(
					"The date " + original + " is not correctly formatted. Please refer to the ISO date/time format",
					scope);
		}

		try {
			result = LocalDateTime.parse(dateStr);
		} catch (final DateTimeParseException e) {
			try {
				result = OffsetDateTime.parse(dateStr);
			} catch (final DateTimeParseException e2) {
				try {
					result = ZonedDateTime.parse(dateStr);
				} catch (final DateTimeParseException e3) {
					throw GamaRuntimeException.error("The date " + original
							+ " is not correctly formatted. Please refer to the ISO date/time format", scope);
				}
			}
		}

		return result;
	}

	public GamaDate(final IScope scope, final int second, final int minute, final int hour, final int day,
			final int month, final int year) {
		this(scope, LocalDateTime.of(year, month, day, hour, minute));
	}

	/**
	 * returns the complete number of seconds since the starting_date of the
	 * model (equivalent to a duration)
	 * 
	 * @param scope
	 *            the current scope from which the simulation can be obtained
	 * @return the duration in seconds since this starting date
	 */
	public double floatValue(final IScope scope) {
		return scope.getSimulation().getStartingDate().until(this, ChronoUnit.SECONDS);
	}

	public int intValue(final IScope scope) {
		return (int) floatValue(scope);
	}

	public IList<?> listValue(final IScope scope, final IType<?> ct) {
		final LocalDateTime ld = LocalDateTime.from(dateTime);
		return GamaListFactory.create(scope, ct, ld.getYear(), ld.getMonthValue(), ld.getDayOfWeek(), ld.getHour(),
				ld.getMinute(), ld.getSecond());
	}

	private static LocalDateTime computeFromList(final IScope scope, final IList<?> vals) {
		int year = 0, month = 1, day = 1, hour = 0, minute = 0, second = 0;
		final int size = vals.size();
		if (size > 0) {
			year = Cast.asInt(scope, vals.get(0));
			if (size > 1) {
				month = Cast.asInt(scope, vals.get(1));
				if (size > 2) {
					day = Cast.asInt(scope, vals.get(2));
					if (size > 3) {
						hour = Cast.asInt(scope, vals.get(3));
						if (size > 4) {
							minute = Cast.asInt(scope, vals.get(4));
							if (size > 5) {
								second = Cast.asInt(scope, vals.get(5));
							}
						}
					}
				}
			}
		}
		return LocalDateTime.of(year, month, day, hour, minute, second);
	}

	@Override
	public String toString() {
		return dateTime.toString();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "date ('" + toString() + "')";
	}

	@Override
	public String stringValue(final IScope scope) {
		return toString();
	}

	@Override
	public IType<?> getType() {
		return Types.DATE;
	}

	@Override
	public GamaDate copy(final IScope scope) throws GamaRuntimeException {
		return new GamaDate(scope, dateTime);
	}

	@getter("year")
	public int getYear() {
		return dateTime.get(YEAR);
	}

	@getter("day_of_year")
	public int getDayOfYear() {
		return dateTime.get(DAY_OF_YEAR);
	}

	@getter("second_of_day")
	public int getSecondOfDay() {
		return dateTime.get(ChronoField.SECOND_OF_DAY);
	}

	@getter("month")
	public int getMonth() {
		return dateTime.get(MONTH_OF_YEAR);
	}

	@getter("day")
	public int getDay() {
		return dateTime.get(DAY_OF_MONTH);
	}

	@getter("hour")
	public int getHour() {
		return dateTime.get(ChronoField.HOUR_OF_DAY);
	}

	@getter("minute")
	public int getMinute() {
		return dateTime.get(MINUTE_OF_HOUR);
	}

	@getter("minute_of_day")
	public int getMinuteOfDay() {
		return dateTime.get(ChronoField.MINUTE_OF_DAY);
	}

	@getter("second")
	public int getSecond() {
		return dateTime.get(SECOND_OF_MINUTE);
	}

	@getter("day_of_week")
	public int getDayWeek() {
		return dateTime.get(DAY_OF_WEEK);
	}

	@getter("leap")
	public boolean getIsLeap() {
		return LocalDate.from(dateTime).isLeapYear();
	}

	@getter("week_of_year")
	public int getWeekYear() {
		return dateTime.get(WeekFields.ISO.weekOfYear());
	}

	@getter("days_in_month")
	public int getDaysMonth() {
		return LocalDate.from(dateTime).lengthOfMonth();
	}

	@getter("days_in_year")
	public int getDaysInYear() {
		return LocalDate.from(dateTime).lengthOfYear();
	}

	public Temporal getTemporal() {
		return dateTime;
	}

	public LocalDateTime getLocalDateTime() {
		return LocalDateTime.from(dateTime);
	}

	public ZonedDateTime getZonedDateTime() {
		return ZonedDateTime.from(dateTime);
	}

	public OffsetDateTime getOffsetDateTime() {
		return OffsetDateTime.from(dateTime);
	}

	@Override
	public boolean isSupported(final TemporalField field) {
		return dateTime.isSupported(field);
	}

	@Override
	public long getLong(final TemporalField field) {
		return dateTime.getLong(field);
	}

	@Override
	public boolean isSupported(final TemporalUnit unit) {
		return dateTime.isSupported(unit);
	}

	@Override
	public GamaDate with(final TemporalField field, final long newValue) {
		return GamaDate.absolute(dateTime.with(field, newValue));
	}

	@Override
	public GamaDate plus(final long amountToAdd, final TemporalUnit unit) {
		return GamaDate.absolute(getLocalDateTime().plus(amountToAdd, unit));
	}

	@Override
	public long until(final Temporal endExclusive, final TemporalUnit unit) {
		return unit.between(getLocalDateTime(), LocalDateTime.from(endExclusive));
	}

	public String toString(final String string) {
		return Dates.getFormatter(string).format(this);
	}

	public boolean isGreaterThan(final GamaDate date2, final boolean strict) {
		final LocalDateTime me = getLocalDateTime();
		final LocalDateTime other = date2.getLocalDateTime();
		final boolean greater = me.isAfter(other);
		return strict ? greater : greater || me.isEqual(other);
	}

	public boolean isSmallerThan(final GamaDate date2, final boolean strict) {
		final LocalDateTime me = getLocalDateTime();
		final LocalDateTime other = date2.getLocalDateTime();
		final boolean smaller = me.isBefore(other);
		return strict ? smaller : smaller || me.isEqual(other);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GamaDate) {
			return getLocalDateTime().isEqual(((GamaDate) o).getLocalDateTime());
		}
		return false;
	}

	public GamaDate plus(final double duration, final TemporalUnit unit) {
		return plus((long) duration, unit);
	}

}
