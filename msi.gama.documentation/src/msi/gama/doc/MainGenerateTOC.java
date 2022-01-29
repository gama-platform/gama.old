/*******************************************************************************************************
 *
 * MainGenerateTOC.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc;

import java.io.IOException;

import msi.gama.doc.util.HomeToTOC;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.precompiler.doc.utils.Constants;

/**
 * The Class MainGenerateTOC.
 */
public class MainGenerateTOC {
	
	/**
	 * The main method.
	 *
	 * @param argc the arguments
	 */
	public static void  main(String[] argc) {
		try {		
			final String tocMDFile = Constants.TOC_SIDEBAR_FILE;
			
			System.out.println("TRANSLATE THE MARKDOWN SIDEBAR INTO A TOC FILE");
	
			// Prepare the folders
			System.out.print("Preparation of the folders................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");

			// Generate the tocNUM_VERSION.xml file
			System.out.print("Generation of the toc file................");			
			HomeToTOC.md2toc(tocMDFile,Constants.TOC_FILE_PATH, Constants.WIKI_FOLDER);	
			System.out.println("GENERATION OF THE TOC FILE DONE");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
