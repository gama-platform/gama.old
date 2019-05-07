/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGisFile.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import java.util.ArrayList;
import java.util.List;

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GamaCoordinateSequenceFactory;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaGeometryType;

/**
 * Class GamaGisFile.
 * 
 * @author drogoul
 * @since 12 déc. 2013
 * 
 */
public abstract class GamaGisFile extends GamaGeometryFile {

	// The code to force reading the GIS data as already projected
	public static final int ALREADY_PROJECTED_CODE = 0;
	static CoordinateFilter ZERO_Z = coord -> ((GamaPoint) coord).z = 0;
	protected IProjection gis;
	protected Integer initialCRSCode = null;
	protected String initialCRSCodeStr = null;
	protected boolean with3D = false;

	// Faire les tests sur ALREADY_PROJECTED ET LE PASSER AUSSI A GIS UTILS ???

	/**
	 * Returns the CRS defined with this file (in a ".prj" file or elsewhere)
	 * 
	 * @return
	 */
	protected CoordinateReferenceSystem getExistingCRS(final IScope scope) {
		if (initialCRSCode != null) {
			try {
				return scope.getSimulation().getProjectionFactory().getCRS(scope, initialCRSCode);
			} catch (final GamaRuntimeException e) {
				throw GamaRuntimeException.error(
						"The code " + initialCRSCode
								+ " does not correspond to a known EPSG code. GAMA is unable to load " + getPath(scope),
						scope);
			}
		}
		if (initialCRSCodeStr != null) {
			try {
				return scope.getSimulation().getProjectionFactory().getCRS(scope, initialCRSCodeStr);
			} catch (final GamaRuntimeException e) {
				throw GamaRuntimeException.error(
						"The code " + initialCRSCodeStr
								+ " does not correspond to a known CRS code. GAMA is unable to load " + getPath(scope),
						scope);
			}
		}
		CoordinateReferenceSystem crs = getOwnCRS(scope);
		if (crs == null && scope != null) {
			crs = scope.getSimulation().getProjectionFactory().getDefaultInitialCRS(scope);
		}
		return crs;
	}

	/**
	 * @return
	 */
	protected abstract CoordinateReferenceSystem getOwnCRS(IScope scope);

	protected void computeProjection(final IScope scope, final Envelope3D env) {
		if (scope == null ) { return; }
		final CoordinateReferenceSystem crs = getExistingCRS(scope);
		final ProjectionFactory pf = scope.getSimulation() == null ?  new ProjectionFactory() : scope.getSimulation().getProjectionFactory();
		gis = pf.fromCRS(scope, crs, env);
	}

	protected Geometry multiPolygonManagement(Geometry geom) {
		if (geom instanceof MultiPolygon) {
				GamaCoordinateSequenceFactory f = new GamaCoordinateSequenceFactory();
				Polygon gs[] = new Polygon[geom.getNumGeometries()];
				for (int i = 0; i < geom.getNumGeometries(); i++ ) {
					Polygon p = (Polygon) geom.getGeometryN(i);
					ICoordinates coords = f.create(p.getCoordinates());
					Coordinate[] coord = new Coordinate[coords.size()+1];
					for (int j = 0; j < coords.size();j++) coord[j] = coords.getCoordinate(j);
					coord[coords.size()] = coord[0];
					LinearRing lr = GEOMETRY_FACTORY.createLinearRing(coord);

					List<LinearRing> holes = new ArrayList<>();
					for (int j = 0; j < p.getNumInteriorRing(); j++) {
						LinearRing h = (LinearRing) p.getInteriorRingN(j);
						if (!hasNullElements(h.getCoordinates()))
							holes.add(h);
					}	
					LinearRing[] stockArr = new LinearRing[holes.size()];
					stockArr = holes.toArray(stockArr);

					gs[i] = GEOMETRY_FACTORY.createPolygon(lr, stockArr);
				}
				return GEOMETRY_FACTORY.createMultiPolygon(gs);
		}
		return geom;
	}
	
	protected static boolean hasNullElements(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) {
				return true;
			}
		}
		return false;
	}

	public GamaGisFile(final IScope scope, final String pathName, final Integer code, final boolean withZ) {
		super(scope, pathName);
		initialCRSCode = code;
		with3D = withZ;
	}

	public GamaGisFile(final IScope scope, final String pathName, final Integer code) {
		super(scope, pathName);
		initialCRSCode = code;
	}

	public GamaGisFile(final IScope scope, final String pathName, final String code) {
		super(scope, pathName);
		initialCRSCodeStr = code;
	}

	public GamaGisFile(final IScope scope, final String pathName, final String code, final boolean withZ) {
		super(scope, pathName);
		initialCRSCodeStr = code;
		with3D = withZ;
	}

	public IProjection getGis(final IScope scope) {
		if (gis == null) {
			fillBuffer(scope);
		}
		return gis;
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		return GamaGeometryType.geometriesToGeometry(scope, getBuffer());
	}

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		gis = null;
		initialCRSCode = null;
		initialCRSCodeStr = null;
	}

}
