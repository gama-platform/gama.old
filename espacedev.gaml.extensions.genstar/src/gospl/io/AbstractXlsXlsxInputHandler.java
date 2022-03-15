package gospl.io;

import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import core.metamodel.io.GSSurveyType;

/**
 * Abstract class that define the general contract for
 * input manager to read workbook, whatever file
 * extension that pertains to the excel family product is. 
 * See concrete sub-class for extension file details handle 
 * through this Input manager program 
 * 
 * @author chapuisk
 *
 */
public abstract class AbstractXlsXlsxInputHandler extends AbstractInputHandler {

	
	private int firstRowDataIndex;
	private int firstColumnDataIndex;
	
	public AbstractXlsXlsxInputHandler(String surveyFileName, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) {
		super(dataFileType, surveyFileName);

		this.firstRowDataIndex = firstRowDataIndex;
		this.firstColumnDataIndex = firstColumnDataIndex;

	}

// ------------------------ unique value parser ------------------------ //
	
	@Override
	public String read(int rowIndex, int columnIndex){
		return null;
	}
	
// ------------------------ Line-parser methods ------------------------ //
	
	@Override
	public List<String> readLine(int rowIndex) {
		return null;
	}
	
	@Override
	public List<List<String>> readLines(
			int fromFirstRowIndex, int toLastRowIndex){
		List<List<String>> lines = new ArrayList<List<String>>();
		for(int i = fromFirstRowIndex; i < toLastRowIndex; i++)
			lines.add(this.readLine(i));
		return lines;
	}
	
	@Override
	public List<String> readLines(
			int fromFirstRowIndex, int toLastRowIndex,
			int columnIndex){
		List<String> lines = new ArrayList<>();
		for(int i = fromFirstRowIndex; i < toLastRowIndex; i++)
			lines.add(this.read(i, columnIndex));
		return lines;
	}
	
	@Override
	public List<List<String>> readLines(
			int fromFirstRowIndex, int toLastRowIndex, 
			int fromFirstColumnIndex, int toLastColumnIndex){
		List<List<String>> lines = new ArrayList<List<String>>();
		for(int i = fromFirstRowIndex; i < toLastRowIndex; i++){
			lines.add(new ArrayList<String>(this.readLine(i).subList(fromFirstColumnIndex, toLastColumnIndex)));
		}
		return lines;
	}

// ------------------------ Column-parser methods ------------------------ //
	
	@Override
	public List<String> readColumn(int columnIndex){
		return null;
	}

	@Override
	public List<List<String>> readColumns(
			int fromFirstColumnIndex, int toLastColumnIndex){
		List<List<String>> columns = new ArrayList<List<String>>();
		for(int i = fromFirstColumnIndex; i < toLastColumnIndex; i++)
			columns.add(this.readColumn(i));
		return columns;
	}
	
	@Override
	public List<String> readColumns(
			int fromFirstColumnIndex, int toLastColumnIndex,
			int rowIndex){
		List<String> columns = new ArrayList<>();
		for(int i = fromFirstColumnIndex; i < toLastColumnIndex; i++)
			columns.add(read(rowIndex, i));
		return columns;
	}
	
	@Override
	public List<List<String>> readColumns(
			int fromFirstRowIndex, int toLastRowIndex, 
			int fromFirstColumnIndex, int toLastColumnIndex){
		List<List<String>> columns = new ArrayList<List<String>>();
		for(int i = fromFirstColumnIndex; i < toLastColumnIndex; i++)
			columns.add(new ArrayList<String>(this.readColumn(i).subList(fromFirstRowIndex, toLastRowIndex)));
		return columns;
	}

// ---------------------------- getter & setter ---------------------------- //
	
	@Override
	public String getName(){
		return surveyFileName;
	}
	
	@Override
	public int getLastRowIndex(){
		return 0;
	}
	
	@Override
	public int getLastColumnIndex(){
		return 0;
	}
	
	@Override
	public int getFirstRowIndex() {
		return firstRowDataIndex;
	}

	@Override
	public int getFirstColumnIndex() {
		return firstColumnDataIndex;
	}
		

// ------------------------------------------------------------------------ //
	
	@Override
	public CSVReader getBufferReader(boolean skipHeader) { throw new UnsupportedOperationException(); }
	
	@Override
	public String toString(){
		String s = "";
		s+="Survey name: "+getName()+"\n";
		s+="\tline number: "+(getLastRowIndex()+1);
		s+="\tcolumn number: "+(getLastColumnIndex()+1);
		return s;
	}
}
