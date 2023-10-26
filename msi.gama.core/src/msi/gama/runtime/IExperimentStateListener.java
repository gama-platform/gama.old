/*******************************************************************************************************
 *
 * ISimulationStateProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

/**
 * The class ISimulationStateProvider.
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface IExperimentStateListener {

	/** The Constant SIMULATION_RUNNING_STATE. */
	String EXPERIMENT_RUNNING_STATE = "ummisco.gama.ui.experiment.SimulationRunningState";

	/** The Constant SIMULATION_TYPE. */
	String EXPERIMENT_TYPE = "ummisco.gama.ui.experiment.SimulationType";

	/** The Constant SIMULATION_STEPBACK. */
	String EXPERIMENT_STEPBACK = "ummisco.gama.ui.experiment.SimulationStepBack";

	/**
	 * Change the UI state based on the state of the simulation (none, stopped, running or notready)
	 */
	void updateStateTo(final String state);

}