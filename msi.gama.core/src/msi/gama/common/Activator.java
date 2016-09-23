package msi.gama.common;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gaml.compilation.kernel.GamaBundleLoader;

public class Activator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		/* Early build of the contributions made by plugins to GAMA */
		new Thread(new Runnable() {

			@Override
			public void run() {
				GamaBundleLoader.preBuildContributions();
			}
		}).start();

	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
