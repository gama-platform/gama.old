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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import msi.gama.doc.util.Constants;
import msi.gama.doc.util.DocTransformer;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlToWiki {

	public static String suffix = ""; // "Dev"
	
	public static void createAllWikis() 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");

		System.out.print("Creation of the wiki page for Operators.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Operators-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "G__Operators"+suffix+".md");
		System.out.println("Done");			
		//
		System.out.print("Creation of the wiki page for Statements.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Statements-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "G__Statements"+suffix+".md");	
		System.out.println("Done");		
		//
		System.out.print("Creation of the wiki page for Skills.......");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Skills-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "G__BuiltInSkills"+suffix+".md");	
		System.out.println("Done");		
		//
		System.out.print("Creation of the wiki page for the Index.......");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Index-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "G__Index"+suffix+".md");	
		System.out.println("Done");	
		
		
		// To uncomment when we want to generate Architectures
		// System.out.print("Creation of the wiki page for Architectures.......");		
		// createWiki(Constants.DOCGAMA_GLOBAL_FILE,
		//		Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Architectures-xml2wiki.xsl",
		//		Constants.XML2WIKI_FOLDER + File.separator + "G__BuiltInArchitectures"+suffix+".wiki");	
		// System.out.println("Done");		
		//
		System.out.print("Creation of the wiki page for Built-in Species.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Species-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "G__BuiltInSpecies"+suffix+".md");	
		System.out.println("Done");	
		//
		System.out.print("Creation of the wiki page for Constants and units.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Constants-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "G__UnitsAndConstants"+suffix+".md");	
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
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		createAllWikis();
	}
	
}
