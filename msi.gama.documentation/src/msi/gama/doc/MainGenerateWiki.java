/*******************************************************************************************************
 *
 * MainGenerateWiki.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc;

import java.io.File;
import msi.gama.doc.transform.XmlToWiki;
import msi.gama.doc.transform.XmlTransform;
import msi.gama.doc.util.CheckConcepts;
import msi.gama.doc.util.GenerateCategoryXML;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;
import msi.gama.precompiler.doc.utils.Constants;

/**
 * The Class MainGenerateWiki.
 */
public class MainGenerateWiki {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) { 
		try {
			// build the file keywords.xml
			GenerateCategoryXML.GenerateKeywordsXML();

			// generate the wiki documentation
			System.out.println("GENERATION OF THE WIKI DOCUMENTATION FROM JAVA CODE");
			System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
			System.out.print("Preparation of the folders................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");

			System.out.print("Merge all the docGAMA.xml files................");
			UnifyDoc.unify((args.length > 0) ? (args[0].equals("-online") ? false : true) : true);
			System.out.println("DONE");

			System.out.print(
					"Transform the docGAMA.xml file into Wiki Files (md) and create/update them in the gama.wiki folder................");
			XmlToWiki.createAllWikis();
			XmlToWiki.createExtentionsWiki();
			System.out.println("DONE");

			// check the concept used, print a report and write it in the file
			// "website generation"
			CheckConcepts.DoCheckConcepts();			
			
			System.out.print("GENERATION of the prism highlight JS file.....");
			// Creation of the DOM source
			XmlTransform.transformXML(Constants.DOCGAMA_GLOBAL_FILE, 
					Constants.XSL_XML2PRISM_FOLDER + File.separator + "docGama-xml2prism.xsl", 
					Constants.PRISM_GEN_FOLDER + File.separator + "prism-gaml.js");	
			System.out.println("DONE");
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
