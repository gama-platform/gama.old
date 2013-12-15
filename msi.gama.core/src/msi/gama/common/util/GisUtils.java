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

import java.util.Map;
import msi.gama.common.GamaPreferences;
import msi.gama.util.file.GamaGisFile;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.*;

public class GisUtils {

	private GeometryCoordinateSequenceTransformer transformer, inverseTransformer;
	CoordinateFilter gisToAbsoluteTranslation, absoluteToGisTranslation;
	private CoordinateReferenceSystem initialCRS;
	private static CoordinateReferenceSystem targetCRS;
	public static CoordinateReferenceSystem saveCRS;
	Envelope projectedEnv;

	private GisUtils() {}

	private GisUtils(final CoordinateReferenceSystem crs, final Envelope env) {
		initialCRS = crs;
		if ( env != null ) {
			if ( CRS.getProjectedCRS(initialCRS) == null ) {
				createTransformation(computeProjection(env.centre().x, env.centre().y));
			}
			// We project the envelope and we use it for initializing the translations
			projectedEnv = transform(env);
			createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY());
		}
	}

	public static GisUtils fromEPSG(final Integer code, final Envelope env) {
		GuiUtils.debug("GisUtils.fromEPSG int code : " + code);
		return fromEPSG(code, true, env);
	}

	public static GisUtils fromEPSG(final Integer code, final Boolean longitudeFirst, final Envelope env) {
		GuiUtils.debug("GisUtils.fromEPSG int code with longitude first : " + code);
		try {
			if ( code == GamaGisFile.ALREADY_PROJECTED_CODE ) { return new GisUtils(getTargetCRS(), env); }
			return fromCRS(CRS.decode("EPSG:" + code, longitudeFirst), env);
		} catch (Exception e) {
			GuiUtils.debug("" + code + " cannot be decoded as an existing EPSG code. Falling back to default value");
			return fromCRS(null, env);
		}
	}

	public static GisUtils fromParams(final Map<String, Object> params, final Envelope env) {
		GuiUtils.debug("GisUtils.fromParams :" + params);
		Object srid = params.get("srid");
		Object crs = params.get("crs");
		Boolean longitudeFirst = params.containsKey("longitudeFirst") && (Boolean) params.get("longitudeFirst");
		if ( crs instanceof String ) { return GisUtils.fromWKT((String) crs, env); }
		if ( srid instanceof String ) { return GisUtils.fromEPSG((String) srid, longitudeFirst, env); }
		return fromCRS(null, env);
	}

	public static GisUtils fromCRS(final CoordinateReferenceSystem crs, final Envelope env) {
		GuiUtils.debug("GisUtils.fromCRS : " + crs);
		if ( crs == null ) {
			if ( !GamaPreferences.LIB_PROJECTED.getValue() ) {
				return fromEPSG(GamaPreferences.LIB_INITIAL_CRS.getValue(), env);
			} else {
				return new GisUtils(getTargetCRS(), env);
			}
		}
		return new GisUtils(crs, env);
	}

	public static GisUtils fromEPSG(final String srid, final Boolean longitudeFirst, final Envelope env) {
		GuiUtils.debug("GisUtils.fromEPSG string code longitude first : " + srid);
		try {
			return fromEPSG(Integer.decode(srid), longitudeFirst, env);
		} catch (NumberFormatException e) {
			GuiUtils.debug("" + srid + " cannot be decoded as an EPSG code. Falling back to default value");
			return fromCRS(null, env);
		}
	}

	public static GisUtils fromEPSG(final String srid, final Envelope env) {
		GuiUtils.debug("GisUtils.fromEPSG string code : " + srid);
		return fromEPSG(srid, true, env);
	}

	public static GisUtils fromEnvelope(final Envelope env) {
		GuiUtils.debug("GisUtils.fromEnvelope with WGSS84 ");
		return fromCRS(DefaultGeographicCRS.WGS84, env);
	}

	public static GisUtils fromWKT(final String crs, final Envelope env) {
		GuiUtils.debug("GisUtils.fromWKT : " + crs);
		try {
			return fromCRS(CRS.parseWKT(crs), env);
		} catch (FactoryException e) {
			GuiUtils.debug("" + crs + " cannot be decoded as a WKT defintion. Falling back to default value");
			return fromCRS(null, env);
		}
	}

	public static void forgetTargetCRS() {
		setTargetCRS(null);
	}

	private static void computeTargetCRS(final double longitude, final double latitude) {
		// If we already know in which CRS we project the data in GAMA, no need to recompute it. This information is
		// normally wiped when an experiment is disposed
		if ( getTargetCRS() != null ) { return; }
		try {
			if ( !GamaPreferences.LIB_TARGETED.getValue() ) {
				computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true);
			} else {
				int index = (int) (0.5 + (longitude + 186.0) / 6);
				boolean north = latitude > 0;
				String newCode = "EPSG:" + 32600 + index + (north ? 0 : 100);
				GuiUtils.debug("GisUtils.computeTargetCRS targetCRS is " + newCode);
				setTargetCRS(CRS.decode(newCode));
			}
		} catch (Exception e) {
			GuiUtils.debug("An error prevented GAMA from computing a correct Coordinate System: " + e);
		}
	}

	public static void computeDefaultCRS(final int code, final boolean target) {
		String type = target ? "target CRS" : "output CRS";
		String def = "EPSG:" + (target ? 32648 : 4326);
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode("EPSG:" + code);
			System.out.println(type + " successfully changed to EPSG:" + code);
		} catch (Exception e) {
			GuiUtils.debug("Error in computing the " + type + " for code " + code + ". Falling back to " + def);
		} finally {
			if ( crs == null ) {
				try {
					crs = CRS.decode(def);
				} catch (Exception e) {}
			}
		}
		if ( target ) {
			setTargetCRS(crs);
		} else {
			saveCRS = crs;
		}
	}

	private void createTranslations(final double minX, final double height, final double minY) {
		gisToAbsoluteTranslation = new CoordinateFilter() {

			@Override
			public void filter(final Coordinate coord) {
				coord.x -= minX;
				coord.y = -coord.y + height + minY;
			}
		};
		absoluteToGisTranslation = new CoordinateFilter() {

			@Override
			public void filter(final Coordinate coord) {
				coord.x += minX;
				coord.y = -coord.y + height + minY;
			}
		};
	}

	private void createTransformation(final MathTransform t) {
		if ( t != null ) {
			transformer = new GeometryCoordinateSequenceTransformer();
			// TODO see ConcatenatedTransformDirect2D
			transformer.setMathTransform(t);
			try {
				inverseTransformer = new GeometryCoordinateSequenceTransformer();
				inverseTransformer.setMathTransform(t.inverse());
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
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
		if ( gisToAbsoluteTranslation != null ) {
			geom.apply(gisToAbsoluteTranslation);
		}
		return geom;
	}

	private Envelope transform(final Envelope g) {
		if ( transformer == null ) { return g; }
		return transform(JTS.toGeometry(g)).getEnvelopeInternal();
	}

	public Geometry inverseTransform(final Geometry g) {
		Geometry geom = GeometryUtils.factory.createGeometry(g);
		if ( absoluteToGisTranslation != null ) {
			geom.apply(absoluteToGisTranslation);
		}
		if ( inverseTransformer != null ) {
			try {
				geom = inverseTransformer.transform(geom);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		return geom;
	}

	private MathTransform computeProjection(final double longitude, final double latitude) {
		MathTransform crsTransformation = null;
		computeTargetCRS(longitude, latitude);
		try {
			crsTransformation = CRS.findMathTransform(initialCRS, getTargetCRS());
		} catch (FactoryException e) {
			e.printStackTrace();
			return null;
		}
		return crsTransformation;
	}

	public CoordinateReferenceSystem getInitialCRS() {
		return initialCRS;
	}

	public static GisUtils forSavingWithEPSG(final Integer epsgCode) {
		CoordinateReferenceSystem forcedSaveCRS = null;
		if ( epsgCode != null ) {
			try {
				forcedSaveCRS = CRS.decode("EPSG:" + epsgCode);
			} catch (Exception e) {
				System.out.println("Impossible to save in the CRS EPSG:" + epsgCode + ". Falling back to the default.");
				forcedSaveCRS = saveCRS;
			}
		} else {
			forcedSaveCRS = saveCRS;
		}
		GisUtils gis = new GisUtils();
		gis.initialCRS = forcedSaveCRS;
		try {
			gis.createTransformation(CRS.findMathTransform(gis.initialCRS, getTargetCRS()));
		} catch (FactoryException e) {
			e.printStackTrace();
			return null;
		}
		return gis;
	}

	public static GisUtils forSavingWithWKT(final String wkt) {
		CoordinateReferenceSystem forcedSaveCRS = null;
		if ( wkt != null ) {
			try {
				forcedSaveCRS = CRS.parseWKT(wkt);
			} catch (Exception e) {
				System.out.println("Impossible to save in the CRS WKT:" + wkt + ". Falling back to the default.");
				forcedSaveCRS = saveCRS;
			}
		} else {
			forcedSaveCRS = saveCRS;
		}
		GisUtils gis = new GisUtils();
		gis.initialCRS = forcedSaveCRS;
		try {
			gis.createTransformation(CRS.findMathTransform(gis.initialCRS, getTargetCRS()));
		} catch (FactoryException e) {
			e.printStackTrace();
			return null;
		}
		return gis;
	}

	public static GisUtils forSavingWithEPSG(final String srid) {
		CoordinateReferenceSystem forcedSaveCRS = null;
		if ( srid != null ) {
			try {
				forcedSaveCRS = CRS.decode("EPSG:" + Integer.decode(srid));
			} catch (Exception e) {
				System.out.println("Impossible to save in the CRS EPSG:" + srid + ". Falling back to the default.");
				forcedSaveCRS = saveCRS;
			}
		} else {
			forcedSaveCRS = saveCRS;
		}
		GisUtils gis = new GisUtils();
		gis.initialCRS = forcedSaveCRS;
		try {
			gis.createTransformation(CRS.findMathTransform(gis.initialCRS, getTargetCRS()));
		} catch (FactoryException e) {
			e.printStackTrace();
			return null;
		}
		return gis;
	}

	public Envelope getProjectedEnvelope() {
		return projectedEnv;
	}

	public static void forgetSaveCRS() {
		saveCRS = getTargetCRS();
	}

	private static CoordinateReferenceSystem getTargetCRS() {
		if ( targetCRS == null ) {
			computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true);
		}
		return targetCRS;
	}

	private static void setTargetCRS(final CoordinateReferenceSystem targetCRS) {
		GisUtils.targetCRS = targetCRS;
	}

}
