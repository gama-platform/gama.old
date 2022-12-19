/*******************************************************************************************************
 *
 * ProjectionFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultProjectedCRS;
import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.GamaGisFile;
import tech.units.indriya.unit.Units;

/**
 * Class ProjectionFactory.
 *
 * @author drogoul
 * @since 17 déc. 2013
 *
 */
public class ProjectionFactory {

	/** The Constant EPSGPrefix. */
	private static final String EPSGPrefix = "EPSG:";

	/** The Constant defaultTargetCRS. */
	private static final String defaultTargetCRS =
			String.valueOf(GamaPreferences.External.LIB_TARGET_CRS.getInitialValue(null));

	/** The Constant defaultSaveCRS. */
	private static final String defaultSaveCRS =
			String.valueOf(GamaPreferences.External.LIB_OUTPUT_CRS.getInitialValue(null));

	/** The CRS cache. */
	private static Map<String, CoordinateReferenceSystem> CRSCache = GamaMapFactory.createUnordered();

	/** The world. */
	private IProjection world;

	/** The unit converter. */
	private UnitConverter unitConverter = null;

	/** The target CRS. */
	public CoordinateReferenceSystem targetCRS;

	public static CoordinateReferenceSystem EPSG3857 = null;

	static {
		try {
			EPSG3857 = CRS.decode("EPSG:3857");
		} catch (FactoryException e) {}
	}

	/**
	 * Manage google CRS.
	 *
	 * @param url
	 *            the url
	 * @return the coordinate reference system
	 */
	// ugly method to manage Google CRS.... hoping that it is better managed by the next versions of Geotools
	public static CoordinateReferenceSystem manageGoogleCRS(final URL url) {
		CoordinateReferenceSystem crs = null;
		try {
			final String path = new File(url.toURI()).getAbsolutePath().replace(".shp", ".prj");
			if (Files.exists(Paths.get(path))) {
				final byte[] encoded = Files.readAllBytes(Paths.get(path));
				final String content = new String(encoded, StandardCharsets.UTF_8);
				if (content.contains("WGS 84 / Pseudo-Mercator")
						|| content.contains("WGS_1984_Web_Mercator_Auxiliary_Sphere")) {
					crs = EPSG3857;
				}
			}
		} catch (final IOException | URISyntaxException e) {}
		return crs;
	}

	/**
	 * Sets the world projection env.
	 *
	 * @param scope
	 *            the scope
	 * @param env
	 *            the env
	 */
	public void setWorldProjectionEnv(final IScope scope, final Envelope3D env) {
		if (world != null) return;
		world = new WorldProjection(scope, null, env, this);
		// ((WorldProjection) world).updateTranslations(env);
	}

	/**
	 * Compute target CRS.
	 *
	 * @param scope
	 *            the scope
	 * @param crs
	 *            the crs
	 * @param longitude
	 *            the longitude
	 * @param latitude
	 *            the latitude
	 */
	void computeTargetCRS(final IScope scope, final CoordinateReferenceSystem crs, final double longitude,
			final double latitude) {
		// If we already know in which CRS we project the data in GAMA, no need to recompute it. This information is
		// normally wiped when an experiment is disposed
		if (targetCRS != null) return;
		try {
			if (!GamaPreferences.External.LIB_TARGETED.getValue()) {
				targetCRS = computeDefaultCRS(scope, GamaPreferences.External.LIB_TARGET_CRS.getValue(), true);
			} else if (crs instanceof DefaultProjectedCRS) { // Temporary fix of issue 766... a better
																// solution
				final CartesianCS ccs = ((DefaultProjectedCRS) crs).getCoordinateSystem();
				@SuppressWarnings ("unchecked") final Unit<Length> unitX = (Unit<Length>) ccs.getAxis(0).getUnit();
				if (unitX != null && !unitX.equals(Units.METRE)) { unitConverter = unitX.getConverterTo(Units.METRE); }
				targetCRS = crs;
			} else {
				final int index = (int) (0.5 + (longitude + 186.0) / 6);
				final boolean north = latitude > 0;
				final String newCode = EPSGPrefix + (32600 + index + (north ? 0 : 100));
				targetCRS = getCRS(scope, newCode);
			}
		} catch (final GamaRuntimeException e) {
			e.addContext(
					"The cause could be that you try to re-project already projected data (see Gama > Preferences... > External for turning the option to true)");
			throw e;
		}
	}

	/**
	 * Gets the crs.
	 *
	 * @param scope
	 *            the scope
	 * @return the crs
	 */
	public static CoordinateReferenceSystem getTargetCRSOrDefault(final IScope scope) {
		IProjection worldProjection = scope.getSimulation().getProjectionFactory().getWorld();
		return worldProjection == null ? ProjectionFactory.EPSG3857 : worldProjection.getTargetCRS(scope);
	}

