package msi.gama.util.file;

import java.io.*;
import java.util.Scanner;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Files;
import msi.gaml.types.*;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.Hints;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.geometry.*;
import com.vividsolutions.jts.geom.Envelope;

public class GamaGridFile extends GamaFile<Integer, GamaGisGeometry> {

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

		Envelope env;
		int numRows, numCols;
		IShape geom;

		GamaGridReader(final IScope scope, final InputStream fis) throws GamaRuntimeException {
			buffer = new GamaList();
			ArcGridReader store = null;
			try {
				store = new ArcGridReader(fis, new Hints(Hints.USE_JAI_IMAGEREAD, false));
				final GeneralEnvelope genv = store.getOriginalEnvelope();
				env = new Envelope(genv.getMinimum(0), genv.getMaximum(0), genv.getMinimum(1), genv.getMaximum(1));
				if ( store.getCrs() != null ) {
					final double latitude = env.centre().y;
					final double longitude = env.centre().x;
					final GisUtils gis = scope.getTopology().getGisUtils();
					gis.setInitialCRS(store.getCrs(), longitude, latitude);
					env = gis.transform(env);
				}
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

					rect = new GamaShape(scope.getTopology().getGisUtils().transform(rect.getInnerGeometry()));
					rect.getOrCreateAttributes();
					rect.getAttributes().put("grid_value", vals[0]);
					((IList) buffer).add(rect);
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
		super(scope, pathName);
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return createReader(scope).env;
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		createReader(scope);
	}

	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO at least, save as ASCII grid (plain text)

	}

	@Override
	protected IGamaFile _copy(final IScope scope) {
		// TODO ? Will require to do a copy of the file. But how to get the new name ? Or maybe just
		// as something usable like
		return null;
	}

	@Override
	public String getKeyword() {
		return Files.GRID;
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isGrid(getFile().getName()) ) { throw GamaRuntimeException.error("The extension " +
			this.getExtension() + " is not recognized for ArcGrid files"); }
	}

	public int getNbRows(final IScope scope) {
		return createReader(scope).numRows;
	}

	public int getNbCols(final IScope scope) {
		return createReader(scope).numCols;
	}

	public IShape getGeometry(final IScope scope) {
		return createReader(scope).geom;
	}

}
