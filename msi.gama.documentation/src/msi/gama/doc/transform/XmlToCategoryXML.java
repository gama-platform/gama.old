package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import msi.gama.precompiler.doc.utils.Constants;

public class XmlToCategoryXML {

	public static void createCategoryWiki() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");

		System.out.print("Creation of the wiki page for Operators.....");
		XmlTransform.transformXML(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2KEYWORDS_XML_FOLDER + File.separator + "docGama-KeywordsXML.xsl",
				Constants.PATH_TO_KEYWORDS_XML);
		System.out.println("Done");				
	}

}
