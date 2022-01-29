/*******************************************************************************************************
 *
 * ImageUtils.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.Files;
import com.sun.media.jai.codec.FileSeekableStream;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ImageUtils.
 */
public class ImageUtils {

	static {
		DEBUG.OFF();
	}

	/** The no image. */
	private static BufferedImage NO_IMAGE;

	/** The cache. */
	private final Cache<String, BufferedImage> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
			.removalListener(notification -> ((BufferedImage) notification.getValue()).flush()).build();

	/** The open GL cache. */
	private final Cache<String, BufferedImage> openGLCache =
			CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
					.removalListener(notification -> ((BufferedImage) notification.getValue()).flush()).build();

	/** The gif cache. */
	private final Cache<String, GifDecoder> gifCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
			.removalListener(notification -> ((GifDecoder) notification.getValue()).dispose()).build();

	/** The cached GC. */
	private static GraphicsConfiguration cachedGC;

	/**
	 * Gets the cached GC.
	 *
	 * @return the cached GC
	 */
	public static GraphicsConfiguration getCachedGC() {
		if (cachedGC == null) {
			DEBUG.OUT("Creating cached Graphics Configuration");
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			DEBUG.OUT("Local Graphics Environment selected");
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			DEBUG.OUT("Default Graphics Device selected");
			cachedGC = gd.getDefaultConfiguration();
			DEBUG.OUT("Default Graphics Configuration selected");
		}
		return cachedGC;
	}

