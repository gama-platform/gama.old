/*******************************************************************************************************
 *
 * JsonAbstractObject.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;

/**
 * Represents an abstract JSON object, a set of name/value pairs, where the names are strings and the values are JSON
 * values.
 * <p>
 * Members can be added using the <code>add(String, ...)</code> methods which accept instances of {@link JsonValue},
 * strings, primitive numbers, and boolean values. To modify certain values of an object, use the
 * <code>set(String, ...)</code> methods. Please note that the <code>add</code> methods are faster than <code>set</code>
 * as they do not search for existing members. On the other hand, the <code>add</code> methods do not prevent adding
 * multiple members with the same name. Duplicate names are discouraged but not prohibited by JSON.
 * </p>
 * <p>
 * Members can be accessed by their name using {@link #get(String)}. A list of all names can be obtained from the method
 * {@link #names()}. This class also supports iterating over the members in document order using an {@link #iterator()}
 * or an enhanced for loop:
 * </p>
 *
 * <pre>
 * for (Member member : jsonObject) {
 *   String name = member.getName();
 *   JsonValue value = member.getValue();
 *   ...
 * }
 * </pre>
 * <p>
 * Even though JSON objects are unordered by definition, instances of this class preserve the order of members to allow
 * processing in document order and to guarantee a predictable output.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a <code>JsonObject</code>
 * instance concurrently, while at least one of these threads modifies the contents of this object, access to the
 * instance must be synchronized externally. Failure to do so may lead to an inconsistent state.
 * </p>
 *
 */
@SuppressWarnings ("serial") // use default serial UID
public abstract class JsonAbstractObject<GamlValueType> extends JsonValue implements Iterable<JsonObjectMember> {

	/** The names. */
	protected final List<String> names;

	/** The values. */
	protected final List<JsonValue> values;

	/** The table. */
	private transient HashIndexTable table;

	/** The json. */
	protected final Json json;

