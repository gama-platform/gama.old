/*******************************************************************************************************
 *
 * OpenGLActivator.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.jogamp.common.util.JarUtil;
import com.jogamp.opengl.GLProfile;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.THREADS;

/**
 * The Class OpenGLActivator.
 */
public class OpenGLActivator extends AbstractUIPlugin {

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		// // Necessary to load the native libraries correctly (see
		// //
		// http://forum.jogamp.org/Return-of-the-quot-java-lang-UnsatisfiedLinkError-Can-t-load-library-System-Library-Frameworks-glueg-td4034549.html)
		CompletableFuture.runAsync(() -> {
			DEBUG.TIMER("GAMA: Preloading OpenGL subsystem", "done in", () -> {

				JarUtil.setResolver(url -> {
					try {
						final URL urlUnescaped = FileLocator.resolve(url);
						return new URI(urlUnescaped.getProtocol(), urlUnescaped.getPath(), null).toURL();
					} catch (final IOException | URISyntaxException urisyntaxexception) {
						return url;
					}
				});

				// Necessary to initialize very early because initializing it
				// while opening a Java2D view before leads to a deadlock
				try {
					GLProfile.initSingleton();
				} catch (Exception e1) {
					DEBUG.ERR("Impossible to initialize OpenGL", e1);
					return;
				}
				while (!GLProfile.isInitialized()) { THREADS.WAIT(100, null, "Impossible to initialize OpenGL"); }
			});

		});
	}

}
