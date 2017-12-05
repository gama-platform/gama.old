/*********************************************************************************************
 * 
 *
 * 'MainGenerateWiki.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import msi.gama.doc.transform.XmlToCatalog;
import msi.gama.doc.util.GenerateCategoryXML;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;
import msi.gama.precompiler.doc.utils.Constants;

public class MainGenerateCatalog {

	public static void main(final String[] args) {
		try {
			// build the file keywords.xml
			GenerateCategoryXML.GenerateKeywordsXML();

			// generate the wiki documentation
			System.out.println("GENERATION OF THE WIKI DOCUMENTATION FROM JAVA CODE");
			System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
			System.out.print("Preparation of the folders................");
			PrepareEnv.prepareDocumentation(Constants.ONLINE);
			System.out.println("DONE");

			System.out.print("Merge all the docGAMA.xml files................");
			UnifyDoc.unify(true);
			System.out.println("DONE");

			System.out.print(
					"Transform the docGAMA.xml file into a catalog file containg keyword, their type, the plugin................");
			XmlToCatalog.createCatalog();
			
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
