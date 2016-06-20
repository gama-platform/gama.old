package ummisco.gama.ui.activator;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import ummisco.gama.ui.bindings.GamaKeyBindings;

public class ExperimentUIActivator extends AbstractUIPlugin {

	public ExperimentUIActivator() {
		super();
		GamaKeyBindings.install();
	}

}
