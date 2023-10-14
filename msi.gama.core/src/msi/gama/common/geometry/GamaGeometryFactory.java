/*******************************************************************************************************
 *
 * GamaGeometryFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * A factory for creating GamaGeometry objects.
 */
public class GamaGeometryFactory extends GeometryFactory {

	/** The Constant COORDINATES_FACTORY. */
	public static final GamaCoordinateSequenceFactory COORDINATES_FACTORY = new GamaCoordinateSequenceFactory();

	/** The Constant JTS_COORDINATES_FACTORY. */
	public static final CoordinateSequenceFactory JTS_COORDINATES_FACTORY = CoordinateArraySequenceFactory.instance();

	/**
	 * Instantiates a new gama geometry factory.
	 */
	public GamaGeometryFactory() {
		super(COORDINATES_FACTORY);
	}

	/**
	 * Creates a new GamaGeometry object.
	 *
	 * @param geometries
	 *            the geometries
	 * @return the geometry collection
	 */
	public GeometryCollection createCollection(final Geometry... geometries) {
		return new GeometryCollection(geometries, this);
	}

	/**
	 * Checks if is ring.
	 *
	 * @param pts
	 *            the pts
	 * @return true, if is ring
	 */
	public static boolean isRing(final Coordinate[] pts) {
		if (pts.length < 4 || !pts[0].equals(pts[pts.length - 1])) return false;
		return true;
	}

	/**
	 * Checks if is ring.
	 *
	 * @param pts
	 *            the pts
	 * @return true, if is ring
	 */
	public static boolean isRing(final List<GamaPoint> pts) {
		final int size = pts.size();
		if (size < 4 || !pts.get(0).equals(pts.get(size - 1))) return false;
		return true;
	}

	// public static boolean isRing(final double[] array) {
	// final int size = array.length;
	// if (size < 12) { return false; }
	// if (array[0] != array[size - 3] || array[1] != array[size - 2] || array[2] != array[size - 1]) { return false; }
	// return true;
	// }

	/**
	 * Linear rings are created using a simple coordinate array, without enforcing any clockwiseness condition.
	 */
	@Override
	public LinearRing createLinearRing(final Coordinate[] coordinates) {
		Coordinate[] coords = coordinates;
		if (!isRing(coords)) { coords = ArrayUtils.add(coords, coords[0]); }
		return createLinearRing(JTS_COORDINATES_FACTORY.create(coords));
	}

	/**
	 * Builds the rectangle.
	 *
	 * @param points
	 *            the points
	 * @return the polygon
	 */
	public Polygon createRectangle(final Coordinate... points) {
		final CoordinateSequenceFactory fact = GamaGeometryFactory.COORDINATES_FACTORY;
		final CoordinateSequence cs = fact.create(points);
		final LinearRing geom = GeometryUtils.GEOMETRY_FACTORY.createLinearRing(cs);
		return GeometryUtils.GEOMETRY_FACTORY.createPolygon(geom, null);
	}

	/**
	 * Polygons are created after ensuring that the coordinate sequence in them has been turned clockwise
	 */

	@Override
	public Polygon createPolygon(final LinearRing shell, final LinearRing[] holes) {
		final LinearRing shellClockwise = turnClockwise(shell);
		if (holes != null) { for (int i = 0; i < holes.length; i++) { holes[i] = turnClockwise(holes[i]); } }
		return super.createPolygon(shellClockwise, holes);
	}

	/**
	 * Turn clockwise.
	 *
	 * @param ring
	 *            the ring
	 * @return the linear ring
	 */
	private LinearRing turnClockwise(final LinearRing ring) {
		if (ring == null || ring.isEmpty()) return ring;
		return createLinearRing(COORDINATES_FACTORY.create(ring.getCoordinateSequence()));
	}

	@Override
	public GamaCoordinateSequenceFactory getCoordinateSequenceFactory() { return COORDINATES_FACTORY; }

	/**
	 * Creates a new GamaGeometry object.
	 *
	 * @param coordinates
	 *            the coordinates
	 * @param copyPoints
	 *            the copy points
	 * @return the line string
	 */
	public LineString createLineString(final GamaPoint[] coordinates, final boolean copyPoints) {
		return createLineString(COORDINATES_FACTORY.create(coordinates, copyPoints));
	}

	/**
	 * Creates one or several rectangle(s) out of a (possibly poly) line (see
	 * https://stackoverflow.com/questions/1936934/turn-a-line-into-a-rectangle)
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometry
	 *            the geometry
	 * @param depth
	 *            the depth
	 * @return the geometry
	 * @date 14 oct. 2023
	 */
	public Geometry createFatLine(final Geometry geometry, final double thickness) {
		ICoordinates c = GeometryUtils.getContourCoordinates(geometry);
		Polygon[] rectangles = new Polygon[c.size() - 1];
		int[] index = { 0 };
		c.visit((p0, p1) -> {
			double x1 = p1.x, x0 = p0.x, y1 = p1.y, y0 = p0.y;
			double dx = x1 - x0; // delta x
			double dy = y1 - y0; // delta y
			double linelength = p1.distance(p0);
			dx /= linelength;
			dy /= linelength;
			// Ok, (dx, dy) is now a unit vector pointing in the direction of the line
			// A perpendicular vector is given by (-dy, dx)
			double px = 0.5d * thickness * -dy; // perpendicular vector with lenght thickness * 0.5
			double py = 0.5d * thickness * dx;
			rectangles[index[0]] = createRectangle(new GamaPoint(x0 + px, y0 + py), new GamaPoint(x1 + px, y1 + py),
					new GamaPoint(x1 - px, y1 - py), new GamaPoint(x0 - px, y0 - py), new GamaPoint(x0 + px, y0 + py));
			index[0] += 1;
		});
		GeometryCollection result = createGeometryCollection(rectangles);
		return result.union();
	}

}
