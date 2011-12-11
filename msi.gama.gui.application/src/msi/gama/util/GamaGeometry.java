/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.List;
import msi.gama.environment.GeometricFunctions;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Common.Operations;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul Modified on 25 aožt 2010
 * 
 * 
 * 
 */
@vars({ @var(name = "area", type = IType.FLOAT_STR),
	@var(name = "location", type = IType.POINT_STR),
	@var(name = "perimeter", type = IType.FLOAT_STR), @var(name = "width", type = IType.FLOAT_STR),
	@var(name = "height", type = IType.FLOAT_STR),
	@var(name = "points", type = IType.LIST_STR, of = IType.POINT_STR),
	@var(name = "envelope", type = IType.GEOM_STR),
	@var(name = "geometries", type = IType.LIST_STR, of = IType.GEOM_STR),
	@var(name = "multiple", type = IType.BOOL_STR),
	@var(name = "holes", type = IType.LIST_STR, of = IType.GEOM_STR),
	@var(name = "contour", type = IType.GEOM_STR) })
public class GamaGeometry implements IGeometry {

	private Geometry geometry;
	private GamaPoint location;
	private boolean isPoint;
	private Operations optimizedOperations;
	private IAgent agent;

	public static GeometryFactory factory = new GeometryFactory();

	public GamaGeometry(final Geometry geom) {
		setInnerGeometry(geom);
	}

	public GamaGeometry(final IGeometry geom) {
		this(geom.getInnerGeometry());
	}

	public GamaGeometry(final GamaPoint point) {
		this(GamaGeometry.getFactory().createPoint(point.toCoordinate()));
	}

	public GamaGeometry() {

	}

	@Override
	public IType type() {
		return Types.get(IType.GEOMETRY);
	}

	@getter(var = "multiple")
	public boolean isMultiple() {
		return getInnerGeometry() instanceof GeometryCollection;
	}

