/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.lang.reflect.Field;
import java.util.*;
import msi.gama.util.GamaColor;

public class IUnits {

	/**
	 * Special units
	 */

	public final static double pixels = 1, px = pixels; // Represents the value of a pixel in terms
														// of model units. Parsed early
														// and never used as a constant.

	public final static double display_width = 1;
	public final static double display_height = 1;

	/**
	 * Mathematical constants
	 * 
	 */
	public final static double pi = Math.PI;

	public final static double e = Math.E;

	public final static double to_deg = 180d / Math.PI;

	public final static double to_rad = Math.PI / 180d;

	public final static double nan = Double.NaN;
	public final static double infinity = Double.POSITIVE_INFINITY;
	public final static double min_float = Double.MIN_VALUE;
	public final static double max_float = Double.MAX_VALUE;
	public final static double min_int = Integer.MIN_VALUE;
	public final static double max_int = Integer.MAX_VALUE;
	/*
	 * 
	 * Distance & size conversions
	 */
	/** The Constant m. */
	public final static double m = 1, meter = m, meters = m;

	/** The Constant cm. */
	public final static double cm = 0.01f * m, centimeter = cm, centimeters = cm;

	/** The Constant dm. */
	public final static double dm = 0.1f * m, decimeter = dm, decimeters = dm;

	/** The Constant mm. */
	public final static double mm = cm / 10, millimeter = mm, millimeters = mm;

	/** The Constant km. */
	public final static double km = 1000 * m, kilometer = km, kilometers = km;

	/** The Constant mile. */
	public final static double mile = 1.609344d * km, miles = mile;

	/** The Constant yard. */
	public final static double yard = 0.9144d * m, yards = yard;

	/** The Constant inch. */
	public final static double inch = 2.54d * cm, inches = inch;

	/** The Constant foot. */
	public final static double foot = 30.48d * cm, feet = foot, ft = foot;

	/*
	 * 
	 * Time conversions
	 */
	/** The Constant s. */
	public final static double sec = 1, second = sec, seconds = sec, s = sec;

	/** The Constant mn. */
	public final static double minute = 60 * sec, minutes = minute, mn = minute;

	/** The Constant h. */
	public final static double h = 60 * minute, hour = h, hours = h;

	/** The Constant d. */
	public final static double day = 24 * h, days = day, d = day;

	/** The Constant month. */
	public final static double month = 30 * day, months = month;

	/** The Constant y. */
	public final static double year = 12 * month, years = year, y = year;

	/** The Constant msec. */
	public final static double msec = sec / 1000, millisecond = msec, milliseconds = msec;

	/*
	 * 
	 * Weight conversions
	 */

	/** The Constant kg. */
	public final static double kg = 1, kilo = kg, kilogram = kg, kilos = kg;

	/** The Constant g. */
	public final static double gram = kg / 1000, grams = gram;

	/** The Constant ton. */
	public final static double ton = 1000 * kg, tons = ton;

	/** The Constant ounce. */
	public final static double ounce = 28.349523125 * gram, oz = ounce, ounces = ounce;

	/** The Constant pound. */
	public final static double pound = 0.45359237 * kg, lb = pound, pounds = pound, lbm = pound;
	/*
	 * 
	 * Volume conversions
	 */
	/** The Constant m3. */
	public final static double m3 = 1;

	/** Constant field dm3. */
	public final static double l = m3 / 1000, liter = l, liters = l, dm3 = l;

	/** The Constant cl. */
	public final static double cl = l / 100, centiliter = cl, centiliters = cl;

	/** The Constant dl. */
	public final static double dl = l / 10, deciliter = dl, deciliters = dl;

	/** The Constant hl. */
	public final static double hl = l * 100, hectoliter = hl, hectoliters = hl;
	/*
	 * 
	 * Surface conversions
	 */
	/** The Constant m2. */
	public final static double m2 = m * m, square_meter = m2, square_meters = m2;

	/** The Constant square inch. */
	public final static double sqin = inch * inch, square_inch = sqin, square_inches = sqin;

	/** The Constant square foot. */
	public final static double sqft = foot * foot, square_foot = sqft, square_feet = sqft;

	/** The Constant square mile. */
	public final static double sqmi = mile * mile, square_mile = sqmi, square_miles = sqmi;

	public final static Map<String, Object> UNITS = new HashMap();

	static {

		for ( Map.Entry<String, GamaColor> entry : GamaColor.colors.entrySet() ) {
			UNITS.put(entry.getKey(), entry.getValue());
		}

		for ( final Field f : IUnits.class.getDeclaredFields() ) {
			try {
				if ( f.getType().equals(double.class) ) {
					UNITS.put(f.getName(), f.getDouble(IUnits.class));
				}
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}