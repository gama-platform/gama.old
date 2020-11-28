/*******************************************************************************************************
 *
 * msi.gaml.types.GamaGeometryType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import static msi.gama.metamodel.shape.IShape.Type.BOX;
import static msi.gama.metamodel.shape.IShape.Type.CONE;
import static msi.gama.metamodel.shape.IShape.Type.CUBE;
import static msi.gama.metamodel.shape.IShape.Type.CYLINDER;
import static msi.gama.metamodel.shape.IShape.Type.LINECYLINDER;
import static msi.gama.metamodel.shape.IShape.Type.PLAN;
import static msi.gama.metamodel.shape.IShape.Type.POLYHEDRON;
import static msi.gama.metamodel.shape.IShape.Type.POLYPLAN;
import static msi.gama.metamodel.shape.IShape.Type.PYRAMID;
import static msi.gama.metamodel.shape.IShape.Type.SPHERE;
import static msi.gama.metamodel.shape.IShape.Type.TEAPOT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.CoordinateSequences;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;
import org.locationtech.jts.util.AssertionFailedException;
import org.locationtech.jts.util.GeometricShapeFactory;

import msi.gama.common.geometry.GamaGeometryFactory;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.DynamicLineString;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Spatial;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@type (
		name = IKeyword.GEOMETRY,
		id = IType.GEOMETRY,
		wraps = { IShape.class, GamaShape.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.GEOMETRY },
		doc = @doc ("Represents geometries, i.e. the support for the shapes of agents and all the spatial operations in GAMA."))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGeometryType extends GamaType<IShape> {

	public static WKTReader SHAPE_READER = new WKTReader();

	@Override
	public IShape cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	public static IShape staticCast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {

		if (obj instanceof IShape) {
			// WARNING TODO Take copy into account
			return (IShape) obj;
		}
		// if ( obj instanceof GamaPoint ) { return createPoint((GamaPoint)
		// obj); }
		// if ( obj instanceof IShape ) { return ((IShape) obj).getGeometry(); }
		if (obj instanceof ISpecies) {
			return geometriesToGeometry(scope, scope.getAgent().getPopulationFor((ISpecies) obj));
		}
		if (obj instanceof GamaPair) { return pairToGeometry(scope, (GamaPair) obj); }
		if (obj instanceof GamaGeometryFile) { return ((GamaGeometryFile) obj).getGeometry(scope); }
		if (obj instanceof IContainer) {
			if (isPoints(scope, (IContainer) obj)) { return pointsToGeometry(scope, (IContainer<?, ILocation>) obj); }
			return geometriesToGeometry(scope, (IContainer) obj);
		}
		if (obj instanceof String) {
			// Try to decode a WKT representation (the format outputted by the
			// conversion of geometries to strings)
			try {
				final Geometry g = SHAPE_READER.read((String) obj);
				return new GamaShape(g);
			} catch (final ParseException e) {
				GAMA.reportError(scope, GamaRuntimeException.warning("WKT Parsing exception: " + e.getMessage(), scope),
						false);
			}
		}

		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	private static boolean isPoints(final IScope scope, final IContainer obj) {
		for (final Object o : obj.iterable(scope)) {
			if (!(o instanceof ILocation)) { return false; }
		}
		return true;
	}

	@Override
	public GamaShape getDefault() {
		return null;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public IType getKeyType() {
		return Types.STRING;
	}

	//
	// @Override
	// public boolean hasContents() {
	// return true;
	// }

	@Override
	public boolean isFixedLength() {
		return false;
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

	/**
	 * Builds a (cleansed) polygon from a list of points. The input points must be valid to create a linear ring (first
	 * point and last point are duplicated). It is the responsibility of the caller to assure the validity of the input
	 * parameter. Update: the coordinate sequence is now validated before creating the polygon, and any necessary point
	 * is added.
	 *
	 * @param points
	 * @return
	 */
	public static IShape buildPolygon(final List<? extends IShape> points) {
		final CoordinateSequenceFactory fact = GamaGeometryFactory.COORDINATES_FACTORY;
		final int size = points.size();
		// AD 12/05/13 The dimensions of the points to create have been changed
		// to 3, otherwise the z coordinates could
		// be lost when copying this geometry
		CoordinateSequence cs = fact.create(size, 3);
		for (int i = 0; i < size; i++) {
			final Coordinate p = (GamaPoint) points.get(i).getLocation();
			cs.setOrdinate(i, 0, p.x);
			cs.setOrdinate(i, 1, p.y);
			cs.setOrdinate(i, 2, p.z);
		}
		cs = CoordinateSequences.ensureValidRing(fact, cs);
		final LinearRing geom = GeometryUtils.GEOMETRY_FACTORY.createLinearRing(cs);
		final Polygon p = GeometryUtils.GEOMETRY_FACTORY.createPolygon(geom, null);
		// Commented out, see Issue 760, comment #15.
		// return new GamaShape(p.isValid() ? p.buffer(0.0) : p);
		// if ( p.isValid() ) { return new GamaShape(p.buffer(0.0)); } // Why
		// buffer (0.0) ???
		// return buildPolyline(points);
		// / ???

		return new GamaShape(p);
		// return new GamaShape(GeometryUtils.isClockWise(p) ? p :
		// GeometryUtils.changeClockWise(p));
	}

	// A.G 28/05/2015 ADDED for gamanalyser
	public static IShape buildMultiPolygon(final List<List<IShape>> lpoints) {
		final Polygon[] polys = new Polygon[lpoints.size()];
		for (int z = 0; z < lpoints.size(); z++) {
			final List<IShape> points = lpoints.get(z);
			final CoordinateSequenceFactory fact = GamaGeometryFactory.COORDINATES_FACTORY;
			final int size = points.size();
			// AD 12/05/13 The dimensions of the points to create have been
			// changed to 3, otherwise the z coordinates could
			// be lost when copying this geometry
			CoordinateSequence cs = fact.create(size, 3);
			for (int i = 0; i < size; i++) {
				final Coordinate p = (GamaPoint) points.get(i).getLocation();
				cs.setOrdinate(i, 0, p.x);
				cs.setOrdinate(i, 1, p.y);
				cs.setOrdinate(i, 2, p.z);
			}
			cs = CoordinateSequences.ensureValidRing(fact, cs);
			final LinearRing geom = GeometryUtils.GEOMETRY_FACTORY.createLinearRing(cs);
			final Polygon p = (Polygon) GeometryUtils.GEOMETRY_FACTORY.createPolygon(geom, null).convexHull();
			polys[z] = p;
		}
		final MultiPolygon m = GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(polys);

		// if ( m.isValid() ) { return new GamaShape(m.buffer(0.0)); } // Why
		// buffer (0.0) ???
		return new GamaShape(m.buffer(0.0));
	}

	public static IShape buildTriangle(final double base, final double height, final ILocation location) {
		final Coordinate[] points = new Coordinate[4];
		final double z = location == null ? 0.0 : location.getZ();
		points[0] = new GamaPoint(-base / 2.0, height / 2, z);
		points[1] = new GamaPoint(0, -height / 2, z);
		points[2] = new GamaPoint(base / 2.0, height / 2, z);
		points[3] = points[0];
		final CoordinateSequenceFactory fact = GamaGeometryFactory.COORDINATES_FACTORY;
		final CoordinateSequence cs = fact.create(points);
		final LinearRing geom = GeometryUtils.GEOMETRY_FACTORY.createLinearRing(cs);
		final Polygon p = GeometryUtils.GEOMETRY_FACTORY.createPolygon(geom, null);
		final IShape s = new GamaShape(p);
		if (location != null) {
			s.setLocation(location);
		}
		return s;
	}

	public static IShape buildTriangle(final double side_size, final ILocation location) {
		final double h = Math.sqrt(3) / 2 * side_size;
		final Coordinate[] points = new Coordinate[4];
		final double x = location == null ? 0 : location.getX();
		final double y = location == null ? 0 : location.getY();
		final double z = location == null ? 0 : location.getZ();
		points[0] = new GamaPoint(x - side_size / 2.0, y + h / 3, z);
		points[1] = new GamaPoint(x, y - 2 * h / 3, z);
		points[2] = new GamaPoint(x + side_size / 2.0, y + h / 3, z);
		points[3] = points[0];
		final CoordinateSequenceFactory fact = GamaGeometryFactory.COORDINATES_FACTORY;
		final CoordinateSequence cs = fact.create(points);
		final LinearRing geom = GeometryUtils.GEOMETRY_FACTORY.createLinearRing(cs);
		final Polygon p = GeometryUtils.GEOMETRY_FACTORY.createPolygon(geom, null);
		return new GamaShape(p);
	}

	public static IShape buildRectangle(final double width, final double height, final ILocation location) {
		final Coordinate[] points = new Coordinate[5];
		final double x = location == null ? 0 : location.getX();
		final double y = location == null ? 0 : location.getY();
		final double z = location == null ? 0 : location.getZ();
		points[4] = new GamaPoint(x - width / 2.0, y + height / 2.0, z);
		points[3] = new GamaPoint(x + width / 2.0, y + height / 2.0, z);
		points[2] = new GamaPoint(x + width / 2.0, y - height / 2.0, z);
		points[1] = new GamaPoint(x - width / 2.0, y - height / 2.0, z);
		points[0] = new GamaPoint(x - width / 2.0, y + height / 2.0, z);
		return new GamaShape(GeometryUtils.GEOMETRY_FACTORY.buildRectangle(points));
	}

	/**
	 * Builds a (cleansed) polyhedron from a list of points and a given depth. The input points must be valid to create
	 * a linear ring (first point and last point are duplicated). It is the responsible of the caller to assure the
	 * validity of the input parameter. Update: the coordinate sequence is now validated before creating the polygon,
	 * and any necessary point is added.
	 *
	 * @param points
	 * @return
	 */
	public static IShape buildPolyhedron(final List<IShape> points, final Double depth) {
		final IShape g = buildPolygon(points);
		// if (!Spatial.ThreeD.isClockwise(null, g)) {
		// g = Spatial.ThreeD.changeClockwise(null, g);
		// }
		g.setDepth(depth);
		g.setGeometricalType(POLYHEDRON);
		return g;
	}

	public static IShape buildLine(final IShape location2) {
		return buildLine(new GamaPoint(), location2);
	}

	public static IShape buildLine(final IShape location1, final IShape location2) {
		final Coordinate coordinates[] =
				{ location1 == null ? new GamaPoint(0, 0) : (GamaPoint) location1.getLocation(),
						location2 == null ? new GamaPoint(0, 0) : (GamaPoint) location2.getLocation() };
		// WARNING Circumvents a bug in JTS 1.13, where a line built between two
		// identical points would return a null
		// centroid
		if (coordinates[0].equals(coordinates[1])) { return createPoint((GamaPoint) coordinates[0]); }
		return new GamaShape(GeometryUtils.GEOMETRY_FACTORY.createLineString(coordinates));
	}

	public static IShape buildLineCylinder(final IShape location1, final IShape location2, final double radius) {
		final IShape g = buildLine(location1, location2);
		g.setDepth(radius);
		g.setGeometricalType(LINECYLINDER);
		return g;
	}

	public static IShape buildPlan(final IShape location1, final IShape location2, final Double depth) {
		final IShape g = buildLine(location1, location2);
		g.setDepth(depth);
		g.setGeometricalType(PLAN);
		return g;
	}

	public static IShape buildPolyline(final List<IShape> points) {
		final List<Coordinate> coordinates = new ArrayList<>();
		for (final IShape p : points) {
			coordinates.add((GamaPoint) p.getLocation());
		}
		return new GamaShape(GeometryUtils.GEOMETRY_FACTORY
				.createLineString(coordinates.toArray(new Coordinate[coordinates.size()])));
	}

	public static IShape buildPolylineCylinder(final List<IShape> points, final double radius) {
		final IShape g = buildPolyline(points);
		g.setDepth(radius);
		g.setGeometricalType(LINECYLINDER);
		return g;
	}

	public static IShape buildPolyplan(final List<IShape> points, final Double depth) {
		final IShape g = buildPolyline(points);
		g.setDepth(depth);
		g.setGeometricalType(POLYPLAN);
		return g;
	}

	public static GamaShape createPoint(final IShape location) {
		return new GamaShape(GeometryUtils.GEOMETRY_FACTORY
				.createPoint(location == null ? new GamaPoint(0, 0) : (GamaPoint) location.getLocation()));
	}

	public static IShape buildSquare(final double side_size, final ILocation location) {
		return buildRectangle(side_size, side_size, location);
	}

	public static IShape buildCube(final double side_size, final ILocation location) {

		final IShape g = buildRectangle(side_size, side_size, location);
		g.setDepth(side_size);
		g.setGeometricalType(CUBE);
		return g;

	}

	public static IShape buildBox(final double width, final double height, final double depth,
			final ILocation location) {
		final IShape g = buildRectangle(width, height, location);
		g.setDepth(depth);
		g.setGeometricalType(BOX);
		return g;
	}

	public static IShape buildHexagon(final double size, final double x, final double y) {
		return buildHexagon(size, new GamaPoint(x, y));
	}

	public static IShape buildHexagon(final double size, final ILocation location) {
		return buildHexagon(size, size, location);
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
		final Geometry g = GeometryUtils.GEOMETRY_FACTORY
				.createPolygon(GeometryUtils.GEOMETRY_FACTORY.createLinearRing(coords), null);
		return new GamaShape(g);

	}

	public static IShape buildCircle(final double radius, final ILocation location) {
		final Geometry geom = GeometryUtils.GEOMETRY_FACTORY
				.createPoint(location == null ? new GamaPoint(0, 0) : (GamaPoint) location);
		final Geometry g = geom.buffer(radius);
		if (location != null) {
			final Coordinate[] coordinates = g.getCoordinates();
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i].z = ((GamaPoint) location).z;
			}
		}
		return new GamaShape(g);
	}

	public static IShape buildEllipse(final double xRadius, final double yRadius, final GamaPoint location) {
		if (xRadius <= 0) {
			if (yRadius <= 0) { return new GamaShape(location); }
		}
		final GeometricShapeFactory factory = new GeometricShapeFactory(GeometryUtils.GEOMETRY_FACTORY);
		factory.setNumPoints(GamaPreferences.Displays.DISPLAY_SLICE_NUMBER.getValue()); // WARNING AD Arbitrary number.
																						// Maybe add a
		// parameter and/or preference ?
		factory.setCentre(location);
		factory.setWidth(xRadius);
		factory.setHeight(yRadius);
		final Geometry g = factory.createEllipse();
		if (location != null) {
			final Coordinate[] coordinates = g.getCoordinates();
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i].z = location.z;
			}
		}
		return new GamaShape(g);
	}

	public static IShape buildSquircle(final double xRadius, final double power, final GamaPoint location) {
		if (xRadius <= 0) { return new GamaShape(location); }
		final GeometricShapeFactory factory = new GeometricShapeFactory(GeometryUtils.GEOMETRY_FACTORY);
		factory.setNumPoints(GamaPreferences.Displays.DISPLAY_SLICE_NUMBER.getValue()); // WARNING AD Arbitrary number.
																						// Maybe add a
		// parameter and/or preference ?
		factory.setCentre(location);
		factory.setSize(xRadius);
		final Geometry g = factory.createSupercircle(power);
		if (location != null) {
			final Coordinate[] coordinates = g.getCoordinates();
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i].z = location.z;
			}
		}
		return new GamaShape(g);

	}

	/**
	 *
	 * @param xRadius
	 * @param heading
	 *            in decimal degrees
	 * @param amplitude
	 *            in decimal degrees
	 * @param filled
	 * @param location
	 * @return
	 */
	public static IShape buildArc(final double xRadius, final double heading, final double amplitude,
			final boolean filled, final GamaPoint location) {
		if (amplitude <= 0 || xRadius <= 0) { return new GamaShape(location); }
		final GeometricShapeFactory factory = new GeometricShapeFactory(GeometryUtils.GEOMETRY_FACTORY);
		factory.setNumPoints(GamaPreferences.Displays.DISPLAY_SLICE_NUMBER.getValue()); // WARNING AD Arbitrary number.
																						// Maybe add a
		// parameter and/or preference ?
		factory.setCentre(location);
		factory.setSize(xRadius);
		final double ampl = Maths.checkHeading(amplitude);

		final double angExtent = Maths.toRad * ampl;
		final double startAng = Maths.toRad * Maths.checkHeading(heading - ampl / 2);
		Geometry g;
		if (filled) {
			g = factory.createArcPolygon(startAng, angExtent);
		} else {
			g = factory.createArc(startAng, angExtent);
		}
		if (location != null) {
			final Coordinate[] coordinates = g.getCoordinates();
			for (int i = 0; i < coordinates.length; i++) {
				coordinates[i].z = location.z;
			}
		}
		return new GamaShape(g);

	}

	public static IShape buildCylinder(final double radius, final double depth, final ILocation location) {
		final IShape g = buildCircle(radius, location);
		g.setDepth(depth);
		g.setGeometricalType(CYLINDER);
		return g;
	}

	// FIXME: Be sure that a buffer on a sphere returns a sphere.
	public static IShape buildSphere(final double radius, final ILocation location) {
		final IShape g = buildCircle(radius, location);
		g.setDepth(radius);
		g.setGeometricalType(SPHERE);
		return g;
	}

	public static IShape buildCone3D(final double radius, final double depth, final ILocation location) {
		final IShape g = buildCircle(radius, location);
		g.setDepth(depth);
		g.setGeometricalType(CONE);
		return g;
	}

	public static IShape buildTeapot(final double size, final ILocation location) {
		final IShape g = buildCircle(size, location);
		g.setDepth(size);
		g.setGeometricalType(TEAPOT);
		return g;
	}

	public static IShape buildPyramid(final double side_size, final ILocation location) {
		final IShape g = buildRectangle(side_size, side_size, location);
		g.setDepth(side_size);
		g.setGeometricalType(PYRAMID);
		return g;
	}

	private static double theta = Math.tan(0.423d);

	public static IShape buildArrow(final GamaPoint head, final double size) {
		return buildArrow(new GamaPoint(), head, size, size, true);
	}

	public static IShape buildArrow(final GamaPoint tail, final GamaPoint head, final double arrowWidth,
			final double arrowLength, final boolean closed) {
		final IList points = GamaListFactory.createWithoutCasting(Types.POINT, head);
		// build the line vector
		final GamaPoint vecLine = head.minus(tail);
		// build the arrow base vector - normal to the line
		GamaPoint vecLeft = new GamaPoint(-vecLine.y, vecLine.x);
		if (vecLine.y == 0 && vecLine.x == 0) {
			vecLeft = new GamaPoint(-vecLine.z, 0, 0);
		}
		// setup length parameters
		final double fLength = vecLine.norm();
		final double th = arrowWidth / (2.0d * fLength);
		final double ta = arrowLength / (2.0d * theta * fLength);
		// find the base of the arrow
		final GamaPoint base = head.minus(vecLine.times(ta));
		// build the points on the sides of the arrow
		if (closed) {
			points.add(base.plus(vecLeft.times(th)));
		} else {
			points.add(0, base.plus(vecLeft.times(th)));
		}
		points.add(base.minus(vecLeft.times(th)));
		return closed ? buildPolygon(points) : buildPolyline(points);
	}

	public static GamaShape geometriesToGeometry(final IScope scope, final IContainer<?, ? extends IShape> ags)
			throws GamaRuntimeException {
		if (ags == null || ags.isEmpty(scope)) { return null; }
		// final Geometry geoms[] = new Geometry[ags.length(scope)];
		final List<Geometry> geoms = new ArrayList(ags.length(scope));
		// int cpt = 0;
		boolean is_polygon = true;
		boolean is_polyline = true;
		for (final IShape ent : ags.iterable(scope)) {
			if (ent == null) {
				continue;
			}
			final Geometry geom = ent.getInnerGeometry();
			geoms.add(geom);
			if (is_polygon && !(geom instanceof Polygon)) {
				is_polygon = false;
			}
			if (is_polyline && !(geom instanceof LineString)) {
				is_polyline = false;
			}
			// cpt++;
		}
		if (geoms.size() == 1) { return new GamaShape(geoms.get(0)); }
		try {
			if (is_polygon) {
				final Geometry geom = CascadedPolygonUnion.union(geoms);
				if (geom != null && !geom.isEmpty()) { return new GamaShape(geom); }
			} else if (is_polyline) {
				final LineMerger merger = new LineMerger();
				for (final Geometry g : geoms) {
					merger.add(g);
				}
				final Collection<LineString> collection = merger.getMergedLineStrings();

				Geometry geom =
						GeometryUtils.GEOMETRY_FACTORY.createGeometryCollection(collection.toArray(new Geometry[0]));
				geom = geom.union();
				if (!geom.isEmpty()) { return new GamaShape(geom); }

			} else {
				Geometry geom = GeometryUtils.GEOMETRY_FACTORY.createGeometryCollection(geoms.toArray(new Geometry[0]));
				geom = geom.union();
				if (!geom.isEmpty()) { return new GamaShape(geom); }
			}
		} catch (final AssertionFailedException | TopologyException | IllegalArgumentException e) {
			// Geometry gs[] = new Geometry[geoms.length];
			final List<Geometry> gs = new ArrayList(geoms.size());
			for (final Geometry g : geoms) {
				gs.add(g.buffer(0.0));
			}
			try {
				final Geometry geom = CascadedPolygonUnion.union(gs);
				if (geom != null && !geom.isEmpty()) { return new GamaShape(geom); }
			} catch (final AssertionFailedException | TopologyException | IllegalArgumentException e2) {
				return null;
			}

		}
		return null;
	}

	public static GamaShape pointsToGeometry(final IScope scope, final IContainer<?, ILocation> coordinates)
			throws GamaRuntimeException {
		if (coordinates != null && !coordinates.isEmpty(scope)) {
			final List<List<ILocation>> geoSimp = GamaListFactory.create(Types.LIST.of(Types.POINT));
			// WARNING The list of points is NOT recopied (verify side effects)
			geoSimp.add(coordinates.listValue(scope, Types.NO_TYPE, false));
			final List<List<List<ILocation>>> geomG = GamaListFactory.create(Types.LIST);
			geomG.add(geoSimp);
			final Geometry geom = GeometryUtils.buildGeometryJTS(geomG);
			return new GamaShape(geom);
		}
		return null;
	}

	public static GamaShape buildLink(final IScope scope, final IShape source, final IShape target) {
		return new GamaShape(new DynamicLineString(GeometryUtils.GEOMETRY_FACTORY, source, target));
	}

	public static IShape pairToGeometry(final IScope scope, final GamaPair p) throws GamaRuntimeException {
		final IShape first = staticCast(scope, p.first(), null, false);
		if (first == null) { return null; }
		final IShape second = staticCast(scope, p.last(), null, false);
		if (second == null) { return null; }
		return buildLink(scope, first, second);
	}

	public static IShape buildMultiGeometry(final IList<IShape> shapes) {
		if (shapes.size() == 0) { return null; }
		if (shapes.size() == 1) { return shapes.get(0); }
		final Geometry geom = GeometryUtils.buildGeometryCollection(shapes);
		if (geom == null) { return null; }
		return new GamaShape(geom);
	}

	public static IShape buildMultiGeometry(final IShape... shapes) {
		try (final Collector.AsList<IShape> list = Collector.getList()) {
			for (final IShape shape : shapes) {
				if (shape != null) {
					list.add(shape);
				}
			}
			return buildMultiGeometry(list.items());
		}
	}

	public static IShape buildCross(final Double xRadius, final Double width, final GamaPoint location) {
		if (xRadius <= 0) { return new GamaShape(location); }
		final double val = xRadius / Math.sqrt(2);
		IShape line1 = GamaGeometryType.buildLine(new GamaPoint(location.x - val, location.y - val),
				new GamaPoint(location.x + val, location.y + val));
		IShape line2 = GamaGeometryType.buildLine(new GamaPoint(location.x - val, location.y + val),
				new GamaPoint(location.x + val, location.y - val));
		if (width != null && width > 0) {
			line1 = Spatial.Transformations.enlarged_by(null, line1, width);
			line2 = Spatial.Transformations.enlarged_by(null, line2, width);
		}

		return Spatial.Operators.union(null, line1, line2);
	}

	// /////////////////////// 3D Shape (Not yet implemented in 3D (e.g a Sphere
	// is displayed as a
	// sphere but is a JTS circle) /////////////////////////////

}
