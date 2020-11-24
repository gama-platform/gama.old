/*********************************************************************************************
 *
 *
 * 'Activator.java', in plugin 'msi.gama.headless', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.ext;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;

import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import it.geosolutions.jaiext.ConcurrentOperationRegistry;

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
		final JAI jaiDef = JAI.getDefaultInstance();
		if (!(jaiDef.getOperationRegistry() instanceof ConcurrentOperationRegistry)) {
			jaiDef.setOperationRegistry(ConcurrentOperationRegistry.initializeRegistry());
		}
		ImageIO.scanForPlugins();
//		Hints.putSystemDefault(Hints.FILTER_FACTORY, CommonFactoryFinder.getFilterFactory2(null));
//		Hints.putSystemDefault(Hints.STYLE_FACTORY, CommonFactoryFinder.getStyleFactory(null));
//		Hints.putSystemDefault(Hints.FEATURE_FACTORY, CommonFactoryFinder.getFeatureFactory(null));
		Hints.putSystemDefault(Hints.USE_JAI_IMAGEREAD, true);
		final Hints defHints = GeoTools.getDefaultHints();
//
//		// Initialize GridCoverageFactory so that we don't make a lookup every time a factory is
//		// needed
		Hints.putSystemDefault(Hints.GRID_COVERAGE_FACTORY, CoverageFactoryFinder.getGridCoverageFactory(defHints));
//		// Forces early initialisation of operation registry of JAI.
//		// It fixes initialisation problems in some third party equinox
//		// applications such as OpenMOLE.
//
//		// final String os = System.getProperty("os.name");
//		// if (!os.startsWith("Mac")) {
//
//		// }
	System.out.println("> JAI/ImageIO subsystem activated");
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
