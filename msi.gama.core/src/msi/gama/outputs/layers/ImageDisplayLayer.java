/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.*;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.IMAGE, kind = ISymbolKind.LAYER)
@inside(symbols = IKeyword.DISPLAY)
@facets({ @facet(name = IKeyword.FILE, type = IType.STRING_STR, optional = true),
	@facet(name = IKeyword.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.GIS, type = IType.STRING_STR, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR_STR, optional = true) })
public class ImageDisplayLayer extends AbstractDisplayLayer {

	private static ImageUtils cachedImages = new ImageUtils();

	public ImageDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(desc);
	}

	IExpression imageFileExpression = null;
	String constantImage = null;
	String currentImage = null;

	private GisLayer gisLayer = null;

	public BufferedImage getImage(final String fileName) throws IOException {
		BufferedImage image;
		File f =
			new File(GAMA.getFrontmostSimulation().getModel().getRelativeFilePath(fileName, true));
		image = ImageIO.read(f);
		cachedImages.add(fileName, image);
		return cachedImages.get(fileName);
	}

	public GisLayer getGisLayer() {
		return gisLayer;
	}

	@Override
	public short getType() {
		if ( getFacet(IKeyword.GIS) == null ) { return IDisplay.IMAGE; }
		return IDisplay.GIS;
	}

	public String getImageFileName() {
		return currentImage;
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope scope) throws GamaRuntimeException {
		super.prepare(out, scope);
		if ( getFacet(IKeyword.GIS) != null ) {
			buildGisLayer(scope);
		} else {
			if ( constantImage == null ) {
				// Redefined to allow replacing the "name" attribute by "file"
				IExpression tag = getFacet(IKeyword.NAME);
				if ( tag == null ) {
					tag = getFacet(IKeyword.FILE);
				}
				if ( tag == null ) { throw new GamaRuntimeException("Missing properties " +
					IKeyword.NAME + " and " + IKeyword.FILE); }
				if ( tag.isConst() ) {
					setName(Cast.asString(scope, tag.value(scope)));
				} else {
					setName(tag.toGaml());
				}
				imageFileExpression = getFacet(IKeyword.FILE);
				if ( imageFileExpression == null ) {
					imageFileExpression = getFacet(IKeyword.NAME);
				}
				if ( imageFileExpression == null ) { throw new GamaRuntimeException(
					"Image file not defined"); }
				setFacet(IKeyword.FILE, imageFileExpression);
				if ( imageFileExpression.isConst() ) {
					constantImage = Cast.asString(scope, imageFileExpression.value(scope));
					currentImage = constantImage;
					try {
						getImage(constantImage);
					} catch (final Exception ex) {
						constantImage = null;
						throw new GamaRuntimeException(ex);
					}
				}
			}
		}
	}

	public void buildGisLayer(final IScope scope) throws GamaRuntimeException {
		String fileName =
			getFacet(IKeyword.GIS) != null ? Cast.asString(scope,
				getFacet(IKeyword.GIS).value(scope)) : name;
		String shapeFile =
			scope.getSimulationScope().getModel().getRelativeFilePath(fileName, true);
		Set<Geometry> layer = new HashSet<Geometry>();
		File shpFile = new File(shapeFile);
		ShapefileDataStore store;
		String type = "";

		try {
			store = new ShapefileDataStore(shpFile.toURI().toURL());

			String name = store.getTypeNames()[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> source = store.getFeatureSource(name);
			FeatureCollection<SimpleFeatureType, SimpleFeature> featureShp = source.getFeatures();
			CoordinateReferenceSystem crs = featureShp.getSchema().getCoordinateReferenceSystem();
			MathTransform transformCRS = null;

			if ( crs != null ) {
				try {
					transformCRS = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs, true);
				} catch (FactoryException e) {
					e.printStackTrace();
				}
			}

			FeatureIterator<SimpleFeature> it3 = featureShp.features();
			while (it3.hasNext()) {
				SimpleFeature fact = it3.next();
				Geometry geom = (Geometry) fact.getDefaultGeometry();
				if ( transformCRS != null ) {
					geom = JTS.transform(geom, transformCRS);
				}

				geom = GisUtils.fromGISToAbsolute(geom);
				layer.add(geom);
				type = geom.getGeometryType();
			}

			it3.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		GamaColor c = null;
		IExpression colorExpr = getFacet(IKeyword.COLOR);
		if ( colorExpr != null ) {
			c = Cast.asColor(scope, getFacet(IKeyword.COLOR).value(scope));
		}
		gisLayer = new GisLayer(layer, c, type);
	}

	@Override
	public void dispose() {
		super.dispose();
		gisLayer = null;
	}

	public static class GisLayer {

		private Set<Geometry> objects;
		private String type;
		private Color color = Color.black;

		public GisLayer(final Set<Geometry> objects, final Color color, final String type) {
			super();
			this.objects = objects;
			if ( color != null ) {
				this.color = color;
			}
			this.type = type;
		}

		public Set<Geometry> getObjects() {
			return objects;
		}

		public void setObjects(final Set<Geometry> objects) {
			this.objects = objects;
		}

		public void dipose() {
			objects.clear();
			objects = null;
			type = null;
		}

		public String getType() {
			return type;
		}

		public void setType(final String type) {
			this.type = type;
		}

		public Color getColor() {
			return color;
		}
	}

	@Override
	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		super.compute(scope, cycle);
		if ( gisLayer == null ) {
			currentImage =
				constantImage != null ? constantImage : Cast.asString(scope,
					imageFileExpression.value(scope));
		}
	}

	/**
	 * @throws GamlException
	 * @throws GamaRuntimeException
	 * @param newValue
	 */
	public void setGisLayerName(final String newValue) throws GamaRuntimeException, GamlException {
		setName(newValue);
		IScope scope = GAMA.obtainNewScope();
		if ( scope == null ) { throw new GamaRuntimeException("No simulation running"); }
		try {
			buildGisLayer(scope);
		} finally {
			GAMA.releaseScope(scope);
		}

	}

	/**
	 * @param newValue
	 */
	public void setImageFileName(final String newValue) {
		constantImage = newValue;
	}

}
