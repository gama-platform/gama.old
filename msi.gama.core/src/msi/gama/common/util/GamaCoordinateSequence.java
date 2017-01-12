package msi.gama.common.util;

import static com.google.common.collect.Iterators.forArray;
import static com.vividsolutions.jts.algorithm.CGAlgorithms.isCCW;
import static org.apache.commons.lang.ArrayUtils.reverse;

import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * Clockwise sequence of points. Allows several computations (on convexity, etc.) and a cheap visitor pattern. Be aware
 * that CW property is not maintained if individual points are modified via the setOrdinate() method and if the sequence
 * is not a ring. All other methods should however maintain it.
 * 
 * @author A. Drogoul
 *
 */

public class GamaCoordinateSequence implements ICoordinates {

	/**
	 * The final array of GamaPoint, considered to be internally mutable
	 */
	final GamaPoint[] points;

	/**
	 * @param points2
	 */
	GamaCoordinateSequence(final Coordinate... points2) {
		this(true, points2);
	}

	GamaCoordinateSequence(final boolean copy, final Coordinate... points2) {
		if (copy) {
			final int size = points2.length;
			points = new GamaPoint[size];
			for (int i = 0; i < size; i++) {
				points[i] = new GamaPoint(points2[i]);
			}
			turnClockwise(points);
		} else {
			points = (GamaPoint[]) points2;
		}
	}

	/*
	 * @param values [x0, y0, z0, x1, y1, z1, ...]
	 */
	GamaCoordinateSequence(final double... values) {
		final int size = values.length / 3;
		points = new GamaPoint[size];
		for (int i = 0; i < values.length; i += 3)
			points[i / 3] = new GamaPoint(values[i], values[i + 1], values[i + 2]);
		turnClockwise(points);
	}

	/**
	 * @param size
	 */
	GamaCoordinateSequence(final int size) {
		points = new GamaPoint[size];
		for (int i = 0; i < size; i++)
			points[i] = new GamaPoint(0d, 0d, 0d);
	}

	/**
	 * Method getDimension()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#getDimension()
	 */
	@Override
	public int getDimension() {
		return 3;
	}

	/**
	 * Makes a complete copy (incl. cloning the points themselves)
	 */
	@Override
	public GamaCoordinateSequence clone() {
		return new GamaCoordinateSequence(true, points);
	}

	/**
	 * Method getCoordinate()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#getCoordinate(int)
	 */
	@Override
	public GamaPoint getCoordinate(final int i) {
		return points[i];
	}

	@Override
	public GamaPoint at(final int i) {
		return points[i];
	}

	/**
	 * Method getCoordinateCopy()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#getCoordinateCopy(int)
	 */
	@Override
	public GamaPoint getCoordinateCopy(final int i) {
		return new GamaPoint((Coordinate) points[i]);
	}

	/**
	 * Method getCoordinate()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#getCoordinate(int, com.vividsolutions.jts.geom.Coordinate)
	 */
	@Override
	public void getCoordinate(final int index, final Coordinate coord) {
		coord.setCoordinate(points[index]);
	}

	/**
	 * Method getX()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#getX(int)
	 */
	@Override
	public double getX(final int index) {
		return points[index].x;
	}

	/**
	 * Method getY()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#getY(int)
	 */
	@Override
	public double getY(final int index) {
		return points[index].y;
	}

	/**
	 * Method getOrdinate()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#getOrdinate(int, int)
	 */
	@Override
	public double getOrdinate(final int index, final int ordinateIndex) {
		return points[index].getOrdinate(ordinateIndex);
	}

	/**
	 * Method size()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#size()
	 */
	@Override
	public int size() {
		return points.length;
	}

	/**
	 * Method setOrdinate(). Be aware that CW property is not maintained
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#setOrdinate(int, int, double)
	 */
	@Override
	public void setOrdinate(final int index, final int ordinateIndex, final double value) {
		points[index].setOrdinate(ordinateIndex, value);
	}

	/**
	 * Method toCoordinateArray()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#toCoordinateArray()
	 */
	@Override
	public GamaPoint[] toCoordinateArray() {
		return points;
	}

	/**
	 * Method expandEnvelope()
	 * 
	 * @see com.vividsolutions.jts.geom.CoordinateSequence#expandEnvelope(com.vividsolutions.jts.geom.Envelope)
	 */
	@Override
	public Envelope expandEnvelope(final Envelope env) {
		// TODO Create an Envelope3D ??
		for (final GamaPoint p : points) {
			env.expandToInclude(p);
		}
		return env;
	}

