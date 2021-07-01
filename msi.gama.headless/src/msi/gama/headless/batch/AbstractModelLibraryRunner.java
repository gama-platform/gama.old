package msi.gama.headless.batch;

import java.io.IOException;
import java.net.URL;

import com.google.inject.Injector;

import msi.gama.common.GamlFileExtension;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import ummisco.gama.dev.utils.DEBUG;

public abstract class AbstractModelLibraryRunner {
	
	protected AbstractModelLibraryRunner() {
		DEBUG.ON();
	}

	protected GamlModelBuilder createBuilder(final Injector injector) {
		final GamlModelBuilder builder = new GamlModelBuilder(injector);
		return builder;
	}

	protected boolean isModel(final URL url) {
		return isModel(url.getFile());
	}

	protected boolean isModel(final String file) {
		return GamlFileExtension.isGaml(file) || GamlFileExtension.isExperiment(file);
	}

	protected boolean isTest(final URL url) {
		final String file = url.getFile();
		return isModel(file) && (file.contains("test") || file.contains("Test"));
	}

	public abstract int start() throws IOException;
}
