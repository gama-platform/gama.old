/*******************************************************************************************************
 *
 * Startup.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui;

import org.eclipse.ui.IStartup;

import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.commands.TestsRunner;
import ummisco.gama.ui.utils.CleanupHelper;

/**
 * The Class Startup.
 */
public class Startup implements IStartup {

	static {
		DEBUG.OFF();
	}

	@Override
	public void earlyStartup() {
		DEBUG.OUT("Startup of ui plugin begins");
		CleanupHelper.run();
		GamaKeyBindings.install();
		DEBUG.OUT("Startup of ui plugin finished");
		if (GamaPreferences.Runtime.START_TESTS.getValue()) { TestsRunner.start(); }

	}

}
