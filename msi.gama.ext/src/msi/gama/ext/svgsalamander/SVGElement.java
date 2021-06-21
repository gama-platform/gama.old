/*
 * SVGElement.java
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
 * Created on January 26, 2004, 1:59 AM
 */

package msi.gama.ext.svgsalamander;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
abstract public class SVGElement implements Serializable, IShapeElement {
	public static final long serialVersionUID = 0;

	public static final int AT_CSS = 0;
	public static final int AT_XML = 1;
	public static final int AT_AUTO = 2; // Check CSS first, then XML

	public static final String SVG_NS = "http://www.w3.org/2000/svg";

	private SVGElement parent = null;

	/**
	 * Styles defined for this elemnt via the <b>style</b> attribute.
	 */
	private final HashMap<String, StyleAttribute> inlineStyles = new HashMap<>();

	/**
	 * Presentation attributes set for this element. Ie, any attribute other than the <b>style</b> attribute.
	 */
	private final HashMap<String, StyleAttribute> presAttribs = new HashMap<>();

	boolean dirty = true;

	/** Creates a new instance of SVGElement */
	public SVGElement() {
		this(null);
	}

	public SVGElement(final SVGElement parent) {
		this.parent = parent;
	}

	public void setParent(final SVGElement parent) {
		this.parent = parent;
	}

	/**
	 * Called during SAX load process to notify that this tag has begun the process of being loaded
	 *
	 * @param attrs
	 *            - Attributes of this tag
	 * @param helper
	 *            - An object passed to all SVG elements involved in this build process to aid in sharing information.
	 */
	@Override
	public void loaderStartElement(final Attributes attrs, final IShapeElement parent2) {
		// Set identification info
		this.parent = (SVGElement) parent2;
		// Place all other attributes into the presentation attribute list
		final int numAttrs = attrs.getLength();
		for (int i = 0; i < numAttrs; i++) {
			final String name = attrs.getQName(i);
			presAttribs.put(name, new StyleAttribute(name, attrs.getValue(i)));
		}
	}

	public void addAttribute(final String name, final int attribType, final String value) throws SVGElementException {
		// Alter layout for id attribute
		switch (attribType) {
			case AT_CSS:
				inlineStyles.put(name, new StyleAttribute(name, value));
				return;
			case AT_XML:
				presAttribs.put(name, new StyleAttribute(name, value));
				return;
		}
		throw new SVGElementException(this, "Invalid attribute type " + attribType);
	}

	/**
	 * Called after the start element but before the end element to indicate each child tag that has been processed
	 */
	@Override
	public void loaderAddChild(final IShapeElement ele) throws SVGElementException {
		SVGElement svgEle = (SVGElement) ele;
		svgEle.setParent(this);
	}

	/*
	 * Returns the named style attribute. Checks for inline styles first, then internal and extranal style sheets, and
	 * finally checks for presentation attributes.
	 *
	 * @param styleName - Name of attribute to return
	 *
	 * @param recursive - If true and this object does not contain the named style attribute, checks attributes of
	 * parents abck to root until one found.
	 */
	public boolean getStyle(final StyleAttribute attrib) throws SVGException {
		return getStyle(attrib, true);
	}

	public void setAttribute(final String name, final int attribType, final String value) throws SVGElementException {
		StyleAttribute styAttr;

		switch (attribType) {
			case AT_CSS: {
				styAttr = inlineStyles.get(name);
				break;
			}
			case AT_XML: {
				styAttr = presAttribs.get(name);
				break;
			}
			case AT_AUTO: {
				styAttr = inlineStyles.get(name);

				if (styAttr == null) { styAttr = presAttribs.get(name); }
				break;
			}
			default:
				throw new SVGElementException(this, "Invalid attribute type " + attribType);
		}

		if (styAttr == null) throw new SVGElementException(this,
				"Could not find attribute " + name + " Make sure to create attribute before setting it.");
		styAttr.setStringValue(value);
	}

