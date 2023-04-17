/*******************************************************************************************************
 *
 * AbstractModelLibraryRunner.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.batch;

import java.io.IOException;
import java.net.URL;

import com.google.inject.Injector;

import msi.gama.common.GamlFileExtension;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class AbstractModelLibraryRunner.
 */
public abstract class AbstractModelLibraryRunner {

	/**
	 * Creates the builder.
	 *
	 * @param injector the injector
	 * @return the gaml model builder
	 */
	protected GamlModelBuilder createBuilder(final Injector injector) {
		return new GamlModelBuilder(injector);
	}

	/**
	 * Checks if is model.
	 *
	 * @param url the url
	 * @return true, if is model
	 */
	protected boolean isModel(final URL url) {
		return isModel(url.getFile());
	}

	/**
	 * Checks if is model.
	 *
	 * @param file the file
	 * @return true, if is model
	 */
	protected boolean isModel(final String file) {
		return GamlFileExtension.isGaml(file) || GamlFileExtension.isExperiment(file);
	}

	/**
	 * Checks if is test.
	 *
	 * @param url the url
	 * @return true, if is test
	 */
	protected boolean isTest(final URL url) {
		final String file = url.getFile();
		return isModel(file) && (file.contains("test") || file.contains("Test"));
	}

	/**
	 * Start.
	 *
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract int start() throws IOException;
}
