/*******************************************************************************************************
 *
 * JsonParser.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A streaming parser for JSON text. The parser reports all events to a given handler.
 */
public class JsonParser {

	/** The Constant MAX_NESTING_LEVEL. */
	private static final int MAX_NESTING_LEVEL = 1000;

	/** The Constant MIN_BUFFER_SIZE. */
	private static final int MIN_BUFFER_SIZE = 10;

	/** The Constant DEFAULT_BUFFER_SIZE. */
	private static final int DEFAULT_BUFFER_SIZE = 1024;

	/** The handler. */
	private final JsonHandler<Object, Object> handler;

	/** The reader. */
	private Reader reader;

	/** The buffer. */
	private char[] buffer;

	/** The buffer offset. */
	private int bufferOffset;

	/** The index. */
	private int index;

	/** The fill. */
	private int fill;

	/** The line. */
	private int line;

	/** The line offset. */
	private int lineOffset;

	/** The current. */
	private int current;

	/** The capture buffer. */
	private StringBuilder captureBuffer;

	/** The capture start. */
	private int captureStart;

	/** The nesting level. */
	private int nestingLevel;

	/*
	 * | bufferOffset v [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t] < input [l|m|n|o|p|q|r|s|t|?|?] < buffer ^ ^ | index
	 * fill
	 */

	/**
	 * Creates a new JsonParser with the given handler. The parser will report all parser events to this handler.
	 *
	 * @param handler
	 *            the handler to process parser events
	 */
	@SuppressWarnings ("unchecked")
	public JsonParser(final JsonHandler<?, ?> handler) {
		if (handler == null) throw new NullPointerException("handler is null");
		this.handler = (JsonHandler<Object, Object>) handler;
		handler.parser = this;
	}

