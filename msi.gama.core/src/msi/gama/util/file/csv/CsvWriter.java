/*******************************************************************************************************
 *
 * CsvWriter.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.csv;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * A stream based writer for writing delimited text data to a file or a stream.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CsvWriter extends AbstractCSVManipulator {

	/** The output stream. */
	private Writer outputStream = null;

	/**
	 * Creates a {@link msi.gama.util.file.csv.csvreader.CsvWriter CsvWriter} object using a file as the data
	 * destination.
	 *
	 * @param fileName
	 *            The path to the file to output the data.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 * @param charset
	 *            The {@link java.nio.charset.Charset Charset} to use while writing the data.
	 */
	public CsvWriter(final String fileName, final char delimiter) {
		if (fileName == null) throw new IllegalArgumentException("Parameter fileName can not be null.");
		this.fileName = fileName;
		this.delimiter = delimiter;
	}

	/**
	 * Creates a {@link msi.gama.util.file.csv.csvreader.CsvWriter CsvWriter} object using a file as the data
	 * destination.&nbsp;Uses a comma as the column delimiter and ISO-8859-1 as the {@link java.nio.charset.Charset
	 * Charset}.
	 *
	 * @param fileName
	 *            The path to the file to output the data.
	 */
	public CsvWriter(final String fileName) {
		this(fileName, getDefaultDelimiter());
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
		if (outputStream == null) throw new IllegalArgumentException("Parameter outputStream can not be null.");
		this.outputStream = outputStream;
		this.delimiter = delimiter;
	}

	/**
	 * Instantiates a new csv writer.
	 *
	 * @param outputStream
	 *            the output stream.
	 */
	public CsvWriter(final Writer outputStream) {
		this(outputStream, getDefaultDelimiter());
	}

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
		checkInit();
		if (content == null) { content = ""; }
		if (!firstColumn) { outputStream.write(delimiter); }
		if (changeDelimiter && content.length() > 0) {
			content = content.replace(delimiter, REPLACEMENTS.get(delimiter));
		}
		outputStream.write(content);
		firstColumn = false;
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
			for (final String value : values) { write(value, changeDelimiter); }
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
	@Override
	public void endRecord() throws IOException {
		checkInit();
		outputStream.write(System.lineSeparator());
		firstColumn = true;
	}

	/**
	 *
	 */
	private void checkInit() throws IOException {
		if (outputStream == null && fileName != null) {
			outputStream = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("UTF-8")));
		}
	}

	/**
	 * Closes and releases all related resources.
	 */
	@Override
	public void close() {
		try {
			if (outputStream != null) { outputStream.close(); }
		} catch (final Exception e) {}
		outputStream = null;
	}

	/**
	 * Replace.
	 *
	 * @param original
	 *            the original
	 * @param pattern
	 *            the pattern
	 * @param replace
	 *            the replace
	 * @return the string
	 */
	public static String replace(final String original, final String pattern, final String replace) {
		final int len = pattern.length();
		int found = original.indexOf(pattern);
		if (found > -1) {
			final StringBuilder sb = new StringBuilder();
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