	public static boolean saveTargetCRSAsPRJFile(final IScope scope, final String path) {
		CoordinateReferenceSystem crs = getTargetCRSOrDefault(scope);
		try (FileWriter fw =
				new FileWriter(path.replace(".png", ".prj").replace(".tif", ".prj").replace(".asc", ".prj"))) {
			fw.write(crs.toString());
			return true;
		} catch (final IOException e) {
			return false;
		}
	}

	/**
	 * Gets the target CRS.
	 *
	 * @param scope
	 *            the scope
	 * @return the target CRS
	 */
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

	/**
	 * Gets the save CRS.
	 *
	 * @param scope
	 *            the scope
	 * @return the save CRS
	 */
	public CoordinateReferenceSystem getSaveCRS(final IScope scope) {
		if (GamaPreferences.External.LIB_USE_DEFAULT.getValue()) return getWorld().getInitialCRS(scope);
		return computeDefaultCRS(scope, GamaPreferences.External.LIB_OUTPUT_CRS.getValue(), false);
	}

	/**
	 * Gets the crs.
	 *
	 * @param scope
	 *            the scope
	 * @param code
	 *            the code
	 * @return the crs
	 */
	public CoordinateReferenceSystem getCRS(final IScope scope, final int code) {
		return getCRS(scope, code, true);
	}

	/**
	 * Gets the crs.
	 *
	 * @param scope
	 *            the scope
	 * @param code
	 *            the code
	 * @param longitudeFirst
	 *            the longitude first
	 * @return the crs
	 */
	public CoordinateReferenceSystem getCRS(final IScope scope, final int code, final boolean longitudeFirst) {
		if (code == GamaGisFile.ALREADY_PROJECTED_CODE) return getTargetCRS(scope);
		return getCRS(scope, EPSGPrefix + code, longitudeFirst);
	}

	/**
	 * Gets the crs.
	 *
	 * @param scope
	 *            the scope
	 * @param code
	 *            the code
	 * @return the crs
	 */
	public CoordinateReferenceSystem getCRS(final IScope scope, final String code) {
		return getCRS(scope, code, true);
	}

