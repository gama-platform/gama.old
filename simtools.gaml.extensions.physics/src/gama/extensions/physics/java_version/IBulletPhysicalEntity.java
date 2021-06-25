package gama.extensions.physics.java_version;

import javax.vecmath.Vector3f;

import gama.extensions.physics.common.IPhysicalEntity;
import gama.extensions.physics.common.VectorUtils;
import msi.gama.metamodel.shape.GamaPoint;

public interface IBulletPhysicalEntity extends IPhysicalEntity<Vector3f> {
	@Override
	default Vector3f toVector(final GamaPoint v) {
		return VectorUtils.toBulletVector(v);
	}

	@Override
	default GamaPoint toGamaPoint(final Vector3f v) {
		return VectorUtils.toGamaPoint(v);

	}

	default GamaPoint toGamaPoint(final Vector3f v, final GamaPoint result) {
		return VectorUtils.toGamaPoint(v, result);

	}

	default Vector3f toVector(final GamaPoint v, final Vector3f result) {
		return VectorUtils.toBulletVector(v, result);
	}
}
