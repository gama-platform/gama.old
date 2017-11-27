/*********************************************************************************************
 * 
 *
 * 'XmlToTestGAML.java', in plugin 'msi.gama.documentation', is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc.transform;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import msi.gama.doc.util.PrepareEnv;
import msi.gama.precompiler.doc.utils.Constants;
import msi.gama.precompiler.doc.utils.XMLElements;
import msi.gama.precompiler.doc.utils.XMLUtils;

public class XmlToTestGAML {

	public static final String ATT_NAME_FILE = "fileName";

	public static void createEachTest(final File docFile)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		final File pluginFolder = docFile.getParentFile().getParentFile();

		Document document = XMLUtils.createDoc(docFile.getAbsolutePath());
		document = cleanDocumentTest(document);

		System.out.println("Beginning of the transformation for: " + docFile.getAbsolutePath());
		PrepareEnv.prepareUnitTestGenerator(pluginFolder);

		//////////////////////////////////////////////////////////////////////////////////
		System.out.print("Creation of the test models for Operators.....");
		final File dirOperators = new File(pluginFolder + File.separator + Constants.TEST_PLUGIN_GEN_MODELS
				+ File.separator + Constants.TEST_OPERATORS_FOLDER);
		//
		// File dirOperators = new File(Constants.TEST_FOLDER + File.separator + Constants.TEST_OPERATORS_FOLDER);
		dirOperators.mkdir();

		createOperatorsTests(document,
				Constants.XSL_XML2TEST_FOLDER + File.separator + "testGaml-Operators-xml2test.xsl",
				dirOperators.getCanonicalPath());
		System.out.println("Done");

	}

	public static void createAllTests()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		Document document = XMLUtils.createDoc(Constants.DOCGAMA_GLOBAL_FILE);
		document = cleanDocumentTest(document);

		System.out.println("Beginning of the transformation");

		//////////////////////////////////////////////////////////////////////////////////
		System.out.print("Creation of the test models for Operators.....");
		final File dirOperators = new File(Constants.TEST_FOLDER + File.separator + Constants.TEST_OPERATORS_FOLDER);
		dirOperators.mkdir();

		createOperatorsTests(document,
				Constants.XSL_XML2TEST_FOLDER + File.separator + "testGaml-Operators-xml2test.xsl",
				dirOperators.getCanonicalPath());
		System.out.println("Done");

		//////////////////////////////////////////////////////////////////////////////////
		System.out.print("Creation of the test models for Statements.....");
		final File dirStatements = new File(Constants.TEST_FOLDER + File.separator + Constants.TEST_STATEMENTS_FOLDER);
		dirStatements.mkdir();

		createMasterTest(document, Constants.XSL_XML2TEST_FOLDER + File.separator + "testGaml-Statements-xml2test.xsl",
				dirStatements.getCanonicalPath(), "StatementsTest.gaml");
		System.out.println("Done");

		//////////////////////////////////////////////////////////////////////////////////
		System.out.print("Creation of the master test model.....");
		createMasterTest(document, Constants.XSL_XML2TEST_FOLDER + File.separator + "testGaml-Master-xml2test.xsl",
				Constants.TEST_FOLDER, "masterTest.gaml");
		System.out.println("Done");
		//
		System.out.println("End of the transformation");
	}

	private static void createMasterTest(final Document document, final String xsl, final String targetFolder,
			final String targetFile) throws ParserConfigurationException, SAXException, IOException {
		XMLUtils.transformDocument(document, xsl, targetFolder + File.separator + targetFile);
	}

	private static void createOperatorsTests(final Document document, final String xsl, final String targetFolder)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		final DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = fabriqueD.newDocumentBuilder();

		final NodeList nLCategoriesOp = document.getElementsByTagName(XMLElements.OPERATORS_CATEGORIES);
		final NodeList nLCategories =
				((org.w3c.dom.Element) nLCategoriesOp.item(0)).getElementsByTagName(XMLElements.CATEGORY);

		final NodeList nLOperators = document.getElementsByTagName(XMLElements.OPERATOR);

		for (int i = 0; i < nLCategories.getLength(); i++) {
			final org.w3c.dom.Element eltCategory = (org.w3c.dom.Element) nLCategories.item(i);
			final String nameFileSpecies = "Op" + eltCategory.getAttribute("id") + "Test";
			// System.out.println(nameFileSpecies);

			final Document docTemp = builder.newDocument();
			final org.w3c.dom.Element root = docTemp.createElement(XMLElements.DOC);
			root.setAttribute(ATT_NAME_FILE, nameFileSpecies);
			final org.w3c.dom.Element rootOperators = docTemp.createElement(XMLElements.OPERATORS);

			for (int j = 0; j < nLOperators.getLength(); j++) {
				final org.w3c.dom.Element eltOperator = (org.w3c.dom.Element) nLOperators.item(j);
				final NodeList nLOperatorCategories = eltOperator.getElementsByTagName(XMLElements.CATEGORY);

				int k = 0;
				boolean categoryFound = false;
				while (k < nLOperatorCategories.getLength() && !categoryFound) {
					if (eltCategory.getAttribute(XMLElements.ATT_CAT_ID)
							.equals(((org.w3c.dom.Element) nLOperatorCategories.item(k)).getAttribute("id"))) {
						final Node importedOpElt = docTemp.importNode(eltOperator.cloneNode(true), true);
						rootOperators.appendChild(importedOpElt);
						categoryFound = true;
					}
					k++;
				}

				// if(eltCategory.getAttribute(XMLElements.ATT_CAT_ID).equals(eltOperator.getAttribute(XMLElements.CATEGORY))){
				// Node importedOpElt = docTemp.importNode(eltOperator.cloneNode(true), true);
				// rootOperators.appendChild(importedOpElt);
				// }
			}
			root.appendChild(rootOperators);
			docTemp.appendChild(root);

			XMLUtils.transformDocument(docTemp, xsl, targetFolder + File.separator + nameFileSpecies + ".experiment");
		}
	}

	// Cleaning means:
	// - Category: remove space and minus characters in the category name to be able to use it in the model
	// - Operators: replace special characters like +, -, *, /
	// - Operators and statements: addition of an index to have different variables
	public static Document cleanDocumentTest(final Document doc) {
		final NameOperatorConverter nameConverter = new NameOperatorConverter();
		final NodeList nLCategories = doc.getElementsByTagName(XMLElements.CATEGORY);
		final NodeList nLOperators = doc.getElementsByTagName(XMLElements.OPERATOR);
		final NodeList nLStatements = doc.getElementsByTagName(XMLElements.STATEMENT);

		for (int i = 0; i < nLCategories.getLength(); i++) {
			final org.w3c.dom.Element eltCategory = (org.w3c.dom.Element) nLCategories.item(i);
			eltCategory.setAttribute(XMLElements.ATT_CAT_ID,
					eltCategory.getAttribute(XMLElements.ATT_CAT_ID).replaceAll(" ", "__").replaceAll("-", "_"));
		}

		for (int j = 0; j < nLOperators.getLength(); j++) {
			final org.w3c.dom.Element eltOperator = (org.w3c.dom.Element) nLOperators.item(j);
			// eltOperator.setAttribute("category",eltOperator.getAttribute("category").replaceAll(" ",
			// "__").replaceAll("-", "_"));
			eltOperator.setAttribute(XMLElements.ATT_OP_ID,
					nameConverter.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_ID)));
			eltOperator.setAttribute(XMLElements.ATT_OP_NAME,
					nameConverter.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_NAME)));
			eltOperator.setAttribute(XMLElements.ATT_OP_ALT_NAME,
					nameConverter.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_ALT_NAME)));

			final NodeList nLExamples = eltOperator.getElementsByTagName(XMLElements.EXAMPLE);
			for (int k = 0; k < nLExamples.getLength(); k++) {
				final org.w3c.dom.Element eltExample = (org.w3c.dom.Element) nLExamples.item(k);
				eltExample.setAttribute(XMLElements.ATT_EXAMPLE_INDEX, "" + k);
			}
		}

		for (int j = 0; j < nLStatements.getLength(); j++) {
			final org.w3c.dom.Element eltStatement = (org.w3c.dom.Element) nLStatements.item(j);

			final NodeList nLExamples = eltStatement.getElementsByTagName(XMLElements.EXAMPLE);
			for (int k = 0; k < nLExamples.getLength(); k++) {
				final org.w3c.dom.Element eltExample = (org.w3c.dom.Element) nLExamples.item(k);
				eltExample.setAttribute(XMLElements.ATT_EXAMPLE_INDEX, "" + k);
			}
		}

		return doc;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		createAllTests();
	}

	static class NameOperatorConverter {
		HashMap<String, String> properNameOperatorMap;

		public NameOperatorConverter() {
			properNameOperatorMap = initProperNameOperatorMap();
		}

		private HashMap<String, String> initProperNameOperatorMap() {
			final HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("*", "Multiply");
			hm.put("-", "Minus");
			hm.put("/", "Divide");
			hm.put("+", "Plus");
			hm.put("^", "Power");
			hm.put("!=", "Different");
			hm.put("<>", "Different2");
			hm.put("<", "LT");
			hm.put("<=", "LE");
			hm.put(">", "GT");
			hm.put(">=", "GE");
			hm.put("=", "Equals");
			hm.put(":", "ELSEoperator");
			hm.put("!", "NOunary");
			hm.put("?", "IFoperator");
			hm.put("::", "DoublePoint");
			hm.put("@", "Arobase");
			hm.put(".", "PointAcces");
			return hm;
		}

		public String getProperOperatorName(final String opName) {
			if (properNameOperatorMap.containsKey(opName)) {
				return properNameOperatorMap.get(opName);
			} else {
				return opName;
			}
		}
	}
}
