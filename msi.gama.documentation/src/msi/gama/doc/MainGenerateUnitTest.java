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

import msi.gama.doc.transform.XmlToTestGAML;
import msi.gama.doc.util.Constants;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;

public class MainGenerateUnitTest {

	public static void main(String[] args) 
			throws Exception {
		System.out.println("GENERATION OF THE TESTS FROM JAVA CODE");
		System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
		System.out.print("Preparation of the folders................");
		PrepareEnv.prepareDocumentation(Constants.ONLINE);
		System.out.println("DONE");
		System.out.print("Merge all the docGAMA.xml files................");		
		UnifyDoc.unify();
		System.out.println("DONE");
		System.out.print("Transform the docGAMA.xml file into test files................");		
		XmlToTestGAML.createAllTests();
		System.out.println("DONE");		
	}

}
