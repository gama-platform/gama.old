/*******************************************************************************************************
 *
 * OpenGLActivator.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
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
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;

import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.THREADS;
import ummisco.gama.opengl.OpenGL;

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
			DEBUG.TIMER("OpenGL", "Subsystem preloaded", "in", () -> {

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
				gatherOpenGLProperties();
				// }).start();
			});

		});
	}

	/**
	 * Initialize GL preferences.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 21 déc. 2023
	 */
	private void gatherOpenGLProperties() {
		GL gl = null;
		GLOffscreenAutoDrawable offscreen = null;
		String property = "Loading";
		String prefix = "OpenGL";
		try {
			GLCapabilities cap = new GLCapabilities(OpenGL.PROFILE);
			cap.setDepthBits(24);
			cap.setDoubleBuffered(true);
			cap.setHardwareAccelerated(true);
			cap.setSampleBuffers(true);
			cap.setAlphaBits(8);
			cap.setNumSamples(8);
			offscreen =
					GLDrawableFactory.getFactory(OpenGL.PROFILE).createOffscreenAutoDrawable(null, cap, null, 10, 10);
			offscreen.display();
			GLContext gc = offscreen.getContext();
			gc.makeCurrent();
			gl = gc.getGL();
			DEBUG.BANNER(prefix, "Profile initialized", "version", gc.getGLVersionNumber().toString());
			float fresult[] = { 0.0f, 0.0f };
			int iresult[] = { 0 };
			property = "Line width range";
			gl.glGetFloatv(GL.GL_SMOOTH_LINE_WIDTH_RANGE, fresult, 0);
			DEBUG.BANNER(prefix, property, "between",
					String.valueOf(fresult[0]) + " and " + String.valueOf(fresult[1]));
			GamaPreferences.Displays.CORE_LINE_WIDTH.between(fresult[0], fresult[1]);
			property = "Line width granularity";
			gl.glGetFloatv(GL2GL3.GL_SMOOTH_LINE_WIDTH_GRANULARITY, fresult, 0);
			DEBUG.BANNER(prefix, property, "value", String.valueOf(fresult[0]));
			GamaPreferences.Displays.CORE_LINE_WIDTH.step(fresult[0]);
			property = "Point size range";
			gl.glGetFloatv(GL.GL_POINT_SIZE, fresult, 0);
			DEBUG.BANNER(prefix, property, "between",
					String.valueOf(fresult[0]) + " and " + String.valueOf(fresult[1]));
			property = "Max anisotropy level";
			gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fresult, 0);
			OpenGL.ANISOTROPIC_LEVEL = fresult[0];
			DEBUG.BANNER(prefix, property, "value", String.valueOf(OpenGL.ANISOTROPIC_LEVEL));
			property = "Max texture size";
			gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, iresult, 0);
			DEBUG.BANNER(prefix, property, "value", String.valueOf(iresult[0]) + "x" + String.valueOf(iresult[0]));
		} catch (Exception e) {
			// do not interrupt the thread if something goes wrong and simply report problem
			DEBUG.BANNER(prefix, "Properties", "error on", property);
		} finally {
			if (gl != null) { gl.getContext().destroy(); }
			if (offscreen != null) { offscreen.destroy(); }
		}

	}

}
