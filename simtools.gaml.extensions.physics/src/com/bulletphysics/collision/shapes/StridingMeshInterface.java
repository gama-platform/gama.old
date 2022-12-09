/*******************************************************************************************************
 *
 * StridingMeshInterface.java, in simtools.gaml.extensions.physics, is part of the source code of the
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

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * StridingMeshInterface is the abstract class for high performance access to triangle meshes. It allows for sharing
 * graphics and collision meshes. Also it provides locking/unlocking of graphics meshes that are in GPU memory.
 *
 * @author jezek2
 */
public abstract class StridingMeshInterface {

	/** The scaling. */
	protected final Vector3f scaling = new Vector3f(1f, 1f, 1f);

	/**
	 * Internal process all triangles.
	 *
	 * @param callback the callback
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 */
	public void internalProcessAllTriangles( final InternalTriangleIndexCallback callback,
			final Vector3f aabbMin, final Vector3f aabbMax) {
		int graphicssubparts = getNumSubParts();
		Vector3f[] triangle/* [3] */ = new Vector3f[] { VECTORS.get(), VECTORS.get(), VECTORS.get() };

		Vector3f meshScaling = getScaling(VECTORS.get());

		for (int part = 0; part < graphicssubparts; part++) {
			VertexData data = getLockedReadOnlyVertexIndexBase(part);

			for (int i = 0, cnt = data.getIndexCount() / 3; i < cnt; i++) {
				data.getTriangle(i * 3, meshScaling, triangle);
				callback.internalProcessTriangleIndex( triangle, part, i);
			}

			unLockReadOnlyVertexBase(part);
		}
		VECTORS.release(triangle[0], triangle[1], triangle[2], meshScaling);
	}

	/**
	 * The Class AabbCalculationCallback.
	 */
	private static class AabbCalculationCallback implements InternalTriangleIndexCallback {
		
		/** The aabb min. */
		public final Vector3f aabbMin = new Vector3f(1e30f, 1e30f, 1e30f);
		
		/** The aabb max. */
		public final Vector3f aabbMax = new Vector3f(-1e30f, -1e30f, -1e30f);

		@Override
		public void internalProcessTriangleIndex( final Vector3f[] triangle, final int partId,
				final int triangleIndex) {
			VectorUtil.setMin(aabbMin, triangle[0]);
			VectorUtil.setMax(aabbMax, triangle[0]);
			VectorUtil.setMin(aabbMin, triangle[1]);
			VectorUtil.setMax(aabbMax, triangle[1]);
			VectorUtil.setMin(aabbMin, triangle[2]);
			VectorUtil.setMax(aabbMax, triangle[2]);
		}
	}

	/**
	 * Calculate aabb brute force.
	 *
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 */
	public void calculateAabbBruteForce( final Vector3f aabbMin, final Vector3f aabbMax) {
		// first calculate the total aabb for all triangles
		AabbCalculationCallback aabbCallback = new AabbCalculationCallback();
		aabbMin.set(-1e30f, -1e30f, -1e30f);
		aabbMax.set(1e30f, 1e30f, 1e30f);
		internalProcessAllTriangles( aabbCallback, aabbMin, aabbMax);

		aabbMin.set(aabbCallback.aabbMin);
		aabbMax.set(aabbCallback.aabbMax);
	}

	/**
	 * Get read and write access to a subpart of a triangle mesh. This subpart has a continuous array of vertices and
	 * indices. In this way the mesh can be handled as chunks of memory with striding very similar to OpenGL vertexarray
	 * support. Make a call to unLockVertexBase when the read and write access is finished.
	 */
	public abstract VertexData getLockedVertexIndexBase(int subpart/* =0 */);

	/**
	 * Gets the locked read only vertex index base.
	 *
	 * @param subpart the subpart
	 * @return the locked read only vertex index base
	 */
	public abstract VertexData getLockedReadOnlyVertexIndexBase(int subpart/* =0 */);

	/**
	 * unLockVertexBase finishes the access to a subpart of the triangle mesh. Make a call to unLockVertexBase when the
	 * read and write access (using getLockedVertexIndexBase) is finished.
	 */
	public abstract void unLockVertexBase(int subpart);

	/**
	 * Un lock read only vertex base.
	 *
	 * @param subpart the subpart
	 */
	public abstract void unLockReadOnlyVertexBase(int subpart);

	/**
	 * getNumSubParts returns the number of seperate subparts. Each subpart has a continuous array of vertices and
	 * indices.
	 */
	public abstract int getNumSubParts();

	/**
	 * Preallocate vertices.
	 *
	 * @param numverts the numverts
	 */
	public abstract void preallocateVertices(int numverts);

	/**
	 * Preallocate indices.
	 *
	 * @param numindices the numindices
	 */
	public abstract void preallocateIndices(int numindices);

	/**
	 * Gets the scaling.
	 *
	 * @param out the out
	 * @return the scaling
	 */
	public Vector3f getScaling(final Vector3f out) {
		out.set(scaling);
		return out;
	}

	/**
	 * Sets the scaling.
	 *
	 * @param scaling the new scaling
	 */
	public void setScaling(final Vector3f scaling) {
		this.scaling.set(scaling);
	}

}
