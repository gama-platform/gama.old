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
package msi.gama.common.util;

import java.io.*;
import msi.gama.common.GamaPreferences;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.*;

public class GisUtils {

	public GisUtils() {}

	static final boolean DEBUG = false; // Change DEBUG = false for release version

	Envelope translationEnvelope;
	private GeometryCoordinateSequenceTransformer transformer;
	private GeometryCoordinateSequenceTransformer inverseTransformer;
	private CoordinateReferenceSystem initialCRS;

	public void init(final Envelope bounds) {
		translationEnvelope = new ReferencedEnvelope(bounds, initialCRS);
	}

	CoordinateFilter gisToAbsolute = new CoordinateFilter() {

		@Override
		public void filter(final Coordinate coord) {
			if ( translationEnvelope == null ) { return; }
			coord.x -= translationEnvelope.getMinX();
			coord.y = -coord.y + translationEnvelope.getHeight() + translationEnvelope.getMinY();
		}
	};

	CoordinateFilter absoluteToGis = new CoordinateFilter() {

		@Override
		public void filter(final Coordinate coord) {
			if ( translationEnvelope == null ) { return; }
			coord.x += translationEnvelope.getMinX();
			coord.y = -coord.y + translationEnvelope.getHeight() + translationEnvelope.getMinY();
		}

	};

	public void setTransformCRS(final MathTransform t) {
		if ( t != null ) {
			transformer = new GeometryCoordinateSequenceTransformer();
			transformer.setMathTransform(t);
			try {
				inverseTransformer = new GeometryCoordinateSequenceTransformer();
				inverseTransformer.setMathTransform(t.inverse());
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
		} else {
			transformer = null;
			inverseTransformer = null;
		}
	}

	public Geometry transform(final Geometry g) {
		Geometry geom = GeometryUtils.factory.createGeometry(g);
		if ( transformer != null ) {
			try {
				geom = transformer.transform(g);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		geom.apply(gisToAbsolute);
		return geom;
	}

	public Envelope transform(final Envelope g) {
		if ( transformer == null ) { return g; }
		return transform(JTS.toGeometry(g)).getEnvelopeInternal();
	}

	public Geometry inverseTransform(final Geometry g) {
		Geometry geom = GeometryUtils.factory.createGeometry(g);
		geom.apply(absoluteToGis);
		if ( inverseTransformer != null ) {
			try {
				geom = inverseTransformer.transform(geom);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		return geom;
	}

	public void setInitialCRS(final File shpf, final double longitude, final double latitude) throws IOException {
		ShpFiles shpFiles = new ShpFiles(shpf);
		PrjFileReader prjreader = new PrjFileReader(shpFiles);
		try {
			setInitialCRS(CRS.parseWKT(prjreader.getCoodinateSystem().toWKT()), longitude, latitude);
		} catch (FactoryException e2) {
			e2.printStackTrace();
			initialCRS = null;
		} finally {
			prjreader.close();
		}
	}

	public void setInitialCRS(final double longitude, final double latitude) {
		initialCRS = DefaultGeographicCRS.WGS84;
		MathTransform transfCRS = computeProjection(longitude, latitude);
		setTransformCRS(transfCRS);
	}

	public void setInitialCRS(final CoordinateReferenceSystem crsI, final double longitude, final double latitude) {
		MathTransform crsTransformation = null;
		initialCRS = crsI;
		ProjectedCRS projectd = CRS.getProjectedCRS(initialCRS);
		if ( projectd == null ) {
			crsTransformation = computeProjection(longitude, latitude);
		} else {
			System.out.println("The GIS data is projected using " + projectd.toWKT());
		}
		setTransformCRS(crsTransformation);
	}

	public void setInitialCRS(final String coordinateRS, final double longitude, final double latitude) {
		try {
			setInitialCRS(CRS.parseWKT(coordinateRS), longitude, latitude);
		} catch (FactoryException e2) {
			initialCRS = null;
		}
	}

	public void setInitialCRS(final String srid, final boolean longitudeFirst, final double longitude,
		final double latitude) {
		setInitialCRS(Integer.decode(srid), longitudeFirst, longitude, latitude);
	}

	public void setInitialCRS(final Integer epsgCode, final boolean longitudeFirst, final double longitude,
		final double latitude) {
		try {
			setInitialCRS(CRS.decode("EPSG:" + epsgCode, longitudeFirst), longitude, latitude);
		} catch (FactoryException e2) {
			initialCRS = null;
		}
	}

	private MathTransform computeProjection(final double longitude, final double latitude) {
		MathTransform crsTransformation = null;
		try {
			Integer pref;
			if ( !GamaPreferences.LIB_TARGETED.getValue() ) {
				pref = GamaPreferences.LIB_TARGET_CRS.getValue();
			} else {
				int index = (int) (0.5 + (longitude + 186.0) / 6);
				boolean north = latitude > 0;
				pref = 32600 + index + (north ? 0 : 100);
			}
			CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:" + pref);
			crsTransformation = CRS.findMathTransform(initialCRS, targetCRS);
			System.out.println("Decoded CRS : " + targetCRS);
		} catch (NoSuchAuthorityCodeException e) {
			System.out.println("An error prevented GIS data to be projected: " + e);
		} catch (FactoryException e) {
			System.out.println("An error prevented GIS data to be projected: " + e);
		}
		return crsTransformation;
	}

	public CoordinateReferenceSystem getCrs() {
		return initialCRS;
	}

}
