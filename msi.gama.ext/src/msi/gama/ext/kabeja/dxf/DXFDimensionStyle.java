/*******************************************************************************************************
 *
 * DXFDimensionStyle.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFDimensionStyle {
	/**
	 * the size of the dimension text
	 */
	public static final String PROPERTY_DIMTXT = "140";

	/**
	 * the leader arrow block
	 */
	public static final String PROPERTY_DIMLDRBLK = "341";

	/**
	 * the color of dimensionline, arrow and helpline
	 */
	public static final String PROPERTY_DIMCLRD = "176";

	/** The Constant PROPERTY_DIMASZ. */
	public static final String PROPERTY_DIMASZ = "41";

	/** The Constant PROPERTY_DIMGAP. */
	public static final String PROPERTY_DIMGAP = "147";

	/** The Constant PROPERTY_DIMSCALE. */
	public static final String PROPERTY_DIMSCALE = "40";

	/**
	 * the textstyle of the dimensiontext
	 */
	public static final String PROPERTY_DIMTXSTY = "340";

	/** The Constant PROPERTY_DIMLWD. */
	public static final String PROPERTY_DIMLWD = "371";

	/** The Constant PROPERTY_DIMADEC. */
	public static final String PROPERTY_DIMADEC = "179";

	/** The Constant PROPERTY_DIMALT. */
	public static final String PROPERTY_DIMALT = "170";

	/** The Constant PROPERTY_DIMALTD. */
	public static final String PROPERTY_DIMALTD = "171";

	/** The Constant PROPERTY_DIMALTF. */
	public static final String PROPERTY_DIMALTF = "143";

	/** The Constant PROPERTY_DIMALTRND. */
	public static final String PROPERTY_DIMALTRND = "148";

	/** The Constant PROPERTY_DIMALTTD. */
	public static final String PROPERTY_DIMALTTD = "274";

	/** The Constant PROPERTY_DIMALTTZ. */
	public static final String PROPERTY_DIMALTTZ = "286";

	/** The Constant PROPERTY_DIMALTU. */
	public static final String PROPERTY_DIMALTU = "273";

	/** The Constant PROPERTY_DIMALTZ. */
	public static final String PROPERTY_DIMALTZ = "285";

	/** The Constant PROPERTY_DIMAPOST. */
	public static final String PROPERTY_DIMAPOST = "4";

	/** The Constant PROPERTY_DIMATFIT. */
	public static final String PROPERTY_DIMATFIT = "289";

	/** The Constant PROPERTY_DIMUNIT. */
	public static final String PROPERTY_DIMUNIT = "275";

	/** The Constant PROPERTY_DIMAZIN. */
	public static final String PROPERTY_DIMAZIN = "79";

	/**
	 * the arrow block
	 */
	public static final String PROPERTY_DIMBLK = "342";

	/**
	 * the first or left arrow block (if different blocks used)
	 */
	public static final String PROPERTY_DIMBLK1 = "343";

	/**
	 * the second or right arrow block (if different blocks used)
	 */
	public static final String PROPERTY_DIMBLK2 = "344";

	/** The Constant PROPERTY_DIMCEN. */
	public static final String PROPERTY_DIMCEN = "141";

	/** The Constant PROPERTY_DIMCLRE. */
	public static final String PROPERTY_DIMCLRE = "177";

	/** The Constant PROPERTY_DIMCLRT. */
	public static final String PROPERTY_DIMCLRT = "178";

	/** The Constant PROPERTY_DIMDEC. */
	public static final String PROPERTY_DIMDEC = "271";

	/** The Constant PROPERTY_DIMDLE. */
	public static final String PROPERTY_DIMDLE = "46";

	/** The Constant PROPERTY_DIMDLI. */
	public static final String PROPERTY_DIMDLI = "43";

	/** The Constant PROPERTY_DIMDSEP. */
	public static final String PROPERTY_DIMDSEP = "278";

	/** The Constant PROPERTY_DIMEXE. */
	public static final String PROPERTY_DIMEXE = "44";

	/** The Constant PROPERTY_DIMEXO. */
	public static final String PROPERTY_DIMEXO = "42";

	/** The Constant PROPERTY_DIMFRAC. */
	public static final String PROPERTY_DIMFRAC = "276";

	/** The Constant PROPERTY_DIMJUST. */
	public static final String PROPERTY_DIMJUST = "280";

	/** The Constant PROPERTY_DIMLFAC. */
	public static final String PROPERTY_DIMLFAC = "144";

	/** The Constant PROPERTY_DIMLIM. */
	public static final String PROPERTY_DIMLIM = "72";

	/** The Constant PROPERTY_DIMLUNIT. */
	public static final String PROPERTY_DIMLUNIT = "277";

	/** The Constant PROPERTY_DIMLWE. */
	public static final String PROPERTY_DIMLWE = "372";

	/** The Constant PROPERTY_DIMPOST. */
	public static final String PROPERTY_DIMPOST = "3";

	/** The Constant PROPERTY_DIMMD. */
	public static final String PROPERTY_DIMMD = "45";

	/** The Constant PROPERTY_DIMSAH. */
	public static final String PROPERTY_DIMSAH = "173";

	/** The Constant PROPERTY_DIMSD1. */
	public static final String PROPERTY_DIMSD1 = "281";

	/** The Constant PROPERTY_DIMSD2. */
	public static final String PROPERTY_DIMSD2 = "282";

	/** The Constant PROPERTY_DIMSE1. */
	public static final String PROPERTY_DIMSE1 = "75";

	/** The Constant PROPERTY_DIMSE2. */
	public static final String PROPERTY_DIMSE2 = "76";

	/** The Constant PROPERTY_DIMSOXD. */
	public static final String PROPERTY_DIMSOXD = "175";

	/** The Constant PROPERTY_DIMRAD. */
	public static final String PROPERTY_DIMRAD = "77";

	/** The Constant PROPERTY_DIMTDEC. */
	public static final String PROPERTY_DIMTDEC = "272";

	/** The Constant PROPERTY_DIMDTFAC. */
	public static final String PROPERTY_DIMDTFAC = "146";

	/** The Constant PROPERTY_DIMTIH. */
	public static final String PROPERTY_DIMTIH = "73";

	/** The Constant PROPERTY_DIMTIX. */
	public static final String PROPERTY_DIMTIX = "174";

	/** The Constant PROPERTY_DIMDIMTM. */
	public static final String PROPERTY_DIMDIMTM = "48";

	/** The Constant PROPERTY_DIMTMOVE. */
	public static final String PROPERTY_DIMTMOVE = "289";

	/** The Constant PROPERTY_DIMTOFL. */
	public static final String PROPERTY_DIMTOFL = "172";

	/** The Constant PROPERTY_DIMTOH. */
	public static final String PROPERTY_DIMTOH = "74";

	/** The Constant PROPERTY_DIMTOL. */
	public static final String PROPERTY_DIMTOL = "71";

	/** The Constant PROPERTY_DIMTOLJ. */
	public static final String PROPERTY_DIMTOLJ = "283";

	/** The Constant PROPERTY_DIMTP. */
	public static final String PROPERTY_DIMTP = "47";

	/** The Constant PROPERTY_DIMTSZ. */
	public static final String PROPERTY_DIMTSZ = "142";

	/** The Constant PROPERTY_DIMTVP. */
	public static final String PROPERTY_DIMTVP = "145";

	/** The Constant PROPERTY_DIMTZIN. */
	public static final String PROPERTY_DIMTZIN = "284";

	/** The Constant PROPERTY_DIMZIN. */
	public static final String PROPERTY_DIMZIN = "78";

	/** The properties. */
	private final HashMap<String, String> properties = new HashMap<>();

	/** The flags. */
	private int flags = 0;

	/** The name. */
	private String name = "";

	/**
	 * Sets the property.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setProperty(final String key, final String value) {
		properties.put(key, value);
	}

	/**
	 * Checks for property.
	 *
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean hasProperty(final String name) {
		return properties.containsKey(name);
	}

	/**
	 * Gets the property.
	 *
	 * @param name
	 *            the name
	 * @return the property
	 */
	public String getProperty(final String name) {
		return properties.get(name);
	}

	/**
	 * Gets the integer property.
	 *
	 * @param name
	 *            the name
	 * @return the integer property
	 */
	public int getIntegerProperty(final String name) {
		String value = properties.get(name);

		return Integer.parseInt(value);
	}

	/**
	 * Gets the integer property.
	 *
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value
	 * @return the integer property
	 */
	public int getIntegerProperty(final String name, final int defaultValue) {
		if (hasProperty(name)) {
			String value = properties.get(name);

			return Integer.parseInt(value);
		}
		return defaultValue;
	}

	/**
	 * Gets the double property.
	 *
	 * @param name
	 *            the name
	 * @return the double property
	 */
	public double getDoubleProperty(final String name) {
		String value = properties.get(name);

		return Double.parseDouble(value);
	}

	/**
	 * Gets the double property.
	 *
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value
	 * @return the double property
	 */
	public double getDoubleProperty(final String name, final double defaultValue) {
		if (hasProperty(name)) {
			String value = properties.get(name);

			return Double.parseDouble(value);
		}
		return defaultValue;
	}

	/**
	 * Gets the boolean property.
	 *
	 * @param name
	 *            the name
	 * @return the boolean property
	 */
	public boolean getBooleanProperty(final String name) {
		String value = properties.get(name);

		if ("1".equals(value)) return true;
		return false;
	}

	/**
	 * Gets the boolean property.
	 *
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value
	 * @return the boolean property
	 */
	public boolean getBooleanProperty(final String name, final boolean defaultValue) {
		if (!hasProperty(name)) return defaultValue;
		String value = properties.get(name);

		if ("1".equals(value)) return true;
		return false;
	}

	/**
	 * Gets the property iterator.
	 *
	 * @return the property iterator
	 */
	public Iterator getPropertyIterator() { return properties.values().iterator(); }

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() { return name; }

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) { this.name = name; }

	/**
	 * Sets the flags.
	 *
	 * @param flags
	 *            the new flags
	 */
	public void setFlags(final int flags) { this.flags = flags; }

	/**
	 * Gets the flags.
	 *
	 * @return the flags
	 */
	public int getFlags() { return flags; }
}
