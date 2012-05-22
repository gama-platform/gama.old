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
 * - Benoï¿½t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

/**
 * Written by drogoul Modified on 10 dï¿½c. 2010
 * 
 * All the spatial operators available in GAML. Regrouped by types of operators.
 * 
 */
public abstract class Spatial {

	/**
	 * The class Common.
	 * 
	 * @author drogoul
	 * @since 29 nov. 2011
	 * 
	 */

	final static PointLocator pl = new PointLocator();

	public static class Common extends Object {}

	public static abstract class Creation {

		@operator("circle")
		@doc(
				value = "A circle geometry which radius is equal to the operand.",
				specialCases = {"returns a point if the operand is lower or equal to 0."},
				comment =  "the centre of the circle is by default the location of the current agent in which has been called this operator.",
				examples = {"circle(10) -> returns a geometry as a circle of radius 10."},
				see = {"around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle"})
		public static IShape opCircle(final IScope scope, final Double radius) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCircle(radius, location);
		}

		@operator("cone")
		@doc(
				value = "A cone geometry which min and max angles are given by the operands.",
				specialCases = {"returns nil if the operand is nil."},
				comment =  "the centre of the cone is by default the location of the current agent in which has been called this operator.",
				examples = {"cone({0, 45}) -> returns a geometry as a cone with min angle is 0 and max angle is 45."},
				see = {"around", "circle", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle"})
		public static IShape opCone(final IScope scope, final GamaPoint p) {
			if ( p == null ) { return null; }

			int min_angle = Maths.checkHeading((int) p.x);
			int max_angle = Maths.checkHeading((int) p.y);
			IAgent a = scope.getAgentScope();
			ILocation origin = a.getLocation() == null ? new GamaPoint(0, 0) : a.getLocation();
			double originx = origin.getX();
			double originy = origin.getY();
			double worldWidth = a.getTopology().getWidth() - originx;
			double worldHeight = a.getTopology().getHeight() - originy;

			double min_point_x = originx + Maths.cos(min_angle) * worldWidth;
			double min_point_y = originy + Maths.sin(min_angle) * worldHeight;
			ILocation minPoint = new GamaPoint(min_point_x, min_point_y);

			double max_point_x = originx + Maths.cos(max_angle) * worldWidth;
			double max_point_y = originy + Maths.sin(max_angle) * worldHeight;
			ILocation maxPoint = new GamaPoint(max_point_x, max_point_y);

			return opPolygon(GamaList.with(origin, minPoint, maxPoint));
		}

		@operator("square")
		@doc(
				value = "A square geometry which side size is equal to the operand.",
				specialCases = {"returns nil if the operand is nil."},
				comment =  "the centre of the square is by default the location of the current agent in which has been called this operator.",
				examples = {"square(10) -> returns a geometry as a square of side size 10."},
				see = {"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "triangle"})
		public static IShape opSquare(final IScope scope, final Double side_size) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSquare(side_size, location);
		}

		@operator("rectangle")
		@doc(
				value = "A rectangle geometry which side sizes are given by the operands.",
				specialCases = {"returns nil if the operand is nil."},
				comment =  "the centre of the rectangle is by default the location of the current agent in which has been called this operator.",
				examples = {"rectangle({10, 5}) -> returns a geometry as a rectangle with width = 10 and heigh = 5."},
				see = {"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "square", "triangle"})
		public static IShape opRect(final IScope scope, final GamaPoint p) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(p.x, p.y, location);
		}

		@operator("triangle")
		@doc(
				value = "A triangle geometry which side size is given by the operand.",
				specialCases = {"returns nil if the operand is nil."},
				comment =  "the centre of the triangle is by default the location of the current agent in which has been called this operator.",
				examples = {"triangle(5) -> returns a geometry as a triangle with side_size = 5."},
				see = {"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square"})
		public static IShape opTriangle(final IScope scope, final Double side_size) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildTriangle(side_size, location);
		}

		@operator({ "polygon" })
		@doc(
				value = "A polygon geometry from the given list of points.",
				specialCases = {"if the operand is nil, returns the point geometry {0,0}; if the operand is composed of a single point, returns a point geometry; if the operand is composed of 2 points, returns a polyline geometry."},
				examples = {"polygon([{0,0}, {0,10}, {10,10}, {10,0}]) -> returns a polygon geometry composed of the 4 points."},
				see = {"around", "circle", "cone", "line", "link", "norm", "point", "polyline", "rectangle", "square", "triangle"})
		public static IShape opPolygon(final IList<GamaPoint> points) {
			if ( points == null || points.isEmpty() ) { return new GamaShape(new GamaPoint(0, 0)); }
			if ( points.size() == 1 ) { return GamaGeometryType.createPoint(points.get(0)); }
			if ( new HashSet(points).size() == 2 ) { return GamaGeometryType.buildLine(
				points.get(0), points.get(1)); }
			if ( !points.get(0).equals(points.get(points.size() - 1)) ) {
				points.add(points.get(0));
			}

			return GamaGeometryType.buildPolygon(points);
		}

		@operator({ "line", "polyline" })
		@doc(
				value = "A polyline geometry from the given list of points.",
				specialCases = {"if the operand is nil, returns the point geometry {0,0}; if the operand is composed of a single point, returns a point geometry."},
				examples = {"polyline([{0,0}, {0,10}, {10,10}, {10,0}]) -> returns a polyline geometry composed of the 4 points."},
				see = {"around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square", "triangle"})
		public static IShape opPolyline(final IList<GamaPoint> points) {
			if ( points == null || points.isEmpty() ) { return new GamaShape(new GamaPoint(0, 0)); }
			if ( points.size() == 1 ) { return GamaGeometryType.createPoint(points.get(0)); }
			if ( points.size() == 2 ) { return GamaGeometryType.buildLine(points.get(0),
				points.get(1)); }
			return GamaGeometryType.buildPolyline(points);
		}

		@operator({ "link" })
		@doc(
				value = "A link between the 2 elements of the pair.",
				specialCases = {"if the operand is null, link returns a point {0,0}; if one of the elements of the pair is a list of geometries or a species, link will consider the union of the geometries or of the geometry of each agent of the species"},
				comment =  "The geometry of the link is the intersection of the two geometries when they intersect, and a line between their centroids when they do not.",
				examples = {"link (geom1::geom2)  -> returns a link geometry between geom1 and geom2."},
				see = {"around", "circle", "cone", "line", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle"})
		public static IShape opLink(final IScope scope, final GamaPair points)
			throws GamaRuntimeException {
			if ( points == null || points.isEmpty() ) { return new GamaShape(new GamaPoint(0, 0)); }
			return GamaGeometryType.pairToGeometry(scope, points);
		}

		@operator("around")
		@doc(
				value = "A geometry resulting from the difference between a buffer around the right-operand casted in geometry at a distance left-operand (right-operand buffer left-operand) and the right-operand casted as geometry.",
				specialCases = {"returns a circle geometry of radius right-operand if the left-operand is nil"},
				examples = {"10 around circle(5) -> returns a the ring geometry between 5 and 10."},
				see = {"circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle"})
		public static IShape opFringe(final IScope scope, final Double width,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			IShape g = Cast.asGeometry(scope, toBeCastedIntoGeometry);
			if ( g == null ) { return opCircle(scope, width); }
			return Operators.opDifference(Transformations.opBuffer(g, width), g);
		}

	}

	public static abstract class Operators {

		@operator(value = { "inter", "intersection" })
		@doc(
				value = "A geometry resulting from the intersection between the two geometries",
				specialCases = {"returns false if the right-operand is nil"},
				examples = {"square(5) intersects {10,10} -> false"},
				see = {"union", "+", "-"})
		public static IShape opInter(final IShape g1, final IShape g2) {
			if ( g2 == null || g1 == null ) { return null; }
			if ( g2.isPoint() && g1.covers(g2.getLocation()) ) { return new GamaShape(g2); }
			if ( g1.isPoint() && g2.covers(g1.getLocation()) ) { return new GamaShape(g1); }
			Geometry geom = null;
			try {
				geom = g1.getInnerGeometry().intersection(g2.getInnerGeometry());
			} catch (TopologyException ex) {
				geom =
					g1.getInnerGeometry().buffer(0.0)
						.intersection(g2.getInnerGeometry().buffer(0.0));
			}
			if ( geom.isEmpty() ) { return null; }
			return new GamaShape(geom);
		}

		@operator(value = { "+", "union" })
		@doc(
				specialCases = {"if the right-operand is a point, a geometry or an agent, returns the geometry resulting from the union between both geometries"},
				examples = {"geom1 + geom2 -> a geometry corresponding to union between geom1 and geom2"})
			public static IShape opUnion(final IShape g1, final IShape g2) {
			if ( g1 == null ) {
				if ( g2 == null ) { return null; }
				return g2;
			}
			if ( g2 == null ) { return g1; }
			return new GamaShape(opUnion(g1.getInnerGeometry(), g2.getInnerGeometry()));
		}

		private static Geometry opUnion(final Geometry geom1, final Geometry geom2) {
			Geometry geom;
			try {
				geom = geom1.union(geom2);
			} catch (TopologyException e) {
				geom = geom1.buffer(0.01).union(geom2.buffer(0.01));
			}
			return geom;
		}

		@operator(value = { "union" })
		@doc(
				specialCases = {"if the right-operand is a list of points, geometries or agents, returns the geometry resulting from the union all the geometries"},
				examples = {"union([geom1, geom2, geom3]) -> a geometry corresponding to union between geom1, geom2 and geom3"})
		public static IShape opUnion(final IScope scope, final GamaList elements) {
			try {
				return Cast.asGeometry(scope, elements);
			} catch (GamaRuntimeException e) {
				return null;
			}
		}

		@operator(value = { "union" })
		@doc(
				value = "The geometry resulting from the union of all geometries of agents of the operand-species",
				examples = {"union(species1)  -> retutns the geometry resulting from the union of all of the geometries of agents of species species1."})
		public static IShape opUnion(final IScope scope, final ISpecies target) {
			try {
				return Cast.asGeometry(scope, target);
			} catch (GamaRuntimeException e) {
				return null;
			}
		}

		@operator(IKeyword.MINUS)
		@doc(
			specialCases = {"if the right-operand is a point, a geometry or an agent, returns the geometry resulting from the difference between both geometries"},
			examples = {"geom1 - geom2 -> a geometry corresponding to difference between geom1 and geom2"})
		public static IShape opDifference(final IShape g1, final IShape g2) {
			if ( g2 == null || g2.getInnerGeometry() == null ) { return g1; }
			return new GamaShape(g1.getInnerGeometry().difference(g2.getInnerGeometry()));
		}

		@operator(IKeyword.MINUS)
		@doc(
				specialCases = {"if the right-operand is a list of points, geometries or agents, returns the geometry resulting from the difference between the left-geometry and all of the right-geometries"},
				examples = {"geom1 - [geom2, geom3, geom4] -> a geometry corresponding to geom1 - (geom2 + geom3 + geom4)"})
			public static IShape opDifferenceAgents(final IShape g1, final IList<IShape> agents) {
			if ( agents == null || agents.isEmpty() ) { return g1; }
			Geometry geom1 = GeometryUtils.getFactory().createGeometry(g1.getInnerGeometry());
			for ( IShape ag : agents ) {
				if ( ag != null && ag.getInnerGeometry() != null ) {
					geom1 = geom1.difference(ag.getInnerGeometry());
				}
			}
			return new GamaShape(geom1);
		}

		@operator(IKeyword.MINUS)
		@doc(
				specialCases = {"if the right-operand is a species, returns the geometry resulting from the difference between the left-geometry and all of geometries all agents of the right-species"},
				examples = {"geom1 - speciesA -> a geometry corresponding to geom1 - (the geometry of all agents of species speciesA)"})
		public static IShape opDifferenceSpecies(final IScope scope, final IShape g1,
			final ISpecies target) throws GamaRuntimeException {
			IList agents = target.listValue(scope);
			return opDifferenceAgents(g1, agents);
		}

		@operator(value = { "add_point" })
		@doc(
				value = "A geometry resulting from the adding of a right-point (coordinate) to the right-geometry",
				examples = {"square(5) add_point {10,10} -> returns a hexagon"})
		public static IShape opAddPoint(final IShape g, final ILocation p) {
			if ( p == null ) { return g; }
			Coordinate point = (Coordinate) p;
			Geometry geometry = g.getInnerGeometry();
			Geometry geom_Tmp = null;
			int nb = geometry.getCoordinates().length;
			Coordinate[] coord = new Coordinate[nb + 1];
			if ( geometry instanceof Point || geometry instanceof MultiPoint ) {
				coord[0] = geometry.getCoordinate();
				coord[1] = point;
				geom_Tmp = GeometryUtils.getFactory().createLineString(coord);
			} else if ( geometry instanceof LineString || geometry instanceof MultiLineString ) {
				for ( int i = 0; i < nb; i++ ) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[nb] = point;
				geom_Tmp = GeometryUtils.getFactory().createLineString(coord);
			} else if ( geometry instanceof Polygon || geometry instanceof MultiPolygon ) {
				for ( int i = 0; i < nb - 1; i++ ) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[nb - 1] = point;
				coord[nb] = geometry.getCoordinates()[nb - 1];
				LinearRing ring = GeometryUtils.getFactory().createLinearRing(coord);
				geom_Tmp = GeometryUtils.getFactory().createPolygon(ring, null);
			}
			if ( geom_Tmp != null && geom_Tmp.isValid() ) { return new GamaShape(geom_Tmp);

			}
			return g;
		}

		

		@operator("masked_by")
		@doc(
			value = "A geometry representing the part of the right operand visible from the point of view of the agent using the operator while considering the obstacles defined by the left operand",
			examples = {"perception_geom masked_by obstacle_species -> returns the geometry representing the part of perception_geom visible from the agent position considering the obstacles of species obstacle_species."})
			public static IShape opMaskedBy(final IScope scope, final IShape source,
			final ISpecies targets) {
			IAgent a = scope.getAgentScope();
			ILocation location = a.getLocation();
			ITopology t = a.getTopology();
			IList<IAgent> obstacles = t.getAgentsIn(source, Different.with(), false);
			return maskedBy(source, obstacles, location);
		}
		
		@operator("masked_by")
		@doc(
				examples = {"perception_geom masked_by obstacle_list -> returns the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list."})
		public static IShape opMaskedBy(final IScope scope, final IShape source,
			final GamaList<IAgent> obstacles) {
			IAgent a = scope.getAgentScope();
			ILocation location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return maskedBy(source, obstacles, location);
		}

		private static IShape maskedBy(final IShape source, final IList<IAgent> obstacles,
			final ILocation location) {
			Geometry visiblePercept =
				GeometryUtils.getFactory().createGeometry(source.getInnerGeometry());
			if ( obstacles != null && !obstacles.isEmpty() ) {
				Envelope env = visiblePercept.getEnvelopeInternal();
				double percep_dist = Math.max(env.getHeight(), env.getWidth());
				Geometry locG =
					GeometryUtils.getFactory().createPoint(location.toCoordinate()).buffer(0.01)
						.getEnvelope();

				// PRECISION VALUE DEFINED BY DEFAULT.... MAYBE WE HAVE TO GIVE
				// THE MODELER THE POSSIBILITY TO MODIFY THIS VALUE?
				double precision = 120;

				IList<Geometry> geoms = new GamaList<Geometry>();
				Coordinate prec = new Coordinate(location.getX() + percep_dist, location.getY());
				for ( int k = 1; k <= precision; k++ ) {
					double angle = k / precision * 2 * Math.PI;
					Coordinate next = null;
					if ( k < precision ) {
						next =
							new Coordinate(location.getX() + Math.cos(angle) * percep_dist,
								location.getY() + Math.sin(angle) * percep_dist);
					} else {
						next = new Coordinate(location.getX() + percep_dist, location.getY());
					}
					Coordinate[] coordinates = new Coordinate[4];
					coordinates[0] = location.toCoordinate();
					coordinates[1] = prec;
					coordinates[2] = next;
					coordinates[3] = location.toCoordinate();
					LinearRing closeRing = GeometryUtils.getFactory().createLinearRing(coordinates);
					geoms.add(source.getInnerGeometry().intersection(
						GeometryUtils.getFactory().createPolygon(closeRing, null)));
					prec = next;
				}
				IList<Geometry> geomsVisible = new GamaList<Geometry>();
				Geometry obstaclesGeom = obstacles.get(0).getInnerGeometry();
				for ( int i = 1; i < obstacles.size(); i++ ) {
					obstaclesGeom = obstaclesGeom.union(obstacles.get(i).getInnerGeometry());
				}
				for ( Geometry geom : geoms ) {
					if ( !obstaclesGeom.intersects(geom) ) {
						geomsVisible.add(geom);
					} else {
						Geometry result = perceivedArea(geom, obstaclesGeom, locG);
						if ( result != null ) {
							geomsVisible.add(result);
						}
					}
				}
				Geometry newGeom = null;
				if ( !geomsVisible.isEmpty() ) {
					newGeom = geomsVisible.get(0);
					for ( int i = 1; i < geomsVisible.size(); i++ ) {
						try {
							newGeom = newGeom.union(geomsVisible.get(i));
						} catch (Exception e) {
							newGeom = newGeom.buffer(0.01).union(geomsVisible.get(i));
						}
					}
				}
				if ( newGeom != null ) { return new GamaShape(newGeom); }
				return null;
			}
			return new GamaShape(visiblePercept);
		}

		private static Geometry perceivedArea(final Geometry percept, final Geometry obst,
			final Geometry locG) {
			Geometry perceptReal = percept.difference(obst);
			PreparedGeometry ref = PreparedGeometryFactory.prepare(locG);
			if ( perceptReal instanceof GeometryCollection ) {
				GeometryCollection gc = (GeometryCollection) perceptReal;
				int nb = gc.getNumGeometries();
				for ( int i = 0; i < nb; i++ ) {
					if ( !ref.disjoint(gc.getGeometryN(i)) ) { return gc.getGeometryN(i); }
				}
				return null;
			} else if ( ref.disjoint(perceptReal) ) { return null; }
			return perceptReal;
		}

		@operator("split_at")
		@doc(
				value = "The two part of the left-operand lines split at the given right-operand point",
				specialCases = {"if the left-operand is a point or a polygon, returns an empty list"},		
				examples = {"polyline([{1,2},{4,6}]) split_at {7,6}  -> [polyline([{1.0;2.0},{7.0;6.0}]), polyline([{7.0;6.0},{4.0;6.0}])]."})
		public static GamaList<IShape> splitLineAt(final IShape geom, final ILocation pt) {
			GamaList<IShape> lines = new GamaList<IShape>();
			GamaList<Geometry> geoms = null;
			if ( geom.getInnerGeometry() instanceof LineString ) {
				geoms = splitLineAt((LineString) geom.getInnerGeometry(), pt);
			} else if ( geom.getInnerGeometry() instanceof MultiLineString ) {
				Point point = GeometryUtils.getFactory().createPoint((Coordinate) pt);
				Geometry geom2 = null;
				double distMin = Double.MAX_VALUE;
				MultiLineString ml = (MultiLineString) geom.getInnerGeometry();
				for ( int i = 0; i < ml.getNumGeometries(); i++ ) {
					double dist = ml.getGeometryN(i).distance(point);
					if ( dist <= distMin ) {
						geom2 = ml.getGeometryN(i);
						distMin = dist;
					}
				}
				geoms = splitLineAt((LineString) geom2, pt);
			}
			if ( geoms != null ) {
				for ( Geometry g : geoms ) {
					lines.add(new GamaShape(g));
				}
			}
			return lines;
		}

		// slit a line at a given point (cutpoint)
		public static GamaList<Geometry> splitLineAt(final LineString geom, final ILocation cutPoint) {
			Coordinate[] coords = geom.getCoordinates();
			Point pt =
				GeometryUtils.getFactory().createPoint(new GamaPoint(cutPoint.getLocation()));
			int nb = coords.length;
			int indexTarget = -1;
			double distanceT = Double.MAX_VALUE;
			for ( int i = 0; i < nb - 1; i++ ) {
				Coordinate s = coords[i];
				Coordinate t = coords[i + 1];
				Coordinate[] seg = { s, t };
				Geometry segment = GeometryUtils.getFactory().createLineString(seg);
				double distT = segment.distance(pt);
				if ( distT < distanceT ) {
					distanceT = distT;
					indexTarget = i;
				}
			}
			int nbSp = indexTarget + 2;
			Coordinate[] coords1 = new Coordinate[nbSp];
			for ( int i = 0; i <= indexTarget; i++ ) {
				coords1[i] = coords[i];
			}
			coords1[indexTarget + 1] = new GamaPoint(cutPoint.getLocation());

			nbSp = coords.length - indexTarget;
			Coordinate[] coords2 = new Coordinate[nbSp];
			coords2[0] = new GamaPoint(cutPoint.getLocation());
			int k = 1;
			for ( int i = indexTarget + 1; i < coords.length; i++ ) {
				coords2[k] = coords[i];
				k++;
			}
			GamaList<Geometry> geoms = new GamaList<Geometry>();
			geoms.add(GeometryUtils.getFactory().createLineString(coords1));
			geoms.add(GeometryUtils.getFactory().createLineString(coords2));
			return geoms;
		}
	}

	public static abstract class Transformations {

		@operator("convex_hull")
		@doc(
				value = "A geometry correspondong to the convex hull of the operand.",
				examples = {"convex_hull(self) -> returns the convex hull of the geometry of the agent applying the operator"})
		public static IShape opConvexHull(final IShape g) {
			return new GamaShape(g.getInnerGeometry().convexHull());
		}

		@operator(value = { IKeyword.MULTIPLY, "scaled_by" }, priority = IPriority.PRODUCT)
		@doc(
				specialCases = {"if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) scaled by the right-hand operand coefficient"},
				examples = {"shape * 2 -> returns a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 2"})
		public static IShape opScaledBy(final IShape g, final Double coefficient) {
			return ((GamaShape) g.getGeometry()).scaledBy(coefficient);
			// return new GamaShape(GeometryUtils.homothetie(g.getInnerGeometry(), coefficient));
		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" }, priority = IPriority.ADDITION)
		@doc(
				specialCases = {"if the left-hand operand is a geometry and the rigth-hand operand a map (with [distance::float, quadrantSegments:: int (the number of line segments used to represent a quadrant of a circle), endCapStyle::int (1: (default) a semi-circle, 2: a straight line perpendicular to the end segment, 3: a half-square)]), returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged considering the right-hand operand parameters"},
				examples = {"shape + [distance::5.0, quadrantSegments::4, endCapStyle:: 2] -> returns a geometry corresponding to the geometry of the agent applying the operator enlarged by a distance of 5, with 4 segments to represent a quadrant of a circle and a straight line perpendicular to the end segment"})
		public static IShape opBuffer(final IShape g, final GamaMap parameters) {
			Double distance = (Double) parameters.get("distance");
			Integer quadrantSegments = (Integer) parameters.get("quadrantSegments");
			Integer endCapStyle = (Integer) parameters.get("endCapStyle");

			return new GamaShape(g.getInnerGeometry().buffer(distance, quadrantSegments,
				endCapStyle));
		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" }, priority = IPriority.ADDITION)
		@doc(
				specialCases = {"if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged by the right-hand operand distance"},
				examples = {"shape + 5 -> returns a geometry corresponding to the geometry of the agent applying the operator enlarged by a distance of 5"})
		public static IShape opBuffer(final IShape g, final Double size) {
			return new GamaShape(g.getInnerGeometry().buffer(size));
		}

		@operator(value = { "-", "reduced_by" }, priority = IPriority.ADDITION)
		@doc(
				specialCases = {"if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) reduced by the right-hand operand distance"},
				examples = {"shape - 5 -> returns a geometry corresponding to the geometry of the agent applying the operator reduced by a distance of 5"})
		public static IShape opNegativeBuffer(final IShape g, final Double size) {
			return opBuffer(g, -size);
		}

		// @operator(value = "as_matrix", content_type = IType.INT)
		// // TODO revoir cet opï¿½rateur qui ne semble plus correspondre ï¿½ rien...
		// public static IMatrix asMatrix(final IScope scope, final GamaGeometry gg,
		// final Double squareSize) {
		// if ( gg == null ) { return null; }
		// Geometry geom = gg.getInnerGeometry();
		// double size = squareSize == null ? 1 : squareSize.doubleValue();
		// ITopology env = scope.getAgentScope().getTopology();
		// GamaIntMatrix matrix =
		// GeometricFunctions.discretisationGrid(geom, size, env.getWidth(), env.getHeight());
		// matrix.setCellSize(size);
		// return matrix;
		// }

		/**
		 * @throws GamaRuntimeException
		 *             Apply a rotation (of a given angle) to the agent geometry
		 * 
		 * @param args : angle -> double, degree
		 * @return the prim CommandStatus
		 */
		@operator("rotated_by")
		@doc(
				value = "A geometry resulting from the application of a rotation by the right-hand operand angle (degree) to the left-hand operand (geometry, agent, point)",
				examples = {"self rotated_by 45 -> returns the geometry resulting from a 45¡ rotation to the geometry of the agent applying the operator."},
				see = {"transformed_by", "translated_by"})
		public static IShape primRotation(final IShape g1, final Double angle) {
			return ((GamaShape) g1.getGeometry()).rotatedBy(Math.toRadians(angle));
		}

		@operator("rotated_by")
		@doc(
				comment = "the right-hand operand can be a float or a int")
		public static IShape primRotation(final IShape g1, final Integer angle) {
			return ((GamaShape) g1.getGeometry()).rotatedBy(angle);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a affinite operation (of a given coefficient and angle)to the agent
		 *             geometry. Angle
		 *             is given by the point.x ; Coefficient by the point.y
		 * 
		 * @param args : coefficient -> double; angle -> double, rad
		 * @return the prim CommandStatus
		 */
		@operator("transformed_by")
		@doc(
				value = "A geometry resulting from the application of a rotation and a translation (rigth-operand : point {angle(degree), distance} of the left-hand operand (geometry, agent, point)",
				examples = {"self transformed_by {45, 20} -> returns the geometry resulting from 45¡ rotation and 10m translation of the geometry of the agent applying the operator."},
				see = {"rotated_by", "translated_by"})
		public static IShape primAffinite(final IShape g, final GamaPoint p) {
			return opScaledBy(primRotation(g, p.x), p.y);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a translation operation (vector (dx, dy)) to the agent geometry
		 * 
		 * @param args : dx -> double; dy -> double
		 * @return the prim CommandStatus
		 */
		@operator("translated_by")
		@doc(
				value = "A geometry resulting from the application of a translation by the right-hand operand distance to the left-hand operand (geometry, agent, point)",
				examples = {"self translated_by 45 -> returns the geometry resulting from a 10m translation to the geometry of the agent applying the operator."},
				see = {"rotated_by", "transformed_by"})
		public static IShape primTranslationBy(final IShape g, final GamaPoint p)
			throws GamaRuntimeException {
			return primTranslationTo(g,
				msi.gaml.operators.Points.add((GamaPoint) g.getLocation(), p));
		}

		@operator(value = { "at_location", "translated_to" })
		@doc(
				value = "A geometry resulting from the tran of a translation to the right-hand operand point of the left-hand operand (geometry, agent, point)",
				examples = {"self at_location {10, 20} -> returns the geometry resulting from a translation to the location {10, 20} of the geometry of the agent applying the operator."})
		public static IShape primTranslationTo(final IShape g, final ILocation p)
			throws GamaRuntimeException {
			GamaShape newShape = (GamaShape) g.copy();
			newShape.setLocation(p);
			return newShape;
		}

		@operator(value = { "without_holes", "solid" })
		@doc(
				value = "A geometry corresponding to the operand geometry (geometry, agent, point) without its holes",
				examples = {"solid(self) -> returns the geometry corresponding to the geometry of the agent applying the operator without its holes."})
		public static IShape opWithoutHoles(final IShape g) {
			Geometry geom = g.getInnerGeometry();
			Geometry result = geom;

			if ( geom instanceof Polygon ) {
				result =
					GeometryUtils.getFactory().createPolygon(
						GeometryUtils.getFactory().createLinearRing(
							((Polygon) geom).getExteriorRing().getCoordinates()), null);
			} else if ( geom instanceof MultiPolygon ) {
				MultiPolygon mp = (MultiPolygon) geom;
				Polygon[] polys = new Polygon[mp.getNumGeometries()];
				for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
					Polygon p = (Polygon) mp.getGeometryN(i);
					polys[i] =
						GeometryUtils.getFactory().createPolygon(
							GeometryUtils.getFactory().createLinearRing(
								p.getExteriorRing().getCoordinates()), null);
				}
				result = GeometryUtils.getFactory().createMultiPolygon(polys);
			}
			return new GamaShape(result);
		}

		@operator(value = "triangulate", content_type = IType.GEOMETRY)
		@doc(
				value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand geometry (geometry, agent, point)",
				examples = {"triangulate(self) -> returns the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator."})
		public static GamaList<IShape> primTriangulate(final IShape g) {
			return GeometryUtils.triangulation(g.getInnerGeometry());
		}

		@operator(value = "as_grid", content_type = IType.GEOMETRY)
		@doc(
				value = "A matrix of square geometries (grid with 8-neighbourhood) with dimension given by the rigth-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)",
				examples = {"self as_grid {10, 5} -> returns a matrix of square geometries (grid with 8-neighbourhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator."})
		public static IMatrix opAsGrid(final IScope scope, final IShape g, final GamaPoint dim)
			throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(g, (int) dim.x, (int) dim.y, false);
		}

		@operator(value = "as_4_grid", content_type = IType.GEOMETRY)
		@doc(
				value = "A matrix of square geometries (grid with 4-neighbourhood) with dimension given by the rigth-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)",
				examples = {"self as_grid {10, 5} -> returns matrix of square geometries (grid with 4-neighbourhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator."})
		public static IMatrix opAs4Grid(final IScope scope, final IShape g, final GamaPoint dim)
			throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(g, (int) dim.x, (int) dim.y, true);
		}

		@operator(value = "split_lines", content_type = IType.GEOMETRY)
		@doc(
				value = "A list of geometries resulting after cutting the lines at their intersections.",
				examples = {"split_lines([line([{0,10}, {20,10}], line([{0,10}, {20,10}]]) -> returns a list of four polylines: line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) and line([{10,10}, {10,20}])."})
		public static IList<IShape> splitLines(final IScope scope, final IList geoms)
			throws GamaRuntimeException {
			if ( geoms.isEmpty() ) { return new GamaList<IShape>(); }
			Geometry grandMls = Cast.asGeometry(scope, geoms).getInnerGeometry();
			Point mlsPt = GeometryUtils.getFactory().createPoint(grandMls.getCoordinate());
			Geometry nodedLines = grandMls.union(mlsPt);
			GamaList<IShape> nwGeoms = new GamaList<IShape>();

			for ( int i = 0, n = nodedLines.getNumGeometries(); i < n; i++ ) {
				Geometry g = nodedLines.getGeometryN(i);
				if ( g instanceof LineString ) {
					nwGeoms.add(new GamaShape(g));
				}
			}
			return nwGeoms;
		}

		@operator(value = "skeletonize", content_type = IType.GEOMETRY)
		@doc(
				value = "A list of geometries (polylines) corresponding to the skeleton of the operand geometry (geometry, agent)",
				examples = {"skeletonize(self) -> returns the list of geometries corresponding to the skeleton of the geometry of the agent applying the operator."})
		public static GamaList<IShape> primSkeletonization(final IScope scope, final IShape g) {
			List<LineString> netw = GeometryUtils.squeletisation(scope, g.getInnerGeometry());
			GamaList<IShape> geoms = new GamaList();
			for ( LineString ls : netw ) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator("clean")
		@doc(
				value = "A geometry corresponding to the cleaning of the operand (geometry, agent, point)",
				comment = "The cleaning corresponds to a buffer with a distance of 0.0",
				examples = {"cleaning(self) -> returns the geometry resulting from the cleaning of the geometry of the agent applying the operator."})
		public static IShape opClean(final IShape g) {
			if ( g == null || g.getInnerGeometry() == null ) { return g; }
			if ( g.getInnerGeometry() instanceof Polygon ) { return new GamaShape(g
				.getInnerGeometry().buffer(0.0)); }
			if ( g.getInnerGeometry() instanceof MultiPolygon ) {
				MultiPolygon mp = (MultiPolygon) g.getInnerGeometry();
				int nb = mp.getNumGeometries();
				Polygon[] polys = new Polygon[nb];
				for ( int i = 0; i < nb; i++ ) {
					polys[i] = (Polygon) mp.getGeometryN(i).buffer(0.0);
				}
				return new GamaShape(GeometryUtils.getFactory().createMultiPolygon(polys));
			}
			return new GamaShape(g.getInnerGeometry());
		}

		/**
		 * Simplification of a geometry (Douglas-Peuker algorithm)
		 */

		@operator("simplification")
		@doc(
				value = "A geometry corresponding to the simplification of the operand (geometry, agent, point) considering a tolerance distance.",
				comment = "The algorithm used for the simplification is Douglas-Peucker",
				examples = {"self simplification 0.1 -> returns the geometry resulting from the application of the Douglas-Peuker algorithm on the geometry of the agent applying the operator with a tolerance distance of 0.1."})
		public static IShape opSimplication(final IShape g1, final Double distanceTolerance) {
			if ( g1 == null || g1.getInnerGeometry() == null ||
				g1.getInnerGeometry() instanceof Point ||
				g1.getInnerGeometry() instanceof MultiPoint ) { return g1; }
			return new GamaShape(DouglasPeuckerSimplifier.simplify(g1.getInnerGeometry(),
				distanceTolerance));
		}

	}

	public static abstract class Relations {

		@operator(value = { "towards", "direction_to" })
		@doc(
				value = "The direction (in degree) between the two geometries (geometries, agents, points) considering the topology of the agent applying the operator.",
				examples = {"ag1 towards ag2 -> the direction between ag1 and ag2 and ag3 considering the topology of the agent applying the operator"},
				see = {"distance_between","distance_to", "direction_between", "path_between", "path_to"})
		public static Integer opTowards(final IScope scope, final IShape agent, final IShape target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator("distance_between")
		@doc(
				value = "A distance between a list of geometries (geometries, agents, points) considering a topology.",
				examples = {"my_topology distance_between [ag1, ag2, ag3] -> the distance between ag1, ag2 and ag3 considering the topology my_topology"},
				see = {"towards", "direction_to","distance_to", "direction_between", "path_between", "path_to"})
		public static Double opDistanceBetween(final IScope scope, final ITopology t,
			final IContainer<?, IShape> geometries) {
			int size = geometries.length();
			if ( size == 0 || size == 1 ) { return 0d; }
			IShape previous = null;
			Double distance = 0d;
			for ( IShape obj : geometries ) {
				if ( previous != null ) {
					Double d = t.distanceBetween(previous, obj);
					if ( d == null ) { return null; }
					distance += d;
				}
				previous = obj;
			}
			return distance;
		}

		@operator(value = "direction_between")
		@doc(
				value = "A direction (in degree) between a list of two geometries (geometries, agents, points) considering a topology.",
				examples = {"my_topology direction_between [ag1, ag2] -> the direction between ag1 and ag2 considering the topology my_topology"},
				see = {"towards", "direction_to","distance_to", "distance_between", "path_between", "path_to"})
		public static Integer opDirectionBetween(final IScope scope, final ITopology t,
			final IContainer<?, IShape> geometries) throws GamaRuntimeException {
			int size = geometries.length();
			if ( size == 0 || size == 1 ) { return 0; }
			IShape g1 = geometries.first();
			IShape g2 = geometries.last();
			return t.directionInDegreesTo(g1, g2);
		}

		@operator(value = "path_between", content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		@doc(
				value = "A path between a list of two geometries (geometries, agents or points) considering a topology.",
				examples = {"my_topology path_between [ag1, ag2] -> A path between ag1 and ag2"},
				see = {"towards", "direction_to","distance_between", "direction_between", "path_to", "distance_to"})
		public static IPath pathBetween(final IScope scope, final ITopology graph,
			final IContainer<?, IShape> nodes) throws GamaRuntimeException {
			// TODO Assumes that all elements in nodes are vertices of the graph... Should be
			// checked

			if ( nodes.isEmpty() ) { return null; }
			int n = nodes.length();
			IShape source = nodes.first();
			if ( n == 1 ) { return new GamaPath(scope.getAgentScope().getTopology(), source,
				source, new GamaList()); }
			IShape target = nodes.last();
			if ( n == 2 ) { return graph.pathBetween(source, target); }
			GamaList<IShape> edges = new GamaList();
			IShape previous = null;
			for ( IShape gg : nodes ) {
				if ( previous != null ) {
					// TODO Take the case of ILocation
					edges.addAll(graph.pathBetween(previous, gg).getEdgeList());
				}
				previous = gg;
			}
			return new GamaPath(graph, source, target, edges);
		}

		@operator(value = "distance_to")
		@doc(
				value = "A distance between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.",
				examples = {"ag1 distance_to ag2 -> the distance between ag1 and ag2 considering the topology of the agent applying the operator"},
				see = {"towards", "direction_to","distance_between", "direction_between", "path_between", "path_to"})
		public static Double opDistanceTo(final IScope scope, final IShape source,
			final IShape target) {
			return scope.getAgentScope().getTopology().distanceBetween(source, target);
		}

		@operator(value = "distance_to")
		//No documentation because it is same same as the previous one (but optimized for points?)
		public static Double opDistanceTo(final IScope scope, final GamaPoint source,
			final GamaPoint target) {
			return scope.getAgentScope().getTopology().distanceBetween(source, target);
		}

		@operator("path_to")
		@doc(
				value = "A path between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.",
				examples = {"ag1 path_to ag2 -> the path between ag1 and ag2 considering the topology of the agent applying the operator"},
				see = {"towards", "direction_to","distance_between", "direction_between", "path_between", "distance_to"})
		public static IPath opPathTo(final IScope scope, final IShape g, final IShape g1)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(g1, g);
		}

		@operator("path_to")
		//No documentation because it is same same as the previous one (but optimized for points?)
		public static IPath opPathTo(final IScope scope, final GamaPoint g, final GamaPoint g1)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(g1, g);
		}

	}

	public static abstract class Properties {

		@operator(value = { "<->", "disjoint_from" }, priority = IPriority.COMPARATOR)
		@doc(
				value = "A boolean, equal to true if the left-geometry (or agent/point) is disjoints from the right-geometry (or agent/point).",
				specialCases = {"if one of the operand is null, returns true.", "if one operand is a point, returns false if the point is included in the geometry."},
				examples = {"polyline([{10,10},{20,20}]) disjoint_from polyline([{15,15},{25,25}]) -> false.","polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{15,15},{15,25},{25,25},{25,15}]) -> false.", "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from geometry({15,15}) -> false.","polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from geometry({25,25}) -> true.", "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{35,35},{35,45},{45,45},{45,35}]) -> true"},
				see = {"intersects", "crosses", "overlaps", "partially_overlaps", "touches"})
		public static Boolean opDisjoint(final IScope scope, final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return true; }
			if ( g1.getInnerGeometry() == null || g2.getInnerGeometry() == null ) { return true; }
			return !g1.getInnerGeometry().intersects(g2.getInnerGeometry());
		}

		/**
		 * Return true if the agent geometry overlaps the geometry of the localized entity passed in
		 * parameter
		 * 
		 * @param args : agent -> a localized entity
		 * @return the prim CommandStatus
		 */

		@operator("overlaps")
		@doc(
				value = "A boolean, equal to true if the left-geometry (or agent/point) overlaps the right-geometry (or agent/point).",
				specialCases = {"if one of the operand is null, returns false.", "if one operand is a point, returns true if the point is included in the geometry"},
				examples = {"polyline([{10,10},{20,20}]) overlaps polyline([{15,15},{25,25}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps geometry({25,25}) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{35,35},{35,45},{45,45},{45,35}]) -> false", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polyline([{10,10},{20,20}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps geometry({15,15}) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{10,20},{20,20},{20,30},{10,30}]) -> true"},
				see = { "<->", "disjoint_from", "crosses", "intersects", "partially_overlaps", "touches"})
		public static Boolean opOverlaps(final IScope scope, final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return !opDisjoint(scope, g1, g2);
		}

		/**
		 * Return true if the agent geometry partially overlaps the geometry of the localized agent
		 * passed in parameter
		 * 
		 * @param args : agent -> a localized entity
		 * @return the prim CommandStatus
		 */

		@operator("partially_overlaps")
		@doc(
				value = "A boolean, equal to true if the left-geometry (or agent/point) partially overlaps the right-geometry (or agent/point).",
				specialCases = {"if one of the operand is null, returns false."},
				comment = "if one geometry operand fully covers the other geometry operand, returns false (contrarily to the overlaps operator).",
				examples = {"polyline([{10,10},{20,20}]) partially_overlaps polyline([{15,15},{25,25}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps geometry({25,25}) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{35,35},{35,45},{45,45},{45,35}]) -> false", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polyline([{10,10},{20,20}]) -> false", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps geometry({15,15}) -> false", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]) -> false", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{10,20},{20,20},{20,30},{10,30}]) -> false"},
				see = { "<->", "disjoint_from", "crosses", "overlaps", "intersects", "touches"})
		public static Boolean opPartiallyOverlaps(final IShape g1, final IShape g) {
			if ( g == null ) { return false; }
			return g1.getInnerGeometry().overlaps(g.getInnerGeometry());
		}

		/**
		 * Return true if the agent geometry touches the geometry of the localized entity passed in
		 * parameter
		 * 
		 * @param args : agent -> a localized entity
		 * @return the prim CommandStatus
		 */
		@operator("touches")
		@doc(
				value = "A boolean, equal to true if the left-geometry (or agent/point) touches the right-geometry (or agent/point).",
				specialCases = {"if one of the operand is null, returns false."},
				comment = "returns true when the left-operand only touches the right-operand. When one geometry covers partially (or fully) the other one, it returns false.",
				examples = {"polyline([{10,10},{20,20}]) touches geometry({15,15}) -> false", "polyline([{10,10},{20,20}]) touches geometry({10,10}) -> true", "geometry({15,15}) touches geometry({15,15}) -> false", "polyline([{10,10},{20,20}]) touches polyline([{10,10},{5,5}]) -> true", "polyline([{10,10},{20,20}]) touches polyline([{5,5},{15,15}]) -> false", "polyline([{10,10},{20,20}]) touches polyline([{15,15},{25,25}]) -> false","polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{15,15},{15,25},{25,25},{25,15}]) -> false", "polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,20},{20,20},{20,30},{10,30}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,10},{0,10},{0,0},{10,0}]) -> true", "polygon([{10,10},{10,20},{20,20},{20,10}]) touches geometry({15,15}) -> false", "polygon([{10,10},{10,20},{20,20},{20,10}]) touches geometry({10,15}) -> true"},
				see = { "<->", "disjoint_from", "crosses", "overlaps", "partially_overlaps", "intersects"})
		public static Boolean opTouches(final IShape g, final IShape g2) {
			if ( g == null ) { return false; }
			return g2.getInnerGeometry().touches(g.getInnerGeometry());
		}

		/**
		 * Return true if the agent geometry crosses the geometry of the localized entity passed in
		 * parameter
		 * 
		 * @param args : agent -> a localized entity
		 * @return the prim CommandStatus
		 */

		@operator("crosses")
		@doc(
				value = "A boolean, equal to true if the left-geometry (or agent/point) crosses the right-geometry (or agent/point).",
				specialCases = {"if one of the operand is null, returns false.", "if one operand is a point, returns false."},
				examples = {"polyline([{10,10},{20,20}]) crosses polyline([{10,20},{20,10}]) -> true.", "polyline([{10,10},{20,20}]) crosses geometry({15,15}) -> false", "polyline([{0,0},{25,25}]) crosses polygon([{10,10},{10,20},{20,20},{20,10}]) -> true"},
				see = { "<->", "disjoint_from", "intersects", "overlaps", "partially_overlaps", "touches"})
		public static Boolean opCrosses(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.getInnerGeometry().crosses(g2.getInnerGeometry());
		}

		@operator("intersects")
		@doc(
				value = "A boolean, equal to true if the left-geometry (or agent/point) intersects the right-geometry (or agent/point).",
				specialCases = {"if one of the operand is null, returns false."},
				examples = {"square(5) intersects {10,10} -> false."},
				see = { "<->", "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches"})
		public static Boolean opIntersects(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.getInnerGeometry().intersects(g2.getInnerGeometry());
		}

		@operator("intersects")
		// no documentation because same same as before but optimized for points.
		public static Boolean opIntersects(final IShape g1, final GamaPoint p) {
			if ( g1 == null || p == null ) { return false; }
			return pl.intersects(p, g1.getInnerGeometry());
		}
	}

	public static abstract class Points {

		@operator(value = { "any_location_in", "any_point_in" })
		@doc(
				value = "A point inside (or touching) the operand-geometry.",
				examples = {"any_location_in(square(5)) -> a point of the square, for example : {3,4.6}."},
				see = {"closest_points_with", "farthest_point_to", "points_at"})
		public static ILocation opAnyLocationIn(final IScope scope, final IShape g) {
			ILocation p = GeometryUtils.pointInGeom(g.getInnerGeometry(), GAMA.getRandom());
			return p;
		}

		private static IList<GamaPoint> locExteriorRing(final Geometry geom, final Double distance) {
			IList<GamaPoint> locs = new GamaList<GamaPoint>();

			if ( geom instanceof Point ) {
				locs.add(new GamaPoint(geom.getCoordinate()));
			}
			if ( geom instanceof LineString ) {
				double dist_cur = 0;
				int nbSp = geom.getNumPoints();
				Coordinate[] coordsSimp = geom.getCoordinates();
				boolean same = false;
				double x_t = 0, y_t = 0, x_s = 0, y_s = 0;
				for ( int i = 0; i < nbSp - 1; i++ ) {
					if ( !same ) {
						Coordinate s = coordsSimp[i];
						Coordinate t = coordsSimp[i + 1];
						x_t = t.x;
						y_t = t.y;
						x_s = s.x;
						y_s = s.y;
					} else {
						i = i - 1;
					}
					double dist = Math.sqrt(Math.pow(x_s - x_t, 2) + Math.pow(y_s - y_t, 2));
					if ( dist_cur < dist ) {
						double ratio = dist_cur / dist;
						x_s = x_s + ratio * (x_t - x_s);
						y_s = y_s + ratio * (y_t - y_s);
						locs.add(new GamaPoint(x_s, y_s));
						dist_cur = distance;
						same = true;
					} else if ( dist_cur > dist ) {
						dist_cur = dist_cur - dist;
						same = false;
					} else {
						locs.add(new GamaPoint(x_t, y_t));
						dist_cur = distance;
						same = false;
					}
				}
			}
			if ( geom instanceof Polygon ) {
				Polygon poly = (Polygon) geom;
				locs.addAll(locExteriorRing(poly.getExteriorRing(), distance));
				for ( int i = 0; i < poly.getNumInteriorRing(); i++ ) {
					locs.addAll(locExteriorRing(poly.getInteriorRingN(i), distance));
				}
			}
			return locs;
		}

		@operator(value = { "points_at" }, content_type = IType.POINT)
		@doc(
				value = "A list of left-operand number of points located at a the right-operand distance to the agent location.",
				examples = {"3 points_at(20.0) -> returns [pt1, pt2, pt3] with pt1, pt2 and pt3 located at a distance of 20.0 to the agent location"},
				see = {"any_location_in", "any_point_in", "closest_points_with", "farthest_point_to"})
		public static GamaList opPointsAt(final IScope scope, final Integer nbLoc,
			final Double distance) {
			if ( distance == null || nbLoc == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			GamaList<ILocation> locations = new GamaList();
			ILocation loc = scope.getAgentScope().getLocation();
			double angle1 = GAMA.getRandom().between(0, 2 * Math.PI);

			for ( int i = 0; i < nbLoc; i++ ) {
				GamaPoint p =
					new GamaPoint(loc.getX() + distance *
						Math.cos(angle1 + (double) i / nbLoc * 2 * Math.PI), loc.getY() + distance *
						Math.sin(angle1 + (double) i / nbLoc * 2 * Math.PI));
				locations.add(p);
			}
			return locations;

		}

		@operator("closest_points_with")
		@doc(
				value = "A list of two closest points between the two geometries.",
				examples = {"geom1 closest_points_with(geom2) -> [pt1, pt2] with pt1 the closest point of geom1 to geom2 and pt1 the closest point of geom2 to geom1"},
				see = {"any_location_in", "any_point_in", "farthest_point_to", "points_at"})
		public static IList<GamaPoint> opClosestPointsBetween(final IShape a, final IShape b) {
			Coordinate[] coors =
				DistanceOp.nearestPoints(a.getInnerGeometry(), b.getInnerGeometry());
			return GamaList.with(new GamaPoint(coors[0]), new GamaPoint(coors[1]));
		}

		@operator("farthest_point_to")
		@doc(
				value = "the farthest point of the left-operand to the left-point.",
				examples = {"geom farthest_point_to(pt) -> the closest point of geom to pt"},
				see = {"any_location_in", "any_point_in", "closest_points_with", "points_at"})
		public static ILocation opFarthestPointTo(final IShape g, final GamaPoint p) {
			if ( g == null ) { return p.getLocation(); }
			if ( p == null ) { return g.getLocation(); }

			return getFarthestPoint(p, g);
		}

		private static ILocation getFarthestPoint(final GamaPoint pt, final IShape geom) {
			Coordinate[] cg = geom.getInnerGeometry().getCoordinates();
			if ( cg.length == 0 ) { return pt; }
			Coordinate pt_max = cg[0];
			double dist_max = pt.distance(pt_max);
			for ( int i = 1; i < cg.length; i++ ) {
				double dist = pt.distance(cg[i]);
				if ( dist > dist_max ) {
					pt_max = cg[i];
					dist_max = dist;
				}
			}
			return new GamaPoint(pt_max);
		}

		/**
		 * @throws GamaRuntimeException
		 *             determine the closest point of a geometry to another given point.
		 * 
		 * @param pt a point
		 * @param poly a polygon
		 */
		public static ILocation opClosestPointTo(final IShape pt, final IShape geom) {
			if ( pt == null ) { return null; }
			if ( geom == null ) { return pt.getLocation(); }
			Coordinate[] cp =
				new DistanceOp(geom.getInnerGeometry(), pt.getInnerGeometry()).nearestPoints();
			return new GamaPoint(cp[0]);
		}

	}

	public static abstract class Queries {

		@operator(value = "neighbours_of")
		@doc(
				value = "a list, containing all the agents located at a distance inferior or equal to 1 to the right-hand operand agent considering the left-hand operand topology.",
				examples = {"topology(self) neighbours_of self -> returns all the agents located at a distance lower or equal to 1 to the agent applying the operator considering its topology."},
				see = {"neighbours_at", "closest_to", "overlapping", "agents_overlapping" , "agents_inside", "agent_closest_to"})
		public static IList opNeighboursOf(final IScope scope, final ITopology t, final IAgent agent)
			throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return t.getNeighboursOf(agent, 1.0, Different.with());
		}

		@operator(value = "neighbours_of")
		@doc(
				specialCases = "a list, containing all the agents located at a distance inferior or equal to the right member (float) of the pair (right-hand operand) to the left member (agent, geometry or point) considering the left-hand operand topology.",
				examples = {"topology(self) neighbours_of self::10-> returns all the agents located at a distance lower or equal to 10 to the agent applying the operator considering its topology."})
		public static IList opNeighboursOf(final IScope scope, final ITopology t,
			final GamaPair pair) throws GamaRuntimeException {
			if ( pair == null ) { return GamaList.EMPTY_LIST; }
			Object a = pair.key;
			if ( a == null ) { throw new GamaRuntimeException(
				"Cannot compute neighbours of a null agent"); }
			if ( !(a instanceof IShape) ) { throw new GamaRuntimeException(
				"The operator neighbours_of expects a pair agent::float as its right member"); }
			Object d = pair.value;
			if ( !(d instanceof Number) ) { throw new GamaRuntimeException(
				"The operator neighbours_of expects a pair agent::float as its right member"); }
			if ( a instanceof ILocation ) { return t.getNeighboursOf((ILocation) a,
				Cast.asFloat(scope, d), Different.with()); }
			return t.getNeighboursOf((IShape) a, Cast.asFloat(scope, d), Different.with());
		}

		// / CHANGES AS OF 02/02/12
		// / Topologies are computed dynamically w.r.t. to the last agent mentioned
		// / For instance :
		// / cell neighbours_at 4 will "correctly" use the topology of cell (and not the topology of
		// the agent that calls this expression)

		@operator(value = "neighbours_at")
		@doc(
				value = "a list, containing all the agents located at a distance inferior or equal to the right-hand operand to the left-hand operand (geometry, agent, point).",
				comment = "The topology used to compute the neighbourhood  is the one of the left-operand if this one is an agent; otherwise the one of the agent applying the operator.",
				examples = {"(self neighbours_at (10)) -> returns all the agents located at a distance lower or equal to 10 to the agent applying the operator."},
				see = {"neighbours_of", "closest_to", "overlapping", "agents_overlapping" , "agents_inside", "agent_closest_to"})
		public static IList opNeighboursAt(final IScope scope, final IShape agent,
			final Double distance) throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			// CHANGE
			IAgent target = agent.getAgent();
			ITopology t =
				target == null ? scope.getAgentScope().getTopology() : target.getTopology();
			// ITopology t = scope.getAgentScope().getTopology();
			return t.getNeighboursOf(agent, distance, Different.with());
		}

		@operator(value = "neighbours_at")
		//no doc, because same same as before but optimized for "point".
		public static IList opNeighboursAt(final IScope scope, final GamaPoint agent,
			final Double distance) throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return scope.getAgentScope().getTopology()
				.getNeighboursOf(agent, distance, Different.with());
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		@doc(
				value = "A list of agents among the left-operand list, covered by the operand (casted as a geometry).",
				examples = {"[ag1, ag2, ag3] inside(self) -> return the agents among ag1, ag2 and ag3 that are covered by the shape of the agent applying the operator."},
				see = {"neighbours_at", "neighbours_of","closest_to", "overlapping", "agents_overlapping" , "agents_inside", "agent_closest_to"})
		public static IList<IAgent> opInside(final IScope scope,
			final IContainer<?, IShape> targets, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.list(scope, targets), true);
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		@doc(
				specialCases = {"if the left-operand is a species, return agents of the specified species."},
				examples = {"species1 inside(self) -> return the agents of species species1 that are covered by the shape of the agent applying the operator."})
		public static IList<IAgent> opInside(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			IPopulation pop = scope.getAgentScope().getPopulationFor(targets);
			if ( pop == null ) { return new GamaList(); }
			// CHANGE
			ITopology t = pop.getTopology();
			// ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.population(pop), true);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		@doc(
				value = "A list of agents a mong the left-operand list, overlapping the operand (casted as a geometry).",
				examples = {"[ag1, ag2, ag3] overlapping(self) -> return the agents among ag1, ag2 and ag3 that overlap the shape of the agent applying the operator."},
				see = {"neighbours_at", "neighbours_of","agent_closest_to", "agents_inside", "closest_to", "inside", "agents_overlapping"})
		public static IList<IAgent> opOverlapping(final IScope scope,
			final IContainer<?, IShape> targets, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.list(scope, targets), false);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		@doc(
				specialCases = {"if the left-operand is a species, return agents of the specified species."},
				examples = {"species1 overlapping(self) -> return the agents of species species1 that overlap the shape of the agent applying the operator."})
		public static IList<IAgent> opOverlapping(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			IPopulation pop = scope.getAgentScope().getPopulationFor(targets);
			if ( pop == null ) { return new GamaList(); }
			// CHANGE
			ITopology t = pop.getTopology();
			IList<IAgent> temp =
				t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), In.population(pop),
					false);
			// for ( IAgent a : temp ) {
			// if ( a.dead() ) {
			// GuiUtils.debug("Dead agent inside");
			// }
			// }
			// ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.population(pop), false);
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.LEFT_CONTENT_TYPE)
		@doc(
				value = "An agent among the left-operand list, the closest to the operand (casted as a geometry).",
				comment =  "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
				examples = {"[ag1, ag2, ag3] closest_to(self) -> return the closest agent among ag1, ag2 and ag3 to the agent applying the operator."},
				see = {"neighbours_at", "neighbours_of","neighbours_at", "neighbours_of","inside", "overlapping", "agents_overlapping" , "agents_inside", "agent_closest_to"})
		public static Object opClosestTo(final IScope scope, final IContainer<?, IShape> targets,
			final IShape source) throws GamaRuntimeException {
			if ( source instanceof ILocation ) {
				return scope.getAgentScope().getTopology()
					.getAgentClosestTo((ILocation) source, In.list(scope, targets));
			} else if ( source instanceof IShape ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IShape) source, In.list(scope, targets)); }
			throw new GamaRuntimeException(StringUtils.toGaml(source) +
				" is not a geometrical object");
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.LEFT_CONTENT_TYPE)
		@doc(
				specialCases = {"if the left-operand is a species, return an agent of the specified species."},
				examples = {"neighbours_at", "neighbours_of","species1 closest_to(self) -> return the closest agent of species species1 to the agent applying the operator."})
		public static IAgent opClosestTo(final IScope scope, final ISpecies targets,
			final IShape source) throws GamaRuntimeException {
			IPopulation pop = scope.getAgentScope().getPopulationFor(targets);
			if ( pop == null ) { return null; }
			// CHANGE
			ITopology t = pop.getTopology();
			// ITopology t = scope.getAgentScope().getTopology();
			if ( source instanceof ILocation ) {
				return t.getAgentClosestTo((ILocation) source, In.population(pop));
			} else if ( source instanceof IShape ) { return t.getAgentClosestTo((IShape) source,
				In.population(pop)); }
			throw new GamaRuntimeException(StringUtils.toGaml(source) +
				" is not a geometrical object");
		}

