package msi.gama.headless.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.JobPlan;
import msi.gama.headless.util.WorkspaceManager;
import msi.gama.headless.xml.XmlTAG;

public class ExperimentationPlanFactory {

	public static String DEFAULT_HEADLESS_DIRECTORY_IN_WORKSPACE = ".headless";
	public static String DEFAULT_MODEL_DIRECTORY_IN_WORKSPACE = "models";
	public static long DEFAULT_SEED = 1l;
	public static long DEFAULT_FINAL_STEP = 1000;

	public static void analyseWorkspace(final String directoryPath)
			throws IOException, ParserConfigurationException, TransformerException {
		final ArrayList<String> modelFileNames =
				WorkspaceManager.readDirectory(directoryPath + "./" + DEFAULT_MODEL_DIRECTORY_IN_WORKSPACE);
		for (final String nm : modelFileNames) {
			analyseModelInWorkspace(new File(nm));
		}

	}

	public static void analyseModelInWorkspace(final File modelFile)
			throws IOException, ParserConfigurationException, TransformerException {
		final String headlessDirectory = modelFile.getParentFile().getParentFile().getAbsolutePath() + "/"
				+ DEFAULT_HEADLESS_DIRECTORY_IN_WORKSPACE;
		final String outFileName = modelFile.getName().substring(0, modelFile.getName().lastIndexOf('.'));
		final File storeDirectory = new File(headlessDirectory);
		if (!storeDirectory.exists()) {
			storeDirectory.mkdirs();
		}
		final File outFile = new File(headlessDirectory + "/" + outFileName + ".xml");
		final File outerrFile = new File(headlessDirectory + "/err_" + outFileName + ".log");

		final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFile));
		final OutputStreamWriter outErr = new OutputStreamWriter(new FileOutputStream(outerrFile));
		analyseModel(modelFile.getAbsolutePath(), out, outErr);

		/*
		 * JobPlan jb = new JobPlan(); List<IExperimentJob> generatedExperiment; try {
		 * jb.loadModelAndCompileJob(modelFile.getAbsolutePath()); long[] seed = {DEFAULT_FINAL_STEP};
		 * generatedExperiment = jb.constructAllJobs(seed,DEFAULT_FINAL_STEP);
		 * 
		 * 
		 * Document dd =ExperimentationPlanFactory.buildXmlDocument(generatedExperiment); TransformerFactory
		 * transformerFactory = TransformerFactory.newInstance(); Transformer transformer =
		 * transformerFactory.newTransformer(); DOMSource source = new DOMSource(dd); StreamResult result = new
		 * StreamResult(new File("/tmp/file.xml")); transformer.transform(source, result);
		 * 
		 * //generatedExperiment = jb.loadModelAndCompileJob(modelFile.getAbsolutePath()); }catch(Exception e) {
		 * outErr.write("Error in file : " + modelFile.getAbsolutePath()); }
		 */

		outErr.close();
		out.close();

	}
	//
	// public static void analyseModelsDirectoryForValidation(final String modelFileName, final OutputStreamWriter
	// output,
	// final OutputStreamWriter err) throws IOException, TransformerException {
	// final List<String> allFiles = ScriptFactory.getModelsInDirectory(modelFileName);
	// for (final String fPath : allFiles) {
	// analyseModelsDirectoryForValidation(modelFileName, output, err);
	// }
	//
	// }

	public static void analyseModel(final String modelFileName, final OutputStreamWriter output,
			final OutputStreamWriter err) throws IOException, TransformerException {
		final JobPlan jb = new JobPlan();
		try {
			jb.loadModelAndCompileJob(modelFileName);
		} catch (final Exception e) {
			err.write("Error building plan: " + modelFileName);
		}
		final long[] seeds = { DEFAULT_SEED };
		final List<IExperimentJob> jobs = jb.constructAllJobs(seeds, DEFAULT_FINAL_STEP);
		Document dd;
		try {
			dd = buildXmlDocument(jobs);
		} catch (final ParserConfigurationException e) {
			err.write("Error building xml: " + modelFileName);
			return;
		}
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(dd);
		final StreamResult result = new StreamResult(output);
		transformer.transform(source, result);

	}

	public static List<IExperimentJob> buildExperiment(final String modelFileName)
			throws IOException, GamaHeadlessException {
		final JobPlan jb = new JobPlan();
		jb.loadModelAndCompileJob(modelFileName);
		final long[] seeds = { DEFAULT_SEED };
		final List<IExperimentJob> jobs = jb.constructAllJobs(seeds, DEFAULT_FINAL_STEP);
		return jobs;
	}

	public static Document buildXmlDocument(final List<IExperimentJob> jobs) throws ParserConfigurationException {
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.newDocument();
		final Element rootElement = doc.createElement(XmlTAG.EXPERIMENT_PLAN_TAG);
		doc.appendChild(rootElement);

		for (final IExperimentJob job : jobs) {
			final Element jb = job.asXMLDocument(doc);
			rootElement.appendChild(jb);
		}

		return doc;
	}

	public static Document buildXmlDocumentForModelLibrary(final List<IExperimentJob> jobs)
			throws ParserConfigurationException {
		// this class will be executed if "buildModelLibrary" is turn to true. (automatic generation for the website)
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.newDocument();
		final Element rootElement = doc.createElement(XmlTAG.EXPERIMENT_PLAN_TAG);
		doc.appendChild(rootElement);

		for (int i = 0; i < jobs.size(); i++) {
			final IExperimentJob job = jobs.get(i);

			final Element jb = job.asXMLDocument(doc);
			// make sure the pathSeparator is correct
			final String modelPath = jb.getAttribute(XmlTAG.SOURCE_PATH_TAG).replace("\\", "/");
			// add the character pathSeparator at the beginning of the string.
			jb.setAttribute(XmlTAG.SOURCE_PATH_TAG, "/" + jb.getAttribute(XmlTAG.SOURCE_PATH_TAG));
			// set the final step to 11
			jb.setAttribute(XmlTAG.FINAL_STEP_TAG, "11");

			final Node outputRoot = jb.getElementsByTagName("Outputs").item(0);
			final NodeList outputs = outputRoot.getChildNodes();
			for (int outputId = 0; outputId < outputs.getLength(); outputId++) {
				// add the attribute "output_path" with the path : path + name_of_display
				final Element output = (Element) outputs.item(outputId);
				final String outputName = output.getAttribute(XmlTAG.NAME_TAG);
				output.setAttribute(XmlTAG.OUTPUT_PATH,
						modelPath.substring(0, modelPath.length() - 5) + "/" + outputName);
				// set the framerate to 10
				output.setAttribute(XmlTAG.FRAMERATE_TAG, "10");
			}
			rootElement.appendChild(jb);
		}

		return doc;
	}

}
