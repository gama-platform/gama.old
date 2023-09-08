/*******************************************************************************************************
 *
 * NativeLoader.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extensions.physics;

import static ummisco.gama.dev.utils.DEBUG.ERR;
import static ummisco.gama.dev.utils.DEBUG.TIMER_WITH_EXCEPTIONS;

import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;

import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class PhysicsActivator.
 */
public class NativeLoader {

	static {
		DEBUG.ON();
	}

	/** The Constant LOAD_NATIVE_BULLET_LIBRARY. */
	public static final boolean LOAD_NATIVE_BULLET_LIBRARY = true;

	/** The native bullet library loaded. */
	public static Boolean NATIVE_BULLET_LIBRARY_LOADED = null;

	/** The Constant NATIVE_LIBRARY_LOCATION. */
	public static final String NATIVE_LIBRARY_LOCATION = "/lib/native/";

	/** The Constant MAC_NATIVE_LIBRARY_NAME. */
	public static final String MAC_NATIVE_LIBRARY_NAME = "MacOSX64ReleaseDp_libbulletjme.dylib";

	/** The Constant MAC_ARM_NATIVE_LIBRARY_NAME. */
	public static final String MAC_ARM_NATIVE_LIBRARY_NAME = "MacOSX_ARM64ReleaseDp_libbulletjme.dylib";

	/** The Constant WIN_NATIVE_LIBRARY_NAME. */
	public static final String WIN_NATIVE_LIBRARY_NAME = "Windows64ReleaseDp_bulletjme.dll";

	/** The Constant LIN_NATIVE_LIBRARY_NAME. */
	public static final String LIN_NATIVE_LIBRARY_NAME = "Linux64ReleaseDp_libbulletjme.so";

	/** The Load native library. */
	public static boolean LoadNativeLibrary() {
		if (NATIVE_BULLET_LIBRARY_LOADED == null) {
			NATIVE_BULLET_LIBRARY_LOADED = false;
			if (LOAD_NATIVE_BULLET_LIBRARY) {
				TIMER_WITH_EXCEPTIONS("GAMA: native Bullet library", "loaded in", () -> {
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
							case MacOSX_ARM64:
								name = MAC_ARM_NATIVE_LIBRARY_NAME;
								break;
							default:
								throw new RuntimeException("Platform " + platform + " is not supported");
						}

						NativeUtils.loadLibraryFromJar(NATIVE_LIBRARY_LOCATION + name);
						NATIVE_BULLET_LIBRARY_LOADED = true;
					} catch (Throwable e) {
						NATIVE_BULLET_LIBRARY_LOADED = false;
						ERR(">> Impossible to load Bullet native library because " + e.getMessage());
						ERR(">> GAMA will fall back to JBullet instead");
					}
				});

			}
		}
		return NATIVE_BULLET_LIBRARY_LOADED;
	}

}
