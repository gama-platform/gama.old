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

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.AabbUtil2;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * Concave triangle mesh abstract class. Use {@link BvhTriangleMeshShape} as concrete implementation.
 *
 * @author jezek2
 */
public abstract class TriangleMeshShape extends ConcaveShape {

	protected final Vector3f localAabbMin = new Vector3f();
	protected final Vector3f localAabbMax = new Vector3f();
	protected StridingMeshInterface meshInterface;

	/**
	 * TriangleMeshShape constructor has been disabled/protected, so that users will not mistakenly use this class.
	 * Don't use btTriangleMeshShape but use btBvhTriangleMeshShape instead!
	 */
	protected TriangleMeshShape(final StridingMeshInterface meshInterface) {
		this.meshInterface = meshInterface;

		// JAVA NOTE: moved to BvhTriangleMeshShape
		// recalcLocalAabb();
	}

	public Vector3f localGetSupportingVertex( final Vector3f vec, final Vector3f out) {
		Vector3f tmp = VECTORS.get();

		Vector3f supportVertex = out;

		Transform ident = TRANSFORMS.get();
		ident.setIdentity();

		SupportVertexCallback supportCallback = new SupportVertexCallback(vec, ident);

		Vector3f aabbMax = VECTORS.get();
		aabbMax.set(1e30f, 1e30f, 1e30f);
		tmp.negate(aabbMax);

		processAllTriangles( supportCallback, tmp, aabbMax);

		supportCallback.getSupportVertexLocal(supportVertex);
		VECTORS.release(tmp, aabbMax);
		TRANSFORMS.release(ident);
		return out;
	}

	public Vector3f localGetSupportingVertexWithoutMargin( final Vector3f vec,
			final Vector3f out) {
		assert false;
		return localGetSupportingVertex( vec, out);
	}

	public void recalcLocalAabb() {
		for (int i = 0; i < 3; i++) {
			Vector3f vec = VECTORS.get();
			vec.set(0f, 0f, 0f);
			VectorUtil.setCoord(vec, i, 1f);
			Vector3f tmp = localGetSupportingVertex( vec, VECTORS.get());
			VectorUtil.setCoord(localAabbMax, i, VectorUtil.getCoord(tmp, i) + collisionMargin);
			VectorUtil.setCoord(vec, i, -1f);
			localGetSupportingVertex( vec, tmp);
			VectorUtil.setCoord(localAabbMin, i, VectorUtil.getCoord(tmp, i) - collisionMargin);
			VECTORS.release(vec, tmp);
		}
	}

	@Override
	public void getAabb(final Transform trans, final Vector3f aabbMin, final Vector3f aabbMax) {
		Vector3f tmp = VECTORS.get();

		Vector3f localHalfExtents = VECTORS.get();
		localHalfExtents.sub(localAabbMax, localAabbMin);
		localHalfExtents.scale(0.5f);

		Vector3f localCenter = VECTORS.get();
		localCenter.add(localAabbMax, localAabbMin);
		localCenter.scale(0.5f);

		Matrix3f abs_b = MATRICES.get(trans.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f center = VECTORS.get(localCenter);
		trans.transform(center);

		Vector3f extent = VECTORS.get();
		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(localHalfExtents);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(localHalfExtents);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(localHalfExtents);

		Vector3f margin = VECTORS.get();
		margin.set(getMargin(), getMargin(), getMargin());
		extent.add(margin);

		aabbMin.sub(center, extent);
		aabbMax.add(center, extent);
		VECTORS.release(tmp, localHalfExtents, localCenter, center, extent, margin);
		MATRICES.release(abs_b);
	}

	@Override
	public void processAllTriangles( final TriangleCallback callback, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		FilteredCallback filterCallback = new FilteredCallback(callback, aabbMin, aabbMax);

		meshInterface.internalProcessAllTriangles( filterCallback, aabbMin, aabbMax);
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		// moving concave objects not supported
		assert false;
		inertia.set(0f, 0f, 0f);
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		meshInterface.setScaling(scaling);
		recalcLocalAabb();
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f out) {
		return meshInterface.getScaling(out);
	}

	public StridingMeshInterface getMeshInterface() {
		return meshInterface;
	}

	public Vector3f getLocalAabbMin(final Vector3f out) {
		out.set(localAabbMin);
		return out;
	}

	public Vector3f getLocalAabbMax(final Vector3f out) {
		out.set(localAabbMax);
		return out;
	}

	@Override
	public String getName() {
		return "TRIANGLEMESH";
	}

	////////////////////////////////////////////////////////////////////////////

	private class SupportVertexCallback implements TriangleCallback {
		private final Vector3f supportVertexLocal = new Vector3f(0f, 0f, 0f);
		public final Transform worldTrans = new Transform();
		public float maxDot = -1e30f;
		public final Vector3f supportVecLocal = new Vector3f();

		public SupportVertexCallback(final Vector3f supportVecWorld, final Transform trans) {
			this.worldTrans.set(trans);
			MatrixUtil.transposeTransform(supportVecLocal, supportVecWorld, worldTrans.basis);
		}

		@Override
		public void processTriangle( final Vector3f[] triangle, final int partId,
				final int triangleIndex) {
			for (int i = 0; i < 3; i++) {
				float dot = supportVecLocal.dot(triangle[i]);
				if (dot > maxDot) {
					maxDot = dot;
					supportVertexLocal.set(triangle[i]);
				}
			}
		}

		// public Vector3f getSupportVertexWorldSpace(final Vector3f out) {
		// out.set(supportVertexLocal);
		// worldTrans.transform(out);
		// return out;
		// }

		public Vector3f getSupportVertexLocal(final Vector3f out) {
			out.set(supportVertexLocal);
			return out;
		}
	}

	private static class FilteredCallback implements InternalTriangleIndexCallback {
		public TriangleCallback callback;
		public final Vector3f aabbMin = new Vector3f();
		public final Vector3f aabbMax = new Vector3f();

		public FilteredCallback(final TriangleCallback callback, final Vector3f aabbMin, final Vector3f aabbMax) {
			this.callback = callback;
			this.aabbMin.set(aabbMin);
			this.aabbMax.set(aabbMax);
		}

		@Override
		public void internalProcessTriangleIndex( final Vector3f[] triangle, final int partId,
				final int triangleIndex) {
			if (AabbUtil2.testTriangleAgainstAabb2(triangle, aabbMin, aabbMax)) {
				// check aabb in triangle-space, before doing this
				callback.processTriangle( triangle, partId, triangleIndex);
			}
		}
	}

}
