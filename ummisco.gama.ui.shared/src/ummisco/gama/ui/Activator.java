/*******************************************************************************************************
 *
 * Activator.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;
import ummisco.gama.ui.resources.GamaIconsLoader;
import ummisco.gama.ui.utils.SwtGui;

/**
 * The Class Activator.
 */
public class Activator extends AbstractUIPlugin {

	static {
		DEBUG.OFF();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		if (GAMA.getRegularGui() == null) { GAMA.setRegularGui(new SwtGui()); }
		DEBUG.OUT("Regular GUI has been set");
		if (FLAGS.PRODUCE_ICONS) {
			// We produce the icons and then leave the application immediately
			GamaIconsLoader.buildIconsOnDisk();
			System.exit(0);
		} else {
			GamaIconsLoader.preloadIcons();
		}
	}

}
