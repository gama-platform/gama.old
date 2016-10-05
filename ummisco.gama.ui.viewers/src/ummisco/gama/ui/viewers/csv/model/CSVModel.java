/*
 * Copyright 2011 csvedit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ummisco.gama.ui.viewers.csv.model;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.CsvReader;
import msi.gama.util.file.CsvWriter;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import msi.gama.util.file.IGamaFileMetaData;

/**
 *
 * @author fhenri
 *
 */
public class CSVModel implements IRowChangesListener {

	private boolean displayFirstLine;
	private final ArrayList<CSVRow> rows;
	private final ArrayList<ICsvFileModelListener> listeners;
	private final IFile file;
	private CSVInfo currentInfo;

	/**
	 * Default constructor
	 */
	public CSVModel(final IFile file) {
		this.file = file;
		displayFirstLine = true;
		rows = new ArrayList<>();
		listeners = new ArrayList<>();
	}

	/**
	 * Check if first line in the file will be considered as the file header
	 * 
	 * @return true if the first line in the file represents the header
	 */
	public boolean isFirstLineHeader() {
		return getInfo().header;
	}

	public void setFirstLineHeader(final boolean header) {
		final CSVInfo info = getInfo();
		if (info.header != header) {
			info.header = header;
			saveMetaData();
		}
		// ResourceRefreshHandler.discardMetaData(file);
	}

	/**
	 * Get custom delimiter to use as a separator
	 * 
	 * @return the delimiter
	 */
	public char getCustomDelimiter() {
		return getInfo().delimiter;
	}

	public void setCustomDelimiter(final char c) {
		final CSVInfo info = getInfo();
		if (c == info.delimiter) {
			return;
		}
		info.delimiter = c;
		saveMetaData();
	}

	/**
	 * Get the character that defines comment lines
	 * 
	 * @return the comment line starting character. If no comments are allowed
	 *         in this file, then Character.UNASSIGNED constant must be
	 *         returned;
	 *
	 */
	public char getCommentChar() {
		final char result = Character.UNASSIGNED;
		return result;
	}

	/**
	 * Get custom text qualifier to use as a text qualifier in the data
	 * 
	 * @return the text qualifier character to use as a text qualifier in the
	 *         data
	 */
	public char getTextQualifier() {
		final char result = Character.UNASSIGNED;
		return result;
	}

	/**
	 * check if the text qualifier has to be use for all fields or not
	 * 
	 * @return true if the text qualifier is to be used for all data fields
	 */
	public boolean useQualifier() {
		return false;
	}

	/**
	 * @param text
	 */
	public void setInput(final String text) {
		readLines(text);
	}

	/**
	 * @param display
	 */
	public void displayFirstLine(final boolean display) {
		displayFirstLine = display;
	}

	/**
	 * @param reader
	 * @return
	 */
	protected CsvReader initializeReader(final Reader reader) {
		final CsvReader csvReader = new CsvReader(reader);

		final char customDelimiter = getCustomDelimiter();
		csvReader.setDelimiter(customDelimiter);

		final char commentChar = getCommentChar();
		if (commentChar != Character.UNASSIGNED) {
			csvReader.setComment(commentChar);
			// prevent loss of comment in csv source file
			csvReader.setUseComments(false);
		}

		csvReader.setTextQualifier(getTextQualifier());
		csvReader.setUseTextQualifier(false);

		return csvReader;
	}

	protected void readLines(final String fileText) {
		readLines(new StringReader(fileText));
	}

