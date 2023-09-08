/*******************************************************************************************************
 *
 * AbstractCSVManipulator.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.csv;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import msi.gama.common.preferences.GamaPreferences;

/**
 * The Class AbstractCSVManipulator.
 */
public abstract class AbstractCSVManipulator implements Closeable {

	/** The replacements. */
	public static Map<Character, Character> REPLACEMENTS =
			Map.of(';', ',', ',', ';', ' ', ';', '|', ';', ':', ';', '\t', ';');

	/** The Constant MAX_BUFFER_SIZE. */
	public static final int MAX_BUFFER_SIZE = 1024;

	/** The Constant MAX_FILE_BUFFER_SIZE. */
	public static final int MAX_FILE_BUFFER_SIZE = 4 * 1024;

	/** The Constant INITIAL_COLUMN_COUNT. */
	public static final int INITIAL_COLUMN_COUNT = 10;

	/** The Constant INITIAL_COLUMN_BUFFER_SIZE. */
	public static final int INITIAL_COLUMN_BUFFER_SIZE = 50;

	/**
	 * The Class Letters.
	 */
	/**
	 * The Class Letters.
	 */
	public static class Letters {

		/** The Constant LF. */
		public static final char LF = '\n';

		/** The Constant CR. */
		public static final char CR = '\r';

		/** The Constant QUOTE. */
		public static final char QUOTE = '"';

		/** The Constant COMMA. */
		public static final char COMMA = ',';

		/** The Constant SPACE. */
		public static final char SPACE = ' ';

		/** The Constant TAB. */
		public static final char TAB = '\t';

		/** The Constant POUND. */
		public static final char POUND = '#';

		/** The Constant BACKSLASH. */
		public static final char BACKSLASH = '\\';

		/** The Constant NULL. */
		public static final char NULL = '\0';

		/** The Constant BACKSPACE. */
		public static final char BACKSPACE = '\b';

		/** The Constant FORM_FEED. */
		public static final char FORM_FEED = '\f';

		/** The Constant ESCAPE. */
		public static final char ESCAPE = '\u001B'; // ASCII/ANSI escape

		/** The Constant VERTICAL_TAB. */
		public static final char VERTICAL_TAB = '\u000B';

		/** The Constant ALERT. */
		public static final char ALERT = '\u0007';

		/** The Constant PIPE. */
		public static final char PIPE = '|';

		/** The Constant SEMICOLUMN. */
		public static final char SEMICOLUMN = ';';

		/** The Constant COLUMN. */
		public static final char COLUMN = ':';

		/** The Constant SLASH. */
		public static final char SLASH = '/';
	}

	/** The first column. */
	protected boolean firstColumn = true;

	/** The file name. */
	protected String fileName = null;

	/** The Text qualifier. */
	public Character textQualifier = getDefaultQualifier();

	/** The Delimiter. */
	public char delimiter = getDefaultDelimiter();

	/**
	 * Gets the default delimiter.
	 *
	 * @return the default delimiter
	 */
	public static char getDefaultDelimiter() {
		String del = GamaPreferences.External.CSV_SEPARATOR.getValue();
		if (del == null || del.isEmpty()) return Letters.COMMA;
		return del.charAt(0);
	}

	/**
	 * Gets the default qualifier.
	 *
	 * @return the default qualifier
	 */
	public static char getDefaultQualifier() {
		String del = GamaPreferences.External.CSV_STRING_QUALIFIER.getValue();
		if (del == null || del.isEmpty()) return Letters.QUOTE;
		return del.charAt(0);
	}

	@Override
	public abstract void close() throws IOException;

	/**
	 * Sets the character to use as the column delimiter. Default is comma, ','.
	 *
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public void setDelimiter(final char delimiter) { this.delimiter = delimiter; }

	/**
	 * Sets the character to use as a text qualifier in the data.
	 *
	 * @param textQualifier
	 *            The character to use as a text qualifier in the data.
	 */
	public void setTextQualifier(final Character textQualifier) { this.textQualifier = textQualifier; }

	/**
	 * End record.
	 */
	public abstract void endRecord() throws IOException;

}