		@operator(value = "agent_closest_to", type = IType.NONE)
		@doc(
				value = "A agent, the closest to the operand (casted as a geometry).",
				comment =  "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
				examples = {"agent_closest_to(self) -> return the closest agent to the agent applying the operator."},
				see = {"neighbours_at", "neighbours_of","agents_inside", "agents_overlapping", "closest_to", "inside", "overlapping"})
		public static IAgent opAgentsClosestTo(final IScope scope, final Object source)
			throws GamaRuntimeException {
			if ( source instanceof ILocation ) {
				return scope.getAgentScope().getTopology()
					.getAgentClosestTo((ILocation) source, Different.with());
			} else if ( source instanceof IShape ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IShape) source, Different.with()); }
			throw new GamaRuntimeException(StringUtils.toGaml(source) +
				" is not a geometrical object");
		}

		@operator(value = "agents_inside", content_type = IType.NONE)
		@doc(
				value = "A list of agents covered by the operand (casted as a geometry).",
				examples = {"agents_inside(self) -> return the agents that are covered by the shape of the agent applying the operator."},
				see = {"agent_closest_to", "agents_overlapping", "closest_to", "inside", "overlapping"})
		public static IList<IAgent> opAgentsIn(final IScope scope,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), Different.with(),
				true);
		}

		@operator(value = "agents_overlapping", content_type = IType.NONE)
		@doc(
				value = "A list of agents overlapping the operand (casted as a geometry).",
				examples = {"agents_overlapping(self) -> return the agents that overlap the shape of the agent applying the operator."},
				see = {"neighbours_at", "neighbours_of","agent_closest_to", "agents_inside", "closest_to", "inside", "overlapping"})
		public static IList<IAgent> opOverlappingAgents(final IScope scope,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), Different.with(),
				false);
		}

	}

	public static abstract class Statistics {

		@operator(value = { "simple_clustering_by_distance" }, content_type = IType.LIST)
		@doc(
				value = "A list of agent groups clustered by distance considering a distance min between two groups.",
				comment =  "use of hierarchical clustering with Minimum for linkage criterion between two groups of agents.",
				examples = {"[ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0 -> for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]"},
				see = {"simple_clustering_by_envelope_distance"})
		public static IList simpleClusteringByDistance(final IScope scope,
			final IList<IAgent> agents, final Double distance) {
			int nb = agents.size();

			if ( nb == 0 ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			double distMin = Double.MAX_VALUE;
			Set<IList<IAgent>> minFusion = null;

			IList<IList<IAgent>> groups = new GamaList<IList<IAgent>>();
			Map<Set<IList<IAgent>>, Double> distances = new HashMap<Set<IList<IAgent>>, Double>();
			for ( IAgent ag : agents ) {
				IList<IAgent> group = new GamaList<IAgent>();
				group.add(ag);
				groups.add(group);
			}

			if ( nb == 1 ) { return groups; }
			// BY GEOMETRIES
			for ( int i = 0; i < nb - 1; i++ ) {
				IList<IAgent> g1 = groups.get(i);
				for ( int j = i + 1; j < nb; j++ ) {
					IList<IAgent> g2 = groups.get(j);
					Set<IList<IAgent>> distGp = new HashSet<IList<IAgent>>();
					distGp.add(g1);
					distGp.add(g2);
					IAgent a = g1.get(0);
					IAgent b = g2.get(0);
					Double dist = scope.getAgentScope().getTopology().distanceBetween(a, b);
					if ( dist < distance ) {
						distances.put(distGp, dist);
						if ( dist < distMin ) {
							distMin = dist;
							minFusion = distGp;
						}
					}
				}
			}
			while (distMin <= distance) {
				IList<IList<IAgent>> fusionL = new GamaList<IList<IAgent>>(minFusion);
				IList<IAgent> g1 = fusionL.get(0);
				IList<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				IList<IAgent> groupeF = new GamaList<IAgent>(g2);
				groupeF.addAll(g1);
				for ( IList<IAgent> groupe : groups ) {
					Set<IList<IAgent>> newDistGp = new HashSet<IList<IAgent>>();
					newDistGp.add(groupe);
					newDistGp.add(g1);
					double dist1 = Double.MAX_VALUE;
					if ( distances.containsKey(newDistGp) ) {
						dist1 = distances.remove(newDistGp).doubleValue();
					}
					newDistGp.remove(g1);
					newDistGp.add(g2);
					double dist2 = Double.MAX_VALUE;
					if ( distances.containsKey(newDistGp) ) {
						dist2 = distances.remove(newDistGp).doubleValue();
					}
					double dist = Math.min(dist1, dist2);
					if ( dist <= distance ) {
						newDistGp.remove(g2);
						newDistGp.add(groupeF);
						distances.put(newDistGp, Double.valueOf(dist));
					}

				}
				groups.add(groupeF);

				distMin = Double.MAX_VALUE;
				minFusion = null;
				for ( Set<IList<IAgent>> distGp : distances.keySet() ) {
					double dist = distances.get(distGp).doubleValue();
					if ( dist < distMin ) {
						minFusion = distGp;
						distMin = dist;
					}
				}
			}
			return groups;
		}

		@operator(value = { "simple_clustering_by_envelope_distance" }, content_type = IType.LIST)
		@doc(
				value = "A list of agent groups clustered by distance (considering the agent envelop) considering a distance min between two groups.",
				comment =  "use of hierarchical clustering with Minimum for linkage criterion between two groups of agents.",
				examples = {"[ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0 -> for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]"},
				see = {"simple_clustering_by_distance"})
		// CHANGER LE NOM !!!
		public static IList simpleClusteringByEnvelopeDistance(final IScope scope,
			final IList<IAgent> agents, final Double distance) {
			int nb = agents.size();

			if ( nb == 0 ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			double distMin = Double.MAX_VALUE;
			Set<IList<IAgent>> minFusion = null;

			IList<IList<IAgent>> groups = new GamaList<IList<IAgent>>();
			Map<Set<IList<IAgent>>, Double> distances = new HashMap<Set<IList<IAgent>>, Double>();
			for ( IAgent ag : agents ) {
				IList<IAgent> group = new GamaList<IAgent>();
				group.add(ag);
				groups.add(group);
			}

			if ( nb == 1 ) { return groups; }

			for ( int i = 0; i < nb - 1; i++ ) {
				IList<IAgent> g1 = groups.get(i);
				for ( int j = i + 1; j < nb; j++ ) {
					IList<IAgent> g2 = groups.get(j);
					Set<IList<IAgent>> distGp = new HashSet<IList<IAgent>>();
					distGp.add(g1);
					distGp.add(g2);

					Envelope gg1 = g1.get(0).getEnvelope();
					Envelope gg2 = g2.get(0).getEnvelope();
					double dist = gg1.distance(gg2);
					if ( dist <= distance ) {
						distances.put(distGp, dist);
						if ( dist < distMin ) {
							distMin = dist;
							minFusion = distGp;
						}
					}
				}
			}
			while (distMin <= distance) {
				IList<IList<IAgent>> fusionL = new GamaList<IList<IAgent>>(minFusion);
				IList<IAgent> g1 = fusionL.get(0);
				IList<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				IList<IAgent> groupeF = new GamaList<IAgent>(g2);
				groupeF.addAll(g1);
				for ( IList<IAgent> groupe : groups ) {
					Set<IList<IAgent>> newDistGp = new HashSet<IList<IAgent>>();
					newDistGp.add(groupe);
					newDistGp.add(g1);
					double dist1 = Double.MAX_VALUE;
					if ( distances.containsKey(newDistGp) ) {
						dist1 = distances.remove(newDistGp).doubleValue();
					}
					newDistGp.remove(g1);
					newDistGp.add(g2);
					double dist2 = Double.MAX_VALUE;
					if ( distances.containsKey(newDistGp) ) {
						dist2 = distances.remove(newDistGp).doubleValue();
					}
					double dist = Math.min(dist1, dist2);
					if ( dist <= distance ) {
						newDistGp.remove(g2);
						newDistGp.add(groupeF);
						distances.put(newDistGp, Double.valueOf(dist));
					}

				}
				groups.add(groupeF);

				distMin = Double.MAX_VALUE;
				minFusion = null;
				for ( Set<IList<IAgent>> distGp : distances.keySet() ) {
					double dist = distances.get(distGp).doubleValue();
					if ( dist < distMin ) {
						minFusion = distGp;
						distMin = dist;
					}
				}
			}
			return groups;
		}

	}

}
