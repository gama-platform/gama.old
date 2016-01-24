/*********************************************************************************************
 *
 *
 * 'GamaIcons.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt;

import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gaml.types.IType;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaIcons /* implements IGamaIcons */ {

	static private GamaIcons instance = new GamaIcons();

	public static GamaIcons getInstance() {
		return instance;
	}

	static final String DEFAULT_PATH = "/icons/";
	static final String SIZER_PREFIX = "sizer_";
	static final String COLOR_PREFIX = "color_";

	Map<String, GamaIcon> iconCache = new THashMap<String, GamaIcon>();
	Map<String, Image> imageCache = new THashMap<String, Image>();
	public static GamaPreferences.Entry<Boolean> CORE_ICONS_BRIGHTNESS = GamaPreferences
		.create("core.icons_brightness", "Icons and buttons dark mode (restart to see the change)", true, IType.BOOL)
		.in(GamaPreferences.UI).group("Icons");
	public static GamaPreferences.Entry<Integer> CORE_ICONS_HEIGHT = GamaPreferences
		.create("core.icons_size", "Size of the icons in the UI (restart to see the change)", 24, IType.INT)
		.among(16, 24).in(GamaPreferences.UI).group("Icons");

	GamaIcon getIcon(final String name) {
		return iconCache.get(name);
	}

	Image putImageInCache(final String name, final Image image) {
		int height = image.getBounds().height;
		int width = image.getBounds().width;
		int desiredHeight = CORE_ICONS_HEIGHT.getValue();
		if ( desiredHeight == 16 ) {
			if ( height <= desiredHeight || name.startsWith("sizer") ) {
				imageCache.put(name, image);
				return image;
			}
			double ratio = height / (double) width;
			int desiredWidth = (int) (desiredHeight * ratio);
			Image new_image = scaleImage(SwtGui.getDisplay(), image, desiredWidth, desiredHeight);
			image.dispose();
			imageCache.put(name, new_image);
			return new_image;
		} else {
			imageCache.put(name, image);
			return image;
		}
	}

	void putIconInCache(final String name, final GamaIcon icon) {
		iconCache.put(name, icon);
	}

	Image getImageInCache(final String code) {
		return imageCache.get(code);
	}

	public static GamaIcon createSizer(final Color color, final int width, final int height) {
		String name = SIZER_PREFIX + width + "x" + height + color.hashCode();
		GamaIcon sizer = getInstance().getIcon(name);
		if ( sizer == null ) {
			// RGB c = new RGB(color.getRed(), color.getGreen(), color.getBlue());
			Image sizerImage = new Image(Display.getDefault(), width, height);
			GC gc = new GC(sizerImage);
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
		return create(s, IGui.PLUGIN_ID);
	}

	public static GamaIcon create(final String code, final String plugin) {
		return create(code, code, plugin);
	}

	public static GamaIcon create(final String code, final String path, final String plugin) {
		GamaIcon result = getInstance().getIcon(code);
		if ( result == null ) {
			result = new GamaIcon(code, path, plugin);
			getInstance().putIconInCache(code, result);
		}
		return result;
	}

	public static GamaIcon createColorIcon(final String s, final GamaUIColor gcolor, final int width,
		final int height) {
		String name = COLOR_PREFIX + s;
		GamaIcon icon = getInstance().getIcon(s);
		if ( icon == null ) {
			// Color color = gcolor.color();
			// RGB c = new RGB(color.getRed(), color.getGreen(), color.getBlue());
			Image image = new Image(Display.getDefault(), width, height);
			GC gc = new GC(image);
			gc.setAntialias(SWT.ON);
			gc.setBackground(gcolor.color());
			gc.fillRoundRectangle(0, 0, width, height, width / 3, height / 3);
			gc.dispose();
			ImageData data = image.getImageData();
			data.transparentPixel = data.palette.getPixel(new RGB(255, 255, 255));
			icon = new GamaIcon(name);
			getInstance().putImageInCache(name, new Image(Display.getDefault(), data));
			image.dispose();
			getInstance().putIconInCache(name, icon);
		}
		return icon;
	}

	/**
	 * Creates an icon that needs to be disposed afterwards
	 * @param gcolor
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image createTempColorIcon(final GamaUIColor gcolor) {
		String name = "color" + gcolor.getRGB().toString();
		GamaIcon icon = getInstance().getIcon(name);
		if ( icon != null ) { return icon.image(); }
		// Color color = gcolor.color();
		GamaIcon blank = create("display.color2");
		Image image = new Image(Display.getDefault(), blank.image().getImageData());
		GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillRoundRectangle(6, 6, 12, 12, 4, 4);
		if ( !gcolor.isDark() ) {
			gc.setForeground(IGamaColors.BLACK.color());
			gc.drawRoundRectangle(6, 6, 12, 12, 4, 4);
		}
		gc.dispose();
		getInstance().putImageInCache(name, image);
		getInstance().putIconInCache(name, new GamaIcon(name));
		return image;
	}

	public static Image createTempRoundColorIcon(final GamaUIColor gcolor) {
		String name = "roundcolor" + gcolor.getRGB().toString();
		GamaIcon icon = getInstance().getIcon(name);
		if ( icon != null ) { return icon.image(); }
		// Color color = gcolor.color();
		GamaIcon blank = create("display.color3");
		Image image = new Image(Display.getDefault(), blank.image().getImageData());
		GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillOval(6, 6, 12, 12);
		if ( !gcolor.isDark() ) {
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
	public static ImageDescriptor getEclipseIconDescriptor(final String icon) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(icon);
	}

	public static Image system(final String icon) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(icon);
	}

	public static Image scaleImage(final Device d, final Image im, final int width, final int height) {
		Rectangle curBounds = im.getBounds();
		// no change required
		if ( curBounds.width == width && curBounds.height == height ) { return im; }

		// create a new image
		Image newIm = new Image(d, width, height);
		GC gc = null;
		try {
			gc = new GC(newIm);

			// put up a border for debugging to help see where the image is
			// located in the available space
			// gc.drawRectangle(0, 0, width - 1, height - 1);

			// image is smaller than requested since, so center
			if ( curBounds.width <= width && curBounds.height <= height ) {
				gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, (width - curBounds.width) / 2,
					(height - curBounds.height) / 2, curBounds.width, curBounds.height);
			} else // too wide or too tall
			{
				// shortcut if the image is perfectly proportional to avoid
				// some of the math below
				if ( curBounds.width == curBounds.height ) {
					gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, 0, 0, width, height);
				}
				// try to keep the proportions of the original image
				// wider than tall
				else if ( curBounds.width > curBounds.height ) {
					// the proportional new height
					int newHt = (int) (height * ((double) curBounds.height / (double) curBounds.width));
					// and center that
					gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, 0, (height - newHt) / 2, width, newHt);
				} else // taller than wide
				{
					// the proportional new width
					int newWd = (int) (width * ((double) curBounds.width / (double) curBounds.height));
					// and center that
					gc.drawImage(im, 0, 0, curBounds.width, curBounds.height, (width - newWd) / 2, 0, newWd, height);

				}
			}
			// clear this up since we successfully created a new image
			im.dispose();
			return newIm;
		} catch (RuntimeException ex) {
			newIm.dispose();
			throw ex;
		} finally {
			if ( gc != null ) {
				gc.dispose();
			}
		}
	}

}
