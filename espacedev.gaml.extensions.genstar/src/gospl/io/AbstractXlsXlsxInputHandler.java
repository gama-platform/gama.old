/*******************************************************************************************************
 *
 * AbstractXlsXlsxInputHandler.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.io;

import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import core.metamodel.io.GSSurveyType;

/**
 * Abstract class that define the general contract for input manager to read workbook, whatever file extension that
 * pertains to the excel family product is. See concrete sub-class for extension file details handle through this Input
 * manager program
 *
 * @author chapuisk
 *
 */
public abstract class AbstractXlsXlsxInputHandler extends AbstractInputHandler {

	/** The first row data index. */
	private final int firstRowDataIndex;
	
	/** The first column data index. */
	private final int firstColumnDataIndex;

	/**
	 * Instantiates a new abstract xls xlsx input handler.
	 *
	 * @param surveyFileName the survey file name
	 * @param firstRowDataIndex the first row data index
	 * @param firstColumnDataIndex the first column data index
	 * @param dataFileType the data file type
	 */
	public AbstractXlsXlsxInputHandler(final String surveyFileName, final int firstRowDataIndex,
			final int firstColumnDataIndex, final GSSurveyType dataFileType) {
		super(dataFileType, surveyFileName);

		this.firstRowDataIndex = firstRowDataIndex;
		this.firstColumnDataIndex = firstColumnDataIndex;

	}

	// ------------------------ unique value parser ------------------------ //

	@Override
	public String read(final int rowIndex, final int columnIndex) {
		return null;
	}

	// ------------------------ Line-parser methods ------------------------ //

	@Override
	public List<String> readLine(final int rowIndex) {
		return null;
	}

	@Override
	public List<List<String>> readLines(final int fromFirstRowIndex, final int toLastRowIndex) {
		List<List<String>> lines = new ArrayList<>();
		for (int i = fromFirstRowIndex; i < toLastRowIndex; i++) { lines.add(this.readLine(i)); }
		return lines;
	}

	@Override
	public List<String> readLines(final int fromFirstRowIndex, final int toLastRowIndex, final int columnIndex) {
		List<String> lines = new ArrayList<>();
		for (int i = fromFirstRowIndex; i < toLastRowIndex; i++) { lines.add(this.read(i, columnIndex)); }
		return lines;
	}

	@Override
	public List<List<String>> readLines(final int fromFirstRowIndex, final int toLastRowIndex,
			final int fromFirstColumnIndex, final int toLastColumnIndex) {
		List<List<String>> lines = new ArrayList<>();
		for (int i = fromFirstRowIndex; i < toLastRowIndex; i++) {
			lines.add(new ArrayList<>(this.readLine(i).subList(fromFirstColumnIndex, toLastColumnIndex)));
		}
		return lines;
	}

	// ------------------------ Column-parser methods ------------------------ //

	@Override
	public List<String> readColumn(final int columnIndex) {
		return null;
	}

	@Override
	public List<List<String>> readColumns(final int fromFirstColumnIndex, final int toLastColumnIndex) {
		List<List<String>> columns = new ArrayList<>();
		for (int i = fromFirstColumnIndex; i < toLastColumnIndex; i++) { columns.add(this.readColumn(i)); }
		return columns;
	}

	@Override
	public List<String> readColumns(final int fromFirstColumnIndex, final int toLastColumnIndex, final int rowIndex) {
		List<String> columns = new ArrayList<>();
		for (int i = fromFirstColumnIndex; i < toLastColumnIndex; i++) { columns.add(read(rowIndex, i)); }
		return columns;
	}

	@Override
	public List<List<String>> readColumns(final int fromFirstRowIndex, final int toLastRowIndex,
			final int fromFirstColumnIndex, final int toLastColumnIndex) {
		List<List<String>> columns = new ArrayList<>();
		for (int i = fromFirstColumnIndex; i < toLastColumnIndex; i++) {
			columns.add(new ArrayList<>(this.readColumn(i).subList(fromFirstRowIndex, toLastRowIndex)));
		}
		return columns;
	}

	// ---------------------------- getter & setter ---------------------------- //

	@Override
	public String getName() { return surveyFileName; }

	@Override
	public int getLastRowIndex() { return 0; }

	@Override
	public int getLastColumnIndex() { return 0; }

	@Override
	public int getFirstRowIndex() { return firstRowDataIndex; }

	@Override
	public int getFirstColumnIndex() { return firstColumnDataIndex; }

	// ------------------------------------------------------------------------ //

	@Override
	public CSVReader getBufferReader(final boolean skipHeader) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Survey name: ").append(getName()).append("\n");
		s.append("\tline number: ").append(getLastRowIndex() + 1);
		s.append("\tcolumn number: ").append(getLastColumnIndex() + 1);
		return s.toString();
	}
}
