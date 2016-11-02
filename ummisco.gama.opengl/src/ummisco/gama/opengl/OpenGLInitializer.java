/*********************************************************************************************
 *
 * 'OpenGLInitializer.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import com.jogamp.common.util.JarUtil;
import com.jogamp.opengl.GLProfile;

public class OpenGLInitializer extends AbstractServiceFactory implements ummisco.gama.ui.interfaces.IOpenGLInitializer {

	boolean isInitialized = false;

	@Override
	public void run() {
		// // Necessary to load the native libraries correctly (see
		// //
		// http://forum.jogamp.org/Return-of-the-quot-java-lang-UnsatisfiedLinkError-Can-t-load-library-System-Library-Frameworks-glueg-td4034549.html)
		JarUtil.setResolver(new JarUtil.Resolver() {

			@Override
			public URL resolve(final URL url) {
				try {
					final URL urlUnescaped = FileLocator.resolve(url);
					final URL urlEscaped = new URI(urlUnescaped.getProtocol(), urlUnescaped.getPath(), null).toURL();
					return urlEscaped;
				} catch (final IOException ioexception) {
					return url;
				} catch (final URISyntaxException urisyntaxexception) {
					return url;
				}
			}
		});

		// Necessary to initialize very early because initializing it
		// while opening a Java2D view before leads to a deadlock
		GLProfile.initSingleton();
		while (!GLProfile.isInitialized()) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		isInitialized = true;

	}

	@Override
	public boolean isDone() {
		return isInitialized;
	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

}
