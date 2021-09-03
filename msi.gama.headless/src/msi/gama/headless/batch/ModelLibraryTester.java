package msi.gama.headless.batch;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import com.google.common.collect.Multimap;
import com.google.inject.Injector;

import msi.gama.common.preferences.GamaPreferences;
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
import msi.gaml.statements.test.TestState;
import ummisco.gama.dev.utils.DEBUG;

public class ModelLibraryTester extends AbstractModelLibraryRunner {

	private static ModelLibraryTester instance;

	PrintStream original;
	PrintStream nullStream;

	private ModelLibraryTester() {
		DEBUG.ON();
	}
	

	@Override
	public int start() throws IOException {
		final Injector injector = HeadlessSimulationLoader.getInjector();
		final GamlModelBuilder builder = createBuilder(injector);

		original = System.out;
		nullStream = new PrintStream(new OutputStream() {
			@Override
			public void write(final int b) {
				// DO NOTHING
			}
		});
		final int[] count = { 0 };
		final int[] code = { 0 };
		final Multimap<Bundle, String> plugins = GamaBundleLoader.getPluginsWithTests();
		final List<URL> allURLs = new ArrayList<>();
		for (final Bundle bundle : plugins.keySet()) {
			for (final String entry : plugins.get(bundle)) {
				final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
				if (urls != null) {
					while (urls.hasMoreElements()) {
						final URL url = urls.nextElement();
						if (isTest(url)) {
							final URL resolvedFileURL = FileLocator.toFileURL(url);
							allURLs.add(resolvedFileURL);
						}
					}
				}
			}
		}
		builder.loadURLs(allURLs);

		allURLs.forEach(u -> test(builder, count, code, u));

		DEBUG.OUT("" + count[0] + " tests executed in built-in library and plugins. " + code[0] + " failed or aborted");
		DEBUG.OUT(code[0]);
		return code[0];
	}

	public void test(final GamlModelBuilder builder, final int[] count, final int[] code, final URL p) {
		// DEBUG.OUT(p);
		final List<GamlCompilationError> errors = new ArrayList<>();
		try {
			final IModel model = builder.compile(p, errors);
			if (model == null || model.getDescription() == null) return;
			final List<String> testExpNames = ((ModelDescription) model.getDescription()).getExperimentNames().stream()
					.filter(e -> model.getExperiment(e).isTest()).collect(Collectors.toList());

			if (testExpNames.isEmpty()) return;
			for (final String expName : testExpNames) {
				final IExperimentPlan exp = GAMA.addHeadlessExperiment(model, expName, new ParametersSet(), null);
				if (exp != null) {
					System.setOut(nullStream);
					final TestAgent agent = (TestAgent) exp.getAgent();
					exp.setHeadless(true);
					exp.getController().getScheduler().paused = false;
					exp.getAgent().step(agent.getScope());
					code[0] += agent.getSummary().countTestsWith(TestState.FAILED);
					code[0] += agent.getSummary().countTestsWith(TestState.ABORTED);
					count[0] += agent.getSummary().size();

					System.setOut(original);
					if (agent.getSummary().countTestsWith(TestState.FAILED) > 0
							|| agent.getSummary().countTestsWith(TestState.ABORTED) > 0) {

						DEBUG.OUT(agent.getSummary().toString());
					}
				}
			}
		} catch (final Exception ex) {
			DEBUG.OUT(ex.getMessage());
		}

	}

	public static ModelLibraryTester getInstance() {
		if (instance == null) { instance = new ModelLibraryTester(); }
		return instance;
	}
}
