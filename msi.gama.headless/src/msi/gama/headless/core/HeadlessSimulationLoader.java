/*********************************************************************************************
 *
 *
 * 'HeadlessSimulationLoader.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.core;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import org.eclipse.emf.common.util.URI;
import msi.gama.common.GamaPreferences;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.compilation.*;

public class HeadlessSimulationLoader {

	/**
	 *
	 * load in headless mode a specified model and create an experiment
	 * @param fileName model to load
	 * @param params parameters of the experiment
	 * @return the loaded experiment
	 * @throws GamaRuntimeException
	 * @throws InterruptedException
	 */
	private static void configureHeadLessSimulation() {
		System.setProperty("java.awt.headless", "true");
		GAMA.setHeadLessMode();
	}

	public static void preloadGAMA() {
		Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("GAMA configuring and loading...");
		configureHeadLessSimulation();
		GAMA.setHeadlessGui(new HeadlessListener());

		try {
			// We initialize the extensions of Gaml
			GamaBundleLoader.preBuildContributions();
			// We initialize XText and Gaml.
			GamlStandaloneSetup.doSetup();
		} catch (Exception e1) {
			throw GamaRuntimeException.create(e1, null);
		}
		Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("GAMA loading complete");

		// SEED HACK // WARNING AD : Why ?
		GamaPreferences.CORE_SEED_DEFINED.set(true);
		GamaPreferences.CORE_SEED.set(1.0);
		// SEED HACK
	}

	public static synchronized IModel loadModel(final File myFile) {
		String fileName = myFile.getAbsolutePath();
		Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer(fileName + " Model is loading...");
		try {
			List<GamlCompilationError> errors = new ArrayList();
			IModel model = GAML.getModelFactory().compile(URI.createURI("file:///" + fileName, false), errors);
			if ( model == null ) {
				System.err.println("GAMA cannot build model " + fileName);
				for ( GamlCompilationError d : errors ) {
					System.err.println(">> Error " + d.toString());
				}
			}
			Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("Experiment created ");
			return model;

		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

	}

}
