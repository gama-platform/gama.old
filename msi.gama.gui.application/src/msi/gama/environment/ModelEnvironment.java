/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import java.awt.Graphics2D;
import java.io.*;
import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.*;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.*;
import org.opengis.referencing.operation.MathTransform;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 17 janv. 2009
 * 
 * @todo Description
 */
@symbol(name = ISymbol.ENVIRONMENT, kind = ISymbolKind.ENVIRONMENT)
@inside(symbols = ISymbol.MODEL)
@facets({ @facet(name = ITopology.WIDTH, type = IType.INT_STR, optional = true),
	@facet(name = ITopology.HEIGHT, type = IType.INT_STR, optional = true),
	@facet(name = ITopology.TORUS, type = IType.BOOL_STR, optional = true),
	@facet(name = ITopology.BOUNDS, type = IType.NONE_STR, optional = true) })
public class ModelEnvironment extends Symbol {

	final IExpression		boundsExp, widthExp, heightExp, torusExp;
	private double			width	= 100d, height = 100d;
	private boolean			isTorus	= false;

	private ISpatialIndex	quadTree;

	public ModelEnvironment(final IDescription desc) {
		super(desc);
		boundsExp = getFacet(ITopology.BOUNDS);
		widthExp = getFacet(ITopology.WIDTH);
		heightExp = getFacet(ITopology.HEIGHT);
		torusExp = getFacet(ITopology.TORUS);
	}

	public void initializeFor(final IScope scope) throws GamaRuntimeException {
		isTorus = torusExp == null ? false : Cast.asBool(scope, torusExp.value(scope));
		Object bounds = boundsExp == null ? null : boundsExp.value(scope);
		double xMin = 0d, yMin = 0d;
		MathTransform transformCRS = null;
		if ( bounds instanceof Number ) {
			height = width = ((Number) bounds).doubleValue();
		} else if ( bounds instanceof GamaPoint ) {
			GamaPoint wh = (GamaPoint) bounds;
			width = wh.x;
			height = wh.y;
		} else if ( bounds instanceof String ) {
			String boundsStr = (String) bounds;
			if ( boundsStr.toLowerCase().endsWith(".shp") ) {
				try {
					File shpFile = new File(GAMA.getModel().getRelativeFilePath(boundsStr, true));
					ShapefileDataStore store = new ShapefileDataStore(shpFile.toURI().toURL());
					String name = store.getTypeNames()[0];
					FeatureSource<SimpleFeatureType, SimpleFeature> source =
						store.getFeatureSource(name);
					// CoordinateReferenceSystem crs = source.getFeatures()
					// .getSchema().getCoordinateReferenceSystem();
					Envelope env = source.getBounds();

					if ( store.getSchema().getCoordinateReferenceSystem() != null ) {
						ShpFiles shpf = new ShpFiles(shpFile);
						double latitude = env.centre().x;
						double longitude = env.centre().y;
						transformCRS = GisUtil.getTransformCRS(shpf, latitude, longitude);
						if ( transformCRS != null ) {
							env = JTS.transform(env, transformCRS);
						}
					}
					xMin = env.getMinX();
					yMin = env.getMinY();
					width = env.getWidth();
					height = env.getHeight();
					store.dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ( boundsStr.toLowerCase().endsWith(".asc") ) {
				try {
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
					xMin = xllcorner;
					yMin = yllcorner;
					width = cellSize * nbCols;
					height = cellSize * nbRows;
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			width = widthExp == null ? width : Cast.asFloat(scope, widthExp.value(scope));
			height = heightExp == null ? height : Cast.asFloat(scope, heightExp.value(scope));

		}
		GisUtil.init(height, width, xMin, yMin, xMin + width, yMin + height, transformCRS);

		initializeSpatialIndex();

	}

	/**
	 * Initializes the global spatial index.
	 */
	private void initializeSpatialIndex() {
		Envelope e = new Envelope(0, width, 0, height);
		quadTree = new GamaQuadTree(e);
	}

	public ISpatialIndex getSpatialIndex() {
		return quadTree;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public boolean isTorus() {
		return isTorus;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {}

	public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {
		if ( quadTree == null ) { return; }
		quadTree.drawOn(g2, width, height);
	}

}
