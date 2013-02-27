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
package msi.gama.common.util;

/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

import java.io.*;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.*;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.feature.*;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.*;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.*;

public class GisUtils {
	static final boolean DEBUG = false; // Change DEBUG = false for release version
	private static SimpleFeature gisReader;

	public static void setCurrentGisReader(final SimpleFeature fact) {
		gisReader = fact;
	}

	public static SimpleFeature getCurrentGisReader() {
		return gisReader;
	}

	public static double XMinComp;
	public static double YMinComp;
	public static MathTransform transformCRS;
	public static double absEnvHeight;
	private static final double margin = 0;
	private static CoordinateReferenceSystem crsInit;
	
	public static void init(final double absHeight, final double absEnvWidth, final double xMin,
		final double yMin, final double xMax, final double yMax, final MathTransform transform) {
		absEnvHeight = absHeight;
		XMinComp = xMin - margin * (xMax - xMin);
		YMinComp = yMin - margin * (yMax - yMin);
		transformCRS = transform;
	}

	public static Geometry fromGISToAbsolute(final Geometry geom) {
		Geometry geom2 = GeometryUtils.factory.createGeometry(geom);
		for ( Coordinate coord : geom2.getCoordinates() ) {
			coord.x = coord.x - XMinComp;
			coord.y = absEnvHeight - (coord.y - YMinComp);
		}
		return geom2;
	}

	public static Geometry fromAbsoluteToGis(final Geometry geom) {
		Geometry geom2 = GeometryUtils.factory.createGeometry(geom);
		for ( Coordinate coord : geom2.getCoordinates() ) {
			coord.x += XMinComp;
			coord.y = absEnvHeight - coord.y + YMinComp;
		}
		if ( transformCRS != null ) {
			try {
				geom2 = JTS.transform(geom2, transformCRS.inverse());
			} catch (MismatchedDimensionException e) {
				e.printStackTrace();
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		return geom2;

	}

	public static MathTransform getTransformCRS(final ShpFiles shpf, final double latitude,
		final double longitude) throws IOException {
		PrjFileReader prjreader = new PrjFileReader(shpf);
		MathTransform transfCRS = null;
		crsInit = null;
		try {
			crsInit = CRS.parseWKT(prjreader.getCoodinateSystem().toWKT());
			//begin ---------------------------------------------------------------------------------------------
			//Thai.truongminh@gmail.com 
			// 18-sep-2012: for create agen from:list
			// for tracing nly

			if (DEBUG)
				GuiUtils.informConsole("GisUtil.CRS="+crsInit.toString());
			//--------------------------------------------------------------------------------------------- end
		} catch (FactoryException e2) {
			e2.printStackTrace();
		}
		ProjectedCRS projectd = CRS.getProjectedCRS(crsInit);		
		if ( projectd == null ) {
			System.out.println("NOT PROJECTED");
			try {
				int index = (int) (0.5 + (latitude + 180.0) / 360 * 60);
				boolean north = longitude > 0;
				int wgs84utm = 32600 + index + (north ? 0 : 100);
				CoordinateReferenceSystem crs = CRS.decode("EPSG:" + wgs84utm);
				transfCRS = CRS.findMathTransform(crsInit, crs);
				System.out.println("decodedcrs : " + crs);
			} catch (NoSuchAuthorityCodeException e) {
				System.out.println("WARNING : STILL NOT PROJECTED");
			} catch (FactoryException e) {
				System.out.println("WARNING : STILL NOT PROJECTED");
			}
		} else {
			System.out.println(" IT IS ALREADY PROJECTED" + projectd.toWKT());
		}
		prjreader.close();
		return transfCRS;
	}
//Begin
//-----------------------------------------------------------------------------------
	public static MathTransform getTransformCRS(final String coordinateRS, final double latitude,
			final double longitude) throws IOException {
			MathTransform transfCRS = null;
			crsInit = null;
			try {
				crsInit = CRS.parseWKT(coordinateRS);
				if (DEBUG)
					GuiUtils.informConsole("GisUtil.getTransformCRS:"+crsInit.toString());
			} catch (FactoryException e2) {
				e2.printStackTrace();
			}
			ProjectedCRS projectd = CRS.getProjectedCRS(crsInit);		
			if ( projectd == null ) {
				System.out.println("NOT PROJECTED");
				try {
					int index = (int) (0.5 + (latitude + 180.0) / 360 * 60);
					boolean north = longitude > 0;
					int wgs84utm = 32600 + index + (north ? 0 : 100);
					CoordinateReferenceSystem crs = CRS.decode("EPSG:" + wgs84utm);
					transfCRS = CRS.findMathTransform(crsInit, crs);
					System.out.println("decodedcrs : " + crs);
				} catch (NoSuchAuthorityCodeException e) {
					System.out.println("WARNING : STILL NOT PROJECTED");
				} catch (FactoryException e) {
					System.out.println("WARNING : STILL NOT PROJECTED");
				}
			} else {
				System.out.println(" IT IS ALREADY PROJECTED" + projectd.toWKT());
			}
			return transfCRS;
		}

	public static MathTransform getTransformCRS(final String srid,final boolean longitudeFirst, final double latitude,
			final double longitude) throws IOException {
			MathTransform transfCRS = null;
			crsInit = null;
			try {
				crsInit = CRS.decode("EPSG:"+srid, longitudeFirst);
				transfCRS=getTransformCRS(crsInit.toWKT(),latitude,longitude);
				if (DEBUG) GuiUtils.informConsole("GisUtil.getTransformCRS:"+crsInit.toString());
			} catch (FactoryException e2) {
				e2.printStackTrace();
			}
			return transfCRS;
		}


//------------------------------------------------------------------------------------
//end	
	public static FeatureIterator<SimpleFeature> getFeatureIterator(final File file) {
		try {
			ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
			String name = store.getTypeNames()[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> source = store.getFeatureSource(name);
			FeatureCollection<SimpleFeatureType, SimpleFeature> featureShp = source.getFeatures();
			if ( store.getSchema().getCoordinateReferenceSystem() != null ) {
				ShpFiles shpf = new ShpFiles(file);
				double latitude = featureShp.getBounds().centre().x;
				double longitude = featureShp.getBounds().centre().y;
				transformCRS = getTransformCRS(shpf, latitude, longitude);
			}
			return featureShp.features();
		} catch (IOException e) {
			return null;
		}
	}

	public static CoordinateReferenceSystem getCrs() {
		return crsInit;
	}

	
	

}
