/*******************************************************************************************************
 *
 * ImageOperators.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.ImagingOpException;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.Map;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;

/**
 * The Class ImageOperators. largely inspired from imgscalr library
 * (https://github.com/rkalla/imgscalr/blob/master/src/main/java/org/imgscalr/Scalr.java)
 *
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class ImageOperators {

	/** The Constant clipboard. */
	public static final Clipboard clipboard =
			GraphicsEnvironment.isHeadless() ? null : Toolkit.getDefaultToolkit().getSystemClipboard();

	/** The Constant FLIP_HORZ. */
	public static final int FLIP_HORZ = 0;

	/** The Constant FLIP_VERT. */
	public static final int FLIP_VERT = 1;

	/** The hints. */
	static RenderingHints HINTS =
			new RenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
					RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC,
					RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	/**
	 * A {@link ConvolveOp} using a very light "blur" kernel that acts like an anti-aliasing filter (softens the image a
	 * bit) when applied to an image.
	 */
	public static final ConvolveOp OP_ANTIALIAS =
			new ConvolveOp(new Kernel(3, 3, new float[] { .0f, .08f, .0f, .08f, .68f, .08f, .0f, .08f, .0f }),
					ConvolveOp.EDGE_NO_OP, HINTS);

	/**
	 * A {@link RescaleOp} used to make any input image 10% darker.
	 */
	public static final RescaleOp OP_DARKER = new RescaleOp(0.9f, 0, HINTS);

	/**
	 * A {@link RescaleOp} used to make any input image 10% brighter.
	 */
	public static final RescaleOp OP_BRIGHTER = new RescaleOp(1.1f, 0, HINTS);

	/** The Constant OP_SHARPEN. */
	public static final ConvolveOp OP_SHARPEN = new ConvolveOp(
			new Kernel(3, 3, new float[] { -1.0f, -1.0f, -1.0f, -1.0f, 9.0f, -1.0f, -1.0f, -1.0f, -1.0f }),
			ConvolveOp.EDGE_NO_OP, HINTS);

	/** The Constant OP_BLUR. */
	public static final ConvolveOp OP_BLUR = new ConvolveOp(
			new Kernel(3, 3, new float[] { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f, 0.0625f, 0.125f, 0.0625f }),
			ConvolveOp.EDGE_NO_OP, HINTS);

	/**
	 * A {@link ColorConvertOp} used to convert any image to a grayscale color palette.
	 */
	public static final ColorConvertOp OP_GRAYSCALE =
			new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

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
	private static GamaImage apply(GamaImage src, final BufferedImageOp op) {
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
	private static GamaImage rotate(final GamaImage src, final int typeOfRotation)
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
		g2d.setRenderingHints(ImageOperators.HINTS);
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
			result = scaleImage(src, targetWidth, targetHeight);
		} else {
			result = scaleImageIncrementally(src, targetWidth, targetHeight);
		}
		return result;
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
	static GamaImage scaleImage(final Image bufferedImage, final int targetWidth, final int targetHeight) {
		GamaImage result = GamaImage.bestFor(bufferedImage, targetWidth, targetHeight);
		Graphics2D resultGraphics = result.createGraphics();
		resultGraphics.setRenderingHints(ImageOperators.HINTS);
		resultGraphics.drawImage(bufferedImage, 0, 0, targetWidth, targetHeight, null);
		resultGraphics.dispose();
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
	 * Snapshot.
	 *
	 * @param scope
	 *            the scope
	 * @param displayName
	 *            the display name
	 * @return the gama image
	 */
	@operator (
			value = "snapshot",
			can_be_const = false)
	@doc ("Takes a snapshot of the display whose name is passed in parameter and returns the image. "
			+ "The search for the display begins in the current agent's simulation and, if not found, its experiment. "
			+ "Returns nil if no display can be found or the snapshot cannot be taken.")
	@no_test
	public static GamaImage snapshot(final IScope scope, final String displayName) {
		return snapshot(scope, scope.getAgent(), displayName);
	}

	/**
	 * Snapshot.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param displayName
	 *            the display name
	 * @return the gama image
	 */

	/**
	 * Snapshot.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param displayName
	 *            the display name
	 * @return the gama image
	 */
	@operator (
			value = "snapshot",
			can_be_const = false)
	@doc ("Takes a snapshot of the display whose name is passed in parameter and returns the image. "
			+ "The search for the display begins in the agent passed in parameter and, if not found, its experiment. "
			+ "Returns nil if no display can be found or the snapshot cannot be taken.")
	@no_test
	public static GamaImage snapshot(final IScope scope, final IAgent exp, final String displayName) {
		if (exp == null) return null;
		ITopLevelAgent agentWithOutputs;
		if (exp instanceof ITopLevelAgent top) {
			agentWithOutputs = top;
		} else {
			agentWithOutputs = exp.getTopLevelHost();
		}
		IOutput output = null;
		while (agentWithOutputs != null && output == null) {
			output = agentWithOutputs.getOutputManager().getOutputWithOriginalName(displayName);
			agentWithOutputs = agentWithOutputs.getTopLevelHost();
		}
		if (!(output instanceof LayeredDisplayOutput ldo)) return null;
		IDisplaySurface surface = ldo.getSurface();
		return SnapshotMaker.getInstance().captureImage(surface);
	}

	/**
	 * Grayscale.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("grayscale")
	@doc ("Used to convert any image to a grayscale color palette and return it. The original image is left untouched")
	@no_test
	public static GamaImage grayscale(final IScope scope, final GamaImage image) {
		try {
			return apply(image, OP_GRAYSCALE);
		} catch (Exception e) {
			return image;
		}
	}

	/**
	 * Darker.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("darker")
	@doc ("Used to return an image 10% darker. This operation can be applied multiple times in a row if greater than 10% changes in brightness are desired.")
	@no_test
	public static GamaImage darker(final IScope scope, final GamaImage image) {
		try {
			return apply(image, OP_DARKER);
		} catch (Exception e) {
			return image;
		}
	}

	/**
	 * Brigther.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("brighter")
	@doc ("Used to return an image 10% brigther. This operation can be applied multiple times in a row if greater than 10% changes in brightness are desired.")
	@no_test
	public static GamaImage brigther(final IScope scope, final GamaImage image) {
		try {
			return apply(image, OP_BRIGHTER);
		} catch (Exception e) {
			return image;
		}
	}

	/**
	 * Antialiased.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("antialiased")
	@doc ("Application of a very light blur kernel that acts like an anti-aliasing filter when applied to an image. This operation can be applied multiple times in a row if greater.")
	@no_test
	public static GamaImage antialiased(final IScope scope, final GamaImage image) {
		try {
			return apply(image, OP_ANTIALIAS);
		} catch (Exception e) {
			return image;
		}
	}

	/**
	 * Scaled by.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param scale
	 *            the scale
	 * @return the gama image
	 */
	@operator ("*")
	@doc ("Applies a proportional scaling ratio to the image passed in parameter and returns a new scaled image. "
			+ "A ratio of 0 will return nil, a ratio of 1 will return the original image. Automatic scaling and resizing methods are used. The original image is left untouched")
	@no_test
	public static GamaImage scaled_by(final IScope scope, final GamaImage image, final Double scale) {
		if (scale == 0d) return null;
		if (scale == 1d) return image;
		int newWidth = (int) Math.round(image.getWidth() * scale);
		int newHeight = (int) Math.round(image.getHeight() * scale);
		return resize(image, Mode.FIT_TO_WIDTH, newWidth, newHeight);
	}

	/**
	 * With width
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param scale
	 *            the scale
	 * @return the gama image
	 */
	@operator ("with_width")
	@doc ("Applies a proportional scaling to the image passed in parameter to  return a new scaled image with the corresponding width. "
			+ "A width of 0 will return nil, a width equal to the width of the image will return the original image. Automatic scaling and resizing methods are used. The original image is left untouched")
	@no_test
	public static GamaImage with_width(final IScope scope, final GamaImage image, final Integer width) {
		return image == null || width <= 0d ? null : width == image.getWidth() ? image
				: resize(image, Mode.FIT_TO_WIDTH, width, width);
	}

	/**
	 * With width.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param height
	 *            the width
	 * @return the gama image
	 */
	@operator ("with_height")
	@doc ("Applies a proportional scaling to the image passed in parameter to return a new scaled image with the corresponding height. "
			+ "A height of 0 will return nil, a height equal to the height of the image will return the original image. Automatic scaling and resizing methods are used. The original image is left untouched")
	@no_test
	public static GamaImage with_height(final IScope scope, final GamaImage image, final Integer height) {
		return image == null || height <= 0d ? null : height == image.getHeight() ? image
				: resize(image, Mode.FIT_TO_HEIGHT, height, height);
	}

	/**
	 * With size.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama image
	 */
	@operator ("with_size")
	@doc ("Applies a non-proportional scaling to the image passed in parameter to return a new scaled image with the corresponding width and height. "
			+ "A height of 0 or a width of 0 will return nil. If the width and height parameters are repectively equal to the width and height of the original image, it is returned. Automatic scaling and resizing methods are used. The original image is left untouched")
	@no_test
	public static GamaImage with_size(final IScope scope, final GamaImage image, final Integer width,
			final Integer height) {
		return image == null || height <= 0d || width <= 0d ? null
				: height == image.getHeight() && width == image.getWidth() ? image
				: resize(image, Mode.FIT_EXACT, width, height);
	}

	/**
	 * Horizontal flip.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("horizontal_flip")
	@doc ("Returns an image flipped horizontally by reflecting the original image around the y axis. The original image is left untouched")
	@no_test
	public static GamaImage horizontalFlip(final IScope scope, final GamaImage image) {
		return rotate(image, FLIP_HORZ);
	}

	/**
	 * Vertical flip.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("vertical_flip")
	@doc ("Returns an image flipped vertically by reflecting the original image around the x axis. The original image is left untouched")
	@no_test
	public static GamaImage verticalFlip(final IScope scope, final GamaImage image) {
		return rotate(image, FLIP_VERT);
	}

	/**
	 * Rotated.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param angleInDegrees
	 *            the angle in degrees
	 * @return the gama image
	 */
	@operator ("rotated_by")
	@doc ("Returns the image rotated using the angle in degrees passed in parameter. A positive angle means a clockwise rotation, and a negative one a counter-clockwise. The original image is left untouched")
	@no_test
	public static GamaImage rotated(final IScope scope, final GamaImage image, final double angleInDegrees) {
		double angle = Math.abs(angleInDegrees) % 360 * Math.signum(angleInDegrees);
		if (angle == Math.floor(angle)) {
			switch ((int) angle) {
				case 0:
					return image;
				case 90, -270:
					return rotate(image, 90);
				case 180, -180:
					return rotate(image, 180);
				case 270, -90:
					return rotate(image, 270);
			}
		}
		double rads = Math.toRadians(angle);
		double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
		int w = image.getWidth();
		int h = image.getHeight();
		int newWidth = (int) Math.floor(w * cos + h * sin);
		int newHeight = (int) Math.floor(h * cos + w * sin);
		GamaImage rotated = GamaImage.ofDimensions(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = rotated.createGraphics();

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f));
		g2.setColor(new Color(0, 0, 0, 0));
		g2.fillRect(0, 0, newWidth, newHeight);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

		// g2.setRenderingHints(HINTS);
		// g2.setBackground(new Color(0, 0, 0, 0));
		// graphics2d.setBackground(new Color(0, true));
		// graphics2d.clearRect(0, 0, newWidth, newHeight);
		g2.translate((newWidth - w) / 2, (newHeight - h) / 2);
		g2.rotate(rads, w / 2, h / 2);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return rotated;
	}

	/**
	 * Tint.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param color
	 *            the color
	 * @return the gama image
	 */
	@operator ({ "tinted_with", "*" })
	@doc ("Returns the image tinted using the color passed in parameter. This effectively multiplies the colors of the image by it. The original image is left untouched")
	@no_test
	public static GamaImage tint(final IScope scope, final GamaImage image, final GamaColor color) {
		GamaImage tintedSprite = GamaImage.ofDimensions(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
		Graphics2D graphics = tintedSprite.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		ColorModel cm = tintedSprite.getColorModel();
		WritableRaster raster = tintedSprite.getRaster();
		float r = color.getRed() / 255f;
		float g = color.getGreen() / 255f;
		float b = color.getBlue() / 255f;
		float a = color.getAlpha() / 255f;
		for (int i = 0; i < tintedSprite.getWidth(); i++) {
			for (int j = 0; j < tintedSprite.getHeight(); j++) {
				int ax = cm.getAlpha(raster.getDataElements(i, j, null));
				int rx = cm.getRed(raster.getDataElements(i, j, null));
				int gx = cm.getGreen(raster.getDataElements(i, j, null));
				int bx = cm.getBlue(raster.getDataElements(i, j, null));
				rx *= r;
				gx *= g;
				bx *= b;
				ax *= a;
				tintedSprite.setRGB(i, j, ax << 24 | rx << 16 | gx << 8 | bx);
			}
		}
		return tintedSprite;
	}

	/**
	 * Tint.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param color
	 *            the color
	 * @return the gama image
	 */

	/**
	 * Tint.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param color
	 *            the color
	 * @param ratio
	 *            the ratio
	 * @return the gama image
	 */
	@operator ({ "tinted_with" })
	@doc ("Returns the image tinted using the color passed in parameter and a factor between 0 and 1, determining the transparency of the dyeing to apply. The original image is left untouched")
	@no_test
	public static GamaImage tint(final IScope scope, final GamaImage image, final GamaColor color, final double ratio) {
		int w = image.getWidth();
		int h = image.getHeight();
		GamaImage dyed = GamaImage.ofDimensions(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dyed.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.setComposite(AlphaComposite.SrcAtop.derive(Math.min(1f, Math.max((float) ratio, 0f))));
		g.setColor(color);
		g.fillRect(0, 0, w, h);
		g.dispose();
		return dyed;
	}

	/**
	 * Blend.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param overlay
	 *            the overlay
	 * @param ratio
	 *            the ratio
	 * @return the gama image
	 */

	/**
	 * Blend.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param overlay
	 *            the overlay
	 * @param ratio
	 *            the ratio
	 * @return the gama image
	 */
	@operator (
			value = "blend",
			can_be_const = true)
	@doc (
			value = "Blend two images with an optional ratio between 0 and 1 (determines the transparency of the second image, applied as an overlay to the first). The size of the resulting image is that of the first parameter. The original image is left untouched",
			masterDoc = true,
			examples = { @example (
					value = "blend(img1, img2, 0.3)",
					equals = "to a composed image with the two",
					isExecutable = false) })
	@no_test
	public static GamaImage blend(final IScope scope, final GamaImage image, final GamaImage overlay,
			final double ratio) {
		GamaImage composed = copyToOptimalImage(image);
		Graphics2D g2d = composed.createGraphics();
		g2d.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, Math.max((float) ratio, 0f))));
		int x = (composed.getWidth() - overlay.getWidth()) / 2;
		int y = (composed.getHeight() - overlay.getHeight()) / 2;
		g2d.drawImage(overlay, x, y, null);
		g2d.dispose();
		return composed;
	}

	/**
	 * Blur.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("blurred")
	@doc ("Application of a blurrying filter to the image passed in parameter. This operation can be applied multiple times. The original image is left untouched")
	@no_test
	public static GamaImage blur(final IScope scope, final GamaImage image) {
		return apply(image, OP_BLUR);
	}

	/**
	 * Sharpen.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	@operator ("sharpened")
	@doc ("Application of a sharpening filter to the image passed in parameter. This operation can be applied multiple times. The original image is left untouched")
	@no_test
	public static GamaImage sharpen(final IScope scope, final GamaImage image) {
		return apply(image, OP_SHARPEN);
	}

	/**
	 * Cropped.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param ox
	 *            the ox
	 * @param oy
	 *            the oy
	 * @param ow
	 *            the ow
	 * @param oh
	 *            the oh
	 * @return the gama image
	 */
	@operator ({ "clipped_with", "cropped_to" })
	@doc ("Used to crop the given image using a rectangle starting at the top-left x, y coordinates and expanding using the width and height. "
			+ "If one of the dimensions of the resulting image is 0, returns nil. "
			+ "If they are equal to that of the given image, it is returned.  The original image is left untouched")
	@no_test

	public static GamaImage cropped(final IScope scope, final GamaImage image, final int ox, final int oy, final int ow,
			final int oh) {
		int iw = image.getWidth();
		int ih = image.getHeight();
		int width = Math.min(iw, Math.max(0, ow));
		int height = Math.min(ih, Math.max(0, oh));
		int x = Math.min(iw, Math.max(0, ox));
		int y = Math.min(ih, Math.max(0, oy));
		if (x == width || width == 0 || height == 0 || y == height) return null;
		if (x == 0 && y == 0 && width == iw && height == ih) return image;
		GamaImage result = GamaImage.bestFor(image, width, height);
		Graphics g = result.getGraphics();
		g.drawImage(image, 0, 0, width, height, x, y, x + width, y + height, null);
		g.dispose();
		return result;
	}

	/**
	 * The TransferableImage.
	 */
	private record TransferableImage(Image i) implements Transferable {

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

	/**
	 * Copy to clipboard.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the boolean
	 */
	@operator (
			value = "copy_to_clipboard",
			can_be_const = false,
			category = { IOperatorCategory.SYSTEM },
			concept = { IConcept.SYSTEM })
	@doc (
			examples = @example ("bool copied  <- copy_to_clipboard(img);"),
			value = "Tries to copy the given image to the clipboard and returns whether it has been correctly copied or not (for instance it might be impossible in a headless environment)")
	@no_test ()
	public static Boolean copyToClipboard(final IScope scope, final GamaImage image) {
		if (image == null || clipboard == null) return false;
		clipboard.setContents(new TransferableImage(image), null);
		return true;
	}

	/**
	 * Image.
	 *
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param type
	 *            the type
	 * @return the gama image
	 */
	@operator (
			can_be_const = true,
			value = "image")
	@doc ("Builds a new blank image of the specified dimensions, which does not accept transparency")
	@no_test
	public static GamaImage image(final int w, final int h) {
		return GamaImage.ofDimensions(w, h, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Image.
	 *
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param color
	 *            the color
	 * @return the gama image
	 */
	@operator (
			can_be_const = true,
			value = "image")
	@doc ("Builds a new image with the specified dimensions and already filled with the given rgb color")
	@no_test
	public static GamaImage image(final int w, final int h, final GamaColor color) {
		GamaImage gi = GamaImage.ofDimensions(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = gi.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, w, h);
		g.dispose();
		return gi;
	}

	/**
	 * Image.
	 *
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param type
	 *            the type
	 * @return the gama image
	 */
	@operator (
			can_be_const = true,
			value = "image")
	@doc ("Builds a new blank image with the specified dimensions and indicates if it will support transparency or not")
	@no_test
	public static GamaImage image(final int w, final int h, final boolean alpha) {
		return GamaImage.ofDimensions(w, h, alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Matrix.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @return the gama int matrix
	 */
	@operator (
			value = "matrix",
			content_type = IType.INT,
			can_be_const = true)
	@doc ("Returns the matrix<int> value of the image passed in parameter, where each pixel is represented by the RGB int value. The dimensions of the matrix are those of the image. ")
	@no_test
	public static IMatrix matrix(final IScope scope, final GamaImage image) {
		return GamaImageFile.matrixValueFromImage(scope, image, null);
	}

}
