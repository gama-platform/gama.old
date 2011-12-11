/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.precompiler.util;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Written by drogoul Modified on 25 déc. 2010
 * 
 * @todo Description
 * 
 */
public class MathUtils {

	/** Constant field PRECISION. */
	public final static int PRECISION = 360;
	/** Constant field PI. */
	public static final double PI = java.lang.Math.PI;
	/** Constant field PI_2. */
	public static final double PI_2 = PI * 2;
	/** Constant field PREC_MIN_1. */
	public final static int PREC_MIN_1 = PRECISION - 1;
	/** Constant field PI_4. */
	public final static double PI_4 = PI / 4d;
	/** Constant field PI_34. */
	public final static double PI_34 = PI_4 * 3d;
	/** Constant field PI_2_OVER1. */
	public final static double PI_2_OVER1 = 1f / PI_2;
	/** Constant field PI_2_OVER1_P. */
	public final static double PI_2_OVER1_P = PI_2_OVER1 * PRECISION;
	/** Constant field RAD_SLICE. */
	public final static double RAD_SLICE = PI_2 / PRECISION;
	/** Constant field toRad. */
	public static final double toRad = Math.PI / 180;
	/** Constant field toDeg. */
	public static final double toDeg = 180 / Math.PI;
	/** Constant field sinTable. */
	public final static double[] sinTable = new double[PRECISION];
	/** Constant field cosTable. */
	public final static double[] cosTable = new double[PRECISION];
	/** Constant field tanTable. */
	public final static double[] tanTable = new double[PRECISION];

	/** Constant field UNITS. */
	public final static Map<String, Double> UNITS = new HashMap();

