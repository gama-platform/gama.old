package gospl.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import core.metamodel.io.GSSurveyType;

class XlsInputHandler extends AbstractXlsXlsxInputHandler {
	
	/**
	 * Create a concrete copy of the workbook from the
	 * file named as indicate with parameter. As default
	 * value, the sheet to work with is the first one.
	 * 
	 * @param filename
	 * @param sheetNumber
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param dataFileType
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected XlsInputHandler(String filename, int sheetNumber, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) 
					throws FileNotFoundException, IOException{
		super(filename, firstRowDataIndex, firstColumnDataIndex, dataFileType);
		//wb = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(filename)));
		//this.setCurrentSheet(wb.getSheetAt(sheetNumber));		
	}

	protected XlsInputHandler(File file, int sheetNumber, int firstRowDataIndex, 
			int firstColumnDataIndex, GSSurveyType dataFileType) 
					throws FileNotFoundException, IOException {
		super(file.getPath(), firstRowDataIndex, firstColumnDataIndex, dataFileType);
		//wb = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(file)));
		//this.setCurrentSheet(wb.getSheetAt(sheetNumber));
	}

	protected XlsInputHandler(InputStream surveyIS, int sheetNumber, String filename, 
			int firstRowDataIndex, int firstColumnDataIndex, GSSurveyType dataFileType) 
					throws IOException {
		super(filename, firstRowDataIndex, firstColumnDataIndex, dataFileType);
		//wb = new HSSFWorkbook(surveyIS);
		//this.setCurrentSheet(wb.getSheetAt(sheetNumber));
	}
}
