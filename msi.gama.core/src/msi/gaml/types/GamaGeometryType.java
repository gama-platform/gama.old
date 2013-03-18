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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import java.util.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul Modified on 1 août 2010
 * 
 * @todo Description
 * 
 */
@type(name = IType.GEOM_STR, id = IType.GEOMETRY, wraps = { GamaShape.class, IShape.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaGeometryType extends GamaType<IShape> {

	@Override
	public IShape cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static IShape staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj instanceof IShape ) { return ((IShape) obj).getGeometry(); }
		if ( obj instanceof ILocation ) { return GamaGeometryType.createPoint((GamaPoint) obj); }
		if ( obj instanceof ISpecies ) {
			IList<IAgent> agents =
				scope.getAgentScope().getPopulationFor((ISpecies) obj).getAgentsList();
			return geometriesToGeometry(scope, agents);
		}
		if ( obj instanceof GamaPair ) { return pairToGeometry(scope, (GamaPair) obj); }
		if ( obj instanceof IContainer ) {
			if ( isPoints((IContainer) obj) ) { return pointsToGeometry(scope,
				(IContainer<?, ILocation>) obj); }
			return geometriesToGeometry(scope, (IContainer) obj);
		}

		// Faire ici tous les casts nécessaires pour construire des géométries (liste, string, etc.)
		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	private static boolean isPoints(final IContainer obj) {
		for ( Object o : obj ) {
			if ( !(o instanceof ILocation) ) { return false; }
		}
		return true;
	}

	@Override
	public GamaShape getDefault() {
		return null; // Retourner un point; ?
	}

	/**
	 * Builds a (cleansed) polygon from a list of points. The input points must be valid to create a
	 * linear ring (first point and last point are duplicated). It is the responsible of the caller
	 * to assure the validity of the input parameter.
	 * Update: the coordinate sequence is now validated before creating the polygon, and any
	 * necessary point is added.
	 * 
	 * @param points
	 * @return
	 */
	public static IShape buildPolygon(final List<GamaPoint> points) {
		CoordinateSequenceFactory fact = GeometryUtils.getFactory().getCoordinateSequenceFactory();
		int size = points.size();
		CoordinateSequence cs = fact.create(size, 2);
		for ( int i = 0; i < size; i++ ) {
			Coordinate p = points.get(i);
			cs.setOrdinate(i, 0, p.x);
			cs.setOrdinate(i, 1, p.y);
			cs.setOrdinate(i, 2, p.z);
		}
		cs = CoordinateSequences.ensureValidRing(fact, cs);
		LinearRing geom = GeometryUtils.getFactory().createLinearRing(cs);
		Polygon p = GeometryUtils.getFactory().createPolygon(geom, null);

		if ( p.isValid() ) { return new GamaShape(p.buffer(0.0)); } // Why buffer (0.0) ???
		return buildPolyline(points);
		// / ???
	}

	/**
	 * Builds a (cleansed) polyhedron from a list of points and a given depth. The input points must
	 * be valid to create a
	 * linear ring (first point and last point are duplicated). It is the responsible of the caller
	 * to assure the validity of the input parameter.
	 * Update: the coordinate sequence is now validated before creating the polygon, and any
	 * necessary point is added.
	 * 
	 * @param points
	 * @return
	 */
	public static IShape buildPolyhedron(final List<GamaPoint> points, final Double depth) {
		GamaShape g = new GamaShape(buildPolygon(points));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "polyhedron");
		return g;
	}

	public static IShape buildLine(final ILocation location1, final ILocation location2) {
		Coordinate coordinates[] =
			{ location1 == null ? new GamaPoint(0, 0) : (GamaPoint) location1,
				location2 == null ? new GamaPoint(0, 0) : (GamaPoint) location2 };
		return new GamaShape(GeometryUtils.getFactory().createLineString(coordinates));
	}

	public static IShape buildPlan(final ILocation location1, final ILocation location2,
		final Double depth) {
		GamaShape g = new GamaShape(buildLine(location1, location2));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "plan");
		return g;
	}

	public static IShape buildPolyline(final List<GamaPoint> points) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		for ( ILocation p : points ) {
			coordinates.add((Coordinate) p);
		}
		return new GamaShape(GeometryUtils.getFactory().createLineString(
			coordinates.toArray(new Coordinate[0])));
	}

	public static IShape buildPolyplan(final List<GamaPoint> points, final Double depth) {
		GamaShape g = new GamaShape(buildPolyline(points));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "polyplan");
		return g;
	}

	public static IShape createPoint(final ILocation location) {
		return new GamaShape(GeometryUtils.getFactory().createPoint(
			location == null ? new GamaPoint(0, 0) : (Coordinate) location));
	}

	public static IShape buildTriangle(final double side_size, final ILocation location) {
		double sqrt2 = Math.sqrt(2.0);
		double x = location == null ? 0 : location.getX();
		double y = location == null ? 0 : location.getY();
		Coordinate[] coordinates = new Coordinate[4];
		coordinates[0] = new Coordinate(x, y - side_size / sqrt2);
		coordinates[1] = new Coordinate(x - side_size / sqrt2, y + side_size / sqrt2);
		coordinates[2] = new Coordinate(x + side_size / sqrt2, y + side_size / sqrt2);
		coordinates[3] = (Coordinate) coordinates[0].clone();
		LinearRing geom = GeometryUtils.getFactory().createLinearRing(coordinates);
		Geometry g = GeometryUtils.getFactory().createPolygon(geom, null);
		if ( location != null ) {
			Coordinate[] coords = g.getCoordinates();
			for ( int i = 0; i < coords.length; i++ ) {
				coords[i].z = ((GamaPoint) location).z;
			}
		}
		return new GamaShape(g);
	}

	public static IShape buildSquare(final double side_size, final ILocation location) {
		return buildRectangle(side_size, side_size, location == null ? new GamaPoint(0, 0)
			: location);
	}

	public static IShape buildCube(final double side_size, final ILocation location) {

		GamaShape g = new GamaShape(buildRectangle(side_size, side_size, location));
		g.setAttribute("depth", side_size);
		g.setAttribute("type", "cube");
		return g;

	}

	public static IShape buildRectangle(final double width, final double height,
		final ILocation location) {
		Coordinate[] coordinates = new Coordinate[5];
		double x = location == null ? 0 : location.getX();
		double y = location == null ? 0 : location.getY();
		coordinates[0] = new Coordinate(x - width / 2.0, y + height / 2.0);
		coordinates[1] = new Coordinate(x + width / 2.0, y + height / 2.0);
		coordinates[2] = new Coordinate(x + width / 2.0, y - height / 2.0);
		coordinates[3] = new Coordinate(x - width / 2.0, y - height / 2.0);
		coordinates[4] = (Coordinate) coordinates[0].clone();
		LinearRing geom = GeometryUtils.getFactory().createLinearRing(coordinates);
		Geometry g = GeometryUtils.getFactory().createPolygon(geom, null);
		if ( location != null ) {
			Coordinate[] coords = g.getCoordinates();
			for ( int i = 0; i < coords.length; i++ ) {
				coords[i].z = ((GamaPoint) location).z;
			}
		}
		return new GamaShape(g);
	}

	public static IShape buildBox(final double width, final double height, final double depth,
		final ILocation location) {
		GamaShape g = new GamaShape(buildRectangle(width, height, location));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "box");
		return g;
	}

	public static IShape buildHexagon(final double size, final double x, final double y) {
		return buildHexagon(size, new GamaPoint(x, y));
	}

	public static IShape buildHexagon(final double size, final ILocation location) {
		double x = location.getX();
		double y = location.getY();
		double h = size / 2;
		double width = size * (Math.sqrt(3) / 2.0);
		Coordinate[] coords = new Coordinate[7];
		coords[0] = new GamaPoint(x, y + width);
		coords[1] = new GamaPoint(x + h, y);
		coords[2] = new GamaPoint(x + 1.5 * size, y);
		coords[3] = new GamaPoint(x + 2 * size, y + width);
		coords[4] = new GamaPoint(x + 1.5 * size, y + 2 * width);
		coords[5] = new GamaPoint(x + h, y + 2 * width);
		coords[6] = new GamaPoint(coords[0]);
		return new GamaShape(GeometryUtils.factory.createPolygon(
			GeometryUtils.factory.createLinearRing(coords), null));

	}

	public static IShape buildHexagon(final double sizeX, final double sizeY,
		final ILocation location) {
		double x = location.getX();
		double y = location.getY();
		Coordinate[] coords = new Coordinate[7];
		coords[0] = new GamaPoint(x - sizeX / 2.0, y);
		coords[1] = new GamaPoint(x - sizeX / 4, y + sizeY / 2);
		coords[2] = new GamaPoint(x + sizeX / 4, y + sizeY / 2);
		coords[3] = new GamaPoint(x + sizeX / 2, y);
		coords[4] = new GamaPoint(x + sizeX / 4, y - sizeY / 2);
		coords[5] = new GamaPoint(x - sizeX / 4, y - sizeY / 2);
		coords[6] = new GamaPoint(coords[0]);
		return new GamaShape(GeometryUtils.factory.createPolygon(
			GeometryUtils.factory.createLinearRing(coords), null));

	}

	public static IShape buildCircle(final double radius, final ILocation location) {
		Geometry geom =
			GeometryUtils.getFactory().createPoint(
				location == null ? new GamaPoint(0, 0) : (GamaPoint) location);
		Geometry g = geom.buffer(radius);
		if ( location != null ) {
			Coordinate[] coordinates = g.getCoordinates();
			for ( int i = 0; i < coordinates.length; i++ ) {
				coordinates[i].z = ((GamaPoint) location).z;
			}
		}
		return new GamaShape(g);
	}

	public static IShape buildCylinder(final double radius, final double depth,
		final ILocation location) {
		GamaShape g = new GamaShape(buildCircle(radius, location));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "cylinder");
		return g;
	}

	// FIXME: Be sure that a buffer on a sphere returns a sphere.
	public static IShape buildSphere(final double radius, final ILocation location) {
		GamaShape g = new GamaShape(buildCircle(radius, location));
		g.setAttribute("depth", radius);
		g.setAttribute("type", "sphere");
		return g;
	}

	public static GamaShape geometriesToGeometry(final IScope scope,
		final IContainer<?, ? extends IShape> ags) throws GamaRuntimeException {
		if ( ags == null || ags.isEmpty(scope) ) { return null; }
		Geometry geom = ((IShape) ags.first(scope)).getInnerGeometry();
		Geometry geoms[] = new Geometry[ags.length(scope)];
		int cpt = 0;
		for ( IShape ent : ags ) {
			geoms[cpt] = ent.getInnerGeometry();
			cpt++;
		}
		geom = GeometryUtils.factory.createGeometryCollection(geoms);
		geom.union();
		if ( geom.isValid() && !geom.isEmpty() ) { return new GamaShape(geom); }
		return null;
	}

	public static GamaShape pointsToGeometry(final IScope scope,
		final IContainer<?, ILocation> coordinates) throws GamaRuntimeException {
		if ( coordinates != null && !coordinates.isEmpty(scope) ) {
			List<List<ILocation>> geoSimp = new GamaList();
			geoSimp.add(coordinates.listValue(scope));
			List<List<List<ILocation>>> geomG = new GamaList();
			geomG.add(geoSimp);
			Geometry geom = GeometryUtils.buildGeometryJTS(geomG);
			return new GamaShape(geom);
		}
		return null;
	}

	public static IShape pairToGeometry(final IScope scope, final GamaPair p)
		throws GamaRuntimeException {
		IShape first = staticCast(scope, p.first(), null);
		if ( first == null ) { return null; }
		IShape second = staticCast(scope, p.last(), null);
		if ( second == null ) { return null; }
		return new GamaDynamicLink(first, second);
	}

	// /////////////////////// 3D Shape (Not yet implemented in 3D (e.g a Sphere is displayed as a
	// sphere but is a JTS circle) /////////////////////////////

}
