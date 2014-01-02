/**
 * Created by drogoul, 17 déc. 2013
 * 
 */
package msi.gama.metamodel.topology.projection;

import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.util.file.GamaGisFile;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Class ProjectionFactory.
 * 
 * @author drogoul
 * @since 17 déc. 2013
 * 
 */
public class ProjectionFactory {

	private static final String EPSGPrefix = "EPSG:";
	private static final String defaultTargetCRS = String.valueOf(GamaPreferences.LIB_TARGET_CRS.getInitialValue(null));
	private static final String defaultSaveCRS = String.valueOf(GamaPreferences.LIB_OUTPUT_CRS.getInitialValue(null));
	private static Map<String, CoordinateReferenceSystem> CRSCache = new HashMap();

	private IProjection world;
	public CoordinateReferenceSystem targetCRS;

	void computeTargetCRS(final CoordinateReferenceSystem crs,final double longitude, final double latitude) {
		// If we already know in which CRS we project the data in GAMA, no need to recompute it. This information is
		// normally wiped when an experiment is disposed
		if ( targetCRS != null ) { return; }
		try {
			if ( !GamaPreferences.LIB_TARGETED.getValue() ) {
				targetCRS = computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true);
			} else { 
				if (crs != null && crs instanceof ProjectedCRS) { // Temporary fix of issue 766... a better solution can be found 
                    targetCRS = crs;
				} else {
					int index = (int) (0.5 + (longitude + 186.0) / 6);
					boolean north = latitude > 0;
					String newCode = EPSGPrefix + (32600 + index + (north ? 0 : 100));
					targetCRS = getCRS(newCode);
				}
			}
		} catch (Exception e) {
			GuiUtils.debug("An error prevented GAMA from computing a correct Coordinate System: " + e);
		}
	}

	CoordinateReferenceSystem getTargetCRS() {
		if ( targetCRS == null ) { return computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true); }
		return targetCRS;
	}

	CoordinateReferenceSystem getSaveCRS() {
		if ( GamaPreferences.LIB_USE_DEFAULT.getValue() ) { return getTargetCRS(); }
		return computeDefaultCRS(GamaPreferences.LIB_OUTPUT_CRS.getValue(), false);
	}

	public CoordinateReferenceSystem getCRS(final int code) {
		return getCRS(code, true);
	}

	public CoordinateReferenceSystem getCRS(final int code, final boolean longitudeFirst) {
		if ( code == GamaGisFile.ALREADY_PROJECTED_CODE ) { return getTargetCRS(); }
		return getCRS(EPSGPrefix + code, longitudeFirst);
	}

	public CoordinateReferenceSystem getCRS(final String code) {
		return getCRS(code, true);
	}

	public CoordinateReferenceSystem getCRS(final String code, final boolean longitudeFirst) {
		try {
			CoordinateReferenceSystem crs = CRSCache.get(code);
			if ( crs == null ) {
				if ( code.startsWith(EPSGPrefix) ) {
					crs = CRS.decode(code, longitudeFirst);
				} else if ( code.startsWith("PROJCS") || code.startsWith("GEOGCS") || code.startsWith("COMPD_CS") ) {
					crs = CRS.parseWKT(code);
				} else if ( Character.isDigit(code.charAt(0)) ) {
					crs = CRS.decode(EPSGPrefix + code, longitudeFirst);
				}
				CRSCache.put(code, crs);
			}
			return crs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public CoordinateReferenceSystem computeDefaultCRS(final int code, final boolean target) {
		CoordinateReferenceSystem crs = getCRS(code);
		if ( crs == null ) {
			crs = getCRS(EPSGPrefix + (target ? defaultTargetCRS : defaultSaveCRS));
		}
		return crs;
	}

	public IProjection fromParams(final Map<String, Object> params, final Envelope env) {
		Boolean lonFirst = params.containsKey("longitudeFirst") ? (Boolean) params.get("longitudeFirst") : true;
		Object crs = params.get("crs");
		if ( crs instanceof String ) { return fromCRS(getCRS((String) crs, lonFirst), env); }
		Object srid = params.get("srid");
		if ( srid instanceof String ) { return fromCRS(getCRS((String) srid, lonFirst), env); }
		return fromCRS(getDefaultInitialCRS(), env);
	}

	public IProjection fromCRS(final CoordinateReferenceSystem crs, final Envelope env) {
		if ( world == null ) {
			if ( env != null ) {
				computeTargetCRS(crs, env.centre().x, env.centre().y);
			}
			world = new WorldProjection(crs, env, this);
			return world;
		} else {
			return new Projection(world, crs, env, this);
		}
	}

	public IProjection forSavingWith(final Integer epsgCode) {
		return forSavingWith(epsgCode, true);
	}

	public IProjection forSavingWith(final Integer epsgCode, final boolean lonFirst) {
		return forSavingWith(EPSGPrefix + epsgCode, lonFirst);
	}

	public IProjection forSavingWith(final String code) {
		return forSavingWith(code, true);
	}

	public IProjection forSavingWith(final String code, final boolean lonFirst) {
		CoordinateReferenceSystem crs = getCRS(code, lonFirst);
		if ( crs == null ) {
			crs = getSaveCRS();
		}
		Projection gis = new Projection(world, this);
		gis.initialCRS = crs;
		gis.computeProjection();
		return gis;
	}

	public CoordinateReferenceSystem getDefaultInitialCRS() {
		if ( !GamaPreferences.LIB_PROJECTED.getValue() ) {
			return getCRS(GamaPreferences.LIB_INITIAL_CRS.getValue());
		} else {
			return getTargetCRS();
		}
	}
}
