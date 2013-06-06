package msi.gama.util.file;

import java.io.*;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
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

	public GamaGridFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		final File gridFile = getFile();
		gridFile.setReadable(true);
		ArcGridReader store = null;
		Envelope env = null;
		try {
			store = new ArcGridReader(gridFile, new Hints(Hints.USE_JAI_IMAGEREAD, false));
			final GeneralEnvelope genv = store.getOriginalEnvelope();
			env = new Envelope(genv.getMinimum(0), genv.getMaximum(0), genv.getMinimum(1), genv.getMaximum(1));
			if ( store.getCrs() != null ) {
				final double latitude = env.centre().y;
				final double longitude = env.centre().x;
				final GisUtils gis = scope.getTopology().getGisUtils();
				gis.setTransformCRS(store.getCrs(), longitude,latitude);
				env = gis.transform(env);
			}
		} catch (final IOException e) {
			final GamaRuntimeException ex = GamaRuntimeException.create(e);
			ex.addContext("for file " + gridFile.getPath());
			throw ex;
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}
		return env;
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList();
		final File gridFile = getFile();
		gridFile.setReadable(true);
		ArcGridReader store = null;
		try {
			try {
				store = new ArcGridReader(gridFile, new Hints(Hints.USE_JAI_IMAGEREAD, false));
			} catch (final Exception e) {
				final GamaRuntimeException ex = GamaRuntimeException.create(e);
				ex.addContext("for file " + gridFile.getPath());
				throw ex;
			}
			final GeneralEnvelope genv = store.getOriginalEnvelope();
			final int numRows = store.getOriginalGridRange().getHigh(1) + 1;
			final int numCols = store.getOriginalGridRange().getHigh(0) + 1;
			final double cellHeight = genv.getSpan(1) / numRows;
			final double cellWidth = genv.getSpan(0) / numCols;
			final GamaList<IShape> shapes = new GamaList<IShape>();
			shapes.add(new GamaPoint(genv.getMinimum(0), genv.getMinimum(1)));
			shapes.add(new GamaPoint(genv.getMaximum(0), genv.getMinimum(1)));
			shapes.add(new GamaPoint(genv.getMaximum(0), genv.getMaximum(1)));
			shapes.add(new GamaPoint(genv.getMinimum(0), genv.getMaximum(1)));
			shapes.add(shapes.get(0));
			// IShape environmentFrame = GamaGeometryType.buildPolygon(shapes);

			final GamaPoint p = new GamaPoint(0, 0);
			// GamaPoint origin =
			// new GamaPoint(environmentFrame.getEnvelope().getMinX(), environmentFrame.getEnvelope()
			// .getMinY());
			final double originX = genv.getMinimum(0);
			final double maxY = genv.getMaximum(1);
			// GeometryUtils.translation(g, -origin.x, -origin.y);
			GridCoverage2D coverage;
			try {
				coverage = store.read(null);
			} catch (final Exception e) {
				final GamaRuntimeException ex = GamaRuntimeException.create(e);
				ex.addContext("for file " + gridFile.getPath());
				throw ex;
			}
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
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}

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

	public int getNbRows() {
		final File gridFile = getFile();
		gridFile.setReadable(true);
		ArcGridReader store = null;
		int nbRows = 0;
		try {
			store = new ArcGridReader(gridFile, new Hints(Hints.USE_JAI_IMAGEREAD, false));
			nbRows = store.getOriginalGridRange().getHigh(1) + 1;
		} catch (final Exception e) {
			final GamaRuntimeException ex = GamaRuntimeException.create(e);
			ex.addContext("for file " + gridFile.getPath());
			throw ex;
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}
		return nbRows;
	}

	public int getNbCols() {
		final File gridFile = getFile();
		gridFile.setReadable(true);
		ArcGridReader store = null;
		int nbRows = 0;
		try {
			store = new ArcGridReader(gridFile, new Hints(Hints.USE_JAI_IMAGEREAD, false));
			nbRows = store.getOriginalGridRange().getHigh(0) + 1;
		} catch (final Exception e) {
			final GamaRuntimeException ex = GamaRuntimeException.create(e);
			ex.addContext("for file " + gridFile.getPath());
			throw ex;
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}
		return nbRows;
	}

	public IShape getGeometry() {
		final File gridFile = getFile();
		gridFile.setReadable(true);
		ArcGridReader store = null;
		IShape geom = null;
		try {
			store = new ArcGridReader(gridFile, new Hints(Hints.USE_JAI_IMAGEREAD, false));
			final GeneralEnvelope genv = store.getOriginalEnvelope();
			final GamaList<IShape> shapes = new GamaList<IShape>();
			shapes.add(new GamaPoint(genv.getMinimum(0), genv.getMinimum(1)));
			shapes.add(new GamaPoint(genv.getMaximum(0), genv.getMinimum(1)));
			shapes.add(new GamaPoint(genv.getMaximum(0), genv.getMaximum(1)));
			shapes.add(new GamaPoint(genv.getMinimum(0), genv.getMaximum(1)));
			shapes.add(shapes.get(0));
			geom = GamaGeometryType.buildPolygon(shapes);
		} catch (final Exception e) {
			final GamaRuntimeException ex = GamaRuntimeException.create(e);
			ex.addContext("for file " + gridFile.getPath());
			throw ex;
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}
		return geom;
	}

}
