/*********************************************************************************************
 *
 *
 * 'Spatial.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import gnu.trove.set.hash.THashSet;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.*;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.*;
import msi.gaml.types.*;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.algorithm.distance.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.operation.buffer.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.util.AssertionFailedException;

/**
 * Written by drogoul Modified on 10 dec. 2010
 *
 * All the spatial operators available in GAML. Regrouped by types of operators.
 *
 */
public abstract class Spatial {

	/**
	 * The class Spatial.
	 *
	 * @author Alexis Drogoul, Patrick Taillandier, Arnaud Grignard
	 * @since 29 nov. 2011
	 *
	 */

	final static PointLocator pl = new PointLocator();

	public static class Common extends Object {}

	public static abstract class Creation {

		@operator(value = "circle", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A circle geometry which radius is equal to the operand.",
			usages = { @usage(value = "returns a point if the operand is lower or equal to 0.") },
			comment = "the centre of the circle is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "circle(10)", equals = "a geometry as a circle of radius 10.", test = false) },
			see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
				"triangle" })
		public static
			IShape circle(final IScope scope, final Double radius) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCircle(radius, location);
		}

		@operator(value = "ellipse", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An ellipse geometry which x-radius is equal to the first operand and y-radius is equal to the second operand",
			usages = { @usage(value = "returns a point if both operands are lower or equal to 0, a line if only one is.") },
			comment = "the centre of the ellipse is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "ellipse(10, 10)",
				equals = "a geometry as an ellipse of width 10 and height 10.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "circle", "squircle", "triangle" })
		public static
			IShape ellipse(final IScope scope, final Double xRadius, final Double yRadius) {
			GamaPoint location;
			final IAgent a = scope.getAgentScope();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if ( xRadius <= 0 ) {
				if ( yRadius <= 0 ) { return new GamaShape(location); }
			}
			return GamaGeometryType.buildEllipse(xRadius, yRadius, location);
		}

		@operator(value = "squircle", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A mix of square and circle geometry (see : http://en.wikipedia.org/wiki/Squircle), which side size is equal to the first operand and power is equal to the second operand",
			usages = { @usage(value = "returns a point if the side operand is lower or equal to 0.") },
			comment = "the centre of the ellipse is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "squircle(4,4)",
				equals = "a geometry as a squircle of side 4 with a power of 4.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"super_ellipse", "rectangle", "square", "circle", "ellipse", "triangle" })
		public static
			IShape squicle(final IScope scope, final Double xRadius, final Double power) {
			GamaPoint location;
			final IAgent a = scope.getAgentScope();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if ( xRadius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSquircle(xRadius, power, location);
		}

		@operator(value = "arc", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An arc, which radius is equal to the first operand, heading to the second and amplitude the third",
			usages = { @usage(value = "returns a point if the radius operand is lower or equal to 0.") },
			comment = "the centre of the arc is by default the location of the current agent in which has been called this operator. This operator returns a polygon by default.",
			examples = { @example(value = "arc(4,45,90)",
				equals = "a geometry as an arc of radius 4, in a direction of 45° and an amplitude of 90°",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"super_ellipse", "rectangle", "square", "circle", "ellipse", "triangle" })
		public static
			IShape arc(final IScope scope, final Double xRadius, final Double heading, final Double amplitude) {
			return arc(scope, xRadius, heading, amplitude, true);
		}

		@operator(value = "arc", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An arc, which radius is equal to the first operand, heading to the second, amplitude to the third and a boolean indicating whether to return a linestring or a polygon to the fourth",
			usages = { @usage(value = "returns a point if the radius operand is lower or equal to 0.") },
			comment = "the centre of the arc is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "arc(4,45,90, false)",
				equals = "a geometry as an arc of radius 4, in a direction of 45° and an amplitude of 90°, which only contains the points on the arc",
				test = false) },
			see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "super_ellipse",
				"rectangle", "square", "circle", "ellipse", "triangle" })
		public static
			IShape arc(final IScope scope, final Double xRadius, final Double heading, final Double amplitude,
				final boolean filled) {
			GamaPoint location;
			final IAgent a = scope.getAgentScope();
			location = (GamaPoint) (a != null ? a.getLocation() : new GamaPoint(0, 0));
			if ( xRadius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildArc(xRadius, heading, amplitude, filled, location);
		}

		@operator(value = "cylinder", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A cylinder geometry which radius is equal to the operand.",
			usages = { @usage(value = "returns a point if the operand is lower or equal to 0.") },
			comment = "the centre of the cylinder is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "cylinder(10,10)",
				equals = "a geometry as a circle of radius 10.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle" })
		public static
			IShape cylinder(final IScope scope, final Double radius, final Double depth) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCylinder(radius, depth, location);
		}

		@operator(value = "sphere", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A sphere geometry which radius is equal to the operand.",
			usages = { @usage(value = "returns a point if the operand is lower or equal to 0.") },
			comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "sphere(10)",
				equals = "a geometry as a circle of radius 10 but displays a sphere.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle" })
		public static
			IShape sphere(final IScope scope, final Double radius) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSphere(radius, location);
		}

		@operator(value = "antislice", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A sphere geometry which radius is equal to the operand made of 2 hemispheres.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the sphere is by default the location of the current agent that is calling this operator.",
			examples = { @example(value = "antislice(10,0.3)",
				equals = "a circle geometry of radius 10, displayed as an antislice.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle", "hemisphere", "pie3D" })
		public static
			IShape hemispherePac(final IScope scope, final Double radius, final Double ratio) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildAntiSlice(radius, location, ratio);
		}

		@operator(value = "slice", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An sphere geometry which radius is equal to the operand made of 2 hemisphere.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "slice(10,0.3)",
				equals = "a circle geometry of radius 10, displayed as a slice.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle", "hemisphere", "pie3D" })
		public static
			IShape hemisphereMan(final IScope scope, final Double radius, final Double ratio) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSlice(radius, location, ratio);
		}

		@operator(value = "spherical_pie", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An sphere geometry which radius is equal to the operand made of n pie.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "spherical_pie(10,[1.0,1.0,1.0])",
				equals = "a circle geometry of radius 10, displayed as a sphere with 4 slices.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle", "hemisphere", "pie3D" })
		public static
			IShape pieSphere(final IScope scope, final Double radius, final IList<Double> ratio) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }

			Double sum = 0.0;
			for ( Object curR : ratio ) {
				sum = sum + Cast.asFloat(scope, curR);
			}
			for ( int i = 0; i < ratio.size(); i++ ) {
				ratio.set(i, Cast.asFloat(scope, ratio.get(i)) / sum);
			}

			return GamaGeometryType.buildPieSphere(radius, location, ratio);
		}

		@operator(value = "spherical_pie", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An sphere geometry which radius is equal to the operand made of n pie.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "spherical_pie(10/2,[0.1,0.9],[#red,#green])",
				equals = "a circle geometry of radius 10, displayed as a sphere with 2 slices.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle", "hemisphere", "pie3D" })
		public static
			IShape pieSphere(final IScope scope, final Double radius, final IList<Double> ratio,
				final IList<GamaColor> colors) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }

			Double sum = 0.0;
			for ( Object curR : ratio ) {
				sum = sum + Cast.asFloat(scope, curR);
			}
			for ( int i = 0; i < ratio.size(); i++ ) {
				ratio.set(i, Cast.asFloat(scope, ratio.get(i)) / sum);
			}
			if ( ratio.size() > colors.size() ) {

			throw GamaRuntimeException.warning(
				"The number of value is greater of the number  of color whereas it should be equal.", scope); }

			if ( ratio.size() < colors.size() ) { throw GamaRuntimeException.warning(
				"The number of color is greater of the number  of value whereas it should be equal.", scope); }

			return GamaGeometryType.buildPieSphereWithDynamicColor(radius, location, ratio, colors);
		}

		@operator(value = "pacman", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An pacman geometry which radius is equal to first argument.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "pacman(1)",
				equals = "a geometry as a circle of radius 10 but displays a sphere.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle", "hemisphere", "pie3D" })
		public static
			IShape pacMan(final IScope scope, final Double radius) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildPacMan(radius, location, 0.2);
		}

		@operator(value = "pacman", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An pacman geometry with a dynamic opening mouth which radius is equal to first argument.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "pacman(1,0.2)",
				equals = "a geometry as a circle of radius 10 but displays a sphere.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle", "hemisphere", "pie3D" })
		public static
			IShape pacMan(final IScope scope, final Double radius, final Double ratio) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildPacMan(radius, location, ratio);
		}

		@operator(value = "hemisphere", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "An hemisphere geometry which radius is equal to the operand.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the hemisphere is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "hemisphere(10,0.5)",
				equals = "a geometry as a circle of radius 10 but displays an hemisphere.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle", "hemisphere", "pie3D" })
		public static
			IShape hemisphere(final IScope scope, final Double radius, final Double ratio) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildHemiSphere(radius, location, ratio);
		}

		@operator(value = "cone3D", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A cone geometry which radius is equal to the operand.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the cone is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "cone3D(10.0,10.0)",
				equals = "a geometry as a circle of radius 10 but displays a cone.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle" })
		public static
			IShape cone3D(final IScope scope, final Double radius, final Double height) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCone3D(radius, height, location);
		}

		@operator(value = "teapot", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A teapot geometry which radius is equal to the operand.",
			special_cases = { "returns a point if the operand is lower or equal to 0." },
			comment = "the centre of the teapot is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "teapot(10)",
				equals = "a geometry as a circle of radius 10 but displays a teapot.",
				test = false) }, see = { "around", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle" })
		public static
			IShape teapot(final IScope scope, final Double size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildTeapot(size, location);
		}

		@operator(value = "cone", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A cone geometry which min and max angles are given by the operands.",
			special_cases = { "returns nil if the operand is nil." },
			comment = "the centre of the cone is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "cone(0, 45)",
				equals = "a geometry as a cone with min angle is 0 and max angle is 45.",
				test = false) }, see = { "around", "circle", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle" })
		public static
			IShape cone(final IScope scope, final Integer p1, final Integer p2) {
			if ( p1 == null || p2 == null ) { return null; }
			final Integer min_angle = Maths.checkHeading(p1);
			final Integer max_angle = Maths.checkHeading(p2);
			final IAgent a = scope.getAgentScope();
			final ILocation origin = a.getLocation() == null ? new GamaPoint(0, 0) : a.getLocation();
			final double originx = origin.getX();
			final double originy = origin.getY();
			final double worldWidth = scope.getTopology().getWidth();// - originx;
			final double worldHeight = scope.getTopology().getHeight();// - originy;
			final double max = Math.max(worldWidth, worldHeight);
			final double min_point_x = originx + Maths.cos(min_angle) * max;
			final double min_point_y = originy + Maths.sin(min_angle) * max;
			final ILocation minPoint = new GamaPoint(min_point_x, min_point_y);

			final double max_point_x = originx + Maths.cos(max_angle) * max;
			final double max_point_y = originy + Maths.sin(max_angle) * max;
			final ILocation maxPoint = new GamaPoint(max_point_x, max_point_y);

			return polygon(scope, GamaListFactory.createWithoutCasting(Types.POINT, origin, minPoint, maxPoint));

		}

		@operator(value = "cone", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A cone geometry which min and max angles are given by the operands.",
			special_cases = { "returns nil if the operand is nil." },
			comment = "the centre of the cone is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "cone({0, 45})",
				equals = "a geometry as a cone with min angle is 0 and max angle is 45.",
				test = false) }, see = { "around", "circle", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle" })
		public static
			IShape cone(final IScope scope, final GamaPoint p) {
			if ( p == null ) { return null; }
			return cone(scope, (int) p.x, (int) p.y);
		}

		@operator(value = "square", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A square geometry which side size is equal to the operand.",
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the square is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "square(10)",
				equals = "a geometry as a square of side size 10.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "rectangle", "triangle" })
		public static
			IShape square(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSquare(side_size, location);
		}

		@operator(value = "cube", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A cube geometry which side size is equal to the operand.",
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the cube is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "cube(10)", equals = "a geometry as a square of side size 10.", test = false) },
			see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
				"triangle" })
		public static
			IShape cube(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCube(side_size, location);
		}

		@operator(value = { "rgb_cube" }, category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A cube geometry which side size is equal to the operand.",
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the cube is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "cube(10)", equals = "a geometry as a square of side size 10.", test = false) },
			see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle",
				"triangle" })
		public static
			IShape rgbcube(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildRGBCube(side_size, location);
		}

		@operator(value = "rectangle", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A rectangle geometry which side sizes are given by the operands.",
			masterDoc = true,
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the rectangle is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "rectangle({10, 5})",
				equals = "a geometry as a rectangle with width = 10 and heigh = 5.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "square", "triangle" })
		public static
			IShape rectangle(final IScope scope, final GamaPoint p) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(p.x, p.y, location);
		}

		@operator(value = "rectangle", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A rectangle geometry which side sizes are given by the operands.",
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the rectangle is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "rectangle(10, 5)",
				equals = "a geometry as a rectangle with width = 10 and heigh = 5.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "square", "triangle" })
		public static
			IShape rectangle(final IScope scope, final double x, final double y) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(x, y, location);
		}

		@operator(value = "box", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A box geometry which side sizes are given by the operands.",
			masterDoc = true,
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the box is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "box({10, 5 , 5})",
				equals = "a geometry as a rectangle with width = 10, heigh = 5 depth= 5.",
				test = false) }, see = { "around", "circle", "sphere", "cone", "line", "link", "norm", "point",
				"polygon", "polyline", "square", "cube", "triangle" })
		public static
			IShape box(final IScope scope, final GamaPoint p) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildBox(p.x, p.y, p.z, location);
		}

		@operator(value = "box", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A box geometry which side sizes are given by the operands.",
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the box is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "box(10, 5 , 5)",
				equals = "a geometry as a rectangle with width = 10, heigh = 5 depth= 5.",
				test = false) }, see = { "around", "circle", "sphere", "cone", "line", "link", "norm", "point",
				"polygon", "polyline", "square", "cube", "triangle" })
		public static
			IShape box(final IScope scope, final double x, final double y, final double z) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildBox(x, y, z, location);
		}

		@operator(value = "triangle", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A triangle geometry which side size is given by the operand.",
			usages = { @usage("returns nil if the operand is nil.") },
			comment = "the centre of the triangle is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "triangle(5)",
				equals = "a geometry as a triangle with side_size = 5.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "rectangle", "square" })
		public static
			IShape triangle(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildTriangle(side_size, location);
		}

		@operator(value = "rgb_triangle", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A triangle geometry which side size is given by the operand.",
			usages = { @usage("returns nil if the operand is nil.") },
			comment = "the centre of the triangle is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "triangle(5)",
				equals = "a geometry as a triangle with side_size = 5.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "rectangle", "square" })
		public static
			IShape rgbtriangle(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildRGBTriangle(side_size, location);
		}

		@operator(value = "pyramid", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A square geometry which side size is given by the operand.",
			usages = { @usage(value = "returns nil if the operand is nil.") },
			comment = "the centre of the pyramid is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "pyramid(5)",
				equals = "a geometry as a square with side_size = 5.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "rectangle", "square" })
		public static
			IShape pyramid(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildPyramid(side_size, location);
		}

		@operator(value = "hexagon", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A hexagon geometry which the given with and height",
			masterDoc = true,
			usages = { @usage("returns nil if the operand is nil.") },
			comment = "the centre of the hexagon is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "hexagon(10)",
				equals = "a geometry as a hexagon of width of 10 and height of 10.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "rectangle", "triangle" })
		public static
			IShape hexagon(final IScope scope, final Double size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildHexagon(size, location);
		}

		@operator(value = "hexagon", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE,
			IOperatorCategory.THREED })
		@doc(value = "A hexagon geometry which the given with and height",
			usages = { @usage("returns nil if the operand is nil.") },
			comment = "the centre of the hexagon is by default the location of the current agent in which has been called this operator.",
			examples = { @example(value = "hexagon({10,5})",
				equals = "a geometry as a hexagon of width of 10 and height of 5.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polygon",
				"polyline", "rectangle", "triangle" })
		public static
			IShape hexagon(final IScope scope, final GamaPoint size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			final Double width = size.x;
			final Double height = size.y;
			if ( width <= 0 || height <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildHexagon(width, height, location);
		}

		@operator(value = "polygon", expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT }, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A polygon geometry from the given list of points.",
			usages = { @usage(value = "if the operand is nil, returns the point geometry {0,0}"),
				@usage(value = "if the operand is composed of a single point, returns a point geometry"),
				@usage(value = "if the operand is composed of 2 points, returns a polyline geometry.") },
			examples = { @example(value = "polygon([{0,0}, {0,10}, {10,10}, {10,0}])",
				equals = "a polygon geometry composed of the 4 points.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polyline",
				"rectangle", "square", "triangle" })
		public static IShape polygon(final IScope scope, final IContainer<?, ? extends IShape> points) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			// final IList<IShape> shapes = points.listValue(scope); Now replaced by a copy of the list (see Issue 740)
			final IList<IShape> shapes = GamaListFactory.create(scope, Types.GEOMETRY, points);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			if ( size == 2 ) { return GamaGeometryType.buildLine(first, shapes.lastValue(scope)); }
			if ( !first.equals(shapes.lastValue(scope)) ) {
				shapes.add(first);
			}
			return GamaGeometryType.buildPolygon(shapes);
		}

		@operator(value = "polyhedron",
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A polyhedron geometry from the given list of points.",
			usages = { @usage(value = "if the operand is nil, returns the point geometry {0,0}"),
				@usage(value = "" + "if the operand is composed of a single point, returns a point geometry"),
				@usage(value = "if the operand is composed of 2 points, returns a polyline geometry.") },
			examples = { @example(value = "polyhedron([{0,0}, {0,10}, {10,10}, {10,0}],10)",
				equals = "a polygon geometry composed of the 4 points and of depth 10.",
				test = false) }, see = { "around", "circle", "cone", "line", "link", "norm", "point", "polyline",
				"rectangle", "square", "triangle" })
		public static IShape polyhedron(final IScope scope, final IContainer<?, IShape> points, final Double depth) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			// final IList<IShape> shapes = points.listValue(scope); Now replaced by a copy of the list (see Issue 740)
			final IList<IShape> shapes = GamaListFactory.create(scope, Types.POINT, points);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			final IShape last = shapes.lastValue(scope);
			if ( size == 2 ) { return GamaGeometryType.buildLine(first, last); }
			if ( !first.equals(last) ) {
				shapes.add(first);
			}
			return GamaGeometryType.buildPolyhedron(shapes, depth);
		}

		@operator(value = { "curve" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A quadratic Bezier curve geometry built from the three given points composed of 10 points.",
			usages = { @usage(value = "if the operand is nil, returns nil") },
			examples = { @example(value = "curve({0,0}, {0,10}, {10,10})",
				equals = "a quadratic Bezier curve geometry composed of 10 points from p0 to p2.",
				test = false) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle",
				"square", "triangle", "line" })
		public static IShape
			BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1, final GamaPoint P2) {
			if ( P0 == null || P1 == null || P2 == null ) { return null; }
			return GamaGeometryType.buildPolyline(quadraticBezierCurve(P0, P1, P2, 10));
		}

		@operator(value = { "curve" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A quadratic Bezier curve geometry built from the three given points composed of a given numnber of points.",
			usages = { @usage(value = "if the operand is nil, returns nil"),
				@usage(value = "if the last operand (number of points) is inferior to 2, returns nil") },
			examples = { @example(value = "curve({0,0}, {0,10}, {10,10}, 20)",
				equals = "a quadratic Bezier curve geometry composed of 20 points from p0 to p2.",
				test = false) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle",
				"square", "triangle", "line" })
		public static
			IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
				final int nbPoints) {
			if ( P0 == null || P1 == null || P2 == null || nbPoints < 2 ) { return null; }
			return GamaGeometryType.buildPolyline(quadraticBezierCurve(P0, P1, P2, nbPoints));
		}

		@operator(value = { "curve" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A cubic Bezier curve geometry built from the four given points composed of 10 points.",
			usages = { @usage(value = "if the operand is nil, returns nil") },
			examples = { @example(value = "curve({0,0}, {0,10}, {10,10})",
				equals = "a cubic Bezier curve geometry composed of 10 points from p0 to p3.",
				test = false) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle",
				"square", "triangle", "line" })
		public static IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1,
			final GamaPoint P2, final GamaPoint P3) {
			if ( P0 == null || P1 == null || P2 == null || P3 == null ) { return null; }
			return GamaGeometryType.buildPolyline(cubicBezierCurve(P0, P1, P2, P3, 10));
		}

		@operator(value = { "curve" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A cubic Bezier curve geometry built from the four given points composed of a given number of points.",
			usages = { @usage(value = "if the operand is nil, returns nil"),
				@usage(value = "if the last operand (number of points) is inferior to 2, returns nil") },
			examples = { @example(value = "curve({0,0}, {0,10}, {10,10})",
				equals = "a cubic Bezier curve geometry composed of 10 points from p0 to p3.",
				test = false) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle",
				"square", "triangle", "line" })
		public static
			IShape BezierCurve(final IScope scope, final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
				final GamaPoint P3, final int nbPoints) {
			if ( P0 == null || P1 == null || P2 == null || P3 == null || nbPoints < 2 ) { return null; }
			return GamaGeometryType.buildPolyline(cubicBezierCurve(P0, P1, P2, P3, nbPoints));
		}

		private static List<IShape> quadraticBezierCurve(final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
			final int nbPoints) {
			final List<IShape> points = new ArrayList<IShape>();
			for ( int i = 0; i < nbPoints; i++ ) {
				double x = quadraticBezier(P0.x, P1.x, P2.x, (double) i / (nbPoints - 1));
				double y = quadraticBezier(P0.y, P1.y, P2.y, (double) i / (nbPoints - 1));
				double z = quadraticBezier(P0.z, P1.z, P2.z, (double) i / (nbPoints - 1));
				points.add(new GamaPoint(x, y, z));
			}
			return points;
		}

		private static List<IShape> cubicBezierCurve(final GamaPoint P0, final GamaPoint P1, final GamaPoint P2,
			final GamaPoint P3, final int nbPoints) {
			final List<IShape> points = new ArrayList<IShape>();
			for ( int i = 0; i < nbPoints; i++ ) {
				double x = cubicBezier(P0.x, P1.x, P2.x, P3.x, (double) i / (nbPoints - 1));
				double y = cubicBezier(P0.y, P1.y, P2.y, P3.y, (double) i / (nbPoints - 1));
				double z = cubicBezier(P0.z, P1.z, P2.z, P3.z, (double) i / (nbPoints - 1));
				points.add(new GamaPoint(x, y, z));
			}
			return points;
		}

		private static double quadraticBezier(final double v0, final double v1, final double v2, final double t) {
			return (1 - t) * ((1 - t) * v0 + t * v1) + t * ((1 - t) * v1 + t * v2);
		}

		private static double cubicBezier(final double v0, final double v1, final double v2, final double v3,
			final double t) {
			return Math.pow(1 - t, 3) * v0 + 3 * (1 - t) * (1 - t) * t * v1 + 3 * (1 - t) * t * t * v2 +
				Math.pow(t, 3) * v3;
		}

		@operator(value = { "line", "polyline" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A polyline geometry from the given list of points.",
			usages = { @usage(value = "if the operand is nil, returns the point geometry {0,0}"),
				@usage(value = "if the operand is composed of a single point, returns a point geometry.") },
			examples = { @example(value = "polyline([{0,0}, {0,10}, {10,10}, {10,0}])",
				equals = "a polyline geometry composed of the 4 points.",
				test = false) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle",
				"square", "triangle" })
		public static IShape line(final IScope scope, final IContainer<?, IShape> points) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = points.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			if ( size == 2 ) { return GamaGeometryType.buildLine(first, points.lastValue(scope)); }
			return GamaGeometryType.buildPolyline(shapes);
		}

		@operator(value = { "geometry_collection" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A geometry collection (multi-geometry) composed of the given list of geometries.",
			usages = { @usage(value = "if the operand is nil, returns the point geometry {0,0}"),
				@usage(value = "if the operand is composed of a single geometry, returns a copy of the geometry.") },
			examples = { @example(value = "geometry_collection([{0,0}, {0,10}, {10,10}, {10,0}])",
				equals = "a geometry composed of the 4 points (multi-point).",
				test = false) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle",
				"square", "triangle", "line" })
		public static IShape geometryCollection(final IScope scope, final IContainer<?, IShape> geometries) {
			if ( geometries == null || geometries.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = geometries.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if ( size == 1 ) { return first.copy(scope); }

			return GamaGeometryType.buildMultiGeometry(shapes);
		}

		@operator(value = { "line", "polyline" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A polyline geometry from the given list of points represented as a cylinder of radius r.",
			usages = {
				@usage(value = "if the operand is nil, returns the point geometry {0,0}"),
				@usage(value = "if the operand is composed of a single point, returns a point geometry."),
				@usage(value = "if a radius is added, the given list of points represented as a cylinder of radius r",
					examples = { @example(value = "polyline([{0,0}, {0,10}, {10,10}, {10,0}],0.2)",
						equals = "a polyline geometry composed of the 4 points.",
						test = false) }) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone",
				"rectangle", "square", "triangle" })
		public static IShape line(final IScope scope, final IContainer<?, IShape> points, final double radius) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = points.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			if ( size == 2 ) { return GamaGeometryType.buildLineCylinder(first, points.lastValue(scope), radius); }
			return GamaGeometryType.buildPolylineCylinder(shapes, radius);
		}

		@operator(value = { "plan", "polyplan" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A polyline geometry from the given list of points.",
			usages = { @usage(value = "if the operand is nil, returns the point geometry {0,0}"),
				@usage(value = "if the operand is composed of a single point, returns a point geometry.") },
			examples = { @example(value = "polyplan([{0,0}, {0,10}, {10,10}, {10,0}],10)",
				equals = "a polyline geometry composed of the 4 points with a depth of 10.",
				test = false) }, see = { "around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle",
				"square", "triangle" })
		public static IShape plan(final IScope scope, final IContainer<?, IShape> points, final Double depth) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = points.listValue(scope, Types.NO_TYPE, false);
			final int size = shapes.length(scope);
			final IShape first = shapes.firstValue(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			if ( size == 2 ) { return GamaGeometryType.buildPlan(first, shapes.lastValue(scope), depth); }
			return GamaGeometryType.buildPolyplan(shapes, depth);
		}

		@operator(value = { "link" }, category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A link between the 2 elements of the pair.",
			usages = {
				@usage("if the operand is nil, link returns a point {0,0}"),
				@usage("if one of the elements of the pair is a list of geometries or a species, link will consider the union of the geometries or of the geometry of each agent of the species") },
			comment = "The geometry of the link is the intersection of the two geometries when they intersect, and a line between their centroids when they do not.",
			examples = { @example(value = "link (geom1::geom2)",
				equals = "a link geometry between geom1 and geom2.",
				isExecutable = false) }, see = { "around", "circle", "cone", "line", "norm", "point", "polygon",
				"polyline", "rectangle", "square", "triangle" })
		public static
			IShape link(final IScope scope, final GamaPair points) throws GamaRuntimeException {
			if ( points == null || points.first() == null && points.last() == null ) { return new GamaShape(
				new GamaPoint(0, 0)); }
			return GamaGeometryType.pairToGeometry(scope, points);
		}

		@operator(value = "around", category = { IOperatorCategory.SPATIAL })
		@doc(value = "A geometry resulting from the difference between a buffer around the right-operand casted in geometry at a distance left-operand (right-operand buffer left-operand) and the right-operand casted as geometry.",
			usages = { @usage(value = "returns a circle geometry of radius right-operand if the left-operand is nil") },
			examples = { @example(value = "10 around circle(5)",
				equals = "the ring geometry between 5 and 10.",
				test = false) }, see = { "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline",
				"rectangle", "square", "triangle" })
		public static
			IShape around(final IScope scope, final Double width, final Object toBeCastedIntoGeometry)
				throws GamaRuntimeException {
			final IShape g = Cast.asGeometry(scope, toBeCastedIntoGeometry, false);
			if ( g == null ) { return circle(scope, width); }
			return Operators.minus(scope, Transformations.enlarged_by(scope, g, width), g);
		}

		/*
		 * @operator("envelope")
		 * 
		 * @doc(value =
		 * "A rectangular 3D geometry that represents the rectangle that surrounds the geometries or the surface described by the arguments. More general than geometry(arguments).envelope, as it allows to pass int, double, point, image files, shape files, asc files, or any list combining these arguments, in which case the envelope will be correctly expanded. If an envelope cannot be determined from the arguments, a default one of dimensions (0,100, 0, 100, 0, 100) is returned"
		 * )
		 * public static IShape envelope(final IScope scope, final Object obj) {
		 * Envelope3D env = new Envelope3D(GeometryUtils.computeEnvelopeFrom(scope, obj));
		 * if ( env.isNull() ) {
		 * env = new Envelope3D(0, 100, 0, 100, 0, 100);
		 * }
		 * final IShape shape =
		 * GamaGeometryType.buildBox(env.getWidth(), env.getHeight(), env.getDepth(), new GamaPoint(env.centre()));
		 * return shape;
		 * }
		 */

		@operator(value = "envelope", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
		@doc(value = "A 3D geometry that represents the box that surrounds the geometries or the surface described by the arguments. More general than geometry(arguments).envelope, as it allows to pass int, double, point, image files, shape files, asc files, or any list combining these arguments, in which case the envelope will be correctly expanded. If an envelope cannot be determined from the arguments, a default one of dimensions (0,100, 0, 100, 0, 100) is returned")
		public static
			IShape envelope(final IScope scope, final Object obj) {
			Envelope3D env = new Envelope3D(GeometryUtils.computeEnvelopeFrom(scope, obj));
			if ( env.isNull() ) {
				env = new Envelope3D(0, 100, 0, 100, 0, 100);
			}
			final IShape shape =
				GamaGeometryType.buildBox(env.getWidth(), env.getHeight(), env.getDepth(), new GamaPoint(
					(ILocation) env.centre()));
			return shape;
		}
	}

	public static abstract class Operators {

		@operator(value = { "inter", "intersection" }, category = { IOperatorCategory.SPATIAL })
		@doc(value = "A geometry resulting from the intersection between the two geometries",
		special_cases = { "returns nil if one of the operands is nil" },
		examples = { @example(value = "square(10) inter circle(5)", equals = "circle(5)") },
		see = { "union", "+", "-" })
		public static IShape inter(final IScope scope, final IShape g1, final IShape g2) {
			if ( g2 == null || g1 == null ) { return null; }
			if ( g2.isPoint() && g1.covers(g2.getLocation()) ) { return g2.copy(scope); }
			if ( g1.isPoint() && g2.covers(g1.getLocation()) ) { return g1.copy(scope); }
			Geometry geom = null;
			final Geometry geom1 = g1.getInnerGeometry();
			final Geometry geom2 = g2.getInnerGeometry();
			try {

				geom = geom1.intersection(geom2);
			} catch (final Exception ex) {
				try {
					final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
					geom =
						GeometryPrecisionReducer.reducePointwise(geom1, pm).intersection(
							GeometryPrecisionReducer.reducePointwise(geom2, pm));
				} catch (final Exception e) {
					// AD 12/04/13 : Addition of a third method in case of exception
					geom =
						geom1.buffer(0.01, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_FLAT)
						.intersection(
							geom2.buffer(0.01, BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
								BufferParameters.CAP_FLAT));
				}
			}
			if ( geom == null || geom.isEmpty() ) { return null; }
			// WARNING The attributes of the left-hand shape are kept, but not those of the right-hand shape
			return new GamaShape(g1, geom);
		}

		@operator(value = { "+", "union" }, category = { IOperatorCategory.SPATIAL })
		@doc(usages = @usage(value = "if the right-operand is a point, a geometry or an agent, returns the geometry resulting from the union between both geometries",
		examples = @example(value = "geom1 + geom2",
				equals = "a geometry corresponding to union between geom1 and geom2",
				isExecutable = false)))
		public static
		IShape union(final IScope scope, final IShape g1, final IShape g2) {
			if ( g1 == null ) {
				if ( g2 == null ) { return null; }
				return g2.copy(scope);
			}
			if ( g2 == null ) { return g1.copy(scope); }
			final Geometry geom1 = g1.getInnerGeometry();
			final Geometry geom2 = g2.getInnerGeometry();
			Geometry geom;
			try {
				geom = geom1.union(geom2);
			} catch (final Exception e) {
				e.printStackTrace();
				try {
					final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
					geom =
						GeometryPrecisionReducer.reducePointwise(geom1, pm).intersection(
							GeometryPrecisionReducer.reducePointwise(geom2, pm));
				} catch (final Exception e1) {
					// AD 12/04/13 : Addition of a third method in case of exception
					geom = geom1.buffer(0.01, 0, BufferOp.CAP_BUTT).union(geom2.buffer(0.01, 0, BufferOp.CAP_BUTT));
				}

			}
			if ( geom == null || geom.isEmpty() ) { return null; }
			return new GamaShape(g1, geom);
		}

		@operator(value = { "union" },
			expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT },
			category = { IOperatorCategory.SPATIAL })
		@doc(usages = { @usage(value = "if the right-operand is a container of points, geometries or agents, returns the geometry resulting from the union all the geometries") },
		examples = { @example(value = "union([geom1, geom2, geom3])",
				equals = "a geometry corresponding to union between geom1, geom2 and geom3",
				isExecutable = false) })
		public static
		IShape union(final IScope scope, final IContainer<?, IShape> elements) {
			try {
				return Cast.asGeometry(scope, elements, false);
			} catch (final GamaRuntimeException e) {
				return null;
			}
		}

		@operator(value = IKeyword.MINUS, category = { IOperatorCategory.SPATIAL })
		@doc(usages = @usage(value = "if both operands are a point, a geometry or an agent, returns the geometry resulting from the difference between both geometries",
		examples = @example(value = "geom1 - geom2",
				equals = "a geometry corresponding to difference between geom1 and geom2",
				isExecutable = false)))
		public static
		IShape minus(final IScope scope, final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null || g1.getInnerGeometry() == null || g2.getInnerGeometry() == null ) { return g1; }
			Geometry res = difference(g1.getInnerGeometry(), g2.getInnerGeometry());
			if ( res != null && !res.isEmpty() ) { return new GamaShape(g1, res); }
			return null;
		}

		@operator(value = IKeyword.MINUS, category = { IOperatorCategory.SPATIAL })
		@doc(usages = @usage(value = "if the right-operand is a list of points, geometries or agents, returns the geometry resulting from the difference between the left-geometry and all of the right-geometries",
		examples = @example(value = "rectangle(10,10) - [circle(2), square(2)]",
				equals = "rectangle(10,10) - (circle(2) + square(2))")))
		public static
		IShape minus(final IScope scope, final IShape g1, final IContainer<?, IShape> agents) {
			if ( g1 == null || agents == null || g1.getInnerGeometry() == null || agents.isEmpty(scope) ) { return g1; }
			Geometry geom1 = GeometryUtils.FACTORY.createGeometry(g1.getInnerGeometry());
			for ( final IShape ag : agents.iterable(scope) ) {
				if ( ag != null && ag.getInnerGeometry() != null ) {
					geom1 = difference(geom1, ag.getInnerGeometry());
					if ( geom1 == null || geom1.isEmpty() ) { return null; }
				}
			}
			if ( geom1 == null || geom1.isEmpty() ) { return null; }
			return new GamaShape(g1, geom1);
		}

		private static Geometry difference(Geometry g1, final Geometry g2) {
			if ( g2 instanceof GeometryCollection ) {
				GeometryCollection g2c = (GeometryCollection) g2;
				int nb = g2c.getNumGeometries();
				for ( int i = 0; i < nb; i++ ) {
					g1 = difference(g1, g2c.getGeometryN(i));
					if ( g1 == null || g1.isEmpty() ) { return null; }
				}
				return g1;
			}
			try {
				return g1.difference(g2);
			} catch (AssertionFailedException e) {
				try {
					final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
					return GeometryPrecisionReducer.reducePointwise(g1, pm).difference(
						GeometryPrecisionReducer.reducePointwise(g2, pm));
				} catch (final Exception e1) {
					return g1.difference(g2.buffer(0.01, 0, BufferOp.CAP_BUTT));
				}
			}
		}

		@operator(value = { "add_point" }, category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT })
		@doc(value = "A geometry resulting from the addition of the right point (coordinate) to the left-hand geometry",
		examples = { @example(value = "polygon([{10,10},{10,20},{20,20}]) add_point {20,10}",
				returnType = "geometry",
				equals = "polygon([{10,10},{10,20},{20,20},{20,10}])") })
		public static
		IShape add_point(final IScope scope, final IShape g, final ILocation p) {
			if ( p == null ) { return g; }
			final Coordinate point = (Coordinate) p;
			final Geometry geometry = g.getInnerGeometry();
			Geometry geom_Tmp = null;
			if ( geometry instanceof Point ) {
				Coordinate[] coord = new Coordinate[2];
				coord[0] = geometry.getCoordinate();
				coord[1] = point;
				geom_Tmp = GeometryUtils.FACTORY.createLineString(coord);
			} else if ( geometry instanceof MultiPoint ) {
				Coordinate[] coordinates = new Coordinate[geometry.getNumPoints() + 1];
				coordinates[coordinates.length - 1] = p.toCoordinate();
				geom_Tmp = GeometryUtils.FACTORY.createMultiPoint(coordinates);
			} else if ( geometry instanceof LineString ) {
				geom_Tmp = createLineStringWithPoint(geometry, point);
			} else if ( geometry instanceof MultiLineString ) {
				Geometry closestGeom = null;
				double distMin = Double.MAX_VALUE;
				int id = -1;
				Point pt = GeometryUtils.FACTORY.createPoint(point);
				for ( int i = 0; i < geometry.getNumGeometries(); i++ ) {
					Geometry geom = geometry.getGeometryN(i);
					double dist = geom.distance(pt);
					if ( dist < distMin ) {
						distMin = dist;
						closestGeom = geom;
						id = i;
					}
				}
				LineString[] lineStrings = new LineString[geometry.getNumGeometries()];
				for ( int i = 0; i < geometry.getNumGeometries(); i++ ) {
					if ( i != id ) {
						lineStrings[i] = (LineString) geometry.getGeometryN(i);
					} else {
						lineStrings[i] = (LineString) createLineStringWithPoint(closestGeom, point);
					}
				}
				geom_Tmp = GeometryUtils.FACTORY.createMultiLineString(lineStrings);
			} else if ( geometry instanceof Polygon ) {
				geom_Tmp = createPolygonWithPoint(geometry, point);
			} else if ( geometry instanceof MultiPolygon ) {
				Geometry closestGeom = null;
				double distMin = Double.MAX_VALUE;
				int id = -1;
				Point pt = GeometryUtils.FACTORY.createPoint(point);
				for ( int i = 0; i < geometry.getNumGeometries(); i++ ) {
					Geometry geom = geometry.getGeometryN(i);
					double dist = geom.distance(pt);
					if ( dist < distMin ) {
						distMin = dist;
						closestGeom = geom;
						id = i;
					}
				}
				Polygon[] polygons = new Polygon[geometry.getNumGeometries()];
				for ( int i = 0; i < geometry.getNumGeometries(); i++ ) {
					if ( i != id ) {
						polygons[i] = (Polygon) geometry.getGeometryN(i);
					} else {
						polygons[i] = (Polygon) createPolygonWithPoint(closestGeom, point);
					}
				}
				geom_Tmp = GeometryUtils.FACTORY.createMultiPolygon(polygons);
			}
			if ( geom_Tmp != null ) { return new GamaShape(g, geom_Tmp);

			}
			return g;
		}

		private static Geometry createPolygonWithPoint(final Geometry geometry, final Coordinate point) {
			int index = indexClosestSegment(((Polygon) geometry).getExteriorRing(), point);
			if ( index == -1 ) { return null; }
			Coordinate[] coord = new Coordinate[geometry.getCoordinates().length + 1];
			for ( int i = 0; i < index + 1; i++ ) {
				coord[i] = geometry.getCoordinates()[i];
			}
			coord[index + 1] = point;
			for ( int i = index + 2; i < coord.length; i++ ) {
				coord[i] = geometry.getCoordinates()[i - 1];
			}
			LinearRing[] lrs = new LinearRing[((Polygon) geometry).getNumInteriorRing()];
			for ( int i = 0; i < lrs.length; i++ ) {
				lrs[i] = (LinearRing) ((Polygon) geometry).getInteriorRingN(i);
			}
			return GeometryUtils.FACTORY.createPolygon(GeometryUtils.FACTORY.createLinearRing(coord), lrs);
		}

		private static Geometry createLineStringWithPoint(final Geometry geometry, final Coordinate point) {
			int index = indexClosestSegment(geometry, point);
			if ( index == -1 ) { return null; }
			Coordinate[] coord = new Coordinate[geometry.getCoordinates().length + 1];
			for ( int i = 0; i < index + 1; i++ ) {
				coord[i] = geometry.getCoordinates()[i];
			}
			coord[index + 1] = point;
			for ( int i = index + 2; i < coord.length; i++ ) {
				coord[i] = geometry.getCoordinates()[i - 1];
			}
			return GeometryUtils.FACTORY.createLineString(coord);
		}

		private static int indexClosestSegment(final Geometry geom, final Coordinate coord) {
			int index = -1;
			Point pt = GeometryUtils.FACTORY.createPoint(coord);
			double distMin = Double.MAX_VALUE;
			for ( int i = 0; i < geom.getCoordinates().length - 1; i++ ) {
				Coordinate cc = geom.getCoordinates()[i];
				if ( cc.equals(coord) ) { return -1; }
				Coordinate[] coordinates = new Coordinate[2];
				coordinates[0] = cc;
				coordinates[1] = geom.getCoordinates()[i + 1];
				Geometry geom_Tmp = GeometryUtils.FACTORY.createLineString(coordinates);
				double dist = geom_Tmp.distance(pt);
				if ( dist < distMin ) {
					distMin = dist;
					index = i;
				}
			}
			if ( geom.getCoordinates()[geom.getCoordinates().length - 1].equals(coord) ) { return -1; }
			return index;
		}

		@operator(value = "masked_by", category = { IOperatorCategory.SPATIAL })
		@doc(examples = { @example(value = "perception_geom masked_by obstacle_list",
		equals = "the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list.",
		isExecutable = false) })
		public static
		IShape masked_by(final IScope scope, final IShape source, final IContainer<?, IShape> obstacles,
			Integer precision) {
			final IAgent a = scope.getAgentScope();
			List<IShape> obst =
				obstacles == null ? new ArrayList<IShape>() : obstacles.listValue(scope, Types.GEOMETRY, false);
			final ILocation location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( precision == null ) {
				precision = 120;
			}
			final Geometry visiblePercept = GeometryUtils.FACTORY.createGeometry(source.getInnerGeometry());
			boolean isPoint = source.isPoint();
			if ( obstacles != null && !obstacles.isEmpty(scope) ) {
				final Geometry pt = GeometryUtils.FACTORY.createPoint(location.toCoordinate());
				final Geometry locG = pt.buffer(0.01).getEnvelope();
				double percep_dist = 0;
				for ( ILocation p : source.getPoints() ) {
					double dist = location.euclidianDistanceTo(p);
					if ( dist > percep_dist ) {
						percep_dist = dist;
					}
				}
				final Geometry gbuff = pt.buffer(percep_dist, precision / 4);
				final List<IShape> geoms = new ArrayList<IShape>();
				for ( int k = 1; k < gbuff.getNumPoints(); k++ ) {

					final Coordinate[] coordinates = new Coordinate[4];
					coordinates[0] = location.toCoordinate();
					coordinates[1] = gbuff.getCoordinates()[k - 1];
					coordinates[2] = gbuff.getCoordinates()[k];
					coordinates[3] = location.toCoordinate();
					final LinearRing closeRing = GeometryUtils.FACTORY.createLinearRing(coordinates);
					Geometry gg =
						source.getGeometry().getInnerGeometry()
							.intersection(GeometryUtils.FACTORY.createPolygon(closeRing, null));
					if ( isPoint || !(gg instanceof Point) ) {
						geoms.add(new GamaShape(gg));
					}
				}
				final GamaList geomsVisible = (GamaList) GamaListFactory.create();
				final PreparedGeometry ref = PreparedGeometryFactory.prepare(locG);

				for ( final IShape geom : geoms ) {
					if ( !intersection(geom, obst) ) {
						geomsVisible.addValue(scope, geom);
					} else {
						IShape perceptReal = difference(geom, obst, ref);

						if ( perceptReal != null && (isPoint || !perceptReal.isPoint()) ) {
							geomsVisible.addValue(scope, perceptReal);
						}
					}
				}
				if ( !geomsVisible.isEmpty(scope) ) {
					IShape result = Cast.asGeometry(scope, geomsVisible, false);
					if ( result.getInnerGeometry() instanceof GeometryCollection ) {
						result = Spatial.Transformations.enlarged_by(scope, result, 0.1);
					}
					return result;
				}
				return null;
			}
			return new GamaShape(visiblePercept);
		}

		private static boolean intersection(final IShape geom, final List<IShape> geoms) {
			if ( geom == null ) { return false; }
			for ( IShape g : geoms ) {
				if ( g != null && geom.intersects(g) ) { return true; }
			}
			return false;
		}

		private static IShape difference(final IShape geom, final List<IShape> geoms, final PreparedGeometry ref) {
			if ( geom == null ) { return null; }
			Geometry gR = geom.getInnerGeometry();
			for ( IShape g : geoms ) {

				if ( g != null && geom.intersects(g) ) {
					try {
						gR = gR.difference(g.getInnerGeometry());

					} catch (TopologyException e) {
						IShape gp = new GamaShape(g, null, 1.0, null);
						gR = gR.difference(gp.getInnerGeometry());
					}
					if ( gR instanceof GeometryCollection ) {
						final GeometryCollection gc = (GeometryCollection) gR;
						gR = null;
						final int nb = gc.getNumGeometries();
						for ( int i1 = 0; i1 < nb; i1++ ) {
							if ( !ref.disjoint(gc.getGeometryN(i1)) ) {
								gR = gc.getGeometryN(i1);
								break;
							}
						}
					} else if ( ref.disjoint(gR) ) { return null; }
				}
			}
			if ( gR != null ) { return new GamaShape(gR); }
			return null;
		}

		@operator(value = "masked_by", category = { IOperatorCategory.SPATIAL })
		@doc(examples = { @example(value = "perception_geom masked_by obstacle_list",
		equals = "the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list.",
		isExecutable = false) })
		public static
		IShape masked_by(final IScope scope, final IShape source, final IContainer<?, IShape> obstacles) {
			return masked_by(scope, source, obstacles, null);
		}

		@operator(value = "split_at", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL })
		@doc(value = "The two part of the left-operand lines split at the given right-operand point",
		usages = { @usage("if the left-operand is a point or a polygon, returns an empty list") },
		examples = { @example(value = "polyline([{1,2},{4,6}]) split_at {7,6}",
				equals = "[polyline([{1.0,2.0},{7.0,6.0}]), polyline([{7.0,6.0},{4.0,6.0}])]") })
		public static IList<IShape> split_at(final IShape geom, final ILocation pt) {
			final IList<IShape> lines = GamaListFactory.create(Types.GEOMETRY);
			List<Geometry> geoms = null;
			if ( geom.getInnerGeometry() instanceof LineString ) {
				final Coordinate[] coords = ((LineString) geom.getInnerGeometry()).getCoordinates();
				final Point pt1 = GeometryUtils.FACTORY.createPoint(new GamaPoint(pt.getLocation()));
				final int nb = coords.length;
				int indexTarget = -1;
				double distanceT = Double.MAX_VALUE;
				for ( int i = 0; i < nb - 1; i++ ) {
					final Coordinate s = coords[i];
					final Coordinate t = coords[i + 1];
					final Coordinate[] seg = { s, t };
					final Geometry segment = GeometryUtils.FACTORY.createLineString(seg);
					final double distT = segment.distance(pt1);
					if ( distT < distanceT ) {
						distanceT = distT;
						indexTarget = i;
					}
				}
				int nbSp = indexTarget + 2;
				final Coordinate[] coords1 = new Coordinate[nbSp];
				for ( int i = 0; i <= indexTarget; i++ ) {
					coords1[i] = coords[i];
				}
				coords1[indexTarget + 1] = new GamaPoint(pt.getLocation());

				nbSp = coords.length - indexTarget;
				final Coordinate[] coords2 = new Coordinate[nbSp];
				coords2[0] = new GamaPoint(pt.getLocation());
				int k = 1;
				for ( int i = indexTarget + 1; i < coords.length; i++ ) {
					coords2[k] = coords[i];
					k++;
				}
				final List<Geometry> geoms1 = new ArrayList<Geometry>();
				geoms1.add(GeometryUtils.FACTORY.createLineString(coords1));
				geoms1.add(GeometryUtils.FACTORY.createLineString(coords2));
				geoms = geoms1;
			} else if ( geom.getInnerGeometry() instanceof MultiLineString ) {
				final Point point = GeometryUtils.FACTORY.createPoint((Coordinate) pt);
				final MultiLineString ml = (MultiLineString) geom.getInnerGeometry();
				Geometry geom2 = ml.getGeometryN(0);
				double distMin = geom2.distance(point);
				for ( int i = 1; i < ml.getNumGeometries(); i++ ) {
					Geometry gg = ml.getGeometryN(i);
					final double dist = gg.distance(point);
					if ( dist <= distMin ) {
						geom2 = gg;
						distMin = dist;
					}
				}
				final Coordinate[] coords = ((LineString) geom2).getCoordinates();
				final Point pt1 = GeometryUtils.FACTORY.createPoint(new GamaPoint(pt.getLocation()));
				final int nb = coords.length;
				int indexTarget = -1;
				double distanceT = Double.MAX_VALUE;
				for ( int i = 0; i < nb - 1; i++ ) {
					final Coordinate s = coords[i];
					final Coordinate t = coords[i + 1];
					final Coordinate[] seg = { s, t };
					final Geometry segment = GeometryUtils.FACTORY.createLineString(seg);
					final double distT = segment.distance(pt1);
					if ( distT < distanceT ) {
						distanceT = distT;
						indexTarget = i;
					}
				}
				int nbSp = indexTarget + 2;
				final Coordinate[] coords1 = new Coordinate[nbSp];
				for ( int i = 0; i <= indexTarget; i++ ) {
					coords1[i] = coords[i];
				}
				coords1[indexTarget + 1] = new GamaPoint(pt.getLocation());

				nbSp = coords.length - indexTarget;
				final Coordinate[] coords2 = new Coordinate[nbSp];
				coords2[0] = new GamaPoint(pt.getLocation());
				int k = 1;
				for ( int i = indexTarget + 1; i < coords.length; i++ ) {
					coords2[k] = coords[i];
					k++;
				}
				final List<Geometry> geoms1 = new ArrayList<Geometry>();
				geoms1.add(GeometryUtils.FACTORY.createLineString(coords1));
				geoms1.add(GeometryUtils.FACTORY.createLineString(coords2));
				geoms = geoms1;
			}
			if ( geoms != null ) {
				for ( final Geometry g : geoms ) {
					lines.add(new GamaShape(g));
				}
			}
			return lines;
		}
	}

	public static abstract class Transformations {

		@operator(value = "convex_hull", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry corresponding to the convex hull of the operand.",
		examples = { @example(value = "convex_hull(self)",
				equals = "the convex hull of the geometry of the agent applying the operator",
				test = false) })
		public static IShape convex_hull(final IScope scope, final IShape g) {
			return new GamaShape(g, g.getInnerGeometry().convexHull());
		}

		@operator(value = { IKeyword.MULTIPLY, "scaled_by" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) scaled by the right-hand operand coefficient",
		examples = {
			// @example(value = "shape * 2",
			// equals = "a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 2",
			@example(value = "circle(10) * 2", equals = "circle(20)") }) })
		public static
		IShape scaled_by(final IScope scope, final IShape g, final Double coefficient) {
			return new GamaShape(g, null, null, null, coefficient);
			// return g1.scaledBy(scope, coefficient);
			// return new GamaShape(g1.getInnerGeometry()).scaledBy(scope, coefficient);
			// return new GamaShape(GeometryUtils.homothetie(g.getInnerGeometry(), coefficient));
		}

		@operator(value = { IKeyword.MULTIPLY, "scaled_by" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "if the left-hand operand is a geometry and the rigth-hand operand a point, returns a geometry corresponding to the left-hand operand (geometry, agent, point) scaled by the right-hand operand coefficients in the 3 dimensions",
		examples = { @example(value = "shape * {0.5,0.5,2}",
				equals = "a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 0.5 in x, 0.5 in y and 2 in z",
				test = false) }) })
		public static
		IShape scaled_by(final IScope scope, final IShape g, final GamaPoint coefficients) {
			return new GamaShape(g, null, null, null, coefficients, false);
			// return g1.scaledBy(scope, coefficient);
			// return new GamaShape(g1.getInnerGeometry()).scaledBy(scope, coefficient);
			// return new GamaShape(GeometryUtils.homothetie(g.getInnerGeometry(), coefficient));
		}

		@operator(value = { "scaled_to" },
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "allows to restrict the size of a geometry so that it fits in the envelope {width, height, depth} defined by the second operand",
		examples = { @example(value = "shape scaled_to {10,10}",
				equals = "a geometry corresponding to the geometry of the agent applying the operator scaled so that it fits a square of 10x10",
				test = false) })
		public static
		IShape scaled_to(final IScope scope, final IShape g, final GamaPoint bounds) {
			return new GamaShape(g, null, null, null, bounds, true);
			// final GamaShape g1 = g.asShapeWithGeometry(scope, null);
			// return g1.scaledTo(scope, bounds);
			// return new GamaShape(g1.getInnerGeometry()).scaledTo(scope, bounds);

		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "",
		usages = @usage(value = "if the left-hand operand is a geometry and the rigth-hand operand a map (with [distance::float, quadrantSegments:: int (the number of line segments used to represent a quadrant of a circle), endCapStyle::int (1: (default) a semi-circle, 2: a straight line perpendicular to the end segment, 3: a half-square)]), returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged considering the right-hand operand parameters",
				examples = @example(value = "shape + [\"distance\"::5.0, \"quadrantSegments\"::4, \"endCapStyle\":: 2]",
					equals = "a geometry corresponding to the geometry of the agent applying the operator enlarged by a distance of 5, with 4 segments to represent a quadrant of a circle and a straight line perpendicular to the end segment",
					test = false)))
		public static
		IShape enlarged_by(final IScope scope, final IShape g, final GamaMap parameters) {
			final Double distance = (Double) parameters.get("distance");
			final Integer quadrantSegments = (Integer) parameters.get("quadrantSegments");
			final Integer endCapStyle = (Integer) parameters.get("endCapStyle");
			if ( endCapStyle == null ) { return new GamaShape(g, g.getInnerGeometry()
				.buffer(distance, quadrantSegments)); }
			return new GamaShape(g, g.getInnerGeometry().buffer(distance, quadrantSegments, endCapStyle));
		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged by the right-hand operand distance",
		examples = { @example(value = "circle(5) + 5",
		// equals = "a geometry corresponding to the geometry of the agent applying the operator enlarged by a distance of 5", test = false
				equals = "circle(10)") }) })
		public static
		IShape enlarged_by(final IScope scope, final IShape g, final Double size) {
			if ( g == null ) { return null; }
			Geometry gg = g.getInnerGeometry().buffer(size);
			if ( gg != null && !gg.isEmpty() ) { return new GamaShape(g, gg); }
			return null;
		}

		@operator(value = { "-", "reduced_by" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) reduced by the right-hand operand distance",
		examples = { @example(value = "shape - 5",
				equals = "a geometry corresponding to the geometry of the agent applying the operator reduced by a distance of 5",
				test = false) }) })
		public static
		IShape reduced_by(final IScope scope, final IShape g, final Double size) {
			if ( g == null ) { return null; }
			return enlarged_by(scope, g, -size);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a rotation (of a given angle) to the agent geometry
		 *
		 * @param args : angle --: double, degree
		 *
		 */
		@operator(value = "rotated_by", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry resulting from the application of a rotation by the right-hand operand angle (degree) to the left-hand operand (geometry, agent, point)",
		masterDoc = true,
		examples = { @example(value = "self rotated_by 45",
				equals = "the geometry resulting from a 45 degres rotation to the geometry of the agent applying the operator.",
				test = false) },
		see = { "transformed_by", "translated_by" })
		public static
		IShape rotated_by(final IScope scope, final IShape g1, final Double angle) {
			if ( g1 == null ) { return null; }
			return new GamaShape(g1, null, angle, null);
			// final GamaShape s = g1.asShapeWithGeometry(scope, null);
			// return s.rotatedBy(scope, Math.toRadians(angle));
			// return new GamaShape(s.getInnerGeometry()).rotatedBy(scope, Math.toRadians(angle));
		}

		@operator(value = "rotated_by", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry resulting from the application of a rotation by the right-hand operand angles (degree) along the three axis (x,y,z) to the left-hand operand (geometry, agent, point)",
		masterDoc = true,
		examples = { @example(value = "rotated_by(45, {1,0,0})",
				equals = "the geometry resulting from a 45 degres rotation along the {1,0,0} vector to the geometry of the agent applying the operator.",
				test = false) },
		see = { "transformed_by", "translated_by" })
		public static
		IShape rotated_by(final IScope scope, final IShape g1, final Double rotation, final GamaPoint vector) {
			if ( g1 == null ) { return null; }
			return new GamaShape(g1, null, rotation, vector, g1.getLocation());
		}

		@operator(value = "rotated_by", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(comment = "the right-hand operand can be a float or a int")
		public static IShape rotated_by(final IScope scope, final IShape g1, final Integer angle) {
			if ( g1 == null ) { return null; }
			if ( angle == null ) { return g1.copy(scope); }
			// if ( g1.isPoint() ) { return g1.copy(scope); }
			return new GamaShape(g1, null, angle.doubleValue(), null);
			// final GamaShape s = g1.asShapeWithGeometry(scope, null);
			// return s.rotatedBy(scope, angle);
			// return new GamaShape(s.getInnerGeometry()).rotatedBy(scope, angle);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a affinite operation (of a given coefficient and angle)to the agent
		 *             geometry. Angle
		 *             is given by the point.x ; Coefficient by the point.y
		 *
		 * @param args : coefficient --: double; angle --: double, rad
		 *
		 */
		@operator(value = "transformed_by", category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry resulting from the application of a rotation and a scaling (rigth-operand : point {angle(degree), scale factor} of the left-hand operand (geometry, agent, point)",
		examples = { @example(value = "self transformed_by {45, 0.5}",
				equals = "the geometry resulting from 45 degrees rotation and 50% scaling of the geometry of the agent applying the operator.",
				test = false) },
		see = { "rotated_by", "translated_by" })
		public static
		IShape transformed_by(final IScope scope, final IShape g, final GamaPoint p) {
			if ( g == null ) { return null; }
			return scaled_by(scope, rotated_by(scope, g, p.x), p.y);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a translation operation (vector (dx, dy)) to the agent geometry
		 *
		 * @param args : dx --: double; dy --: double
		 *
		 */
		@operator(value = "translated_by",
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry resulting from the application of a translation by the right-hand operand distance to the left-hand operand (geometry, agent, point)",
		examples = { @example(value = "self translated_by {10,10,10}",
				equals = "the geometry resulting from applying the translation to the left-hand geometry (or agent).",
				test = false) }, see = { "rotated_by", "transformed_by" })
		public static
		IShape translated_by(final IScope scope, final IShape g, final GamaPoint p) throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return at_location(scope, g, msi.gaml.operators.Points.add((GamaPoint) g.getLocation(), p));
		}

		@operator(value = { "at_location", "translated_to" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry resulting from the tran of a translation to the right-hand operand point of the left-hand operand (geometry, agent, point)",
		examples = { @example(value = "self at_location {10, 20}",
				equals = "the geometry resulting from a translation to the location {10, 20} of the left-hand geometry (or agent).",
				test = false) })
		public static
		IShape at_location(final IScope scope, final IShape g, final ILocation p) throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return new GamaShape(g, null, (Double) null, p);
		}

		@operator(value = { "without_holes", "solid" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry corresponding to the operand geometry (geometry, agent, point) without its holes",
		examples = { @example(value = "solid(self)",
				equals = "the geometry corresponding to the geometry of the agent applying the operator without its holes.",
				test = false) })
		public static
		IShape without_holes(final IScope scope, final IShape g) {
			if ( g == null ) { return null; }
			final Geometry geom = g.getInnerGeometry();
			Geometry result = geom;
			if ( geom instanceof Polygon ) {
				result =
					GeometryUtils.FACTORY.createPolygon(
						GeometryUtils.FACTORY.createLinearRing(((Polygon) geom).getExteriorRing().getCoordinates()),
						null);
			} else if ( geom instanceof MultiPolygon ) {
				final MultiPolygon mp = (MultiPolygon) geom;
				final Polygon[] polys = new Polygon[mp.getNumGeometries()];
				for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
					final Polygon p = (Polygon) mp.getGeometryN(i);
					polys[i] =
						GeometryUtils.FACTORY.createPolygon(
							GeometryUtils.FACTORY.createLinearRing(p.getExteriorRing().getCoordinates()), null);
				}
				result = GeometryUtils.FACTORY.createMultiPolygon(polys);
			}
			return new GamaShape(g, result);
		}

		@operator(value = "triangulate", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand geometry (geometry, agent, point)",
		masterDoc = true,
		examples = { @example(value = "triangulate(self)",
				equals = "the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.",
				test = false) })
		public static
		IList<IShape> triangulate(final IScope scope, final IShape g) {
			if ( g == null ) { return null; }
			return GeometryUtils.triangulation(scope, g.getInnerGeometry());
		}

		@operator(value = { "triangulate", "to_triangles" }, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand list of geometries",
		examples = { @example(value = "triangulate(self)",
				equals = "the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator.",
				test = false) })
		public static
		IList<IShape> triangulate(final IScope scope, final IList<IShape> ls) {
			return GeometryUtils.triangulation(scope, ls);
		}

		@operator(value = "voronoi", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries corresponding to the Voronoi diagram built from the list of points",
		masterDoc = true,
		examples = { @example(value = "voronoi([{10,10},{50,50},{90,90},{10,90},{90,10}])",
				equals = "the list of geometries corresponding to the Voronoi Diagram built from the list of points.",
				test = false) })
		public static IList<IShape> vornoi(final IScope scope, final IList<GamaPoint> pts) {
			if ( pts == null ) { return null; }
			return GeometryUtils.voronoi(scope, pts);
		}

		@operator(value = "smooth", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "Returns a 'smoothed' geometry, where straight lines are replaces by polynomial (bicubic) curves. The first parameter is the original geometry, the second is the 'fit' parameter which can be in the range 0 (loose fit) to 1 (tightest fit).",
		masterDoc = true,
		examples = { @example(value = "smooth(square(10), 0.0)", equals = "a 'rounded' square", test = false) })
		public static
		IShape smooth(final IScope scope, final IShape geometry, final Double fit) {
			if ( geometry == null ) { return null; }
			double param = fit == null ? 0d : fit < 0 ? 0d : fit > 1 ? 1d : fit;
			return GeometryUtils.smooth(geometry.getInnerGeometry(), param);
		}

		@operator(value = "to_squares", type = IType.LIST, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of squares of the size corresponding to the given size that result from the decomposition of the geometry into squares (geometry, size, overlaps), if overlaps = true, add the squares that overlap the border of the geometry",
		examples = { @example(value = "to_squares(self, 10.0, true)",
				equals = "the list of squares of side size 10.0 corresponding to the discretisation into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept",
				test = false) })
		public static
		IList<IShape> toSquares(final IScope scope, final IShape geom, final Double dimension,
			final boolean overlaps) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			return GeometryUtils.discretisation(geom.getInnerGeometry(), dimension, dimension, overlaps);
		}

		@operator(value = "to_squares", type = IType.LIST, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of a given number of squares from the decomposition of the geometry into squares (geometry, nb_square, overlaps), if overlaps = true, add the squares that overlap the border of the geometry",
		examples = { @example(value = "to_squares(self, 10, true)",
				equals = "the list of 10 squares corresponding to the discretisation into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept",
				test = false) })
		public static
		IList<IShape> toSquares(final IScope scope, final IShape geom, final Integer nbSquares,
			final boolean overlaps) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			return GeometryUtils.discretisation(geom.getInnerGeometry(), nbSquares, overlaps, 0.99);
		}

		@operator(value = "to_squares", type = IType.LIST, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of a given number of squares from the decomposition of the geometry into squares (geometry, nb_square, overlaps, precision_coefficient), if overlaps = true, add the squares that overlap the border of the geometry, coefficient_precision should be close to 1.0",
		examples = { @example(value = "to_squares(self, 10, true, 0.99)",
				equals = "the list of 10 squares corresponding to the discretisation into squares of the geometry of the agent applying the operator. The squares overlapping the border of the geometry are kept",
				test = false) })
		public static
		IList<IShape> toSquares(final IScope scope, final IShape geom, final Integer nbSquares,
			final boolean overlaps, final double precision) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			return GeometryUtils.discretisation(geom.getInnerGeometry(), nbSquares, overlaps, precision);
		}

		@operator(value = "to_rectangles", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of rectangles of the size corresponding to the given dimension that result from the decomposition of the geometry into rectangles (geometry, dimension, overlaps), if overlaps = true, add the rectangles that overlap the border of the geometry",
		examples = { @example(value = "to_rectangles(self, {10.0, 15.0}, true)",
				equals = "the list of rectangles of size {10.0, 15.0} corresponding to the discretisation into rectangles of the geometry of the agent applying the operator. The rectangles overlapping the border of the geometry are kept",
				test = false) })
		public static
		IList<IShape> to_rectangle(final IScope scope, final IShape geom, final GamaPoint dimension,
			final boolean overlaps) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			return GeometryUtils.discretisation(geom.getInnerGeometry(), dimension.x, dimension.y, overlaps);
		}

		@operator(value = "to_rectangles", type = IType.LIST, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of rectangles corresponding to the given dimension that result from the decomposition of the geometry into rectangles (geometry, nb_cols, nb_rows, overlaps) by a grid composed of the given number of columns and rows, if overlaps = true, add the rectangles that overlap the border of the geometry",
		examples = { @example(value = "to_rectangles(self, 5, 20, true)",
				equals = "the list of rectangles corresponding to the discretisation by a grid of 5 columns and 20 rows into rectangles of the geometry of the agent applying the operator. The rectangles overlapping the border of the geometry are kept",
				test = false) })
		public static
		IList<IShape> to_rectangle(final IScope scope, final IShape geom, final int nbCols, final int nbRows,
			final boolean overlaps) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			double x_size = geom.getEnvelope().getWidth() / nbCols;
			double y_size = geom.getEnvelope().getHeight() / nbRows;

			return GeometryUtils.discretisation(geom.getInnerGeometry(), x_size, y_size, overlaps);
		}

		@operator(value = { "split_geometry", "to_squares" }, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries that result from the decomposition of the geometry by square cells of the given side size (geometry, size)",
		examples = { @example(value = "to_squares(self, 10.0)",
				equals = "the list of the geometries corresponding to the decomposition of the geometry by squares of side size 10.0",
				test = false) })
		public static
		IList<IShape> toSquares(final IScope scope, final IShape geom, final Double dimension) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			return GeometryUtils.geometryDecomposition(geom, dimension, dimension);
		}

		@operator(value = { "split_geometry", "to_rectangles" }, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries that result from the decomposition of the geometry by rectangle cells of the given dimension (geometry, {size_x, size_y})",
		examples = { @example(value = "to_rectangles(self, {10.0, 15.0})",
				equals = "the list of the geometries corresponding to the decomposition of the geometry by rectangles of size 10.0, 15.0",
				test = false) })
		public static
		IList<IShape> toRectangle(final IScope scope, final IShape geom, final GamaPoint dimension) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			return GeometryUtils.geometryDecomposition(geom, dimension.x, dimension.y);
		}

		@operator(value = { "split_geometry", "to_rectangles" }, content_type = IType.GEOMETRY, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries that result from the decomposition of the geometry according to a grid with the given number of rows and columns (geometry, nb_cols, nb_rows)",
		examples = { @example(value = "to_rectangles(self, 10,20)",
				equals = "the list of the geometries corresponding to the decomposition of the geometry of the agent applying the operator",
				test = false) })
		public static
		IList<IShape> to_rectangle(final IScope scope, final IShape geom, final int nbCols, final int nbRows) {
			if ( geom == null || geom.getInnerGeometry().getArea() <= 0 ) { return GamaListFactory
				.create(Types.GEOMETRY); }
			double x_size = geom.getEnvelope().getWidth() / nbCols;
			double y_size = geom.getEnvelope().getHeight() / nbRows;

			return GeometryUtils.geometryDecomposition(geom, x_size, y_size);
		}

		@operator(value = "as_hexagonal_grid", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS, IOperatorCategory.GRID })
		@doc(value = "A list of geometries (hexagonal) corresponding to the hexagonal tesselation of the first operand geometry",
		examples = { @example(value = "self as_hexagonal_grid {10, 5}",
				equals = "list of geometries (hexagonal) corresponding to the hexagonal tesselation of the first operand geometry",
				test = false) },
		see = { "as_4_grid", "as_grid" })
		public static
		IList<IShape> as_hexagonal_grid(final IShape ls, final GamaPoint param) {
			return GeometryUtils.hexagonalGridFromGeom(ls, (int) param.x, (int) param.y);
		}

		@operator(value = "as_grid", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS, IOperatorCategory.GRID })
		@doc(value = "A matrix of square geometries (grid with 8-neighbourhood) with dimension given by the rigth-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)",
		examples = { @example(value = "self as_grid {10, 5}",
				equals = "a matrix of square geometries (grid with 8-neighbourhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.",
				test = false) },
		see = { "as_4_grid", "as_hexagonal_grid" })
		public static
		IMatrix as_grid(final IScope scope, final IShape g, final GamaPoint dim) throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(scope, g, (int) dim.x, (int) dim.y, false, false, false, false);
		}

		@operator(value = "as_4_grid", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS, IOperatorCategory.GRID })
		@doc(value = "A matrix of square geometries (grid with 4-neighbourhood) with dimension given by the rigth-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)",
		examples = { @example(value = "self as_4_grid {10, 5}",
				equals = "the matrix of square geometries (grid with 4-neighbourhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator.",
				test = false) },
		see = { "as_grid", "as_hexagonal_grid" })
		public static
		IMatrix as_4_grid(final IScope scope, final IShape g, final GamaPoint dim) throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(scope, g, (int) dim.x, (int) dim.y, false, true, false, false);
		}

		@operator(value = "split_lines", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries resulting after cutting the lines at their intersections.",
		examples = { @example(value = "split_lines([line([{0,10}, {20,10}]), line([{0,10}, {20,10}])])",
				equals = "a list of four polylines: line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) and line([{10,10}, {10,20}])",
				test = false) })
		public static
		IList<IShape> split_lines(final IScope scope, final IContainer<?, IShape> geoms)
			throws GamaRuntimeException {
			if ( geoms.isEmpty(scope) ) { return GamaListFactory.create(Types.GEOMETRY); }
			Geometry nodedLineStrings = geoms.firstValue(scope).getInnerGeometry();

			for ( final Object obj : geoms.iterable(scope) ) {
				final Geometry g = ((IShape) obj).getInnerGeometry();
				if ( g instanceof LineString ) {
					nodedLineStrings = nodedLineStrings.union(g);
				}
			}
			final IList<IShape> nwGeoms = GamaListFactory.create(Types.GEOMETRY);

			for ( int i = 0, n = nodedLineStrings.getNumGeometries(); i < n; i++ ) {
				final Geometry g = nodedLineStrings.getGeometryN(i);
				if ( g instanceof LineString ) {
					nwGeoms.add(new GamaShape(g));
				}
			}
			return nwGeoms;
		}

		@operator(value = "skeletonize", content_type = IType.GEOMETRY, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A list of geometries (polylines) corresponding to the skeleton of the operand geometry (geometry, agent)",
		examples = { @example(value = "skeletonize(self)",
				equals = "the list of geometries corresponding to the skeleton of the geometry of the agent applying the operator.",
				test = false) })
		public static
		IList<IShape> skeletonize(final IScope scope, final IShape g) {
			final List<LineString> netw = GeometryUtils.squeletisation(scope, g.getInnerGeometry());
			final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			for ( final LineString ls : netw ) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator(value = "clean", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry corresponding to the cleaning of the operand (geometry, agent, point)",
		comment = "The cleaning corresponds to a buffer with a distance of 0.0",
		examples = { @example(value = "clean(self)",
				equals = "returns the geometry resulting from the cleaning of the geometry of the agent applying the operator.",
				test = false) })
		public static
		IShape clean(final IScope scope, final IShape g) {

			if ( g == null || g.getInnerGeometry() == null ) { return g; }
			if ( g.getInnerGeometry() instanceof Polygon ) { return new GamaShape(g, g.getInnerGeometry().buffer(0.0,
				BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_FLAT)); }
			if ( g.getInnerGeometry() instanceof MultiPolygon ) {
				final MultiPolygon mp = (MultiPolygon) g.getInnerGeometry();
				final int nb = mp.getNumGeometries();
				final Polygon[] polys = new Polygon[nb];
				for ( int i = 0; i < nb; i++ ) {
					polys[i] =
						(Polygon) mp.getGeometryN(i).buffer(0.0, BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
							BufferParameters.CAP_FLAT);
				}
				return new GamaShape(g, GeometryUtils.FACTORY.createMultiPolygon(polys));
			}
			return g.copy(scope);
		}

		/**
		 * Simplification of a geometry (Douglas-Peuker algorithm)
		 */

		@operator(value = "simplification", category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(value = "A geometry corresponding to the simplification of the operand (geometry, agent, point) considering a tolerance distance.",
		comment = "The algorithm used for the simplification is Douglas-Peucker",
		examples = { @example(value = "self simplification 0.1",
				equals = "the geometry resulting from the application of the Douglas-Peuker algorithm on the geometry of the agent applying the operator with a tolerance distance of 0.1.",
				test = false) })
		public static
		IShape simplification(final IScope scope, final IShape g1, final Double distanceTolerance) {
			if ( g1 == null || g1.getInnerGeometry() == null ) { return g1; }
			if ( g1.isPoint() ) { return g1.copy(scope); }
			final Geometry geomSimp = DouglasPeuckerSimplifier.simplify(g1.getInnerGeometry(), distanceTolerance);
			if ( geomSimp != null && !geomSimp.isEmpty() && geomSimp.isSimple() ) { return new GamaShape(g1, geomSimp); }
			return g1.copy(scope);
		}

	}

	public static abstract class Relations {

		@operator(value = { "towards", "direction_to" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_RELATIONS })
		@doc(value = "The direction (in degree) between the two geometries (geometries, agents, points) considering the topology of the agent applying the operator.",
		examples = { @example(value = "ag1 towards ag2",
				equals = "the direction between ag1 and ag2 and ag3 considering the topology of the agent applying the operator",
				isExecutable = false) },
		see = { "distance_between", "distance_to", "direction_between", "path_between", "path_to" })
		public static
		Integer towards(final IScope scope, final IShape agent, final IShape target) {
			return scope.getTopology().directionInDegreesTo(scope, agent, target);
		}

		@operator(value = "distance_between", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS })
		@doc(value = "A distance between a list of geometries (geometries, agents, points) considering a topology.",
		examples = { @example(value = "my_topology distance_between [ag1, ag2, ag3]",
				equals = "the distance between ag1, ag2 and ag3 considering the topology my_topology",
				isExecutable = false) }, see = { "towards", "direction_to", "distance_to", "direction_between",
			"path_between", "path_to" })
		public static Double distance_between(final IScope scope, final ITopology t,
			final IContainer<?, IShape> geometries) {
			final int size = geometries.length(scope);
			if ( size == 0 || size == 1 ) { return 0d; }
			IShape previous = null;
			Double distance = 0d;
			for ( final IShape obj : geometries.iterable(scope) ) {
				if ( previous != null ) {
					final Double d = t.distanceBetween(scope, previous, obj);
					if ( d == null ) { return null; }
					distance += d;
				}
				previous = obj;
			}
			return distance;
		}

		@operator(value = "direction_between", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS })
		@doc(value = "A direction (in degree) between a list of two geometries (geometries, agents, points) considering a topology.",
		examples = { @example(value = "my_topology direction_between [ag1, ag2]",
				equals = "the direction between ag1 and ag2 considering the topology my_topology",
				isExecutable = false) }, see = { "towards", "direction_to", "distance_to", "distance_between",
			"path_between", "path_to" })
		public static
		Integer direction_between(final IScope scope, final ITopology t, final IContainer<?, IShape> geometries)
			throws GamaRuntimeException {
			final int size = geometries.length(scope);
			if ( size == 0 || size == 1 ) { return 0; }
			final IShape g1 = geometries.firstValue(scope);
			final IShape g2 = geometries.lastValue(scope);
			return t.directionInDegreesTo(scope, g1, g2);
		}

		@operator(value = "path_between",
			type = IType.PATH,
			content_type = ITypeProvider.FIRST_CONTENT_TYPE,
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS, IOperatorCategory.PATH })
		@doc(value = "A path between a list of two geometries (geometries, agents or points) considering a topology.",
		examples = { @example(value = "my_topology path_between [ag1, ag2]",
				equals = "A path between ag1 and ag2",
				isExecutable = false) }, see = { "towards", "direction_to", "distance_between", "direction_between",
			"path_to", "distance_to" })
		public static IPath path_between(final IScope scope, final ITopology graph, final IContainer<?, IShape> nodes)
			throws GamaRuntimeException {
			// TODO Assumes that all elements in nodes are vertices of the graph... Should be
			// checked

			if ( nodes.isEmpty(scope) ) { return null; }
			final int n = nodes.length(scope);
			final IShape source = nodes.firstValue(scope);
			if ( n == 1 ) { return PathFactory.newInstance(scope.getTopology(), source, source,
				GamaListFactory.<IShape> create(Types.GEOMETRY));
			// return new GamaPath(scope.getTopology(), source, source, new GamaList());
			}
			final IShape target = nodes.lastValue(scope);
			if ( n == 2 ) { return graph.pathBetween(scope, source, target); }
			final IList<IShape> edges = GamaListFactory.create(Types.GEOMETRY);
			IShape previous = null;
			for ( final IShape gg : nodes.iterable(scope) ) {
				if ( previous != null ) {
					// TODO Take the case of ILocation
					edges.addAll(graph.pathBetween(scope, previous, gg).getEdgeList());
				}
				previous = gg;
			}
			return PathFactory.newInstance(graph, source, target, edges);
			// new GamaPath(graph, source, target, edges);
		}

		@operator(value = "distance_to", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS })
		@doc(value = "A distance between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.",
		masterDoc = true,
		examples = { @example(value = "ag1 distance_to ag2",
				equals = "the distance between ag1 and ag2 considering the topology of the agent applying the operator",
				isExecutable = false) },
		see = { "towards", "direction_to", "distance_between", "direction_between", "path_between", "path_to" })
		public static
		Double distance_to(final IScope scope, final IShape source, final IShape target) {
			return scope.getTopology().distanceBetween(scope, source, target);
		}

		@operator(value = "distance_to", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_RELATIONS })
		@doc(value = "An Euclidean distance between two points.")
		// No documentation because it is same same as the previous one (but optimized for points?)
		public static
		Double distance_to(final IScope scope, final GamaPoint source, final GamaPoint target) {
			return scope.getTopology().distanceBetween(scope, source, target);
		}

		@operator(value = "path_to", type = IType.PATH, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_RELATIONS, IOperatorCategory.PATH })
		@doc(value = "A path between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.",
		masterDoc = true,
		examples = { @example(value = "ag1 path_to ag2",
				equals = "the path between ag1 and ag2 considering the topology of the agent applying the operator",
				isExecutable = false) }, see = { "towards", "direction_to", "distance_between", "direction_between",
			"path_between", "distance_to" })
		public static
		IPath path_to(final IScope scope, final IShape g, final IShape g1) throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getTopology().pathBetween(scope, g, g1);
		}

		@operator(value = "path_to", type = IType.PATH, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_RELATIONS, IOperatorCategory.PATH })
		@doc(value = "A shortest path between two points considering the topology of the agent applying the operator.")
		// No documentation because it is same same as the previous one (but optimized for points?)
		public static
		IPath path_to(final IScope scope, final GamaPoint g, final GamaPoint g1) throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getTopology().pathBetween(scope, g, g1);
		}

	}

	public static abstract class Properties {

		@operator(value = { "disjoint_from" }, category = { IOperatorCategory.SPATIAL })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) is disjoints from the right-geometry (or agent/point).",
		usages = { @usage(value = "if one of the operand is null, returns true."),
			@usage(value = "if one operand is a point, returns false if the point is included in the geometry.") },
			examples = {
			@example(value = "polyline([{10,10},{20,20}]) disjoint_from polyline([{15,15},{25,25}])",
				equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{15,15},{15,25},{25,25},{25,15}])",
				equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from {15,15}", equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from {25,25}", equals = "true"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{35,35},{35,45},{45,45},{45,35}])",
				equals = "true") },
			see = { "intersects", "crosses", "overlaps", "partially_overlaps", "touches" })
		public static
		Boolean disjoint_from(final IScope scope, final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return true; }
			if ( g1.getInnerGeometry() == null || g2.getInnerGeometry() == null ) { return true; }
			return !g1.intersects(g2);
		}

		/**
		 * Return true if the agent geometry overlaps the geometry of the localized entity passed in
		 * parameter
		 *
		 * @param args : agent --: a localized entity
		 *
		 */

		@operator(value = "overlaps", category = { IOperatorCategory.SPATIAL })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) overlaps the right-geometry (or agent/point).",
		usages = { @usage("if one of the operand is null, returns false."),
			@usage("if one operand is a point, returns true if the point is included in the geometry") },
			examples = {
			@example(value = "polyline([{10,10},{20,20}]) overlaps polyline([{15,15},{25,25}])", equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}])",
					equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps {25,25}", equals = "false"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{35,35},{35,45},{45,45},{45,35}])",
					equals = "false"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polyline([{10,10},{20,20}])",
					equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps {15,15}", equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{0,0},{0,30},{30,30}, {30,0}])",
					equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}])",
					equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{10,20},{20,20},{20,30},{10,30}])",
					equals = "true") },
			see = { "disjoint_from", "crosses", "intersects", "partially_overlaps", "touches" })
		public static
		Boolean overlaps(final IScope scope, final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return !disjoint_from(scope, g1, g2);
		}

		/**
		 * Return true if the agent geometry partially overlaps the geometry of the localized agent
		 * passed in parameter
		 *
		 * @param args : agent --: a localized entity
		 *
		 */

		@operator(value = "partially_overlaps",
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) partially overlaps the right-geometry (or agent/point).",
		usages = { @usage("if one of the operand is null, returns false.") },
		comment = "if one geometry operand fully covers the other geometry operand, returns false (contrarily to the overlaps operator).",
		examples = {
			@example(value = "polyline([{10,10},{20,20}]) partially_overlaps polyline([{15,15},{25,25}])",
				equals = "true"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}])",
				equals = "true"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {25,25}",
				equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{35,35},{35,45},{45,45},{45,35}])",
				equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polyline([{10,10},{20,20}])",
				equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps {15,15}",
				equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{0,0},{0,30},{30,30}, {30,0}])",
				equals = "false"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}])",
				equals = "true"),
				@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{10,20},{20,20},{20,30},{10,30}])",
				equals = "false") },
			see = { "disjoint_from", "crosses", "overlaps", "intersects", "touches" })
		public static
		Boolean partially_overlaps(final IShape g1, final IShape g) {
			if ( g == null ) { return false; }
			return g1.getInnerGeometry().overlaps(g.getInnerGeometry());
		}

		/**
		 * Return true if the agent geometry touches the geometry of the localized entity passed in
		 * parameter
		 *
		 * @param args : agent --: a localized entity
		 *
		 */
		@operator(value = "touches", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) touches the right-geometry (or agent/point).",
		usages = { @usage("if one of the operand is null, returns false.") },
		comment = "returns true when the left-operand only touches the right-operand. When one geometry covers partially (or fully) the other one, it returns false.",
		examples = {
			@example(value = "polyline([{10,10},{20,20}]) touches {15,15}", equals = "false"),
			@example(value = "polyline([{10,10},{20,20}]) touches {10,10}", equals = "true"),
			@example(value = "{15,15} touches {15,15}", equals = "false"),
			@example(value = "polyline([{10,10},{20,20}]) touches polyline([{10,10},{5,5}])", equals = "true"),
			@example(value = "polyline([{10,10},{20,20}]) touches polyline([{5,5},{15,15}])", equals = "false"),
			@example(value = "polyline([{10,10},{20,20}]) touches polyline([{15,15},{25,25}])", equals = "false"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{15,15},{15,25},{25,25},{25,15}])",
					equals = "false"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,20},{20,20},{20,30},{10,30}])",
					equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,10},{0,10},{0,0},{10,0}])",
					equals = "true"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches {15,15}", equals = "false"),
			@example(value = "polygon([{10,10},{10,20},{20,20},{20,10}]) touches {10,15}", equals = "true") },
			see = { "disjoint_from", "crosses", "overlaps", "partially_overlaps", "intersects" })
		public static
		Boolean touches(final IShape g, final IShape g2) {
			if ( g == null ) { return false; }
			return g2.getInnerGeometry().touches(g.getInnerGeometry());
		}

		/**
		 * Return true if the agent geometry crosses the geometry of the localized entity passed in
		 * parameter
		 *
		 * @param args : agent --: a localized entity
		 *
		 */

		@operator(value = "crosses", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) crosses the right-geometry (or agent/point).",
		usages = { @usage("if one of the operand is null, returns false."),
			@usage("if one operand is a point, returns false.") },
			examples = {
			@example(value = "polyline([{10,10},{20,20}]) crosses polyline([{10,20},{20,10}])", equals = "true"),
			@example(value = "polyline([{10,10},{20,20}]) crosses {15,15}", equals = "true"),
			@example(value = "polyline([{0,0},{25,25}]) crosses polygon([{10,10},{10,20},{20,20},{20,10}])",
					equals = "true") },
			see = { "disjoint_from", "intersects", "overlaps", "partially_overlaps", "touches" })
		public static
		Boolean crosses(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.crosses(g2);
		}

		@operator(value = "intersects", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) intersects the right-geometry (or agent/point).",
		usages = { @usage("if one of the operand is null, returns false.") },
		examples = { @example(value = "square(5) intersects {10,10}", equals = "false") },
		see = { "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
		public static
		Boolean intersects(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.intersects(g2);
		}

		// @operator("intersects")
		// @doc(value = "A boolean, equal to true if the geometry (left operand) intersects the point (right operand).")
		// // no documentation because same same as before but optimized for points.
		// public static Boolean intersects(final IShape g1, final GamaPoint p) {
		// if ( g1 == null || p == null ) { return false; }
		// return pl.intersects(p, g1.getInnerGeometry());
		// }

		@operator(value = "covers", category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) covers the right-geometry (or agent/point).",
		usages = { @usage("if one of the operand is null, returns false.") },
		examples = { @example(value = "square(5) covers square(2)", equals = "true") },
		see = { "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
		public static
		Boolean covers(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.covers(g2);
		}

	}

	public static abstract class Punctal {

		@operator(value = { "any_location_in", "any_point_in" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.POINT })
		@doc(value = "A point inside (or touching) the operand-geometry.",
		examples = { @example(value = "any_location_in(square(5))",
				equals = "a point in the square, for example : {3,4.6}.",
				test = false) }, see = { "closest_points_with", "farthest_point_to", "points_at" })
		public static ILocation any_location_in(final IScope scope, final IShape g) {
			final ILocation p = GeometryUtils.pointInGeom(g.getInnerGeometry(), scope.getRandom());
			return p;
		}

		@operator(value = { "points_on" }, type = IType.LIST, content_type = IType.POINT, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.POINT })
		@doc(value = "A list of points of the operand-geometry distant from each other to the float right-operand .",
		examples = { @example(value = " square(5) points_on(2)",
				equals = "a list of points belonging to the exterior ring of the square distant from each other of 2.",
				test = false) }, see = { "closest_points_with", "farthest_point_to", "points_at" })
		public static IList points_on(final IShape geom, final Double distance) {
			final IList<GamaPoint> locs = GamaListFactory.create(Types.POINT);
			if ( geom.getInnerGeometry() instanceof GeometryCollection ) {
				for ( int i = 0; i < geom.getInnerGeometry().getNumGeometries(); i++ ) {
					locs.addAll(GeometryUtils.locsOnGeometry(geom.getInnerGeometry().getGeometryN(i), distance));
				}
			} else {
				locs.addAll(GeometryUtils.locsOnGeometry(geom.getInnerGeometry(), distance));
			}
			return locs;
		}

		@operator(value = { "points_at" }, content_type = IType.POINT, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.POINT })
		@doc(value = "A list of left-operand number of points located at a the right-operand distance to the agent location.",
		examples = { @example(value = "3 points_at(20.0)",
				equals = "returns [pt1, pt2, pt3] with pt1, pt2 and pt3 located at a distance of 20.0 to the agent location",
				test = false) },
		see = { "any_location_in", "any_point_in", "closest_points_with", "farthest_point_to" })
		public static
		IList<ILocation> points_at(final IScope scope, final Integer nbLoc, final Double distance) {
			if ( distance == null || nbLoc == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				throw GamaRuntimeException.error("Impossible to compute points_at");
			}
			final IList<ILocation> locations = GamaListFactory.create(Types.POINT);
			final ILocation loc = scope.getAgentScope().getLocation();
			final double angle1 = scope.getRandom().between(0, 2 * Math.PI);

			for ( int i = 0; i < nbLoc; i++ ) {
				final GamaPoint p =
					new GamaPoint(loc.getX() + distance * Math.cos(angle1 + (double) i / nbLoc * 2 * Math.PI),
						loc.getY() + distance * Math.sin(angle1 + (double) i / nbLoc * 2 * Math.PI));
				locations.add(p);
			}
			return locations;

		}

		@operator(value = "closest_points_with", type = IType.LIST, content_type = IType.POINT, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.POINT })
		@doc(value = "A list of two closest points between the two geometries.",
		examples = { @example(value = "geom1 closest_points_with(geom2)",
				equals = "[pt1, pt2] with pt1 the closest point of geom1 to geom2 and pt1 the closest point of geom2 to geom1",
				isExecutable = false) },
		see = { "any_location_in", "any_point_in", "farthest_point_to", "points_at" })
		public static
		IList<GamaPoint> closest_points_with(final IShape a, final IShape b) {
			final Coordinate[] coors = DistanceOp.nearestPoints(a.getInnerGeometry(), b.getInnerGeometry());
			return GamaListFactory.createWithoutCasting(Types.POINT, new GamaPoint(coors[0]), new GamaPoint(coors[1]));
		}

		@operator(value = "farthest_point_to", category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT })
		@doc(value = "the farthest point of the left-operand to the left-point.",
		examples = { @example(value = "geom farthest_point_to(pt)",
				equals = "the closest point of geom to pt",
				isExecutable = false) },
		see = { "any_location_in", "any_point_in", "closest_points_with", "points_at" })
		public static ILocation farthest_point_to(final IShape g, final GamaPoint p) {
			if ( g == null ) { return p.getLocation(); }
			if ( p == null ) { return g.getLocation(); }

			final Coordinate[] cg = g.getInnerGeometry().getCoordinates();
			if ( cg.length == 0 ) { return p; }
			Coordinate pt_max = cg[0];
			double dist_max = p.distance(pt_max);
			for ( int i = 1; i < cg.length; i++ ) {
				final double dist = p.distance(cg[i]);
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
		public static ILocation _closest_point_to(final IShape pt, final IShape geom) {
			if ( pt == null ) { return null; }
			if ( geom == null ) { return pt.getLocation(); }
			final Coordinate[] cp = new DistanceOp(geom.getInnerGeometry(), pt.getInnerGeometry()).nearestPoints();
			return new GamaPoint(cp[0]);
		}

		public static ILocation _closest_point_to(final ILocation pt, final IShape geom) {
			if ( pt == null ) { return null; }
			if ( geom == null ) { return pt; }
			PointPairDistance ppd = new PointPairDistance();
			DistanceToPoint.computeDistance(geom.getInnerGeometry(), pt.toCoordinate(), ppd);
			return new GamaPoint(ppd.getCoordinate(0));
		}

		@operator(value = "angle_between", category = { IOperatorCategory.SPATIAL, IOperatorCategory.POINT })
		@doc(value = "the angle between vectors P0P1 and P0P2 (P0, P1, P2 being the three point operands)",
		examples = { @example(value = "angle_between({5,5},{10,5},{5,10})", equals = "90") })
		public static Integer angleInDegreesBetween(final GamaPoint p0, final GamaPoint p1, final GamaPoint p2) {
			final double Xa = p1.x - p0.x;
			final double Ya = p1.y - p0.y;
			final double Xb = p2.x - p0.x;
			final double Yb = p2.y - p0.y;

			final double Na = Maths.sqrt(Xa * Xa + Ya * Ya);
			final double Nb = Maths.sqrt(Xb * Xb + Yb * Yb);
			final double C = (Xa * Xb + Ya * Yb) / (Na * Nb);
			final double S = Xa * Yb - Ya * Xb;
			final double result = S > 0 ? Maths.acos(C) : -1 * Maths.acos(C);
			return Maths.checkHeading((int) result);
		}

	}

	public static abstract class Queries {

		@operator(value = "neighbors_of", content_type = IType.AGENT, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_QUERIES })
		@doc(value = "a list, containing all the agents of the same species than the argument (if it is an agent) located at a distance inferior or equal to 1 to the right-hand operand agent considering the left-hand operand topology.",
		masterDoc = true,
		examples = { @example(value = "topology(self) neighbors_of self",
				equals = "returns all the agents located at a distance lower or equal to 1 to the agent applying the operator considering its topology.",
				test = false) },
		see = { "neighbors_at", "closest_to", "overlapping", "agents_overlapping", "agents_inside",
				"agent_closest_to" })
		public static
		IList neighbours_of(final IScope scope, final ITopology t, final IAgent agent) {
			return _neighbours(scope,
				agent instanceof IAgent ? In.list(scope, agent.getPopulation()) : Different.with(), agent, 1.0, t);
			// TODO We could compute a filter based on the population if it is an agent
		}

		@operator(value = "neighbors_of", content_type = IType.AGENT, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_QUERIES })
		/* TODO, expected_content_type = { IType.FLOAT, IType.INT } */
		@doc(usages = @usage(value = "a list, containing all the agents of the same species than the left argument (if it is an agent) located at a distance inferior or equal to the third argument to the second argument (agent, geometry or point) considering the first operand topology.",
		examples = { @example(value = "neighbors_of (topology(self), self,10)",
				equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator considering its topology.",
				test = false) }))
		public static
		IList neighbours_of(final IScope scope, final ITopology t, final IShape agent, final Double distance) {
			return _neighbours(scope, agent instanceof IAgent ? In.list(scope, ((IAgent) agent).getPopulation())
				: Different.with(), agent, distance, t);
			// TODO We could compute a filter based on the population if it is an agent
		}

		@operator(value = "neighbors_at", content_type = ITypeProvider.FIRST_TYPE, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
		@doc(value = "a list, containing all the agents of the same species than the left argument (if it is an agent) located at a distance inferior or equal to the right-hand operand to the left-hand operand (geometry, agent, point).",
		comment = "The topology used to compute the neighbourhood  is the one of the left-operand if this one is an agent; otherwise the one of the agent applying the operator.",
		examples = { @example(value = "(self neighbors_at (10))",
				equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator.",
				test = false) },
		see = { "neighbors_of", "closest_to", "overlapping", "agents_overlapping", "agents_inside",
			"agent_closest_to", "at_distance" })
		public static
		IList neighbours_at(final IScope scope, final IShape agent, final Double distance) {
			return _neighbours(scope, agent instanceof IAgent ? In.list(scope, ((IAgent) agent).getPopulation())
				: Different.with(), agent, distance);
		}

		@operator(value = "at_distance", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
		@doc(value = "A list of agents among the left-operand list that are located at a distance <= the right operand from the caller agent (in its topology)",
		examples = { @example(value = "[ag1, ag2, ag3] at_distance 20",
				equals = "the agents of the list located at a distance <= 20 from the caller agent (in the same order).",
				isExecutable = false) },
		see = { "neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
				"overlapping" })
		public static
		IList<IAgent> at_distance(final IScope scope, final IContainer<?, ? extends IShape> list,
			final Double distance) {
			return _neighbours(scope, In.list(scope, list), scope.getAgentScope(), distance);
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
		@doc(value = "A list of agents among the left-operand list, species or meta-population (addition of species), covered by the operand (casted as a geometry).",
		examples = {
			@example(value = "[ag1, ag2, ag3] inside(self)",
				equals = "the agents among ag1, ag2 and ag3 that are covered by the shape of the right-hand argument.",
				isExecutable = false),
				@example(value = "(species1 + species2) inside (self)",
				equals = "the agents among species species1 and species2 that are covered by the shape of the right-hand argument.",
				isExecutable = false) },
			see = { "neighbours_at", "neighbours_of", "closest_to", "overlapping", "agents_overlapping",
			"agents_inside", "agent_closest_to" })
		public static
		IList<IAgent> inside(final IScope scope, final IContainer<?, ? extends IShape> list, final Object source) {
			return _gather(scope, In.list(scope, list), source, true);
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
		@doc(value = "A list of agents among the left-operand list, species or meta-population (addition of species), overlapping the operand (casted as a geometry).",
		examples = {
			@example(value = "[ag1, ag2, ag3] overlapping(self)",
				equals = "return the agents among ag1, ag2 and ag3 that overlap the shape of the agent applying the operator.",
				isExecutable = false),
				@example(value = "(species1 + species2) overlapping self", isExecutable = false) },
			see = { "neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
				"agents_overlapping" })
		public static
		IList<IAgent> overlapping(final IScope scope, final IContainer<?, ? extends IShape> list,
			final Object source) {
			return _gather(scope, In.list(scope, list), source, false);
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
		@doc(value = "An agent among the left-operand list of agents, species or meta-population (addition of species), the closest to the operand (casted as a geometry).",
		comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
		examples = {
			@example(value = "[ag1, ag2, ag3] closest_to(self)",
				equals = "return the closest agent among ag1, ag2 and ag3 to the agent applying the operator.",
				isExecutable = false),
				@example(value = "(species1 + species2) closest_to self", isExecutable = false) }, see = {
			"neighbours_at", "neighbours_of", "neighbours_at", "neighbours_of", "inside", "overlapping",
			"agents_overlapping", "agents_inside", "agent_closest_to" })
		public static
		IAgent closest_to(final IScope scope, final IContainer<?, ? extends IShape> list, final Object source) {
			return _closest(scope, In.list(scope, list), source);
		}

		@operator(value = "agent_closest_to", type = IType.AGENT, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_QUERIES })
		@doc(value = "An agent, the closest to the operand (casted as a geometry).",
		comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.",
		examples = { @example(value = "agent_closest_to(self)",
				equals = "the closest agent to the agent applying the operator.",
				test = false) }, see = { "neighbours_at", "neighbours_of", "agents_inside", "agents_overlapping",
			"closest_to", "inside", "overlapping" })
		public static
		IAgent agent_closest_to(final IScope scope, final Object source) {
			return _closest(scope, Different.with(), source);
		}

		@operator(value = "agents_inside", content_type = IType.AGENT, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_QUERIES })
		@doc(value = "A list of agents covered by the operand (casted as a geometry).",
		examples = { @example(value = "agents_inside(self)",
				equals = "the agents that are covered by the shape of the agent applying the operator.",
				test = false) }, see = { "agent_closest_to", "agents_overlapping", "closest_to", "inside",
				"overlapping" })
		public static IList<IAgent> agents_inside(final IScope scope, final Object source) {
			return _gather(scope, Different.with(), source, true);
		}

		@operator(value = "agents_overlapping", content_type = IType.AGENT, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_QUERIES })
		@doc(value = "A list of agents overlapping the operand (casted as a geometry).",
		examples = { @example(value = "agents_overlapping(self)",
				equals = "the agents that overlap the shape of the agent applying the operator.",
				test = false) }, see = { "neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside",
			"closest_to", "inside", "overlapping", "at_distance" })
		public static IList<IAgent> agents_overlapping(final IScope scope, final Object source) {
			return _gather(scope, Different.with(), source, false);
		}

		@operator(value = "agents_at_distance", content_type = IType.AGENT, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_QUERIES })
		@doc(value = "A list of agents situated at a distance lower than the right argument.",
		examples = { @example(value = "agents_at_distance(20)",
				equals = "all the agents (excluding the caller) which distance to the caller is lower than 20",
				test = false) }, see = { "neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside",
			"closest_to", "inside", "overlapping", "at_distance" })
		public static IList agents_at_distance(final IScope scope, final Double distance) {
			return _neighbours(scope, Different.with(), scope.getAgentScope(), distance);
		}

		// Support methods used by the different queries

		private static IList<IAgent> _gather(final IScope scope, final IAgentFilter filter, final Object source,
			final boolean inside) {
			if ( filter == null || source == null ) { return GamaListFactory.EMPTY_LIST; }
			IType type =
				filter.getSpecies() == null ? Types.AGENT : scope.getModelContext().getTypeNamed(
					filter.getSpecies().getName());
			return GamaListFactory.createWithoutCasting(type,
				scope.getTopology().getAgentsIn(scope, Cast.asGeometry(scope, source, false), filter, inside));
		}

		private static IAgent _closest(final IScope scope, final IAgentFilter filter, final Object source) {
			if ( filter == null || source == null ) { return null; }
			return scope.getTopology().getAgentClosestTo(scope, Cast.asGeometry(scope, source, false), filter);
		}

		private static IList<IAgent> _neighbours(final IScope scope, final IAgentFilter filter, final Object source,
			final Object distance) {
			return _neighbours(scope, filter, source, distance, scope.getTopology());
		}

		static IList<IAgent> _neighbours(final IScope scope, final IAgentFilter filter, final Object source,
			final Object distance, final ITopology t) {
			if ( filter == null || source == null ) { return GamaListFactory.EMPTY_LIST; }
			IType type =
				filter.getSpecies() == null ? Types.AGENT : scope.getModelContext().getTypeNamed(
					filter.getSpecies().getName());
			return GamaListFactory.createWithoutCasting(type,
				t.getNeighboursOf(scope, Cast.asGeometry(scope, source, false), Cast.asFloat(scope, distance), filter));
		}

	}

	public static abstract class Statistics {

		@operator(value = { "simple_clustering_by_distance", "simple_clustering_by_envelope_distance" },
			content_type = IType.LIST,
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_STATISTICAL, IOperatorCategory.STATISTICAL })
		@doc(value = "A list of agent groups clustered by distance considering a distance min between two groups.",
		examples = { @example(value = "[ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0",
				equals = "for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]",
				isExecutable = false) }, see = { "hierarchical_clustering" })
		public static IList<IList<IAgent>> simpleClusteringByDistance(final IScope scope,
			final IContainer<?, IAgent> agents, final Double distance) {
			final IList<IList<IAgent>> groups =
				GamaListFactory.create(Types.LIST.of(agents.getType().getContentType()));
			IAgentFilter filter = In.list(scope, agents);
			if ( filter == null ) { return groups; }
			Set<IAgent> clusteredCells = new THashSet<IAgent>();
			for ( final IAgent ag : agents.iterable(scope) ) {
				if ( !clusteredCells.contains(ag) ) {
					groups.add(simpleClusteringByDistanceRec(scope, filter, distance, clusteredCells, ag));
				}
			}
			return groups;
		}

		public static IList<IAgent> simpleClusteringByDistanceRec(final IScope scope, final IAgentFilter filter,
			final Double distance, final Set<IAgent> clusteredAgs, final IAgent currentAg) {
			IList<IAgent> group = GamaListFactory.create(Types.AGENT);
			List<IAgent> ags =
				new ArrayList<IAgent>(scope.getTopology().getNeighboursOf(scope, currentAg, distance, filter));
			clusteredAgs.add(currentAg);
			group.add(currentAg);
			for ( IAgent ag : ags ) {
				if ( !clusteredAgs.contains(ag) ) {
					group.addAll(simpleClusteringByDistanceRec(scope, filter, distance, clusteredAgs, ag));
				}
			}
			return group;
		}

		@operator(value = { "hierarchical_clustering" }, content_type = IType.LIST, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.SP_STATISTICAL, IOperatorCategory.STATISTICAL })
		@doc(value = "A tree (list of list) contained groups of agents clustered by distance considering a distance min between two groups.",
		comment = "use of hierarchical clustering with Minimum for linkage criterion between two groups of agents.",
		examples = { @example(value = "[ag1, ag2, ag3, ag4, ag5] hierarchical_clustering 20.0",
				equals = "for example, can return [[[ag1],[ag3]], [ag2], [[[ag4],[ag5]],[ag6]]",
				isExecutable = false) }, see = { "simple_clustering_by_distance" })
		public static
		IList
		hierarchicalClusteringe(final IScope scope, final IContainer<?, IAgent> agents, final Double distance) {
			final int nb = agents.length(scope);
			final IList<IList> groups = GamaListFactory.create(Types.LIST.of(Types.LIST));

			if ( nb == 0 ) {
				// scope.setStatus(ExecutionStatus.failure);
				return groups;
			}
			double distMin = Double.MAX_VALUE;

			Set<IList> minFusion = null;

			final Map<Set<IList>, Double> distances = new HashMap<Set<IList>, Double>();
			for ( final IAgent ag : agents.iterable(scope) ) {
				final IList group = GamaListFactory.create(Types.AGENT);
				group.add(ag);
				groups.add(group);
			}

			if ( nb == 1 ) { return groups; }
			// BY GEOMETRIES
			for ( int i = 0; i < nb - 1; i++ ) {
				final IList g1 = groups.get(i);
				for ( int j = i + 1; j < nb; j++ ) {
					final IList g2 = groups.get(j);
					final Set<IList> distGp = new THashSet<IList>();
					distGp.add(g1);
					distGp.add(g2);
					final IAgent a = (IAgent) g1.get(0);
					final IAgent b = (IAgent) g2.get(0);
					final Double dist = scope.getTopology().distanceBetween(scope, a, b);
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
				IList<IList> fusionL = GamaListFactory.create(scope, Types.LIST.of(Types.LIST), minFusion);
				final IList<IAgent> g1 = fusionL.get(0);
				final IList<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				IList groupeF = GamaListFactory.create(Types.LIST.of(Types.AGENT));
				groupeF.add(g2);
				groupeF.add(g1);

				for ( final IList groupe : groups ) {
					final Set<IList> newDistGp = new THashSet<IList>();
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
					final double dist = Math.min(dist1, dist2);
					if ( dist <= distance ) {
						newDistGp.remove(g2);
						newDistGp.add(groupeF);
						distances.put(newDistGp, Double.valueOf(dist));
					}

				}
				groups.add(groupeF);

				distMin = Double.MAX_VALUE;
				minFusion = null;
				for ( final Set<IList> distGp : distances.keySet() ) {
					final double dist = distances.get(distGp).doubleValue();
					if ( dist < distMin ) {
						minFusion = distGp;
						distMin = dist;
					}
				}
			}
			return groups;
		}

		@operator(value = { "IDW", "inverse_distance_weighting" }, category = { IOperatorCategory.SPATIAL })
		@doc(value = "Inverse Distance Weighting (IDW) is a type of deterministic method for multivariate "
			+ "interpolation with a known scattered set of points. The assigned values to each geometry are calculated with a weighted average of the values available at the known points. See: http://en.wikipedia.org/wiki/Inverse_distance_weighting "
			+ "Usage: IDW (list of geometries, map of points (key: point, value: value), power parameter)",
			examples = { @example(value = "IDW([ag1, ag2, ag3, ag4, ag5],[{10,10}::25.0, {10,80}::10.0, {100,10}::15.0], 2)",
			equals = "for example, can return [ag1::12.0, ag2::23.0,ag3::12.0,ag4::14.0,ag5::17.0]",
			isExecutable = false) })
		public static
		GamaMap<IShape, Double> primIDW(final IScope scope, final IContainer<?, ? extends IShape> geometries,
			final GamaMap<GamaPoint, Double> points, final int power) {
			GamaMap<IShape, Double> results = GamaMapFactory.create(Types.GEOMETRY, Types.FLOAT);
			if ( points == null || points.isEmpty() ) { return null; }
			if ( geometries == null || geometries.isEmpty(scope) ) { return results; }
			for ( IShape geom : geometries.iterable(scope) ) {
				double sum = 0;
				double weight = 0;
				double sumNull = 0;
				int nbNull = 0;
				for ( GamaPoint pt : points.keySet() ) {
					double dist = scope.getTopology().distanceBetween(scope, geom, pt);
					if ( dist == 0 ) {
						nbNull++;
						sumNull += points.get(pt);
					}
					if ( nbNull == 0 ) {
						double w = 1 / Math.pow(dist, power);
						weight += w;
						sum += w * points.get(pt);
					}
				}
				if ( nbNull > 0 ) {
					results.put(geom, sumNull / nbNull);
				} else {
					results.put(geom, sum / weight);
				}

			}
			return results;
		}

	}

	public static abstract class ThreeD {

		@operator(value = { "set_z" }, category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
		@doc(value = "Sets the z ordinate of the n-th point of a geometry to the value provided by the third argument",
		masterDoc = true,
		examples = { @example("loop i from: 0 to: length(shape.points) - 1{"
			+ "set shape <-  set_z (shape, i, 3.0);" + "}") },
			see = { "add_z" })
		public static IShape set_z(final IScope scope, final IShape geom, final Integer index, final Double z) {
			if ( geom == null ) { return null; }
			Geometry g = geom.getInnerGeometry();
			if ( g == null ) { return geom; }
			if ( index < 0 || index > g.getNumPoints() - 1 ) { throw GamaRuntimeException.warning(
				"Trying to modify a point outside the bounds of the geometry", scope); }
			g.apply(new CoordinateSequenceFilter() {

				boolean done = false;

				@Override
				public void filter(final CoordinateSequence seq, final int i) {
					if ( i == index ) {
						seq.getCoordinate(i).setOrdinate(2, z);
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

		@operator(value = { "set_z" }, category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
		@doc(value = "Sets the z ordinate of each point of a geometry to the value provided, in order, by the right argument",
		examples = { @example("shape <- triangle(3) set_z [5,10,14];") },
		see = { "add_z" })
		public static
		IShape set_z(final IScope scope, final IShape geom, final IContainer<?, Double> coords) {
			if ( geom == null ) { return null; }
			Geometry g = geom.getInnerGeometry();
			if ( g == null ) { return geom; }
			if ( coords == null || coords.isEmpty(scope) ) { return null; }
			if ( coords.length(scope) > g.getNumPoints() ) { throw GamaRuntimeException.warning(
				"Trying to modify a point outside the bounds of the geometry", scope); }
			final Double[] zs = (Double[]) coords.listValue(scope, Types.FLOAT, false).toArray(new Double[0]);
			g.apply(new CoordinateSequenceFilter() {

				@Override
				public void filter(final CoordinateSequence seq, final int i) {
					if ( i <= zs.length - 1 ) {
						seq.getCoordinate(i).z = zs[i];
					}
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

		@operator(value = "dem", category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
		@doc(value = "A polygon that is equivalent to the surface of the texture",
		masterDoc = true,
		usages = { @usage("a point if the operand is lower or equal to 0.") },
		comment = "",
		examples = { @example(value = "dem(dem)",
				equals = "returns a geometry as a rectangle of weight and height equal to the texture.",
				isExecutable = false) }, see = {})
		public static IShape dem(final IScope scope, final GamaFile demFileName) {
			return dem(scope, demFileName, demFileName, 1.0);
		}

		@operator(value = "dem", category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
		@doc(value = "A polygon equivalent to the surface of the texture",
		examples = { @example(value = "dem(dem,texture)",
				equals = "a geometry as a rectangle of weight and height equal to the texture.",
				isExecutable = false) }, see = {})
		public static IShape dem(final IScope scope, final GamaFile demFile, final GamaFile textureFile) {
			return dem(scope, demFile, textureFile, 1.0);
		}

		@operator(value = "dem", category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
		@doc(value = "A polygon that equivalent to the surface of the texture",
		examples = { @example(value = "dem(dem,z_factor)",
				equals = "a geometry as a rectangle of weight and height equal to the texture.",
				isExecutable = false) }, see = {})
		public static IShape dem(final IScope scope, final GamaFile demFileName, final Double z_factor) {
			return dem(scope, demFileName, demFileName, z_factor);
		}

		@operator(value = "dem", category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
		@doc(value = "A polygon equivalent to the surface of the texture",
		examples = { @example(value = "dem(dem,texture,z_factor)",
				equals = "a geometry as a rectangle of weight and height equal to the texture.",
				isExecutable = false) }, see = {})
		public static IShape dem(final IScope scope, final GamaFile demFile, final GamaFile textureFile,
			final Double z_factor) {
			if ( !(demFile instanceof GamaImageFile) || !(textureFile instanceof GamaImageFile) ) { throw GamaRuntimeException
				.error(
					"The 'dem' operator requires image files. Either " + demFile.getPath() + " or " +
						textureFile.getPath() + " is not an image", scope); }
			final IGraphics graphics = scope.getGraphics();
			if ( graphics instanceof IGraphics.OpenGL ) {
				BufferedImage dem = ((GamaImageFile) demFile).getImage(scope);
				BufferedImage texture = ((GamaImageFile) textureFile).getImage(scope);
				texture = ImageUtils.flipRightSideLeftImage(texture);
				((IGraphics.OpenGL) graphics).drawDEM(scope, dem, texture, z_factor);
			}
			// ILocation location;
			// final IAgent a = scope.getAgentScope();
			// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return null;// new GamaShape(scope.getSimulationScope().getInnerGeometry());
		}

		@operator(value = "rgb_to_xyz", type = IType.LIST, content_type = IType.POINT, category = {
			IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
		@doc(value = "A list of point corresponding to RGB value of an image (r:x , g:y, b:z)",
		examples = { @example(value = "rgb_to_xyz(texture)", equals = "a list of points", isExecutable = false) },
		see = {})
		public static IList<ILocation> rgb_to_xyz(final IScope scope, final GamaFile textureFileName) {

			final IList<ILocation> points = GamaListFactory.create(Types.POINT);
			BufferedImage texture = null;
			int rows, cols, x, y;

			try {
				texture = ImageUtils.getInstance().getImageFromFile(scope, textureFileName.getPath());
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if ( texture == null ) { return points; }
			rows = texture.getHeight() - 1;
			cols = texture.getWidth() - 1;

			for ( y = 0; y < rows; y++ ) {
				for ( x = 0; x <= cols; x++ ) {
					Color c = new Color(texture.getRGB(cols - x, y));
					points.add(new GamaPoint(c.getRed(), c.getGreen(), c.getBlue()));
				}
			}

			return points;
		}
	}

	public static abstract class Projections {

		@operator(value = "crs", category = { IOperatorCategory.SPATIAL, IOperatorCategory.FILE })
		@doc(value = "the Coordinate Reference System (CRS) of the GIS file",
		examples = { @example(value = "crs(my_shapefile)",
				equals = "the crs of the shapefile",
				isExecutable = false) }, see = {})
		public static String crsFromFile(final IScope scope, final GamaFile gisFile) {
			if ( gisFile instanceof GamaGisFile ) {
				CoordinateReferenceSystem crs = ((GamaGisFile) gisFile).getGis(scope).getInitialCRS();
				if ( crs == null ) { return null; }
				try {
					return CRS.lookupIdentifier(crs, true).toString();
				} catch (FactoryException e) {
					return null;
				} catch (NullPointerException e) {
					return null;
				}
			} else {
				throw GamaRuntimeException.error("Impossible to compute the CRS for this type of file");
			}
		}

		@operator(value = { "CRS_transform" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry by the current CRS (Coordinate Reference System), the one corresponding to the world's agent one",
		examples = { @example(value = "CRS_transform(shape)",
				equals = "a geometry corresponding to the agent geometry transformed into the current CRS",
				test = false) }) })
		public static
		IShape transform_CRS(final IScope scope, final IShape g) {
			IProjection gis = scope.getSimulationScope().getProjectionFactory().getWorld();
			if ( gis == null ) { return g.copy(scope); }
			IShape s = new GamaShape(gis.inverseTransform(g.getInnerGeometry()));
			if ( g instanceof ILocation ) { return s.getLocation(); }
			return s;
		}

		@operator(value = { "to_GAMA_CRS" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry to the GAMA CRS (Coordinate Reference System) assuming the given geometry is referenced by the current CRS, the one corresponding to the world's agent one",
		examples = { @example(value = "to_GAMA_CRS({121,14})",
				equals = "a geometry corresponding to the agent geometry transformed into the GAMA CRS",
				test = false) }) })
		public static
		IShape to_GAMA_CRS(final IScope scope, final IShape g) {
			IProjection gis = scope.getSimulationScope().getProjectionFactory().getWorld();
			if ( gis == null ) { return g.copy(scope); }
			IShape s = new GamaShape(gis.transform(g.getInnerGeometry()));
			if ( g instanceof ILocation ) { return s.getLocation(); }
			return s;
		}

		@operator(value = { "to_GAMA_CRS" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry to the GAMA CRS (Coordinate Reference System) assuming the given geometry is referenced by given CRS",
		examples = { @example(value = "to_GAMA_CRS({121,14}, \"EPSG:4326\")",
				equals = "a geometry corresponding to the agent geometry transformed into the GAMA CRS",
				test = false) }) })
		public static
		IShape to_GAMA_CRS(final IScope scope, final IShape g, final String code) {
			IProjection gis;
			try {
				gis = scope.getSimulationScope().getProjectionFactory().forSavingWith(code);
			} catch (FactoryException e) {
				throw GamaRuntimeException.error("The code " + code + " does not correspond to a known EPSG code",
					scope);
			}
			if ( gis == null ) { return g.copy(scope); }
			IShape s = new GamaShape(gis.transform(g.getInnerGeometry()));
			if ( g instanceof ILocation ) { return s.getLocation(); }
			return s;
		}

		@operator(value = { "CRS_transform" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry by the left operand CRS (Coordinate Reference System)",
		examples = { @example(value = "shape CRS_transform(\"EPSG:4326\")",
				equals = "a geometry corresponding to the agent geometry transformed into the EPSG:4326 CRS",
				test = false) }) })
		public static
		IShape transform_CRS(final IScope scope, final IShape g, final String code) {
			IProjection gis;
			try {
				gis = scope.getSimulationScope().getProjectionFactory().forSavingWith(code);
			} catch (FactoryException e) {
				throw GamaRuntimeException.error("The code " + code + " does not correspond to a known EPSG code",
					scope);
			}
			if ( gis == null ) { return g.copy(scope); }
			IShape s = new GamaShape(gis.inverseTransform(g.getInnerGeometry()));
			if ( g instanceof ILocation ) { return s.getLocation(); }
			return s;
		}
	}
}