	/**
	 * @param fileText
	 */
	protected void readLines(final Reader reader) {
		rows.clear();
		final CSVInfo info = getInfo();
		info.cols = 0;

		try {
			final CsvReader csvReader = initializeReader(reader);
			// case when the first line is the encoding
			if (!displayFirstLine) {
				csvReader.skipLine();
			}

			boolean setHeader = false;
			while (csvReader.readRecord()) {
				final String[] rowValues = csvReader.getValues();
				if (rowValues.length > info.cols) {
					info.cols = rowValues.length;
				}
				final CSVRow csvRow = new CSVRow(rowValues, this);
				if (!rowValues[0].startsWith(String.valueOf(getCommentChar()))) {
					if (info.header && !setHeader) {
						setHeader = true;
						csvRow.setHeader(true);
						getInfo().headers = new String[getInfo().cols];
						for (int i = 0; i < getInfo().cols; i++) {
							getInfo().headers[i] = rowValues[i];
						}
					}
				} else {
					csvRow.setCommentLine(true);
				}
				rows.add(csvRow);
			}
			if (!info.header) {
				getInfo().headers = new String[getInfo().cols];
				for (int i = 0; i < getInfo().cols; i++) {
					getInfo().headers[i] = "Column" + (i + 1);
				}
			}

			csvReader.close();
		} catch (final Exception e) {
			System.out.println("exception in readLines " + e);
			e.printStackTrace();
		}
		saveMetaData();
	}

	/**
	 * @return
	 */
	public List<String> getHeader() {
		return Arrays.asList(getInfo().headers);
	}

	/**
	 * @return
	 */
	public String[] getArrayHeader() {
		return getInfo().headers;
	}

	// ----------------------------------
	// Helper method on rows management
	// ----------------------------------

	/**
	 * @param row
	 */
	// public void duplicateRow(final CSVRow row) {
	// CSVRow newRow = new CSVRow(row, this);
	// CSVInfo info = getInfo();
	// int indexRow = findRow(row);
	// if ( indexRow != -1 ) {
	// rows.add(indexRow, newRow);
	// } else {
	// addRow(newRow);
	// }
	// info.rows++;
	// saveMetaData();
	// }

	/**
	 *
	 */
	public void addRow() {
		final CSVRow row = CSVRow.createEmptyLine(getInfo().cols, this);
		addRow(row);
	}

	/**
	 * @param row
	 */
	public void addRow(final CSVRow row) {
		rows.add(row);
		final CSVInfo info = getInfo();
		info.rows++;
		saveMetaData();

	}

	/**
	 * @param row
	 */
	public void addRowAfterElement(final CSVRow row) {
		final CSVInfo info = getInfo();
		final CSVRow newRow = CSVRow.createEmptyLine(info.cols, this);
		final int indexRow = findRow(row);

		if (indexRow != -1) {
			rows.add(indexRow, newRow);
			info.rows++;
		} else {
			addRow(newRow);
		}
		saveMetaData();
	}

