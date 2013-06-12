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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gaml.operators.Maths;
import msi.gaml.types.IType;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.algorithm.distance.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.operation.distance.IndexedFacetDistance;

/**
 * Written by drogoul Modified on 25 aožt 2010
 * 
 * 
 * 
 */
@vars({ @var(name = "area", type = IType.FLOAT), @var(name = "width", type = IType.FLOAT),
	@var(name = "height", type = IType.FLOAT), @var(name = "points", type = IType.LIST, of = IType.POINT),
	@var(name = "envelope", type = IType.GEOMETRY), @var(name = "geometries", type = IType.LIST, of = IType.GEOMETRY),
	@var(name = "multiple", type = IType.BOOL), @var(name = "holes", type = IType.LIST, of = IType.GEOMETRY),
	@var(name = "contour", type = IType.GEOMETRY) })
public class GamaShape implements IShape /* , IContainer */{

	protected Geometry geometry;
	protected volatile ILocation location;
	private boolean isPoint;
	private Operations optimizedOperations;
	private IAgent agent;

	// Property map to add all kinds of information (e.g to specify if the geometry is a sphere, a
	// cube, etc...). Can be reused by subclasses (for example to store GIS information)
	protected GamaMap attributes;

	public GamaShape(final Geometry geom) {
		setInnerGeometry(geom);
	}

	public GamaShape(final IShape geom) {
		this(geom.getInnerGeometry());
	}

	public GamaShape(final ILocation point) {
		this(GeometryUtils.factory.createPoint(point instanceof Coordinate ? (Coordinate) point : point.toCoordinate()));
	}

	public GamaShape() {

	}

	//
	// @Override
	// public IType type() {
	// return Types.get(IType.GEOMETRY);
	// }

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
	public String stringValue(final IScope scope) {
		return getInnerGeometry().getGeometryType();
	}

	@Override
	public String toGaml() {
		if ( isPoint ) { return getLocation().toGaml() + " as geometry"; }
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
		// if ( Double.isNaN(l.getX()) ) {
		// GuiUtils.debug("GamaShape.setLocation NAN");
		// }
		final ILocation previous = location;
		location = l;
		if ( previous != null ) {
			if ( isPoint ) {
				geometry.apply(modification.with((Coordinate) location));
			} else {
				// if ( isPoint ) {
				final double dx = location.getX() - previous.getX();
				final double dy = location.getY() - previous.getY();
				final double dz = location.getZ() - previous.getZ();
				// if ( Double.isNaN(dx) ) {
				// GuiUtils.debug("GamaShape.setLocation");
				// }
				geometry.apply(translation.by(dx, dy, dz));
			}
			geometry.geometryChanged();
		}
	}

	public GamaShape rotatedBy(final IScope scope, final int angle) {
		return rotatedBy(scope, Maths.toRad * angle);
	}

	public GamaShape rotatedBy(final IScope scope, final double angle) {
		if ( isPoint ) { return copy(scope); }
		final Geometry newGeom = (Geometry) geometry.clone();
		newGeom.apply(rotation.of(angle, (Coordinate) location));
		return new GamaShape(newGeom);
	}

	public GamaShape scaledBy(final IScope scope, final double coeff) {
		if ( isPoint ) { return copy(scope); }
		final Geometry newGeom = (Geometry) geometry.clone();
		newGeom.apply(scaling.of(coeff, (Coordinate) location));
		return new GamaShape(newGeom);
	}

	public static Translation translation = new Translation();
	public static Modification modification = new Modification();
	public static Rotation rotation = new Rotation();
	public static Scaling scaling = new Scaling();

	static class Translation implements CoordinateFilter {

		double dx, dy, dz;

		/**
		 * @see com.vividsolutions.jts.geom.CoordinateFilter#filter(com.vividsolutions.jts.geom.Coordinate)
		 */
		@Override
		public void filter(final Coordinate coord) {
			coord.x += dx;
			coord.y += dy;
			coord.z += dz;
		}

		/**
		 * @param dx
		 * @param dy
		 * @param dz
		 * @return
		 */
		public Translation by(final double dx, final double dy, final double dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
			return this;
		}

		public Translation by(final ILocation translation) {
			this.dx = translation.getX();
			this.dy = translation.getY();
			this.dz = translation.getZ();
			return this;
		}

	}

	private static class Scaling extends AffineTransformation {

		Scaling of(final double coeff, final Coordinate c) {
			setToTranslation(-c.x, -c.y);
			scale(coeff, coeff);
			translate(c.x, c.y);
			return this;
		}

	}

	private static class Rotation extends AffineTransformation {

