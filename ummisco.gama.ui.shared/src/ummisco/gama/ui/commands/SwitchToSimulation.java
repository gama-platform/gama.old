/*******************************************************************************************************
 *
 * SwitchToSimulation.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import msi.gama.application.workbench.PerspectiveHelper;

/**
 * The Class SwitchToSimulation.
 */
public class SwitchToSimulation extends SwitchToHandler {

	/**
	 * Execute.
	 */
	protected void execute() {
		PerspectiveHelper.switchToSimulationPerspective();
	}

}
