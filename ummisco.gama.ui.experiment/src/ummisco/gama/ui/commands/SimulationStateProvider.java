/*******************************************************************************************************
 *
 * SimulationStateProvider.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import msi.gama.common.interfaces.IGui;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.ISimulationStateProvider;

/**
 * The Class SimulationStateProvider.
 */
public class SimulationStateProvider extends AbstractSourceProvider implements ISimulationStateProvider {

	/** The Constant SIMULATION_RUNNING_STATE. */
	public final static String SIMULATION_RUNNING_STATE = "ummisco.gama.ui.experiment.SimulationRunningState";

	/** The Constant SIMULATION_TYPE. */
	public final static String SIMULATION_TYPE = "ummisco.gama.ui.experiment.SimulationType";

	/** The Constant SIMULATION_STEPBACK. */
	public final static String SIMULATION_STEPBACK = "ummisco.gama.ui.experiment.SimulationStepBack";

	/** The Constant map. */
	private final static Map<String, String> map = new HashMap<>(3);

	@Override
	public void dispose() {}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SIMULATION_RUNNING_STATE, SIMULATION_TYPE, SIMULATION_STEPBACK };
	}

	@Override
	public Map<String, String> getCurrentState() {
		final String state = GAMA.getGui().getExperimentState();
		final IExperimentPlan exp = GAMA.getExperiment();
		final String type =
				exp == null ? IGui.NONE : exp.isBatch() ? "BATCH" : exp.isMemorize() ? "MEMORIZE" : "REGULAR";

		String canStepBack = "CANNOT_STEP_BACK";
		if ((exp != null) && (exp.getAgent() != null)) {
			canStepBack = exp.getAgent().canStepBack() ? "CAN_STEP_BACK" : "CANNOT_STEP_BACK";
		}

		map.put(SIMULATION_RUNNING_STATE, state);
		map.put(SIMULATION_TYPE, type);
		map.put(SIMULATION_STEPBACK, canStepBack);
		return map;
	}

	/**
	 * Change the UI state based on the state of the simulation (none, stopped, running or notready)
	 */
	@Override
	public void updateStateTo(final String state) {
		fireSourceChanged(ISources.WORKBENCH, SIMULATION_RUNNING_STATE, state);
		final IExperimentPlan exp = GAMA.getExperiment();
		final String type = exp == null ? "NONE" : exp.isBatch() ? "BATCH" : exp.isMemorize() ? "MEMORIZE" : "REGULAR";
		fireSourceChanged(ISources.WORKBENCH, SIMULATION_TYPE, type);

		String canStepBack = "CANNOT_STEP_BACK";

		if ((exp != null) && (exp.getAgent() != null)) {
			canStepBack = exp.getAgent().canStepBack() ? "CAN_STEP_BACK" : "CANNOT_STEP_BACK";
		}

		fireSourceChanged(ISources.WORKBENCH, SIMULATION_STEPBACK, canStepBack);

	}

}