package msi.gama.doc.util;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import msi.gama.doc.Constants;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class XmlToWiki {

	public static void createAllWikis() 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");
		//
		System.out.print("Creation of the wiki page for Operators.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Operators-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "OperatorsDev.wiki");
		System.out.println("Done");	
		//
		System.out.print("Creation of the wiki page for Statements.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Statements-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "StatementsDev.wiki");	
		System.out.println("Done");		
		//
		System.out.print("Creation of the wiki page for Skills.......");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Skills-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "SkillsDev.wiki");	
		System.out.println("Done");			
		//
		System.out.print("Creation of the wiki page for Built-in Species.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Species-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "SpeciesDev.wiki");	
		System.out.println("Done");	
		//
		System.out.println("End of the transformation");		
	}

	private static void createWiki(String xml, String xsl, String wiki) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// Creation of the DOM source
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = constructeur.parse(fileXml);
		Source source = new DOMSource(document);
		
		// Creation of the output file
		File fileWiki = new File(wiki);
		Result resultat = new StreamResult(fileWiki);
		
		// configuration of the transformer
		TransformerFactory fabriqueT = TransformerFactory.newInstance();
		StreamSource stylesource = new StreamSource(xsl);
		Transformer transformer = fabriqueT.newTransformer(stylesource);
		transformer.setOutputProperty(OutputKeys.METHOD, "text");
		
		// Transformation
		transformer.transform(source, resultat);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		createAllWikis();
	}
	
}
