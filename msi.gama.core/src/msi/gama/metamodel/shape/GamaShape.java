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
package msi.gama.metamodel.shape;

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.types.IType;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul Modified on 25 ao�t 2010
 * 
 * 
 * 
 */
@vars({ @var(name = "area", type = IType.FLOAT), @var(name = "volume", type = IType.FLOAT),
	@var(name = "width", type = IType.FLOAT), @var(name = "depth", type = IType.FLOAT),
	@var(name = "height", type = IType.FLOAT), @var(name = "points", type = IType.LIST, of = IType.POINT),
	@var(name = "envelope", type = IType.GEOMETRY), @var(name = "geometries", type = IType.LIST, of = IType.GEOMETRY),
	@var(name = "multiple", type = IType.BOOL), @var(name = "holes", type = IType.LIST, of = IType.GEOMETRY),
	@var(name = "contour", type = IType.GEOMETRY) })
public class GamaShape implements IShape /* , IContainer */{

	// private static final boolean USE_PREPARED_OPERATIONS = false;

	protected Geometry geometry;
	protected volatile ILocation location;
	private boolean isPoint;
	private IAgent agent;
	// TODO This represents a waste of memory but it is necessary to maintain it, as the Geometry does not give access
	// to a custom envelope builder
	private Envelope3D envelope;

	// Property map to add all kinds of information (e.g to specify if the geometry is a sphere, a
	// cube, etc...). Can be reused by subclasses (for example to store GIS information)
	protected GamaMap attributes;

	public GamaShape(final Geometry geom) {
		setInnerGeometry(geom);
	}

