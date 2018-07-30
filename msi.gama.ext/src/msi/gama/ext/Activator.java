/*********************************************************************************************
 * 
 *
 * 'Activator.java', in plugin 'msi.gama.headless', is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.ext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework. BundleContext)
	 */
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// Forces early initialisation of operation registry of JAI.
		// It fixes initialisation problems in some third party equinox
		// applications such as OpenMOLE.

		// final String os = System.getProperty("os.name");
		// if (!os.startsWith("Mac")) {
		javax.media.jai.JAI.getDefaultInstance().getOperationRegistry().getRegistryModes();
		// }
		System.out.println("JAI activated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
