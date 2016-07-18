/*********************************************************************************************
 * 
 *
 * 'PrepareEnv.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class PrepareEnv {
	
	public static void prepareDocumentation(boolean online) throws IOException{
		// - Deletes every generated folders		
		// - Creates every folders when they do not exist

		File genFolder = new File(Constants.GEN_FOLDER);
		File testFolder = new File(Constants.TEST_FOLDER);
		
		if(genFolder.exists()) {deleteDirectory(genFolder);}
		if(testFolder.exists()) {deleteDirectory(testFolder);}

		genFolder.mkdir();	
		new File(Constants.JAVA2XML_FOLDER).mkdirs();	
		new File(Constants.XML2WIKI_FOLDER).mkdirs();
//		new File(Constants.WIKI2WIKI_FOLDER).mkdirs();
//		new File(Constants.HTML2XML_FOLDER).mkdirs();
		new File(Constants.PDF_FOLDER).mkdirs();
		new File(Constants.TEST_FOLDER).mkdirs();
		new File(Constants.TOC_GEN_FOLDER).mkdir();
		new File(Constants.XML_KEYWORD_GEN_FOLDER).mkdirs();

//		copyPythonTemplate();
	}
	
	static public boolean deleteDirectory(File path) {
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
	                deleteDirectory(files[i]);
	            } else {
	                files[i].delete();
	            }
	        }
	    }
	    return (path.delete());
	}

//	private static void copyPythonTemplate() throws IOException{
//		File pythonTemplate = new File(Constants.PYTHON_TEMPLATE_FOLDER);
//		FileUtils.copyDirectory(pythonTemplate, new File(Constants.WIKI2WIKI_FOLDER));
//	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		prepareDocumentation(Constants.ONLINE);
	}	
}
