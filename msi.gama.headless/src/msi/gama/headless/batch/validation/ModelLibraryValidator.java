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
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import ummisco.gama.dev.utils.DEBUG;

public class ModelLibraryValidator extends AbstractModelLibraryRunner {

	private static ModelLibraryValidator instance;

	private ModelLibraryValidator() {
		super();
	}

	@Override
	public int start() throws IOException {
		final Injector injector = HeadlessSimulationLoader.getInjector();
		final GamlModelBuilder builder = createBuilder(injector);
		final int[] count = { 0 };
		final int[] code = { 0, 0 };

		this.validatePluginsFromURLs(GamaBundleLoader.getPluginsWithModels(), builder, count, code);

		DEBUG.OUT("" + count[0] + " GAMA models compiled in built-in library and plugins. " + code[0]
				+ " compilation errors found");

		code[1] = code[0];
		code[0] = 0;
		count[0] = 0;
		
		this.validatePluginsFromURLs(GamaBundleLoader.getPluginsWithTests(), builder, count, code);

		DEBUG.OUT("" + count[0] + " GAMA tests compiled in built-in library and plugins. " + code[0]
				+ " compilation errors found");
		DEBUG.OUT(code[0] + code[1]);
		return code[0] + code[1];
	}
	
	private void validatePluginsFromURLs(final Multimap<Bundle, String> pluginsURLs, final GamlModelBuilder builder, 
			final int[] count, final int[] code) throws IOException {
		List<URL> allURLs = new ArrayList<>();
		for (final Bundle bundle : pluginsURLs.keySet()) {
			for (final String entry : pluginsURLs.get(bundle)) {
				final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
				if (urls != null) {
					while (urls.hasMoreElements()) {
						final URL url = urls.nextElement();
						if (isModel(url)) {
							final URL resolvedFileURL = FileLocator.toFileURL(url);
							allURLs.add(resolvedFileURL);
						}
					}
				}
			}
		}
		builder.loadURLs(allURLs);
		allURLs.forEach(u -> validate(builder, count, code, u));
	}

	private void validate(final GamlModelBuilder builder, final int[] countOfModelsValidated, final int[] returnCode,
			final URL pathToModel) {
		final List<GamlCompilationError> errors = new ArrayList<>();
		// log("Compiling " + pathToModel.getFile());
		builder.compile(pathToModel, errors);
		countOfModelsValidated[0]++;
		errors.stream().filter(e -> e.isError()).forEach(e -> {
			// log("Error in " + e.getURI().lastSegment() + ": " + e);
			DEBUG.OUT("Error in " + e.getURI() + ":\n " + e.toString() + " \n " + e.getStatement().toString() + "\n");
			returnCode[0]++;
		});
	}

	public static ModelLibraryValidator getInstance() {
		if (instance == null) { instance = new ModelLibraryValidator(); }
		return instance;
	}
}
