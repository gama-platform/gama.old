/*********************************************************************************************
 * 
 *
 * 'XMLUtils.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLUtils {
	
	public static Document createDoc(String xml) throws ParserConfigurationException, SAXException, IOException{
		// Creation of the DOM source
		File fileXml = new File(xml);
		
		return createDoc(fileXml);
	}
	
	public static Document createDoc(File XMLFile) throws ParserConfigurationException, SAXException, IOException{
		// Creation of the DOM source
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = fabriqueD.newDocumentBuilder();
		Document document = builder.parse(XMLFile);
		
		return document;
	}
}
