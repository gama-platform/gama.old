/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.util.*;
import msi.gama.environment.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.GamaGeometryType;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gama.util.graph.GamaPath;
import msi.gama.util.matrix.*;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.algorithm.distance.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.operation.distance.*;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

/**
 * Written by drogoul Modified on 10 déc. 2010
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
	final static PointPairDistance ppd = new PointPairDistance();

	public static class Common extends Object {

		public static class Operations {

			final static GamaPoint currentPoint = new GamaPoint(0d, 0d);
			final static Point point = GamaGeometry.getFactory().createPoint(new Coordinate(0, 0));
			final static public CoordinateFilter cf = new CoordinateFilter() {

				@Override
				public void filter(final Coordinate arg0) {
					arg0.x = currentPoint.x;
					arg0.y = currentPoint.y;
				}
			};

			final Geometry cached;
			IndexedFacetDistance distance;
			PreparedGeometry prepared;

			public Operations(final GamaGeometry g1) {
				cached = g1.getInnerGeometry();
			}

			private IndexedFacetDistance distanceOp() {
				if ( distance == null ) {
					distance = new IndexedFacetDistance(cached);
				}
				return distance;
			}

			private PreparedGeometry preparedOp() {
				if ( prepared == null ) {
					prepared = PreparedGeometryFactory.prepare(cached);
				}
				return prepared;
			}

			public double getDistance(final IGeometry g2) {
				if ( g2.isPoint() ) {
					ppd.initialize();
					DistanceToPoint.computeDistance(cached, g2.getLocation(), ppd);
					return ppd.getDistance();
				}
				return distanceOp().getDistance(g2.getInnerGeometry());
			}

			public boolean covers(final IGeometry g) {
				return g.isPoint() ? pl.intersects(g.getLocation(), cached) : preparedOp().covers(
					g.getInnerGeometry());
			}

			public boolean intersects(final IGeometry g) {
				return g.isPoint() ? pl.intersects(g.getLocation(), cached) : preparedOp()
					.intersects(g.getInnerGeometry());
			}
		}
	}

	public static abstract class Creation {

		@operator("circle")
		public static GamaGeometry opCircle(final IScope scope, final Double radius) {
			GamaPoint location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaGeometry(location); }
			return GamaGeometry.buildCircle(radius, location);
		}

		@operator("cone")
		public static GamaGeometry opCone(final IScope scope, final GamaPoint p) {
			if ( p == null ) { return null; }

			int min_angle = MathUtils.checkHeading((int) p.x);
			int max_angle = MathUtils.checkHeading((int) p.y);
			IAgent a = scope.getAgentScope();
			GamaPoint origin = a.getLocation() == null ? new GamaPoint(0, 0) : a.getLocation();

			double worldWidth = a.getTopology().getWidth() - origin.x;
			double worldHeight = a.getTopology().getHeight() - origin.y;

			double min_point_x = origin.x + MathUtils.cos(min_angle) * worldWidth;
			double min_point_y = origin.y + MathUtils.sin(min_angle) * worldHeight;
			GamaPoint minPoint = new GamaPoint(min_point_x, min_point_y);

			double max_point_x = origin.x + MathUtils.cos(max_angle) * worldWidth;
			double max_point_y = origin.y + MathUtils.sin(max_angle) * worldHeight;
			GamaPoint maxPoint = new GamaPoint(max_point_x, max_point_y);

			return opPolygon(GamaList.with(origin, minPoint, maxPoint));
		}

		@operator("square")
		public static GamaGeometry opSquare(final IScope scope, final Double side_size) {
			GamaPoint location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaGeometry(location); }
			return GamaGeometry.buildSquare(side_size, location);
		}

		@operator("rectangle")
		public static GamaGeometry opRect(final IScope scope, final GamaPoint p) {
			GamaPoint location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometry.buildRectangle(p.x, p.y, location);
		}

		@operator("triangle")
		public static GamaGeometry opTriangle(final IScope scope, final Double side_size) {
			GamaPoint location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaGeometry(location); }
			return GamaGeometry.buildTriangle(side_size, location);
		}

		@operator({ "polygon" })
		public static GamaGeometry opPolygon(final List<GamaPoint> points) {
			if ( points == null || points.isEmpty() ) { return new GamaGeometry(new GamaPoint(0, 0)); }
			if ( points.size() == 1 ) { return GamaGeometry.createPoint(points.get(0)); }
			if ( new HashSet(points).size() == 2 ) { return GamaGeometry.buildLine(points.get(0),
				points.get(1)); }
			if ( !points.get(0).equals(points.get(points.size() - 1)) ) {
				points.add(points.get(0));
			}

			return GamaGeometry.buildPolygon(points);
		}

		@operator({ "line", "polyline" })
		public static GamaGeometry opPolyline(final List<GamaPoint> points) {
			if ( points == null || points.isEmpty() ) { return new GamaGeometry(new GamaPoint(0, 0)); }
			if ( points.size() == 1 ) { return GamaGeometry.createPoint(points.get(0)); }
			if ( points.size() == 2 ) { return GamaGeometry.buildLine(points.get(0), points.get(1)); }
			return GamaGeometry.buildPolyline(points);
		}

		@operator({ "link" })
		public static GamaGeometry opLink(final IScope scope, final GamaPair points)
			throws GamaRuntimeException {
			if ( points == null || points.isEmpty() ) { return new GamaGeometry(new GamaPoint(0, 0)); }
			return GamaGeometryType.pairToGeometry(scope, points);
		}

		@operator("around")
		public static GamaGeometry opFringe(final IScope scope, final Double width,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			GamaGeometry g = Casting.asGeometry(scope, toBeCastedIntoGeometry);
			if ( g == null ) { return opCircle(scope, width); }
			return Operators.opDifference(Transformations.opBuffer(g, width), g);
		}

	}

	public static abstract class Operators {

		@operator(value = { "inter", "intersection" })
		public static GamaGeometry opInter(final GamaGeometry g1, final GamaGeometry g2) {
			if ( g2 == null || g1 == null ) { return null; }
			Geometry geom = null;
			try {
				geom = g1.getInnerGeometry().intersection(g2.getInnerGeometry());
			} catch (TopologyException ex) {
				geom =
					g1.getInnerGeometry().buffer(0.0)
						.intersection(g2.getInnerGeometry().buffer(0.0));
			}
			if ( geom.isEmpty() ) { return null; }
			return new GamaGeometry(geom);
		}

		@operator(value = { "inter", "intersection" })
		public static GamaGeometry opInter(final GamaGeometry g, final GamaPoint p) {
			if ( p == null ) { return null; }
			if ( g.contains(p) ) { return new GamaGeometry(GamaGeometry.getFactory().createPoint(p)); }
			return null;
		}

		@operator(value = { "+", "union" })
		public static GamaGeometry opUnion(final GamaGeometry g1, final GamaGeometry g2) {
			if ( g1 == null ) {
				if ( g2 == null ) { return null; }
				return g2;
			}
			if ( g2 == null ) { return g1; }
			return new GamaGeometry(opUnion(g1.getInnerGeometry(), g2.getInnerGeometry()));
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
		public static GamaGeometry opUnion(final IScope scope, final GamaList elements) {
			try {
				return Cast.asGeometry(scope, elements);
			} catch (GamaRuntimeException e) {
				return null;
			}
		}

		@operator(value = { "union" })
		public static GamaGeometry opUnion(final IScope scope, final ISpecies target) {
			try {
				return Cast.asGeometry(scope, target);
			} catch (GamaRuntimeException e) {
				return null;
			}
		}

		@operator(Maths.MINUS)
		public static GamaGeometry opDifference(final GamaGeometry g1, final GamaGeometry g2) {
			if ( g2 == null || g2.getInnerGeometry() == null ) { return g1; }
			return new GamaGeometry(g1.getInnerGeometry().difference(g2.getInnerGeometry()));
		}

		@operator(Maths.MINUS)
		public static GamaGeometry opDifferenceAgents(final GamaGeometry g1,
			final GamaList<IGeometry> agents) {
			if ( agents == null || agents.isEmpty() ) { return g1; }
			Geometry geom1 = GamaGeometry.getFactory().createGeometry(g1.getInnerGeometry());
			for ( IGeometry ag : agents ) {
				if ( ag != null && ag.getInnerGeometry() != null ) {
					geom1 = geom1.difference(ag.getInnerGeometry());
				}
			}
			return new GamaGeometry(geom1);
		}

		@operator(Maths.MINUS)
		public static GamaGeometry opDifferenceSpecies(final IScope scope, final GamaGeometry g1,
			final ISpecies target) {
			GamaList agents = target.listValue(scope);
			return opDifferenceAgents(g1, agents);
		}

		@operator(Maths.PLUS)
		public static GamaGeometry opPlus(final GamaGeometry g, final GamaPoint p) {
			if ( p == null ) { return g; }
			Coordinate point = p.toCoordinate();
			Geometry geometry = g.getInnerGeometry();
			Geometry geom_Tmp = null;
			int nb = geometry.getCoordinates().length;
			Coordinate[] coord = new Coordinate[nb + 1];
			if ( geometry instanceof Point || geometry instanceof MultiPoint ) {
				coord[0] = geometry.getCoordinate();
				coord[1] = point;
				geom_Tmp = GamaGeometry.getFactory().createLineString(coord);
			} else if ( geometry instanceof LineString || geometry instanceof MultiLineString ) {
				for ( int i = 0; i < nb; i++ ) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[nb] = point;
				geom_Tmp = GamaGeometry.getFactory().createLineString(coord);
			} else if ( geometry instanceof Polygon || geometry instanceof MultiPolygon ) {
				for ( int i = 0; i < nb - 1; i++ ) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[nb - 1] = point;
				coord[nb] = geometry.getCoordinates()[nb - 1];
				LinearRing ring = GamaGeometry.getFactory().createLinearRing(coord);
				geom_Tmp = GamaGeometry.getFactory().createPolygon(ring, null);
			}
			if ( geom_Tmp != null && geom_Tmp.isValid() ) { return new GamaGeometry(geom_Tmp);

			}
			return g;
		}

		@operator("masked_by")
		public static GamaGeometry opMaskedBy(final IScope scope, final GamaGeometry source,
			final GamaList<IAgent> obstacles) {
			IAgent a = scope.getAgentScope();
			GamaPoint location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return maskedBy(source, obstacles, location);
		}

		@operator("masked_by")
		public static GamaGeometry opMaskedBy(final IScope scope, final GamaGeometry source,
			final ISpecies targets) {
			IAgent a = scope.getAgentScope();
			GamaPoint location = a.getLocation();
			ITopology t = a.getTopology();
			GamaList<IAgent> obstacles = t.getAgentsIn(source, Different.with(), false);
			return maskedBy(source, obstacles, location);
		}

		private static GamaGeometry maskedBy(final GamaGeometry source,
			final List<IAgent> obstacles, final GamaPoint location) {
			Geometry visiblePercept =
				GamaGeometry.getFactory().createGeometry(source.getInnerGeometry());
			if ( obstacles != null && !obstacles.isEmpty() ) {
				Envelope env = visiblePercept.getEnvelopeInternal();
				double percep_dist = Math.max(env.getHeight(), env.getWidth());
				Geometry locG =
					GamaGeometry.getFactory().createPoint(location.toCoordinate()).buffer(0.01)
						.getEnvelope();

				// PRECISION VALUE DEFINED BY DEFAULT.... MAYBE WE HAVE TO GIVE
				// THE MODELER THE POSSIBILITY TO MODIFY THIS VALUE?
				double precision = 120;

				List<Geometry> geoms = new GamaList<Geometry>();
				Coordinate prec = new Coordinate(location.x + percep_dist, location.y);
				for ( int k = 1; k <= precision; k++ ) {
					double angle = k / precision * 2 * Math.PI;
					Coordinate next = null;
					if ( k < precision ) {
						next =
							new Coordinate(location.x + Math.cos(angle) * percep_dist, location.y +
								Math.sin(angle) * percep_dist);
					} else {
						next = new Coordinate(location.x + percep_dist, location.y);
					}
					Coordinate[] coordinates = new Coordinate[4];
					coordinates[0] = location;
					coordinates[1] = prec;
					coordinates[2] = next;
					coordinates[3] = location;
					LinearRing closeRing = GamaGeometry.getFactory().createLinearRing(coordinates);
					geoms.add(source.getInnerGeometry().intersection(
						GamaGeometry.getFactory().createPolygon(closeRing, null)));
					prec = next;
				}
				List<Geometry> geomsVisible = new GamaList<Geometry>();
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
				if ( newGeom != null ) { return new GamaGeometry(newGeom); }
				return null;
			}
			return new GamaGeometry(visiblePercept);
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
		public static GamaList<GamaGeometry> splitLine(final GamaGeometry geom, final GamaPoint pt) {
			GamaList<GamaGeometry> lines = new GamaList<GamaGeometry>();
			GamaList<Geometry> geoms = null;
			if ( geom.getInnerGeometry() instanceof LineString ) {
				geoms = GeometricFunctions.splitLine((LineString) geom.getInnerGeometry(), pt);
			} else if ( geom.getInnerGeometry() instanceof MultiLineString ) {
				Point point = GamaGeometry.getFactory().createPoint(pt.toCoordinate());
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
				geoms = GeometricFunctions.splitLine((LineString) geom2, pt);
			}
			if ( geoms != null ) {
				for ( Geometry g : geoms ) {
					lines.add(new GamaGeometry(g));
				}
			}
			return lines;
		}
	}

	public static abstract class Transformations {

		@operator("convex_hull")
		public static GamaGeometry opConvexHull(final GamaGeometry g) {
			return new GamaGeometry(g.getInnerGeometry().convexHull());
		}

		@operator(value = { Maths.TIMES, "scaled_by" }, priority = IPriority.PRODUCT)
		public static GamaGeometry opScaledBy(final GamaGeometry g, final Double coefficient) {
			return new GamaGeometry(
				GeometricFunctions.homothetie(g.getInnerGeometry(), coefficient));
		}

		@operator(value = { Maths.PLUS, "buffer", "enlarged_by" }, priority = IPriority.ADDITION)
		public static GamaGeometry opBuffer(final GamaGeometry g, final GamaMap parameters) {
			Double distance = (Double) parameters.get("distance");
			Integer quadrantSegments = (Integer) parameters.get("quadrantSegments");
			Integer endCapStyle = (Integer) parameters.get("endCapStyle");

			return new GamaGeometry(g.getInnerGeometry().buffer(distance, quadrantSegments,
				endCapStyle));
		}

		@operator(value = { Maths.PLUS, "buffer", "enlarged_by" }, priority = IPriority.ADDITION)
		public static GamaGeometry opBuffer(final GamaGeometry g, final Double size) {
			return new GamaGeometry(g.getInnerGeometry().buffer(size));
		}

		@operator(value = { "-", "reduced_by" }, priority = IPriority.ADDITION)
		public static GamaGeometry opNegativeBuffer(final GamaGeometry g, final Double size) {
			return opBuffer(g, -size);
		}

		@operator(value = "as_matrix", content_type = IType.INT)
		// TODO revoir cet opérateur qui ne semble plus correspondre à rien...
		public static IMatrix asMatrix(final IScope scope, final GamaGeometry gg,
			final Double squareSize) {
			if ( gg == null ) { return null; }
			Geometry geom = gg.getInnerGeometry();
			double size = squareSize == null ? 1 : squareSize.doubleValue();
			ITopology env = scope.getAgentScope().getTopology();
			GamaIntMatrix matrix =
				GeometricFunctions.discretisationGrid(geom, size, env.getWidth(), env.getHeight());
			matrix.setCellSize(size);
			return matrix;
		}

		/**
		 * Apply a rotation (of a given angle) to the agent geometry
		 * 
		 * @param args : angle -> double, rad
		 * @return the prim CommandStatus
		 */
		@operator("rotated_by")
		public static GamaGeometry primRotation(final GamaGeometry g1, final Double angle) {
			return new GamaGeometry(GeometricFunctions.rotation(g1.getInnerGeometry(), angle));
		}

		@operator("rotated_by")
		public static GamaGeometry primRotation(final GamaGeometry g1, final Integer angle) {
			// angle in degrees
			return new GamaGeometry(GeometricFunctions.rotation(g1.getInnerGeometry(), angle *
				MathUtils.toRad)); // A
			// VERIFIER
		}

		/**
		 * Apply a affinite operation (of a given coefficient and angle)to the agent geometry. Angle
		 * is given by the point.x ; Coefficient by the point.y
		 * 
		 * @param args : coefficient -> double; angle -> double, rad
		 * @return the prim CommandStatus
		 */
		@operator("transformed_by")
		public static GamaGeometry primAffinite(final GamaGeometry g, final GamaPoint p) {
			double angle = p.x;
			double coefficient = p.y;
			return new GamaGeometry(GeometricFunctions.affinite(g.getInnerGeometry(), coefficient,
				angle));
		}

		/**
		 * Apply a translation operation (vector (dx, dy)) to the agent geometry
		 * 
		 * @param args : dx -> double; dy -> double
		 * @return the prim CommandStatus
		 */
		@operator("translated_by")
		public static GamaGeometry primTranslationBy(final GamaGeometry g, final GamaPoint p) {
			double dx = p.x;
			double dy = p.y;
			return new GamaGeometry(GeometricFunctions.translation(g.getInnerGeometry(), dx, dy));
		}

		@operator(value = { "at_location", "translated_to" })
		public static GamaGeometry primTranslationTo(final GamaGeometry g, final GamaPoint p) {
			return new GamaGeometry(GeometricFunctions.translation(g.getInnerGeometry(),
				p.x - g.getLocation().x, p.y - g.getLocation().y));
		}

		@operator(value = { "without_holes", "solid" })
		public static GamaGeometry opWithoutHoles(final GamaGeometry g) {
			return new GamaGeometry(GeometricFunctions.removeHoles(g.getInnerGeometry()));
		}

		@operator(value = "triangulate", content_type = IType.GEOMETRY)
		public static GamaList<GamaGeometry> primTriangulate(final GamaGeometry g) {
			List<Polygon> netw = GeometricFunctions.triangulation(g.getInnerGeometry());
			GamaList<GamaGeometry> geoms = new GamaList();
			for ( Polygon ps : netw ) {
				geoms.add(new GamaGeometry(ps));
			}
			return geoms;
		}

		@operator(value = "as_grid", content_type = IType.GEOMETRY)
		public static IMatrix opAsGrid(final IScope scope, final GamaGeometry g, final GamaPoint dim) {
			// cols, rows
			return new GamaSpatialMatrix(g, (int) dim.x, (int) dim.y, false);
		}

		@operator(value = "as_4_grid", content_type = IType.GEOMETRY)
		public static IMatrix opAs4Grid(final IScope scope, final GamaGeometry g,
			final GamaPoint dim) {
			// cols, rows
			return new GamaSpatialMatrix(g, (int) dim.x, (int) dim.y, true);
		}

		@operator(value = "split_lines", content_type = IType.GEOMETRY)
		public static GamaList<GamaGeometry> splitLines(final IScope scope, final List geoms)
			throws GamaRuntimeException {
			if ( geoms.isEmpty() ) { return new GamaList<GamaGeometry>(); }
			Geometry grandMls = Cast.asGeometry(scope, geoms).getInnerGeometry();
			Point mlsPt = GamaGeometry.getFactory().createPoint(grandMls.getCoordinate());
			Geometry nodedLines = grandMls.union(mlsPt);
			GamaList<GamaGeometry> nwGeoms = new GamaList<GamaGeometry>();

			for ( int i = 0, n = nodedLines.getNumGeometries(); i < n; i++ ) {
				Geometry g = nodedLines.getGeometryN(i);
				if ( g instanceof LineString ) {
					nwGeoms.add(new GamaGeometry(g));
				}
			}
			return nwGeoms;
		}

		@operator(value = "skeletonize", content_type = IType.GEOMETRY)
		public static GamaList<GamaGeometry> primSkeletonization(final IScope scope,
			final GamaGeometry g) {
			// java.lang.System.out.println(" g : " + g);
			List<LineString> netw = GeometricFunctions.squeletisation(scope, g.getInnerGeometry());
			GamaList<GamaGeometry> geoms = new GamaList();
			for ( LineString ls : netw ) {
				geoms.add(new GamaGeometry(ls));
			}
			return geoms;
		}

		@operator("clean")
		public static GamaGeometry opClean(final GamaGeometry g) {
			if ( g == null || g.getInnerGeometry() == null ) { return g; }
			if ( g.getInnerGeometry() instanceof Polygon ) { return new GamaGeometry(g
				.getInnerGeometry().buffer(0.0)); }
			if ( g.getInnerGeometry() instanceof MultiPolygon ) {
				MultiPolygon mp = (MultiPolygon) g.getInnerGeometry();
				int nb = mp.getNumGeometries();
				Polygon[] polys = new Polygon[nb];
				for ( int i = 0; i < nb; i++ ) {
					polys[i] = (Polygon) mp.getGeometryN(i).buffer(0.0);
				}
				return new GamaGeometry(GamaGeometry.getFactory().createMultiPolygon(polys));
			}
			return new GamaGeometry(g.getInnerGeometry());
		}

		/**
		 * Simplification of a geometry (Douglas-Peuker algorithm)
		 */

		@operator("simplification")
		public static GamaGeometry opSimplication(final GamaGeometry g1,
			final Double distanceTolerance) {
			if ( g1 == null || g1.getInnerGeometry() == null ||
				g1.getInnerGeometry() instanceof Point ||
				g1.getInnerGeometry() instanceof MultiPoint ) { return g1; }
			return new GamaGeometry(DouglasPeuckerSimplifier.simplify(g1.getInnerGeometry(),
				distanceTolerance));
		}

	}

	public static abstract class Relations {

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final IAgent agent,
			final GamaPoint target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final IAgent agent, final IAgent target) {
			if ( target.dead() ) {
				scope.setStatus(ExecutionStatus.interrupt);
				return agent.getHeading();
			}
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final IAgent agent,
			final GamaGeometry target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final GamaPoint agent,
			final GamaGeometry target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final GamaPoint agent,
			final GamaPoint target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final GamaPoint agent,
			final IAgent target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final GamaGeometry agent,
			final IAgent target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final GamaGeometry agent,
			final GamaGeometry target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator(value = { "towards", "direction_to" })
		public static Integer opTowards(final IScope scope, final GamaGeometry agent,
			final GamaPoint target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator("distance_between")
		public static Double opDistanceBetween(final IScope scope, final ITopology t,
			final IContainer<?, IGeometry> geometries) {
			int size = geometries.length();
			if ( size == 0 || size == 1 ) { return 0d; }
			IGeometry previous = null;
			Double distance = 0d;
			for ( IGeometry obj : geometries ) {
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
		public static Integer opDirectionBetween(final IScope scope, final ITopology t,
			final IContainer<?, IGeometry> geometries) throws GamaRuntimeException {
			int size = geometries.length();
			if ( size == 0 || size == 1 ) { return 0; }
			IGeometry g1 = geometries.first();
			IGeometry g2 = geometries.last();
			return t.directionInDegreesTo(g1, g2);
		}

		@operator(value = "path_between", content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static GamaPath pathBetween(final IScope scope, final ITopology graph,
			final IContainer<?, IGeometry> nodes) throws GamaRuntimeException {
			// TODO Assumes that all elements in nodes are vertices of the graph... Should be
			// checked

			if ( nodes.isEmpty() ) { return null; }
			int n = nodes.length();
			IGeometry source = nodes.first();
			if ( n == 1 ) { return new GamaPath(scope.getAgentScope().getTopology(), source,
				source, new GamaList()); }
			IGeometry target = nodes.last();
			if ( n == 2 ) { return graph.pathBetween(source, target); }
			GamaList<IGeometry> edges = new GamaList();
			IGeometry previous = null;
			for ( IGeometry gg : nodes ) {
				if ( previous != null ) {
					edges.addAll(graph.pathBetween(previous, gg).getEdgeList());
				}
				previous = gg;
			}
			return new GamaPath(graph, source, target, edges);
		}

		@operator(value = "distance_to")
		public static Double opDistanceTo(final IScope scope, final GamaPoint source,
			final GamaPoint target) {
			return scope.getAgentScope().getTopology().distanceBetween(source, target);
		}

		@operator(value = "distance_to")
		public static Double opDistanceTo(final IScope scope, final GamaPoint source,
			final IAgent target) {
			if ( target == null || target.dead() ) { return 0d; }
			return scope.getAgentScope().getTopology().distanceBetween(source, target);
		}

		@operator(value = "distance_to")
		public static Double opDistanceTo(final IScope scope, final IAgent source,
			final GamaPoint target) {
			if ( source == null ) { return 0d; }
			return scope.getAgentScope().getTopology().distanceBetween(source, target);
		}

		@operator(value = "distance_to")
		public static Double opDistanceTo(final IScope scope, final IAgent source,
			final IAgent target) {
			if ( source == null || target == null ) { return 0d; }
			return scope.getAgentScope().getTopology().distanceBetween(source, target);
		}

		@operator("distance_to")
		public static Double opDistanceTo(final IScope scope, final GamaGeometry g1,
			final GamaGeometry g2) {
			if ( g2 == null || g1 == null ) { return 0d; }
			return scope.getAgentScope().getTopology().distanceBetween(g1, g2);
		}

		@operator("distance_to")
		public static Double opDistanceTo(final IScope scope, final GamaGeometry g1,
			final GamaPoint g) {
			if ( g == null ) { return 0d; }
			return scope.getAgentScope().getTopology().distanceBetween(g1, g);
		}

		@operator("distance_to")
		public static Double opDistanceTo(final IScope scope, final GamaPoint g,
			final GamaGeometry g1) {
			if ( g == null ) { return 0d; }
			return scope.getAgentScope().getTopology().distanceBetween(g1, g);
		}

		@operator(value = "path_to")
		public static GamaPath opPathTo(final IScope scope, final GamaPoint source,
			final GamaPoint target) throws GamaRuntimeException {
			return scope.getAgentScope().getTopology().pathBetween(source, target);
		}

		@operator(value = "path_to")
		public static GamaPath opPathTo(final IScope scope, final GamaPoint source,
			final IAgent target) throws GamaRuntimeException {
			if ( target == null || target.dead() ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(source, target);
		}

		@operator(value = "path_to")
		public static GamaPath opPathTo(final IScope scope, final IAgent source,
			final GamaPoint target) throws GamaRuntimeException {
			if ( source == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(source, target);
		}

		@operator(value = "path_to")
		public static GamaPath opPathTo(final IScope scope, final IAgent source, final IAgent target)
			throws GamaRuntimeException {
			if ( source == null || target == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(source, target);
		}

		@operator("path_to")
		public static GamaPath opPathTo(final IScope scope, final GamaGeometry g1,
			final GamaGeometry g2) throws GamaRuntimeException {
			if ( g2 == null || g1 == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(g1, g2);
		}

		@operator("path_to")
		public static GamaPath opPathTo(final IScope scope, final GamaGeometry g1, final GamaPoint g)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(g1, g);
		}

		@operator("path_to")
		public static GamaPath opPathTo(final IScope scope, final GamaPoint g, final GamaGeometry g1)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(g1, g);
		}

	}

	public static abstract class Properties {

		@operator(value = { "<->", "disjoint_from" }, priority = IPriority.COMPARATOR)
		public static Boolean opDisjoint(final IScope scope, final GamaGeometry g1,
			final GamaGeometry g2) {
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
		public static Boolean opOverlaps(final IScope scope, final GamaGeometry g1,
			final GamaGeometry g2) {
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
		public static Boolean opPartiallyOverlaps(final GamaGeometry g1, final GamaGeometry g) {
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
		public static Boolean opTouches(final GamaGeometry g, final GamaGeometry g2) {
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
		public static Boolean opCrosses(final GamaGeometry g1, final GamaGeometry g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.getInnerGeometry().crosses(g2.getInnerGeometry());
		}

		@operator("intersects")
		public static Boolean opIntersects(final GamaGeometry g1, final GamaGeometry g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.getInnerGeometry().intersects(g2.getInnerGeometry());
		}

		@operator("intersects")
		public static Boolean opIntersects(final GamaGeometry g1, final GamaPoint p) {
			if ( g1 == null || p == null ) { return false; }
			return pl.intersects(p, g1.getInnerGeometry());
		}
	}

	public static abstract class Points {

		@operator(value = "next_point_to")
		public static GamaPoint opNextPointTo(final IScope scope, final GamaPoint other) {
			IAgent agent = scope.getAgentScope();
			GamaPoint source = agent.getLocation();
			if ( other == null ) { return source; }
			return other;
			// USELESS METHOD for the moment : the speed of the agent is not
			// taken into account, nor
			// is
			// the geometry on which it is moving
		}

		@operator(value = "next_point_to")
		public static GamaPoint opNextPointTo(final IScope scope, final IAgent other) {
			if ( other == null ) { return null; }
			return opNextPointTo(scope, other.getLocation());
		}

		@operator(value = { "any_location_in", "any_point_in" })
		public static GamaPoint opAnyLocationIn(final IScope scope, final GamaGeometry g) {
			GamaPoint p = GeometricFunctions.pointInGeom(g.getInnerGeometry(), GAMA.getRandom());
			return p;
		}

		@operator("contour_points_every")
		public static GamaList opPointExteriorRing(final IScope scope, final GamaGeometry g,
			final Double distance) {
			GamaList<GamaPoint> locs = new GamaList<GamaPoint>();
			Geometry geom = g.getInnerGeometry();
			if ( geom instanceof GeometryCollection ) {
				for ( int i = 0; i < geom.getNumGeometries(); i++ ) {
					locs.addAll(GeometricFunctions.locExteriorRing(geom.getGeometryN(i), distance));
				}
			} else {
				locs.addAll(GeometricFunctions.locExteriorRing(geom, distance));
			}
			return locs;
		}

		@operator(value = { "points_at" }, content_type = IType.POINT)
		public static GamaList opPointsAt(final IScope scope, final Integer nbLoc,
			final Double distance) {
			if ( distance == null || nbLoc == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			GamaList<GamaPoint> locations = new GamaList<GamaPoint>();
			GamaPoint loc = scope.getAgentScope().getLocation();
			double angle1 = GAMA.getRandom().between(0, 2 * Math.PI);

			for ( int i = 0; i < nbLoc; i++ ) {
				GamaPoint p =
					new GamaPoint(loc.x + distance *
						Math.cos(angle1 + (double) i / nbLoc * 2 * Math.PI), loc.y + distance *
						Math.sin(angle1 + (double) i / nbLoc * 2 * Math.PI));
				locations.add(p);
			}
			return locations;

		}

		@operator("closest_points_with")
		public static List<GamaPoint> opClosestPointsBetween(final GamaGeometry a,
			final GamaGeometry b) {
			Coordinate[] coors =
				DistanceOp.nearestPoints(a.getInnerGeometry(), b.getInnerGeometry());
			return GamaList.with(new GamaPoint(coors[0]), new GamaPoint(coors[1]));
		}

		@operator("closest_points_with")
		public static List<GamaPoint> opClosestPointsBetween(final GamaGeometry a, final GamaPoint p) {
			return opClosestPointsBetween(a, new GamaGeometry(p));
		}

		@operator("closest_point_to")
		public static GamaPoint opClosestPointTo(final GamaGeometry g, final GamaPoint p) {
			return opClosestPointTo(p, g);
		}

		@operator("closest_point_to")
		public static GamaPoint opClosestPointTo(final GamaGeometry g, final IAgent a) {
			return opClosestPointTo(g, a);
		}

		@operator("farthest_point_to")
		public static GamaPoint opFarthestPointTo(final GamaGeometry g, final GamaPoint p) {
			if ( g == null ) { return p; }
			if ( p == null ) { return g.getLocation(); }

			return GeometricFunctions.getFarthestPoint(p, g);
		}

		@operator("farthest_point_to")
		public static GamaPoint opFarthestPointTo(final GamaGeometry g, final IAgent a) {
			if ( a == null ) { return g.getLocation(); }
			return opFarthestPointTo(g, a.getLocation());
		}

		/**
		 * determine the closest point of a geometry to another given point.
		 * 
		 * @param pt a point
		 * @param poly a polygon
		 */
		public static GamaPoint opClosestPointTo(final IGeometry pt, final IGeometry geom) {
			if ( pt == null ) { return null; }
			if ( geom == null ) { return pt.getLocation(); }
			Coordinate[] cp =
				new DistanceOp(geom.getInnerGeometry(), pt.getInnerGeometry()).nearestPoints();
			return new GamaPoint(cp[0]);
		}
	}

	public static abstract class Queries {

		@operator(value = "neighbours_of")
		public static GamaList opNeighboursOf(final IScope scope, final ITopology t,
			final IAgent agent) throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return t.getNeighboursOf(agent, 1.0, Different.with());
		}

		@operator(value = "neighbours_of")
		public static GamaList opNeighboursOf(final IScope scope, final ITopology t,
			final GamaPair pair) throws GamaRuntimeException {
			if ( pair == null ) { return GamaList.EMPTY_LIST; }
			Object a = pair.key;
			if ( !(a instanceof IGeometry) ) { throw new GamaRuntimeException(
				"The operator neighbours_of expects a pair agent::float as its right member"); }
			Object d = pair.value;
			if ( !(d instanceof Number) ) { throw new GamaRuntimeException(
				"The operator neighbours_of expects a pair agent::float as its right member"); }
			return t.getNeighboursOf((IGeometry) a, Cast.asFloat(scope, d), Different.with());
		}

		@operator(value = "neighbours_at")
		public static GamaList opNeighboursAt(final IScope scope, final IAgent agent,
			final Double distance) throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return scope.getAgentScope().getTopology()
				.getNeighboursOf(agent, distance, Different.with());
		}

		@operator(value = "neighbours_at")
		public static GamaList opNeighboursAt(final IScope scope, final IAgent agent,
			final Integer distance) throws GamaRuntimeException {
			return opNeighboursAt(scope, agent, distance.doubleValue());
		}

		@operator(value = { "neighbours_at" })
		public static GamaList neighboursAt(final IScope scope, final GamaGeometry geom,
			final Double distance) throws GamaRuntimeException {
			return scope.getAgentScope().getTopology()
				.getNeighboursOf(geom, distance, Different.with());
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opInside(final IScope scope,
			final IContainer<?, IGeometry> targets, final IAgent source)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(source, In.list(scope, targets), true);
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opInside(final IScope scope,
			final IContainer<?, IGeometry> targets, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Casting.asGeometry(scope, toBeCastedIntoGeometry),
				In.list(scope, targets), true);
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opInside(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Casting.asGeometry(scope, toBeCastedIntoGeometry),
				In.species(targets), true);
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opInside(final IScope scope, final ISpecies targets,
			final IAgent source) {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(source, In.species(targets), true);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opOverlapping(final IScope scope,
			final IContainer<?, IGeometry> targets, final IAgent source)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(source, In.list(scope, targets), false);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opOverlapping(final IScope scope,
			final IContainer<?, IGeometry> targets, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Casting.asGeometry(scope, toBeCastedIntoGeometry),
				In.list(scope, targets), false);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opOverlapping(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Casting.asGeometry(scope, toBeCastedIntoGeometry),
				In.species(targets), false);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static List<IAgent> opOverlapping(final IScope scope, final ISpecies targets,
			final IAgent source) {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(source, In.species(targets), true);
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static Object opClosestTo(final IScope scope,
			final IContainer<?, IGeometry> targets, final Object source)
			throws GamaRuntimeException {
			if ( source instanceof IGeometry ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IGeometry) source, In.list(scope, targets)); }
			throw new GamaRuntimeException(Cast.toGaml(source) + " is not a geometrical object");
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static IAgent opClosestTo(final IScope scope, final ISpecies targets,
			final Object source) throws GamaRuntimeException {
			if ( source instanceof IGeometry ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IGeometry) source, In.species(targets)); }
			throw new GamaRuntimeException(Cast.toGaml(source) + " is not a geometrical object");
		}

		@operator(value = "agent_closest_to", type = IType.NONE)
		public static IAgent opAgentsClosestTo(final IScope scope, final Object source)
			throws GamaRuntimeException {
			if ( source instanceof IGeometry ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IGeometry) source, Different.with()); }
			throw new GamaRuntimeException(Cast.toGaml(source) + " is not a geometrical object");
		}

		@operator(value = "agents_inside", content_type = IType.NONE)
		public static List<IAgent> opAgentsIn(final IScope scope,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Casting.asGeometry(scope, toBeCastedIntoGeometry),
				Different.with(), true);
		}

		@operator(value = "agents_overlapping", content_type = IType.NONE)
		public static List<IAgent> opOverlappingAgents(final IScope scope,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Casting.asGeometry(scope, toBeCastedIntoGeometry),
				Different.with(), false);
		}

	}

	public static abstract class Statistics {

		@operator(value = { "simple_clustering_by_distance" }, content_type = IType.LIST)
		public static GamaList simpleClusteringByDistance(final IScope scope,
			final GamaList<IAgent> agents, final Double distance) {
			int nb = agents.size();

			if ( nb == 0 ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			double distMin = Double.MAX_VALUE;
			Set<List<IAgent>> minFusion = null;

			GamaList<List<IAgent>> groups = new GamaList<List<IAgent>>();
			Map<Set<List<IAgent>>, Double> distances = new HashMap<Set<List<IAgent>>, Double>();
			for ( IAgent ag : agents ) {
				List<IAgent> group = new GamaList<IAgent>();
				group.add(ag);
				groups.add(group);
			}

			if ( nb == 1 ) { return groups; }
			// BY GEOMETRIES
			for ( int i = 0; i < nb - 1; i++ ) {
				List<IAgent> g1 = groups.get(i);
				for ( int j = i + 1; j < nb; j++ ) {
					List<IAgent> g2 = groups.get(j);
					Set<List<IAgent>> distGp = new HashSet<List<IAgent>>();
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
					// Geometry gg1 = g1.get(0).getGeometry().geometry;
					// Geometry gg2 = g2.get(0).getGeometry().geometry;
					// if ( gg1.isWithinDistance(gg2, distance) ) {
					// dist = gg1.distance(gg2);
					// distances.put(distGp, dist);
					// if ( dist < distMin ) {
					// distMin = dist;
					// minFusion = distGp;
					// }
					// }
				}
			}
			while (distMin <= distance) {
				List<List<IAgent>> fusionL = new GamaList<List<IAgent>>(minFusion);
				List<IAgent> g1 = fusionL.get(0);
				List<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				List<IAgent> groupeF = new GamaList<IAgent>(g2);
				groupeF.addAll(g1);
				for ( List<IAgent> groupe : groups ) {
					Set<List<IAgent>> newDistGp = new HashSet<List<IAgent>>();
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
				for ( Set<List<IAgent>> distGp : distances.keySet() ) {
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
		// CHANGER LE NOM !!!
		public static GamaList simpleClusteringByEnvelopeDistance(final IScope scope,
			final GamaList<IAgent> agents, final Double distance) {
			int nb = agents.size();

			if ( nb == 0 ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			double distMin = Double.MAX_VALUE;
			Set<List<IAgent>> minFusion = null;

			GamaList<List<IAgent>> groups = new GamaList<List<IAgent>>();
			Map<Set<List<IAgent>>, Double> distances = new HashMap<Set<List<IAgent>>, Double>();
			for ( IAgent ag : agents ) {
				List<IAgent> group = new GamaList<IAgent>();
				group.add(ag);
				groups.add(group);
			}

			if ( nb == 1 ) { return groups; }

			for ( int i = 0; i < nb - 1; i++ ) {
				List<IAgent> g1 = groups.get(i);
				for ( int j = i + 1; j < nb; j++ ) {
					List<IAgent> g2 = groups.get(j);
					Set<List<IAgent>> distGp = new HashSet<List<IAgent>>();
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
				List<List<IAgent>> fusionL = new GamaList<List<IAgent>>(minFusion);
				List<IAgent> g1 = fusionL.get(0);
				List<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				List<IAgent> groupeF = new GamaList<IAgent>(g2);
				groupeF.addAll(g1);
				for ( List<IAgent> groupe : groups ) {
					Set<List<IAgent>> newDistGp = new HashSet<List<IAgent>>();
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
				for ( Set<List<IAgent>> distGp : distances.keySet() ) {
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
