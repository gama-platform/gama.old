/*******************************************************************************************************
 *
 * JsonNull.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;

import msi.gama.runtime.IScope;

/**
 * The Class JsonLiteral.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
@SuppressWarnings ("serial") // use default serial UID
class JsonNull extends JsonValue {

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
		writer.writeLiteral("null");
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
		return "null";
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
		return "null".hashCode();
	}

	/**
	 * Checks if is null.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is null
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isNull() { return true; }

	/**
	 * Checks if is true.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is true
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isTrue() { return false; }

	/**
	 * Checks if is false.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is false
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isFalse() { return false; }

	/**
	 * Checks if is boolean.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is boolean
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isBoolean() { return false; }

	/**
	 * As boolean.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if successful
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean asBoolean() {
		return false;
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
		if (object == null || getClass() != object.getClass()) return false;
		return true;
	}

	@Override
	public Object toGamlValue(final IScope scope) {
		return null;
	}

}
