package gama.extensions.physics;

import static ummisco.gama.dev.utils.DEBUG.ERR;
import static ummisco.gama.dev.utils.DEBUG.PAD;
import static ummisco.gama.dev.utils.DEBUG.TIMER_WITH_EXCEPTIONS;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ummisco.gama.dev.utils.DEBUG;

public class BulletActivator implements BundleActivator {

	static {
		DEBUG.ON();
	}

	public static final boolean LOAD_NATIVE_BULLET_LIBRARY = true;
	public static boolean NATIVE_BULLET_LIBRARY_LOADED = false;

	@Override
	public void start(final BundleContext context) throws Exception {

		if (LOAD_NATIVE_BULLET_LIBRARY) {
			TIMER_WITH_EXCEPTIONS(PAD("> GAMA: native Bullet library", 45, ' ') + DEBUG.PAD(" loaded in", 15, '_'),
					() -> {
						try {
							System.loadLibrary("bulletjme");
							NATIVE_BULLET_LIBRARY_LOADED = true;
						} catch (Exception e) {
							ERR(">> Impossible to load Bullet native library from "
									+ context.getBundle().getSymbolicName() + " because of " + e.getMessage());
							ERR(">> GAMA will revert to JBullet instead");
							// throw e;
						}
					});

		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {}

}
