/*******************************************************************************************************
 *
 * INativeBulletPhysicalEntity.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.native_version;

import com.jme3.math.Vector3f;

import gama.extensions.physics.common.IPhysicalEntity;
import gama.extensions.physics.common.VectorUtils;
import msi.gama.metamodel.shape.GamaPoint;

/**
 * The Interface INativeBulletPhysicalEntity.
 */
public interface INativeBulletPhysicalEntity extends IPhysicalEntity<Vector3f> {
	
	/**
	 * To vector.
	 *
	 * @param v the v
	 * @return the vector 3 f
	 */
	@Override
	default Vector3f toVector(final GamaPoint v) {
		return VectorUtils.toNativeBulletVector(v);
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @return the gama point
	 */
	@Override
	default GamaPoint toGamaPoint(final Vector3f v) {
		return VectorUtils.toGamaPoint(v);
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @param result the result
	 * @return the gama point
	 */
	default GamaPoint toGamaPoint(final Vector3f v, final GamaPoint result) {
		return VectorUtils.toGamaPoint(v, result);
	}
}
