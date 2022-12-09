/*******************************************************************************************************
 *
 * MinkowskiSumShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
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

	/** The trans A. */
	private final Transform transA = new Transform();
	
	/** The trans B. */
	private final Transform transB = new Transform();
	
	/** The shape A. */
	private final ConvexShape shapeA;
	
	/** The shape B. */
	private final ConvexShape shapeB;

	/**
	 * Instantiates a new minkowski sum shape.
	 *
	 * @param shapeA the shape A
	 * @param shapeB the shape B
	 */
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

	/**
	 * Sets the transform A.
	 *
	 * @param transA the new transform A
	 */
	public void setTransformA(final Transform transA) {
		this.transA.set(transA);
	}

	/**
	 * Sets the transform B.
	 *
	 * @param transB the new transform B
	 */
	public void setTransformB(final Transform transB) {
		this.transB.set(transB);
	}

	/**
	 * Gets the transform A.
	 *
	 * @param dest the dest
	 * @return the transform A
	 */
	public void getTransformA(final Transform dest) {
		dest.set(transA);
	}

	/**
	 * Gets the transform B.
	 *
	 * @param dest the dest
	 * @return the transform B
	 */
	public void getTransformB(final Transform dest) {
		dest.set(transB);
	}

}
