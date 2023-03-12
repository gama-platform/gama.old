/*******************************************************************************************************
 *
 * GamaImageFile.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.IField;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Spatial.Projections;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaImageFile.
 */
@file (
		name = "image",
		extensions = { "tiff", "jpg", "jpeg", "png", "pict", "bmp" },
		buffer_type = IType.MATRIX,
		buffer_content = IType.INT,
		buffer_index = IType.POINT,
		concept = { IConcept.IMAGE, IConcept.FILE },
		doc = @doc ("Image files can be of 6 different formats: tiff, jpeg, png, pict or bmp. Their internal representation is a matrix of colors"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaImageFile extends GamaFile<IMatrix<Integer>, Integer> implements IFieldMatrixProvider, IImageProvider {

	/**
	 * The Class ImageInfo.
	 */
	public static class ImageInfo extends GamaFileMetaData {

		/** The Constant formatsShortNames. */
		public final static Map<Integer, String> formatsShortNames = new HashMap<>() {

			{
				// Hack: Corresponds to SWT.IMAGE_xxx + ImagePropertyPage
				// constants
				put(0, "BMP");
				put(1, "BMP");
				put(7, "BMP");
				put(2, "GIF");
				put(4, "JPEG");
				put(5, "PNG");
				put(3, "ICO");
				put(6, "TIFF");
				put(-1, "Unknown Format");
				put(8, "ASCII");
				put(9, "PGM");
			}
		};

		/** The type. */
		private final int type;

		/** The width. */
		private final int width;

		/** The height. */
		private final int height;

		/**
		 * Instantiates a new image info.
		 *
		 * @param modificationStamp
		 *            the modification stamp
		 * @param origType
		 *            the orig type
		 * @param origWidth
		 *            the orig width
		 * @param origHeight
		 *            the orig height
		 */
		public ImageInfo(final long modificationStamp, /* final Object thumbnail, */final int origType,
				final int origWidth, final int origHeight) {
			super(modificationStamp);
			this.type = origType;
			this.width = origWidth;
			this.height = origHeight;
		}

		/**
		 * Instantiates a new image info.
		 *
		 * @param propertyString
		 *            the property string
		 */
		public ImageInfo(final String propertyString) {
			super(propertyString);
			final String[] segments = split(propertyString);
			type = Integer.parseInt(segments[1]);
			width = Integer.parseInt(segments[2]);
			height = Integer.parseInt(segments[3]);
		}

		/**
		 * Gets the short label.
		 *
		 * @param type
		 *            the type
		 * @return the short label
		 */
		public String getShortLabel(final int type) {
			return formatsShortNames.containsKey(type) ? formatsShortNames.get(type) : formatsShortNames.get(-1);
		}

		@Override
		public String getSuffix() { return "" + width + "x" + height + ", " + getShortLabel(type) + ""; }

		@Override
		public void appendSuffix(final StringBuilder sb) {
			sb.append(width).append("x").append(height).append(SUFFIX_DEL).append(getShortLabel(type));
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append(getShortLabel(type)).append(" Image File").append(Strings.LN);
			sb.append("Dimensions: ").append(width + " pixels x " + height + " pixels").append(Strings.LN);
			return sb.toString();
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public int getType() { return type; }

		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + type + DELIMITER + width + DELIMITER + height;
		}
	}

	/**
	 * The Class GamaPgmFile.
	 */
	@file (
			name = "pgm",
			extensions = { "pgm" },
			buffer_type = IType.MATRIX,
			buffer_content = IType.INT,
			doc = @doc ("PGM files are special image files in 256 gray levels"))
	public static class GamaPgmFile extends GamaImageFile {

		/**
		 * @param scope
		 * @param pathName
		 * @throws GamaRuntimeException
		 */
		@doc (
				value = "This file constructor allows to read a pgm file",
				examples = { @example (
						value = "file f <-pgm_file(\"file.pgm\");",
						isExecutable = false) })

		public GamaPgmFile(final IScope scope, final String pathName) throws GamaRuntimeException {
			super(scope, pathName);
		}

		@Override
		protected boolean isPgmFile() { return true; }

		/*
		 * (non-Javadoc)
		 *
		 * @see msi.gama.util.GamaFile#flushBuffer()
		 */
		@Override
		protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
			throw GamaRuntimeException
					.error("Saving is not yet impletemented for files of type " + this.getExtension(scope), scope);
		}

	}

	/** The is georeferenced. */
	// protected BufferedImage image;
	private boolean isGeoreferenced = false;

	/** The extension. */
	private String extension = null;

	/**
	 * Instantiates a new gama image file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read an image file (tiff, jpg, jpeg, png, pict, bmp)",
			examples = { @example (
					value = "file f <-image_file(\"file.png\");",
					isExecutable = false) })

	public GamaImageFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	/**
	 * Instantiates a new gama image file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param pathName
	 *            the extension of the file
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read an image file (tiff, jpg, jpeg, png, pict, bmp) and to force the extension of the file (can be useful for images coming from URL)",
			examples = { @example (
					value = "file f <-image_file(\"http://my_url\", \"png\");",
					isExecutable = false) })

	public GamaImageFile(final IScope scope, final String pathName, final String extension)
			throws GamaRuntimeException {
		super(scope, pathName);
		this.extension = extension;
	}

	/**
	 * Instantiates a new gama image file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param image
	 *            the image
	 */
	@doc (
			value = "This file constructor allows to store a matrix in a image file (it does not save it - just store it in memory)",
			examples = { @example (
					value = "file f <-image_file(\"file.png\");",
					isExecutable = false) })
	public GamaImageFile(final IScope scope, final String pathName, final IMatrix<Integer> image) {
		super(scope, pathName, image);
		ImageUtils.getInstance().clearCache(getPath(scope));
	}

	/**
	 * Instantiates a new gama image file.
	 *
	 * @param scope
	 *            the scope
	 * @param filename
	 *            the filename
	 * @param im
	 *            the im
	 */
	public GamaImageFile(final IScope scope, final String filename, final BufferedImage im, final boolean forOpenGL) {
		super(scope, filename, null);
		ImageUtils.getInstance().forceCacheImage(im, filename, forOpenGL);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// No attributes
		return GamaListFactory.EMPTY_LIST;
	}

	@Override
	public IContainerType getGamlType() { return Types.FILE.of(Types.POINT, Types.INT); }

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		// Temporary workaround for pgm files, which can be read by ImageIO but
		// produce wrong results. See Issue 880.
		// TODO change this behavior
		setBuffer(isPgmFile() || "pgm".equals(getExtension(scope)) ? matrixValueFromPgm(scope, null)
				: matrixValueFromImage(scope, null));
	}

	/**
	 * Checks if is pgm file.
	 *
	 * @return true, if is pgm file
	 */
	protected boolean isPgmFile() { return false; }

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		if (!writable || getBuffer() == null || getBuffer().isEmpty(scope)) return;
		try {
			final File f = getFile(scope);
			f.setWritable(true);
			ImageIO.write(imageFromMatrix(scope), getExtension(scope), f);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final IType contentsType, final GamaPoint preferredSize,
			final boolean copy) throws GamaRuntimeException {
		getContents(scope);
		if (preferredSize != null)
			return matrixValueFromImage(scope, preferredSize).matrixValue(scope, contentsType, copy);
		return getBuffer().matrixValue(scope, contentsType, copy);
	}

	/**
	 * Load image.
	 *
	 * @param scope
	 *            the scope
	 * @param useCache
	 *            the use cache
	 * @return the buffered image
	 */
	protected BufferedImage loadImage(final IScope scope, final boolean useCache, final boolean forOpenGL) {
		// if (image == null) {
		final BufferedImage image;
		try {
			image = ImageUtils.getInstance().getImageFromFile(scope, getPath(scope), useCache, forOpenGL, null,
					extension);
			if (image == null) throw GamaRuntimeException.error("This image format (." + getExtension(scope)
					+ ") is not recognized. Please use a proper operator to read it (for example, pgm_file to read a .pgm format",
					scope);
		} catch (final IOException e) {
			GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.create(e, scope), true);
			return null;
		}
		// }
		return image;
	}

	/**
	 * Gets the image.
	 *
	 * @param scope
	 *            the scope
	 * @param useCache
	 *            the use cache
	 * @return the image
	 */
	@Override
	public BufferedImage getImage(final IScope scope, final boolean useCache, final boolean forOpenGL) {
		return loadImage(scope, useCache, forOpenGL);
	}

	/**
	 * Matrix value from image.
	 *
	 * @param scope
	 *            the scope
	 * @param preferredSize
	 *            the preferred size
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private IMatrix matrixValueFromImage(final IScope scope, final GamaPoint preferredSize)
			throws GamaRuntimeException {
		final BufferedImage image = loadImage(scope, true, false);
		return matrixValueFromImage(scope, image, preferredSize);
	}

	/**
	 * Matrix value from image.
	 *
	 * @param scope
	 *            the scope
	 * @param image
	 *            the image
	 * @param preferredSize
	 *            the preferred size
	 * @return the i matrix
	 */
	private IMatrix matrixValueFromImage(final IScope scope, final BufferedImage image, final GamaPoint preferredSize) {
		int xSize, ySize;
		BufferedImage resultingImage = image;
		if (preferredSize == null) {
			xSize = image.getWidth();
			ySize = image.getHeight();
		} else {
			xSize = (int) preferredSize.getX();
			ySize = (int) preferredSize.getY();
			resultingImage = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g = resultingImage.createGraphics();
			g.drawImage(image, 0, 0, xSize, ySize, null);
			g.dispose();
			// image = resultingImage;
		}
		final IMatrix matrix = new GamaIntMatrix(xSize, ySize);
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) { matrix.set(scope, i, j, resultingImage.getRGB(i, j)); }
		}
		return matrix;

	}

	/**
	 * Image from matrix.
	 *
	 * @param scope
	 *            the scope
	 * @return the buffered image
	 */
	private BufferedImage imageFromMatrix(final IScope scope) {
		final int xSize = getBuffer().getCols(scope);
		final int ySize = getBuffer().getRows(scope);
		final BufferedImage resultingImage = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) { resultingImage.setRGB(i, j, getBuffer().get(scope, i, j)); }
		}
		return resultingImage;

	}

	/**
	 * Matrix value from pgm.
	 *
	 * @param scope
	 *            the scope
	 * @param preferredSize
	 *            the preferred size
	 * @return the i matrix
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private IMatrix matrixValueFromPgm(final IScope scope, final GamaPoint preferredSize) throws GamaRuntimeException {
		// TODO PreferredSize is not respected here
		try (BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
			StringTokenizer tok;
			String str = in.readLine();
			if (str != null && !"P2".equals(str))
				throw new UnsupportedEncodingException("File is not in PGM ascii format");
			str = in.readLine();
			if (str == null) return GamaMatrixType.with(scope, 0, preferredSize, Types.INT);
			tok = new StringTokenizer(str);
			final int xSize = Integer.parseInt(tok.nextToken());
			final int ySize = Integer.parseInt(tok.nextToken());
			in.readLine();
			final StringBuilder buf = new StringBuilder();
			String line = in.readLine();
			while (line != null) {
				buf.append(line);
				buf.append(' ');
				line = in.readLine();
			}
			// in.close();
			str = buf.toString();
			tok = new StringTokenizer(str);
			final IMatrix matrix = new GamaIntMatrix(xSize, ySize);
			for (int j = 0; j < ySize; j++) {
				for (int i = 0; i < xSize; i++) {
					final Integer val = Integer.valueOf(tok.nextToken());
					matrix.set(scope, i, j, val);
				}
			}
			return matrix;
		} catch (final Throwable ex) {
			throw GamaRuntimeException.create(ex, scope);
		}

	}

	/**
	 * Gets the geo data file.
	 *
	 * @param scope
	 *            the scope
	 * @return the geo data file
	 */
	public String getGeoDataFile(final IScope scope) {
		final String extension = getExtension(scope);
		String val = null;
		String geodataFile = getPath(scope).replaceAll(extension, "");
		if ("jpg".equals(extension)) {
			geodataFile = geodataFile + "jgw";
		} else if ("png".equals(extension)) {
			geodataFile = geodataFile + "pgw";
		} else if ("tiff".equals(extension) || "tif".equals(extension)) {
			geodataFile = geodataFile + "tfw";
			val = "";
		} else
			return null;
		final File infodata = new File(geodataFile);
		if (infodata.exists()) return geodataFile;
		return val;
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		final String geodataFile = getGeoDataFile(scope);
		double cellSizeX = 1;
		double cellSizeY = 1;
		double xllcorner = 0;
		double yllcorner = 0;
		boolean xNeg = false;
		boolean yNeg = false;
		final String extension = getExtension(scope);
		if (geodataFile != null && !"".equals(geodataFile)) {
			try (final InputStream ips = new FileInputStream(geodataFile);
					final InputStreamReader ipsr = new InputStreamReader(ips);
					final BufferedReader in = new BufferedReader(ipsr);) {
				String line = in.readLine();
				if (line != null) {
					final String[] cellSizeXStr = line.split(" ");
					cellSizeX = Double.parseDouble(cellSizeXStr[cellSizeXStr.length - 1]);
				}
				xNeg = cellSizeX < 0;
				line = in.readLine();
				line = in.readLine();
				line = in.readLine();
				if (line != null) {
					final String[] cellSizeYStr = line.split(" ");
					cellSizeY = Double.parseDouble(cellSizeYStr[cellSizeYStr.length - 1]);
				}
				yNeg = cellSizeY < 0;
				line = in.readLine();
				if (line != null) {
					final String[] xllcornerStr = line.split(" ");
					xllcorner = Double.parseDouble(xllcornerStr[xllcornerStr.length - 1]);
				}
				line = in.readLine();
				if (line != null) {
					final String[] yllcornerStr = line.split(" ");
					yllcorner = Double.parseDouble(yllcornerStr[yllcornerStr.length - 1]);
				}
				isGeoreferenced = true;

			} catch (final Throwable e) {
				throw GamaRuntimeException.create(e, scope);
			}
		} else if ("tiff".equals(extension) || "tif".equals(extension)) {
			final GamaGridFile file = new GamaGridFile(null, this.getPath(scope));

			final Envelope e = file.computeEnvelope(scope);
			if (e != null) {
				GamaPoint minCorner = new GamaPoint(e.getMinX(), e.getMinY());
				GamaPoint maxCorner = new GamaPoint(e.getMaxX(), e.getMaxY());
				if (geodataFile != null) {
					IProjection pr;
					try {
						pr = scope.getSimulation().getProjectionFactory().forSavingWith(scope,
								file.gis.getTargetCRS(scope));
						minCorner = new GamaShape(pr.transform(minCorner.getInnerGeometry())).getLocation();
						maxCorner = new GamaShape(pr.transform(maxCorner.getInnerGeometry())).getLocation();
					} catch (final FactoryException e1) {
						e1.printStackTrace();
					}

				}
				isGeoreferenced = true;
				return Envelope3D.of(minCorner.x, maxCorner.x, minCorner.y, maxCorner.y, 0, 0);
			}

		}
		final int nbCols = getCols(scope);
		final int nbRows = getRows(scope);

		final double x1 = xllcorner;
		final double x2 = xllcorner + cellSizeX * nbCols;
		final double y1 = yllcorner;
		final double y2 = yllcorner + cellSizeY * nbRows;
		GamaPoint minCorner =
				new GamaPoint(xNeg ? Math.max(x1, x2) : Math.min(x1, x2), yNeg ? Math.max(y1, y2) : Math.min(y1, y2));
		GamaPoint maxCorner =
				new GamaPoint(xNeg ? Math.min(x1, x2) : Math.max(x1, x2), yNeg ? Math.min(y1, y2) : Math.max(y1, y2));
		if (geodataFile != null) {
			minCorner = Projections.to_GAMA_CRS(scope, minCorner, "EPSG:3857").getLocation();
			maxCorner = Projections.to_GAMA_CRS(scope, maxCorner, "EPSG:3857").getLocation();
		}

		return Envelope3D.of(minCorner.x, maxCorner.x, minCorner.y, maxCorner.y, 0, 0);

	}

	/**
	 * Checks if is georeferenced.
	 *
	 * @return true, if is georeferenced
	 */
	public boolean isGeoreferenced() { return isGeoreferenced; }

	@Override
	public double getNoData(final IScope scope) {
		return IField.NO_NO_DATA;
	}

	@Override
	public int getRows(final IScope scope) {
		final BufferedImage image = loadImage(scope, true, false);
		if (image == null) return 0;
		return image.getHeight();
	}

	@Override
	public int getCols(final IScope scope) {
		final BufferedImage image = loadImage(scope, true, false);
		if (image == null) return 0;
		return image.getWidth();
	}

	@Override
	public int getBandsNumber(final IScope scope) {
		BufferedImage image = getImage(scope, true);
		return image.getColorModel().getNumComponents();
	}

	/**
	 * Gets the extension.
	 *
	 * @return the extension
	 */
	public String getExtension() { return extension; }

	@Override
	public double[] getBand(final IScope scope, final int index) {
		BufferedImage image = getImage(scope, true);
		final double[] values = new double[image.getWidth() * image.getHeight()];
		int[] pixels = new int[values.length];
		PixelGrabber pgb =
				new PixelGrabber(image, 0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		try {
			pgb.grabPixels();
		} catch (InterruptedException e) {}
		for (int i = 0; i < values.length; ++i) {
			// Verify this ... Especially if the number of color components does not correspond
			values[i] = pixels[i] & (index + 1) * 255;
		}
		return values;
	}

}
