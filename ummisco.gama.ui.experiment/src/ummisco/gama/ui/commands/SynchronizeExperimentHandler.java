/*******************************************************************************************************
 *
 * SynchronizeExperimentHandler.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SynchronizeExperimentHandler.
 */
public class SynchronizeExperimentHandler extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (GAMA.isSynchronized()) {
			GAMA.desynchronizeFrontmostExperiment();
		} else {
			GAMA.synchronizeFrontmostExperiment();
		}
		return this;
	}

	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		element.setChecked(GAMA.isSynchronized());
		if (GAMA.isSynchronized()) {
			element.setTooltip("Desynchronizes the experiment with its outputs");
			element.setText("Desynchonize Experiment");
		} else {
			element.setTooltip("Synchronizes the experiment with its outputs");
			element.setText("Synchronize Experiment");
		}

	}

}
