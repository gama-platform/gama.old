package msi.gama.headless.batch;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.inject.Injector;

import msi.gama.lang.gaml.validation.GamlModelBuilder;
import ummisco.gama.dev.utils.DEBUG;

public abstract class AbstractModelLibraryRunner {

	protected GamlModelBuilder createBuilder(Injector injector) {
		GamlModelBuilder builder = new GamlModelBuilder(injector);
		return builder;
	}

	static {
		DEBUG.OFF();
	}

	public static void log(final String s) {
		DEBUG.LOG(s); // Use a logger ?
	}

	protected boolean isModel(final URL url) {
		final String file = url.getFile();
		return file.endsWith(".gaml") || file.endsWith(".experiment");
	}

	protected boolean isTest(final URL url) {
		return isModel(url) && url.toString().contains("tests");
	}

	public abstract int start(List<String> args) throws IOException;
}
