/*******************************************************************************************************
 *
 * GamaImage.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import msi.gama.common.interfaces.IAsset;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.file.IFieldMatrixProvider;
import msi.gama.util.matrix.GamaField;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.IField;
import msi.gaml.types.IType;

/**
 * Class GamaImage. A simple wrapper on a BufferedImage of type TYPE_INT_ARGB
 *
 * @author drogoul
 * @since 22 mars 2015
 *
 */
@vars ({ @variable (
		name = IKeyword.ALPHA,
		type = IType.BOOL,
		doc = { @doc ("Returns wether the image has an alpha (transparency) component or not") }),
		@variable (
				name = IKeyword.HEIGHT,
				type = IType.INT,
				doc = { @doc ("Returns the height (in pixels) of this image") }),
		@variable (
				name = IKeyword.WIDTH,
				type = IType.INT,
				doc = { @doc ("Returns the width (in pixels) of this image") }) })
public class GamaImage extends BufferedImage implements IImageProvider, IAsset, IFieldMatrixProvider {

	/** The id. */
	final String id;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Override
	public String getId() { return id; }

	/**
	 * Instantiates a new gama image.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param imageType
	 *            the image type
	 */
	private GamaImage(final int width, final int height, final int type, final String uniqueID) {
		super(width, height, type);
		id = uniqueID;
	}

	/**
	 * Instantiates a new gama image.
	 *
	 * @param cm
	 *            the cm
	 * @param raster
	 *            the raster
	 * @param b
	 *            the b
	 * @param uniqueID
	 *            the unique ID
	 */
	private GamaImage(final ColorModel cm, final WritableRaster raster, final boolean b, final String uniqueID) {
		super(cm, raster, b, null);
		id = uniqueID;
	}

	/**
	 * Gets the width.
	 *
	 * @param scope
	 *            the scope
	 * @return the width
	 */
	@getter (IKeyword.WIDTH)
	public int getWidth(final IScope scope) {
		return getWidth();
	}

	/**
	 * Gets the height.
	 *
	 * @param scope
	 *            the scope
	 * @return the height
	 */
	@getter (IKeyword.HEIGHT)
	public int getHeight(final IScope scope) {
		return getHeight();
	}

	/**
	 * Gets the alpha.
	 *
	 * @param scope
	 *            the scope
	 * @return the alpha
	 */
	@getter (IKeyword.ALPHA)
	public boolean getAlpha(final IScope scope) {
		return getType() == TYPE_INT_ARGB;
	}

	/**
	 * From.
	 *
	 * @param image
	 *            the image
	 * @return the gama image
	 */
	public static GamaImage from(final Image image, final boolean withAlpha) {
		return from(image, withAlpha, "image" + System.currentTimeMillis());
	}

	/**
	 * From.
	 *
	 * @param image
	 *            the image
	 * @param withAlpha
	 *            the with alpha
	 * @param id
	 *            the id
	 * @return the gama image
	 */
	public static GamaImage from(final Image image, final boolean withAlpha, final String id) {
		if (image == null) return null;
		GamaImage gi = new GamaImage(image.getWidth(null), image.getHeight(null),
				withAlpha ? TYPE_INT_ARGB : TYPE_INT_RGB, id);
		Graphics2D g = gi.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return gi;
	}

