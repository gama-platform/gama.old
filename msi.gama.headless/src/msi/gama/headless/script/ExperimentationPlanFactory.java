/*******************************************************************************************************
 *
 * ExperimentationPlanFactory.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.JobListFactory;
import msi.gama.headless.xml.XmlTAG;

/**
 * A factory for creating ExperimentationPlan objects.
 */
public class ExperimentationPlanFactory {

	/** The default headless directory in workspace. */
	public static String DEFAULT_HEADLESS_DIRECTORY_IN_WORKSPACE = ".headless";

	/** The default model directory in workspace. */
	public static String DEFAULT_MODEL_DIRECTORY_IN_WORKSPACE = "models";

	/** The default seed. */
	public static long DEFAULT_SEED = 1l;

	/** The default final step. */
	public static long DEFAULT_FINAL_STEP = 1000;

	// /**
	// * Analyse workspace.
	// *
	// * @param directoryPath
	// * the directory path
	// * @throws IOException
	// * Signals that an I/O exception has occurred.
	// * @throws ParserConfigurationException
	// * the parser configuration exception
	// * @throws TransformerException
	// * the transformer exception
	// */
	// public static void analyseWorkspace(final String directoryPath) throws IOException, TransformerException {
	// final ArrayList<String> modelFileNames =
	// readDirectory(directoryPath + "./" + DEFAULT_MODEL_DIRECTORY_IN_WORKSPACE);
	// for (final String nm : modelFileNames) { analyseModelInWorkspace(new File(nm)); }
	//
	// }
	//
	// /**
	// * Analyse model in workspace.
	// *
	// * @param modelFile
	// * the model file
	// * @throws IOException
	// * Signals that an I/O exception has occurred.
	// * @throws ParserConfigurationException
	// * the parser configuration exception
	// * @throws TransformerException
	// * the transformer exception
	// */
	// public static void analyseModelInWorkspace(final File modelFile) throws IOException, TransformerException {
	// final String headlessDirectory = modelFile.getParentFile().getParentFile().getAbsolutePath() + "/"
	// + DEFAULT_HEADLESS_DIRECTORY_IN_WORKSPACE;
	// final String outFileName = modelFile.getName().substring(0, modelFile.getName().lastIndexOf('.'));
	// final File storeDirectory = new File(headlessDirectory);
	// if (!storeDirectory.exists()) { storeDirectory.mkdirs(); }
	// final File outFile = new File(headlessDirectory + "/" + outFileName + ".xml");
	// final File outerrFile = new File(headlessDirectory + "/err_" + outFileName + ".log");
	//
	// try (final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFile));
	// final OutputStreamWriter outErr = new OutputStreamWriter(new FileOutputStream(outerrFile));) {
	// analyseModel(modelFile.getAbsolutePath(), out, outErr);
	//
	// /*
	// * JobPlan jb = new JobPlan(); List<IExperimentJob> generatedExperiment; try {
	// * jb.loadModelAndCompileJob(modelFile.getAbsolutePath()); long[] seed = {DEFAULT_FINAL_STEP};
	// * generatedExperiment = jb.constructAllJobs(seed,DEFAULT_FINAL_STEP);
	// *
	// *
	// * Document dd =ExperimentationPlanFactory.buildXmlDocument(generatedExperiment); TransformerFactory
	// * transformerFactory = TransformerFactory.newInstance(); Transformer transformer =
	// * transformerFactory.newTransformer(); DOMSource source = new DOMSource(dd); StreamResult result = new
	// * StreamResult(new File("/tmp/file.xml")); transformer.transform(source, result);
	// *
	// * //generatedExperiment = jb.loadModelAndCompileJob(modelFile.getAbsolutePath()); }catch(Exception e) {
	// * outErr.write("Error in file : " + modelFile.getAbsolutePath()); }
	// */
	//
	// }
	//
	// }
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

	// /**
	// * Analyse model.
	// *
	// * @param modelFileName
	// * the model file name
	// * @param output
	// * the output
	// * @param err
	// * the err
	// * @throws IOException
	// * Signals that an I/O exception has occurred.
	// * @throws TransformerException
	// * the transformer exception
	// */
	// public static void analyseModel(final String modelFileName, final OutputStreamWriter output,
	// final OutputStreamWriter err) throws IOException, TransformerException {
	// try {
	// final long[] seeds = { DEFAULT_SEED };
	// final List<IExperimentJob> jobs = JobListFactory.constructAllJobs(modelFileName, seeds, DEFAULT_FINAL_STEP);
	// Document dd;
	// try {
	// dd = buildXmlDocument(jobs);
	// } catch (final ParserConfigurationException e) {
	// err.write("Error building xml: " + modelFileName);
	// return;
	// }
	// final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	// Transformer transformer;
	// transformer = transformerFactory.newTransformer();
	// final DOMSource source = new DOMSource(dd);
	// final StreamResult result = new StreamResult(output);
	// transformer.transform(source, result);
	// } catch (final Exception e) {
	// err.write("Error building plan: " + modelFileName);
	// }
	//
	// }

	/**
	 * Builds the experiment.
	 *
	 * @param modelFileName
	 *            the model file name
	 * @param numberOfCores
	 *            the number of cores passed throuhg the '-hpc' parameter. If null, means that '-hpc' is not set
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	public static List<IExperimentJob> buildExperiment(final String modelFileName, final Integer numberOfCores)
			throws IOException, GamaHeadlessException {
		final long[] seeds = { DEFAULT_SEED };
		return JobListFactory.constructAllJobs(modelFileName, seeds, DEFAULT_FINAL_STEP, numberOfCores);
	}

	/**
	 * Builds the experiment.
	 *
	 * @param modelFileName
	 *            the model file name
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	public static List<IExperimentJob> buildExperiment(final String modelFileName)
			throws IOException, GamaHeadlessException {
		return buildExperiment(modelFileName, null);
	}

	/**
	 * Builds the xml document.
	 *
	 * @param jobs
	 *            the jobs
	 * @return the document
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
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

	/**
	 * Builds the xml document for model library.
	 *
	 * @param jobs
	 *            the jobs
	 * @return the document
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
	public static Document buildXmlDocumentForModelLibrary(final List<IExperimentJob> jobs)
			throws ParserConfigurationException {
		// this class will be executed if "buildModelLibrary" is turn to true. (automatic generation for the website)
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.newDocument();
		final Element rootElement = doc.createElement(XmlTAG.EXPERIMENT_PLAN_TAG);
		doc.appendChild(rootElement);

		for (final IExperimentJob job : jobs) {
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

	/**
	 * Read directory.
	 *
	 * @param dir
	 *            the dir
	 * @return the array list
	 */
	public static ArrayList<String> readDirectory(final String dir) {
		final ArrayList<String> listFiles = new ArrayList<>();
		final File rep = new File(dir);

		if (rep.isDirectory()) {
			final String t[] = rep.list();

			if (t != null) {
				for (final String fName : t) {
					final ArrayList<String> newList = readDirectory(rep.getAbsolutePath() + File.separator + fName);
					listFiles.addAll(newList);
				}
			}
		} else if ("gaml".equals(getFileExtension(rep.getAbsolutePath()))) { listFiles.add(rep.getAbsolutePath()); }

		return listFiles;
	}

	/**
	 * Gets the file extension.
	 *
	 * @param fileName
	 *            the file name
	 * @return the file extension
	 */
	public static String getFileExtension(final String fileName) {
		String extension = null;
		try {
			extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return extension;
	}

}
