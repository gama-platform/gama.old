/*********************************************************************************************
 * 
 *
 * 'MainGenerateUnitTest.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import java.io.File;
import java.util.HashMap;

import msi.gama.doc.transform.XmlToTestGAML;
import msi.gama.doc.util.WorkspaceManager;

public class MainGenerateUnitTest_vNew {

	public static void main(String[] args) 
			throws Exception {
		System.out.println("GENERATION OF THE TESTS FROM JAVA CODE");
		System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
		// System.out.print("Preparation of the folders................");
		// Tests will be generated in each plugins
		// PrepareEnv.prepareDocumentation(Constants.ONLINE);
		// System.out.println("DONE");
		// System.out.print("Merge all the docGAMA.xml files................");		
		// UnifyDoc.unify();
		// System.out.println("DONE");
		System.out.print("Transform each docGAMA.xml file into test files................");		
		
		try {

			WorkspaceManager ws = new WorkspaceManager(".");
			HashMap<String, File> hmFiles = ws.getAllDocFilesLocal();

			for(File docFile : hmFiles.values()) {
				XmlToTestGAML.createEachTest(docFile);
			}
			
			System.out.println("" + hmFiles);

		} catch (Exception ex) {
			ex.printStackTrace();
		}				
		
		System.out.println("DONE");		
	}

}
