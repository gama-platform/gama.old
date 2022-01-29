/*******************************************************************************************************
 *
 * Startup.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui;

import org.eclipse.ui.IStartup;

import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.commands.TestsRunner;
import ummisco.gama.ui.utils.CleanupHelper;

/**
 * The Class Startup.
 */
public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		CleanupHelper.run();
		GamaKeyBindings.install();
		if (GamaPreferences.Runtime.START_TESTS.getValue()) {
			TestsRunner.start();
		}

	}

}
