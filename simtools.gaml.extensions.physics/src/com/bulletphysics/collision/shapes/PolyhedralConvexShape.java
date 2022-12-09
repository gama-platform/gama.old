/*******************************************************************************************************
 *
 * PolyhedralConvexShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.AabbUtil2;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * PolyhedralConvexShape is an internal interface class for polyhedral convex shapes.
 *
 * @author jezek2
 */
public abstract class PolyhedralConvexShape extends ConvexInternalShape {

	/** The directions. */
	private static Vector3f[] _directions = new Vector3f[] { new Vector3f(1f, 0f, 0f), new Vector3f(0f, 1f, 0f),
			new Vector3f(0f, 0f, 1f), new Vector3f(-1f, 0f, 0f), new Vector3f(0f, -1f, 0f), new Vector3f(0f, 0f, -1f) };

	/** The supporting. */
	private static Vector3f[] _supporting = new Vector3f[] { new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f),
			new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f) };

	/** The local aabb min. */
	protected final Vector3f localAabbMin = new Vector3f(1f, 1f, 1f);
	
	/** The local aabb max. */
	protected final Vector3f localAabbMax = new Vector3f(-1f, -1f, -1f);
	
	/** The is local aabb valid. */
	protected boolean isLocalAabbValid = false;

	// /** optional Hull is for optional Separating Axis Test Hull collision detection, see Hull.cpp */
	// public Hull optionalHull = null;

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec0, final Vector3f out) {
		int i;
		Vector3f supVec = out;
		supVec.set(0f, 0f, 0f);

		float maxDot = -1e30f;

		Vector3f vec = VECTORS.get(vec0);
		float lenSqr = vec.lengthSquared();
		if (lenSqr < 0.0001f) {
			vec.set(1f, 0f, 0f);
		} else {
			float rlen = 1f / (float) Math.sqrt(lenSqr);
			vec.scale(rlen);
		}

		Vector3f vtx = VECTORS.get();
		float newDot;

		for (i = 0; i < getNumVertices(); i++) {
			getVertex(i, vtx);
			newDot = vec.dot(vtx);
			if (newDot > maxDot) {
				maxDot = newDot;
				supVec = vtx;
			}
		}
		VECTORS.release(vec, vtx);
		return out;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		int i;

		Vector3f vtx = VECTORS.get();
		float newDot;

		// JAVA NOTE: rewritten as code used W coord for temporary usage in Vector3
		// TODO: optimize it
		float[] wcoords = new float[numVectors];

		for (i = 0; i < numVectors; i++) {
			// TODO: used w in vector3:
			// supportVerticesOut[i].w = -1e30f;
			wcoords[i] = -1e30f;
		}

		for (int j = 0; j < numVectors; j++) {
			Vector3f vec = vectors[j];

			for (i = 0; i < getNumVertices(); i++) {
				getVertex(i, vtx);
				newDot = vec.dot(vtx);
				// if (newDot > supportVerticesOut[j].w)
				if (newDot > wcoords[j]) {
					// WARNING: don't swap next lines, the w component would get overwritten!
					supportVerticesOut[j].set(vtx);
					// supportVerticesOut[j].w = newDot;
					wcoords[j] = newDot;
				}
			}
		}
		VECTORS.release(vtx);
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		// not yet, return box inertia

		float margin = getMargin();

		Transform ident = TRANSFORMS.get();
		ident.setIdentity();
		Vector3f aabbMin = VECTORS.get(), aabbMax = VECTORS.get();
		getAabb(ident, aabbMin, aabbMax);

		Vector3f halfExtents = VECTORS.get();
		halfExtents.sub(aabbMax, aabbMin);
		halfExtents.scale(0.5f);

		float lx = 2f * (halfExtents.x + margin);
		float ly = 2f * (halfExtents.y + margin);
		float lz = 2f * (halfExtents.z + margin);
		float x2 = lx * lx;
		float y2 = ly * ly;
		float z2 = lz * lz;
		float scaledmass = mass * 0.08333333f;

		inertia.set(y2 + z2, x2 + z2, x2 + y2);
		inertia.scale(scaledmass);
		TRANSFORMS.release(ident);
		VECTORS.release(aabbMin, aabbMax, halfExtents);
	}

	/**
	 * Gets the nonvirtual aabb.
	 *
	 * @param trans the trans
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @param margin the margin
	 * @return the nonvirtual aabb
	 */
	private void getNonvirtualAabb(final Transform trans, final Vector3f aabbMin, final Vector3f aabbMax,
			final float margin) {
		// lazy evaluation of local aabb
		assert isLocalAabbValid;

		AabbUtil2.transformAabb(localAabbMin, localAabbMax, margin, trans, aabbMin, aabbMax);
	}

	@Override
	public void getAabb(final Transform trans, final Vector3f aabbMin, final Vector3f aabbMax) {
		getNonvirtualAabb(trans, aabbMin, aabbMax, getMargin());
	}

	/**
	 * Polyhedral convex shape get aabb.
	 *
	 * @param trans the trans
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 */
	protected final void _PolyhedralConvexShape_getAabb(final Transform trans, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		getNonvirtualAabb(trans, aabbMin, aabbMax, getMargin());
	}

	/**
	 * Recalc local aabb.
	 */
	public void recalcLocalAabb() {
		isLocalAabbValid = true;

		// #if 1

		batchedUnitVectorGetSupportingVertexWithoutMargin(_directions, _supporting, 6);

		for (int i = 0; i < 3; i++) {
			VectorUtil.setCoord(localAabbMax, i, VectorUtil.getCoord(_supporting[i], i) + collisionMargin);
			VectorUtil.setCoord(localAabbMin, i, VectorUtil.getCoord(_supporting[i + 3], i) - collisionMargin);
		}
	}

	@Override
	public void setLocalScaling(final Vector3f scaling) {
		super.setLocalScaling(scaling);
		recalcLocalAabb();
	}

	/**
	 * Gets the num vertices.
	 *
	 * @return the num vertices
	 */
	public abstract int getNumVertices();

	/**
	 * Gets the num edges.
	 *
	 * @return the num edges
	 */
	public abstract int getNumEdges();

	/**
	 * Gets the edge.
	 *
	 * @param i the i
	 * @param pa the pa
	 * @param pb the pb
	 * @return the edge
	 */
	public abstract void getEdge(int i, Vector3f pa, Vector3f pb);

	/**
	 * Gets the vertex.
	 *
	 * @param i the i
	 * @param vtx the vtx
	 * @return the vertex
	 */
	public abstract void getVertex(int i, Vector3f vtx);

	/**
	 * Gets the num planes.
	 *
	 * @return the num planes
	 */
	public abstract int getNumPlanes();

	/**
	 * Gets the plane.
	 *
	 * @param planeNormal the plane normal
	 * @param planeSupport the plane support
	 * @param i the i
	 * @return the plane
	 */
	public abstract void getPlane(Vector3f planeNormal, Vector3f planeSupport, int i);

	// public abstract int getIndex(int i) const = 0 ;

	/**
	 * Checks if is inside.
	 *
	 * @param pt the pt
	 * @param tolerance the tolerance
	 * @return true, if is inside
	 */
	public abstract boolean isInside(Vector3f pt, float tolerance);

}
