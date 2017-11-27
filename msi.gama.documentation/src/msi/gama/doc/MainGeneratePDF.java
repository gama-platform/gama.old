/*********************************************************************************************
 * 
 *
 * 'MainGenerateWiki.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import msi.gama.doc.util.ConvertToPDF;
import msi.gama.doc.util.GamaStyleGeneration;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.precompiler.doc.utils.Constants;


public class MainGeneratePDF {

	public static boolean generateGamaStyle = false;
	
	public static void main(String[] args) {
		try {
			System.out.println("GENERATION OF THE PDF DOCUMENTATION");
		
		if (generateGamaStyle) {
			GamaStyleGeneration.generateGamaStyle();
		}

			System.out.print("Preparation of the folders.......................");
			PrepareEnv.prepareDocumentation(Constants.ONLINE);
			System.out.println("DONE");
			
			if(Constants.ONLINE){
				System.out.print("Checkout Wiki Files from GAMA SVN................PLEASE WAIT");	
				// TODO faire un pull !!!
				System.out.println("Checkout Wiki Files from GAMA SVN................DONE");	
			} else {
				System.out.println("NO CHECKOUT DONE  ...");
			}
			System.out.println("Generation of the PDF file .................");
			ConvertToPDF.convert();
			System.out.println("DONE");			
		} catch(Exception e){
			System.out.println("ERROR: Impossible connection to the SVN repository.");
			System.out.println(e);
		}
	}

}
	