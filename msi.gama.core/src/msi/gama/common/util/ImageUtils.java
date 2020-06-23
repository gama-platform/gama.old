/*******************************************************************************************************
 *
 * msi.gama.common.util.ImageUtils.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sun.media.jai.codec.FileSeekableStream;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;

public class ImageUtils {

	private static BufferedImage NO_IMAGE;
	private final Cache<String, BufferedImage> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
			.removalListener(notification -> ((BufferedImage) notification.getValue()).flush()).build();
	private final Cache<String, BufferedImage> openGLCache =
			CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
					.removalListener(notification -> ((BufferedImage) notification.getValue()).flush()).build();
	private final Cache<String, GifDecoder> gifCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
			.removalListener(notification -> ((GifDecoder) notification.getValue()).dispose()).build();

	private static GraphicsConfiguration cachedGC;

	public static GraphicsConfiguration getCachedGC() {
		if (cachedGC == null) {
			cachedGC = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();
		}
		return cachedGC;
	}

	public static BufferedImage getNoImage() {
		if (NO_IMAGE == null) {
			NO_IMAGE = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		}
		return NO_IMAGE;
	}

	private static final List<String> tiffExt = Arrays.asList(".tiff", ".tif", ".TIF", ".TIFF");
	private static final List<String> gifExt = Arrays.asList(".gif", ".GIF");

	private static ImageUtils instance = new ImageUtils();

	public static ImageUtils getInstance() {
		return instance;
	}

	private ImageUtils() {}

	/**
	 * fileName is supposed to be already absolute, and to have been checked before, as the calls come from GamaFile
	 *
	 * @param scope
	 * @param fileName
	 * @param useCache
	 * @return
	 * @throws IOException
	 */
	public BufferedImage getImageFromFile(final IScope scope, final String fileName, final boolean useCache)
			throws IOException {
		if (useCache) {
			final BufferedImage image = cache.getIfPresent(fileName);
			if (image != null) { return image; }
			final GifDecoder gif = gifCache.getIfPresent(fileName);
			if (gif != null) { return gif.getImage(); }
		}
		// final String s = scope != null ? FileUtils.constructAbsoluteFilePath(scope, fileName, true) : fileName;
		final File f = new File(fileName);
		final BufferedImage result = getImageFromFile(f, useCache, false);
		return result == getNoImage() ? null : result;
	}

	public int getFrameCount(final String path) {
		final GifDecoder gif = gifCache.getIfPresent(path);
		if (gif == null) { return 1; }
		return gif.getFrameCount();
	}

	public int getDuration(final String path) {
		final GifDecoder gif = gifCache.getIfPresent(path);
		if (gif == null) { return 0; }
		return gif.getDuration();
	}

	private BufferedImage privateReadFromFile(final File file, final boolean forOpenGL) throws IOException {
		// DEBUG.OUT("READING " + file.getName());
		BufferedImage result = getNoImage();
		if (file == null) { return result; }
		final String name = file.getName();
		String ext = null;
		if (name.contains(".")) {
			ext = name.substring(file.getName().lastIndexOf('.'));
		}
		if (tiffExt.contains(ext)) {
			try (FileSeekableStream stream = new FileSeekableStream(file.getAbsolutePath())) {
				/**
				 * AD TODO : decodeParam is not used ...
				 */
				// final TIFFDecodeParam decodeParam = new TIFFDecodeParam();
				// decodeParam.setDecodePaletteAsShorts(true);
				final ParameterBlock params = new ParameterBlock();
				params.add(stream);
				final RenderedOp image1 = JAI.create("tiff", params);
				return image1.getAsBufferedImage();
			}
		} else if (gifExt.contains(ext)) {
			final GifDecoder d = new GifDecoder();
			d.read(new FileInputStream(file.getAbsolutePath()));
			return d.getImage();
		}

		try {
			result = forOpenGL ? ImageIO.read(file) : toCompatibleImage(ImageIO.read(file));
		} catch (final Exception e) {
			return getNoImage();
		}
		return result;
	}

	private GifDecoder privateReadGifFromFile(final File file) throws IOException {
		final GifDecoder d = new GifDecoder();
		d.read(new FileInputStream(file.getAbsolutePath()));
		return d;
	}

	public BufferedImage getImageFromFile(final File file, final boolean useCache, final boolean forOpenGL) {
		final BufferedImage image;
		String name, ext = null;
		try {
			name = file.getName();
			if (name.contains(".")) {
				ext = name.substring(file.getName().lastIndexOf('.'));
			}
			if (gifExt.contains(ext)) {
				if (useCache) {
					image = gifCache.get(file.getAbsolutePath(), () -> privateReadGifFromFile(file)).getImage();
				} else {
					image = privateReadGifFromFile(file).getImage();
				}
			} else if (useCache) {
				if (forOpenGL) {
					image = openGLCache.get(file.getAbsolutePath(), () -> privateReadFromFile(file, true));
				} else {
					image = cache.get(file.getAbsolutePath(), () -> privateReadFromFile(file, false));
				}
			} else {
				image = privateReadFromFile(file, forOpenGL);
			}
			return image == getNoImage() ? null : image;
		} catch (final ExecutionException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static boolean NO_GRAPHICS_ENVIRONMENT = false;

	public static BufferedImage createPremultipliedBlankImage(final int width, final int height) {
		return new BufferedImage(width != 0 ? width : 1024, height != 0 ? height : 1024,
				BufferedImage.TYPE_INT_ARGB_PRE);
	}

	public static BufferedImage createCompatibleImage(final int width, final int height, final boolean forOpenGL) {
		if (forOpenGL) { return createPremultipliedBlankImage(width, height); }
		BufferedImage new_image = null;
		if (NO_GRAPHICS_ENVIRONMENT || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()) {
			new_image = new BufferedImage(width != 0 ? width : 1024, height != 0 ? height : 1024,
					BufferedImage.TYPE_INT_ARGB);
		} else {
			new_image = getCachedGC().createCompatibleImage(width, height);
		}
		return new_image;
	}

	public static BufferedImage toCompatibleImage(final BufferedImage image) {
		if (NO_GRAPHICS_ENVIRONMENT || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()) { return image; }

		/*
		 * if image is already compatible and optimized for current system settings, simply return it
		 */
		if (image.getColorModel().equals(getCachedGC().getColorModel())) { return image; }

		// image is not optimized, so create a new image that is
		final BufferedImage new_image =
				getCachedGC().createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
		// new BufferedImage(image.getWidth() != 0 ? image.getWidth() : 1024,
		// image.getHeight() != 0 ? image.getHeight() : 1024, BufferedImage.TYPE_INT_ARGB);
		// get the graphics context of the new image to draw the old image on
		final Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image;
	}

	/**
	 * Convenience method that returns a scaled instance of the provided {@code BufferedImage}.
	 *
	 * @param img
	 *            the original image to be scaled
	 * @param targetWidth
	 *            the desired width of the scaled instance, in pixels
	 * @param targetHeight
	 *            the desired height of the scaled instance, in pixels
	 * @param hint
	 *            one of the rendering hints that corresponds to {@code RenderingHints.KEY_INTERPOLATION} (e.g.
	 *            {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
	 *            {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
	 *            {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality
	 *            if true, this method will use a multi-step scaling technique that provides higher quality than the
	 *            usual one-step technique (only useful in downscaling cases, where {@code targetWidth} or
	 *            {@code targetHeight} is smaller than the original dimensions, and generally only when the
	 *            {@code BILINEAR} hint is specified)
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	public static BufferedImage resize(final BufferedImage img, final int targetWidth, final int targetHeight,
			final Object hint, final boolean higherQuality) {

		final int type =
				img.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			final BufferedImage tmp = new BufferedImage(w, h, type);
			final Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	public static BufferedImage resize(final BufferedImage snapshot, final int width, final int height) {
		if (width == snapshot.getWidth() && height == snapshot.getHeight()) { return snapshot; }
		return resize(snapshot, width, height, RenderingHints.VALUE_INTERPOLATION_BILINEAR, false);
	}

	public void clearCache(final String pathName) {
		cache.invalidate(pathName);
		openGLCache.invalidate(pathName);

	}

}