	/**
	 * From.
	 *
	 * @param f
	 *            the f
	 * @return the gama image
	 */
	public static GamaImage from(final IScope scope, final GamaField field) {
		final int cols = field.numCols;
		final int rows = field.numRows;
		final GamaImage image = new GamaImage(cols, rows, TYPE_INT_RGB, "field" + System.currentTimeMillis());
		if (field.getBandsNumber(scope) > 1) {
			IList<? extends IField> bands = field.getBands(scope);
			// boolean hasAlpha = bands.size() > 4;
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					double r = bands.get(1).get(scope, col, row);
					double g = bands.get(2).get(scope, col, row);
					double b = bands.get(3).get(scope, col, row);
					image.setRGB(col, rows - 1 - row, 0x00000000 | (int) r << 16 | (int) g << 8 | (int) b);
				}
			}
		} else {
			double[] minmax = field.getMinMax(null);
			double range = minmax[1] - minmax[0];
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					double v = field.get(scope, col, row);
					double vRef = (v - minmax[0]) / range;
					image.setRGB(col, rows - 1 - row, grayDoubleToRGB(vRef));
				}
			}
		}
		return image;
	}

	/**
	 * Double to RGB.
	 *
	 * @param d
	 *            the d
	 * @return the int
	 */
	private static int grayDoubleToRGB(final double d) {
		var gray = (int) (d * 256);
		if (gray < 0) { gray = 0; }
		if (gray > 255) { gray = 255; }
		return 0x010101 * gray;
	}

	/**
	 * From.
	 *
	 * @param g
	 *            the g
	 * @return the gama image
	 */
	public static GamaImage from(final GamaSpatialMatrix g) {
		GamaImage gi = new GamaImage(g.numCols, g.numRows, TYPE_INT_RGB, "matrix" + System.currentTimeMillis());
		final int[] imageData = ((DataBufferInt) gi.getRaster().getDataBuffer()).getData();
		System.arraycopy(g.getDisplayData(), 0, imageData, 0, imageData.length);
		return gi;
	}

	/**
	 * From.
	 *
	 * @param g
	 *            the g
	 * @return the gama image
	 */
	public static GamaImage from(final IScope scope, final GamaIntMatrix g) {
		BufferedImage im = g.getImage(scope);
		return from(im, false, "matrix" + System.currentTimeMillis());
	}

	/**
	 * Of dimensions.
	 *
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the gama image
	 */
	public static GamaImage ofDimensions(final int w, final int h) {
		return new GamaImage(w, h, TYPE_INT_ARGB, "image" + System.currentTimeMillis());
	}

	/**
	 * Of dimensions.
	 *
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param alpha
	 *            the alpha
	 * @return the gama image
	 */
	public static GamaImage ofDimensions(final int w, final int h, final boolean alpha) {
		return new GamaImage(w, h, alpha ? TYPE_INT_ARGB : TYPE_INT_RGB, "image" + System.currentTimeMillis());
	}

	/**
	 * Of dimensions.
	 *
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param type
	 *            the type
	 * @return the gama image
	 */
	public static GamaImage ofDimensions(final int w, final int h, final int type) {
		return new GamaImage(w, h, type, "image" + System.currentTimeMillis());
	}

	/**
	 * Best for.
	 *
	 * @param src
	 *            the src
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the gama image
	 */
	public static GamaImage bestFor(final Image src, final int width, final int height) {
		return ofDimensions(width, height,
				src instanceof BufferedImage bi && bi.getTransparency() == OPAQUE ? TYPE_INT_RGB : TYPE_INT_ARGB);
	}

	@Override
	public int getRows(final IScope scope) {
		return getHeight();
	}

	@Override
	public int getCols(final IScope scope) {
		return getWidth();
	}

	@Override
	public BufferedImage getImage(final IScope scope, final boolean useCache, final boolean forOpenGL) {
		return this;
	}

	@Override
	public int getBandsNumber(final IScope scope) {
		return getColorModel().getNumComponents();
	}

	@Override
	public double[] getBand(final IScope scope, final int index) {
		final double[] values = new double[getWidth() * getHeight()];
		int[] pixels = new int[values.length];
		PixelGrabber pgb = new PixelGrabber(this, 0, 0, getWidth(), getHeight(), pixels, 0, getWidth());
		try {
			pgb.grabPixels();
		} catch (InterruptedException e) {}
		for (int i = 0; i < values.length; ++i) {
			// Verify this ... Especially if the number of color components does not correspond
			values[i] = pixels[i] & (index + 1) * 255;
		}
		return values;
	}

	/**
	 * From.
	 *
	 * @param cm
	 *            the cm
	 * @param raster
	 *            the raster
	 * @param b
	 *            the b
	 * @return the gama image
	 */
	public static GamaImage from(final ColorModel cm, final WritableRaster raster, final boolean b) {
		return new GamaImage(cm, raster, b, "raster" + System.currentTimeMillis());
	}

}
