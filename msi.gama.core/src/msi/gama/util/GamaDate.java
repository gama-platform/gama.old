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
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaDate. Immutable class that holds a date (based on JSR-310)
 *
 * @author Taillandier
 * @author Alexis Drogoul
 */
@vars({ @var(name = "day_of_week", type = IType.INT, doc = { @doc("Returns the day of the week") }),
		@var(name = "leap", type = IType.BOOL, doc = { @doc("Returns true if the year is leap") }),
		@var(name = "days_in_month", type = IType.INT, doc = { @doc("Returns the number of days of the month") }),
		@var(name = "week_of_year", type = IType.INT, doc = { @doc("Returns the week of the year") }),
		@var(name = "second", type = IType.INT, doc = { @doc("Returns the second") }),
		@var(name = "minute", type = IType.INT, doc = { @doc("Returns the minute") }),
		@var(name = "hour", type = IType.INT, doc = { @doc("Returns the hour") }),
		@var(name = "day", type = IType.INT, doc = { @doc("Returns the day") }),
		@var(name = "month", type = IType.INT, doc = { @doc("Returns the month") }),
		@var(name = "year", type = IType.INT, doc = { @doc("Returns the year") }) })
public class GamaDate implements IValue, Temporal {

	final Temporal dateTime;

	public GamaDate(final GamaDate other) {
		this(LocalDateTime.from(other));
	}

	public GamaDate(final Temporal d) {
		dateTime = d;
	}

	public GamaDate(final IScope scope, final String dateStr) {
		this(parse(scope, dateStr, null));
	}

	public GamaDate(final IScope scope, final String dateStr, final String pattern) {
		this(parse(scope, dateStr, Dates.getFormatter(pattern)));

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

	public GamaDate(final int second, final int minute, final int hour, final int day, final int month,
			final int year) {
		this(LocalDateTime.of(year, month, day, hour, minute));
	}

	public GamaDate(final int val) {
		this(LocalDateTime.of(val, 0, 0, 0, 0));
	}

	public GamaDate(final IList<?> vals) {
		this(computeFromList(vals));

	}

	private static LocalDateTime computeFromList(final IList<?> vals) {
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		final int size = vals.size();
		if (size > 0) {
			year = Cast.asInt(null, vals.get(0));
			if (size > 1) {
				month = Cast.asInt(null, vals.get(1));
				if (size > 2) {
					day = Cast.asInt(null, vals.get(2));
					if (size > 3) {
						hour = Cast.asInt(null, vals.get(3));
						if (size > 4) {
							minute = Cast.asInt(null, vals.get(4));
							if (size > 5) {
								second = Cast.asInt(null, vals.get(5));
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
		return new GamaDate(this);
	}

	@getter("year")
	public int getYear() {
		return dateTime.get(YEAR);
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
		return dateTime.get(HOUR_OF_DAY);
	}

	@getter("minute")
	public int getMinute() {
		return dateTime.get(MINUTE_OF_HOUR);
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
		return LocalDate.from(dateTime).getMonth().length(true);
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
		return new GamaDate(dateTime.with(field, newValue));
	}

	@Override
	public GamaDate plus(final long amountToAdd, final TemporalUnit unit) {
		return new GamaDate(dateTime.plus(amountToAdd, unit));
	}

	@Override
	public long until(final Temporal endExclusive, final TemporalUnit unit) {
		return unit.between(getLocalDateTime(), LocalDateTime.from(endExclusive));
	}

	public String toString(final String string) {
		return Dates.getFormatter(string).format(this);
	}

	public int yearsDifferenceWith(final GamaDate date1) {
		return (int) until(date1, ChronoUnit.YEARS);
	}

	public int monthDifferenceWith(final GamaDate date1) {
		return (int) until(date1, ChronoUnit.MONTHS);
	}

	public boolean isGreaterThan(final GamaDate date2, final boolean strict) {
		final long until = until(date2, ChronoUnit.SECONDS);
		return strict ? until < 0 : until <= 0;
	}

	public boolean isSmallerThan(final GamaDate date2, final boolean strict) {
		final long until = until(date2, ChronoUnit.SECONDS);
		return strict ? until > 0 : until >= 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GamaDate) {
			return getLocalDateTime().equals(((GamaDate) o).getLocalDateTime());
		}
		return false;
	}

	public GamaDate plus(final double duration, final TemporalUnit unit) {
		return plus((long) duration, unit);
	}

}
