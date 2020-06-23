/*******************************************************************************************************
 *
 * msi.gama.util.file.CsvWriter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.csv;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * A stream based writer for writing delimited text data to a file or a stream.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CsvWriter implements Closeable {

	private Writer outputStream = null;

	private String fileName = null;

	private boolean firstColumn = true;

	private boolean useCustomRecordDelimiter = false;

	private Charset charset = null;

	// this holds all the values for switches that the user is allowed to set
	private final UserSettings userSettings = new UserSettings();

	private boolean initialized = false;

	private boolean closed = false;

	private final String systemRecordDelimiter = System.getProperty("line.separator");

	/**
	 * Double up the text qualifier to represent an occurrence of the text qualifier.
	 */
	public static final int ESCAPE_MODE_DOUBLED = 1;

	/**
	 * Use a backslash character before the text qualifier to represent an occurrence of the text qualifier.
	 */
	public static final int ESCAPE_MODE_BACKSLASH = 2;

	/**
	 * Creates a {@link msi.gama.util.file.csv.csvreader.CsvWriter CsvWriter} object using a file as the data destination.
	 *
	 * @param fileName
	 *            The path to the file to output the data.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 * @param charset
	 *            The {@link java.nio.charset.Charset Charset} to use while writing the data.
	 */
	public CsvWriter(final String fileName, final char delimiter, final Charset charset) {
		if (fileName == null) { throw new IllegalArgumentException("Parameter fileName can not be null."); }

		if (charset == null) { throw new IllegalArgumentException("Parameter charset can not be null."); }

		this.fileName = fileName;
		userSettings.Delimiter = delimiter;
		this.charset = charset;
	}

	/**
	 * Creates a {@link msi.gama.util.file.csv.csvreader.CsvWriter CsvWriter} object using a file as the data destination.&nbsp;Uses a
	 * comma as the column delimiter and ISO-8859-1 as the {@link java.nio.charset.Charset Charset}.
	 *
	 * @param fileName
	 *            The path to the file to output the data.
	 */
	public CsvWriter(final String fileName) {
		this(fileName, Letters.COMMA, Charset.forName("UTF-8"));
	}

	/**
	 * Creates a {@link msi.gama.util.file.csv.csvreader.CsvWriter CsvWriter} object using a Writer to write data to.
	 *
	 * @param outputStream
	 *            The stream to write the column delimited data to.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public CsvWriter(final Writer outputStream, final char delimiter) {
		if (outputStream == null) { throw new IllegalArgumentException("Parameter outputStream can not be null."); }

		this.outputStream = outputStream;
		userSettings.Delimiter = delimiter;
		initialized = true;
	}

	/**
	 * Gets the character being used as the column delimiter.
	 *
	 * @return The character being used as the column delimiter.
	 */
	public char getDelimiter() {
		return userSettings.Delimiter;
	}

	/**
	 * Sets the character to use as the column delimiter.
	 *
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public void setDelimiter(final char delimiter) {
		userSettings.Delimiter = delimiter;
	}

	public char getRecordDelimiter() {
		return userSettings.RecordDelimiter;
	}

	/**
	 * Sets the character to use as the record delimiter.
	 *
	 * @param recordDelimiter
	 *            The character to use as the record delimiter. Default is combination of standard end of line
	 *            characters for Windows, Unix, or Mac.
	 */
	public void setRecordDelimiter(final char recordDelimiter) {
		useCustomRecordDelimiter = true;
		userSettings.RecordDelimiter = recordDelimiter;
	}

	/**
	 * Gets the character to use as a text qualifier in the data.
	 *
	 * @return The character to use as a text qualifier in the data.
	 */
	public char getTextQualifier() {
		return userSettings.TextQualifier;
	}

	/**
	 * Sets the character to use as a text qualifier in the data.
	 *
	 * @param textQualifier
	 *            The character to use as a text qualifier in the data.
	 */
	public void setTextQualifier(final char textQualifier) {
		userSettings.TextQualifier = textQualifier;
	}

	/**
	 * Whether text qualifiers will be used while writing data or not.
	 *
	 * @return Whether text qualifiers will be used while writing data or not.
	 */
	public boolean getUseTextQualifier() {
		return userSettings.UseTextQualifier;
	}

	/**
	 * Sets whether text qualifiers will be used while writing data or not.
	 *
	 * @param useTextQualifier
	 *            Whether to use a text qualifier while writing data or not.
	 */
	public void setUseTextQualifier(final boolean useTextQualifier) {
		userSettings.UseTextQualifier = useTextQualifier;
	}

	public int getEscapeMode() {
		return userSettings.EscapeMode;
	}

	public void setEscapeMode(final int escapeMode) {
		userSettings.EscapeMode = escapeMode;
	}

	public void setComment(final char comment) {
		userSettings.Comment = comment;
	}

	public char getComment() {
		return userSettings.Comment;
	}

	/**
	 * Whether fields will be surrounded by the text qualifier even if the qualifier is not necessarily needed to escape
	 * this field.
	 *
	 * @return Whether fields will be forced to be qualified or not.
	 */
	public boolean getForceQualifier() {
		return userSettings.ForceQualifier;
	}

	/**
	 * Use this to force all fields to be surrounded by the text qualifier even if the qualifier is not necessarily
	 * needed to escape this field. Default is false.
	 *
	 * @param forceQualifier
	 *            Whether to force the fields to be qualified or not.
	 */
	public void setForceQualifier(final boolean forceQualifier) {
		userSettings.ForceQualifier = forceQualifier;
	}

	private static Map<Character, Character> REPLACEMENTS = new HashMap() {

		{
			put(';', ',');
			put(',', ';');
			put(' ', ';');
			put('|', ';');
			put(':', ';');
			put('\t', ';');
		}
	};

	/**
	 * Writes another column of data to this record.
	 *
	 * @param content
	 *            The data for the new column.
	 * @param changeDelimiter
	 *            Whether to change the delimiter to another character if it happens to be in the string
	 * @exception IOException
	 *                Thrown if an error occurs while writing data to the destination stream.
	 */
	public void write(final String c, final boolean changeDelimiter) throws IOException {
		String content = c;
		checkClosed();

		checkInit();

		if (content == null) {
			content = "";
		}

		if (!firstColumn) {
			outputStream.write(userSettings.Delimiter);
		}

		boolean textQualify = userSettings.ForceQualifier;

		if (changeDelimiter && content.length() > 0) {
			content = content.replace(userSettings.Delimiter, REPLACEMENTS.get(userSettings.Delimiter));
		}

		if (!textQualify && userSettings.UseTextQualifier && (content.indexOf(userSettings.TextQualifier) > -1
				|| content.indexOf(userSettings.Delimiter) > -1
				|| !useCustomRecordDelimiter && (content.indexOf(Letters.LF) > -1 || content.indexOf(Letters.CR) > -1)
				|| useCustomRecordDelimiter && content.indexOf(userSettings.RecordDelimiter) > -1
				|| firstColumn && content.length() > 0 && content.charAt(0) == userSettings.Comment ||
				// check for empty first column, which if on its own
				// line must
				// be qualified or the line will be skipped
				firstColumn && content.length() == 0)) {
			textQualify = true;
		}

		if (userSettings.UseTextQualifier && !textQualify && content.length() > 0 /* && preserveSpaces */ ) {
			final char firstLetter = content.charAt(0);

			if (firstLetter == Letters.SPACE || firstLetter == Letters.TAB) {
				textQualify = true;
			}

			if (!textQualify && content.length() > 1) {
				final char lastLetter = content.charAt(content.length() - 1);

				if (lastLetter == Letters.SPACE || lastLetter == Letters.TAB) {
					textQualify = true;
				}
			}
		}

		if (textQualify) {
			outputStream.write(userSettings.TextQualifier);

			if (userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH) {
				content = replace(content, "" + Letters.BACKSLASH, "" + Letters.BACKSLASH + Letters.BACKSLASH);
				content = replace(content, "" + userSettings.TextQualifier,
						"" + Letters.BACKSLASH + userSettings.TextQualifier);
			} else {
				content = replace(content, "" + userSettings.TextQualifier,
						"" + userSettings.TextQualifier + userSettings.TextQualifier);
			}
		} else if (userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH) {
			content = replace(content, "" + Letters.BACKSLASH, "" + Letters.BACKSLASH + Letters.BACKSLASH);
			content = replace(content, "" + userSettings.Delimiter, "" + Letters.BACKSLASH + userSettings.Delimiter);

			if (useCustomRecordDelimiter) {
				content = replace(content, "" + userSettings.RecordDelimiter,
						"" + Letters.BACKSLASH + userSettings.RecordDelimiter);
			} else {
				content = replace(content, "" + Letters.CR, "" + Letters.BACKSLASH + Letters.CR);
				content = replace(content, "" + Letters.LF, "" + Letters.BACKSLASH + Letters.LF);
			}

			if (firstColumn && content.length() > 0 && content.charAt(0) == userSettings.Comment) {
				if (content.length() > 1) {
					content = "" + Letters.BACKSLASH + userSettings.Comment + content.substring(1);
				} else {
					content = "" + Letters.BACKSLASH + userSettings.Comment;
				}
			}
		}

		outputStream.write(content);

		if (textQualify) {
			outputStream.write(userSettings.TextQualifier);
		}

		firstColumn = false;
	}

	public void writeComment(final String commentText) throws IOException {
		checkClosed();

		checkInit();

		outputStream.write(userSettings.Comment);

		outputStream.write(commentText);

		if (useCustomRecordDelimiter) {
			outputStream.write(userSettings.RecordDelimiter);
		} else {
			outputStream.write(systemRecordDelimiter);
		}

		firstColumn = true;
	}

	/**
	 * Writes a new record using the passed in array of values.
	 *
	 * @param values
	 *            Values to be written.
	 *
	 * @param changeDelimiter
	 *            whether or not to change the delimiter with another character if it happens to be in the strings
	 *
	 * @throws IOException
	 *             Thrown if an error occurs while writing data to the destination stream.
	 */
	public void writeRecord(final String[] values, final boolean changeDelimiter) throws IOException {
		if (values != null && values.length > 0) {
			for (final String value : values) {
				write(value, changeDelimiter);
			}

			endRecord();
		}
	}

	/**
	 * Writes a new record using the passed in array of values.
	 *
	 * @param values
	 *            Values to be written.
	 *
	 * @throws IOException
	 *             Thrown if an error occurs while writing data to the destination stream.
	 */
	public void writeRecord(final String[] values) throws IOException {
		writeRecord(values, false);
	}

	/**
	 * Ends the current record by sending the record delimiter.
	 *
	 * @exception IOException
	 *                Thrown if an error occurs while writing data to the destination stream.
	 */
	public void endRecord() throws IOException {
		checkClosed();

		checkInit();

		if (useCustomRecordDelimiter) {
			outputStream.write(userSettings.RecordDelimiter);
		} else {
			outputStream.write(systemRecordDelimiter);
		}

		firstColumn = true;
	}

	/**
	 *
	 */
	private void checkInit() throws IOException {
		if (!initialized) {
			if (fileName != null) {
				outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), charset));
			}

			initialized = true;
		}
	}

	/**
	 * Closes and releases all related resources.
	 */
	@Override
	public void close() {
		if (!closed) {
			close(true);

			closed = true;
		}
	}

	/**
	 *
	 */
	private void close(final boolean closing) {
		if (!closed) {
			if (closing) {
				charset = null;
			}

			try {
				if (initialized) {
					outputStream.close();
				}
			} catch (final Exception e) {
				// just eat the exception
			}

			outputStream = null;

			closed = true;
		}
	}

	/**
	 *
	 */
	private void checkClosed() throws IOException {
		if (closed) { throw new IOException("This instance of the CsvWriter class has already been closed."); }
	}

	/**
	 *
	 */
	@Override
	protected void finalize() {
		close(false);
	}

	public static class Letters {

		public static final char LF = '\n';

		public static final char CR = '\r';

		public static final char QUOTE = '"';

		public static final char COMMA = ',';

		public static final char SPACE = ' ';

		public static final char TAB = '\t';

		public static final char POUND = '#';

		public static final char BACKSLASH = '\\';

		public static final char NULL = '\0';
	}

	private class UserSettings {

		// having these as publicly accessible members will prevent
		// the overhead of the method call that exists on properties
		public char TextQualifier;

		public boolean UseTextQualifier;

		public char Delimiter;

		public char RecordDelimiter;

		public char Comment;

		public int EscapeMode;

		public boolean ForceQualifier;

		public UserSettings() {
			TextQualifier = Letters.QUOTE;
			UseTextQualifier = false; /* was true */
			Delimiter = Letters.COMMA;
			RecordDelimiter = Letters.NULL;
			Comment = Letters.POUND;
			EscapeMode = ESCAPE_MODE_DOUBLED;
			ForceQualifier = false;
		}
	}

	public static String replace(final String original, final String pattern, final String replace) {
		final int len = pattern.length();
		int found = original.indexOf(pattern);

		if (found > -1) {
			final StringBuffer sb = new StringBuffer();
			int start = 0;

			while (found != -1) {
				sb.append(original.substring(start, found));
				sb.append(replace);
				start = found + len;
				found = original.indexOf(pattern, start);
			}

			sb.append(original.substring(start));

			return sb.toString();
		}
		return original;
	}
}