package msi.gama.doc;

import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.SVNUtils;
import msi.gama.doc.util.WikiCleaner;

public class MainBeforePython {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("GENERATION OF THE DOCUMENTATION - STEP 1/3");
		System.out.print("Preparation of the folders................");
		PrepareEnv.prepareDocumentation();
		System.out.println("DONE");
		
		System.out.print("Checkout Wiki Files from GAMA SVN................");		
		SVNUtils.checkoutSVNGamaDoc();		
		System.out.println("DONE");	
		
		System.out.print("Select and clean some wiki files................");
		WikiCleaner.selectWikiFiles();
		System.out.println("DONE");			
		
		System.out.println("");
		System.out.println("This is the end of the step 1. ");
		System.out.println("Please run the python file 'statwiki.py' in the python folder with arguments: --build --d=files/gen/wiki2wiki ");
	}

}
