/*******************************************************************************************************
 *
 * PostProcessorConfig.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.processing;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class PostProcessorConfig {

	/** The properties. */
	private Map<String, String> properties = new HashMap<>();

	/** The post processor name. */
	private String postProcessorName;

	/**
	 * Instantiates a new post processor config.
	 *
	 * @param properties
	 *            the properties
	 */
	public PostProcessorConfig(final Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * Instantiates a new post processor config.
	 */
	public PostProcessorConfig() {
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
	public String getPostProcessorName() { return postProcessorName; }

	/**
	 * @param filterName
	 *            The filterName to set.
	 */
	public void setPostProcessorName(final String filterName) { this.postProcessorName = filterName; }
}
