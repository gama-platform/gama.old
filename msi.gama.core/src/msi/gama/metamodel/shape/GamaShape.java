/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.GamaShape.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.geometry.GeometryUtils.getContourCoordinates;
import static msi.gama.common.geometry.GeometryUtils.rotate;
import static msi.gama.common.geometry.GeometryUtils.translate;
import static msi.gama.util.GamaListFactory.create;
import static msi.gaml.types.Types.POINT;

import org.locationtech.jts.algorithm.PointLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.util.AssertionFailedException;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.common.interfaces.IAttributed;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.operators.Maths;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 25 ao�t 2010
 *
 *
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaShape implements IShape {

	class ShapeData {
		private Double depth;
		private Type type;
	}

	protected Geometry geometry;
	private IAgent agent;
	protected IMap<String, Object> attributes;

	public GamaShape(final Geometry geom) {
		setInnerGeometry(geom);
	}

	@Override
	public IType getGamlType() {
		return Types.GEOMETRY;
	}

	public GamaShape(final Envelope3D env) {
		this(env == null ? null : env.toGeometry());
	}

	public GamaShape(final IShape geom) {
		this(geom, null);

	}

	/**
	 * Creates a GamaShape from a source and a (optional) geometry. If the geometry is null, the geometry of the source
	 * is used. In any case, we copy its attributes if present and if copyAttributes is true
	 *
	 * @param source
	 * @param geom
	 * @param copyAttributes
	 */

	public GamaShape(final IShape source, final Geometry geom) {
		this((Geometry) (geom == null ? source.getInnerGeometry().clone() : geom));
		mixAttributes(source);
	}

	/**
	 * This is where the attributes of this shape and the attributes of an incoming shape are mixed. The default
	 * strategy is to copy all the attributes to this
	 *
	 * @param source
	 */
	private void mixAttributes(final IShape source) {
		if (source == null) return;
		// final GamaMap<String, Object> attr = (GamaMap<String, Object>) source.getAttributes();
		copyShapeAttributesFrom(source);
		if (source instanceof GamaShape) {
			final GamaShape shape = (GamaShape) source;
			if (shape.attributes != null) {
				getOrCreateAttributes();
				shape.attributes.forEach((key, val) -> {
					if (val != source) { attributes.put(key, val); }
				});
			}
		} else {
			// if (attr == null) { return; }
			source.forEachAttribute((key, val) -> {
				if (val != source) { setAttribute(key, val); }
				return true;
			});
			// for (final Map.Entry<String, Object> entry : attr.entrySet()) {
			// if (entry.getValue() != source) {
			// setAttribute(entry.getKey(), entry.getValue());
			// }
			// }
		}
	}

	@Override
	public void copyAttributesOf(final IAttributed source) {
		if (source instanceof GamaShape) {
			final GamaShape shape = (GamaShape) source;
			if (shape.attributes != null) {
				getOrCreateAttributes();
				attributes.putAll(shape.attributes);
			}
		} else {
			IShape.super.copyAttributesOf(source);
		}

	}

	/**
	 * Same as above, but applies a (optional) rotation around a given vector and (optional) translation to the geometry
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

	public GamaShape(final IShape source, final Geometry geom, final AxisAngle rotation, final ILocation newLocation) {
		this(source, geom);
		if (!isPoint() && rotation != null) {
			Double normalZ = null;
			if (is3D()) { normalZ = getContourCoordinates(geometry).getNormal(true).z; }
			rotate(geometry, getLocation(), rotation);
			if (normalZ != null) {
				final Double normalZ2 = getContourCoordinates(geometry).getNormal(true).z;
				if (normalZ > 0 && normalZ2 < 0) { setDepth(-getDepth()); }
			}
		}
		if (newLocation != null) { setLocation(newLocation); }
	}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by specifying a bounding box or a set of
	 * coefficients.
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
	 *            indicates whether the previous parameter should be considered as an absolute bounding box (width,
	 *            height, ) or as a set of coefficients.
	 */
	public GamaShape(final IShape source, final Geometry geom, final AxisAngle rotation, final ILocation newLocation,
			final Scaling3D bounds, final boolean isBoundingBox) {
		this(source, geom, rotation, newLocation);
		if (bounds != null && !isPoint()) {
			final Envelope3D env = getEnvelope();
			final GamaPoint previous = getLocation();
			// final boolean flat = env.isFlat();
			if (isBoundingBox) {
				geometry.apply(bounds.asBoundingBoxIn(env));
			} else {
				geometry.apply(bounds);
			}
			setLocation(previous);
			if (is3D()) { setDepth(isBoundingBox ? bounds.getZ() : getDepth() * bounds.getZ()); }
		}
	}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by a given coefficient
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
	public GamaShape(final IShape source, final Geometry geom, final AxisAngle rotation, final ILocation newLocation,
			final Double scaling) {
		this(source, geom, rotation, newLocation);
		if (scaling != null && !isPoint()) {
			final GamaPoint previous = getLocation();
			geometry.apply(Scaling3D.of(scaling));
			setLocation(previous);
			if (is3D()) { setDepth(getDepth() * scaling); }
		}
	}

	@Override
	public boolean isMultiple() {
		return getInnerGeometry() instanceof GeometryCollection;
	}

	public boolean is3D() {
		return getDepth() != null;
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
		if (geometry == null) return false;
		return geometry.getNumPoints() == 1;
	}

	@Override
	public boolean isLine() {
		return getInnerGeometry() instanceof LineString || getInnerGeometry() instanceof MultiLineString;
	}

	@Override
	public String stringValue(final IScope scope) {
		if (geometry == null) return "";
		return SHAPE_WRITER.write(geometry);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if (isPoint()) return getLocation().serialize(includingBuiltIn) + " as geometry";
		if (isMultiple()) return getGeometries().serialize(includingBuiltIn) + " as geometry";
		final IList<GamaShape> holes = getHoles();
		String result = "";
		if (getInnerGeometry() instanceof LineString) {
			result = "polyline (" + getPoints().serialize(includingBuiltIn) + ")";
		} else {
			result = "polygon (" + getPoints().serialize(includingBuiltIn) + ")";
		}
		if (holes.isEmpty()) return result;
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
		if (isPoint()) return (GamaPoint) geometry.getCoordinate();
		return getContourCoordinates(geometry).getCenter();
	}

	@Override
	public void setLocation(final ILocation l) {
		if (isPoint()) {
			geometry = GEOMETRY_FACTORY.createPoint(l.toGamaPoint());
		} else {
			translate(geometry, getLocation(), l.toGamaPoint());
		}
	}

	public GamaShape translatedTo(final IScope scope, final ILocation target) {
		final GamaShape result = copy(scope);
		result.setLocation(target);
		return result;
	}

	public final static PointLocator pl = new PointLocator();

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
		final Double d = getDepth();
		if (d == 0)
			return 0d;
		else {
			final Type shapeType = getGeometricalType();
			// TODO : should put any specific shape volume calculation here !!!
			switch (shapeType) {
				case SPHERE:
					return 4 / (double) 3 * Maths.PI * Maths.pow(getWidth() / 2.0, 3);
				case CONE:
					return 1 / (double) 3 * Maths.PI * Maths.pow(getWidth() / 2.0, 2) * d;
				case PYRAMID:
					return Maths.pow(getWidth(), 2) * d / 3;
				case THREED_FILE:
				case NULL:
					final Envelope3D env3D = getEnvelope();
					return env3D == null ? Envelope3D.of(this.getGeometry().getInnerGeometry()).getVolume()
							: env3D.getVolume();
				default:
					return getArea() * d;
			}
		}
	}

	@Override
	public double getPerimeter() {
		if (geometry instanceof GeometryCollection) {
			final int[] result = new int[1];
			GeometryUtils.applyToInnerGeometries((GeometryCollection) geometry,
					(g) -> result[0] += GeometryUtils.getContourCoordinates(g).getLength());
			return result[0];
		}
		final ICoordinates seq = GeometryUtils.getContourCoordinates(geometry);
		return seq.getLength();
	}

	@Override
	public IList<GamaShape> getHoles() {
		final IList<GamaShape> holes = GamaListFactory.create(Types.GEOMETRY);
		if (getInnerGeometry() instanceof Polygon) {
			final Polygon p = (Polygon) getInnerGeometry();
			final int n = p.getNumInteriorRing();
			for (int i = 0; i < n; i++) {
				holes.add(new GamaShape(GEOMETRY_FACTORY.createPolygon(p.getInteriorRingN(i).getCoordinates())));
			}
		}
		return holes;
	}

	@Override
	public GamaPoint getCentroid() {
		if (geometry == null) return null;
		if (isPoint()) return getLocation();
		final Coordinate c = geometry.getCentroid().getCoordinate();
		c.z = computeAverageZOrdinate();
		return (GamaPoint) c;
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
			result = GEOMETRY_FACTORY.createMultiLineString(lines);

		}
		return new GamaShape(result);
	}

	private ShapeData getData(final boolean createIt) {
		final Geometry g = getInnerGeometry();
		if (g == null) return null;
		Object o = g.getUserData();
		if (o == null) {
			if (createIt) {
				o = new ShapeData();
				g.setUserData(o);
			} else
				return null;
		}
		return (ShapeData) o;
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
		final ShapeData data = getData(false);
		return data == null ? null : data.depth;
	}

	@Override
	public void setDepth(final double depth) {
		final ShapeData data = getData(true);
		if (data != null) { data.depth = depth; }
	}

	@Override
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(getEnvelope());
	}

	@Override
	public IList<? extends ILocation> getPoints() {
		if (getInnerGeometry() == null) return create(POINT);
		return (IList<? extends ILocation>) GamaListFactory.wrap(POINT, getInnerGeometry().getCoordinates());
	}

	@Override
	public Envelope3D getEnvelope() {
		if (geometry == null) return null;
		return Envelope3D.of(this);
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
		if (geom.isEmpty()) // See Issue 725
			return;
		if (geom instanceof GeometryCollection && geom.getNumGeometries() == 1) {
			geometry = geom.getGeometryN(0);
		} else {
			geometry = geom;
		}
	}

	@Override
	public void setGeometry(final IShape geom) {
		if (geom == null || geom == this) return;
		setInnerGeometry(geom.getInnerGeometry());
		mixAttributes(geom);
	}

	private double computeAverageZOrdinate() {
		double z = 0d;
		final Coordinate[] coords = geometry.getCoordinates();
		for (final Coordinate c : coords) {
			if (Double.isNaN(c.z)) { continue; }
			z += c.z;
		}
		return z / coords.length;
	}

	@Override
	public void dispose() {
		agent = null;
		if (attributes != null) { attributes.clear(); }
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GamaShape) {
			final Geometry shape = ((GamaShape) o).geometry;
			// Fix a possible NPE when calling equalsExact with a null shape
			if (shape == null) return geometry == null;
			if (geometry == null) return false;
			return geometry.equalsExact(((GamaShape) o).geometry);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (geometry == null) return 0;
		return geometry.hashCode();
		// return super.hashCode();
		// if (geomtry == null) return s
		// return GeometryUtils.getContourCoordinates(geometry)..
		// return geometry == null ? super.hashCode() : geometry.hashCode();
	}

	@Override
	public Geometry getInnerGeometry() {
		return geometry;
	}

	@Override
	public GamaShape copy(final IScope scope) {
		final Geometry gg = (Geometry) geometry.clone();
		return new GamaShape(this, gg);
	}

	/**
	 *
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		// WARNING Only 2D now
		if (g.isPoint()) return pl.intersects((Coordinate) g.getLocation(), geometry);
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
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		// WARNING Only 2D now
		if (isPoint() && g.isPoint()) return g.getLocation().euclidianDistanceTo(getLocation());
		return getInnerGeometry().distance(g.getInnerGeometry());
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		// WARNING Only 2D now
		if (isPoint()) return g.euclidianDistanceTo(getLocation());
		return getInnerGeometry().distance(g.getInnerGeometry());
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		// WARNING Only 2D now
		if (g.isPoint()) return pl.intersects((Coordinate) g.getLocation(), getInnerGeometry());
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
	}

	@Override
	public boolean crosses(final IShape g) {
		// WARNING Only 2D now
		if (g.isPoint()) return pl.intersects((Coordinate) g.getLocation(), getInnerGeometry());
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
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Used when the geometry is not affected to an agent and directly accessed by 'read' or 'get' operators. Can be
	 * used in Java too, of course, to retrieve any value stored in the shape
	 *
	 * @param s
	 * @return the corresponding value of the attribute named 's' in the feature, or null if it is not present
	 */
	@Override
	public Object getAttribute(final String s) {
		if (attributes == null) return null;
		return attributes.get(s);
	}

	@Override
	public void setAttribute(final String key, final Object value) {
		getOrCreateAttributes().put(key, value);
	}

	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		if (attributes == null) { attributes = GamaMapFactory.create(Types.STRING, Types.NO_TYPE); }
		return attributes;
	}

	// @Override
	// public GamaMap getAttributes() {
	// return attributes;
	// }

	@Override
	public boolean hasAttribute(final String key) {
		return attributes != null && attributes.containsKey(key);
	}

	/**
	 * Method getGeometricalType()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometricalType()
	 */
	@Override
	public Type getGeometricalType() {
		final ShapeData data = getData(false);
		Type type = data == null ? null : data.type;
		if (type == null) {
			final String tt = getInnerGeometry().getGeometryType();
			if (JTS_TYPES.containsKey(tt)) {
				type = JTS_TYPES.get(tt);
			} else {
				type = Type.NULL;
			}
			if (data != null) { data.type = type; }
		}
		return type;
	}

	/**
	 * Invoked when a geometrical primitive undergoes an operation (like minus(), plus()) that makes it change
	 */
	public void losePredefinedProperty() {
		if (THREED_TYPES.contains(getGeometricalType())) { setGeometricalType(Type.POLYHEDRON); }
	}

	@Override
	public void setGeometricalType(final Type t) {
		final ShapeData data = getData(true);
		if (data != null) { data.type = t; }
	}

	@Override
	public void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {
		if (attributes == null) return;
		attributes.forEachPair(visitor);
	}

}
