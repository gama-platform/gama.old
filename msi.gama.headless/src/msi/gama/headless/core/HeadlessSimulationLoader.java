/*********************************************************************************************
 *
 *
 * 'HeadlessSimulationLoader.java', in plugin 'msi.gama.headless', is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;

import com.google.inject.Injector;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamlCompilationError;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;

public class HeadlessSimulationLoader {

	static {
		DEBUG.ON();
	}

	// The injector to use in headless mode
	Injector injector;

	private static HeadlessSimulationLoader INSTANCE = new HeadlessSimulationLoader();

	// Singleton
	private HeadlessSimulationLoader() {}

	public static Injector getInjector() {
		return INSTANCE.configureInjector();
	}

	public static void preloadGAMA(Injector inj) {
		INSTANCE.configureInjector(inj);
	}
	
	public static void preloadGAMA() {
		INSTANCE.configureInjector();
	}
	
	private Injector configureInjector() { 
		try {
			// We initialize XText and Gaml.
			Injector inj = GamlStandaloneSetup.doSetup();
			return this.configureInjector(inj);
		} catch (final Exception e1) {
			throw GamaRuntimeException.create(e1, GAMA.getRuntimeScope());
		}
	}

	private Injector configureInjector(Injector inj) {
		if (injector != null) return injector;
		DEBUG.LOG("GAMA configuring and loading...");
		System.setProperty("java.awt.headless", "true");
		GAMA.setHeadLessMode();
		injector = inj;

		// SEED HACK // WARNING AD : Why ?
		GamaPreferences.External.CORE_SEED_DEFINED.set(true);
		GamaPreferences.External.CORE_SEED.set(1.0);
		// SEED HACK
		return injector;
	}

	/**
	 * Compiles a file to a GAMA model ready to be experimented
	 *
	 * @param myFile
	 *            the main model file
	 * @return a compiled model
	 * @throws IOException
	 *             in case the file is null or not found
	 * @throws GamaHeadlessException
	 *             in case the compilation ends in error
	 * @deprecated use loadModel(File, List<GamlCompilationError>) instead
	 */
	@Deprecated
	public static synchronized IModel loadModel(final File myFile) throws IOException, GamaHeadlessException {
		return loadModel(myFile, null);
	}

	/**
	 * Compiles a file to a GAMA model ready to be experimented
	 *
	 * @param myFile
	 *            the main model file
	 * @param errors
	 *            a list that will be filled with compilation errors / warnings (can be null)
	 * @return a compiled model
	 * @throws IOException
	 *             in case the file is null or not found
	 * @throws GamaHeadlessException
	 *             in case the compilation ends in error
	 */
	public static synchronized IModel loadModel(final File myFile, final List<GamlCompilationError> errors)
			throws IOException, GamaHeadlessException {
		return loadModel(myFile, errors, null, true);
	}

	/**
	 * Compiles a file to a GAMA model ready to be experimented
	 *
	 * @param myFile
	 *            the main model file
	 * @param errors
	 *            a list that will be filled with compilation errors / warnings (can be null)
	 * @param metaProperties
	 *            an instance of GamlProperties that will be filled with the sylmbolic names of bundles required to run
	 *            the model (can be null) and other informations (skills, operators, statements, ...).
	 * @return a compiled model
	 * @throws IOException
	 *             in case the file is null or not found
	 * @throws GamaHeadlessException
	 *             in case the compilation ends in error
	 */
	public static synchronized IModel loadModel(final File myFile, final List<GamlCompilationError> errors,
			final GamlProperties metaProperties, final boolean initHeadless) throws IOException, GamaHeadlessException {
		if (initHeadless) {
			preloadGAMA(); // make sure the injector is created.
		}
		if (myFile == null) throw new IOException("Model file is null");
		final String fileName = myFile.getAbsolutePath();
		if (!myFile.exists()) throw new IOException("Model file does not exist: " + fileName);
		DEBUG.LOG(fileName + " model is being compiled...");

		final IModel model = GamlModelBuilder.getDefaultInstance().compile(URI.createFileURI(fileName), errors);
		if (model == null) {
			DEBUG.LOG("Model compiled with following indications: \n"
					+ (errors == null ? "" : StreamEx.of(errors).joining("\n")));
			throw new GamaHeadlessException(
					"Model cannot be compiled. See list of attached errors \n" + StreamEx.of(errors).joining("\n"));
		}
		// if (metaProperties != null)
		// model.getDescription().collectMetaInformation(metaProperties);
		return model;
	}

}