	/**
	 * @param row
	 * @return
	 */
	public int findRow(final CSVRow findRow) {
		for (int i = 0; i <= getArrayRows(true).length; i++) {
			final CSVRow row = getRowAt(i);
			if (row.equals(findRow)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return
	 */
	public List<CSVRow> getRows() {
		return rows;
	}

	/**
	 * @return
	 */
	public Object[] getArrayRows(final boolean includeCommentLine) {
		// filter header and comment rows
		final ArrayList<CSVRow> myrows = new ArrayList<>();
		for (final CSVRow row : rows) {
			// should we return the comment line
			if (row.isCommentLine()) {
				if (includeCommentLine) {
					myrows.add(row);
				}
			}
			// we do not add the header line
			else if (!row.isHeader()) {
				myrows.add(row);
			}
		}
		return myrows.toArray();
	}

	/**
	 * @param index
	 * @return
	 */
	public CSVRow getRowAt(final int index) {
		return rows.get(index);
	}

	/**
	 * @see org.fhsolution.eclipse.plugins.csvedit.model.IRowChangesListener#rowChanged(org.fhsolution.eclipse.plugins.csvedit.model.CSVRow,
	 *      int)
	 */
	@Override
	public void rowChanged(final CSVRow row, final int rowIndex) {
		for (final ICsvFileModelListener l : listeners) {
			l.entryChanged(row, rowIndex);
		}
	}

	/**
	 * @param rowIndex
	 */
	public void removeRow(final int rowIndex) {
		rows.remove(rowIndex);
	}

	/**
	 *
	 */
	public void removeRow(final CSVRow row) {
		if (!rows.remove(row)) {
			return;
			// TODO return error message
		}
		final CSVInfo info = getInfo();
		info.rows--;
		saveMetaData();
	}

	// ----------------------------------
	// Helper method on column management
	// ----------------------------------

	/**
	 * @param colName
	 */
	public void addColumn(final String colName) {
		final CSVInfo info = getInfo();
		info.cols++;
		info.headers = Arrays.copyOf(info.headers, info.headers.length + 1);
		info.headers[info.headers.length - 1] = colName;
		for (final CSVRow row : rows) {
			row.addElement("");
		}
		saveMetaData();

	}

	/**
	 * @return
	 */
	public int getColumnCount() {
		return getInfo().cols;
	}

	/**
	 * Remove the column represented by the index
	 *
	 * @param colIndex
	 */
	public void removeColumn(final int colIndex) {
		final CSVInfo info = getInfo();
		if (info.header) {
			final ArrayList<String> cols = new ArrayList<>(Arrays.asList(info.headers));
			cols.remove(colIndex);
			info.headers = cols.toArray(new String[cols.size()]);
		}
		info.cols--;
		for (final CSVRow row : rows) {
			if (!row.isCommentLine()) {
				// System.out.println("remove elmt:[" + colIndex + "] in row ["
				// + row + "]");
				row.removeElementAt(colIndex);
			}
		}
		saveMetaData();
	}

	/**
	 * Remove the column represented by its name
	 *
	 * @param colIndex
	 */
	public void removeColumn(final String columnName) {
		if (columnName == null) {
			return;
		}
		final List<String> cols = Arrays.asList(getInfo().headers);
		final int colIndex = cols.indexOf(columnName);
		removeColumn(colIndex);
	}

	/**
	 * @param csvFileListener
	 */
	public void removeModelListener(final ICsvFileModelListener csvFileListener) {
		listeners.remove(csvFileListener);
	}

	/**
	 * @param csvFileListener
	 */
	public void addModelListener(final ICsvFileModelListener csvFileListener) {
		if (!listeners.contains(csvFileListener)) {
			listeners.add(csvFileListener);
		}
	}

	/**
	 * Initialize the CsvWriter
	 * 
	 * @param writer
	 * @return
	 */
	protected CsvWriter initializeWriter(final Writer writer) {
		final char delimiter = getCustomDelimiter();
		final CsvWriter csvWriter = new CsvWriter(writer, delimiter);
		csvWriter.setTextQualifier(getTextQualifier());
		csvWriter.setForceQualifier(useQualifier());
		csvWriter.setComment(getCommentChar());
		return csvWriter;
	}

	/**
	 * @return
	 */
	public String getTextRepresentation() {

		final StringWriter sw = new StringWriter();
		try {
			final CsvWriter clw = initializeWriter(sw);

			for (final CSVRow row : rows) {
				if (row.isCommentLine()) {
					clw.writeComment(row.getComment());
				} else {
					clw.writeRecord(row.getEntriesAsArray());
				}
			}
			clw.close();
			sw.close();
		} catch (final Exception e) {
			System.out.println("cannot write csv file");
			e.printStackTrace();
		} finally {
		}

		return sw.toString();

	}

	/**
	 *
	 */
	public void saveMetaData() {
		// System.out.println("Saving the following metadata: " +
		// getInfo().getSuffix());
		GAMA.getGui().getMetaDataProvider().storeMetadata(file, getInfo(), true);
	}

	/**
	 *
	 */
	public void reload() {
		try {
			readLines(new InputStreamReader(file.getContents()));
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the info
	 */
	public CSVInfo getInfo() {
		if (currentInfo == null) {
			final IGamaFileMetaData metaData = GAMA.getGui().getMetaDataProvider().getMetaData(file, false, true);
			if (metaData instanceof CSVInfo) {
				currentInfo = (CSVInfo) metaData;
			} else {
				GAMA.getGui().getMetaDataProvider().storeMetadata(file, null, true);
				currentInfo = (CSVInfo) GAMA.getGui().getMetaDataProvider().getMetaData(file, false, true);
			}
		}
		return currentInfo;
	}
}
