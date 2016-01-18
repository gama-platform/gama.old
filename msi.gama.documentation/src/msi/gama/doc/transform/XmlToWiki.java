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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import msi.gama.doc.util.Constants;
import msi.gama.doc.util.WorkspaceManager;
import msi.gama.doc.util.XMLUtils;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlToWiki {

	public static String suffix = ""; // "Dev"
	public static String extFileName = "Extension";
	public static String extFolder = "PluginDocumentation/";
	
	public static void createAllWikis() 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("Beginning of the transformation");

		System.out.print("Creation of the wiki page for Operators.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Operators-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Operators"+suffix+".md");
		System.out.println("Done");			
		//
		System.out.print("Creation of the wiki page for Statements.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Statements-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Statements"+suffix+".md");	
		System.out.println("Done");		
		//
		System.out.print("Creation of the wiki page for Skills.......");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Skills-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "BuiltInSkills"+suffix+".md");	
		System.out.println("Done");		
		//
		System.out.print("Creation of the wiki page for the Index.......");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Index-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Index"+suffix+".md");	
		System.out.println("Done");	
		
		
		System.out.print("Creation of the wiki page for Architectures.......");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Architectures-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "BuiltInArchitectures"+suffix+".md");	
		System.out.println("Done");		
		
		System.out.print("Creation of the wiki page for Built-in Species.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Species-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "BuiltInSpecies"+suffix+".md");	
		System.out.println("Done");	
		//
		System.out.print("Creation of the wiki page for Constants and units.....");		
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Constants-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "UnitsAndConstants"+suffix+".md");	
		System.out.println("Done");	
		//
		System.out.println("End of the transformation");		
	}

	private static void createWiki(String xml, String xsl, String wiki) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		createWiki( xml,  xsl,  wiki, "");
	}

	
	private static void createWiki(String xml, String xsl, String wiki, String pluginName) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// Creation of the DOM source
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = constructeur.parse(fileXml);
		
		// We the pluginName in the doc root
		NodeList nLDoc = document.getElementsByTagName(XMLElements.DOC);
		org.w3c.dom.Element eltDoc = (org.w3c.dom.Element) nLDoc.item(0);
		eltDoc.setAttribute(XMLElements.ATT_DOC_PLUGINNAME, pluginName);
		
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
		
		XMLUtils.transformDocument(document, xsl, wiki); 		
	}
	
	public static void createExtentionsWiki() 
			throws IOException, ParserConfigurationException, SAXException, TransformerException{
		WorkspaceManager ws = new WorkspaceManager(".");
		HashMap<String, File> hmExt = ws.getExtensionsDocFiles();
		
		// Create the G__Extensions.md file as a toc to each G__extensions_plugin.md files
		String pathExtension = Constants.WIKI_FOLDER_EXT + File.separator + extFileName +suffix+".md";
		File ext = new File(pathExtension);
		FileWriter fw=new FileWriter(ext);
		BufferedWriter extBw= new BufferedWriter(fw);
		
		extBw.write("# Extensions");
		extBw.newLine();
		extBw.write("----");		
		extBw.newLine();
		extBw.write("**This file is automatically generated from java files. Do Not Edit It.**");
		extBw.newLine();
		extBw.newLine();		
		extBw.write("----");		
		extBw.newLine();	
		extBw.newLine();		
		extBw.write("## Introduction");
		extBw.newLine();
		extBw.write("This page provides a link to each of the extension pages. Extensions are the additional GAMA plugins that exist in the GAMA GitHub repository but will not be packaged with the release. **These extensions are not maintained by GAMA core team but by their authors.**");
		extBw.newLine();
		
		for(String pluginName : hmExt.keySet()){
			extBw.write("* ["+ pluginName +"]("+extFolder+extFileName+"_"+pluginName+")");	
			extBw.newLine();
		}
		extBw.close();
		
		// Create 1 G__extension_plugin.md file per plugin
		for(Entry<String,File> docPlug : hmExt.entrySet()){
			System.out.print("Creation of the wiki pages for extension: " + docPlug.getKey());		
			createWiki(docPlug.getValue().getAbsolutePath(),
					Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Extensions-xml2md.xsl",
					Constants.WIKI_FOLDER_EXT_PLUGIN + File.separator + extFileName + "_" + docPlug.getKey()+ suffix+".md",
					docPlug.getKey());	
			System.out.println("Done");				
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		createExtentionsWiki();
		// createAllWikis();
	}
	
}
