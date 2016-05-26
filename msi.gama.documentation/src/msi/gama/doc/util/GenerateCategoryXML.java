package msi.gama.doc.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom2.JDOMException;
import org.tmatesoft.svn.core.SVNException;
import org.xml.sax.SAXException;

import msi.gama.doc.transform.XmlToCategoryXML;
import msi.gama.doc.util.Constants;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;

public class GenerateCategoryXML {

	public static void GenerateKeywordsXML() 
			throws IOException, JDOMException, ParserConfigurationException, SAXException, 
					TransformerException, SVNException {
		System.out.println("GENERATION OF THE XML CATEGORY FILES FROM XML docGAMA.xml");
		System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
		System.out.print("Preparation of the folders................");
		PrepareEnv.prepareDocumentation(Constants.ONLINE);
		System.out.println("DONE");
		
		System.out.print("Merge all the docGAMA.xml files................");		
		UnifyDoc.unifyAllProjects();
		System.out.println("DONE");
		
		System.out.print("Transform the docGAMA.xml file into the XML category file ................");		
		XmlToCategoryXML.createCategoryWiki();
		System.out.println("DONE");		
	}
}
