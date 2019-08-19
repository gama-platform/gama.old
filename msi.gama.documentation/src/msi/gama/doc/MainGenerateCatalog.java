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
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;
import ummisco.gama.dev.utils.DEBUG;

public class MainGenerateCatalog {

	public static void main(final String[] args) {
		try {
			DEBUG.LOG("GENERATION OF THE CATALOG FROM JAVA CODE");
			DEBUG.LOG("Please notice that the docGAMA.xml files should have been previously generated..");
			DEBUG.LOG("Preparation of the folders................");
			PrepareEnv.prepareDocumentation();
			DEBUG.LOG("DONE");

			DEBUG.LOG("Merge all the docGAMA.xml files................");
			UnifyDoc.unifyAllProjects(true);
			
			DEBUG.LOG("DONE"); 

			DEBUG.LOG(
					"Transform the docGAMA.xml file into a catalog file containg keyword, their type, the plugin................");
			XmlToCatalog.createCatalog();
			
			DEBUG.LOG("DONE");
		} catch (Exception e) {
			DEBUG.ERR("Error in the catalog generation.", e);
		}
	}

}
