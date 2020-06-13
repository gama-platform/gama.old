/*******************************************************************************************************
 *
 * msi.gama.util.GamaDate.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;

import org.apache.commons.lang.StringUtils;

import msi.gama.common.interfaces.IValue;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.TimeUnitConstantExpression;
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
@vars ({ @variable (
		name = "day_of_week",
		type = IType.INT,
		doc = { @doc ("Returns the index of the day of the week (with Monday being 1)") }),
		@variable (
				name = "date",
				type = IType.DATE,
				doc = { @doc ("Returns a new date object with only the year-month-day components of this date") }),
		@variable (
				name = "leap",
				type = IType.BOOL,
				doc = { @doc ("Returns true if the year is a leap year") }),
		@variable (
				name = "days_in_month",
				type = IType.INT,
				doc = { @doc ("Returns the number of days of the month (28-31) of this date") }),
		@variable (
				name = "day_of_year",
				type = IType.INT,
				doc = { @doc ("Returns the current day number of the year of this date") }),
		@variable (
				name = "days_in_year",
				type = IType.INT,
				doc = { @doc ("Returns the number of days of the year (365-366) of this date") }),
		@variable (
				name = "week_of_year",
				type = IType.INT,
				doc = { @doc ("Returns the week (1-52) of the year") }),
		@variable (
				name = "second",
				type = IType.INT,
				doc = { @doc ("Returns the second of minute (0-59) of this date") }),
		@variable (
				name = "second_of_day",
				type = IType.INT,
				doc = { @doc ("Returns the second of day (0-86399) of this date") }),
		@variable (
				name = "minute",
				type = IType.INT,
				doc = { @doc ("Returns the minute of hour (0-59) of this date") }),
		@variable (
				name = "minute_of_day",
				type = IType.INT,
				doc = { @doc ("Returns the minute of day (0-1439) of this date") }),
		@variable (
				name = "hour",
				type = IType.INT,
				doc = { @doc ("Returns the hour of the day (0-23) of this date") }),
		@variable (
				name = "day",
				type = IType.INT,
				doc = { @doc ("Returns the day of month (1-31) of this date") }),
		@variable (
				name = "month",
				type = IType.INT,
				doc = { @doc ("Returns the month of year (1-12) of this date") }),
		@variable (
				name = "year",
				type = IType.INT,
				doc = { @doc ("Returns the year") }) })
public class GamaDate implements IValue, Temporal, Comparable<GamaDate> {

	final Temporal internal;

	public static GamaDate of(final Temporal t) {
		return new GamaDate(t);
	}

	private GamaDate(final Temporal t) {
		this(null, t);
	}

	public GamaDate(final IScope scope, final GamaDate other) {
		this(scope, LocalDateTime.from(other));
	}

	public GamaDate(final IScope scope, final Temporal d) {
		final ZoneId zone;
		if (d instanceof ChronoZonedDateTime) {
			zone = ZonedDateTime.from(d).getZone();
		} else if (d.isSupported(ChronoField.OFFSET_SECONDS)) {
			zone = ZoneId.ofOffset("", ZoneOffset.ofTotalSeconds(d.get(ChronoField.OFFSET_SECONDS)));
		} else {
			zone = GamaDateType.DEFAULT_ZONE;
		}
		if (!d.isSupported(MINUTE_OF_HOUR)) {
			internal = ZonedDateTime.of(LocalDate.from(d), LocalTime.of(0, 0), zone);
		} else if (!d.isSupported(DAY_OF_MONTH)) {
			internal = ZonedDateTime.of(LocalDate.from(
					scope == null ? Dates.DATES_STARTING_DATE.getValue() : scope.getSimulation().getStartingDate()),
					LocalTime.from(d), zone);
		} else {
			internal = d;
		}
	}

	public GamaDate(final IScope scope, final String dateStr) {
		this(scope, parse(scope, dateStr, null));
	}

	public GamaDate(final IScope scope, final String dateStr, final String pattern) {
		this(scope, dateStr, pattern, null);
	}

	public GamaDate(final IScope scope, final String dateStr, final String pattern, final String locale) {
		this(scope, parse(scope, dateStr, Dates.getFormatter(pattern, locale)));
	}

	public GamaDate(final IScope scope, final double val) {
		this(scope, scope.getSimulation().getStartingDate().plus(val * 1000, ChronoUnit.MILLIS));
	}

	public GamaDate(final IScope scope, final IList<?> vals) {
		this(scope, computeFromList(scope, vals));

	}

	private static Temporal parse(final IScope scope, final String original, final DateTimeFormatter df) {
		if (original == null || original.isEmpty() || original.equals("now")) {
			return LocalDateTime.now(GamaDateType.DEFAULT_ZONE);
		}
		Temporal result = null;

		if (df != null) {
			try {
				final TemporalAccessor ta = df.parse(original);
				if (ta instanceof Temporal) { return (Temporal) ta; }
				if (!ta.isSupported(ChronoField.YEAR) && !ta.isSupported(ChronoField.MONTH_OF_YEAR)
						&& !ta.isSupported(ChronoField.DAY_OF_MONTH)) {
					if (ta.isSupported(ChronoField.HOUR_OF_DAY)) { return LocalTime.from(ta); }
				}
				if (!ta.isSupported(ChronoField.HOUR_OF_DAY) && !ta.isSupported(ChronoField.MINUTE_OF_HOUR)
						&& !ta.isSupported(ChronoField.SECOND_OF_MINUTE)) {
					return LocalDate.from(ta);
				}
				return LocalDateTime.from(ta);
			} catch (final DateTimeParseException e) {
				e.printStackTrace();
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
			} else {
				other = base[1];
			}
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
			if (year.length() == 2) {
				year = "20" + year;
			}
			if (month.length() == 1) {
				month = '0' + month;
			}
			if (day.length() == 1) {
				day = '0' + day;
			}
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
	 * returns the complete number of seconds since the starting_date of the model (equivalent to a duration)
	 *
	 * @param scope
	 *            the current scope from which the simulation can be obtained
	 * @return the duration in seconds since this starting date
	 */
	public double floatValue(final IScope scope) {
		final SimulationAgent sim = scope.getSimulation();
		if (sim == null) { return Dates.DATES_STARTING_DATE.getValue().until(this, ChronoUnit.SECONDS); }
		return sim.getStartingDate().until(this, ChronoUnit.SECONDS);
	}

	public int intValue(final IScope scope) {
		return (int) floatValue(scope);
	}

	public IList<?> listValue(final IScope scope, final IType<?> ct) {
		final LocalDateTime ld = LocalDateTime.from(internal);
		return GamaListFactory.create(scope, ct, ld.getYear(), ld.getMonthValue(), ld.getDayOfWeek().getValue(),
				ld.getHour(), ld.getMinute(), ld.getSecond());
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
		return toString(null, null);
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
	public IType<?> getGamlType() {
		return Types.DATE;
	}

	@Override
	public GamaDate copy(final IScope scope) throws GamaRuntimeException {
		return new GamaDate(scope, internal);
	}

	@getter ("year")
	public int getYear() {
		return internal.get(YEAR);
	}

	@getter ("date")
	public GamaDate getDate() {
		return GamaDate.of(LocalDate.of(getYear(), getMonth(), getDay()));
	}

	@getter ("day_of_year")
	public int getDayOfYear() {
		return internal.get(DAY_OF_YEAR);
	}

	@getter ("second_of_day")
	public int getSecondOfDay() {
		return internal.get(ChronoField.SECOND_OF_DAY);
	}

	@getter ("month")
	public int getMonth() {
		return internal.get(MONTH_OF_YEAR);
	}

	@getter ("day")
	public int getDay() {
		return internal.get(DAY_OF_MONTH);
	}

	@getter ("hour")
	public int getHour() {
		return internal.get(ChronoField.HOUR_OF_DAY);
	}

	@getter ("minute")
	public int getMinute() {
		return internal.get(MINUTE_OF_HOUR);
	}

	@getter ("minute_of_day")
	public int getMinuteOfDay() {
		return internal.get(ChronoField.MINUTE_OF_DAY);
	}

	@getter ("second")
	public int getSecond() {
		return internal.get(SECOND_OF_MINUTE);
	}

	@getter ("day_of_week")
	public int getDayWeek() {
		return internal.get(DAY_OF_WEEK);
	}

	@getter ("leap")
	public boolean getIsLeap() {
		return LocalDate.from(internal).isLeapYear();
	}

	@getter ("week_of_year")
	public int getWeekYear() {
		return internal.get(WeekFields.ISO.weekOfYear());
	}

	@getter ("days_in_month")
	public int getDaysMonth() {
		return LocalDate.from(internal).lengthOfMonth();
	}

	@getter ("days_in_year")
	public int getDaysInYear() {
		return LocalDate.from(internal).lengthOfYear();
	}

	public Temporal getTemporal() {
		return internal;
	}

	public LocalDateTime getLocalDateTime() {
		return LocalDateTime.from(internal);
	}

	public ZonedDateTime getZonedDateTime() {
		return ZonedDateTime.from(internal);
	}

	public OffsetDateTime getOffsetDateTime() {
		return OffsetDateTime.from(internal);
	}

	@Override
	public boolean isSupported(final TemporalField field) {
		return internal.isSupported(field) || field.equals(ChronoField.OFFSET_SECONDS)
				|| field.equals(ChronoField.INSTANT_SECONDS);
	}

	@Override
	public long getLong(final TemporalField field) {
		if (internal.isSupported(field)) { return internal.getLong(field); }
		if (field.equals(ChronoField.OFFSET_SECONDS)) {
			// If no offset or time zone is supplied, we assume it is the zone of the modeler
			return GamaDateType.DEFAULT_OFFSET_IN_SECONDS.getTotalSeconds();
		}
		if (field.equals(ChronoField.INSTANT_SECONDS)) {
			return GamaDateType.EPOCH.until(internal, ChronoUnit.SECONDS);
		}
		return 0l;

	}

	@Override
	public boolean isSupported(final TemporalUnit unit) {
		return internal.isSupported(unit);
	}

	@Override
	public GamaDate with(final TemporalField field, final long newValue) {
		return GamaDate.of(internal.with(field, newValue));
	}

	@Override
	public GamaDate plus(final long amountToAdd, final TemporalUnit unit) {
		return GamaDate.of(internal.plus(amountToAdd, unit));
	}

	@Override
	public GamaDate minus(final long amountToAdd, final TemporalUnit unit) {
		return GamaDate.of(internal.minus(amountToAdd, unit));
	}

	@Override
	public long until(final Temporal endExclusive, final TemporalUnit unit) {
		return unit.between(internal, endExclusive);
	}

	public String toString(final String string, final String locale) {
		return Dates.getFormatter(string, locale).format(this);
	}

	public boolean isGreaterThan(final GamaDate date2, final boolean strict) {
		final boolean greater = getLocalDateTime().isAfter(date2.getLocalDateTime());
		return strict ? greater : greater || equals(date2);
	}

	public boolean isSmallerThan(final GamaDate date2, final boolean strict) {
		final boolean smaller = getLocalDateTime().isBefore(date2.getLocalDateTime());
		return strict ? smaller : smaller || equals(date2);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GamaDate) {
			Temporal a = getLocalDateTime();
			Temporal b = ((GamaDate) o).getLocalDateTime();
			return a.equals(b);
		}

		// return getLocalDateTime().equals(((GamaDate) o).getLocalDateTime()); }
		// return internal.equals(((GamaDate) o).internal); }
		return false;
	}

	@Override
	public int hashCode() {
		return internal.hashCode();
	}

	public GamaDate plus(final double duration, final TemporalUnit unit) {
		return plus((long) duration, unit);
	}

	@Override
	public GamaDate plus(final TemporalAmount amount) {
		return GamaDate.of(internal.plus(amount));
	}

	@Override
	public GamaDate minus(final TemporalAmount amount) {
		return GamaDate.of(internal.minus(amount));
	}

	public GamaDate plusMillis(final double duration) {
		return plus((long) duration, ChronoUnit.MILLIS);
	}

	public GamaDate minusMillis(final double duration) {
		return minus((long) duration, ChronoUnit.MILLIS);
	}

	public boolean isIntervalReached(final IScope scope, final IExpression period) {
		// We get the current date from the model
		final GamaDate current = scope.getClock().getCurrentDate();
		// Exact date ?
		if (this.equals(current)) { return true; }
		// Not yet reached ?
		if (isGreaterThan(current, true)) { return false; }
		GamaDate nextByPeriod = plus(scope, period);
		// Null period ?
		if (nextByPeriod.equals(this)) { return false; }
		// Exactly reached ?
		if (nextByPeriod.equals(current)) { return true; }
		while (nextByPeriod.isSmallerThan(current, true)) {
			nextByPeriod = nextByPeriod.plus(scope, period);
		}

		final long stepInMillis = scope.getClock().getStepInMillis();
		final GamaDate nextByStep = current.plus(stepInMillis, ChronoUnit.MILLIS);

		return nextByStep.isGreaterThan(nextByPeriod, true);

	}

	// class Amount {
	// Duration d;
	// Period p;
	//
	// Amount() {
	// d = Duration.ZERO;
	// p = Period.ZERO;
	// }
	//
	// }

	public GamaDate plus(final IScope scope, final IExpression period) {
		// This is where #month and the others will be reduced
		// The period evaluation should return a Period and a Duration that will
		// be applied to the date. i.e.
		// Amount a = new Amount();
		// period.evaluateAsTemporalExpression(scope, a);
		// return this.plus(a.d).plus(a.p);
		final long p = (long) (Cast.asFloat(scope, period.value(scope)) * 1000);
		if (p == 0) { return this; }
		return plus(p, ChronoUnit.MILLIS);
	}

	public GamaDate plus(final double period, final int repeat, final ChronoUnit unit) {
		// This is where #month and the others will be reduced
		// The period evaluation should return a Period and a Duration that will
		// be applied to the date. i.e.
		// Amount a = new Amount();
		// period.evaluateAsTemporalExpression(scope, a);
		// return this.plus(a.d).plus(a.p);
		GamaDate result = this;
		for (int i = 0; i < repeat; i++) {
			result = result.plus(period, unit);
		}
		return result;
	}

	// For exact durations, we can use the remainder of the modulo between
	// the elapsed time and the frequency. However, it is not always
	// possible when we have things like months or years in the computation
	// of the frequency
	// if (period.canBeComputed()) {
	// return isIntervalReached(scope, current, period);
	// }
	// private boolean isIntervalReached(final IScope scope, final GamaDate current, final IExpression period) {
	// // We compute the frequency (should not include the fancy stuff
	// // related to #week, #month and #year). The frequency should be
	// // expressed in seconds, so we convert it immediately to
	// // milliseconds
	// final long frequencyInMillis = (long) (Cast.asFloat(scope, period.value(scope)) * 1000);
	// // Fail fast 3: if the frequency is null, we return false
	// if (frequencyInMillis == 0) { return false; }
	//
	// // We then grab the step from the scope and convert it to
	// // milliseconds
	// final long stepInMillis = scope.getClock().getStepInMillis();
	// final long elapsedTime = until(current, ChronoUnit.MILLIS);
	// final long remainder = elapsedTime % frequencyInMillis;
	// // Fail fast 5: if we have exactly reached an interval, we return
	// // true
	// if (remainder == 0) {
	// DEBUG.LOG("We return true for " + current + " because the remainder is 0 between the elapsed_time "
	// + elapsedTime + " and the frequency " + frequencyInMillis);
	// return true;
	// }
	// // Finally, we return if the step is greater than the remainder
	// final boolean result = stepInMillis > remainder;
	// if (result) {
	// DEBUG.LOG("We return true for " + current + " because the step " + stepInMillis
	// + " is greater than the remainder " + remainder);
	// }
	// return result;
	// }

	public double getDuration(final IScope scope, final TimeUnitConstantExpression exp, final Double number) {
		final String name = exp.getName();
		// final boolean isTimeDependent = !exp.isConst();
		final boolean month = name.startsWith("m");
		final GamaDate next = this.plus(number, month ? ChronoUnit.MONTHS : ChronoUnit.YEARS);
		final double result = this.until(next, ChronoUnit.MILLIS) / 1000d;
		// DEBUG.LOG("Computation of " + number + " " + exp.getName() + " = " + result + "s or "
		// + this.until(next, ChronoUnit.DAYS) + " days");

		return result;
	}

	@Override
	public int compareTo(final GamaDate o) {
		return isSmallerThan(o, true) ? -1 : isGreaterThan(o, true) ? 1 : 0;
	}

	public boolean isBefore(final GamaDate startInclusive) {
		return isSmallerThan(startInclusive, true);
	}

	public boolean isAfter(final GamaDate startInclusive) {
		return isGreaterThan(startInclusive, true);
	}

	public String toISOString() {
		return toString(Dates.ISO_OFFSET_KEY, null);
	}

	public static GamaDate fromISOString(final String s) {
		try {
			final TemporalAccessor t = Dates.getFormatter(Dates.ISO_OFFSET_KEY, null).parse(s);
			if (t instanceof Temporal) { return of((Temporal) t); }
		} catch (final DateTimeParseException e) {}
		return new GamaDate(null, s);
	}

}
