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

	/**
	 * The Enum State.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 26 oct. 2023
	 */
	public enum State {
		/** The PAUSED state. The experiment is open and has been paused. */
		PAUSED,
		/** The FINISHED state. The experiment is finished but not closed. Used at the end of batch experiments */
		FINISHED,
		/** The RUNNING state. The experiment is open and not paused. */
		RUNNING,
		/**
		 * The NOTREADY state. The experiment has been opened but is still initializing. If it is set to autorun, the
		 * next state becomes RUNNING, otherwise PAUSED.
		 */
		NOTREADY,
		/** The NONE state. The experiment has not been launched yet or is already closed. */
		NONE;
	}

	/**
	 * The Enum Type. Used to modify the UI depending on what type of experiment is displayed.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 3 nov. 2023
	 */
	public enum Type {

		/**
		 * The NONE type. This type of experiment is unknown -- indicates that the experiment is not launched yet or
		 * already closed
		 */
		NONE,

		/**
		 * The BATCH type. This experiment automatically runs multiple simulations for exploring scenarios or optimizing
		 * parameters
		 */
		BATCH,

		/** The RECORD type. This experiment can record its states and play them back. */
		RECORD,

		/** The REGULAR type. The classical experiment type. */
		REGULAR
	}

	/** The Constant CANNOT_STEP_BACK. */
	String CANNOT_STEP_BACK = "CANNOT_STEP_BACK";

	/** The Constant CAN_STEP_BACK. */
	String CAN_STEP_BACK = "CAN_STEP_BACK";

	/**
	 * Change the UI state based on the state of the simulation (NONE, PAUSED, RUNNING, FINISHED or NOTREADY)
	 */
	void updateStateTo(IExperimentPlan experiment, final State state);

}