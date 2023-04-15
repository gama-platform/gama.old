/*******************************************************************************************************
 *
 * MainGenerateCatalog.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc;

import msi.gama.doc.transform.XmlToCatalog;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;

/**
 * The Class MainGenerateCatalog.
 */
public class MainGenerateCatalog {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		try {
			System.out.println("GENERATION OF THE CATALOG FROM JAVA CODE");
			System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
			System.out.print("Preparation of the folders................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");

			System.out.print("Merge all the docGAMA.xml files................");
			UnifyDoc.unifyAllProjects(true);
			//UnifyDoc.unify(true);
			
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
