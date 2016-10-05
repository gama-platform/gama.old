/*********************************************************************************************
 *
 *
 * 'GamaShape.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.shape;

import static msi.gama.metamodel.shape.IShape.Type.SPHERE;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.util.AssertionFailedException;

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 25 ao�t 2010
 *
 *
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaShape implements IShape /* , IContainer */ {

	static Field envelopeField = null;
	static {

		try {
			envelopeField = Geometry.class.getDeclaredField("envelope");
		} catch (NoSuchFieldException | SecurityException e) {
		}

		if (envelopeField != null) {
			envelopeField.setAccessible(true);
		}
	}

	// private static final boolean USE_PREPARED_OPERATIONS = false;

	protected Geometry geometry;
	private IAgent agent;
	// This represents a waste of memory but it is necessary to maintain it, as
	// the Geometry does not give access
	// to a custom envelope builder
	// private Envelope3D envelope;

	// Property map to add all kinds of information (e.g to specify if the
	// geometry is a sphere, a
	// cube, etc...). Can be reused by subclasses (for example to store GIS
	// information)
	protected GamaMap attributes;

	public GamaShape(final Geometry geom) {
		setInnerGeometry(geom);
	}

	@Override
	public IType getType() {
		return Types.GEOMETRY;
	}

	public GamaShape(final Envelope3D env) {
		this(env == null ? null : env.toGeometry());
	}

	public GamaShape(final IShape geom) {
		this(geom, null);

	}

	/**
	 * Creates a GamaShape from a source and a (optional) geometry. If the
	 * geometry is null, the geometry of the source is used. In any case, we
	 * copy its attributes if present.
	 * 
	 * @param source
	 * @param geom
	 */

	public GamaShape(final IShape source, final Geometry geom) {
		this((Geometry) (geom == null ? source.getInnerGeometry().clone() : geom));
		mixAttributes(source);
	}

	/**
	 * This is where the attributes of this shape and the attributes of an
	 * incoming shape are mixed. The strategy is to only copy the geometrical
	 * attributes, leaving aside the attributes read from files or set by the
	 * agent. Any attribute-specific behavior should be introduced here
	 * 
	 * @param source
	 */
	private void mixAttributes(final IShape source) {
		if (source == null) {
			return;
		}
		final GamaMap<Object, Object> attr = (GamaMap) source.getAttributes();
		if (attr == null) {
			return;
		}
		final Object depth = attr.get(IShape.DEPTH_ATTRIBUTE);
		if (depth != null) {
			// we have a depth. Choose to copy it
			setAttribute(IShape.DEPTH_ATTRIBUTE, depth);
		}
		final Object type = attr.get(IShape.TYPE_ATTRIBUTE);
		if (type != null) {
			// we have a specific type of geometry. Choose to copy it.
			setAttribute(IShape.TYPE_ATTRIBUTE, type);
		}
		for (final Map.Entry entry : attr.entrySet()) {
			if (entry.getValue() != source) {
				setAttribute(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Same as above, but applies a (optional) rotation and (optional)
	 * translation to the geometry
	 * 
	 * @param source
	 *            cannot be null
	 * @param geom
	 *            can be null
	 * @param rotation
	 *            can be null, expressed in degrees
	 * @param newLocation
	 *            can be null
	 */
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final ILocation newLocation) {
		this(source, geom, rotation, null, newLocation);
	}

	/**
	 * Same as above, but applies a (optional) rotation along a given vector and
	 * (optional) translation to the geometry
	 * 
	 * @param source
	 *            cannot be null
	 * @param geom
	 *            can be null
	 * @param rotation
	 *            can be null, expressed in degrees
	 * @param newLocation
	 *            can be null
	 */

	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final GamaPoint vector,
			final ILocation newLocation) {
		this(source, geom);
		if (!isPoint() && rotation != null) {
			if (vector == null) {
				final Coordinate c = geometry.getCentroid().getCoordinate();
				geometry.apply(AffineTransform3D.createRotationOz(FastMath.toRadians(rotation), c.x, c.y));
			} else {
				final Vector3D v3D = new Vector3D(vector.getX(), vector.getY(), vector.getZ());
				final Rotation rot = new Rotation(v3D, FastMath.toRadians(rotation));
				for (final Coordinate c : this.getInnerGeometry().getCoordinates()) {
					final Vector3D result = rot.applyTo(new Vector3D(c.x, c.y, c.z));
					c.x = result.getX();
					c.y = result.getY();
					c.z = result.getZ();
				}
			}
		}

		if (newLocation != null) {
			setLocation(newLocation);
		}
	}

	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final ILocation newLocation,
			final GamaPoint bounds, final boolean isBoundingBox) {
		this(source, geom, rotation, null, newLocation, bounds, isBoundingBox);
	}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by
	 * specifying a bounding box or a set of coefficients.
	 * 
	 * @param source
	 *            cannot be null
	 * @param geom
	 *            can be null
	 * @param rotation
	 *            can be null, expressed in degrees
	 * @param newLocation
	 *            can be null
	 * @param isBoundingBox
	 *            indicates whether the previous parameter should be considered
	 *            as an absolute bounding box (width, height, ) or as a set of
	 *            coefficients.
	 */
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final GamaPoint vector,
			final ILocation newLocation, final GamaPoint bounds, final boolean isBoundingBox) {
		this(source, geom, rotation, vector, newLocation);
		if (bounds != null && !isPoint()) {
			if (getAttribute(IShape.TYPE_ATTRIBUTE) != SPHERE) {
				final GamaPoint previous = getLocation();
				// getEnvelope();
				final boolean flat = getEnvelope().isFlat();
				if (isBoundingBox) {
					geometry.apply(AffineTransform3D.createScaling(bounds.x / getEnvelope().getWidth(),
							bounds.y / getEnvelope().getHeight(), flat ? 1.0 : bounds.z / getEnvelope().getDepth()));
				} else {
					geometry.apply(AffineTransform3D.createScaling(bounds.x, bounds.y, bounds.z));
				}
				setEnvelope(null);
				setLocation(previous);
			} else {
				final Double scaling = FastMath.min(FastMath.min(bounds.x, bounds.y), bounds.z);
				final Double box = FastMath.max(FastMath.max(bounds.x, bounds.y), bounds.z);
				setAttribute(IShape.DEPTH_ATTRIBUTE,
						isBoundingBox ? box : (Double) getAttribute(IShape.DEPTH_ATTRIBUTE) * scaling);
			}
		}
	}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by a
	 * given coefficient
	 * 
	 * @param source
	 *            cannot be null
	 * @param geom
	 *            can be null
	 * @param rotation
	 *            can be null, expressed in degrees
	 * @param newLocation
	 *            can be null
	 */
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final ILocation newLocation,
			final Double scaling) {
		this(source, geom, rotation, newLocation);
		if (scaling != null && !isPoint()) {
			if (getAttribute(IShape.TYPE_ATTRIBUTE) != SPHERE) {
				final GamaPoint previous = getLocation();
				geometry.apply(AffineTransform3D.createScaling(scaling, scaling, scaling));
				setEnvelope(null);
				setLocation(previous);
			} else {
				setAttribute(IShape.DEPTH_ATTRIBUTE, (Double) getAttribute(IShape.DEPTH_ATTRIBUTE) * scaling);
			}
		}
	}

	@Override
	public boolean isMultiple() {
		return getInnerGeometry() instanceof GeometryCollection;
	}

	@Override
	public IList<GamaShape> getGeometries() {
		final IList<GamaShape> result = GamaListFactory.create(Types.GEOMETRY);
		if (isMultiple()) {
			for (int i = 0, n = getInnerGeometry().getNumGeometries(); i < n; i++) {
				result.add(new GamaShape(getInnerGeometry().getGeometryN(i)));
			}
		} else {
			result.add(this);
		}
		return result;
	}

	@Override
	public boolean isPoint() {
		// return isPoint;
		if (geometry == null) {
			return false;
		}
		return geometry.getNumPoints() == 1;
	}

	@Override
	public boolean isLine() {
		return getInnerGeometry() instanceof LineString || getInnerGeometry() instanceof MultiLineString;
	}

	@Override
	public String stringValue(final IScope scope) {
		if (geometry == null) {
			return "";
		}
		return SHAPE_WRITER.write(geometry);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if (isPoint()) {
			return getLocation().serialize(includingBuiltIn) + " as geometry";
		}
		if (isMultiple()) {
			return getGeometries().serialize(includingBuiltIn) + " as geometry";
		}
		final IList<GamaShape> holes = getHoles();
		String result = "";
		if (getInnerGeometry() instanceof LineString) {
			result = "polyline ("
					+ GamaListFactory.createWithoutCasting(Types.POINT, getPoints()).serialize(includingBuiltIn) + ")";
		} else {
			result = "polygon ("
					+ GamaListFactory.createWithoutCasting(Types.POINT, getPoints()).serialize(includingBuiltIn) + ")";
		}
		if (holes.isEmpty()) {
			return result;
		}
		for (final GamaShape g : holes) {
			result = "(" + result + ") - (" + g.serialize(includingBuiltIn) + ")";
		}
		return result;
	}

	@Override
	public String toString() {
		return getInnerGeometry().toText() + " at " + getLocation();
	}

	@Override
	public GamaPoint getLocation() {
		if (isPoint()) {
			return new GamaPoint(geometry.getCoordinate());
		}
		return getEnvelope().centre();
	}

	@Override
	public void setLocation(final ILocation l) {
		final GamaPoint previous = getLocation();
		final Coordinate location = GeometryUtils.toCoordinate(l);
		if (previous != null) {
			if (isPoint()) {
				geometry = GeometryUtils.FACTORY.createPoint(location);
				setEnvelope(Envelope3D.of(location));
			} else {
				// if ( isPoint ) {
				final double dx = location.x - previous.getX();
				final double dy = location.y - previous.getY();
				final double dz = location.z - previous.getZ();
				geometry.apply(new Translation(dx, dy, dz));
				// We move the envelope as well if it has been computed
				// if ( envelope != null ) {
				getEnvelope().translate(dx, dy, dz);
				// }
				// Changed to avoid side effects when computing displacements &
				// display at a given location at the same
				// time.
			}

			// geometry.geometryChanged();
		}
	}

	public GamaShape translatedTo(final IScope scope, final ILocation target) {
		final GamaShape result = copy(scope);
		result.setLocation(target);
		return result;
	}

	public static class Translation implements CoordinateFilter {

		double dx, dy, dz;

		Translation(final double x, final double y, final double z) {
			dx = x;
			dy = y;
			dz = z;
		}

		/**
		 * @see com.vividsolutions.jts.geom.CoordinateFilter#filter(com.vividsolutions.jts.geom.Coordinate)
		 */
		@Override
		public synchronized void filter(final Coordinate coord) {
			coord.x += dx;
			coord.y += dy;
			if (Double.isNaN(coord.z)) {
				coord.z = dz;
			} else {
				coord.z += dz;
			}
		}

	}

	final static PointLocator pl = new PointLocator();

	@Override
	public GamaShape getGeometry() {
		return this;
	}

	@Override
	public Double getArea() {
		// WARNING only 2D (XY) area
		return getInnerGeometry().getArea();
	}

	@Override
	public Double getVolume() {
		return getEnvelope().getVolume();
	}

	@Override
	public double getPerimeter() {
		return getInnerGeometry().getLength();
	}

	@Override
	public IList<GamaShape> getHoles() {
		final IList<GamaShape> holes = GamaListFactory.create(Types.GEOMETRY);
		if (getInnerGeometry() instanceof Polygon) {
			final Polygon p = (Polygon) getInnerGeometry();
			final int n = p.getNumInteriorRing();
			for (int i = 0; i < n; i++) {
				holes.add(new GamaShape(GeometryUtils.fromLineToPoylgon(p.getInteriorRingN(i))));
			}
		}
		return holes;
	}

	@Override
	public GamaPoint getCentroid() {
		if (geometry == null) {
			return null;
		}
		if (isPoint()) {
			return getLocation();
		}
		final Coordinate c = geometry.getCentroid().getCoordinate();
		c.z = computeAverageZOrdinate();
		return new GamaPoint(c);
	}

	@Override
	public GamaShape getExteriorRing(final IScope scope) {

		// WARNING Only in 2D
		Geometry result = getInnerGeometry();
		if (result instanceof Polygon) {
			result = ((Polygon) result).getExteriorRing();
		} else

		if (result instanceof MultiPolygon) {
			final MultiPolygon mp = (MultiPolygon) result;
			final LineString lines[] = new LineString[mp.getNumGeometries()];
			for (int i = 0; i < mp.getNumGeometries(); i++) {
				lines[i] = ((Polygon) mp.getGeometryN(i)).getExteriorRing();
			}
			result = GeometryUtils.FACTORY.createMultiLineString(lines);

		}
		return new GamaShape(result);
	}

	@Override
	public Double getWidth() {
		return getEnvelope().getWidth();
	}

	@Override
	public Double getHeight() {
		return getEnvelope().getHeight();
	}

	@Override
	public Double getDepth() {
		return (Double) this.getAttribute(IShape.DEPTH_ATTRIBUTE);
	}

	@Override
	public void setDepth(final double depth) {
		this.setAttribute(IShape.DEPTH_ATTRIBUTE, depth);
		this.setEnvelope(null);
	}

	@Override
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(getEnvelope());
	}

	@Override
	public IList<? extends ILocation> getPoints() {
		final IList<GamaPoint> result = GamaListFactory.create(Types.POINT);
		if (getInnerGeometry() == null) {
			return result;
		}
		final Coordinate[] points = getInnerGeometry().getCoordinates();
		for (final Coordinate c : points) {
			result.add(new GamaPoint(c));
		}
		return result;
	}

	@Override
	public Envelope3D getEnvelope() {
		if (geometry == null) {
			return null;
		}
		try {
			Envelope e = (Envelope) envelopeField.get(geometry);
			if (e == null || !(e instanceof Envelope3D)) {
				e = Envelope3D.of(this);
				envelopeField.set(geometry, e);
			}
			return (Envelope3D) e;
		} catch (IllegalArgumentException | IllegalAccessException e) {
		}
		return null;
		// if ( envelope == null ) {
		// envelope = Envelope3D.of(this);
		// }
		// return envelope;
	}

	@Override
	public IAgent getAgent() {
		return agent;
	}

	@Override
	public void setAgent(final IAgent a) {
		agent = a;
	}

	@Override
	public void setInnerGeometry(final Geometry geom) {
		if (geom == null) {
			geometry = null;
			return;
		}
		if (geom.isEmpty()) {
			// See Issue 725
			return;
		}
		if (geom instanceof GeometryCollection && geom.getNumGeometries() == 1) {
			geometry = geom.getGeometryN(0);
		} else {
			geometry = geom;
		}
		if (geometry != null && !GeometryUtils.isClockWise(geometry)) {
			GeometryUtils.changeClockWise(geometry);
		}
		// setEnvelope(null);
	}

	private void setEnvelope(final Envelope3D envelope) {
		if (geometry == null) {
			return;
		}
		try {
			envelopeField.set(geometry, envelope);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		// this.envelope = envelope;
	}

	@Override
	public void setGeometry(final IShape geom) {
		if (geom == null || geom == this) {
			return;
		}
		setInnerGeometry(geom.getInnerGeometry());
		mixAttributes(geom);
	}

	private double computeAverageZOrdinate() {
		double z = 0d;
		final Coordinate[] coords = geometry.getCoordinates();
		for (final Coordinate c : coords) {
			if (Double.isNaN(c.z)) {
				continue;
			}
			z += c.z;
		}
		return z / coords.length;
	}

	@Override
	public void dispose() {
		agent = null;
		if (attributes != null) {
			attributes.clear();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GamaShape) {
			final Geometry shape = ((GamaShape) o).geometry;
			// Fix a possible NPE when calling equalsExact with a null shape
			if (shape == null) {
				return geometry == null;
			}
			if (geometry == null) {
				return false;
			}
			return geometry.equalsExact(((GamaShape) o).geometry);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return geometry == null ? super.hashCode() : geometry.hashCode();
	}

	@Override
	public Geometry getInnerGeometry() {
		return geometry;
	}

	@Override
	public GamaShape copy(final IScope scope) {
		final GamaShape g = new GamaShape(this, (Geometry) geometry.clone());
		// We clone the envelope if it has been computed
		// if ( envelope != null ) {
		// g.envelope = new Envelope3D(envelope);
		// }
		// if ( attributes != null ) {
		// g.attributes = GamaMapFactory.create(attributes);
		// }
		// g.setLocation(location.copy(scope));
		return g;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		// WARNING Only 2D now
		if (g.isPoint()) {
			return pl.intersects((Coordinate) g.getLocation(), geometry);
		}
		// if ( !USE_PREPARED_OPERATIONS ) {

		try {
			return geometry.covers(g.getInnerGeometry());
		} catch (final TopologyException e) {
			try {
				return geometry.buffer(0).covers(g.getInnerGeometry().buffer(0));
			} catch (final TopologyException e2) {
				return false;
			}
		} catch (final AssertionFailedException e) {
			try {
				return geometry.buffer(0).covers(g.getInnerGeometry().buffer(0));
			} catch (final AssertionFailedException e2) {
				return false;
			}
		}
		// }
		// return operations().covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		// WARNING Only 2D now
		if (isPoint() && g.isPoint()) {
			return g.getLocation().euclidianDistanceTo(getLocation());
		}
		// if ( g.isPoint() ) { return euclidianDistanceTo(g.getLocation()); }
		// if ( isPoint ) { return g.euclidianDistanceTo(getLocation()); }
		// if ( !USE_PREPARED_OPERATIONS ) {
		// return getInnerGeometry().distance(g.getInnerGeometry());
		return getInnerGeometry().distance(g.getInnerGeometry());
		// }
		// return operations().getDistance(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		// WARNING Only 2D now
		if (isPoint()) {
			return g.euclidianDistanceTo(getLocation());
		}

		return getInnerGeometry().distance(g.getInnerGeometry());

		// ppd.initialize();
		// DistanceToPoint.computeDistance(geometry, (Coordinate) g, ppd);
		// return ppd.getDistance();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		// WARNING Only 2D now
		if (g.isPoint()) {
			return pl.intersects((Coordinate) g.getLocation(), getInnerGeometry());
		}
		// if ( !USE_PREPARED_OPERATIONS ) {
		try {
			return getInnerGeometry().intersects(g.getInnerGeometry());
		} catch (final TopologyException e) {
			try {
				return getInnerGeometry().buffer(0).intersects(g.getInnerGeometry().buffer(0));
			} catch (final TopologyException e2) {
				return false;
			}
		} catch (final AssertionFailedException e) {
			try {
				return getInnerGeometry().buffer(0).intersects(g.getInnerGeometry().buffer(0));
			} catch (final AssertionFailedException e2) {
				return false;
			}

		}

		// } {
		// return operations().intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		// WARNING Only 2D now
		if (g.isPoint()) {
			return pl.intersects((Coordinate) g.getLocation(), getInnerGeometry());
		}
		try {
			return geometry.crosses(g.getInnerGeometry());
		} catch (final TopologyException e) {
			try {
				return getInnerGeometry().buffer(0).crosses(g.getInnerGeometry().buffer(0));
			} catch (final TopologyException e2) {
				return false;
			}
		} catch (final AssertionFailedException e) {
			try {
				return getInnerGeometry().buffer(0).crosses(g.getInnerGeometry().buffer(0));
			} catch (final AssertionFailedException e2) {
				return false;
			}
		}
	}

	/**
	 * Used when the geometry is not affected to an agent and directly accessed
	 * by 'read' or 'get' operators. Can be used in Java too, of course, to
	 * retrieve any value stored in the shape
	 * 
	 * @param s
	 * @return the corresponding value of the attribute named 's' in the
	 *         feature, or null if it is not present
	 */
	@Override
	public Object getAttribute(final Object s) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(s);
	}

	@Override
	public void setAttribute(final Object key, final Object value) {
		getOrCreateAttributes().put(key, value);
	}

	@Override
	public GamaMap getOrCreateAttributes() {
		if (attributes == null) {
			attributes = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		}
		return attributes;
	}

	@Override
	public GamaMap getAttributes() {
		return attributes;
	}

	@Override
	public boolean hasAttribute(final Object key) {
		return attributes != null && attributes.containsKey(key);
	}

	/**
	 * Method getGeometricalType()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getGeometricalType()
	 */
	@Override
	public Type getGeometricalType() {
		if (hasAttribute(TYPE_ATTRIBUTE)) {
			return (Type) getAttribute(TYPE_ATTRIBUTE);
		}
		final String type = getInnerGeometry().getGeometryType();
		if (JTS_TYPES.containsKey(type)) {
			return JTS_TYPES.get(type);
		}
		return Type.NULL;
	}

}
