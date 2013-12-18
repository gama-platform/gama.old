/**
 * Created by drogoul, 17 déc. 2013
 * 
 */
package msi.gama.metamodel.topology.projection;

import java.util.Map;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.util.file.GamaGisFile;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Class ProjectionFactory.
 * 
 * @author drogoul
 * @since 17 déc. 2013
 * 
 */
public class ProjectionFactory {

	// TODO Build a cache of CRS using either int codes or String codes

	private static IProjection world;
	public static CoordinateReferenceSystem targetCRS;

	public static void reset() {
		world = null;
		targetCRS = null;
	}

	static void computeTargetCRS(final double longitude, final double latitude) {
		// If we already know in which CRS we project the data in GAMA, no need to recompute it. This information is
		// normally wiped when an experiment is disposed
		if ( targetCRS != null ) { return; }
		try {
			if ( !GamaPreferences.LIB_TARGETED.getValue() ) {
				targetCRS = computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true);
			} else {
				int index = (int) (0.5 + (longitude + 186.0) / 6);
				boolean north = latitude > 0;
				String newCode = "EPSG:" + 32600 + index + (north ? 0 : 100);
				GuiUtils.debug("GisUtils.computeTargetCRS targetCRS is " + newCode);
				targetCRS = CRS.decode(newCode);
			}
		} catch (Exception e) {
			GuiUtils.debug("An error prevented GAMA from computing a correct Coordinate System: " + e);
		}
	}

	static CoordinateReferenceSystem getTargetCRS() {
		if ( targetCRS == null ) { return computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true); }
		return targetCRS;
	}

	static CoordinateReferenceSystem getSaveCRS() {
		if ( GamaPreferences.LIB_USE_DEFAULT.getValue() ) { return getTargetCRS(); }
		return computeDefaultCRS(GamaPreferences.LIB_OUTPUT_CRS.getValue(), false);
	}

	public static CoordinateReferenceSystem computeCRS(final int code) {
		return computeCRS(code, true);
	}

	public static CoordinateReferenceSystem computeCRS(final int code, final boolean longitudeFirst) {
		if ( code == GamaGisFile.ALREADY_PROJECTED_CODE ) { return getTargetCRS(); }
		return computeCRS("EPSG:" + code, longitudeFirst);
	}

	public static CoordinateReferenceSystem computeCRS(final String code) {
		return computeCRS(code, true);
	}

	public static CoordinateReferenceSystem computeCRS(final String code, final boolean longitudeFirst) {
		try {
			CoordinateReferenceSystem crs = CRS.decode(code, longitudeFirst);
			return crs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static CoordinateReferenceSystem computeDefaultCRS(final int code, final boolean target) {
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
		return crs;
	}

	public static IProjection fromEPSG(final Integer code, final Envelope env) {
		GuiUtils.debug("GisUtils.fromEPSG int code : " + code);
		return fromEPSG(code, true, env);
	}

	public static IProjection fromEPSG(final Integer code, final Boolean longitudeFirst, final Envelope env) {
		GuiUtils.debug("GisUtils.fromEPSG int code with longitude first : " + code);
		try {
			return fromCRS(computeCRS(code, longitudeFirst), env);
		} catch (Exception e) {
			GuiUtils.debug("" + code + " cannot be decoded as an existing EPSG code. Falling back to default value");
			return fromCRS(getDefaultInitialCRS(), env);
		}
	}

	public static IProjection fromParams(final Map<String, Object> params, final Envelope env) {
		GuiUtils.debug("GisUtils.fromParams :" + params);
		Object srid = params.get("srid");
		Object crs = params.get("crs");
		// Boolean longitudeFirst = params.containsKey("longitudeFirst") && (Boolean) params.get("longitudeFirst");
		Boolean longitudeFirst = params.containsKey("longitudeFirst") ? (Boolean) params.get("longitudeFirst") : true;
		if ( crs instanceof String ) { return fromWKT((String) crs, env); }
		if ( srid instanceof String ) { return fromEPSG((String) srid, longitudeFirst, env); }
		return fromCRS(getDefaultInitialCRS(), env);
	}

	public static IProjection fromCRS(final CoordinateReferenceSystem c, final Envelope env) {
		CoordinateReferenceSystem crs = c;
		if ( world == null ) {
			if ( env != null ) {
				computeTargetCRS(env.centre().x, env.centre().y);
			}
			world = new WorldProjection(crs, env);
			return world;
		} else {
			return new Projection(world, crs, env);
		}
	}

	public static IProjection fromEPSG(final String srid, final Boolean longitudeFirst, final Envelope env) {
		try {
			return fromEPSG(Integer.decode(srid), longitudeFirst, env);
		} catch (NumberFormatException e) {
			GuiUtils.debug("" + srid + " cannot be decoded as an EPSG code. Falling back to default value");
			return fromCRS(null, env);
		}
	}

	public static IProjection fromEPSG(final String srid, final Envelope env) {
		return fromEPSG(srid, true, env);
	}

	public static IProjection fromEnvelope(final Envelope env) {
		return fromCRS(DefaultGeographicCRS.WGS84, env);
	}

	public static IProjection fromWKT(final String crs, final Envelope env) {
		try {
			return fromCRS(CRS.parseWKT(crs), env);
		} catch (FactoryException e) {
			GuiUtils.debug("" + crs + " cannot be decoded as a WKT defintion. Falling back to the default");
			return fromCRS(getDefaultInitialCRS(), env);
		}
	}

	public static IProjection forSavingWithEPSG(final Integer epsgCode) {
		CoordinateReferenceSystem forcedSaveCRS = null;
		if ( epsgCode != null ) {
			try {
				forcedSaveCRS = computeCRS(epsgCode);
			} catch (Exception e) {
				System.out.println("Impossible to save in the CRS EPSG:" + epsgCode + ". Falling back to the default.");
				forcedSaveCRS = getSaveCRS();
			}
		} else {
			forcedSaveCRS = getSaveCRS();
		}
		Projection gis = new Projection();
		gis.initialCRS = forcedSaveCRS;
		gis.computeProjection();
		return gis;
	}

	public static IProjection forSavingWithWKT(final String wkt) {
		CoordinateReferenceSystem forcedSaveCRS = null;
		if ( wkt != null ) {
			try {
				forcedSaveCRS = CRS.parseWKT(wkt);
			} catch (Exception e) {
				System.out.println("Impossible to save in the CRS WKT:" + wkt + ". Falling back to the default.");
				forcedSaveCRS = getSaveCRS();
			}
		} else {
			forcedSaveCRS = getSaveCRS();
		}
		Projection gis = new Projection();
		gis.initialCRS = forcedSaveCRS;
		gis.computeProjection();
		return gis;
	}

	public static IProjection forSavingWithEPSG(final String srid) {
		CoordinateReferenceSystem forcedSaveCRS = null;
		if ( srid != null ) {
			try {
				forcedSaveCRS = CRS.decode("EPSG:" + Integer.decode(srid));
			} catch (Exception e) {
				System.out.println("Impossible to save in the CRS EPSG:" + srid + ". Falling back to the default.");
				forcedSaveCRS = getSaveCRS();
			}
		} else {
			forcedSaveCRS = getSaveCRS();
		}
		Projection gis = new Projection();
		gis.initialCRS = forcedSaveCRS;
		gis.computeProjection();
		return gis;
	}

	public static IProjection forSavingWithEPSG(final String srid, final boolean longitudeFirst) {
		CoordinateReferenceSystem forcedSaveCRS = null;
		if ( srid != null ) {
			try {
				forcedSaveCRS = CRS.decode("EPSG:" + Integer.decode(srid), longitudeFirst);
			} catch (Exception e) {
				System.out.println("Impossible to save in the CRS EPSG:" + srid + ". Falling back to the default.");
				forcedSaveCRS = getSaveCRS();
			}
		} else {
			forcedSaveCRS = getSaveCRS();
		}
		Projection gis = new Projection();
		gis.initialCRS = forcedSaveCRS;
		gis.computeProjection();
		return gis;
	}

	public static CoordinateReferenceSystem getDefaultInitialCRS() {
		if ( !GamaPreferences.LIB_PROJECTED.getValue() ) {
			return computeCRS(GamaPreferences.LIB_INITIAL_CRS.getValue());
		} else {
			return getTargetCRS();
		}
	}
}