	/**
	 * Copies the current style into the passed style attribute. Checks for inline styles first, then internal and
	 * extranal style sheets, and finally checks for presentation attributes. Recursively checks parents.
	 *
	 * @param attrib
	 *            - Attribute to write style data to. Must have it's name set to the name of the style being queried.
	 * @param recursive
	 *            - If true and this object does not contain the named style attribute, checks attributes of parents
	 *            abck to root until one found.
	 */
	public boolean getStyle(final StyleAttribute attrib, final boolean recursive) throws SVGException {
		final String styName = attrib.getName();
		// Check for local inline styles
		final StyleAttribute styAttr = inlineStyles.get(styName);
		attrib.setStringValue(styAttr == null ? "" : styAttr.getStringValue());
		// Return if we've found a non animated style
		if (styAttr != null) return true;
		// Check for presentation attribute
		final StyleAttribute presAttr = presAttribs.get(styName);
		attrib.setStringValue(presAttr == null ? "" : presAttr.getStringValue());
		// Return if we've found a presentation attribute instead
		if (presAttr != null) return true;
		// If we're recursive, check parents
		if (recursive) { if (parent != null) return parent.getStyle(attrib, true); }
		// Unsuccessful reading style attribute
		return false;
	}

	/**
	 * @return the raw style value of this attribute. Does not take the presentation value or animation into
	 *         consideration. Used by animations to determine the base to animate from.
	 */
	public StyleAttribute getStyleAbsolute(final String styName) {
		// Check for local inline styles
		return inlineStyles.get(styName);
	}

	/**
	 * Copies the presentation attribute into the passed one.
	 *
	 * @return - True if attribute was read successfully
	 */
	public boolean getPres(final StyleAttribute attrib) throws SVGException {
		final String presName = attrib.getName();

		// Make sure we have a coresponding presentation attribute
		final StyleAttribute presAttr = presAttribs.get(presName);
		// Copy presentation value directly
		attrib.setStringValue(presAttr == null ? "" : presAttr.getStringValue());
		// Return if we found presentation attribute
		if (presAttr != null) return true;

		return false;
	}

	/**
	 * @return the raw presentation value of this attribute. Ignores any modifications applied by style attributes or
	 *         animation. Used by animations to determine the starting point to animate from
	 */
	public StyleAttribute getPresAbsolute(final String styName) {
		// Check for local inline styles
		return presAttribs.get(styName);
	}

	static protected AffineTransform parseTransform(final String val) throws SVGException {
		final Matcher matchExpression = Pattern.compile("\\w+\\([^)]*\\)").matcher("");

		final AffineTransform retXform = new AffineTransform();

		matchExpression.reset(val);
		while (matchExpression.find()) {
			retXform.concatenate(parseSingleTransform(matchExpression.group()));
		}

		return retXform;
	}

	static public AffineTransform parseSingleTransform(final String val) throws SVGException {
		final Matcher matchWord = Pattern.compile("[-.\\w]+").matcher("");
		final AffineTransform retXform = new AffineTransform();
		matchWord.reset(val);
		if (!matchWord.find()) // Return identity transformation if no data present (eg, empty string)
			return retXform;
		final String function = matchWord.group().toLowerCase();
		final LinkedList<String> termList = new LinkedList<>();
		while (matchWord.find()) {
			termList.add(matchWord.group());
		}

		final double[] terms = new double[termList.size()];
		final Iterator<String> it = termList.iterator();
		int count = 0;
		while (it.hasNext()) {
			terms[count++] = XMLParseUtil.parseDouble(it.next());
		}

		// Calculate transformation
		if (function.equals("matrix")) {
			retXform.setTransform(terms[0], terms[1], terms[2], terms[3], terms[4], terms[5]);
		} else if (function.equals("translate")) {
			retXform.setToTranslation(terms[0], terms[1]);
		} else if (function.equals("scale")) {
			if (terms.length > 1) {
				retXform.setToScale(terms[0], terms[1]);
			} else {
				retXform.setToScale(terms[0], terms[0]);
			}
		} else if (function.equals("rotate")) {
			if (terms.length > 2) {
				retXform.setToRotation(Math.toRadians(terms[0]), terms[1], terms[2]);
			} else {
				retXform.setToRotation(Math.toRadians(terms[0]));
			}
		} else if (function.equals("skewx")) {
			retXform.setToShear(Math.toRadians(terms[0]), 0.0);
		} else if (function.equals("skewy")) {
			retXform.setToShear(0.0, Math.toRadians(terms[0]));
		} else
			throw new SVGException("Unknown transform type");

		return retXform;
	}

}
