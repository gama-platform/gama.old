package java2.testLibs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;

public class testFOP {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		creerPDFnew();
	}

	public static void creerPDFnew() throws Exception {

		// Step 1: Construct a FopFactory
		// (reuse if you plan to render multiple documents!)
		FopFactory fopFactory = FopFactory.newInstance();

		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
		OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("BuiltIn15.pdf")));

		try {
		  // Step 3: Construct fop with desired output format
		  Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

		  // Step 4: Setup JAXP using identity transformer
		  TransformerFactory factory = TransformerFactory.newInstance();
		  Transformer transformer = factory.newTransformer(); // identity transformer
		           
		  // Step 5: Setup input and output for XSLT transformation 
		  // Setup input stream
		  Source src = new StreamSource(new File("wikiGAMA/BuiltIn15.html"));

		  // Resulting SAX events (the generated FO) must be piped through to FOP
		  Result res = new SAXResult(fop.getDefaultHandler());
		            
		  // Step 6: Start XSLT transformation and FOP processing
		  transformer.transform(src, res);

		} finally {
		  //Clean-up
		  out.close();
		}
	}
	
//	public static void creerPDF(String xml, String xsl, String pdf) throws Exception{
//		// création du résultat (pdf)
//		Driver driver = new Driver();
//		driver.setRenderer(Driver.RENDER_PDF);
//		driver.setOutputStream(new java.io.FileOutputStream(pdf));
//		Result resultat = new SAXResult(driver.getContentHandler());
//		
//		// récupération de la source xml
//		Source source = new StreamSource(xml);
//		
//		// création du transformer en fonction du xsl
//		Source style = new StreamSource(xsl);
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer(style);
//		
//		// transformation
//		transformer.transform(source, resultat);
//	}
	
}