	/**
	 * Parses the given input string. The input must contain a valid JSON value, optionally padded with whitespace.
	 *
	 * @param string
	 *            the input string, must be valid JSON
	 * @throws ParseException
	 *             if the input is not valid JSON
	 */
	public void parse(final String string) {
		if (string == null) throw new NullPointerException("string is null");
		int bufferSize = Math.max(MIN_BUFFER_SIZE, Math.min(DEFAULT_BUFFER_SIZE, string.length()));
		try {
			parse(new StringReader(string), bufferSize);
		} catch (IOException exception) {
			// StringReader does not throw IOException
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Reads the entire input from the given reader and parses it as JSON. The input must contain a valid JSON value,
	 * optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into a default-sized input buffer. Hence, wrapping a reader in an additional
	 * <code>BufferedReader</code> likely won't improve reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the input from
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 * @throws ParseException
	 *             if the input is not valid JSON
	 */
	public void parse(final Reader reader) throws IOException {
		parse(reader, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Reads the entire input from the given reader and parses it as JSON. The input must contain a valid JSON value,
	 * optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into an input buffer of the given size. Hence, wrapping a reader in an additional
	 * <code>BufferedReader</code> likely won't improve reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the input from
	 * @param buffersize
	 *            the size of the input buffer in chars
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 * @throws ParseException
	 *             if the input is not valid JSON
	 */
	public void parse(final Reader reader, final int buffersize) throws IOException {
		if (reader == null) throw new NullPointerException("reader is null");
		if (buffersize <= 0) throw new IllegalArgumentException("buffersize is zero or negative");
		this.reader = reader;
		buffer = new char[buffersize];
		bufferOffset = 0;
		index = 0;
		fill = 0;
		line = 1;
		lineOffset = 0;
		current = 0;
		captureStart = -1;
		read();
		skipWhiteSpace();
		readValue();
		skipWhiteSpace();
		if (!isEndOfText()) throw error("Unexpected character");
	}

	/**
	 * Read value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readValue() throws IOException {
		switch (current) {
			case 'n':
				readNull();
				break;
			case 't':
				readTrue();
				break;
			case 'f':
				readFalse();
				break;
			case '"':
				readString();
				break;
			case '[':
				readArray();
				break;
			case '{':
				readObject();
				break;
			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				readNumber();
				break;
			default:
				throw expected("value");
		}
	}

	/**
	 * Read array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readArray() throws IOException {
		Object array = handler.startArray();
		read();
		if (++nestingLevel > MAX_NESTING_LEVEL) throw error("Nesting too deep");
		skipWhiteSpace();
		if (readChar(']')) {
			nestingLevel--;
			handler.endArray(array);
			return;
		}
		do {
			skipWhiteSpace();
			handler.startArrayValue(array);
			readValue();
			handler.endArrayValue(array);
			skipWhiteSpace();
		} while (readChar(','));
		if (!readChar(']')) throw expected("',' or ']'");
		nestingLevel--;
		handler.endArray(array);
	}

	/**
	 * Read object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readObject() throws IOException {
		Object object = handler.startObject();
		read();
		if (++nestingLevel > MAX_NESTING_LEVEL) throw error("Nesting too deep");
		skipWhiteSpace();
		if (readChar('}')) {
			nestingLevel--;
			handler.endObject(object);
			return;
		}
		boolean isGamlObject = false;
		String type = null;
		do {
			skipWhiteSpace();
			handler.startMemberName(object);
			String name = readName();
			handler.endMemberName(object, name);
			if (Json.GAML_TYPE_LABEL.equals(name)) {
				isGamlObject = true;
				skipWhiteSpace();
				if (!readChar(':')) throw expected("':'");
				skipWhiteSpace();
				type = readStringInternal();
			} else {
				skipWhiteSpace();
				if (!readChar(':')) throw expected("':'");
				skipWhiteSpace();
				handler.startObjectValue(object, name);
				readValue();
				handler.endObjectValue(object, name);
			}
			skipWhiteSpace();
		} while (readChar(','));
		if (!readChar('}')) throw expected("',' or '}'");
		nestingLevel--;
		if (isGamlObject) {
			handler.endGamlObject(type, object);
		} else {
			handler.endObject(object);
		}
	}

	/**
	 * Read name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private String readName() throws IOException {
		if (current != '"') throw expected("name");
		return readStringInternal();
	}

	/**
	 * Read null.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readNull() throws IOException {
		handler.startNull();
		read();
		readRequiredChar('u');
		readRequiredChar('l');
		readRequiredChar('l');
		handler.endNull();
	}

	/**
	 * Read true.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readTrue() throws IOException {
		handler.startBoolean();
		read();
		readRequiredChar('r');
		readRequiredChar('u');
		readRequiredChar('e');
		handler.endBoolean(true);
	}

	/**
	 * Read false.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readFalse() throws IOException {
		handler.startBoolean();
		read();
		readRequiredChar('a');
		readRequiredChar('l');
		readRequiredChar('s');
		readRequiredChar('e');
		handler.endBoolean(false);
	}

	/**
	 * Read required char.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param ch
	 *            the ch
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readRequiredChar(final char ch) throws IOException {
		if (!readChar(ch)) throw expected("'" + ch + "'");
	}

	/**
	 * Read string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readString() throws IOException {
		handler.startString();
		handler.endString(readStringInternal());
	}

	/**
	 * Read string internal.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private String readStringInternal() throws IOException {
		read();
		startCapture();
		while (current != '"') {
			if (current == '\\') {
				pauseCapture();
				readEscape();
				startCapture();
			} else if (current < 0x20)
				throw expected("valid string character");
			else {
				read();
			}
		}
		String string = endCapture();
		read();
		return string;
	}

	/**
	 * Read escape.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readEscape() throws IOException {
		read();
		switch (current) {
			case '"':
			case '/':
			case '\\':
				captureBuffer.append((char) current);
				break;
			case 'b':
				captureBuffer.append('\b');
				break;
			case 'f':
				captureBuffer.append('\f');
				break;
			case 'n':
				captureBuffer.append('\n');
				break;
			case 'r':
				captureBuffer.append('\r');
				break;
			case 't':
				captureBuffer.append('\t');
				break;
			case 'u':
				char[] hexChars = new char[4];
				for (int i = 0; i < 4; i++) {
					read();
					if (!isHexDigit()) throw expected("hexadecimal digit");
					hexChars[i] = (char) current;
				}
				captureBuffer.append((char) Integer.parseInt(new String(hexChars), 16));
				break;
			default:
				throw expected("valid escape sequence");
		}
		read();
	}

	/**
	 * Read number.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void readNumber() throws IOException {
		handler.startNumber();
		startCapture();
		readChar('-');
		int firstDigit = current;
		if (!readDigit()) throw expected("digit");
		if (firstDigit != '0') { while (readDigit()) {} }
		boolean isFloat = readFraction();
		readExponent();
		handler.endNumber(endCapture(), isFloat);
	}

	/**
	 * Read fraction.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private boolean readFraction() throws IOException {
		if (!readChar('.')) return false;
		if (!readDigit()) throw expected("digit");
		while (readDigit()) {}
		return true;
	}

	/**
	 * Read exponent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private boolean readExponent() throws IOException {
		if (!readChar('e') && !readChar('E')) return false;
		if (!readChar('+')) { readChar('-'); }
		if (!readDigit()) throw expected("digit");
		while (readDigit()) {}
		return true;
	}

	/**
	 * Read char.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param ch
	 *            the ch
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private boolean readChar(final char ch) throws IOException {
		if (current != ch) return false;
		read();
		return true;
	}

	/**
	 * Read digit.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private boolean readDigit() throws IOException {
		if (!isDigit()) return false;
		read();
		return true;
	}

	/**
	 * Skip white space.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void skipWhiteSpace() throws IOException {
		while (isWhiteSpace()) { read(); }
	}

	/**
	 * Read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	private void read() throws IOException {
		if (index == fill) {
			if (captureStart != -1) {
				captureBuffer.append(buffer, captureStart, fill - captureStart);
				captureStart = 0;
			}
			bufferOffset += fill;
			fill = reader.read(buffer, 0, buffer.length);
			index = 0;
			if (fill == -1) {
				current = -1;
				index++;
				return;
			}
		}
		if (current == '\n') {
			line++;
			lineOffset = bufferOffset + index;
		}
		current = buffer[index++];
	}

	/**
	 * Start capture.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 oct. 2023
	 */
	private void startCapture() {
		if (captureBuffer == null) { captureBuffer = new StringBuilder(); }
		captureStart = index - 1;
	}

	/**
	 * Pause capture.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 oct. 2023
	 */
	private void pauseCapture() {
		int end = current == -1 ? index : index - 1;
		captureBuffer.append(buffer, captureStart, end - captureStart);
		captureStart = -1;
	}

	/**
	 * End capture.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @date 29 oct. 2023
	 */
	private String endCapture() {
		int start = captureStart;
		int end = index - 1;
		captureStart = -1;
		if (captureBuffer.length() > 0) {
			captureBuffer.append(buffer, start, end - start);
			String captured = captureBuffer.toString();
			captureBuffer.setLength(0);
			return captured;
		}
		return new String(buffer, start, end - start);
	}

	/**
	 * Gets the location.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the location
	 * @date 29 oct. 2023
	 */
	Location getLocation() {
		int offset = bufferOffset + index - 1;
		int column = offset - lineOffset + 1;
		return new Location(offset, line, column);
	}

	/**
	 * Expected.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param expected
	 *            the expected
	 * @return the parses the exception
	 * @date 29 oct. 2023
	 */
	private ParseException expected(final String expected) {
		if (isEndOfText()) return error("Unexpected end of input");
		return error("Expected " + expected);
	}

	/**
	 * Error.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param message
	 *            the message
	 * @return the parses the exception
	 * @date 29 oct. 2023
	 */
	private ParseException error(final String message) {
		return new ParseException(message, getLocation());
	}

	/**
	 * Checks if is white space.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is white space
	 * @date 29 oct. 2023
	 */
	private boolean isWhiteSpace() {
		return current == ' ' || current == '\t' || current == '\n' || current == '\r';
	}

	/**
	 * Checks if is digit.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is digit
	 * @date 29 oct. 2023
	 */
	private boolean isDigit() { return current >= '0' && current <= '9'; }

	/**
	 * Checks if is hex digit.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is hex digit
	 * @date 29 oct. 2023
	 */
	private boolean isHexDigit() {
		return current >= '0' && current <= '9' || current >= 'a' && current <= 'f' || current >= 'A' && current <= 'F';
	}

	/**
	 * Checks if is end of text.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is end of text
	 * @date 29 oct. 2023
	 */
	private boolean isEndOfText() { return current == -1; }

}
