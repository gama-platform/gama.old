/*******************************************************************************************************
 *
 * Activator.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.GraphicsEnvironment;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class Activator.
 */
public class Activator implements BundleActivator {

	/** The context. */
	private static BundleContext context;

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	static BundleContext getContext() { return context; }

	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// We set the snapshot maker early so that it becomes available early from the IGui access
		// In reference to #3689, the GraphicsEnvironment is queried earlier for headless mode.
		String message, mode;

		if (GraphicsEnvironment.isHeadless()) {
			message = "inactive";
			mode = "(headless mode)";
		} else {
			message = "active";
			mode = "(gui mode)";
			GAMA.setSnapshotMaker(SnapshotMaker.getInstance());
		}
		DEBUG.BANNER("GAMA: Snapshot services", message, mode);
	}

	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
