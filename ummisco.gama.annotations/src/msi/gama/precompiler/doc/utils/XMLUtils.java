/*********************************************************************************************
 * 
 *
 * 'XMLUtils.java', in plugin 'msi.gama.documentation', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.precompiler.doc.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
import org.xml.sax.SAXException;

public class XMLUtils {

	public static Document createDoc(final String xml) throws ParserConfigurationException, SAXException, IOException {
		// Creation of the DOM source
		final File fileXml = new File(xml);

		return createDoc(fileXml);
	}

	public static Document createDoc(final File XMLFile)
			throws ParserConfigurationException, SAXException, IOException {
		// Creation of the DOM source
		final DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = fabriqueD.newDocumentBuilder();
		final Document document = builder.parse(XMLFile);

		return document;
	}

	public static Document createDoc(final InputStream docFile)
			throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = fabriqueD.newDocumentBuilder();
		final Document document = builder.parse(docFile);
		return document;
	}

	public static void transformDocument(final Document doc, final String xsl, final String targetFile) {
		try {
			final Source source = new DOMSource(doc);

			// Creation of the output file
			final File file = new File(targetFile);
			final Result result = new StreamResult(file);

			// configuration of the transformer
			final TransformerFactory factoryT = TransformerFactory.newInstance();
			final StreamSource stylesource = new StreamSource(xsl);
			Transformer transformer;

			transformer = factoryT.newTransformer(stylesource);
			transformer.setOutputProperty(OutputKeys.METHOD, "text");

			// Transformation
			transformer.transform(source, result);
		} catch (final TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (final TransformerException e) {
			e.printStackTrace();
		}

	}

}
