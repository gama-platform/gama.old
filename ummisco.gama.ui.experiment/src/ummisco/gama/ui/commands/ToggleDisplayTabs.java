/*******************************************************************************************************
 *
 * ToggleDisplayTabs.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import static ummisco.gama.ui.commands.ArrangeDisplayViews.collectAndPrepareDisplayViews;
import static ummisco.gama.ui.commands.LayoutTreeConverter.convertCurrentLayout;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.application.workbench.SimulationPerspectiveDescriptor;

/**
 * The Class ToggleDisplayTabs.
 */
public class ToggleDisplayTabs extends AbstractHandler {

	// NOT YET READY FOR PRIME TIME
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final SimulationPerspectiveDescriptor sd = PerspectiveHelper.getActiveSimulationPerspective();
		if (sd != null) { sd.keepTabs(!sd.keepTabs()); }
		ArrangeDisplayViews.execute(convertCurrentLayout(collectAndPrepareDisplayViews()));
		return this;
	}

}
