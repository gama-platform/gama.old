/*******************************************************************************************************
 *
 * Application.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.runtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.URI;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
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
import msi.gama.headless.listener.GamaListener;
import msi.gama.headless.listener.GamaWebSocketServer;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.headless.xml.ConsoleReader;
import msi.gama.headless.xml.Reader;
import msi.gama.headless.xml.XMLWriter;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gaml.compilation.GamlCompilationError;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class Application.
 */
public class Application implements IApplication {

	/** The Constant HELP_PARAMETER. */
	final public static String HELP_PARAMETER = "-help";

	/** The Constant GAMA_VERSION. */
	final public static String GAMA_VERSION = "-version";

	/** The Constant CONSOLE_PARAMETER. */
	final public static String CONSOLE_PARAMETER = "-c";

	/** The Constant VERBOSE_PARAMETER. */
	final public static String VERBOSE_PARAMETER = "-v";

	/** The Constant THREAD_PARAMETER. */
	final public static String THREAD_PARAMETER = "-hpc";

	/** The Constant SOCKET_PARAMETER. */
	final public static String SOCKET_PARAMETER = "-socket";

	/** The Constant SECURE_SSL_SOCKET_PARAMETER. */
	final public static String SSOCKET_PARAMETER = "-ssocket";

	/** The Constant TUNNELING_PARAMETER. */
	final public static String TUNNELING_PARAMETER = "-p";

	/** The Constant VALIDATE_LIBRARY_PARAMETER. */
	final public static String VALIDATE_LIBRARY_PARAMETER = "-validate";

	/** The Constant TEST_LIBRARY_PARAMETER. */
	final public static String TEST_LIBRARY_PARAMETER = "-test";

	/** The Constant BUILD_XML_PARAMETER. */
	final public static String BUILD_XML_PARAMETER = "-xml";

	/** The Constant CHECK_MODEL_PARAMETER. */
	final public static String CHECK_MODEL_PARAMETER = "-check";

	/** The Constant RUN_LIBRARY_PARAMETER. */
	final public static String RUN_LIBRARY_PARAMETER = "-runLibrary";

	/** The Constant BATCH_PARAMETER. */
	// -> Code still exist, but not documented nor use
	final public static String BATCH_PARAMETER = "-batch";

	/** The Constant GAML_PARAMETER. */
	final public static String GAML_PARAMETER = "-gaml";

	/** The Constant WRITE_XMI. */
	final public static String WRITE_XMI = "-write-xmi";

	/** The head less simulation. */
	public static boolean headLessSimulation = false;

	/** The socket. */
	public int socket = -1;

	/** The console mode. */
	public boolean consoleMode = false;

	/** The tunneling mode. */
	public boolean tunnelingMode = false;

	/** The verbose. */
	public boolean verbose = false;

	/** The processor queue. */
	public final SimulationRuntime processorQueue = new SimulationRuntime();

	/** The socket server. */
	public GamaWebSocketServer socketServer;

	/**
	 * Show version.
	 */
	private static void showVersion() {
		DEBUG.ON();
		DEBUG.LOG("Welcome to Gama-platform.org version " + GAMA.VERSION + "\n");
		DEBUG.OFF();
	}

