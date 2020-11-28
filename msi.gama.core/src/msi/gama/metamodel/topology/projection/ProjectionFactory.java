/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.projection.ProjectionFactory.java, in plugin msi.gama.core, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;

import java.util.Map;

import si.uom.SI;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultProjectedCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.GamaGisFile;

/**
 * Class ProjectionFactory.
 *
 * @author drogoul
 * @since 17 déc. 2013
 *
 */
public class ProjectionFactory {

	private static final String EPSGPrefix = "EPSG:";
	private static final String defaultTargetCRS =
			String.valueOf(GamaPreferences.External.LIB_TARGET_CRS.getInitialValue(null));
	private static final String defaultSaveCRS =
			String.valueOf(GamaPreferences.External.LIB_OUTPUT_CRS.getInitialValue(null));
	private static Map<String, CoordinateReferenceSystem> CRSCache = GamaMapFactory.createUnordered();

	private IProjection world;
	private UnitConverter unitConverter = null;
	public CoordinateReferenceSystem targetCRS;

	public void setWorldProjectionEnv(final IScope scope, final Envelope3D env) {
		if (world != null) { return; }
		world = new WorldProjection(scope, null, env, this);
		// ((WorldProjection) world).updateTranslations(env);
	}

	void computeTargetCRS(final IScope scope, final CoordinateReferenceSystem crs, final double longitude,
			final double latitude) {
		// If we already know in which CRS we project the data in GAMA, no need to recompute it. This information is
		// normally wiped when an experiment is disposed
		if (targetCRS != null) { return; }
		try {
			if (!GamaPreferences.External.LIB_TARGETED.getValue()) {
				targetCRS = computeDefaultCRS(scope, GamaPreferences.External.LIB_TARGET_CRS.getValue(), true);
			} else {
				if (crs != null && crs instanceof DefaultProjectedCRS) { // Temporary fix of issue 766... a better solution
					final CartesianCS ccs = ((DefaultProjectedCRS) crs).getCoordinateSystem();
					final Unit<Length> unitX = (Unit<Length>) ccs.getAxis(0).getUnit();
					if (unitX != null && !unitX.equals(SI.METRE)) {
						unitConverter = unitX.getConverterTo(SI.METRE);
					}
					targetCRS = crs;
				} else {
					final int index = (int) (0.5 + (longitude + 186.0) / 6);
					final boolean north = latitude > 0;
					final String newCode = EPSGPrefix + (32600 + index + (north ? 0 : 100));
					targetCRS = getCRS(scope, newCode);
				}
			}
		} catch (final GamaRuntimeException e) {
			e.addContext(
					"The cause could be that you try to re-project already projected data (see Gama > Preferences... > External for turning the option to true)");
			throw e;
		}
	}

	public CoordinateReferenceSystem getTargetCRS(final IScope scope) {
		if (targetCRS == null) {

			try {
				return computeDefaultCRS(scope, GamaPreferences.External.LIB_TARGET_CRS.getValue(), true);

			} catch (final GamaRuntimeException e) {
				e.addContext(
						"The cause could be that you try to re-project already projected data (see Gama > Preferences... > External for turning the option to true)");
				throw e;
			}

		}
		return targetCRS;
	}

	public CoordinateReferenceSystem getSaveCRS(final IScope scope) {
		if (GamaPreferences.External.LIB_USE_DEFAULT.getValue()) { return getWorld().getInitialCRS(scope); }
		return computeDefaultCRS(scope, GamaPreferences.External.LIB_OUTPUT_CRS.getValue(), false);
	}

	public CoordinateReferenceSystem getCRS(final IScope scope, final int code) {
		return getCRS(scope, code, true);
	}

	public CoordinateReferenceSystem getCRS(final IScope scope, final int code, final boolean longitudeFirst) {
		if (code == GamaGisFile.ALREADY_PROJECTED_CODE) { return getTargetCRS(scope); }
		return getCRS(scope, EPSGPrefix + code, longitudeFirst);
	}

	public CoordinateReferenceSystem getCRS(final IScope scope, final String code) {
		return getCRS(scope, code, true);
	}

	public CoordinateReferenceSystem getCRS(final IScope scope, final String code, final boolean longitudeFirst) {
		try {
			CoordinateReferenceSystem crs = CRSCache.get(code);
			if (crs == null) {
				if (code.startsWith(EPSGPrefix) || code.startsWith("CRS:")) {
					crs = CRS.decode(code, longitudeFirst);
				} else if (code.startsWith("PROJCS") || code.startsWith("GEOGCS") || code.startsWith("COMPD_CS")) {
					crs = CRS.parseWKT(code);
				} else if (Character.isDigit(code.charAt(0))) {
					crs = CRS.decode(EPSGPrefix + code, longitudeFirst);
				}
				CRSCache.put(code, crs);
			}
			return crs;
		} catch (final NoSuchAuthorityCodeException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(
					"The EPSG code " + code + " cannot be found. GAMA may be unable to load or save any GIS data",
					scope);
		} catch (final FactoryException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("An exception occured in trying to decode GIS data:" + e.getMessage(),
					scope);
		}
	}