	@getter(var = "geometries")
	public GamaList<GamaGeometry> getGeometries() {
		GamaList<GamaGeometry> result = new GamaList();
		if ( isMultiple() ) {
			for ( int i = 0, n = getInnerGeometry().getNumGeometries(); i < n; i++ ) {
				result.add(new GamaGeometry(getInnerGeometry().getGeometryN(i)));
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
	public String stringValue() {
		return getInnerGeometry().getGeometryType();
	}

	@Override
	public String toGaml() {
		if ( isPoint ) { return getLocation().toGaml() + " as geometry"; }
		if ( isMultiple() ) { return getGeometries().toGaml() + " as geometry"; }
		GamaList<GamaGeometry> holes = getHoles();
		String result = "polygon (" + getPoints().toGaml() + ")";
		if ( holes.isEmpty() ) { return result; }
		for ( GamaGeometry g : holes ) {
			result = "(" + result + ") - (" + g.toGaml() + ")";
		}
		return result;
	}

	@Override
	public String toJava() {
		// TODO False now
		return "new " + getClass().getCanonicalName() + "(" +
			GeometricFunctions.class.getCanonicalName() + ".buildGeometryJTS(" +
			Cast.toJava(getGeometries()) + "))";
	}

	@Override
	public String toString() {
		return getInnerGeometry().toText() + " at " + getLocation();
	}

	@Override
	@getter(var = "location")
	public GamaPoint getLocation() {
		return location;
	}

	@Override
	public void setLocation(final GamaPoint l) {
		final GamaPoint previous = location;
		location = l;
		if ( previous != null ) {
			if ( isPoint ) {
				translation.setTranslation(location.x - previous.x, location.y - previous.y);
				getInnerGeometry().apply(translation);
			} else {
				Geometry g =
					GeometricFunctions.translation(getInnerGeometry(), location.x - previous.x,
						location.y - previous.y);
				setGeometry(g, false);
			}
		}
	}

	// @Override
	// public boolean equals(final Object o) {
	// if ( !(o instanceof GamaGeometry) ) { return false; }
	// GamaGeometry g = (GamaGeometry) o;
	// if ( isPoint && g.isPoint ) { return location.equals(g.location); }
	// return geometry.equals(((GamaGeometry) o).geometry);
	// }

	private static Translation translation = new Translation();

	private static class Translation implements CoordinateSequenceFilter {

		double dx = 0, dy = 0;

		void setTranslation(final double x, final double y) {
			dx = x;
			dy = y;
		}

		@Override
		public void filter(final CoordinateSequence seq, final int i) {
			double xp = seq.getOrdinate(i, 0) + dx;
			double yp = seq.getOrdinate(i, 1) + dy;
			seq.setOrdinate(i, 0, xp);
			seq.setOrdinate(i, 1, yp);
		}

		@Override
		public boolean isDone() {
			return false;
		}

		@Override
		public boolean isGeometryChanged() {
			return true;
		}

	}

	@Override
	public GamaGeometry getGeometry() {
		return this;
	}

	@getter(var = "area")
	public Double getArea() {
		return getInnerGeometry().getArea();
	}

	@getter(var = "perimeter")
	public Double getPerimeter() {
		return getInnerGeometry().getLength();
	}

	@getter(var = "holes")
	public GamaList<GamaGeometry> getHoles() {
		GamaList<GamaGeometry> holes = new GamaList();
		if ( getInnerGeometry() instanceof Polygon ) {
			Polygon p = (Polygon) getInnerGeometry();
			int n = p.getNumInteriorRing();
			for ( int i = 0; i < n; i++ ) {
				holes.add(new GamaGeometry(p.getInteriorRingN(i)));
			}
		}
		return holes;
	}

	@getter(var = "contour")
	public GamaGeometry getExteriorRing() {
		Geometry result = getInnerGeometry();
		if ( result instanceof Polygon ) {
			result = ((Polygon) result).getExteriorRing();
		} else

		if ( result instanceof MultiPolygon ) {
			MultiPolygon mp = (MultiPolygon) result;
			LineString lines[] = new LineString[mp.getNumGeometries()];
			for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
				lines[i] = ((Polygon) mp.getGeometryN(i)).getExteriorRing();
			}
			result = GamaGeometry.getFactory().createMultiLineString(lines);

		}
		return new GamaGeometry(result);
	}

	@getter(var = "width")
	public Double getWidth() {
		return getInnerGeometry().getEnvelopeInternal().getWidth();
	}

	@getter(var = "height")
	public Double getHeight() {
		return getInnerGeometry().getEnvelopeInternal().getHeight();
	}

	@getter(var = "envelope")
	public GamaGeometry getGeometricEnvelope() {
		return new GamaGeometry(getInnerGeometry().getEnvelope());
	}

	@getter(var = "points")
	public GamaList<GamaPoint> getPoints() {
		GamaList<GamaPoint> result = new GamaList();
		Coordinate[] points = getInnerGeometry().getCoordinates();
		for ( Coordinate c : points ) {
			result.add(new GamaPoint(c));
		}
		return result;
	}

	@Override
	public Envelope getEnvelope() {
		return getInnerGeometry().getEnvelopeInternal();
	}

	public IAgent getAgent() {
		return agent;
	}

	public void setAgent(final IAgent a) {
		agent = a;
	}

	public void setInnerGeometry(final Geometry geom) {
		setGeometry(geom, true);
	}

	@Override
	public void setGeometry(final GamaGeometry geom) {
		if ( geom == null || geom == this ) { return; }
		location = geom.location;
		setGeometry(geom.getInnerGeometry(), false);
	}

	protected void setGeometry(final Geometry geom, final boolean computeLoc) {
		geometry = geom;
		if ( geom == null ) { return; }
		isPoint = geom.getNumPoints() == 1;
		if ( computeLoc ) {
			computeLocation();
		}
		optimizedOperations = null;
	}

	private void computeLocation() {
		Coordinate c = getInnerGeometry().getCentroid().getCoordinate();
		if ( location == null ) {
			location = new GamaPoint(c);
		} else {
			location.x = c.x;
			location.y = c.y;
		}
	}

	public void dispose() {
		// if ( getInnerGeometry() != null ) {
		// setInnerGeometry((Geometry) null);
		// }
		// TODO IMPORTANT We now leave the geometry of the agent intact in case it is used elsewhere
		// in
		// topologies, etc.
		optimizedOperations = null;
		agent = null;
	}

	public boolean contains(final Object o) {
		if ( o == null ) { return false; }
		if ( o instanceof Geometry ) { return getInnerGeometry().covers((Geometry) o); }
		if ( o instanceof GamaGeometry ) { return getInnerGeometry().covers(
			((GamaGeometry) o).getInnerGeometry()); }
		if ( o instanceof GamaPoint ) { return contains(GamaGeometry.getFactory().createPoint(
			((GamaPoint) o).toCoordinate())); }
		return false;

	}

	public static GeometryFactory getFactory() {
		return factory;
	}

	/**
	 * Builds a (cleansed) polygon from a list of points. The input points must be valid to create a
	 * linear ring (first point and last point are duplicated). It is the responsible of the caller
	 * to assure the validity of the input parameter.
	 * 
	 * @param points
	 * @return
	 */
	public static GamaGeometry buildPolygon(final List<GamaPoint> points) {
		GamaList<Coordinate> coordinates = new GamaList<Coordinate>();

		for ( GamaPoint p : points ) {
			coordinates.add(p.toCoordinate());
		}

		LinearRing geom =
			GamaGeometry.factory.createLinearRing(coordinates.toArray(new Coordinate[0]));
		Polygon p = GamaGeometry.factory.createPolygon(geom, null);
		if ( p.isValid() ) { return new GamaGeometry(p.buffer(0.0)); }
		return buildPolyline(points);

	}

	public static GamaGeometry buildLine(final GamaPoint location1, final GamaPoint location2) {
		Coordinate coordinates[] =
			{ location1 == null ? new GamaPoint(0, 0) : location1,
				location2 == null ? new GamaPoint(0, 0) : location2 };
		return new GamaGeometry(GamaGeometry.factory.createLineString(coordinates));
	}

	public static GamaGeometry buildPolyline(final List<GamaPoint> points) {
		GamaList<Coordinate> coordinates = new GamaList<Coordinate>();

		for ( GamaPoint p : points ) {
			coordinates.add(p.toCoordinate());
		}
		return new GamaGeometry(GamaGeometry.factory.createLineString(coordinates
			.toArray(new Coordinate[0])));
	}

	public static GamaGeometry createPoint(final GamaPoint location) {
		return new GamaGeometry(GamaGeometry.factory.createPoint(location == null ? new GamaPoint(
			0, 0) : location));
	}

	public static GamaGeometry buildTriangle(final double side_size, final GamaPoint location) {
		double sqrt2 = Math.sqrt(2.0);
		double x = location == null ? 0 : location.x;
		double y = location == null ? 0 : location.y;
		Coordinate[] coordinates = new Coordinate[4];
		coordinates[0] = new Coordinate(x, y - side_size / sqrt2);
		coordinates[1] = new Coordinate(x - side_size / sqrt2, y + side_size / sqrt2);
		coordinates[2] = new Coordinate(x + side_size / sqrt2, y + side_size / sqrt2);
		coordinates[3] = coordinates[0];
		LinearRing geom = GamaGeometry.factory.createLinearRing(coordinates);
		return new GamaGeometry(GamaGeometry.factory.createPolygon(geom, null));
	}

	public static GamaGeometry buildSquare(final double side_size, final GamaPoint location) {
		return buildRectangle(side_size, side_size, location == null ? new GamaPoint(0, 0)
			: location);
	}

	public static GamaGeometry buildRectangle(final double width, final double height,
		final GamaPoint location) {
		Coordinate[] coordinates = new Coordinate[5];
		double x = location == null ? 0 : location.x;
		double y = location == null ? 0 : location.y;
		coordinates[0] = new Coordinate(x - width / 2.0, y + height / 2.0);
		coordinates[1] = new Coordinate(x + width / 2.0, y + height / 2.0);
		coordinates[2] = new Coordinate(x + width / 2.0, y - height / 2.0);
		coordinates[3] = new Coordinate(x - width / 2.0, y - height / 2.0);
		coordinates[4] = coordinates[0];
		LinearRing geom = GamaGeometry.factory.createLinearRing(coordinates);
		return new GamaGeometry(GamaGeometry.factory.createPolygon(geom, null));
	}

	public static GamaGeometry buildCircle(final double radius, final GamaPoint location) {
		Geometry geom =
			GamaGeometry.factory.createPoint(location == null ? new GamaPoint(0, 0) : location);
		return new GamaGeometry(geom.buffer(radius));
	}

	@Override
	public Geometry getInnerGeometry() {
		return geometry;
	}

	@Override
	public GamaGeometry copy() {
		return new GamaGeometry(geometry.buffer(0.0));
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IGeometry g) {
		return operations().covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IGeometry g) {
		return operations().getDistance(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IGeometry g) {
		return operations().intersects(g);
	}

	private Spatial.Common.Operations operations() {
		if ( optimizedOperations == null ) {
			optimizedOperations = new Spatial.Common.Operations(this);
		}
		return optimizedOperations;
	}

}
