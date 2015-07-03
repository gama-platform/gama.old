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
package msi.gama.gui.viewers.csv.model;

import java.io.*;
import java.util.*;
import msi.gama.gui.navigator.FileMetaDataProvider;
import msi.gama.gui.navigator.commands.ResourceRefreshHandler;
import msi.gama.util.file.*;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 *
 * @author fhenri
 *
 */
public class CSVModel implements IRowChangesListener {

	// private int nbOfColumns;
	private boolean displayFirstLine;
	private final ArrayList<CSVRow> rows;
	// private final ArrayList<String> header;
	private final ArrayList<ICsvFileModelListener> listeners;
	private final CSVInfo info;
	private final IFile file;

	/**
	 * Default constructor
	 */
	public CSVModel(final IFile file) {
		info = (CSVInfo) FileMetaDataProvider.getInstance().getMetaData(file, false);
		this.file = file;
		// nbOfColumns = 1;
		displayFirstLine = true;
		rows = new ArrayList<CSVRow>();
		listeners = new ArrayList<ICsvFileModelListener>();
		// header = new ArrayList<String>();
	}

	/**
	 * Check if first line in the file will be considered as the file header
	 * @return true if the first line in the file represents the header
	 */
	public boolean isFirstLineHeader() {
		return info.header;
	}

	public void setFirstLineHeader(final boolean header) {
		info.header = header;
	}

	/**
	 * Get custom delimiter to use as a separator
	 * @return the delimiter
	 */
	public char getCustomDelimiter() {
		return info.delimiter;
	}

	public void setCustomDelimiter(final char c) {
		if ( c == info.delimiter ) { return; }
		info.delimiter = c;
	}

	/**
	 * Get the character that defines comment lines
	 * @return the comment line starting character. If no comments are allowed in this
	 *         file, then Character.UNASSIGNED constant must be returned;
	 *
	 */
	public char getCommentChar() {
		char result = Character.UNASSIGNED;
		return result;
	}

	/**
	 * Get custom text qualifier to use as a text qualifier in the data
	 * @return the text qualifier character to use as a text qualifier in the data
	 */
	public char getTextQualifier() {
		char result = Character.UNASSIGNED;
		return result;
	}

	/**
	 * check if the text qualifier has to be use for all fields or not
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
		CsvReader csvReader = new CsvReader(reader);

		char customDelimiter = getCustomDelimiter();
		csvReader.setDelimiter(customDelimiter);

		char commentChar = getCommentChar();
		if ( commentChar != Character.UNASSIGNED ) {
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
		info.cols = 0;

		try {
			CsvReader csvReader = initializeReader(reader);
			// case when the first line is the encoding
			if ( !displayFirstLine ) {
				csvReader.skipLine();
			}

			boolean setHeader = false;
			while (csvReader.readRecord()) {
				String[] rowValues = csvReader.getValues();
				if ( rowValues.length > info.cols ) {
					info.cols = rowValues.length;
				}
				CSVRow csvRow = new CSVRow(rowValues, this);
				if ( !rowValues[0].startsWith(String.valueOf(getCommentChar())) ) {
					if ( info.header && !setHeader ) {
						setHeader = true;
						csvRow.setHeader(true);
						populateHeaders(rowValues);
					}
				} else {
					csvRow.setCommentLine(true);
				}
				rows.add(csvRow);

			}

			if ( !info.header ) {
				populateHeaders(null);
			}

			csvReader.close();
		} catch (Exception e) {
			System.out.println("exception in readLines " + e);
			e.printStackTrace();
		}
		this.discardMetaData();
	}

	// ----------------------------------
	// Helper method on header management
	// ----------------------------------
	/**
	 * @param entries
	 */
	private void populateHeaders(final String[] entries) {
		info.headers = new String[info.cols];
		Arrays.fill(info.headers, "");
		if ( entries != null ) {
			System.arraycopy(entries, 0, info.headers, 0, entries.length);
		} else {
			for ( int i = 0; i < info.cols; i++ ) {
				info.headers[i] = "Column" + (i + 1);
			}
		}
	}

	/**
	 * @return
	 */
	public List<String> getHeader() {
		return Arrays.asList(info.headers);
	}

	/**
	 * @return
	 */
	public String[] getArrayHeader() {
		return info.headers;
	}

	// ----------------------------------
	// Helper method on rows management
	// ----------------------------------

	/**
	 * @param row
	 */
	public void duplicateRow(final CSVRow row) {
		CSVRow newRow = new CSVRow(row, this);
		int indexRow = findRow(row);
		if ( indexRow != -1 ) {
			rows.add(indexRow, newRow);
			discardMetaData();

		} else {
			addRow(newRow);
		}
	}

