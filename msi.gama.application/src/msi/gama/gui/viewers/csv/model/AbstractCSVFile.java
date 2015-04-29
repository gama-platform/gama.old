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
import msi.gama.util.file.*;

/**
 * 
 * @author fhenri
 * 
 */
public abstract class AbstractCSVFile implements IRowChangesListener {

	private int nbOfColumns;
	private boolean displayFirstLine;
	private final ArrayList<CSVRow> rows;
	private final ArrayList<String> header;
	private final ArrayList<ICsvFileModelListener> listeners;

	/**
	 * Default constructor
	 */
	public AbstractCSVFile() {
		nbOfColumns = 1;
		displayFirstLine = true;
		rows = new ArrayList<CSVRow>();
		listeners = new ArrayList<ICsvFileModelListener>();
		header = new ArrayList<String>();
	}

	// TODO : all abstract methods should be moved to a specific interface
	/**
	 * Check if first line in the file will be considered as the file header
	 * @return true if the first line in the file represents the header
	 */
	public abstract boolean isFirstLineHeader();

	public abstract void setFirstLineHeader(boolean header);

	/**
	 * Check search in the text must be case sensitive
	 * @return true if the search must be case sensitive.
	 */
	public abstract boolean getSensitiveSearch();

	/**
	 * Get custom delimiter to use as a separator
	 * @return the delimiter
	 */
	public abstract char getCustomDelimiter();

	/**
	 * Get the character that defines comment lines
	 * @return the comment line starting character. If no comments are allowed in this
	 *         file, then Character.UNASSIGNED constant must be returned;
	 * 
	 */
	public abstract char getCommentChar();

	/**
	 * Get custom text qualifier to use as a text qualifier in the data
	 * @return the text qualifier character to use as a text qualifier in the data
	 */
	public abstract char getTextQualifier();

	/**
	 * check if the text qualifier has to be use for all fields or not
	 * @return true if the text qualifier is to be used for all data fields
	 */
	public abstract boolean useQualifier();

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

	/**
	 * @param fileText
	 */
	protected void readLines(final String fileText) {
		rows.clear();

		try {
			CsvReader csvReader = initializeReader(new StringReader(fileText));
			// case when the first line is the encoding
			if ( !displayFirstLine ) {
				csvReader.skipLine();
			}

			boolean setHeader = false;
			while (csvReader.readRecord()) {
				String[] rowValues = csvReader.getValues();
				CSVRow csvRow = new CSVRow(rowValues, this);
				if ( !rowValues[0].startsWith(String.valueOf(getCommentChar())) ) {
					if ( isFirstLineHeader() && !setHeader ) {
						setHeader = true;
						csvRow.setHeader(true);
						populateHeaders(rowValues);
					}
				} else {
					csvRow.setCommentLine(true);
				}
				rows.add(csvRow);

				if ( rowValues.length > nbOfColumns ) {
					nbOfColumns = rowValues.length;
				}
			}

			if ( !isFirstLineHeader() ) {
				populateHeaders(null);
			}

			csvReader.close();
		} catch (Exception e) {
			System.out.println("exception in readLines " + e);
			e.printStackTrace();
		}
	}

	// ----------------------------------
	// Helper method on header management
	// ----------------------------------
	/**
	 * @param entries
	 */
	private void populateHeaders(final String[] entries) {
		header.clear();

		if ( entries != null ) {
			for ( String entry : entries ) {
				header.add(entry);
			}

			for ( int i = header.size(); i < nbOfColumns; i++ ) {
				header.add("");
			}
		} else {
			for ( int i = 1; i < nbOfColumns + 1; i++ ) {
				header.add("Column" + i);
			}
		}
	}

	/**
	 * @return
	 */
	public ArrayList<String> getHeader() {
		return header;
	}

	/**
	 * @return
	 */
	public String[] getArrayHeader() {
		return header.toArray(new String[header.size()]);
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
		} else {
			addRow(newRow);
		}
	}

	/**
     *
     */
	public void addRow() {
		CSVRow row = CSVRow.createEmptyLine(nbOfColumns, this);
		addRow(row);
	}

	/**
	 * @param row
	 */
	public void addRow(final CSVRow row) {
		rows.add(row);
	}

	/**
	 * @param row
	 */
	public void addRowAfterElement(final CSVRow row) {
		CSVRow newRow = CSVRow.createEmptyLine(nbOfColumns, this);
		int indexRow = findRow(row);

		if ( indexRow != -1 ) {
			rows.add(indexRow, newRow);
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
		nbOfColumns++;
		header.add(colName);
		for ( CSVRow row : rows ) {
			row.addElement("");
		}
	}

	/**
	 * @return
	 */
	public int getColumnCount() {
		return nbOfColumns;
	}

	/**
	 * Remove the column represented by the index
	 * 
	 * @param colIndex
	 */
	public void removeColumn(final int colIndex) {
		if ( isFirstLineHeader() ) {
			header.remove(colIndex);
			nbOfColumns--;
		}
		for ( CSVRow row : rows ) {
			if ( !row.isCommentLine() ) {
				System.out.println("remove elmt:[" + colIndex + "] in row [" + row + "]");
				row.removeElementAt(colIndex);
			}
		}
	}

	/**
	 * Remove the column represented by its name
	 * 
	 * @param colIndex
	 */
	public void removeColumn(final String columnName) {
		if ( columnName == null ) { return; }
		int colIndex = header.indexOf(columnName);
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

			/*
			 * if (isFirstLineHeader() && header.size() > 0) {
			 * String[] headerArray = new String[header.size()];
			 * for (int i=0; i<header.size(); i++) {
			 * headerArray[i] = header.get(i);
			 * }
			 * clw.writeRecord(headerArray);
			 * }
			 */
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
}
