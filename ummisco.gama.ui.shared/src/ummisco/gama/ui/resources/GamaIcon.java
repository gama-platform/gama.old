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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
// import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;

import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaIcon.
 */
public class GamaIcon {

	static {
		DEBUG.ON();
	}

	/** The code. */
	final String code;

	/** The url. */
	final URL url;

	/** The descriptor. */
	final ImageDescriptor descriptor;

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
	public GamaIcon(final String c) {
		code = c;
		url = computeURL(code);
		descriptor = ImageDescriptor.createFromURL(url);
		image();
	}

	/**
	 * Compute URL.
	 *
	 * @return the url
	 */
	public static URL computeURL(final String code) {
		IPath uriPath = new Path("/plugin").append(GamaIcons.PLUGIN_ID).append(GamaIcons.DEFAULT_PATH + code + ".png"); //$NON-NLS-1$
		try {
			URI uri = new URI("platform", null, uriPath.toString(), null); //$NON-NLS-1$
			return uri.toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return null;
		}
	}

	/**
	 * Instantiates a new gama icon.
	 *
	 * @param c
	 *            the c
	 * @param stream
	 *            the stream
	 */
	public GamaIcon(final String path, final InputStream stream) {
		code = path;
		url = computeURL(code);
		descriptor = ImageDescriptor.createFromImageData(new ImageLoader().load(stream)[0]);
		image();
	}

	/**
	 * Instantiates a new gama icon.
	 *
	 * @param name
	 *            the name
	 * @param im
	 *            the im
	 */
	public GamaIcon(final String path, final Image im) {
		code = path;
		url = computeURL(code);
		descriptor = ImageDescriptor.createFromImage(im);
		image();
	}

	/**
	 * Descriptor.
	 *
	 * @return the image descriptor
	 */
	public ImageDescriptor descriptor() {
		return descriptor;
	}

	/**
	 * Image.
	 *
	 * @return the image
	 */
	private Image image(final String key, final Callable<Image> imageCreator) {
		final Image image = JFaceResources.getImage(key);
		if (image == null) {
			try {
				JFaceResources.getImageRegistry().put(key, imageCreator.call());
			} catch (Exception e) {}
		}
		return JFaceResources.getImage(key);
	}

	/**
	 * Image.
	 *
	 * @return the image
	 */
	public Image image() {
		return image(url.toString(), () -> descriptor().createImage());
	}

	/**
	 * Disabled.
	 *
	 * @return the image
	 */
	public Image disabled() {
		return image(code + "_disabled", () -> disabledVersionOf(image()));
	}

	/**
	 * Checked.
	 *
	 * @return the image
	 */
	public Image checked() {
		return image(code + "_checked", () -> checkedVersionOf(image()));
	}

	/**
	 * Disabled version of.
	 *
	 * @param im
	 *            the im
	 * @return the image
	 */
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

	/**
	 * Checked version of.
	 *
	 * @param im
	 *            the im
	 * @return the image
	 */
	Image checkedVersionOf(final Image im) {
		Rectangle bounds = im.getBounds();
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