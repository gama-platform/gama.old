/*******************************************************************************************************
 *
 * CsvReader.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * A stream based parser for parsing delimited text data from a file or a stream.
 */

/**
 * The Class CsvReader.
 */

/**
 * The Class CsvReader.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CsvReader extends AbstractCSVManipulator {

	/** The input stream. */
	private Reader inputStream = null;

	// this will be our working buffer to hold data chunks
	// read in from the data file

	/** The data buffer. */
	private final DataBuffer dataBuffer = new DataBuffer();

	/** The column buffer. */
	private char[] columnBuffer = new char[INITIAL_COLUMN_BUFFER_SIZE];

	/** The column buffer position. */
	private int columnBufferPosition = 0;

	/** The headers holder. */
	HeadersHolder headersHolder;

	// these are all more or less global loop variables
	// to keep from needing to pass them all into various
	// methods during parsing

	/** The started column. */
	boolean startedColumn = false;

	/** The has more data. */
	boolean hasMoreData = true;

	/** The has read next line. */
	boolean hasReadNextLine = false;

	/** The columns count. */
	public int columnsCount = 0;

	/** The current record. */
	public long currentRecord = 0;

	/** The values. */
	String[] values = new String[INITIAL_COLUMN_COUNT];

	/**
	 * Creates a {@link msi.gama.util.file.csv.csvreader.CsvReader CsvReader} object using a file as the data
	 * source.&nbsp;Uses ISO-8859-1 as the {@link java.nio.charset.Charset Charset}.
	 *
	 * @param fileName
	 *            The path to the file to use as the data source.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public CsvReader(final String fileName, final char delimiter) throws FileNotFoundException {
		if (fileName == null) throw new IllegalArgumentException("Parameter fileName can not be null.");
		if (!new File(fileName).exists()) throw new FileNotFoundException("File " + fileName + " does not exist.");
		this.fileName = fileName;
		this.delimiter = delimiter;
	}

	/**
	 * Creates a {@link msi.gama.util.file.csv.csvreader.CsvReader CsvReader} object using a file as the data
	 * source.&nbsp;Uses a comma as the column delimiter and ISO-8859-1 as the {@link java.nio.charset.Charset Charset}.
	 *
	 * @param fileName
	 *            The path to the file to use as the data source.
	 */
	public CsvReader(final String fileName) throws FileNotFoundException {
		this(fileName, getDefaultDelimiter());
	}

	/**
	 * Constructs a {@link msi.gama.util.file.csv.csvreader.CsvReader CsvReader} object using a {@link java.io.Reader
	 * Reader} object as the data source.
	 *
	 * @param inputStream
	 *            The stream to use as the data source.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public CsvReader(final Reader inputStream, final char delimiter) {
		this.inputStream = inputStream;
		this.delimiter = delimiter;
	}

	/**
	 * Constructs a {@link msi.gama.util.file.csv.csvreader.CsvReader CsvReader} object using a {@link java.io.Reader
	 * Reader} object as the data source.&nbsp;Uses a comma as the column delimiter.
	 *
	 * @param inputStream
	 *            The stream to use as the data source.
	 */
	public CsvReader(final Reader inputStream) {
		this(inputStream, getDefaultDelimiter());
	}

	/**
	 * Gets the index of the current record.
	 *
	 * @return The index of the current record.
	 */
	public long getCurrentRecord() { return currentRecord - 1; }

	/**
	 * Gets the count of headers read in by a previous call to
	 * {@link msi.gama.util.file.csv.csvreader.CsvReader#readHeaders readHeaders()}.
	 *
	 * @return The count of headers read in by a previous call to
	 *         {@link msi.gama.util.file.csv.csvreader.CsvReader#readHeaders readHeaders()}.
	 */
	public int getHeaderCount() { return headersHolder == null ? 0 : headersHolder.names.length; }

	/**
	 * Returns the header values as a string array.
	 *
	 * @return The header values as a String array.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public String[] getHeaders() {
		if (headersHolder == null || headersHolder.names() == null) return null;
		// use clone here to prevent the outside code from
		// setting values on the array directly, which would
		// throw off the index lookup based on header name
		final String[] clone = new String[headersHolder.names.length];
		System.arraycopy(headersHolder.names, 0, clone, 0, headersHolder.names.length);
		return clone;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String[] getValues() {
		// need to return a clone, and can't use clone because values.Length
		// might be greater than columnsCount
		final String[] clone = new String[columnsCount];
		System.arraycopy(values, 0, clone, 0, columnsCount);
		return clone;
	}

	/**
	 * Reads another record.
	 *
	 * @return Whether another record was successfully read or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the source stream.
	 */
	public boolean readRecord() throws IOException {
		this.columnsCount = 0;
		hasReadNextLine = false;
		boolean startedWithQualifier = false;
		char lastLetter = '\0';
		// check to see if we've already found the end of data
		if (hasMoreData) {
			// loop over the data stream until the end of data is found
			// or the end of the record is found
			do {
				if (dataBuffer.position == dataBuffer.count) {
					checkDataLength();
				} else {
					startedWithQualifier = false;
					// grab the current letter as a char
					char currentLetter = dataBuffer.buffer[dataBuffer.position];
					if (textQualifier != null && currentLetter == textQualifier.charValue()) {
						// this will be a text qualified column, so
						// we need to set startedWithQualifier to make it
						// enter the seperate branch to handle text
						// qualified columns
						lastLetter = currentLetter;
						// read qualified
						startedColumn = true;
						dataBuffer.columnStart = dataBuffer.position + 1;
						startedWithQualifier = true;
						boolean lastLetterWasQualifier = false;
						boolean eatingTrailingJunk = false;
						boolean lastLetterWasEscape = false;
						dataBuffer.position++;
						do {
							if (dataBuffer.position == dataBuffer.count) {
								checkDataLength();
							} else {
								// grab the current letter as a char
								currentLetter = dataBuffer.buffer[dataBuffer.position];
								if (eatingTrailingJunk) {
									dataBuffer.columnStart = dataBuffer.position + 1;

									if (currentLetter == delimiter) {
										endColumn(startedWithQualifier);
									} else if (currentLetter == Letters.CR || currentLetter == Letters.LF) {
										endColumn(startedWithQualifier);
										endRecord();
									}
								} else if (currentLetter == textQualifier.charValue()) {
									if (lastLetterWasEscape) {
										lastLetterWasEscape = false;
										lastLetterWasQualifier = false;
									} else {
										updateCurrentValue();
										lastLetterWasEscape = true;
										lastLetterWasQualifier = true;
									}
								} else if (lastLetterWasQualifier) {
									if (currentLetter == delimiter) {
										endColumn(startedWithQualifier);
									} else if (currentLetter == Letters.CR || currentLetter == Letters.LF) {
										endColumn(startedWithQualifier);
										endRecord();
									} else {
										dataBuffer.columnStart = dataBuffer.position + 1;
										eatingTrailingJunk = true;
									}
									// make sure to clear the flag for next
									// run of the loop
									lastLetterWasQualifier = false;
								}
								// keep track of the last letter because we need
								// it for several key decisions
								lastLetter = currentLetter;
								if (startedColumn) { dataBuffer.position++; }
							} // end else
						} while (hasMoreData && startedColumn);
					} else if (currentLetter == delimiter) {
						// we encountered a column with no data, so
						// just send the end column
						lastLetter = currentLetter;
						endColumn(startedWithQualifier);
					} else if (currentLetter == Letters.CR || currentLetter == Letters.LF) {
						// this will skip blank lines
						if (startedColumn || columnsCount > 0) {
							endColumn(startedWithQualifier);
							endRecord();
						}
						lastLetter = currentLetter;
					} else if (currentLetter == Letters.SPACE || currentLetter == Letters.TAB) {
						// do nothing, this will trim leading whitespace
						// for both text qualified columns and non
						startedColumn = true;
						dataBuffer.columnStart = dataBuffer.position + 1;
					} else {
						// since the letter wasn't a special letter, this
						// will be the first letter of our current column
						startedColumn = true;
						dataBuffer.columnStart = dataBuffer.position;
						boolean firstLoop = true;
						do {
							if (!firstLoop && dataBuffer.position == dataBuffer.count) {
								checkDataLength();
							} else {
								if (!firstLoop) {
									// grab the current letter as a char
									currentLetter = dataBuffer.buffer[dataBuffer.position];
								}
								if (currentLetter == delimiter) {
									endColumn(startedWithQualifier);
								} else if (currentLetter == Letters.CR || currentLetter == Letters.LF) {
									endColumn(startedWithQualifier);
									endRecord();
								}
								lastLetter = currentLetter;
								firstLoop = false;
								if (startedColumn) { dataBuffer.position++; }
							} // end else
						} while (hasMoreData && startedColumn);
					}

					if (hasMoreData) { dataBuffer.position++; }
				} // end else
			} while (hasMoreData && !hasReadNextLine);

			// check to see if we hit the end of the file
			// without processing the current record

			if (startedColumn || lastLetter == delimiter) {
				endColumn(startedWithQualifier);
				endRecord();
			}
		}

		return hasReadNextLine;
	}

	/**
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the source stream.
	 */
	private void checkDataLength() throws IOException {
		if (inputStream == null && fileName != null) {
			inputStream =
					new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8")),
							MAX_FILE_BUFFER_SIZE);
		}
		updateCurrentValue();
		try {
			dataBuffer.count = inputStream.read(dataBuffer.buffer, 0, dataBuffer.buffer.length);
		} catch (final IOException ex) {
			close();
			throw ex;
		}

		// if no more data could be found, set flag stating that
		// the end of the data was found
		if (dataBuffer.count == -1) { hasMoreData = false; }
		dataBuffer.position = 0;
		dataBuffer.columnStart = 0;
	}

	/**
	 * Read the first record of data as column headers.
	 *
	 * @return Whether the header record was successfully read or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the source stream.
	 */
	public boolean readHeaders() throws IOException {
		final boolean result = readRecord();
		// copy the header data from the column array
		// to the header string array
		String[] headers = new String[columnsCount];
		HashMap indexByName = new HashMap();
		for (int i = 0; i < columnsCount; i++) {
			final String columnValue = values[i];
			headers[i] = columnValue;
			// if there are duplicate header names, we will save the last one
			indexByName.put(columnValue, i);
		}
		headersHolder = new HeadersHolder(headers, indexByName);
		if (result) { currentRecord--; }
		this.columnsCount = 0;
		return result;
	}

	/**
	 * End column.
	 *
	 * @param startedWithQualifier
	 *            the started with qualifier
	 */
	private void endColumn(final boolean startedWithQualifier) {
		String currentValue = "";
		// must be called before setting startedColumn = false
		if (startedColumn) {
			if (columnBufferPosition == 0) {
				if (dataBuffer.columnStart < dataBuffer.position) {
					int lastLetter = dataBuffer.position - 1;
					if (!startedWithQualifier) {
						while (lastLetter >= dataBuffer.columnStart && (dataBuffer.buffer[lastLetter] == Letters.SPACE
								|| dataBuffer.buffer[lastLetter] == Letters.TAB)) {
							lastLetter--;
						}
					}
					currentValue = new String(dataBuffer.buffer, dataBuffer.columnStart,
							lastLetter - dataBuffer.columnStart + 1);
				}
			} else {
				updateCurrentValue();
				int lastLetter = columnBufferPosition - 1;
				if (!startedWithQualifier) {
					while (lastLetter >= 0
							&& (columnBuffer[lastLetter] == Letters.SPACE || columnBuffer[lastLetter] == Letters.TAB)) {
						lastLetter--;
					}
				}
				currentValue = new String(columnBuffer, 0, lastLetter + 1);
			}
		}

		columnBufferPosition = 0;
		startedColumn = false;
		// check to see if our current holder array for
		// column chunks is still big enough to handle another
		// column chunk
		if (columnsCount == values.length) {
			// holder array needs to grow to be able to hold another column
			final int newLength = values.length * 2;
			final String[] holder = new String[newLength];
			System.arraycopy(values, 0, holder, 0, values.length);
			values = holder;
		}
		values[columnsCount] = StringUtils.trimToEmpty(currentValue);
		currentValue = "";
		this.columnsCount = columnsCount + 1;
	}

	/**
	 * Update current value.
	 */
	private void updateCurrentValue() {
		if (startedColumn && dataBuffer.columnStart < dataBuffer.position) {
			if (columnBuffer.length - columnBufferPosition < dataBuffer.position - dataBuffer.columnStart) {
				final int newLength = columnBuffer.length
						+ Math.max(dataBuffer.position - dataBuffer.columnStart, columnBuffer.length);
				final char[] holder = new char[newLength];
				System.arraycopy(columnBuffer, 0, holder, 0, columnBufferPosition);
				columnBuffer = holder;
			}
			System.arraycopy(dataBuffer.buffer, dataBuffer.columnStart, columnBuffer, columnBufferPosition,
					dataBuffer.position - dataBuffer.columnStart);
			columnBufferPosition += dataBuffer.position - dataBuffer.columnStart;
		}
		dataBuffer.columnStart = dataBuffer.position + 1;
	}

	@Override
	public void endRecord() {
		// this flag is used as a loop exit condition during parsing
		hasReadNextLine = true;
		currentRecord++;
	}

	/**
	 * Gets the corresponding column index for a given column header name.
	 *
	 * @param headerName
	 *            The header name of the column.
	 * @return The column index for the given column header name.&nbsp;Returns -1 if not found.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public int getIndex(final String headerName) throws IOException {
		if (headersHolder == null) return -1;
		final Object indexValue = headersHolder.indexes.get(headerName);
		if (indexValue != null) return (Integer) indexValue;
		return -1;
	}

	/**
	 * Skips the next line of data using the standard end of line characters and does not do any column delimited
	 * parsing.
	 *
	 * @return Whether a line was successfully skipped or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the source stream.
	 */
	public String skipLine() throws IOException {
		// clear public column values for current line
		this.columnsCount = 0;
		StringBuilder skippedLine = new StringBuilder();
		if (hasMoreData) {
			boolean foundEol = false;

			do {
				if (dataBuffer.position == dataBuffer.count) {
					checkDataLength();
				} else {
					// grab the current letter as a char
					final char currentLetter = dataBuffer.buffer[dataBuffer.position];
					if (currentLetter == Letters.CR || currentLetter == Letters.LF) { foundEol = true; }
					// keep track of the last letter because we need
					// it for several key decisions
					if (!foundEol) {
						skippedLine.append(currentLetter);
						dataBuffer.position++;
					}
				} // end else
			} while (hasMoreData && !foundEol);
			columnBufferPosition = 0;
		}
		return skippedLine.toString();
	}

	/**
	 * Closes and releases all related resources.
	 */
	@Override
	public void close() {
		if (inputStream != null) {
			dataBuffer.buffer = null;
			columnBuffer = null;
			try {
				if (inputStream != null) { inputStream.close(); }
			} catch (final Exception e) {}
			inputStream = null;
		}
	}

	/**
	 * The Class DataBuffer.
	 */
	private static class DataBuffer {

		/** The Buffer. */
		public char[] buffer = new char[MAX_BUFFER_SIZE];

		/** The Position. */
		public int position;

		/**
		 * The Count. How much usable data has been read into the stream, which will not always be as long as
		 * Buffer.Length.
		 */
		public int count;

		/**
		 * The Column start. The position of the cursor in the buffer when the current column was started or the last
		 * time data was moved out to the column buffer
		 */
		public int columnStart;

	}

	/**
	 * The Class HeadersHolder.
	 */
	private record HeadersHolder(String[] names, HashMap<String, Integer> indexes) {}

}