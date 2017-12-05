/*********************************************************************************************
 *
 * 'IUnits.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.operators;

import java.awt.Font;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IConstantCategory;
import msi.gama.util.GAML;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaMaterial;
import msi.gaml.expressions.UnitConstantExpression;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public interface IUnits {

	/**
	 * Buffer constants
	 */
	@constant (
			value = "round",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GEOMETRY, IConcept.CONSTANT },
			doc = @doc ("This constant represents a round line buffer end cap style")) public final static int round =
					1;

	@constant (
			value = "flat",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GEOMETRY, IConcept.CONSTANT },
			doc = @doc ("This constant represents a flat line buffer end cap style")) public final static int flat = 2;

	@constant (
			value = "square",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GEOMETRY, IConcept.CONSTANT },
			doc = @doc ("This constant represents a square line buffer end cap style")) public final static int square =
					3;

	/**
	 * Layout constants
	 *
	 */
	@constant (
			value = "none",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents the absence of a predefined layout")) public final static int none =
					0;
	@constant (
			value = "stack",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are stacked")) public final static int stack =
					1;
	@constant (
			value = "split",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are split in a grid-like structure")) public final static int split =
					2;
	@constant (
			value = "horizontal",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are aligned horizontally")) public final static int horizontal =
					3;
	@constant (
			value = "vertical",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are aligned vertically")) public final static int vertical =
					4;

	/**
	 * Font style constants
	 */

	@constant (
			value = "bold",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.TEXT },
			doc = @doc ("This constant allows to build a font with a bold face. Can be combined with #italic")) public final static int bold =
					Font.BOLD; /* 1 */

	@constant (
			value = "italic",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.TEXT },
			doc = @doc ("This constant allows to build a font with an italic face. Can be combined with #bold")) public final static int italic =
					Font.ITALIC; /* 2 */

	@constant (
			value = "plain",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.TEXT },
			doc = @doc ("This constant allows to build a font with a plain face")) public final static int plain =
					Font.PLAIN;
	/**
	 * Special units
	 */

	// @constant(value = "view_x",
	// category = IConstantCategory.GRAPHIC,
	// doc = @doc("This unit, only available when running aspects or declaring
	// displays, returns the x ordinate of the top-left corner of the view in
	// the world") )
	// public final static double view_x = 1;
	//
	// @constant(value = "view_y",
	// category = IConstantCategory.GRAPHIC,
	// doc = @doc("This unit, only available when running aspects or declaring
	// displays, returns the y ordinate of the top-left corner of the view in
	// the world") )
	// public final static double view_y = 1;
	//
	// @constant(value = "view_width",
	// category = IConstantCategory.GRAPHIC,
	// doc = @doc("This unit, only available when running aspects or declaring
	// displays, returns the width of the view in world units") )
	// public final static double view_width = 1;
	//
	// @constant(value = "view_height",
	// category = IConstantCategory.GRAPHIC,
	// doc = @doc("This unit, only available when running aspects or declaring
	// displays, returns the height of the view in world units") )
	// public final static double view_height = 1;

	@constant (
			value = "user_location",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.DISPLAY },
			doc = @doc ("This unit contains in permanence the location of the mouse on the display in which it is situated. The latest location is provided when it is out of a display")) public final static GamaPoint user_location =
					GamaPoint.NULL_POINT;

	@constant (
			value = "camera_location",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT, IConcept.THREED },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current position of the camera as a point")) public final static GamaPoint camera_location =
					GamaPoint.NULL_POINT;

	@constant (
			value = "camera_target",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT, IConcept.THREED },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current target of the camera as a point")) public final static GamaPoint camera_target =
					GamaPoint.NULL_POINT;

	@constant (
			value = "camera_orientation",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT, IConcept.THREED },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current orientation of the camera as a point")) public final static GamaPoint camera_orientation =
					GamaPoint.NULL_POINT;

	@constant (
			value = "zoom",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.DISPLAY },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current zoom level of the display as a positive float, where 1.0 represent the neutral zoom (100%)")) public final static double zoom =
					1;

	@constant (
			value = "pixels",
			altNames = { "px" },
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT },
			doc = @doc ("This unit, only available when running aspects or declaring displays,  returns a dynamic value instead of a fixed one. px (or pixels), returns the value of one pixel on the current view in terms of model units.")) public final static double pixels =
					1d, px = pixels; // Represents the value
										// of a pixel in
										// terms
	// of model units. Parsed early
	// and never used as a constant.
	@constant (
			value = "display_width",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT },
			doc = @doc ("This constant is only accessible in a graphical context: display, graphics...")) public final static double display_width =
					1;

	@constant (
			value = "display_height",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT },
			doc = @doc ("This constant is only accessible in a graphical context: display, graphics...")) public final static double display_height =
					1;

	@constant (
			value = "now",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME },
			doc = @doc ("This value represents the current date")) public final static double now = 1;

	/**
	 * Mathematical constants
	 *
	 */
	@constant (
			value = "pi",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT, IConcept.MATH },
			doc = @doc ("The PI constant")) public final static double pi = Math.PI;

	@constant (
			value = "e",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT, IConcept.MATH },
			doc = @doc ("The e constant")) public final static double e = Math.E;

	@constant (
			value = "to_deg",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the value to convert radians into degrees")) public final static double to_deg =
					180d / Math.PI;
	@constant (
			value = "to_rad",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the value to convert degrees into radians")) public final static double to_rad =
					Math.PI / 180d;

	@constant (
			value = "nan",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding a Not-a-Number (NaN) value of type float (Java Double.POSITIVE_INFINITY)")) public final static double nan =
					Double.NaN;
	@constant (
			value = "infinity",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the positive infinity of type float (Java Double.POSITIVE_INFINITY)")) public final static double infinity =
					Double.POSITIVE_INFINITY;
	@constant (
			value = "min_float",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the smallest positive nonzero value of type float (Java Double.MIN_VALUE)")) public final static double min_float =
					Double.MIN_VALUE;
	@constant (
			value = "max_float",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the largest positive finite value of type float (Java Double.MAX_VALUE)")) public final static double max_float =
					Double.MAX_VALUE;
	@constant (
			value = "min_int",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the minimum value an int can have (Java Integer.MIN_VALUE)")) public final static double min_int =
					Integer.MIN_VALUE;
	@constant (
			value = "max_int",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the maximum value an int can have (Java Integer.MAX_VALUE)")) public final static double max_int =
					Integer.MAX_VALUE;
	/*
	 *
	 * Distance & size conversions
	 */
	/** The Constant m. */
	@constant (
			value = "m",
			altNames = { "meter", "meters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = @doc ("meter: the length basic unit")) public final static double m = 1, meter = m, meters = m;

	/** The Constant cm. */
	@constant (
			value = "cm",
			altNames = { "centimeter", "centimeters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("centimeter unit") }) public final static double cm = 0.01f * m, centimeter = cm,
					centimeters = cm;

	/** The Constant dm. */
	@constant (
			value = "dm",
			altNames = { "decimeter", "decimeters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("decimeter unit") }) public final static double dm = 0.1f * m, decimeter = dm,
					decimeters = dm;

	/** The Constant mm. */
	@constant (
			value = "mm",
			altNames = { "milimeter", "milimeters" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("millimeter unit") }) public final static double mm = cm / 10, millimeter = mm,
					millimeters = mm;

	/** The Constant km. */
	@constant (
			value = "km",
			altNames = { "kilometer", "kilometers" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("kilometer unit") }) public final static double km = 1000 * m, kilometer = km,
					kilometers = km;

	/** The Constant mile. */
	@constant (
			value = "mile",
			altNames = { "miles" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("mile unit") }) public final static double mile = 1.609344d * km, miles = mile;

	/** The Constant yard. */
	@constant (
			value = "yard",
			altNames = { "yards" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("yard unit") }) public final static double yard = 0.9144d * m, yards = yard;

	/** The Constant inch. */
	@constant (
			value = "inch",
			altNames = { "inches" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("inch unit") }) public final static double inch = 2.54d * cm, inches = inch;

	/** The Constant foot. */
	@constant (
			value = "foot",
			altNames = { "feet", "ft" },
			category = { IConstantCategory.LENGTH },
			concept = { IConcept.DIMENSION, IConcept.LENGTH_UNIT },
			doc = { @doc ("foot unit") }) public final static double foot = 30.48d * cm, feet = foot, ft = foot;

	/*
	 *
	 * Time conversions
	 */

	/** The Constant iso_local */
	@constant (
			value = "iso_local",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso_local: the standard ISO 8601 output / parsing format for local dates (i.e. with no time-zone information)")) public final static String iso_local =
					Dates.ISO_LOCAL_KEY;

	@constant (
			value = "iso_zoned",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso_zoned: the standard ISO 8601 output / parsing format for dates with a time zone")) public final static String iso_zoned =
					Dates.ISO_ZONED_KEY;

	@constant (
			value = "iso_offset",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso_offset: the standard ISO 8601 output / parsing format for dates with a time offset")) public final static String iso_offset =
					Dates.ISO_OFFSET_KEY;

	@constant (
			value = "iso-simple",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("iso: a simplified and readable version of the standard ISO 8601 output / parsing format for dates, e.g '16-03-30 10:12:11'")) public final static String iso_simple =
					Dates.ISO_SIMPLE_KEY;

	@constant (
			value = "custom",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("custom: a custom date/time pattern that can be defined in the preferences of GAMA and reused in models")) public final static String custom =
					Dates.CUSTOM_KEY;

	/** The Constant cycle. */
	@constant (
			value = "cycle",
			altNames = { "cycles" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("cycle: the discrete measure of time in the simulation. Used to force a temporal expression to be expressed in terms of cycles rather than seconds")) public final static int cycle =
					1, cycles = cycle;

	/** The Constant s. */
	@constant (
			value = "sec",
			altNames = { "second", "seconds", "s" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc ("second: the time basic unit, with a fixed value of 1. All other durations are expressed with respect to it")) public final static double sec =
					1d, second = sec, seconds = sec, s = sec;

	/** The Constant mn. */
	@constant (
			value = "minute",
			altNames = { "minutes", "mn" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("minute time unit: defined an exact duration of 60 seconds") }) public final static double minute =
					60d * sec, minutes = minute, mn = minute;

	/** The Constant h. */
	@constant (
			value = "h",
			altNames = { "hour", "hours" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("hour time unit: defines an exact duration of 60 minutes") }) public final static double h =
					60d * minute, hour = h, hours = h;

	/** The Constant d. */
	@constant (
			value = "day",
			altNames = { "days" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("day time unit: defines an exact duration of 24 hours") }) public final static double day =
					24d * h, days = day, d = day;

	/** The Constant week */
	@constant (
			value = "week",
			altNames = { "weeks" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("week time unit: defines an exact duration of  7 days") }) public final static double week =
					7d * day, weeks = week;

	/** The Constant month. */
	@constant (
			value = "month",
			altNames = { "months" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc (
					value = "month time unit: does not define an exact duration. The duration will depend on the step/date at which this unit is computed.")) public final static double month =
							30 * day, months = month;

	/** The Constant y. */
	@constant (
			value = "year",
			altNames = { "years", "y" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = @doc (
					value = "year time unit: does not define an exact duration. The duration will depend on the step/date at which this unit is computed.")) public final static double year =
							12 * month, years = year, y = year;

	/** The Constant msec. */
	@constant (
			value = "msec",
			altNames = { "millisecond", "milliseconds", "ms" },
			category = { IConstantCategory.TIME },
			concept = { IConcept.DIMENSION, IConcept.DATE, IConcept.TIME_UNIT, IConcept.TIME },
			doc = { @doc ("millisecond time unit: defines an exact duration of 0.001 second") }) public final static double msec =
					sec / 1000d, millisecond = msec, milliseconds = msec, ms = msec;

	/** The Constant msec. */
	@constant (
			value = "epoch",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME },
			doc = { @doc ("The epoch default starting date as defined by the ISO format (1970-01-01T00:00Z)") }) public final static GamaDate epoch =
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
			doc = @doc ("second: the basic unit for weights")) public final static double kg = 1, kilo = kg,
					kilogram = kg, kilos = kg;

	/** The Constant g. */
	@constant (
			value = "gram",
			altNames = { "grams" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("gram unit") }) public final static double gram = kg / 1000, grams = gram;

	/** The Constant ton. */
	@constant (
			value = "ton",
			altNames = { "tons" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("ton unit") }) public final static double ton = 1000 * kg, tons = ton;

	/** The Constant ounce. */
	@constant (
			value = "ounce",
			altNames = { "oz", "ounces" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("ounce unit") }) public final static double ounce = 28.349523125 * gram, oz = ounce,
					ounces = ounce;

	/** The Constant pound. */
	@constant (
			value = "pound",
			altNames = { "lb", "pounds", "lbm" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("pound unit") }) public final static double pound = 0.45359237 * kg, lb = pound,
					pounds = pound, lbm = pound;

	/** The Constant stone. */
	@constant (
			value = "stone",
			altNames = { "st" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("stone unit") }) public final static double stone = 14 * pound, st = stone;

	/** The Constant short ton. */
	@constant (
			value = "shortton",
			altNames = { "ston" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("short ton unit") }) public final static double shortton = 2000 * pound, ston = shortton;

	/** The Constant long ton. */
	@constant (
			value = "longton",
			altNames = { "lton" },
			category = { IConstantCategory.WEIGHT },
			concept = { IConcept.DIMENSION, IConcept.WEIGHT_UNIT },
			doc = { @doc ("short ton unit") }) public final static double longton = 2240 * pound, lton = longton;

	/*
	 *
	 * Volume conversions
	 */
	/** The Constant m3. */
	@constant (
			value = "m3",
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = @doc ("cube meter: the basic unit for volumes")) public final static double m3 = 1;

	/** Constant field dm3. */
	@constant (
			value = "l",
			altNames = { "liter", "liters", "dm3" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("liter unit") }) public final static double l = m3 / 1000, liter = l, liters = l, dm3 = l;

	/** The Constant cl. */
	@constant (
			value = "cl",
			altNames = { "centiliter", "centiliters" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("centiliter unit") }) public final static double cl = l / 100, centiliter = cl,
					centiliters = cl;

	/** The Constant dl. */
	@constant (
			value = "dl",
			altNames = { "deciliter", "deciliters" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("deciliter unit") }) public final static double dl = l / 10, deciliter = dl, deciliters = dl;

	/** The Constant hl. */
	@constant (
			value = "hl",
			altNames = { "hectoliter", "hectoliters" },
			category = { IConstantCategory.VOLUME },
			concept = { IConcept.DIMENSION, IConcept.VOLUME_UNIT },
			doc = { @doc ("hectoliter unit") }) public final static double hl = l * 100, hectoliter = hl,
					hectoliters = hl;
	/*
	 *
	 * Surface conversions
	 */
	/** The Constant m2. */
	@constant (
			value = "m2",
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = @doc ("square meter: the basic unit for surfaces")) public final static double m2 = m * m,
					square_meter = m2, square_meters = m2;

	/** The Constant square inch. */
	@constant (
			value = "sqin",
			altNames = { "square_inch", "square_inches" },
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = { @doc ("square inch unit") }) public final static double sqin = inch * inch, square_inch = sqin,
					square_inches = sqin;

	/** The Constant square foot. */
	@constant (
			value = "sqft",
			altNames = { "square_foot", "square_feet" },
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = { @doc ("square foot unit") }) public final static double sqft = foot * foot, square_foot = sqft,
					square_feet = sqft;

	/** The Constant square mile. */
	@constant (
			value = "sqmi",
			altNames = { "square_mile", "square_miles" },
			category = { IConstantCategory.SURFACE },
			concept = { IConcept.DIMENSION, IConcept.SURFACE_UNIT },
			doc = { @doc ("square mile unit") }) public final static double sqmi = mile * mile, square_mile = sqmi,
					square_miles = sqmi;
	/*
	 * Others
	 */
	@constant (
			value = "current_error",
			altNames = {},
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.ACTION },
			doc = { @doc ("The text of the last error thrown during the current execution") }) public final static String current_error =
					"";

	public final static Map<String, UnitConstantExpression> UNITS_EXPR = new HashMap<>();

	@SuppressWarnings ("rawtypes")
	public static Object add(final String name, final Object value, final String doc, final String deprec,
			final boolean isTime, final String[] names) {
		if (UNITS_EXPR.containsKey(name)) { return null; }
		final IType t = Types.get(value.getClass());
		final UnitConstantExpression exp =
				GAML.getExpressionFactory().createUnit(value, t, name, doc, deprec, isTime, names);
		UNITS_EXPR.put(name, exp);
		if (names != null) {
			for (final String s : names) {
				UNITS_EXPR.put(s, exp);
			}
		}
		return value;
	}

	public static void initialize() {
		for (final Map.Entry<String, GamaColor> entry : GamaColor.colors.entrySet()) {
			final GamaColor c = entry.getValue();
			final String doc = "standard CSS color corresponding to " + "rgb (" + c.red() + ", " + c.green() + ", "
					+ c.blue() + "," + c.getAlpha() + ")";
			add(entry.getKey(), c, doc, null, false, null);
		}

		for (final Map.Entry<String, GamaMaterial> entry : GamaMaterial.materials.entrySet()) {
			final GamaMaterial m = entry.getValue();
			final String doc = "standard materials.";
			add(entry.getKey(), m, doc, null, false, null);
		}

		for (final Field f : IUnits.class.getDeclaredFields()) {
			try {
				if (f.getName().equals("UNITS_EXPR"))
					continue;
				final Object v = f.get(IUnits.class);
				String[] names = null;
				final constant annotation = f.getAnnotation(constant.class);
				boolean isTime = false;
				String documentation = "Its value is " + Cast.toGaml(v) + ". </b>";
				String deprecated = null;
				if (annotation != null) {
					names = annotation.altNames();
					final doc[] ds = annotation.doc();
					if (ds != null && ds.length > 0) {
						final doc d = ds[0];
						documentation += d.value();
						deprecated = d.deprecated();
						if (deprecated.isEmpty())
							deprecated = null;
					}
					final String[] e = annotation.category();
					isTime = Arrays.asList(e).contains(IConstantCategory.TIME);
				}
				add(f.getName(), v, documentation, deprecated, isTime, names);

			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}