		Rotation of(final double theta, final Coordinate c) {
			setToRotation(theta, c.x, c.y);
			return this;
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

	public static class Operations {

		final static GamaPoint currentPoint = new GamaPoint(0d, 0d);
		final static Point point = GeometryUtils.factory.createPoint(new Coordinate(0, 0));
		public final static PointPairDistance ppd = new PointPairDistance();
		final static PointLocator pl = new PointLocator();

		final Geometry cached;
		IndexedFacetDistance distance;
		PreparedGeometry prepared;

		public Operations(final GamaShape g1) {
			cached = g1.getInnerGeometry();
		}

		private IndexedFacetDistance distanceOp() {
			if ( distance == null ) {
				distance = new IndexedFacetDistance(cached);
			}
			return distance;
		}

		private PreparedGeometry preparedOp() {
			if ( prepared == null ) {
				prepared = PreparedGeometryFactory.prepare(cached);
			}
			return prepared;
		}

		public static double getDistance(final Geometry source, final Coordinate target) {
			ppd.initialize();
			DistanceToPoint.computeDistance(source, target, ppd);
			return ppd.getDistance();
		}

		public double getDistance(final IShape g2) {
			if ( g2.isPoint() ) {
				// if (isPoint()) {return getLocation().distance(g2.getLocation());
				ppd.initialize();
				DistanceToPoint.computeDistance(cached, (Coordinate) g2.getLocation(), ppd);
				return ppd.getDistance();
			}
			return distanceOp().getDistance(g2.getInnerGeometry());
		}

		public boolean covers(final IShape g) {
			return g.isPoint() ? pl.intersects((Coordinate) g.getLocation(), cached) : preparedOp().covers(
				g.getInnerGeometry());
		}

		public boolean intersects(final IShape g) {
			return g.isPoint() ? pl.intersects((Coordinate) g.getLocation(), cached) : preparedOp().intersects(
				g.getInnerGeometry());
		}

		public boolean crosses(final IShape g) {
			return g.isPoint() ? pl.intersects((Coordinate) g.getLocation(), cached) : preparedOp().crosses(
				g.getInnerGeometry());
		}

	}

	@Override
	public IShape getGeometry() {
		return this;
	}

	@getter("area")
	public Double getArea() {
		return getInnerGeometry().getArea();
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
			result = GeometryUtils.factory.createMultiLineString(lines);

		}
		return new GamaShape(result);
	}

	@getter("width")
	public Double getWidth() {
		return getInnerGeometry().getEnvelopeInternal().getWidth();
	}

	@getter("height")
	public Double getHeight() {
		return getInnerGeometry().getEnvelopeInternal().getHeight();
	}

	@getter("envelope")
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(getInnerGeometry().getEnvelope());
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
	public Envelope getEnvelope() {
		return getInnerGeometry().getEnvelopeInternal();
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
		geometry = geom;
		if ( geom == null ) { return; }
		// if ( !computeLoc ) {
		// GuiUtils.debug("Agent " + getAgent() + " setGeometry location: " + location +
		// " centroid:" + geometry.getCentroid().getCoordinate());
		// }
		isPoint = geom.getNumPoints() == 1;
		if ( computeLoc ) {
			computeLocation();
		}
		optimizedOperations = null;
	}

	private void computeLocation() {
		final Point p = getInnerGeometry().getCentroid();
		final Coordinate c = p.getCoordinate();
		if ( location == null ) {
			location = new GamaPoint(c);
		} else {
			location.setLocation(c.x, c.y, c.z);
		}
		// if ( Double.isNaN(location.getX()) ) {
		//
		// Point p2 = getInnerGeometry().getCentroid();
		// Coordinate c2 = p2.getCoordinate();
		// GuiUtils.debug("GamaShape.computeLocation" + c2);
		// }
	}

	@Override
	public void dispose() {
		// if ( getInnerGeometry() != null ) {
		// setInnerGeometry((Geometry) null);
		// }
		// IMPORTANT We now leave the geometry of the agent intact in case it is used elsewhere
		// in topologies, etc.
		optimizedOperations = null;
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
		GamaShape g = null;
		g = new GamaShape((Geometry) geometry.clone());
		if ( attributes != null ) {
			g.attributes = new GamaMap(attributes);
		}
		g.setLocation(location.copy(scope));
		return g;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		return operations().covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		if ( isPoint() ) { return g.euclidianDistanceTo(this.getLocation()); }
		return operations().getDistance(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		if ( isPoint ) { return g.euclidianDistanceTo(getLocation()); }
		return operations().getDistance(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		return operations().intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		return operations().crosses(g);
	}

	private GamaShape.Operations operations() {
		if ( optimizedOperations == null ) {
			optimizedOperations = new GamaShape.Operations(this);
		}
		return optimizedOperations;
	}

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

}
