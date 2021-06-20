/*******************************************************************************************************
 *
 * msi.gama.common.geometry.GamaCoordinateSequence.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import static com.google.common.collect.Iterators.forArray;
import static msi.gama.common.geometry.GamaGeometryFactory.isRing;
import static org.locationtech.jts.algorithm.CGAlgorithms.signedArea;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * Clockwise sequence of points. Supports several computations (rotation, etc.) and a cheap visitor pattern. Be aware
 * that CW property is not maintained if individual points are modified via the setOrdinate() or replaceWith() method
 * and if the sequence is not a ring. All other methods should however maintain it.
 *
 * @author A. Drogoul
 *
 */

public class GamaCoordinateSequence implements ICoordinates {

	final int dimension;

	/**
	 * The final array of GamaPoint, considered to be internally mutable (i.e. points can be changed inside)
	 */
	final GamaPoint[] points;

	/**
	 * Creates a sequence from an array of points. The points will be cloned before being added (to prevent side
	 * effects). The order of the points will not necessarily remain the same if the sequence is a ring (as this class
	 * enforces a clockwise direction of the sequence)
	 *
	 * @param points2
	 *            an array of points
	 */
	GamaCoordinateSequence(final int dimension, final Coordinate... points2) {
		this(dimension, true, points2);
	}

	/**
	 * Creates a sequence from an array of points. If copy is true, the points are cloned before being added to the
	 * sequence (to prevent side effects, for instance). The sequence will be modified to enforce a clockwise direction
	 * if the array represents a ring
	 *
	 * @param copy
	 *            whether or not to copy the points or to add them directly
	 * @param points2
	 *            an array of points
	 */
	GamaCoordinateSequence(final int dimension, final boolean copy, final Coordinate... points2) {
		this.dimension = dimension;
		if (copy) {
			final int size = points2.length;
			points = new GamaPoint[size];
			for (int i = 0; i < size; i++) {
				points[i] = new GamaPoint(points2[i]);
			}
			ensureClockwiseness();
		} else {
			points = (GamaPoint[]) points2;
		}
	}

	/**
	 * Creates a sequence of points with a given size (that may be altered after)
	 *
	 * @param size
	 *            an int > 0 (negative sizes will be treated as 0)
	 */
	GamaCoordinateSequence(final int dimension, final int size) {
		this.dimension = dimension;
		points = new GamaPoint[size < 0 ? 0 : size];
		for (int i = 0; i < size; i++) {
			points[i] = new GamaPoint(0d, 0d, 0d);
		}
	}

	/**
	 * Method getDimension(). Always 3 for these sequences
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#getDimension()
	 */
	@Override
	public int getDimension() {
		return dimension;
	}

	/**
	 * Makes a complete copy of this sequence (incl. cloning the points themselves)
	 */
	@Override
	public final GamaCoordinateSequence copy() {
		return new GamaCoordinateSequence(dimension, true, points);
	}

	@Override
	@Deprecated
	public GamaCoordinateSequence clone() {
		return copy();
	}

	@Override
	public String toString() {
		return Arrays.toString(points);
	}

	/**
	 * Method getCoordinate(). The coordinate is *not* a copy of the original one, so any modification to it will
	 * directly affect the sequence of points
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#getCoordinate(int)
	 */
	@Override
	public GamaPoint getCoordinate(final int i) {
		return points[i];
	}

	/**
	 * Method getCoordinateCopy()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#getCoordinateCopy(int)
	 */
	@Override
	public GamaPoint getCoordinateCopy(final int i) {
		return new GamaPoint(points[i]);
	}

	/**
	 * Method getCoordinate()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#getCoordinate(int, org.locationtech.jts.geom.Coordinate)
	 */
	@Override
	public void getCoordinate(final int index, final Coordinate coord) {
		coord.setCoordinate(points[index]);
	}

	/**
	 * Method getX()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#getX(int)
	 */
	@Override
	public double getX(final int index) {
		return points[index].x;
	}

	/**
	 * Method getY()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#getY(int)
	 */
	@Override
	public double getY(final int index) {
		return points[index].y;
	}

	/**
	 * Method getOrdinate()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#getOrdinate(int, int)
	 */
	@Override
	public double getOrdinate(final int index, final int ordinateIndex) {
		return points[index].getOrdinate(ordinateIndex);
	}

	/**
	 * Method size()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#size()
	 */
	@Override
	public int size() {
		return points.length;
	}

	/**
	 * Method setOrdinate(). Be aware that CW property is not maintained in case of direct modifications like this
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#setOrdinate(int, int, double)
	 */
	@Override
	public void setOrdinate(final int index, final int ordinateIndex, final double value) {
		points[index].setOrdinate(ordinateIndex, value);
	}

	/**
	 * Method toCoordinateArray()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#toCoordinateArray()
	 */
	@Override
	public GamaPoint[] toCoordinateArray() {
		return points;
	}

