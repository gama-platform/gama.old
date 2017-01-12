package msi.gama.common.util;

import java.util.Iterator;

import com.google.common.collect.Iterators;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.util.GamaCoordinateSequence.IndexedVisitor;
import msi.gama.common.util.GamaCoordinateSequence.PairVisitor;
import msi.gama.metamodel.shape.GamaPoint;

public class UniqueCoordinateSequence implements ICoordinates {

	final GamaPoint point;

	public UniqueCoordinateSequence(final Coordinate coord) {
		point = new GamaPoint(coord);
	}

	public UniqueCoordinateSequence(final boolean copy, final GamaPoint gamaPoint) {
		point = gamaPoint;
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public GamaPoint getCoordinate(final int i) {
		return point;
	}

	@Override
	public GamaPoint at(final int i) {
		return point;
	}

	@Override
	public Coordinate getCoordinateCopy(final int i) {
		return new GamaPoint((Coordinate) point);
	}

	@Override
	public void getCoordinate(final int index, final Coordinate coord) {
		coord.x = point.x;
		coord.y = point.y;
		coord.z = point.z;

	}

	@Override
	public double getX(final int index) {
		return point.x;
	}

	@Override
	public double getY(final int index) {
		return point.y;
	}

	@Override
	public double getOrdinate(final int index, final int ordinateIndex) {
		return point.getOrdinate(ordinateIndex);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public void setOrdinate(final int index, final int ordinateIndex, final double value) {
		point.setOrdinate(ordinateIndex, value);

	}

	@Override
	public GamaPoint[] toCoordinateArray() {
		return new GamaPoint[] { point };
	}

	@Override
	public Envelope expandEnvelope(final Envelope env) {
		env.expandToInclude(point);
		return env;
	}

	@Override
	public Iterator<GamaPoint> iterator() {
		return Iterators.singletonIterator(point);
	}

	@Override
	public GamaPoint getCenter() {
		return point;
	}

	@Override
	public ICoordinates yNegated() {
		return new UniqueCoordinateSequence(false, point.yNegated());
	}

	@Override
	public ICoordinates clone() {
		return new UniqueCoordinateSequence(new GamaPoint((Coordinate) point));
	}

	@Override
	public void visit(final IndexedVisitor v, final int max, final boolean reversed) {
		if (max == 0)
			return;
		v.process(point.x, point.y, point.z, 0);
	}

	@Override
	public void visit(final PairVisitor v) {
		// Nothing to do here
	}

	@Override
	public void applyTranslation(final int i, final double dx, final double dy, final double dz) {
		point.x += dx;
		point.y += dy;
		point.z += dz;
	}

	@Override
	public boolean isConvex() {
		return true;
	}

	@Override
	public GamaPoint getNormal(final boolean clockwise) {
		return new GamaPoint(0, 0, clockwise ? -1 : 1);
	}

	@Override
	public void getNormal(final boolean clockwise, final double factor, final GamaPoint normal) {
		normal.setLocation(0, 0, clockwise ? -factor : factor);
	}

	@Override
	public boolean isClockwise() {
		return true;
	}

	@Override
	public double averageZ() {
		return point.z;
	}

	@Override
	public void replaceWith(final GamaPoint... points) {
		if (points.length == 0)
			return;
		final GamaPoint p = points[0];
		point.x = p.x;
		point.y = p.y;
		point.z = p.z;
	}

	@Override
	public void replaceWith(final double... points) {
		if (points.length < 3)
			return;
		point.x = points[0];
		point.y = points[1];
		point.z = points[2];
	}

	@Override
	public void addCenterTo(final GamaPoint other) {
		other.add(point);
	}

}
