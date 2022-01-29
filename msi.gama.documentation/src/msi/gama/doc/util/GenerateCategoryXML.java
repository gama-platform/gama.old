/*******************************************************************************************************
 *
 * GenerateCategoryXML.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc.util;

import msi.gama.doc.transform.XmlToCategoryXML;

/**
 * The Class GenerateCategoryXML.
 */
public class GenerateCategoryXML {

	/**
	 * Generate keywords XML.
	 */
	public static void GenerateKeywordsXML() {
		try {

			System.out.println("GENERATION OF THE XML CATEGORY FILES FROM XML docGAMA.xml");
			System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
			System.out.print("Preparation of the folders................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");

			System.out.print("Merge all the docGAMA.xml filess................");
			UnifyDoc.unifyAllProjects(false);
			System.out.println("DONE");

			System.out.print("Transform the docGAMA.xml file into the XML category file ................");
			XmlToCategoryXML.createCategoryWiki();
			System.out.println("DONE");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