	/**
	 * Method expandEnvelope()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequence#expandEnvelope(org.locationtech.jts.geom.Envelope)
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
		return new GamaCoordinateSequence(dimension, false, points2);
	}

	@Override
	public void visit(final IndexedVisitor v, final int max, final boolean clockwise) {
		final int limit = max < 0 || max > points.length ? points.length : max;
		final boolean reversed = isRing(points) && !clockwise;
		if (reversed) {
			reverseVisit(v, limit);
		} else {
			visit(v, limit);
		}
	}

	private void visit(final IndexedVisitor v, final int max) {
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points[i];
			v.process(i, p.x, p.y, p.z);
		}
	}

	private void reverseVisit(final IndexedVisitor v, final int max) {
		for (int i = max - 1, j = 0; i >= 0; i--, j++) {
			final GamaPoint p = points[i];
			v.process(j, p.x, p.y, p.z);
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
	 * @param factor
	 *            the factor to multiply the unit normal vector with
	 * @param normal
	 *            the returned vector
	 */

	@Override
	public void getNormal(final boolean clockwise, final double factor, final GamaPoint normal) {
		normal.setLocation(0, 0, 0);
		if (points.length < 3) return;
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

	@Override
	public Envelope3D getEnvelopeInto(final Envelope3D envelope) {
		envelope.setToNull();
		expandEnvelope(envelope);
		return envelope;
	}

	@Override
	public double averageZ() {
		double sum = 0d;
		if (points.length == 0) return sum;
		for (final GamaPoint p : points) {
			sum += p.z;
		}
		return sum / points.length;
	}

	@Override
	public ICoordinates setTo(final GamaPoint... points2) {
		final int size = Math.min(points2.length, points.length);
		for (int i = 0; i < size; i++) {
			points[i].setCoordinate(points2[i]);
		}
		ensureClockwiseness();
		return this;
	}

	@Override
	public ICoordinates setTo(final int index, final double... points2) {
		final int size = Math.min(points2.length, points.length * 3);
		for (int i = index; i < size; i += 3) {
			final GamaPoint self = points[i / 3];
			self.x = points2[i];
			self.y = points2[i + 1];
			self.z = points2[i + 2];
		}
		ensureClockwiseness();
		return this;
	}

	@Override
	public GamaPoint directionBetweenLastPointAndOrigin() {
		final GamaPoint result = new GamaPoint();
		final GamaPoint origin = points[0];
		for (int i = points.length - 1; i > 0; i--) {
			if (!points[i].equals(origin)) {
				result.setLocation(points[i]).subtract(origin).normalize();
				return result;
			}
		}
		return result;
	}

	@Override
	public void applyRotation(final Rotation3D rotation) {
		for (final GamaPoint point : points) {
			rotation.applyTo(point);
		}
	}

	@Override
	public void replaceWith(final int i, final double x, final double y, final double z) {
		if (i < 0 || i >= points.length) return;
		points[i].setLocation(x, y, z);

	}

	@Override
	public boolean isHorizontal() {
		final double z = points[0].z;
		for (int i = 1; i < points.length; i++) {
			if (points[i].z != z) return false;
		}
		return true;
	}

	@Override
	public double getLength() {
		double result = 0;
		for (int i = 1; i < points.length; i++) {
			result += points[i].euclidianDistanceTo(points[i - 1]);
		}
		return result;
	}

	@Override
	public void setAllZ(final double elevation) {
		for (int i = 0; i < points.length; i++) {
			points[i].z = elevation;
		}

	}

	@Override
	public boolean isCoveredBy(final Envelope3D env) {
		for (final GamaPoint point : points) {
			if (!env.covers(point)) return false;
		}
		return true;
	}

	@Override
	public void visitClockwise(final VertexVisitor v) {
		final int max = isRing(points) ? points.length - 1 : points.length;
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points[i];
			v.process(p.x, p.y, p.z);
		}

	}

	/**
	 * Same as clockwise, since it visits the coordinates with y-negated
	 */
	@Override
	public void visitYNegatedCounterClockwise(final VertexVisitor v) {
		final int max = isRing(points) ? points.length - 1 : points.length;
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points[i];
			v.process(p.x, -p.y, p.z);
		}

	}

	@Override
	public boolean isClockwise() {
		return signedArea(points) > 0;
	}

	@Override
	public void completeRing() {
		points[points.length - 1] = points[0];
	}

	@Override
	public void translateBy(final double x, final double y, final double z) {
		for (final GamaPoint p : points) {
			p.add(x, y, z);
		}

	}

	/**
	 * Turns this sequence of coordinates into a clockwise orientation. Only done for rings (as it may change the
	 * definition of line strings)
	 *
	 * @param points
	 * @return
	 */
	@Override
	public void ensureClockwiseness() {
		if (!isRing(points)) return;
		if (signedArea(points) <= 0) { ArrayUtils.reverse(points); }
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof GamaCoordinateSequence)) return false;
		final GamaCoordinateSequence other = (GamaCoordinateSequence) object;
		return Arrays.equals(points, other.points);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(points);
	}

}