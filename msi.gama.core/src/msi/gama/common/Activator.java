package msi.gama.common;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gaml.compilation.kernel.GamaBundleLoader;

public class Activator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		/* Early build of the contributions made by plugins to GAMA */
		new Thread(() -> {
			GamaBundleLoader.preBuildContributions();
			GamaExecutorService.startUp();
		}).start();

	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
