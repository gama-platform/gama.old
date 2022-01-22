/*******************************************************************************************************
 *
 * msi.gama.common.Activator.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.operators.Dates;

public class Activator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		/* Early build of the contributions made by plugins to GAMA */
		GamaBundleLoader.preBuildContributions();
		GamaExecutorService.reset();
		Dates.initialize();

	}

	@Override
	public void stop(final BundleContext context) throws Exception {}

}
