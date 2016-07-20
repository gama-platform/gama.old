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
import java.io.IOException;
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

		// SEED HACK // WARNING AD : Why ?
		GamaPreferences.CORE_SEED_DEFINED.set(true);
		GamaPreferences.CORE_SEED.set(1.0);
		// SEED HACK
	}

	public static synchronized IModel loadModel(final File myFile) throws IOException  {
		String fileName = myFile.getAbsolutePath();
		if(!myFile.exists())
			throw new IOException("Model file does not exist: "+fileName );
		
		Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer(fileName + " Model is loading...");
		try {
			List<GamlCompilationError> errors = new ArrayList();
			IModel model = GAML.getModelFactory().compile(URI.createFileURI(fileName), errors);
			if ( model == null ) {
				{
					String errorData ="\n";
					for(GamlCompilationError line:errors)
						errorData +=line.toString()+"\n";
					throw new GamaHeadlessException("Compilation errors: "+errorData);
				}
				
			}
			Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("Experiment created ");
			return model;

		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

	}

}
