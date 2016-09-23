/*********************************************************************************************
 *
 *
 * 'GamaImageFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import org.opengis.referencing.FactoryException;

import com.vividsolutions.jts.geom.Envelope;

import gnu.trove.map.hash.TIntObjectHashMap;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Spatial.Projections;
import msi.gaml.operators.Strings;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file(name = "image", extensions = { "tiff", "jpg", "jpeg", "png", "gif", "pict",
		"bmp" }, buffer_type = IType.MATRIX, buffer_content = IType.INT, buffer_index = IType.POINT, concept = {
				IConcept.IMAGE, IConcept.FILE })
public class GamaImageFile extends GamaFile<IMatrix<Integer>, Integer, ILocation, Integer> {

	public static class ImageInfo extends GamaFileMetaData {

		public final static TIntObjectHashMap<String> formatsShortNames = new TIntObjectHashMap() {

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

		// private Object thumbnail;
		private final int type;
		private final int width;
		private final int height;

		public ImageInfo(final long modificationStamp,
				/* final Object thumbnail, */final int origType, final int origWidth, final int origHeight) {
			super(modificationStamp);
			// this.thumbnail = thumbnail;
			this.type = origType;
			this.width = origWidth;
			this.height = origHeight;
		}

		public ImageInfo(final String propertyString) {
			super(propertyString);
			final String[] segments = split(propertyString);
			type = Integer.valueOf(segments[1]);
			width = Integer.valueOf(segments[2]);
			height = Integer.valueOf(segments[3]);
			// thumbnail = null;
		}

		public String getShortLabel(final int type) {
			return formatsShortNames.contains(type) ? formatsShortNames.get(type) : formatsShortNames.get(-1);
		}

		// public void setThumbnail(final Object thumb) {
		// thumbnail = thumb;
		// }

		@Override
		public String getSuffix() {
			return "" + width + "x" + height + ", " + getShortLabel(type) + "";
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append(getShortLabel(type)).append(" Image File").append(Strings.LN);
			sb.append("Dimensions: ").append(width + " pixels x " + height + " pixels").append(Strings.LN);
			return sb.toString();
		}

		// @Override
		// public Object getThumbnail() {
		// return thumbnail;
		// }

		public int getType() {
			return type;
		}

		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + type + DELIMITER + width + DELIMITER + height;
		}
	}

	@file(name = "pgm", extensions = { "pgm" }, buffer_type = IType.MATRIX, buffer_content = IType.INT)
	public static class GamaPgmFile extends GamaImageFile {

		/**
		 * @param scope
		 * @param pathName
		 * @throws GamaRuntimeException
		 */
		public GamaPgmFile(final IScope scope, final String pathName) throws GamaRuntimeException {
			super(scope, pathName);
		}

		@Override
		protected boolean isPgmFile() {
			return true;
		}

	}

	private BufferedImage image;
	private boolean isGeoreferenced = false;

	public GamaImageFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaImageFile(final IScope scope, final String pathName, final IMatrix<Integer> image) {
		super(scope, pathName, image);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// No attributes
		return GamaListFactory.create();
	}

	@Override
	public IContainerType getType() {
		return Types.FILE.of(Types.POINT, Types.INT);
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) {
			return;
		}
		// Temporary workaround for pgm files, which can be read by ImageIO but
		// produce wrong results. See Issue 880.
		// TODO change this behavior
		setBuffer(isPgmFile() || getExtension().equals("pgm") ? matrixValueFromPgm(scope, null)
				: matrixValueFromImage(scope, null));
	}

	protected boolean isPgmFile() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer(IScope scope) throws GamaRuntimeException {
		// TODO Create a rendered image from the Matrix.
		// Use ImageIO to write it.
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		getContents(scope);
		if (preferredSize != null) {
			return matrixValueFromImage(scope, preferredSize).matrixValue(scope, contentsType, copy);
		}
		return getBuffer().matrixValue(scope, contentsType, copy);
	}

	private void loadImage(final IScope scope) {
		if (image == null) {
			try {
				image = ImageUtils.getInstance().getImageFromFile(scope, path);
				if (image == null) {
					throw GamaRuntimeException.error(
							"This image format (." + getExtension()
									+ ") is not recognized. Please use a proper operator to read it (for example, pgm_file to read a .pgm format",
							scope);
				}
			} catch (final IOException e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}
	}

	public BufferedImage getImage(final IScope scope) {
		loadImage(scope);
		return image;
	}

	public int getWidth(final IScope scope) {
		loadImage(scope);
		return image.getWidth();
	}

	public int getHeight(final IScope scope) {
		loadImage(scope);
		return image.getHeight();

	}

	private IMatrix matrixValueFromImage(final IScope scope, final ILocation preferredSize)
			throws GamaRuntimeException {
		loadImage(scope);
		int xSize, ySize;
		if (preferredSize == null) {
			xSize = image.getWidth();
			ySize = image.getHeight();
		} else {
			xSize = (int) preferredSize.getX();
			ySize = (int) preferredSize.getY();
			final BufferedImage resultingImage = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g = resultingImage.createGraphics();
			g.drawImage(image, 0, 0, xSize, ySize, null);
			g.dispose();
			image = resultingImage;
		}
		final IMatrix matrix = new GamaIntMatrix(xSize, ySize);
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				matrix.set(scope, i, j, image.getRGB(i, j));
			}
		}
		return matrix;
	}

	private IMatrix matrixValueFromPgm(final IScope scope, final GamaPoint preferredSize) throws GamaRuntimeException {
		// TODO PreferredSize is not respected here
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(getFile()));
			StringTokenizer tok;
			String str = in.readLine();
			if (!str.equals("P2")) {
				throw new UnsupportedEncodingException("File is not in PGM ascii format");
			}
			str = in.readLine();
			if (str == null) {
				return GamaMatrixType.with(scope, 0, preferredSize, Types.INT);
			}
			tok = new StringTokenizer(str);
			final int xSize = Integer.valueOf(tok.nextToken());
			final int ySize = Integer.valueOf(tok.nextToken());
			in.readLine();
			final StringBuilder buf = new StringBuilder();
			String line = in.readLine();
			while (line != null) {
				buf.append(line);
				buf.append(' ');
				line = in.readLine();
			}
			in.close();
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
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException e) {
					throw GamaRuntimeException.create(e, scope);
				}
			}
		}
	}

	public String getGeoDataFile() {
		final String extension = getExtension();
		String val = null;
		String geodataFile = getPath().replaceAll(extension, "");
		if (extension.equals("jpg")) {
			geodataFile = geodataFile + "jgw";
		} else if (extension.equals("png")) {
			geodataFile = geodataFile + "pgw";
		} else if (extension.equals("tiff") || extension.equals("tif")) {
			geodataFile = geodataFile + "tfw";
			val = "";
		} else {
			return null;
		}
		final File infodata = new File(geodataFile);
		if (infodata.exists()) {
			return geodataFile;
		}
		return val;
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		final String geodataFile = getGeoDataFile();
		double cellSizeX = 1;
		double cellSizeY = 1;
		double xllcorner = 0;
		double yllcorner = 0;
		boolean xNeg = false;
		boolean yNeg = false;
		final String extension = getExtension();
		if (geodataFile != null && !geodataFile.equals("")) {
			try {
				final InputStream ips = new FileInputStream(geodataFile);
				final InputStreamReader ipsr = new InputStreamReader(ips);
				final BufferedReader in = new BufferedReader(ipsr);
				final String[] cellSizeXStr = in.readLine().split(" ");
				cellSizeX = Double.valueOf(cellSizeXStr[cellSizeXStr.length - 1]);
				xNeg = cellSizeX < 0;
				in.readLine();
				in.readLine();
				final String[] cellSizeYStr = in.readLine().split(" ");
				cellSizeY = Double.valueOf(cellSizeYStr[cellSizeYStr.length - 1]);
				yNeg = cellSizeY < 0;
				final String[] xllcornerStr = in.readLine().split(" ");
				xllcorner = Double.valueOf(xllcornerStr[xllcornerStr.length - 1]);
				final String[] yllcornerStr = in.readLine().split(" ");
				yllcorner = Double.valueOf(yllcornerStr[yllcornerStr.length - 1]);
				isGeoreferenced = true;

				in.close();
			} catch (final Throwable e) {
				throw GamaRuntimeException.create(e, scope);
			}
		} else if (extension.equals("tiff") || extension.equals("tif")) {
			final GamaGridFile file = new GamaGridFile(null, this.getPath());

			final Envelope e = file.computeEnvelopeWithoutBuffer(scope);
			if (e != null) {
				GamaPoint minCorner = new GamaPoint(e.getMinX(), e.getMinY());
				GamaPoint maxCorner = new GamaPoint(e.getMaxX(), e.getMaxY());
				if (geodataFile != null) {
					IProjection pr;
					try {
						pr = scope.getSimulation().getProjectionFactory().forSavingWith(file.gis.getTargetCRS());
						minCorner = new GamaShape(pr.transform(minCorner.getInnerGeometry())).getLocation();
						maxCorner = new GamaShape(pr.transform(maxCorner.getInnerGeometry())).getLocation();
					} catch (final FactoryException e1) {
						e1.printStackTrace();
					}

				}
				isGeoreferenced = true;
				return new Envelope(minCorner.x, maxCorner.x, minCorner.y, maxCorner.y);
			}

		}
		final int nbCols = getWidth(scope);
		final int nbRows = getHeight(scope);

		final double x1 = xllcorner;
		final double x2 = xllcorner + cellSizeX * nbCols;
		final double y1 = yllcorner;
		final double y2 = yllcorner + cellSizeY * nbRows;
		GamaPoint minCorner = new GamaPoint(xNeg ? FastMath.max(x1, x2) : FastMath.min(x1, x2),
				yNeg ? FastMath.max(y1, y2) : FastMath.min(y1, y2));
		GamaPoint maxCorner = new GamaPoint(xNeg ? FastMath.min(x1, x2) : FastMath.max(x1, x2),
				yNeg ? FastMath.min(y1, y2) : FastMath.max(y1, y2));
		if (geodataFile != null) {
			minCorner = (GamaPoint) Projections.to_GAMA_CRS(scope, minCorner).getLocation();
			maxCorner = (GamaPoint) Projections.to_GAMA_CRS(scope, maxCorner).getLocation();
		}

		final Envelope boundsEnv = new Envelope(minCorner.x, maxCorner.x, minCorner.y, maxCorner.y);
		return boundsEnv;

	}

	public boolean isGeoreferenced() {
		return isGeoreferenced;
	}

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		image = null;
	}

	// @Override
	// public String getKeyword() {
	// return Files.IMAGE;
	// }

	public void setImage(final IScope scope, final BufferedImage image2) {
		// AD QUESTION : Shouldnt we also erase the buffer in that case ?
		image = image2;
	}

}
