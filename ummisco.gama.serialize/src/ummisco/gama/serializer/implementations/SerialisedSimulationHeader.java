/*******************************************************************************************************
 *
 * SerialisedSimulationHeader.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import msi.gama.kernel.simulation.SimulationAgent;

/**
 * Captures the essential information used for saving and restoring a simulation
 */
public class SerialisedSimulationHeader {

	/** The path to model. */
	final String pathToModel;
	/** The name of experiment. */
	final String nameOfExperiment;

	/**
	 * Instantiates a new serialised simulation header.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 8 août 2023
	 */
	public SerialisedSimulationHeader(final SimulationAgent sim) {
		this(sim.getExperiment().getModel().getFilePath(), sim.getExperiment().getName());
	}

	/**
	 * Instantiates a new serialised simulation header.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param filePath
	 *            the file path
	 * @param name
	 *            the name
	 * @date 8 août 2023
	 */
	public SerialisedSimulationHeader(final String filePath, final String name) {
		pathToModel = filePath;
		nameOfExperiment = name;
	}

}
