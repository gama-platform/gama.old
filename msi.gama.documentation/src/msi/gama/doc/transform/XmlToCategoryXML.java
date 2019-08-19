package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import msi.gama.precompiler.doc.utils.Constants;
import ummisco.gama.dev.utils.DEBUG;

public class XmlToCategoryXML {

	public static void createCategoryWiki() throws ParserConfigurationException, SAXException, IOException {
		DEBUG.LOG("Beginning of the transformation");

		DEBUG.LOG("Creation of the wiki page for Operators.....");
		XmlTransform.transformXML(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2KEYWORDS_XML_FOLDER + File.separator + "docGama-KeywordsXML.xsl",
				Constants.PATH_TO_KEYWORDS_XML);
		DEBUG.LOG("Done");				
	}

}
