/*********************************************************************************************
 *
 * 'ExperimentUIActivator.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.activator;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import ummisco.gama.ui.bindings.GamaKeyBindings;

public class ExperimentUIActivator extends AbstractUIPlugin {

	public ExperimentUIActivator() {
		super();
		GamaKeyBindings.install();
	}

}
