/*******************************************************************************************************
 *
 * MainGeneratePDF.java, in msi.gama.documentation, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.doc;

import msi.gama.doc.util.ConvertToPDF;
import msi.gama.doc.util.GamaStyleGeneration;
import msi.gama.doc.util.PrepareEnv;

/**
 * The Class MainGeneratePDF.
 */
public class MainGeneratePDF {

	/** The generate gama style. */
	public static boolean generateGamaStyle = false;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			System.out.println("GENERATION OF THE PDF DOCUMENTATION");
		
			if (generateGamaStyle) {
				GamaStyleGeneration.generateGamaStyle();
			}

			System.out.print("Preparation of the folders.......................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");

			System.out.println("Generation of the PDF file .................");
			ConvertToPDF.convert();
			System.out.println("DONE");			
		} catch(Exception e){
			System.out.println(e);
		}
	}

}
	