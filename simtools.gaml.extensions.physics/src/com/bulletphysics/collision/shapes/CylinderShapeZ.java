/*******************************************************************************************************
 *
 * CylinderShapeZ.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

/**
 * Cylinder shape around the Z axis.
 *
 * @author jezek2
 */
public class CylinderShapeZ extends CylinderShape {

	/**
	 * Instantiates a new cylinder shape Z.
	 *
	 * @param halfExtents the half extents
	 */
	public CylinderShapeZ(final Vector3f halfExtents) {
		super(halfExtents, false);
		upAxis = 2;
		recalcLocalAabb();
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec, final Vector3f out) {
		Vector3f tmp = getHalfExtentsWithMargin(VECTORS.get());
		Vector3f result = cylinderLocalSupportZ(tmp, vec, out);
		VECTORS.release(tmp);
		return result;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		for (int i = 0; i < numVectors; i++) {
			Vector3f tmp = getHalfExtentsWithMargin(VECTORS.get());
			cylinderLocalSupportZ(tmp, vectors[i], supportVerticesOut[i]);
			VECTORS.release(tmp);
		}
	}

	@Override
	public float getRadius() {
		Vector3f tmp = getHalfExtentsWithMargin(VECTORS.get());
		float result = tmp.x;
		VECTORS.release(tmp);
		return result;
	}

	@Override
	public String getName() {
		return "CylinderZ";
	}

}
