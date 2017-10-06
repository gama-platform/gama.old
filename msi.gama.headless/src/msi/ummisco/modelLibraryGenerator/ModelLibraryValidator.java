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

import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gaml.compilation.GamlCompilationError;

public class ModelLibraryValidator {
	final static int[] RETURN_CODE = { 0 };

	static final Predicate<Path> isModel = p -> {
		final String s = p.getFileName().toString();
		return s.endsWith(".gaml") || s.endsWith(".experiment");
	};

	static void log(final String s) {
		System.out.println(s);
	}

	static public int start(final String pluginsFolder) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		final List<GamlCompilationError> errors = new ArrayList<>();
		Files.walk(Paths.get(pluginsFolder)).filter(isModel).forEach(p -> compile(createFileURI(p.toString()), errors));
		errors.stream().filter(e -> e.isError()).forEach(e -> {
			log("Error in " + e.getURI().toFileString().replace(pluginsFolder, "") + ": " + e);
			RETURN_CODE[0] = 1;
		});
		return RETURN_CODE[0];
	}
}
