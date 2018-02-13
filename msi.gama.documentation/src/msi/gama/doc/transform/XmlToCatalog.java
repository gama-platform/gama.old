/*********************************************************************************************
 * 
 *
 * 'XmlToWiki.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import msi.gama.precompiler.doc.utils.Constants;


public class XmlToCatalog {
	public static void createCatalog() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");

		System.out.print("Creation of the catalog.....");
		XmlTransform.transformXML(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2CATALOG_FOLDER + File.separator + "docGama-Catalog-xml2catalog.xsl",
				Constants.CATALOG_GEN_FOLDER + File.separator + "catalog.csv");		
		System.out.println("Done");				
	}
}
