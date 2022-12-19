/*******************************************************************************************************
 *
 * ConvexInternalShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
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

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * ConvexInternalShape is an internal base class, shared by most convex shape implementations.
 *
 * @author jezek2
 */
public abstract class ConvexInternalShape implements ConvexShape {

	/** The local scaling. */
	// local scaling. collisionMargin is not scaled !
	protected final Vector3f localScaling = new Vector3f(1f, 1f, 1f);
	
	/** The implicit shape dimensions. */
	protected final Vector3f implicitShapeDimensions = new Vector3f();
	
	/** The collision margin. */
	protected float collisionMargin = BulletGlobals.CONVEX_DISTANCE_MARGIN;

	/**
	 * getAabb's default implementation is brute force, expected derived classes to implement a fast dedicated version.
	 */
	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		getAabbSlow(t, aabbMin, aabbMax);
	}

	@Override
	public void getAabbSlow(final Transform trans, final Vector3f minAabb, final Vector3f maxAabb) {
		float margin = getMargin();
		Vector3f vec = VECTORS.get();
		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		for (int i = 0; i < 3; i++) {
			vec.set(0f, 0f, 0f);
			VectorUtil.setCoord(vec, i, 1f);

			MatrixUtil.transposeTransform(tmp1, vec, trans.basis);
			localGetSupportingVertex(tmp1, tmp2);

			trans.transform(tmp2);

			VectorUtil.setCoord(maxAabb, i, VectorUtil.getCoord(tmp2, i) + margin);

			VectorUtil.setCoord(vec, i, -1f);

			MatrixUtil.transposeTransform(tmp1, vec, trans.basis);
			localGetSupportingVertex(tmp1, tmp2);
			trans.transform(tmp2);

			VectorUtil.setCoord(minAabb, i, VectorUtil.getCoord(tmp2, i) - margin);
		}
	}

	@Override
	public Vector3f localGetSupportingVertex(final Vector3f vec, final Vector3f out) {
		Vector3f supVertex = localGetSupportingVertexWithoutMargin(vec, out);

		if (getMargin() != 0f) {
			Vector3f vecnorm = VECTORS.get(vec);
			if (vecnorm.lengthSquared() < BulletGlobals.FLT_EPSILON * BulletGlobals.FLT_EPSILON) {
				vecnorm.set(-1f, -1f, -1f);
			}
			vecnorm.normalize();
			supVertex.scaleAdd(getMargin(), vecnorm, supVertex);
		}
		return out;
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		localScaling.absolute(scaling);
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f out) {
		out.set(localScaling);
		return out;
	}

	@Override
	public float getMargin() {
		return collisionMargin;
	}

	@Override
	public void setMargin(final float margin) {
		this.collisionMargin = margin;
	}

	@Override
	public int getNumPreferredPenetrationDirections() {
		return 0;
	}

	@Override
	public void getPreferredPenetrationDirection(final int index, final Vector3f penetrationVector) {
		throw new InternalError();
	}

}
