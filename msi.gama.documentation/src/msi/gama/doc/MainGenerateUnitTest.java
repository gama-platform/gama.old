/*********************************************************************************************
 * 
 *
 * 'MainGenerateUnitTest.java', in plugin 'msi.gama.documentation', is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import java.io.File;
import java.util.HashMap;

import msi.gama.doc.util.WorkspaceManager;

public class MainGenerateUnitTest {

	public static void main(final String[] args) throws Exception {
		System.out.println("GENERATION OF THE TESTS FROM JAVA CODE");
		System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
		System.out.print("Transform each docGAMA.xml file into test files................");

		try {

			final WorkspaceManager ws = new WorkspaceManager(".");
			final HashMap<String, File> hmFiles = ws.getAllDocFilesLocal();

			for (final File docFile : hmFiles.values()) {
				// XmlToTestGAML.createEachTest(docFile);
			}

			System.out.println("" + hmFiles);

		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("DONE");
	}

}
