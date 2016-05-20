package msi.gama.headless.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.JobPlan;
import msi.gama.headless.util.WorkspaceManager;
import msi.gama.headless.xml.XmlTAG;

public class ExperimentationPlanFactory {
	
	public static String DEFAULT_HEADLESS_DIRECTORY_IN_WORKSPACE = ".headless";
	public static String DEFAULT_MODEL_DIRECTORY_IN_WORKSPACE = "models";
	public static long DEFAULT_SEED = 1l;
	public static long DEFAULT_FINAL_STEP = 1000;
	
	

	public static void analyseWorkspace(String directoryPath) throws IOException, ParserConfigurationException, TransformerException
	{
		ArrayList<String> modelFileNames = WorkspaceManager.readDirectory(directoryPath+"./"+DEFAULT_MODEL_DIRECTORY_IN_WORKSPACE);
		for(String nm:modelFileNames)
		{
			analyseModelInWorkspace(new File(nm));
		}
	
	}

	public static void analyseModelInWorkspace(File modelFile) throws IOException, ParserConfigurationException, TransformerException
	{
		String headlessDirectory = modelFile.getParentFile().getParentFile().getAbsolutePath()+"/"+DEFAULT_HEADLESS_DIRECTORY_IN_WORKSPACE;
		String outFileName =modelFile.getName().substring(0, modelFile.getName().lastIndexOf( '.'));
		File storeDirectory = new File(headlessDirectory);
		if(!storeDirectory.exists())
		{
			storeDirectory.mkdirs();
		}
		File outFile = new File(headlessDirectory + "/"+ outFileName+".xml");
		File outerrFile = new File(headlessDirectory + "/err_"+ outFileName+".log");
		
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFile));
		OutputStreamWriter outErr = new OutputStreamWriter(new FileOutputStream(outerrFile));
		analyseModel(modelFile.getAbsolutePath(),out,outErr);
		
		
	/*	JobPlan jb = new JobPlan();
		List<IExperimentJob> generatedExperiment;
		try
		{
			jb.loadModelAndCompileJob(modelFile.getAbsolutePath());
			long[] seed = {DEFAULT_FINAL_STEP};
			generatedExperiment = jb.constructAllJobs(seed,DEFAULT_FINAL_STEP);
			
			
			Document dd =ExperimentationPlanFactory.buildXmlDocument(generatedExperiment);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(dd);
			StreamResult result = new StreamResult(new File("/tmp/file.xml"));
			transformer.transform(source, result);
			
			//generatedExperiment = jb.loadModelAndCompileJob(modelFile.getAbsolutePath());
		}catch(Exception e)
		{
			outErr.write("Error in file : " + modelFile.getAbsolutePath());
		}*/
			
		outErr.close();
		out.close();

	}
	
	public static void analyseModelsDirectoryForValidation(String modelFileName, OutputStreamWriter output , OutputStreamWriter err)
			throws IOException, TransformerException
	{
		List<String> allFiles = ScriptFactory.getModelsInDirectory(modelFileName);
		for(String fPath:allFiles)
		{
			 analyseModelsDirectoryForValidation( modelFileName,  output ,  err);
		}
		
	}
	public static void analyseModel(String modelFileName, OutputStreamWriter output , OutputStreamWriter err)
					throws IOException, TransformerException
	{
		JobPlan jb = new JobPlan();
		try
		{
				jb.loadModelAndCompileJob(modelFileName);
		}catch(Exception e)
		{
				err.write("Error building plan: " + modelFileName);
		}
		long[] seeds = {DEFAULT_SEED};
		List<IExperimentJob> jobs = jb.constructAllJobs(seeds,DEFAULT_FINAL_STEP);
		Document dd;
		try {
			dd = buildXmlDocument(jobs);
		} catch (ParserConfigurationException e) {
			err.write("Error building xml: " + modelFileName);
			return;
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(dd);
		StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
		
	}
	public static List<IExperimentJob> buildExperiment(String modelFileName) throws IOException
	{
		JobPlan jb = new JobPlan();
		jb.loadModelAndCompileJob(modelFileName);
		long[] seeds = {DEFAULT_SEED};
		List<IExperimentJob> jobs = jb.constructAllJobs(seeds,DEFAULT_FINAL_STEP);
		return jobs;
	}
	public static Document buildXmlDocument(List<IExperimentJob> jobs) throws ParserConfigurationException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(XmlTAG.EXPERIMENT_PLAN_TAG);
		doc.appendChild(rootElement);

		
		for(IExperimentJob job:jobs)
		{
			Element jb = job.asXMLDocument(doc);
			rootElement.appendChild(jb);
		}
		
		return doc;
	}
	

}
	
