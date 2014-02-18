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
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = builder.parse(fileXml);
		
		return document;
	}
}
