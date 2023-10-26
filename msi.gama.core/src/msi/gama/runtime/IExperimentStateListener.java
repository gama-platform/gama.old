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

		/** The paused. */
		PAUSED("STOPPED"),
		/** The finished. */
		FINISHED("FINISHED"),
		/** The running. */
		RUNNING("RUNNING"),
		/** The notready. */
		NOTREADY("NOTREADY"),
		/** The none. */
		NONE("NONE");

		/** The name. */
		String name;

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() { return name; }

		/**
		 * Instantiates a new state.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param n
		 *            the n
		 * @date 26 oct. 2023
		 */
		State(final String n) {
			name = n;
		}
	}

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
	void updateStateTo(IExperimentPlan experiment, final State state);

}