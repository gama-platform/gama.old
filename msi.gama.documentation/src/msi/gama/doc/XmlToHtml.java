package msi.gama.doc;
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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class XmlToHtml {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Debut Tranformation");
		createHTML("files/doc.xml","xsl/docGama-xml2html.xsl","gen/html/operators.html");
		System.out.println("Fin Transformation");
	}

	public static void createHTML(String xml, String xsl, String html) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// Creation of the DOM source
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = constructeur.parse(fileXml);
		Source source = new DOMSource(document);
		
		// Creation of the output file
		File fileHtml = new File(html);
		Result resultat = new StreamResult(fileHtml);
		
		// configuration of the transformer
		TransformerFactory fabriqueT = TransformerFactory.newInstance();
		StreamSource stylesource = new StreamSource(xsl);
		Transformer transformer = fabriqueT.newTransformer(stylesource);
		transformer.setOutputProperty(OutputKeys.METHOD, "html");
		
		// Transformation
		transformer.transform(source, resultat);
	}

}
