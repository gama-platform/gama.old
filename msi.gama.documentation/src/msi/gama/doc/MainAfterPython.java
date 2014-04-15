/*********************************************************************************************
 * 
 *
 * 'MainAfterPython.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import msi.gama.doc.transform.XmlToPdf;
import msi.gama.doc.util.*;

public class MainAfterPython {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("GENERATION OF THE DOCUMENTATION - STEP 3/3");

		System.out.print("Merge all HTML files produced from Wiki................");
		MergeHTML.mergeHTMLFiles();
		System.out.println("DONE");			
		
		System.out.print("Produce the final PDF file................");
		XmlToPdf.xmlToPdf();
		System.out.println("DONE");		
	}

}
