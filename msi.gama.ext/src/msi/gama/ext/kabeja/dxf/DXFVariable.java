/*******************************************************************************************************
 *
 * DXFVariable.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 *
 */
public class DXFVariable {

	/** The values. */
	private final Hashtable<String, String> values = new Hashtable<>();

	/** The name. */
	private String name = "";

	/**
	 * Instantiates a new DXF variable.
	 *
	 * @param name
	 *            the name
	 */
	public DXFVariable(final String name) {
		this.name = name;
	}

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
	 * Gets the value.
	 *
	 * @param name
	 *            the name
	 * @return the value
	 */
	public String getValue(final String name) {
		return values.get(name);
	}

	/**
	 * Gets the integer value.
	 *
	 * @param name
	 *            the name
	 * @return the integer value
	 */
	public int getIntegerValue(final String name) {
		return Integer.parseInt(values.get(name));
	}

	/**
	 * Gets the double value.
	 *
	 * @param name
	 *            the name
	 * @return the double value
	 */
	public double getDoubleValue(final String name) {
		return Double.parseDouble(values.get(name));
	}

	/**
	 * Sets the value.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setValue(final String name, final String value) {
		values.put(name, value);
	}

	/**
	 *
	 * @return a iterator over all keys of this DXFValue
	 */
	public Iterator getValueKeyIterator() { return values.keySet().iterator(); }
}
