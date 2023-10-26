/*******************************************************************************************************
 *
 * ExperimentStateProvider.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExperimentStateListener;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class SimulationStateProvider.
 */
public class ExperimentStateProvider extends AbstractSourceProvider implements IExperimentStateListener {

	/** The Constant map. */
	private final static Map<String, String> map = new HashMap<>(3);

	@Override
	public void dispose() {}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { EXPERIMENT_RUNNING_STATE, EXPERIMENT_TYPE, EXPERIMENT_STEPBACK };
	}

	/** The current simulation running state. */
	String currentState;

	/** The current simulation type. */
	String currentType;

	/** The current simulation step back. */
	String currentStepBack;

	@Override
	public Map<String, String> getCurrentState() {
		if (currentState == null) { updateStateTo(GAMA.getGui().getExperimentState()); }
		return map;
	}

	/**
	 * Change the UI state based on the state of the experiment (see variables STATE_XXX in iGui)
	 */
	@Override
	public void updateStateTo(final String state) {
		if (!Objects.equals(currentState, state)) {
			currentState = state;
			WorkbenchHelper.run(() -> fireSourceChanged(ISources.WORKBENCH, EXPERIMENT_RUNNING_STATE, state));
		}
		final IExperimentPlan exp = GAMA.getExperiment();
		String simulationType = exp == null ? IExperimentStateListener.TYPE_NONE
				: exp.isBatch() ? IExperimentStateListener.TYPE_BATCH
				: exp.isMemorize() ? IExperimentStateListener.TYPE_MEMORIZE : IExperimentStateListener.TYPE_REGULAR;
		if (!Objects.equals(currentType, simulationType)) {
			currentType = simulationType;
			WorkbenchHelper.run(() -> fireSourceChanged(ISources.WORKBENCH, EXPERIMENT_TYPE, currentType));
		}
		String canStepBack = exp != null && exp.getAgent() != null && exp.getAgent().canStepBack() ? CAN_STEP_BACK
				: CANNOT_STEP_BACK;
		if (!Objects.equals(currentStepBack, canStepBack)) {
			currentStepBack = canStepBack;
			WorkbenchHelper.run(() -> fireSourceChanged(ISources.WORKBENCH, EXPERIMENT_STEPBACK, currentStepBack));
		}
		map.put(EXPERIMENT_RUNNING_STATE, currentState);
		map.put(EXPERIMENT_TYPE, currentType);
		map.put(EXPERIMENT_STEPBACK, currentStepBack);
	}

}