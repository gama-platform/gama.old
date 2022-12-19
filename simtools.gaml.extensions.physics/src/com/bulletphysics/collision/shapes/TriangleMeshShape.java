/*******************************************************************************************************
 *
 * TriangleMeshShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The local aabb min. */
	protected final Vector3f localAabbMin = new Vector3f();
	
	/** The local aabb max. */
	protected final Vector3f localAabbMax = new Vector3f();
	
	/** The mesh interface. */
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

	/**
	 * Local get supporting vertex.
	 *
	 * @param vec the vec
	 * @param out the out
	 * @return the vector 3 f
	 */
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

	/**
	 * Local get supporting vertex without margin.
	 *
	 * @param vec the vec
	 * @param out the out
	 * @return the vector 3 f
	 */
	public Vector3f localGetSupportingVertexWithoutMargin( final Vector3f vec,
			final Vector3f out) {
		assert false;
		return localGetSupportingVertex( vec, out);
	}

	/**
	 * Recalc local aabb.
	 */
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

	/**
	 * Gets the mesh interface.
	 *
	 * @return the mesh interface
	 */
	public StridingMeshInterface getMeshInterface() {
		return meshInterface;
	}

	/**
	 * Gets the local aabb min.
	 *
	 * @param out the out
	 * @return the local aabb min
	 */
	public Vector3f getLocalAabbMin(final Vector3f out) {
		out.set(localAabbMin);
		return out;
	}

	/**
	 * Gets the local aabb max.
	 *
	 * @param out the out
	 * @return the local aabb max
	 */
	public Vector3f getLocalAabbMax(final Vector3f out) {
		out.set(localAabbMax);
		return out;
	}

	@Override
	public String getName() {
		return "TRIANGLEMESH";
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class SupportVertexCallback.
	 */
	private class SupportVertexCallback implements TriangleCallback {
		
		/** The support vertex local. */
		private final Vector3f supportVertexLocal = new Vector3f(0f, 0f, 0f);
		
		/** The world trans. */
		public final Transform worldTrans = new Transform();
		
		/** The max dot. */
		public float maxDot = -1e30f;
		
		/** The support vec local. */
		public final Vector3f supportVecLocal = new Vector3f();

		/**
		 * Instantiates a new support vertex callback.
		 *
		 * @param supportVecWorld the support vec world
		 * @param trans the trans
		 */
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

		/**
		 * Gets the support vertex local.
		 *
		 * @param out the out
		 * @return the support vertex local
		 */
		public Vector3f getSupportVertexLocal(final Vector3f out) {
			out.set(supportVertexLocal);
			return out;
		}
	}

	/**
	 * The Class FilteredCallback.
	 */
	private static class FilteredCallback implements InternalTriangleIndexCallback {
		
		/** The callback. */
		public TriangleCallback callback;
		
		/** The aabb min. */
		public final Vector3f aabbMin = new Vector3f();
		
		/** The aabb max. */
		public final Vector3f aabbMax = new Vector3f();

		/**
		 * Instantiates a new filtered callback.
		 *
		 * @param callback the callback
		 * @param aabbMin the aabb min
		 * @param aabbMax the aabb max
		 */
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