	/**
	 * Show help.
	 */
	private static void showHelp() {
		showVersion();
		DEBUG.ON();
		DEBUG.LOG("sh ./gama-headless.sh [Options]\n" + "\nList of available options:" + "\n\t=== Headless Options ==="
				+ "\n\t\t-m [mem]                     -- allocate memory (ex 2048m)" + "\n\t\t" + CONSOLE_PARAMETER
				+ "                           -- start the console to write xml parameter file" + "\n\t\t"
				+ VERBOSE_PARAMETER + "                           -- verbose mode" + "\n\t\t" + THREAD_PARAMETER
				+ " [core]                  -- set the number of core available for experimentation" + "\n\t\t"
				+ SOCKET_PARAMETER + " [socketPort]         -- start socket pipeline to interact with another framework"
				+ "\n\t\t" + TUNNELING_PARAMETER
				+ "                           -- start pipeline to interact with another framework"
				+ "\n\t=== Infos ===" + "\n\t\t" + HELP_PARAMETER
				+ "                        -- get the help of the command line" + "\n\t\t" + GAMA_VERSION
				+ "                     -- get the the version of gama" + "\n\t=== Library Runner ===" + "\n\t\t"
				+ VALIDATE_LIBRARY_PARAMETER
				+ "                    -- invokes GAMA to validate models present in built-in library and plugins"
				+ "\n\t\t" + TEST_LIBRARY_PARAMETER
				+ "                        -- invokes GAMA to execute the tests present in built-in library and plugins and display their results"
				+ "\n\t=== GAMA Headless Runner ===" + "\n\t\t" + BATCH_PARAMETER + " [experimentName] [modelFile.gaml]"
				+ "\n\t\t                             -- Run batch experiment in headless mode"
				// + "\n\t\t" + GAML_PARAMETER + " [experimentName] [modelFile.gaml]"
				// + "\n\t\t -- Run single gaml experiment in headless mode"
				+ "\n\t\t" + BUILD_XML_PARAMETER + " [experimentName] [modelFile.gaml] [xmlOutputFile.xml]"
				+ "\n\t\t                             -- build an xml parameter file from a model"
				+ "\n\t\t[xmlHeadlessFile.xml] [outputDirectory]"
				+ "\n\t\t                             -- default usage of GAMA headless");
		// + "\n\t\t" + WRITE_XMI + " -- write scope provider resource files to disk");
		DEBUG.OFF();
	}

	/**
	 * Check parameters.
	 *
	 * @param args
	 *            the args
	 * @param apply
	 *            the apply
	 * @return true, if successful
	 */
	private boolean checkParameters(final List<String> args, final boolean apply) {

		int size = args.size();
		boolean mustContainInFile = true;
		boolean mustContainOutFolder = true;

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
			mustContainOutFolder = false;

			// Change value only if function should apply parameter
			this.tunnelingMode = apply;
		}
		if (args.contains(SOCKET_PARAMETER)) {
			size = size - 2;
			mustContainOutFolder = mustContainInFile = false;

			// Change value only if function should apply parameter
			this.socket = apply ? Integer.parseInt(after(args, SOCKET_PARAMETER)) : -1;
		}
		if (args.contains(SSOCKET_PARAMETER)) {
			size = size - 2;
			mustContainOutFolder = mustContainInFile = false;

			// Change value only if function should apply parameter
			this.socket = apply ? Integer.parseInt(after(args, SSOCKET_PARAMETER)) : -1;
		}
		if (args.contains(THREAD_PARAMETER)) {
			size = size - 2;

			// Change value only if function should apply parameter
			processorQueue.setNumberOfThreads(
					apply ? Integer.parseInt(after(args, THREAD_PARAMETER)) : SimulationRuntime.DEFAULT_NB_THREADS);
		}

		// Commands
		// ========================
		if (args.contains(WRITE_XMI) || args.contains(GAMA_VERSION) || args.contains(HELP_PARAMETER)
				|| args.contains(VALIDATE_LIBRARY_PARAMETER) || args.contains(TEST_LIBRARY_PARAMETER)) {
			size = size - 1;
			mustContainOutFolder = mustContainInFile = false;
		}
		if (args.contains(BATCH_PARAMETER)) {
			size = size - 3;
			mustContainOutFolder = false;
		}
		if (args.contains(BUILD_XML_PARAMETER)) {
			size = size - 4;
			mustContainInFile = mustContainOutFolder = false;
		}

		// Runner verification
		// ========================
		if (mustContainInFile && mustContainOutFolder && size < 2)
			return showError(HeadLessErrors.INPUT_NOT_DEFINED, null);
		if (!mustContainInFile && mustContainOutFolder && size < 1)
			return showError(HeadLessErrors.OUTPUT_NOT_DEFINED, null);

