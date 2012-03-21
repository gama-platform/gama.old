package msi.gama.headless;


public class HeadlessGamaLoader {

	private String fileName;
	
	public HeadlessGamaLoader(String theFile)
	{
		this.fileName=theFile;
	}
	
	public void load()
	{
		System.out.println("loading ....");

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*	try {
			GamaBundleLoader.preBuildContributions();
		} catch (GamaStartupException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		*/
		
		//String core = "msi.gama.core";
	
	/*	try {
			GamaBundleLoader.preBuildBundle(core, "gaml");
		} catch (GamaStartupException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		*/
		/*try {
			GamaBundleLoader.preBuildContributions();
		} catch (GamaStartupException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		
		/*	String corePath = additions.get(core);
		if ( corePath == null ) { throw new GamaStartupException(
			"Core implementation of GAML not found. Please check that msi.gama.core is in the application bundles",
			(Throwable) null); }
		*/
		
	/*	ErrorCollector e = new ErrorCollector();
		GamlStandaloneSetup.doSetup();		
		ResourceSet rs = new ResourceSetImpl();
		Resource r = rs.getResource(URI.createURI(this.fileName), true);
		
		//Map<Resource, ISyntacticElement> elements =
		//		GamlToSyntacticElements.buildSyntacticTree(r, e);
		
		EObject eobject = r.getContents().get(0);
		Model model2 = (Model) eobject;
		GamlToSyntacticElements.buildSyntacticTree(r, e);*/
		/* 
		 * fireBuildStarted(r);
		 
		Map<Resource, ISyntacticElement> elements =
				GamlToSyntacticElements.buildSyntacticTree(r, e);
		//fireBuildStarted(resource2);
		
		EObject eobject = r.getContents().get(0);
		
		Model model2 = (Model) eobject;
		//GamlToSyntacticElements.buildSyntacticTree(model2, e);
		System.out.println(model2);
*/
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new HeadlessGamaLoader("src/boids.gaml").load();
		
		
		
	}


}
