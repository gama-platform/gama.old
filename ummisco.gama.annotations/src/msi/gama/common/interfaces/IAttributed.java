/*******************************************************************************************************
 *
 * IAttributed.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.util.Map;

/**
 * Represents objects that are provided with attributes (collection of string-value pairs)
 *
 * @author drogoul
 *
 */
public interface IAttributed {

	/**
	 * Gets the attributes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param createIfNeeded
	 *            whether or not to create the attributes map if it is does not exist. If true, the map should be
	 *            created if it is null.
	 * @return a map containing the attributes, an empty map if no attributes are defined or null if no attributes can
	 *         be defined
	 * @date 17 sept. 2023
	 */
	Map<String, Object> getAttributes(boolean createIfNeeded);

	/**
	 * Allows to retrieve the value stored at key "key"
	 *
	 * @return the value stored at key "key". Returns null if no such key exists. However, please note that null is a
	 *         valid value, which means that receiving null when calling this method does not necessarily mean that the
	 *         key is absent. Use hasAttribute(Object key) to verify the presence of a key
	 */
	default Object getAttribute(final String key) {
		Map<String, Object> attributes = getAttributes(false);
		return attributes == null ? null : attributes.get(key);
	}

	/**
	 * Allows to set the value stored at key "key". A new entry is created when "key" is not already present, otherwise
	 * the previous occurrence is replaced.
	 *
	 */

	default void setAttribute(final String key, final Object value) {
		Map<String, Object> attributes = getAttributes(true);
		if (attributes == null) return;
		attributes.put(key, value);
	}

	/**
	 * Answers whether or not this object has any value set at key "key".
	 *
	 * @return true if the object has such an attribute, false otherwise
	 */
	default boolean hasAttribute(final String key) {
		Map<String, Object> attributes = getAttributes(false);
		if (attributes == null) return false;
		return attributes.containsKey(key);
	}

	/**
	 * Allows to visit the attributes like a map. Returns true if all the attributes have been visited, false otherwise.
	 */

	default void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {
		if (visitor == null) return;
		Map<String, Object> attributes = getAttributes(false);
		if (attributes == null) return;
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			if (!visitor.process(entry.getKey(), entry.getValue())) return;
		}
	}

	/**
	 * Copy all the attributes of the other instance of IAttributed
	 */

	default void copyAttributesOf(final IAttributed source) {
		if (source == null) return;
		setAttributes(source.getAttributes(false));
	}

	/**
	 * Sets the attributes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param attr
	 *            the attr
	 * @date 21 sept. 2023
	 */
	default void setAttributes(final Map<String, Object> attr) {
		if (attr == null) return;
		Map<String, Object> thisAttributes = getAttributes(true);
		if (thisAttributes == null) return;
		thisAttributes.putAll(attr);
	}

}
