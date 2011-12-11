/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import msi.gama.environment.GisUtil;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.*;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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
@symbol(name = ISymbol.IMAGE, kind = ISymbolKind.LAYER)
@inside(symbols = ISymbol.DISPLAY)
@facets({ @facet(name = ISymbol.FILE, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.GIS, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.COLOR, type = IType.COLOR_STR, optional = true) })
public class ImageDisplayLayer extends AbstractDisplayLayer {

	private static ImageCache cachedImages = new ImageCache();

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
			new File(output.getOwnScope().getSimulationScope().getModel()
				.getRelativeFilePath(fileName, true));
		image = ImageIO.read(f);
		cachedImages.add(fileName, image);
		return cachedImages.get(fileName);
	}

	public GisLayer getGisLayer() {
		return gisLayer;
	}

	@Override
	public short getType() {
		if ( getFacet(ISymbol.GIS) == null ) { return IDisplay.IMAGE; }
		return IDisplay.GIS;
	}

	public String getImageFileName() {
		return currentImage;
	}

	@Override
	public void prepare(final LayerDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		super.prepare(out, sim);
		if ( getFacet(ISymbol.GIS) != null ) {
			buildGisLayer(sim);
		} else {
			if ( constantImage == null ) {
				// Redefined to allow replacing the "name" attribute by "file"
				IExpression tag = getFacet(ISymbol.NAME);
				if ( tag == null ) {
					tag = getFacet(ISymbol.FILE);
				}
				if ( tag == null ) { throw new GamaRuntimeException("Missing properties " +
					ISymbol.NAME + " and " + ISymbol.FILE); }
				if ( tag.isConst() ) {
					setName(Cast.asString(tag.value(sim)));
				} else {
					setName(tag.toGaml());
				}
				imageFileExpression = getFacet(ISymbol.FILE);
				if ( imageFileExpression == null ) {
					imageFileExpression = getFacet(ISymbol.NAME);
				}
				if ( imageFileExpression == null ) { throw new GamaRuntimeException(
					"Image file not defined"); }
				setFacet(ISymbol.FILE, imageFileExpression);
				if ( imageFileExpression.isConst() ) {
					constantImage = Cast.asString(imageFileExpression.value(sim));
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
			getFacet(ISymbol.GIS) != null ? Cast
				.asString(getFacet(ISymbol.GIS).value(scope)) : name;
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

				geom = GisUtil.fromGISToAbsolute(geom);
				layer.add(geom);
				type = geom.getGeometryType();
			}

			it3.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		GamaColor c = null;
		IExpression colorExpr = getFacet(ISymbol.COLOR);
		if (colorExpr != null) {
			c = Cast.asColor(scope, getFacet(ISymbol.COLOR).value(scope)); 
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
			if (color != null) { this.color = color; }
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
	public void compute(final IScope sim, final long cycle) throws GamaRuntimeException {
		super.compute(sim, cycle);
		if ( gisLayer == null ) {
			currentImage =
				constantImage != null ? constantImage : Cast.asString(imageFileExpression.value(sim));
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
