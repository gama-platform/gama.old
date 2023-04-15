/*******************************************************************************************************
 *
 * XmlToCategoryXML.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import msi.gama.precompiler.doc.utils.Constants;

/**
 * The Class XmlToCategoryXML.
 */
public class XmlToCategoryXML {

	/**
	 * Creates the category wiki.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws TransformerException the transformer exception
	 */
	public static void createCategoryWiki() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");

		System.out.print("Creation of the wiki page for Operators.....");
		XmlTransform.transformXML(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2KEYWORDS_XML_FOLDER + File.separator + "docGama-KeywordsXML.xsl",
				Constants.PATH_TO_KEYWORDS_XML);
		System.out.println("Done");				
	}

}
