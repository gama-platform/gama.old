package msi.gama.headless.batch;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import utils.DEBUG;

public abstract class AbstractModelLibraryRunner {

	static {
		DEBUG.OFF();
	}

	public static void log(final String s) {
		DEBUG.OUT(s); // Use a logger ?
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
