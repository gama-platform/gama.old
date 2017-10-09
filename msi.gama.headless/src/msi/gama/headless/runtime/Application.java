/*********************************************************************************************
 * 
 *
 * GAMA modeling and simulation platform. 'Application.java', in plugin 'msi.gama.headless', is part of the source code
 * of the (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.runtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.w3c.dom.Document;

import msi.gama.headless.batch.documentation.ModelLibraryGenerator;
import msi.gama.headless.batch.test.ModelLibraryTester;
import msi.gama.headless.batch.validation.ModelLibraryValidator;
import msi.gama.headless.common.Globals;
import msi.gama.headless.common.HeadLessErrors;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.headless.xml.ConsoleReader;
import msi.gama.headless.xml.Reader;
import msi.gama.headless.xml.XMLWriter;

public class Application implements IApplication {

	final public static String CONSOLE_PARAMETER = "-c";
	final public static String TUNNELING_PARAMETER = "-p";
	final public static String THREAD_PARAMERTER = "-hpc";
	final public static String VERBOSE_PARAMERTER = "-v";
	final public static String HELP_PARAMERTER = "-help";
	final public static String BUILD_XML_PARAMERTER = "-xml";
	final public static String CHECK_MODEL_PARAMERTER = "-check";
	final public static String VALIDATE_LIBRARY_PARAMETER = "-validate";
	final public static String TEST_LIBRARY_PARAMETER = "-test";

	public static boolean headLessSimulation = false;
	public int numberOfThread = -1;
	public boolean consoleMode = false;
	public boolean tunnelingMode = false;
	public boolean verbose = false;
	public SimulationRuntime processorQueue;

	private static String showHelp() {
		final String res = " sh ./gama-headless.sh [Options] [XML Input] [output directory]\n"
				+ "\nList of available options:" + "\n      -help     -- get the help of the command line"
				+ "\n      -m mem    -- allocate memory (ex 2048m)"
				+ "\n      -c        -- start the console to write xml parameter file"
				+ "\n      -v 		-- verbose mode"
				+ "\n      -hpc core -- set the number of core available for experimentation"
				+ "\n      -p        -- start piplines to interact with another framework" + "\n" + "\n"
				+ " sh ./gama-headless.sh -xml experimentName gamlFile xmlOutputFile\n"
				+ "\n      build an xml parameter file from a model" + "\n" + "\n";
		return res;
	}

	private static boolean containParameter(final String[] args, final String param) {
		for (final String p : args) {
			if (p.equals(param))
				return true;
		}
		return false;
	}

	private static boolean containConsoleParameter(final String[] args) {
		return containParameter(args, CONSOLE_PARAMETER);
	}

	private static boolean containCheckModelsCommandParameter(final String[] args) {
		return containParameter(args, CHECK_MODEL_PARAMERTER);
	}

	private static boolean containValidateLibraryCommandParameter(final String[] args) {
		return containParameter(args, VALIDATE_LIBRARY_PARAMETER);
	}

	private static boolean containTestLibraryCommandParameter(final String[] args) {
		return containParameter(args, TEST_LIBRARY_PARAMETER);
	}

	private static boolean containHelpParameter(final String[] args) {
		return containParameter(args, HELP_PARAMERTER);
	}

	private static boolean containXMLParameter(final String[] args) {
		return containParameter(args, BUILD_XML_PARAMERTER);
	}

	private static boolean containTunnellingParameter(final String[] args) {
		return containParameter(args, TUNNELING_PARAMETER);
	}

	private static boolean containVerboseParameter(final String[] args) {
		return containParameter(args, VERBOSE_PARAMERTER);
	}

	private static boolean containNumberOfThread(final String[] args) {
		return containParameter(args, THREAD_PARAMERTER);
	}

	private static int getNumberOfThread(final String[] args) {
		for (int n = 0; n < args.length; n++) {
			if (args[n].equals(THREAD_PARAMERTER))
				return Integer.valueOf(args[n + 1]).intValue();
		}
		return SimulationRuntime.UNDEFINED_QUEUE_SIZE;
	}

	private boolean checkParameters(final String[] args) {

		int size = args.length;
		boolean mustContainInFile = true;
		boolean mustContainOutFile = true;
		if (containConsoleParameter(args)) {
			size = size - 1;
			mustContainInFile = false;
		}
		if (containTunnellingParameter(args)) {
			size = size - 1;
			mustContainOutFile = false;
		}
		if (containNumberOfThread(args)) {
			size = size - 2;
		}
		if (containVerboseParameter(args)) {
			size = size - 1;
		}
		if (mustContainInFile && mustContainOutFile && size < 2) {
			showError(HeadLessErrors.INPUT_NOT_DEFINED, null);
			return false;
		}
		if (!mustContainInFile && mustContainOutFile && size < 1) {
			showError(HeadLessErrors.OUTPUT_NOT_DEFINED, null);
			return false;
		}

		if (mustContainOutFile) {
			final int outIndex = args.length - 1;
			Globals.OUTPUT_PATH = args[outIndex];
			Globals.IMAGES_PATH = args[outIndex] + "/snapshot";
			final File output = new File(Globals.OUTPUT_PATH);
			if (!output.exists())
				output.mkdir();
			final File images = new File(Globals.IMAGES_PATH);
			if (!images.exists())
				images.mkdir();
		}

		if (mustContainInFile) {
			final int inIndex = mustContainOutFile ? args.length - 2 : args.length - 1;
			final File input = new File(args[inIndex]);
			if (!input.exists()) {
				showError(HeadLessErrors.NOT_EXIST_FILE_ERROR, args[inIndex]);
				return false;
			}
		}
		return true;
	}

	private static boolean showError(final int errorCode, final String path) {
		SystemLogger.activeDisplay();
		System.out.println(HeadLessErrors.getError(errorCode, path));
		SystemLogger.removeDisplay();

		return false;
	}

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		SystemLogger.removeDisplay();
		final Map<String, String[]> mm = context.getArguments();
		final String[] args = mm.get("application.args");
		if (containHelpParameter(args)) {
			System.out.println(showHelp());
		} else if (containValidateLibraryCommandParameter(args)) {
			return ModelLibraryValidator.getInstance().start(args[args.length - 1]);
		} else if (containTestLibraryCommandParameter(args)) {
			return ModelLibraryTester.getInstance().start(args[args.length - 1]);
		} else if (containCheckModelsCommandParameter(args)) {
			ModelLibraryGenerator.start(this, args);
		} else if (containXMLParameter(args)) {
			buildXML(args);
		} else {
			runSimulation(args);
		}
		return null;
	}

	public void buildXML(final String arg[]) throws ParserConfigurationException, TransformerException, IOException {
		verbose = containVerboseParameter(arg);
		if (this.verbose) {
			SystemLogger.activeDisplay();
		}

		if (arg.length < 3) {
			SystemLogger.activeDisplay();
			System.out.println("Check your parameters!");
			System.out.println(showHelp());
			return;
		}
		HeadlessSimulationLoader.preloadGAMA();
		final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(arg[arg.length - 2]);
		final ArrayList<IExperimentJob> selectedJob = new ArrayList<IExperimentJob>();
		for (final IExperimentJob j : jb) {
			if (j.getExperimentName().equals(arg[arg.length - 3])) {
				selectedJob.add(j);
				break;
			}
		}

		final Document dd = ExperimentationPlanFactory.buildXmlDocument(selectedJob);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(dd);
		final File output = new File(arg[arg.length - 1]);
		final StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
		SystemLogger.activeDisplay();
		System.out.println("Parameter file saved at: " + output.getAbsolutePath());
	}

	public void buildXMLForModelLibrary(final ArrayList<File> modelPaths, final String outputPath)
			throws ParserConfigurationException, TransformerException, IOException {
		// "arg[]" are the paths to the different models
		HeadlessSimulationLoader.preloadGAMA();
		final ArrayList<IExperimentJob> selectedJob = new ArrayList<IExperimentJob>();
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
		SystemLogger.activeDisplay();
		System.out.println("Parameter file saved at: " + output.getAbsolutePath());
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

	public void runSimulation(final String args[]) throws FileNotFoundException, InterruptedException {
		if (!checkParameters(args)) {
			System.exit(-1);
		}

		verbose = containVerboseParameter(args);
		if (verbose) {
			SystemLogger.activeDisplay();
		}
		HeadlessSimulationLoader.preloadGAMA();
		this.tunnelingMode = Application.containTunnellingParameter(args);
		this.consoleMode = Application.containConsoleParameter(args);
		this.numberOfThread = Application.getNumberOfThread(args);
		processorQueue = new LocalSimulationRuntime(this.numberOfThread);

		Reader in = null;
		if (this.verbose && !this.tunnelingMode) {
			SystemLogger.activeDisplay();
		}

		if (this.consoleMode) {
			in = new Reader(ConsoleReader.readOnConsole());
		} else {
			in = new Reader(args[args.length - 2]);
		}
		in.parseXmlFile();
		this.buildAndRunSimulation(in.getSimulation());
		in.dispose();
		while (processorQueue.isPerformingSimulation()) {
			Thread.sleep(1000);
		}

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
	public void stop() {}

}
