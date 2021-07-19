/*********************************************************************************************
 *
 * 'GamaIcon.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.resources;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class GamaIcon {

	static {
		DEBUG.ON();
	}

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
				DEBUG.ERR("ERROR: Cannot find icon " + GamaIcons.DEFAULT_PATH + path + ".png in plugin: " + plugin);
				descriptor = ImageDescriptor.getMissingImageDescriptor();
			}
		}
		return descriptor;
	}

	public Image image() {
		final Image[] image = { GamaIcons.getInstance().getImageInCache(code) };
		if (image[0] == null) {
			WorkbenchHelper
					.run(() -> image[0] = GamaIcons.getInstance().putImageInCache(code, descriptor().createImage()));
		}
		return image[0];
	}

	public Image disabled() {
		final Image[] image = { GamaIcons.getInstance().getImageInCache(code + "_disabled") };
		if (image[0] == null) {
			WorkbenchHelper.run(() -> image[0] =
					GamaIcons.getInstance().putImageInCache(code + "_disabled", disabledVersionOf(image())));
		}
		return image[0];
	}

	Image disabledVersionOf(final Image im) {
		Rectangle bounds = im.getBounds();
		ImageData srcData = im.getImageData();
		ImageData dstData = new ImageData(bounds.width, bounds.height, srcData.depth, srcData.palette);
		dstData.transparentPixel = srcData.transparentPixel;
		dstData.alpha = srcData.alpha;
		for (int sx = 0; sx < bounds.width; sx++) {
			for (int sy = 0; sy < bounds.height; sy++) {
				dstData.setAlpha(sx, sy, srcData.getAlpha(sx, sy) / 2);
				dstData.setPixel(sx, sy, srcData.getPixel(sx, sy));
			}
		}
		return new Image(im.getDevice(), dstData);
	}

	public String getCode() {
		return code;
	}
}