	/**
	 *
	 */
	public void addRow() {
		CSVRow row = CSVRow.createEmptyLine(info.cols, this);
		addRow(row);
	}

	/**
	 * @param row
	 */
	public void addRow(final CSVRow row) {
		rows.add(row);
		discardMetaData();

	}

	/**
	 * @param row
	 */
	public void addRowAfterElement(final CSVRow row) {
		CSVRow newRow = CSVRow.createEmptyLine(info.cols, this);
		int indexRow = findRow(row);

		if ( indexRow != -1 ) {
			rows.add(indexRow, newRow);
			discardMetaData();

		} else {
			addRow(newRow);
		}

	}

	/**
	 * @param row
	 * @return
	 */
	public int findRow(final CSVRow findRow) {
		for ( int i = 0; i <= getArrayRows(true).length; i++ ) {
			CSVRow row = getRowAt(i);
			if ( row.equals(findRow) ) { return i; }
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
		ArrayList<CSVRow> myrows = new ArrayList<CSVRow>();
		for ( CSVRow row : rows ) {
			// should we return the comment line
			if ( row.isCommentLine() ) {
				if ( includeCommentLine ) {
					myrows.add(row);
				}
			}
			// we do not add the header line
			else if ( !row.isHeader() ) {
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
	 * @see org.fhsolution.eclipse.plugins.csvedit.model.IRowChangesListener#rowChanged(org.fhsolution.eclipse.plugins.csvedit.model.CSVRow, int)
	 */
	@Override
	public void rowChanged(final CSVRow row, final int rowIndex) {
		for ( ICsvFileModelListener l : listeners ) {
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
		if ( !rows.remove(row) ) {
			// TODO return error message
		}
	}

	// ----------------------------------
	// Helper method on column management
	// ----------------------------------

	/**
	 * @param colName
	 */
	public void addColumn(final String colName) {
		info.cols++;
		info.headers = Arrays.copyOf(info.headers, info.headers.length + 1);
		info.headers[info.headers.length - 1] = colName;
		for ( CSVRow row : rows ) {
			row.addElement("");
		}
		discardMetaData();

	}

	/**
	 * @return
	 */
	public int getColumnCount() {
		return info.cols;
	}

	/**
	 * Remove the column represented by the index
	 *
	 * @param colIndex
	 */
	public void removeColumn(final int colIndex) {
		if ( info.header ) {
			ArrayList<String> cols = new ArrayList(Arrays.asList(info.headers));
			cols.remove(colIndex);
			info.headers = cols.toArray(new String[cols.size()]);
		}
		info.cols--;
		for ( CSVRow row : rows ) {
			if ( !row.isCommentLine() ) {
				System.out.println("remove elmt:[" + colIndex + "] in row [" + row + "]");
				row.removeElementAt(colIndex);
			}
		}
		discardMetaData();
	}

	/**
	 * Remove the column represented by its name
	 *
	 * @param colIndex
	 */
	public void removeColumn(final String columnName) {
		if ( columnName == null ) { return; }
		List<String> cols = Arrays.asList(info.headers);
		int colIndex = cols.indexOf(columnName);
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
		if ( !listeners.contains(csvFileListener) ) {
			listeners.add(csvFileListener);
		}
	}

	/**
	 * Initialize the CsvWriter
	 * @param writer
	 * @return
	 */
	protected CsvWriter initializeWriter(final Writer writer) {
		char delimiter = getCustomDelimiter();
		CsvWriter csvWriter = new CsvWriter(writer, delimiter);
		csvWriter.setTextQualifier(getTextQualifier());
		csvWriter.setForceQualifier(useQualifier());
		csvWriter.setComment(getCommentChar());
		return csvWriter;
	}

	/**
	 * @return
	 */
	public String getTextRepresentation() {

		StringWriter sw = new StringWriter();
		try {
			CsvWriter clw = initializeWriter(sw);

			for ( CSVRow row : rows ) {
				if ( row.isCommentLine() ) {
					clw.writeComment(row.getComment());
				} else {
					clw.writeRecord(row.getEntriesAsArray());
				}
			}
			clw.close();
			sw.close();
		} catch (Exception e) {
			System.out.println("cannot write csv file");
			e.printStackTrace();
		} finally {}

		return sw.toString();

	}

	/**
	 * @return
	 */
	public CSVInfo getCurrentMetaData() {
		return info;
	}

	/**
	 *
	 */
	public void discardMetaData() {
		// reload();

		// System.out.println("Saving the following metadata: " + info.getSuffix());

		ResourceRefreshHandler.discardMetaData(file);
	}

	/**
	 *
	 */
	public void reload() {
		try {
			readLines(new InputStreamReader(file.getContents()));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
