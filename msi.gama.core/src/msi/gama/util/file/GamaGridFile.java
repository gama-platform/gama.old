package msi.gama.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.types.GamaGeometryType;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.PrjFileReader;
import org.geotools.factory.Hints;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeneralEnvelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

@file(name = "grid", extensions = { "asc" })
public class GamaGridFile extends GamaGisFile {

	private GamaGridReader reader;

	private GamaGridReader createReader(final IScope scope) {
		if ( reader == null ) {
			final File gridFile = getFile();
			gridFile.setReadable(true);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(gridFile);
			} catch (FileNotFoundException e) {
				// Should not happen;
			}
			try {
				reader = new GamaGridReader(scope, fis);
			} catch (GamaRuntimeException e) {
				// A problem appeared, likely related to the wrong format of the file (see Issue 412)
				GAMA.reportError(
					GamaRuntimeException.warning("The format of " + getFile().getName() +
						" is incorrect. Attempting to read it anyway."), false);
				StringBuilder text = new StringBuilder();
				String NL = System.getProperty("line.separator");
				Scanner scanner = null;
				try {
					scanner = new Scanner(getFile());
					while (scanner.hasNextLine()) {
						text.append(scanner.nextLine() + NL);
					}
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				} finally {
					if ( scanner != null ) {
						scanner.close();
					}
				}
				text.append(NL);
				// fis = new StringBufferInputStream(text.toString());
				reader = new GamaGridReader(scope, new StringBufferInputStream(text.toString()));
			}
		}
		return reader;
	}

	class GamaGridReader {

		int numRows, numCols;
		IShape geom;

		GamaGridReader(final IScope scope, final InputStream fis) throws GamaRuntimeException {
			setBuffer(new GamaList());
			ArcGridReader store = null;
			try {
				// Necessary to compute it here, because it needs to be passed to the Hints
				CoordinateReferenceSystem crs = getExistingCRS(scope);
				store =
					new ArcGridReader(fis, new Hints(Hints.USE_JAI_IMAGEREAD, false,
						Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, crs));
				final GeneralEnvelope genv = store.getOriginalEnvelope();
				Envelope env =
					new Envelope(genv.getMinimum(0), genv.getMaximum(0), genv.getMinimum(1), genv.getMaximum(1));
				computeProjection(scope, env);
				numRows = store.getOriginalGridRange().getHigh(1) + 1;
				numCols = store.getOriginalGridRange().getHigh(0) + 1;
				final double cellHeight = genv.getSpan(1) / numRows;
				final double cellWidth = genv.getSpan(0) / numCols;
				final GamaList<IShape> shapes = new GamaList<IShape>();
				final double originX = genv.getMinimum(0);
				final double maxY = genv.getMaximum(1);
				shapes.add(new GamaPoint(originX, genv.getMinimum(1)));
				shapes.add(new GamaPoint(genv.getMaximum(0), genv.getMinimum(1)));
				shapes.add(new GamaPoint(genv.getMaximum(0), maxY));
				shapes.add(new GamaPoint(originX, maxY));
				shapes.add(shapes.get(0));
				geom = GamaGeometryType.buildPolygon(shapes);
				final GamaPoint p = new GamaPoint(0, 0);
				GridCoverage2D coverage;
				coverage = store.read(null);
				final double cmx = cellWidth / 2;
				final double cmy = cellHeight / 2;
				for ( int i = 0, n = numRows * numCols; i < n; i++ ) {
					final int yy = i / numCols;
					final int xx = i - yy * numCols;
					p.x = originX + xx * cellWidth + cmx;
					p.y = maxY - (yy * cellHeight + cmy);
					GamaShape rect = (GamaShape) GamaGeometryType.buildRectangle(cellWidth, cellHeight, p);
					final double[] vals =
						(double[]) coverage.evaluate(new DirectPosition2D(rect.getLocation().getX(), rect.getLocation()
							.getY()));

					rect = new GamaShape(gis.transform(rect.getInnerGeometry()));
					rect.getOrCreateAttributes();
					rect.getAttributes().put("grid_value", vals[0]);
					((IList) getBuffer()).add(rect);
				}
			} catch (final Exception e) {
				final GamaRuntimeException ex =
					GamaRuntimeException.error("The format of " + getFile().getName() + " is not correct. Error: " +
						e.getMessage());
				ex.addContext("for file " + getFile().getPath());
				throw ex;
			} finally {
				if ( store != null ) {
					store.dispose();
				}
			}
		}
	}

	public GamaGridFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	public GamaGridFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}
	public GamaGridFile(final IScope scope, final String pathName, final String code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		fillBuffer(scope);
		return gis.getProjectedEnvelope();
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( getBuffer() != null ) { return; }
		createReader(scope);
	}

	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO at least, save as ASCII grid (plain text)

	}

	public int getNbRows(final IScope scope) {
		return createReader(scope).numRows;
	}

	public int getNbCols(final IScope scope) {
		return createReader(scope).numCols;
	}

	@Override
	public IShape getGeometry(final IScope scope) {
		return createReader(scope).geom;
	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS() {
		File source = getFile();
		// check to see if there is a projection file
		// getting name for the prj file
		final String sourceAsString;
		sourceAsString = source.getAbsolutePath();
		int index = sourceAsString.lastIndexOf(".");
		final StringBuffer prjFileName;
		if ( index == -1 ) {
			prjFileName = new StringBuffer(sourceAsString);
		} else {
			prjFileName = new StringBuffer(sourceAsString.substring(0, index));
		}
		prjFileName.append(".prj");

		// does it exist?
		final File prjFile = new File(prjFileName.toString());
		if ( prjFile.exists() ) {
			// it exists then we have to read it
			PrjFileReader projReader = null;
			try {
				FileChannel channel = new FileInputStream(prjFile).getChannel();
				projReader = new PrjFileReader(channel);
				return projReader.getCoordinateReferenceSystem();
			} catch (FileNotFoundException e) {
				// warn about the error but proceed, it is not fatal
				// we have at least the default crs to use
				return null;
			} catch (IOException e) {
				// warn about the error but proceed, it is not fatal
				// we have at least the default crs to use
				return null;
			} catch (FactoryException e) {
				// warn about the error but proceed, it is not fatal
				// we have at least the default crs to use
				return null;
			} finally {
				if ( projReader != null ) {
					try {
						projReader.close();
					} catch (IOException e) {
						// warn about the error but proceed, it is not fatal
						// we have at least the default crs to use
						return null;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		reader = null;
	}

}
