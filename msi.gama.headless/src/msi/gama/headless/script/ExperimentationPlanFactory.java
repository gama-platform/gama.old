package msi.gama.headless.script;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.JobPlan;
import msi.gama.headless.util.WorkspaceManager;

public class ExperimentationPlanFactory {
	public static void analyseModelForValidation(String modelFileName)
	{
		File errorFile = new File(modelFileName) ;
		//PrintWriter
	}
	
	
/*int main()
{
	
	WorkspaceManager ws = new WorkspaceManager("/Users/nicolas/git/gama_mars/");
	ArrayList<String> allFiles = ws.getModelLibrary();
	JobPlan jb = new JobPlan();
	
	for(String fPath:allFiles)
	{
		try
		{
			System.out.println("Lecture du fichier "+ fPath);
			jb.loadModelAndCompileJob(fPath);
		}catch(Exception e)
		{
			System.out.println("*********************  error in  "+ fPath);
			
		}
	}
	long[] seeds = {1l,2l,3l,4l,5l,6l,7l,8l,9l,10l};
	List<IExperimentJob> jobs = jb.constructAllJobs(seeds,100, null, null);
	
	Document dd =ScriptFactory.buildXmlDocument(jobs);
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(dd);
	StreamResult result = new StreamResult(new File("/tmp/file4.xml"));
	transformer.transform(source, result);

	System.exit(-1);
}*/
}