		// In/out files
		// ========================
		if (mustContainOutFolder) {
			// Check and create output folder
			Globals.OUTPUT_PATH = args.get(args.size() - 1);
			final File output = new File(Globals.OUTPUT_PATH);
			if (!output.exists()) { output.mkdir(); }
			// Check and create output image folder
			Globals.IMAGES_PATH = Globals.OUTPUT_PATH + "/snapshot";
			final File images = new File(Globals.IMAGES_PATH);
			if (!images.exists()) { images.mkdir(); }
		}
		if (mustContainInFile) {
			final int inIndex = args.size() - (mustContainOutFolder ? 2 : 1);
			final File input = new File(args.get(inIndex));
			if (!input.exists()) return showError(HeadLessErrors.NOT_EXIST_FILE_ERROR, args.get(inIndex));
		}
		return true;
	}

	/**
	 * Show error.
	 *
	 * @param errorCode
	 *            the error code
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
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
		if (!checkParameters(args, true)) { System.exit(-1); }

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

		if (shouldExit) { System.exit(0); }

		// ========================
		// With GAMA run
		// ========================
		HeadlessSimulationLoader.preloadGAMA();

		DEBUG.OFF();

		// Debug runner
		if (args.contains(VALIDATE_LIBRARY_PARAMETER)) return ModelLibraryValidator.getInstance().start();
		if (args.contains(TEST_LIBRARY_PARAMETER)) return ModelLibraryTester.getInstance().start();
		if (args.contains(RUN_LIBRARY_PARAMETER)) return ModelLibraryRunner.getInstance().start();
		if (args.contains(CHECK_MODEL_PARAMETER)) {
			ModelLibraryGenerator.start(this, args);
			// else if (args.contains(WRITE_XMI)) {
			// String outputDir = "all-resources";
			// DEBUG.ON();
			// DEBUG.LOG("Writing xmi files to " + outputDir);
			// IMap<EClass, Resource> resources = BuiltinGlobalScopeProvider.getResources();
			// resources.forEach((k, res) -> {
			// String out = k.getName() + ".xmi";
			// OutputStream outf;
			// try {
			// outf = new FileOutputStream(out);
			// res.save(outf, null);
			// DEBUG.LOG("Resource written to " + out);
			// } catch (Exception e1) {
			// e1.printStackTrace();
			// }
			// });
			// DEBUG.LOG("Done");
			// }
		} else if (args.contains(BATCH_PARAMETER)) {
			runBatchSimulation(args.get(args.size() - 2), args.get(args.size() - 1));
		} else if (args.contains(GAML_PARAMETER)) {
			runGamlSimulation(args);
		} else if (args.contains(BUILD_XML_PARAMETER)) {
			buildXML(args);
		} else if (args.contains(SOCKET_PARAMETER)) {
			// GamaListener.newInstance(this.socket, this);
			GamaListener gl = new GamaListener(this.socket, this, false);
		} else if (args.contains(SSOCKET_PARAMETER)) {
			// GamaListener.newInstance(this.socket, this);
			GamaListener gl = new GamaListener(this.socket, this, true);
		} else {
			runSimulation(args);
		}

		return null;
	}

	/**
	 * After.
	 *
	 * @param args
	 *            the args
	 * @param arg
	 *            the arg
	 * @return the string
	 */
	public String after(final List<String> args, final String arg) {
		if (args == null || args.size() < 2) return null;
		for (int i = 0; i < args.size() - 1; i++) { if (args.get(i).equals(arg)) return args.get(i + 1); }
		return null;
	}

	/**
	 * Builds the XML.
	 *
	 * @param arg
	 *            the arg
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws TransformerException
	 *             the transformer exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
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

		if (selectedJob.size() == 0) {
			DEBUG.ON();
			DEBUG.ERR("\n=== ERROR ===" + "\n\tGAMA is about to generate an empty XML file."
					+ "\n\tIf you want to run a Batch experiment, please check the \"-batch\" flag.");
			System.exit(-1);
		} else {
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
	}

	/**
	 * Builds the XML for model library.
	 *
	 * @param modelPaths
	 *            the model paths
	 * @param outputPath
	 *            the output path
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws TransformerException
	 *             the transformer exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	public void buildXMLForModelLibrary(final ArrayList<File> modelPaths, final String outputPath)
			throws ParserConfigurationException, TransformerException, IOException, GamaHeadlessException {
		// "arg[]" are the paths to the different models
		final ArrayList<IExperimentJob> selectedJob = new ArrayList<>();
		for (final File modelFile : modelPaths) {
			final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(modelFile.getAbsolutePath());
			selectedJob.addAll(jb);
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

	/**
	 * Run XML for model library.
	 *
	 * @param xmlPath
	 *            the xml path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InterruptedException
	 */
	public void runXMLForModelLibrary(final String xmlPath) throws FileNotFoundException, InterruptedException {
		runXML(new Reader(xmlPath));
	}

	/**
	 * Run simulation.
	 *
	 * @param args
	 *            the args
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void runSimulation(final List<String> args) throws FileNotFoundException, InterruptedException {
		if (this.verbose && !this.tunnelingMode) { DEBUG.ON(); }
		runXML(consoleMode ? new Reader(ConsoleReader.readOnConsole()) : new Reader(args.get(args.size() - 2)));
		System.exit(0);
	}

	/**
	 * Run XML.
	 *
	 * @param in
	 *            the in
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private void runXML(final Reader in) throws InterruptedException {
		in.parseXmlFile();
		this.buildAndRunSimulation(in.getSimulation());
		in.dispose();
		processorQueue.shutdown();
		while (!processorQueue.awaitTermination(100, TimeUnit.MILLISECONDS)) {}
	}

	/**
	 * Builds the and run simulation.
	 *
	 * @param sims
	 *            the sims
	 */
	public void buildAndRunSimulation(final Collection<ExperimentJob> sims) {
		for (ExperimentJob sim : sims) {
			try {
				XMLWriter ou = null;
				if (tunnelingMode) {
					ou = new XMLWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
				} else {
					ou = new XMLWriter(
							Globals.OUTPUT_PATH + "/" + Globals.OUTPUT_FILENAME + sim.getExperimentID() + ".xml");
				}
				sim.setBufferedWriter(ou);
				processorQueue.execute(sim);
			} catch (final Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	@Override
	public void stop() {}

	/*
	 * New runner implementations
	 */

	/**
	 * Auto launch batch experiment in headless mode from a gaml file
	 *
	 * @param experimentName
	 * @param pathToModel
	 */
	public void runBatchSimulation(final String experimentName, final String pathToModel)
			throws FileNotFoundException, InterruptedException {
		if (!GamlFileExtension.isGaml(pathToModel)) { System.exit(-1); }

		final Injector injector = HeadlessSimulationLoader.getInjector();
		final GamlModelBuilder builder = new GamlModelBuilder(injector);

		final List<GamlCompilationError> errors = new ArrayList<>();
		final IModel mdl = builder.compile(URI.createURI(pathToModel), errors);

		GamaExecutorService.CONCURRENCY_SIMULATIONS.set(true);
		GamaExecutorService.THREADS_NUMBER.set(processorQueue.getCorePoolSize());

		final IExperimentPlan expPlan = mdl.getExperiment(experimentName);

		expPlan.setHeadless(true);
		expPlan.open();
		expPlan.getController().userStart();

		System.exit(0);
	}

	/**
	 * Auto launch gui experiment in headless mode from a gaml file
	 *
	 * @param experimentName
	 * @param pathToModel
	 * @throws InterruptedException
	 */
	public void runGamlSimulation(final List<String> args)
			throws IOException, GamaHeadlessException, InterruptedException {
		final String pathToModel = args.get(args.size() - 1);

		if (!GamlFileExtension.isGaml(pathToModel)) { System.exit(-1); }
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
		if (selectedJob == null) return;
		Globals.OUTPUT_PATH = args.get(args.size() - 3);

		selectedJob.setBufferedWriter(new XMLWriter(Globals.OUTPUT_PATH + "/" + Globals.OUTPUT_FILENAME + ".xml"));
		int numberOfThreads;
		if (args.contains(THREAD_PARAMETER)) {
			numberOfThreads = Integer.parseInt(after(args, THREAD_PARAMETER));
		} else {
			numberOfThreads = SimulationRuntime.DEFAULT_NB_THREADS;
		}
		processorQueue.setNumberOfThreads(numberOfThreads);
		processorQueue.execute(selectedJob);
		processorQueue.shutdown();
		while (!processorQueue.awaitTermination(100, TimeUnit.MILLISECONDS)) {}
		System.exit(0);
	}

}
