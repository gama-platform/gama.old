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
package msi.gaml.operators;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.skills.GeometricSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;
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

	public static class Common extends Object {}

	public static abstract class Creation {

		@operator("circle")
		public static IShape opCircle(final IScope scope, final Double radius) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCircle(radius, location);
		}

		@operator("cone")
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
		public static IShape opSquare(final IScope scope, final Double side_size) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSquare(side_size, location);
		}

		@operator("rectangle")
		public static IShape opRect(final IScope scope, final GamaPoint p) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(p.x, p.y, location);
		}

		@operator("triangle")
		public static IShape opTriangle(final IScope scope, final Double side_size) {
			ILocation location;
			IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildTriangle(side_size, location);
		}

		@operator({ "polygon" })
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
		public static IShape opPolyline(final IList<GamaPoint> points) {
			if ( points == null || points.isEmpty() ) { return new GamaShape(new GamaPoint(0, 0)); }
			if ( points.size() == 1 ) { return GamaGeometryType.createPoint(points.get(0)); }
			if ( points.size() == 2 ) { return GamaGeometryType.buildLine(points.get(0),
				points.get(1)); }
			return GamaGeometryType.buildPolyline(points);
		}

		@operator({ "link" })
		public static IShape opLink(final IScope scope, final GamaPair points)
			throws GamaRuntimeException {
			if ( points == null || points.isEmpty() ) { return new GamaShape(new GamaPoint(0, 0)); }
			return GamaGeometryType.pairToGeometry(scope, points);
		}

		@operator("around")
		public static IShape opFringe(final IScope scope, final Double width,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			IShape g = Cast.asGeometry(scope, toBeCastedIntoGeometry);
			if ( g == null ) { return opCircle(scope, width); }
			return Operators.opDifference(Transformations.opBuffer(g, width), g);
		}

	}

	public static abstract class Operators {

		@operator(value = { "inter", "intersection" })
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
		public static IShape opUnion(final IScope scope, final GamaList elements) {
			try {
				return Cast.asGeometry(scope, elements);
			} catch (GamaRuntimeException e) {
				return null;
			}
		}

		@operator(value = { "union" })
		public static IShape opUnion(final IScope scope, final ISpecies target) {
			try {
				return Cast.asGeometry(scope, target);
			} catch (GamaRuntimeException e) {
				return null;
			}
		}

		@operator(IKeyword.MINUS)
		public static IShape opDifference(final IShape g1, final IShape g2) {
			if ( g2 == null || g2.getInnerGeometry() == null ) { return g1; }
			return new GamaShape(g1.getInnerGeometry().difference(g2.getInnerGeometry()));
		}

		@operator(IKeyword.MINUS)
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
		public static IShape opDifferenceSpecies(final IScope scope, final IShape g1,
			final ISpecies target) throws GamaRuntimeException {
			IList agents = target.listValue(scope);
			return opDifferenceAgents(g1, agents);
		}

		@operator(IKeyword.PLUS)
		public static IShape opPlus(final IShape g, final ILocation p) {
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
		public static IShape opMaskedBy(final IScope scope, final IShape source,
			final GamaList<IAgent> obstacles) {
			IAgent a = scope.getAgentScope();
			ILocation location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return maskedBy(source, obstacles, location);
		}

		@operator("masked_by")
		public static IShape opMaskedBy(final IScope scope, final IShape source,
			final ISpecies targets) {
			IAgent a = scope.getAgentScope();
			ILocation location = a.getLocation();
			ITopology t = a.getTopology();
			IList<IAgent> obstacles = t.getAgentsIn(source, Different.with(), false);
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
		public static GamaList<IShape> splitLine(final IShape geom, final ILocation pt) {
			GamaList<IShape> lines = new GamaList<IShape>();
			GamaList<Geometry> geoms = null;
			if ( geom.getInnerGeometry() instanceof LineString ) {
				geoms = splitLine((LineString) geom.getInnerGeometry(), pt);
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
				geoms = splitLine((LineString) geom2, pt);
			}
			if ( geoms != null ) {
				for ( Geometry g : geoms ) {
					lines.add(new GamaShape(g));
				}
			}
			return lines;
		}

		// slit a line at a given point (cutpoint)
		public static GamaList<Geometry> splitLine(final LineString geom, final ILocation cutPoint) {
			Coordinate[] coords = geom.getCoordinates();
			Point pt = GeometryUtils.getFactory().createPoint((Coordinate) cutPoint);
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
			coords1[indexTarget + 1] = (Coordinate) cutPoint;

			nbSp = coords.length - indexTarget;
			Coordinate[] coords2 = new Coordinate[nbSp];
			coords2[0] = (Coordinate) cutPoint;
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
		public static IShape opConvexHull(final IShape g) {
			return new GamaShape(g.getInnerGeometry().convexHull());
		}

		@operator(value = { IKeyword.MULTIPLY, "scaled_by" }, priority = IPriority.PRODUCT)
		public static IShape opScaledBy(final IShape g, final Double coefficient) {
			return ((GamaShape) g.getGeometry()).scaledBy(coefficient);
			// return new GamaShape(GeometryUtils.homothetie(g.getInnerGeometry(), coefficient));
		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" }, priority = IPriority.ADDITION)
		public static IShape opBuffer(final IShape g, final GamaMap parameters) {
			Double distance = (Double) parameters.get("distance");
			Integer quadrantSegments = (Integer) parameters.get("quadrantSegments");
			Integer endCapStyle = (Integer) parameters.get("endCapStyle");

			return new GamaShape(g.getInnerGeometry().buffer(distance, quadrantSegments,
				endCapStyle));
		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" }, priority = IPriority.ADDITION)
		public static IShape opBuffer(final IShape g, final Double size) {
			return new GamaShape(g.getInnerGeometry().buffer(size));
		}

		@operator(value = { "-", "reduced_by" }, priority = IPriority.ADDITION)
		public static IShape opNegativeBuffer(final IShape g, final Double size) {
			return opBuffer(g, -size);
		}

		// @operator(value = "as_matrix", content_type = IType.INT)
		// // TODO revoir cet opérateur qui ne semble plus correspondre à rien...
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
		 * Apply a rotation (of a given angle) to the agent geometry
		 * 
		 * @param args : angle -> double, rad
		 * @return the prim CommandStatus
		 */
		@operator("rotated_by")
		public static IShape primRotation(final IShape g1, final Double angle) {
			return ((GamaShape) g1.getGeometry()).rotatedBy(angle);
		}

		@operator("rotated_by")
		public static IShape primRotation(final IShape g1, final Integer angle) {
			return ((GamaShape) g1.getGeometry()).rotatedBy(angle);
		}

		/**
		 * Apply a affinite operation (of a given coefficient and angle)to the agent geometry. Angle
		 * is given by the point.x ; Coefficient by the point.y
		 * 
		 * @param args : coefficient -> double; angle -> double, rad
		 * @return the prim CommandStatus
		 */
		@operator("transformed_by")
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
		public static IShape primTranslationBy(final IShape g, final GamaPoint p)
			throws GamaRuntimeException {
			return primTranslationTo(g,
				msi.gaml.operators.Points.add((GamaPoint) g.getLocation(), p));
		}

		@operator(value = { "at_location", "translated_to" })
		public static IShape primTranslationTo(final IShape g, final ILocation p)
			throws GamaRuntimeException {
			GamaShape newShape = (GamaShape) g.copy();
			newShape.setLocation(p);
			return newShape;
		}

		@operator(value = { "without_holes", "solid" })
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
		public static GamaList<IShape> primTriangulate(final IShape g) {
			List<Polygon> netw = GeometryUtils.triangulation(g.getInnerGeometry());
			GamaList<IShape> geoms = new GamaList(netw.size());
			for ( Polygon ps : netw ) {
				geoms.add(new GamaShape(ps));
			}
			return geoms;
		}

		@operator(value = "as_grid", content_type = IType.GEOMETRY)
		public static IMatrix opAsGrid(final IScope scope, final IShape g, final GamaPoint dim)
			throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(g, (int) dim.x, (int) dim.y, false);
		}

		@operator(value = "as_4_grid", content_type = IType.GEOMETRY)
		public static IMatrix opAs4Grid(final IScope scope, final IShape g, final GamaPoint dim)
			throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(g, (int) dim.x, (int) dim.y, true);
		}

		@operator(value = "split_lines", content_type = IType.GEOMETRY)
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
		public static GamaList<IShape> primSkeletonization(final IScope scope, final IShape g) {
			// java.lang.System.out.println(" g : " + g);
			List<LineString> netw = GeometricSkill.squeletisation(scope, g.getInnerGeometry());
			GamaList<IShape> geoms = new GamaList();
			for ( LineString ls : netw ) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator("clean")
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
		public static Integer opTowards(final IScope scope, final IShape agent, final IShape target) {
			return scope.getAgentScope().getTopology().directionInDegreesTo(agent, target);
		}

		@operator("distance_between")
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
		public static Integer opDirectionBetween(final IScope scope, final ITopology t,
			final IContainer<?, IShape> geometries) throws GamaRuntimeException {
			int size = geometries.length();
			if ( size == 0 || size == 1 ) { return 0; }
			IShape g1 = geometries.first();
			IShape g2 = geometries.last();
			return t.directionInDegreesTo(g1, g2);
		}

		@operator(value = "path_between", content_type = ITypeProvider.LEFT_CONTENT_TYPE)
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
					edges.addAll(graph.pathBetween(previous, gg).getEdgeList());
				}
				previous = gg;
			}
			return new GamaPath(graph, source, target, edges);
		}

		@operator(value = "distance_to")
		public static Double opDistanceTo(final IScope scope, final IShape source,
			final IShape target) {
			return scope.getAgentScope().getTopology().distanceBetween(source, target);
		}

		@operator("path_to")
		public static IPath opPathTo(final IScope scope, final IShape g, final IShape g1)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getAgentScope().getTopology().pathBetween(g1, g);
		}

	}

	public static abstract class Properties {

		@operator(value = { "<->", "disjoint_from" }, priority = IPriority.COMPARATOR)
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
		public static Boolean opCrosses(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.getInnerGeometry().crosses(g2.getInnerGeometry());
		}

		@operator("intersects")
		public static Boolean opIntersects(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.getInnerGeometry().intersects(g2.getInnerGeometry());
		}

		@operator("intersects")
		public static Boolean opIntersects(final IShape g1, final GamaPoint p) {
			if ( g1 == null || p == null ) { return false; }
			return pl.intersects(p, g1.getInnerGeometry());
		}
	}

	public static abstract class Points {

		@operator(value = "next_point_to")
		public static ILocation opNextPointTo(final IScope scope, final ILocation other) {
			IAgent agent = scope.getAgentScope();
			ILocation source = agent.getLocation();
			if ( other == null ) { return source; }
			return other;
			// USELESS METHOD for the moment : the speed of the agent is not
			// taken into account, nor
			// is
			// the geometry on which it is moving
		}

		@operator(value = "next_point_to")
		public static ILocation opNextPointTo(final IScope scope, final IShape other) {
			if ( other == null ) { return null; }
			return opNextPointTo(scope, other.getLocation());
		}

		@operator(value = { "any_location_in", "any_point_in" })
		public static ILocation opAnyLocationIn(final IScope scope, final IShape g) {
			ILocation p = GeometryUtils.pointInGeom(g.getInnerGeometry(), GAMA.getRandom());
			return p;
		}

		@operator("contour_points_every")
		public static GamaList opPointExteriorRing(final IScope scope, final IShape g,
			final Double distance) {
			GamaList<GamaPoint> locs = new GamaList<GamaPoint>();
			Geometry geom = g.getInnerGeometry();
			if ( geom instanceof GeometryCollection ) {
				for ( int i = 0; i < geom.getNumGeometries(); i++ ) {
					locs.addAll(locExteriorRing(geom.getGeometryN(i), distance));
				}
			} else {
				locs.addAll(locExteriorRing(geom, distance));
			}
			return locs;
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
				// if ( coordsSimp[0].equals(coordsSimp[nbSp - 1]) ) {
				// nbSp--;
				// }
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
		public static IList<GamaPoint> opClosestPointsBetween(final IShape a, final IShape b) {
			Coordinate[] coors =
				DistanceOp.nearestPoints(a.getInnerGeometry(), b.getInnerGeometry());
			return GamaList.with(new GamaPoint(coors[0]), new GamaPoint(coors[1]));
		}

		@operator("farthest_point_to")
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
		 * determine the closest point of a geometry to another given point.
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
		public static IList opNeighboursOf(final IScope scope, final ITopology t, final IAgent agent)
			throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return t.getNeighboursOf(agent, 1.0, Different.with());
		}

		@operator(value = "neighbours_of")
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
			return t.getNeighboursOf((IShape) a, Cast.asFloat(scope, d), Different.with());
		}

		@operator(value = "neighbours_at")
		public static IList opNeighboursAt(final IScope scope, final IShape agent,
			final Double distance) throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return scope.getAgentScope().getTopology()
				.getNeighboursOf(agent, distance, Different.with());
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static IList<IAgent> opInside(final IScope scope,
			final IContainer<?, IShape> targets, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.list(scope, targets), true);
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static IList<IAgent> opInside(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.species(targets), true);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static IList<IAgent> opOverlapping(final IScope scope,
			final IContainer<?, IShape> targets, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.list(scope, targets), false);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static IList<IAgent> opOverlapping(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry),
				In.species(targets), false);
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static Object opClosestTo(final IScope scope, final IContainer<?, IShape> targets,
			final Object source) throws GamaRuntimeException {
			if ( source instanceof IShape ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IShape) source, In.list(scope, targets)); }
			throw new GamaRuntimeException(StringUtils.toGaml(source) +
				" is not a geometrical object");
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.LEFT_CONTENT_TYPE)
		public static IAgent opClosestTo(final IScope scope, final ISpecies targets,
			final Object source) throws GamaRuntimeException {
			if ( source instanceof IShape ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IShape) source, In.species(targets)); }
			throw new GamaRuntimeException(StringUtils.toGaml(source) +
				" is not a geometrical object");
		}

		@operator(value = "agent_closest_to", type = IType.NONE)
		public static IAgent opAgentsClosestTo(final IScope scope, final Object source)
			throws GamaRuntimeException {
			if ( source instanceof IShape ) { return scope.getAgentScope().getTopology()
				.getAgentClosestTo((IShape) source, Different.with()); }
			throw new GamaRuntimeException(StringUtils.toGaml(source) +
				" is not a geometrical object");
		}

		@operator(value = "agents_inside", content_type = IType.NONE)
		public static IList<IAgent> opAgentsIn(final IScope scope,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), Different.with(),
				true);
		}

		@operator(value = "agents_overlapping", content_type = IType.NONE)
		public static IList<IAgent> opOverlappingAgents(final IScope scope,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			ITopology t = scope.getAgentScope().getTopology();
			return t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), Different.with(),
				false);
		}

	}

	public static abstract class Statistics {

		@operator(value = { "simple_clustering_by_distance" }, content_type = IType.LIST)
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
