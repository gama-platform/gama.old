package msi.ummisco.modelLibraryGenerator;

import static msi.gama.lang.gaml.validation.GamlModelBuilder.compile;
import static org.eclipse.emf.common.util.URI.createFileURI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gaml.compilation.GamlCompilationError;

public class ModelLibraryValidator {

	static final Predicate<Path> isModel = p -> p.endsWith(".gaml") || p.endsWith(".experiment");
	static final List<GamlCompilationError> errors = new ArrayList<>();

	static void log(final String s) {
		Logger.getLogger(ModelLibraryValidator.class.getName()).info("VALIDATION: " + s);
	}

	static public void start(final String pluginsFolder) throws IOException {
		log("Starting validation");
		HeadlessSimulationLoader.preloadGAMA();
		log("GAMA loaded");
		Files.walk(Paths.get(pluginsFolder)).filter(isModel).forEach(p -> compile(createFileURI(p.toString()), errors));
		log("All models validated");
		errors.stream().filter(e -> e.isError()).forEach(e -> log(e.getURI() + ": " + e));
	}
}
