/*******************************************************************************************************
 *
 * GamaIcons.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

import msi.gama.application.workbench.IIconProvider;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaIcons implements IIconProvider {

	/** The Constant PLUGIN_ID. */
	public static final String PLUGIN_ID = "ummisco.gama.ui.shared";

	/** The instance. */
	static private GamaIcons instance = new GamaIcons();

	/**
	 * Gets the single instance of GamaIcons.
	 *
	 * @return single instance of GamaIcons
	 */
	public static GamaIcons getInstance() { return instance; }

	/** The Constant DEFAULT_PATH. */
	static public final String DEFAULT_PATH = "/icons/";

	/** The Constant SIZER_PREFIX. */
	static final String SIZER_PREFIX = "sizer_";

	/** The Constant COLOR_PREFIX. */
	static final String COLOR_PREFIX = "color_";

	/** The icon cache. */
	Map<String, GamaIcon> iconCache = new HashMap<>();

	/** The image cache. */
	Map<String, Image> imageCache = new HashMap<>();

	/**
	 * Gets the icon.
	 *
	 * @param name
	 *            the name
	 * @return the icon
	 */
	GamaIcon getIcon(final String name) {
		return iconCache.get(name);
	}

	/**
	 * Put image in cache.
	 *
	 * @param name
	 *            the name
	 * @param image
	 *            the image
	 * @return the image
	 */
	Image putImageInCache(final String name, final Image image) {
		imageCache.put(name, image);
		return image;

	}

	/**
	 * Put icon in cache.
	 *
	 * @param name
	 *            the name
	 * @param icon
	 *            the icon
	 */
	void putIconInCache(final String name, final GamaIcon icon) {
		iconCache.put(name, icon);
	}

	/**
	 * Gets the image in cache.
	 *
	 * @param code
	 *            the code
	 * @return the image in cache
	 */
	Image getImageInCache(final String code) {
		return imageCache.get(code);
	}

	/**
	 * Creates the sizer.
	 *
	 * @param color
	 *            the color
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama icon
	 */
	public static GamaIcon createSizer(final Color color, final int width, final int height) {
		final String name = SIZER_PREFIX + width + "x" + height + color.hashCode();
		GamaIcon sizer = getInstance().getIcon(name);
		if (sizer == null) {
			final Image sizerImage = new Image(WorkbenchHelper.getDisplay(), width, height);
			final GC gc = new GC(sizerImage);
			gc.setBackground(color);
			gc.fillRectangle(0, 0, width, height);
			gc.dispose();
			sizer = new GamaIcon(name);
			getInstance().putImageInCache(name, sizerImage);
			getInstance().putIconInCache(name, sizer);
		}
		return sizer;
	}

	/**
	 * Creates the.
	 *
	 * @param s
	 *            the s
	 * @return the gama icon
	 */
	public static GamaIcon create(final String s) {
		return create(s, PLUGIN_ID);
	}

	/**
	 * Creates the.
	 *
	 * @param code
	 *            the code
	 * @param plugin
	 *            the plugin
	 * @return the gama icon
	 */
	public static GamaIcon create(final String code, final String plugin) {
		return create(code, code, plugin);
	}

	/**
	 * Creates the.
	 *
	 * @param code
	 *            the code
	 * @param path
	 *            the path
	 * @param plugin
	 *            the plugin
	 * @return the gama icon
	 */
	public static GamaIcon create(final String code, final String path, final String plugin) {
		GamaIcon result = getInstance().getIcon(code);
		if (result == null) {
			result = new GamaIcon(code, path, plugin);
			getInstance().putIconInCache(code, result);
		}
		return result;
	}

	/**
	 * Creates the color icon.
	 *
	 * @param s
	 *            the s
	 * @param gcolor
	 *            the gcolor
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama icon
	 */
	public static GamaIcon createColorIcon(final String s, final GamaUIColor gcolor, final int width,
			final int height) {
		final String name = COLOR_PREFIX + s;
		GamaIcon icon = getInstance().getIcon(s);
		if (icon == null) {
			// Color color = gcolor.color();
			// RGB c = new RGB(color.getRed(), color.getGreen(),
			// color.getBlue());
			final Image image = new Image(WorkbenchHelper.getDisplay(), width, height);
			final GC gc = new GC(image);
			gc.setAntialias(SWT.ON);
			gc.setBackground(gcolor.color());
			gc.fillRoundRectangle(0, 0, width, height, width / 3, height / 3);
			gc.dispose();
			final ImageData data = image.getImageData();
			data.transparentPixel = data.palette.getPixel(new RGB(255, 255, 255));
			icon = new GamaIcon(name);
			getInstance().putImageInCache(name, new Image(WorkbenchHelper.getDisplay(), data));
			image.dispose();
			getInstance().putIconInCache(name, icon);
		}
		return icon;
	}

	/**
	 * Creates an icon that needs to be disposed afterwards
	 *
	 * @param gcolor
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image createTempColorIcon(final GamaUIColor gcolor) {
		final String name = "color" + gcolor.getRGB().toString();
		final GamaIcon icon = getInstance().getIcon(name);
		if (icon != null) return icon.image();
		// Color color = gcolor.color();
		final GamaIcon blank = create("display.color2");
		final Image image = new Image(WorkbenchHelper.getDisplay(), blank.image().getImageData());
		final GC gc = new GC(image);
		// gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillRoundRectangle(6, 6, 12, 12, 4, 4);
		// if (!gcolor.isDark()) {
		// gc.setForeground(IGamaColors.BLACK.color());
		// gc.drawRoundRectangle(6, 6, 12, 12, 4, 4);
		// }
		// See Issue #3138 about weird artefacts in handmade icons. dispose() does it on Retina screens. If removing
		// this condition, the weird artefacts come back on Retina screens. Otherwise they do not.
		if (!PlatformHelper.isMac() || !PlatformHelper.isHiDPI()) { gc.dispose(); }
		getInstance().putImageInCache(name, image);
		getInstance().putIconInCache(name, new GamaIcon(name));
		return image;
	}

	/**
	 * Creates the temp round color icon.
	 *
	 * @param gcolor
	 *            the gcolor
	 * @return the image
	 */
	public static Image createTempRoundColorIcon(final GamaUIColor gcolor) {
		final String name = "roundcolor" + gcolor.getRGB().toString();
		final GamaIcon icon = getInstance().getIcon(name);
		if (icon != null) return icon.image();
		// Color color = gcolor.color();
		final GamaIcon blank = create("display.color3");
		final Image image = new Image(WorkbenchHelper.getDisplay(), blank.image().getImageData());

		final GC gc = new GC(image);
		gc.setAdvanced(true);
		// gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillOval(6, 7, 12, 12);
		// if (!gcolor.isDark()) {
		// gc.setForeground(IGamaColors.BLACK.color());
		// gc.drawOval(6, 6, 12, 12);
		// }
		// See Issue #3138 about weird artefacts in handmade icons. dispose() does it on Retina screens. If removing
		// this condition, the weird artefacts come back on Retina screens. Otherwise they do not.
		if (!PlatformHelper.isMac() || !PlatformHelper.isHiDPI()) { gc.dispose(); }

		getInstance().putImageInCache(name, image);
		getInstance().putIconInCache(name, new GamaIcon(name));
		return image;
	}

	@Override
	public ImageDescriptor desc(final String name) {
		final GamaIcon icon = create(name);
		return icon.descriptor();
	}

	@Override
	public Image image(final String name) {
		final GamaIcon icon = create(name);
		return icon.image();
	}

}
