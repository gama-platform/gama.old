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
package msi.gama.precompiler.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.precompiler.Constants;
import msi.gama.precompiler.GamaProcessor;
import msi.gama.precompiler.ProcessorContext;
import msi.gama.precompiler.doc.utils.XMLElements;

public class ExamplesToTests {

	public static final String ATT_NAME_FILE = "fileName";

	public static void createTests(final ProcessorContext context, final Document doc) {
		// Document document;
		// try (InputStream docFile = context.getInputStream("docGAMA.xml")) {
		// document = XMLUtils.createDoc(docFile);
		// } catch (final ParserConfigurationException | SAXException | IOException e) {
		// context.emitError("Impossible to parse documentation: " + e.getMessage(), null);
		// return;
		// }
		if (doc == null || !doc.hasChildNodes()) { return; }
		final Document document = cleanDocumentTest(doc);
		createOperatorsTests(context, document, "testGaml-Operators-xml2test.xsl");
	}

	private static void createOperatorsTests(final ProcessorContext context, final Document document,
			final String xsl) {

		final NodeList nLCategoriesOp = document.getElementsByTagName(XMLElements.OPERATORS_CATEGORIES);
		final NodeList nLCategories =
				((org.w3c.dom.Element) nLCategoriesOp.item(0)).getElementsByTagName(XMLElements.CATEGORY);

		final NodeList nLOperators = document.getElementsByTagName(XMLElements.OPERATOR);

		for (int i = 0; i < nLCategories.getLength(); i++) {
			final org.w3c.dom.Element eltCategory = (org.w3c.dom.Element) nLCategories.item(i);
			// System.out.println("Processing category " + eltCategory.getAttribute("id"));
			final String nameFileSpecies = eltCategory.getAttribute("id");
			// System.out.println(nameFileSpecies);
			DocumentBuilder builder = null;
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (final ParserConfigurationException e1) {}
			final Document docTemp = builder == null ? null : builder.newDocument();
			if (docTemp == null) { return; }
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

			}
			root.appendChild(rootOperators);
			docTemp.appendChild(root);

			transformDocument(context, docTemp, xsl, nameFileSpecies + ".experiment");
		}
	}

	public static void transformDocument(final ProcessorContext context, final Document doc, final String xslFileName,
			final String targetFile) {

		try (final Writer writer = context.createTestWriter(targetFile);) {
			// If no writer can be created, just abort
			if (writer == null) { return; }
			final URL url =
					GamaProcessor.class.getClassLoader().getResource("msi/gama/precompiler/resources/" + xslFileName);
			if (url == null) {
				System.err.println("Impossible to read XML transformer");
				context.emitError("Impossible to read XML transformer");
				return;
			}
			final Source source = new DOMSource(doc);
			final Result result = new StreamResult(writer);
			final TransformerFactory factoryT = TransformerFactory.newInstance();

			Transformer transformer = null;
			try (final InputStream xsl = url.openStream();) {
				final StreamSource stylesource = new StreamSource(xsl);
				transformer = factoryT.newTransformer(stylesource);
			} catch (final TransformerConfigurationException e) {
				e.printStackTrace();
				context.emitError("Impossible to create XML transformer: ", e);
				return;
			} catch (final IOException e2) {
				e2.printStackTrace();
				context.emitError("Impossible to read XML transformer: ", e2);
				return;
			}
			transformer.setOutputProperty(OutputKeys.METHOD, "text");
			try {
				transformer.transform(source, result);
			} catch (final TransformerException e) {
				e.printStackTrace();
				context.emitError("Impossible to transform XML: ", e);
			}
		} catch (final IOException e1) {
			context.emitError("Impossible to open file for writing: ", e1);
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
			eltCategory.setAttribute(XMLElements.ATT_CAT_ID, Constants
					.capitalizeAllWords(eltCategory.getAttribute(XMLElements.ATT_CAT_ID).replaceAll("-", " ")));
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

	static class NameOperatorConverter {
		HashMap<String, String> properNameOperatorMap;

		public NameOperatorConverter() {
			properNameOperatorMap = initProperNameOperatorMap();
		}

		private HashMap<String, String> initProperNameOperatorMap() {
			final HashMap<String, String> hm = new HashMap<>();
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