	/**
	 * Gets the no image.
	 *
	 * @return the no image
	 */
	public static BufferedImage getNoImage() {
		if (NO_IMAGE == null) { NO_IMAGE = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB); }
		return NO_IMAGE;
	}

	/** The Constant tiffExt. */
	private static final List<String> tiffExt = Arrays.asList(".tiff", ".tif", ".TIF", ".TIFF");

	/** The Constant gifExt. */
	private static final List<String> gifExt = Arrays.asList(".gif", ".GIF");

	/** The instance. */
	private static ImageUtils instance = new ImageUtils();

	/**
	 * Gets the single instance of ImageUtils.
	 *
	 * @return single instance of ImageUtils
	 */
	public static ImageUtils getInstance() { return instance; }

	/**
	 * Instantiates a new image utils.
	 */
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
	public BufferedImage getImageFromFile(final IScope scope, final String fileName, final boolean useCache,
			final IIOReadProgressListener listener) throws IOException {
		if (useCache) {
			final BufferedImage image = cache.getIfPresent(fileName);
			if (image != null) return image;
			final GifDecoder gif = gifCache.getIfPresent(fileName);
			if (gif != null) return gif.getImage();
		}
		// final String s = scope != null ? FileUtils.constructAbsoluteFilePath(scope, fileName, true) : fileName;
		final File f = new File(fileName);
		final BufferedImage result = getImageFromFile(f, useCache, false, listener);
		return result == getNoImage() ? null : result;
	}

	/**
	 * Gets the frame count.
	 *
	 * @param path
	 *            the path
	 * @return the frame count
	 */
	public int getFrameCount(final String path) {
		final GifDecoder gif = gifCache.getIfPresent(path);
		if (gif == null) return 1;
		return gif.getFrameCount();
	}

	/**
	 * Gets the duration.
	 *
	 * @param path
	 *            the path
	 * @return the duration
	 */
	public int getDuration(final String path) {
		final GifDecoder gif = gifCache.getIfPresent(path);
		if (gif == null) return 0;
		return gif.getDuration();
	}

	/**
	 * Private read from file.
	 *
	 * @param file
	 *            the file
	 * @param forOpenGL
	 *            the for open GL
	 * @param listener
	 *            the listener
	 * @return the buffered image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedImage privateReadFromFile(final File file, final boolean forOpenGL,
			final IIOReadProgressListener listener) throws IOException {
		// DEBUG.OUT("READING " + file.getName());
		BufferedImage result = getNoImage();
		if (file == null) return result;
		final String name = file.getName();
		String ext = null;
		if (name.contains(".")) { ext = name.substring(file.getName().lastIndexOf('.')); }
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
		}
		if (gifExt.contains(ext)) {
			final GifDecoder d = new GifDecoder();
			d.read(new FileInputStream(file.getAbsolutePath()));
			return d.getImage();
		}

		try {
			result = forOpenGL ? ioRead(file, listener) : toCompatibleImage(ioRead(file, listener));
		} catch (final Exception e) {
			return getNoImage();
		}
		return result;
	}

	/**
	 * Io read.
	 *
	 * @param file
	 *            the file
	 * @param listener
	 *            the listener
	 * @return the buffered image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedImage ioRead(final File file, final IIOReadProgressListener listener) throws IOException {
		ImageReader imageReader = null;
		ImageInputStream imageInputStream = null;
		try {
			Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(Files.getFileExtension(file.getName()));
			imageReader = readers.next();
			imageInputStream = ImageIO.createImageInputStream(file);

		} catch (final Exception e) {
			Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix("jpg");
			imageReader = readers.next();
			imageInputStream = ImageIO.createImageInputStream(file);
		}

		imageReader.setInput(imageInputStream, false);
		if (listener != null) { imageReader.addIIOReadProgressListener(listener); }
		return imageReader.read(0);
	}

	/**
	 * Private read gif from file.
	 *
	 * @param file
	 *            the file
	 * @return the gif decoder
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private GifDecoder privateReadGifFromFile(final File file) throws IOException {
		final GifDecoder d = new GifDecoder();
		d.read(new FileInputStream(file.getAbsolutePath()));
		return d;
	}

	/**
	 * Gets the image from file.
	 *
	 * @param file
	 *            the file
	 * @param useCache
	 *            the use cache
	 * @param forOpenGL
	 *            the for open GL
	 * @param listener
	 *            the listener
	 * @return the image from file
	 */
	public BufferedImage getImageFromFile(final File file, final boolean useCache, final boolean forOpenGL,
			final IIOReadProgressListener listener) {
		final BufferedImage image;
		String name, ext = null;
		try {
			name = file.getName();
			if (name.contains(".")) { ext = name.substring(file.getName().lastIndexOf('.')); }
			if (gifExt.contains(ext)) {
				if (useCache) {
					image = gifCache.get(file.getAbsolutePath(), () -> privateReadGifFromFile(file)).getImage();
				} else {
					image = privateReadGifFromFile(file).getImage();
				}
			} else if (useCache) {
				if (forOpenGL) {
					image = openGLCache.get(file.getAbsolutePath(), () -> privateReadFromFile(file, true, listener));
				} else {
					image = cache.get(file.getAbsolutePath(), () -> privateReadFromFile(file, false, listener));
				}
			} else {
				image = privateReadFromFile(file, forOpenGL, listener);
			}
			return image == getNoImage() ? null : image;
		} catch (final ExecutionException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** The no graphics environment. */
	static boolean NO_GRAPHICS_ENVIRONMENT = false;

	/**
	 * Creates the premultiplied blank image.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the buffered image
	 */
	public static BufferedImage createPremultipliedBlankImage(final int width, final int height) {
		return new BufferedImage(width != 0 ? width : 1024, height != 0 ? height : 1024,
				BufferedImage.TYPE_INT_ARGB_PRE);
	}

	/**
	 * Creates the compatible image.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param forOpenGL
	 *            the for open GL
	 * @return the buffered image
	 */
	public static BufferedImage createCompatibleImage(final int width, final int height, final boolean forOpenGL) {
		if (forOpenGL) return createPremultipliedBlankImage(width, height);
		BufferedImage new_image = null;
		if (NO_GRAPHICS_ENVIRONMENT || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()) {
			new_image = new BufferedImage(width != 0 ? width : 1024, height != 0 ? height : 1024,
					BufferedImage.TYPE_INT_ARGB);
		} else {
			new_image = getCachedGC().createCompatibleImage(width, height);
		}
		return new_image;
	}

	/**
	 * To compatible image.
	 *
	 * @param image
	 *            the image
	 * @return the buffered image
	 */
	public static BufferedImage toCompatibleImage(final BufferedImage image) {
		/*
		 * if image is already compatible and optimized for current system settings, simply return it
		 */
		if (NO_GRAPHICS_ENVIRONMENT || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()
				|| image.getColorModel().equals(getCachedGC().getColorModel()))
			return image;

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
				if (w < targetWidth) { w = targetWidth; }
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) { h = targetHeight; }
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

	/**
	 * Resize.
	 *
	 * @param snapshot
	 *            the snapshot
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the buffered image
	 */
	public static BufferedImage resize(final BufferedImage snapshot, final int width, final int height) {
		if (width == snapshot.getWidth() && height == snapshot.getHeight()) return snapshot;
		return resize(snapshot, width, height, RenderingHints.VALUE_INTERPOLATION_BILINEAR, false);
	}

	/**
	 * Clear cache.
	 *
	 * @param pathName
	 *            the path name
	 */
	public void clearCache(final String pathName) {
		cache.invalidate(pathName);
		openGLCache.invalidate(pathName);

	}

}