	static {
		double rad = 0;
		for ( int i = 0; i < PRECISION; i++ ) {
			rad = i * RAD_SLICE;
			sinTable[i] = java.lang.Math.sin(rad);
			cosTable[i] = java.lang.Math.cos(rad);
			tanTable[i] = java.lang.Math.tan(rad);
			// OutputManager.debug("Tan " + i + " = " + tanTable[i]);
		}
		for ( final Field f : Units.class.getDeclaredFields() ) {
			try {
				if ( f.getType().equals(double.class) ) {
					UNITS.put(f.getName(), f.getDouble(Units.class));
				}
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static interface Units {

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
		public final static double mile = 1.609344f * km, miles = mile;

		/** The Constant yard. */
		public final static double yard = 0.9144f * m, yards = yard;

		/** The Constant inch. */
		public final static double inch = 2.54f * cm, inches = inch;

		/** The Constant foot. */
		public final static double foot = 30.48f * cm, feet = foot, ft = foot;

		/*
		 * 
		 * Time conversions
		 */
		/** The Constant s. */
		public final static double sec = 1, second = sec, seconds = sec;

		/** The Constant mn. */
		public final static double minute = 60 * sec, minutes = minute;

		/** The Constant h. */
		public final static double h = 60 * minute, hour = h, hours = h;

		/** The Constant d. */
		public final static double day = 24 * h, days = day;

		/** The Constant month. */
		public final static double month = 30 * day, months = month;

		/** The Constant y. */
		public final static double year = 12 * month, years = year;

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
		public final static double pound = 0.45359237 * kg, lb = pound, pounds = pound,
			lbm = pound;
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

	}

	/**
	 * Rad to index.
	 * 
	 * @param radians the radians
	 * 
	 * @return the int
	 */
	public static final int radToIndex(final double radians) {
		return (int) (radians * PI_2_OVER1_P) & PREC_MIN_1;
	}

	/**
	 * Sin.
	 * 
	 * @param radians the radians
	 * 
	 * @return the double
	 */
	public static final double sin(final double radians) {
		return sinTable[radToIndex(radians)];
	}

	/**
	 * Check heading : keep it in the 0 - 360 degrees interval.
	 * 
	 * @param newHeading the new heading
	 * 
	 * @return the integer
	 */
	public static int checkHeading(final int newHeading) {
		int result = newHeading;
		while (result < 0) {
			result += PRECISION;
		}
		return result % PRECISION;
	}

	/**
	 * Cos.
	 * 
	 * @param radians the radians
	 * 
	 * @return the double
	 */
	public static final double cos(final double radians) {
		return cosTable[radToIndex(radians)];
	}

	/**
	 * Cos.
	 * 
	 * @param degrees the degrees
	 * 
	 * @return the double
	 */
	public static final double cos(final int degrees) {
		return cosTable[degrees];
	}

	public static double aTan2(final double y, final double x) {
		final double abs_y = Math.abs(y);
		double angle;
		if ( x >= 0d ) {
			final double r = (x - abs_y) / (x + abs_y);
			angle = PI_4 - PI_4 * r;
		} else {
			final double r = (x + abs_y) / (abs_y - x);
			angle = PI_34 - PI_4 * r;
		}
		return y < 0d ? -angle : angle;
	}

	public static double hypot(final double x1, final double x2, final double y1, final double y2) {
		final double dx = x2 - x1;
		final double dy = y2 - y1;
		double a = dx * dx + dy * dy;
		final long x = Double.doubleToLongBits(a) >> 32;
		double y = Double.longBitsToDouble(x + 1072632448 << 31);
		// repeat the following line for more precision
		y = (y + a / y) * 0.5;
		y = (y + a / y) * 0.5;
		// y = (y + a / y) * 0.5;
		return y;
	}

	/**
	 * Sin.
	 * 
	 * @param degrees the degrees
	 * 
	 * @return the double
	 */
	public static final double sin(final int degrees) {
		return sinTable[degrees];
	}

	/**
	 * Tan.
	 * 
	 * @param radians the radians
	 * 
	 * @return the double
	 */
	public static final double tan(final double radians) {
		return tanTable[radToIndex(radians)];
	}

	/**
	 * Tan.
	 * 
	 * @param degrees the degrees
	 * 
	 * @return the double
	 */
	public static final double tan(final int degrees) {
		return tanTable[degrees];
	}

	public static final double floor(final double d) {
		int i;
		if ( d >= 0 ) {
			i = (int) d;
		} else {
			i = -((int) -d) - 1;
		}
		return i;
	}

	public static int round(final double d) {
		int i;
		if ( d >= 0 ) {
			i = (int) (d + .5);
		} else {
			i = (int) (d - .5);
		}
		return i;
	}

	public static final double ceil(final double d) {
		int i;
		if ( d >= 0 ) {
			i = -((int) -d) + 1;
		} else {
			i = (int) d;
		}
		return i;
	}

	public static boolean even(final int rv) {
		return rv % 2 == 0;
	}

	/**
	 * @param doubleValue
	 * @param i
	 * @return
	 */
	public static double truncate(final double x, final int precision) {
		double fract;
		double whole;
		double mult;
		if ( x > 0 ) {
			whole = MathUtils.floor(x);
			mult = pow(10.0, precision);
			fract = MathUtils.floor((x - whole) * mult) / mult;
		} else {
			whole = MathUtils.ceil(x);
			mult = pow(10, precision);
			fract = MathUtils.ceil((x - whole) * mult) / mult;
		}
		return whole + fract;
	}

	/**
	 * @param doubleValue
	 * @param doubleValue2
	 * @return
	 */
	public static Double pow(final double a, final double b) {
		// Math.pow(a, b);
		// Based on the Taylor series approximation.

		int oc = -1; // used to alternate math symbol (+,-)
		int iter = 20; // number of iterations
		double p, x, x2, sumX, sumY; // is exponent a whole number?
		if ( b - MathUtils.floor(b) == 0 ) { // return base^exponent
			double x1 = a;
			int y = (int) b;
			switch (y) {
				case -3:
					return 1 / (x1 * x1 * x1);
				case -2:
					return 1 / (x1 * x1);
				case -1:
					return 1 / x1;
				case 0:
					return 1d;
				case 1:
					return x1;
				case 2:
					return x1 * x1;
				case 3:
					return x1 * x1 * x1;
				case 4:
					return x1 * x1 * x1 * x1;
				default:
					if ( y > 0 ) {
						double z = 1;
						do {
							if ( (y & 1) != 0 ) {
								z *= x1;
							}
							x1 *= x1;
							y >>= 1;
						} while (y != 0);
						return z;
					}
					y = -y;
					double z = 1;
					do {
						if ( (y & 1) != 0 ) {
							z /= x1;
						}
						x1 *= x1;
						y >>= 1;
					} while (y != 0);
					return z;
			}
		}
		// true if base is greater
		// than 1
		boolean gt1 = Math.sqrt((a - 1) * (a - 1)) <= 1 ? false : true;
		x = gt1 ? a / (a - 1) : // base is greater than 1
			a - 1; // base is 1 or less
		sumX = gt1 ? 1 / x : // base is greater than 1
			x; // base is 1 or less
		for ( int i = 2; i < iter; i++ ) { // find x^iteration
			p = x;
			for ( int j = 1; j < i; j++ ) {
				p *= x;
			}
			double xTemp = gt1 ? 1 / (i * p) : // base is greater than 1
				p / i; // base is 1 or less
			sumX = gt1 ? sumX + xTemp : // base is greater than 1
				sumX + xTemp * oc; // base is 1 or less
			oc *= -1; // change math symbol (+,-)
		}
		x2 = b * sumX;
		sumY = 1 + x2; // our estimate
		for ( int i = 2; i <= iter; i++ ) { // find x2^iteration
			p = x2;
			for ( int j = 1; j < i; j++ ) {
				p *= x2; // multiply iterations (ex: 3 iterations = 3*2*1)
			}
			int yTemp = 2;
			for ( int j = i; j > 2; j-- ) {
				yTemp *= j; // add to estimate (ex: 3rd iteration => (x2^3)/(3*2*1) )
			}
			sumY += p / yTemp;
		}
		return sumY; // return our estimate
	}
}
