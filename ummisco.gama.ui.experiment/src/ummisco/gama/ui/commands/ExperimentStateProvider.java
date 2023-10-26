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

import static java.util.Map.of;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.IExperimentStateListener;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class SimulationStateProvider.
 */
public class ExperimentStateProvider extends AbstractSourceProvider implements IExperimentStateListener {

	/** The Constant SOURCE_NAMES. */
	final static String[] SOURCE_NAMES = { EXPERIMENT_RUNNING_STATE, EXPERIMENT_TYPE, EXPERIMENT_STEPBACK };

	/** The Constant map. */
	private final Map<String, String> states = new HashMap<>(of(EXPERIMENT_RUNNING_STATE, STATE_NONE, EXPERIMENT_TYPE,
			TYPE_NONE, EXPERIMENT_STEPBACK, CANNOT_STEP_BACK));

	@Override
	public void dispose() {}

	@Override
	public String[] getProvidedSourceNames() { return SOURCE_NAMES; }

	/**
	 * FALSE: should target the experiment ?
	 */
	@Override
	public Map<String, String> getCurrentState() { return states; }

	/**
	 * Change the UI state based on the state of the experiment (see variables STATE_XXX in iGui)
	 */
	@Override
	public void updateStateTo(final IExperimentPlan exp, final String state) {
		if (!Objects.equals(states.get(EXPERIMENT_RUNNING_STATE), state)) {
			states.put(EXPERIMENT_RUNNING_STATE, state);
			WorkbenchHelper.run(() -> fireSourceChanged(ISources.WORKBENCH, EXPERIMENT_RUNNING_STATE, state));
		}
		String simulationType =
				exp == null ? TYPE_NONE : exp.isBatch() ? TYPE_BATCH : exp.isMemorize() ? TYPE_MEMORIZE : TYPE_REGULAR;
		if (!Objects.equals(states.get(EXPERIMENT_TYPE), simulationType)) {
			states.put(EXPERIMENT_TYPE, simulationType);
			WorkbenchHelper.run(() -> fireSourceChanged(ISources.WORKBENCH, EXPERIMENT_TYPE, simulationType));
		}
		String canStepBack = exp != null && exp.getAgent() != null && exp.getAgent().canStepBack() ? CAN_STEP_BACK
				: CANNOT_STEP_BACK;
		if (!Objects.equals(states.get(EXPERIMENT_STEPBACK), canStepBack)) {
			states.put(EXPERIMENT_STEPBACK, canStepBack);
			WorkbenchHelper.run(() -> fireSourceChanged(ISources.WORKBENCH, EXPERIMENT_STEPBACK, canStepBack));
		}
	}

}