package msi.gama.doc.transform;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import msi.gama.doc.Constants;
import msi.gama.doc.util.DocTransformer;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlToWiki {

	public static String suffix = "161"; // "Dev"
	
	public static void createAllWikis() 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");
		//
		System.out.print("Creation of the wiki page for Operators from A to K.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsAK-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "OperatorsAK"+suffix+".wiki");
		System.out.println("Done");	
		//
		System.out.print("Creation of the wiki page for Operators from L to Z.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsLZ-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "OperatorsLZ"+suffix+".wiki");
		System.out.println("Done");	
		//
		System.out.print("Creation of the wiki page for Operators.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Operators-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Operators"+suffix+".wiki");
		System.out.println("Done");			
		//
		System.out.print("Creation of the wiki page for Statements.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Statements-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Statements"+suffix+".wiki");	
		System.out.println("Done");		
		//
		System.out.print("Creation of the wiki page for Skills.......");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Skills-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Skills"+suffix+".wiki");	
		System.out.println("Done");			
		//
		System.out.print("Creation of the wiki page for Built-in Species.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Species-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Species"+suffix+".wiki");	
		System.out.println("Done");	
		//
		System.out.print("Creation of the wiki page for Constants and units.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Constants-xml2wiki.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Constants"+suffix+".wiki");	
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
		
		// We add index to example to distinguish variables in the doc 
		NodeList nLOperators = document.getElementsByTagName(XMLElements.OPERATOR);
		for(int j = 0; j < nLOperators.getLength(); j++){
			org.w3c.dom.Element eltOperator = (org.w3c.dom.Element) nLOperators.item(j);
			NodeList nLExamples = eltOperator.getElementsByTagName(XMLElements.EXAMPLE);
			for(int k = 0; k < nLExamples.getLength(); k++){
				org.w3c.dom.Element eltExample = (org.w3c.dom.Element) nLExamples.item(k);
				eltExample.setAttribute(XMLElements.ATT_EXAMPLE_INDEX, ""+k);
			}
		}		
		
		DocTransformer.transformDocument(document, xsl, wiki); 		
		
//		Source source = new DOMSource(document);
//		
//		// Creation of the output file
//		File fileWiki = new File(wiki);
//		Result resultat = new StreamResult(fileWiki);
//		
//		// configuration of the transformer
//		TransformerFactory fabriqueT = TransformerFactory.newInstance();
//		StreamSource stylesource = new StreamSource(xsl);
//		Transformer transformer = fabriqueT.newTransformer(stylesource);
//		transformer.setOutputProperty(OutputKeys.METHOD, "text");
//		
//		// Transformation
//		transformer.transform(source, resultat);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		createAllWikis();
	}
	
}
