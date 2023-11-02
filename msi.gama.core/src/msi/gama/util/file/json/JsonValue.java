/*******************************************************************************************************
 *
 * JsonValue.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

import msi.gama.runtime.IScope;

/**
 * Represents a JSON value. This can be a JSON <strong>object</strong>, an <strong> array</strong>, a
 * <strong>number</strong>, a <strong>string</strong>, or one of the literals <strong>true</strong>,
 * <strong>false</strong>, and <strong>null</strong>.
 * <p>
 * The literals <strong>true</strong>, <strong>false</strong>, and <strong>null</strong> are represented by the
 * constants {@link Json#TRUE}, {@link Json#FALSE}, and {@link Json#NULL}.
 * </p>
 * <p>
 * JSON <strong>objects</strong> and <strong>arrays</strong> are represented by the subtypes {@link JsonObject} and
 * {@link JsonArray}. Instances of these types can be created using the public constructors of these classes.
 * </p>
 * <p>
 * Instances that represent JSON <strong>numbers</strong>, <strong>strings</strong> and <strong>boolean</strong> values
 * can be created using the static factory methods {@link Json#valueOf(String)}, {@link Json#valueOf(long)},
 * {@link Json#valueOf(double)}, etc.
 * </p>
 * <p>
 * In order to find out whether an instance of this class is of a certain type, the methods {@link #isObject()},
 * {@link #isArray()}, {@link #isString()}, {@link #isNumber()} etc. can be used.
 * </p>
 * <p>
 * If the type of a JSON value is known, the methods {@link #asObject()}, {@link #asArray()}, {@link #asString()},
 * {@link #asInt()}, etc. can be used to get this value directly in the appropriate target type.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings ("serial") // use default serial UID
public abstract class JsonValue implements Serializable {

	/**
	 * Instantiates a new json value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 oct. 2023
	 */
	JsonValue() {}

	/**
	 * Detects whether this value represents a JSON object. If this is the case, this value is an instance of
	 * {@link JsonObject}.
	 *
	 * @return <code>true</code> if this value is an instance of JsonObject
	 */
	public boolean isObject() { return false; }

	/**
	 * Detects whether this value represents a JSON array. If this is the case, this value is an instance of
	 * {@link JsonArray}.
	 *
	 * @return <code>true</code> if this value is an instance of JsonArray
	 */
	public boolean isArray() { return false; }

	/**
	 * Detects whether this value represents a JSON number.
	 *
	 * @return <code>true</code> if this value represents a JSON number
	 */
	public boolean isNumber() { return false; }

	/**
	 * Detects whether this value represents a JSON string.
	 *
	 * @return <code>true</code> if this value represents a JSON string
	 */
	public boolean isString() { return false; }

	/**
	 * Detects whether this value represents a boolean value.
	 *
	 * @return <code>true</code> if this value represents either the JSON literal <code>true</code> or
	 *         <code>false</code>
	 */
	public boolean isBoolean() { return false; }

	/**
	 * Detects whether this value represents the JSON literal <code>true</code>.
	 *
	 * @return <code>true</code> if this value represents the JSON literal <code>true</code>
	 */
	public boolean isTrue() { return false; }

	/**
	 * Detects whether this value represents the JSON literal <code>false</code>.
	 *
	 * @return <code>true</code> if this value represents the JSON literal <code>false</code>
	 */
	public boolean isFalse() { return false; }

	/**
	 * Detects whether this value represents the JSON literal <code>null</code>.
	 *
	 * @return <code>true</code> if this value represents the JSON literal <code>null</code>
	 */
	public boolean isNull() { return false; }

	/**
	 * Returns this JSON value as {@link JsonObject}, assuming that this value represents a JSON object. If this is not
	 * the case, an exception is thrown.
	 *
	 * @return a JSONObject for this value
	 * @throws UnsupportedOperationException
	 *             if this value is not a JSON object
	 */
	public JsonObject asObject() {
		throw new UnsupportedOperationException("Not an object: " + toString());
	}

	/**
	 * Returns this JSON value as {@link JsonArray}, assuming that this value represents a JSON array. If this is not
	 * the case, an exception is thrown.
	 *
	 * @return a JSONArray for this value
	 * @throws UnsupportedOperationException
	 *             if this value is not a JSON array
	 */
	public JsonArray asArray() {
		throw new UnsupportedOperationException("Not an array: " + toString());
	}

	/**
	 * Returns this JSON value as an <code>int</code> value, assuming that this value represents a JSON number that can
	 * be interpreted as Java <code>int</code>. If this is not the case, an exception is thrown.
	 * <p>
	 * To be interpreted as Java <code>int</code>, the JSON number must neither contain an exponent nor a fraction part.
	 * Moreover, the number must be in the <code>Integer</code> range.
	 * </p>
	 *
	 * @return this value as <code>int</code>
	 * @throws UnsupportedOperationException
	 *             if this value is not a JSON number
	 * @throws NumberFormatException
	 *             if this JSON number can not be interpreted as <code>int</code> value
	 */
	public int asInt() {
		throw new UnsupportedOperationException("Not a number: " + toString());
	}

	/**
	 * Returns this JSON value as a <code>long</code> value, assuming that this value represents a JSON number that can
	 * be interpreted as Java <code>long</code>. If this is not the case, an exception is thrown.
	 * <p>
	 * To be interpreted as Java <code>long</code>, the JSON number must neither contain an exponent nor a fraction
	 * part. Moreover, the number must be in the <code>Long</code> range.
	 * </p>
	 *
	 * @return this value as <code>long</code>
	 * @throws UnsupportedOperationException
	 *             if this value is not a JSON number
	 * @throws NumberFormatException
	 *             if this JSON number can not be interpreted as <code>long</code> value
	 */
	public long asLong() {
		throw new UnsupportedOperationException("Not a number: " + toString());
	}

	/**
	 * Returns this JSON value as a <code>float</code> value, assuming that this value represents a JSON number. If this
	 * is not the case, an exception is thrown.
	 * <p>
	 * If the JSON number is out of the <code>Float</code> range, {@link Float#POSITIVE_INFINITY} or
	 * {@link Float#NEGATIVE_INFINITY} is returned.
	 * </p>
	 *
	 * @return this value as <code>float</code>
	 * @throws UnsupportedOperationException
	 *             if this value is not a JSON number
	 */
	public float asFloat() {
		throw new UnsupportedOperationException("Not a number: " + toString());
	}

	/**
	 * Returns this JSON value as a <code>double</code> value, assuming that this value represents a JSON number. If
	 * this is not the case, an exception is thrown.
	 * <p>
	 * If the JSON number is out of the <code>Double</code> range, {@link Double#POSITIVE_INFINITY} or
	 * {@link Double#NEGATIVE_INFINITY} is returned.
	 * </p>
	 *
	 * @return this value as <code>double</code>
	 * @throws UnsupportedOperationException
	 *             if this value is not a JSON number
	 */
	public double asDouble() {
		throw new UnsupportedOperationException("Not a number: " + toString());
	}

	/**
	 * Returns this JSON value as String, assuming that this value represents a JSON string. If this is not the case, an
	 * exception is thrown.
	 *
	 * @return the string represented by this value
	 * @throws UnsupportedOperationException
	 *             if this value is not a JSON string
	 */
	public String asString() {
		throw new UnsupportedOperationException("Not a string: " + toString());
	}

	/**
	 * Returns this JSON value as a <code>boolean</code> value, assuming that this value is either <code>true</code> or
	 * <code>false</code>. If this is not the case, an exception is thrown.
	 *
	 * @return this value as <code>boolean</code>
	 * @throws UnsupportedOperationException
	 *             if this value is neither <code>true</code> or <code>false</code>
	 */
	public boolean asBoolean() {
		throw new UnsupportedOperationException("Not a boolean: " + toString());
	}

	/**
	 * Writes the JSON representation of this value to the given writer in its minimal form, without any additional
	 * whitespace.
	 * <p>
	 * Writing performance can be improved by using a {@link java.io.BufferedWriter BufferedWriter}.
	 * </p>
	 *
	 * @param writer
	 *            the writer to write this value to
	 * @throws IOException
	 *             if an I/O error occurs in the writer
	 */
	public void writeTo(final Writer writer) throws IOException {
		writeTo(writer, WriterConfig.MINIMAL);
	}

	/**
	 * Writes the JSON representation of this value to the given writer using the given formatting.
	 * <p>
	 * Writing performance can be improved by using a {@link java.io.BufferedWriter BufferedWriter}.
	 * </p>
	 *
	 * @param writer
	 *            the writer to write this value to
	 * @param config
	 *            a configuration that controls the formatting or <code>null</code> for the minimal form
	 * @throws IOException
	 *             if an I/O error occurs in the writer
	 */
	public void writeTo(final Writer writer, final WriterConfig config) throws IOException {
		if (writer == null) throw new NullPointerException("writer is null");
		if (config == null) throw new NullPointerException("config is null");
		WritingBuffer buffer = new WritingBuffer(writer, 128);
		write(config.createWriter(buffer));
		buffer.flush();
	}

	/**
	 * Returns the JSON string for this value in its minimal form, without any additional whitespace. The result is
	 * guaranteed to be a valid input for the method {@link Json#parse(String)} and to create a value that is
	 * <em>equal</em> to this object.
	 *
	 * @return a JSON string that represents this value
	 */
	@Override
	public String toString() {
		return toString(WriterConfig.MINIMAL);
	}

	/**
	 * Returns the JSON string for this value using the given formatting.
	 *
	 * @param config
	 *            a configuration that controls the formatting or <code>null</code> for the minimal form
	 * @return a JSON string that represents this value
	 */
	public String toString(final WriterConfig config) {
		StringWriter writer = new StringWriter();
		try {
			writeTo(writer, config);
		} catch (IOException exception) {
			// StringWriter does not throw IOExceptions
			throw new RuntimeException(exception);
		}
		return writer.toString();
	}

	/**
	 * Indicates whether some other object is "equal to" this one according to the contract specified in
	 * {@link Object#equals(Object)}.
	 * <p>
	 * Two JsonValues are considered equal if and only if they represent the same JSON text. As a consequence, two given
	 * JsonObjects may be different even though they contain the same set of names with the same values, but in a
	 * different order.
	 * </p>
	 *
	 * @param object
	 *            the reference object with which to compare
	 * @return true if this object is the same as the object argument; false otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
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
	abstract void write(JsonWriter writer) throws IOException;

	/**
	 * To gaml value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the object
	 * @date 2 nov. 2023
	 */
	public abstract Object toGamlValue(IScope scope);

}
