package gama.extensions.physics.common;

import msi.gama.metamodel.shape.GamaPoint;

public interface IPhysicalEntity<VectorType> extends IPhysicalConstants {

	VectorType toVector(final GamaPoint v);

	GamaPoint toGamaPoint(VectorType v);

}