	/*
	 * Thai.truongming@gmail.com ---------------begin date: 03-01-2014
	 */
	public IProjection getWorld() {
		return world;
	}

	/*
	 * thai.truongming@gmail.com -----------------end
	 */
	public CoordinateReferenceSystem computeDefaultCRS(final IScope scope, final int code, final boolean target) {
		CoordinateReferenceSystem crs = getCRS(scope, code);
		if (crs == null) {
			crs = getCRS(scope, EPSGPrefix + (target ? defaultTargetCRS : defaultSaveCRS));
		}
		return crs;
	}

	public IProjection fromParams(final IScope scope, final Map<String, Object> params, final Envelope3D env) {
		final Boolean lonFirst = params.containsKey("longitudeFirst") ? (Boolean) params.get("longitudeFirst") : true;
		final Object crs = params.get("crs");
		if (crs instanceof String) { return fromCRS(scope, getCRS(scope, (String) crs, lonFirst), env); }
		final Object srid = params.get("srid");
		if (srid instanceof String) { return fromCRS(scope, getCRS(scope, (String) srid, lonFirst), env); }
		return fromCRS(scope, getDefaultInitialCRS(scope), env);
	}

	public IProjection fromCRS(final IScope scope, final CoordinateReferenceSystem crs, final Envelope3D env) {
		if (env != null) {
			testConsistency(scope, crs, env);
		}
		if (world == null) {
			if (env != null) {
				computeTargetCRS(scope, crs, env.centre().x, env.centre().y);
			}
			world = new WorldProjection(scope, crs, env, this);
			return world;
		} else {
			return new Projection(scope, world, crs, env, this);
		}
	}

	public IProjection forSavingWith(final IScope scope, final Integer epsgCode) throws FactoryException {
		return forSavingWith(scope, epsgCode, true);
	}

	public IProjection forSavingWith(final IScope scope, final Integer epsgCode, final boolean lonFirst)
			throws FactoryException {
		return forSavingWith(scope, EPSGPrefix + epsgCode, lonFirst);
	}

	public IProjection forSavingWith(final IScope scope, final String code) throws FactoryException {
		return forSavingWith(scope, code, true);
	}

	public IProjection forSavingWith(final IScope scope, final CoordinateReferenceSystem crs) throws FactoryException {
		final Projection gis = new Projection(world, this);
		gis.initialCRS = crs;
		gis.createTransformation(gis.computeProjection(scope));
		return gis;
	}

	public IProjection forSavingWith(final IScope scope, final String code, final boolean lonFirst)
			throws FactoryException {
		CoordinateReferenceSystem crs = null;
		try {
			crs = getCRS(scope, code, lonFirst);
		} catch (final Exception e) {
			crs = null;
		}
		if (crs == null) {
			crs = getSaveCRS(scope);
		}
		final Projection gis = new Projection(world, this);
		gis.initialCRS = crs;
		// gis.computeProjection();
		gis.createTransformation(gis.computeProjection(scope));

		return gis;
	}

	public CoordinateReferenceSystem getDefaultInitialCRS(final IScope scope) {
		if (!GamaPreferences.External.LIB_PROJECTED.getValue()) {
			try {
				return getCRS(scope, GamaPreferences.External.LIB_INITIAL_CRS.getValue());
			} catch (final GamaRuntimeException e) {
				throw GamaRuntimeException.error("The code " + GamaPreferences.External.LIB_INITIAL_CRS.getValue()
						+ " does not correspond to a known EPSG code. Try to change it in Gama > Preferences... > External",
						scope);
			}
		} else {
			return getTargetCRS(scope);
		}
	}

	public void testConsistency(final IScope scope, final CoordinateReferenceSystem crs, final Envelope env) {
		if (!(crs instanceof DefaultProjectedCRS)) {
			if (env.getHeight() > 180 || env.getWidth() > 180) {
				throw GamaRuntimeException.error(
						"Inconsistency between the data and the CRS: The CRS " + crs
								+ " corresponds to a not projected one, whereas the data seem to be already projected.",
						scope);
			}
		}
	}

	public UnitConverter getUnitConverter() {
		return unitConverter;
	}

}
