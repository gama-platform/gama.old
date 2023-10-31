/*******************************************************************************************************
 *
 * JsonWriter.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.io.Writer;

/**
 * The Class JsonWriter.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
class JsonWriter {

	/** The Constant CONTROL_CHARACTERS_END. */
	private static final int CONTROL_CHARACTERS_END = 0x001f;

	/** The Constant QUOT_CHARS. */
	private static final char[] QUOT_CHARS = { '\\', '"' };

	/** The Constant BS_CHARS. */
	private static final char[] BS_CHARS = { '\\', '\\' };

	/** The Constant LF_CHARS. */
	private static final char[] LF_CHARS = { '\\', 'n' };

	/** The Constant CR_CHARS. */
	private static final char[] CR_CHARS = { '\\', 'r' };

	/** The Constant TAB_CHARS. */
	private static final char[] TAB_CHARS = { '\\', 't' };
	// In JavaScript, U+2028 and U+2029 characters count as line endings and must be encoded.
	/** The Constant UNICODE_2028_CHARS. */
	// http://stackoverflow.com/questions/2965293/javascript-parse-error-on-u2028-unicode-character
	private static final char[] UNICODE_2028_CHARS = { '\\', 'u', '2', '0', '2', '8' };

	/** The Constant UNICODE_2029_CHARS. */
	private static final char[] UNICODE_2029_CHARS = { '\\', 'u', '2', '0', '2', '9' };

	/** The Constant HEX_DIGITS. */
	private static final char[] HEX_DIGITS =
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/** The writer. */
	protected final Writer writer;

	/**
	 * Instantiates a new json writer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param writer
	 *            the writer
	 * @date 29 oct. 2023
	 */
	JsonWriter(final Writer writer) {
		this.writer = writer;
	}

	/**
	 * Write literal.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param value
	 *            the value
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeLiteral(final String value) throws IOException {
		writer.write(value);
	}

	/**
	 * Write number.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeNumber(final String string) throws IOException {
		writer.write(string);
	}

	/**
	 * Write string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeString(final String string) throws IOException {
		writer.write('"');
		writeJsonString(string);
		writer.write('"');
	}

	/**
	 * Write array open.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeArrayOpen() throws IOException {
		writer.write('[');
	}

	/**
	 * Write array close.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeArrayClose() throws IOException {
		writer.write(']');
	}

	/**
	 * Write array separator.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeArraySeparator() throws IOException {
		writer.write(',');
	}

	/**
	 * Write object open.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeObjectOpen() throws IOException {
		writer.write('{');
	}

	/**
	 * Write object close.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeObjectClose() throws IOException {
		writer.write('}');
	}

	/**
	 * Write member name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeMemberName(final String name) throws IOException {
		writer.write('"');
		writeJsonString(name);
		writer.write('"');
	}

	/**
	 * Write member separator.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeMemberSeparator() throws IOException {
		writer.write(':');
	}

	/**
	 * Write object separator.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeObjectSeparator() throws IOException {
		writer.write(',');
	}

	/**
	 * Write json string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	protected void writeJsonString(final String string) throws IOException {
		int length = string.length();
		int start = 0;
		for (int index = 0; index < length; index++) {
			char[] replacement = getReplacementChars(string.charAt(index));
			if (replacement != null) {
				writer.write(string, start, index - start);
				writer.write(replacement);
				start = index + 1;
			}
		}
		writer.write(string, start, length - start);
	}

	/**
	 * Gets the replacement chars.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param ch
	 *            the ch
	 * @return the replacement chars
	 * @date 29 oct. 2023
	 */
	private static char[] getReplacementChars(final char ch) {
		if (ch > '\\') {
			if (ch < '\u2028' || ch > '\u2029') // The lower range contains 'a' .. 'z'. Only 2 checks required.
				return null;
			return ch == '\u2028' ? UNICODE_2028_CHARS : UNICODE_2029_CHARS;
		}
		if (ch == '\\') return BS_CHARS;
		if (ch > '"') // This range contains '0' .. '9' and 'A' .. 'Z'. Need 3 checks to get here.
			return null;
		if (ch == '"') return QUOT_CHARS;
		if (ch > CONTROL_CHARACTERS_END) return null;
		switch (ch) {
			case '\n':
				return LF_CHARS;
			case '\r':
				return CR_CHARS;
			case '\t':
				return TAB_CHARS;
			default:
				break;
		}
		return new char[] { '\\', 'u', '0', '0', HEX_DIGITS[ch >> 4 & 0x000f], HEX_DIGITS[ch & 0x000f] };
	}

}
