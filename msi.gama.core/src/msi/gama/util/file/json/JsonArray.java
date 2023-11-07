/*******************************************************************************************************
 *
 * JsonArray.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;

/**
 * Represents a JSON array, an ordered collection of JSON values.
 * <p>
 * Elements can be added using the <code>add(...)</code> methods which accept instances of {@link JsonValue}, strings,
 * primitive numbers, and boolean values. To replace an element of an array, use the <code>set(int, ...)</code> methods.
 * </p>
 * <p>
 * Elements can be accessed by their index using {@link #get(int)}. This class also supports iterating over the elements
 * in document order using an {@link #iterator()} or an enhanced for loop:
 * </p>
 *
 * <pre>
 * for (JsonValue value : jsonArray) {
 *   ...
 * }
 * </pre>
 * <p>
 * An equivalent {@link List} can be obtained from the method {@link #values()}.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a <code>JsonArray</code>
 * instance concurrently, while at least one of these threads modifies the contents of this array, access to the
 * instance must be synchronized externally. Failure to do so may lead to an inconsistent state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings ("serial") // use default serial UID
public class JsonArray extends JsonValue implements Iterable<JsonValue>, IJsonConstants {

	/** The values. */
	private final List<JsonValue> values;

	/** The json. */
	private final Json json;

	/**
	 * Creates a new empty JsonArray.
	 */
	JsonArray(final Json json) {
		this.json = json;
		values = new ArrayList<>();
	}

	/**
	 * Appends the JSON representation of the specified <code>int</code> value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JsonArray add(final int value) {
		values.add(json.valueOf(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>long</code> value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JsonArray add(final long value) {
		values.add(json.valueOf(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>float</code> value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JsonArray add(final float value) {
		values.add(json.valueOf(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>double</code> value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JsonArray add(final double value) {
		values.add(json.valueOf(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>boolean</code> value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JsonArray add(final boolean value) {
		values.add(json.valueOf(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified string to the end of this array.
	 *
	 * @param value
	 *            the string to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JsonArray add(final String value) {
		values.add(json.valueOf(value));
		return this;
	}

	/**
	 * Appends the specified JSON value to the end of this array.
	 *
	 * @param value
	 *            the JsonValue to add to the array, must not be <code>null</code>
	 * @return the array itself, to enable method chaining
	 */
	public JsonArray add(final JsonValue value) {
		if (value == null) throw new NullPointerException("value is null");
		values.add(value);
		return this;
	}

	/**
	 * Adds the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return the json array
	 * @date 29 oct. 2023
	 */
	public JsonArray add(final Object object) {
		values.add(json.valueOf(object));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the JSON representation of the specified
	 * <code>int</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray set(final int index, final int value) {
		values.set(index, json.valueOf(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the JSON representation of the specified
	 * <code>long</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray set(final int index, final long value) {
		values.set(index, json.valueOf(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the JSON representation of the specified
	 * <code>float</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray set(final int index, final float value) {
		values.set(index, json.valueOf(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the JSON representation of the specified
	 * <code>double</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray set(final int index, final double value) {
		values.set(index, json.valueOf(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the JSON representation of the specified
	 * <code>boolean</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray set(final int index, final boolean value) {
		values.set(index, json.valueOf(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the JSON representation of the specified
	 * string.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the string to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray set(final int index, final String value) {
		values.set(index, json.valueOf(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the specified JSON value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position, must not be <code>null</code>
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray set(final int index, final JsonValue value) {
		if (value == null) throw new NullPointerException("value is null");
		values.set(index, value);
		return this;
	}

	/**
	 * Removes the element at the specified index from this array.
	 *
	 * @param index
	 *            the index of the element to remove
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonArray remove(final int index) {
		values.remove(index);
		return this;
	}

	/**
	 * Returns the number of elements in this array.
	 *
	 * @return the number of elements in this array
	 */
	public int size() {
		return values.size();
	}

	/**
	 * Returns <code>true</code> if this array contains no elements.
	 *
	 * @return <code>true</code> if this array contains no elements
	 */
	public boolean isEmpty() { return values.isEmpty(); }

	/**
	 * Returns the value of the element at the specified position in this array.
	 *
	 * @param index
	 *            the index of the array element to return
	 * @return the value of the element at the specified position
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code> or <code>index &gt;= size</code>
	 */
	public JsonValue get(final int index) {
		return values.get(index);
	}

	/**
	 * Returns a list of the values in this array in document order. The returned list is backed by this array and will
	 * reflect subsequent changes. It cannot be used to modify this array. Attempts to modify the returned list will
	 * result in an exception.
	 *
	 * @return a list of the values in this array
	 */
	public List<JsonValue> values() {
		return Collections.unmodifiableList(values);
	}

	/**
	 * Returns an iterator over the values of this array in document order. The returned iterator cannot be used to
	 * modify this array.
	 *
	 * @return an iterator over the values of this array
	 */
	@Override
	public Iterator<JsonValue> iterator() {
		final Iterator<JsonValue> iterator = values.iterator();
		return new Iterator<>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public JsonValue next() {
				return iterator.next();
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
		writer.writeArrayOpen();
		Iterator<JsonValue> iterator = iterator();
		if (iterator.hasNext()) {
			iterator.next().write(writer);
			while (iterator.hasNext()) {
				writer.writeArraySeparator();
				iterator.next().write(writer);
			}
		}
		writer.writeArrayClose();
	}

	/**
	 * Checks if is array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is array
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isArray() { return true; }

	/**
	 * As array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the json array
	 * @date 29 oct. 2023
	 */
	@Override
	public JsonArray asArray() {
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
		return values.hashCode();
	}

	/**
	 * Indicates whether a given object is "equal to" this JsonArray. An object is considered equal if it is also a
	 * <code>JsonArray</code> and both arrays contain the same list of values.
	 * <p>
	 * If two JsonArrays are equal, they will also produce the same JSON output.
	 * </p>
	 *
	 * @param object
	 *            the object to be compared with this JsonArray
	 * @return <tt>true</tt> if the specified object is equal to this JsonArray, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		JsonArray other = (JsonArray) object;
		return values.equals(other.values);
	}

	@Override
	public IList toGamlValue(final IScope scope) {
		IList<Object> result = GamaListFactory.create();
		for (JsonValue v : values) { result.add(v.toGamlValue(scope)); }
		return result;
	}

}
