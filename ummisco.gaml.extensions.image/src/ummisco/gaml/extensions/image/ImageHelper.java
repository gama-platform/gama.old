/*******************************************************************************************************
 *
 * ImageHelper.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;

/**
 * The Class ImageHelper.
 */
public class ImageHelper implements ImageConstants {

	/**
	 * Used to apply a {@link BufferedImageOp}s to a given {@link GamaImage} and return the result.
	 *
	 * @param src
	 *            The image that will have the ops applied to it.
	 * @param op
	 *            the to apply to the image.
	 *
	 * @return a new {@link GamaImage} that represents the <code>src</code> with the op applied to it.
	 *
	 */
	static GamaImage apply(GamaImage src, final BufferedImageOp op) {
		int type = src.getType();
		if (type != BufferedImage.TYPE_INT_RGB && type != BufferedImage.TYPE_INT_ARGB) {
			src = copyToOptimalImage(src);
		}
		Rectangle2D bounds = op.getBounds2D(src);
		if (bounds == null) return src;
		GamaImage result =
				GamaImage.bestFor(src, (int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight()));
		op.filter(src, result);
		return result;
	}

	/**
	 * Used to copy a {@link Image} from a non-optimal type into a new {@link GamaImage} instance of an optimal type
	 * (RGB or ARGB).
	 *
	 * @param src
	 *            The image to copy (if necessary) into an optimally typed {@link BufferedImage}.
	 *
	 * @return a representation of the <code>src</code> image in an optimally typed {@link BufferedImage}, otherwise
	 *         <code>src</code> if it was already of an optimal type.
	 *
	 */
	protected static GamaImage copyToOptimalImage(final Image src) {
		if (src == null) return null;
		int type = src instanceof BufferedImage bu && bu.getTransparency() == Transparency.OPAQUE
				? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		GamaImage result = GamaImage.ofDimensions(src.getWidth(null), src.getHeight(null), type);
		Graphics g = result.getGraphics();
		g.drawImage(src, 0, 0, null);
		g.dispose();
		return result;
	}

	/**
	 * Used to apply a quadrant / flip rotation to the image
	 *
	 * @param src
	 *            The image that will have the rotation applied to it.
	 * @param typeOfRotation
	 *            The rotation that will be applied to the image.
	 *
	 * @return a new {@link BufferedImage} representing <code>src</code> rotated by the given amount and any optional
	 *         ops applied to it.
	 *
	 */
	static GamaImage rotate(final GamaImage src, final int typeOfRotation)
			throws IllegalArgumentException, ImagingOpException {
		int newWidth = src.getWidth();
		int newHeight = src.getHeight();
		AffineTransform tx = new AffineTransform();
		switch (typeOfRotation) {
			case 90:
				newWidth = newHeight;
				newHeight = src.getWidth();
				tx.translate(newWidth, 0);
				tx.quadrantRotate(1);
				break;
			case 270:
				newWidth = newHeight;
				newHeight = src.getWidth();
				tx.translate(0, newHeight);
				tx.quadrantRotate(3);
				break;
			case 180:
				tx.translate(newWidth, newHeight);
				tx.quadrantRotate(2);
				break;
			case FLIP_HORZ:
				tx.translate(newWidth, 0);
				tx.scale(-1.0, 1.0);
				break;
			case FLIP_VERT:
				tx.translate(0, newHeight);
				tx.scale(1.0, -1.0);
				break;
		}
		GamaImage result = GamaImage.bestFor(src, newWidth, newHeight);
		Graphics2D g2d = result.createGraphics();
		g2d.setRenderingHints(HINTS);
		g2d.drawImage(src, tx, null);
		g2d.dispose();
		return result;
	}

	/**
	 * Used to define the different modes of resizing that the algorithm can use.
	 *
	 * @author Riyad Kalla (software@thebuzzmedia.com)
	 * @since 3.1
	 */
	public enum Mode {
		/**
		 * Used to fit the image to the exact dimensions given regardless of the image's proportions. If the dimensions
		 * are not proportionally correct, this will introduce vertical or horizontal stretching to the image.
		 */
		FIT_EXACT,

		/**
		 * Used to indicate that the scaling implementation should calculate dimensions for the resultant image that
		 * best-fit within the given width, regardless of the orientation of the image.
		 */
		FIT_TO_WIDTH,
		/**
		 * Used to indicate that the scaling implementation should calculate dimensions for the resultant image that
		 * best-fit within the given height, regardless of the orientation of the image.
		 */
		FIT_TO_HEIGHT;
	}

	/**
	 * Resize a given image (maintaining its original proportion) to the target width and height (or fitting the image
	 * to the given WIDTH or HEIGHT explicitly, depending on the {@link Mode} specified) using the given scaling method
	 * to the result before returning it.
	 *
	 * @param src
	 *            The image that will be scaled.
	 * @param resizeMode
	 *            Used to indicate how imgscalr should calculate the final target size for the image, either fitting the
	 *            image to the given width ({@link Mode#FIT_TO_WIDTH}) or fitting the image to the given height
	 *            ({@link Mode#FIT_TO_HEIGHT}).
	 * @param targetWidth
	 *            The target width that you wish the image to have.
	 * @param targetHeight
	 *            The target height that you wish the image to have.
	 * @return a new {@link BufferedImage} representing the scaled <code>src</code> image.
	 *
	 *
	 * @see Mode
	 */
	static GamaImage resize(final GamaImage src, final Mode resizeMode, int targetWidth, int targetHeight)
			throws IllegalArgumentException, ImagingOpException {
		GamaImage result = null;
		int currentWidth = src.getWidth();
		int currentHeight = src.getHeight();
		float ratio = (float) currentHeight / (float) currentWidth;
		if (resizeMode == Mode.FIT_TO_WIDTH) {
			targetHeight = (int) Math.ceil(targetWidth * ratio);
		} else if (resizeMode == Mode.FIT_TO_HEIGHT) { targetWidth = Math.round(targetHeight / ratio); }
		if (targetWidth > currentWidth || targetHeight > currentHeight) {
			result = ImageHelper.scaleImage(src, targetWidth, targetHeight);
		} else {
			result = ImageHelper.scaleImageIncrementally(src, targetWidth, targetHeight);
		}
		return result;
	}

