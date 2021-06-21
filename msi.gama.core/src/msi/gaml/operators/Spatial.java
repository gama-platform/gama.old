/*******************************************************************************************************
 *
 * msi.gaml.operators.Spatial.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import static msi.gama.runtime.exceptions.GamaRuntimeException.error;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.algorithm.Centroid;
import org.locationtech.jts.algorithm.distance.DistanceToPoint;
import org.locationtech.jts.algorithm.distance.PointPairDistance;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.precision.EnhancedPrecisionOp;
import org.locationtech.jts.precision.GeometryPrecisionReducer;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.util.AssertionFailedException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.filter.In;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.precompiler.Reason;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGisFile;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.GamaSpatialPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.compilation.annotations.depends_on;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 10 dec. 2010
 *
 * All the spatial operators available in GAML. Regrouped by types of operators.
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class Spatial {

	/**
	 * The class Spatial.
	 *
	 * @author Alexis Drogoul, Patrick Taillandier, Arnaud Grignard
	 * @since 29 nov. 2011
	 *
	 */

	public static abstract class Common {

		@operator (
				value = "using",
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.TOPOLOGY, IConcept.SPATIAL_COMPUTATION },
				type = ITypeProvider.TYPE_AT_INDEX + 1,
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				index_type = ITypeProvider.KEY_TYPE_AT_INDEX + 1)
		@doc (
				value = "Allows to specify in which topology a spatial computation should take place.",
				usages = { @usage (
						value = "has no effect if the topology passed as a parameter is nil") },
				examples = { @example (
						value = "(agents closest_to self) using topology(world)",
						equals = "the closest agent to self (the caller) in the continuous topology of the world",
						test = false) })
		@no_test // comment="See Topology.experiment in test models"
		public static Object using(final IScope scope, final IExpression expression, final ITopology topology) {
			final ITopology oldTopo = scope.getTopology();
			try {
				if (topology != null) { scope.setTopology(topology); }
				return expression.value(scope);
			} finally {
				scope.setTopology(oldTopo);
			}
		}

	}

	public static abstract class Creation {

		@operator (
				value = "circle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A circle geometry which radius is equal to the operand.",
				masterDoc = true,
				usages = { @usage (
						value = "returns a point if the radius operand is lower or equal to 0.") },
				comment = "the center of the circle is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "circle(10)",
						equals = "a geometry as a circle of radius 10.",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"triangle" })
		@no_test // (comment="See Creation.experiment in test models : {Circle tests with tolerance}")
		public static IShape circle(final IScope scope, final Double radius) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (radius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildCircle(radius, location);
		}

		@operator (
				value = "circle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A circle geometry which radius is equal to the first operand, and the center has the location equal to the second operand.",
				usages = { @usage (
						value = "When circle is used with 2 operands, the second one is the center of the created circle.",
						examples = { @example (
								value = "circle(10,{80,30})",
								equals = "a geometry as a circle of radius 10, the center will be in the location {80,30}.",
								test = false) }) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"triangle" })
		@no_test
		public static IShape circle(final IScope scope, final Double radius, final GamaPoint position) {
			ILocation location;
			location = position;
			if (radius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildCircle(radius, location);
		}

		@operator (
				value = "ellipse",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "An ellipse geometry which x-radius is equal to the first operand and y-radius is equal to the second operand",
				usages = { @usage (
						value = "returns a point if both operands are lower or equal to 0, a line if only one is.") },
				comment = "the center of the ellipse is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "ellipse(10, 10)",
						equals = "a geometry as an ellipse of width 10 and height 10.",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"circle", "squircle", "triangle" })
		@no_test // (comment="See Creation.experiment in test models : {Ellipse tests}")
		public static IShape ellipse(final IScope scope, final Double xRadius, final Double yRadius) {
			GamaPoint location;
			final IAgent a = scope.getAgent();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if (xRadius <= 0) { if (yRadius <= 0) return new GamaShape(location); }
			return GamaGeometryType.buildEllipse(xRadius, yRadius, location);
		}

		@operator (
				value = "squircle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A mix of square and circle geometry (see : http://en.wikipedia.org/wiki/Squircle), which side size is equal to the first operand and power is equal to the second operand",
				usages = { @usage (
						value = "returns a point if the side operand is lower or equal to 0.") },
				comment = "the center of the ellipse is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "squircle(4,4)",
						equals = "a geometry as a squircle of side 4 with a power of 4.",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "super_ellipse",
						"rectangle", "square", "circle", "ellipse", "triangle" })
		@no_test // Because who cares "du cul"
		public static IShape squicle(final IScope scope, final Double xRadius, final Double power) {
			GamaPoint location;
			final IAgent a = scope.getAgent();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if (xRadius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildSquircle(xRadius, power, location);
		}

		@operator (
				value = "arc",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "An arc, which radius is equal to the first operand, heading to the second and amplitude the third",
				masterDoc = false,
				usages = { @usage (
						value = "returns a point if the radius operand is lower or equal to 0.") },
				comment = "the center of the arc is by default the location of the current agent in which has been called this operator. This operator returns a polygon by default.",
				examples = { @example (
						value = "arc(4,45,90)",
						equals = "a geometry as an arc of radius 4, in a direction of 45° and an amplitude of 90°",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "super_ellipse",
						"rectangle", "square", "circle", "ellipse", "triangle" })
		@no_test // (comment="See Creation.experiment in test models : {Arc tests}")
		public static IShape arc(final IScope scope, final Double xRadius, final Double heading,
				final Double amplitude) {
			return arc(scope, xRadius, heading, amplitude, true);
		}

		@operator (
				value = "arc",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "An arc, which radius is equal to the first operand, heading to the second, amplitude to the third and a boolean indicating whether to return a linestring or a polygon to the fourth",
				masterDoc = true,
				usages = { @usage (
						value = "returns a point if the radius operand is lower or equal to 0.") },
				comment = "the center of the arc is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "arc(4,45,90, false)",
						equals = "a geometry as an arc of radius 4, in a direction of 45° and an amplitude of 90°, which only contains the points on the arc",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "super_ellipse",
						"rectangle", "square", "circle", "ellipse", "triangle" })
		@no_test // (comment="See Creation.experiment in test models : {Arc tests}")
		public static IShape arc(final IScope scope, final Double xRadius, final Double heading, final Double amplitude,
				final boolean filled) {
			GamaPoint location;
			final IAgent a = scope.getAgent();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if (xRadius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildArc(xRadius, heading, amplitude, filled, location);
		}

		@operator (
				value = "elliptical_arc",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "An elliptical arc from the first operand (point) to the second operand (point), which radius is equal to the third operand, and a int giving the number of points to use as a last operand",
				examples = { @example (
						value = "elliptical_arc({0,0},{10,10},5.0, 20)",
						equals = "a geometry from {0,0} to {10,10} considering a radius of 5.0 built using 20 points",
						test = false) },
				see = { "arc", "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
						"super_ellipse", "rectangle", "square", "circle", "ellipse", "triangle" })
		@no_test // (comment="See Creation.experiment in test models : {Arc tests}")

		public static IShape EllipticalArc(final IScope scope, final GamaPoint pt1, final GamaPoint pt2, final double h,
				final int nPts) {
			double xRadius = pt1.distance(pt2) / 2.0;
			double yRadius = h;

			Coordinate[] pts = new Coordinate[nPts];
			int iPt = 0;
			for (int i = 0; i < nPts; i++) {
				double ang = i * (Math.PI / nPts);
				double x = xRadius * Math.cos(ang);
				double y = yRadius * Math.sin(ang);
				pts[iPt++] = new Coordinate(x, y);
			}
			IShape shape = new GamaShape(GeometryUtils.GEOMETRY_FACTORY.createLineString(pts));
			shape = Transformations.rotated_by(scope, shape, Relations.towards(scope, pt2, pt1));
			shape = Transformations.translated_by(scope, shape,
					pt1.minus(shape.getPoints().firstValue(scope).toGamaPoint()));
			return shape;
		}

		@operator (
				value = "cross",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cross, which radius is equal to the first operand (and eventually the width of the lines for the second)",
				masterDoc = true,
				examples = { @example (
						value = "cross(10,2)",
						equals = "a geometry as a cross of radius 10, and with a width of 2 for the lines ",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "super_ellipse",
						"rectangle", "square", "circle", "ellipse", "triangle" })
		@no_test
		public static IShape cross(final IScope scope, final Double xRadius, final Double width) {
			GamaPoint location;
			final IAgent a = scope.getAgent();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if (xRadius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildCross(xRadius, width, location);
		}

		@operator (
				value = "cross",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cross, which radius is equal to the first operand",
				examples = { @example (
						value = "cross(10)",
						equals = "a geometry as a cross of radius 10",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "super_ellipse",
						"rectangle", "square", "circle", "ellipse", "triangle" })
		@no_test
		public static IShape cross(final IScope scope, final Double xRadius) {
			GamaPoint location;
			final IAgent a = scope.getAgent();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if (xRadius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildCross(xRadius, null, location);
		}

		@operator (
				value = "cylinder",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A cylinder geometry which radius is equal to the operand.",
				usages = { @usage (
						value = "returns a point if the operand is lower or equal to 0.") },
				comment = "the center of the cylinder is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "cylinder(10,10)",
						equals = "a geometry as a circle of radius 10.",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"triangle" })
		@no_test // (comment="Dummy init in test models > Creation.experiment")
		public static IShape cylinder(final IScope scope, final Double radius, final Double depth) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (radius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildCylinder(radius, depth, location);
		}

		@operator (
				value = "sphere",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.THREED })
		@doc (
				value = "A sphere geometry which radius is equal to the operand.",
				usages = { @usage (
						value = "returns a point if the operand is lower or equal to 0.") },
				comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "sphere(10)",
						equals = "a geometry as a circle of radius 10 but displays a sphere.",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"triangle" })
		@no_test // (comment="Dummy init in test models > Creation.experiment")
		public static IShape sphere(final IScope scope, final Double radius) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (radius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildSphere(radius, location);
		}

		@operator (
				value = "teapot",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.THREED })
		@doc (
				value = "A teapot geometry which radius is equal to the operand.",
				special_cases = { "returns a point if the operand is lower or equal to 0." },
				comment = "the centre of the teapot is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "teapot(10)",
						equals = "a geometry as a circle of radius 10 but displays a teapot.",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"triangle" })
		@no_test // (comment="Dummy init in test models > Creation.experiment")
		public static IShape teapot(final IScope scope, final Double size) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (size <= 0) return new GamaShape(location);
			return GamaGeometryType.buildTeapot(size, location);
		}

		@operator (
				value = "cone",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A cone geometry which min and max angles are given by the operands.",
				masterDoc = true,
				special_cases = { "returns nil if the operand is nil." },
				comment = "the center of the cone is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "cone(0, 45)",
						equals = "a geometry as a cone with min angle is 0 and max angle is 45.",
						test = false) },
				see = { "around", "circle", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"square", "triangle" })
		@no_test // no idea how to test a cone
		@depends_on (IKeyword.SHAPE)
		public static IShape cone(final IScope scope, final Integer p1, final Integer p2) {
			if (p1 == null || p2 == null) return null;
			final Double min_angle = Maths.checkHeading(p1);
			final Double max_angle = Maths.checkHeading(p2);
			final IAgent a = scope.getAgent();
			final ILocation origin = a.getLocation() == null ? new GamaPoint(0, 0) : a.getLocation();
			final double originx = origin.getX();
			final double originy = origin.getY();
			final double worldWidth = scope.getTopology().getWidth();// -
																		// originx;
			final double worldHeight = scope.getTopology().getHeight();// -
																		// originy;
			final double max = Math.max(worldWidth, worldHeight);
			final double min_point_x = originx + Maths.cos(min_angle) * max;
			final double min_point_y = originy + Maths.sin(min_angle) * max;
			final ILocation minPoint = new GamaPoint(min_point_x, min_point_y);

			final double max_point_x = originx + Maths.cos(max_angle) * max;
			final double max_point_y = originy + Maths.sin(max_angle) * max;
			final ILocation maxPoint = new GamaPoint(max_point_x, max_point_y);

			return polygon(scope, GamaListFactory.wrap(Types.POINT, origin, minPoint, maxPoint));
		}

		@operator (
				value = "cone",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cone geometry which min and max angles are given by the operands.",
				examples = { @example (
						value = "cone({0, 45})",
						equals = "a geometry as a cone with min angle is 0 and max angle is 45.",
						test = false) },
				see = { "around", "circle", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"square", "triangle" })
		@no_test // no idea how to test a cone
		public static IShape cone(final IScope scope, final GamaPoint p) {
			if (p == null) return null;
			return cone(scope, (int) p.x, (int) p.y);
		}

		@operator (
				value = "cone3D",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.THREED })
		@doc (
				value = "A cone geometry which base radius size is equal to the first operand, and which the height is equal to the second operand.",
				special_cases = { "returns a point if the operand is lower or equal to 0." },
				comment = "the center of the cone is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "cone3D(10.0,5.0)",
						equals = "a geometry as a cone with a base circle of radius 10 and a height of 5.",
						test = false) },
				see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"triangle" })
		@no_test
		public static IShape cone3D(final IScope scope, final Double radius, final Double height) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (radius <= 0) return new GamaShape(location);
			return GamaGeometryType.buildCone3D(radius, height, location);
		}

		@operator (
				value = "square",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A square geometry which side size is equal to the operand.",
				usages = { @usage (
						value = "returns nil if the operand is nil.") },
				comment = "the centre of the square is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "square(10)",
						equals = "a geometry as a square of side size 10.",
						test = false),
						@example (
								value = "var0.area",
								equals = "100.0",
								returnType = "float") },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"triangle" })
		@test ("square(10).area = 100")
		public static IShape square(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (side_size <= 0) return new GamaShape(location);
			return GamaGeometryType.buildSquare(side_size, location);
		}

		@operator (
				value = "cube",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.THREED })
		@doc (
				value = "A cube geometry which side size is equal to the operand.",
				usages = { @usage (
						value = "returns nil if the operand is nil.") },
				comment = "the center of the cube is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "cube(10)",
						equals = "a geometry as a square of side size 10.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"triangle" })
		@test ("cube(10).volume = 1000")
		public static IShape cube(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (side_size <= 0) return new GamaShape(location);
			return GamaGeometryType.buildCube(side_size, location);
		}

		@operator (
				value = "rectangle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A rectangle geometry which side sizes are given by the operands (as a point).",
				comment = "the center of the rectangle is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "rectangle({10, 5})",
						equals = "a geometry as a rectangle with width = 10 and height = 5.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "square",
						"triangle" })
		@test ("rectangle({10, 5}).area = 50.0")
		public static IShape rectangle(final IScope scope, final GamaPoint p) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(p.x, p.y, location);
		}

		@operator (
				value = "rectangle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A rectangle geometry, computed from the operands values (e.g. the 2 side sizes).",
				masterDoc = true,
				usages = { @usage (
						value = "returns nil if the operand is nil.") },
				comment = "the center of the rectangle is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "rectangle(10, 5)",
						equals = "a geometry as a rectangle with width = 10 and height = 5.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "square",
						"triangle" })
		@test ("rectangle(10, 5).area = 50.0")
		public static IShape rectangle(final IScope scope, final double x, final double y) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(x, y, location);
		}

		@operator (
				value = "rectangle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A rectangle geometry which upper-left and lower-right corners are defined as points.",
				examples = { @example (
						value = "rectangle({0.0,0.0}, {10.0,10.0})",
						equals = "a geometry as a rectangle with {1.0,1.0} as the upper-left corner, {10.0,10.0} as the lower-right corner.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "square",
						"triangle" })
		@test ("rectangle({0.0,0.0}, {10.0,10.0}).area = 100.0")
		public static IShape rectangle(final IScope scope, final GamaPoint upperLeftCorner,
				final GamaPoint lowerRightCorner) {
			ILocation location;
			final double width = Math.abs(upperLeftCorner.x - lowerRightCorner.x);
			final double height = Math.abs(upperLeftCorner.y - lowerRightCorner.y);
			final GamaPoint realTopLeftCorner = new GamaPoint(Math.min(upperLeftCorner.x, lowerRightCorner.x),
					Math.min(upperLeftCorner.y, lowerRightCorner.y));
			location = new GamaPoint(realTopLeftCorner.x + width / 2, realTopLeftCorner.y + height / 2);
			return GamaGeometryType.buildRectangle(width, height, location);
		}

		@operator (
				value = "box",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.THREED })
		@doc (
				value = "A box geometry which side sizes are given by the operands.",
				masterDoc = true,
				usages = { @usage (
						value = "returns nil if the operand is nil.") },
				comment = "the center of the box is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "box({10, 5 , 5})",
						equals = "a geometry as a rectangle with width = 10, height = 5 depth= 5.",
						test = false),
						@example (
								value = " (box({10, 10 , 5}) at_location point(50,50,0)).location.y",
								equals = "50.0",
								returnType = "float") },
				see = { "around", "circle", "sphere", "cone", "line", "link", "norm", "point", "polygon", "polyline",
						"square", "cube", "triangle" })
		public static IShape box(final IScope scope, final GamaPoint p) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildBox(p.x, p.y, p.z, location);
		}

		@operator (
				value = "box",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = {})
		@doc (
				value = "A box geometry which side sizes are given by the operands.",
				usages = { @usage (
						value = "returns nil if the operand is nil.") },
				comment = "the center of the box is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "box(10, 5 , 5)",
						equals = "a geometry as a rectangle with width = 10, height = 5 depth= 5.",
						test = false) },
				see = { "around", "circle", "sphere", "cone", "line", "link", "norm", "point", "polygon", "polyline",
						"square", "cube", "triangle" })
		@test ("box(10,5,5).volume = 250")
		public static IShape box(final IScope scope, final double x, final double y, final double z) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildBox(x, y, z, location);
		}

		@operator (
				value = "triangle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A triangle geometry which side size is given by the operand.",
				usages = { @usage ("returns nil if the operand is nil.") },
				comment = "the center of the triangle is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "triangle(5)",
						equals = "a geometry as a triangle with side_size = 5.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"square" })
		@no_test
		public static IShape triangle(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (side_size <= 0) return new GamaShape(location);
			return GamaGeometryType.buildTriangle(side_size, location);
		}

		@operator (
				value = "triangle",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A triangle geometry which the base and height size are given by the operand.",
				usages = { @usage ("returns nil if one of the operand is nil.") },
				comment = "the center of the triangle is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "triangle(5, 10)",
						equals = "a geometry as a triangle with a base of 5m and a height of 10m.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"square" })
		@no_test
		public static IShape triangle(final IScope scope, final Double base, final Double height) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (base <= 0 || height <= 0) return new GamaShape(location);
			return GamaGeometryType.buildTriangle(base, height, location);
		}

		@operator (
				value = "pyramid",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.THREED })
		@doc (
				value = "A square geometry which side size is given by the operand.",
				usages = { @usage (
						value = "returns nil if the operand is nil.") },
				comment = "the center of the pyramid is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "pyramid(5)",
						equals = "a geometry as a square with side_size = 5.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"square" })
		@no_test
		public static IShape pyramid(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (side_size <= 0) return new GamaShape(location);
			return GamaGeometryType.buildPyramid(side_size, location);
		}

		@operator (
				value = "hexagon",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY })
		@doc (
				value = "A hexagon geometry which the given with and height",
				masterDoc = true,
				usages = { @usage ("returns nil if the operand is nil.") },
				comment = "the center of the hexagon is by default the location of the current agent in which has been called this operator.",
				examples = { @example (
						value = "hexagon(10)",
						equals = "a geometry as a hexagon of width of 10 and height of 10.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"triangle" })
		@no_test
		public static IShape hexagon(final IScope scope, final Double size) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (size <= 0) return new GamaShape(location);
			return GamaGeometryType.buildHexagon(size, location);
		}

		@operator (
				value = "hexagon",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.THREED })
		@doc (
				value = "A hexagon geometry which the given width and height",
				examples = { @example (
						value = "hexagon({10,5})",
						equals = "a geometry as a hexagon of width of 10 and height of 5.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"triangle" })
		@no_test
		public static IShape hexagon(final IScope scope, final GamaPoint size) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			final Double width = size.x;
			final Double height = size.y;
			if (width <= 0 || height <= 0) return new GamaShape(location);
			return GamaGeometryType.buildHexagon(width, height, location);
		}

		@operator (
				value = "hexagon",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE, IOperatorCategory.THREED },
				concept = { IConcept.THREED })
		@doc (
				value = "A hexagon geometry which the given width and height",
				examples = { @example (
						value = "hexagon(10,5)",
						equals = "a geometry as a hexagon of width of 10 and height of 5.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
						"triangle" })
		@no_test
		public static IShape hexagon(final IScope scope, final Double width, final Double height) {
			ILocation location;
			final IAgent a = scope.getAgent();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if (width == null || height == null || width <= 0 || height <= 0) return new GamaShape(location);
			return GamaGeometryType.buildHexagon(width, height, location);
		}

		@operator (
				value = "polygon",
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.POINT })
		@doc (
				value = "A polygon geometry from the given list of points.",
				usages = { @usage (
						value = "if the operand is nil, returns the point geometry {0,0}"),
						@usage (
								value = "if the operand is composed of a single point, returns a point geometry"),
						@usage (
								value = "if the operand is composed of 2 points, returns a polyline geometry.") },
				examples = { @example (
						value = "polygon([{0,0}, {0,10}, {10,10}, {10,0}])",
						equals = "a polygon geometry composed of the 4 points.",
						test = false),
						@example (
								value = "polygon([{0,0}, {0,10}, {10,10}, {10,0}]).area",
								equals = "100.0",
								returnType = "float"),
						@example (
								value = "polygon([{0,0}, {0,10}, {10,10}, {10,0}]).location",
								equals = "point(5.0,5.0,0.0)",
								returnType = "point") },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polyline", "rectangle", "square",
						"triangle" })
		@no_test
		public static IShape polygon(final IScope scope, final IContainer<?, ? extends IShape> points) {
			if (points == null || points.isEmpty(scope)) return new GamaShape(new GamaPoint(0, 0));
			// final IList<IShape> shapes = points.listValue(scope); Now
			// replaced by a copy of the list (see Issue 740)
			final IList<IShape> shapes = GamaListFactory.create(scope, Types.GEOMETRY, points);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if (size == 1) return GamaGeometryType.createPoint(first);
			if (size == 2) return GamaGeometryType.buildLine(first, shapes.lastValue(scope));
			if (!first.equals(shapes.lastValue(scope))) { shapes.add(first); }
			return GamaGeometryType.buildPolygon(shapes);
		}

		@operator (
				value = "polyhedron",
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.POINT })
		@doc (
				value = "A polyhedron geometry from the given list of points.",
				usages = { @usage (
						value = "if the operand is nil, returns the point geometry {0,0}"),
						@usage (
								value = "" + "if the operand is composed of a single point, returns a point geometry"),
						@usage (
								value = "if the operand is composed of 2 points, returns a polyline geometry.") },
				examples = { @example (
						value = "polyhedron([{0,0}, {0,10}, {10,10}, {10,0}],10)",
						equals = "a polygon geometry composed of the 4 points and of depth 10.",
						test = false) },
				see = { "around", "circle", "cone", "line", "link", "norm", "point", "polyline", "rectangle", "square",
						"triangle" })
		@no_test
		public static IShape polyhedron(final IScope scope, final IContainer<?, IShape> points, final Double depth) {
			if (points == null || points.isEmpty(scope)) return new GamaShape(new GamaPoint(0, 0));
			// final IList<IShape> shapes = points.listValue(scope); Now
			// replaced by a copy of the list (see Issue 740)
			final IList<IShape> shapes = GamaListFactory.create(scope, Types.POINT, points);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if (size == 1) return GamaGeometryType.createPoint(first);
			final IShape last = shapes.lastValue(scope);
			if (size == 2) return GamaGeometryType.buildLine(first, last);
			if (!first.equals(last)) { shapes.add(first); }
			return GamaGeometryType.buildPolyhedron(shapes, depth);
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.POINT })
		@doc (
				value = "The operator computes a Bezier curve geometry between the given operators, with 10 or a given number of points, and from left to rigth or right to left.",
				masterDoc = true,
				usages = { @usage (
						value = "if one  of the operand is nil, returns nil"),
						@usage (
								value = "When used with 3 points, it computes a quadratic Bezier curve geometry built from the three given points and composed of 10 points.",
								examples = { @example (
										value = "curve({0,0}, {0,10}, {10,10})",
										equals = "a quadratic Bezier curve geometry composed of 10 points from p0 to p2.",
										test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final GamaPoint P2) {
			if (P0 == null || P1 == null || P2 == null) return null;
			return GamaGeometryType.buildPolyline(quadraticBezierCurve(P0, P1, P2, 10));
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A quadratic Bezier curve geometry built from the three given points composed of a given numnber of points.",
				usages = { @usage (
						value = "When used with 3 points and an integer, it  computes a quadratic Bezier curve geometry built from the three given points. If the last operand (number of points) is inferior to 2, returns nil",
						examples = { @example (
								value = "curve({0,0}, {0,10}, {10,10}, 20)",
								equals = "a quadratic Bezier curve geometry composed of 20 points from p0 to p2.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
				final int nbPoints) {
			if (P0 == null || P1 == null || P2 == null || nbPoints < 2) return null;
			return GamaGeometryType.buildPolyline(quadraticBezierCurve(P0, P1, P2, nbPoints));
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the four given points composed of 10 points.",
				usages = { @usage (
						value = "When used with 4 points, it computes, it computes a cubic Bezier curve geometry built from the four given points and composed of 10 points. ",
						examples = { @example (
								value = "curve({0,0}, {0,10}, {10,10})",
								equals = "a cubic Bezier curve geometry composed of 10 points from p0 to p3.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
				final GamaPoint P3) {
			if (P0 == null || P1 == null || P2 == null || P3 == null) return null;
			return GamaGeometryType.buildPolyline(cubicBezierCurve(P0, P1, P2, P3, 10));
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of 10 points.",
				usages = { @usage (
						value = "When used with 2 points and a float coefficient, it computes a cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of 10 points.",
						examples = { @example (
								value = "curve({0,0},{10,10}, 0.5)",
								equals = "a cubic Bezier curve geometry composed of 10 points from p0 to p1.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final Double coefficient) {
			return BezierCurve(scope, P0, P1, coefficient, true, 10, 0.5);
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of 10 points - the last boolean is used to specified if it is the right side.",
				usages = { @usage (
						value = "When used with 2 points, a float coefficient and a boolean, it computes a cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of 10 points. The last boolean is used to specified if it is the right side.",
						examples = { @example (
								value = "curve({0,0},{10,10}, 0.5, false)",
								equals = "a cubic Bezier curve geometry composed of 10 points from p0 to p1 at the left side.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final Double coefficient, final boolean right) {
			return BezierCurve(scope, P0, P1, coefficient, right, 10, 0.5);
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points - the boolean is used to specified if it is the right side.",
				usages = { @usage (
						value = "When used with 2 points, a float coefficient, a boolean, and an integer number of points, it computes a cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points - the boolean is used to specified if it is the right side.",
						examples = { @example (
								value = "curve({0,0},{10,10}, 0.5, false, 100)",
								equals = "a cubic Bezier curve geometry composed of 100 points from p0 to p1 at the right side.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final Double coefficient, final boolean right, final int nbPoints) {
			return BezierCurve(scope, P0, P1, coefficient, right, nbPoints, 0.5);
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points - the boolean is used to specified if it is the right side and the last value to indicate where is the inflection point (between 0.0 and 1.0 - default 0.5).",
				usages = { @usage (
						value = "When used with 2 points, a float coefficient, a boolean, an integer number of points, and a float proportion, it computes a cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points - the boolean is used to specified if it is the right side and the last value to indicate where is the inflection point (between 0.0 and 1.0 - default 0.5).",
						examples = { @example (
								value = "curve({0,0},{10,10}, 0.5, false, 100, 0.8)",
								equals = "a cubic Bezier curve geometry composed of 100 points from p0 to p1 at the right side.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final Double coefficient, final boolean right, final int nbPoints, final double proportion) {
			if (P0 == null || P1 == null) return null;
			GamaPoint P01 = new GamaPoint(P0.x + (P1.x - P0.x) * proportion, P0.y + (P1.y - P0.y) * proportion,
					P0.z + (P1.z - P0.z) * proportion);
			final double val = coefficient * P0.euclidianDistanceTo(P1);
			final double heading = Relations.towards(scope, P0, P1);
			P01 = new GamaPoint(P01.x + Maths.cos(heading + 90 * (right ? 1.0 : -1.0)) * val,
					P01.y + Maths.sin(heading + 90 * (right ? 1.0 : -1.0)) * val, P01.z);
			return BezierCurve(scope, P0, P01, P1, nbPoints);
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points, considering the given inflection point (between 0.0 and 1.0 - default 0.5), and the given rotation angle (90 = along the z axis).",
				usages = { @usage (
						value = "When used with 2 points, a float coefficient, a boolean, an integer number of points, a float proportion, and a float angle, it computes a cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points, considering the given inflection point (between 0.0 and 1.0 - default 0.5), and the given rotation angle (90 = along the z axis).",
						examples = { @example (
								value = "curve({0,0},{10,10}, 0.5, 100, 0.8, 90)",
								equals = "a cubic Bezier curve geometry composed of 100 points from p0 to p1 at the right side.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final Double coefficient, final int nbPoints, final double proportion, final double angle) {
			IShape shape = BezierCurve(scope, P0, P1, coefficient, false, nbPoints, proportion);
			shape = Transformations.rotated_by(scope, shape, angle,
					new GamaPoint(P0.x - P1.x, P0.y - P1.y, P0.z - P1.z));
			final GamaPoint newPt0 = (GamaPoint) shape.getPoints().get(0);
			shape = Transformations.translated_by(scope, shape,
					new GamaPoint(P0.x - newPt0.x, P0.y - newPt0.y, P0.z - newPt0.z));
			return shape;
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points, considering the given rotation angle (90 = along the z axis).",
				usages = { @usage (
						value = "When used with 2 points, a float coefficient, a boolean, an integer number of points, and a float angle, it computes a cubic Bezier curve geometry built from the two given points with the given coefficient for the radius and composed of the given number of points, considering the given rotation angle (90 = along the z axis).",
						examples = { @example (
								value = "curve({0,0},{10,10}, 0.5, 100, 90)",
								equals = "a cubic Bezier curve geometry composed of 100 points from p0 to p1 at the right side.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final Double coefficient, final int nbPoints, final double angle) {
			return BezierCurve(scope, P0, P1, coefficient, nbPoints, 0.5, angle);
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the two given points with the given coefficient for the radius considering the given rotation angle (90 = along the z axis).",
				usages = { @usage (
						value = "When used with 2 points, a float coefficient, and a float angle, it computes a cubic Bezier curve geometry built from the two given points with the given coefficient for the radius considering the given rotation angle (90 = along the z axis).",
						examples = { @example (
								value = "curve({0,0},{10,10}, 0.5, 90)",
								equals = "a cubic Bezier curve geometry composed of 100 points from p0 to p1 at the right side.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
				final Double coefficient, final double angle) {
			return BezierCurve(scope, P0, P1, coefficient, 10, 0.5, angle);
		}

		@operator (
				value = { "curve" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = {})
		@doc (
				value = "A cubic Bezier curve geometry built from the four given points composed of a given number of points.",
				usages = { @usage (
						value = "When used with 4 points and an integer number of  points, it computes a cubic Bezier curve geometry built from the four given points composed of a given number of points. If the number of points is  lower than 2, it returns nil.",
						examples = { @example (
								value = "curve({0,0}, {0,10}, {10,10})",
								equals = "a cubic Bezier curve geometry composed of 10 points from p0 to p3.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
				final GamaPoint P3, final int nbPoints) {
			if (P0 == null || P1 == null || P2 == null || P3 == null || nbPoints < 2) return null;
			return GamaGeometryType.buildPolyline(cubicBezierCurve(P0, P1, P2, P3, nbPoints));
		}

		private static List<IShape> quadraticBezierCurve(final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
				final int nbPoints) {
			final List<IShape> points = new ArrayList<>();
			for (int i = 0; i < nbPoints; i++) {
				final double x = quadraticBezier(P0.x, P1.x, P2.x, (double) i / (nbPoints - 1));
				final double y = quadraticBezier(P0.y, P1.y, P2.y, (double) i / (nbPoints - 1));
				final double z = quadraticBezier(P0.z, P1.z, P2.z, (double) i / (nbPoints - 1));
				points.add(new GamaPoint(x, y, z));
			}
			return points;
		}

		private static List<IShape> cubicBezierCurve(final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
				final GamaPoint P3, final int nbPoints) {
			final List<IShape> points = new ArrayList<>();
			for (int i = 0; i < nbPoints; i++) {
				final double x = cubicBezier(P0.x, P1.x, P2.x, P3.x, (double) i / (nbPoints - 1));
				final double y = cubicBezier(P0.y, P1.y, P2.y, P3.y, (double) i / (nbPoints - 1));
				final double z = cubicBezier(P0.z, P1.z, P2.z, P3.z, (double) i / (nbPoints - 1));
				points.add(new GamaPoint(x, y, z));
			}
			return points;
		}

		private static double quadraticBezier(final double v0, final double v1, final double v2, final double t) {
			return (1 - t) * ((1 - t) * v0 + t * v1) + t * ((1 - t) * v1 + t * v2);
		}

		private static double cubicBezier(final double v0, final double v1, final double v2, final double v3,
				final double t) {
			return Math.pow(1 - t, 3) * v0 + 3 * (1 - t) * (1 - t) * t * v1 + 3 * (1 - t) * t * t * v2
					+ Math.pow(t, 3) * v3;
		}

		@operator (
				value = { "line", "polyline" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.GEOMETRY, IConcept.POINT })
		@doc (
				value = "A polyline geometry from the given list of points.",
				masterDoc = true,
				usages = { @usage (
						value = "if the points list operand is nil, returns the point geometry {0,0}"),
						@usage (
								value = "if the points list operand is composed of a single point, returns a point geometry.") },
				examples = { @example (
						value = "polyline([{0,0}, {0,10}, {10,10}])",
						equals = "a polyline geometry composed of the 3 points.",
						test = false),
						@example (
								value = "line([{10,10}, {10,0}])",
								equals = "a line from 2 points.",
								test = false),
						@example (
								value = "string(polyline([{0,0}, {0,10}, {10,10}])+line([{10,10}, {10,0}]))",
								equals = "\"MULTILINESTRING ((0 0, 0 10, 10 10), (10 10, 10 0))\"",
								returnType = "string"), },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle" })
		@test ("points_along(line({0,0},{0,10}),[0.5])[0] = point({0,5})")
		public static IShape line(final IScope scope, final IContainer<?, IShape> points) {
			if (points == null || points.isEmpty(scope)) return new GamaShape(new GamaPoint(0, 0));
			final IList<IShape> shapes = points.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if (size == 1) return GamaGeometryType.createPoint(first);
			if (size == 2) return GamaGeometryType.buildLine(first, points.lastValue(scope));
			return GamaGeometryType.buildPolyline(shapes);
		}

		@operator (
				value = { "geometry_collection" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc (
				value = "A geometry collection (multi-geometry) composed of the given list of geometries.",
				usages = { @usage (
						value = "if the operand is nil, returns the point geometry {0,0}"),
						@usage (
								value = "if the operand is composed of a single geometry, returns a copy of the geometry.") },
				examples = { @example (
						value = "geometry_collection([{0,0}, {0,10}, {10,10}, {10,0}])",
						equals = "a geometry composed of the 4 points (multi-point).",
						test = false) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle", "line" })
		@no_test
		public static IShape geometryCollection(final IScope scope, final IContainer<?, IShape> geometries) {
			if (geometries == null || geometries.isEmpty(scope)) return new GamaShape(new GamaPoint(0, 0));
			final IList<IShape> shapes = geometries.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if (size == 1) return first.copy(scope);

			return GamaGeometryType.buildMultiGeometry(shapes);
		}

		@operator (
				value = { "line", "polyline" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc (
				value = "A polyline geometry from the given list of points represented as a cylinder of radius r.",
				usages = { @usage (
						value = "if a radius is added, the given list of points represented as a cylinder of radius r",
						examples = { @example (
								value = "polyline([{0,0}, {0,10}, {10,10}, {10,0}],0.2)",
								equals = "a polyline geometry composed of the 4 points.",
								test = false) }) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle" })
		@no_test
		public static IShape line(final IScope scope, final IContainer<?, IShape> points, final double radius) {
			if (points == null || points.isEmpty(scope)) return new GamaShape(new GamaPoint(0, 0));
			final IList<IShape> shapes = points.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if (size == 1) return GamaGeometryType.createPoint(first);
			if (size == 2) return GamaGeometryType.buildLineCylinder(first, points.lastValue(scope), radius);
			return GamaGeometryType.buildPolylineCylinder(shapes, radius);
		}

		@operator (
				value = { "plan", "polyplan" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc (
				value = "A polyline geometry from the given list of points.",
				usages = { @usage (
						value = "if the operand is nil, returns the point geometry {0,0}"),
						@usage (
								value = "if the operand is composed of a single point, returns a point geometry.") },
				examples = { @example (
						value = "polyplan([{0,0}, {0,10}, {10,10}, {10,0}],10)",
						equals = "a polyline geometry composed of the 4 points with a depth of 10.",
						test = false) },
				see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square",
						"triangle" })
		@no_test
		public static IShape plan(final IScope scope, final IContainer<?, IShape> points, final Double depth) {
			if (points == null || points.isEmpty(scope)) return new GamaShape(new GamaPoint(0, 0));
			final IList<IShape> shapes = points.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if (size == 1) return GamaGeometryType.createPoint(first);
			if (size == 2) return GamaGeometryType.buildPlan(first, shapes.lastValue(scope), depth);
			return GamaGeometryType.buildPolyplan(shapes, depth);
		}

		@operator (
				value = { "link" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.SHAPE, IConcept.SPATIAL_COMPUTATION, IConcept.GEOMETRY })
		@doc (
				value = "A dynamic line geometry between the location of the two operands",
				usages = {
						@usage ("if one of the operands is nil, link returns a point geometry at the location of the other. If both are null, it returns a point geometry at {0,0}"), },
				comment = "The geometry of the link is a line between the locations of the two operands, which is built and maintained dynamically ",
				examples = { @example (
						value = "link (geom1,geom2)",
						equals = "a link geometry between geom1 and geom2.",
						isExecutable = false) },
				see = { "around", "circle", "cone", "line", "norm", "point", "polygon", "polyline", "rectangle",
						"square", "triangle" })
		@no_test
		public static IShape link(final IScope scope, final IShape source, final IShape target)
				throws GamaRuntimeException {
			if (source == null) {
				if (target == null)
					return new GamaPoint(0, 0);
				else
					return GamaGeometryType.createPoint(target.getLocation());
			}
			if (target == null) return GamaGeometryType.createPoint(source.getLocation());
			return GamaGeometryType.buildLink(scope, source, target);
		}

		@operator (
				value = "around",
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				value = "A geometry resulting from the difference between a buffer around the right-operand casted in geometry at a distance left-operand (right-operand buffer left-operand) and the right-operand casted as geometry.",
				usages = { @usage (
						value = "returns a circle geometry of radius right-operand if the left-operand is nil") },
				examples = { @example (
						value = "10 around circle(5)",
						equals = "the ring geometry between 5 and 10.",
						test = false) },
				see = { "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
						"triangle" })
		@no_test
		public static IShape around(final IScope scope, final Double width, final Object toBeCastedIntoGeometry)
				throws GamaRuntimeException {
			final IShape g = Cast.asGeometry(scope, toBeCastedIntoGeometry, false);
			if (g == null) return circle(scope, width);
			return Operators.minus(scope, Transformations.enlarged_by(scope, g, width), g);
		}

		/*
		 * @operator("envelope")
		 *
		 * @doc(value =
		 * "A rectangular 3D geometry that represents the rectangle that surrounds the geometries or the surface described by the arguments. More general than geometry(arguments).envelope, as it allows to pass int, double, point, image files, shape files, asc files, or any list combining these arguments, in which case the envelope will be correctly expanded. If an envelope cannot be determined from the arguments, a default one of dimensions (0,100, 0, 100, 0, 100) is returned"
		 * ) public static IShape envelope(final IScope scope, final Object obj) { Envelope3D env = new
		 * Envelope3D(GeometryUtils.computeEnvelopeFrom(scope, obj)); if ( env.isNull() ) { env = new Envelope3D(0, 100,
		 * 0, 100, 0, 100); } final IShape shape = GamaGeometryType.buildBox(env.getWidth(), env.getHeight(),
		 * env.getDepth(), new GamaPoint(env.centre())); return shape; }
		 */

		@operator (
				value = "envelope",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				value = "A 3D geometry that represents the box that surrounds the geometries or the surface described by the arguments. More general than geometry(arguments).envelope, as it allows to pass int, double, point, image files, shape files, asc files, or any list combining these arguments, in which case the envelope will be correctly expanded. If an envelope cannot be determined from the arguments, a default one of dimensions (0,100, 0, 100, 0, 100) is returned",
				usages = { @usage (
						value = "This operator is often used to define the environment of simulation") },
				examples = { @example (
						value = "file road_shapefile <- file(\"../includes/roads.shp\");",
						isExecutable = false),
						@example (
								value = "geometry shape <- envelope(road_shapefile);",
								isExecutable = false),
						@example (
								value = "// shape is the system variable of  the environment",
								isExecutable = false),
						@example (
								value = "polygon([{0,0}, {20,0}, {10,10}, {10,0}])",
								equals = "create a polygon to get the envolpe",
								test = false),
						@example (
								value = "envelope(polygon([{0,0}, {20,0}, {10,10}, {10,0}])).area",
								equals = "200.0",
								returnType = "float") }

		)
		public static IShape envelope(final IScope scope, final Object obj) {
			Envelope3D env = Envelope3D.of(GeometryUtils.computeEnvelopeFrom(scope, obj));
			try {
				if (env.isNull()) { env = Envelope3D.of(0, 100, 0, 100, 0, 100); }
				return GamaGeometryType.buildBox(env.getWidth(), env.getHeight(), env.getDepth(),
						env.centre().toGamaPoint());
			} finally {
				env.dispose();
			}
		}
	}

	public static abstract class Operators {

		@operator (
				value = { "inter", "intersection" },
				category = { IOperatorCategory.SPATIAL })
		@doc (
				value = "A geometry resulting from the intersection between the two geometries",
				special_cases = { "returns nil if one of the operands is nil" },
				examples = { @example (
						value = "square(10) inter circle(5)",
						equals = "circle(5)") },
				see = { "union", "+", "-" })
		public static IShape inter(final IScope scope, final IShape g1, final IShape g2) {
			if (g2 == null || g1 == null) return null;
			if (g2.isPoint() && g1.covers(g2.getLocation())) return g2.copy(scope);
			if (g1.isPoint() && g2.covers(g1.getLocation())) return g1.copy(scope);
			Geometry geom = null;
			final Geometry geom1 = g1.getInnerGeometry();
			final Geometry geom2 = g2.getInnerGeometry();
			try {

				geom = geom1.intersection(geom2);
			} catch (final Exception ex) {
				try {
					final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
					geom = GeometryPrecisionReducer.reducePointwise(geom1, pm)
							.intersection(GeometryPrecisionReducer.reducePointwise(geom2, pm));
				} catch (final Exception e) {
					// AD 12/04/13 : Addition of a third method in case of
					// exception
					try {
						geom = geom1.buffer(0.01, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_FLAT)

								.intersection(geom2.buffer(0.01, BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
										BufferParameters.CAP_FLAT));
					} catch (final Exception e2) {
						return null;
					}
				}
			}
			if (geom == null || geom.isEmpty()) return null;
			// WARNING The attributes of the left-hand shape are kept, but not
			// those of the right-hand shape
			final GamaShape result = new GamaShape(g1, geom);
			result.losePredefinedProperty();
			return result;
		}

		@operator (
				value = { "+", "union" },
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				usages = @usage (
						value = "if the right-operand is a point, a geometry or an agent, returns the geometry resulting from the union between both geometries",
						examples = @example (
								value = "geom1 + geom2",
								equals = "a geometry corresponding to union between geom1 and geom2",
								isExecutable = false)))
		@no_test // test already done in Spatial tests Models
		public static IShape union(final IScope scope, final IShape g1, final IShape g2) {
			if (g1 == null) {
				if (g2 == null) return null;
				return g2.copy(scope);
			}
			if (g2 == null) return g1.copy(scope);
			final Geometry geom1 = g1.getInnerGeometry();
			final Geometry geom2 = g2.getInnerGeometry();
			Geometry geom;
			try {
				geom = geom1.union(geom2);
			} catch (final Exception e) {
				try {
					final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
					geom = GeometryPrecisionReducer.reducePointwise(geom1, pm)
							.intersection(GeometryPrecisionReducer.reducePointwise(geom2, pm));
				} catch (final Exception e1) {
					try {
						geom = Spatial.Transformations.translated_by(scope, g2.copy(scope), new GamaPoint(0.01, 0))
								.getInnerGeometry().union(geom1);

					} catch (final Exception e2) {
						// AD 12/04/13 : Addition of a third method in case of
						// exception
						try {
							geom = geom1.buffer(0.01, 0, BufferParameters.CAP_SQUARE)
									.union(geom2.buffer(0.01, 0, BufferParameters.CAP_SQUARE));
						} catch (final Exception e3) {
							geom = Spatial.Transformations.rotated_by(scope, g2.copy(scope), 0.1).getInnerGeometry()
									.union(geom1);
						}
					}
				}

			}
			if (geom == null || geom.isEmpty()) return null;
			final GamaShape result = new GamaShape(g1, geom);
			result.losePredefinedProperty();
			return result;
		}

		@operator (
				value = { "union" },
				expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				usages = { @usage (
						value = "if the right-operand is a container of points, geometries or agents, returns the geometry resulting from the union all the geometries") },
				examples = { @example (
						value = "union([geom1, geom2, geom3])",
						equals = "a geometry corresponding to union between geom1, geom2 and geom3",
						isExecutable = false) })
		@no_test // test already done in Spatial tests Models
		public static IShape union(final IScope scope, final IContainer<?, IShape> elements) {
			try {
				return Cast.asGeometry(scope, elements, false);
			} catch (final GamaRuntimeException e) {
				return null;
			}
		}

		@operator (
				value = IKeyword.MINUS,
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				usages = @usage (
						value = "if both operands are a point, a geometry or an agent, returns the geometry resulting from the difference between both geometries",
						examples = @example (
								value = "geom1 - geom2",
								equals = "a geometry corresponding to difference between geom1 and geom2",
								isExecutable = false)))
		@no_test // test already done in Spatial tests Models
		public static IShape minus(final IScope scope, final IShape g1, final IShape g2) {
			if (g1 == null || g2 == null || g1.getInnerGeometry() == null || g2.getInnerGeometry() == null) return g1;
			final Geometry res = difference(g1.getInnerGeometry(), g2.getInnerGeometry());
			if (res != null && !res.isEmpty()) {
				final GamaShape result = new GamaShape(g1, res);
				result.losePredefinedProperty();
				return result;
			}
			return null;
		}

		@operator (
				value = IKeyword.MINUS,
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				usages = @usage (
						value = "if the right-operand is a list of points, geometries or agents, returns the geometry resulting from the difference between the left-geometry and all of the right-geometries",
						examples = @example (
								value = "rectangle(10,10) - [circle(2), square(2)]",
								equals = "rectangle(10,10) - (circle(2) + square(2))")))
		public static IShape minus(final IScope scope, final IShape g1, final IContainer<?, IShape> agents) {
			if (g1 == null || agents == null || g1.getInnerGeometry() == null || agents.isEmpty(scope)) return g1;
			Geometry geom1 = GeometryUtils.GEOMETRY_FACTORY.createGeometry(g1.getInnerGeometry());
			for (final IShape ag : agents.iterable(scope)) {
				if (ag != null && ag.getInnerGeometry() != null) {
					geom1 = difference(geom1, ag.getInnerGeometry());
					if (geom1 == null || geom1.isEmpty()) return null;
				}
			}
			if (geom1 == null || geom1.isEmpty()) return null;
			final GamaShape result = new GamaShape(g1, geom1);
			result.losePredefinedProperty();
			return result;
		}

		private static Geometry difference(final Geometry first, final Geometry g2) {
			Geometry g1 = first;
			if (g2 instanceof GeometryCollection) {
				final GeometryCollection g2c = (GeometryCollection) g2;
				final int nb = g2c.getNumGeometries();
				for (int i = 0; i < nb; i++) {
					g1 = difference(g1, g2c.getGeometryN(i));
					if (g1 == null || g1.isEmpty()) return null;
				}
				return g1;
			}
			try {
				return g1.difference(g2);
			} catch (AssertionFailedException | TopologyException e) {
				try {
					final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
					return GeometryPrecisionReducer.reducePointwise(g1, pm)
							.difference(GeometryPrecisionReducer.reducePointwise(g2, pm));
				} catch (final RuntimeException e1) {
					try {
						return g1.buffer(0, 10, BufferParameters.CAP_FLAT)
								.difference(g2.buffer(0, 10, BufferParameters.CAP_FLAT));
					} catch (final TopologyException e2) {
						try {
							final PrecisionModel pm = new PrecisionModel(100000d);
							return GeometryPrecisionReducer.reduce(g1, pm)
									.difference(GeometryPrecisionReducer.reduce(g2, pm));
						} catch (final RuntimeException e3) {
							try {
								return EnhancedPrecisionOp.difference(g1, g2);
							} catch (final RuntimeException e4) {
								try {
									return g1.difference(g2.buffer(Math.min(0.01, g2.getArea() / 1000), 10,
											BufferParameters.CAP_FLAT));
								} catch (final RuntimeException last) {
									return null; // return g1; ??
								}
							}
						}
					}
				}
			}
		}

		@operator (
				value = { "add_point" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.POINT, IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				value = "A new geometry resulting from the addition of the right point (coordinate) to the left-hand geometry. Note that adding a point to a line or polyline will always return a closed contour. Also note that the position at which the added point will appear in the geometry is not necessarily the last one, as points are always ordered in a clockwise fashion in geometries",
				examples = { @example (
						value = "polygon([{10,10},{10,20},{20,20}]) add_point {20,10}",
						returnType = "geometry",
						equals = "polygon([{10,10},{10,20},{20,20},{20,10}])") })
		public static IShape add_point(final IScope scope, final IShape g, final ILocation p) {
			if (p == null || g == null) return g;
			final Coordinate point = (Coordinate) p;
			final Geometry geometry = g.getInnerGeometry();
			Geometry geom_Tmp = null;
			if (geometry instanceof Point) {
				final Coordinate[] coord = new Coordinate[2];
				coord[0] = geometry.getCoordinate();
				coord[1] = point;
				geom_Tmp = GeometryUtils.GEOMETRY_FACTORY.createLineString(coord);
			} else if (geometry instanceof MultiPoint) {
				final Coordinate[] coordinates = new Coordinate[geometry.getNumPoints() + 1];
				coordinates[coordinates.length - 1] = GeometryUtils.toCoordinate(p);
				geom_Tmp = GeometryUtils.GEOMETRY_FACTORY.createMultiPoint(coordinates);
			} else if (geometry instanceof LineString) {
				geom_Tmp = createLineStringWithPoint(geometry, point);
			} else if (geometry instanceof MultiLineString) {
				Geometry closestGeom = null;
				double distMin = Double.MAX_VALUE;
				int id = -1;
				final Point pt = GeometryUtils.GEOMETRY_FACTORY.createPoint(point);
				for (int i = 0; i < geometry.getNumGeometries(); i++) {
					final Geometry geom = geometry.getGeometryN(i);
					final double dist = geom.distance(pt);
					if (dist < distMin) {
						distMin = dist;
						closestGeom = geom;
						id = i;
					}
				}
				final LineString[] lineStrings = new LineString[geometry.getNumGeometries()];
				for (int i = 0; i < geometry.getNumGeometries(); i++) {
					if (i != id) {
						lineStrings[i] = (LineString) geometry.getGeometryN(i);
					} else {
						lineStrings[i] = (LineString) createLineStringWithPoint(closestGeom, point);
					}
				}
				geom_Tmp = GeometryUtils.GEOMETRY_FACTORY.createMultiLineString(lineStrings);
			} else if (geometry instanceof Polygon) {
				geom_Tmp = createPolygonWithPoint(geometry, point);
			} else if (geometry instanceof MultiPolygon) {
				Geometry closestGeom = null;
				double distMin = Double.MAX_VALUE;
				int id = -1;
				final Point pt = GeometryUtils.GEOMETRY_FACTORY.createPoint(point);
				for (int i = 0; i < geometry.getNumGeometries(); i++) {
					final Geometry geom = geometry.getGeometryN(i);
					final double dist = geom.distance(pt);
					if (dist < distMin) {
						distMin = dist;
						closestGeom = geom;
						id = i;
					}
				}
				final Polygon[] polygons = new Polygon[geometry.getNumGeometries()];
				for (int i = 0; i < geometry.getNumGeometries(); i++) {
					if (i != id) {
						polygons[i] = (Polygon) geometry.getGeometryN(i);
					} else {
						polygons[i] = (Polygon) createPolygonWithPoint(closestGeom, point);
					}
				}
				geom_Tmp = GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(polygons);
			}
			if (geom_Tmp != null) {
				final GamaShape result = new GamaShape(g, geom_Tmp);
				result.losePredefinedProperty();
				return result;
			}
			return g;
		}

		private static Geometry createPolygonWithPoint(final Geometry geometry, final Coordinate point) {
			double simpleMinLength = Double.MAX_VALUE;
			Geometry simpleMinGeom = null;
			double complexMinLength = Double.MAX_VALUE;
			Geometry complexMinGeom = null;
			final int nbPts = ((Polygon) geometry).getExteriorRing().getCoordinates().length;
			for (int index = 0; index <= nbPts; index++) {
				final Coordinate[] coord = new Coordinate[nbPts + 1];
				for (int i = 0; i < index; i++) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[index] = point;
				for (int i = index + 1; i < coord.length; i++) {
					coord[i] = geometry.getCoordinates()[i - 1];
				}
				final LinearRing[] lrs = new LinearRing[((Polygon) geometry).getNumInteriorRing()];
				for (int i = 0; i < lrs.length; i++) {
					lrs[i] = ((Polygon) geometry).getInteriorRingN(i);
				}
				final Geometry g = GeometryUtils.GEOMETRY_FACTORY
						.createPolygon(GeometryUtils.GEOMETRY_FACTORY.createLinearRing(coord), lrs);
				if (g.isValid()) {
					if (simpleMinLength > g.getArea()) {
						simpleMinLength = g.getArea();
						simpleMinGeom = g;
					}
				} else {
					if (complexMinLength > g.getArea()) {
						complexMinLength = g.getArea();
						complexMinGeom = g;
					}
				}
			}
			if (simpleMinGeom != null) return simpleMinGeom;
			return complexMinGeom;
		}

		private static Geometry createLineStringWithPoint(final Geometry geometry, final Coordinate point) {
			double simpleMinLength = Double.MAX_VALUE;
			Geometry simpleMinGeom = null;
			double complexMinLength = Double.MAX_VALUE;
			Geometry complexMinGeom = null;
			for (int index = 0; index <= geometry.getCoordinates().length; index++) {
				final Coordinate[] coord = new Coordinate[geometry.getCoordinates().length + 1];
				for (int i = 0; i < index; i++) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[index] = point;
				for (int i = index + 1; i < coord.length; i++) {
					coord[i] = geometry.getCoordinates()[i - 1];
				}
				final Geometry g = GeometryUtils.GEOMETRY_FACTORY.createLineString(coord);
				if (g.isValid()) {
					if (simpleMinLength > g.getLength()) {
						simpleMinLength = g.getLength();
						simpleMinGeom = g;
					}
				} else {
					if (complexMinLength > g.getLength()) {
						complexMinLength = g.getLength();
						complexMinGeom = g;
					}
				}
			}
			if (simpleMinGeom != null) return simpleMinGeom;
			return complexMinGeom;
		}

		/*
		 * private static int indexClosestSegment(final Geometry geom, final Coordinate coord) { int index = -1; final
		 * Point pt = GeometryUtils.GEOMETRY_FACTORY.createPoint(coord); double distMin = Double.MAX_VALUE; for (int i =
		 * 0; i < geom.getCoordinates().length - 1; i++) { final Coordinate cc = geom.getCoordinates()[i]; if
		 * (cc.equals(coord)) { return -1; } final Coordinate[] coordinates = new Coordinate[2]; coordinates[0] = cc;
		 * coordinates[1] = geom.getCoordinates()[i + 1]; final Geometry geom_Tmp =
		 * GeometryUtils.GEOMETRY_FACTORY.createLineString(coordinates); final double dist = geom_Tmp.distance(pt); if
		 * (dist < distMin) { distMin = dist; index = i; } } if (geom.getCoordinates()[geom.getCoordinates().length -
		 * 1].equals(coord)) { return -1; } return index; }
		 */

		@operator (
				value = "masked_by",
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.OBSTACLE })
		@doc (
				examples = { @example (
						value = "perception_geom masked_by obstacle_list",
						equals = "the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list.",
						isExecutable = false) })
		public static IShape masked_by(final IScope scope, final IShape source, final IContainer<?, IShape> obstacles,
				final Integer prec) {
			final Integer precision = prec == null ? 120 : prec;
			final IAgent a = scope.getAgent();
			final List<IShape> obst =
					obstacles == null ? new ArrayList<>() : obstacles.listValue(scope, Types.GEOMETRY, false);
			final ILocation location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			final Geometry visiblePercept = GeometryUtils.GEOMETRY_FACTORY.createGeometry(source.getInnerGeometry());
			final boolean isPoint = source.isPoint();
			if (obstacles != null && !obstacles.isEmpty(scope)) {
				final Geometry pt = GeometryUtils.GEOMETRY_FACTORY.createPoint(GeometryUtils.toCoordinate(location));
				final Geometry locG = pt.buffer(0.01).getEnvelope();
				double percep_dist = 0;
				for (final ILocation p : source.getPoints()) {
					final double dist = location.euclidianDistanceTo(p);
					if (dist > percep_dist) { percep_dist = dist; }
				}
				final Geometry gbuff = pt.buffer(percep_dist, precision / 4);
				final List<IShape> geoms = new ArrayList<>();
				for (int k = 1; k < gbuff.getNumPoints(); k++) {
					final IList coordinates = GamaListFactory.create(Types.POINT, 4);
					coordinates.add(location);
					coordinates.add(new GamaPoint(gbuff.getCoordinates()[k - 1]));
					coordinates.add(new GamaPoint(gbuff.getCoordinates()[k]));
					coordinates.add(location);
					final IShape gg =
							Spatial.Operators.inter(scope, source, Spatial.Creation.polygon(scope, coordinates));

					if (gg != null && (isPoint || !gg.isPoint())) {
						final IShape s =
								new GamaShape(GeometryUtils.geometryCollectionManagement(gg.getInnerGeometry()));
						geoms.add(s);
					}
				}
				final IList<IShape> geomsVisible = GamaListFactory.create();
				final PreparedGeometry ref = PreparedGeometryFactory.prepare(locG);

				for (final IShape geom : geoms) {
					if (!intersection(geom, obst)) {
						geomsVisible.addValue(scope, geom);
					} else {
						final IShape perceptReal = difference(scope, geom, obst, ref);

						if (perceptReal != null && (isPoint || !perceptReal.isPoint())) {
							geomsVisible.addValue(scope, perceptReal);
						}
					}
				}
				IList<IShape> geomVisibleF = GamaListFactory.create(Types.GEOMETRY);
				for (final IShape geom : geomsVisible) {
					if (geom.getGeometries().size() > 1) {
						for (IShape g : geom.getGeometries()) {
							if (g.intersects(location)) { geomVisibleF.add(g); }
						}

					} else {
						geomVisibleF.add(geom);
					}
				}
				boolean isPolygon = false;
				boolean isLine = false;
				for (final IShape geom : geomVisibleF) {
					isLine = isLine || geom.isLine();
					isPolygon = isPolygon || !geom.isPoint() && !geom.isLine();
				}
				final boolean isPolygonF = isPolygon;
				final boolean isLineF = isLine;

				geomVisibleF.removeIf(g -> (isPolygonF || isLineF) && g.isPoint() && isPolygonF && g.isLine());
				if (!geomVisibleF.isEmpty(scope)) {
					IShape result = Cast.asGeometry(scope, geomVisibleF, false);
					if (result == null || result.getInnerGeometry() == null) {
						geomVisibleF.stream().forEach(g -> Spatial.Transformations.enlarged_by(scope, g, 0.1));
						result = Cast.asGeometry(scope, geomVisibleF, false);
					}
					if (result == null || result.getInnerGeometry() == null) return null;
					if (result.getInnerGeometry() instanceof GeometryCollection) {

						result = Spatial.Transformations.enlarged_by(scope, result, 0.1);
					}
					return result;
				}
				return null;
			}
			return new GamaShape(visiblePercept);
		}

		private static boolean intersection(final IShape geom, final List<IShape> geoms) {
			if (geom == null) return false;
			for (final IShape g : geoms) {
				if (g != null && geom.intersects(g)) return true;
			}
			return false;
		}

		private static IShape difference(final IScope scope, final IShape geom, final List<IShape> geoms,
				final PreparedGeometry ref) {
			if (geom == null) return null;
			IShape gR = new GamaShape(geom);
			for (final IShape g : geoms) {

				if (g != null && geom.intersects(g)) {
					gR = Spatial.Operators.minus(scope, gR, g);
					if (gR == null) return null;
					if (gR.getGeometries().size() > 1) {
						for (final IShape sh : gR.getGeometries()) {
							if (!ref.disjoint(sh.getInnerGeometry())) {
								gR = sh;
								break;
							}
						}
					} else if (ref.disjoint(gR.getInnerGeometry())) return null;
				}
			}

			return gR;
		}

		@operator (
				value = "masked_by",
				category = { IOperatorCategory.SPATIAL },
				concept = {})
		@doc (
				examples = { @example (
						value = "perception_geom masked_by obstacle_list",
						equals = "the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list.",
						isExecutable = false) })
		public static IShape masked_by(final IScope scope, final IShape source, final IContainer<?, IShape> obstacles) {
			return masked_by(scope, source, obstacles, null);
		}

		@operator (
				value = "split_at",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL })
		@doc (
				value = "The two part of the left-operand lines split at the given right-operand point",
				usages = { @usage ("if the left-operand is a point or a polygon, returns an empty list") },
				examples = { @example (
						value = "polyline([{1,2},{4,6}]) split_at {7,6}",
						equals = "[polyline([{1.0,2.0},{7.0,6.0}]), polyline([{7.0,6.0},{4.0,6.0}])]") })
		public static IList<IShape> split_at(final IShape geom, final ILocation pt) {
			final IList<IShape> lines = GamaListFactory.create(Types.GEOMETRY);
			List<Geometry> geoms = null;
			if (geom.getInnerGeometry() instanceof LineString) {
				final Coordinate[] coords = ((LineString) geom.getInnerGeometry()).getCoordinates();
				final Point pt1 = GeometryUtils.GEOMETRY_FACTORY.createPoint(pt.getLocation().toGamaPoint());
				final int nb = coords.length;
				int indexTarget = -1;
				double distanceT = Double.MAX_VALUE;
				for (int i = 0; i < nb - 1; i++) {
					final Coordinate s = coords[i];
					final Coordinate t = coords[i + 1];
					final Coordinate[] seg = { s, t };
					final Geometry segment = GeometryUtils.GEOMETRY_FACTORY.createLineString(seg);
					final double distT = segment.distance(pt1);
					if (distT < distanceT) {
						distanceT = distT;
						indexTarget = i;
					}
				}
				int nbSp = indexTarget + 2;
				final Coordinate[] coords1 = new Coordinate[nbSp];
				for (int i = 0; i <= indexTarget; i++) {
					coords1[i] = coords[i];
				}
				coords1[indexTarget + 1] = new GamaPoint(pt.getLocation().toGamaPoint());

				nbSp = coords.length - indexTarget;
				final Coordinate[] coords2 = new Coordinate[nbSp];
				coords2[0] = new GamaPoint(pt.getLocation().toGamaPoint());
				int k = 1;
				for (int i = indexTarget + 1; i < coords.length; i++) {
					coords2[k] = coords[i];
					k++;
				}
				final List<Geometry> geoms1 = new ArrayList<>();
				geoms1.add(GeometryUtils.GEOMETRY_FACTORY.createLineString(coords1));
				geoms1.add(GeometryUtils.GEOMETRY_FACTORY.createLineString(coords2));
				geoms = geoms1;
			} else if (geom.getInnerGeometry() instanceof MultiLineString) {
				final Point point = GeometryUtils.GEOMETRY_FACTORY.createPoint((Coordinate) pt);
				final MultiLineString ml = (MultiLineString) geom.getInnerGeometry();
				Geometry geom2 = ml.getGeometryN(0);
				double distMin = geom2.distance(point);
				for (int i = 1; i < ml.getNumGeometries(); i++) {
					final Geometry gg = ml.getGeometryN(i);
					final double dist = gg.distance(point);
					if (dist <= distMin) {
						geom2 = gg;
						distMin = dist;
					}
				}
				final Coordinate[] coords = ((LineString) geom2).getCoordinates();
				final Point pt1 =
						GeometryUtils.GEOMETRY_FACTORY.createPoint(new GamaPoint(pt.getLocation().toGamaPoint()));
				final int nb = coords.length;
				int indexTarget = -1;
				double distanceT = Double.MAX_VALUE;
				for (int i = 0; i < nb - 1; i++) {
					final Coordinate s = coords[i];
					final Coordinate t = coords[i + 1];
					final Coordinate[] seg = { s, t };
					final Geometry segment = GeometryUtils.GEOMETRY_FACTORY.createLineString(seg);
					final double distT = segment.distance(pt1);
					if (distT < distanceT) {
						distanceT = distT;
						indexTarget = i;
					}
				}
				int nbSp = indexTarget + 2;
				final Coordinate[] coords1 = new Coordinate[nbSp];
				for (int i = 0; i <= indexTarget; i++) {
					coords1[i] = coords[i];
				}
				coords1[indexTarget + 1] = new GamaPoint(pt.getLocation().toGamaPoint());

				nbSp = coords.length - indexTarget;
				final Coordinate[] coords2 = new Coordinate[nbSp];
				coords2[0] = new GamaPoint(pt.getLocation().toGamaPoint());
				int k = 1;
				for (int i = indexTarget + 1; i < coords.length; i++) {
					coords2[k] = coords[i];
					k++;
				}
				final List<Geometry> geoms1 = new ArrayList<>();
				geoms1.add(GeometryUtils.GEOMETRY_FACTORY.createLineString(coords1));
				geoms1.add(GeometryUtils.GEOMETRY_FACTORY.createLineString(coords2));
				geoms = geoms1;
			}
			if (geoms != null) {
				for (final Geometry g : geoms) {
					lines.add(new GamaShape(g));
				}
			}
			// if (geom.getAttributes() != null) {
			for (final IShape li : lines) {
				li.copyAttributesOf(geom);
				// geom.forEachAttribute((s, at) -> {
				// li.setAttribute(s, at);
				// return true;
				// });
			}
			// }

			return lines;
		}
	}

	public static abstract class Transformations {

		@operator (
				value = "convex_hull",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry corresponding to the convex hull of the operand.",
				examples = { @example (
						value = "convex_hull(self)",
						equals = "the convex hull of the geometry of the agent applying the operator",
						test = false) })
		@no_test
		public static IShape convex_hull(final IScope scope, final IShape g) {
			return new GamaShape(g, g.getInnerGeometry().convexHull());
		}

		@operator (
				value = { IKeyword.MULTIPLY, "scaled_by" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				usages = { @usage (
						value = "if the left-hand operand is a geometry and the right-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) scaled by the right-hand operand coefficient",
						examples = {
								// @example(value = "shape * 2",
								// equals = "a geometry corresponding to the geometry of
								// the agent applying the operator scaled by a
								// coefficient of 2",
								@example (
										value = "circle(10) * 2",
										equals = "circle(20)",
										test = false),
								@example (
										value = "(circle(10) * 2).location with_precision 9",
										equals = "(circle(20)).location with_precision 9"),
								@example (
										value = "(circle(10) * 2).height with_precision 9",
										equals = "(circle(20)).height with_precision 9",
										returnType = "float") }) })
		public static IShape scaled_by(final IScope scope, final IShape g, final Double coefficient) {
			return new GamaShape(g, null, null, null, coefficient);
		}

		@operator (
				value = { IKeyword.MULTIPLY, "scaled_by" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				usages = { @usage (
						value = "if the left-hand operand is a geometry and the right-hand operand a point, returns a geometry corresponding to the left-hand operand (geometry, agent, point) scaled by the right-hand operand coefficients in the 3 dimensions",
						examples = { @example (
								value = "shape * {0.5,0.5,2}",
								equals = "a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 0.5 in x, 0.5 in y and 2 in z",
								test = false) }) })
		@test ("geometry g <- cube (2);" + "float v1 <- g.area * g.height; " + "g <- g * {5, 5, 5};"
				+ "float v2 <- g.area * g.height;  " + "v1 < v2")
		public static IShape scaled_by(final IScope scope, final IShape g, final GamaPoint coefficients) {
			return new GamaShape(g, null, null, null, Scaling3D.of(coefficients), false);
		}

		@operator (
				value = { "scaled_to" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "allows to restrict the size of a geometry so that it fits in the envelope {width, height, depth} defined by the second operand",
				examples = { @example (
						value = "shape scaled_to {10,10}",
						equals = "a geometry corresponding to the geometry of the agent applying the operator scaled so that it fits a square of 10x10",
						test = false) })
		@test ("geometry g <- cube (2);" + "float v1 <- g.area * g.height; " + "g <- g scaled_to {20,20};"
				+ "float v2 <- g.area * g.height;  " + "v1 < v2")
		public static IShape scaled_to(final IScope scope, final IShape g, final GamaPoint bounds) {
			return new GamaShape(g, null, null, null, Scaling3D.of(bounds), true);
		}

		@operator (
				value = { IKeyword.PLUS, "buffer", "enlarged_by" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "",
				deprecated = "Please use +(distance, number_of_segments) or +(distance, number_of_segments, end_cap) instead",
				usages = @usage (
						value = "if the left-hand operand is a geometry and the right-hand operand a map (with [distance::float, quadrantSegments:: int (the number of line segments used to represent a quadrant of a circle), endCapStyle::int (1: (default) a semi-circle, 2: a straight line perpendicular to the end segment, 3: a half-square)]), returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged considering the right-hand operand parameters",
						examples = @example (
								value = "shape + [\"distance\"::5.0, \"quadrantSegments\"::4, \"endCapStyle\":: 2]",
								equals = "a geometry corresponding to the geometry of the agent applying the operator enlarged by a distance of 5, with 4 segments to represent a quadrant of a circle and a straight line perpendicular to the end segment",
								test = false)))
		@no_test (Reason.DEPRECATED)
		public static IShape enlarged_by(final IScope scope, final IShape g, final IMap parameters) {
			final Double distance = Cast.asFloat(scope, parameters.get("distance"));
			final Integer quadrantSegments = Cast.asInt(scope, parameters.get("quadrantSegments"));
			final Integer endCapStyle = Cast.asInt(scope, parameters.get("endCapStyle"));
			if (endCapStyle == null) return new GamaShape(g, g.getInnerGeometry().buffer(distance, quadrantSegments));
			return new GamaShape(g, g.getInnerGeometry().buffer(distance, quadrantSegments, endCapStyle));
		}

		@operator (
				value = { IKeyword.PLUS, "buffer", "enlarged_by" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				usages = { @usage (
						value = "if the left-hand operand is a geometry and the right-hand operands a float and an integer, returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged by the first right-hand operand (distance), using a number of segments equal to the second right-hand operand",
						examples = { @example (
								value = "circle(5) + (5,32)",
								equals = "circle(10)",
								test = false) }) })
		@test ("(circle(5) + (5,32)).height with_precision 5 = 20.0")
		public static IShape enlarged_by(final IScope scope, final IShape g, final Double size,
				final Integer numberOfSegments) {
			if (g == null) return null;
			final Geometry gg = g.getInnerGeometry().buffer(size, numberOfSegments);
			if (gg != null && !gg.isEmpty()) return new GamaShape(g, gg);
			return null;
		}

		@operator (
				value = { IKeyword.PLUS, "buffer", "enlarged_by" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				usages = { @usage (
						value = "if the left-hand operand is a geometry and the right-hand operands a float, an integer and one of #round, #square or #flat, returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged by the first right-hand operand (distance), using a number of segments equal to the second right-hand operand and a flat, square or round end cap style",
						examples = { @example (
								value = "circle(5) + (5,32,#round)",
								equals = "circle(10)",
								test = false) }) })
		@test ("(circle(5) + (5,32,#round)).height with_precision 5 = 20.0")
		public static IShape enlarged_by(final IScope scope, final IShape g, final Double size,
				final Integer numberOfSegments, final Integer endCap) {
			if (g == null) return null;
			final Geometry gg = g.getInnerGeometry().buffer(size, numberOfSegments, endCap);
			if (gg != null && !gg.isEmpty()) return new GamaShape(g, gg);
			return null;
		}

		@operator (
				value = { IKeyword.PLUS, "buffer", "enlarged_by" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				usages = { @usage (
						value = "if the left-hand operand is a geometry and the right-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged by the right-hand operand distance. The number of segments used by default is 8 and the end cap style is #round",
						examples = { @example (
								value = "circle(5) + 5",
								equals = "circle(10)",
								test = false) }) })
		@test ("(circle(5) + 5).height with_precision 1 = 20.0")
		@test ("(circle(5) + 5).location with_precision 9 = (circle(10)).location with_precision 9")
		public static IShape enlarged_by(final IScope scope, final IShape g, final Double size) {
			if (g == null) return null;
			final Geometry gg = g.getInnerGeometry().buffer(size);
			if (gg != null && !gg.isEmpty()) return new GamaShape(g, gg);
			return null;
		}

		@operator (
				value = { "-", "reduced_by" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				usages = { @usage (
						value = "if the left-hand operand is a geometry and the right-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) reduced by the right-hand operand distance",
						examples = { @example (
								value = "shape - 5",
								equals = "a geometry corresponding to the geometry of the agent applying the operator reduced by a distance of 5",
								test = false) }) })
		@test ("(square(20) - 5).area = 100.0")
		public static IShape reduced_by(final IScope scope, final IShape g, final Double size) {
			if (g == null) return null;
			return enlarged_by(scope, g, -size);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a rotation (of a given angle) to the agent geometry
		 *
		 * @param args
		 *            : angle --: double, degree
		 *
		 */
		@operator (
				value = "rotated_by",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry resulting from the application of a rotation by the right-hand operand angle (degree) to the left-hand operand (geometry, agent, point)",
				masterDoc = true,
				examples = { @example (
						value = "self rotated_by 45",
						equals = "the geometry resulting from a 45 degrees rotation to the geometry of the agent applying the operator.",
						test = false) },
				see = { "transformed_by", "translated_by" })
		@test ("(( square(5) rotated_by 45).width with_precision 2 = 7.07)")
		public static IShape rotated_by(final IScope scope, final IShape g1, final Double angle) {
			if (g1 == null) return null;
			return new GamaShape(g1, null, new AxisAngle(angle), null);
		}

		@operator (
				value = "inverse_rotation",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "The inverse rotation. It is a rotation around the same axis with the opposite angle.",
				masterDoc = true,
				examples = { @example (
						value = "inverse_rotation(38.0::{1,1,1})",
						equals = "-38.0::{1,1,1}",
						test = false) },
				see = { "rotation_composition, normalized_rotation" })
		@test ("inverse_rotation(38.0::{1,1,1}) = (-38.0::{1,1,1})")
		public static GamaPair<Double, GamaPoint> inverse_rotation(final IScope scope,
				final GamaPair<Double, GamaPoint> rotation) {
			return new GamaPair(-rotation.key, rotation.value, Types.FLOAT, Types.POINT);
		}

		@operator (
				value = "normalized_rotation",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "The rotation normalized according to Euler formalism with a positive angle, such that each rotation has a unique set of parameters (positive angle, normalize axis rotation).",
				masterDoc = true,
				examples = { @example (
						value = "normalized_rotation(-38.0::{1,1,1})",
						equals = "38.0::{-0.5773502691896258,-0.5773502691896258,-0.5773502691896258}",
						test = false) },
				see = { "rotation_composition, inverse_rotation" })
		@test ("normalized_rotation(-38::{1,1,1})=(38.0::{-0.5773502691896258,-0.5773502691896258,-0.5773502691896258})")
		public static GamaPair<Double, GamaPoint> normalized_rotation(final IScope scope, final GamaPair rotation) {
			final GamaPair<Double, GamaPoint> rot = (GamaPair<Double, GamaPoint>) GamaType
					.from(Types.PAIR, Types.FLOAT, Types.POINT).cast(scope, rotation, null, false);
			final GamaPoint axis = rot.getValue();
			final double norm = Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
			axis.x = Math.signum(rot.getKey()) * axis.x / norm;
			axis.y = Math.signum(rot.getKey()) * axis.y / norm;
			axis.z = Math.signum(rot.getKey()) * axis.z / norm;
			return new GamaPair(Math.signum(rot.getKey()) * rot.getKey(), axis, Types.FLOAT, Types.POINT);
		}

		@operator (
				value = "rotation_composition",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "The rotation resulting from the composition of the rotations in the list, from left to right. Angles are in degrees.",
				masterDoc = true,
				examples = { @example (
						value = "rotation_composition([38.0::{1,1,1},90.0::{1,0,0}])",
						equals = "115.22128507898108::{0.9491582126366207,0.31479943993669307,-0.0}",
						test = false) },
				see = { "inverse_rotation" })
		// public static GamaPair<Double, GamaPoint> rotation_composition(final IScope scope,
		// final GamaList<GamaPair<Double, GamaPoint>> rotation_list) {
		// Rotation3D rotation = new Rotation3D(new GamaPoint(1, 0, 0), 0.0);
		// for (GamaPair<Double, GamaPoint> rot : rotation_list) {
		// rotation = rotation.applyTo(new Rotation3D(rot.value, 2 * Math.PI / 360 * rot.key));
		// }
		// return new GamaPair(180 / Math.PI * rotation.getAngle(), rotation.getAxis(), Types.FLOAT, Types.POINT);
		// }
		@test ("normalized_rotation(rotation_composition(38.0::{1,1,1},90.0::{1,0,0}))=normalized_rotation(115.22128507898108::{0.9491582126366207,0.31479943993669307,-0.0})")
		public static GamaPair<Double, GamaPoint> rotation_composition(final IScope scope,
				final IList<GamaPair> rotation_list) {
			Rotation3D rotation = new Rotation3D(new GamaPoint(1, 0, 0), 0.0);
			for (final GamaPair element : rotation_list) {
				final GamaPair<Double, GamaPoint> rot = (GamaPair<Double, GamaPoint>) GamaType
						.from(Types.PAIR, Types.FLOAT, Types.POINT).cast(scope, element, null, false);
				rotation = rotation.applyTo(new Rotation3D(rot.value, 2 * Math.PI / 360 * rot.key));
			}
			return new GamaPair(180 / Math.PI * rotation.getAngle(), rotation.getAxis(), Types.FLOAT, Types.POINT);
		}

		@operator (
				value = "rotated_by",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry resulting from the application of a rotation by the operand angles (degree)"
						+ " along the operand axis (last operand) to the left-hand operand (geometry, agent, point)",
				masterDoc = true,
				examples = { @example (
						value = "rotated_by(pyramid(10),45.0, {1,0,0})",
						equals = "the geometry resulting from a 45 degrees rotation along the {1,0,0} vector to the geometry of "
								+ "the agent applying the operator.",
						test = false) },
				see = { "transformed_by", "translated_by" })
		@no_test
		public static IShape rotated_by(final IScope scope, final IShape g1, final Double rotation,
				final GamaPoint vector) {
			if (g1 == null) return null;
			if (vector.x == 0d && vector.y == 0d && vector.z == 0d) return g1;
			return new GamaShape(g1, null, new AxisAngle(vector, rotation), g1.getLocation());
		}

		@operator (
				value = "rotated_by",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				usages = {
						@usage ("When used  with a  point and  a pair angle::point, it returns a point resulting from the application of the right-hand rotation operand (angles in degree)"
								+ " to the left-hand operand point") })
		@no_test
		public static GamaPoint rotated_by(final IScope scope, final GamaPoint p1, final GamaPair rotation) {
			if (p1 == null) return null;
			final GamaPair<Double, GamaPoint> rot = (GamaPair<Double, GamaPoint>) GamaType
					.from(Types.PAIR, Types.FLOAT, Types.POINT).cast(scope, rotation, null, false);
			final GamaPoint p2 = new GamaPoint(p1);
			new Rotation3D(rot.getValue(), 2 * Math.PI / 360 * rot.getKey()).applyTo(p2);
			return p2;
		}

		@operator (
				value = "rotated_by",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				examples = { @example (
						value = "rotated_by(pyramid(10),45.0::{1,0,0})",
						equals = "the geometry resulting from a 45 degrees rotation along the {1,0,0} vector to the geometry of "
								+ "the agent applying the operator.",
						test = false) },
				see = { "transformed_by", "translated_by" })
		@no_test
		public static IShape rotated_by(final IScope scope, final IShape g1, final GamaPair rotation) {
			final GamaPair<Double, GamaPoint> rot = (GamaPair<Double, GamaPoint>) GamaType
					.from(Types.PAIR, Types.FLOAT, Types.POINT).cast(scope, rotation, null, false);
			if (g1 == null || rot == null) return null;
			return new GamaShape(g1, null, new AxisAngle(rot.getValue(), rot.getKey()), g1.getLocation());
		}

		@operator (
				value = "rotated_by",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				usages = { @usage ("the right-hand operand representing  the angle can be a float or an integer") })
		@no_test
		public static IShape rotated_by(final IScope scope, final IShape g1, final Integer angle) {
			if (g1 == null) return null;
			if (angle == null) return g1.copy(scope);
			// if ( g1.isPoint() ) { return g1.copy(scope); }
			return new GamaShape(g1, null, new AxisAngle(angle.doubleValue()), null);

		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a affinite operation (of a given coefficient and angle)to the agent geometry. Angle is
		 *             given by the point.x ; Coefficient by the point.y
		 *
		 * @param args
		 *            : coefficient --: double; angle --: double, rad
		 *
		 */
		@operator (
				value = "transformed_by",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry resulting from the application of a rotation and a scaling (right-operand : "
						+ "point {angle(degree), scale factor} of the left-hand operand (geometry, agent, point)",
				examples = { @example (
						value = "self transformed_by {45, 0.5}",
						equals = "the geometry resulting from 45 degrees rotation and 50% scaling of the geometry "
								+ "of the agent applying the operator.",
						test = false) },
				see = { "rotated_by", "translated_by" })
		public static IShape transformed_by(final IScope scope, final IShape g, final GamaPoint p) {
			if (g == null) return null;
			return scaled_by(scope, rotated_by(scope, g, p.x), p.y);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a translation operation (vector (dx, dy)) to the agent geometry
		 *
		 * @param args
		 *            : dx --: double; dy --: double
		 *
		 */
		@operator (
				value = "translated_by",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry resulting from the application of a translation by the right-hand operand distance to the left-hand operand (geometry, agent, point)",
				examples = { @example (
						value = "self translated_by {10,10,10}",
						equals = "the geometry resulting from applying the translation to the left-hand geometry (or agent).",
						test = false) },
				see = { "rotated_by", "transformed_by" })
		public static IShape translated_by(final IScope scope, final IShape g, final GamaPoint p)
				throws GamaRuntimeException {
			if (g == null) return null;
			return at_location(scope, g, msi.gaml.operators.Points.add((GamaPoint) g.getLocation(), p));
		}

		@operator (
				value = { "at_location", "translated_to" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A geometry resulting from the tran of a translation to the right-hand operand point of the left-hand operand (geometry, agent, point)",
				examples = { @example (
						value = "self at_location {10, 20}",
						equals = "the geometry resulting from a translation to the location {10, 20} of the left-hand geometry (or agent).",
						test = false),
						@example (
								value = " (box({10, 10 , 5}) at_location point(50,50,0)).location.x",
								equals = "50.0",
								returnType = "float") })
		public static IShape at_location(final IScope scope, final IShape g, final ILocation p)
				throws GamaRuntimeException {
			if (g == null) return null;
			return new GamaShape(g, null, (AxisAngle) null, p);
		}

		@operator (
				value = { "without_holes", "solid" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A geometry corresponding to the operand geometry (geometry, agent, point) without its holes",
				examples = { @example (
						value = "solid(self)",
						equals = "the geometry corresponding to the geometry of the agent applying the operator without "
								+ "its holes.",
						test = false),
						@example (
								value = "without_holes(polygon([{0,50}, {0,0}, {50,0}, {50,50}, {0,50}]) - square(10) at_location {10,10}).area",
								equals = "2500.0",
								returnType = "float") })
		public static IShape without_holes(final IScope scope, final IShape g) {
			if (g == null) return null;
			final Geometry geom = g.getInnerGeometry();
			Geometry result = geom;
			if (geom instanceof Polygon) {
				result = GeometryUtils.GEOMETRY_FACTORY.createPolygon(GeometryUtils.GEOMETRY_FACTORY
						.createLinearRing(((Polygon) geom).getExteriorRing().getCoordinates()), null);
			} else if (geom instanceof MultiPolygon) {
				final MultiPolygon mp = (MultiPolygon) geom;
				final Polygon[] polys = new Polygon[mp.getNumGeometries()];
				for (int i = 0; i < mp.getNumGeometries(); i++) {
					final Polygon p = (Polygon) mp.getGeometryN(i);
					polys[i] = GeometryUtils.GEOMETRY_FACTORY.createPolygon(
							GeometryUtils.GEOMETRY_FACTORY.createLinearRing(p.getExteriorRing().getCoordinates()),
							null);
				}
				result = GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(polys);
			}
			return new GamaShape(g, result);
		}

		@operator (
				value = "skeletonize",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				usages = {
						@usage ("It can be used with 2 additional float operands: the tolerances for the clipping and for the triangulation") })
		@no_test
		public static IList<IShape> skeletonize(final IScope scope, final IShape g, final Double clippingTolerance,
				final Double triangulationTolerance) {
			final List<LineString> netw = GeometryUtils.squeletisation(scope, g.getInnerGeometry(),
					triangulationTolerance, clippingTolerance, false);
			final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			for (final LineString ls : netw) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator (
				value = "skeletonize",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				usages = {
						@usage ("It can be used with 3 additional float operands: the tolerance for the clipping, the  tolerance for the triangulation, and the approximation for the clipping.") })
		@no_test
		public static IList<IShape> skeletonize(final IScope scope, final IShape g, final Double clippingTolerance,
				final Double triangulationTolerance, final boolean approxiClipping) {
			final List<LineString> netw = GeometryUtils.squeletisation(scope, g.getInnerGeometry(),
					triangulationTolerance, clippingTolerance, approxiClipping);
			final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			for (final LineString ls : netw) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator (
				value = "skeletonize",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				usages = { @usage ("It can be used with 1 additional float operand: the tolerance for the clipping.") })
		@no_test
		public static IList<IShape> skeletonize(final IScope scope, final IShape g, final Double clippingTolerance) {
			final List<LineString> netw =
					GeometryUtils.squeletisation(scope, g.getInnerGeometry(), 0.0, clippingTolerance, false);
			final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			for (final LineString ls : netw) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator (
				value = "skeletonize",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries (polylines) corresponding to the skeleton of the operand geometry (geometry, agent)",
				masterDoc = true,
				examples = { @example (
						value = "skeletonize(self)",
						equals = "the list of geometries corresponding to the skeleton of the geometry of the agent applying the operator.",
						test = false) })
		@test (" // applies only to a square \n " + "length(skeletonize(square(5))) = 1")
		public static IList<IShape> skeletonize(final IScope scope, final IShape g) {
			final List<LineString> netw = GeometryUtils.squeletisation(scope, g.getInnerGeometry(), 0.0, 0.0, false);
			final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			for (final LineString ls : netw) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator (
				value = { "triangulate", "to_triangles" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand geometry (geometry, agent, point)",
				masterDoc = true,
				examples = { @example (
						value = "triangulate(self)",
						equals = "the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.",
						test = false) })
		public static IList<IShape> triangulate(final IScope scope, final IShape g) {
			if (g == null) return null;
			return GeometryUtils.triangulation(scope, g.getInnerGeometry(), 0.0, 0.0, false);
		}

		@operator (
				value = { "triangulate", "to_triangles" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand geometry (geometry, agent, point) with the given tolerance for the clipping",
				masterDoc = true,
				examples = { @example (
						value = "triangulate(self, 0.1)",
						equals = "the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.",
						test = false) })
		@no_test
		public static IList<IShape> triangulate(final IScope scope, final IShape g, final Double clipTolerance) {
			if (g == null) return null;
			return GeometryUtils.triangulation(scope, g.getInnerGeometry(), 0.0, clipTolerance, false);
		}

		@operator (
				value = { "triangulate", "to_triangles" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries (triangles) corresponding to the Delaunay triangulation computed from the list of polylines",
				masterDoc = true,
				examples = { @example (
						value = "triangulate([line([{0,50},{100,50}]), line([{50,0},{50,100}]))",
						equals = "the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.",
						test = false) })
		@no_test
		public static IList<IShape> triangulate(final IScope scope, final IList<IShape> gs) {
			if (gs == null || gs.isEmpty()) return null;
			return GeometryUtils.triangulation(scope, gs);
		}

		@operator (
				value = { "triangulate", "to_triangles" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand geometry (geometry, agent, point) with the given tolerance for the clipping and for the triangulation",
				masterDoc = true,
				examples = { @example (
						value = "triangulate(self,0.1, 1.0)",
						equals = "the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.",
						test = false) })
		@no_test
		public static IList<IShape> triangulate(final IScope scope, final IShape g, final Double clipTolerance,
				final Double triangulationTolerance) {
			if (g == null) return null;
			return GeometryUtils.triangulation(scope, g.getInnerGeometry(), triangulationTolerance, clipTolerance,
					false);
		}

		@operator (
				value = { "triangulate", "to_triangles" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand geometry (geometry, agent, point, use_approx_clipping) with the given tolerance for the clipping and for the triangulation with using an approximate clipping is the last operand is true",
				masterDoc = true,
				examples = { @example (
						value = "triangulate(self,0.1, 1.0, true)",
						equals = "the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.",
						test = false) })
		@no_test
		public static IList<IShape> triangulate(final IScope scope, final IShape g, final Double clipTolerance,
				final Double triangulationTolerance, final boolean approxClip) {
			if (g == null) return null;
			return GeometryUtils.triangulation(scope, g.getInnerGeometry(), triangulationTolerance, clipTolerance,
					approxClip);
		}

		@operator (
				value = "voronoi",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries corresponding to the Voronoi diagram built from the list of points (with eventually a given  clip).",
				masterDoc = true,
				examples = { @example (
						value = "voronoi([{10,10},{50,50},{90,90},{10,90},{90,10}])",
						equals = "the list of geometries corresponding to the Voronoi Diagram built from the list of points.",
						test = false) })
		@no_test
		public static IList<IShape> vornoi(final IScope scope, final IList<GamaPoint> pts) {
			if (pts == null) return null;
			return GeometryUtils.voronoi(scope, pts);
		}

		@operator (
				value = "voronoi",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				value = "A list of geometries corresponding to the Voronoi diagram built from the list of points according to the given clip",
				examples = { @example (
						value = "voronoi([{10,10},{50,50},{90,90},{10,90},{90,10}], square(300))",
						equals = "the list of geometries corresponding to the Voronoi Diagram built from the list of points with a square of 300m side size as clip.",
						test = false) })
		@no_test
		public static IList<IShape> vornoi(final IScope scope, final IList<GamaPoint> pts, final IShape clip) {
			if (pts == null) return null;
			return GeometryUtils.voronoi(scope, pts, clip);
		}

		@operator (
				value = "smooth",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "Returns a 'smoothed' geometry, where straight lines are replaces by polynomial (bicubic) curves. The first parameter is the original geometry, the second is the 'fit' parameter which can be in the range 0 (loose fit) to 1 (tightest fit).",
				masterDoc = true,
				examples = { @example (
						value = "smooth(square(10), 0.0)",
						equals = "a 'rounded' square",
						test = false) })
		@no_test
		public static IShape smooth(final IScope scope, final IShape geometry, final Double fit) {
			if (geometry == null) return null;
			final double param = fit == null ? 0d : fit < 0 ? 0d : fit > 1 ? 1d : fit;
			return GeometryUtils.smooth(geometry.getInnerGeometry(), param);
		}

		@operator (
				value = "to_squares",
				type = IType.LIST,
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of squares of the size corresponding to the given size that result from the decomposition of the geometry into squares (geometry, size, overlaps), if overlaps = true, add the squares that overlap the border of the geometry",
				examples = { @example (
						value = "to_squares(self, 10.0, true)",
						equals = "the list of squares of side size 10.0 corresponding to the discretization into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept",
						test = false) })
		public static IList<IShape> toSquares(final IScope scope, final IShape geom, final Double dimension,
				final boolean overlaps) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			return GeometryUtils.discretization(geom.getInnerGeometry(), dimension, dimension, overlaps);
		}

		@operator (
				value = "to_squares",
				type = IType.LIST,
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				value = "A list of a given number of squares from the decomposition of the geometry into squares (geometry, nb_square, overlaps), if overlaps = true, add the squares that overlap the border of the geometry",
				examples = { @example (
						value = "to_squares(self, 10, true)",
						equals = "the list of 10 squares corresponding to the discretization into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept",
						test = false) })
		@no_test
		public static IList<IShape> toSquares(final IScope scope, final IShape geom, final Integer nbSquares,
				final boolean overlaps) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			return GeometryUtils.squareDiscretization(geom.getInnerGeometry(), nbSquares, overlaps, 0.99);
		}

		@operator (
				value = "to_squares",
				type = IType.LIST,
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				value = "A list of a given number of squares from the decomposition of the geometry into squares (geometry, nb_square, overlaps, precision_coefficient), if overlaps = true, add the squares that overlap the border of the geometry, coefficient_precision should be close to 1.0",
				examples = { @example (
						value = "to_squares(self, 10, true, 0.99)",
						equals = "the list of 10 squares corresponding to the discretization into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept",
						test = false) })
		@no_test
		public static IList<IShape> squareDiscretization(final IScope scope, final IShape geom, final Integer nbSquares,
				final boolean overlaps, final double precision) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			return GeometryUtils.squareDiscretization(geom.getInnerGeometry(), nbSquares, overlaps, precision);
		}

		@operator (
				value = "to_rectangles",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of rectangles of the size corresponding to the given dimension that result from the decomposition of the geometry into rectangles (geometry, dimension, overlaps), if overlaps = true, add the rectangles that overlap the border of the geometry",
				masterDoc = true,
				examples = { @example (
						value = "to_rectangles(self, {10.0, 15.0}, true)",
						equals = "the list of rectangles of size {10.0, 15.0} corresponding to the discretization into rectangles of the geometry of the agent applying the operator. The rectangles overlapping the border of the geometry are kept",
						test = false) })
		@no_test
		public static IList<IShape> to_rectangle(final IScope scope, final IShape geom, final GamaPoint dimension,
				final boolean overlaps) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			return GeometryUtils.discretization(geom.getInnerGeometry(), dimension.x, dimension.y, overlaps);
		}

		@operator (
				value = "to_rectangles",
				type = IType.LIST,
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				value = "A list of rectangles corresponding to the given dimension that result from the decomposition of the geometry into rectangles (geometry, nb_cols, nb_rows, overlaps) by a grid composed of the given number of columns and rows, if overlaps = true, add the rectangles that overlap the border of the geometry",
				examples = { @example (
						value = "to_rectangles(self, 5, 20, true)",
						equals = "the list of rectangles corresponding to the discretization by a grid of 5 columns and 20 rows into rectangles of the geometry of the agent applying the operator. The rectangles overlapping the border of the geometry are kept",
						test = false) })
		@no_test
		public static IList<IShape> to_rectangle(final IScope scope, final IShape geom, final int nbCols,
				final int nbRows, final boolean overlaps) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			final Envelope3D envelope = geom.getEnvelope();
			final double x_size = envelope.getWidth() / nbCols;
			final double y_size = envelope.getHeight() / nbRows;

			return GeometryUtils.discretization(geom.getInnerGeometry(), x_size, y_size, overlaps);
		}

		@operator (
				value = { "split_geometry", "to_squares" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries that result from the decomposition of the geometry by square cells of the given side size (geometry, size). It can be used to split in rectangles by giving a point or 2 integer values as operand.",
				masterDoc = true,
				examples = { @example (
						value = "to_squares(self, 10.0)",
						equals = "the list of the geometries corresponding to the decomposition of the geometry by squares of side size 10.0",
						test = false) })
		@test ("length(square(10.0) split_geometry(3)) = 16")
		public static IList<IShape> toSquares(final IScope scope, final IShape geom, final Double dimension) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			return GeometryUtils.geometryDecomposition(geom, dimension, dimension);
		}

		@operator (
				value = { "split_geometry", "to_rectangles" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries that result from the decomposition of the geometry by rectangle cells of the given dimension (geometry, {size_x, size_y})",
				examples = { @example (
						value = "to_rectangles(self, {10.0, 15.0})",
						equals = "the list of the geometries corresponding to the decomposition of the geometry by rectangles of size 10.0, 15.0",
						test = false) })
		@test ("length(square(10.0) split_geometry({2,3})) = 20")
		public static IList<IShape> toRectangle(final IScope scope, final IShape geom, final GamaPoint dimension) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			return GeometryUtils.geometryDecomposition(geom, dimension.x, dimension.y);
		}

		@operator (
				value = { "split_geometry", "to_rectangles" },
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				value = "A list of geometries that result from the decomposition of the geometry according to a grid with the given number of rows and columns (geometry, nb_cols, nb_rows)",
				examples = { @example (
						value = "to_rectangles(self, 10,20)",
						equals = "the list of the geometries corresponding to the decomposition of the geometry of the agent applying the operator",
						test = false) })
		@test ("length(square(10.0) split_geometry(2,2)) = 4")
		public static IList<IShape> to_rectangle(final IScope scope, final IShape geom, final int nbCols,
				final int nbRows) {
			if (geom == null || geom.getInnerGeometry().getArea() <= 0) return GamaListFactory.create(Types.GEOMETRY);
			final Envelope3D envelope = geom.getEnvelope();
			final double x_size = envelope.getWidth() / nbCols;
			final double y_size = envelope.getHeight() / nbRows;

			return GeometryUtils.geometryDecomposition(geom, x_size, y_size);
		}

		@operator (
				value = "to_segments",
				type = IType.LIST,
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				value = "A list of a segments resulting from the decomposition of the geometry (or its contours for polygons) into sgements",
				examples = { @example (
						value = "to_segments(line([{10,10},{80,10},{80,80}]))",
						equals = "[line([{10,10},{80,10}]), line([{80,10},{80,80}])]",
						test = false) })
		public static IList<IShape> toSegments(final IScope scope, final IShape geom) {
			if (geom == null) return GamaListFactory.create(Types.GEOMETRY);
			final IList<IShape> segments = GamaListFactory.create(Types.GEOMETRY);
			if (geom.isMultiple()) {
				for (final IShape g : geom.getGeometries()) {
					segments.addAll(toSegments(scope, g));
				}
			} else {
				if (geom.isPoint()) {
					segments.add(new GamaShape(geom));
				} else {
					for (int i = 1; i < geom.getPoints().size(); i++) {
						final IList<IShape> points = GamaListFactory.create(Types.POINT);
						points.add(geom.getPoints().get(i - 1));
						points.add(geom.getPoints().get(i));
						segments.add(Spatial.Creation.line(scope, points));
					}
				}
			}

			return segments;
		}

		@operator (
				value = "as_hexagonal_grid",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS, IOperatorCategory.GRID },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.GRID })
		@doc (
				value = "A list of geometries (hexagonal) corresponding to the hexagonal tesselation of the first operand geometry",
				examples = { @example (
						value = "self as_hexagonal_grid {10, 5}",
						equals = "list of geometries (hexagonal) corresponding to the hexagonal tesselation of the first operand geometry",
						test = false) },
				see = { "as_4_grid", "as_grid" })
		public static IList<IShape> as_hexagonal_grid(final IShape ls, final GamaPoint param) {
			return GeometryUtils.hexagonalGridFromGeom(ls, (int) param.x, (int) param.y);
		}

		@operator (
				value = "as_grid",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS, IOperatorCategory.GRID },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.GRID })
		@doc (
				value = "A matrix of square geometries (grid with 8-neighborhood) with dimension given by the right-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)",
				examples = { @example (
						value = "self as_grid {10, 5}",
						equals = "a matrix of square geometries (grid with 8-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.",
						test = false) },
				see = { "as_4_grid", "as_hexagonal_grid" })
		public static IMatrix as_grid(final IScope scope, final IShape g, final GamaPoint dim)
				throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(scope, g, (int) dim.x, (int) dim.y, false, false, false, false, "");
		}

		@operator (
				value = "as_4_grid",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS, IOperatorCategory.GRID },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.GRID })
		@doc (
				value = "A matrix of square geometries (grid with 4-neighborhood) with dimension given by the right-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)",
				examples = { @example (
						value = "self as_4_grid {10, 5}",
						equals = "the matrix of square geometries (grid with 4-neighborhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.",
						test = false) },
				see = { "as_grid", "as_hexagonal_grid" })
		@no_test
		public static IMatrix as_4_grid(final IScope scope, final IShape g, final GamaPoint dim)
				throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(scope, g, (int) dim.x, (int) dim.y, false, true, false, false, "");
		}

		@operator (
				value = "to_sub_geometries",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries resulting after spliting the geometry into sub-geometries.",
				masterDoc = true,
				examples = { @example (
						value = "to_sub_geometries(rectangle(10, 50), [0.1, 0.5, 0.4])",
						equals = "a list of three geometries corresponding to 3 sub-geometries",
						test = false) })
		@test ("length(to_sub_geometries(rectangle(10, 50), [0.1, 0.5, 0.4])) = 3")
		public static IList<IShape> splitGeometries(final IScope scope, final IShape geom, final IList<Double> rates) {
			if (geom == null) return GamaListFactory.create(Types.GEOMETRY);

			final double dimension = geom.getArea() / 2000.0;
			return splitGeometries(scope, geom, rates, dimension);
		}

		@operator (
				value = "to_sub_geometries",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries resulting after spliting the geometry into sub-geometries.",
				examples = { @example (
						value = "to_sub_geometries(rectangle(10, 50), [0.1, 0.5, 0.4], 1.0)",
						equals = "a list of three geometries corresponding to 3 sub-geometries using cubes of 1m size",
						test = false) })
		@no_test
		public static IList<IShape> splitGeometries(final IScope scope, final IShape geom, final IList<Double> rates,
				final Double dimension) throws GamaRuntimeException {
			if (geom == null || rates == null || rates.isEmpty()) return GamaListFactory.create(Types.GEOMETRY);
			final IList<IShape> nwGeoms = GamaListFactory.create(Types.GEOMETRY);
			if (geom.isPoint()) {
				nwGeoms.add(geom.copy(scope));
			} else if (geom.isLine()) {
				final IList<Double> translatedRates = GamaListFactory.create(Types.FLOAT);
				final Double sum = (Double) Containers.sum(scope, rates);
				double accu = 0;
				for (int i = 0; i < rates.size() - 1; i++) {
					accu += rates.get(i);
					translatedRates.add(accu / sum);
				}
				final IList<GamaPoint> pts = Punctal.points_along(geom, translatedRates);
				IShape g = geom.copy(scope);
				for (final GamaPoint pt : pts) {
					final IList<IShape> shapes = Operators.split_at(g, pt);
					nwGeoms.add(shapes.get(0));
					g = shapes.get(1);
				}
				nwGeoms.add(g);
			} else if (geom.getArea() > 0) {
				Comparator<IShape> comp;
				if (geom.getWidth() > geom.getHeight()) {
					comp = (o1, o2) -> Double.compare(o1.getLocation().getX(), o2.getLocation().getX());
				} else {
					comp = (o1, o2) -> Double.compare(o1.getLocation().getY(), o2.getLocation().getY());
				}
				final List<IShape> listSq =
						toSquares(scope, geom, dimension).stream().sorted(comp).collect(Collectors.toList());
				final Double sum = (Double) Containers.sum(scope, rates);
				final int totalNumber = listSq.size();
				for (final Double rate : rates) {
					final int number = Math.min((int) (rate / sum * totalNumber + 0.5), listSq.size());
					final IList<IShape> squares = GamaListFactory.create(Types.GEOMETRY);
					for (int i = 0; i < number; i++) {
						squares.add(listSq.remove(0));
					}
					if (squares.size() > 0) {
						final IShape unionG = Transformations.clean(scope, Operators.union(scope, squares));
						nwGeoms.add(unionG);

					}
				}

			}
			return nwGeoms;
		}

		@operator (
				value = "split_lines",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries resulting after cutting the lines at their intersections.",
				examples = { @example (
						value = "split_lines([line([{0,10}, {20,10}]), line([{0,10}, {20,10}])])",
						equals = "a list of four polylines: line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) and line([{10,10}, {10,20}])",
						test = false) })
		@test ("split_lines([line([{0,10}, {20,10}]), line([{10,0}, {10,20}])]) = [line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) , line([{10,10}, {10,20}])]")
		public static IList<IShape> split_lines(final IScope scope, final IContainer<?, IShape> geoms)
				throws GamaRuntimeException {
			if (geoms.isEmpty(scope)) return GamaListFactory.create(Types.GEOMETRY);
			final IShape line = Spatial.Operators.union(scope, geoms);
			final Geometry nodedLineStrings = line.getInnerGeometry();
			final IList<IShape> nwGeoms = GamaListFactory.create(Types.GEOMETRY);

			for (int i = 0, n = nodedLineStrings.getNumGeometries(); i < n; i++) {
				final Geometry g = nodedLineStrings.getGeometryN(i);
				if (g instanceof LineString) { nwGeoms.add(new GamaShape(g)); }
			}
			return nwGeoms;
		}

		@operator (
				value = "split_lines",
				content_type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of geometries resulting after cutting the lines at their intersections. if the last boolean operand is set to true, the split lines will import the attributes of the initial lines",
				examples = { @example (
						value = "split_lines([line([{0,10}, {20,10}]), line([{0,10}, {20,10}])])",
						equals = "a list of four polylines: line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) and line([{10,10}, {10,20}])",
						test = false) })
		@test ("split_lines([line({10,10}, {20,20}), line({10.0,20.0,0.0},{15.0,15.0,0.0})]) = [line ({10.0,10.0,0.0},{15.0,15.0,0.0}), line({15.0,15.0,0.0},{20.0,20.0,0.0}), line({10.0,20.0,0.0},{15.0,15.0,0.0})]")
		@test ("length(split_lines([line({10,10}, {20,20}), line({10.0,20.0,0.0},{15.0,15.0,0.0})])) = 3")
		public static IList<IShape> split_lines(final IScope scope, final IContainer<?, IShape> geoms,
				final boolean readAttributes) throws GamaRuntimeException {
			if (geoms.isEmpty(scope)) return GamaListFactory.create(Types.GEOMETRY);
			if (!readAttributes) return split_lines(scope, geoms);
			boolean change = true;
			IList<IShape> lines = GamaListFactory.create(Types.GEOMETRY);
			lines.addAll((Collection<? extends IShape>) geoms);
			final IList<IShape> split_lines = GamaListFactory.create(Types.GEOMETRY);
			while (change) {
				change = false;
				final IList<IShape> lines2 = GamaListFactory.createWithoutCasting(Types.GEOMETRY, lines);
				for (final IShape l : lines) {
					lines2.remove(l);
					if (!l.getInnerGeometry().isSimple()) {
						final IList<IShape> segments = GamaListFactory.create(Types.GEOMETRY);
						for (int i = 0; i < l.getPoints().size() - 1; i++) {
							final IList<IShape> points = GamaListFactory.create(Types.POINT);
							points.add(l.getPoints().get(i));
							points.add(l.getPoints().get(i + 1));
							segments.add(Spatial.Creation.line(scope, points));
						}
						final IShape line = Spatial.Operators.union(scope, segments);
						final Geometry nodedLineStrings = line.getInnerGeometry();

						for (int i = 0, n = nodedLineStrings.getNumGeometries(); i < n; i++) {
							final Geometry g = nodedLineStrings.getGeometryN(i);
							if (g instanceof LineString) {
								final IShape gS = new GamaShape(g);
								gS.copyAttributesOf(l);
								// if (l.getAttributes() != null) {
								// gS.getAttributes().putAll(l.getAttributes());
								// }
								lines2.add(new GamaShape(g));
							}
						}
						change = true;

						lines = lines2;
						break;
					}
					final IShape gg =
							Transformations.enlarged_by(scope, l, Math.min(0.001, l.getPerimeter() / 1000.0), 10);

					final List<IShape> ls = gg == null ? GamaListFactory.create()
							: (List<IShape>) Spatial.Queries.overlapping(scope, lines2, gg);
					if (!ls.isEmpty()) {
						final ILocation pto = l.getPoints().firstValue(scope);
						final ILocation ptd = l.getPoints().lastValue(scope);
						final PreparedGeometry pg = PreparedGeometryFactory.prepare(gg.getInnerGeometry());
						for (final IShape l2 : ls) {
							if (pg.covers(l2.getInnerGeometry()) || pg.coveredBy(l2.getInnerGeometry())) { continue; }
							final IShape it = Spatial.Operators.inter(scope, l, l2);

							if (it == null || it.getPerimeter() > 0.0) { continue; }
							if (!it.getLocation().equals(pto) || !it.getLocation().equals(ptd)) {
								final ILocation pt = it.getPoints().firstValue(scope);
								final IList<IShape> res1 = Spatial.Operators.split_at(l2, pt);
								res1.removeIf(a -> a.getPerimeter() == 0.0);
								final IList<IShape> res2 = Spatial.Operators.split_at(l, pt);
								res2.removeIf(a -> a.getPerimeter() == 0.0);
								if (res1.size() > 1 || res2.size() > 1) {
									change = true;
									lines2.addAll(res1);
									lines2.addAll(res2);
									lines2.remove(l2);
									break;
								}
							}
						}
						if (change) {
							lines = lines2;
							break;
						}
					}
					split_lines.add(l);
				}

			}

			return split_lines;
		}

		@operator (
				value = "clean",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry corresponding to the cleaning of the operand (geometry, agent, point)",
				comment = "The cleaning corresponds to a buffer with a distance of 0.0",
				examples = { @example (
						value = "clean(self)",
						equals = "returns the geometry resulting from the cleaning of the geometry of the agent applying the operator.",
						test = false) })
		public static IShape clean(final IScope scope, final IShape g) {

			if (g == null || g.getInnerGeometry() == null) return g;
			if (g.getInnerGeometry() instanceof Polygon)
				return new GamaShape(g, GeometryUtils.cleanGeometry(g.getInnerGeometry()));
			if (g.getInnerGeometry() instanceof MultiPolygon) {
				final MultiPolygon mp = (MultiPolygon) g.getInnerGeometry();
				final int nb = mp.getNumGeometries();
				final Polygon[] polys = new Polygon[nb];
				for (int i = 0; i < nb; i++) {
					polys[i] = (Polygon) GeometryUtils.cleanGeometry(mp.getGeometryN(i));
				}
				return new GamaShape(g, GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(polys));
			}
			return g.copy(scope);
		}

		@operator (
				value = "clean_network",
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A list of polylines corresponding to the cleaning of the first operand (list of polyline geometry or agents), considering the tolerance distance given "
						+ "by the second operand; the third operator is used to define if the operator should as well split the lines at their intersections(true to split the lines); the last operand"
						+ "is used to specify if the operator should as well keep only the main connected component of the network. "
						+ "Usage: clean_network(lines:list of geometries or agents, tolerance: float, split_lines: bool, keepMainConnectedComponent: bool)",
				comment = "The cleaned set of polylines",
				examples = { @example (
						value = "clean_network(my_road_shapefile.contents, 1.0, true, false)",
						equals = "returns the list of polulines resulting from the cleaning of the geometry of the agent applying the operator with a tolerance of 1m, and splitting the lines at their intersections.",
						isExecutable = false),
						@example (
								value = "clean_network([line({10,10}, {20,20}), line({10,20},{20,10})],3.0,true,false)",
								equals = "[line({10.0,20.0,0.0},{15.0,15.0,0.0}),line({15.0,15.0,0.0},{20.0,10.0,0.0}), line({10.0,10.0,0.0},{15.0,15.0,0.0}), line({15.0,15.0,0.0},{20.0,20.0,0.0})]") })
		@test ("length(clean_network([line({10,10}, {20,20}), line({10,20},{20,10})],3.0,true,false)) = 4")
		public static IList<IShape> clean(final IScope scope, final IList<IShape> polylines, final double tolerance,
				final boolean splitlines, final boolean keepMainGraph) {
			if (polylines == null || polylines.isEmpty()) return polylines;
			final IList<IShape> geoms = polylines.copy(scope);
			geoms.removeIf(a -> !a.getGeometry().isLine());
			if (geoms.isEmpty()) return GamaListFactory.EMPTY_LIST;

			IList<IShape> results = GamaListFactory.create();

			IList<IShape> geomsTmp = geoms.copy(scope);
			boolean modif = true;
			if (tolerance > 0) {

				while (modif) {
					for (final IShape geom : geomsTmp) {
						final GamaPoint ptF = geom.getPoints().firstValue(scope).toGamaPoint();
						modif = connectLine(scope, ptF, geom, true, geoms, results, tolerance);
						if (modif) {
							geomsTmp = GamaListFactory.create();
							geomsTmp.addAll(geoms);
							break;
						}
						final GamaPoint ptL = geom.getPoints().lastValue(scope).toGamaPoint();
						modif = connectLine(scope, ptL, geom, false, geoms, results, tolerance);
						if (modif) {
							geomsTmp = GamaListFactory.create();
							geomsTmp.addAll(geoms);
							break;
						}
						results.add(geom);
						geoms.remove(geom);
					}
				}
			} else {
				results = geomsTmp;
			}
			results.removeIf(
					a -> a.getPerimeter() == 0 || !a.getInnerGeometry().isValid() || a.getInnerGeometry().isEmpty());

			if (splitlines) {
				results = Transformations.split_lines(scope, results, true);
				results.removeIf(a -> !a.getInnerGeometry().isValid() || a.getInnerGeometry().isEmpty()
						|| a.getPerimeter() == 0);
			}
			if (keepMainGraph) {
				IGraph graph = Graphs.spatialFromEdges(scope, results);
				graph = Graphs.ReduceToMainconnectedComponentOf(scope, graph);
				return graph.getEdges();
			}
			return results;
		}

		private static boolean connectLine(final IScope scope, final GamaPoint pt, final IShape shape,
				final boolean first, final IList<IShape> geoms, final IList<IShape> results, final double tolerance) {
			final IList<IShape> tot = geoms.copy(scope);
			tot.addAll(results);
			tot.remove(shape);
			final IShape closest = Queries.closest_to(scope, tot, pt);
			if (closest.intersects(shape)) return false;
			if (closest.euclidianDistanceTo(pt) <= tolerance) {
				final GamaPoint fp = (GamaPoint) closest.getPoints().firstValue(scope);
				if (pt.equals3D(fp)) return false;
				final GamaPoint lp = (GamaPoint) closest.getPoints().lastValue(scope);
				if (pt.equals3D(lp)) return false;
				if (pt.euclidianDistanceTo(fp) <= tolerance) {
					modifyPoint(scope, shape, fp, first);
					return false;
				} else if (pt.euclidianDistanceTo(lp) <= tolerance) {
					modifyPoint(scope, shape, lp, first);
					return false;
				} else {
					final GamaPoint ptS = Punctal.closest_points_with(pt, closest).get(1);
					modifyPoint(scope, shape, ptS, first);
					final IList<IShape> spliL = Operators.split_at(closest, ptS);
					if (results.contains(closest)) {
						results.remove(closest);
						results.addAll(spliL);
					} else {
						geoms.remove(closest);
						geoms.addAll(spliL);
					}
					return true;
				}
			}
			return false;
		}

		/*
		 * if (first) {g <- line([pt] + (g.points - first(g.points)));} else {g <- line((g.points - last(g.points)) +
		 * [pt]);} return g;
		 */
		private static void modifyPoint(final IScope scope, final IShape shape, final GamaPoint pt,
				final boolean first) {
			if (first) {
				shape.getInnerGeometry().getCoordinates()[0] = pt;
			} else {
				shape.getInnerGeometry().getCoordinates()[shape.getInnerGeometry().getCoordinates().length - 1] = pt;
			}
			shape.getInnerGeometry().geometryChanged();
		}

		/**
		 * Simplification of a geometry (Douglas-Peuker algorithm)
		 */

		@operator (
				value = "simplification",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry corresponding to the simplification of the operand (geometry, agent, point) considering a tolerance distance.",
				comment = "The algorithm used for the simplification is Douglas-Peucker",
				examples = { @example (
						value = "self simplification 0.1",
						equals = "the geometry resulting from the application of the Douglas-Peuker algorithm on the geometry of the agent applying the operator with a tolerance distance of 0.1.",
						test = false) })
		public static IShape simplification(final IScope scope, final IShape g1, final Double distanceTolerance) {
			if (g1 == null || g1.getInnerGeometry() == null) return g1;
			if (g1.isPoint()) return g1.copy(scope);
			final Geometry geomSimp = DouglasPeuckerSimplifier.simplify(g1.getInnerGeometry(), distanceTolerance);
			if (geomSimp != null && !geomSimp.isEmpty() && geomSimp.isSimple()) return new GamaShape(g1, geomSimp);
			return g1.copy(scope);
		}

		@operator (
				value = "with_precision",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION })
		@doc (
				value = "A geometry corresponding to the rounding of points of the operand considering a given precison.",
				examples = { @example (
						value = "self with_precision 2",
						equals = "the geometry resulting from the rounding of points of the geometry with a precision of 0.1.",
						test = false) })
		public static IShape withPrecision(final IScope scope, final IShape g1, final Integer precision) {
			if (g1 == null || g1.getInnerGeometry() == null) return g1;
			final double scale = Math.pow(10, precision);
			final PrecisionModel pm = new PrecisionModel(scale);
			return new GamaShape(GeometryPrecisionReducer.reduce(g1.getInnerGeometry(), pm));
		}

	}

	public static abstract class Relations {

		@operator (
				value = { "towards", "direction_to" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION, IConcept.AGENT_LOCATION,
						IConcept.TOPOLOGY })
		@doc (
				value = "The direction (in degree) between the two geometries (geometries, agents, points) considering the topology of the agent applying the operator.",
				examples = { @example (
						value = "ag1 towards ag2",
						equals = "the direction between ag1 and ag2 and ag3 considering the topology of the agent applying the operator",
						isExecutable = false) },
				see = { "distance_between", "distance_to", "direction_between", "path_between", "path_to" })
		@no_test // Test already done in Spatial tests models
		public static Double towards(final IScope scope, final IShape agent, final IShape target) {
			return scope.getTopology().directionInDegreesTo(scope, agent, target);
		}

		@operator (
				value = "distance_between",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION, IConcept.AGENT_LOCATION,
						IConcept.TOPOLOGY })
		@doc (
				value = "A distance between a list of geometries (geometries, agents, points) considering a topology.",
				examples = { @example (
						value = "my_topology distance_between [ag1, ag2, ag3]",
						equals = "the distance between ag1, ag2 and ag3 considering the topology my_topology",
						isExecutable = false) },
				see = { "towards", "direction_to", "distance_to", "direction_between", "path_between", "path_to" })
		@no_test // Test already done in Spatial tests models
		public static Double distance_between(final IScope scope, final ITopology t,
				final IContainer<?, IShape> geometries) {
			final int size = geometries.length(scope);
			if (size == 0 || size == 1) return 0d;
			IShape previous = null;
			Double distance = 0d;
			for (final IShape obj : geometries.iterable(scope)) {
				if (previous != null) {
					final Double d = t.distanceBetween(scope, previous, obj);
					if (d == null) return null;
					distance += d;
				}
				previous = obj;
			}
			return distance;
		}

		@operator (
				value = "direction_between",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS },
				concept = {})
		@doc (
				value = "A direction (in degree) between a list of two geometries (geometries, agents, points) considering a topology.",
				examples = { @example (
						value = "my_topology direction_between [ag1, ag2]",
						equals = "the direction between ag1 and ag2 considering the topology my_topology",
						isExecutable = false) },
				see = { "towards", "direction_to", "distance_to", "distance_between", "path_between", "path_to" })
		@test ("topology(world) direction_between([{0,0},{50,50}]) = 45.0")
		public static Double direction_between(final IScope scope, final ITopology t,
				final IContainer<?, IShape> geometries) throws GamaRuntimeException {
			final int size = geometries.length(scope);
			if (size == 0 || size == 1) return 0.0;
			final IShape g1 = geometries.firstValue(scope);
			final IShape g2 = geometries.lastValue(scope);
			return t.directionInDegreesTo(scope, g1, g2);
		}

		@operator (
				value = "path_between",
				type = IType.PATH,
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS, IOperatorCategory.PATH },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION, IConcept.AGENT_LOCATION,
						IConcept.TOPOLOGY })
		@doc (
				value = "A path between two geometries (geometries, agents or points) considering a topology.",
				examples = { @example (
						value = "my_topology path_between (ag1, ag2)",
						equals = "A path between ag1 and ag2",
						isExecutable = false) },
				see = { "towards", "direction_to", "distance_between", "direction_between", "path_to", "distance_to" })
		@no_test // Test already done in Spatial Tests Models.
		public static IPath path_between(final IScope scope, final ITopology graph, final IShape source,
				final IShape target) throws GamaRuntimeException {
			return graph.pathBetween(scope, source, target);

		}

		@operator (
				value = "path_between",
				type = IType.PATH,
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS, IOperatorCategory.PATH },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION, IConcept.AGENT_LOCATION,
						IConcept.TOPOLOGY })
		@doc (
				value = "A path between a list of several geometries (geometries, agents or points) considering a topology.",
				examples = { @example (
						value = "my_topology path_between [ag1, ag2]",
						equals = "A path between ag1 and ag2",
						isExecutable = false) },
				see = { "towards", "direction_to", "distance_between", "direction_between", "path_to", "distance_to" })
		@no_test // test already done in Spatial tests models
		public static IPath path_between(final IScope scope, final ITopology topo, final IContainer<?, IShape> nodes)
				throws GamaRuntimeException {
			if (nodes.isEmpty(scope)) return null;
			final int n = nodes.length(scope);
			final IShape source = nodes.firstValue(scope);
			if (n == 1) return PathFactory.newInstance(scope, scope.getTopology(), source, source,
					GamaListFactory.<IShape> create(Types.GEOMETRY));
			final IShape target = nodes.lastValue(scope);
			if (n == 2) return topo.pathBetween(scope, source, target);
			final IList<IShape> edges = GamaListFactory.create(Types.GEOMETRY);
			IShape previous = null;
			for (final IShape gg : nodes.iterable(scope)) {
				if (previous != null) {
					// TODO Take the case of ILocation
					final GamaSpatialPath path = topo.pathBetween(scope, previous, gg);
					if (path != null && path.getEdgeList() != null) { edges.addAll(path.getEdgeList()); }
				}
				previous = gg;
			}

			final GamaSpatialPath path = PathFactory.newInstance(scope, topo, source, target, edges);
			path.setWeight(path.getVertexList().size());
			return path;
		}

		@operator (
				value = "path_between",

				category = { IOperatorCategory.GRID, IOperatorCategory.PATH },
				concept = { IConcept.GRID })
		@doc (
				value = "The shortest path between several objects according to set of cells",
				masterDoc = true,
				examples = { @example (
						value = "path_between (cell_grid where each.is_free, [ag1, ag2, ag3])",
						equals = "A path between ag1 and ag2 and ag3 passing through the given cell_grid agents",
						isExecutable = false) })
		@no_test // test already done in Spatial tests models
		public static IPath path_between(final IScope scope, final IList<IAgent> cells,
				final IContainer<?, IShape> nodes) throws GamaRuntimeException {
			if (cells == null || cells.isEmpty()) return null;

			if (nodes.isEmpty(scope)) return null;
			final ITopology topo = cells.get(0).getTopology();

			final int n = nodes.length(scope);
			final IShape source = nodes.firstValue(scope);
			if (n == 1) {
				if (topo instanceof GridTopology)
					return ((GridTopology) topo).pathBetween(scope, source, source, cells);
				else
					return scope.getTopology().pathBetween(scope, source, source);
			}
			final IShape target = nodes.lastValue(scope);
			if (n == 2) {
				if (topo instanceof GridTopology)
					return ((GridTopology) topo).pathBetween(scope, source, target, cells);
				else
					return scope.getTopology().pathBetween(scope, source, target);
			}
			final IList<IShape> edges = GamaListFactory.create(Types.GEOMETRY);
			IShape previous = null;
			for (final IShape gg : nodes.iterable(scope)) {
				if (previous != null) {
					// TODO Take the case of ILocation
					if (topo instanceof GridTopology) {
						final GamaSpatialPath path = ((GridTopology) topo).pathBetween(scope, previous, gg, cells);
						edges.addAll(path.getEdgeList());
					} else {
						edges.addAll(scope.getTopology().pathBetween(scope, previous, gg).getEdgeList());
					}
				}
				previous = gg;
			}
			final GamaSpatialPath path = PathFactory.newInstance(scope,
					topo instanceof GridTopology ? topo : scope.getTopology(), source, target, edges);
			path.setWeight(path.getVertexList().size());
			return path;
		}

		@operator (
				value = "path_between",

				category = { IOperatorCategory.GRID, IOperatorCategory.PATH },
				concept = { IConcept.GRID })
		@doc (
				value = "The shortest path between several objects according to set of cells with corresponding weights",
				masterDoc = true,
				examples = { @example (
						value = "path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), [ag1, ag2, ag3])",
						equals = "A path between ag1 and ag2 and ag3 passing through the given cell_grid agents with minimal cost",
						isExecutable = false) })
		@no_test // test already done in Spatial tests models
		public static IPath path_between(final IScope scope, final IMap<IAgent, Object> cells,
				final IContainer<?, IShape> nodes) throws GamaRuntimeException {
			if (cells == null || cells.isEmpty()) return null;

			if (nodes.isEmpty(scope)) return null;
			final ITopology topo = cells.getKeys().get(0).getTopology();

			final int n = nodes.length(scope);
			final IShape source = nodes.firstValue(scope);
			if (n == 1) {
				if (topo instanceof GridTopology)
					return ((GridTopology) topo).pathBetween(scope, source, source, cells);
				else
					return scope.getTopology().pathBetween(scope, source, source);
			}
			final IShape target = nodes.lastValue(scope);
			if (n == 2) {
				if (topo instanceof GridTopology)
					return ((GridTopology) topo).pathBetween(scope, source, target, cells);
				else
					return scope.getTopology().pathBetween(scope, source, target);
			}
			double weight = 0;
			final IList<IShape> edges = GamaListFactory.create(Types.GEOMETRY);
			IShape previous = null;
			for (final IShape gg : nodes.iterable(scope)) {
				if (previous != null) {
					// TODO Take the case of ILocation
					if (topo instanceof GridTopology) {
						final GamaSpatialPath path = ((GridTopology) topo).pathBetween(scope, previous, gg, cells);
						edges.addAll(path.getEdgeList());
						weight += path.getWeight();
					} else {
						edges.addAll(scope.getTopology().pathBetween(scope, previous, gg).getEdgeList());
					}
				}
				previous = gg;
			}
			final GamaSpatialPath path = PathFactory.newInstance(scope,
					topo instanceof GridTopology ? topo : scope.getTopology(), source, target, edges);
			path.setWeight(topo instanceof GridTopology ? weight : path.getVertexList().size());
			return path;
		}

		@operator (
				value = "path_between",

				category = { IOperatorCategory.GRID, IOperatorCategory.PATH },
				concept = { IConcept.GRID })
		@doc (
				value = "The shortest path between two objects according to set of cells",
				masterDoc = true,
				examples = { @example (
						value = "path_between (cell_grid where each.is_free, ag1, ag2)",
						equals = "A path between ag1 and ag2 passing through the given cell_grid agents",
						isExecutable = false) })
		@no_test // test already done in Spatial tests models
		public static IPath path_between(final IScope scope, final IList<IAgent> cells, final IShape source,
				final IShape target) throws GamaRuntimeException {
			if (cells == null || cells.isEmpty()) return null;
			if (source == null || target == null) return null;
			final ITopology topo = cells.get(0).getTopology();
			if (topo instanceof GridTopology)
				return ((GridTopology) topo).pathBetween(scope, source, target, cells);
			else
				return scope.getTopology().pathBetween(scope, source, target);
		}

		@operator (
				value = "path_between",

				category = { IOperatorCategory.GRID, IOperatorCategory.PATH },
				concept = { IConcept.GRID })
		@doc (
				value = "The shortest path between two objects according to set of cells with corresponding weights",
				masterDoc = true,
				examples = { @example (
						value = "path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), ag1, ag2)",
						equals = "A path between ag1 and ag2 passing through the given cell_grid agents with a minimal cost",
						isExecutable = false) })
		@no_test // test already done in Spatial tests models
		public static IPath path_between(final IScope scope, final IMap<IAgent, Object> cells, final IShape source,
				final IShape target) throws GamaRuntimeException {
			if (cells == null || cells.isEmpty()) return null;
			final ITopology topo = cells.getKeys().get(0).getTopology();
			if (topo instanceof GridTopology)
				return ((GridTopology) topo).pathBetween(scope, source, target, cells);
			else
				return scope.getTopology().pathBetween(scope, source, target);
		}

		@operator (
				value = "distance_to",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION, IConcept.AGENT_LOCATION,
						IConcept.TOPOLOGY })
		@doc (
				value = "A distance between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.",
				masterDoc = true,
				examples = { @example (
						value = "ag1 distance_to ag2",
						equals = "the distance between ag1 and ag2 considering the topology of the agent applying the operator",
						isExecutable = false) },
				see = { "towards", "direction_to", "distance_between", "direction_between", "path_between", "path_to" })
		@no_test // test already done in Spatial tests models
		public static Double distance_to(final IScope scope, final IShape source, final IShape target) {
			return scope.getTopology().distanceBetween(scope, source, target);
		}

		@operator (
				value = "distance_to",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS })
		@doc (
				value = "An Euclidean distance between two points.")
		// No documentation because it is same same as the previous one (but
		// optimized for points?)
		@test (" {20,20} distance_to {30,30} = 14.142135623730951")
		public static Double distance_to(final IScope scope, final GamaPoint source, final GamaPoint target) {
			return scope.getTopology().distanceBetween(scope, source, target);
		}

		@operator (
				value = "path_to",
				type = IType.PATH,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS, IOperatorCategory.PATH },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION, IConcept.AGENT_LOCATION,
						IConcept.TOPOLOGY })
		@doc (
				value = "A path between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.",
				masterDoc = true,
				examples = { @example (
						value = "ag1 path_to ag2",
						equals = "the path between ag1 and ag2 considering the topology of the agent applying the operator",
						isExecutable = false) },
				see = { "towards", "direction_to", "distance_between", "direction_between", "path_between",
						"distance_to" })
		@no_test // test already done in Spatial tests models
		public static IPath path_to(final IScope scope, final IShape g, final IShape g1) throws GamaRuntimeException {
			if (g == null) return null;
			return scope.getTopology().pathBetween(scope, g, g1);
		}

		@operator (
				value = "path_to",
				type = IType.PATH,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS, IOperatorCategory.PATH },
				concept = { IConcept.SHORTEST_PATH })
		@doc (
				value = "A shortest path between two points considering the topology of the agent applying the operator.")
		// No documentation because it is same same as the previous one (but
		// optimized for points?)
		@no_test // test already done in Spatial tests models
		public static IPath path_to(final IScope scope, final GamaPoint g, final GamaPoint g1)
				throws GamaRuntimeException {
			if (g == null) return null;
			return scope.getTopology().pathBetween(scope, g, g1);
		}

	}

	public static abstract class Properties {

		@operator (
				value = { "disjoint_from" },
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION })
		@doc (
				value = "A boolean, equal to true if the left-geometry (or agent/point) is disjoints from the right-geometry (or agent/point).",
				usages = { @usage (
						value = "if one of the operand is null, returns true."),
						@usage (
								value = "if one operand is a point, returns false if the point is included in the geometry.") },
				examples = { @example (
						value = "polyline([{10,10},{20,20}]) disjoint_from polyline([{15,15},{25,25}])",
						equals = "false"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{15,15},{15,25},{25,25},{25,15}])",
								equals = "false"),
						// @example (
						// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from {15,15}",
						// equals = "false"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from {25,25}",
								equals = "true"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{35,35},{35,45},{45,45},{45,35}])",
								equals = "true") },
				see = { "intersects", "crosses", "overlaps", "partially_overlaps", "touches" })
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from {15,15} = false")
		public static Boolean disjoint_from(final IScope scope, final IShape g1, final IShape g2) {
			if (g1 == null || g2 == null) return true;
			if (g1.getInnerGeometry() == null || g2.getInnerGeometry() == null) return true;
			return !g1.intersects(g2);
		}

		/**
		 * Return true if the agent geometry overlaps the geometry of the localized entity passed in parameter
		 *
		 * @param args
		 *            : agent --: a localized entity
		 *
		 */

		@operator (
				value = "overlaps",
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION })
		@doc (
				value = "A boolean, equal to true if the left-geometry (or agent/point) overlaps the right-geometry (or agent/point).",
				usages = { @usage ("if one of the operand is null, returns false."),
						@usage ("if one operand is a point, returns true if the point is included in the geometry") },
				examples = { @example (
						value = "polyline([{10,10},{20,20}]) overlaps polyline([{15,15},{25,25}])",
						equals = "true"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}])",
								equals = "true"),
						// @example (
						// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps {25,25}",
						// equals = "false"),
						// @example (
						// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps
						// polygon([{35,35},{35,45},{45,45},{45,35}])",
						// equals = "false"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polyline([{10,10},{20,20}])",
								equals = "true"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps {15,15}",
								equals = "true") },
				// @example (
				// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{0,0},{0,30},{30,30},
				// {30,0}])",
				// equals = "true"),
				// @example (
				// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps
				// polygon([{15,15},{15,25},{25,25},{25,15}])",
				// equals = "true"),
				// @example (
				// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps
				// polygon([{10,20},{20,20},{20,30},{10,30}])",
				// equals = "true") },
				see = { "disjoint_from", "crosses", "intersects", "partially_overlaps", "touches" })
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{0,0},{0,30},{30,30},{30,0}])")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}])")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{10,20},{20,20},{20,30},{10,30}])")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{35,35},{35,45},{45,45},{45,35}]) =  false")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps {25,25} = false")
		public static Boolean overlaps(final IScope scope, final IShape g1, final IShape g2) {
			if (g1 == null || g2 == null) return false;
			return !disjoint_from(scope, g1, g2);
		}

		/**
		 * Return true if the agent geometry partially overlaps the geometry of the localized agent passed in parameter
		 *
		 * @param args
		 *            : agent --: a localized entity
		 *
		 */

		@operator (
				value = "partially_overlaps",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION })
		@doc (
				value = "A boolean, equal to true if the left-geometry (or agent/point) partially overlaps the right-geometry (or agent/point).",
				usages = { @usage ("if one of the operand is null, returns false.") },
				comment = "if one geometry operand fully covers the other geometry operand, returns false (contrarily to the overlaps operator).",
				examples = { @example (
						value = "polyline([{10,10},{20,20}]) partially_overlaps polyline([{15,15},{25,25}])",
						equals = "true"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}])",
								equals = "true"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {25,25}",
								equals = "false"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polyline([{10,10},{20,20}])",
								equals = "false") },
				see = { "disjoint_from", "crosses", "overlaps", "intersects", "touches" })
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]) =  false")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}])")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{10,20},{20,20},{20,30},{10,30}]) = false")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {15,15} = false")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{35,35},{35,45},{45,45},{45,35}]) = false")
		public static Boolean partially_overlaps(final IShape g1, final IShape g) {
			if (g == null) return false;
			return g1.getInnerGeometry().overlaps(g.getInnerGeometry());
		}

		/**
		 * Return true if the agent geometry touches the geometry of the localized entity passed in parameter
		 *
		 * @param args
		 *            : agent --: a localized entity
		 *
		 */
		@operator (
				value = "touches",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION })
		@doc (
				value = "A boolean, equal to true if the left-geometry (or agent/point) touches the right-geometry (or agent/point).",
				usages = { @usage ("if one of the operand is null, returns false.") },
				comment = "returns true when the left-operand only touches the right-operand. When one geometry covers partially (or fully) the other one, it returns false.",
				examples = {
						// @example (
						// value = "polyline([{10,10},{20,20}]) touches {15,15}",
						// equals = "false"),
						@example (
								value = "{15,15} touches {15,15}",
								equals = "false"),
						@example (
								value = "polyline([{10,10},{20,20}]) touches {10,10}",
								equals = "true"),
						@example (
								value = "polyline([{10,10},{20,20}]) touches polyline([{10,10},{5,5}])",
								equals = "true"),
						// @example (
						// value = "polyline([{10,10},{20,20}]) touches polyline([{5,5},{15,15}])",
						// equals = "false"),
						// @example (
						// value = "polyline([{10,10},{20,20}]) touches polyline([{15,15},{25,25}])",
						// equals = "false"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{15,15},{15,25},{25,25},{25,15}])",
								equals = "false"),
						// @example (
						// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches
						// polygon([{10,20},{20,20},{20,30},{10,30}])",
						// equals = "true"),
						// @example (
						// value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches {15,15}",
						// equals = "false"),
						@example (
								value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches {10,15}",
								equals = "true") },
				see = { "disjoint_from", "crosses", "overlaps", "partially_overlaps", "intersects" })
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,20},{20,20},{20,30},{10,30}])")
		@test ("polyline([{10,10},{20,20}]) touches polyline([{15,15},{25,25}]) = false")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,10},{0,10},{0,0},{10,0}])")
		@test ("polyline([{10,10},{20,20}]) touches polyline([{5,5},{15,15}]) = false")
		@test ("polyline([{10,10},{20,20}]) touches {15,15} = false")
		@test ("polygon([{10,10},{10,20},{20,20},{20,10}]) touches {15,15} = false")
		public static Boolean touches(final IShape g, final IShape g2) {
			if (g == null) return false;
			return g2.getInnerGeometry().touches(g.getInnerGeometry());
		}

		/**
		 * Return true if the agent geometry crosses the geometry of the localized entity passed in parameter
		 *
		 * @param args
		 *            : agent --: a localized entity
		 *
		 */

		@operator (
				value = "crosses",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION })
		@doc (
				value = "A boolean, equal to true if the left-geometry (or agent/point) crosses the right-geometry (or agent/point).",
				usages = { @usage ("if one of the operand is null, returns false."),
						@usage ("if one operand is a point, returns false.") },
				examples = { @example (
						value = "polyline([{10,10},{20,20}]) crosses polyline([{10,20},{20,10}])",
						equals = "true"),
						@example (
								value = "polyline([{10,10},{20,20}]) crosses {15,15}",
								equals = "true"),
						@example (
								value = "polyline([{0,0},{25,25}]) crosses polygon([{10,10},{10,20},{20,20},{20,10}])",
								equals = "true") },
				see = { "disjoint_from", "intersects", "overlaps", "partially_overlaps", "touches" })
		@test ("polyline([{10,10},{20,20}]) crosses polyline([{10,20},{20,10}])")
		public static Boolean crosses(final IShape g1, final IShape g2) {
			if (g1 == null || g2 == null) return false;
			return g1.crosses(g2);
		}

		@operator (
				value = "intersects",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION })
		@doc (
				value = "A boolean, equal to true if the left-geometry (or agent/point) intersects the right-geometry (or agent/point).",
				usages = { @usage ("if one of the operand is null, returns false.") },
				examples = { @example (
						value = "square(5) intersects {10,10}",
						equals = "false") },
				see = { "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
		@test ("square(5) intersects square(2)")
		public static Boolean intersects(final IShape g1, final IShape g2) {
			if (g1 == null || g2 == null) return false;
			return g1.intersects(g2);
		}

		@operator (
				value = "covers",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION })
		@doc (
				value = "A boolean, equal to true if the left-geometry (or agent/point) covers the right-geometry (or agent/point).",
				usages = { @usage ("if one of the operand is null, returns false.") },
				examples = { @example (
						value = "square(5) covers square(2)",
						equals = "true") },
				see = { "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
		@test ("square(5) covers square(2)")
		public static Boolean covers(final IShape g1, final IShape g2) {
			if (g1 == null || g2 == null) return false;
			return g1.covers(g2);
		}

	}

	public static abstract class Punctal {

		@operator (
				value = "centroid",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.POINT })
		@doc (
				value = "Centroid (weighted sum of the centroids of a decomposition of the area into triangles) of the operand-geometry. Can be different to the location of the geometry",
				examples = { @example (
						value = "centroid(world)",
						equals = "the centroid of the square, for example : {50.0,50.0}.",
						test = false) },
				see = { "any_location_in", "closest_points_with", "farthest_point_to", "points_at" })
		@test (" centroid(world) = {50.0, 50.0, 0.0} ")
		public static ILocation centroidArea(final IScope scope, final IShape g) {
			if (g == null || g.getInnerGeometry() == null) return null;
			final Centroid cent = new Centroid(g.getInnerGeometry());
			return new GamaPoint(cent.getCentroid());
		}

		@operator (
				value = { "any_location_in", "any_point_in" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.POINT })
		@doc (
				value = "A point inside (or touching) the operand-geometry.",
				examples = { @example (
						value = "any_location_in(square(5))",
						equals = "a point in the square, for example : {3,4.6}.",
						test = false) },
				see = { "closest_points_with", "farthest_point_to", "points_at" })
		@no_test
		public static ILocation any_location_in(final IScope scope, final IShape g) {
			return GeometryUtils.pointInGeom(g, scope.getRandom());
		}

		@operator (
				value = { "points_on" },
				type = IType.LIST,
				content_type = IType.POINT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.POINT })
		@doc (
				value = "A list of points of the operand-geometry distant from each other to the float right-operand .",
				examples = { @example (
						value = " square(5) points_on(2)",
						equals = "a list of points belonging to the exterior ring of the square distant from each other of 2.",
						test = false) },
				see = { "closest_points_with", "farthest_point_to", "points_at" })
		@test ("line({0,0},{0,10}) points_on 5 = [{0.0,0.0,0.0},{0.0,5.0,0.0},{0.0,10.0,0.0}]")
		public static IList points_on(final IShape geom, final Double distance) {
			final IList<GamaPoint> locs = GamaListFactory.create(Types.POINT);
			if (geom.getInnerGeometry() instanceof GeometryCollection) {
				for (int i = 0; i < geom.getInnerGeometry().getNumGeometries(); i++) {
					locs.addAll(GeometryUtils.locsOnGeometry(geom.getInnerGeometry().getGeometryN(i), distance));
				}
			} else {
				locs.addAll(GeometryUtils.locsOnGeometry(geom.getInnerGeometry(), distance));
			}
			return locs;
		}

		@operator (
				value = { "points_along" },
				type = IType.LIST,
				content_type = IType.POINT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.POINT })
		@doc (
				value = "A list of points along the operand-geometry given its location in terms of rate of distance from the starting points of the geometry.",
				examples = { @example (
						value = " line([{10,10},{80,80}]) points_along ([0.3, 0.5, 0.9])",
						equals = "the list of following points: [{31.0,31.0,0.0},{45.0,45.0,0.0},{73.0,73.0,0.0}]",
						test = false) },
				see = { "closest_points_with", "farthest_point_to", "points_at", "points_on" })
		@test ("line({0,0},{0,10}) points_along [0.50, 0.75] = [{0.0,5.0,0.0},{0.0,7.5,0.0}]")
		public static IList points_along(final IShape geom, final IList<Double> rates) {
			final IList<GamaPoint> locs = GamaListFactory.create(Types.POINT);
			if (geom.getInnerGeometry() instanceof GeometryCollection) {
				for (int i = 0; i < geom.getInnerGeometry().getNumGeometries(); i++) {
					locs.addAll(GeometryUtils.locsAlongGeometry(geom.getInnerGeometry().getGeometryN(i), rates));
				}
			} else {
				locs.addAll(GeometryUtils.locsAlongGeometry(geom.getInnerGeometry(), rates));
			}
			return locs;
		}

		@operator (
				value = { "points_at" },
				content_type = IType.POINT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.POINT })
		@doc (
				value = "A list of left-operand number of points located at a the right-operand distance to the agent location.",
				examples = { @example (
						value = "3 points_at(20.0)",
						equals = "returns [pt1, pt2, pt3] with pt1, pt2 and pt3 located at a distance of 20.0 to the agent location",
						test = false) },
				see = { "any_location_in", "any_point_in", "closest_points_with", "farthest_point_to" })

		public static IList<ILocation> points_at(final IScope scope, final Integer nbLoc, final Double distance) {
			if (distance == null || nbLoc == null) // scope.setStatus(ExecutionStatus.failure);
				throw GamaRuntimeException.error("Impossible to compute points_at", scope);
			final IList<ILocation> locations = GamaListFactory.create(Types.POINT);
			final ILocation loc = scope.getAgent().getLocation();
			final double angle1 = scope.getRandom().between(0, 2 * Math.PI);

			for (int i = 0; i < nbLoc; i++) {
				final GamaPoint p =
						new GamaPoint(loc.getX() + distance * Math.cos(angle1 + (double) i / nbLoc * 2 * Math.PI),
								loc.getY() + distance * Math.sin(angle1 + (double) i / nbLoc * 2 * Math.PI));
				locations.add(p);
			}
			return locations;

		}

		@operator (
				value = "closest_points_with",
				type = IType.LIST,
				content_type = IType.POINT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.POINT })
		@doc (
				value = "A list of two closest points between the two geometries.",
				examples = { @example (
						value = "geom1 closest_points_with(geom2)",
						equals = "[pt1, pt2] with pt1 the closest point of geom1 to geom2 and pt1 the closest point of geom2 to geom1",
						isExecutable = false) },
				see = { "any_location_in", "any_point_in", "farthest_point_to", "points_at" })
		public static IList<GamaPoint> closest_points_with(final IShape a, final IShape b) {
			final Coordinate[] coors = DistanceOp.nearestPoints(a.getInnerGeometry(), b.getInnerGeometry());
			return GamaListFactory.wrap(Types.POINT, new GamaPoint(coors[0]), new GamaPoint(coors[1]));
		}

		@operator (
				value = "farthest_point_to",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.POINT })
		@doc (
				value = "the farthest point of the left-operand to the left-point.",
				examples = { @example (
						value = "geom farthest_point_to(pt)",
						equals = "the farthest point of geom to pt",
						isExecutable = false) },
				see = { "any_location_in", "any_point_in", "closest_points_with", "points_at" })
		public static ILocation farthest_point_to(final IShape g, final GamaPoint p) {
			if (g == null) return p.getLocation();
			if (p == null) return g.getLocation();

			final Coordinate[] cg = g.getInnerGeometry().getCoordinates();
			if (cg.length == 0) return p;
			Coordinate pt_max = cg[0];
			double dist_max = p.distance(pt_max);
			for (int i = 1; i < cg.length; i++) {
				final double dist = p.distance(cg[i]);
				if (dist > dist_max) {
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
		 * @param pt
		 *            a point
		 * @param poly
		 *            a polygon
		 */
		public static ILocation _closest_point_to(final IShape pt, final IShape geom) {
			if (pt == null) return null;
			if (geom == null) return pt.getLocation();
			final Coordinate[] cp = new DistanceOp(geom.getInnerGeometry(), pt.getInnerGeometry()).nearestPoints();
			return new GamaPoint(cp[0]);
		}

		public static ILocation _closest_point_to(final ILocation pt, final IShape geom) {
			if (pt == null) return null;
			if (geom == null) return pt;
			final PointPairDistance ppd = new PointPairDistance();
			DistanceToPoint.computeDistance(geom.getInnerGeometry(), GeometryUtils.toCoordinate(pt), ppd);
			return new GamaPoint(ppd.getCoordinate(0));
		}

		@operator (
				value = "angle_between",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT },
				concept = { IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION, IConcept.POINT })
		@doc (
				value = "the angle between vectors P0P1 and P0P2 (P0, P1, P2 being the three point operands)",
				examples = { @example (
						value = "angle_between({5,5},{10,5},{5,10})",
						equals = "90") })
		public static Double angleInDegreesBetween(final IScope scope, final GamaPoint p0, final GamaPoint p1,
				final GamaPoint p2) {
			final double Xa = p1.x - p0.x;
			final double Ya = p1.y - p0.y;
			final double Xb = p2.x - p0.x;
			final double Yb = p2.y - p0.y;
			final double Na = Maths.sqrt(scope, Xa * Xa + Ya * Ya);
			final double Nb = Maths.sqrt(scope, Xb * Xb + Yb * Yb);
			final double C = Maths.round((Xa * Xb + Ya * Yb) / (Na * Nb), 10);
			final double S = Xa * Yb - Ya * Xb;
			final double result = S > 0 ? Maths.acos(C) : -1 * Maths.acos(C);
			return Maths.checkHeading(result);
		}

	}

	public static abstract class Queries {

		@operator (
				value = "neighbors_of",
				content_type = IType.AGENT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION, IConcept.NEIGHBORS })
		@doc (
				value = "a list, containing all the agents of the same species than the argument (if it is an agent) located at a distance inferior or equal to 1 to the right-hand operand agent considering the left-hand operand topology.",
				masterDoc = true,
				examples = { @example (
						value = "topology(self) neighbors_of self",
						equals = "returns all the agents located at a distance lower or equal to 1 to the agent applying the operator considering its topology.",
						test = false) },
				see = { "neighbors_at", "closest_to", "overlapping", "agents_overlapping", "agents_inside",
						"agent_closest_to" })
		@no_test // already done in Spatial tests Models
		public static IList neighbors_of(final IScope scope, final ITopology t, final IAgent agent) {
			return _neighbors(scope, In.list(scope, agent.getPopulation()), agent, 1.0, t);
			// TODO We could compute a filter based on the population if it is
			// an agent
		}

		@operator (
				value = "neighbors_of",
				content_type = IType.AGENT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = {})
		/* TODO, expected_content_type = { IType.FLOAT, IType.INT } */
		@doc (
				usages = @usage (
						value = "a list, containing all the agents of the same species than the left argument (if it is an agent) located at a distance inferior or equal to the third argument to the second argument (agent, geometry or point) considering the first operand topology.",
						examples = { @example (
								value = "neighbors_of (topology(self), self,10)",
								equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator considering its topology.",
								test = false) }))
		@no_test // already done in Spatial tests Models
		public static IList neighbors_of(final IScope scope, final ITopology t, final IShape agent,
				final Double distance) {
			return _neighbors(scope,
					agent instanceof IAgent ? In.list(scope, ((IAgent) agent).getPopulation()) : Different.with(),
					agent, distance, t);
			// TODO We could compute a filter based on the population if it is
			// an agent
		}

		@operator (
				value = "neighbors_at",
				content_type = ITypeProvider.TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION, IConcept.NEIGHBORS })
		@doc (
				value = "a list, containing all the agents of the same species than the left argument (if it is an agent) located at a distance inferior or equal to the right-hand operand to the left-hand operand (geometry, agent, point).",
				comment = "The topology used to compute the neighborhood  is the one of the left-operand if this one is an agent; otherwise the one of the agent applying the operator.",
				examples = { @example (
						value = "(self neighbors_at (10))",
						equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator.",
						test = false) },
				see = { "neighbors_of", "closest_to", "overlapping", "agents_overlapping", "agents_inside",
						"agent_closest_to", "at_distance" })
		@no_test // already done in Spatial tests Models
		public static IList neighbors_at(final IScope scope, final IShape agent, final Double distance) {
			return _neighbors(scope,
					agent instanceof IAgent ? In.list(scope, ((IAgent) agent).getPopulation()) : Different.with(),
					agent, distance);
		}

		@operator (
				value = "at_distance",
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A list of agents or geometries among the left-operand list that are located at a distance <= the right operand from the caller agent (in its topology)",
				examples = { @example (
						value = "[ag1, ag2, ag3] at_distance 20",
						equals = "the agents of the list located at a distance <= 20 from the caller agent (in the same order).",
						isExecutable = false) },
				see = { "neighbors_at", "neighbors_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
						"overlapping" })
		@no_test // already done in Spatial tests Models
		public static IList<? extends IShape> at_distance(final IScope scope,
				final IContainer<?, ? extends IShape> list, final Double distance) {
			if (GamaPreferences.External.AT_DISTANCE_OPTIMIZATION.getValue()) {
				if (scope.getAgent().isPoint()) {
					final ITopology topo = scope.getTopology();
					if (topo.isContinuous() && !topo.isTorus()) {
						if ((double) list.length(scope) / (double) scope.getSimulation().getMembersSize(scope) < 0.1) {
							try (final Collector.AsList<IAgent> results = Collector.getList()) {
								final IAgent ag = scope.getAgent();
								for (final IShape sp : list.iterable(scope)) {
									if (ag.euclidianDistanceTo(sp) <= distance) { results.add((IAgent) sp); }
								}
								results.remove(ag);
								return results.items();
							}
						}
					}

				}

			}

			final IType contentType = list.getGamlType().getContentType();
			if (contentType.isAgentType())
				return _neighbors(scope, In.list(scope, list), scope.getAgent(), distance);
			else if (contentType == Types.GEOMETRY) return geomAtDistance(scope, list, distance);
			return GamaListFactory.create();
		}

		public static IList<? extends IShape> geomAtDistance(final IScope scope,
				final IContainer<?, ? extends IShape> list, final Double distance) {
			final IShape ag = scope.getAgent();
			final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			for (final Object shape : list.listValue(scope, Types.GEOMETRY, false)) {
				if (!(shape instanceof IShape)) { continue; }
				if (scope.getTopology().distanceBetween(scope, ag, (IShape) shape) <= distance) {
					geoms.add((IShape) shape);
				}
			}
			return geoms;
		}

		@operator (
				value = { "inside" },
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A list of agents or geometries among the left-operand list, species or meta-population (addition of species), covered by the operand (casted as a geometry).",
				examples = { @example (
						value = "[ag1, ag2, ag3] inside(self)",
						equals = "the agents among ag1, ag2 and ag3 that are covered by the shape of the right-hand argument.",
						isExecutable = false),
						@example (
								value = "(species1 + species2) inside (self)",
								equals = "the agents among species species1 and species2 that are covered by the shape of the right-hand argument.",
								isExecutable = false) },
				see = { "neighbors_at", "neighbors_of", "closest_to", "overlapping", "agents_overlapping",
						"agents_inside", "agent_closest_to" })
		@no_test // already done in Spatial tests Models
		public static IList<? extends IShape> inside(final IScope scope, final IContainer<?, ? extends IShape> list,
				final IShape source) {
			final IType contentType = list.getGamlType().getContentType();
			if (contentType.isAgentType())
				return _gather(scope, In.list(scope, list), source, true);
			else if (contentType == Types.GEOMETRY) return geomOverlapping(scope, list, source, true);
			return GamaListFactory.EMPTY_LIST;
		}

		@operator (
				value = { "overlapping" },
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A list of agents or geometries among the left-operand list, species or meta-population (addition of species), overlapping the operand (casted as a geometry).",
				examples = { @example (
						value = "[ag1, ag2, ag3] overlapping(self)",
						equals = "return the agents among ag1, ag2 and ag3 that overlap the shape of the agent applying the operator.",
						isExecutable = false),
						@example (
								value = "(species1 + species2) overlapping self",
								isExecutable = false) },
				see = { "neighbors_at", "neighbors_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
						"agents_overlapping" })
		@no_test // test already done in Spatial tests models
		public static IList<? extends IShape> overlapping(final IScope scope,
				final IContainer<?, ? extends IShape> list, final IShape source) {
			final IType contentType = list.getGamlType().getContentType();
			if (contentType.isAgentType())
				return _gather(scope, In.list(scope, list), source, false);
			else if (contentType == Types.GEOMETRY) return geomOverlapping(scope, list, source, false);
			return GamaListFactory.EMPTY_LIST;
		}

		public static IList<? extends IShape> geomOverlapping(final IScope scope,
				final IContainer<?, ? extends IShape> list, final IShape source, final boolean cover) {
			final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			for (final Object shape : list.listValue(scope, Types.GEOMETRY, false)) {
				if (!(shape instanceof IShape)) { continue; }
				if (cover) {
					if (source.covers((IShape) shape)) { geoms.add((IShape) shape); }
				} else {
					if (source.intersects((IShape) shape)) { geoms.add((IShape) shape); }
				}

			}
			return geoms;
		}

		@operator (
				value = { "closest_to" },
				type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "An agent or a geometry among the left-operand list of agents, species or meta-population (addition of species), the closest to the operand (casted as a geometry).",
				comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
				examples = { @example (
						value = "[ag1, ag2, ag3] closest_to(self)",
						equals = "return the closest agent among ag1, ag2 and ag3 to the agent applying the operator.",
						isExecutable = false),
						@example (
								value = "(species1 + species2) closest_to self",
								isExecutable = false) },
				see = { "neighbors_at", "neighbors_of", "inside", "overlapping", "agents_overlapping", "agents_inside",
						"agent_closest_to" })
		@no_test // already done in Spatial tests Models
		public static IShape closest_to(final IScope scope, final IContainer<?, ? extends IShape> list,
				final IShape source) {
			if (list == null) return null;
			final IType contentType = list.getGamlType().getContentType();
			if (contentType.isAgentType())
				return _closest(scope, In.list(scope, list), source);
			else if (list.getGamlType().getContentType().isTranslatableInto(Types.GEOMETRY))
				return geomClostestTo(scope, list, source);
			return null;
		}

		@operator (
				value = { "closest_to" },
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "The N agents or geometries among the left-operand list of agents, species or meta-population (addition of species), that are the closest to the operand (casted as a geometry).",
				comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
				examples = { @example (
						value = "[ag1, ag2, ag3] closest_to(self, 2)",
						equals = "return the 2 closest agents among ag1, ag2 and ag3 to the agent applying the operator.",
						isExecutable = false),
						@example (
								value = "(species1 + species2) closest_to (self, 5)",
								isExecutable = false) },
				see = { "neighbors_at", "neighbors_of", "inside", "overlapping", "agents_overlapping", "agents_inside",
						"agent_closest_to" })
		@no_test // already done in Spatial tests Models
		public static IList<IShape> closest_to(final IScope scope, final IContainer<?, ? extends IShape> list,
				final IShape source, final int number) {
			if (list == null || list.isEmpty(scope)) return GamaListFactory.EMPTY_LIST;
			final IType contentType = list.getGamlType().getContentType();
			if (contentType.isAgentType())
				return (IList) _closest(scope, In.list(scope, list), source, number);
			else if (list.getGamlType().getContentType().isTranslatableInto(Types.GEOMETRY))
				return (IList<IShape>) geomClostestTo(scope, list, source, number);
			return GamaListFactory.create(contentType);
		}

		@operator (
				value = { "farthest_to" },
				type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "An agent or a geometry among the left-operand list of agents, species or meta-population (addition of species), the farthest to the operand (casted as a geometry).",
				comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
				examples = { @example (
						value = "[ag1, ag2, ag3] closest_to(self)",
						equals = "return the farthest agent among ag1, ag2 and ag3 to the agent applying the operator.",
						isExecutable = false),
						@example (
								value = "(species1 + species2) closest_to self",
								isExecutable = false) },
				see = { "neighbors_at", "neighbors_of", "neighbors_at", "inside", "overlapping", "agents_overlapping",
						"agents_inside", "agent_closest_to", "closest_to", "agent_farthest_to" })
		@no_test // already done in Spacial tests Models
		public static IShape farthest_to(final IScope scope, final IContainer<?, ? extends IShape> list,
				final IShape source) {
			final IType contentType = list.getGamlType().getContentType();
			if (contentType.isAgentType())
				return _farthest(scope, In.list(scope, list), source);
			else if (list.getGamlType().getContentType().isTranslatableInto(Types.GEOMETRY))
				return geomFarthestTo(scope, list, source);
			return null;
		}

		public static IShape geomClostestTo(final IScope scope, final IContainer<?, ? extends IShape> list,
				final IShape source) {
			IShape shp = null;
			double distMin = Double.MAX_VALUE;
			for (final Object shape : list.listValue(scope, Types.GEOMETRY, false)) {
				if (!(shape instanceof IShape)) { continue; }
				final double dist = scope.getTopology().distanceBetween(scope, source, (IShape) shape);
				if (dist < distMin) {
					shp = (IShape) shape;
					distMin = dist;
				}
			}
			return shp;
		}

		public static Collection<IShape> geomClostestTo(final IScope scope, final IContainer<?, ? extends IShape> list,
				final IShape source, final int number) {
			final IList<?> objects = list.listValue(scope, Types.GEOMETRY, true);
			objects.removeIf(a -> !(a instanceof IShape));
			final IList<IShape> shapes = (IList<IShape>) objects;
			if (shapes.size() <= number) return shapes;
			scope.getRandom().shuffleInPlace(shapes);
			final Ordering<IShape> ordering = Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
			return GamaListFactory.wrap(Types.GEOMETRY, ordering.leastOf(shapes, number));
		}

		public static IShape geomFarthestTo(final IScope scope, final IContainer<?, ? extends IShape> list,
				final IShape source) {
			IShape shp = null;
			double distMax = Double.MIN_VALUE;
			for (final Object shape : list.listValue(scope, Types.GEOMETRY, false)) {
				if (!(shape instanceof IShape)) { continue; }
				final double dist = scope.getTopology().distanceBetween(scope, source, (IShape) shape);
				if (dist > distMax) {
					shp = (IShape) shape;
					distMax = dist;
				}
			}
			return shp;
		}

		@operator (
				value = "agent_closest_to",
				type = IType.AGENT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "An agent, the closest to the operand (casted as a geometry).",
				comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
				examples = { @example (
						value = "agent_closest_to(self)",
						equals = "the closest agent to the agent applying the operator.",
						test = false) },
				see = { "neighbors_at", "neighbors_of", "agents_inside", "agents_overlapping", "closest_to", "inside",
						"overlapping" })
		@no_test // already done in Spatial tests Models
		public static IAgent agent_closest_to(final IScope scope, final Object source) {
			return _closest(scope, Different.with(), source);
		}

		@operator (
				value = "agent_farthest_to",
				type = IType.AGENT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "An agent, the farthest to the operand (casted as a geometry).",
				comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
				examples = { @example (
						value = "agent_farthest_to(self)",
						equals = "the farthest agent to the agent applying the operator.",
						test = false) },
				see = { "neighbors_at", "neighbors_of", "agents_inside", "agents_overlapping", "closest_to", "inside",
						"overlapping", "agent_closest_to", "farthest_to" })
		public static IAgent agent_farthest_to(final IScope scope, final Object source) {
			return _farthest(scope, Different.with(), source);
		}

		@operator (
				value = "agents_inside",
				content_type = IType.AGENT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A list of agents covered by the operand (casted as a geometry).",
				examples = { @example (
						value = "agents_inside(self)",
						equals = "the agents that are covered by the shape of the agent applying the operator.",
						test = false) },
				see = { "agent_closest_to", "agents_overlapping", "closest_to", "inside", "overlapping" })
		@no_test // already done in Spacial tests Models
		public static IList<IAgent> agents_inside(final IScope scope, final Object source) {
			return _gather(scope, Different.with(), source, true);
		}

		@operator (
				value = "agents_overlapping",
				content_type = IType.AGENT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A list of agents overlapping the operand (casted as a geometry).",
				examples = { @example (
						value = "agents_overlapping(self)",
						equals = "the agents that overlap the shape of the agent applying the operator.",
						test = false) },
				see = { "neighbors_at", "neighbors_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
						"overlapping", "at_distance" })
		@no_test // already done in Spatial tests Models
		public static IList<IAgent> agents_overlapping(final IScope scope, final Object source) {
			return _gather(scope, Different.with(), source, false);
		}

		@operator (
				value = "agents_at_distance",
				content_type = IType.AGENT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_RELATION,
						IConcept.AGENT_LOCATION })
		@doc (
				value = "A list of agents situated at a distance lower than the right argument.",
				examples = { @example (
						value = "agents_at_distance(20)",
						equals = "all the agents (excluding the caller) which distance to the caller is lower than 20",
						test = false) },
				see = { "neighbors_at", "neighbors_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
						"overlapping", "at_distance" })
		public static IList agents_at_distance(final IScope scope, final Double distance) {
			return _neighbors(scope, Different.with(), scope.getAgent(), distance);
		}

		// Support methods used by the different queries

		private static IList<IAgent> _gather(final IScope scope, final IAgentFilter filter, final Object source,
				final boolean inside) {
			if (filter == null || source == null) return GamaListFactory.EMPTY_LIST;
			final IType type = filter.getSpecies() == null ? Types.AGENT : scope.getType(filter.getSpecies().getName());
			return GamaListFactory.wrap(type,
					scope.getTopology().getAgentsIn(scope, Cast.asGeometry(scope, source, false), filter, inside));
		}

		private static IAgent _closest(final IScope scope, final IAgentFilter filter, final Object source) {
			if (filter == null || source == null) return null;
			return scope.getTopology().getAgentClosestTo(scope, Cast.asGeometry(scope, source, false), filter);
		}

		private static Collection<IAgent> _closest(final IScope scope, final IAgentFilter filter, final Object source,
				final int number) {
			if (filter == null || source == null) return null;
			return scope.getTopology().getAgentClosestTo(scope, Cast.asGeometry(scope, source, false), filter, number);
		}

		private static IAgent _farthest(final IScope scope, final IAgentFilter filter, final Object source) {
			if (filter == null || source == null) return null;
			return scope.getTopology().getAgentFarthestTo(scope, Cast.asGeometry(scope, source, false), filter);
		}

		private static IList<IAgent> _neighbors(final IScope scope, final IAgentFilter filter, final Object source,
				final Object distance) {
			return _neighbors(scope, filter, source, distance, scope.getTopology());
		}

		static IList<IAgent> _neighbors(final IScope scope, final IAgentFilter filter, final Object source,
				final Object distance, final ITopology t) {
			if (filter == null || source == null) return GamaListFactory.EMPTY_LIST;
			final IType type = filter.getSpecies() == null ? Types.AGENT : scope.getType(filter.getSpecies().getName());
			return GamaListFactory.wrap(type, t.getNeighborsOf(scope, Cast.asGeometry(scope, source, false),
					Cast.asFloat(scope, distance), filter));
		}

	}

	public static abstract class Statistics {

		@operator (
				value = { "simple_clustering_by_distance", "simple_clustering_by_envelope_distance" },
				content_type = ITypeProvider.TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_STATISTICAL,
						IOperatorCategory.STATISTICAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.AGENT_LOCATION,
						IConcept.STATISTIC })
		@doc (
				value = "A list of agent groups clustered by distance considering a distance min between two groups.",
				examples = { @example (
						value = "[ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0",
						equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
						isExecutable = false) },
				see = { "hierarchical_clustering" })
		public static IList<IList<IAgent>> simpleClusteringByDistance(final IScope scope,
				final IContainer<?, IAgent> agents, final Double distance) {
			final IList<IList<IAgent>> groups =
					GamaListFactory.create(Types.LIST.of(agents.getGamlType().getContentType()));
			final IAgentFilter filter = In.list(scope, agents);
			if (filter == null) return groups;
			try (Collector.AsSet<IAgent> clusteredCells = Collector.getSet()) {
				for (final IAgent ag : agents.iterable(scope)) {
					if (!clusteredCells.contains(ag)) {
						groups.add(simpleClusteringByDistanceRec(scope, filter, distance, clusteredCells, ag));
					}
				}
				return groups;
			}
		}

		public static IList<IAgent> simpleClusteringByDistanceRec(final IScope scope, final IAgentFilter filter,
				final Double distance, final Collection<IAgent> clusteredAgs, final IAgent currentAg) {
			final IList<IAgent> group = GamaListFactory.create(Types.AGENT);
			final List<IAgent> ags =
					new ArrayList<>(scope.getTopology().getNeighborsOf(scope, currentAg, distance, filter));
			clusteredAgs.add(currentAg);
			group.add(currentAg);
			for (final IAgent ag : ags) {
				if (!clusteredAgs.contains(ag)) {
					group.addAll(simpleClusteringByDistanceRec(scope, filter, distance, clusteredAgs, ag));
				}
			}
			return group;
		}

		@operator (
				value = { "hierarchical_clustering" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_STATISTICAL,
						IOperatorCategory.STATISTICAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.AGENT_LOCATION,
						IConcept.STATISTIC })
		@doc (
				value = "A tree (list of list) contained groups of agents clustered by distance considering a distance min between two groups.",
				comment = "use of hierarchical clustering with Minimum for linkage criterion between two groups of agents.",
				examples = { @example (
						value = "[ag1, ag2, ag3, ag4, ag5] hierarchical_clustering 20.0",
						equals = "for example, can return [[[ag1],[ag3]], [ag2], [[[ag4],[ag5]],[ag6]]",
						isExecutable = false) },
				see = { "simple_clustering_by_distance" })
		public static IList hierarchicalClusteringe(final IScope scope, final IContainer<?, IAgent> agents,
				final Double distance) {
			final int nb = agents.length(scope);
			final IList<IList> groups = GamaListFactory.create();

			if (nb == 0) // scope.setStatus(ExecutionStatus.failure);
				return groups;
			double distMin = Double.MAX_VALUE;

			Set<IList> minFusion = null;

			final Map<Set<IList>, Double> distances = new HashMap<>();
			for (final IAgent ag : agents.iterable(scope)) {
				final IList group = GamaListFactory.create(Types.AGENT);
				group.add(ag);
				groups.add(group);
			}

			if (nb == 1) return groups;
			// BY GEOMETRIES
			for (int i = 0; i < nb - 1; i++) {
				final IList g1 = groups.get(i);
				for (int j = i + 1; j < nb; j++) {
					final IList g2 = groups.get(j);
					final Set<IList> distGp = new HashSet<>();
					distGp.add(g1);
					distGp.add(g2);
					final IAgent a = (IAgent) g1.get(0);
					final IAgent b = (IAgent) g2.get(0);
					final Double dist = scope.getTopology().distanceBetween(scope, a, b);
					if (dist < distance) {
						distances.put(distGp, dist);
						if (dist < distMin) {
							distMin = dist;
							minFusion = distGp;
						}
					}
				}
			}
			while (distMin <= distance) {

				IList<IList> fusionL = GamaListFactory.create();
				fusionL.addAll(minFusion);
				final IList<IAgent> g1 = fusionL.get(0);
				final IList<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				final IList groupeF = GamaListFactory.create(Types.LIST.of(Types.AGENT));
				groupeF.add(g2);
				groupeF.add(g1);

				for (final IList groupe : groups) {
					final Set<IList> newDistGp = new HashSet<>();
					newDistGp.add(groupe);
					newDistGp.add(g1);
					double dist1 = Double.MAX_VALUE;
					if (distances.containsKey(newDistGp)) { dist1 = distances.remove(newDistGp).doubleValue(); }
					newDistGp.remove(g1);
					newDistGp.add(g2);
					double dist2 = Double.MAX_VALUE;
					if (distances.containsKey(newDistGp)) { dist2 = distances.remove(newDistGp).doubleValue(); }
					final double dist = Math.min(dist1, dist2);
					if (dist <= distance) {
						newDistGp.remove(g2);
						newDistGp.add(groupeF);
						distances.put(newDistGp, Double.valueOf(dist));
					}

				}
				groups.add(groupeF);

				distMin = Double.MAX_VALUE;
				minFusion = null;
				for (final Set<IList> distGp : distances.keySet()) {
					final double dist = distances.get(distGp).doubleValue();
					if (dist < distMin) {
						minFusion = distGp;
						distMin = dist;
					}
				}
			}
			return groups;
		}

		@operator (
				value = { "IDW", "inverse_distance_weighting" },
				category = { IOperatorCategory.SPATIAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.STATISTIC })
		@doc (
				value = "Inverse Distance Weighting (IDW) is a type of deterministic method for multivariate "
						+ "interpolation with a known scattered set of points. The assigned values to each geometry are calculated with a weighted average of the values available at the known points. See: http://en.wikipedia.org/wiki/Inverse_distance_weighting "
						+ "Usage: IDW (list of geometries, map of points (key: point, value: value), power parameter)",
				examples = { @example (
						value = "IDW([ag1, ag2, ag3, ag4, ag5],[{10,10}::25.0, {10,80}::10.0, {100,10}::15.0], 2)",
						equals = "for example, can return [ag1::12.0, ag2::23.0,ag3::12.0,ag4::14.0,ag5::17.0]",
						isExecutable = false) })
		@test ("map<point, float> mapLocationPoints <- [{0,0}::10.0,{0,10}::-3.0];\r\n"
				+ "		list<point> queryPoint <- [{0,5}];\r\n"
				+ "		float((IDW(list(geometry(queryPoint)),mapLocationPoints,1)).pairs[0].value) with_precision 1 = 3.5")
		public static IMap<IShape, Double> primIDW(final IScope scope, final IContainer<?, ? extends IShape> geometries,
				final IMap points, final int power) {
			final IMap<IShape, Double> results = GamaMapFactory.create(Types.GEOMETRY, Types.FLOAT);
			if (points == null || points.isEmpty()) return null;
			if (geometries == null || geometries.isEmpty(scope)) return results;
			for (final IShape geom : geometries.iterable(scope)) {
				double sum = 0;
				double weight = 0;
				double sumNull = 0;
				int nbNull = 0;
				for (final Object obj : points.keySet()) {
					final GamaPoint pt = (GamaPoint) Cast.asPoint(scope, obj);
					final double dist = scope.getTopology().distanceBetween(scope, geom, pt);
					if (dist == 0) {
						nbNull++;
						sumNull += Cast.asFloat(scope, points.get(pt));
					}
					if (nbNull == 0) {
						final double w = 1 / Math.pow(dist, power);
						weight += w;
						sum += w * Cast.asFloat(scope, points.get(pt));
					}
				}
				if (nbNull > 0) {
					results.put(geom, sumNull / nbNull);
				} else {
					results.put(geom, sum / weight);
				}

			}
			return results;
		}

		@operator (
				value = "moran",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.STATISTICAL },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION })
		@doc (
				usages = { @usage (
						value = "return the Moran Index of the given list of interest points (list of floats) and the weight matrix (matrix of float)",
						examples = { @example (
								value = "moran([1.0, 0.5, 2.0], weight_matrix)",
								equals = "the Moran index is computed",
								test = false,
								isExecutable = false) }) })
		public static double moranIndex(final IScope scope, final IList<Double> vals, final IMatrix<Double> mat) {
			final GamaMatrix<Double> weightMatrix = (GamaMatrix<Double>) mat;
			if (weightMatrix == null || weightMatrix.numCols != weightMatrix.numRows) throw GamaRuntimeException
					.error("A squared weight matrix should be given for the moran index computation", scope);
			final int N = vals.size();
			Double I = 0.0;
			Double sumWeights = 0.0;
			double sumXi = 0;
			final Double mean = (Double) Containers.mean(scope, vals);
			for (int i = 0; i < N; i++) {
				final double xi = vals.get(i);
				final double xiDev = xi - mean;
				sumXi += Math.pow(xiDev, 2);
				for (int j = 0; j < N; j++) {
					final Double weight = weightMatrix.get(scope, i, j);
					sumWeights += weight;
					I += weight * xiDev * (vals.get(j) - mean);
				}
			}
			I /= sumXi;
			I *= N / sumWeights;
			return I;
		}
	}

	public static abstract class ThreeD {

		@operator (
				value = { "set_z" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.POINT, IConcept.THREED })
		@doc (
				value = "Sets the z ordinate of the n-th point of a geometry to the value provided by the third argument",
				masterDoc = true,
				examples = { @example (
						value = "set_z (triangle(3), 1, 3.0)",
						test = false) },
				see = {})
		@test ("set_z (triangle(3), 1, 3.0).points[1].z = 3.0")
		public static IShape set_z(final IScope scope, final IShape geom, final Integer index, final Double z) {
			if (geom == null) return null;
			final Geometry g = geom.getInnerGeometry();
			if (g == null) return geom;
			if (index < 0 || index > g.getNumPoints() - 1) throw GamaRuntimeException
					.warning("Trying to modify a point outside the bounds of the geometry", scope);
			g.apply(new CoordinateSequenceFilter() {

				boolean done = false;

				@Override
				public void filter(final CoordinateSequence seq, final int i) {
					if (i == index) {
						seq.getCoordinate(i).z = z;
						done = true;
					}
				}

				@Override
				public boolean isDone() {
					return done;
				}

				@Override
				public boolean isGeometryChanged() {
					return done;
				}
			});

			return geom;
		}

		@operator (
				value = { "set_z" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
				concept = {})
		@doc (
				value = "Sets the z ordinate of each point of a geometry to the value provided, in order, by the right argument",
				examples = { @example (
						value = "triangle(3) set_z [5,10,14]",
						test = false) },
				see = {})
		@test ("list zzz <- (triangle(3) set_z [5,10,14]).points collect each.z; zzz[1] = 10")
		public static IShape set_z(final IScope scope, final IShape geom, final IContainer<?, Double> coords) {
			if (geom == null) return null;
			final Geometry g = geom.getInnerGeometry();
			if (g == null) return geom;
			if (coords == null || coords.isEmpty(scope)) return null;
			if (coords.length(scope) > g.getNumPoints()) throw GamaRuntimeException
					.warning("Trying to modify a point outside the bounds of the geometry", scope);
			final Double[] zs = coords.listValue(scope, Types.FLOAT, false).toArray(new Double[0]);
			g.apply(new CoordinateSequenceFilter() {

				@Override
				public void filter(final CoordinateSequence seq, final int i) {
					if (i <= zs.length - 1) { seq.getCoordinate(i).z = zs[i]; }
				}

				@Override
				public boolean isDone() {
					return false;
				}

				@Override
				public boolean isGeometryChanged() {
					return true;
				}
			});

			return geom;
		}

		@operator (
				value = "rgb_to_xyz",
				type = IType.LIST,
				content_type = IType.POINT,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.THREED, IConcept.COLOR })
		@doc (
				value = "A list of point corresponding to RGB value of an image (r:x , g:y, b:z)",
				deprecated = "Not used anymore",
				examples = { @example (
						value = "rgb_to_xyz(texture)",
						equals = "a list of points",
						isExecutable = false) },
				see = {})
		public static IList<ILocation> rgb_to_xyz(final IScope scope, final GamaFile file) {

			final IList<ILocation> points = GamaListFactory.create(Types.POINT);
			BufferedImage texture = null;
			int rows, cols, x, y;

			if (file instanceof GamaImageFile) {
				texture = ((GamaImageFile) file).getImage(scope, true);
			} else {
				if (file == null) throw error("File is null in rgb_to_xyz", scope);
				throw error("Impossible to read points from " + file.getPath(scope), scope);
			}
			if (texture == null) return points;
			rows = texture.getHeight() - 1;
			cols = texture.getWidth() - 1;

			for (y = 0; y < rows; y++) {
				for (x = 0; x <= cols; x++) {
					final Color c = new Color(texture.getRGB(cols - x, y));
					points.add(new GamaPoint(c.getRed(), c.getGreen(), c.getBlue()));
				}
			}

			return points;
		}

		@operator (
				value = "is_clockwise",
				type = IType.BOOL,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.THREED })
		@doc (
				value = "returns true if the geometry is defined clockwise ",
				deprecated = "Always returns true as all geometries are defined clockwise",
				examples = { @example (
						value = "is_clockwise(circle(10))",
						equals = "true",
						isExecutable = true) },
				see = { "change_clockwise" })
		public static Boolean isClockwise(final IScope scope, final IShape shape) {
			return true;
			// final IShape sp = Cast.asGeometry(scope, shape);
			// if (sp == null) { return true; }
			// return GeometryUtils.isClockwise(sp.getInnerGeometry());
		}

		@operator (
				value = "change_clockwise",
				type = IType.GEOMETRY,
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.THREED })
		@doc (
				value = "Returns a new geometry defined clockwise ",
				deprecated = "All geometries are now defined clockwise in GAMA",
				examples = { @example (
						value = "is_clockwise(circle(10))",
						equals = "true",
						isExecutable = true) },
				see = { "is_clockwise" })
		public static IShape changeClockwise(final IScope scope, final IShape shape) {
			return new GamaShape(shape);
			// final IShape sp = Cast.asGeometry(scope, shape);
			// if (sp == null) { return null; }
			// return new GamaShape(GeometryUtils.changeClockwise(sp.getInnerGeometry()));
		}

	}

	public static abstract class Projections {

		@operator (
				value = "crs",
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.FILE },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.FILE, IConcept.GIS })
		@doc (
				value = "the Coordinate Reference System (CRS) of the GIS file",
				examples = { @example (
						value = "crs(my_shapefile)",
						equals = "the crs of the shapefile",
						isExecutable = false) },
				see = {})
		public static String crsFromFile(final IScope scope, final GamaFile gisFile) {
			if (gisFile instanceof GamaGisFile) {
				final CoordinateReferenceSystem crs = ((GamaGisFile) gisFile).getGis(scope).getInitialCRS(scope);
				if (crs == null) return null;
				try {
					return CRS.lookupIdentifier(crs, true);
				} catch (final FactoryException e) {
					return null;
				} catch (final NullPointerException e) {
					return null;
				}
			} else
				throw GamaRuntimeException.error("Impossible to compute the CRS for this type of file", scope);
		}

		@operator (
				value = { "CRS_transform" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.GIS })
		@doc (
				usages = { @usage (
						value = "returns the geometry corresponding to the transformation of the given geometry by the current CRS (Coordinate Reference System), the one corresponding to the world's agent one",
						examples = { @example (
								value = "CRS_transform(shape)",
								equals = "a geometry corresponding to the agent geometry transformed into the current CRS",
								test = false) }) })
		public static IShape transform_CRS(final IScope scope, final IShape g) {
			final IProjection gis = scope.getSimulation().getProjectionFactory().getWorld();
			if (gis == null) return g.copy(scope);
			final IShape s = new GamaShape(gis.inverseTransform(g.getInnerGeometry()));
			if (g instanceof ILocation) return s.getLocation();
			return s;
		}

		@operator (
				value = { "to_GAMA_CRS" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.GIS })
		@doc (
				usages = { @usage (
						value = "returns the geometry corresponding to the transformation of the given geometry to the GAMA CRS (Coordinate Reference System) assuming the given geometry is referenced by the current CRS, the one corresponding to the world's agent one",
						examples = { @example (
								value = "to_GAMA_CRS({121,14})",
								equals = "a geometry corresponding to the agent geometry transformed into the GAMA CRS",
								test = false) }) })
		public static IShape to_GAMA_CRS(final IScope scope, final IShape g) {
			final IProjection gis = scope.getSimulation().getProjectionFactory().getWorld();
			if (gis == null) return g.copy(scope);
			final IShape s = new GamaShape(gis.transform(g.getInnerGeometry()));
			if (g instanceof ILocation) return s.getLocation();
			return s;
		}

		@operator (
				value = { "to_GAMA_CRS" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = {})
		@doc (
				usages = { @usage (
						value = "returns the geometry corresponding to the transformation of the given geometry to the GAMA CRS (Coordinate Reference System) assuming the given geometry is referenced by given CRS",
						examples = { @example (
								value = "to_GAMA_CRS({121,14}, \"EPSG:4326\")",
								equals = "a geometry corresponding to the agent geometry transformed into the GAMA CRS",
								test = false) }) })
		@no_test
		public static IShape to_GAMA_CRS(final IScope scope, final IShape g, final String code) {
			IProjection gis;
			try {
				gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
			} catch (final FactoryException e) {
				throw GamaRuntimeException.error("The code " + code + " does not correspond to a known EPSG code",
						scope);
			}
			if (gis == null) return g.copy(scope);
			final IShape s = new GamaShape(gis.transform(g.getInnerGeometry()));
			if (g instanceof ILocation) return s.getLocation();
			return s;
		}

		@operator (
				value = { "CRS_transform" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.GIS })
		@doc (
				usages = { @usage (
						value = "returns the geometry corresponding to the transformation of the given geometry by the left operand CRS (Coordinate Reference System)",
						examples = { @example (
								value = "shape CRS_transform(\"EPSG:4326\")",
								equals = "a geometry corresponding to the agent geometry transformed into the EPSG:4326 CRS",
								test = false) }) })
		@no_test
		public static IShape transform_CRS(final IScope scope, final IShape g, final String code) {
			IProjection gis;
			try {
				gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
			} catch (final FactoryException e) {
				throw GamaRuntimeException.error("The code " + code + " does not correspond to a known EPSG code",
						scope);
			}
			if (gis == null) return g.copy(scope);
			final IShape s = new GamaShape(gis.inverseTransform(g.getInnerGeometry()));
			if (g instanceof ILocation) return s.getLocation();
			return s;
		}

		@operator (
				value = { "CRS_transform" },
				category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS },
				concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.SPATIAL_TRANSFORMATION,
						IConcept.GIS })
		@doc (
				usages = { @usage (
						value = "returns the geometry corresponding to the transformation of the given geometry from the first CRS to the second CRS (Coordinate Reference System)",
						examples = { @example (
								value = "{8.35,47.22} CRS_transform(\"EPSG:4326\",\"EPSG:4326\")",
								equals = "{929517.7481238344,5978057.894895313,0.0}",
								test = false) }) })
		@no_test
		public static IShape transform_CRS(final IScope scope, final IShape g, final String sourceCode,
				final String targetcode) {
			if (g == null) return g;
			CoordinateReferenceSystem sourceCRS;
			try {
				sourceCRS = CRS.decode(sourceCode);
			} catch (final FactoryException e) {
				throw GamaRuntimeException.error("The code " + sourceCode + " does not correspond to a known EPSG code",
						scope);
			}
			CoordinateReferenceSystem targetCRS;
			try {
				targetCRS = CRS.decode(targetcode);
			} catch (final FactoryException e) {
				throw GamaRuntimeException.error("The code " + targetcode + " does not correspond to a known EPSG code",
						scope);
			}

			MathTransform transform;
			Geometry targetGeometry = null;
			try {
				transform = CRS.findMathTransform(sourceCRS, targetCRS);
				targetGeometry = JTS.transform(g.getInnerGeometry(), transform);
			} catch (final Exception e) {
				throw GamaRuntimeException.error("No transformation found from " + sourceCode + " to " + targetcode,
						scope);
			}
			if (targetGeometry == null) return null;
			final IShape s = new GamaShape(targetGeometry);
			if (g instanceof ILocation) return s.getLocation();
			return s;
		}
	}
}
