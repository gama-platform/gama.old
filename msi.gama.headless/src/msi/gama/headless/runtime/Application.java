/*********************************************************************************************
 *
 *
 * GAMA modeling and simulation platform. 'Application.java', in plugin 'msi.gama.headless', is part of the source code
 * of the (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.runtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.URI;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;

import com.google.inject.Injector;

import msi.gama.common.GamlFileExtension;
import msi.gama.headless.batch.ModelLibraryRunner;
import msi.gama.headless.batch.ModelLibraryTester;
import msi.gama.headless.batch.ModelLibraryValidator;
import msi.gama.headless.batch.documentation.ModelLibraryGenerator;

import msi.gama.headless.common.Globals;
import msi.gama.headless.common.HeadLessErrors;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.headless.xml.ConsoleReader;
import msi.gama.headless.xml.Reader;
import msi.gama.headless.xml.XMLWriter;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import ummisco.gama.dev.utils.DEBUG;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;

public class Application implements IApplication {

	final public static String HELP_PARAMETER = "-help";
	final public static String GAMA_VERSION = "-version";

	final public static String CONSOLE_PARAMETER = "-c";
	final public static String VERBOSE_PARAMETER = "-v";
	final public static String THREAD_PARAMETER = "-hpc";
	final public static String SOCKET_PARAMETER = "-socket";

	final public static String TUNNELING_PARAMETER = "-p";

	final public static String VALIDATE_LIBRARY_PARAMETER = "-validate";
	final public static String TEST_LIBRARY_PARAMETER = "-test";
	final public static String BUILD_XML_PARAMETER = "-xml";
	final public static String CHECK_MODEL_PARAMETER = "-check";
	final public static String RUN_LIBRARY_PARAMETER = "-runLibrary";

  // -> Code still exist, but not documented nor use
	final public static String BATCH_PARAMETER = "-batch";
	final public static String GAML_PARAMETER = "-gaml";

	public static boolean headLessSimulation = false;
	public int numberOfThread = -1;
	public int socket = -1;
	public boolean consoleMode = false;
	public boolean tunnelingMode = false;
	public boolean verbose = false;
	public SimulationRuntime processorQueue;

	private static void showVersion() {
		DEBUG.ON();
		DEBUG.LOG(
			"Welcome to Gama-platform.org version " + GAMA.VERSION + "\n"
		);
		DEBUG.OFF();
	}

	private static void showHelp() {
		showVersion();
		DEBUG.ON();
		DEBUG.LOG(
			"sh ./gama-headless.sh [Options]\n" 
			+ "\nList of available options:" 
			+ "\n\t=== Headless Options ==="
			+ "\n\t\t-m [mem]                     -- allocate memory (ex 2048m)" 
			+ "\n\t\t" + CONSOLE_PARAMETER + "                           -- start the console to write xml parameter file" 
			+ "\n\t\t" + VERBOSE_PARAMETER + "                           -- verbose mode" 
			+ "\n\t\t" + THREAD_PARAMETER + " [core]                  -- set the number of core available for experimentation" 
			+ "\n\t\t" + SOCKET_PARAMETER + " [socketPort]         -- start socket pipeline to interact with another framework"
			+ "\n\t\t" + TUNNELING_PARAMETER + "                           -- start pipeline to interact with another framework"
			+ "\n\t=== Infos ===" 
			+ "\n\t\t" + HELP_PARAMETER + "                        -- get the help of the command line" 
			+ "\n\t\t" + GAMA_VERSION + "                     -- get the the version of gama" 
			+ "\n\t=== Library Runner ===" 
			+ "\n\t\t" + VALIDATE_LIBRARY_PARAMETER + "                    -- invokes GAMA to validate models present in built-in library and plugins"
			+ "\n\t\t" + TEST_LIBRARY_PARAMETER + "                        -- invokes GAMA to execute the tests present in built-in library and plugins and display their results"
			+ "\n\t=== GAMA Headless Runner ===" 
			+ "\n\t\t" + BATCH_PARAMETER + " [experimentName] [modelFile.gaml]"
			+ "\n\t\t                             -- Run batch experiment in headless mode"
			// + "\n\t\t" + GAML_PARAMETER + " [experimentName] [modelFile.gaml]"
			// + "\n\t\t -- Run single gaml experiment in headless mode"
			+ "\n\t\t" + BUILD_XML_PARAMETER + " [experimentName] [modelFile.gaml] [xmlOutputFile.xml]"
			+ "\n\t\t                             -- build an xml parameter file from a model"
			+ "\n\t\t[xmlHeadlessFile.xml] [outputDirectory]"
			+ "\n\t\t                             -- default usage of GAMA headless"
		);
		DEBUG.OFF();
	}

	private boolean checkParameters(final List<String> args) {
		return checkParameters(args, false);
	}

	private boolean checkParameters(final List<String> args, boolean apply) {

		int size = args.size();
		boolean mustContainInFile = true;
		boolean mustContainOutFile = true;

		// Parameters flag
		// ========================
		if (args.contains(VERBOSE_PARAMETER)) {
			size = size - 1;
			if (apply) {
				this.verbose = true;
				DEBUG.ON();
				DEBUG.LOG("Log active", true);
			}
		}
    
		if (args.contains(CONSOLE_PARAMETER)) {
			size = size - 1;
			mustContainInFile = false;

			// Change value only if function should apply parameter
			this.consoleMode = apply;
		}
		if (args.contains(TUNNELING_PARAMETER)) {
			size = size - 1;
			mustContainOutFile = false;

			// Change value only if function should apply parameter
			this.tunnelingMode = apply;
		}
		if (args.contains(SOCKET_PARAMETER)) {
			size = size - 2;
			mustContainOutFile = false;

			// Change value only if function should apply parameter
			this.socket = apply ? Integer.valueOf(after(args, SOCKET_PARAMETER)) : -1;
		}
		if (args.contains(THREAD_PARAMETER)) {
			size = size - 2;

			// Change value only if function should apply parameter
			this.numberOfThread = apply ? Integer.valueOf(after(args, THREAD_PARAMETER)) : SimulationRuntime.UNDEFINED_QUEUE_SIZE;
		}

		// Commands
		// ========================
		if (args.contains(GAMA_VERSION) || args.contains(HELP_PARAMETER) || args.contains(VALIDATE_LIBRARY_PARAMETER) || args.contains(TEST_LIBRARY_PARAMETER) ) {
			size = size - 1;
			mustContainOutFile = mustContainInFile = false;
		}
		if (args.contains(BATCH_PARAMETER)) {
			size = size - 3;
			mustContainOutFile = false;
		}

		// Runner verification
		// ========================
		if (mustContainInFile && mustContainOutFile && size < 2) {
			return showError(HeadLessErrors.INPUT_NOT_DEFINED, null);
		}
		if (!mustContainInFile && mustContainOutFile && size < 1) {
			return showError(HeadLessErrors.OUTPUT_NOT_DEFINED, null);
		}

		// In/out files
		// ========================
		if (mustContainOutFile) {
			// Check and create output folder
			Globals.OUTPUT_PATH = args.get(args.size() - 1);
			final File output = new File(Globals.OUTPUT_PATH);
			if (!output.exists()) {
				output.mkdir();
			}
			// Check and create output image folder
			Globals.IMAGES_PATH = Globals.OUTPUT_PATH + "/snapshot";
			final File images = new File(Globals.IMAGES_PATH);
			if (!images.exists()) {
				images.mkdir();
			}
		}
		if (mustContainInFile) {
			final int inIndex = args.size() - (mustContainOutFile ? 2 : 1);
			final File input = new File(args.get(inIndex));
			if (!input.exists()) {
				return showError(HeadLessErrors.NOT_EXIST_FILE_ERROR, args.get(inIndex));
			}
		}
		return true;
	}

	private static boolean showError(final int errorCode, final String path) {
		DEBUG.ON();
		DEBUG.ERR(HeadLessErrors.getError(errorCode, path));
		DEBUG.OFF();

		return false;
	}

	@Override
	public Object start(final IApplicationContext context) throws Exception {

		final Map<String, String[]> mm = context.getArguments();
		final List<String> args = Arrays.asList(mm.get("application.args"));
		
		// Check and apply parameters
		if ( !checkParameters(args, true) ) {
			System.exit(-1);
		}

		// ========================
		// No GAMA run
		// ========================		
		boolean shouldExit = false;
		
		if (args.contains(GAMA_VERSION)) {
			showVersion();
			shouldExit = true;
		} else if (args.contains(HELP_PARAMETER)) {
			showHelp();
			shouldExit = true;
		} 
		
		if (shouldExit)
			System.exit(0);

		// ========================
		// With GAMA run
		// ========================
		HeadlessSimulationLoader.preloadGAMA();
		DEBUG.OFF();
		
		// Debug runner
		if (args.contains(VALIDATE_LIBRARY_PARAMETER))
			return ModelLibraryValidator.getInstance().start();
		else if (args.contains(TEST_LIBRARY_PARAMETER))
			return ModelLibraryTester.getInstance().start();
		else if (args.contains(RUN_LIBRARY_PARAMETER))
			return ModelLibraryRunner.getInstance().start();
		else if (args.contains(CHECK_MODEL_PARAMETER))
			ModelLibraryGenerator.start(this, args);

		// User runner
		else if (args.contains(BATCH_PARAMETER))
			runBatchSimulation(args.get(args.size() - 2), args.get(args.size() - 1));
		else if (args.contains(GAML_PARAMETER))
			runGamlSimulation(args);
		else if (args.contains(BUILD_XML_PARAMETER))
			buildXML(args);
		else
			runSimulation(args);
		
		return null;
	}

	public String after(final List<String> args, final String arg) {
		if (args == null || args.size() < 2)
			return null;
		for (int i = 0; i < args.size() - 1; i++) {
			if (args.get(i).equals(arg))
				return args.get(i + 1);
		}
		return null;
	}

	public void buildXML(final List<String> arg)
			throws ParserConfigurationException, TransformerException, IOException, GamaHeadlessException {
		if (arg.size() < 3) {
			DEBUG.ON();
			DEBUG.ERR("Check your parameters!");
			showHelp();
			return;
		}

		
		// -xml [exp] [gaml] [xml]
		final String argExperimentName = arg.get(arg.size() - 3);
		final String argGamlFile = arg.get(arg.size() - 2);
		final String argXMLFile = arg.get(arg.size() - 1);
		
		final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(argGamlFile);
		final ArrayList<IExperimentJob> selectedJob = new ArrayList<>();
		for (final IExperimentJob j : jb) {
			if (j.getExperimentName().equals(argExperimentName)) {
				selectedJob.add(j);
				break;
			}
		}

		final Document dd = ExperimentationPlanFactory.buildXmlDocument(selectedJob);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(dd);
		final File output = new File(argXMLFile);
		final StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
		DEBUG.ON();
		DEBUG.LOG("Parameter file saved at: " + output.getAbsolutePath());
	}

	public void buildXMLForModelLibrary(final ArrayList<File> modelPaths, final String outputPath)
			throws ParserConfigurationException, TransformerException, IOException, GamaHeadlessException {
		// "arg[]" are the paths to the different models
		final ArrayList<IExperimentJob> selectedJob = new ArrayList<>();
		for (final File modelFile : modelPaths) {
			final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(modelFile.getAbsolutePath());
			for (final IExperimentJob j : jb) {
				selectedJob.add(j);
			}
		}

		final Document dd = ExperimentationPlanFactory.buildXmlDocumentForModelLibrary(selectedJob);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(dd);
		final File output = new File(outputPath);
		output.createNewFile();
		final StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
		DEBUG.ON();
		DEBUG.LOG("Parameter file saved at: " + output.getAbsolutePath());
	}

	public void runXMLForModelLibrary(final String xmlPath) throws FileNotFoundException {

		processorQueue = new LocalSimulationRuntime();
		final Reader in = new Reader(xmlPath);
		in.parseXmlFile();
		this.buildAndRunSimulation(in.getSimulation());
		in.dispose();
		while (processorQueue.isPerformingSimulation()) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void runSimulation(final List<String> args) throws FileNotFoundException, InterruptedException {
		processorQueue = new LocalSimulationRuntime(this.numberOfThread);

		Reader in = null;
		if (this.verbose && !this.tunnelingMode) {
			DEBUG.ON();
		}

		if (this.consoleMode) {
			in = new Reader(ConsoleReader.readOnConsole());
		} else {
			in = new Reader(args.get(args.size() - 2));
		}
		in.parseXmlFile();
		this.buildAndRunSimulation(in.getSimulation());
		in.dispose();
		while (processorQueue.isPerformingSimulation()) {
			Thread.sleep(1000);
		}

		System.exit(0);
	}

	public void runBatchSimulation(final List<String> args) throws FileNotFoundException, InterruptedException {
		final String pathToModel = args.get(args.size() - 1);
		
		if (!GamlFileExtension.isGaml(pathToModel)) { System.exit(-1); }
	
		final Injector injector = HeadlessSimulationLoader.getInjector();
		final GamlModelBuilder builder = new GamlModelBuilder(injector);

		final List<GamlCompilationError> errors = new ArrayList<>();
		final IModel mdl = builder.compile(URI.createFileURI(pathToModel), errors);
		
		final IExperimentPlan expPlan = mdl.getExperiment(args.get(args.size() - 2));
		
		expPlan.setHeadless(true);
		expPlan.open();
		
		System.exit(0);
	} 
	
	public void buildAndRunSimulation(final Collection<ExperimentJob> sims) {
		final Iterator<ExperimentJob> it = sims.iterator();
		while (it.hasNext()) {
			final ExperimentJob sim = it.next();
			try {
				XMLWriter ou = null;
				if (tunnelingMode) {
					ou = new XMLWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
				} else {
					ou = new XMLWriter(
							Globals.OUTPUT_PATH + "/" + Globals.OUTPUT_FILENAME + sim.getExperimentID() + ".xml");
				}
				sim.setBufferedWriter(ou);

				processorQueue.pushSimulation(sim);
			} catch (final Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	@Override
	public void stop() {
	}

	/*
	 * New runner implementations
	 */

	/**
	 * Auto launch batch experiment in headless mode from a gaml file
	 * 
	 * @param experimentName
	 * @param pathToModel
	 */
	public void runBatchSimulation(String experimentName, String pathToModel) {
		if (!GamlFileExtension.isGaml(pathToModel)) {
			System.exit(-1);
		}

		final Injector injector = HeadlessSimulationLoader.getInjector();
		final GamlModelBuilder builder = new GamlModelBuilder(injector);

		final List<GamlCompilationError> errors = new ArrayList<>();
		final IModel mdl = builder.compile(URI.createFileURI(pathToModel), errors);

		final IExperimentPlan expPlan = mdl.getExperiment(experimentName);
		expPlan.setHeadless(true);

		expPlan.open();

		System.exit(0);
	}

	/**
	 * Auto launch gui experiment in headless mode from a gaml file
	 * 
	 * @param experimentName
	 * @param pathToModel
	 */
	public void runGamlSimulation(final List<String> args) throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		if (!GamlFileExtension.isGaml(pathToModel)) {
			System.exit(-1);
		}
		final String argExperimentName = args.get(args.size() - 2);
		final String argGamlFile = args.get(args.size() - 1);
		
		final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(argGamlFile);
		ExperimentJob selectedJob = null;
		for (final IExperimentJob j : jb) {
			if (j.getExperimentName().equals(argExperimentName)) {
				selectedJob = (ExperimentJob) j;
				break;
			}
		}
		
		Globals.OUTPUT_PATH = args.get(args.size() - 3);
		
		selectedJob.setBufferedWriter(
				new XMLWriter(Globals.OUTPUT_PATH + "/" + Globals.OUTPUT_FILENAME + ".xml")
			);
		
		if (args.contains(THREAD_PARAMETER)) {
			this.numberOfThread = Integer.valueOf(after(args, THREAD_PARAMETER));
		} else {
			numberOfThread = SimulationRuntime.UNDEFINED_QUEUE_SIZE;
		}
		processorQueue = new LocalSimulationRuntime(this.numberOfThread);

		processorQueue.pushSimulation(selectedJob);
		
		System.exit(0);
	}

}
