/*******************************************************************************************************
 *
 * IExperimentStateListener.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import msi.gama.kernel.experiment.IExperimentPlan;

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

	/** The paused. */
	String STATE_PAUSED = "STOPPED";

	/** The finished. */
	String STATE_FINISHED = "FINISHED";

	/** The running. */
	String STATE_RUNNING = "RUNNING";

	/** The notready. */
	String STATE_NOTREADY = "NOTREADY";

	/** The none. */
	String STATE_NONE = "NONE";

	/** The none. */
	String TYPE_NONE = "NONE";

	/** The batch. */
	String TYPE_BATCH = "BATCH";

	/** The memorize. */
	String TYPE_MEMORIZE = "MEMORIZE";

	/** The regular. */
	String TYPE_REGULAR = "REGULAR";

	/** The Constant CANNOT_STEP_BACK. */
	String CANNOT_STEP_BACK = "CANNOT_STEP_BACK";

	/** The Constant CAN_STEP_BACK. */
	String CAN_STEP_BACK = "CAN_STEP_BACK";

	/**
	 * Change the UI state based on the state of the simulation (none, stopped, running or notready)
	 */
	void updateStateTo(IExperimentPlan experiment, final String state);

}