package msi.gama.headless.batch.validation;

import static msi.gama.lang.gaml.validation.GamlModelBuilder.compile;
import static org.eclipse.emf.common.util.URI.createFileURI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import msi.gama.headless.batch.AbstractModelLibraryRunner;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gaml.compilation.GamlCompilationError;

public class ModelLibraryValidator extends AbstractModelLibraryRunner {

	private static ModelLibraryValidator instance;

	private ModelLibraryValidator() {}

	@Override
	public int start(final String pluginsFolder) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		final int[] count = { 0 };
		final int[] code = { 0 };
		Files.walk(Paths.get(pluginsFolder)).filter(isModel).forEach(p -> {
			validate(pluginsFolder, count, code, p);
		});
		log("" + count[0] + " GAMA models compiled in built-in library and plugins. " + code[0]
				+ " compilation errors found");
		return code[0];
	}

	private void validate(final String pluginsFolder, final int[] countOfModelsValidated, final int[] returnCode,
			final Path pathToModel) {
		final List<GamlCompilationError> errors = new ArrayList<>();
		log("Compiling " + pathToModel.getFileName());
		try {			
			compile(createFileURI(pathToModel.toString()), errors);
		}catch(Exception ex) {
			log(ex.getMessage());
		}
		countOfModelsValidated[0]++;
		errors.stream().filter(e -> e.isError()).forEach(e -> {
			log("Error in " + e.getURI().toFileString().replace(pluginsFolder, "") + ": " + e);
			returnCode[0]++;
		});
	}

	public static ModelLibraryValidator getInstance() {
		if (instance == null)
			instance = new ModelLibraryValidator();
		return instance;
	}
}
