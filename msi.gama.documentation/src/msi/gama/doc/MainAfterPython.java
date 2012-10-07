package msi.gama.doc;

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
