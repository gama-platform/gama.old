/*********************************************************************************************
 *
 * 'GamaIcons.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaIcons /* implements IGamaIcons */ {

	public static final String PLUGIN_ID = "ummisco.gama.ui.shared";

	static private GamaIcons instance = new GamaIcons();

	public static GamaIcons getInstance() {
		return instance;
	}

	static public final String DEFAULT_PATH = "/icons/";
	static final String SIZER_PREFIX = "sizer_";
	static final String COLOR_PREFIX = "color_";

	Map<String, GamaIcon> iconCache = new HashMap<String, GamaIcon>();
	Map<String, Image> imageCache = new HashMap<String, Image>();

	GamaIcon getIcon(final String name) {
		return iconCache.get(name);
	}

	Image putImageInCache(final String name, final Image image) {
		// final int height = image.getBounds().height;
		// final int width = image.getBounds().width;
		// final int desiredHeight = CORE_ICONS_HEIGHT.getValue();
		// if (desiredHeight == 16) {
		// if (height <= desiredHeight || name.startsWith("sizer")) {
		// imageCache.put(name, image);
		// return image;
		// }
		// final double ratio = height / (double) width;
		// final int desiredWidth = (int) (desiredHeight * ratio);
		// final Image new_image = scaleImage(Display.getCurrent(), image,
		// desiredWidth, desiredHeight);
		// image.dispose();
		// imageCache.put(name, new_image);
		// return new_image;
		// } else {
		imageCache.put(name, image);
		return image;
		// }
	}

	void putIconInCache(final String name, final GamaIcon icon) {
		iconCache.put(name, icon);
	}

	Image getImageInCache(final String code) {
		return imageCache.get(code);
	}

	public static GamaIcon createSizer(final Color color, final int width, final int height) {
		final String name = SIZER_PREFIX + width + "x" + height + color.hashCode();
		GamaIcon sizer = getInstance().getIcon(name);
		if (sizer == null) {
			// RGB c = new RGB(color.getRed(), color.getGreen(),
			// color.getBlue());
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

	public static GamaIcon create(final String s) {
		return create(s, PLUGIN_ID);
	}

	public static GamaIcon create(final String code, final String plugin) {
		return create(code, code, plugin);
	}

	public static GamaIcon create(final String code, final String path, final String plugin) {
		GamaIcon result = getInstance().getIcon(code);
		if (result == null) {
			result = new GamaIcon(code, path, plugin);
			getInstance().putIconInCache(code, result);
		}
		return result;
	}

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
		if (icon != null) { return icon.image(); }
		// Color color = gcolor.color();
		final GamaIcon blank = create("display.color2");
		final Image image = new Image(WorkbenchHelper.getDisplay(), blank.image().getImageData());
		final GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillRoundRectangle(6, 6, 12, 12, 4, 4);
		if (!gcolor.isDark()) {
			gc.setForeground(IGamaColors.BLACK.color());
			gc.drawRoundRectangle(6, 6, 12, 12, 4, 4);
		}
		gc.dispose();
		getInstance().putImageInCache(name, image);
		getInstance().putIconInCache(name, new GamaIcon(name));
		return image;
	}

	public static Image createTempRoundColorIcon(final GamaUIColor gcolor) {
		final String name = "roundcolor" + gcolor.getRGB().toString();
		final GamaIcon icon = getInstance().getIcon(name);
		if (icon != null) { return icon.image(); }
		// Color color = gcolor.color();
		final GamaIcon blank = create("display.color3");
		final Image image = new Image(WorkbenchHelper.getDisplay(), blank.image().getImageData());
		final GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillOval(6, 6, 12, 12);
		if (!gcolor.isDark()) {
			gc.setForeground(IGamaColors.BLACK.color());
			gc.drawOval(6, 6, 12, 12);
		}
		gc.dispose();
		getInstance().putImageInCache(name, image);
		getInstance().putIconInCache(name, new GamaIcon(name));
		return image;
	}

	/*
	 * Use "ISharedImages.field"
	 */
	// public static ImageDescriptor getEclipseIconDescriptor(final String icon)
	// {
	// return
	// PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(icon);
	// }
	//
	// public static Image system(final String icon) {
	// return PlatformUI.getWorkbench().getSharedImages().getImage(icon);
	// }

	public static Image scaleImage(final Device d, final Image im, final int width, final int height) {
		final Rectangle curBounds = im.getBounds();
		// no change required
		if (curBounds.width == width && curBounds.height == height) { return im; }

		// create a new image
		final Image newIm = new Image(d, width, height);
		GC gc = null;
		try {
			gc = new GC(newIm);

			// put up a border for debugging to help see where the image is
			// located in the available space
			// gc.drawRectangle(0, 0, width - 1, height - 1);

			// image is smaller than requested since, so center
			if (curBounds.width <= width && curBounds.height <= height) {
				gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, (width - curBounds.width) / 2,
						(height - curBounds.height) / 2, curBounds.width, curBounds.height);
			} else // too wide or too tall
			{
				// shortcut if the image is perfectly proportional to avoid
				// some of the math below
				if (curBounds.width == curBounds.height) {
					gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, 0, 0, width, height);
				}
				// try to keep the proportions of the original image
				// wider than tall
				else if (curBounds.width > curBounds.height) {
					// the proportional new height
					final int newHt = (int) (height * ((double) curBounds.height / (double) curBounds.width));
					// and center that
					gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, 0, (height - newHt) / 2, width, newHt);
				} else // taller than wide
				{
					// the proportional new width
					final int newWd = (int) (width * ((double) curBounds.width / (double) curBounds.height));
					// and center that
					gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, (width - newWd) / 2, 0, newWd, height);

				}
			}
			// clear this up since we successfully created a new image
			im.dispose();
			return newIm;
		} catch (final RuntimeException ex) {
			newIm.dispose();
			throw ex;
		} finally {
			if (gc != null) {
				gc.dispose();
			}
		}
	}

}
