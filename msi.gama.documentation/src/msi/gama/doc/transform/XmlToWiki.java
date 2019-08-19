/*********************************************************************************************
 *
 *
 * 'XmlToWiki.java', in plugin 'msi.gama.documentation', is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
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
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import msi.gama.doc.util.WorkspaceManager;
import msi.gama.precompiler.doc.utils.Constants;
import msi.gama.precompiler.doc.utils.XMLElements;
import msi.gama.precompiler.doc.utils.XMLUtils;
import ummisco.gama.dev.utils.DEBUG;

public class XmlToWiki {

	public static final String SUFFIX = ""; // "Dev"
	public static final String EXT_FILE_NAME = "Extension";
	public static final String EXT_FOLDER = "PluginDocumentation/";

	public static void createAllWikis()
			throws ParserConfigurationException, SAXException, IOException {
		DEBUG.LOG("Beginning of the transformation");

		DEBUG.LOG("Creation of the wiki page for Operators.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Operators-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Operators" + SUFFIX + ".md");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsSplitted-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "OperatorsSplitted" + SUFFIX + ".md");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsAA-xml2md.xsl",
				Constants.WIKI_FOLDER_WIKI_ONLY + File.separator + "OperatorsAA" + SUFFIX + ".md");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsBC-xml2md.xsl",
				Constants.WIKI_FOLDER_WIKI_ONLY + File.separator + "OperatorsBC" + SUFFIX + ".md");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsDH-xml2md.xsl",
				Constants.WIKI_FOLDER_WIKI_ONLY + File.separator + "OperatorsDH" + SUFFIX + ".md");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsIM-xml2md.xsl",
				Constants.WIKI_FOLDER_WIKI_ONLY + File.separator + "OperatorsIM" + SUFFIX + ".md");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsNR-xml2md.xsl",
				Constants.WIKI_FOLDER_WIKI_ONLY + File.separator + "OperatorsNR" + SUFFIX + ".md");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-OperatorsSZ-xml2md.xsl",
				Constants.WIKI_FOLDER_WIKI_ONLY + File.separator + "OperatorsSZ" + SUFFIX + ".md");
		DEBUG.LOG("Done");
		//
		DEBUG.LOG("Creation of the wiki page for Statements.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Statements-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "Statements" + SUFFIX + ".md");
		DEBUG.LOG("Done");
		//
		DEBUG.LOG("Creation of the wiki page for Skills.......");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Skills-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "BuiltInSkills" + SUFFIX + ".md");
		DEBUG.LOG("Done");
		//
		DEBUG.LOG("Creation of the wiki page for the Index.......");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Index-xml2md.xsl",
				Constants.WIKI_FOLDER + File.separator + "Index" + SUFFIX + ".md");
		DEBUG.LOG("Done");

		// JSON
		DEBUG.LOG("Creation of the json file for the web search engine.......");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2JSON_FOLDER + File.separator + "docGama-Index-xml2json.xsl",
				Constants.WIKI_FOLDER_WIKI_ONLY + File.separator + "database.json");
		DEBUG.LOG("Done");

		DEBUG.LOG("Creation of the wiki page for Architectures.......");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Architectures-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "BuiltInArchitectures" + SUFFIX + ".md");
		DEBUG.LOG("Done");

		DEBUG.LOG("Creation of the wiki page for Built-in Species.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Species-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "BuiltInSpecies" + SUFFIX + ".md");
		DEBUG.LOG("Done");
		//
		DEBUG.LOG("Creation of the wiki page for Constants and units.....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Constants-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "UnitsAndConstants" + SUFFIX + ".md");
		DEBUG.LOG("Done");
		//
		DEBUG.LOG("Creation of the page for Constants and units (PDF format).....");
		createWiki(Constants.DOCGAMA_GLOBAL_FILE,
				Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-ConstantsPDF-xml2md.xsl",
				Constants.XML2WIKI_FOLDER + File.separator + "UnitsAndConstantsPDF" + SUFFIX + ".md");
		DEBUG.LOG("Done");
		//
		DEBUG.LOG("End of the transformation");
	}

	private static void createWiki(final String xml, final String xsl, final String wiki)
			throws ParserConfigurationException, SAXException, IOException {
		createWiki(xml, xsl, wiki, "");
	}

	private static void createWiki(final String xml, final String xsl, final String wiki, final String pluginName)
			throws ParserConfigurationException, SAXException, IOException {
		// Creation of the DOM source
		final DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		fabriqueD.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		
		final DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		final File fileXml = new File(xml);
		final Document document = constructeur.parse(fileXml);

		// We the pluginName in the doc root
		final NodeList nLDoc = document.getElementsByTagName(XMLElements.DOC);
		final org.w3c.dom.Element eltDoc = (org.w3c.dom.Element) nLDoc.item(0);
		eltDoc.setAttribute(XMLElements.ATT_DOC_PLUGINNAME, pluginName);

		// We add index to example to distinguish variables in the doc
		final NodeList nLOperators = document.getElementsByTagName(XMLElements.OPERATOR);
		for (int j = 0; j < nLOperators.getLength(); j++) {
			final org.w3c.dom.Element eltOperator = (org.w3c.dom.Element) nLOperators.item(j);
			final NodeList nLExamples = eltOperator.getElementsByTagName(XMLElements.EXAMPLE);
			for (int k = 0; k < nLExamples.getLength(); k++) {
				final org.w3c.dom.Element eltExample = (org.w3c.dom.Element) nLExamples.item(k);
				eltExample.setAttribute(XMLElements.ATT_EXAMPLE_INDEX, "" + k);
			}
		}

		XMLUtils.transformDocument(document, xsl, wiki);
	}

	public static void createExtentionsWiki()
			throws IOException, ParserConfigurationException, SAXException {
		final WorkspaceManager ws = new WorkspaceManager(".", false);
		final Map<String, File> hmExt = ws.getExtensionsDocFiles();

		// Create the G__Extensions.md file as a toc to each G__extensions_plugin.md files
		final String pathExtension = Constants.WIKI_FOLDER_EXT + File.separator + EXT_FILE_NAME + SUFFIX + ".md";
		final File ext = new File(pathExtension);
		try (FileWriter fw = new FileWriter(ext); BufferedWriter extBw = new BufferedWriter(fw);) {

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
			extBw.write(
					"This page provides a link to each of the extension pages. Extensions are the additional GAMA plugins that exist in the GAMA GitHub repository but will not be packaged with the release. **These extensions are not maintained by GAMA core team but by their authors.**");
			extBw.newLine();

			for (final String pluginName : hmExt.keySet()) {
				extBw.write("* [" + pluginName + "](" + EXT_FOLDER + EXT_FILE_NAME + "_" + pluginName + ")");
				extBw.newLine();
			}
		}

		// Create 1 G__extension_plugin.md file per plugin
		for (final Entry<String, File> docPlug : hmExt.entrySet()) {
			DEBUG.LOG("Creation of the wiki pages for extension: " + docPlug.getKey());
			createWiki(docPlug.getValue().getAbsolutePath(),
					Constants.XSL_XML2WIKI_FOLDER + File.separator + "docGama-Extensions-xml2md.xsl",
					Constants.WIKI_FOLDER_EXT_PLUGIN + File.separator + EXT_FILE_NAME + "_" + docPlug.getKey() + SUFFIX
							+ ".md",
					docPlug.getKey());
			DEBUG.LOG("Done");
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		createExtentionsWiki();
	}

}
