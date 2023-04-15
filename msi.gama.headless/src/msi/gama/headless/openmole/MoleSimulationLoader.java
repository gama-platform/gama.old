/*******************************************************************************************************
 *
 * MoleSimulationLoader.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.openmole;

import java.io.File;
import java.io.IOException;
import java.util.List;

import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.model.IModel;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.compilation.GamlCompilationError;

/**
 * The Class MoleSimulationLoader.
 */
public abstract class MoleSimulationLoader {

	/**
	 *
	 * @param modelPath
	 * @return a compiled model
	 * @throws IOException
	 * @throws GamaHeadlessException
	 * @deprecated use loadModel(File, List<GamlCompilationError>) instead
	 */
	@Deprecated
	public static IModel loadModel(final File modelPath) throws IOException, GamaHeadlessException {
		return loadModel(modelPath, null);
	}

	/**
	 * Load model.
	 *
	 * @param modelPath the model path
	 * @param errors the errors
	 * @return the i model
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public static IModel loadModel(final File modelPath, final List<GamlCompilationError> errors)
			throws IOException, GamaHeadlessException {
		return loadModel(modelPath, errors, null);
	}

	/**
	 * Load model.
	 *
	 * @param modelPath the model path
	 * @param errors the errors
	 * @param metadata the metadata
	 * @return the i model
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public static IModel loadModel(final File modelPath, final List<GamlCompilationError> errors,
			final GamlProperties metadata) throws IOException, GamaHeadlessException {
		return HeadlessSimulationLoader.loadModel(modelPath, errors, metadata, true);
	}

	/**
	 * New experiment.
	 *
	 * @param model the model
	 * @return the i mole experiment
	 */
	public static IMoleExperiment newExperiment(final IModel model) {
		return new MoleExperiment(model);
	}

}
