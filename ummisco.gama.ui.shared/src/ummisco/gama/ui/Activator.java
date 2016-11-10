/*********************************************************************************************
 *
 * 'Activator.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import msi.gama.runtime.GAMA;
import ummisco.gama.ui.utils.SwtGui;

public class Activator extends AbstractUIPlugin {

	public Activator() {
		if (GAMA.getRegularGui() == null) {
			GAMA.setRegularGui(new SwtGui());
		}

	}

}
