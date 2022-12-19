/*******************************************************************************************************
 *
 * ScaledBvhTriangleMeshShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

// JAVA NOTE: ScaledBvhTriangleMeshShape from 2.73 SP1

/**
 * The ScaledBvhTriangleMeshShape allows to instance a scaled version of an existing {@link BvhTriangleMeshShape}. Note
 * that each {@link BvhTriangleMeshShape} still can have its own local scaling, independent from this
 * ScaledBvhTriangleMeshShape 'localScaling'.
 *
 * @author jezek2
 */
public class ScaledBvhTriangleMeshShape extends ConcaveShape {

	/** The local scaling. */
	protected final Vector3f localScaling = new Vector3f();
	
	/** The bvh tri mesh shape. */
	protected BvhTriangleMeshShape bvhTriMeshShape;

	/**
	 * Instantiates a new scaled bvh triangle mesh shape.
	 *
	 * @param childShape the child shape
	 * @param localScaling the local scaling
	 */
	public ScaledBvhTriangleMeshShape(final BvhTriangleMeshShape childShape, final Vector3f localScaling) {
		this.localScaling.set(localScaling);
		this.bvhTriMeshShape = childShape;
	}

	/**
	 * Gets the child shape.
	 *
	 * @return the child shape
	 */
	public BvhTriangleMeshShape getChildShape() {
		return bvhTriMeshShape;
	}

	@Override
	public void processAllTriangles( final TriangleCallback callback, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		ScaledTriangleCallback scaledCallback = new ScaledTriangleCallback(callback, localScaling);

		Vector3f invLocalScaling = VECTORS.get();
		invLocalScaling.set(1.f / localScaling.x, 1.f / localScaling.y, 1.f / localScaling.z);

		Vector3f scaledAabbMin = VECTORS.get();
		Vector3f scaledAabbMax = VECTORS.get();

		// support negative scaling
		scaledAabbMin.x = localScaling.x >= 0f ? aabbMin.x * invLocalScaling.x : aabbMax.x * invLocalScaling.x;
		scaledAabbMin.y = localScaling.y >= 0f ? aabbMin.y * invLocalScaling.y : aabbMax.y * invLocalScaling.y;
		scaledAabbMin.z = localScaling.z >= 0f ? aabbMin.z * invLocalScaling.z : aabbMax.z * invLocalScaling.z;

		scaledAabbMax.x = localScaling.x <= 0f ? aabbMin.x * invLocalScaling.x : aabbMax.x * invLocalScaling.x;
		scaledAabbMax.y = localScaling.y <= 0f ? aabbMin.y * invLocalScaling.y : aabbMax.y * invLocalScaling.y;
		scaledAabbMax.z = localScaling.z <= 0f ? aabbMin.z * invLocalScaling.z : aabbMax.z * invLocalScaling.z;

		bvhTriMeshShape.processAllTriangles( scaledCallback, scaledAabbMin, scaledAabbMax);
		VECTORS.release(scaledAabbMin, scaledAabbMax, invLocalScaling);
	}

	@Override
	public void getAabb(final Transform trans, final Vector3f aabbMin, final Vector3f aabbMax) {
		Vector3f localAabbMin = bvhTriMeshShape.getLocalAabbMin(VECTORS.get());
		Vector3f localAabbMax = bvhTriMeshShape.getLocalAabbMax(VECTORS.get());

		Vector3f tmpLocalAabbMin = VECTORS.get();
		Vector3f tmpLocalAabbMax = VECTORS.get();
		VectorUtil.mul(tmpLocalAabbMin, localAabbMin, localScaling);
		VectorUtil.mul(tmpLocalAabbMax, localAabbMax, localScaling);

		localAabbMin.x = localScaling.x >= 0f ? tmpLocalAabbMin.x : tmpLocalAabbMax.x;
		localAabbMin.y = localScaling.y >= 0f ? tmpLocalAabbMin.y : tmpLocalAabbMax.y;
		localAabbMin.z = localScaling.z >= 0f ? tmpLocalAabbMin.z : tmpLocalAabbMax.z;
		localAabbMax.x = localScaling.x <= 0f ? tmpLocalAabbMin.x : tmpLocalAabbMax.x;
		localAabbMax.y = localScaling.y <= 0f ? tmpLocalAabbMin.y : tmpLocalAabbMax.y;
		localAabbMax.z = localScaling.z <= 0f ? tmpLocalAabbMin.z : tmpLocalAabbMax.z;

		Vector3f localHalfExtents = VECTORS.get();
		localHalfExtents.sub(localAabbMax, localAabbMin);
		localHalfExtents.scale(0.5f);

		float margin = bvhTriMeshShape.getMargin();
		localHalfExtents.x += margin;
		localHalfExtents.y += margin;
		localHalfExtents.z += margin;

		Vector3f localCenter = VECTORS.get();
		localCenter.add(localAabbMax, localAabbMin);
		localCenter.scale(0.5f);

		Matrix3f abs_b = MATRICES.get(trans.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f center = VECTORS.get(localCenter);
		trans.transform(center);

		Vector3f extent = VECTORS.get();
		Vector3f tmp = VECTORS.get();
		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(localHalfExtents);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(localHalfExtents);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(localHalfExtents);

		aabbMin.sub(center, extent);
		aabbMax.add(center, extent);
		MATRICES.release(abs_b);
		VECTORS.release(center, tmp, extent, localHalfExtents, tmpLocalAabbMin, tmpLocalAabbMax, localAabbMax,
				localAabbMin);
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.SCALED_TRIANGLE_MESH_SHAPE_PROXYTYPE;
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		localScaling.set(scaling);
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f out) {
		out.set(localScaling);
		return out;
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {}

	@Override
	public String getName() {
		return "SCALEDBVHTRIANGLEMESH";
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class ScaledTriangleCallback.
	 */
	private static class ScaledTriangleCallback implements TriangleCallback {
		
		/** The original callback. */
		private final TriangleCallback originalCallback;
		
		/** The local scaling. */
		private final Vector3f localScaling;
		
		/** The new triangle. */
		private final Vector3f[] newTriangle = new Vector3f[3];

		/**
		 * Instantiates a new scaled triangle callback.
		 *
		 * @param originalCallback the original callback
		 * @param localScaling the local scaling
		 */
		public ScaledTriangleCallback(final TriangleCallback originalCallback, final Vector3f localScaling) {
			this.originalCallback = originalCallback;
			this.localScaling = localScaling;

			for (int i = 0; i < newTriangle.length; i++) {
				newTriangle[i] = new Vector3f();
			}
		}

		@Override
		public void processTriangle( final Vector3f[] triangle, final int partId,
				final int triangleIndex) {
			VectorUtil.mul(newTriangle[0], triangle[0], localScaling);
			VectorUtil.mul(newTriangle[1], triangle[1], localScaling);
			VectorUtil.mul(newTriangle[2], triangle[2], localScaling);
			originalCallback.processTriangle( newTriangle, partId, triangleIndex);
		}
	}

}
