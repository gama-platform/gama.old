package msi.gama.common.geometry;

import static msi.gama.common.geometry.GamaGeometryFactory.isRing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;

public class UnboundedCoordinateSequence implements ICoordinates {
	final List<GamaPoint> points;

	public UnboundedCoordinateSequence() {
		points = new ArrayList<>();
	}

	@Override
	public int getDimension() {
		return 3;
	}

	UnboundedCoordinateSequence(final boolean copy, final List<GamaPoint> points2) {

		if (copy) {
			final int size = points2.size();
			final List<GamaPoint> result = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				result.add(new GamaPoint(points2.get(i)));
			}
			points = turnClockwise(result);
		} else {
			points = points2;
		}
	}

	@Override
	public UnboundedCoordinateSequence clone() {
		return new UnboundedCoordinateSequence(true, points);
	}

	@Override
	public Coordinate getCoordinateCopy(final int i) {
		return (Coordinate) points.get(i).clone();
	}

	@Override
	public void getCoordinate(final int index, final Coordinate coord) {
		coord.setCoordinate(points.get(index));
	}

	@Override
	public double getX(final int index) {
		return points.get(index).x;
	}

	@Override
	public double getY(final int index) {
		return points.get(index).y;
	}

	@Override
	public double getOrdinate(final int index, final int ordinateIndex) {
		return points.get(index).getOrdinate(ordinateIndex);
	}

	@Override
	public int size() {
		return points.size();
	}

	@Override
	public void setOrdinate(final int index, final int ordinateIndex, final double value) {
		points.get(index).setOrdinate(ordinateIndex, value);

	}

	@Override
	public Envelope expandEnvelope(final Envelope env) {
		for (final GamaPoint p : points) {
			env.expandToInclude(p);
		}
		return env;
	}

	@Override
	public Iterator<GamaPoint> iterator() {
		return points.iterator();
	}

	@Override
	public void addCenterTo(final GamaPoint other) {
		final int size = isRing(points) ? points.size() - 1 : points.size();
		double x = 0, y = 0, z = 0;
		for (int i = 0; i < size; i++) {
			final GamaPoint p = points.get(i);
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
		return points.get(i);
	}

	@Override
	public ICoordinates yNegated() {
		final int size = points.size();
		final GamaPoint[] points2 = new GamaPoint[size];
		for (int i = 0; i < size; i++) {
			// CW property is ensured by reversing the resulting array
			points2[i] = points.get(size - i - 1).yNegated();
		}
		final GamaCoordinateSequence result = new GamaCoordinateSequence(false, points2);
		return result;

	}

	@Override
	public GamaPoint[] toCoordinateArray() {
		return points.toArray(new GamaPoint[points.size()]);
	}

	@Override
	public void visit(final IndexedVisitor v, final int max, final boolean clockwise) {
		final int limit = max < 0 || max > points.size() ? points.size() : max;
		final boolean reversed = isRing(points) && !clockwise;
		if (reversed)
			reverseVisit(v, limit);
		else
			visit(v, limit);
	}

	private void visit(final IndexedVisitor v, final int max) {
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points.get(i);
			v.process(i, p.x, p.y, p.z);
		}
	}

	private void reverseVisit(final IndexedVisitor v, final int max) {
		for (int i = max - 1, j = 0; i >= 0; i--, j++) {
			final GamaPoint p = points.get(i);
			v.process(j, p.x, p.y, p.z);
		}
	}

	@Override
	public void visitClockwise(final VertexVisitor v) {
		final int max = isRing(points) ? points.size() - 1 : points.size();
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points.get(i);
			v.process(p.x, p.y, p.z);
		}

	}

	@Override
	public void visitCounterClockwise(final VertexVisitor v) {
		final int min = isRing(points) ? 1 : 0;
		for (int i = points.size() - 1; i >= min; i--) {
			final GamaPoint p = points.get(i);
			v.process(p.x, p.y, p.z);
		}

	}

	@Override
	public void visitYNegatedCounterClockwise(final VertexVisitor v) {
		final int max = isRing(points) ? points.size() - 1 : points.size();
		for (int i = 0; i < max; i++) {
			final GamaPoint p = points.get(i);
			v.process(p.x, -p.y, p.z);
		}

	}

	@Override
	public void visitYNegatedClockwise(final VertexVisitor v) {
		final int min = isRing(points) ? 1 : 0;
		for (int i = points.size() - 1; i >= min; i--) {
			final GamaPoint p = points.get(i);
			v.process(p.x, -p.y, p.z);
		}

	}

	@Override
	public void visit(final PairVisitor v) {
		for (int i = 0; i < points.size() - 1; i++) {
			v.process(points.get(i), points.get(i + 1));
		}
	}

	@Override
	public void getNormal(final boolean clockwise, final double factor, final GamaPoint normal) {
		normal.setLocation(0, 0, 0);
		if (points.size() < 3)
			return;
		for (int i = 0; i < points.size() - 1; i++) {
			final GamaPoint v0 = points.get(i);
			final GamaPoint v1 = points.get(i + 1);
			normal.x += (v0.y - v1.y) * (v0.z + v1.z);
			normal.y += (v0.z - v1.z) * (v0.x + v1.x);
			normal.z += (v0.x - v1.x) * (v0.y + v1.y);
		}
		if (!isRing(points)) {
			final GamaPoint v0 = points.get(0);
			final GamaPoint v1 = points.get(1);
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
		if (points.size() == 0)
			return sum;
		for (final GamaPoint p : points) {
			sum += p.z;
		}
		return sum / points.size();
	}

	@Override
	public ICoordinates setTo(final GamaPoint... points2) {
		points.clear();
		points.addAll(Arrays.asList(points2));
		return this;
	}

	@Override
	public ICoordinates setTo(final double... points2) {
		final int size = points2.length;
		points.clear();
		for (int i = 0; i < size; i += 3) {
			final GamaPoint self = new GamaPoint();
			points.add(self);
			self.x = points2[i];
			self.y = points2[i + 1];
			self.z = points2[i + 2];
		}
		turnClockwise(points);
		return this;
	}

	@Override
	public void replaceWith(final int i, final double x, final double y, final double z) {
		if (i < 0 || i >= points.size())
			return;
		points.get(i).setLocation(x, y, z);

	}

	@Override
	public GamaPoint directionBetweenLastPointAndOrigin() {
		final GamaPoint result = new GamaPoint();
		final GamaPoint origin = points.get(0);
		for (int i = points.size() - 1; i > 0; i--)
			if (!points.get(i).equals(origin)) {
				result.setLocation(points.get(i)).subtract(origin).normalize();
				return result;
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
	public boolean isHorizontal() {
		final double z = points.get(0).z;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).z != z)
				return false;
		}
		return true;
	}

	@Override
	public double getLength() {
		double result = 0;
		for (int i = 1; i < points.size(); i++) {
			result += points.get(i).euclidianDistanceTo(points.get(i - 1));
		}
		return result;
	}

	@Override
	public void setAllZ(final double elevation) {
		for (final GamaPoint p : points)
			p.z = elevation;
	}

	@Override
	public boolean isCoveredBy(final Envelope3D envelope3d) {
		for (final GamaPoint p : points) {
			if (!envelope3d.covers(p))
				return false;
		}
		return true;
	}

	@Override
	public boolean isClockwise() {
		return signedArea(points) > 0;
	}

	@Override
	public void completeRing() {
		points.add(points.get(0));
	}

	@Override
	public void translateBy(final double i, final double j, final double k) {
		for (final GamaPoint p : points) {
			p.add(i, j, k);
		}

	}

	@Override
	public void ensureClockwiseness() {
		turnClockwise(points);
	}

	public static List<GamaPoint> turnClockwise(final List<GamaPoint> points) {
		if (!isRing(points))
			return points;
		if (signedArea(points) <= 0) {
			Collections.reverse(points);
		}
		return points;
	}

	public static double signedArea(final List<GamaPoint> ring) {
		if (ring.size() < 3)
			return 0.0;
		double sum = 0.0;
		/**
		 * Based on the Shoelace formula. http://en.wikipedia.org/wiki/Shoelace_formula
		 */
		final double x0 = ring.get(0).x;
		for (int i = 1; i < ring.size() - 1; i++) {
			final double x = ring.get(i).x - x0;
			final double y1 = ring.get(i + 1).y;
			final double y2 = ring.get(i - 1).y;
			sum += x * (y2 - y1);
		}
		return sum / 2.0;
	}

	public void setToYNegated(final ICoordinates other) {
		points.clear();
		for (final GamaPoint p : other.toCoordinateArray()) {
			points.add(p.yNegated());
		}
		if (isRing(points))
			Collections.reverse(points);
	}

	public void setTo(final ICoordinates other) {
		points.clear();
		for (final GamaPoint p : other.toCoordinateArray()) {
			points.add(new GamaPoint(p));
		}

	}

}
