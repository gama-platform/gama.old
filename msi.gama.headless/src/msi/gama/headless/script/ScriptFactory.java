package msi.gama.headless.script;

public abstract class ScriptFactory {

	// public static IModel loadAndBuildJobs(final String path) throws IOException, GamaHeadlessException {
	// final IModel model = HeadlessSimulationLoader.loadModel(new File(path));
	// return model;
	// }

	// public static ArrayList<String> getModelsInDirectory(final String path) throws IOException {
	// final WorkspaceManager ws = new WorkspaceManager(path);
	// final ArrayList<String> allFiles = ws.getModelLibrary();
	// return allFiles;
	// }

	/*
	 * public static List<IExperimentJob> loadAndBuildJobs(IModel model) { ModelDescription modelDesc =
	 * model.getDescription().getModelDescription(); Set<String> experimentName = modelDesc.getExperimentNames();
	 * 
	 * List<IExperimentJob> res = new ArrayList<IExperimentJob>();
	 * 
	 * @SuppressWarnings("unchecked") Collection<ExperimentDescription> experiments =
	 * (Collection<ExperimentDescription>) modelDesc.getExperiments();
	 * 
	 * for(ExperimentDescription expD:experiments) {
	 * if(!expD.getFacets().get(IKeyword.TYPE).getExpression().literalValue().equals(IKeyword.BATCH)) { IExperimentJob
	 * tj = ExperimentJob.loadAndBuildJob( expD,model.getFilePath(), model); tj.setSeed(12); res.add(tj); } } return
	 * res; }
	 */

}
