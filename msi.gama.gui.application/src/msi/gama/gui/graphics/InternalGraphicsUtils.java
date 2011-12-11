/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.graphics;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.Icon;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Provides a bunch of Utility methods for converting between AWT and SWT
 * 
 * @author jesse
 * @since 1.1.0
 */
public final class InternalGraphicsUtils {

	/**
	 * Convert an SWT Image to a BufferedImage - this one rips the ImageData out of the live Image;
	 * and then copies it into a BufferedImage.
	 * 
	 */
	public static BufferedImage convertToAWT(final Image image) {
		ImageData data = image.getImageData();
		return convertToAWT(data);
	}

	/**
	 * Converts an SWT ImageData to a BufferedImage - It isn't incredibly optimized so be careful :)
	 * <p>
	 * We should be able to use use JAI to produce a RenderedImage around the provided ImageData. It
	 * wound be a buffered image but it will be something that can efficiently be drawn when
	 * printing.
	 * </p>
	 * @return a Buffered Image
	 */
	public static BufferedImage convertToAWT(final ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if ( palette.isDirect ) {
			// no alpha data?
			if ( data.alphaData == null ) {
				colorModel =
					new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000);
				BufferedImage bufferedImage =
					new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(
						data.width, data.height), false, null);
				WritableRaster raster = bufferedImage.getRaster();
				int[] pixelArray = new int[4];
				for ( int y = 0; y < data.height; y++ ) {
					for ( int x = 0; x < data.width; x++ ) {
						int pixel = data.getPixel(x, y);
						RGB rgb = palette.getRGB(pixel);
						pixelArray[0] = rgb.red;
						pixelArray[1] = rgb.green;
						pixelArray[2] = rgb.blue;
						if ( pixel == data.transparentPixel ) {
							pixelArray[3] = 0; // transparent
						} else {
							pixelArray[3] = 255; // opaque
						}
						raster.setPixels(x, y, 1, 1, pixelArray);
					}
				}
				int w = bufferedImage.getWidth();
				int h = bufferedImage.getHeight();
				Raster ras = bufferedImage.getData();
				for ( int i = 0; i < w; i++ ) {
					for ( int j = 0; j < h; j++ ) {
						ras.getPixel(i, j, new double[4]);
					}
				}

				return bufferedImage;
			}
			colorModel = new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000);
			BufferedImage bufferedImage =
				new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width,
					data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[4];
			for ( int y = 0; y < data.height; y++ ) {
				for ( int x = 0; x < data.width; x++ ) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					pixelArray[3] = data.getAlpha(x, y);
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;

			// la paleta swt no es directa ¿?¿?¿?

			// ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
			// colorModel = new DirectColorModel(colorSpace, data.depth, palette.redMask,
			// palette.greenMask, palette.blueMask, 0, false, DataBuffer.TYPE_INT);
			// // colorModel = new DirectColorModel(data.depth, palette.redMask,
			// // palette.greenMask, palette.blueMask);
			// BufferedImage bufferedImage = new BufferedImage(colorModel,
			// colorModel.createCompatibleWritableRaster(data.width,
			// data.height), false, null);
			// WritableRaster raster = bufferedImage.getRaster();
			// int[] pixelArray = new int[3];
			// for (int y = 0; y < data.height; y++) {
			// for (int x = 0; x < data.width; x++) {
			// int pixel = data.getPixel(x, y);
			// RGB rgb = palette.getRGB(pixel);
			// pixelArray[0] = rgb.red;
			// pixelArray[1] = rgb.green;
			// pixelArray[2] = rgb.blue;
			// raster.setPixels(x, y, 1, 1, pixelArray);
			// }
			// }
			// return bufferedImage;
		}
		RGB[] rgbs = palette.getRGBs();
		byte[] red = new byte[rgbs.length];
		byte[] green = new byte[rgbs.length];
		byte[] blue = new byte[rgbs.length];
		for ( int i = 0; i < rgbs.length; i++ ) {
			RGB rgb = rgbs[i];
			red[i] = (byte) rgb.red;
			green[i] = (byte) rgb.green;
			blue[i] = (byte) rgb.blue;
		}
		if ( data.transparentPixel != -1 ) {
			colorModel =
				new IndexColorModel(data.depth, rgbs.length, red, green, blue,
					data.transparentPixel);
		} else {
			colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
		}
		BufferedImage bufferedImage =
			new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width,
				data.height), false, null);
		WritableRaster raster = bufferedImage.getRaster();
		int[] pixelArray = new int[1];
		for ( int y = 0; y < data.height; y++ ) {
			for ( int x = 0; x < data.width; x++ ) {
				int pixel = data.getPixel(x, y);
				pixelArray[0] = pixel;
				raster.setPixel(x, y, pixelArray);
			}
		}
		return bufferedImage;
	}

	/**
	 * Converts the shape to a path object. Remember to dispose of the path object when done.
	 * 
	 * @param shape
	 * @return the shape converted to a {@link Path} object.
	 */
	public static Path convertToPath(final Shape shape, final Device device) {
		InternalGraphicsUtils.checkAccess();
		PathIterator p = shape.getPathIterator(IInternalGraphics.AFFINE_TRANSFORM);

		return InternalGraphicsUtils.createPath(p, device);
	}

	public static Path createPath(final PathIterator p, final Device device) {
		if ( p.isDone() ) { return null; }

		float[] current = new float[6];
		Path path = new Path(device);
		while (!p.isDone()) {
			int result = p.currentSegment(current);
			switch (result) {
				case PathIterator.SEG_CLOSE:
					path.close();
				break;
				case PathIterator.SEG_LINETO:
					path.lineTo(current[0], current[1]);
				break;
				case PathIterator.SEG_MOVETO:
					path.moveTo(current[0], current[1]);
				break;
				case PathIterator.SEG_QUADTO:
					path.quadTo(current[0], current[1], current[2], current[3]);
				break;
				case PathIterator.SEG_CUBICTO:
					path.cubicTo(current[0], current[1], current[2], current[3], current[4],
						current[5]);
				break;
				default:
			}
			p.next();
		}
		return path;
	}

	/**
	 * Creates an image with a depth of 24 and has a transparency channel.
	 * 
	 * @param device device to use for creating the image
	 * @param width the width of the final image
	 * @param height the height of the final image
	 * @return an image with a depth of 24 and has a transparency channel.
	 */
	public static Image createDefaultImage(final Device device, final int width, final int height) {
		InternalGraphicsUtils.checkAccess();
		ImageData swtdata = null;
		PaletteData palette;
		int depth;

		depth = 24;
		palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		swtdata = new ImageData(width, height, depth, palette);
		swtdata.transparentPixel = -1;
		swtdata.alpha = -1;
		swtdata.alphaData = new byte[swtdata.data.length];
		for ( int i = 0; i < swtdata.alphaData.length; i++ ) {
			swtdata.alphaData[i] = 0;
		}
		return new Image(device, swtdata);

	}

	public static Image createDefaultImage(final Display display, final int width, final int height) {
		InternalGraphicsUtils.checkAccess();
		ImageData swtdata = null;
		PaletteData palette;
		int depth;

		depth = 24;
		palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		swtdata = new ImageData(width, height, depth, palette);
		swtdata.transparentPixel = -1;
		// swtdata.transparentPixel = -1;
		swtdata.alpha = -1;
		swtdata.alphaData = new byte[swtdata.data.length];
		for ( int i = 0; i < swtdata.alphaData.length; i++ ) {
			swtdata.alphaData[i] = (byte) 255;
		}

		return new Image(display, swtdata);
	}

	/** Create a buffered image that can be be converted to SWTland later */
	public static BufferedImage createBufferedImage(final int w, final int h) {
		// AWTSWTImageUtils.checkAccess();
		return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
	}

	public static Image createSWTImage(final RenderedImage image, final boolean transparent) {
		InternalGraphicsUtils.checkAccess();

		ImageData data;
		if ( image instanceof BufferedImage ) {
			data = InternalGraphicsUtils.createImageData((BufferedImage) image);
		} else {
			data = InternalGraphicsUtils.createImageData(image, transparent);
		}

		return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
	}

	public static ImageData createImageData(final RenderedImage image, final boolean transparent) {
		InternalGraphicsUtils.checkAccess();

		ImageData swtdata = null;
		int width = image.getWidth();
		int height = image.getHeight();
		PaletteData palette;
		int depth;

		depth = 24;
		palette = new PaletteData(0xFF, 0xFF00, 0xFF0000);
		swtdata = new ImageData(width, height, depth, palette);
		Raster raster = image.getData();
		int numbands = raster.getNumBands();
		int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height * numbands]);
		int step = swtdata.depth / 8;

		byte[] data = swtdata.data;
		swtdata.transparentPixel = -1;

		int baseindex = 0;
		for ( int y = 0; y < height; y++ ) {
			int idx = (0 + y) * swtdata.bytesPerLine + 0 * step;

			for ( int x = 0; x < width; x++ ) {
				int pixel = x + y * width;
				baseindex = pixel * numbands;

				data[idx++] = (byte) awtdata[baseindex + 2];
				data[idx++] = (byte) awtdata[baseindex + 1];
				data[idx++] = (byte) awtdata[baseindex];
				// if ( numbands == 4 && transparent ) {
				// swtdata.setAlpha(x, y, awtdata[baseindex + 3]);
				// }
			}
		}
		return swtdata;
	}

	public static ImageDescriptor createImageDescriptor(final RenderedImage image,
		final boolean transparent) {
		InternalGraphicsUtils.checkAccess();
		return new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				return createImageData(image, transparent);
			}
		};
	}

	/**
	 * Creates an image descriptor that from the source image.
	 * 
	 * @param image source image
	 * @return an image descriptor that from the source image.
	 */
	public static ImageDescriptor createImageDescriptor(final BufferedImage image) {
		InternalGraphicsUtils.checkAccess();
		return new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				return InternalGraphicsUtils.createImageData(image);
			}
		};
	}

	/**
	 * Converts a BufferedImage to an SWT Image. You are responsible for disposing the created
	 * image. This method is faster than creating a SWT image from a RenderedImage so use this
	 * method if possible.
	 * 
	 * @param image source image.
	 * @return a swtimage showing the source image.
	 */
	public static Image convertToSWTImage(final BufferedImage image) {
		InternalGraphicsUtils.checkAccess();
		ImageData data;
		data = InternalGraphicsUtils.createImageData(image);

		return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
	}

	/**
	 * Creates an ImageData from the 0,0,width,height section of the source BufferedImage.
	 * <p>
	 * This method is faster than creating the ImageData from a RenderedImage so use this method if
	 * possible.
	 * </p>
	 * 
	 * @param image source image.
	 * @return an ImageData from the 0,0,width,height section of the source BufferedImage
	 */
	public static ImageData createImageData(final BufferedImage image) {
		InternalGraphicsUtils.checkAccess();

		if ( image.getType() != BufferedImage.TYPE_3BYTE_BGR ) { return createImageData(image,
			image.getTransparency() != Transparency.OPAQUE); }

		int width = image.getWidth();
		int height = image.getHeight();
		int bands = image.getColorModel().getColorSpace().getNumComponents();
		int depth = 24;
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		ImageData data =
			new ImageData(width, height, depth, new PaletteData(0x0000ff, 0x00ff00, 0xff0000),
				width * bands, pixels);
		return data;
	}

	/**
	 * Converts a RenderedImage to an SWT Image. You are responsible for disposing the created
	 * image. This method is slower than calling
	 * {@link InternalSWTGraphics#createSWTImage(BufferedImage, int, int)}.
	 * 
	 * @param image source image.
	 * @param width the width of the final image
	 * @param height the height of the final image
	 * @return a swtimage showing the 0,0,width,height rectangle of the source image.
	 */
	public static Image createSWTImage(final RenderedImage image) {
		InternalGraphicsUtils.checkAccess();
		ImageData data = InternalGraphicsUtils.createImageData(image);

		return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
	}

	/**
	 * Creates an ImageData from the source RenderedImage.
	 * <p>
	 * This method is slower than using {@link createImageData}.
	 * </p>
	 * 
	 * @param image source image.
	 * @return an ImageData from the source RenderedImage.
	 */
	public static ImageData createImageData(final RenderedImage image) {
		InternalGraphicsUtils.checkAccess();

		if ( image instanceof BufferedImage ) { return createImageData((BufferedImage) image); }
		int depth = 24;
		int width = image.getWidth();
		int height = image.getHeight();
		byte[] pixels = ((DataBufferByte) image.getTile(0, 0).getDataBuffer()).getData();
		ImageData data =
			new ImageData(width, height, depth, new PaletteData(0xff0000, 0x00ff00, 0x0000ff),
				width, pixels);
		return data;
	}

	public static java.awt.Color swtColor2awtColor(final GC gc, final Color swt) {
		java.awt.Color awt =
			new java.awt.Color(swt.getRed(), swt.getGreen(), swt.getBlue(), gc.getAlpha());
		return awt;
	}

	public static Color awtColor2swtColor(final Display display, final java.awt.Color awt) {
		return new Color(display, awt.getRed(), awt.getGreen(), awt.getBlue());
	}

	static void checkAccess() {
		if ( Display.getCurrent() == null ) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}
	}

	/**
	 * Converts SWT FontData to a AWT Font
	 * 
	 * @param fontData the font data
	 * @return the equivalent AWT font
	 */
	public static java.awt.Font swtFontToAwt(final FontData fontData) {
		int style = java.awt.Font.PLAIN;
		if ( (fontData.getStyle() & SWT.BOLD) == SWT.BOLD ) {
			style = java.awt.Font.BOLD;
		}
		if ( (fontData.getStyle() & SWT.ITALIC) == SWT.ITALIC ) {
			style |= java.awt.Font.ITALIC;
		}

		java.awt.Font font = new java.awt.Font(fontData.getName(), style, fontData.getHeight());
		return font;
	}

	/**
	 * Converts an AWTFont to a SWT Font
	 * 
	 * @param font and AWT Font
	 * @param fontRegistry
	 * @return the equivalent SWT Font
	 */
	public static org.eclipse.swt.graphics.Font awtFontToSwt(final java.awt.Font font,
		final FontRegistry fontRegistry) {
		String fontName = font.getFontName();
		if ( fontRegistry.hasValueFor(fontName) ) { return fontRegistry.get(fontName); }

		int style = 0;
		if ( (font.getStyle() & java.awt.Font.BOLD) == java.awt.Font.BOLD ) {
			style = SWT.BOLD;
		}
		if ( (font.getStyle() & java.awt.Font.ITALIC) == java.awt.Font.ITALIC ) {
			style |= SWT.ITALIC;
		}
		FontData data = new FontData(fontName, font.getSize(), style);
		fontRegistry.put(fontName, new FontData[] { data });
		return fontRegistry.get(fontName);
	}

	/**
	 * Takes an AWT Font.
	 * 
	 * @param style
	 * @return
	 */
	public static int toFontStyle(final java.awt.Font f) {
		int s = SWT.NORMAL;

		if ( f.isItalic() ) {
			s = s | SWT.ITALIC;
		}
		if ( f.isBold() ) {
			s = s | SWT.BOLD;
		}
		return s;
	}

	public static Icon imageDescriptor2awtIcon(final ImageDescriptor imageDescriptor) {
		Icon awtIcon = new Icon() {

			ImageData imageData = imageDescriptor.getImageData();

			@Override
			public int getIconHeight() {
				return imageData.width;
			}

			@Override
			public int getIconWidth() {
				return imageData.height;
			}

			@Override
			public void paintIcon(final Component comp, final Graphics g, final int x, final int y) {
				BufferedImage image = convertToAWT(imageData);
				g.drawImage(image, x, y, null);
			}

		};
		return awtIcon;
	}

	/**
	 * Converts a Swing {@link Icon} to an {@link ImageDescriptor}
	 * 
	 * @param icon icon to convert
	 * @return an ImageDescriptor
	 */
	public static ImageDescriptor awtIcon2ImageDescriptor(final Icon icon) {
		ImageDescriptor descriptor = new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				BufferedImage image =
					createBufferedImage(icon.getIconWidth(), icon.getIconHeight());
				Graphics2D g = image.createGraphics();
				try {
					icon.paintIcon(null, g, 0, 0);
				} finally {
					g.dispose();
				}
				ImageData data = createImageData(image);
				return data;
			}

		};
		return descriptor;
	}

	/**
	 * Given an arbitrary rectangle, get the rectangle with the given transform. The result
	 * rectangle is positive width and positive height.
	 * @param af AffineTransform
	 * @param src source rectangle
	 * @return rectangle after transform with positive width and height
	 */
	public static Rectangle transformRect(final AffineTransform af, Rectangle src) {
		Rectangle dest = new Rectangle(0, 0, 0, 0);
		src = absRect(src);
		Point p1 = new Point(src.x, src.y);
		p1 = transformPoint(af, p1);
		dest.x = p1.x;
		dest.y = p1.y;
		dest.width = (int) (src.width * af.getScaleX());
		dest.height = (int) (src.height * af.getScaleY());
		return dest;
	}

	/**
	 * Given an arbitrary rectangle, get the rectangle with the inverse given transform. The result
	 * rectangle is positive width and positive height.
	 * @param af AffineTransform
	 * @param src source rectangle
	 * @return rectangle after transform with positive width and height
	 */
	public static Rectangle inverseTransformRect(final AffineTransform af, Rectangle src) {
		Rectangle dest = new Rectangle(0, 0, 0, 0);
		src = absRect(src);
		Point p1 = new Point(src.x, src.y);
		p1 = inverseTransformPoint(af, p1);
		dest.x = p1.x;
		dest.y = p1.y;
		dest.width = (int) (src.width / af.getScaleX());
		dest.height = (int) (src.height / af.getScaleY());
		return dest;
	}

	/**
	 * Given an arbitrary point, get the point with the given transform.
	 * @param af affine transform
	 * @param pt point to be transformed
	 * @return point after tranform
	 */
	public static Point transformPoint(final AffineTransform af, final Point pt) {
		Point2D src = new Point2D.Float(pt.x, pt.y);
		Point2D dest = af.transform(src, null);
		Point point = new Point((int) Math.floor(dest.getX()), (int) Math.floor(dest.getY()));
		return point;
	}

	/**
	 * Given an arbitrary point, get the point with the inverse given transform.
	 * @param af AffineTransform
	 * @param pt source point
	 * @return point after transform
	 */
	public static Point inverseTransformPoint(final AffineTransform af, final Point pt) {
		Point2D src = new Point2D.Float(pt.x, pt.y);
		try {
			Point2D dest = af.inverseTransform(src, null);
			return new Point((int) Math.floor(dest.getX()), (int) Math.floor(dest.getY()));
		} catch (Exception e) {
			e.printStackTrace();
			return new Point(0, 0);
		}
	}

	/**
	 * Given arbitrary rectangle, return a rectangle with upper-left start and positive width and
	 * height.
	 * @param src source rectangle
	 * @return result rectangle with positive width and height
	 */
	public static Rectangle absRect(final Rectangle src) {
		Rectangle dest = new Rectangle(0, 0, 0, 0);
		if ( src.width < 0 ) {
			dest.x = src.x + src.width + 1;
			dest.width = -src.width;
		} else {
			dest.x = src.x;
			dest.width = src.width;
		}
		if ( src.height < 0 ) {
			dest.y = src.y + src.height + 1;
			dest.height = -src.height;
		} else {
			dest.y = src.y;
			dest.height = src.height;
		}
		return dest;
	}

	public static Image toSwt(final java.awt.Image ipImage) {
		RgbImageAdaptor vpAdaptor =
			new RgbImageAdaptor(ipImage.getHeight(null), ipImage.getWidth(null));
		Graphics vpG = vpAdaptor.getGraphics();
		vpG.drawImage(ipImage, 0, 0, null);
		vpG.dispose();
		return vpAdaptor.toSwtImage();
	}

	public static final class RgbImageAdaptor extends BufferedImage {

		public RgbImageAdaptor(final int inWidth, final int inHeight) {
			super(inWidth, inHeight, BufferedImage.TYPE_3BYTE_BGR);
		}

		public Image toSwtImage() {
			int vnWidth = getWidth();
			int vnHeight = getHeight();
			int vnDepth = 24;
			PaletteData vpPalette = new PaletteData(0xff, 0xff00, 0xff0000);
			int vnScanlinePad = vnWidth * 3;
			WritableRaster vpRaster = getRaster();
			DataBufferByte vpBuffer = (DataBufferByte) vpRaster.getDataBuffer();
			byte[] vabData = vpBuffer.getData();
			ImageData vpImageData =
				new ImageData(vnWidth, vnHeight, vnDepth, vpPalette, vnScanlinePad, vabData);
			Image vpImage = new Image(Display.getDefault(), vpImageData);
			return vpImage;
		}

	}

}
