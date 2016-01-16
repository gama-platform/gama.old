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

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 25 ao�t 2010
 *
 *
 *
 */
@vars({ @var(name = "area", type = IType.FLOAT, doc = { @doc("Returns the total area of this geometry") }),
	@var(name = "volume", type = IType.FLOAT, doc = { @doc("Returns the total volume of this geometry") }),
	@var(name = "centroid", type = IType.POINT, doc = { @doc("Returns the centroid of this geometry") }),
	@var(name = "width",
		type = IType.FLOAT,
		doc = { @doc("Returns the width (length on the x-axis) of the rectangular envelope of this  geometry") }),
	@var(name = "depth",
		type = IType.FLOAT,
		doc = { @doc("Returns the depth (length on the z-axis) of the rectangular envelope of this geometry") }),
	@var(name = "height",
		type = IType.FLOAT,
		doc = { @doc("Returns the height (length on the y-axis) of the rectangular envelope of this geometry") }),
	@var(name = "points",
		type = IType.LIST,
		of = IType.POINT,
		doc = {
			@doc("Returns the list of points that delimit this geometry. A point will return a list with itself") }),
	@var(name = "envelope",
		type = IType.GEOMETRY,
		doc = { @doc("Returns the envelope of this geometry (the smallest rectangle that contains the geometry)") }),
	@var(name = "geometries",
		type = IType.LIST,
		of = IType.GEOMETRY,
		doc = {
			@doc("Returns the list of geometries that compose this geometry, or a list containing the geometry itself if it is simple") }),
	@var(name = "multiple",
		type = IType.BOOL,
		doc = { @doc("Returns whether this geometry is composed of multiple geometries or not") }),
	@var(name = "holes",
		type = IType.LIST,
		of = IType.GEOMETRY,
		doc = {
			@doc("Returns the list of holes inside this geometry as a list of geometries, and an emptly list if this geometry is solid") }),
	@var(name = "contour",
		type = IType.GEOMETRY,
		doc = { @doc("Returns the polyline representing the contour of this geometry") }) })
public class GamaShape implements IShape /* , IContainer */ {

	// private static final boolean USE_PREPARED_OPERATIONS = false;

	protected Geometry geometry;
	// protected volatile ILocation location;
	// private boolean isPoint;
	private IAgent agent;
	// This represents a waste of memory but it is necessary to maintain it, as the Geometry does not give access
	// to a custom envelope builder
	private Envelope3D envelope;

	// Property map to add all kinds of information (e.g to specify if the geometry is a sphere, a
	// cube, etc...). Can be reused by subclasses (for example to store GIS information)
	protected GamaMap attributes;

	public GamaShape(final Geometry geom) {
		setInnerGeometry(geom);
	}

	@Override
	public IType getType() {
		return Types.GEOMETRY;
	}

	public GamaShape(final Geometry geom, final boolean computeLocation) {
		setGeometry(geom, computeLocation);
	}

	public GamaShape(final Envelope3D env) {
		this(env.toGeometry());
	}

	public GamaShape(final IShape geom) {
		this(geom, null);

	}

	/**
	 * Creates a GamaShape from a source and a (optional) geometry. If the geometry is null, the geometry of the source
	 * is used. In any case, we copy its attributes if present.
	 * @param source
	 * @param geom
	 */

	public GamaShape(final IShape source, final Geometry geom) {
		this((Geometry) (geom == null ? source.getInnerGeometry().clone() : geom));
		GamaMap attr = source.getAttributes();
		if ( attr != null ) {
			attributes =
				GamaMapFactory.createWithoutCasting(attr.getType().getKeyType(), attr.getType().getContentType(), attr);
		}
	}

