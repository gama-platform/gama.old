/*******************************************************************************************************
 *
 * ImageOperators.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import static ummisco.gaml.extensions.image.ImageHelper.apply;
import static ummisco.gaml.extensions.image.ImageHelper.resize;
import static ummisco.gaml.extensions.image.ImageHelper.rotate;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.image.ImageHelper.Mode;
import ummisco.gaml.extensions.image.ImageHelper.TransferableImage;

/**
 * The Class ImageOperators. largely inspired from imgscalr library
 * (https://github.com/rkalla/imgscalr/blob/master/src/main/java/org/imgscalr/Scalr.java)
 *
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class ImageOperators implements ImageConstants {

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
			+ "The search for the display begins in the agent passed in parameter and, if not found, its experiment. The size of the snapshot will be that of the view"
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
		return SnapshotMaker.getInstance().captureImage(surface, null);
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
	@operator (
			value = "snapshot",
			can_be_const = false)
	@doc ("Takes a snapshot of the display whose name is passed in parameter and returns the image. "
			+ "The search for the display begins in the agent passed in parameter and, if not found, its experiment. A custom size (a point representing width x height) can be given "
			+ "Returns nil if no display can be found or the snapshot cannot be taken.")
	@no_test
	public static GamaImage snapshot(final IScope scope, final IAgent exp, final String displayName,
			final GamaPoint customDimensions) {
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
		return SnapshotMaker.getInstance().captureImage(surface, customDimensions);
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
		return rotate(image, ImageConstants.FLIP_HORZ);
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
		return rotate(image, ImageConstants.FLIP_VERT);
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
		// Make sure the background is transparent
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f));
		g2.setColor(new Color(0, 0, 0, 0));
		g2.fillRect(0, 0, newWidth, newHeight);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g2.setRenderingHints(HINTS);
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
		GamaImage composed = ImageHelper.copyToOptimalImage(image);
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
			+ "If one of the dimensions of the resulting image is 0, of if they are equal to that of the given image, returns it. "
			+ " The original image is left untouched")
	@no_test

	public static GamaImage cropped(final IScope scope, final GamaImage image, final int ox, final int oy, final int ow,
			final int oh) {
		int iw = image.getWidth();
		int ih = image.getHeight();
		int width = Math.min(iw, Math.max(0, ow));
		int height = Math.min(ih, Math.max(0, oh));
		int x = Math.min(iw, Math.max(0, ox));
		int y = Math.min(ih, Math.max(0, oy));
		if (x == width || width == 0 || height == 0 || y == height) return image;
		if (x == 0 && y == 0 && width == iw && height == ih) return image;
		GamaImage result = GamaImage.bestFor(image, width, height);
		Graphics g = result.getGraphics();
		g.drawImage(image, 0, 0, width, height, x, y, x + width, y + height, null);
		g.dispose();
		return result;
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
		if (image == null || ImageConstants.clipboard == null) return false;
		ImageConstants.clipboard.setContents(new TransferableImage(image), null);
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
		final int xSize = image.getWidth();
		final int ySize = image.getHeight();
		final IMatrix matrix = new GamaIntMatrix(xSize, ySize);
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) { matrix.set(scope, i, j, image.getRGB(i, j)); }
		}
		return matrix;
	}

}
