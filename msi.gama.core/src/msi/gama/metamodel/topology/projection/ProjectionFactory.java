/*********************************************************************************************
 * 
 * 
 * 'ProjectionFactory.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.projection;

import gnu.trove.map.hash.THashMap;
import java.util.Map;
import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaGisFile;
import org.geotools.referencing.CRS;
import org.opengis.referencing.*;
import org.opengis.referencing.crs.*;
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
	private static Map<String, CoordinateReferenceSystem> CRSCache = new THashMap<>();

	private IProjection world;
	public CoordinateReferenceSystem targetCRS;

	public void setWorldProjectionEnv(final Envelope3D env) {
		if ( world == null ) { return; }
		world = new WorldProjection(world.getInitialCRS(), env, this);
		//((WorldProjection) world).updateTranslations(env);
	}

	void computeTargetCRS(final CoordinateReferenceSystem crs, final double longitude, final double latitude) {
		// If we already know in which CRS we project the data in GAMA, no need to recompute it. This information is
		// normally wiped when an experiment is disposed
		if ( targetCRS != null ) { return; }
		try {
			if ( !GamaPreferences.LIB_TARGETED.getValue() ) {
				targetCRS = computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true);
			} else {
				if ( crs != null && crs instanceof ProjectedCRS ) { // Temporary fix of issue 766... a better solution
																	// can be found
					targetCRS = crs;
				} else {
					int index = (int) (0.5 + (longitude + 186.0) / 6);
					boolean north = latitude > 0;
					String newCode = EPSGPrefix + (32600 + index + (north ? 0 : 100));
					targetCRS = getCRS(newCode);
				}
			}
		} catch (GamaRuntimeException e) {
			e.addContext("The cause could be that you try to re-project already projected data (see Gama > Preferences... > External for turning the option to true)");
			throw e;
		}
	}

	public CoordinateReferenceSystem getTargetCRS() {
		if ( targetCRS == null ) {

			try {
				return computeDefaultCRS(GamaPreferences.LIB_TARGET_CRS.getValue(), true);

			} catch (GamaRuntimeException e) {
				e.addContext("The cause could be that you try to re-project already projected data (see Gama > Preferences... > External for turning the option to true)");
				throw e;
			}

		}
		return targetCRS;
	}

	public CoordinateReferenceSystem getSaveCRS() {
		if ( GamaPreferences.LIB_USE_DEFAULT.getValue() ) { return getWorld().getInitialCRS(); }
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
				if ( code.startsWith(EPSGPrefix) || code.startsWith("CRS:") ) {
					crs = CRS.decode(code, longitudeFirst);
				} else if ( code.startsWith("PROJCS") || code.startsWith("GEOGCS") || code.startsWith("COMPD_CS") ) {
					crs = CRS.parseWKT(code);
				} else if ( Character.isDigit(code.charAt(0)) ) {
					crs = CRS.decode(EPSGPrefix + code, longitudeFirst);
				}
				CRSCache.put(code, crs);
			}
			return crs;
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("The EPSG code " + code +
				" cannot be found. GAMA may be unable to load or save any GIS data");
		} catch (FactoryException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("An exception occured in trying to decode GIS data:" + e.getMessage());
		}
	}

	/*
	 * Thai.truongming@gmail.com ---------------begin
	 * date: 03-01-2014
	 */
	public IProjection getWorld() {
		return world;
	}

	/*
	 * thai.truongming@gmail.com -----------------end
	 */
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
		if ( env != null ) {
			testConsistency(crs, env);
		}
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

	public IProjection forSavingWith(final Integer epsgCode) throws FactoryException {
		return forSavingWith(epsgCode, true);
	}

	public IProjection forSavingWith(final Integer epsgCode, final boolean lonFirst) throws FactoryException {
		return forSavingWith(EPSGPrefix + epsgCode, lonFirst);
	}

	public IProjection forSavingWith(final String code) throws FactoryException {
		return forSavingWith(code, true);
	}
	
	public IProjection forSavingWith(final CoordinateReferenceSystem crs) throws FactoryException {
		Projection gis = new Projection(world, this);
		gis.initialCRS = crs;
		gis.createTransformation(gis.computeProjection());
		return gis;
	}

	public IProjection forSavingWith(final String code, final boolean lonFirst) throws FactoryException {
		CoordinateReferenceSystem crs = null;
		try {
			crs = getCRS(code, lonFirst);
		} catch (Exception e) {
			crs = null;
		}
		if ( crs == null ) {
			crs = getSaveCRS();
		}
		Projection gis = new Projection(world, this);
		gis.initialCRS = crs;
		// gis.computeProjection();
		gis.createTransformation(gis.computeProjection());
		return gis;
	}

	public CoordinateReferenceSystem getDefaultInitialCRS() {
		if ( !GamaPreferences.LIB_PROJECTED.getValue() ) {
			try {
				return getCRS(GamaPreferences.LIB_INITIAL_CRS.getValue());
			} catch (GamaRuntimeException e) {
				throw GamaRuntimeException.error("The code " + GamaPreferences.LIB_INITIAL_CRS.getValue() +
					" does not correspond to a known EPSG code. Try to change it in Gama > Preferences... > External");
			}
		} else {
			return getTargetCRS();
		}
	}

	public void testConsistency(final CoordinateReferenceSystem crs, final Envelope env) {
		if ( !(crs instanceof ProjectedCRS) ) {
			if ( env.getHeight() > 180 || env.getWidth() > 180 ) { throw GamaRuntimeException
				.error("Inconsistency between the data and the CRS: The CRS " + crs +
					" corresponds to a not projected one, whereas the data seem to be already projected."); }
		}
	}
}
