package ummisco.gama.opengl.scene;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.jogamp.opengl.GL2;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;

public class TextureHelper {

	private final Envelope3D envelope = new Envelope3D();
	Vector3D towardsZ, towardsX, towardsY;
	Rotation rotation;
	boolean horizontal;

	public TextureHelper() {}

	public void computeTextureCoordinates(final ICoordinates vertices, final GamaPoint normal,
			final boolean clockwise) {
		final ICoordinates seq = (ICoordinates) vertices.clone();
		horizontal = normal.z == 1d;
		//
		Rotation alignZAxes = Rotation.IDENTITY;
		if (!horizontal) {
			final Vector3D towardsZ = normal.toVector3D();
			alignZAxes = new Rotation(towardsZ, clockwise ? Vector3D.MINUS_K : Vector3D.PLUS_K);
			seq.applyRotation(alignZAxes);
		}
		//
		final Vector3D towardsX = seq.directionBetweenOriginAndFirstPoint().toVector3D();
		final Rotation alignXAxes = new Rotation(towardsX, Vector3D.PLUS_I);
		seq.applyRotation(alignXAxes);
		//
		// final Vector3D towardsY = seq.directionBetweenLastPointAndOrigin().toVector3D();
		// final Rotation alignYAxes = new Rotation(towardsY, Vector3D.MINUS_J);
		// seq.applyRotation(alignYAxes);

		seq.getEnvelopeInto(envelope);
		if (horizontal)
			rotation = alignXAxes;
		else
			rotation = /* alignYAxes.applyTo */alignXAxes.applyTo(alignZAxes);
	}

	public void process(final GL2 gl, final double... coords) {
		rotation.applyTo(coords, coords);
		final double u = 1 - (coords[0] - envelope.getMinX()) / envelope.getWidth();
		final double v = 1 - (coords[1] - envelope.getMinY()) / envelope.getHeight();
		gl.glTexCoord2d(v, u);
	}

}
