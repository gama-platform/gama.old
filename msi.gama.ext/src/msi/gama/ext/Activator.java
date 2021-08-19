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
import org.geotools.util.factory.GeoTools;
import org.geotools.util.factory.Hints;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import it.geosolutions.jaiext.ConcurrentOperationRegistry;
import one.util.streamex.StreamEx;

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
		// Forces early initialisation of operation registry of JAI. It fixes initialisation problems in some third
		// party equinox x@applications such as OpenMOLE.
		final JAI jaiDef = JAI.getDefaultInstance();
		if (!(jaiDef.getOperationRegistry() instanceof ConcurrentOperationRegistry)) {
			jaiDef.setOperationRegistry(ConcurrentOperationRegistry.initializeRegistry());
		}
		ImageIO.scanForPlugins();
		Hints.putSystemDefault(Hints.FILTER_FACTORY, CommonFactoryFinder.getFilterFactory2(null));
		Hints.putSystemDefault(Hints.STYLE_FACTORY, CommonFactoryFinder.getStyleFactory(null));
		Hints.putSystemDefault(Hints.FEATURE_FACTORY, CommonFactoryFinder.getFeatureFactory(null));
		Hints.putSystemDefault(Hints.USE_JAI_IMAGEREAD, true);
		final Hints defHints = GeoTools.getDefaultHints();
		// Initialize GridCoverageFactory so that we don't make a lookup every time a factory is needed
		Hints.putSystemDefault(Hints.GRID_COVERAGE_FACTORY, CoverageFactoryFinder.getGridCoverageFactory(defHints));

		//
		// See FLAGS.java
		String log = System.getProperty("enable_logging");
		if (log == null || "true".equals(log)) {
			System.out.println(PAD("> JAI : ImageIO extensions", 45, ' ') + PAD(" loaded for", 15, '_') + " "
					+ StreamEx.of(ImageIO.getReaderFileSuffixes()).joining("|"));
		}
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

	// See DEBUG.java
	public static String PAD(final String string, final int minLength, final char pad) {
		if (string.length() >= minLength) return string;
		final StringBuilder sb = new StringBuilder(minLength);
		sb.append(string);
		for (int i = string.length(); i < minLength; i++) {
			sb.append(pad);
		}
		return sb.toString();
	}

}
