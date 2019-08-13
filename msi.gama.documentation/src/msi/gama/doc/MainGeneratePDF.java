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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import msi.gama.doc.pdf.ConvertToPDF;
import msi.gama.doc.util.GamaStyleGeneration;
import msi.gama.doc.util.PrepareEnv;

public class MainGeneratePDF {

	public static boolean generateGamaStyle = true;
	
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
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

}
	