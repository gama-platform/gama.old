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
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.species.ISpecies;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul Modified on 1 août 2010
 * 
 * @todo Description
 * 
 */
@type(name = IKeyword.GEOMETRY, id = IType.GEOMETRY, wraps = { GamaShape.class, IShape.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaGeometryType extends GamaType<IShape> {

	@Override
	public IShape cast(final IScope scope, final Object obj, final Object param) throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static IShape staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj instanceof IShape ) { return ((IShape) obj).getGeometry(); }
		if ( obj instanceof ILocation ) { return GamaGeometryType.createPoint((GamaPoint) obj); }
		if ( obj instanceof ISpecies ) { return geometriesToGeometry(scope,
			scope.getAgentScope().getPopulationFor((ISpecies) obj)); }
		if ( obj instanceof GamaPair ) { return pairToGeometry(scope, (GamaPair) obj); }
		if ( obj instanceof IContainer ) {
			if ( isPoints(scope, (IContainer) obj) ) { return pointsToGeometry(scope, (IContainer<?, ILocation>) obj); }
			return geometriesToGeometry(scope, (IContainer) obj);
		}

		// Faire ici tous les casts nécessaires pour construire des géométries (liste, string, etc.)
		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	private static boolean isPoints(final IScope scope, final IContainer obj) {
		for ( final Object o : obj.iterable(scope) ) {
			if ( !(o instanceof ILocation) ) { return false; }
		}
		return true;
	}

	@Override
	public GamaShape getDefault() {
		return null;
	}

	@Override
	public IType defaultKeyType() {
		return Types.get(IType.STRING);
	}

	@Override
	public boolean hasContents() {
		return true;
	}

	@Override
	public boolean isFixedLength() {
		return false;
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
	public static IShape buildPolygon(final List<IShape> points) {
		final CoordinateSequenceFactory fact = GeometryUtils.coordFactory;
		final int size = points.size();
		// AD 12/05/13 The dimensions of the points to create have been changed to 3, otherwise the z coordinates could
		// be lost when copying this geometry
		CoordinateSequence cs = fact.create(size, 3);
		for ( int i = 0; i < size; i++ ) {
			final Coordinate p = (GamaPoint) points.get(i).getLocation();
			cs.setOrdinate(i, 0, p.x);
			cs.setOrdinate(i, 1, p.y);
			cs.setOrdinate(i, 2, p.z);
		}
		cs = CoordinateSequences.ensureValidRing(fact, cs);
		final LinearRing geom = GeometryUtils.factory.createLinearRing(cs);
		final Polygon p = GeometryUtils.factory.createPolygon(geom, null);

		if ( p.isValid() ) { return new GamaShape(p.buffer(0.0)); } // Why buffer (0.0) ???
		return buildPolyline(points);
		// / ???
	}

	// Maybe a bit overkill, but the list of points is created *and* validated by the call to buildPolygon()
	public static IShape buildTriangle(final double side_size, final ILocation location) {
		final double sqrt2 = Math.sqrt(2.0);
		final double x = location == null ? 0 : location.getX();
		final double y = location == null ? 0 : location.getY();
		final double z = location == null ? 0 : location.getZ();
		final List<IShape> points = new ArrayList(4);
		points.add(new GamaPoint(x, y - side_size / sqrt2, z));
		points.add(new GamaPoint(x - side_size / sqrt2, y + side_size / sqrt2, z));
		points.add(new GamaPoint(x + side_size / sqrt2, y + side_size / sqrt2, z));
		points.add(new GamaPoint(x, y - side_size / sqrt2, z));
		return buildPolygon(points);
	}

	public static IShape buildRectangle(final double width, final double height, final ILocation location) {
		final Coordinate[] points = new Coordinate[5];
		final double x = location == null ? 0 : location.getX();
		final double y = location == null ? 0 : location.getY();
		final double z = location == null ? 0 : location.getZ();
		points[0] = new GamaPoint(x - width / 2.0, y + height / 2.0, z);
		points[1] = new GamaPoint(x + width / 2.0, y + height / 2.0, z);
		points[2] = new GamaPoint(x + width / 2.0, y - height / 2.0, z);
		points[3] = new GamaPoint(x - width / 2.0, y - height / 2.0, z);
		points[4] = new GamaPoint(x - width / 2.0, y + height / 2.0, z);
		final CoordinateSequenceFactory fact = GeometryUtils.coordFactory;
		final CoordinateSequence cs = fact.create(points);
		final LinearRing geom = GeometryUtils.factory.createLinearRing(cs);
		final Polygon p = GeometryUtils.factory.createPolygon(geom, null);
		return new GamaShape(p);
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
	public static IShape buildPolyhedron(final List<IShape> points, final Double depth) {
		final GamaShape g = new GamaShape(buildPolygon(points));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "polyhedron");
		return g;
	}

	public static IShape buildLine(final IShape location1, final IShape location2) {
		final Coordinate coordinates[] =
			{ location1 == null ? new GamaPoint(0, 0) : (GamaPoint) location1.getLocation(),
				location2 == null ? new GamaPoint(0, 0) : (GamaPoint) location2.getLocation() };
		// WARNING Circumvents a bug in JTS 1.13, where a line built between two identical points would return a null
		// centroid
		if ( coordinates[0].equals(coordinates[1]) ) { return createPoint((GamaPoint) coordinates[0]); }
		return new GamaShape(GeometryUtils.factory.createLineString(coordinates));
	}

	public static IShape buildPlan(final IShape location1, final IShape location2, final Double depth) {
		final GamaShape g = new GamaShape(buildLine(location1, location2));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "plan");
		return g;
	}

	public static IShape buildPolyline(final List<IShape> points) {
		final List<Coordinate> coordinates = new ArrayList<Coordinate>();
		for ( final IShape p : points ) {
			coordinates.add((GamaPoint) p.getLocation());
		}
		return new GamaShape(GeometryUtils.factory.createLineString(coordinates.toArray(new Coordinate[0])));
	}

	public static IShape buildPolyplan(final List<IShape> points, final Double depth) {
		final GamaShape g = new GamaShape(buildPolyline(points));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "polyplan");
		return g;
	}

	public static IShape createPoint(final IShape location) {
		return new GamaShape(GeometryUtils.factory.createPoint(location == null ? new GamaPoint(0, 0)
			: (GamaPoint) location.getLocation()));
	}

	public static IShape buildSquare(final double side_size, final ILocation location) {
		return buildRectangle(side_size, side_size, location);
	}

	public static IShape buildCube(final double side_size, final ILocation location) {

		final GamaShape g = new GamaShape(buildRectangle(side_size, side_size, location));
		g.setAttribute("depth", side_size);
		g.setAttribute("type", "cube");
		return g;

	}

	// public static IShape buildRectangle(final double width, final double height, final ILocation location) {
	// Coordinate[] coordinates = new Coordinate[5];
	// double x = location == null ? 0 : location.getX();
	// double y = location == null ? 0 : location.getY();
	// coordinates[0] = new Coordinate(x - width / 2.0, y + height / 2.0);
	// coordinates[1] = new Coordinate(x + width / 2.0, y + height / 2.0);
	// coordinates[2] = new Coordinate(x + width / 2.0, y - height / 2.0);
	// coordinates[3] = new Coordinate(x - width / 2.0, y - height / 2.0);
	// coordinates[4] = (Coordinate) coordinates[0].clone();
	// LinearRing geom = GeometryUtils.getFactory().createLinearRing(coordinates);
	// Geometry g = GeometryUtils.getFactory().createPolygon(geom, null);
	// if ( location != null ) {
	// Coordinate[] coords = g.getCoordinates();
	// for ( int i = 0; i < coords.length; i++ ) {
	// coords[i].z = ((GamaPoint) location).z;
	// }
	// }
	// return new GamaShape(g);
	// }

	public static IShape buildBox(final double width, final double height, final double depth, final ILocation location) {
		final GamaShape g = new GamaShape(buildRectangle(width, height, location));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "box");
		return g;
	}

	public static IShape buildHexagon(final double size, final double x, final double y) {
		return buildHexagon(size, new GamaPoint(x, y));
	}

	public static IShape buildHexagon(final double size, final ILocation location) {
		final double x = location.getX();
		final double y = location.getY();
		final double h = size / 2;
		final double width = size * (Math.sqrt(3) / 2.0);
		final Coordinate[] coords = new Coordinate[7];
		coords[0] = new GamaPoint(x, y + width);
		coords[1] = new GamaPoint(x + h, y);
		coords[2] = new GamaPoint(x + 1.5 * size, y);
		coords[3] = new GamaPoint(x + 2 * size, y + width);
		coords[4] = new GamaPoint(x + 1.5 * size, y + 2 * width);
		coords[5] = new GamaPoint(x + h, y + 2 * width);
		coords[6] = new GamaPoint(coords[0]);
		return new GamaShape(GeometryUtils.factory.createPolygon(GeometryUtils.factory.createLinearRing(coords), null));

	}

	public static IShape buildHexagon(final double sizeX, final double sizeY, final ILocation location) {
		final double x = location.getX();
		final double y = location.getY();
		final Coordinate[] coords = new Coordinate[7];
		coords[0] = new GamaPoint(x - sizeX / 2.0, y);
		coords[1] = new GamaPoint(x - sizeX / 4, y + sizeY / 2);
		coords[2] = new GamaPoint(x + sizeX / 4, y + sizeY / 2);
		coords[3] = new GamaPoint(x + sizeX / 2, y);
		coords[4] = new GamaPoint(x + sizeX / 4, y - sizeY / 2);
		coords[5] = new GamaPoint(x - sizeX / 4, y - sizeY / 2);
		coords[6] = new GamaPoint(coords[0]);
		return new GamaShape(GeometryUtils.factory.createPolygon(GeometryUtils.factory.createLinearRing(coords), null));

	}

	public static IShape buildCircle(final double radius, final ILocation location) {
		final Geometry geom =
			GeometryUtils.factory.createPoint(location == null ? new GamaPoint(0, 0) : (GamaPoint) location);
		final Geometry g = geom.buffer(radius);
		if ( location != null ) {
			final Coordinate[] coordinates = g.getCoordinates();
			for ( int i = 0; i < coordinates.length; i++ ) {
				coordinates[i].z = ((GamaPoint) location).z;
			}
		}
		return new GamaShape(g);
	}

	public static IShape buildCylinder(final double radius, final double depth, final ILocation location) {
		final GamaShape g = new GamaShape(buildCircle(radius, location));
		g.setAttribute("depth", depth);
		g.setAttribute("type", "cylinder");
		return g;
	}

	// FIXME: Be sure that a buffer on a sphere returns a sphere.
	public static IShape buildSphere(final double radius, final ILocation location) {
		final GamaShape g = new GamaShape(buildCircle(radius, location));
		g.setAttribute("depth", radius);
		g.setAttribute("type", "sphere");
		return g;
	}

	public static GamaShape geometriesToGeometry(final IScope scope, final IContainer<?, ? extends IShape> ags)
		throws GamaRuntimeException {
		if ( ags == null || ags.isEmpty(scope) ) { return null; }
		final Geometry geoms[] = new Geometry[ags.length(scope)];
		int cpt = 0;
		for ( final Object ent : ags.iterable(scope) ) {
			if ( ent == null ) {
				continue;
			}
			if ( !(ent instanceof IShape) ) { throw GamaRuntimeException
				.error("Cannot cast to geometry a container of " +
					((ModelDescription) scope.getModel().getDescription()).getTypesManager().get(ent.getClass())); }
			geoms[cpt] = ((IShape) ent).getInnerGeometry();
			cpt++;
		}
		final Geometry geom = GeometryUtils.factory.createGeometryCollection(geoms);
		geom.union();
		if ( geom.isValid() && !geom.isEmpty() ) { return new GamaShape(geom); }
		return null;
	}

	public static GamaShape pointsToGeometry(final IScope scope, final IContainer<?, ILocation> coordinates)
		throws GamaRuntimeException {
		if ( coordinates != null && !coordinates.isEmpty(scope) ) {
			final List<List<ILocation>> geoSimp = new GamaList();
			geoSimp.add(coordinates.listValue(scope));
			final List<List<List<ILocation>>> geomG = new GamaList();
			geomG.add(geoSimp);
			final Geometry geom = GeometryUtils.buildGeometryJTS(geomG);
			return new GamaShape(geom);
		}
		return null;
	}

	public static IShape pairToGeometry(final IScope scope, final GamaPair p) throws GamaRuntimeException {
		final IShape first = staticCast(scope, p.first(), null);
		if ( first == null ) { return null; }
		final IShape second = staticCast(scope, p.last(), null);
		if ( second == null ) { return null; }
		return new GamaDynamicLink(first, second);
	}

	// /////////////////////// 3D Shape (Not yet implemented in 3D (e.g a Sphere is displayed as a
	// sphere but is a JTS circle) /////////////////////////////

}
