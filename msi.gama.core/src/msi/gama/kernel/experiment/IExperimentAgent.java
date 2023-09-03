/*******************************************************************************************************
 *
 * IExperimentAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.Collection;
import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.population.IPopulationFactory;

/**
 * The Interface IExperimentAgent.
 */
public interface IExperimentAgent extends ITopLevelAgent {

	/**
	 * Gets the species.
	 *
	 * @return the species
	 */
	@Override
	IExperimentPlan getSpecies();

	/**
	 * Gets the working path.
	 *
	 * @return the working path
	 */
	String getWorkingPath();

	/**
	 * Gets the working paths.
	 *
	 * @return the working paths
	 */
	List<String> getWorkingPaths();
	//
	// /**
	// * Gets the warnings as errors. Deprecated
	// *
	// * @return the warnings as errors
	// */
	// Boolean getWarningsAsErrors();

	/**
	 * Gets the minimum duration.
	 *
	 * @return the minimum duration
	 */
	Double getMinimumDuration();

	/**
	 * Sets the minimum duration.
	 *
	 * @param d
	 *            the new minimum duration
	 */
	void setMinimumDuration(Double d);

	/**
	 * Close simulations.
	 */
	void closeSimulations();

	/**
	 * Close simulation.
	 *
	 * @param simulationAgent
	 *            the simulation agent
	 */
	void closeSimulation(SimulationAgent simulationAgent);

	/**
	 * Gets the simulation population.
	 *
	 * @return the simulation population
	 */
	SimulationPopulation getSimulationPopulation();

	/**
	 * Can step back.
	 *
	 * @return true, if successful
	 */
	boolean canStepBack();

	/**
	 * Inform status.
	 */
	void informStatus();

	/**
	 * Checks if is headless.
	 *
	 * @return true, if is headless
	 */
	boolean isHeadless();

	/**
	 * Returns the population factory of this type of experiment -- default is a DefaultPopulationFactory
	 *
	 * @return the population factory
	 */
	IPopulationFactory getPopulationFactory();

	/**
	 * Sets the maximum duration.
	 *
	 * @param d
	 *            the new maximum duration
	 */
	void setMaximumDuration(Double d);

	/**
	 * Gets the maximum duration.
	 *
	 * @return the maximum duration
	 */
	Double getMaximumDuration();

	/**
	 * Checks if is record.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is record
	 * @date 9 août 2023
	 */
	default boolean isRecord() { return getSpecies().isMemorize(); }

	/**
	 * Sets the current simulation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param simulationAgent
	 *            the new current simulation
	 * @date 11 août 2023
	 */
	default void setCurrentSimulation(final SimulationAgent simulationAgent) {
		SimulationPopulation pop = getSimulationPopulation();
		if (pop != null) { pop.setCurrentSimulation(simulationAgent); }
	}

	/**
	 * Checks for parameters or user commands.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if successful
	 * @date 14 août 2023
	 */
	default boolean hasParametersOrUserCommands() {
		return getSpecies().hasParametersOrUserCommands();
	}

	/**
	 * Gets the displayables.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the displayables
	 * @date 14 août 2023
	 */
	default List<IExperimentDisplayable> getDisplayables() { return getSpecies().getDisplayables(); }

	/**
	 * Gets the user commands.
	 *
	 * @return the user commands
	 */
	default Collection<? extends IExperimentDisplayable> getUserCommands() { return getSpecies().getUserCommands(); }

}