	@Override
	public Iterator<GamaPoint> iterator() {
		return forArray(points);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.util.ICoordinates#getCenter()
	 */
	@Override
	public GamaPoint getCenter() {
		final GamaPoint p = new GamaPoint();
		addCenterTo(p);
		return p;
	}

	@Override
	public void addCenterTo(final GamaPoint other) {
		final int size = isRing(points) ? points.length - 1 : points.length;
		double x = 0, y = 0, z = 0;
		for (int i = 0; i < size; i++) {
			final GamaPoint p = points[i];
			x += p.x;
			y += p.y;
			z += p.z;
		}
		x /= size;
		y /= size;
		z /= size;
		other.x += x;
		other.y += y;
		other.z += z;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.util.ICoordinates#yNegated()
	 */
	@Override
	public ICoordinates yNegated() {
		final int size = points.length;
		final GamaPoint[] points2 = new GamaPoint[size];
		for (int i = 0; i < size; i++) {
			// CW property is ensured by reversing the resulting array
			points2[i] = points[size - i - 1].yNegated();
		}
		final GamaCoordinateSequence result = new GamaCoordinateSequence(false, points2);
		return result;
	}

	@Override
	public boolean isConvex() {
		final int n = points.length - 1;
		if (n < 4)
			return true;
		boolean sign = false;
		for (int i = 0; i < n; i++) {
			final double dx1 = points[(i + 2) % n].x - points[(i + 1) % n].x;
			final double dy1 = points[(i + 2) % n].y - points[(i + 1) % n].y;
			final double dx2 = points[i].x - points[(i + 1) % n].x;
			final double dy2 = points[i].y - points[(i + 1) % n].y;
			final double zcrossproduct = dx1 * dy2 - dy1 * dx2;
			if (i == 0)
				sign = zcrossproduct > 0;
			else if (sign != zcrossproduct > 0)
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.util.ICoordinates#isClockwise(msi.gama.metamodel.shape.GamaPoint)
	 */
	public static boolean isClockwise(final GamaPoint... points) {
		if (isRing(points))
			return !isCCW(points);
		double sum = 0.0;
		for (int i = 0; i < points.length; i++) {
			final Coordinate v1 = points[i];
			final Coordinate v2 = points[(i + 1) % points.length];
			sum += (v2.x - v1.x) * (v2.y + v1.y);
		}
		return sum < 0.0;
	}

	/**
	 * Turns this sequence of coordinates into a clockwise orientation. Only done for rings (as it may change the
	 * definition of line strings)
	 * 
	 * @param points
	 * @return
	 */
	public static void turnClockwise(final GamaPoint... points) {
		if (!isRing(points))
			return;
		if (isCCW(points))
			reverse(points);
	}

	@FunctionalInterface
	public static interface PairVisitor {
		public void process(GamaPoint p1, GamaPoint p2);
	}

	@FunctionalInterface
	public static interface IndexedVisitor {
		public void process(final double x, final double y, final double z, final int i);
	}

	@Override
	public void visit(final IndexedVisitor v, final int max, final boolean clockwise) {
		final int limit = max < 0 || max > points.length ? points.length : max;
		final boolean reversed = isRing(points) && !clockwise;
		if (reversed)
			reverseVisit(v, limit);
		else
			visit(v, limit);
	}

	private void visit(final IndexedVisitor v, final int max) {
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points[i];
			v.process(p.x, p.y, p.z, i);
		}
	}

	private void reverseVisit(final IndexedVisitor v, final int max) {
		for (int i = max - 1, j = 0; i >= 0; i--, j++) {
			final GamaPoint p = points[i];
			v.process(p.x, p.y, p.z, j);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.util.ICoordinates#visitConsecutive(msi.gama.common.util.GamaCoordinateSequence.PairVisitor)
	 */
	@Override
	public void visit(final PairVisitor v) {
		for (int i = 0; i < points.length - 1; i++) {
			v.process(points[i], points[i + 1]);
		}
	}

	/**
	 * Computes the normal to this sequence of points based on Newell's algorithm, which has proved to be quite robust
	 * even with self-intersecting sequences or non-convex polygons. Its downside is that it processes all the points
	 * (instead of processing only 3 of them) but robustness has a price ! This algorithm only operates on rings (this
	 * is ensured in the code by processing the first point in case the sequence is not a ring).
	 * 
	 * @param clockwise
	 *            whether to obtain the normal facing up (for clockwise sequences) or down.
	 * @return
	 */
	@Override
	public GamaPoint getNormal(final boolean clockwise) {
		final GamaPoint normal = new GamaPoint();
		getNormal(clockwise, 1, normal);
		return normal;
	}

	@Override
	public void getNormal(final boolean clockwise, final double factor, final GamaPoint normal) {
		normal.setLocation(0, 0, 0);
		for (int i = 0; i < points.length - 1; i++) {
			final GamaPoint v0 = points[i];
			final GamaPoint v1 = points[i + 1];
			normal.x += (v0.y - v1.y) * (v0.z + v1.z);
			normal.y += (v0.z - v1.z) * (v0.x + v1.x);
			normal.z += (v0.x - v1.x) * (v0.y + v1.y);
		}
		if (!isRing(points)) {
			final GamaPoint v0 = points[0];
			final GamaPoint v1 = points[1];
			normal.x += (v0.y - v1.y) * (v0.z + v1.z);
			normal.y += (v0.z - v1.z) * (v0.x + v1.x);
			normal.z += (v0.x - v1.x) * (v0.y + v1.y);
		}
		final double norm = clockwise ? -normal.norm() : normal.norm();
		normal.divideBy(norm / factor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.util.ICoordinates#applyTranslation(int, double, double, double)
	 */
	@Override
	public void applyTranslation(final int i, final double dx, final double dy, final double dz) {
		points[i].x += dx;
		points[i].y += dy;
		points[i].z += dz;
	}

	@Override
	public boolean isClockwise() {
		return isClockwise(points);
	}

	@Override
	public double averageZ() {
		double sum = 0d;
		if (points.length == 0)
			return sum;
		for (final GamaPoint p : points) {
			sum += p.z;
		}
		return sum / points.length;
	}

	public static boolean isRing(final GamaPoint[] pts) {
		if (pts.length < 4)
			return false;
		if (!pts[0].equals(pts[pts.length - 1]))
			return false;
		return true;
	}

	@Override
	public void replaceWith(final GamaPoint... points2) {
		final int size = Math.min(points2.length, points.length);
		for (int i = 0; i < size; i++) {
			points[i].setCoordinate(points2[i]);
		}
		turnClockwise(points);
	}

	@Override
	public void replaceWith(final double... points2) {
		final int incomingSize = points2.length / 3;
		final int size = Math.min(incomingSize, points.length);
		for (int i = 0; i < points2.length; i += 3) {
			final GamaPoint self = points[i / 3];
			self.x = points2[i];
			self.y = points2[i + 1];
			self.z = points2[i + 2];
		}
		turnClockwise(points);
	}

}