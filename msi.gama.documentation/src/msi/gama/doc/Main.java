package msi.gama.doc;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("GENERATION OF THE DOCUMENTATION");
		System.out.print("Preparation of the folders................");
		PrepareEnv.prepareDocumentation();
		System.out.println("DONE");
		System.out.print("Merge all the docGAMA.xml files................");		
		UnifyDoc.unify();
		System.out.println("DONE");
		System.out.print("Transform the docGAMA.xml file into Wiki Files................");		
		XmlToWiki.createAllWikis();
		System.out.println("DONE");		
		
		System.out.print("Checkout Wiki Files from GAMA SVN................");		
		SVNUtils.checkoutSVNGamaDoc();		
		System.out.println("DONE");	
		
		System.out.print("Select and clean some wiki files................");
		WikiCleaner.selectWikiFiles();
		System.out.println("DONE");	
	}

}
