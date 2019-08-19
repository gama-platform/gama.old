package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import msi.gama.precompiler.doc.utils.XMLUtils;

public class XmlTransform {
	public static void transformXML(String xml, String xsl, String output) 
			throws ParserConfigurationException, SAXException, IOException {
		// Creation of the DOM source
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		fabriqueD.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);		
		
		DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = constructeur.parse(fileXml);	
		
		XMLUtils.transformDocument(document, xsl, output); 		
	}
}
