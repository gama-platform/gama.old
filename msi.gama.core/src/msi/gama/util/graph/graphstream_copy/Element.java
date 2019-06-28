/*
 * Copyright 2006 - 2016 Stefan Balev <stefan.balev@graphstream-project.org> Julien Baudry
 * <julien.baudry@graphstream-project.org> Antoine Dutot <antoine.dutot@graphstream-project.org> Yoann Pigné
 * <yoann.pigne@graphstream-project.org> Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 *
 * This file is part of GraphStream <http://graphstream-project.org>.
 *
 * GraphStream is a library whose purpose is to handle static or dynamic graph, create them from scratch, file or any
 * source and display them.
 *
 * This program is free software distributed under the terms of two licenses, the CeCILL-C license that fits European
 * law, and the GNU Lesser General Public License. You can use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following URL <http://www.cecill.info> or under
 * the terms of the GNU LGPL as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C and LGPL licenses and
 * that you accept their terms.
 */
package msi.gama.util.graph.graphstream_copy;

import java.util.Collection;

/**
 * An element is a part of a graph (node, edge, the graph itself).
 *
 * <p>
 * An interface that defines common method to manipulate identifiers, attributes and indices of the elements (graph,
 * nodes and edges) of a graph.
 * </p>
 * *
 * <p>
 * Attributes can be any object and are identified by arbitrary strings. Some attributes are stored as numbers or
 * strings and are in this case named number, label or vector. There are utility methods to handle these attributes
 * ({@link #getNumber(String)}, {@link #getLabel(String)}) or {@link #getVector(String)}, however they are also
 * accessible through the more general method {@link #getAttribute(String)}.
 * </p>
 *
 * <h3>Important</h3>
 * <p>
 * Implementing classes should indicate the complexity of their implementation for each method.
 * </p>
 *
 * @since July 12 2007
 *
 */
public interface Element {
	/**
	 * Unique identifier of this element.
	 *
	 * @return The identifier value.
	 */
	String getId();

	/**
	 * The current index of this element
	 *
	 * @return The index value
	 */
	int getIndex();

	/**
	 * Get the attribute object bound to the given key. The returned value may be null to indicate the attribute does
	 * not exists or is not supported.
	 *
	 * @param key
	 *            Name of the attribute to search.
	 * @return The object bound to the given key or null if no object match this attribute name.
	 */
	// Object getAttribute( String key );
	<T> T getAttribute(String key);

	/**
	 * Get the attribute object bound to the given key if it is an instance of the given class. Some The returned value
	 * maybe null to indicate the attribute does not exists or is not an instance of the given class.
	 *
	 * @param key
	 *            The attribute name to search.
	 * @param clazz
	 *            The expected attribute class.
	 * @return The object bound to the given key or null if no object match this attribute.
	 */
	// Object getAttribute( String key, Class<?> clazz );
	<T> T getAttribute(String key, Class<T> clazz);

	/**
	 * Does this element store a value for the given attribute key?
	 *
	 * @param key
	 *            The name of the attribute to search.
	 * @return True if a value is present for this attribute.
	 */
	boolean hasAttribute(String key);

	/**
	 * An iterable view on the set of attribute keys usable within a for-each loop.
	 *
	 * @return an iterable view on attribute keys.
	 */
	Iterable<String> getEachAttributeKey();

	/**
	 * An unmodifiable view on the set of attribute keys.
	 *
	 * @return an unmodifiable collection containing the attribute keys.
	 */
	Collection<String> getAttributeKeySet();

	/**
	 * Add or replace the value of an attribute. Existing attributes are overwritten silently. All classes inheriting
	 * from Number can be considered as numbers. All classes inheriting from CharSequence can be considered as labels.
	 * You can pass zero, one or more arguments for the attribute values. If no value is given, a boolean with value
	 * "true" is added. If there is more than one value, an array is stored. If there is only one value, the value is
	 * stored (but not in an array).
	 *
	 * @param attribute
	 *            The attribute name.
	 * @param values
	 *            The attribute value or set of values.
	 */
	void addAttribute(String attribute, Object... values);

	/**
	 * Like {@link #addAttribute(String, Object...)} but for consistency.
	 *
	 * @param attribute
	 *            The attribute name.
	 * @param values
	 *            The attribute value or array of values.
	 * @see #addAttribute(String, Object...)
	 */
	void changeAttribute(String attribute, Object... values);

	/**
	 * Like {@link #addAttribute(String, Object...)} but for consistency.
	 *
	 * @param attribute
	 *            The attribute name.
	 * @param values
	 *            The attribute value or array of values.
	 * @see #addAttribute(String, Object...)
	 */
	void setAttribute(String attribute, Object... values);

	/**
	 * Remove an attribute. Non-existent attributes errors are ignored silently.
	 *
	 * @param attribute
	 *            Name of the attribute to remove.
	 */
	void removeAttribute(String attribute);

	/**
	 * Number of attributes stored in this element.
	 *
	 * @return the number of attributes.
	 */
	int getAttributeCount();
}