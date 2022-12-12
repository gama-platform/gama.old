/*******************************************************************************************************
 *
 * Activator.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.SwtGui;

/**
 * The Class Activator.
 */
public class Activator extends AbstractUIPlugin {

	static {
		DEBUG.OFF();
	}

	/**
	 * Instantiates a new activator.
	 */
	public Activator() {
		if (GAMA.getRegularGui() == null) { GAMA.setRegularGui(new SwtGui()); }
		DEBUG.OUT("Regular GUI has been set");
	}

}
