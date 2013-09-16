package msi.gama.doc;

import org.tmatesoft.svn.core.SVNException;

import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.SVNUtils;
import msi.gama.doc.util.WikiCleaner;

public class MainBeforePython {

	private static final boolean ONLINE = true;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("GENERATION OF THE DOCUMENTATION - STEP 1/3");
	
		try {
			System.out.print("Preparation of the folders.......................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");
			
			if(ONLINE){
				System.out.print("Checkout Wiki Files from GAMA SVN................PLEASE WAIT");	
				SVNUtils.checkoutSVNGamaDoc();	
				System.out.println("Checkout Wiki Files from GAMA SVN................DONE");	
			} else {
				System.out.println("NO CHECKOUT DONE");
			}
			System.out.print("Select and clean some wiki files.................");
			WikiCleaner.selectWikiFiles();
			System.out.println("DONE");			
		} catch(SVNException e){
			System.out.println("ERROR: Impossible connection to the SVN repository.");
		}	
		
		System.out.println("");
		System.out.println("This is the end of the step 1. ");
		System.out.println("Please run the python file 'statwiki.py' in the python folder with arguments: --build --d=files/gen/wiki2wiki ");

	}
}
