/*******************************************************************************************************
 *
 * ImageCache.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

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

import msi.gama.outputs.display.AbstractDisplayGraphics;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ImageCache.
 */
public class ImageCache {

	static {
		DEBUG.OFF();
	}

	/** The no image. */
	private static BufferedImage NO_IMAGE;

	/** The cache. */
	private final Cache<String, BufferedImage> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS)
			.removalListener(notification -> ((BufferedImage) notification.getValue()).flush()).build();

	/** The gif cache. */
	private final Cache<String, GifImageContainer> gifCache =
			CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS)
					.removalListener(notification -> ((GifImageContainer) notification.getValue()).dispose()).build();

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
	private static ImageCache instance = new ImageCache();

	/**
	 * Gets the single instance of ImageCache.
	 *
	 * @return single instance of ImageCache
	 */
	public static ImageCache getInstance() { return instance; }

	/**
	 * Instantiates a new image utils.
	 */
	private ImageCache() {}

	/**
	 * fileName is supposed to be already absolute, and to have been checked before, as the calls come from GamaFile
	 *
	 * @param scope
	 * @param fileName
	 * @param useCache
	 * @param extension
	 * @return
	 * @throws IOException
	 */
	public BufferedImage getImageFromFile(final IScope scope, final String fileName, final boolean useCache,
			final IIOReadProgressListener listener, final String extension) {
		if (useCache) {
			final BufferedImage image = cache.getIfPresent(fileName);
			if (image != null) return image;
			final GifImageContainer gif = gifCache.getIfPresent(fileName);
			if (gif != null) return gif.getImage();
		}
		// final String s = scope != null ? FileUtils.constructAbsoluteFilePath(scope, fileName, true) : fileName;
		final File f = new File(fileName);
		final BufferedImage result = getImageFromFile(f, useCache, listener, extension);
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
		final GifImageContainer gif = gifCache.getIfPresent(path);
		if (gif == null) return 1;
		return gif.getFrameCount();
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
	 * @param extension
	 *            the optional extension
	 *
	 * @return the buffered image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedImage privateReadFromFile(final File file, final IIOReadProgressListener listener,
			final String extension) throws IOException {
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
			final GifImageContainer d = new GifImageContainer();
			d.read(new FileInputStream(file.getAbsolutePath()));
			return d.getImage();
		}

		try {
			result = AbstractDisplayGraphics.toCompatibleImage(ioRead(file, listener, extension));
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
	private BufferedImage ioRead(final File file, final IIOReadProgressListener listener, final String extension)
			throws IOException {
		ImageReader imageReader = null;
		ImageInputStream imageInputStream = null;
		try {
			Iterator<ImageReader> readers = ImageIO
					.getImageReadersBySuffix(extension != null ? extension : Files.getFileExtension(file.getName()));
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
	private GifImageContainer privateReadGifFromFile(final File file) throws IOException {
		final GifImageContainer d = new GifImageContainer();
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
	 * @param extension
	 *            the optional extension
	 * @return the image from file
	 */
	public BufferedImage getImageFromFile(final File file, final boolean useCache,
			final IIOReadProgressListener listener, final String extension) {
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
				image = cache.get(file.getAbsolutePath(), () -> privateReadFromFile(file, listener, extension));
			} else {
				image = privateReadFromFile(file, listener, extension);
			}
			return image == getNoImage() ? null : image;
		} catch (final ExecutionException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Clear cache.
	 *
	 * @param pathName
	 *            the path name
	 */
	public void clearCache(final String pathName) {
		cache.invalidate(pathName);
	}

	/**
	 * Force cache image.
	 *
	 * @param im
	 *            the im
	 * @param filename
	 *            the filename
	 */
	public void forceCacheImage(final BufferedImage im, final String filename) {
		cache.put(filename, im);
	}

	/**
	 * Contains entry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @return true, if successful
	 * @date 16 juil. 2023
	 */
	public boolean containsEntry(final String key) {
		return cache.asMap().containsKey(key);
	}

	/**
	 * Gets the image.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @return the image
	 * @date 16 juil. 2023
	 */
	public BufferedImage getImage(final String key) {
		return cache.getIfPresent(key);
	}

}
