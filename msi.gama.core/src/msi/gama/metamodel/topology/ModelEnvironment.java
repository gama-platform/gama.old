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
package msi.gama.metamodel.topology;

import java.awt.Graphics2D;
import java.io.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.file.GamaFile;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.*;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.*;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 17 janv. 2009
 * 
 * @todo Description
 */
@symbol(name = IKeyword.ENVIRONMENT, kind = ISymbolKind.ENVIRONMENT, with_sequence = false)
@inside(symbols = IKeyword.MODEL)
@facets(value = { @facet(name = IKeyword.WIDTH, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.HEIGHT, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.TORUS, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.BOUNDS, type = IType.NONE_STR, optional = true) }, omissible = IKeyword.BOUNDS)
public class ModelEnvironment extends Symbol implements IEnvironment {

	final IExpression boundsExp, widthExp, heightExp;
	private double width = 100d, height = 100d;

	private ISpatialIndex quadTree;

	public ModelEnvironment(final IDescription desc) {
		super(desc);
		boundsExp = getFacet(IKeyword.BOUNDS);
		widthExp = getFacet(IKeyword.WIDTH);
		heightExp = getFacet(IKeyword.HEIGHT);
	}

	public Envelope loadAscFile(final String boundsStr) throws IOException {
		File ascFile = new File(GAMA.getModel().getRelativeFilePath(boundsStr, true));
		InputStream ips = new FileInputStream(ascFile);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader in = new BufferedReader(ipsr);

		String[] nbColsStr = in.readLine().split(" ");
		int nbCols = Integer.valueOf(nbColsStr[nbColsStr.length - 1]);
		String[] nbRowsStr = in.readLine().split(" ");
		int nbRows = Integer.valueOf(nbRowsStr[nbRowsStr.length - 1]);
		String[] xllcornerStr = in.readLine().split(" ");
		double xllcorner = Double.valueOf(xllcornerStr[xllcornerStr.length - 1]);
		String[] yllcornerStr = in.readLine().split(" ");
		double yllcorner = Double.valueOf(yllcornerStr[yllcornerStr.length - 1]);
		String[] cellSizeStr = in.readLine().split(" ");
		double cellSize = Double.valueOf(cellSizeStr[cellSizeStr.length - 1]);
		Envelope boundsEnv =
			new Envelope(xllcorner, xllcorner + cellSize * nbCols, yllcorner, yllcorner + cellSize *
				nbRows);
		in.close();
		return boundsEnv;
	}

	public Map<String, Object> loadShapeFile(final String boundsStr, MathTransform transformCRS)
		throws IOException, TransformException {
		File shpFile = new File(GAMA.getModel().getRelativeFilePath(boundsStr, true));
		ShapefileDataStore store = new ShapefileDataStore(shpFile.toURI().toURL());
		String name = store.getTypeNames()[0];
		FeatureSource<SimpleFeatureType, SimpleFeature> source = store.getFeatureSource(name);
		// CoordinateReferenceSystem crs = source.getFeatures()
		// .getSchema().getCoordinateReferenceSystem();
		Envelope env = source.getBounds();
		if ( store.getSchema().getCoordinateReferenceSystem() != null ) {
			ShpFiles shpf = new ShpFiles(shpFile);
			double latitude = env.centre().x;
			double longitude = env.centre().y;
			MathTransform transformCRSNew = GisUtils.getTransformCRS(shpf, latitude, longitude);
			if ( transformCRS == null ) {
				transformCRS = transformCRSNew;
			}
			if ( transformCRSNew != null && transformCRS != null ) {
				env = JTS.transform(env, transformCRS);
			}

		}
		store.dispose();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("envelope", env);
		result.put("transformCRS", transformCRS);
		return result;
	}

	@Override
	public void initializeFor(final IScope scope) throws GamaRuntimeException {
		Object bounds = boundsExp == null ? null : boundsExp.value(scope);
		double xMin = 0d, yMin = 0d;
		MathTransform transformCRS = null;
		if ( bounds instanceof Number ) {
			height = width = ((Number) bounds).doubleValue();
		} else if ( bounds instanceof ILocation ) {
			GamaPoint wh = (GamaPoint) bounds;
			width = wh.x;
			height = wh.y;
		} else if ( bounds instanceof GamaList ) {
			Envelope boundsEnv = null;
			for ( Object el : (GamaList) bounds ) {
				String boundsStr =
					el instanceof String ? (String) bounds : el instanceof GamaFile
						? ((GamaFile) el).getPath() : null;
				if ( boundsStr != null ) {
					Envelope env = null;
					try {
						if ( boundsStr.toLowerCase().endsWith(".shp") ) {
							Map<String, Object> result = loadShapeFile(boundsStr, transformCRS);
							env = (Envelope) result.get("envelope");
							transformCRS = (MathTransform) result.get("transformCRS");
						} else if ( boundsStr.toLowerCase().endsWith(".asc") ) {
							env = loadAscFile(boundsStr);
						}
						if ( env != null ) {
							if ( boundsEnv == null ) {
								boundsEnv = env;
							} else {
								boundsEnv.expandToInclude(env);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (TransformException e) {
						e.printStackTrace();
					}
				}
			}
			if ( boundsEnv != null ) {
				xMin = boundsEnv.getMinX();
				yMin = boundsEnv.getMinY();
				width = boundsEnv.getWidth();
				height = boundsEnv.getHeight();
			}
		} else if ( bounds instanceof String || bounds instanceof GamaFile ) {
			String boundsStr =
				bounds instanceof String ? (String) bounds : ((GamaFile) bounds).getPath();
			if ( boundsStr.toLowerCase().endsWith(".shp") ) {
				try {
					Map<String, Object> result = loadShapeFile(boundsStr, transformCRS);
					Envelope boundsEnv = (Envelope) result.get("envelope");
					transformCRS = (MathTransform) result.get("transformCRS");
					xMin = boundsEnv.getMinX();
					yMin = boundsEnv.getMinY();
					width = boundsEnv.getWidth();
					height = boundsEnv.getHeight();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ( boundsStr.toLowerCase().endsWith(".asc") ) {
				try {
					Envelope boundsEnv = loadAscFile(boundsStr);
					xMin = boundsEnv.getMinX();
					yMin = boundsEnv.getMinY();
					width = boundsEnv.getWidth();
					height = boundsEnv.getHeight();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			width = widthExp == null ? width : Cast.asFloat(scope, widthExp.value(scope));
			height = heightExp == null ? height : Cast.asFloat(scope, heightExp.value(scope));

		}
		GisUtils.init(height, width, xMin, yMin, xMin + width, yMin + height, transformCRS);

		initializeSpatialIndex();

	}

	/**
	 * Initializes the global spatial index.
	 */
	private void initializeSpatialIndex() {
		Envelope e = new Envelope(0, width, 0, height);
		quadTree = new GamaQuadTree(e);
	}

	@Override
	public ISpatialIndex getSpatialIndex() {
		return quadTree;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {}

	@Override
	public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {
		if ( quadTree == null ) { return; }
		quadTree.drawOn(g2, width, height);
	}

}
