package gama.extensions.physics;

import static ummisco.gama.dev.utils.DEBUG.ERR;
import static ummisco.gama.dev.utils.DEBUG.PAD;
import static ummisco.gama.dev.utils.DEBUG.TIMER_WITH_EXCEPTIONS;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;

import ummisco.gama.dev.utils.DEBUG;

public class PhysicsActivator implements BundleActivator {

	static {
		DEBUG.ON();
	}

	public static final boolean LOAD_NATIVE_BULLET_LIBRARY = true;
	public static boolean NATIVE_BULLET_LIBRARY_LOADED = false;
	public static final String NATIVE_LIBRARY_LOCATION = "/lib/native/";
	public static final String MAC_NATIVE_LIBRARY_NAME = "libbulletjme.dylib";
	public static final String WIN_NATIVE_LIBRARY_NAME = "bulletjme.dll";
	public static final String LIN_NATIVE_LIBRARY_NAME = "libbulletjme.so";

	@Override
	public void start(final BundleContext context) throws Exception {

		if (LOAD_NATIVE_BULLET_LIBRARY) {
			TIMER_WITH_EXCEPTIONS(PAD("> GAMA: native Bullet library", 45, ' ') + DEBUG.PAD(" loaded in", 15, '_'),
					() -> {
						try {
							Platform platform = JmeSystem.getPlatform();
							String name;
							switch (platform) {
								case Windows64:
									name = WIN_NATIVE_LIBRARY_NAME;
									break;
								case Linux64:
									name = LIN_NATIVE_LIBRARY_NAME;
									break;
								case MacOSX64:
									name = MAC_NATIVE_LIBRARY_NAME;
									break;
								default:
									throw new RuntimeException("Platform " + platform + " is not supported");
							}

							NativeUtils.loadLibraryFromJar(NATIVE_LIBRARY_LOCATION + name);
							NATIVE_BULLET_LIBRARY_LOADED = true;
						} catch (Throwable e) {
							ERR(">> Impossible to load Bullet native library from "
									+ context.getBundle().getSymbolicName() + " because " + e.getMessage());
							ERR(">> GAMA will fall back to JBullet instead");
						}
					});

		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {}

}
