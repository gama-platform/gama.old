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

import org.joda.time.MutableDateTime;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaDate.
 *
 * @author Taillandier
 */
@vars({
	@var(name = "second",
		type = IType.INT,
		doc = { @doc("Returns the second") }),
	@var(name = "minute",
		type = IType.INT,
		doc = { @doc("Returns the minute") }),
	@var(name = "hour",
		type = IType.INT,
		doc = { @doc("Returns the hour") }),
	@var(name = "day",
		type = IType.INT,
		doc = { @doc("Returns the day") }),
	@var(name = "month",
		type = IType.INT,
		doc = { @doc("Returns the month") }),
	@var(name = "year",
		type = IType.INT,
		doc = { @doc("Returns the year") })})
public class GamaDate extends MutableDateTime implements IValue {

	DateTimeFormatter formatter = ISODateTimeFormat.basicDateTime();
	PeriodType type = PeriodType.yearMonthDayTime();
	
	public GamaDate() {
		super();
		
	}
	
	public GamaDate(MutableDateTime d) {
		super(d);
	}
	
	public GamaDate(String dateStr) {
		super(dateStr);
	}

	public GamaDate(int second, int minute, int hour, int day, int month, int year) {
		super();
		setYear(year);
		setMonthOfYear(month);
		setDayOfMonth(day);
		setHourOfDay(hour);
		setSecondOfDay(second);
	}
	
	public GamaDate(int val) {
		super();
		addYears(val);
	}
	
	
	public GamaDate(IList vals) {
		super();
		if (vals.size() > 0) {
			Integer intVal = Cast.asInt(null, vals.get(0));
			setYear(intVal);
		}else setYear(0);
		if (vals.size() > 1) {
			Integer intVal = Cast.asInt(null, vals.get(1));
			setMonthOfYear(intVal);
		}else setMonthOfYear(0);
		
		if (vals.size() > 2) {
			Integer intVal = Cast.asInt(null, vals.get(2));
			setDayOfMonth(intVal);
		} else setDayOfMonth(0);
		if (vals.size() > 3) {
			Integer intVal = Cast.asInt(null, vals.get(3));
			setHourOfDay(intVal);
		}else setHourOfDay(0);
		if (vals.size() > 4) {
			Integer intVal = Cast.asInt(null, vals.get(4));
			setSecondOfDay(intVal);
		} else setSecondOfDay(0);
		if (vals.size() > 5) {
			Integer intVal = Cast.asInt(null, vals.get(5));
			setSecondOfDay(intVal);
		} else setSecondOfDay(0);
	}
	
	@Override
	public String toString() {
		return toString(formatter);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "date (" + toString(formatter)+ ")";
	}

	@Override
	public String stringValue(final IScope scope) {
		return toString(formatter);
	}

	
	@Override
	public IType getType() {
		return Types.DATE;
	}


	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return new GamaDate(this);
	}

	
	@getter("year")
	public int getYear() {
		return super.getYear();
	}


	@getter("month")
	public int getMonth() {
		return getMonthOfYear();
	}

	@getter("day")
	public int getDay() {
		return getDayOfMonth();
	}

	
	@getter("hour")
	public int getHour() {
		return getHourOfDay();
	}

	@getter("minute")
	public int getMinute() {
		return getMinuteOfHour();
	}

	@getter("second")
	public int getSecond() {
		return getSecondOfMinute();
	}

}
