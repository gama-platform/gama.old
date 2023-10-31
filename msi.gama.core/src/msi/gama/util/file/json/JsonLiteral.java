/*******************************************************************************************************
 *
 * JsonLiteral.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;

/**
 * The Class JsonLiteral.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
@SuppressWarnings ("serial") // use default serial UID
class JsonLiteral extends JsonValue {

	/** The value. */
	private final String value;

	/** The is null. */
	private final boolean isNull;

	/** The is true. */
	private final boolean isTrue;

	/** The is false. */
	private final boolean isFalse;

	/**
	 * Instantiates a new json literal.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param value
	 *            the value
	 * @date 29 oct. 2023
	 */
	JsonLiteral(final String value) {
		this.value = value;
		isNull = "null".equals(value);
		isTrue = "true".equals(value);
		isFalse = "false".equals(value);
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
		writer.writeLiteral(value);
	}

	/**
	 * To string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @date 29 oct. 2023
	 */
	@Override
	public String toString() {
		return value;
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
		return value.hashCode();
	}

	/**
	 * Checks if is null.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is null
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isNull() { return isNull; }

	/**
	 * Checks if is true.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is true
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isTrue() { return isTrue; }

	/**
	 * Checks if is false.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is false
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isFalse() { return isFalse; }

	/**
	 * Checks if is boolean.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is boolean
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isBoolean() { return isTrue || isFalse; }

	/**
	 * As boolean.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if successful
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean asBoolean() {
		return isNull ? super.asBoolean() : isTrue;
	}

	/**
	 * Equals.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return true, if successful
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean equals(final Object object) {
		if (this == object) return true;
		if ((object == null) || (getClass() != object.getClass())) return false;
		JsonLiteral other = (JsonLiteral) object;
		return value.equals(other.value);
	}

}
