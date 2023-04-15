/*******************************************************************************************************
 *
 * GamlCoreUnits.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.constants;

import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IConstantCategory;
import msi.gama.util.GamaDate;
import msi.gaml.operators.Dates;
import msi.gaml.types.GamaDateType;

/**
 * The Interface GamlCoreUnits. Contains the list of GAML units
 */
public interface GamlCoreUnits {

	/** The Constant m. */
	@constant (
			value = "m",
			altNames = { "meter", "meters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = @doc ("meter: the length basic unit")) double m = 1;

	/** The Constant cm. */
	@constant (
			value = "cm",
			altNames = { "centimeter", "centimeters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("centimeter unit") }) double cm = 0.01d * m;

	/** The Constant dm. */
	@constant (
			value = "dm",
			altNames = { "decimeter", "decimeters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("decimeter unit") }) double dm = 0.1d * m;

	/** The Constant mm. */
	@constant (
			value = "mm",
			altNames = { "milimeter", "milimeters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("millimeter unit") }) double mm = cm / 10d;

	/** The micrometers. */
	@constant (
			value = "µm",
			altNames = { "micrometer", "micrometers" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("micrometer unit") }) double µm = mm / 1000d;

	/** The nanometers. */
	@constant (
			value = "nm",
			altNames = { "nanometer", "nanometers" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("nanometer unit") }) double nm = µm / 1000d;

	/** The Constant km. */
	@constant (
			value = "km",
			altNames = { "kilometer", "kilometers" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("kilometer unit") }) double km = 1000 * m;

	/** The Constant mile. */
	@constant (
			value = "mile",
			altNames = { "miles" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("mile unit") }) double mile = 1.609344d * km;

	/** The Constant yard. */
	@constant (
			value = "yard",
			altNames = { "yards" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("yard unit") }) double yard = 0.9144d * m, yards = yard;

	/** The Constant inch. */
	@constant (
			value = "inch",
			altNames = { "inches" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("inch unit") }) double inch = 2.54d * cm, inches = inch;

	/** The Constant foot. */
	@constant (
			value = "foot",
			altNames = { "feet", "ft" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("foot unit") }) double foot = 30.48d * cm;

	/*
	 *
	 * Time conversions
	 */

	/** The Constant iso_local */
	@constant (
			value = "iso_local",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso_local: the standard ISO 8601 output / parsing format for local dates (i.e. with no time-zone information)")) String iso_local =
					Dates.ISO_LOCAL_KEY;

	/** The iso zoned. */
	@constant (
			value = "iso_zoned",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso_zoned: the standard ISO 8601 output / parsing format for dates with a time zone")) String iso_zoned =
					Dates.ISO_ZONED_KEY;

	/** The iso offset. */
	@constant (
			value = "iso_offset",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso_offset: the standard ISO 8601 output / parsing format for dates with a time offset")) String iso_offset =
					Dates.ISO_OFFSET_KEY;

	/** The iso simple. */
	@constant (
			value = "iso-simple",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso: a simplified and readable version of the standard ISO 8601 output / parsing format for dates, e.g '16-03-30 10:12:11'")) String iso_simple =
					Dates.ISO_SIMPLE_KEY;

	/** The custom. */
	@constant (
			value = "custom",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("custom: a custom date/time pattern that can be defined in the preferences of GAMA and reused in models")) String custom =
					Dates.CUSTOM_KEY;

	/** The Constant cycle. */
	@constant (
			value = "cycle",
			altNames = { "cycles" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("cycle: the discrete measure of time in the simulation. Used to force a temporal expression to be expressed in terms of cycles rather than seconds")) int cycle =
					1;

	/** The Constant s. */
	@constant (
			value = "sec",
			altNames = { "second", "seconds", "s" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("second: the time basic unit, with a fixed value of 1. All other durations are expressed with respect to it")) double sec =
					1d;

	/** The Constant mn. */
	@constant (
			value = "minute",
			altNames = { "minutes", "mn" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("minute time unit: defined an exact duration of 60 seconds") }) double minute = 60d * sec;

	/** The Constant h. */
	@constant (
			value = "h",
			altNames = { "hour", "hours" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("hour time unit: defines an exact duration of 60 minutes") }) double h = 60d * minute;

	/** The Constant d. */
	@constant (
			value = "day",
			altNames = { "d", "days" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("day time unit: defines an exact duration of 24 hours") }) double day = 24d * h;

	/** The Constant week */
	@constant (
			value = "week",
			altNames = { "weeks" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("week time unit: defines an exact duration of 7 days") }) double week = 7d * day;

	/** The Constant month. */
	@constant (
			value = "month",
			altNames = { "months" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc (
					value = "month time unit: an approximate duration of 30 days. The number of days of each #month depend of course on the current_date of the model and cannot be constant")) double month =
							30 * day;

	/** The Constant y. */
	@constant (
			value = "year",
			altNames = { "years", "y" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc (
					value = "year time unit: an approximate duration of 365 days. The value of #year in number of days varies depending on leap years, etc. and is dependend on the current_date of the model")) double year =
							365 * day;

	/** The Constant msec. */
	@constant (
			value = "msec",
			altNames = { "millisecond", "milliseconds", "ms" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("millisecond time unit: defines an exact duration of 0.001 second") }) double msec =
					sec / 1000d;

	/** The Constant msec. */
	@constant (
			value = "epoch",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME },
			doc = { @doc ("The epoch default starting date as defined by the ISO format (1970-01-01T00:00Z)") }) GamaDate epoch =
					GamaDateType.EPOCH;

	/*
	 *
	 * Weight conversions
	 */

	/** The Constant kg. */
	@constant (
			value = "kg",
			altNames = { "kilo", "kilogram", "kilos" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = @doc ("second: the basic unit for weights")) double kg = 1, kilo = kg, kilogram = kg, kilos = kg;

	/** The Constant g. */
	@constant (
			value = "gram",
			altNames = { "grams" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("gram unit") }) double gram = kg / 1000;

	/** The Constant ton. */
	@constant (
			value = "ton",
			altNames = { "tons" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("ton unit") }) double ton = 1000 * kg;

	/** The Constant ounce. */
	@constant (
			value = "ounce",
			altNames = { "oz", "ounces" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("ounce unit") }) double ounce = 28.349523125 * gram;

	/** The Constant pound. */
	@constant (
			value = "pound",
			altNames = { "lb", "pounds", "lbm" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("pound unit") }) double pound = 0.45359237 * kg;

	/** The Constant stone. */
	@constant (
			value = "stone",
			altNames = { "st" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("stone unit") }) double stone = 14 * pound;

	/** The Constant short ton. */
	@constant (
			value = "shortton",
			altNames = { "ston" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("short ton unit") }) double shortton = 2000 * pound;

	/** The Constant long ton. */
	@constant (
			value = "longton",
			altNames = { "lton" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("short ton unit") }) double longton = 2240 * pound;

	/*
	 *
	 * Volume conversions
	 */
	/** The Constant m3. */
	@constant (
			value = "m3",
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = @doc ("cube meter: the basic unit for volumes")) double m3 = 1;

	/** Constant field dm3. */
	@constant (
			value = "l",
			altNames = { "liter", "liters", "dm3" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("liter unit") }) double l = m3 / 1000;

	/** The Constant cl. */
	@constant (
			value = "cl",
			altNames = { "centiliter", "centiliters" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("centiliter unit") }) double cl = l / 100;

	/** The Constant dl. */
	@constant (
			value = "dl",
			altNames = { "deciliter", "deciliters" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("deciliter unit") }) double dl = l / 10;

	/** The Constant hl. */
	@constant (
			value = "hl",
			altNames = { "hectoliter", "hectoliters" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("hectoliter unit") }) double hl = l * 100;
	/*
	 *
	 * Surface conversions
	 */
	/** The Constant m2. */
	@constant (
			value = "m2",
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = @doc ("square meter: the basic unit for surfaces")) double m2 = m * m;

	/** The Constant square inch. */
	@constant (
			value = "sqin",
			altNames = { "square_inch", "square_inches" },
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = { @doc ("square inch unit") }) double sqin = inch * inch;

	/** The Constant square foot. */
	@constant (
			value = "sqft",
			altNames = { "square_foot", "square_feet" },
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = { @doc ("square foot unit") }) double sqft = foot * foot;

	/** The Constant square mile. */
	@constant (
			value = "sqmi",
			altNames = { "square_mile", "square_miles" },
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = { @doc ("square mile unit") }) double sqmi = mile * mile;

}