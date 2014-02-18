package msi.gama.doc.util;

import java.io.File;

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

public class DocTransformer {
	public static void transformDocument(Document doc, String xsl, String targetFile){
		try {
			Source source = new DOMSource(doc);
			
			// Creation of the output file
			File file = new File(targetFile);
			Result result = new StreamResult(file);
			
			// configuration of the transformer
			TransformerFactory factoryT = TransformerFactory.newInstance();
			StreamSource stylesource = new StreamSource(xsl);
			Transformer transformer;

			transformer = factoryT.newTransformer(stylesource);
			transformer.setOutputProperty(OutputKeys.METHOD, "text");
			
			// Transformation
			transformer.transform(source, result);					
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	
	}
}
