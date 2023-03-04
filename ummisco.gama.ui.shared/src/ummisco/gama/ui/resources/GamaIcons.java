/*******************************************************************************************************
 *
 * GamaIcons.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.resources;

import static ummisco.gama.dev.utils.DEBUG.PAD;
import static ummisco.gama.dev.utils.DEBUG.TIMER_WITH_EXCEPTIONS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import msi.gama.application.workbench.IIconProvider;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaIcons implements IIconProvider {

	/** The icon cache. */
	public static Cache<String, GamaIcon> ICON_CACHE = CacheBuilder.newBuilder().build();

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

	/** The Constant DVG_PATH. */
	static public final String SVG_PATH = "/svg/";

	/** The Constant SIZER_PREFIX. */
	static final String SIZER_PREFIX = "sizer_";

	/** The Constant COLOR_PREFIX. */
	static final String COLOR_PREFIX = "color_";

	static {

		DEBUG.ON();
		// Turn it on to have simpler overlays
		// createColorOverlay(IGamaIcons.OVERLAY_OK, IGamaColors.OK);
		// createColorOverlay(IGamaIcons.OVERLAY_CLOSED, GamaColors.get(204, 204, 204));
		// createColorOverlay(IGamaIcons.OVERLAY_CLOUD, GamaColors.get(124, 195, 249));
		// createColorOverlay(IGamaIcons.OVERLAY_ERROR, IGamaColors.ERROR);
		// createColorOverlay(IGamaIcons.OVERLAY_WARNING, GamaColors.get(254, 199, 0));

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
	public static GamaIcon createSizer(final Color color, final int width, final int height) {
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

	/**
	 * Creates the.
	 *
	 * @param s
	 *            the s
	 * @return the gama icon
	 */
	public static GamaIcon create(final String s) {
		try {
			return ICON_CACHE.get(s, () -> new GamaIcon(s));
		} catch (ExecutionException e) {
			return null;
		}
	}

	/**
	 * Creates the.
	 *
	 * @param code
	 *            the code
	 * @param stream
	 *            the stream
	 * @return the gama icon
	 */
	public static GamaIcon create(final String code, final InputStream stream) {
		try {
			return ICON_CACHE.get(code, () -> new GamaIcon(code, stream));
		} catch (ExecutionException e) {
			return null;
		}
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
		try {
			return ICON_CACHE.get(name, () -> {
				final Image image = new Image(WorkbenchHelper.getDisplay(), width, height);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(gcolor.color());
				gc.fillRectangle(0, 0, width, height);
				gc.dispose();
				return new GamaIcon(name, image);
			});
		} catch (ExecutionException e) {
			return null;
		}
	}

	/**
	 * Creates the color overlay 2.
	 *
	 * @param s
	 *            the s
	 * @param gcolor
	 *            the gcolor
	 * @return the gama icon
	 */
	public static GamaIcon createColorOverlay(final String s, final GamaUIColor gcolor) {
		try {
			return ICON_CACHE.get(s, () -> {
				// DEBUG.OUT("Building Icon " + s);
				int dim = 6;
				Image image = new Image(null, dim, dim);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(gcolor.color());
				gc.fillRectangle(0, 0, dim, dim);
				gc.setForeground(gcolor.lighter());
				gc.drawRectangle(0, 0, dim, dim);
				gc.dispose();
				return new GamaIcon(s, image);
			});
		} catch (ExecutionException e) {
			return null;
		}
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
		try {
			return ICON_CACHE.get(name, () -> {
				int size = 16;
				ImageData imData = new ImageData(size, size, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));
				Image image = new Image(WorkbenchHelper.getDisplay(), imData);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(IGamaColors.GRAY.color());
				gc.fillRectangle(0, 0, size, size);
				gc.setBackground(IGamaColors.WHITE.color());
				gc.setForeground(IGamaColors.BLACK.color());
				gc.fillRoundRectangle(0, 0, size - 1, size - 1, 4, 4);
				gc.drawRoundRectangle(0, 0, size - 1, size - 1, 4, 4);
				gc.setBackground(gcolor.color());
				gc.fillRoundRectangle(size / 4, size / 4, size / 2, size / 2, 4, 4);
				// See Issue #3138 about weird artefacts in handmade icons.
				gc.dispose();
				ImageData imageData = image.getImageData();
				imageData.transparentPixel = imageData.palette.getPixel(IGamaColors.GRAY.getRGB());
				image = new Image(WorkbenchHelper.getDisplay(), imageData);
				return new GamaIcon(name, image);
			}).image();
		} catch (ExecutionException e) {
			return null;
		}

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

		try {
			return ICON_CACHE.get(name, () -> {
				// The mask gray value needs to be > tolerance, < 255 - tolerance, < gcolor.red - tolerance, >
				// gcolor.red + tolerance
				int tolerance = 10;
				int gray = gcolor.getRGB().red > 127 ? 63 : 190;
				Color mask = GamaColors.get(gray, gray, gray).color();
				int size = 24;
				ImageData imData = new ImageData(size, size, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));
				Image image = new Image(WorkbenchHelper.getDisplay(), imData);
				final GC gc = new GC(image);
				gc.setAntialias(SWT.ON);
				gc.setBackground(mask);
				gc.fillRectangle(0, 0, size, size);
				gc.setBackground(IGamaColors.WHITE.color());
				gc.setForeground(IGamaColors.BLACK.color());
				gc.fillOval(0, 0, size - 1, size - 1);
				gc.drawOval(0, 0, size - 1, size - 1);
				gc.setBackground(gcolor.color());
				gc.fillOval(size / 4, size / 4, size / 2, size / 2);
				// See Issue #3138 about weird artefacts in handmade icons.
				gc.dispose();

				imData = image.getImageData();

				int grayPixelValue = imData.palette.getPixel(mask.getRGB());
				int[] lineData = new int[imData.width];
				for (int y = 0; y < imData.height; y++) {
					imData.getPixels(0, y, size, lineData, 0);
					// Analyze each pixel value in the line
					for (int x = 0; x < lineData.length; x++) {
						// Extract the red, green and blue component
						int p = lineData[x];
						int r = (p & 0xff0000) >> 16;
						int g = (p & 0x00ff00) >> 8;
						int b = (p & 0x0000ff) >> 0;
						if (r > gray - tolerance && r < gray + tolerance && g > gray - tolerance && g < gray + tolerance
								&& b > gray - tolerance && b < gray + tolerance) {
							imData.setPixel(x, y, grayPixelValue);
						}
					}
				}
				// mask.dispose();
				imData.transparentPixel = grayPixelValue;
				image = new Image(WorkbenchHelper.getDisplay(), imData);

				return new GamaIcon(name, image);
			}).image();
		} catch (ExecutionException e) {
			return null;
		}

	}

	@Override
	public ImageDescriptor desc(final String name) {
		final GamaIcon icon = create(name);
		return icon.descriptor();
	}

	/**
	 * Preload icons.
	 *
	 * @param bundle
	 *            the bundle
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void preloadIcons() {
		try {
			URL url = FileLocator.toFileURL(Platform.getBundle(PLUGIN_ID).getEntry(GamaIcons.DEFAULT_PATH));
			Path path = new File(new URI(url.getProtocol(), url.getPath(), null).normalize()).toPath();
			List<String> files = Files.walk(path).map(f -> path.relativize(f).toString())
					.filter(n -> n.endsWith(".png") && !n.contains("@")).toList();
			TIMER_WITH_EXCEPTIONS(
					PAD("> GAMA: Preloading " + files.size() + " icons", 55, ' ') + PAD(" done in", 15, '_'),
					() -> files.forEach(n -> create(n.replace(".png", "")).image()));
		} catch (IOException | URISyntaxException e) {
			DEBUG.ERR("Error when loading GAMA icons ", e);
		}
	}

}
