/*******************************************************************************************************
 *
 * SAXFilterConfig.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SAXFilterConfig {

	/** The properties. */
	private final Map<String, String> properties;

	/** The filter name. */
	private String filterName;

	/**
	 * Instantiates a new SAX filter config.
	 *
	 * @param properties
	 *            the properties
	 */
	public SAXFilterConfig(final Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * Instantiates a new SAX filter config.
	 */
	public SAXFilterConfig() {
		this(new HashMap<>());
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Map getProperties() { return this.properties; }

	/**
	 * Adds the property.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void addProperty(final String name, final String value) {
		this.properties.put(name, value);
	}

	/**
	 * @return Returns the filterName.
	 */
	public String getFilterName() { return filterName; }

	/**
	 * @param filterName
	 *            The filterName to set.
	 */
	public void setFilterName(final String filterName) { this.filterName = filterName; }
}
