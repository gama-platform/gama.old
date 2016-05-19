/*********************************************************************************************
 *
 *
 * 'SimulationStateProvider.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.commands;

import java.util.*;
import org.eclipse.ui.*;
import org.eclipse.ui.services.IServiceLocator;
import msi.gama.common.interfaces.IGui;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.*;

public class SimulationStateProvider extends AbstractSourceProvider implements ISimulationStateProvider {

	public final static String SIMULATION_RUNNING_STATE = "msi.gama.application.commands.SimulationRunningState";
	public final static String SIMULATION_TYPE = "msi.gama.application.commands.SimulationType";

	private final static Map<String, String> map = new HashMap<>(2);

	@Override
	public void dispose() {}

	@Override
	public void initialize(final IServiceLocator locator) {
		SwtGui.state = this;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SIMULATION_RUNNING_STATE, SIMULATION_TYPE };
	}

	@Override
	public Map<String, String> getCurrentState() {
		String state = IGui.NONE;
		IExperimentPlan exp = GAMA.getExperiment();
		if ( exp != null ) {
			state = exp.getExperimentScope().getGui().getFrontmostSimulationState();
		}
		String type = exp == null ? "NONE" : exp.isBatch() ? "BATCH" : (exp.isMemorize() ? "MEMORIZE" : "REGULAR");
		map.put(SIMULATION_RUNNING_STATE, state);
		map.put(SIMULATION_TYPE, type);

		System.out.println("state " + state + " --- type " + type);
		return map;
	}

	/**
	 * Change the UI state based on the state of the simulation (none, stopped, running or notready)
	 */
	@Override
	public void updateStateTo(final String state) {
		fireSourceChanged(ISources.WORKBENCH, SIMULATION_RUNNING_STATE, state);
		IExperimentPlan exp = GAMA.getExperiment();
		String type = exp == null ? "NONE" : exp.isBatch() ? "BATCH" : (exp.isMemorize() ? "MEMORIZE" : "REGULAR");
		fireSourceChanged(ISources.WORKBENCH, SIMULATION_TYPE, type);
	}

}