	/**
	 * Used to implement Chris Campbell's incremental-scaling algorithm:
	 * <a href="http://today.java.net/pub/a/today/2007/04/03/perils -of-image-getscaledinstance
	 * .html">http://today.java.net/pub/a/today/2007/04/03/perils -of-image-getscaledinstance.html</a>.
	 *
	 * @param src
	 *            The image that will be scaled.
	 * @param targetWidth
	 *            The target width for the scaled image.
	 * @param targetHeight
	 *            The target height for the scaled image.
	 * @param scalingMethod
	 *            The scaling method specified by the user (or calculated by imgscalr) to use for this incremental
	 *            scaling operation.
	 *
	 * @return an image scaled to the given dimensions using the given rendering hint.
	 */
	static GamaImage scaleImageIncrementally(GamaImage src, final int targetWidth, final int targetHeight) {
		boolean hasReassignedSrc = false;
		int currentWidth = src.getWidth();
		int currentHeight = src.getHeight();
		do {
			int prevCurrentWidth = currentWidth;
			int prevCurrentHeight = currentHeight;
			if (currentWidth > targetWidth) {
				currentWidth -= currentWidth / 2;
				if (currentWidth < targetWidth) { currentWidth = targetWidth; }
			}
			if (currentHeight > targetHeight) {
				currentHeight -= currentHeight / 2;
				if (currentHeight < targetHeight) { currentHeight = targetHeight; }
			}
			if (prevCurrentWidth == currentWidth && prevCurrentHeight == currentHeight) { break; }
			GamaImage incrementalImage = scaleImage(src, currentWidth, currentHeight);
			if (hasReassignedSrc) { src.flush(); }
			src = incrementalImage;
			hasReassignedSrc = true;
		} while (currentWidth != targetWidth || currentHeight != targetHeight);

		return src;
	}

	/**
	 * Used to implement a straight-forward image-scaling operation using Java 2D.
	 * <p/>
	 * This method uses the Oracle-encouraged method of <code>Graphics2D.drawImage(...)</code> to scale the given image
	 * with the given interpolation hint.
	 *
	 * @param bufferedImage
	 *            The image that will be scaled.
	 * @param targetWidth
	 *            The target width for the scaled image.
	 * @param targetHeight
	 *            The target height for the scaled image.
	 * @return the result of scaling the original <code>src</code> to the given dimensions
	 */
	public static GamaImage scaleImage(final Image bufferedImage, final int targetWidth, final int targetHeight) {
		GamaImage result = GamaImage.bestFor(bufferedImage, targetWidth, targetHeight);
		Graphics2D resultGraphics = result.createGraphics();
		resultGraphics.setRenderingHints(ImageHelper.HINTS);
		resultGraphics.drawImage(bufferedImage, 0, 0, targetWidth, targetHeight, null);
		resultGraphics.dispose();
		return result;
	}

	/**
	 * Creates a premultiplied blank image.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama image
	 */
	public static GamaImage createPremultipliedBlankImage(final int width, final int height) {
		return GamaImage.ofDimensions(width != 0 ? width : 1024, height != 0 ? height : 1024,
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
	public static GamaImage createCompatibleImage(final int width, final int height, final boolean forOpenGL) {
		if (forOpenGL) return createPremultipliedBlankImage(width, height);
		return GamaImage.ofDimensions(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Flip image vertically.
	 *
	 * @param image
	 *            the image
	 */
	public static void flipImageVertically(final GamaImage image) {
		final WritableRaster raster = image.getRaster();
		Object scanline1 = null;
		Object scanline2 = null;

		for (int i = 0; i < image.getHeight() / 2; i++) {
			scanline1 = raster.getDataElements(0, i, image.getWidth(), 1, scanline1);
			scanline2 = raster.getDataElements(0, image.getHeight() - i - 1, image.getWidth(), 1, scanline2);
			raster.setDataElements(0, i, image.getWidth(), 1, scanline2);
			raster.setDataElements(0, image.getHeight() - i - 1, image.getWidth(), 1, scanline1);
		}
	}

	/**
	 * The TransferableImage.
	 */
	record TransferableImage(Image i) implements Transferable {

		@Override
		public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
			if (flavor.equals(DataFlavor.imageFlavor) && i != null) return i;
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] flavors = new DataFlavor[1];
			flavors[0] = DataFlavor.imageFlavor;
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			DataFlavor[] flavors = getTransferDataFlavors();
			for (DataFlavor dataFlavor : flavors) { if (flavor.equals(dataFlavor)) return true; }
			return false;
		}
	}

}
