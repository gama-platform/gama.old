/*******************************************************************************************************
 *
 * GamaIcon.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.resources;

import java.io.InputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class GamaIcon.
 */
public class GamaIcon {

	static {
		DEBUG.ON();
	}

	/** The code. */
	final String code;

	/** The path. */
	final String path;

	/** The plugin. */
	final String plugin;

	/** The descriptor. */
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

	/**
	 * Instantiates a new gama icon.
	 *
	 * @param c
	 *            the c
	 * @param stream
	 *            the stream
	 */
	public GamaIcon(final String c, final InputStream stream) {
		code = c;
		path = c;
		plugin = c;
		descriptor = ImageDescriptor.createFromImageData(new ImageLoader().load(stream)[0]);
	}

	/**
	 * Descriptor.
	 *
	 * @return the image descriptor
	 */
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

	/**
	 * Image.
	 *
	 * @return the image
	 */
	public Image image() {
		final Image[] image = { GamaIcons.getInstance().getImageInCache(code) };
		if (image[0] == null) {
			WorkbenchHelper
					.run(() -> image[0] = GamaIcons.getInstance().putImageInCache(code, descriptor().createImage()));
		}
		return image[0];
	}

	/**
	 * Disabled.
	 *
	 * @return the image
	 */
	public Image disabled() {
		final Image[] image = { GamaIcons.getInstance().getImageInCache(code + "_disabled") };
		if (image[0] == null) {
			WorkbenchHelper.run(() -> image[0] =
					GamaIcons.getInstance().putImageInCache(code + "_disabled", disabledVersionOf(image())));
		}
		return image[0];
	}

	/**
	 * Checked.
	 *
	 * @return the image
	 */
	public Image checked() {
		final Image[] image = { GamaIcons.getInstance().getImageInCache(code + "_checked") };
		if (image[0] == null) {
			WorkbenchHelper.run(() -> image[0] =
					GamaIcons.getInstance().putImageInCache(code + "_checked", checkedVersionOf(image())));
		}
		return image[0];
	}

	/**
	 * Disabled version of.
	 *
	 * @param im
	 *            the im
	 * @return the image
	 */
	Image disabledVersionOf(final Image im) {
		// return new Image(im.getDevice(), im, SWT.IMAGE_DISABLE);

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

	/**
	 * Checked version of.
	 *
	 * @param im
	 *            the im
	 * @return the image
	 */
	Image checkedVersionOf(final Image im) {
		Rectangle bounds = im.getBounds();
		// Image result = new Image(im.getDevice(), im, SWT.IMAGE_GRAY);
		// GC gc = new GC(result);
		// gc.setForeground(GamaColors.system(SWT.COLOR_DARK_GREEN));
		//

		// gc.setLineWidth(3);
		// gc.set
		// gc.drawLine(bounds.width / 4, 3 * bounds.height / 4, bounds.width / 2, bounds.height);
		// gc.drawLine(bounds.width, bounds.height / 2, bounds.width / 2, bounds.height);
		// gc.dispose();
		// return result;

		// Rectangle bounds = im.getBounds();
		// // Color color = GamaColors.system(SWT.COLOR_DARK_GREEN);
		ImageData srcData = im.getImageData();
		ImageData dstData = new ImageData(bounds.width, bounds.height, srcData.depth, srcData.palette);
		dstData.transparentPixel = srcData.transparentPixel;
		dstData.alpha = srcData.alpha;
		for (int sx = 0; sx < bounds.width; sx++) {
			for (int sy = 0; sy < bounds.height; sy++) {
				// dstData.setAlpha(sx, sy, srcData.getAlpha(sx, sy) / 2);
				int p = srcData.getPixel(sx, sy);
				dstData.setPixel(sx, sy, p < 0x999999 ? 0x8800 : p);
			}
		}
		return new Image(im.getDevice(), dstData);

	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() { return code; }
}