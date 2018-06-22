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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
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
import org.w3c.dom.NodeList;

import msi.gama.precompiler.Constants;
import msi.gama.precompiler.GamaProcessor;
import msi.gama.precompiler.ProcessorContext;
import msi.gama.precompiler.doc.utils.XMLElements;

public class ExamplesToTests {

	public static final String ATT_NAME_FILE = "fileName";
	static final TransformerFactory factoryT = TransformerFactory.newInstance();
	static final URL OPERATORS_XSL = GamaProcessor.class.getClassLoader()
			.getResource("msi/gama/precompiler/resources/testGaml-Operators-xml2test.xsl");
	static final Transformer OPERATORS_TRANSFORMER;

	static {
		Transformer transformer = null;
		try (final InputStream xsl = OPERATORS_XSL.openStream();) {
			final StreamSource stylesource = new StreamSource(xsl);
			transformer = factoryT.newTransformer(stylesource);
		} catch (final TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (final IOException e2) {
			e2.printStackTrace();
		}
		if (transformer != null) {
			transformer.setOutputProperty(OutputKeys.METHOD, "text");
		}
		OPERATORS_TRANSFORMER = transformer;
	}

	public static void createTests(final ProcessorContext context, final Document doc) {
		if (doc == null || !doc.hasChildNodes()) { return; }
		final Document document = cleanDocumentTest(doc);
		createOperatorsTests(context, document, OPERATORS_TRANSFORMER);
	}

	private static List<org.w3c.dom.Element> list(final NodeList nl) {
		if (nl.getLength() == 0) { return Collections.EMPTY_LIST; }
		final List<org.w3c.dom.Element> result = new ArrayList<>();
		for (int i = 0; i < nl.getLength(); i++) {
			result.add((org.w3c.dom.Element) nl.item(i));
		}
		return result;
	}

	private static void createOperatorsTests(final ProcessorContext context, final Document document,
			final Transformer xsl) {

		long loopTime = 0l;
		long xlstTime = 0l;
		final NodeList operatorsCategories = document.getElementsByTagName(XMLElements.OPERATORS_CATEGORIES);
		final List<org.w3c.dom.Element> categories =
				list(((org.w3c.dom.Element) operatorsCategories.item(0)).getElementsByTagName(XMLElements.CATEGORY));
		final List<org.w3c.dom.Element> operators = list(document.getElementsByTagName(XMLElements.OPERATOR));

		for (final org.w3c.dom.Element categoryElement : categories) {
			final long loopBegin = System.currentTimeMillis();
			final String category = categoryElement.getAttribute(XMLElements.ATT_CAT_ID);
			final String nameFileSpecies = categoryElement.getAttribute("id");
			final Document tempDocument = context.getBuilder().newDocument();
			final org.w3c.dom.Element root = tempDocument.createElement(XMLElements.DOC);
			root.setAttribute(ATT_NAME_FILE, nameFileSpecies);
			final org.w3c.dom.Element rootOperators = tempDocument.createElement(XMLElements.OPERATORS);

			for (final org.w3c.dom.Element operatorElement : operators) {
				if (operatorElement.hasAttribute("HAS_TESTS")) {
					list(operatorElement.getElementsByTagName(XMLElements.CATEGORY)).stream()
							.filter(o -> o.getAttribute("id").equals(category))
							.map(o -> tempDocument.importNode(o.cloneNode(true), true))
							.forEach(o -> rootOperators.appendChild(o));
				}
			}
			root.appendChild(rootOperators);
			tempDocument.appendChild(root);
			loopTime += System.currentTimeMillis() - loopBegin;

			final long xsltBegin = System.currentTimeMillis();
			transformDocument(context, tempDocument, xsl, nameFileSpecies + ".experiment");
			xlstTime += System.currentTimeMillis() - xsltBegin;

		}
		context.emit(Kind.NOTE, "    GAML Tests: example tests main loop took " + loopTime + "ms", (Element) null);
		context.emit(Kind.NOTE, "    GAML Tests: example tests xslt transformation took " + xlstTime + "ms",
				(Element) null);

	}

	public static void transformDocument(final ProcessorContext context, final Document doc,
			final Transformer transformer, final String targetFile) {

		try (final Writer writer = context.createTestWriter(targetFile);) {
			// If no writer can be created, just abort
			if (writer == null) { return; }
			final Source source = new DOMSource(doc);
			final Result result = new StreamResult(writer);
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

	final static NameOperatorConverter NAME_CONVERTER = new NameOperatorConverter();

	// Cleaning means:
	// - Category: remove space and minus characters in the category name to be able to use it in the model
	// - Operators: replace special characters like +, -, *, /
	// - Operators and statements: addition of an index to have different variables
	public static Document cleanDocumentTest(final Document doc) {

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
					NAME_CONVERTER.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_ID)));
			eltOperator.setAttribute(XMLElements.ATT_OP_NAME,
					NAME_CONVERTER.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_NAME)));
			eltOperator.setAttribute(XMLElements.ATT_OP_ALT_NAME,
					NAME_CONVERTER.getProperOperatorName(eltOperator.getAttribute(XMLElements.ATT_OP_ALT_NAME)));

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
