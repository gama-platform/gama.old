package msi.gama.doc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class PrepareEnv {
	
	public static void prepareDocumentation() throws IOException{
		// - Deletes every generated folders		
		// - Creates every folders when they do not exist

		File genFolder = new File(Constants.GEN_FOLDER);

		if(genFolder.exists()) {FileUtils.deleteDirectory(genFolder);}

		genFolder.mkdir();	
		new File(Constants.JAVA2XML_FOLDER).mkdirs();	
		new File(Constants.XML2WIKI_FOLDER).mkdirs();
		new File(Constants.WIKI2WIKI_FOLDER).mkdirs();
		new File(Constants.HTML2XML_FOLDER).mkdirs();
		new File(Constants.PDF_FOLDER).mkdirs();
//		createCleanFolder(Constants.XML2WIKI_FOLDER);
//		createCleanFolder(Constants.WIKI2WIKI_FOLDER);
		
		copyPythonTemplate();
	}
	
//	private static void createCleanFolder(String fileName){
//		File folder = new File(fileName);
//		if(!folder.exists()) {
//			folder.mkdirs();
//		}
//	
//		for(File f : folder.listFiles()) {
//			f.delete();
//		}		
//	}
	
	private static void copyPythonTemplate() throws IOException{
		File pythonTemplate = new File(Constants.PYTHON_TEMPLATE_FOLDER);
		FileUtils.copyDirectory(pythonTemplate, new File(Constants.WIKI2WIKI_FOLDER));
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		prepareDocumentation();
	}	
	
}
