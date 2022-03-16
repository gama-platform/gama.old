package gospl.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import core.metamodel.io.GSSurveyType;

/**
 * A wrapper of OpenCSV to read csv based data
 * 
 * TODO : from the API, DEAL WITH IT => "Reads the entire file into a List with each element being a String[] of tokens. 
 * Since the current implementation returns a LinkedList, you are strongly discouraged from using index-based access methods to get at items in the list. 
 * Instead, iterate over the list."
 *  
 * @author kevinchapuis
 *
 */
public class CsvInputHandler extends AbstractInputHandler {

	/**
	 * The list of the separators which might be detected automatically.
	 */
	private static final char[] CSV_SEPARATORS_FROM_DETECTION = new char[] {',',';',':','|',' ','\t'};
	
	/*
	 * 
	 */
	private static final int CHUNK_SIZE = 100000;
	private static final boolean CHUNK = false;
	private Map<Integer,List<String[]>> dataTables;
	
	private List<String[]> dataTable;
	
	private int firstRowDataIndex;
	private int firstColumnDataIndex;
	
	private String charset;
	private char csvSeparator;
	
	private boolean storeInMemory;
	// If the data have not been stored in memory still have to get some stats
	private int lastRowIndex = -1;
	private int lastColumnIndex = -1;

	protected CsvInputHandler(String fileName, char csvSeparator, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) throws IOException{
		this(fileName, true, csvSeparator, firstRowDataIndex, firstColumnDataIndex, dataFileType);
	}
	
	protected CsvInputHandler(String fileName, boolean storeInMemory, char csvSeparator, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) throws IOException{
		this(fileName, storeInMemory, Charset.defaultCharset().name(), csvSeparator, 
				firstRowDataIndex, firstColumnDataIndex, dataFileType);
	}
	
