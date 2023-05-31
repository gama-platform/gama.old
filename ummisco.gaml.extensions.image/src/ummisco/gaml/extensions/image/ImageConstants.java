/*******************************************************************************************************
 *
 * ImageConstants.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.util.Map;

/**
 * The Interface ImageConstants.
 */
public interface ImageConstants {

	/** The Constant FLIP_HORZ. */
	int FLIP_HORZ = 0;

	/** The Constant FLIP_VERT. */
	int FLIP_VERT = 1;

	/** The Constant clipboard. */
	Clipboard clipboard = GraphicsEnvironment.isHeadless() ? null : Toolkit.getDefaultToolkit().getSystemClipboard();

	/** The hints. */
	RenderingHints HINTS = new RenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
			RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC, RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY));

	/**
	 * A {@link ConvolveOp} using a very light "blur" kernel that acts like an anti-aliasing filter (softens the image a
	 * bit) when applied to an image.
	 */
	ConvolveOp OP_ANTIALIAS =
			new ConvolveOp(new Kernel(3, 3, new float[] { .0f, .08f, .0f, .08f, .68f, .08f, .0f, .08f, .0f }),
					ConvolveOp.EDGE_NO_OP, HINTS);

	/**
	 * A {@link RescaleOp} used to make any input image 10% darker.
	 */
	RescaleOp OP_DARKER = new RescaleOp(0.9f, 0, HINTS);

	/**
	 * A {@link RescaleOp} used to make any input image 10% brighter.
	 */
	RescaleOp OP_BRIGHTER = new RescaleOp(1.1f, 0, HINTS);

	/** The Constant OP_SHARPEN. */
	ConvolveOp OP_SHARPEN = new ConvolveOp(
			new Kernel(3, 3, new float[] { -1.0f, -1.0f, -1.0f, -1.0f, 9.0f, -1.0f, -1.0f, -1.0f, -1.0f }),
			ConvolveOp.EDGE_NO_OP, HINTS);

	/** The Constant OP_BLUR. */
	ConvolveOp OP_BLUR = new ConvolveOp(
			new Kernel(3, 3, new float[] { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f, 0.0625f, 0.125f, 0.0625f }),
			ConvolveOp.EDGE_NO_OP, HINTS);

	/**
	 * A {@link ColorConvertOp} used to convert any image to a grayscale color palette.
	 */
	ColorConvertOp OP_GRAYSCALE = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

	/** The descriptions. */
	Map<BufferedImageOp, String> DESCRIPTIONS = Map.of(OP_DARKER, "darker", OP_ANTIALIAS, "antialiased", OP_BRIGHTER,
			"brighter", OP_SHARPEN, "sharpened", OP_BLUR, "blurred", OP_GRAYSCALE, "grayscaled");

}
