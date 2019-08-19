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
import ummisco.gama.dev.utils.DEBUG;

public class MainGeneratePDF {

	public static final boolean GENERATE_GAMA_STYLE = true;
	
	public static void main(String[] args) {
		try {
			DEBUG.LOG("GENERATION OF THE PDF DOCUMENTATION");
		
			if (GENERATE_GAMA_STYLE) {
				GamaStyleGeneration.generateGamaStyle();
			}

			DEBUG.LOG("Preparation of the folders.......................");
			PrepareEnv.prepareDocumentation();
			DEBUG.LOG("DONE");

			DEBUG.LOG("Generation of the PDF file .................");
			ConvertToPDF.convert();
			DEBUG.LOG("DONE");		
		} catch (ParserConfigurationException | SAXException | IOException e) {
			DEBUG.ERR("Error in PDF Generation.", e);
		}			
	}

}
	