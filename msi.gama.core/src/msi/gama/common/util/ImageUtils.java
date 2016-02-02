/*********************************************************************************************
 *
 *
 * 'ImageUtils.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

import gnu.trove.map.hash.THashMap;
import msi.gama.runtime.*;

public class ImageUtils {

	// TODO UCdetector: Remove unused code:
	// /**
	// * Creates a rotated version of the input image.
	// *
	// * @param c The component to get properties useful for painting, e.g. the foreground
	// * or background color.
	// * @param icon the image to be rotated.
	// * @param rotatedAngle the rotated angle, in degree, clockwise. It could be any double
	// * but we will mod it with 360 before using it.
	// *
	// * @return the image after rotating.
	// */
	// public BufferedImage createRotatedImage(final BufferedImage icon,
	// final int rotatedAngle) {
	// // convert rotatedAngle to a value from 0 to 360
	// int originalAngle = rotatedAngle % 360;
	// if ( rotatedAngle != 0 && originalAngle == 0 ) {
	// originalAngle = 360;
	// }
	//
	// // convert originalAngle to a value from 0 to 90
	// int angle = originalAngle % 90;
	// if ( originalAngle != 0.0 && angle == 0.0 ) {
	// angle = 90;
	// }
	//
	// double radian = angle * GamaMath.toRad;
	//
	// int iw = icon.getWidth();
	// int ih = icon.getHeight();
	// int w;
	// int h;
	//
	// if ( originalAngle >= 0 && originalAngle <= 90 || originalAngle > 180
	// && originalAngle <= 270 ) {
	// w = (int) (iw * GamaMath.sin(DEGREE_90 - radian) + ih * GamaMath.sin(radian));
	// h = (int) (iw * GamaMath.sin(radian) + ih * GamaMath.sin(DEGREE_90 - radian));
	// } else {
	// w = (int) (ih * GamaMath.sin(DEGREE_90 - radian) + iw * GamaMath.sin(radian));
	// h = (int) (ih * GamaMath.sin(radian) + iw * GamaMath.sin(DEGREE_90 - radian));
	// }
	// BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	// Graphics2D g2d = image.createGraphics();
	//
	// // calculate the center of the icon.
	// int cx = iw / 2;
	// int cy = ih / 2;
	//
	// // move the graphics center point to the center of the icon.
	// g2d.translate(w / 2, h / 2);
	//
	// // rotate the graphcis about the center point of the icon
	// g2d.rotate(Math.toRadians(originalAngle));
	//
	// g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	// RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	// g2d.drawImage(icon, -cx, -cy, null);
	// g2d.dispose();
	// return image;
	// }

	private final Map<String, BufferedImage[]> cache;

	private static GraphicsConfiguration cachedGC;

	public static GraphicsConfiguration getCachedGC() {
		if ( cachedGC == null ) {
			cachedGC =
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		}
		return cachedGC;
	}

	private static final int POSITIONS = 360;

	private static final int ANGLE_INCREMENT = 360 / POSITIONS;
	
	private static final List<String> tiffExt = Arrays.asList(".tiff",".tif",".TIF", ".TIFF"); 

	// private final static double DEGREE_90 = 90.0 * Math.PI / 180.0;

	private static ImageUtils instance = new ImageUtils();

	public static ImageUtils getInstance() {
		return instance;
	}

	private ImageUtils() {
		cache = new THashMap();
	}

	public boolean contains(final String s) {
		return cache.containsKey(s);
	}

	public BufferedImage getImageFromFile(final IScope scope, final String fileName) throws IOException {
		final BufferedImage image = get(fileName);
		if ( image != null ) { return image; }
		String s = scope != null ? FileUtils.constructAbsoluteFilePath(scope, fileName, true) : fileName;
		final File f = new File(s);
		return getImageFromFile(f);
	}

	public BufferedImage getImageFromFile(final File file) throws IOException {
		BufferedImage image = get(file.getAbsolutePath());
		if (image == null ) {
			String ext = file.getName().substring(file.getName().lastIndexOf("."));
			if (tiffExt.contains(ext)) {
				FileSeekableStream stream = new FileSeekableStream(file.getAbsolutePath());
				TIFFDecodeParam decodeParam = new TIFFDecodeParam();
				decodeParam.setDecodePaletteAsShorts(true);
				ParameterBlock params = new ParameterBlock();
				params.add(stream);
				RenderedOp image1 = JAI.create("tiff", params); 
				image = image1.getAsBufferedImage();
			}
		}
		if ( image != null ) { return image; }
		image = ImageIO.read(file);
		if ( image != null ) {
			add(file.getAbsolutePath(), image);
		}
		return image;
	}

	public void add(final String s, final BufferedImage image) {
		add(s, image, 0); // No rotations for the moment
		// for ( int i = 0; i < POSITIONS; i++ ) {
		// add(s, createRotatedImage(image, i * ANGLE_INCREMENT), i);
		// }
	}

	private void add(final String s, final BufferedImage image, final int position) {
		// OutputManager.debug("Creating rotated images of " + s + " at "
		// + position * ANGLE_INCREMENT);
		if ( !cache.containsKey(s) ) {
			cache.put(s, new BufferedImage[POSITIONS]);
		}
		final BufferedImage[] map = cache.get(s);
		map[position] = toCompatibleImage(image);
	}

	static boolean DEBUG_OPTION = true;

	public static BufferedImage createCompatibleImage(final int width, final int height) {
		BufferedImage new_image = null;
		if ( DEBUG_OPTION || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless() ) {
			new_image =
				new BufferedImage(width != 0 ? width : 1024, height != 0 ? height : 1024, BufferedImage.TYPE_INT_ARGB);
		} else {
			// final GraphicsConfiguration gfx_config =
			// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			new_image = getCachedGC().createCompatibleImage(width, height);
		}
		return new_image;
	}

	public static BufferedImage toCompatibleImage(final BufferedImage image) {
		if ( DEBUG_OPTION || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless() ) { return image; }
		// final GraphicsConfiguration gfx_config =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		/*
		 * if image is already compatible and optimized for current system settings, simply return
		 * it
		 */
		if ( image.getColorModel().equals(getCachedGC().getColorModel()) ) { return image; }

		// image is not optimized, so create a new image that is
		final BufferedImage new_image =
			getCachedGC().createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		final Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image;
	}

	public BufferedImage get(final String s) {
		return get(s, 0);
	}

	private BufferedImage get(final String s, final int angle) {
		final BufferedImage[] map = cache.get(s);
		if ( map == null ) { return null; }
		final int position = (int) Math.round((double) (angle % (360 - ANGLE_INCREMENT)) / ANGLE_INCREMENT);
		return map[position];
	}

	//
	// public static ImageData convertToSWT(final BufferedImage bufferedImage) {
	// if ( bufferedImage.getColorModel() instanceof DirectColorModel ) {
	// final DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
	// final PaletteData palette =
	// new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
	// final ImageData data =
	// new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
	// final WritableRaster raster = bufferedImage.getRaster();
	// final int[] pixelArray = new int[3];
	// for ( int y = 0; y < data.height; y++ ) {
	// for ( int x = 0; x < data.width; x++ ) {
	// raster.getPixel(x, y, pixelArray);
	// final int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
	// data.setPixel(x, y, pixel);
	// }
	// }
	// return data;
	// } else if ( bufferedImage.getColorModel() instanceof IndexColorModel ) {
	// final IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
	// final int size = colorModel.getMapSize();
	// final byte[] reds = new byte[size];
	// final byte[] greens = new byte[size];
	// final byte[] blues = new byte[size];
	// colorModel.getReds(reds);
	// colorModel.getGreens(greens);
	// colorModel.getBlues(blues);
	// final RGB[] rgbs = new RGB[size];
	// for ( int i = 0; i < rgbs.length; i++ ) {
	// rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
	// }
	// final PaletteData palette = new PaletteData(rgbs);
	// final ImageData data =
	// new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
	// data.transparentPixel = colorModel.getTransparentPixel();
	// final WritableRaster raster = bufferedImage.getRaster();
	// final int[] pixelArray = new int[1];
	// for ( int y = 0; y < data.height; y++ ) {
	// for ( int x = 0; x < data.width; x++ ) {
	// raster.getPixel(x, y, pixelArray);
	// data.setPixel(x, y, pixelArray[0]);
	// }
	// }
	// return data;
	// }
	// return null;
	// }

	/**
	 * Convenience method that returns a scaled instance of the
	 * provided {@code BufferedImage}.
	 *
	 * @param img the original image to be scaled
	 * @param targetWidth the desired width of the scaled instance,
	 * in pixels
	 * @param targetHeight the desired height of the scaled instance,
	 * in pixels
	 * @param hint one of the rendering hints that corresponds to {@code RenderingHints.KEY_INTERPOLATION} (e.g. {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
	 * {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR}, {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality if true, this method will use a multi-step
	 * scaling technique that provides higher quality than the usual
	 * one-step technique (only useful in downscaling cases, where {@code targetWidth} or {@code targetHeight} is
	 * smaller than the original dimensions, and generally only when
	 * the {@code BILINEAR} hint is specified)
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	public static BufferedImage downScale(final BufferedImage img, final int targetWidth, final int targetHeight,
		final Object hint, final boolean higherQuality) {

		final int type =
			img.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = img;
		int w, h;
		if ( higherQuality ) {
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
			if ( higherQuality && w > targetWidth ) {
				w /= 2;
				if ( w < targetWidth ) {
					w = targetWidth;
				}
			}

			if ( higherQuality && h > targetHeight ) {
				h /= 2;
				if ( h < targetHeight ) {
					h = targetHeight;
				}
			}

			final BufferedImage tmp = new BufferedImage(w, h, type);
			final Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public static BufferedImage createCompatibleImage(final double x, final double y) {
		return createCompatibleImage((int) x, (int) y);
	}

	public static BufferedImage flipRightSideLeftImage(BufferedImage img) {
		final AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img.getWidth(null), 0);
		final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		img = op.filter(img, null);
		return img;

	}
}