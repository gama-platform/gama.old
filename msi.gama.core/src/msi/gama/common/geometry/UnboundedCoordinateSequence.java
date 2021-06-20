/*******************************************************************************************************
 *
 * msi.gama.common.geometry.UnboundedCoordinateSequence.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import static com.google.common.collect.Iterators.forArray;
import static com.google.common.collect.Iterators.limit;

import java.util.Arrays;
import java.util.Iterator;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;

public class UnboundedCoordinateSequence implements ICoordinates {

	final int dimension;
	final static int INITIAL_SIZE = 1000;
	GamaPoint[] points = null;
	int nbPoints;
	final GamaPoint temp = new GamaPoint();

	private void fillFrom(final int begin) {
		for (int i = begin; i < points.length; i++) {
			points[i] = new GamaPoint();
		}
	}

	public UnboundedCoordinateSequence() {
		this(3);
	}

	public UnboundedCoordinateSequence(final int dimension) {
		this.dimension = dimension;
		growTo(INITIAL_SIZE);
	}

	private void growTo(final int size) {
		int begin = 0;
		if (points == null) {
			points = new GamaPoint[size];
		} else {
			if (size <= points.length) return;
			begin = points.length;
			points = Arrays.copyOf(points, Math.max(size, begin + begin / 2));
		}
		fillFrom(begin);
	}

	@Override
	public int getDimension() {
		return 3;
	}

	UnboundedCoordinateSequence(final int dimension, final boolean copy, final int size, final GamaPoint[] points2) {
		this.dimension = dimension;
		growTo(size);
		nbPoints = size;
		for (int i = 0; i < size; i++) {
			points[i].setLocation(points2[i]);
		}
		if (copy) { ensureClockwiseness(); }
	}

	@Override
	public final UnboundedCoordinateSequence copy() {
		return new UnboundedCoordinateSequence(dimension, true, nbPoints, points);
	}

	@Override
	@Deprecated
	public UnboundedCoordinateSequence clone() {
		return copy();
	}

	@Override
	public Coordinate getCoordinateCopy(final int i) {
		return points[i].clone();
	}

	@Override
	public void getCoordinate(final int index, final Coordinate coord) {
		coord.setCoordinate(points[index]);
	}

	@Override
	public double getX(final int index) {
		return points[index].x;
	}

	@Override
	public double getY(final int index) {
		return points[index].y;
	}

	@Override
	public double getOrdinate(final int index, final int ordinateIndex) {
		return points[index].getOrdinate(ordinateIndex);
	}

	@Override
	public int size() {
		return nbPoints;
	}

	@Override
	public void setOrdinate(final int index, final int ordinateIndex, final double value) {
		points[index].setOrdinate(ordinateIndex, value);

	}

	@Override
	public Envelope expandEnvelope(final Envelope env) {
		for (int i = 0; i < nbPoints - 1; i++) {
			env.expandToInclude(points[i]);
		}
		return env;
	}

	@Override
	public Iterator<GamaPoint> iterator() {
		return limit(forArray(points), nbPoints);
	}

	@Override
	public void addCenterTo(final GamaPoint other) {
		final int size = isRing() ? nbPoints - 1 : nbPoints;
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

	@Override
	public GamaPoint getCoordinate(final int i) {
		return points[i];
	}

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
	public GamaPoint[] toCoordinateArray() {
		return points;
	}

	@Override
	public void visit(final IndexedVisitor v, final int max, final boolean clockwise) {
		final int limit = max < 0 || max > nbPoints ? nbPoints : max;
		final boolean reversed = isRing() && !clockwise;
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

	@Override
	public void visitClockwise(final VertexVisitor v) {
		final int max = isRing() ? nbPoints - 1 : nbPoints;
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points[i];
			v.process(p.x, p.y, p.z);
		}

	}

	@Override
	public void visitYNegatedCounterClockwise(final VertexVisitor v) {
		final int max = isRing() ? nbPoints - 1 : nbPoints;
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points[i];
			v.process(p.x, -p.y, p.z);
		}

	}

	@Override
	public void visit(final PairVisitor v) {
		for (int i = 0; i < nbPoints - 1; i++) {
			v.process(points[i], points[i + 1]);
		}
	}

	@Override
	public void getNormal(final boolean clockwise, final double factor, final GamaPoint normal) {
		normal.setLocation(0, 0, 0);
		if (nbPoints < 3) return;
		for (int i = 0; i < nbPoints - 1; i++) {
			final GamaPoint v0 = points[i];
			final GamaPoint v1 = points[i + 1];
			normal.x += (v0.y - v1.y) * (v0.z + v1.z);
			normal.y += (v0.z - v1.z) * (v0.x + v1.x);
			normal.z += (v0.x - v1.x) * (v0.y + v1.y);
		}
		if (!isRing()) {
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
		if (nbPoints == 0) return sum;
		for (int i = 0; i < nbPoints; i++) {
			sum += points[i].z;
		}
		return sum / nbPoints;
	}

	@Override
	public ICoordinates setTo(final GamaPoint... points2) {
		growTo(points2.length);
		nbPoints = points2.length;
		for (int i = 0; i < nbPoints; i++) {
			points[i].setLocation(points2[i]);
		}
		return this;
	}

	@Override
	public ICoordinates setTo(final int index, final double... points2) {
		growTo(points2.length / 3);
		nbPoints = points2.length / 3;
		for (int i = index / 3; i < nbPoints; i++) {
			points[i].setLocation(points2[i * 3], points2[i * 3 + 1], points2[i * 3 + 2]);
		}
		ensureClockwiseness();
		return this;
	}

	@Override
	public void replaceWith(final int i, final double x, final double y, final double z) {
		if (i < 0 || i >= nbPoints) return;
		points[i].setLocation(x, y, z);

	}

	@Override
	public GamaPoint directionBetweenLastPointAndOrigin() {
		final GamaPoint result = new GamaPoint();
		final GamaPoint origin = points[0];
		for (int i = nbPoints - 1; i > 0; i--) {
			if (!points[i].equals(origin)) {
				result.setLocation(points[i]).subtract(origin).normalize();
				return result;
			}
		}
		return result;
	}

	@Override
	public void applyRotation(final Rotation3D rotation) {
		for (int i = 0; i < nbPoints; i++) {
			rotation.applyTo(points[i]);
		}
	}

	@Override
	public boolean isHorizontal() {
		final double z = points[0].z;
		for (int i = 1; i < nbPoints; i++) {
			if (points[i].z != z) return false;
		}
		return true;
	}

	@Override
	public double getLength() {
		double result = 0;
		for (int i = 1; i < nbPoints; i++) {
			result += points[i].euclidianDistanceTo(points[i - 1]);
		}
		return result;
	}

	@Override
	public void setAllZ(final double elevation) {
		for (int i = 0; i < nbPoints; i++) {
			points[i].z = elevation;
		}
	}

	@Override
	public boolean isCoveredBy(final Envelope3D envelope3d) {
		for (int i = 0; i < nbPoints; i++) {
			if (!envelope3d.covers(points[i])) return false;
		}
		return true;
	}

	@Override
	public boolean isClockwise() {
		return signedArea() > 0;
	}

	@Override
	public void completeRing() {
		points[nbPoints++] = points[0];
	}

	@Override
	public void translateBy(final double i, final double j, final double k) {
		for (int index = 0; i < nbPoints; index++) {
			points[index].add(i, j, k);
		}

	}

	@Override
	public void ensureClockwiseness() {
		if (isRing() && signedArea() <= 0) { reverse(); }
	}

	public boolean isRing() {
		if (nbPoints < 4) return false;
		return points[0].equals(points[nbPoints - 1]);
	}

	public double signedArea() {
		if (nbPoints < 3) return 0.0;
		double sum = 0.0;
		/**
		 * Based on the Shoelace formula. http://en.wikipedia.org/wiki/Shoelace_formula
		 */
		final double x0 = points[0].x;
		for (int i = 1; i < nbPoints - 1; i++) {
			final double x = points[i].x - x0;
			final double y1 = points[i + 1].y;
			final double y2 = points[i - 1].y;
			sum += x * (y2 - y1);
		}
		return sum / 2.0;
	}

	public void setToYNegated(final ICoordinates other) {
		growTo(other.size());
		nbPoints = other.size();
		int i = 0;
		for (final GamaPoint p : other) {
			points[i++].setLocation(p.x, -p.y, p.z);
		}
		if (isRing()) { reverse(); }
	}

	public void setTo(final ICoordinates other) {
		growTo(other.size());
		nbPoints = other.size();
		int i = 0;
		for (final GamaPoint p : other) {
			points[i++].setLocation(p);
		}
	}

	public void reverse() {
		for (int i = nbPoints - 1, j = 0; i >= nbPoints / 2; j++, i--) {
			temp.setLocation(points[i]);
			points[i].setLocation(points[j]);
			points[j].setLocation(temp);

		}
	}

}
