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

import static org.eclipse.core.runtime.FileLocator.toFileURL;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageDataProvider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class GamaIcon.
 */
public class GamaIcon {

	/** The icon cache. */
	public static Cache<String, GamaIcon> ICON_CACHE = CacheBuilder.newBuilder().build();

	/** The Constant SIZER_PREFIX. */
	static final String SIZER_PREFIX = "sizer_";

	/** The Constant MISSING. */
	static final String MISSING = "gaml/_unknown";

	/** The Constant DISABLED_SUFFIX. */
	public static final String DISABLED_SUFFIX = "_disabled";

	/** The Constant PATH_TO_ICONS. */
	public static Path PATH_TO_ICONS;

	static {
		try {
			URL pngFolderURL = toFileURL(Platform.getBundle(IGamaIcons.PLUGIN_ID).getEntry(IGamaIcons.ICONS_PATH));
			PATH_TO_ICONS = Path.of(new URI(pngFolderURL.getProtocol(), pngFolderURL.getPath(), null).normalize());
		} catch (Exception e) {}
	}

	/**
	 * Returns the icon named after the path (eg "templates/square.template")
	 *
	 * @param path
	 *            the path
	 * @return the gama icon
	 */
	public static GamaIcon named(final String s) {

		try {
			if (s != null) return ICON_CACHE.get(s, () -> new GamaIcon(s));
		} catch (ExecutionException e) {}
		return named(MISSING);
	}

	/**
	 * Creates a color icon, either a round square or a circle
	 *
	 * @param gcolor
	 *            the gcolor
	 * @param square
	 *            the square
	 * @return the image
	 */
	public static GamaIcon ofColor(final GamaUIColor gcolor, final boolean square) {
		String shape = square ? "square" : "circle";
		final String name = "colors/" + shape + ".color." + String.format("%X", gcolor.gamaColor().getRGB());
		// DEBUG.LOG("Looking for " + shape + ".color" + String.format("%X", gcolor.gamaColor().getRGB()) + ".png");
		try {
			return ICON_CACHE.get(name, () -> {
				Image image =
						ImageDescriptor.createFromURL(computeURL("templates/" + shape + "_template")).createImage();
				final GC gc = new GC(image);
				gc.setBackground(gcolor.color());
				int size = image.getBounds().width;
				if (square) {
					gc.fillRoundRectangle(size / 4, size / 4, size / 2 + 1, size / 2 + 1, 4, 4);
				} else {
					gc.fillOval(size / 4, size / 4, size / 2 + 1, size / 2 + 1);
				}
				gc.dispose();
				return new GamaIcon(name, image);
			});
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Creates a sizer of a given color
	 *
	 * @param color
	 *            the color
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama icon
	 */
	public static GamaIcon ofSize(final Color color, final int width, final int height) {
		final String name = SIZER_PREFIX + width + "x" + height + color.hashCode();
		try {
			return ICON_CACHE.get(name, () -> {
				final Image sizerImage = new Image(WorkbenchHelper.getDisplay(), width, height);
				final GC gc = new GC(sizerImage);
				gc.setBackground(color);
				gc.fillRectangle(0, 0, width, height);
				gc.dispose();
				return new GamaIcon(name, sizerImage);
			});
		} catch (ExecutionException e) {
			return null;
		}

	}

	static {
		DEBUG.ON();
	}

	/** The code. */
	final String code;

	/** The url. */
	final URL url, disabledUrl;

	/** The descriptor. */
	final ImageDescriptor descriptor, disabledDescriptor;

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
	private GamaIcon(final String c) {
		// DEBUG.LOG("Creation of icon " + c);
		code = c;
		url = computeURL(code);
		disabledUrl = computeURL(code + DISABLED_SUFFIX);
		descriptor = ImageDescriptor.createFromURL(url);
		disabledDescriptor = ImageDescriptor.createFromURL(disabledUrl);
		// image();
	}

	/**
	 * Instantiates a new gama icon directly from an image. We do not produce disabled versions
	 *
	 * @param name
	 *            the name
	 * @param im
	 *            the im
	 */
	private GamaIcon(final String path, final Image im) {
		code = path;
		url = computeURL(code);
		disabledUrl = url;
		descriptor = ImageDescriptor.createFromImage(im);
		disabledDescriptor = descriptor;
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
	 * Disabled descriptor.
	 *
	 * @return the image descriptor
	 */
	public ImageDescriptor disabledDescriptor() {
		return disabledDescriptor;
	}

	/**
	 * Image.
	 *
	 * @return the image
	 */
	private Image image(final String key, final Callable<Image> imageCreator) {
		Image image = JFaceResources.getImage(key);
		if (image == null) {
			try {
				image = imageCreator.call();
			} catch (Exception e) {}
			if (image == null) { image = named(MISSING).image(); }
			JFaceResources.getImageRegistry().put(key, image);
		}
		return image;
	}

	/**
	 * Image.
	 *
	 * @return the image
	 */
	public Image image() {
		return image(url.toString(), () -> descriptor.createImage(false));
	}

	/**
	 * Disabled.
	 *
	 * @return the image
	 */
	public Image disabled() {
		return image(disabledUrl.toString(), () -> disabledDescriptor.createImage(false));
	}

	/**
	 * Checked.
	 *
	 * @return the image
	 */
	public Image checked() {
		return image(code + "_checked", () -> {
			Image im = new Image(null, (ImageDataProvider) descriptor());
			GC gc = new GC(im);
			gc.setForeground(IGamaColors.LIGHT_GRAY.color());
			gc.setLineWidth(6);
			gc.drawRectangle(im.getBounds());
			gc.dispose();
			return im;
		});
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() { return code; }

	/**
	 * Compute URL.
	 *
	 * @return the url
	 */
	public static URL computeURL(final String code) {
		IPath uriPath = new org.eclipse.core.runtime.Path("/plugin").append(IGamaIcons.PLUGIN_ID)
				.append(IGamaIcons.ICONS_PATH + code + ".png");
		try {
			URI uri = new URI("platform", null, uriPath.toString(), null);
			return uri.toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return computeURL(GamaIcon.MISSING);
		}
	}

}