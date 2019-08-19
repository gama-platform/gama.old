package msi.gama.doc.util;

import msi.gama.doc.transform.XmlToCategoryXML;
import ummisco.gama.dev.utils.DEBUG;

public class GenerateCategoryXML {

	public static void generateCategories() {
		try {

			DEBUG.LOG("GENERATION OF THE XML CATEGORY FILES FROM XML docGAMA.xml");
			DEBUG.LOG("Please notice that the docGAMA.xml files should have been previously generated..");
			DEBUG.LOG("Preparation of the folders................");
			PrepareEnv.prepareDocumentation();
			DEBUG.LOG("DONE");

			DEBUG.LOG("Merge all the docGAMA.xml filess................");
			UnifyDoc.unifyAllProjects(false);
			DEBUG.LOG("DONE");

			DEBUG.LOG("Transform the docGAMA.xml file into the XML category file ................");
			XmlToCategoryXML.createCategoryWiki();
			DEBUG.LOG("DONE");
		} catch (Exception ex) {
			DEBUG.ERR("Error in generate categories.",ex);
		}
	}
}
