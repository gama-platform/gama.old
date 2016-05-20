package msi.gama.headless.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.util.WorkspaceManager;
import msi.gama.headless.xml.XmlTAG;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.statements.Facets;

public abstract class ScriptFactory {
	
	public static IModel loadAndBuildJobs(String path) throws IOException
	{
		IModel model = HeadlessSimulationLoader.loadModel(new File(path));
		return model;
	}
	
	public static ArrayList<String> getModelsInDirectory(String path) throws IOException
	{
		WorkspaceManager ws = new WorkspaceManager(path);
		ArrayList<String> allFiles = ws.getModelLibrary();
		return allFiles;
	}
	
	

/*	public static List<IExperimentJob> loadAndBuildJobs(IModel model)
	{
		ModelDescription modelDesc = model.getDescription().getModelDescription();
		Set<String> experimentName = modelDesc.getExperimentNames();
		
		List<IExperimentJob> res = new ArrayList<IExperimentJob>();  
		
		@SuppressWarnings("unchecked")
		Collection<ExperimentDescription> experiments =  (Collection<ExperimentDescription>) modelDesc.getExperiments();
		
		for(ExperimentDescription expD:experiments)
		{
			if(!expD.getFacets().get(IKeyword.TYPE).getExpression().literalValue().equals(IKeyword.BATCH))
			{
				IExperimentJob tj = ExperimentJob.loadAndBuildJob(  expD,model.getFilePath(), model);
				tj.setSeed(12);
				res.add(tj);
			}
		}
		return res;
	}
	*/

	
}
