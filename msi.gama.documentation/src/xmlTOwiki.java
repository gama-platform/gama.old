import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;


public class xmlTOwiki {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Debut Tranformation");
		createWiki("files/doc.xml","xsl/docGama-xml2wiki.xsl","gen/wiki/operators.wiki");		
		System.out.println("Fin Transformation");
	}


public static void createWiki(String xml, String xsl, String wiki) throws Exception{
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
	
}