	/**
	 * Creates a new empty JsonObject.
	 */
	JsonAbstractObject(final Json json) {
		this.json = json;
		names = new ArrayList<>();
		values = new ArrayList<>();
		table = new HashIndexTable();
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the JSON representation of the
	 * object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @param object
	 *            the object
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonAbstractObject add(final String name, final Object object) {
		add(name, json.valueOf(object));
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the JSON representation of the
	 * specified <code>int</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
	 * exists in the object will append another member with the same name. In order to replace existing members, use the
	 * method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
	 * <em>set</em></strong> (because it does not need to search for existing members). Therefore <em>add</em> should be
	 * preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject add(final String name, final int value) {
		add(name, json.valueOf(value));
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the JSON representation of the
	 * specified <code>long</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
	 * exists in the object will append another member with the same name. In order to replace existing members, use the
	 * method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
	 * <em>set</em></strong> (because it does not need to search for existing members). Therefore <em>add</em> should be
	 * preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject add(final String name, final long value) {
		add(name, json.valueOf(value));
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the JSON representation of the
	 * specified <code>float</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
	 * exists in the object will append another member with the same name. In order to replace existing members, use the
	 * method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
	 * <em>set</em></strong> (because it does not need to search for existing members). Therefore <em>add</em> should be
	 * preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject add(final String name, final float value) {
		add(name, json.valueOf(value));
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the JSON representation of the
	 * specified <code>double</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
	 * exists in the object will append another member with the same name. In order to replace existing members, use the
	 * method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
	 * <em>set</em></strong> (because it does not need to search for existing members). Therefore <em>add</em> should be
	 * preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject add(final String name, final double value) {
		add(name, json.valueOf(value));
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the JSON representation of the
	 * specified <code>boolean</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
	 * exists in the object will append another member with the same name. In order to replace existing members, use the
	 * method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
	 * <em>set</em></strong> (because it does not need to search for existing members). Therefore <em>add</em> should be
	 * preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject add(final String name, final boolean value) {
		add(name, json.valueOf(value));
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the JSON representation of the
	 * specified string.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
	 * exists in the object will append another member with the same name. In order to replace existing members, use the
	 * method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
	 * <em>set</em></strong> (because it does not need to search for existing members). Therefore <em>add</em> should be
	 * preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject add(final String name, final String value) {
		add(name, json.valueOf(value));
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name and the specified JSON value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
	 * exists in the object will append another member with the same name. In order to replace existing members, use the
	 * method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
	 * <em>set</em></strong> (because it does not need to search for existing members). Therefore <em>add</em> should be
	 * preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add, must not be <code>null</code>
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject add(final String name, final JsonValue value) {
		if (name == null) throw new NullPointerException("name is null");
		if (value == null) throw new NullPointerException("value is null");
		table.add(name, names.size());
		names.add(name);
		values.add(value);
		return this;
	}

	/**
	 * Sets the value of the member with the specified name to the specified JSON value. If this object does not contain
	 * a member with this name, a new member is added at the end of the object. If this object contains multiple members
	 * with this name, only the last one is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with members,
	 * the method <code>add(name, value)</code> should be preferred which is much faster (as it does not need to search
	 * for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add, must not be <code>null</code>
	 * @return the object itself, to enable method chaining
	 */
	public JsonAbstractObject set(final String name, final JsonValue value) {
		if (name == null) throw new NullPointerException("name is null");
		if (value == null) throw new NullPointerException("value is null");
		int index = indexOf(name);
		if (index != -1) {
			values.set(index, value);
		} else {
			table.add(name, names.size());
			names.add(name);
			values.add(value);
		}
		return this;
	}

	/**
	 * Removes a member with the specified name from this object. If this object contains multiple members with the
	 * given name, only the last one is removed. If this object does not contain a member with the specified name, the
	 * object is not modified.
	 *
	 * @param name
	 *            the name of the member to remove
	 * @return the value that has been removed or null if the name was not present
	 */
	public JsonValue remove(final String name) {
		if (name == null) throw new NullPointerException("name is null");
		int index = indexOf(name);
		JsonValue result = null;
		if (index != -1) {
			table.remove(index);
			names.remove(index);
			result = values.remove(index);
		}
		return result;
	}

	/**
	 * Checks if a specified member is present as a child of this object. This will not test if this object contains the
	 * literal <code>null</code>, {@link JsonValue#isNull()} should be used for this purpose.
	 *
	 * @param name
	 *            the name of the member to check for
	 * @return whether or not the member is present
	 */
	public boolean contains(final String name) {
		return names.contains(name);
	}

	/**
	 * Returns the value of the member with the specified name in this object. If this object contains multiple members
	 * with the given name, this method will return the last one.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @return the value of the last member with the specified name, or <code>null</code> if this object does not
	 *         contain a member with that name
	 */
	public JsonValue get(final String name) {
		if (name == null) throw new NullPointerException("name is null");
		int index = indexOf(name);
		return index != -1 ? values.get(index) : null;
	}

	/**
	 * Returns the <code>int</code> value of the member with the specified name in this object. If this object does not
	 * contain a member with this name, the given default value is returned. If this object contains multiple members
	 * with the given name, the last one will be picked. If this member's value does not represent a JSON number or if
	 * it cannot be interpreted as Java <code>int</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the given default value if this object does not
	 *         contain a member with that name
	 */
	public int getInt(final String name, final int defaultValue) {
		JsonValue value = get(name);
		return value != null ? value.asInt() : defaultValue;
	}

	/**
	 * Returns the <code>long</code> value of the member with the specified name in this object. If this object does not
	 * contain a member with this name, the given default value is returned. If this object contains multiple members
	 * with the given name, the last one will be picked. If this member's value does not represent a JSON number or if
	 * it cannot be interpreted as Java <code>long</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the given default value if this object does not
	 *         contain a member with that name
	 */
	public long getLong(final String name, final long defaultValue) {
		JsonValue value = get(name);
		return value != null ? value.asLong() : defaultValue;
	}

	/**
	 * Returns the <code>float</code> value of the member with the specified name in this object. If this object does
	 * not contain a member with this name, the given default value is returned. If this object contains multiple
	 * members with the given name, the last one will be picked. If this member's value does not represent a JSON number
	 * or if it cannot be interpreted as Java <code>float</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the given default value if this object does not
	 *         contain a member with that name
	 */
	public float getFloat(final String name, final float defaultValue) {
		JsonValue value = get(name);
		return value != null ? value.asFloat() : defaultValue;
	}

	/**
	 * Returns the <code>double</code> value of the member with the specified name in this object. If this object does
	 * not contain a member with this name, the given default value is returned. If this object contains multiple
	 * members with the given name, the last one will be picked. If this member's value does not represent a JSON number
	 * or if it cannot be interpreted as Java <code>double</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the given default value if this object does not
	 *         contain a member with that name
	 */
	public double getDouble(final String name, final double defaultValue) {
		JsonValue value = get(name);
		return value != null ? value.asDouble() : defaultValue;
	}

	/**
	 * Returns the <code>boolean</code> value of the member with the specified name in this object. If this object does
	 * not contain a member with this name, the given default value is returned. If this object contains multiple
	 * members with the given name, the last one will be picked. If this member's value does not represent a JSON
	 * <code>true</code> or <code>false</code> value, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the given default value if this object does not
	 *         contain a member with that name
	 */
	public boolean getBoolean(final String name, final boolean defaultValue) {
		JsonValue value = get(name);
		return value != null ? value.asBoolean() : defaultValue;
	}

	/**
	 * Returns the <code>String</code> value of the member with the specified name in this object. If this object does
	 * not contain a member with this name, the given default value is returned. If this object contains multiple
	 * members with the given name, the last one is picked. If this member's value does not represent a JSON string, an
	 * exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the given default value if this object does not
	 *         contain a member with that name
	 */
	public String getString(final String name, final String defaultValue) {
		JsonValue value = get(name);
		return value != null ? value.asString() : defaultValue;
	}

	/**
	 * Returns the number of members (name/value pairs) in this object.
	 *
	 * @return the number of members in this object
	 */
	public int size() {
		return names.size();
	}

	/**
	 * Returns <code>true</code> if this object contains no members.
	 *
	 * @return <code>true</code> if this object contains no members
	 */
	public boolean isEmpty() { return names.isEmpty(); }

	/**
	 * Returns a list of the names in this object in document order. The returned list is backed by this object and will
	 * reflect subsequent changes. It cannot be used to modify this object. Attempts to modify the returned list will
	 * result in an exception.
	 *
	 * @return a list of the names in this object
	 */
	public List<String> names() {
		return Collections.unmodifiableList(names);
	}

	/**
	 * Returns an iterator over the members of this object in document order. The returned iterator cannot be used to
	 * modify this object.
	 *
	 * @return an iterator over the members of this object
	 */
	@Override
	public Iterator<JsonObjectMember> iterator() {
		final Iterator<String> namesIterator = names.iterator();
		final Iterator<JsonValue> valuesIterator = values.iterator();
		return new Iterator<>() {

			@Override
			public boolean hasNext() {
				return namesIterator.hasNext();
			}

			@Override
			public JsonObjectMember next() {
				String name = namesIterator.next();
				JsonValue value = valuesIterator.next();
				return new JsonObjectMember(name, value);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	/**
	 * Write.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param writer
	 *            the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	@Override
	final void write(final JsonWriter writer) throws IOException {
		writer.writeObjectOpen();
		writeMembers(writer);
		writer.writeObjectClose();
	}

	/**
	 * Write members.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param writer
	 *            the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 4 nov. 2023
	 */
	protected void writeMembers(final JsonWriter writer) throws IOException {
		Iterator<String> namesIterator = names.iterator();
		Iterator<JsonValue> valuesIterator = values.iterator();
		if (namesIterator.hasNext()) {
			writer.writeMemberName(namesIterator.next());
			writer.writeMemberSeparator();
			valuesIterator.next().write(writer);
			while (namesIterator.hasNext()) {
				writer.writeObjectSeparator();
				writer.writeMemberName(namesIterator.next());
				writer.writeMemberSeparator();
				valuesIterator.next().write(writer);
			}
		}
	}

	/**
	 * Hash code.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 29 oct. 2023
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + names.hashCode();
		result = 31 * result + values.hashCode();
		return result;
	}

	/**
	 * Equals.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		JsonAbstractObject other = (JsonAbstractObject) obj;
		return names.equals(other.names) && values.equals(other.values);
	}

	/**
	 * Index of.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the int
	 * @date 29 oct. 2023
	 */
	int indexOf(final String name) {
		int index = table.get(name);
		if (index != -1 && name.equals(names.get(index))) return index;
		return names.lastIndexOf(name);
	}

	/**
	 * Read object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param inputStream
	 *            the input stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @date 29 oct. 2023
	 */
	private synchronized void readObject(final ObjectInputStream inputStream)
			throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		table = new HashIndexTable();
		updateHashIndex();
	}

	/**
	 * Update hash index.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 oct. 2023
	 */
	private void updateHashIndex() {
		int size = names.size();
		for (int i = 0; i < size; i++) { table.add(names.get(i), i); }
	}

	@Override
	public abstract GamlValueType toGamlValue(final IScope scope);

	/**
	 * To map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return the i map
	 * @date 4 nov. 2023
	 */
	protected IMap<String, Object> toMap(final IScope scope) {
		IMap<String, Object> result = GamaMapFactory.create();
		Iterator<String> namesIterator = names.iterator();
		Iterator<JsonValue> valuesIterator = values.iterator();
		while (namesIterator.hasNext()) { result.put(namesIterator.next(), valuesIterator.next().toGamlValue(scope)); }
		return result;
	}

}
