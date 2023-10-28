/*******************************************************************************************************
 *
 * GamaShape.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.Scaling3D;
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

/**
 * The Class GamaShape.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 17 sept. 2023
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaShape implements IShape {

	/**
	 * The Class ShapeData.
	 */
	public static class ShapeData {

		/** The depth. */
		private Double depth;

		/** The type. */
		private Type type;
	}

	/** The geometry. */
	protected Geometry geometry;

	/** The agent. */
	private IAgent agent;

	/** The attributes. */
	protected IMap<String, Object> attributes;

	/**
	 * Instantiates a new gama shape.
	 *
	 * @param geom
	 *            the geom
	 * @deprecated use GamaShapeFactory instead
	 */
	@Deprecated
	public GamaShape(final Geometry geom) {
		setInnerGeometry(geom);
	}

	/**
	 * Instantiates a new gama shape.
	 *
	 * @param env
	 *            the env
	 * @deprecated use GamaShapeFactory instead
	 */
	@Deprecated
	public GamaShape(final Envelope3D env) {
		this(env == null ? null : env.toGeometry());
	}

	/**
	 * Instantiates a new gama shape.
	 *
	 * @param geom
	 *            the geom
	 * @deprecated use GamaShapeFactory instead
	 */
	@Deprecated
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
	 * @deprecated use GamaShapeFactory instead
	 */

	@Deprecated
	public GamaShape(final IShape source, final Geometry geom) {
		this(geom == null ? source.getInnerGeometry().copy() : geom);
		withAttributesOf(source);
	}

	/**
	 * Instantiates a new gama shape.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param source
	 *            the source
	 * @param geom
	 *            the geom
	 * @param rotation
	 *            the rotation
	 * @param newLocation
	 *            the new location
	 * @date 18 sept. 2023
	 * @deprecated use GamaShapeFactory instead
	 */
	@Deprecated
	public GamaShape(final IShape source, final Geometry geom, final AxisAngle rotation, final GamaPoint newLocation) {
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
	 * Instantiates a new gama shape.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param source
	 *            the source
	 * @param geom
	 *            the geom
	 * @param rotation
	 *            the rotation
	 * @param newLocation
	 *            the new location
	 * @param bounds
	 *            the bounds
	 * @param isBoundingBox
	 *            the is bounding box
	 * @date 18 sept. 2023
	 * @deprecated use GamaShapeFactory instead
	 */
	@Deprecated
	public GamaShape(final IShape source, final Geometry geom, final AxisAngle rotation, final GamaPoint newLocation,
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
	 * Instantiates a new gama shape.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param source
	 *            the source
	 * @param geom
	 *            the geom
	 * @param rotation
	 *            the rotation
	 * @param newLocation
	 *            the new location
	 * @param scaling
	 *            the scaling
	 * @date 18 sept. 2023
	 * @deprecated use GamaShapeFactory instead
	 */
	@Deprecated
	public GamaShape(final IShape source, final Geometry geom, final AxisAngle rotation, final GamaPoint newLocation,
			final Double scaling) {
		this(source, geom, rotation, newLocation);
		if (scaling != null && !isPoint()) {
			final GamaPoint previous = getLocation();
			geometry.apply(Scaling3D.of(scaling));
			setLocation(previous);
			if (is3D()) { setDepth(getDepth() * scaling); }
		}
	}

	/**
	 * This is where the attributes of this shape and the attributes of an incoming shape are mixed. The default
	 * strategy is to copy all the attributes to this, except the attributes that represent the shape itself
	 *
	 * @param source
	 */
	public GamaShape withAttributesOf(final IShape source) {
		if (source == null) return this;
		copyShapeAttributesFrom(source);
		source.forEachAttribute((key, val) -> {
			if (val != source) { setAttribute(key, val); }
			return true;
		});
		return this;
	}

	/**
	 * With scaling. Applies a scaling to the geometry. Should only be used at construction time
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scaling
	 *            the scaling
	 * @return the gama shape
	 * @date 17 sept. 2023
	 */
	public GamaShape withScaling(final Double scaling) {
		if (scaling != null && !isPoint()) {
			final GamaPoint previous = getLocation();
			geometry.apply(Scaling3D.of(scaling));
			setLocation(previous);
			if (is3D()) { setDepth(getDepth() * scaling); }
		}
		return this;
	}

	/**
	 * With scaling. Applies a scaling described by bounds, either as a bounding box or as factors along the axes
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param bounds
	 *            the bounds
	 * @param isBoundingBox
	 *            the is bounding box
	 * @return the gama shape
	 * @date 17 sept. 2023
	 */
	public GamaShape withScaling(final Scaling3D bounds, final boolean isBoundingBox) {
		if (bounds != null && !isPoint()) {
			final Envelope3D env = getEnvelope();
			final GamaPoint previous = getLocation();
			if (isBoundingBox) {
				geometry.apply(bounds.asBoundingBoxIn(env));
			} else {
				geometry.apply(bounds);
			}
			setLocation(previous);
			if (is3D()) { setDepth(isBoundingBox ? bounds.getZ() : getDepth() * bounds.getZ()); }
		}
		return this;
	}

	/**
	 * With location. Translates the geometry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param newLocation
	 *            the new location
	 * @return the gama shape
	 * @date 17 sept. 2023
	 */
	public GamaShape withLocation(final GamaPoint newLocation) {
		if (newLocation != null) { setLocation(newLocation); }
		return this;
	}

	/**
	 * With rotation. Applies a (possibly null) rotation to the geometry.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param rotation
	 *            the rotation
	 * @return the gama shape
	 * @date 17 sept. 2023
	 */
	public GamaShape withRotation(final AxisAngle rotation) {
		if (!isPoint() && rotation != null) {
			Double normalZ = null;
			if (is3D()) { normalZ = getContourCoordinates(geometry).getNormal(true).z; }
			rotate(geometry, getLocation(), rotation);
			if (normalZ != null) {
				final Double normalZ2 = getContourCoordinates(geometry).getNormal(true).z;
				if (normalZ > 0 && normalZ2 < 0) { setDepth(-getDepth()); }
			}
		}
		return this;
	}

	@Override
	public boolean isMultiple() { return getInnerGeometry() instanceof GeometryCollection; }

	/**
	 * Checks if is 3d.
	 *
	 * @return true, if is 3d
	 */
	public boolean is3D() {
		return getDepth() != null;
	}

	@Override
	public IList<GamaShape> getGeometries() {
		final IList<GamaShape> result = GamaListFactory.create(Types.GEOMETRY);
		if (isMultiple()) {
			for (int i = 0, n = getInnerGeometry().getNumGeometries(); i < n; i++) {
				result.add(GamaShapeFactory.createFrom(getInnerGeometry().getGeometryN(i)));
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
	public String serializeToGaml(final boolean includingBuiltIn) {
		if (isPoint()) return getLocation().serializeToGaml(includingBuiltIn) + " as geometry";
		if (isMultiple()) return getGeometries().serializeToGaml(includingBuiltIn) + " as geometry";
		final IList<GamaShape> holes = getHoles();
		String result = "";
		if (getInnerGeometry() instanceof LineString) {
			result = "polyline (" + getPoints().serializeToGaml(includingBuiltIn) + ")";
		} else {
			result = "polygon (" + getPoints().serializeToGaml(includingBuiltIn) + ")";
		}
		if (holes.isEmpty()) return result;
		for (final GamaShape g : holes) { result = "(" + result + ") - (" + g.serializeToGaml(includingBuiltIn) + ")"; }
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
	public GamaPoint setLocation(final GamaPoint l) {
		if (isPoint()) {
			geometry = GEOMETRY_FACTORY.createPoint(l);
		} else {
			translate(geometry, getLocation(), l);
		}
		return l;
	}

	/**
	 * Translated to.
	 *
	 * @param scope
	 *            the scope
	 * @param target
	 *            the target
	 * @return the gama shape
	 */
	public GamaShape translatedTo(final IScope scope, final GamaPoint target) {
		final GamaShape result = copy(scope);
		result.setLocation(target);
		return result;
	}

	@Override
	public GamaShape getGeometry() { return this; }

	@Override
	public Double getArea() {
		// WARNING only 2D (XY) area
		return getInnerGeometry().getArea();
	}

	@Override
	public Double getVolume() {
		final Double d = getDepth();
		if (d == 0) return 0d;
		final Type shapeType = getGeometricalType();
		// TODO : should put any specific shape volume calculation here !!!
		return switch (shapeType) {
			case SPHERE -> 4 / (double) 3 * Maths.PI * Maths.pow(getWidth() / 2.0, 3);
			case CONE -> 1 / (double) 3 * Maths.PI * Maths.pow(getWidth() / 2.0, 2) * d;
			case PYRAMID -> Maths.pow(getWidth(), 2) * d / 3;
			case THREED_FILE, NULL -> {
				final Envelope3D env3D = getEnvelope();
				yield env3D == null ? Envelope3D.of(this.getGeometry().getInnerGeometry()).getVolume()
						: env3D.getVolume();
			}
			default -> getArea() * d;
		};
	}

	@Override
	public double getPerimeter() {
		if (geometry instanceof GeometryCollection) {
			final int[] result = new int[1];
			GeometryUtils.applyToInnerGeometries((GeometryCollection) geometry,
					g -> result[0] += GeometryUtils.getContourCoordinates(g).getLength());
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
				holes.add(GamaShapeFactory
						.createFrom(GEOMETRY_FACTORY.createPolygon(p.getInteriorRingN(i).getCoordinates())));
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

		if (result instanceof MultiPolygon mp) {
			final LineString lines[] = new LineString[mp.getNumGeometries()];
			for (int i = 0; i < mp.getNumGeometries(); i++) {
				lines[i] = ((Polygon) mp.getGeometryN(i)).getExteriorRing();
			}
			result = GEOMETRY_FACTORY.createMultiLineString(lines);

		}
		return GamaShapeFactory.createFrom(result);
	}

	/**
	 * Gets the data.
	 *
	 * @param createIt
	 *            the create it
	 * @return the data
	 */
	private ShapeData getData(final boolean createIt) {
		final Geometry g = getInnerGeometry();
		if (g == null) return null;
		Object o = g.getUserData();
		if (o == null) {
			if (!createIt) return null;
			o = new ShapeData();
			g.setUserData(o);
		}
		return (ShapeData) o;
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
	public IList<GamaPoint> getPoints() {
		if (getInnerGeometry() == null) return create(POINT);
		return GamaListFactory.wrap(POINT, GeometryUtils.getPointsOf(getInnerGeometry()));
	}

	@Override
	public Envelope3D getEnvelope() {
		if (geometry == null) return null;
		return Envelope3D.of(this);
	}

	@Override
	public IAgent getAgent() { return agent; }

	@Override
	public void setAgent(final IAgent a) { agent = a; }

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
		withAttributesOf(geom);
	}

	/**
	 * Compute average Z ordinate.
	 *
	 * @return the double
	 */
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
		if (attributes != null) {
			attributes.clear();
			attributes = null;
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GamaShape gs) {
			final Geometry shape = gs.geometry;
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
	}

	@Override
	public Geometry getInnerGeometry() { return geometry; }

	@Override
	public GamaShape copy(final IScope scope) {
		return GamaShapeFactory.createFrom(geometry.copy());
	}

	/**
	 *
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		return GeometryUtils.robustCovers(this, g);
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
	public double euclidianDistanceTo(final GamaPoint g) {
		// WARNING Only 2D now
		if (isPoint()) return g.euclidianDistanceTo(getLocation());
		return getInnerGeometry().distance(g.getInnerGeometry());
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		return GeometryUtils.robustIntersects(this, g);
	}

	@Override
	public boolean crosses(final IShape g) {
		return GeometryUtils.robustCrosses(this, g);
	}

	@Override
	public boolean partiallyOverlaps(final IShape g) {
		return GeometryUtils.robustPartiallyOverlaps(this, g);
	}

	@Override
	public boolean touches(final IShape g) {
		return GeometryUtils.robustTouches(this, g);
	}

	@Override
	public IMap<String, Object> getAttributes(final boolean createIfNedeed) {
		if (attributes == null && createIfNedeed) { attributes = GamaMapFactory.create(Types.STRING, Types.NO_TYPE); }
		return attributes;
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
	public IType getGamlType() { return Types.GEOMETRY; }

}
