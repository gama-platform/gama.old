/*******************************************************************************************************
 *
 * XmlTransform.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import msi.gama.precompiler.doc.utils.XMLUtils;

/**
 * The Class XmlTransform.
 */
public class XmlTransform {
	
	/**
	 * Transform XML.
	 *
	 * @param xml the xml
	 * @param xsl the xsl
	 * @param output the output
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws TransformerException the transformer exception
	 */
	public static void transformXML(String xml, String xsl, String output) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// Creation of the DOM source
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = constructeur.parse(fileXml);	
		
		XMLUtils.transformDocument(document, xsl, output); 		
	}
}