	/**
	 * 
	 * @param fileName
	 * @param storeInMemory
	 * @param charset
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param dataFileType
	 * @throws IOException
	 */
	protected CsvInputHandler(String fileName, boolean storeInMemory, String charset, char csvSeparator, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) throws IOException{
		super(dataFileType, fileName);
		
		this.storeInMemory = storeInMemory;
		this.csvSeparator = csvSeparator;
		this.charset = charset;
		
		CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(surveyCompleteFile), this.charset), csvSeparator);
		if(this.storeInMemory) {
			if (CHUNK) {this.dataTables = chunkData(reader, CHUNK_SIZE);}
			else {dataTable = reader.readAll();}
		} else {
			// Store header in memory
			dataTable = new ArrayList<>();
			int length = 0;
			for (int row = 0; row <  firstRowDataIndex; row++) { dataTable.add(reader.readNext()); length++;}
			String[] rec = null;
			do { rec = reader.readNext(); if (this.lastColumnIndex < 0)  {this.lastColumnIndex = rec.length-1;} length++;}  while (rec != null);
			this.lastRowIndex = length-1;
		}
		reader.close();
		
		this.firstRowDataIndex = firstRowDataIndex;
		this.firstColumnDataIndex = firstColumnDataIndex;
	}

	protected CsvInputHandler(File file, char csvSeparator, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) throws IOException {
		this(file, true, csvSeparator, firstRowDataIndex, firstColumnDataIndex, dataFileType);
	}
	
	protected CsvInputHandler(File file, boolean storeInMemory, char csvSeparator, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) throws IOException {
		this(file, storeInMemory, Charset.defaultCharset().name(), csvSeparator, firstRowDataIndex, firstColumnDataIndex, dataFileType);
	}
	
	/**
	 * 
	 * @param file
	 * @param storeInMemory
	 * @param charset
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param dataFileType
	 * @throws IOException
	 */
	protected CsvInputHandler(File file, boolean storeInMemory, String charset, char csvSeparator, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) throws IOException {
		
		super(dataFileType, file);
		
		this.storeInMemory = storeInMemory;
		this.charset = charset;
		this.csvSeparator = csvSeparator;
		
		CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(surveyCompleteFile), this.charset), csvSeparator);
		if(this.storeInMemory) {
			if(CHUNK) {this.dataTables = chunkData(reader, CHUNK_SIZE);}
			else {dataTable = reader.readAll();}
		} else {
			// Store header in memory
			dataTable = new ArrayList<>();
			int length = 0;
			for (int row = 0; row <  firstRowDataIndex; row++) { dataTable.add(reader.readNext()); length++;}
			String[] rec = null;
			do { rec = reader.readNext(); if (this.lastColumnIndex < 0)  {this.lastColumnIndex = rec.length-1;} length++;}  while (rec != null);
			this.lastRowIndex = length-1;
		}
		reader.close();
		
		if (dataTable != null) {
			for (String[] str : dataTable) {
				for (int i = 0; i < str.length; i++) {
					if (str[i].isBlank())
						str[i] = "";
				}
			}
		}
		this.firstRowDataIndex = firstRowDataIndex;
		this.firstColumnDataIndex = firstColumnDataIndex;
		
	}


	protected CsvInputHandler(String fileName, InputStream surveyIS, char csvSeparator, 
			int firstRowDataIndex, int firstColumnDataIndex, GSSurveyType dataFileType) throws IOException { 
		this(fileName, Charset.defaultCharset().name(), surveyIS, true, csvSeparator, 
				firstRowDataIndex, firstColumnDataIndex, dataFileType);
	}

	protected CsvInputHandler(String fileName, String charset, InputStream surveyIS, boolean storeInMemory, 
			char csvSeparator, int firstRowDataIndex, int firstColumnDataIndex, GSSurveyType dataFileType) 
					throws IOException {
		super(dataFileType, fileName);

		this.storeInMemory = storeInMemory;
		this.charset = charset;
		this.csvSeparator = csvSeparator;
		
		CSVReader reader = new CSVReader(new InputStreamReader(surveyIS, this.charset), csvSeparator);
		if(this.storeInMemory) {
			if (CHUNK) {this.dataTables = chunkData(reader, CHUNK_SIZE);}
			else {dataTable = reader.readAll();}
		} else {
			// Store header in memory
			dataTable = new ArrayList<>();
			int length = 0;
			for (int row = 0; row <  firstRowDataIndex; row++) { dataTable.add(reader.readNext()); length++;}
			String[] rec = null;
			do { rec = reader.readNext(); if (this.lastColumnIndex < 0)  {this.lastColumnIndex = rec.length-1;} length++;}  while (rec != null);
			this.lastRowIndex = length-1;
		}
		reader.close();
		
		this.firstRowDataIndex = firstRowDataIndex;
		this.firstColumnDataIndex = firstColumnDataIndex;
	}

// ------------------------ unique value parser ------------------------ //
	
	@Override
	public String read(int rowIndex, int columnIndex){
		if(CHUNK) { return this.readLine(rowIndex).get(columnIndex); }
		return dataTable.get(rowIndex)[columnIndex].trim();
	}
	
// ------------------------ Line-parser methods ------------------------ //
	
	@Override
	public List<String> readLine(int rowNum) {
		if(!storeInMemory) {throw new NullPointerException("Data have not been stored in memory - use #getBufferReader() to access data");}
		if(CHUNK) {
			List<String[]> chunk = dataTables.get(((int) rowNum/CHUNK_SIZE) * CHUNK_SIZE);
			return Arrays.asList(chunk.get(rowNum%CHUNK_SIZE));
		}
		List<String> line = Arrays.asList(dataTable.get(rowNum));
		return line;
	}

	@Override
	public List<List<String>> readLines(
			int fromFirstRowIndex, int toLastRowIndex) {
		List<List<String>> lines = new ArrayList<List<String>>();
		for (int i = fromFirstRowIndex; i < toLastRowIndex; i++)
			lines.add(readLine(i));
		return lines;
	}
	
	@Override
	public List<String> readLines(
			int fromFirstRowIndex, int toLastRowIndex, 
			int columnIndex) {
		List<String> line = new ArrayList<>();
		for (int i = fromFirstRowIndex; i < toLastRowIndex; i++)
			line.add(this.read(i, columnIndex));
		return line;
	}

	@Override
	public List<List<String>> readLines(
			int fromFirstRowIndex, int toLastRowIndex, 
			int fromFirstColumnIndex, int toLastColumnIndex) {
		List<List<String>> lines = new ArrayList<List<String>>();
		for(int i = fromFirstRowIndex; i < toLastRowIndex; i++)
			lines.add(new ArrayList<String>(this.readLine(i).subList(fromFirstColumnIndex, toLastColumnIndex)));
		return lines;
	}

