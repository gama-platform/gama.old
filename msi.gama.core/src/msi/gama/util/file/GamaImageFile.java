/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.StringTokenizer;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import msi.gaml.types.GamaMatrixType;
import com.vividsolutions.jts.geom.Envelope;

@file(name = "image", extensions = { "tif", "tiff", "jpg", "jpeg", "png", "gif", "pict", "bmp" })
public class GamaImageFile extends GamaFile<GamaPoint, Integer> {

	@file(name = "pgm", extensions = { "pgm" })
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

	public GamaImageFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = isPgmFile() ? matrixValueFromPgm(scope, null) : matrixValueFromImage(scope, null);
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
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO Create a rendered image from the Matrix.
		// Use ImageIO to write it.
	}

	@Override
	protected IGamaFile _copy(final IScope scope) {
		return null;
	}

	// @Override
	// protected boolean _isFixedLength() {
	// return true;
	// }

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize) throws GamaRuntimeException {
		getContents(scope);
		if ( preferredSize != null ) { return matrixValueFromImage(scope, preferredSize); }
		return (IMatrix) buffer;
	}

	private void loadImage(final IScope scope) {
		if ( image == null ) {
			try {
				image = ImageUtils.getInstance().getImageFromFile(path);
			} catch (final IOException e) {
				throw GamaRuntimeException.create(e);
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

	private IMatrix matrixValueFromImage(final IScope scope, final ILocation preferredSize) throws GamaRuntimeException {
		loadImage(scope);
		int xSize, ySize;
		if ( preferredSize == null ) {
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
		for ( int i = 0; i < xSize; i++ ) {
			for ( int j = 0; j < ySize; j++ ) {
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
			if ( !str.equals("P2") ) { throw new UnsupportedEncodingException("File is not in PGM ascii format"); }
			str = in.readLine();
			if ( str == null ) { return GamaMatrixType.with(scope, 0, preferredSize); }
			tok = new StringTokenizer(str);
			final int xSize = Integer.valueOf(tok.nextToken());
			final int ySize = Integer.valueOf(tok.nextToken());
			in.readLine();
			StringBuilder buf = new StringBuilder();
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
			for ( int i = 0; i < xSize; i++ ) {
				for ( int j = 0; j < ySize; j++ ) {
					matrix.set(scope, j, i, Integer.valueOf(tok.nextToken()));
				}
			}
			return matrix;
		} catch (final Exception ex) {
			throw GamaRuntimeException.create(ex);
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch (IOException e) {
					throw GamaRuntimeException.create(e);
				}
			}
		}
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		int nbCols = getWidth(scope);
		int nbRows = getHeight(scope);
		String extension = getExtension();
		String geodataFile = getPath().replaceAll(extension, "");
		if ( extension.equals("jpg") ) {
			geodataFile = geodataFile + "jgw";
		} else if ( extension.equals("png") ) {
			geodataFile = geodataFile + "pgw";
		} else if ( extension.equals("tiff") ) {
			geodataFile = geodataFile + "tfw";
		}

		File infodata = new File(geodataFile);
		double cellSizeX = 1;
		double cellSizeY = 1;
		double xllcorner = 0;
		double yllcorner = 0;
		if ( infodata.exists() ) {
			try {
				InputStream ips = new FileInputStream(geodataFile);
				InputStreamReader ipsr = new InputStreamReader(ips);
				BufferedReader in = new BufferedReader(ipsr);
				String[] cellSizeXStr = in.readLine().split(" ");
				cellSizeX = Double.valueOf(cellSizeXStr[cellSizeXStr.length - 1]);
				in.readLine();
				in.readLine();
				String[] cellSizeYStr = in.readLine().split(" ");
				cellSizeY = Double.valueOf(cellSizeYStr[cellSizeYStr.length - 1]);
				String[] xllcornerStr = in.readLine().split(" ");
				xllcorner = Double.valueOf(xllcornerStr[xllcornerStr.length - 1]);
				String[] yllcornerStr = in.readLine().split(" ");
				yllcorner = Double.valueOf(yllcornerStr[yllcornerStr.length - 1]);
				in.close();
			} catch (Exception e) {
				throw GamaRuntimeException.create(e);
			}
		}
		double x1 = xllcorner;
		double x2 = xllcorner + cellSizeX * nbCols;
		double y1 = yllcorner;
		double y2 = yllcorner + cellSizeY * nbRows;

		Envelope boundsEnv = new Envelope(Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2));
		return boundsEnv;

	}

	// @Override
	// public String getKeyword() {
	// return Files.IMAGE;
	// }

}
