/*******************************************************************************************************
 *
 * Activator.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
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
import ummisco.gama.ui.resources.GamaIcon;
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
		GAMA.setRegularGui(new SwtGui());
		GamaIcon.preloadAllIcons();
	}

}
