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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.util.*;
import msi.gama.common.interfaces.*;
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
import msi.gama.util.file.GamaFile;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
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
	 * @author Alexis Drogoul, Patrick Taillandier
	 * @since 29 nov. 2011
	 * 
	 */

	final static PointLocator pl = new PointLocator();

	public static class Common extends Object {}

	public static abstract class Creation {

		@operator("circle")
		@doc(value = "A circle geometry which radius is equal to the operand.", special_cases = { "returns a point if the operand is lower or equal to 0." }, comment = "the centre of the circle is by default the location of the current agent in which has been called this operator.", examples = { "circle(10) --: returns a geometry as a circle of radius 10." }, see = {
			"around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle" })
		public static IShape circle(final IScope scope, final Double radius) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCircle(radius, location);
		}

		@operator("cylinder")
		@doc(value = "A cylinder geometry which radius is equal to the operand.", special_cases = { "returns a point if the operand is lower or equal to 0." }, comment = "the centre of the cylinder is by default the location of the current agent in which has been called this operator.", examples = { "cylinder(10,10) --: returns a geometry as a circle of radius 10." }, see = {
			"around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle" })
		public static IShape cylinder(final IScope scope, final Double radius, final Double depth) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCylinder(radius, depth, location);
		}

		@operator("sphere")
		@doc(value = "A sphere geometry which radius is equal to the operand.", special_cases = { "returns a point if the operand is lower or equal to 0." }, comment = "the centre of the sphere is by default the location of the current agent in which has been called this operator.", examples = { "sphere(10) --: returns a geometry as a circle of radius 10 but displays ." }, see = {
			"around", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle" })
		public static IShape sphere(final IScope scope, final Double radius) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( radius <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSphere(radius, location);
		}

		@operator("cone")
		@doc(value = "A cone geometry which min and max angles are given by the operands.", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the cone is by default the location of the current agent in which has been called this operator.", examples = { "cone({0, 45}) --: returns a geometry as a cone with min angle is 0 and max angle is 45." }, see = {
			"around", "circle", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square",
			"triangle" })
		public static IShape cone(final IScope scope, final GamaPoint p) {
			if ( p == null ) { return null; }

			final int min_angle = Maths.checkHeading((int) p.x);
			final int max_angle = Maths.checkHeading((int) p.y);
			final IAgent a = scope.getAgentScope();
			final ILocation origin = a.getLocation() == null ? new GamaPoint(0, 0) : a.getLocation();
			final double originx = origin.getX();
			final double originy = origin.getY();
			final double worldWidth = scope.getTopology().getWidth() - originx;
			final double worldHeight = scope.getTopology().getHeight() - originy;

			final double min_point_x = originx + Maths.cos(min_angle) * worldWidth;
			final double min_point_y = originy + Maths.sin(min_angle) * worldHeight;
			final ILocation minPoint = new GamaPoint(min_point_x, min_point_y);

			final double max_point_x = originx + Maths.cos(max_angle) * worldWidth;
			final double max_point_y = originy + Maths.sin(max_angle) * worldHeight;
			final ILocation maxPoint = new GamaPoint(max_point_x, max_point_y);

			return polygon(scope, GamaList.with(origin, minPoint, maxPoint));
		}

		@operator("square")
		@doc(value = "A square geometry which side size is equal to the operand.", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the square is by default the location of the current agent in which has been called this operator.", examples = { "square(10) --: returns a geometry as a square of side size 10." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "triangle" })
		public static IShape square(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildSquare(side_size, location);
		}

		@operator("cube")
		@doc(value = "A cube geometry which side size is equal to the operand.", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the cube is by default the location of the current agent in which has been called this operator.", examples = { "cube(10) --: returns a geometry as a square of side size 10." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "triangle" })
		public static IShape cube(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildCube(side_size, location);
		}

		@operator("rectangle")
		@doc(value = "A rectangle geometry which side sizes are given by the operands.", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the rectangle is by default the location of the current agent in which has been called this operator.", examples = { "rectangle({10, 5}) --: returns a geometry as a rectangle with width = 10 and heigh = 5." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "square", "triangle" })
		public static IShape rectangle(final IScope scope, final GamaPoint p) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(p.x, p.y, location);
		}

		@operator("rectangle")
		@doc(value = "A rectangle geometry which side sizes are given by the operands.", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the rectangle is by default the location of the current agent in which has been called this operator.", examples = { "rectangle(10, 5) --: returns a geometry as a rectangle with width = 10 and heigh = 5." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "square", "triangle" })
		public static IShape rectangle(final IScope scope, final double x, final double y) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildRectangle(x, y, location);
		}

		@operator("box")
		@doc(value = "A box geometry which side sizes are given by the operands.", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the box is by default the location of the current agent in which has been called this operator.", examples = { "box({10, 5 , 5}) --: returns a geometry as a rectangle with width = 10, heigh = 5 depth= 5." }, see = {
			"around", "circle", "sphere", "cone", "line", "link", "norm", "point", "polygon", "polyline", "square",
			"cube", "triangle" })
		public static IShape box(final IScope scope, final GamaPoint p) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return GamaGeometryType.buildBox(p.x, p.y, p.z, location);
		}

		@operator("triangle")
		@doc(value = "A triangle geometry which side size is given by the operand.", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the triangle is by default the location of the current agent in which has been called this operator.", examples = { "triangle(5) --: returns a geometry as a triangle with side_size = 5." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square" })
		public static IShape triangle(final IScope scope, final Double side_size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( side_size <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildTriangle(side_size, location);
		}

		@operator("hexagon")
		@doc(value = "A hexagon geometry which the given with and height", special_cases = { "returns nil if the operand is nil." }, comment = "the centre of the hexagon is by default the location of the current agent in which has been called this operator.", examples = { "hexagon({10,5}) --: returns a geometry as a hexagon of width of 10 and height of 5." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "triangle" })
		public static IShape hexagon(final IScope scope, final GamaPoint size) {
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			final Double width = size.x;
			final Double height = size.y;
			if ( width <= 0 || height <= 0 ) { return new GamaShape(location); }
			return GamaGeometryType.buildHexagon(width, height, location);
		}

		@operator(value = "polygon", expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT })
		@doc(value = "A polygon geometry from the given list of points.", special_cases = {
			"if the operand is nil, returns the point geometry {0,0}",
			"" + "if the operand is composed of a single point, returns a point geometry",
			"if the operand is composed of 2 points, returns a polyline geometry." }, examples = { "polygon([{0,0}, {0,10}, {10,10}, {10,0}]) --: returns a polygon geometry composed of the 4 points." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polyline", "rectangle", "square", "triangle" })
		public static IShape polygon(final IScope scope, final IContainer<?, IShape> points) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = points.listValue(scope);
			final int size = shapes.length(scope);
			final IShape first = shapes.first(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			if ( size == 2 ) { return GamaGeometryType.buildLine(first, shapes.last(scope)); }
			if ( !first.equals(shapes.last(scope)) ) {
				shapes.add(first);
			}
			return GamaGeometryType.buildPolygon(shapes);
		}

		@operator(value = "polyhedron", expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT })
		@doc(value = "A polyhedron geometry from the given list of points.", special_cases = {
			"if the operand is nil, returns the point geometry {0,0}",
			"" + "if the operand is composed of a single point, returns a point geometry",
			"if the operand is composed of 2 points, returns a polyline geometry." }, examples = { "polyhedron([{0,0}, {0,10}, {10,10}, {10,0}],10) --: returns a polygon geometry composed of the 4 points and of depth 10." }, see = {
			"around", "circle", "cone", "line", "link", "norm", "point", "polyline", "rectangle", "square", "triangle" })
		public static IShape polyhedron(final IScope scope, final IContainer<?, IShape> points, final Double depth) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = points.listValue(scope);
			final int size = shapes.length(scope);
			final IShape first = shapes.first(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			final IShape last = shapes.last(scope);
			if ( size == 2 ) { return GamaGeometryType.buildLine(first, last); }
			if ( !first.equals(last) ) {
				shapes.add(first);
			}
			return GamaGeometryType.buildPolyhedron(shapes, depth);
		}

		@operator(value = { "line", "polyline" }, expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT })
		@doc(value = "A polyline geometry from the given list of points.", special_cases = {
			"if the operand is nil, returns the point geometry {0,0}",
			"if the operand is composed of a single point, returns a point geometry." }, examples = { "polyline([{0,0}, {0,10}, {10,10}, {10,0}]) --: returns a polyline geometry composed of the 4 points." }, see = {
			"around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square", "triangle" })
		public static IShape line(final IScope scope, final IContainer<?, IShape> points) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = points.listValue(scope);
			final int size = shapes.length(scope);
			final IShape first = shapes.first(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			if ( size == 2 ) { return GamaGeometryType.buildLine(first, points.last(scope)); }
			return GamaGeometryType.buildPolyline(shapes);
		}

		@operator(value = { "plan", "polyplan" }, expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT })
		@doc(value = "A polyline geometry from the given list of points.", special_cases = {
			"if the operand is nil, returns the point geometry {0,0}",
			"if the operand is composed of a single point, returns a point geometry." }, examples = { "polyplan([{0,0}, {0,10}, {10,10}, {10,0}],10) --: returns a polyline geometry composed of the 4 points with a depth of 10." }, see = {
			"around", "circle", "cone", "link", "norm", "point", "polygone", "rectangle", "square", "triangle" })
		public static IShape plan(final IScope scope, final IContainer<?, IShape> points, final Double depth) {
			if ( points == null || points.isEmpty(scope) ) { return new GamaShape(new GamaPoint(0, 0)); }
			final IList<IShape> shapes = points.listValue(scope);
			final int size = shapes.length(scope);
			final IShape first = shapes.first(scope);
			if ( size == 1 ) { return GamaGeometryType.createPoint(first); }
			if ( size == 2 ) { return GamaGeometryType.buildPlan(first, shapes.last(scope), depth); }
			return GamaGeometryType.buildPolyplan(shapes, depth);
		}

		@operator({ "link" })
		@doc(value = "A link between the 2 elements of the pair.", special_cases = {
			"if the operand is nil, link returns a point {0,0}",
			"if one of the elements of the pair is a list of geometries or a species, link will consider the union of the geometries or of the geometry of each agent of the species" }, comment = "The geometry of the link is the intersection of the two geometries when they intersect, and a line between their centroids when they do not.", examples = { "link (geom1::geom2)  --: returns a link geometry between geom1 and geom2." }, see = {
			"around", "circle", "cone", "line", "norm", "point", "polygon", "polyline", "rectangle", "square",
			"triangle" })
		public static IShape link(final IScope scope, final GamaPair points) throws GamaRuntimeException {
			if ( points == null || points.first() == null && points.last() == null ) { return new GamaShape(
				new GamaPoint(0, 0)); }
			return GamaGeometryType.pairToGeometry(scope, points);
		}

		@operator("around")
		@doc(value = "A geometry resulting from the difference between a buffer around the right-operand casted in geometry at a distance left-operand (right-operand buffer left-operand) and the right-operand casted as geometry.", special_cases = { "returns a circle geometry of radius right-operand if the left-operand is nil" }, examples = { "10 around circle(5) --: returns a the ring geometry between 5 and 10." }, see = {
			"circle", "cone", "line", "link", "norm", "point", "polygon", "polyline", "rectangle", "square", "triangle" })
		public static IShape around(final IScope scope, final Double width, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			final IShape g = Cast.asGeometry(scope, toBeCastedIntoGeometry);
			if ( g == null ) { return circle(scope, width); }
			return Operators.minus(Transformations.enlarged_by(g, width), g);
		}

		@operator("envelope")
		@doc(value = "A rectangular 2D geometry that represents the rectangle that surrounds the geometries or the surface described by the arguments. More general than geometry(arguments).envelope, as it allows to pass int, double, point, image files, shape files, asc files, or any list combining these arguments, in which case the envelope will be correctly expanded. If an envelope cannot be determined from the arguments, a default one of dimensions (0,0,100,100) is returned")
		public static IShape envelope(final IScope scope, final Object obj) {

			Envelope env = GeometryUtils.computeEnvelopeFrom(scope, obj);
			if ( env == null ) {
				env = new Envelope(0, 100, 0, 100);
			}
			// TODO Not sure it is correct
			final IShape shape =
				GamaGeometryType.buildRectangle(env.getWidth(), env.getHeight(),
					new GamaPoint(env.getMinX() + env.getWidth() / 2, env.getMinY() + env.getHeight() / 2));
			return shape;
		}
	}

	public static abstract class Operators {

		@operator(value = { "inter", "intersection" })
		@doc(value = "A geometry resulting from the intersection between the two geometries", special_cases = { "returns false if the right-operand is nil" }, examples = { "square(5) intersects {10,10} --: false" }, see = {
			"union", "+", "-" })
		public static IShape inter(final IShape g1, final IShape g2) {
			if ( g2 == null || g1 == null ) { return null; }
			if ( g2.isPoint() && g1.covers(g2.getLocation()) ) { return new GamaShape(g2); }
			if ( g1.isPoint() && g2.covers(g1.getLocation()) ) { return new GamaShape(g1); }
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
					geom = geom1.buffer(0.01).intersection(geom2.buffer(0.01));
				}
			}
			if ( geom == null || geom.isEmpty() ) { return null; }
			return new GamaShape(geom);
		}

		@operator(value = { "+", "union" })
		@doc(special_cases = { "if the right-operand is a point, a geometry or an agent, returns the geometry resulting from the union between both geometries" }, examples = { "geom1 + geom2 --: a geometry corresponding to union between geom1 and geom2" })
		public static IShape union(final IShape g1, final IShape g2) {
			if ( g1 == null ) {
				if ( g2 == null ) { return null; }
				return g2;
			}
			if ( g2 == null ) { return g1; }
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
					geom = geom1.buffer(0.01).union(geom2.buffer(0.01));
				}

			}
			if ( geom == null || geom.isEmpty() ) { return null; }
			return new GamaShape(geom);
		}

		@operator(value = { "union" }, expected_content_type = { IType.POINT, IType.GEOMETRY, IType.AGENT })
		@doc(special_cases = { "if the right-operand is a container of points, geometries or agents, returns the geometry resulting from the union all the geometries" }, examples = { "union([geom1, geom2, geom3]) --: a geometry corresponding to union between geom1, geom2 and geom3" })
		public static IShape union(final IScope scope, final IContainer<?, IShape> elements) {
			try {
				return Cast.asGeometry(scope, elements);
			} catch (final GamaRuntimeException e) {
				return null;
			}
		}

		@operator(IKeyword.MINUS)
		@doc(special_cases = { "if the right-operand is a point, a geometry or an agent, returns the geometry resulting from the difference between both geometries" }, examples = { "geom1 - geom2 --: a geometry corresponding to difference between geom1 and geom2" })
		public static IShape minus(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null || g1.getInnerGeometry() == null || g2.getInnerGeometry() == null ) { return g1; }
			Geometry res = difference(g1.getInnerGeometry(), g2.getInnerGeometry());
			if ( res != null ) { return new GamaShape(res); }
			return null;
		}

		@operator(IKeyword.MINUS)
		@doc(special_cases = { "if the right-operand is a list of points, geometries or agents, returns the geometry resulting from the difference between the left-geometry and all of the right-geometries" }, examples = { "geom1 - [geom2, geom3, geom4] --: a geometry corresponding to geom1 - (geom2 + geom3 + geom4)" })
		public static IShape minus(final IScope scope, final IShape g1, final IContainer<?, IShape> agents) {
			if ( g1 == null || agents == null || g1.getInnerGeometry() == null || agents.isEmpty(scope) ) { return g1; }
			Geometry geom1 = GeometryUtils.factory.createGeometry(g1.getInnerGeometry());
			for ( final IShape ag : agents ) {
				if ( ag != null && ag.getInnerGeometry() != null ) {
					geom1 = difference(geom1, ag.getInnerGeometry());
					if ( geom1 == null || geom1.isEmpty() ) { return null; }
				}
			}
			if ( geom1 == null ) { return null; }
			return new GamaShape(geom1);
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
					return g1.difference(g2.buffer(0.01));
				}
			}
		}

		@operator(value = { "add_point" })
		@doc(value = "A geometry resulting from the adding of a right-point (coordinate) to the right-geometry", examples = { "square(5) add_point {10,10} --: returns a hexagon" })
		public static IShape add_point(final IShape g, final ILocation p) {
			if ( p == null ) { return g; }
			final Coordinate point = (Coordinate) p;
			final Geometry geometry = g.getInnerGeometry();
			Geometry geom_Tmp = null;
			final int nb = geometry.getCoordinates().length;
			final Coordinate[] coord = new Coordinate[nb + 1];
			if ( geometry instanceof Point || geometry instanceof MultiPoint ) {
				coord[0] = geometry.getCoordinate();
				coord[1] = point;
				geom_Tmp = GeometryUtils.factory.createLineString(coord);
			} else if ( geometry instanceof LineString || geometry instanceof MultiLineString ) {
				for ( int i = 0; i < nb; i++ ) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[nb] = point;
				geom_Tmp = GeometryUtils.factory.createLineString(coord);
			} else if ( geometry instanceof Polygon || geometry instanceof MultiPolygon ) {
				for ( int i = 0; i < nb - 1; i++ ) {
					coord[i] = geometry.getCoordinates()[i];
				}
				coord[nb - 1] = point;
				coord[nb] = geometry.getCoordinates()[nb - 1];
				final LinearRing ring = GeometryUtils.factory.createLinearRing(coord);
				geom_Tmp = GeometryUtils.factory.createPolygon(ring, null);
			}
			if ( geom_Tmp != null && geom_Tmp.isValid() ) { return new GamaShape(geom_Tmp);

			}
			return g;
		}

		@operator("masked_by")
		@doc(examples = { "perception_geom masked_by obstacle_list --: returns the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list." })
		public static IShape masked_by(final IScope scope, final IShape source, final IContainer<?, IAgent> obstacles,
			Double precision) {
			final IAgent a = scope.getAgentScope();
			final ILocation location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			if ( precision == null ) {
				precision = 120.0;
			}
			final Geometry visiblePercept = GeometryUtils.factory.createGeometry(source.getInnerGeometry());
			if ( obstacles != null && !obstacles.isEmpty(scope) ) {
				final Envelope env = visiblePercept.getEnvelopeInternal();
				final double percep_dist = Math.max(env.getHeight(), env.getWidth());
				final Geometry locG =
					GeometryUtils.factory.createPoint(location.toCoordinate()).buffer(0.01).getEnvelope();

				final IList<Geometry> geoms = new GamaList<Geometry>();
				Coordinate prec = new Coordinate(location.getX() + percep_dist, location.getY());
				for ( int k = 1; k <= precision; k++ ) {
					final double angle = k / precision * 2 * Math.PI;
					Coordinate next = null;
					if ( k < precision ) {
						next =
							new Coordinate(location.getX() + Math.cos(angle) * percep_dist, location.getY() +
								Math.sin(angle) * percep_dist);
					} else {
						next = new Coordinate(location.getX() + percep_dist, location.getY());
					}
					final Coordinate[] coordinates = new Coordinate[4];
					coordinates[0] = location.toCoordinate();
					coordinates[1] = prec;
					coordinates[2] = next;
					coordinates[3] = location.toCoordinate();
					final LinearRing closeRing = GeometryUtils.factory.createLinearRing(coordinates);
					geoms.add(source.getGeometry().getInnerGeometry()
						.intersection(GeometryUtils.factory.createPolygon(closeRing, null)));
					prec = next;
				}
				final IList<Geometry> geomsVisible = new GamaList<Geometry>();
				final Geometry geomObsts[] = new Geometry[obstacles.length(scope)];
				int i = 0;
				for ( final IShape shape : obstacles ) {
					geomObsts[i++] = shape.getInnerGeometry();
				}

				final Geometry obstaclesGeom = GeometryUtils.factory.createGeometryCollection(geomObsts);
				obstaclesGeom.union();
				for ( final Geometry geom : geoms ) {
					if ( !obstaclesGeom.intersects(geom) ) {
						geomsVisible.add(geom);
					} else {
						Geometry perceptReal = geom.difference(obstaclesGeom);
						final PreparedGeometry ref = PreparedGeometryFactory.prepare(locG);
						if ( perceptReal instanceof GeometryCollection ) {
							final GeometryCollection gc = (GeometryCollection) perceptReal;
							perceptReal = null;
							final int nb = gc.getNumGeometries();
							for ( int i1 = 0; i1 < nb; i1++ ) {
								if ( !ref.disjoint(gc.getGeometryN(i1)) ) {
									perceptReal = gc.getGeometryN(i1);
									break;
								}
							}
						} else if ( ref.disjoint(perceptReal) ) {
							perceptReal = null;
						}
						final Geometry result = perceptReal;
						if ( result != null ) {
							geomsVisible.add(result);
						}
					}
				}
				Geometry newGeom = null;
				if ( !geomsVisible.isEmpty() ) {
					newGeom = GeometryUtils.factory.createGeometryCollection((Geometry[]) geomsVisible.toArray());
					newGeom.union();
				}
				if ( newGeom != null ) { return new GamaShape(newGeom); }
				return null;
			}
			return new GamaShape(visiblePercept);
		}

		@operator("masked_by")
		@doc(examples = { "perception_geom masked_by obstacle_list --: returns the geometry representing the part of perception_geom visible from the agent position considering the list of obstacles obstacle_list." })
		public static IShape masked_by(final IScope scope, final IShape source, final IContainer<?, IAgent> obstacles) {
			return masked_by(scope, source, obstacles, null);
		}

		@operator("split_at")
		@doc(value = "The two part of the left-operand lines split at the given right-operand point", special_cases = { "if the left-operand is a point or a polygon, returns an empty list" }, examples = { "polyline([{1,2},{4,6}]) split_at {7,6}  --: [polyline([{1.0;2.0},{7.0;6.0}]), polyline([{7.0;6.0},{4.0;6.0}])]." })
		public static GamaList<IShape> split_at(final IShape geom, final ILocation pt) {
			final GamaList<IShape> lines = new GamaList<IShape>();
			GamaList<Geometry> geoms = null;
			if ( geom.getInnerGeometry() instanceof LineString ) {
				final Coordinate[] coords = ((LineString) geom.getInnerGeometry()).getCoordinates();
				final Point pt1 = GeometryUtils.factory.createPoint(new GamaPoint(pt.getLocation()));
				final int nb = coords.length;
				int indexTarget = -1;
				double distanceT = Double.MAX_VALUE;
				for ( int i = 0; i < nb - 1; i++ ) {
					final Coordinate s = coords[i];
					final Coordinate t = coords[i + 1];
					final Coordinate[] seg = { s, t };
					final Geometry segment = GeometryUtils.factory.createLineString(seg);
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
				final GamaList<Geometry> geoms1 = new GamaList<Geometry>();
				geoms1.add(GeometryUtils.factory.createLineString(coords1));
				geoms1.add(GeometryUtils.factory.createLineString(coords2));
				geoms = geoms1;
			} else if ( geom.getInnerGeometry() instanceof MultiLineString ) {
				final Point point = GeometryUtils.factory.createPoint((Coordinate) pt);
				Geometry geom2 = null;
				double distMin = Double.MAX_VALUE;
				final MultiLineString ml = (MultiLineString) geom.getInnerGeometry();
				for ( int i = 0; i < ml.getNumGeometries(); i++ ) {
					final double dist = ml.getGeometryN(i).distance(point);
					if ( dist <= distMin ) {
						geom2 = ml.getGeometryN(i);
						distMin = dist;
					}
				}
				final Coordinate[] coords = ((LineString) geom2).getCoordinates();
				final Point pt1 = GeometryUtils.factory.createPoint(new GamaPoint(pt.getLocation()));
				final int nb = coords.length;
				int indexTarget = -1;
				double distanceT = Double.MAX_VALUE;
				for ( int i = 0; i < nb - 1; i++ ) {
					final Coordinate s = coords[i];
					final Coordinate t = coords[i + 1];
					final Coordinate[] seg = { s, t };
					final Geometry segment = GeometryUtils.factory.createLineString(seg);
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
				final GamaList<Geometry> geoms1 = new GamaList<Geometry>();
				geoms1.add(GeometryUtils.factory.createLineString(coords1));
				geoms1.add(GeometryUtils.factory.createLineString(coords2));
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

		@operator("convex_hull")
		@doc(value = "A geometry corresponding to the convex hull of the operand.", examples = { "convex_hull(self) --: returns the convex hull of the geometry of the agent applying the operator" })
		public static IShape convex_hull(final IShape g) {
			return new GamaShape(g.getInnerGeometry().convexHull());
		}

		@operator(value = { IKeyword.MULTIPLY, "scaled_by" })
		@doc(special_cases = { "if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) scaled by the right-hand operand coefficient" }, examples = { "shape * 2 --: returns a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 2" })
		public static IShape scaled_by(final IScope scope, final IShape g, final Double coefficient) {
			final IShape g1 = g.getGeometry();
			if ( g1 instanceof GamaShape ) { return ((GamaShape) g1).scaledBy(scope, coefficient); }
			return new GamaShape(g1.getInnerGeometry()).scaledBy(scope, coefficient);
			// return new GamaShape(GeometryUtils.homothetie(g.getInnerGeometry(), coefficient));

		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" })
		@doc(special_cases = { "if the left-hand operand is a geometry and the rigth-hand operand a map (with [distance::float, quadrantSegments:: int (the number of line segments used to represent a quadrant of a circle), endCapStyle::int (1: (default) a semi-circle, 2: a straight line perpendicular to the end segment, 3: a half-square)]), returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged considering the right-hand operand parameters" }, examples = { "shape + [distance::5.0, quadrantSegments::4, endCapStyle:: 2] --: returns a geometry corresponding to the geometry of the agent applying the operator enlarged by a distance of 5, with 4 segments to represent a quadrant of a circle and a straight line perpendicular to the end segment" })
		public static IShape enlarged_by(final IShape g, final GamaMap parameters) {
			final Double distance = (Double) parameters.get("distance");
			final Integer quadrantSegments = (Integer) parameters.get("quadrantSegments");
			final Integer endCapStyle = (Integer) parameters.get("endCapStyle");
			if ( endCapStyle == null ) { return new GamaShape(g.getInnerGeometry().buffer(distance, quadrantSegments)); }
			return new GamaShape(g.getInnerGeometry().buffer(distance, quadrantSegments, endCapStyle));
		}

		@operator(value = { IKeyword.PLUS, "buffer", "enlarged_by" })
		@doc(special_cases = { "if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) enlarged by the right-hand operand distance" }, examples = { "shape + 5 --: returns a geometry corresponding to the geometry of the agent applying the operator enlarged by a distance of 5" })
		public static IShape enlarged_by(final IShape g, final Double size) {
			if ( g == null ) { return null; }
			return new GamaShape(g.getInnerGeometry().buffer(size));
		}

		@operator(value = { "-", "reduced_by" })
		@doc(special_cases = { "if the left-hand operand is a geometry and the rigth-hand operand a float, returns a geometry corresponding to the left-hand operand (geometry, agent, point) reduced by the right-hand operand distance" }, examples = { "shape - 5 --: returns a geometry corresponding to the geometry of the agent applying the operator reduced by a distance of 5" })
		public static IShape reduced_by(final IShape g, final Double size) {
			if ( g == null ) { return null; }
			return enlarged_by(g, -size);
		}

		/**
		 * @throws GamaRuntimeException
		 *             Apply a rotation (of a given angle) to the agent geometry
		 * 
		 * @param args : angle --: double, degree
		 * 
		 */
		@operator("rotated_by")
		@doc(value = "A geometry resulting from the application of a rotation by the right-hand operand angle (degree) to the left-hand operand (geometry, agent, point)", examples = { "self rotated_by 45 --: returns the geometry resulting from a 45 degres rotation to the geometry of the agent applying the operator." }, see = {
			"transformed_by", "translated_by" })
		public static IShape rotated_by(final IScope scope, final IShape g1, final Double angle) {
			if ( g1 == null ) { return null; }
			final IShape s = g1.getGeometry();
			if ( s instanceof GamaShape ) { return ((GamaShape) s).rotatedBy(scope, Math.toRadians(angle)); }
			return new GamaShape(s.getInnerGeometry()).rotatedBy(scope, Math.toRadians(angle));
		}

		@operator("rotated_by")
		@doc(comment = "the right-hand operand can be a float or a int")
		public static IShape rotated_by(final IScope scope, final IShape g1, final Integer angle) {
			if ( g1 == null ) { return null; }
			final IShape s = g1.getGeometry();
			if ( s instanceof GamaShape ) { return ((GamaShape) s).rotatedBy(scope, angle); }
			return new GamaShape(s.getInnerGeometry()).rotatedBy(scope, angle);
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
		@operator("transformed_by")
		@doc(value = "A geometry resulting from the application of a rotation and a translation (rigth-operand : point {angle(degree), distance} of the left-hand operand (geometry, agent, point)", examples = { "self transformed_by {45, 20} --: returns the geometry resulting from 45� rotation and 10m translation of the geometry of the agent applying the operator." }, see = {
			"rotated_by", "translated_by" })
		public static IShape transformed_by(final IScope scope, final IShape g, final GamaPoint p) {
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
		@operator("translated_by")
		@doc(value = "A geometry resulting from the application of a translation by the right-hand operand distance to the left-hand operand (geometry, agent, point)", examples = { "self translated_by 45 --: returns the geometry resulting from a 10m translation to the geometry of the agent applying the operator." }, see = {
			"rotated_by", "transformed_by" })
		public static IShape translated_by(final IScope scope, final IShape g, final GamaPoint p)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return at_location(scope, g, msi.gaml.operators.Points.add((GamaPoint) g.getLocation(), p));
		}

		@operator(value = { "at_location", "translated_to" })
		@doc(value = "A geometry resulting from the tran of a translation to the right-hand operand point of the left-hand operand (geometry, agent, point)", examples = { "self at_location {10, 20} --: returns the geometry resulting from a translation to the location {10, 20} of the geometry of the agent applying the operator." })
		public static IShape at_location(final IScope scope, final IShape g, final ILocation p)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			final IShape newShape = (IShape) g.copy(scope);
			newShape.setLocation(p);
			return newShape;
		}

		@operator(value = { "without_holes", "solid" })
		@doc(value = "A geometry corresponding to the operand geometry (geometry, agent, point) without its holes", examples = { "solid(self) --: returns the geometry corresponding to the geometry of the agent applying the operator without its holes." })
		public static IShape without_holes(final IShape g) {
			if ( g == null ) { return null; }
			final Geometry geom = g.getInnerGeometry();
			Geometry result = geom;
			if ( geom instanceof Polygon ) {
				result =
					GeometryUtils.factory.createPolygon(
						GeometryUtils.factory.createLinearRing(((Polygon) geom).getExteriorRing().getCoordinates()),
						null);
			} else if ( geom instanceof MultiPolygon ) {
				final MultiPolygon mp = (MultiPolygon) geom;
				final Polygon[] polys = new Polygon[mp.getNumGeometries()];
				for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
					final Polygon p = (Polygon) mp.getGeometryN(i);
					polys[i] =
						GeometryUtils.factory.createPolygon(
							GeometryUtils.factory.createLinearRing(p.getExteriorRing().getCoordinates()), null);
				}
				result = GeometryUtils.factory.createMultiPolygon(polys);
			}
			return new GamaShape(result);
		}

		@operator(value = "triangulate", content_type = IType.GEOMETRY)
		@doc(value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand geometry (geometry, agent, point)", examples = { "triangulate(self) --: returns the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator." })
		public static GamaList<IShape> triangulate(final IScope scope, final IShape g) {
			if ( g == null ) { return null; }
			return GeometryUtils.triangulation(scope, g.getInnerGeometry());
		}

		@operator(value = "triangulate", content_type = IType.GEOMETRY)
		@doc(value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand list of geometries", examples = { "triangulate(self) --: returns the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator." })
		public static GamaList<IShape> triangulate(final IScope scope, final IList<IShape> ls) {
			return GeometryUtils.triangulation(scope, ls);
		}

		@operator(value = "as_hexagonal_grid", content_type = IType.GEOMETRY)
		@doc(value = "A list of geometries (triangles) corresponding to the Delaunay triangulation of the operand list of geometries", examples = { "triangulate(self) --: returns the list of geometries (triangles) corresponding to the Delaunay triangulation of the geometry of the agent applying the operator." })
		public static GamaList<IShape> as_hexagonal_grid(final IShape ls, final GamaPoint param) {
			return GeometryUtils.hexagonalGridFromGeom(ls, (int) param.x, (int) param.y);
		}

		@operator(value = "as_grid", content_type = IType.GEOMETRY)
		@doc(value = "A matrix of square geometries (grid with 8-neighbourhood) with dimension given by the rigth-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)", examples = { "self as_grid {10, 5} --: returns a matrix of square geometries (grid with 8-neighbourhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator." })
		public static IMatrix as_grid(final IScope scope, final IShape g, final GamaPoint dim)
			throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(scope, g, (int) dim.x, (int) dim.y, false, false, false, false);
		}

		@operator(value = "as_4_grid", content_type = IType.GEOMETRY)
		@doc(value = "A matrix of square geometries (grid with 4-neighbourhood) with dimension given by the rigth-hand operand ({nb_cols, nb_lines}) corresponding to the square tessellation of the left-hand operand geometry (geometry, agent)", examples = { "self as_grid {10, 5} --: returns matrix of square geometries (grid with 4-neighbourhood) with 10 columns and 5 lines corresponding to the square tessellation of the geometry of the agent applying the operator." })
		public static IMatrix as_4_grid(final IScope scope, final IShape g, final GamaPoint dim)
			throws GamaRuntimeException {
			// cols, rows
			return new GamaSpatialMatrix(scope, g, (int) dim.x, (int) dim.y, false, true, false, false);
		}

		@operator(value = "split_lines", content_type = IType.GEOMETRY)
		@doc(value = "A list of geometries resulting after cutting the lines at their intersections.", examples = { "split_lines([line([{0,10}, {20,10}], line([{0,10}, {20,10}]]) --: returns a list of four polylines: line([{0,10}, {10,10}]), line([{10,10}, {20,10}]), line([{10,0}, {10,10}]) and line([{10,10}, {10,20}])." })
		public static IList<IShape> split_lines(final IScope scope, final IContainer<?, IShape> geoms)
			throws GamaRuntimeException {
			if ( geoms.isEmpty(scope) ) { return new GamaList<IShape>(); }
			Geometry nodedLineStrings = geoms.first(scope).getInnerGeometry();

			for ( final Object obj : geoms ) {
				final Geometry g = ((IShape) obj).getInnerGeometry();
				if ( g instanceof LineString ) {
					nodedLineStrings = nodedLineStrings.union(g);
				}
			}
			final GamaList<IShape> nwGeoms = new GamaList<IShape>();

			for ( int i = 0, n = nodedLineStrings.getNumGeometries(); i < n; i++ ) {
				final Geometry g = nodedLineStrings.getGeometryN(i);
				if ( g instanceof LineString ) {
					nwGeoms.add(new GamaShape(g));
				}
			}
			return nwGeoms;
		}

		@operator(value = "skeletonize", content_type = IType.GEOMETRY)
		@doc(value = "A list of geometries (polylines) corresponding to the skeleton of the operand geometry (geometry, agent)", examples = { "skeletonize(self) --: returns the list of geometries corresponding to the skeleton of the geometry of the agent applying the operator." })
		public static GamaList<IShape> skeletonize(final IScope scope, final IShape g) {
			final List<LineString> netw = GeometryUtils.squeletisation(scope, g.getInnerGeometry());
			final GamaList<IShape> geoms = new GamaList();
			for ( final LineString ls : netw ) {
				geoms.add(new GamaShape(ls));
			}
			return geoms;
		}

		@operator("clean")
		@doc(value = "A geometry corresponding to the cleaning of the operand (geometry, agent, point)", comment = "The cleaning corresponds to a buffer with a distance of 0.0", examples = { "cleaning(self) --: returns the geometry resulting from the cleaning of the geometry of the agent applying the operator." })
		public static IShape clean(final IShape g) {
			if ( g == null || g.getInnerGeometry() == null ) { return g; }
			if ( g.getInnerGeometry() instanceof Polygon ) { return new GamaShape(g.getInnerGeometry().buffer(0.0)); }
			if ( g.getInnerGeometry() instanceof MultiPolygon ) {
				final MultiPolygon mp = (MultiPolygon) g.getInnerGeometry();
				final int nb = mp.getNumGeometries();
				final Polygon[] polys = new Polygon[nb];
				for ( int i = 0; i < nb; i++ ) {
					polys[i] = (Polygon) mp.getGeometryN(i).buffer(0.0);
				}
				return new GamaShape(GeometryUtils.factory.createMultiPolygon(polys));
			}
			return new GamaShape(g.getInnerGeometry());
		}

		/**
		 * Simplification of a geometry (Douglas-Peuker algorithm)
		 */

		@operator("simplification")
		@doc(value = "A geometry corresponding to the simplification of the operand (geometry, agent, point) considering a tolerance distance.", comment = "The algorithm used for the simplification is Douglas-Peucker", examples = { "self simplification 0.1 --: returns the geometry resulting from the application of the Douglas-Peuker algorithm on the geometry of the agent applying the operator with a tolerance distance of 0.1." })
		public static IShape simplification(final IShape g1, final Double distanceTolerance) {
			if ( g1 == null || g1.getInnerGeometry() == null ) { return g1; }
			if ( g1.isPoint() ) {
				new GamaShape(g1.getInnerGeometry());
			}
			final Geometry geomSimp = DouglasPeuckerSimplifier.simplify(g1.getInnerGeometry(), distanceTolerance);
			if ( geomSimp != null && !geomSimp.isEmpty() && geomSimp.isValid() ) { return new GamaShape(geomSimp); }
			return new GamaShape(g1.getInnerGeometry());
		}

	}

	public static abstract class Relations {

		@operator(value = { "towards", "direction_to" })
		@doc(value = "The direction (in degree) between the two geometries (geometries, agents, points) considering the topology of the agent applying the operator.", examples = { "ag1 towards ag2 --: the direction between ag1 and ag2 and ag3 considering the topology of the agent applying the operator" }, see = {
			"distance_between", "distance_to", "direction_between", "path_between", "path_to" })
		public static Integer towards(final IScope scope, final IShape agent, final IShape target) {
			return scope.getTopology().directionInDegreesTo(scope, agent, target);
		}

		@operator("distance_between")
		@doc(value = "A distance between a list of geometries (geometries, agents, points) considering a topology.", examples = { "my_topology distance_between [ag1, ag2, ag3] --: the distance between ag1, ag2 and ag3 considering the topology my_topology" }, see = {
			"towards", "direction_to", "distance_to", "direction_between", "path_between", "path_to" })
		public static Double distance_between(final IScope scope, final ITopology t,
			final IContainer<?, IShape> geometries) {
			final int size = geometries.length(scope);
			if ( size == 0 || size == 1 ) { return 0d; }
			IShape previous = null;
			Double distance = 0d;
			for ( final IShape obj : geometries ) {
				if ( previous != null ) {
					final Double d = t.distanceBetween(scope, previous, obj);
					if ( d == null ) { return null; }
					distance += d;
				}
				previous = obj;
			}
			return distance;
		}

		@operator(value = "direction_between")
		@doc(value = "A direction (in degree) between a list of two geometries (geometries, agents, points) considering a topology.", examples = { "my_topology direction_between [ag1, ag2] --: the direction between ag1 and ag2 considering the topology my_topology" }, see = {
			"towards", "direction_to", "distance_to", "distance_between", "path_between", "path_to" })
		public static Integer direction_between(final IScope scope, final ITopology t,
			final IContainer<?, IShape> geometries) throws GamaRuntimeException {
			final int size = geometries.length(scope);
			if ( size == 0 || size == 1 ) { return 0; }
			final IShape g1 = geometries.first(scope);
			final IShape g2 = geometries.last(scope);
			return t.directionInDegreesTo(scope, g1, g2);
		}

		@operator(value = "path_between", content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(value = "A path between a list of two geometries (geometries, agents or points) considering a topology.", examples = { "my_topology path_between [ag1, ag2] --: A path between ag1 and ag2" }, see = {
			"towards", "direction_to", "distance_between", "direction_between", "path_to", "distance_to" })
		public static IPath path_between(final IScope scope, final ITopology graph, final IContainer<?, IShape> nodes)
			throws GamaRuntimeException {
			// TODO Assumes that all elements in nodes are vertices of the graph... Should be
			// checked

			if ( nodes.isEmpty(scope) ) { return null; }
			final int n = nodes.length(scope);
			final IShape source = nodes.first(scope);
			if ( n == 1 ) { return PathFactory.newInstance(scope.getTopology(), source, source, new GamaList<IShape>());
			// return new GamaPath(scope.getTopology(), source, source, new GamaList());
			}
			final IShape target = nodes.last(scope);
			if ( n == 2 ) { return graph.pathBetween(scope, source, target); }
			final GamaList<IShape> edges = new GamaList<IShape>();
			IShape previous = null;
			for ( final IShape gg : nodes ) {
				if ( previous != null ) {
					// TODO Take the case of ILocation
					edges.addAll(graph.pathBetween(scope, previous, gg).getEdgeList());
				}
				previous = gg;
			}
			return PathFactory.newInstance(graph, source, target, edges);
			// new GamaPath(graph, source, target, edges);
		}

		@operator(value = "distance_to")
		@doc(value = "A distance between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.", examples = { "ag1 distance_to ag2 --: the distance between ag1 and ag2 considering the topology of the agent applying the operator" }, see = {
			"towards", "direction_to", "distance_between", "direction_between", "path_between", "path_to" })
		public static Double distance_to(final IScope scope, final IShape source, final IShape target) {
			return scope.getTopology().distanceBetween(scope, source, target);
		}

		@operator(value = "distance_to")
		@doc(value = "An Euclidean distance between two points.")
		// No documentation because it is same same as the previous one (but optimized for points?)
		public static Double distance_to(final IScope scope, final GamaPoint source, final GamaPoint target) {
			return scope.getTopology().distanceBetween(scope, source, target);
		}

		@operator("path_to")
		@doc(value = "A path between two geometries (geometries, agents or points) considering the topology of the agent applying the operator.", examples = { "ag1 path_to ag2 --: the path between ag1 and ag2 considering the topology of the agent applying the operator" }, see = {
			"towards", "direction_to", "distance_between", "direction_between", "path_between", "distance_to" })
		public static IPath path_to(final IScope scope, final IShape g, final IShape g1) throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getTopology().pathBetween(scope, g1, g);
		}

		@operator("path_to")
		@doc(value = "A shortest path between two points considering the topology of the agent applying the operator.")
		// No documentation because it is same same as the previous one (but optimized for points?)
		public static IPath path_to(final IScope scope, final GamaPoint g, final GamaPoint g1)
			throws GamaRuntimeException {
			if ( g == null ) { return null; }
			return scope.getTopology().pathBetween(scope, g1, g);
		}

	}

	public static abstract class Properties {

		@operator(value = { "disjoint_from" })
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) is disjoints from the right-geometry (or agent/point).", special_cases = {
			"if one of the operand is null, returns true.",
			"if one operand is a point, returns false if the point is included in the geometry." }, examples = {
			"polyline([{10,10},{20,20}]) disjoint_from polyline([{15,15},{25,25}]) --: false.",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{15,15},{15,25},{25,25},{25,15}]) --: false.",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from geometry({15,15}) --: false.",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from geometry({25,25}) --: true.",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) disjoint_from polygon([{35,35},{35,45},{45,45},{45,35}]) --: true" }, see = {
			"intersects", "crosses", "overlaps", "partially_overlaps", "touches" })
		public static Boolean disjoint_from(final IScope scope, final IShape g1, final IShape g2) {
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

		@operator("overlaps")
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) overlaps the right-geometry (or agent/point).", special_cases = {
			"if one of the operand is null, returns false.",
			"if one operand is a point, returns true if the point is included in the geometry" }, examples = {
			"polyline([{10,10},{20,20}]) overlaps polyline([{15,15},{25,25}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps geometry({25,25}) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{35,35},{35,45},{45,45},{45,35}]) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polyline([{10,10},{20,20}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps geometry({15,15}) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) overlaps polygon([{10,20},{20,20},{20,30},{10,30}]) --: true" }, see = {
			"<--:", "disjoint_from", "crosses", "intersects", "partially_overlaps", "touches" })
		public static Boolean overlaps(final IScope scope, final IShape g1, final IShape g2) {
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

		@operator("partially_overlaps")
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) partially overlaps the right-geometry (or agent/point).", special_cases = { "if one of the operand is null, returns false." }, comment = "if one geometry operand fully covers the other geometry operand, returns false (contrarily to the overlaps operator).", examples = {
			"polyline([{10,10},{20,20}]) partially_overlaps polyline([{15,15},{25,25}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps geometry({25,25}) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{35,35},{35,45},{45,45},{45,35}]) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polyline([{10,10},{20,20}]) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps geometry({15,15}) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{0,0},{0,30},{30,30}, {30,0}]) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{15,15},{15,25},{25,25},{25,15}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) partially_overlaps polygon([{10,20},{20,20},{20,30},{10,30}]) --: false" }, see = {
			"<--:", "disjoint_from", "crosses", "overlaps", "intersects", "touches" })
		public static Boolean partially_overlaps(final IShape g1, final IShape g) {
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
		@operator("touches")
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) touches the right-geometry (or agent/point).", special_cases = { "if one of the operand is null, returns false." }, comment = "returns true when the left-operand only touches the right-operand. When one geometry covers partially (or fully) the other one, it returns false.", examples = {
			"polyline([{10,10},{20,20}]) touches geometry({15,15}) --: false",
			"polyline([{10,10},{20,20}]) touches geometry({10,10}) --: true",
			"geometry({15,15}) touches geometry({15,15}) --: false",
			"polyline([{10,10},{20,20}]) touches polyline([{10,10},{5,5}]) --: true",
			"polyline([{10,10},{20,20}]) touches polyline([{5,5},{15,15}]) --: false",
			"polyline([{10,10},{20,20}]) touches polyline([{15,15},{25,25}]) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{15,15},{15,25},{25,25},{25,15}]) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,20},{20,20},{20,30},{10,30}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) touches polygon([{10,10},{0,10},{0,0},{10,0}]) --: true",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) touches geometry({15,15}) --: false",
			"polygon([{10,10},{10,20},{20,20},{20,10}]) touches geometry({10,15}) --: true" }, see = { "<--:",
			"disjoint_from", "crosses", "overlaps", "partially_overlaps", "intersects" })
		public static Boolean touches(final IShape g, final IShape g2) {
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

		@operator("crosses")
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) crosses the right-geometry (or agent/point).", special_cases = {
			"if one of the operand is null, returns false.", "if one operand is a point, returns false." }, examples = {
			"polyline([{10,10},{20,20}]) crosses polyline([{10,20},{20,10}]) --: true.",
			"polyline([{10,10},{20,20}]) crosses geometry({15,15}) --: false",
			"polyline([{0,0},{25,25}]) crosses polygon([{10,10},{10,20},{20,20},{20,10}]) --: true" }, see = { "<--:",
			"disjoint_from", "intersects", "overlaps", "partially_overlaps", "touches" })
		public static Boolean crosses(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.crosses(g2);
		}

		@operator("intersects")
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) intersects the right-geometry (or agent/point).", special_cases = { "if one of the operand is null, returns false." }, examples = { "square(5) intersects {10,10} --: false." }, see = {
			"<--:", "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
		public static Boolean intersects(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.intersects(g2);
		}

		@operator("intersects")
		@doc(value = "A boolean, equal to true if the geometry (left operand) intersects the point (right operand).")
		// no documentation because same same as before but optimized for points.
		public static Boolean intersects(final IShape g1, final GamaPoint p) {
			if ( g1 == null || p == null ) { return false; }
			return pl.intersects(p, g1.getInnerGeometry());
		}

		@operator("covers")
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) covers the right-geometry (or agent/point).", special_cases = { "if one of the operand is null, returns false." }, examples = { "square(5) covers square(2) --: true." }, see = {
			"<--:", "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
		public static Boolean covers(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g1.covers(g2);
		}

		@operator("covered_by")
		@doc(value = "A boolean, equal to true if the left-geometry (or agent/point) is covered by the right-geometry (or agent/point).", special_cases = { "if one of the operand is null, returns false." }, examples = { "square(5) covered_by square(2) --: false." }, see = {
			"<--:", "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
		public static Boolean covered_by(final IShape g1, final IShape g2) {
			if ( g1 == null || g2 == null ) { return false; }
			return g2.covers(g1);
		}

	}

	public static abstract class Punctal {

		@operator(value = { "any_location_in", "any_point_in" })
		@doc(value = "A point inside (or touching) the operand-geometry.", examples = { "any_location_in(square(5)) --: a point of the square, for example : {3,4.6}." }, see = {
			"closest_points_with", "farthest_point_to", "points_at" })
		public static ILocation any_location_in(final IScope scope, final IShape g) {
			final ILocation p = GeometryUtils.pointInGeom(g.getInnerGeometry(), GAMA.getRandom());
			return p;
		}

		@operator(value = { "points_exterior_ring" })
		@doc(value = "A list of points of the exterior ring of the operand-geometry distant from each other to the float right-operand .", examples = { " square(5) points_exterior_ring(2) --: a list of points belonging to the exterior ring of the square distant from each other of 2." }, see = {
			"closest_points_with", "farthest_point_to", "points_at" })
		public static GamaList points_exterior_ring(final IShape geom, final Double distance) {
			final GamaList<GamaPoint> locs = new GamaList<GamaPoint>();
			if ( geom.getInnerGeometry() instanceof GeometryCollection ) {
				for ( int i = 0; i < geom.getInnerGeometry().getNumGeometries(); i++ ) {
					locs.addAll(GeometryUtils.locExteriorRing(geom.getInnerGeometry().getGeometryN(i), distance));
				}
			} else {
				locs.addAll(GeometryUtils.locExteriorRing(geom.getInnerGeometry(), distance));
			}
			return locs;
		}

		@operator(value = { "points_at" }, content_type = IType.POINT)
		@doc(value = "A list of left-operand number of points located at a the right-operand distance to the agent location.", examples = { "3 points_at(20.0) --: returns [pt1, pt2, pt3] with pt1, pt2 and pt3 located at a distance of 20.0 to the agent location" }, see = {
			"any_location_in", "any_point_in", "closest_points_with", "farthest_point_to" })
		public static GamaList points_at(final IScope scope, final Integer nbLoc, final Double distance) {
			if ( distance == null || nbLoc == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				throw GamaRuntimeException.error("Impossible to compute points_at");
			}
			final GamaList<ILocation> locations = new GamaList();
			final ILocation loc = scope.getAgentScope().getLocation();
			final double angle1 = GAMA.getRandom().between(0, 2 * Math.PI);

			for ( int i = 0; i < nbLoc; i++ ) {
				final GamaPoint p =
					new GamaPoint(loc.getX() + distance * Math.cos(angle1 + (double) i / nbLoc * 2 * Math.PI),
						loc.getY() + distance * Math.sin(angle1 + (double) i / nbLoc * 2 * Math.PI));
				locations.add(p);
			}
			return locations;

		}

		@operator("closest_points_with")
		@doc(value = "A list of two closest points between the two geometries.", examples = { "geom1 closest_points_with(geom2) --: [pt1, pt2] with pt1 the closest point of geom1 to geom2 and pt1 the closest point of geom2 to geom1" }, see = {
			"any_location_in", "any_point_in", "farthest_point_to", "points_at" })
		public static IList<GamaPoint> closest_points_with(final IShape a, final IShape b) {
			final Coordinate[] coors = DistanceOp.nearestPoints(a.getInnerGeometry(), b.getInnerGeometry());
			return GamaList.with(new GamaPoint(coors[0]), new GamaPoint(coors[1]));
		}

		@operator("farthest_point_to")
		@doc(value = "the farthest point of the left-operand to the left-point.", examples = { "geom farthest_point_to(pt) --: the closest point of geom to pt" }, see = {
			"any_location_in", "any_point_in", "closest_points_with", "points_at" })
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

		@operator("angle_between")
		@doc(value = "the angle between vector P0P1 and P0P2", examples = { "angle_between({5,5},{10,5},{5,10}) --: 90°" })
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

		@operator(value = "neighbours_of", content_type = IType.AGENT)
		@doc(value = "a list, containing all the agents located at a distance inferior or equal to 1 to the right-hand operand agent considering the left-hand operand topology.", examples = { "topology(self) neighbours_of self --: returns all the agents located at a distance lower or equal to 1 to the agent applying the operator considering its topology." }, see = {
			"neighbours_at", "closest_to", "overlapping", "agents_overlapping", "agents_inside", "agent_closest_to" })
		public static IList neighbours_of(final IScope scope, final ITopology t, final IAgent agent)
			throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return new GamaList(t.getNeighboursOf(agent, 1.0, Different.with()));
		}

		@operator(value = "neighbours_of", content_type = IType.AGENT)
		@doc(special_cases = "a list, containing all the agents located at a distance inferior or equal to the right member (float) of the pair (right-hand operand) to the left member (agent, geometry or point) considering the left-hand operand topology.", examples = { "topology(self) neighbours_of self::10--: returns all the agents located at a distance lower or equal to 10 to the agent applying the operator considering its topology." })
		public static IList neighbours_of(final IScope scope, final ITopology t, final GamaPair pair)
			throws GamaRuntimeException {
			if ( pair == null ) { return GamaList.EMPTY_LIST; }
			final Object a = pair.key;
			if ( a == null ) { throw GamaRuntimeException.error("Cannot compute neighbours of a null agent"); }
			if ( !(a instanceof IShape) ) { throw GamaRuntimeException
				.error("The operator neighbours_of expects a pair agent::float as its right member"); }
			final Object d = pair.value;
			if ( !(d instanceof Number) ) { throw GamaRuntimeException
				.error("The operator neighbours_of expects a pair agent::float as its right member"); }
			if ( a instanceof ILocation ) { return new GamaList(t.getNeighboursOf((ILocation) a,
				Cast.asFloat(scope, d), Different.with())); }
			return new GamaList(t.getNeighboursOf((IShape) a, Cast.asFloat(scope, d), Different.with()));
		}

		@operator(value = "neighbours_at", content_type = ITypeProvider.FIRST_TYPE)
		@doc(value = "a list, containing all the agents of the same species than the left argument (if it is an agent) located at a distance inferior or equal to the right-hand operand to the left-hand operand (geometry, agent, point).", comment = "The topology used to compute the neighbourhood  is the one of the left-operand if this one is an agent; otherwise the one of the agent applying the operator.", examples = { "(self neighbours_at (10)) --: returns all the agents located at a distance lower or equal to 10 to the agent applying the operator." }, see = {
			"neighbours_of", "closest_to", "overlapping", "agents_overlapping", "agents_inside", "agent_closest_to",
			"at_distance" })
		public static IList neighbours_at(final IScope scope, final IShape agent, final Double distance)
			throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			// CHANGE
			final ITopology t = scope.getTopology();
			final IAgentFilter filter =
				agent instanceof IAgent ? In.population(((IAgent) agent).getPopulation()) : Different.with();
			return new GamaList(t.getNeighboursOf(agent, distance, filter));
		}

		@operator(value = "neighbours_at", content_type = IType.AGENT)
		@doc(value = "a list, containing all the agents located at a distance inferior or equal to the right-hand operand (point).", comment = "The topology used to compute the neighbourhood  is the one of the left-operand if this one is an agent; otherwise the one of the agent applying the operator.", examples = { "({50, 50} neighbours_at (10)) --: returns all the agents located at a distance lower or equal to 10 to point {50, 50}." }, see = {
			"neighbours_of", "closest_to", "overlapping", "agents_overlapping", "agents_inside", "agent_closest_to",
			"at_distance" })
		// no doc, because same same as before but optimized for "point".
		public static IList neighbours_at(final IScope scope, final GamaPoint agent, final Double distance)
			throws GamaRuntimeException {
			if ( agent == null ) { return GamaList.EMPTY_LIST; }
			return new GamaList(scope.getTopology().getNeighboursOf(agent, distance, Different.with()));
		}

		@operator(value = "at_distance", content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(value = "A list of agents among the left-operand list that are located at a distance <= the right operand from the caller agent (in its topology)", examples = { "[ag1, ag2, ag3] at_distance 20 --: return the agents of the list located at a distance <= 20 from the caller agent (in the same order)." }, see = {
			"neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
			"overlapping" })
		public static IList at_distance(final IScope scope, final IList list, final Double distance) {
			if ( list.isEmpty() ) { return GamaList.EMPTY_LIST; }
			final IAgent agent = scope.getAgentScope();
			final ITopology t = scope.getTopology();
			if ( agent.isPoint() ) { return new GamaList(t.getNeighboursOf(agent.getLocation(), distance,
				In.list(scope, list))); }
			if ( t.isTorus() ) { return new GamaList(t.getNeighboursOf(agent, distance, In.list(scope, list))); }
			return new GamaList(
				t.getAgentsIn(Transformations.enlarged_by(agent, distance), In.list(scope, list), false));
		}

		@operator(value = "at_distance", content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(special_cases = "If the left operand is a species, return agents of the specified species (slightly more efficient than using list(species1), for instance)", examples = { "species1 at_distance 20 --: return the agents of species1 located at a distance <= 20 from the caller agent." }, see = {
			"neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
			"overlapping" })
		public static IList at_distance(final IScope scope, final ISpecies species, final Double distance) {
			final IAgent agent = scope.getAgentScope();
			final ITopology t = scope.getTopology();
			final IPopulation pop = agent.getPopulationFor(species);
			if ( pop == null ) { return GamaList.EMPTY_LIST; }
			if ( agent.isPoint() ) { return new GamaList(t.getNeighboursOf(agent.getLocation(), distance,
				In.population(pop))); }
			if ( t.isTorus() ) { return new GamaList(t.getNeighboursOf(agent, distance, In.population(pop))); }
			return new GamaList(t.getAgentsIn(Transformations.enlarged_by(agent, distance), In.population(pop), false));
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(value = "A list of agents among the left-operand list, covered by the operand (casted as a geometry).", examples = { "[ag1, ag2, ag3] inside(self) --: return the agents among ag1, ag2 and ag3 that are covered by the shape of the agent applying the operator." }, see = {
			"neighbours_at", "neighbours_of", "closest_to", "overlapping", "agents_overlapping", "agents_inside",
			"agent_closest_to" })
		public static IList<IAgent> inside(final IScope scope, final IContainer<?, IShape> targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			final ITopology t = scope.getTopology();
			return new GamaList(t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), In.list(scope, targets),
				true));
		}

		@operator(value = { "inside" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(special_cases = { "if the left-operand is a species, return agents of the specified species (slightly more efficient than using list(species1), for instance)." }, examples = { "species1 inside(self) --: return the agents of species species1 that are covered by the shape of the agent applying the operator." })
		public static IList<IAgent> inside(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			final IPopulation pop = scope.getAgentScope().getPopulationFor(targets);
			if ( pop == null ) { return GamaList.EMPTY_LIST; }
			final ITopology t = scope.getTopology(); // VERIFY
			return new GamaList(t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), In.population(pop), true));
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(value = "A list of agents among the left-operand list, overlapping the operand (casted as a geometry).", examples = { "[ag1, ag2, ag3] overlapping(self) --: return the agents among ag1, ag2 and ag3 that overlap the shape of the agent applying the operator." }, see = {
			"neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
			"agents_overlapping" })
		public static IList<IAgent> overlapping(final IScope scope, final IContainer<?, IShape> targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			final ITopology t = scope.getTopology();
			return new GamaList(t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), In.list(scope, targets),
				false));
		}

		@operator(value = { "overlapping" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(special_cases = { "if the left-operand is a species, return agents of the specified species." }, examples = { "species1 overlapping(self) --: return the agents of species species1 that overlap the shape of the agent applying the operator." })
		public static IList<IAgent> overlapping(final IScope scope, final ISpecies targets,
			final Object toBeCastedIntoGeometry) throws GamaRuntimeException {
			final IPopulation pop = scope.getAgentScope().getPopulationFor(targets);
			if ( pop == null ) { return new GamaList(); }
			final ITopology t = scope.getTopology();
			return new GamaList(
				t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), In.population(pop), false));
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(value = "An agent among the left-operand list, the closest to the operand (casted as a geometry).", comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.", examples = { "[ag1, ag2, ag3] closest_to(self) --: return the closest agent among ag1, ag2 and ag3 to the agent applying the operator." }, see = {
			"neighbours_at", "neighbours_of", "neighbours_at", "neighbours_of", "inside", "overlapping",
			"agents_overlapping", "agents_inside", "agent_closest_to" })
		public static Object closest_to(final IScope scope, final IContainer<?, IShape> targets, final IShape source)
			throws GamaRuntimeException {
			if ( source instanceof ILocation ) {
				return scope.getTopology().getAgentClosestTo((ILocation) source, In.list(scope, targets));
			} else if ( source instanceof IShape ) { return scope.getTopology().getAgentClosestTo(source,
				In.list(scope, targets)); }
			throw GamaRuntimeException.error(StringUtils.toGaml(source) + " is not a geometrical object");
		}

		@operator(value = { "closest_to" }, type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(special_cases = { "if the left-operand is a species, return an agent of the specified species." }, examples = {
			"neighbours_at", "neighbours_of",
			"species1 closest_to(self) --: return the closest agent of species species1 to the agent applying the operator." })
		public static IAgent closest_to(final IScope scope, final ISpecies targets, final IShape source)
			throws GamaRuntimeException {
			final IPopulation pop = scope.getAgentScope().getPopulationFor(targets);
			if ( pop == null ) { return null; }
			// CHANGE
			final ITopology t = scope.getTopology(); // VERIFY (was pop.getTopology())
			// ITopology t = scope.getAgentScope().getTopology();
			if ( source instanceof ILocation ) {
				return t.getAgentClosestTo((ILocation) source, In.population(pop));
			} else if ( source instanceof IShape ) { return t.getAgentClosestTo(source, In.population(pop)); }
			throw GamaRuntimeException.error(StringUtils.toGaml(source) + " is not a geometrical object");
		}

		@operator(value = "agent_closest_to", type = IType.AGENT)
		@doc(value = "A agent, the closest to the operand (casted as a geometry).", comment = "the distance is computed in the topology of the calling agent (the agent in which this operator is used), with the distance algorithm specific to the topology.", examples = { "agent_closest_to(self) --: return the closest agent to the agent applying the operator." }, see = {
			"neighbours_at", "neighbours_of", "agents_inside", "agents_overlapping", "closest_to", "inside",
			"overlapping" })
		public static IAgent agent_closest_to(final IScope scope, final Object source) throws GamaRuntimeException {
			if ( source instanceof ILocation ) {
				return scope.getTopology().getAgentClosestTo((ILocation) source, Different.with());
			} else if ( source instanceof IShape ) { return scope.getTopology().getAgentClosestTo((IShape) source,
				Different.with()); }
			throw GamaRuntimeException.error(StringUtils.toGaml(source) + " is not a geometrical object");
		}

		@operator(value = "agents_inside", content_type = IType.AGENT)
		@doc(value = "A list of agents covered by the operand (casted as a geometry).", examples = { "agents_inside(self) --: return the agents that are covered by the shape of the agent applying the operator." }, see = {
			"agent_closest_to", "agents_overlapping", "closest_to", "inside", "overlapping" })
		public static IList<IAgent> agents_inside(final IScope scope, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			final ITopology t = scope.getTopology();
			return new GamaList(t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), Different.with(), true));
		}

		@operator(value = "agents_overlapping", content_type = IType.AGENT)
		@doc(value = "A list of agents overlapping the operand (casted as a geometry).", examples = { "agents_overlapping(self) --: return the agents that overlap the shape of the agent applying the operator." }, see = {
			"neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
			"overlapping", "at_distance" })
		public static IList<IAgent> agents_overlapping(final IScope scope, final Object toBeCastedIntoGeometry)
			throws GamaRuntimeException {
			final ITopology t = scope.getTopology();
			return new GamaList(t.getAgentsIn(Cast.asGeometry(scope, toBeCastedIntoGeometry), Different.with(), false));
		}

		@operator(value = "agents_at_distance", content_type = ITypeProvider.FIRST_CONTENT_TYPE)
		@doc(value = "A list of agents situated at a distance <= the right argument.", comment = "Equivalent to neighbours_at with a left-hand argument equal to 'self'", examples = { "agents_at_distance(20) --: all the agents (excluding the caller) which distance to the caller is <= 20" }, see = {
			"neighbours_at", "neighbours_of", "agent_closest_to", "agents_inside", "closest_to", "inside",
			"overlapping", "at_distance" })
		public static IList agents_at_distance(final IScope scope, final Double distance) {
			final IAgent agent = scope.getAgentScope();
			Iterator<IAgent> result;
			if ( agent.isPoint() ) {
				result = scope.getTopology().getNeighboursOf(agent.getLocation(), distance, Different.with());
			} else {
				result =
					scope.getTopology().getAgentsIn(Transformations.enlarged_by(agent, distance), Different.with(),
						false);
			}
			return new GamaList(result);
		}

	}

	public static abstract class Statistics {

		@operator(value = { "simple_clustering_by_distance" }, content_type = IType.LIST)
		@doc(value = "A list of agent groups clustered by distance considering a distance min between two groups.", comment = "use of hierarchical clustering with Minimum for linkage criterion between two groups of agents.", examples = { "[ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0 --: for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]" }, see = { "simple_clustering_by_envelope_distance" })
		public static IList simple_clustering_by_distance(final IScope scope, final IContainer<?, IAgent> agents,
			final Double distance) {
			final int nb = agents.length(scope);

			if ( nb == 0 ) {
				// scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			double distMin = Double.MAX_VALUE;
			Set<IList<IAgent>> minFusion = null;

			final IList<IList<IAgent>> groups = new GamaList<IList<IAgent>>();
			final Map<Set<IList<IAgent>>, Double> distances = new HashMap<Set<IList<IAgent>>, Double>();
			for ( final IAgent ag : agents.iterable(scope) ) {
				final IList<IAgent> group = new GamaList<IAgent>();
				group.add(ag);
				groups.add(group);
			}

			if ( nb == 1 ) { return groups; }
			// BY GEOMETRIES
			for ( int i = 0; i < nb - 1; i++ ) {
				final IList<IAgent> g1 = groups.get(i);
				for ( int j = i + 1; j < nb; j++ ) {
					final IList<IAgent> g2 = groups.get(j);
					final Set<IList<IAgent>> distGp = new HashSet<IList<IAgent>>();
					distGp.add(g1);
					distGp.add(g2);
					final IAgent a = g1.get(0);
					final IAgent b = g2.get(0);
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
				IList<IList<IAgent>> fusionL = new GamaList<IList<IAgent>>(minFusion);
				final IList<IAgent> g1 = fusionL.get(0);
				final IList<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				final IList<IAgent> groupeF = new GamaList<IAgent>(g2);
				groupeF.addAll(g1);
				for ( final IList<IAgent> groupe : groups ) {
					final Set<IList<IAgent>> newDistGp = new HashSet<IList<IAgent>>();
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
				for ( final Set<IList<IAgent>> distGp : distances.keySet() ) {
					final double dist = distances.get(distGp).doubleValue();
					if ( dist < distMin ) {
						minFusion = distGp;
						distMin = dist;
					}
				}
			}
			return groups;
		}

		@operator(value = { "simple_clustering_by_envelope_distance" }, content_type = IType.LIST)
		@doc(value = "A list of agent groups clustered by distance (considering the agent envelop) considering a distance min between two groups.", comment = "use of hierarchical clustering with Minimum for linkage criterion between two groups of agents.", examples = { "[ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0 --: for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]" }, see = { "simple_clustering_by_distance" })
		// CHANGER LE NOM !!!
		public static IList simple_clustering_by_envelope_distance(final IScope scope,
			final IContainer<?, IAgent> agents, final Double distance) {
			final int nb = agents.length(scope);

			if ( nb == 0 ) {
				// scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			double distMin = Double.MAX_VALUE;
			Set<IList<IAgent>> minFusion = null;

			final IList<IList<IAgent>> groups = new GamaList<IList<IAgent>>();
			final Map<Set<IList<IAgent>>, Double> distances = new HashMap<Set<IList<IAgent>>, Double>();
			for ( final IAgent ag : agents.iterable(scope) ) {
				final IList<IAgent> group = new GamaList<IAgent>();
				group.add(ag);
				groups.add(group);
			}

			if ( nb == 1 ) { return groups; }

			for ( int i = 0; i < nb - 1; i++ ) {
				final IList<IAgent> g1 = groups.get(i);
				for ( int j = i + 1; j < nb; j++ ) {
					final IList<IAgent> g2 = groups.get(j);
					final Set<IList<IAgent>> distGp = new HashSet<IList<IAgent>>();
					distGp.add(g1);
					distGp.add(g2);

					final Envelope gg1 = g1.get(0).getEnvelope();
					final Envelope gg2 = g2.get(0).getEnvelope();
					final double dist = gg1.distance(gg2);
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
				final IList<IAgent> g1 = fusionL.get(0);
				final IList<IAgent> g2 = fusionL.get(1);
				distances.remove(minFusion);
				fusionL = null;
				groups.remove(g2);
				groups.remove(g1);
				final IList<IAgent> groupeF = new GamaList<IAgent>(g2);
				groupeF.addAll(g1);
				for ( final IList<IAgent> groupe : groups ) {
					final Set<IList<IAgent>> newDistGp = new HashSet<IList<IAgent>>();
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
				for ( final Set<IList<IAgent>> distGp : distances.keySet() ) {
					final double dist = distances.get(distGp).doubleValue();
					if ( dist < distMin ) {
						minFusion = distGp;
						distMin = dist;
					}
				}
			}
			return groups;
		}

	}

	public static abstract class ThreeD {

		@operator(value = { "add_z" })
		@doc(deprecated = "use set location instead",value = "add_z", comment = "Return a geometry with a z value"
			+ "The add_z operator set the z value of the whole shape."
			+ "For each point of the cell the same z value is set.", examples = { "set shape <- shape add_z rnd(100);" }, see = { "add_z_pt" })

		@Deprecated
		public static IShape add_z(final IShape g, final Double z) {
			final Coordinate[] coordinates = g.getInnerGeometry().getCoordinates();
			((GamaPoint) g.getLocation()).z = z;
			for ( int i = 0; i < coordinates.length; i++ ) {
				coordinates[i].z = z;
			}
			return g;
		}

		@operator(value = { "add_z_pt" })
		@doc(value = "add_z_pt", comment = "Return a geometry with a z value", examples = { "loop i from: 0 to: length(shape.points) - 1{"
			+ "set shape <- shape add_z_pt {i,valZ};" + "}" }, see = { "add_z" })
		public static IShape add_z_pt(final IShape geom, final GamaPoint data) {
			geom.getInnerGeometry().getCoordinates()[(int) data.x].z = data.y;
			return geom;
		}

		@operator("dem")
		@doc(value = "A polygon that is equivalent to the surface of the texture", special_cases = { "returns a point if the operand is lower or equal to 0." }, comment = "", examples = { "dem(dem,texture) --: returns a geometry as a rectangle of weight and height equal to the texture." }, see = {})
		public static IShape dem(final IScope scope, final GamaFile demFileName, final GamaFile textureFileName) {
			final IGraphics graphics = scope.getGraphics();
			if ( graphics instanceof IGraphics.OpenGL ) {
				((IGraphics.OpenGL) graphics).drawDEM(demFileName, textureFileName, scope.getSimulationScope()
					.getEnvelope(), 1.0);
			}
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return null;// new GamaShape(scope.getSimulationScope().getInnerGeometry());
		}

		@operator("dem")
		@doc(value = "A polygon that equivalent to the surface of the texture", special_cases = { "returns a point if the operand is lower or equal to 0." }, comment = "", examples = { "dem(dem,texture,z_factor) --: returns a geometry as a rectangle of weight and height equal to the texture." }, see = {})
		public static IShape dem(final IScope scope, final GamaFile demFileName, final GamaFile textureFileName,
			final Double z_factor) {
			final IGraphics graphics = scope.getGraphics();
			if ( graphics instanceof IGraphics.OpenGL ) {
				((IGraphics.OpenGL) graphics).drawDEM(demFileName, textureFileName, scope.getSimulationScope()
					.getEnvelope(), z_factor);
			}
			ILocation location;
			final IAgent a = scope.getAgentScope();
			location = a != null ? a.getLocation() : new GamaPoint(0, 0);
			return null;// new GamaShape(scope.getSimulationScope().getInnerGeometry());
		}
	}

}
