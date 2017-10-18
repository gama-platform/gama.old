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

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.headless.batch.AbstractModelLibraryRunner;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.experiment.TestAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ModelDescription;

public class ModelLibraryTester extends AbstractModelLibraryRunner {

	private static ModelLibraryTester instance;
	final List<GamlCompilationError> errors = new ArrayList<>();
	private final static String FAILED_PARAMETER = "-failed";

	private ModelLibraryTester() {}

	@Override
	public int start(final String pluginsFolder, final List<String> args) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		final int[] count = { 0 };
		final int[] code = { 0 };
		final boolean onlyFailed = args.contains(FAILED_PARAMETER);
		final boolean oldPref = GamaPreferences.Modeling.FAILED_TESTS.getValue();
		try {
			GamaPreferences.Modeling.FAILED_TESTS.set(onlyFailed);
			Files.walk(Paths.get(pluginsFolder)).filter(isTest).forEach(p -> {
				test(pluginsFolder, count, code, p);
			});
		} finally {
			GamaPreferences.Modeling.FAILED_TESTS.set(oldPref);
			log("" + count[0] + " tests executed in built-in library and plugins. " + code[0] + " failed or aborted");
		}
		return code[0];
	}

	public void test(final String pluginsFolder, final int[] count, final int[] code, final Path p) {
		final IModel model = compile(createFileURI(p.toString()), errors);
		final List<String> testExpNames = ((ModelDescription) model.getDescription()).getExperimentNames().stream()
				.filter(e -> model.getExperiment(e).isTest()).collect(Collectors.toList());

		if (testExpNames.isEmpty())
			return;
		for (final String expName : testExpNames) {
			final IExperimentPlan exp = GAMA.addHeadlessExperiment(model, expName, new ParametersSet(), null);
			if (exp != null) {
				exp.setHeadless(true);
				exp.getController().getScheduler().paused = false;
				exp.getAgent().step(exp.getAgent().getScope());
				code[0] += ((TestAgent) exp.getAgent()).getNumberOfFailures();
				count[0] += ((TestAgent) exp.getAgent()).getTotalNumberOfTests();
			}
		}

	}

	public static ModelLibraryTester getInstance() {
		if (instance == null)
			instance = new ModelLibraryTester();
		return instance;
	}
}
