package msi.gama.doc;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom2.JDOMException;
import org.tmatesoft.svn.core.SVNException;
import org.xml.sax.SAXException;

import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.SVNUtils;
import msi.gama.doc.util.UnifyDoc;
import msi.gama.doc.util.WikiCleaner;
import msi.gama.doc.util.XmlToWiki;

public class MainGenerateWiki {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws TransformerException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws SVNException 
	 */
	public static void main(String[] args) 
			throws IOException, JDOMException, ParserConfigurationException, SAXException, 
					TransformerException, SVNException {
		System.out.println("GENERATION OF THE WIKI DOCUMENTATION FROM JAVA CODE");
		System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
		System.out.print("Preparation of the folders................");
		PrepareEnv.prepareDocumentation();
		System.out.println("DONE");
		System.out.print("Merge all the docGAMA.xml files................");		
		UnifyDoc.unify();
		System.out.println("DONE");
		System.out.print("Transform the docGAMA.xml file into Wiki Files................");		
		XmlToWiki.createAllWikis();
		System.out.println("DONE");		
		
		System.out.print("Checkout Wiki Files from GAMA SVN................");		
		SVNUtils.checkoutSVNGamaDoc();		
		System.out.println("DONE");			
	}

}
