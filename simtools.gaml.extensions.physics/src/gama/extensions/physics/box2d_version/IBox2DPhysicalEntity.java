package gama.extensions.physics.box2d_version;

import org.jbox2d.common.Vec2;

import gama.extensions.physics.common.IPhysicalEntity;
import gama.extensions.physics.common.VectorUtils;
import msi.gama.metamodel.shape.GamaPoint;

public interface IBox2DPhysicalEntity extends IPhysicalEntity<Vec2> {
	@Override
	default Vec2 toVector(final GamaPoint v) {
		return VectorUtils.toBox2DVector(v);
	}

	default Vec2 toVector(final GamaPoint v, final Vec2 to) {
		return VectorUtils.toBox2DVector(v, to);
	}

	@Override
	default GamaPoint toGamaPoint(final Vec2 v) {
		return VectorUtils.toGamaPoint(v);

	}

	default GamaPoint toGamaPoint(final Vec2 v, final GamaPoint result) {
		return VectorUtils.toGamaPoint(v, result);
	}

}
