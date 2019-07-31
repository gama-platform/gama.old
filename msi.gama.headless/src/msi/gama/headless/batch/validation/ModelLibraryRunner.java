package msi.gama.headless.batch.validation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import com.google.common.collect.Multimap;
import com.google.inject.Injector;

import msi.gama.headless.batch.AbstractModelLibraryRunner;
import msi.gama.headless.core.Experiment;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.runtime.SystemLogger;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.descriptions.ModelDescription;

public class ModelLibraryRunner extends AbstractModelLibraryRunner {

	private static ModelLibraryRunner instance;

	private ModelLibraryRunner() {
		SystemLogger.activeDisplay();
	}

	@Override
	public int start(final List<String> args) throws IOException {
		Injector injector = HeadlessSimulationLoader.preloadGAMA();
		GamlModelBuilder builder = createBuilder(injector);
		final int[] count = { 0 };
		final int[] code = { 0, 0 };
		final Multimap<Bundle, String> plugins = GamaBundleLoader.getPluginsWithModels();
		List<URL> allURLs = new ArrayList<>();
		for (final Bundle bundle : plugins.keySet()) {
			for (final String entry : plugins.get(bundle)) {
				final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
				if (urls != null)
					while (urls.hasMoreElements()) {
						final URL url = urls.nextElement();
						if (isModel(url)) {
							final URL resolvedFileURL = FileLocator.toFileURL(url);
							allURLs.add(resolvedFileURL);
						}
					}
			}
		}
		builder.loadURLs(allURLs);
// allURLs.forEach(u -> validate(builder, count, code, u));
		Map<String, Exception> errors = new HashMap<>();
		allURLs.forEach(u -> validateAndRun(builder, errors, count, code, u, true, 1));

		System.out.println("" + count[0] + " GAMA models compiled in built-in library and plugins. " + code[0]
				+ " compilation errors found");

		System.out.println("========================================");
		System.out.println("========================================");
		System.out.println("==========     SUMMARY        ==========");
		System.out.println("========================================");
		System.out.println("========================================");

		errors.forEach((name, ex) -> System.out.println(name + " = " + ex.toString()));

		System.out.println("========================================");
		System.out.println("========================================");
		System.out.println("==========     SUMMARY        ==========");
		System.out.println("========================================");
		System.out.println("========================================");

//		code[1] = code[0];
//		code[0] = 0;
//		count[0] = 0;
//		final Multimap<Bundle, String> tests = GamaBundleLoader.getPluginsWithTests();
//		allURLs = new ArrayList<>();
//		for (final Bundle bundle : tests.keySet()) {
//			for (final String entry : tests.get(bundle)) {
//				final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
//				if (urls != null)
//					while (urls.hasMoreElements()) {
//						final URL url = urls.nextElement();
//						if (isModel(url)) {
//							final URL resolvedFileURL = FileLocator.toFileURL(url);
//							allURLs.add(resolvedFileURL);
//						}
//					}
//			}
//		}
//		builder.loadURLs(allURLs);
//
//		allURLs.forEach(u -> validate(builder, count, code, u));
//
//		System.out.println("" + count[0] + " GAMA tests compiled in built-in library and plugins. " + code[0]
//				+ " compilation errors found");
//
//		System.out.println(code[0] + code[1]);
		return code[0] + code[1];
	}

//	private void validate(GamlModelBuilder builder, final int[] countOfModelsValidated, final int[] returnCode,
//			final URL pathToModel) {
//		final List<GamlCompilationError> errors = new ArrayList<>();
//// log("Compiling " + pathToModel.getFile());
//		builder.compile(pathToModel, errors);
//		countOfModelsValidated[0]++;
//		errors.stream().filter(e -> e.isError()).forEach(e -> {
//// log("Error in " + e.getURI().lastSegment() + ": " + e);
//			System.out.println(
//					"Error in " + e.getURI() + ":\n " + e.toString() + " \n " + e.getStatement().toString() + "\n");
//			returnCode[0]++;
//		});
//	}

	private void validateAndRun(GamlModelBuilder builder, Map<String, Exception> executionErrors,
			final int[] countOfModelsValidated, final int[] returnCode, final URL pathToModel, final boolean expGUIOnly,
			final int nbCycles) {
		if(pathToModel.toString().contains("Database")) {
			return;
		}
		System.out.println("========================================");

		final List<GamlCompilationError> errors = new ArrayList<>();
		IModel mdl =  builder.compile(pathToModel, errors);


		countOfModelsValidated[0]++;
		errors.stream().filter(e -> e.isError()).forEach(e -> {
			System.out.println(
					"Error in " + e.getURI() + ":\n " + e.toString() + " \n " + e.getStatement().toString() + "\n");
			returnCode[0]++;
		});

		Experiment experiment = null;
		try {
			experiment = new Experiment(mdl);
		} catch (Exception ex) {
			executionErrors.put(pathToModel.getPath() + "\n", ex);
		}

		for (String expName : ((ModelDescription) mdl.getDescription()).getExperimentNames()) {
			IExperimentPlan exp = mdl.getExperiment(expName);
			if (!exp.isBatch() || !expGUIOnly) {
				System.out.println("*********** Run experiment " + exp + " from model: " + mdl.getName());
				try {
					experiment.setup(expName, 0.1);
					for (int i = 0; i < nbCycles; i++) {
						experiment.step();
						System.out.println("****** Ap step()");
					}
				}catch(msi.gama.ext.webb.WebbException ex1) {
					System.out.println("msi.gama.ext.webb.WebbException");
				}
				catch (Exception ex) {
					ex.printStackTrace();
					executionErrors.put(pathToModel.getPath() + "\n" + expName, ex);
				}
			}
		}

	}

	public static ModelLibraryRunner getInstance() {
		if (instance == null)
			instance = new ModelLibraryRunner();
		return instance;
	}
}