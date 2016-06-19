/**
 * Created by drogoul, 15 févr. 2015
 *
 */
package ummisco.gama.ui.resources;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class GamaIcon {

	final String code;
	final String path;
	final String plugin;
	ImageDescriptor descriptor;

	/**
	 * Constructor for dynamic icons
	 * 
	 * @param c
	 *            the code
	 */
	GamaIcon(final String c) {
		this(c, c);
	}

	/**
	 * Constructor for images loaded from the main application plugin
	 * 
	 * @param c
	 *            the code
	 * @param p
	 *            the path (in the 'icons' folder)
	 */
	GamaIcon(final String c, final String p) {
		this(c, p, GamaIcons.PLUGIN_ID);
	}

	/**
	 * Constructor for images loaded from a plugin
	 * 
	 * @param c
	 *            the code
	 * @param p
	 *            the path (in the 'icons' folder)
	 * @param plugin
	 *            the id of the plugin in which the 'icons' folder resides
	 */
	GamaIcon(final String c, final String p, final String plugin) {
		code = c;
		path = p;
		this.plugin = plugin;
	}

	public ImageDescriptor descriptor() {
		if (descriptor == null) {
			final Image image = GamaIcons.getInstance().getImageInCache(code);
			if (image != null) {
				descriptor = ImageDescriptor.createFromImage(image);
			} else {
				descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(plugin, GamaIcons.DEFAULT_PATH + path + ".png");
			}

			if (descriptor == null) {
				System.out.println(
						"ERROR: Cannot find icon " + GamaIcons.DEFAULT_PATH + path + ".png in plugin: " + plugin);
				descriptor = ImageDescriptor.getMissingImageDescriptor();
			}
		}
		return descriptor;
	}

	public Image image() {
		Image image = GamaIcons.getInstance().getImageInCache(code);
		if (image == null) {
			image = GamaIcons.getInstance().putImageInCache(code, descriptor().createImage());
		}
		return image;
	}

	public String getCode() {
		return code;
	}
}