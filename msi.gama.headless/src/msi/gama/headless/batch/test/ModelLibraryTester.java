package msi.gama.headless.batch.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import com.google.common.collect.Multimap;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.headless.batch.AbstractModelLibraryRunner;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.experiment.TestAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.descriptions.ModelDescription;

public class ModelLibraryTester extends AbstractModelLibraryRunner {

	private static ModelLibraryTester instance;
	final List<GamlCompilationError> errors = new ArrayList<>();
	private final static String FAILED_PARAMETER = "-failed";

	private ModelLibraryTester() {}

	@Override
	public int start(final List<String> args) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		final int[] count = { 0 };
		final int[] code = { 0 };
		final boolean onlyFailed = args.contains(FAILED_PARAMETER);
		final boolean oldPref = GamaPreferences.Modeling.FAILED_TESTS.getValue();
		try {
			GamaPreferences.Modeling.FAILED_TESTS.set(onlyFailed);
			final Multimap<Bundle, String> plugins = GamaBundleLoader.getPluginsWithTests();
			for (final Bundle bundle : plugins.keySet()) {
				for (final String entry : plugins.get(bundle)) {
					final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
					if (urls != null)
						while (urls.hasMoreElements()) {
							final URL url = urls.nextElement();
							if (isTest(url)) {
								final URL resolvedFileURL = FileLocator.toFileURL(url);
								test(count, code, resolvedFileURL);
							}
						}
				}
			}

		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			GamaPreferences.Modeling.FAILED_TESTS.set(oldPref);
			log("" + count[0] + " tests executed in built-in library and plugins. " + code[0] + " failed or aborted");
		}
		return code[0];
	}

	public void test(final int[] count, final int[] code, final URL p) throws URISyntaxException {
		final IModel model = GamlModelBuilder.compile(p, errors);
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
