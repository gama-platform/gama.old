package core.metamodel.io;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.GSSurveyWrapperSerializer;

/**
 * Wrapper class that encapsulate needed information for {@link IGSSurvey}
 * to be created from
 * <p>
 * Also define default value for variable that initialize survey readers,
 * like the index of sheet to read, headers index or csv separator character
 * <p>
 * TODO: move first row and first column data attribute to a schema that describe
 * how many attributes in row & column and how there are organized (in a row, meaning
 * consecutive attribute value are to be concatenated, one per row, multiple in one line, etc.)
 * 
 * @author kevinchapuis
 *
 */
@JsonSerialize(using = GSSurveyWrapperSerializer.class)
public class GSSurveyWrapper {
	
	public static final String RELATIVE_PATH = "RELATIVE PATH";
	
	public static final String SURVEY_TYPE = "SURVEY TYPE";
	public static final String SHEET_NB = "SHEET NB";
	public static final String CSV_SEPARATOR = "SEPARATOR";
	public static final String FIRST_ROW_INDEX = "FRI";
	public static final String FIRST_COLUMN_INDEX = "FCI";
	
	public static final String STORE_IN_MEMORY = "STORE DATA";

	// Cannot set a default value to these variables
	private Path relativePath;
	private GSSurveyType surveyType;

	// Possible default value for these variables
	private int sheetNb = 0;
	private char csvSeparator = ',';
	private int firstRowIndex = 1;
	private int firstColumnIndex = 1;

	private boolean storedInMemory = true;

	/**
	 * Json required default constructor
	 */
	protected GSSurveyWrapper() {}

	/**
	 * Wrapper for default survey file attribute
	 * 
	 * @param absoluteFilePath
	 */
	public GSSurveyWrapper(Path relativePath, GSSurveyType surveyType) {
		this.setRelativePath(relativePath);
		this.setSurveyType(surveyType);
	}

	/**
	 * Wrapper for Excel type survey
	 * 
	 * @param absoluteFilePath
	 * @param sheetNb
	 * @param firstRowIndex
	 * @param firstColumnIndex
	 */
	public GSSurveyWrapper(Path relativePath, GSSurveyType surveyType, 
			int sheetNb, int firstRowIndex, int firstColumnIndex) {
		this(relativePath, true, surveyType, sheetNb, firstRowIndex, firstColumnIndex);
	}
	
	/**
	 * Wrapper for Excel type survey
	 * 
	 * @param absoluteFilePath
	 * @param sheetNb
	 * @param firstRowIndex
	 * @param firstColumnIndex
	 */
	public GSSurveyWrapper(Path relativePath, boolean storeInMemory, GSSurveyType surveyType, 
			int sheetNb, int firstRowIndex, int firstColumnIndex) {
		this(relativePath, surveyType);
		this.setSheetNumber(sheetNb);
		this.setStoredInMemory(storeInMemory);
		this.setFirstRowIndex(firstRowIndex);
		this.setFirstColumnIndex(firstColumnIndex);
	}
	
	/**
	 * Wrapper for csv type survey
	 * 
	 * @param absoluteFilePath
	 * @param csvSeparator
	 * @param firstRowIndex
	 * @param firstColumnIndex
	 */
	public GSSurveyWrapper(Path relativePath, GSSurveyType surveyType, 
			char csvSeparator, int firstRowIndex, int firstColumnIndex) {
		this(relativePath, true, surveyType, csvSeparator, firstRowIndex, firstColumnIndex);
	}

	/**
	 * Wrapper for csv type survey
	 * 
	 * @param absoluteFilePath
	 * @param csvSeparator
	 * @param firstRowIndex
	 * @param firstColumnIndex
	 */
	public GSSurveyWrapper(Path relativePath, boolean storeInMemory, GSSurveyType surveyType, 
			char csvSeparator, int firstRowIndex, int firstColumnIndex) {
		this(relativePath, surveyType);
		this.setStoredInMemory(storeInMemory);
		this.setCsvSeparator(csvSeparator);
		this.setStoredInMemory(storeInMemory);
		this.setFirstColumnIndex(firstColumnIndex);
		this.setFirstRowIndex(firstRowIndex);
	}

	// ------------------- GETTERS & SETTERS ------------------- //

	/**
	 * Get the relative {@link Path} to the file
	 * 
	 * @return
	 */
	@JsonProperty(GSSurveyWrapper.RELATIVE_PATH)
	public Path getRelativePath(){
		return relativePath;
	}
	
	@JsonProperty(GSSurveyWrapper.RELATIVE_PATH)
	public void setRelativePath(Path relativePath) {
		this.relativePath = relativePath;
	}

	/**
	 * Get the type of survey as a {@link GSSurveyType} enum
	 * 
	 * @see GSSurveyType
	 * @return
	 */
	@JsonProperty(GSSurveyWrapper.SURVEY_TYPE)
	public GSSurveyType getSurveyType(){
		return surveyType;
	}
	
	@JsonProperty(GSSurveyWrapper.SURVEY_TYPE)
	public void setSurveyType(GSSurveyType surveyType) {
		this.surveyType = surveyType;
	}

	/**
	 * Get the number associated with current sheet (from a tabular data
	 * frame Excel type)
	 * 
	 * @return
	 */
	@JsonProperty(GSSurveyWrapper.SHEET_NB)
	public int getSheetNumber() {
		return sheetNb;
	}
	
	@JsonProperty(GSSurveyWrapper.SHEET_NB)
	public void setSheetNumber(int sheetNb) {
		this.sheetNb = sheetNb;
	}

	/**
	 * Give the csv separator character
	 * 
	 * @return
	 */
	@JsonProperty(GSSurveyWrapper.CSV_SEPARATOR)
	public char getCsvSeparator() {
		return csvSeparator;
	}
	
	@JsonProperty(GSSurveyWrapper.CSV_SEPARATOR)
	public void setCsvSeparator(char csvSeparator) {
		this.csvSeparator = csvSeparator;
	}

	/**
	 * Get the first row data index. Former index denote then
	 * headers and the like
	 * 
	 * @return
	 */
	@JsonProperty(GSSurveyWrapper.FIRST_ROW_INDEX)
	public int getFirstRowIndex(){
		return firstRowIndex;
	}
	
	@JsonProperty(GSSurveyWrapper.FIRST_ROW_INDEX)
	public void setFirstRowIndex(int firstRowIndex) {
		this.firstRowIndex = firstRowIndex;
	}

	/**
	 * Get the first column data index. Former index denote then
	 * info about the content of each row
	 * 
	 * @return
	 */
	@JsonProperty(GSSurveyWrapper.FIRST_COLUMN_INDEX)
	public int getFirstColumnIndex(){
		return firstColumnIndex;
	}
	
	@JsonProperty(GSSurveyWrapper.FIRST_COLUMN_INDEX)
	public void setFirstColumnIndex(int firstColumnIndex) {
		this.firstColumnIndex = firstColumnIndex;
	}
	
	/**
	 * If the data should be stored in memory or not
	 * 
	 * @return
	 */
	@JsonProperty(GSSurveyWrapper.STORE_IN_MEMORY)
	public boolean getStoredInMemory() {
		return this.storedInMemory ;
	}
	
	@JsonProperty(GSSurveyWrapper.STORE_IN_MEMORY)
	public void setStoredInMemory(boolean storedInMemory) {
		this.storedInMemory = storedInMemory;
	}

	// ------------------------------------------------------- //

	public String toString() {
		return "wrapper to survey "+relativePath;
	}
}
