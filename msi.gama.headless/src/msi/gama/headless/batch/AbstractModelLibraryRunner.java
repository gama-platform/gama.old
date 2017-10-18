package msi.gama.headless.batch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractModelLibraryRunner {

	public static final Predicate<Path> isModel = p -> {
		final String s = p.getFileName().toString();
		return s.endsWith(".gaml") || s.endsWith(".experiment");
	};

	public static final Predicate<Path> isInTests = p -> {
		return p.toString().contains("tests");
	};

	public static final Predicate<Path> isTest = isModel.and(isInTests);

	public static void log(final String s) {
		System.out.println(s); // Use a logger ?
	}

	public abstract int start(final String pluginsFolder, List<String> args) throws IOException;
}
