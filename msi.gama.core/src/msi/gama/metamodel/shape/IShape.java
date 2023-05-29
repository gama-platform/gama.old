/*******************************************************************************************************
 *
 * IShape.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama2 for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IAttributed;
import msi.gama.common.interfaces.IEnvelopeProvider;
import msi.gama.common.interfaces.ILocated;
import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.types.IType;

/**
 * Interface for objects that can be provided with a geometry (or which can be translated to a GamaGeometry)
 *
 * @author Alexis Drogoul
 * @since 16 avr. 2011
 * @modified November 2011 to include isPoint(), getInnerGeometry() and getEnvelope()
 *
 */

/**
 * The Interface IShape.
 */
@vars ({ @variable (
		name = "area",
		type = IType.FLOAT,
		doc = { @doc ("Returns the total area of this geometry") }),
		@variable (
				name = "volume",
				type = IType.FLOAT,
				doc = { @doc ("Returns the total volume of this geometry") }),
		@variable (
				name = "centroid",
				type = IType.POINT,
				doc = { @doc ("Returns the centroid of this geometry") }),
		@variable (
				name = "width",
				type = IType.FLOAT,
				doc = { @doc ("Returns the width (length on the x-axis) of the rectangular envelope of this  geometry") }),
		@variable (
				name = "attributes",
				type = IType.MAP,
				index = IType.STRING,
				doc = { @doc ("Returns the attributes kept by this geometry (the ones shared with the agent)") }),
		@variable (
				name = "depth",
				type = IType.FLOAT,
				doc = { @doc ("Returns the depth (length on the z-axis) of the rectangular envelope of this geometry") }),
		@variable (
				name = "height",
				type = IType.FLOAT,
				doc = { @doc ("Returns the height (length on the y-axis) of the rectangular envelope of this geometry") }),
		@variable (
				name = "points",
				type = IType.LIST,
				of = IType.POINT,
				doc = { @doc ("Returns the list of points that delimit this geometry. A point will return a list with itself") }),
		@variable (
				name = "envelope",
				type = IType.GEOMETRY,
				doc = { @doc ("Returns the envelope of this geometry (the smallest rectangle that contains the geometry)") }),
		@variable (
				name = "geometries",
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of geometries that compose this geometry, or a list containing the geometry itself if it is simple") }),
		@variable (
				name = "multiple",
				type = IType.BOOL,
				doc = { @doc ("Returns whether this geometry is composed of multiple geometries or not") }),
		@variable (
				name = "perimeter",
				type = IType.FLOAT,
				doc = { @doc ("Returns the length of the contour of this geometry") }),
		@variable (
				name = "holes",
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of holes inside this geometry as a list of geometries, and an emptly list if this geometry is solid") }),
		@variable (
				name = "contour",
				type = IType.GEOMETRY,
				doc = { @doc ("Returns the polyline representing the contour of this geometry") }) })
public interface IShape extends ILocated, IValue, IAttributed, IEnvelopeProvider {

	/** The jts types. */
	Map<String, Type> JTS_TYPES = GamaMapFactory.createUnordered();

	/** The threed types. */
	Set<Type> THREED_TYPES = new HashSet<>();

	/**
	 * The Enum Type.
	 */
	enum Type {

		/** The box. */
		BOX("3D"),

		/** The circle. */
		CIRCLE("3D"),

		/** The cone. */
		CONE("3D"),

		/** The cube. */
		CUBE("3D"),

		/** The square. */
		SQUARE("3D"),

		/** The rounded. */
		ROUNDED(""),

		/** The cylinder. */
		CYLINDER("3D"),

		/** The gridline. */
		GRIDLINE(""),

		/** The linearring. */
		LINEARRING("LinearRing"),

		/** The linestring. */
		LINESTRING("LineString"),

		/** The multilinestring. */
		MULTILINESTRING("MultiLineString"),

		/** The multipoint. */
		MULTIPOINT("MultiPoint"),

		/** The multipolygon. */
		MULTIPOLYGON("MultiPolygon"),

		/** The null. */
		NULL(""),

		/** The plan. */
		PLAN("3D"),

		/** The point. */
		POINT("Point"),

		/** The polygon. */
		POLYGON("Polygon"),

		/** The polyhedron. */
		POLYHEDRON("3D"),

		/** The polyplan. */
		POLYPLAN("3D"),

		/** The pyramid. */
		PYRAMID("3D"),

		/** The sphere. */
		SPHERE("3D"),

		/** The teapot. */
		TEAPOT("3D"),

		/** The linecylinder. */
		LINECYLINDER("3D"),

		/** The threed file. */
		THREED_FILE("");

		/**
		 * Instantiates a new type.
		 *
		 * @param name
		 *            the name
		 */
		Type(final String name) {
			if (name.isEmpty()) return;
			if ("3D".equals(name)) {
				THREED_TYPES.add(this);
			} else {
				JTS_TYPES.put(name, this);
			}
		}
	}

	/** The shape writer. */
	WKTWriter SHAPE_WRITER = new WKTWriter();

	/**
	 * Copy.
	 *
	 * @param scope
	 *            the scope
	 * @return the i shape
	 */
	@Override
	IShape copy(IScope scope);

	/**
	 * Covers.
	 *
	 * @param g
	 *            the g
	 * @return true, if successful
	 */
	boolean covers(IShape g);

	/**
	 * Partially overlaps.
	 *
	 * @param g
	 *            the g
	 * @return true, if successful
	 */
	boolean partiallyOverlaps(IShape g);

	/**
	 * Touches.
	 *
	 * @param g
	 *            the g
	 * @return true, if successful
	 */
	boolean touches(IShape g);

	/**
	 * Crosses.
	 *
	 * @param g
	 *            the g
	 * @return true, if successful
	 */
	boolean crosses(IShape g);

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Euclidian distance to.
	 *
	 * @param g
	 *            the g
	 * @return the double
	 */
	double euclidianDistanceTo(GamaPoint g);

	/**
	 * Euclidian distance to.
	 *
	 * @param g
	 *            the g
	 * @return the double
	 */
	double euclidianDistanceTo(IShape g);

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	IAgent getAgent();

	/**
	 * Gets the envelope.
	 *
	 * @return the envelope
	 */
	Envelope3D getEnvelope();

	/**
	 * Returns the geometrical type of this shape. May be computed dynamically (from the JTS inner geometry) or stored
	 * somewhere (in the attributes of the shape, using TYPE_ATTRIBUTE)
	 *
	 * @param g
	 * @return
	 */
	IShape.Type getGeometricalType();

	/**
	 * Gets the geometry.
	 *
	 * @return the geometry
	 */
	IShape getGeometry();

	/**
	 * Gets the inner geometry.
	 *
	 * @return the inner geometry
	 */
	Geometry getInnerGeometry();

	/**
	 * Intersects.
	 *
	 * @param g
	 *            the g
	 * @return true, if successful
	 */
	boolean intersects(IShape g);

	/**
	 * Checks if is line.
	 *
	 * @return true, if is line
	 */
	boolean isLine();

	/**
	 * Checks if is point.
	 *
	 * @return true, if is point
	 */
	boolean isPoint();

	/**
	 * Sets the agent.
	 *
	 * @param agent
	 *            the new agent
	 */
	void setAgent(IAgent agent);

	/**
	 * Sets the geometry.
	 *
	 * @param g
	 *            the new geometry
	 */
	void setGeometry(IShape g);

	/**
	 * Sets the inner geometry.
	 *
	 * @param intersection
	 *            the new inner geometry
	 */
	void setInnerGeometry(Geometry intersection);

	/**
	 * Sets the depth.
	 *
	 * @param depth
	 *            the new depth
	 */
	void setDepth(double depth);

	/**
	 * Gets the or create attributes.
	 *
	 * @return the or create attributes
	 */
	@Override
	@getter ("attributes")
	IMap<String, Object> getOrCreateAttributes();

	/**
	 * Checks if is multiple.
	 *
	 * @return true, if is multiple
	 */
	@getter ("multiple")
	boolean isMultiple();

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	@getter ("area")
	Double getArea();

	/**
	 * Gets the volume.
	 *
	 * @return the volume
	 */
	@getter ("volume")
	Double getVolume();

	/**
	 * Gets the perimeter.
	 *
	 * @return the perimeter
	 */
	@getter ("perimeter")
	double getPerimeter();

	/**
	 * Float value.
	 *
	 * @param scope
	 *            the scope
	 * @return the double
	 */
	@Override
	default double floatValue(final IScope scope) {
		return getPerimeter();
	}

	/**
	 * Int value.
	 *
	 * @param scope
	 *            the scope
	 * @return the int
	 */
	@Override
	default int intValue(final IScope scope) {
		return (int) getPerimeter();
	}

	/**
	 * Gets the holes.
	 *
	 * @return the holes
	 */
	@getter ("holes")
	IList<GamaShape> getHoles();

	/**
	 * Gets the centroid.
	 *
	 * @return the centroid
	 */
	@getter ("centroid")
	GamaPoint getCentroid();

	/**
	 * Gets the exterior ring.
	 *
	 * @param scope
	 *            the scope
	 * @return the exterior ring
	 */
	@getter ("contour")
	GamaShape getExteriorRing(IScope scope);

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	@getter ("width")
	Double getWidth();

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	@getter ("height")
	Double getHeight();

	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 */
	@getter ("depth")
	Double getDepth();

	/**
	 * Gets the max dimension.
	 *
	 * @return the max dimension
	 */
	default Double getMaxDimension() {
		return Math.max(Math.max(getHeight(), getWidth()), getDepth() == null ? 0d : getDepth());
	}

	/**
	 * Gets the geometric envelope.
	 *
	 * @return the geometric envelope
	 */
	@getter ("envelope")
	GamaShape getGeometricEnvelope();

	/**
	 * Gets the points.
	 *
	 * @return the points
	 */
	@getter ("points")
	IList<GamaPoint> getPoints();

	/**
	 * Gets the geometries.
	 *
	 * @return the geometries
	 */
	@getter ("geometries")
	IList<? extends IShape> getGeometries();

	/**
	 * Copy only the attributes that support defining the shape
	 *
	 * @param other
	 */
	default void copyShapeAttributesFrom(final IShape other) {
		final Double d = other.getDepth();
		if (d != null) { setDepth(d); }
		final Type t = other.getGeometricalType();
		if (THREED_TYPES.contains(t)) { setGeometricalType(t); }
	}

	/**
	 * Sets the geometrical type.
	 *
	 * @param t
	 *            the new geometrical type
	 */
	void setGeometricalType(Type t);

	/**
	 * Compute envelope.
	 *
	 * @param scope
	 *            the scope
	 * @return the envelope 3 D
	 */
	@Override
	default Envelope3D computeEnvelope(final IScope scope) {
		return getEnvelope();
	}

}
