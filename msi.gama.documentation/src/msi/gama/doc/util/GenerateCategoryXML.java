package msi.gama.doc.util;

import msi.gama.doc.transform.XmlToCategoryXML;

public class GenerateCategoryXML {

	public static void GenerateKeywordsXML() {
		try {

			System.out.println("GENERATION OF THE XML CATEGORY FILES FROM XML docGAMA.xml");
			System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
			System.out.print("Preparation of the folders................");
			PrepareEnv.prepareDocumentation(Constants.ONLINE);
			System.out.println("DONE");

			System.out.print("Merge all the docGAMA.xml filess................");
			UnifyDoc.unifyAllProjects();
			System.out.println("DONE");

			System.out.print("Transform the docGAMA.xml file into the XML category file ................");
			XmlToCategoryXML.createCategoryWiki();
			System.out.println("DONE");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