// ------------------------ Column-parser methods ------------------------ //
	
	@Override
	public List<String> readColumn(int columnIndex) {
		if(!storeInMemory) {throw new NullPointerException("Data have not been stored in memory - use #getBufferReader() to access data");}
		if(CHUNK) {
			List<String> col = new ArrayList<>();
			for(Integer chunkIdx : dataTables.keySet()) {
				Iterator<String[]> it = dataTables.get(chunkIdx).iterator();
				while(it.hasNext()){
					String[] line = it.next();
					col.add(line[columnIndex]);
				}
			}
			
		}
		List<String> column = new ArrayList<String>();
		Iterator<String[]> it = dataTable.iterator();
		while(it.hasNext()){
			String[] line = it.next();
			column.add(line[columnIndex]);
		}
		return column;
	}

	@Override
	public List<List<String>> readColumns(
			int fromFirstColumnIndex, int toLastColumnIndex) {
		List<List<String>> columns = new ArrayList<List<String>>();
		for(int i = fromFirstColumnIndex; i < toLastColumnIndex; i++)
			columns.add(this.readColumn(i));
		return columns;
	}
	
	@Override
	public List<String> readColumns(
			int fromFirstColumnIndex, int toLastColumnIndex, 
			int rowIndex) {
		List<String> column = new ArrayList<String>();
		for(int i = fromFirstColumnIndex; i < toLastColumnIndex; i++)
			column.add(this.read(rowIndex, i));
		return column;
	}

	@Override
	public List<List<String>> readColumns(
			int fromFirstLine, int toLastLine, int fromFirstVariable,
			int toLastVariable) {
		List<List<String>> columns = new ArrayList<List<String>>();
		for(int i = fromFirstVariable; i < toLastVariable; i++)
			columns.add(new ArrayList<String>(this.readColumn(i).subList(fromFirstLine, toLastLine)));
		return columns;
	}
	
