/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;

/**
 * MinkowskiSumShape is only for advanced users. This shape represents implicit based minkowski sum of two convex
 * implicit shapes.
 *
 * @author jezek2
 */
public class MinkowskiSumShape extends ConvexInternalShape {

	private final Transform transA = new Transform();
	private final Transform transB = new Transform();
	private final ConvexShape shapeA;
	private final ConvexShape shapeB;

	public MinkowskiSumShape(final ConvexShape shapeA, final ConvexShape shapeB) {
		this.shapeA = shapeA;
		this.shapeB = shapeB;
		this.transA.setIdentity();
		this.transB.setIdentity();
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec, final Vector3f out) {
		Vector3f tmp = VECTORS.get();
		Vector3f supVertexA = VECTORS.get();
		Vector3f supVertexB = VECTORS.get();

		// btVector3 supVertexA = m_transA(m_shapeA->localGetSupportingVertexWithoutMargin(-vec*m_transA.getBasis()));
		tmp.negate(vec);
		MatrixUtil.transposeTransform(tmp, tmp, transA.basis);
		shapeA.localGetSupportingVertexWithoutMargin(tmp, supVertexA);
		transA.transform(supVertexA);

		// btVector3 supVertexB = m_transB(m_shapeB->localGetSupportingVertexWithoutMargin(vec*m_transB.getBasis()));
		MatrixUtil.transposeTransform(tmp, vec, transB.basis);
		shapeB.localGetSupportingVertexWithoutMargin(tmp, supVertexB);
		transB.transform(supVertexB);

		// return supVertexA - supVertexB;
		out.sub(supVertexA, supVertexB);
		return out;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		// todo: could make recursive use of batching. probably this shape is not used frequently.
		for (int i = 0; i < numVectors; i++) {
			localGetSupportingVertexWithoutMargin(vectors[i], supportVerticesOut[i]);
		}
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.MINKOWSKI_SUM_SHAPE_PROXYTYPE;
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		assert false;
		inertia.set(0, 0, 0);
	}

	@Override
	public String getName() {
		return "MinkowskiSum";
	}

	@Override
	public float getMargin() {
		return shapeA.getMargin() + shapeB.getMargin();
	}

	public void setTransformA(final Transform transA) {
		this.transA.set(transA);
	}

	public void setTransformB(final Transform transB) {
		this.transB.set(transB);
	}

	public void getTransformA(final Transform dest) {
		dest.set(transA);
	}

	public void getTransformB(final Transform dest) {
		dest.set(transB);
	}

}
