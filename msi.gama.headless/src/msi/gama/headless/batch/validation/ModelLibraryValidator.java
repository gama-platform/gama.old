package msi.gama.headless.batch.validation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import com.google.common.collect.Multimap;
import com.google.inject.Injector;

import msi.gama.headless.batch.AbstractModelLibraryRunner;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.runtime.SystemLogger;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;

public class ModelLibraryValidator extends AbstractModelLibraryRunner {

	private static ModelLibraryValidator instance;

	private ModelLibraryValidator() {
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
		allURLs.forEach(u -> validate(builder, count, code, u));

		System.out.println("" + count[0] + " GAMA models compiled in built-in library and plugins. " + code[0]
				+ " compilation errors found");

		code[1] = code[0];
		code[0] = 0;
		count[0] = 0;
		final Multimap<Bundle, String> tests = GamaBundleLoader.getPluginsWithTests();
		allURLs = new ArrayList<>();
		for (final Bundle bundle : tests.keySet()) {
			for (final String entry : tests.get(bundle)) {
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

		allURLs.forEach(u -> validate(builder, count, code, u));

		System.out.println("" + count[0] + " GAMA tests compiled in built-in library and plugins. " + code[0]
				+ " compilation errors found");
		System.out.println(code[0] + code[1]);
		return code[0] + code[1];
	}

	private void validate(GamlModelBuilder builder, final int[] countOfModelsValidated, final int[] returnCode,
			final URL pathToModel) {
		final List<GamlCompilationError> errors = new ArrayList<>();
		// log("Compiling " + pathToModel.getFile());
		builder.compile(pathToModel, errors);
		countOfModelsValidated[0]++;
		errors.stream().filter(e -> e.isError()).forEach(e -> {
			// log("Error in " + e.getURI().lastSegment() + ": " + e);
			System.out.println(
					"Error in " + e.getURI() + ":\n " + e.toString() + " \n " + e.getStatement().toString() + "\n");
			returnCode[0]++;
		});
	}

	public static ModelLibraryValidator getInstance() {
		if (instance == null)
			instance = new ModelLibraryValidator();
		return instance;
	}
}