// -----------------------------

	@Override
	public String getName() {
		return surveyFileName;
	}
	
	@Override
	public int getFirstRowIndex() {
		return firstRowDataIndex;
	}

	@Override
	public int getFirstColumnIndex() {
		return firstColumnDataIndex;
	}
	
	@Override
	public int getLastRowIndex(){
		if(!storeInMemory) {return lastRowIndex;}
		return dataTable==null || dataTable.isEmpty() ? 
				(dataTables.keySet().size()-1)*CHUNK_SIZE+dataTables.get(Collections.max(dataTables.keySet())).size() : 
					dataTable.size() - 1;
	}
	
	@Override
	public int getLastColumnIndex() {
		if(!storeInMemory) {return lastColumnIndex;}
		if(dataTable == null || dataTable.isEmpty()) {
			return dataTables.values().stream().findFirst().get().iterator().next().length;
		}
		String[] firstRow = dataTable.get(0);
		return firstRow.length - 1;
	}
	
	@Override
	public String toString(){
		String s = "";
		s+="Survey name: "+getName()+"\n";
		s+="\tline number: "+(!storeInMemory?lastRowIndex+1:dataTable.size());
		s+="\tcolumn number: "+(!storeInMemory?lastColumnIndex+1:dataTable.get(0).length);
		return s;
	}

	/**
	 * From a given CSV file, tries to detect a plausible separator. 
	 * Will take the one which is used in most lines with the lowest variance.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static char detectSeparator(File f) throws IOException {
		return detectSeparator(f, Charset.defaultCharset().name(), CSV_SEPARATORS_FROM_DETECTION);
	}
	
	/**
	 * From a given CSV file, tries to detect a plausible separator. 
	 * Will take the one which is used in most lines with the lowest variance.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static char detectSeparator(File f, String charset) throws IOException {
		return detectSeparator(f, charset, CSV_SEPARATORS_FROM_DETECTION);
	}
		
	/**
	 * From a given CSV file, tries to detect a plausible separator. 
	 * Will take the one which is used in most lines with the lowest variance.
	 * 
	 * @param f
	 * @param candidates
	 * @return
	 * @throws IOException
	 */
	public static char detectSeparator(File f, String charset, char[] candidates) throws IOException {
		
		int countLines = 20;
		
		// read the first n lines
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
		List<String> firstLines = new ArrayList<>(countLines);
		while (bf.ready()) {
			firstLines.add(bf.readLine());
		}
		// close the file
		bf.close();
			
		countLines = firstLines.size();
		
		if (countLines < 3)
			throw new IllegalArgumentException("cannot detect automatically the CSV separators from so few lines, sorry");

		// we will count the number of occurences of each char in each line
		int[][] counts = new int[countLines][candidates.length]; // automatically init to 0
	
		for (int iline=0; iline<countLines; iline++) {
			for (int i=0; i<candidates.length; i++) {
				counts[iline][i] = StringUtils.countMatches(firstLines.get(iline), ""+candidates[i]);
			}
		}
		
		// so at the end we now how many instances of each separator were found
		double[] averageOccurences = new double[candidates.length];
		double[] variance = new double[candidates.length];
		for (int i=0; i<candidates.length; i++) {
			
			// what is the average of this column?
			for (int iline=0; iline<countLines; iline++) {
				averageOccurences[i] += counts[iline][i];
			}
			averageOccurences[i] = averageOccurences[i]/countLines;
			
			// and so, was is its variance ?
			for (int iline=0; iline<countLines; iline++) {
				variance[i] += Math.pow(counts[iline][i] - averageOccurences[i], 2);
			}
			variance[i] = variance[i]/countLines;
			
		}
		
		String msg = "";
		for (int i=0; i<candidates.length; i++) {
			msg += candidates[i]+": "+averageOccurences[i]+" ~ "+variance[i]+" \n";
		}
		//Log.debug("candidates for separators: "+msg);
		
		// now select the ones which might be relevant, that is the ones with more than one occurence per line
		List<Integer> relevant = new LinkedList<>();
		for (int i=0; i<candidates.length; i++) {
			if (averageOccurences[i]>=1)
				relevant.add(i);
		}
		
		// and select the one with the lowest variance
		Collections.sort(relevant, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Double.compare(variance[o1], variance[o2]);
			}
			
		});
		
	//	Log.debug("order of merit for separators: "+relevant);
		if (relevant.isEmpty()) {
			//Log.warn("no separator detected in this file; it probably contains one or no column");
			return ';';
		}
		return candidates[relevant.get(0)];
		
	}

	/**
	 * TODO : move List<String[]> to a proper CharBuffer with decoder to be lighter in memory 
	 * 
	 * @param reader
	 * @param chunkSize
	 * @return
	 * @throws IOException
	 */
	static Map<Integer, List<String[]>> chunkData(CSVReader reader, int chunkSize) throws IOException {
		Map<Integer,List<String[]>> chunks = new HashMap<>();
		chunks.put(chunkSize, new ArrayList<>());
		int idx = chunkSize;
		int count = 0;
		do {
			String[] line = reader.readNext();
			if(line==null) {return chunks;}
			else {chunks.get(idx).add(line);}
			count++;
			if(count%chunkSize==0) { idx += chunkSize; chunks.put(idx, new ArrayList<>()); 
			System.out.println("[SYSO::"+CsvInputHandler.class.getSimpleName()+"] "+idx+" record have been done");}
		} while (true);
	}
 
	@Override
	public CSVReader getBufferReader(boolean skipHeader) throws UnsupportedEncodingException, FileNotFoundException {
		CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(surveyCompleteFile), this.charset), csvSeparator);
		if(skipHeader) { IntStream.range(0, firstRowDataIndex).forEach(i -> {
			try {
				reader.readNext();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}); }
		return reader;
	}

}