	/**
	 * Gets the crs.
	 *
	 * @param scope
	 *            the scope
	 * @param code
	 *            the code
	 * @param longitudeFirst
	 *            the longitude first
	 * @return the crs
	 */
	public CoordinateReferenceSystem getCRS(final IScope scope, final String code, final boolean longitudeFirst) {
		try {
			CoordinateReferenceSystem crs = CRSCache.get(code);
			if (crs == null) {
				if (code.startsWith(EPSGPrefix) || code.startsWith("CRS:")) {
					crs = CRS.decode(code, longitudeFirst);
				} else if (code.startsWith("PROJCS") || code.startsWith("GEOGCS") || code.startsWith("COMPD_CS")) {
					crs = CRS.parseWKT(code);
				} else if (Character.isDigit(code.charAt(0))) { crs = CRS.decode(EPSGPrefix + code, longitudeFirst); }
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

	/**
	 * Gets the world.
	 *
	 * @return the world
	 */
		/*
		 * Thai.truongming@gmail.com ---------------begin date: 03-01-2014
		 */
	public IProjection getWorld() { return world; }

	/**
	 * Compute default CRS.
	 *
	 * @param scope
	 *            the scope
	 * @param code
	 *            the code
	 * @param target
	 *            the target
	 * @return the coordinate reference system
	 */
	/*
	 * thai.truongming@gmail.com -----------------end
	 */
	public CoordinateReferenceSystem computeDefaultCRS(final IScope scope, final int code, final boolean target) {
		CoordinateReferenceSystem crs = getCRS(scope, code);
		if (crs == null) { crs = getCRS(scope, EPSGPrefix + (target ? defaultTargetCRS : defaultSaveCRS)); }
		return crs;
	}

	/**
	 * From params.
	 *
	 * @param scope
	 *            the scope
	 * @param params
	 *            the params
	 * @param env
	 *            the env
	 * @return the i projection
	 */
	public IProjection fromParams(final IScope scope, final Map<String, Object> params, final Envelope3D env) {
		final Boolean lonFirst = params.containsKey("longitudeFirst") ? (Boolean) params.get("longitudeFirst") : true;
		final Object crs = params.get("crs");
		if (crs instanceof String) return fromCRS(scope, getCRS(scope, (String) crs, lonFirst), env);
		final Object srid = params.get("srid");
		if (srid instanceof String) return fromCRS(scope, getCRS(scope, (String) srid, lonFirst), env);
		return fromCRS(scope, getDefaultInitialCRS(scope), env);
	}

	/**
	 * From CRS.
	 *
	 * @param scope
	 *            the scope
	 * @param crs
	 *            the crs
	 * @param env
	 *            the env
	 * @return the i projection
	 */
	public IProjection fromCRS(final IScope scope, final CoordinateReferenceSystem crs, final Envelope3D env) {
		if (env != null) { testConsistency(scope, crs, env); }
		if (world != null) return new Projection(scope, world, crs, env, this);
		if (env != null) { computeTargetCRS(scope, crs, env.centre().x, env.centre().y); }
		world = new WorldProjection(scope, crs, env, this);
		return world;
	}

	/**
	 * For saving with.
	 *
	 * @param scope
	 *            the scope
	 * @param epsgCode
	 *            the epsg code
	 * @return the i projection
	 * @throws FactoryException
	 *             the factory exception
	 */
	public IProjection forSavingWith(final IScope scope, final Integer epsgCode) throws FactoryException {
		return forSavingWith(scope, epsgCode, true);
	}

	/**
	 * For saving with.
	 *
	 * @param scope
	 *            the scope
	 * @param epsgCode
	 *            the epsg code
	 * @param lonFirst
	 *            the lon first
	 * @return the i projection
	 * @throws FactoryException
	 *             the factory exception
	 */
	public IProjection forSavingWith(final IScope scope, final Integer epsgCode, final boolean lonFirst)
			throws FactoryException {
		return forSavingWith(scope, EPSGPrefix + epsgCode, lonFirst);
	}

	/**
	 * For saving with.
	 *
	 * @param scope
	 *            the scope
	 * @param code
	 *            the code
	 * @return the i projection
	 * @throws FactoryException
	 *             the factory exception
	 */
	public IProjection forSavingWith(final IScope scope, final String code) throws FactoryException {
		return forSavingWith(scope, code, true);
	}

	/**
	 * For saving with.
	 *
	 * @param scope
	 *            the scope
	 * @param crs
	 *            the crs
	 * @return the i projection
	 * @throws FactoryException
	 *             the factory exception
	 */
	public IProjection forSavingWith(final IScope scope, final CoordinateReferenceSystem crs) throws FactoryException {
		final Projection gis = new Projection(world, this);
		gis.initialCRS = crs;
		gis.createTransformation(gis.computeProjection(scope));
		return gis;
	}

	/**
	 * For saving with.
	 *
	 * @param scope
	 *            the scope
	 * @param code
	 *            the code
	 * @param lonFirst
	 *            the lon first
	 * @return the i projection
	 * @throws FactoryException
	 *             the factory exception
	 */
	public IProjection forSavingWith(final IScope scope, final String code, final boolean lonFirst)
			throws FactoryException {
		CoordinateReferenceSystem crs = null;
		try {
			crs = getCRS(scope, code, lonFirst);
		} catch (final Exception e) {
			crs = null;
		}
		if (crs == null) { crs = getSaveCRS(scope); }
		final Projection gis = new Projection(world, this);
		gis.initialCRS = crs;
		// gis.computeProjection();
		gis.createTransformation(gis.computeProjection(scope));

		return gis;
	}

	/**
	 * Gets the default initial CRS.
	 *
	 * @param scope
	 *            the scope
	 * @return the default initial CRS
	 */
	public CoordinateReferenceSystem getDefaultInitialCRS(final IScope scope) {
		if (GamaPreferences.External.LIB_PROJECTED.getValue()) return getTargetCRS(scope);
		try {
			return getCRS(scope, GamaPreferences.External.LIB_INITIAL_CRS.getValue());
		} catch (final GamaRuntimeException e) {
			throw GamaRuntimeException.error("The code " + GamaPreferences.External.LIB_INITIAL_CRS.getValue()
					+ " does not correspond to a known EPSG code. Try to change it in Gama > Preferences... > External",
					scope);
		}
	}

	/**
	 * Test consistency.
	 *
	 * @param scope
	 *            the scope
	 * @param crs
	 *            the crs
	 * @param env
	 *            the env
	 */
	public void testConsistency(final IScope scope, final CoordinateReferenceSystem crs, final Envelope env) {
		if (!(crs instanceof DefaultProjectedCRS) && (env.getHeight() > 180 || env.getWidth() > 180))
			throw GamaRuntimeException.error(
					"Inconsistency between the data and the CRS: The CRS " + crs
							+ " corresponds to a not projected one, whereas the data seem to be already projected.",
					scope);
	}

	/**
	 * Gets the unit converter.
	 *
	 * @return the unit converter
	 */
	public UnitConverter getUnitConverter() { return unitConverter; }

}