	public GamaShape(final Envelope3D env) {
		// WARNING For the moment only in 2D
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
			attributes = new GamaMap(attr);
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
			geometry.apply(Rotation.of(Math.toRadians(rotation), location.toCoordinate()));
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
	 *            (width, height, depth) or as a set of coefficients.
	 */
	public GamaShape(final IShape source, final Geometry geom, final Double rotation, final ILocation newLocation,
		final GamaPoint bounds, final boolean isBoundingBox) {
		this(source, geom, rotation, newLocation);
		if ( bounds != null && !isPoint() ) {
			Envelope3D envelope = getEnvelope();
			boolean flat = envelope.getDepth() == 0.0;
			if ( isBoundingBox ) {
				geometry.apply(Scaling.of(bounds.x / envelope.getWidth(), bounds.y / envelope.getHeight(), flat ? 1.0
					: bounds.z / envelope.getDepth(), (Coordinate) location));
			} else {
				geometry.apply(Scaling.of(bounds.x, bounds.y, bounds.z, (Coordinate) location));
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
			geometry.apply(Scaling.of(scaling, scaling, scaling, (Coordinate) location));
		}
	}

	public GamaShape() {}

	@getter("multiple")
	public boolean isMultiple() {
		return getInnerGeometry() instanceof GeometryCollection;
	}

	@getter("geometries")
	public GamaList<GamaShape> getGeometries() {
		final GamaList<GamaShape> result = new GamaList();
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
		return isPoint;
	}

	@Override
	public boolean isLine() {
		return getInnerGeometry() instanceof LineString || getInnerGeometry() instanceof MultiLineString;
	}

	@Override
	public String stringValue(final IScope scope) {
		return getInnerGeometry().getGeometryType();
	}

	@Override
	public String toGaml() {
		if ( isPoint() ) { return getLocation().toGaml() + " as geometry"; }
		if ( isMultiple() ) { return getGeometries().toGaml() + " as geometry"; }
		final GamaList<GamaShape> holes = getHoles();
		String result = "";
		if ( getInnerGeometry() instanceof LineString ) {
			result = "polyline (" + new GamaList(getPoints()).toGaml() + ")";
		} else {
			result = "polygon (" + new GamaList(getPoints()).toGaml() + ")";
		}
		if ( holes.isEmpty() ) { return result; }
		for ( final GamaShape g : holes ) {
			result = "(" + result + ") - (" + g.toGaml() + ")";
		}
		return result;
	}

	@Override
	public String toString() {
		return getInnerGeometry().toText() + " at " + getLocation();
	}

	@Override
	public ILocation getLocation() {
		return location;
	}

	@Override
	public void setLocation(final ILocation l) {
		final ILocation previous = location;
		location = l;
		if ( previous != null ) {
			if ( isPoint() ) {
				geometry.apply(new Modification().with((Coordinate) location));
				envelope = null;
			} else {
				// if ( isPoint ) {
				final double dx = location.getX() - previous.getX();
				final double dy = location.getY() - previous.getY();
				final double dz = location.getZ() - previous.getZ();
				geometry.apply(new Translation(dx, dy, dz));
				// We move the envelope as well if it has been computed
				if ( envelope != null ) {
					envelope.translate(dx, dy, dz);
				}
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

	private static class Scaling extends AffineTransform3D {

		/**
		 * @param bounds
		 * @param location
		 * @return
		 */
		static Scaling of(final double xCoeff, final double yCoeff, final double zCoeff, final Coordinate c) {
			// TODO minimize the creation of AffineTransform3D by reusing the same object
			Scaling t = new Scaling();
			t.compose(createTranslation(-c.x, -c.y, -c.z));
			t.compose(createScaling(xCoeff, yCoeff, zCoeff));
			t.compose(createTranslation(c.x, c.y, c.z));
			return t;
		}

	}

	private static class Rotation extends AffineTransform3D {

		static AffineTransform3D of(final double theta, final Coordinate c) {

			// TODO Verify this code. Do we need to make the translation ?
			// TODO minimize the creation of AffineTransform3D by reusing the same object
			AffineTransform3D t = createRotationOz(theta, c.x, c.y);
			// t.compose(createTranslation(c.x, c.y, c.z));
			return t;
		}
	}

	private static class Modification implements CoordinateFilter {

		Coordinate modif;

		@Override
		public void filter(final Coordinate c) {
			c.setCoordinate(modif);
		}

		public Modification with(final Coordinate c) {
			modif = c;
			return this;
		}

	}

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
	public GamaList<GamaShape> getHoles() {
		final GamaList<GamaShape> holes = new GamaList();
		if ( getInnerGeometry() instanceof Polygon ) {
			final Polygon p = (Polygon) getInnerGeometry();
			final int n = p.getNumInteriorRing();
			for ( int i = 0; i < n; i++ ) {
				holes.add(new GamaShape(p.getInteriorRingN(i)));
			}
		}
		return holes;
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
		return getEnvelope().getDepth();
	}

	@getter("envelope")
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(getEnvelope());
	}

	@getter("points")
	public IList<GamaPoint> getPoints() {
		final GamaList<GamaPoint> result = new GamaList();
		final Coordinate[] points = getInnerGeometry().getCoordinates();
		for ( final Coordinate c : points ) {
			result.add(new GamaPoint(c));
		}
		return result;
	}

	@Override
	public Envelope3D getEnvelope() {
		if ( envelope == null ) {
			//envelope = Envelope3D.of(getInnerGeometry());
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
		location = geom.getLocation();
		// if ( Double.isNaN(location.getX()) ) {
		// GuiUtils.debug("GamaShape.setGeometry");
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
		isPoint = geom.getNumPoints() == 1;
		envelope = null;
		if ( computeLoc ) {
			computeLocation();
		}
	}

	private void computeLocation() {
		Geometry g = getInnerGeometry();
		final Point p = getInnerGeometry().getCentroid();

		final Coordinate c = p.getCoordinate();
		if ( isPoint() ) {
			c.z = g.getCoordinate().z;
		} else {
			c.z = computeAverageZOrdinate(g);
		}
		if ( location == null ) {
			location = new GamaPoint(c);
		} else {
			location.setLocation(c.x, c.y, c.z);
		}
	}

	private double computeAverageZOrdinate(final Geometry g) {
		double z = 0d;
		Coordinate[] coords = g.getCoordinates();
		for ( Coordinate c : coords ) {
			if ( c.z == Double.NaN ) {
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
			if ( geometry == null ) { return ((GamaShape) o).geometry == null; }
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
		// g.attributes = new GamaMap(attributes);
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
	 *         not present
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
			attributes = new GamaMap();
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
