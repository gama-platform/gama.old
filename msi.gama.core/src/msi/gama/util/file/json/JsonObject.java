/*******************************************************************************************************
 *
 * JsonObject.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
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

/**
 * Represents a JSON object, a set of name/value pairs, where the names are strings and the values are JSON values.
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
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings ("serial") // use default serial UID
public class JsonObject extends JsonValue implements Iterable<msi.gama.util.file.json.JsonObject.Member> {

	/** The names. */
	private final List<String> names;

	/** The values. */
	private final List<JsonValue> values;

	/** The table. */
	private transient HashIndexTable table;

	/** The json. */
	private final Json json;

	/**
	 * Creates a new empty JsonObject.
	 */
	JsonObject(final Json json) {
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
	public JsonObject add(final String name, final Object object) {
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
	public JsonObject add(final String name, final int value) {
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
	public JsonObject add(final String name, final long value) {
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
	public JsonObject add(final String name, final float value) {
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
	public JsonObject add(final String name, final double value) {
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
	public JsonObject add(final String name, final boolean value) {
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
	public JsonObject add(final String name, final String value) {
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
	public JsonObject add(final String name, final JsonValue value) {
		if (name == null) throw new NullPointerException("name is null");
		if (value == null) throw new NullPointerException("value is null");
		table.add(name, names.size());
		names.add(name);
		values.add(value);
		return this;
	}

	/**
	 * Removes a member with the specified name from this object. If this object contains multiple members with the
	 * given name, only the last one is removed. If this object does not contain a member with the specified name, the
	 * object is not modified.
	 *
	 * @param name
	 *            the name of the member to remove
	 * @return the object itself, to enable method chaining
	 */
	public JsonObject remove(final String name) {
		if (name == null) throw new NullPointerException("name is null");
		int index = indexOf(name);
		if (index != -1) {
			table.remove(index);
			names.remove(index);
			values.remove(index);
		}
		return this;
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
	public Iterator<Member> iterator() {
		final Iterator<String> namesIterator = names.iterator();
		final Iterator<JsonValue> valuesIterator = values.iterator();
		return new Iterator<>() {

			@Override
			public boolean hasNext() {
				return namesIterator.hasNext();
			}

			@Override
			public Member next() {
				String name = namesIterator.next();
				JsonValue value = valuesIterator.next();
				return new Member(name, value);
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
	void write(final JsonWriter writer) throws IOException {
		writer.writeObjectOpen();
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
		writer.writeObjectClose();
	}

	/**
	 * Checks if is object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is object
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isObject() { return true; }

	/**
	 * As object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	@Override
	public JsonObject asObject() {
		return this;
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
		JsonObject other = (JsonObject) obj;
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

	/**
	 * Represents a member of a JSON object, a pair of a name and a value.
	 */
	public static class Member {

		/** The name. */
		private final String name;

		/** The value. */
		private final JsonValue value;

		/**
		 * Instantiates a new member.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param name
		 *            the name
		 * @param value
		 *            the value
		 * @date 29 oct. 2023
		 */
		Member(final String name, final JsonValue value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * Returns the name of this member.
		 *
		 * @return the name of this member, never <code>null</code>
		 */
		public String getName() { return name; }

		/**
		 * Returns the value of this member.
		 *
		 * @return the value of this member, never <code>null</code>
		 */
		public JsonValue getValue() { return value; }

		@Override
		public int hashCode() {
			int result = 1;
			result = 31 * result + name.hashCode();
			result = 31 * result + value.hashCode();
			return result;
		}

		/**
		 * Indicates whether a given object is "equal to" this JsonObject. An object is considered equal if it is also a
		 * <code>JsonObject</code> and both objects contain the same members <em>in the same order</em>.
		 * <p>
		 * If two JsonObjects are equal, they will also produce the same JSON output.
		 * </p>
		 *
		 * @param object
		 *            the object to be compared with this JsonObject
		 * @return <tt>true</tt> if the specified object is equal to this JsonObject, <code>false</code> otherwise
		 */
		@Override
		public boolean equals(final Object object) {
			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;
			Member other = (Member) object;
			return name.equals(other.name) && value.equals(other.value);
		}

	}

	/**
	 * The Class HashIndexTable.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 oct. 2023
	 */
	static class HashIndexTable {

		/** The hash table. */
		private final byte[] hashTable = new byte[32]; // must be a power of two

		/**
		 * Instantiates a new hash index table.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @date 29 oct. 2023
		 */
		HashIndexTable() {}

		/**
		 * Instantiates a new hash index table.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param original
		 *            the original
		 * @date 29 oct. 2023
		 */
		HashIndexTable(final HashIndexTable original) {
			System.arraycopy(original.hashTable, 0, hashTable, 0, hashTable.length);
		}

		/**
		 * Adds the.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param name
		 *            the name
		 * @param index
		 *            the index
		 * @date 29 oct. 2023
		 */
		void add(final String name, final int index) {
			int slot = hashSlotFor(name);
			if (index < 0xff) {
				// increment by 1, 0 stands for empty
				hashTable[slot] = (byte) (index + 1);
			} else {
				hashTable[slot] = 0;
			}
		}

		/**
		 * Removes the.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param index
		 *            the index
		 * @date 29 oct. 2023
		 */
		void remove(final int index) {
			for (int i = 0; i < hashTable.length; i++) {
				if ((hashTable[i] & 0xff) == index + 1) {
					hashTable[i] = 0;
				} else if ((hashTable[i] & 0xff) > index + 1) { hashTable[i]--; }
			}
		}

		/**
		 * Gets the.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param name
		 *            the name
		 * @return the int
		 * @date 29 oct. 2023
		 */
		int get(final Object name) {
			int slot = hashSlotFor(name);
			// subtract 1, 0 stands for empty
			return (hashTable[slot] & 0xff) - 1;
		}

		/**
		 * Hash slot for.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param element
		 *            the element
		 * @return the int
		 * @date 29 oct. 2023
		 */
		private int hashSlotFor(final Object element) {
			return element.hashCode() & hashTable.length - 1;
		}

	}

}
