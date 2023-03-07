/*******************************************************************************************************
 *
 * Activator.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui;

import static org.eclipse.core.runtime.FileLocator.toFileURL;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.SwtGui;

/**
 * The Class Activator.
 */
public class Activator extends AbstractUIPlugin {

	static {
		DEBUG.OFF();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		if (GAMA.getRegularGui() == null) { GAMA.setRegularGui(new SwtGui()); }
		// Loading or producing the icons
		URL pngFolderURL = toFileURL(Platform.getBundle(IGamaIcons.PLUGIN_ID).getEntry(IGamaIcons.DEFAULT_PATH));
		Path pngPath = Path.of(new URI(pngFolderURL.getProtocol(), pngFolderURL.getPath(), null).normalize());
		// if (FLAGS.PRODUCE_ICONS) {
		// URL svgFolderURL = toFileURL(Platform.getBundle(IGamaIcons.PLUGIN_ID).getEntry(IGamaIcons.SVG_PATH));
		// Path svgPath = Path.of(new URI(svgFolderURL.getProtocol(), svgFolderURL.getPath(), null).normalize());
		// // We produce the icons and then leave the application immediately
		// DEBUG.TIMER_WITH_EXCEPTIONS(DEBUG.PAD("> GAMA: Producing icons", 55, ' ') + DEBUG.PAD(" done in", 15, '_'),
		// () -> GamaIconsProducer.produceIcons(svgPath, pngPath));
		// System.exit(0);
		// } else {
		DEBUG.TIMER_WITH_EXCEPTIONS(DEBUG.PAD("> GAMA: Preloading icons", 55, ' ') + DEBUG.PAD(" done in", 15, '_'),
				() -> GamaIcon.preloadFrom(pngPath));

		// }
	}

}