	/**
	 * Same as above, but applies a (optional) rotation and (optional) translation to the geometry
	 * @param source cannot be null
	 * @param geom can be null
	 * @param rotation can be null, expressed in degrees
	 * @param newLocation can be null
	 */
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final ILocation newLocation) {
		this(source, geom);
		if ( !isPoint() && rotation != null ) {
			Coordinate c = geometry.getCentroid().getCoordinate();
			geometry.apply(AffineTransform3D.createRotationOz(Math.toRadians(rotation), c.x, c.y));
		}
		if ( newLocation != null ) {
			setLocation(newLocation);
		}
	}

	/**
	 * Same as above, but applies a (optional) rotation along a given vector and (optional) translation to the geometry
	 * @param source cannot be null
	 * @param geom can be null
	 * @param rotation can be null, expressed in degrees
	 * @param newLocation can be null
	 */

	
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final GamaPoint vector,
			final ILocation newLocation) {
			this(source, geom);
			if ( !isPoint() && vector != null && rotation != null ) {
				Vector3D v3D = new Vector3D(vector.getX(),vector.getY(),vector.getZ());
				Rotation rot = new Rotation(v3D, Math.toRadians(rotation));
				for (Coordinate c : this.getInnerGeometry().getCoordinates()) {
					Vector3D result = rot.applyTo(new Vector3D(c.x,c.y,c.z));
					c.x = result.getX();
					c.y = result.getY();
					c.z = result.getZ();
				}
			}
			if ( newLocation != null ) {
				setLocation(newLocation);
			}
		}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by specifying a bounding box or a set of
	 * coefficients.
	 * @param source cannot be null
	 * @param geom can be null
	 * @param rotation can be null, expressed in degrees
	 * @param newLocation can be null
	 * @param isBoundingBox indicates whether the previous parameter should be considered as an absolute bounding box
	 * (width, height, ) or as a set of coefficients.
	 */
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final ILocation newLocation,
		final GamaPoint bounds, final boolean isBoundingBox) {
		this(source, geom, rotation, newLocation);
		if ( bounds != null && !isPoint() ) {
			if ( getAttribute(IShape.TYPE_ATTRIBUTE) != SPHERE ) {
				GamaPoint previous = getLocation();
				Envelope3D envelope = getEnvelope();
				boolean flat = envelope.isFlat();
				if ( isBoundingBox ) {
					geometry.apply(AffineTransform3D.createScaling(bounds.x / envelope.getWidth(),
						bounds.y / envelope.getHeight(), flat ? 1.0 : bounds.z / envelope.getDepth()));
				} else {
					geometry.apply(AffineTransform3D.createScaling(bounds.x, bounds.y, bounds.z));
				}
				envelope = null;
				setLocation(previous);
			} else {
				Double scaling = Math.min(Math.min(bounds.x, bounds.y), bounds.z);
				Double box = Math.max(Math.max(bounds.x, bounds.y), bounds.z);
				setAttribute(IShape.DEPTH_ATTRIBUTE,
					isBoundingBox ? box : (Double) getAttribute(IShape.DEPTH_ATTRIBUTE) * scaling);
			}
		}
	}

	/**
	 * Same as above, but applies a (optional) scaling to the geometry by a given coefficient
	 * @param source cannot be null
	 * @param geom can be null
	 * @param rotation can be null, expressed in degrees
	 * @param newLocation can be null
	 */
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final ILocation newLocation,
		final Double scaling) {
		this(source, geom, rotation, newLocation);
		if ( scaling != null && !isPoint() ) {
			if ( getAttribute(IShape.TYPE_ATTRIBUTE) != SPHERE ) {
				GamaPoint previous = getLocation();
				geometry.apply(AffineTransform3D.createScaling(scaling, scaling, scaling));
				envelope = null;
				setLocation(previous);
			} else {
				setAttribute(IShape.DEPTH_ATTRIBUTE, (Double) getAttribute(IShape.DEPTH_ATTRIBUTE) * scaling);
			}
		}
	}

	public GamaShape() {}

	@getter("multiple")
	public boolean isMultiple() {
		return getInnerGeometry() instanceof GeometryCollection;
	}

	@getter("geometries")
	public IList<GamaShape> getGeometries() {
		final IList<GamaShape> result = GamaListFactory.create(Types.GEOMETRY);
		if ( isMultiple() ) {
			for ( int i = 0, n = getInnerGeometry().getNumGeometries(); i < n; i++ ) {
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
		if ( geometry == null ) { return false; }
		return geometry.getNumPoints() == 1;
	}

	@Override
	public boolean isLine() {
		return getInnerGeometry() instanceof LineString || getInnerGeometry() instanceof MultiLineString;
	}

	@Override
	public String stringValue(final IScope scope) {
		if ( geometry == null ) { return ""; }
		return SHAPE_WRITER.write(geometry);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if ( isPoint() ) { return getLocation().serialize(includingBuiltIn) + " as geometry"; }
		if ( isMultiple() ) { return getGeometries().serialize(includingBuiltIn) + " as geometry"; }
		final IList<GamaShape> holes = getHoles();
		String result = "";
		if ( getInnerGeometry() instanceof LineString ) {
			result = "polyline (" +
				GamaListFactory.createWithoutCasting(Types.POINT, getPoints()).serialize(includingBuiltIn) + ")";
		} else {
			result = "polygon (" +
				GamaListFactory.createWithoutCasting(Types.POINT, getPoints()).serialize(includingBuiltIn) + ")";
		}
		if ( holes.isEmpty() ) { return result; }
		for ( final GamaShape g : holes ) {
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
		if ( isPoint() ) { return new GamaPoint(geometry.getCoordinate()); }
		return getEnvelope().centre();
	}

	@Override
	public void setLocation(final ILocation l) {
		final GamaPoint previous = getLocation();
		Coordinate location = l.toCoordinate();
		if ( previous != null ) {
			if ( isPoint() ) {
				geometry = GeometryUtils.FACTORY.createPoint(location);
				envelope = Envelope3D.of(location);
			} else {
				// if ( isPoint ) {
				final double dx = location.x - previous.getX();
				final double dy = location.y - previous.getY();
				final double dz = location.z - previous.getZ();
				geometry.apply(new Translation(dx, dy, dz));
				// We move the envelope as well if it has been computed
				// if ( envelope != null ) {
				envelope.translate(dx, dy, dz);
				// }
				// Changed to avoid side effects when computing displacements & display at a given location at the same
				// time.
			}

			geometry.geometryChanged();
		}
	}

	public GamaShape translatedTo(final IScope scope, final ILocation target) {
		GamaShape result = copy(scope);
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
			if ( Double.isNaN(coord.z) ) {
				coord.z = dz;
			} else {
				coord.z += dz;
			}
		}

	}

	// private static class Modification implements CoordinateFilter {
	//
	// Coordinate modif;
	//
	// @Override
	// public void filter(final Coordinate c) {
	// c.setCoordinate(modif);
	// }
	//
	// public Modification with(final Coordinate c) {
	// modif = c;
	// return this;
	// }
	//
	// }

	final static PointLocator pl = new PointLocator();

	@Override
	public GamaShape getGeometry() {
		return this;
	}

	@getter("area")
	public Double getArea() {
		// WARNING only 2D (XY) area
		return getInnerGeometry().getArea();
	}

	@getter("volume")
	public Double getVolume() {
		return getEnvelope().getVolume();
	}

	@Override
	public double getPerimeter() {
		return getInnerGeometry().getLength();
	}

	@getter("holes")
	public IList<GamaShape> getHoles() {
		final IList<GamaShape> holes = GamaListFactory.create(Types.GEOMETRY);
		if ( getInnerGeometry() instanceof Polygon ) {
			final Polygon p = (Polygon) getInnerGeometry();
			final int n = p.getNumInteriorRing();
			for ( int i = 0; i < n; i++ ) {
				holes.add(new GamaShape(GeometryUtils.fromLineToPoylgon(p.getInteriorRingN(i))));
			}
		}
		return holes;
	}

	@getter("centroid")
	public GamaPoint getCentroid() {
		if ( geometry == null ) { return null; }
		if ( isPoint() ) { return getLocation(); }
		Coordinate c = geometry.getCentroid().getCoordinate();
		c.z = computeAverageZOrdinate();
		return new GamaPoint(c);
	}

	@getter("contour")
	public GamaShape getExteriorRing() {

		// WARNING Only in 2D
		Geometry result = getInnerGeometry();
		if ( result instanceof Polygon ) {
			result = ((Polygon) result).getExteriorRing();
		} else

		if ( result instanceof MultiPolygon ) {
			final MultiPolygon mp = (MultiPolygon) result;
			final LineString lines[] = new LineString[mp.getNumGeometries()];
			for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
				lines[i] = ((Polygon) mp.getGeometryN(i)).getExteriorRing();
			}
			result = GeometryUtils.FACTORY.createMultiLineString(lines);

		}
		return new GamaShape(result);
	}

	@getter("width")
	public Double getWidth() {
		return getEnvelope().getWidth();
	}

	@getter("height")
	public Double getHeight() {
		return getEnvelope().getHeight();
	}

	@getter("depth")
	public Double getDepth() {
		return (Double) this.getAttribute(IShape.DEPTH_ATTRIBUTE);
		// return getEnvelope().getDepth();
	}

	@Override
	public void setDepth(final double depth) {
		this.setAttribute(IShape.DEPTH_ATTRIBUTE, depth);
		this.envelope = null;
	}

	@Override
	public void setRotate3D(final GamaPair rot3D) {
		this.setAttribute(IShape.ROTATE_ATTRIBUTE, rot3D);
	}

	@getter("envelope")
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(getEnvelope());
	}

	@Override
	@getter("points")
	public IList<? extends ILocation> getPoints() {
		final IList<GamaPoint> result = GamaListFactory.create(Types.POINT);
		if ( getInnerGeometry() == null ) { return result; }
		final Coordinate[] points = getInnerGeometry().getCoordinates();
		for ( final Coordinate c : points ) {
			result.add(new GamaPoint(c));
		}
		return result;
	}

	@Override
	public Envelope3D getEnvelope() {
		if ( envelope == null ) {
			envelope = Envelope3D.of(this);
		}
		return envelope;
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
		setGeometry(geom, true);
	}

	@Override
	public void setGeometry(final IShape geom) {
		if ( geom == null || geom == this ) { return; }
		// location = geom.getLocation();
		// if ( Double.isNaN(location.getX()) ) {
		// scope.getGui().debug("GamaShape.setGeometry");
		// }
		setGeometry(geom.getInnerGeometry(), false);
	}

	protected void setGeometry(final Geometry geom, final boolean computeLoc) {
		if ( geom == null ) {
			geometry = null;
			envelope = null;
			return;
		}
		if ( geom.isEmpty() ) {
			// See Issue 725
			return;
		}
		if ( geom instanceof GeometryCollection && geom.getNumGeometries() == 1 ) {
			geometry = geom.getGeometryN(0);
		} else {
			geometry = geom;
		}
		// isPoint = geom.getNumPoints() == 1;
		envelope = null;
		// if ( computeLoc ) {
		// computeLocation();
		// }
	}

	//
	// private void computeLocation() {
	// Geometry g = getInnerGeometry();
	// final Point p = getInnerGeometry().getCentroid();
	//
	// final Coordinate c = p.getCoordinate();
	// if ( isPoint() ) {
	// c.z = g.getCoordinate().z;
	// } else {
	// c.z = computeAverageZOrdinate(g);
	// }
	// if ( location == null ) {
	// location = new GamaPoint(c);
	// } else {
	// location.setLocation(c.x, c.y, c.z);
	// }
	// }

	// public static GamaPoint computeCentroidOf(final Geometry g) {
	// if ( g.isEmpty() ) { return null; }
	// Coordinate[] coords = g.getCoordinates();
	// int n = coords.length;
	// double x = 0;
	// double y = 0;
	// double z = 0;
	// for ( Coordinate c : coords ) {
	// x = x + c.x;
	// y = y + c.y;
	// z = z + c.z;
	// }
	// return new GamaPoint(x / n, y / n, z / n);
	// }
	//
	private double computeAverageZOrdinate() {
		double z = 0d;
		Coordinate[] coords = geometry.getCoordinates();
		for ( Coordinate c : coords ) {
			if ( Double.isNaN(c.z) ) {
				continue;
			}
			z += c.z;
		}
		return z / coords.length;
	}

	@Override
	public void dispose() {
		// if ( getInnerGeometry() != null ) {
		// setInnerGeometry((Geometry) null);
		// }
		// IMPORTANT We now leave the geometry of the agent intact in case it is used elsewhere
		// in topologies, etc.
		// optimizedOperations = null;
		agent = null;
	}

	@Override
	public boolean equals(final Object o) {
		if ( o instanceof GamaShape ) {
			Geometry shape = ((GamaShape) o).geometry;
			// Fix a possible NPE when calling equalsExact with a null shape
			if ( shape == null ) { return geometry == null; }
			if ( geometry == null ) { return false; }
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
		GamaShape g = new GamaShape(this, (Geometry) geometry.clone());
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
		if ( g.isPoint() ) { return pl.intersects((Coordinate) g.getLocation(), geometry); }
		// if ( !USE_PREPARED_OPERATIONS ) {
		return geometry.covers(g.getInnerGeometry());
		// }
		// return operations().covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		// WARNING Only 2D now
		if ( isPoint() && g.isPoint() ) { return g.getLocation().euclidianDistanceTo(getLocation()); }
		// if ( g.isPoint() ) { return euclidianDistanceTo(g.getLocation()); }
		// if ( isPoint ) { return g.euclidianDistanceTo(getLocation()); }
		// if ( !USE_PREPARED_OPERATIONS ) {
		return getInnerGeometry().distance(g.getInnerGeometry());
		// }
		// return operations().getDistance(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		// WARNING Only 2D now
		if ( isPoint() ) { return g.euclidianDistanceTo(getLocation()); }
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
		if ( g.isPoint() ) { return pl.intersects((Coordinate) g.getLocation(), getInnerGeometry()); }
		// if ( !USE_PREPARED_OPERATIONS ) {
		return getInnerGeometry().intersects(g.getInnerGeometry());
		// }
		// return operations().intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		// WARNING Only 2D now
		if ( g.isPoint() ) { return pl.intersects((Coordinate) g.getLocation(), getInnerGeometry()); }
		// if ( !USE_PREPARED_OPERATIONS ) {
		return geometry.crosses(g.getInnerGeometry());
		// }
		// return operations().crosses(g);
	}

	// private GamaShape.Operations operations() {
	// if ( optimizedOperations == null ) {
	// optimizedOperations = new Operations();
	// }
	// return optimizedOperations;
	// }

	/**
	 * Used when the geometry is not affected to an agent and directly accessed by 'read' or 'get'
	 * operators. Can be used in Java too, of course, to retrieve any value stored in the shape
	 * @param s
	 * @return the corresponding value of the attribute named 's' in the feature, or null if it is
	 * not present
	 */
	@Override
	public Object getAttribute(final Object s) {
		if ( attributes == null ) { return null; }
		return attributes.get(s);
	}

	@Override
	public void setAttribute(final Object key, final Object value) {
		getOrCreateAttributes().put(key, value);
	}

	@Override
	public GamaMap getOrCreateAttributes() {
		if ( attributes == null ) {
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
	 * @see msi.gama.metamodel.shape.IShape#getGeometricalType()
	 */
	@Override
	public Type getGeometricalType() {
		if ( hasAttribute(TYPE_ATTRIBUTE) ) { return (Type) getAttribute(TYPE_ATTRIBUTE); }
		String type = getInnerGeometry().getGeometryType();
		if ( JTS_TYPES.containsKey(type) ) { return JTS_TYPES.get(type); }
		return Type.NULL;
	}

	// /**
	// * Method asShapeWithGeometry()
	// * @see msi.gama.metamodel.shape.IShape#asShapeWithGeometry(com.vividsolutions.jts.geom.Geometry)
	// */
	// @Override
	// public GamaShape asShapeWithGeometry(final IScope scope, final Geometry g) {
	// if ( g == null ) { return this; }
	// GamaShape result = copy(scope);
	// result.setGeometry(g, true);
	// return result;
	// }

}
