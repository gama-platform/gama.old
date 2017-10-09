package msi.gama.headless.batch.test;

import static msi.gama.lang.gaml.validation.GamlModelBuilder.compile;
import static org.eclipse.emf.common.util.URI.createFileURI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import msi.gama.headless.batch.AbstractModelLibraryRunner;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.model.IModel;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ModelDescription;

public class ModelLibraryTester extends AbstractModelLibraryRunner {

	private static ModelLibraryTester instance;

	private ModelLibraryTester() {}

	@Override
	public int start(final String pluginsFolder) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		final int[] count = { 0 };
		final int[] code = { 0 };
		Files.walk(Paths.get(pluginsFolder)).filter(isTest).forEach(p -> {
			test(pluginsFolder, count, code, p);
		});
		log("" + count[0] + " test models executed in built-in library and plugins. " + code[0]
				+ " failed or aborted tests found");
		return code[0];
	}

	public void test(final String pluginsFolder, final int[] count, final int[] code, final Path p) {
		final List<GamlCompilationError> errors = new ArrayList<>();
		log("Testing " + p.getFileName());
		final IModel model = compile(createFileURI(p.toString()), errors);
		final List<String> testExpNames = ((ModelDescription) model.getDescription()).getExperimentNames().stream()
				.filter(e -> model.getExperiment(e).isTest()).collect(Collectors.toList());
		log("Test experiment names = " + testExpNames);
		if (testExpNames.isEmpty())
			return;

		count[0]++;
		errors.stream().filter(e -> e.isError()).forEach(e -> {
			log("Error in " + e.getURI().toFileString().replace(pluginsFolder, "") + ": " + e);
			code[0]++;
		});
	}

	public static ModelLibraryTester getInstance() {
		if (instance == null)
			instance = new ModelLibraryTester();
		return instance;
	}
}
