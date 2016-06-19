package ummisco.gama.opengl;

import java.io.IOException;
import java.net.*;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.IStartup;
import com.jogamp.common.util.JarUtil;
import com.jogamp.opengl.GLProfile;
import ummisco.gama.ui.utils.SwtGui;

public class GamaOpenGLStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// // Necessary to load the native libraries correctly (see
		// // http://forum.jogamp.org/Return-of-the-quot-java-lang-UnsatisfiedLinkError-Can-t-load-library-System-Library-Frameworks-glueg-td4034549.html)
		JarUtil.setResolver(new JarUtil.Resolver() {

			@Override
			public URL resolve(final URL url) {
				try {
					URL urlUnescaped = FileLocator.resolve(url);
					URL urlEscaped = new URI(urlUnescaped.getProtocol(), urlUnescaped.getPath(), null).toURL();
					return urlEscaped;
				} catch (IOException ioexception) {
					return url;
				} catch (URISyntaxException urisyntaxexception) {
					return url;
				}
			}
		});

		SwtGui.setOpenGLStartupSequence(new Runnable() {

			@Override
			public void run() {

				// Necessary to initialize very early because initializing it while opening a Java2D view before leads to a deadlock
				GLProfile.initSingleton();
				while (!GLProfile.isInitialized()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});

	}

}
