package msi.gama.common.geometry;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;

public class GamaCoordinateSequenceWithEnvelope extends GamaCoordinateSequence {

	final Envelope3D envelope = new Envelope3D();

	public GamaCoordinateSequenceWithEnvelope(final Coordinate... points2) {
		super(points2);
		getEnvelopeInto(envelope);
	}

	public GamaCoordinateSequenceWithEnvelope(final boolean copy, final Coordinate... points2) {
		super(copy, points2);
		getEnvelopeInto(envelope);
	}

	public GamaCoordinateSequenceWithEnvelope(final int size) {
		super(size);
		getEnvelopeInto(envelope);
	}

	@Override
	public GamaCoordinateSequence clone() {
		return new GamaCoordinateSequenceWithEnvelope(points);
	}

	@Override
	public GamaPoint getCenter() {
		return envelope.centre();
	}

	@Override
	public Envelope3D getEnvelope() {
		return envelope;
	}

	@Override
	public void setOrdinate(final int index, final int ordinateIndex, final double value) {
		super.setOrdinate(index, ordinateIndex, value);
		getEnvelopeInto(envelope);
	}

	@Override
	public Envelope expandEnvelope(final Envelope env) {
		env.expandToInclude(envelope);
		return env;
	}

	@Override
	public void getNormal(final boolean clockwise, final double factor, final GamaPoint normal) {
		// TODO Auto-generated method stub
		super.getNormal(clockwise, factor, normal);
	}

	@Override
	public double averageZ() {
		return envelope.centre().z;
	}

	@Override
	public void replaceWith(final GamaPoint... points2) {
		super.replaceWith(points2);
		getEnvelopeInto(envelope);
	}

	@Override
	public void replaceWith(final double... points2) {
		super.replaceWith(points2);
		getEnvelopeInto(envelope);
	}

	@Override
	public void applyRotation(final Rotation rotation) {
		super.applyRotation(rotation);
		getEnvelopeInto(envelope);
	}

	@Override
	public void replaceWith(final int i, final double x, final double y, final double z) {
		super.replaceWith(i, x, y, z);
		getEnvelopeInto(envelope);
	}

	@Override
	public boolean isHorizontal() {
		return envelope.isHorizontal();
	}

}
