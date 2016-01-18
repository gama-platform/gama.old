package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import msi.gama.doc.util.Constants;
import msi.gama.doc.util.XMLUtils;
import msi.gama.precompiler.doc.utils.XMLElements;

public class XmlToCategoryXML {

	public static void createCategoryWiki() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");

		System.out.print("Creation of the wiki page for Operators.....");
		createKeywordXML(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2KEYWORDS_XML_FOLDER + File.separator + "docGama-KeywordsXML.xsl",
				Constants.XML_KEYWORD_GEN_FOLDER + File.separator + "keywords.xml");
		System.out.println("Done");				
	}

	private static void createKeywordXML(String xml, String xsl, String wiki) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// Creation of the DOM source
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = constructeur.parse(fileXml);	
		
		XMLUtils.transformDocument(document, xsl, wiki); 		
	}

}
