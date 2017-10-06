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

	static final Predicate<Path> isModel = p -> {
		final String s = p.getFileName().toString();
		return s.endsWith(".gaml") || s.endsWith(".experiment");
	};

	static void log(final String s) {
		System.out.println(s);
	}

	static public int start(final String pluginsFolder) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		final int[] count = { 0 };
		final int[] code = { 0 };
		Files.walk(Paths.get(pluginsFolder)).filter(isModel).forEach(p -> {
			final List<GamlCompilationError> errors = new ArrayList<>();
			compile(createFileURI(p.toString()), errors);
			count[0]++;
			errors.stream().filter(e -> e.isError()).forEach(e -> {
				log("Error in " + e.getURI().toFileString().replace(pluginsFolder, "") + ": " + e);
				code[0]++;
			});
		});
		log("" + count[0] + " GAMA models validated in built-in library and plugins.");
		return code[0];
	}
}
