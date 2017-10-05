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
	static final Logger LOG = Logger.getLogger(ModelLibraryValidator.class.getName());

	static public void start(final String pluginsFolder) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		Files.walk(Paths.get(pluginsFolder)).filter(isModel).forEach(p -> compile(createFileURI(p.toString()), errors));
		errors.stream().filter(e -> e.isError()).forEach(e -> LOG.info(e.getURI() + ": " + e));
	}
}
