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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import msi.gaml.operators.Files;
import msi.gaml.types.*;

public class GamaImageFile extends GamaFile<GamaPoint, Integer> {

	private BufferedImage image;

	public GamaImageFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	protected void fillBuffer() throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = isPgmFile() ? matrixValueFromPgm(null) : matrixValueFromImage(null);
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isImageFile(getFile().getName()) ) { throw new GamaRuntimeException(
			"The extension " + this.getExtension() + " is not recognized for image files"); }
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
	protected IGamaFile _copy() {
		return null;
	}

	@Override
	protected boolean _isFixedLength() {
		return true;
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize)
		throws GamaRuntimeException {
		if ( preferredSize != null ) { return matrixValueFromImage(preferredSize); }
		return (IMatrix) buffer;
	}

	private void loadImage() {
		if ( image == null ) {
			try {
				image = ImageUtils.getInstance().getImageFromFile(path);
				// image = ImageIO.read(getFile());
			} catch (final IOException e) {
				throw new GamaRuntimeException(e);
			}
		}
	}

	public BufferedImage getImage() {
		loadImage();
		return image;
	}

	public int getWidth() {
		loadImage();
		return image.getWidth();
	}

	public int getHeight() {
		loadImage();
		return image.getHeight();

	}

	private IMatrix matrixValueFromImage(final ILocation preferredSize) throws GamaRuntimeException {
		loadImage();
		int xSize, ySize;
		if ( preferredSize == null ) {
			xSize = image.getWidth();
			ySize = image.getHeight();
		} else {
			xSize = (int) preferredSize.getX();
			ySize = (int) preferredSize.getY();
			final BufferedImage resultingImage =
				new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g = resultingImage.createGraphics();
			g.drawImage(image, 0, 0, xSize, ySize, null);
			g.dispose();
			image = resultingImage;
		}
		final IMatrix matrix = new GamaIntMatrix(xSize, ySize);
		for ( int i = 0; i < xSize; i++ ) {
			for ( int j = 0; j < ySize; j++ ) {
				matrix.set(i, j, image.getRGB(i, j));
			}
		}
		return matrix;
	}

	private IMatrix matrixValueFromPgm(final GamaPoint preferredSize) throws GamaRuntimeException {
		// TODO PreferredSize is not respected here
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(getFile()));
			StringTokenizer tok;
			String str = in.readLine();
			if ( !str.equals("P2") ) { throw new UnsupportedEncodingException(
				"File is not in PGM ascii format"); }
			str = in.readLine();
			if ( str == null ) { return GamaMatrixType.with(0, preferredSize); }
			tok = new StringTokenizer(str);
			final int xSize = Integer.valueOf(tok.nextToken());
			final int ySize = Integer.valueOf(tok.nextToken());
			in.readLine();
			StringBuffer buf = new StringBuffer();
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
					matrix.set(j, i, Integer.valueOf(tok.nextToken()));
				}
			}
			return matrix;
		} catch (final Exception ex) {
			throw new GamaRuntimeException(ex);
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch (IOException e) {
					throw new GamaRuntimeException(e);
				}
			}
		}
	}

	@Override
	public String getKeyword() {
		return Files.IMAGE;
	}

}
