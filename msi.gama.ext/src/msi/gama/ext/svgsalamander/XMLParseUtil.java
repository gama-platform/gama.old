/*
 * XMLParseUtil.java
 *
 *
 * The Salamander Project - 2D and 3D graphics libraries in Java Copyright (C) 2004 Mark McKay
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Mark McKay can be contacted at mark@kitfox.com. Salamander and other projects can be found at http://www.kitfox.com
 *
 * Created on February 18, 2004, 1:49 PM
 */

package msi.gama.ext.svgsalamander;

import java.awt.Toolkit;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class XMLParseUtil {
	static final Matcher fpMatch =
			Pattern.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)(\\%|in|cm|mm|pt|pc|px|em|ex)?").matcher("");
	static final Matcher intMatch = Pattern.compile("[-+]?\\d+").matcher("");

	/** Creates a new instance of XMLParseUtil */
	private XMLParseUtil() {}

	/**
	 * Scans the tag's children and returns the first text element found
	 */
	public static String getTagText(final Element ele) {
		final NodeList nl = ele.getChildNodes();
		final int size = nl.getLength();

		Node node = null;
		int i = 0;
		for (; i < size; i++) {
			node = nl.item(i);
			if (node instanceof Text) {
				break;
			}
		}
		if (i == size || node == null) { return null; }

		return ((Text) node).getData();
	}

	/**
	 * Returns the first node that is a direct child of root with the coresponding name. Does not search children of
	 * children.
	 */
	public static Element getFirstChild(final Element root, final String name) {
		final NodeList nl = root.getChildNodes();
		final int size = nl.getLength();
		for (int i = 0; i < size; i++) {
			final Node node = nl.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			final Element ele = (Element) node;
			if (ele.getTagName().equals(name)) { return ele; }
		}

		return null;
	}

	public static String[] parseStringList(final String list) {
		// final Pattern patWs = Pattern.compile("\\s+");
		final Matcher matchWs = Pattern.compile("[^\\s]+").matcher("");
		matchWs.reset(list);

		final LinkedList<String> matchList = new LinkedList<>();
		while (matchWs.find()) {
			matchList.add(matchWs.group());
		}

		final String[] retArr = new String[matchList.size()];
		return matchList.toArray(retArr);
	}

	public static boolean isDouble(final String val) {
		fpMatch.reset(val);
		return fpMatch.matches();
	}

	public static double parseDouble(final String val) {
		/*
		 * if (val == null) return 0.0;
		 *
		 * double retVal = 0.0; try { retVal = Double.parseDouble(val); } catch (Exception e) {} return retVal;
		 */
		return findDouble(val);
	}

	/**
	 * Searches the given string for the first floating point number it contains, parses and returns it.
	 */
	public synchronized static double findDouble(final String original) {
		String val = original;
		if (val == null) { return 0; }

		fpMatch.reset(val);
		try {
			if (!fpMatch.find()) { return 0; }
		} catch (final StringIndexOutOfBoundsException e) {
			System.err.println("XMLParseUtil: regex parse problem: '" + val + "'");
			e.printStackTrace();
		}

		val = fpMatch.group(1);
		// System.err.println("Parsing " + val);

		double retVal = 0;
		try {
			retVal = Double.parseDouble(val);

			float pixPerInch;
			try {
				pixPerInch = Toolkit.getDefaultToolkit().getScreenResolution();
			} catch (final NoClassDefFoundError err) {
				// Default value for headless X servers
				pixPerInch = 72;
			}
			final float inchesPerCm = .3936f;
			final String units = fpMatch.group(6);

			if ("%".equals(units)) {
				retVal /= 100;
			} else if ("in".equals(units)) {
				retVal *= pixPerInch;
			} else if ("cm".equals(units)) {
				retVal *= inchesPerCm * pixPerInch;
			} else if ("mm".equals(units)) {
				retVal *= inchesPerCm * pixPerInch * .1f;
			} else if ("pt".equals(units)) {
				retVal *= 1f / 72f * pixPerInch;
			} else if ("pc".equals(units)) {
				retVal *= 1f / 6f * pixPerInch;
			}
		} catch (final Exception e) {}
		return retVal;
	}

	/**
	 * Scans an input string for double values. For each value found, places in a list. This method regards any
	 * characters not part of a floating point value to be seperators. Thus this will parse whitespace seperated, comma
	 * seperated, and many other separation schemes correctly.
	 */
	public synchronized static double[] parseDoubleList(final String list) {
		if (list == null) { return null; }

		fpMatch.reset(list);

		final LinkedList<Double> doubList = new LinkedList<>();
		while (fpMatch.find()) {
			final String val = fpMatch.group(1);
			doubList.add(Double.valueOf(val));
		}

		final double[] retArr = new double[doubList.size()];
		final Iterator<Double> it = doubList.iterator();
		int idx = 0;
		while (it.hasNext()) {
			retArr[idx++] = it.next().doubleValue();
		}

		return retArr;
	}

	public static float parseFloat(final String val) {
		/*
		 * if (val == null) return 0f;
		 *
		 * float retVal = 0f; try { retVal = Float.parseFloat(val); } catch (Exception e) {} return retVal;
		 */
		return findFloat(val);
	}

	/**
	 * Searches the given string for the first floating point number it contains, parses and returns it.
	 */
	public synchronized static float findFloat(final String original) {
		String val = original;
		if (val == null) { return 0f; }

		fpMatch.reset(val);
		if (!fpMatch.find()) { return 0f; }

		val = fpMatch.group(1);
		// System.err.println("Parsing " + val);

		float retVal = 0f;
		try {
			retVal = Float.parseFloat(val);
			final String units = fpMatch.group(6);
			if ("%".equals(units)) {
				retVal /= 100;
			}
		} catch (final Exception e) {}
		return retVal;
	}

	public synchronized static float[] parseFloatList(final String list) {
		if (list == null) { return null; }

		fpMatch.reset(list);

		final LinkedList<Float> floatList = new LinkedList<>();
		while (fpMatch.find()) {
			final String val = fpMatch.group(1);
			floatList.add(Float.valueOf(val));
		}

		final float[] retArr = new float[floatList.size()];
		final Iterator<Float> it = floatList.iterator();
		int idx = 0;
		while (it.hasNext()) {
			retArr[idx++] = it.next().floatValue();
		}

		return retArr;
	}

	public static int parseInt(final String val) {
		if (val == null) { return 0; }

		int retVal = 0;
		try {
			retVal = Integer.parseInt(val);
		} catch (final Exception e) {}
		return retVal;
	}

	/**
	 * Searches the given string for the first integer point number it contains, parses and returns it.
	 */
	public static int findInt(final String original) {
		String val = original;
		if (val == null) { return 0; }

		intMatch.reset(val);
		if (!intMatch.find()) { return 0; }

		val = intMatch.group();
		// System.err.println("Parsing " + val);

		int retVal = 0;
		try {
			retVal = Integer.parseInt(val);
		} catch (final Exception e) {}
		return retVal;
	}

	public static int[] parseIntList(final String list) {
		if (list == null) { return null; }

		intMatch.reset(list);

		final LinkedList<Integer> intList = new LinkedList<>();
		while (intMatch.find()) {
			final String val = intMatch.group();
			intList.add(Integer.valueOf(val));
		}

		final int[] retArr = new int[intList.size()];
		final Iterator<Integer> it = intList.iterator();
		int idx = 0;
		while (it.hasNext()) {
			retArr[idx++] = it.next().intValue();
		}

		return retArr;
	}

	/*
	 * public static int parseHex(String val) { int retVal = 0;
	 *
	 * for (int i = 0; i < val.length(); i++) { retVal <<= 4;
	 *
	 * char ch = val.charAt(i); if (ch >= '0' && ch <= '9') { retVal |= ch - '0'; } else if (ch >= 'a' && ch <= 'z') {
	 * retVal |= ch - 'a' + 10; } else if (ch >= 'A' && ch <= 'Z') { retVal |= ch - 'A' + 10; } else throw new
	 * RuntimeException(); }
	 *
	 * return retVal; }
	 */
	/**
	 * The input string represents a ratio. Can either be specified as a double number on the range of [0.0 1.0] or as a
	 * percentage [0% 100%]
	 */
	public static double parseRatio(final String val) {
		if (val == null || val.equals("")) { return 0.0; }

		if (val.charAt(val.length() - 1) == '%') {
			parseDouble(val.substring(0, val.length() - 1));
		}
		return parseDouble(val);
	}

	public static NumberWithUnits parseNumberWithUnits(final String val) {
		if (val == null) { return null; }

		return new NumberWithUnits(val);
	}

	/*
	 * public static Color parseColor(String val) { Color retVal = null;
	 *
	 * if (val.charAt(0) == '#') { String hexStrn = val.substring(1);
	 *
	 * if (hexStrn.length() == 3) { hexStrn = "" + hexStrn.charAt(0) + hexStrn.charAt(0) + hexStrn.charAt(1) +
	 * hexStrn.charAt(1) + hexStrn.charAt(2) + hexStrn.charAt(2); } int hexVal = parseHex(hexStrn);
	 *
	 * retVal = new Color(hexVal); } else { final Matcher rgbMatch = Pattern.compile("rgb\\((\\d+),(\\d+),(\\d+)\\)",
	 * Pattern.CASE_INSENSITIVE).matcher("");
	 *
	 * rgbMatch.reset(val); if (rgbMatch.matches()) { int r = Integer.parseInt(rgbMatch.group(1)); int g =
	 * Integer.parseInt(rgbMatch.group(2)); int b = Integer.parseInt(rgbMatch.group(3)); retVal = new Color(r, g, b); }
	 * else { Color lookupCol = ColorTable.instance().lookupColor(val); if (lookupCol != null) retVal = lookupCol; } }
	 *
	 * return retVal; }
	 */
	/**
	 * Parses the given attribute of this tag and returns it as a String.
	 */
	public static String getAttribString(final Element ele, final String name) {
		return ele.getAttribute(name);
	}

	/**
	 * Parses the given attribute of this tag and returns it as an int.
	 */
	public static int getAttribInt(final Element ele, final String name) {
		final String sval = ele.getAttribute(name);
		int val = 0;
		try {
			val = Integer.parseInt(sval);
		} catch (final Exception e) {}

		return val;
	}

	/**
	 * Parses the given attribute of this tag as a hexadecimal encoded string and returns it as an int
	 */
	public static int getAttribIntHex(final Element ele, final String name) {
		final String sval = ele.getAttribute(name);
		int val = 0;
		try {
			val = Integer.parseInt(sval, 16);
		} catch (final Exception e) {}

		return val;
	}

	/**
	 * Parses the given attribute of this tag and returns it as a float
	 */
	public static float getAttribFloat(final Element ele, final String name) {
		final String sval = ele.getAttribute(name);
		float val = 0.0f;
		try {
			val = Float.parseFloat(sval);
		} catch (final Exception e) {}

		return val;
	}

	/**
	 * Parses the given attribute of this tag and returns it as a double.
	 */
	public static double getAttribDouble(final Element ele, final String name) {
		final String sval = ele.getAttribute(name);
		double val = 0.0;
		try {
			val = Double.parseDouble(sval);
		} catch (final Exception e) {}

		return val;
	}

	/**
	 * Parses the given attribute of this tag and returns it as a boolean. Essentially compares the lower case textual
	 * value to the string "true"
	 */
	public static boolean getAttribBoolean(final Element ele, final String name) {
		final String sval = ele.getAttribute(name);

		return sval.toLowerCase().equals("true");
	}

	public static URL getAttribURL(final Element ele, final String name, final URL docRoot) {
		try {
			return new URL(docRoot, ele.getAttribute(name));
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Returns the first ReadableXMLElement with the given name
	 */
	public static ReadableXMLElement getElement(final Class<?> classType, final Element root, final String name,
			final URL docRoot) {
		if (root == null) { return null; }

		// Do not process if not a LoadableObject
		if (!ReadableXMLElement.class.isAssignableFrom(classType)) { return null; }

		final NodeList nl = root.getChildNodes();
		final int size = nl.getLength();
		for (int i = 0; i < size; i++) {
			final Node node = nl.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			final Element ele = (Element) node;
			if (!ele.getTagName().equals(name)) {
				continue;
			}

			ReadableXMLElement newObj = null;
			try {
				newObj = (ReadableXMLElement) classType.newInstance();
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
			newObj.read(ele, docRoot);

			return newObj;
		}

		return null;
	}

	/**
	 * Returns a HashMap of nodes that are children of root. All nodes will be of class classType and have a tag name of
	 * 'name'. 'key' is an attribute of tag 'name' who's string value will be used as the key in the HashMap
	 */
	public static HashMap<String, ReadableXMLElement> getElementHashMap(final Class<?> classType, final Element root,
			final String name, final String key, final URL docRoot) {
		if (root == null) { return null; }

		// Do not process if not a LoadableObject
		if (!ReadableXMLElement.class.isAssignableFrom(classType)) { return null; }

		final HashMap<String, ReadableXMLElement> retMap = new HashMap<>();

		/*
		 * Class[] params = {Element.class, URL.class}; Method loadMethod = null; try { loadMethod =
		 * classType.getMethod("load", params); } catch (Exception e) { e.printStackTrace(); return null; }
		 *
		 */
		final NodeList nl = root.getChildNodes();
		final int size = nl.getLength();
		for (int i = 0; i < size; i++) {
			final Node node = nl.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			final Element ele = (Element) node;
			if (!ele.getTagName().equals(name)) {
				continue;
			}

			ReadableXMLElement newObj = null;
			try {
				newObj = (ReadableXMLElement) classType.newInstance();
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
			newObj.read(ele, docRoot);
			/*
			 * Object[] args = {ele, source}; Object obj = null; try { obj = loadMethod.invoke(null, args); } catch
			 * (Exception e) { e.printStackTrace(); }
			 *
			 */

			final String keyVal = getAttribString(ele, key);
			retMap.put(keyVal, newObj);
		}

		return retMap;
	}

	public static HashSet<ReadableXMLElement> getElementHashSet(final Class<?> classType, final Element root,
			final String name, final URL docRoot) {
		if (root == null) { return null; }

		// Do not process if not a LoadableObject
		if (!ReadableXMLElement.class.isAssignableFrom(classType)) { return null; }

		final HashSet<ReadableXMLElement> retSet = new HashSet<>();

		/*
		 * Class[] params = {Element.class, URL.class}; Method loadMethod = null; try { loadMethod =
		 * classType.getMethod("load", params); } catch (Exception e) { e.printStackTrace(); return null; }
		 */

		final NodeList nl = root.getChildNodes();
		final int size = nl.getLength();
		for (int i = 0; i < size; i++) {
			final Node node = nl.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			final Element ele = (Element) node;
			if (!ele.getTagName().equals(name)) {
				continue;
			}

			ReadableXMLElement newObj = null;
			try {
				newObj = (ReadableXMLElement) classType.newInstance();
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
			newObj.read(ele, docRoot);
			/*
			 * Object[] args = {ele, source}; Object obj = null; try { obj = loadMethod.invoke(null, args); } catch
			 * (Exception e) { e.printStackTrace(); }
			 */

			retSet.add(newObj);
		}

		return retSet;
	}

	public static LinkedList<ReadableXMLElement> getElementLinkedList(final Class<?> classType, final Element root,
			final String name, final URL docRoot) {
		if (root == null) { return null; }

		// Do not process if not a LoadableObject
		if (!ReadableXMLElement.class.isAssignableFrom(classType)) { return null; }

		final NodeList nl = root.getChildNodes();
		final LinkedList<ReadableXMLElement> elementCache = new LinkedList<>();
		final int size = nl.getLength();
		for (int i = 0; i < size; i++) {
			final Node node = nl.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			final Element ele = (Element) node;
			if (!ele.getTagName().equals(name)) {
				continue;
			}

			ReadableXMLElement newObj = null;
			try {
				newObj = (ReadableXMLElement) classType.newInstance();
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
			newObj.read(ele, docRoot);

			elementCache.addLast(newObj);
		}

		return elementCache;
	}

	public static Object[] getElementArray(final Class<?> classType, final Element root, final String name,
			final URL docRoot) {
		if (root == null) { return null; }

		// Do not process if not a LoadableObject
		if (!ReadableXMLElement.class.isAssignableFrom(classType)) { return null; }

		final LinkedList<ReadableXMLElement> elementCache = getElementLinkedList(classType, root, name, docRoot);

		final Object[] retArr = (Object[]) Array.newInstance(classType, elementCache.size());
		return elementCache.toArray(retArr);
	}

	/**
	 * Takes a number of tags of name 'name' that are children of 'root', and looks for attributes of 'attrib' on them.
	 * Converts attributes to an int and returns in an array.
	 */
	public static int[] getElementArrayInt(final Element root, final String name, final String attrib) {
		if (root == null) { return null; }

		final NodeList nl = root.getChildNodes();
		final LinkedList<Integer> elementCache = new LinkedList<>();
		final int size = nl.getLength();

		for (int i = 0; i < size; i++) {
			final Node node = nl.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			final Element ele = (Element) node;
			if (!ele.getTagName().equals(name)) {
				continue;
			}

			final String valS = ele.getAttribute(attrib);
			int eleVal = 0;
			try {
				eleVal = Integer.parseInt(valS);
			} catch (final Exception e) {}

			elementCache.addLast(new Integer(eleVal));
		}

		final int[] retArr = new int[elementCache.size()];
		final Iterator<Integer> it = elementCache.iterator();
		int idx = 0;
		while (it.hasNext()) {
			retArr[idx++] = it.next().intValue();
		}

		return retArr;
	}

	/**
	 * Takes a number of tags of name 'name' that are children of 'root', and looks for attributes of 'attrib' on them.
	 * Converts attributes to an int and returns in an array.
	 */
	public static String[] getElementArrayString(final Element root, final String name, final String attrib) {
		if (root == null) { return null; }

		final NodeList nl = root.getChildNodes();
		final LinkedList<String> elementCache = new LinkedList<>();
		final int size = nl.getLength();

		for (int i = 0; i < size; i++) {
			final Node node = nl.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			final Element ele = (Element) node;
			if (!ele.getTagName().equals(name)) {
				continue;
			}

			final String valS = ele.getAttribute(attrib);

			elementCache.addLast(valS);
		}

		final String[] retArr = new String[elementCache.size()];
		final Iterator<String> it = elementCache.iterator();
		int idx = 0;
		while (it.hasNext()) {
			retArr[idx++] = it.next();
		}

		return retArr;
	}

	/**
	 * Takes a CSS style string and retursn a hash of them.
	 *
	 * @param styleString
	 *            - A CSS formatted string of styles. Eg,
	 *            "font-size:12;fill:#d32c27;fill-rule:evenodd;stroke-width:1pt;"
	 */
	public static HashMap<String, StyleAttribute> parseStyle(final String styleString) {
		return parseStyle(styleString, new HashMap<String, StyleAttribute>());
	}

	/**
	 * Takes a CSS style string and retursn a hash of them.
	 *
	 * @param styleString
	 *            - A CSS formatted string of styles. Eg,
	 *            "font-size:12;fill:#d32c27;fill-rule:evenodd;stroke-width:1pt;"
	 * @param map
	 *            - A map to which these styles will be added
	 */
	public static HashMap<String, StyleAttribute> parseStyle(final String styleString,
			final HashMap<String, StyleAttribute> map) {
		final Pattern patSemi = Pattern.compile(";");
		final Pattern patColonSpace = Pattern.compile(":");

		// Strips left and right whitespace
		final Matcher matcherContent = Pattern.compile("\\s*([^\\s](.*[^\\s])?)\\s*").matcher("");

		final String[] styles = patSemi.split(styleString);

		for (final String style : styles) {
			final String[] vals = patColonSpace.split(style);

			matcherContent.reset(vals[0]);
			matcherContent.matches();
			vals[0] = matcherContent.group(1);

			matcherContent.reset(vals[1]);
			matcherContent.matches();
			vals[1] = matcherContent.group(1);

			map.put(vals[0], new StyleAttribute(vals[0], vals[1]));
		}

		return map;
	}
}
