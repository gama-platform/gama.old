/*******************************************************************************************************
 *
 * BoxShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;
import static com.bulletphysics.Pools.VECTORS4;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.AabbUtil2;
import com.bulletphysics.linearmath.ScalarUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * BoxShape is a box primitive around the origin, its sides axis aligned with length specified by half extents, in local
 * shape coordinates. When used as part of a {@link CollisionObject} or {@link RigidBody} it will be an oriented box in
 * world space.
 *
 * @author jezek2
 */
public class BoxShape extends PolyhedralConvexShape {

	/**
	 * Instantiates a new box shape.
	 *
	 * @param boxHalfExtents the box half extents
	 */
	public BoxShape(final Vector3f boxHalfExtents) {
		Vector3f margin = new Vector3f(getMargin(), getMargin(), getMargin());
		VectorUtil.mul(implicitShapeDimensions, boxHalfExtents, localScaling);
		implicitShapeDimensions.sub(margin);
	}

	/**
	 * Gets the half extents with margin.
	 *
	 * @param out the out
	 * @return the half extents with margin
	 */
	public Vector3f getHalfExtentsWithMargin(final Vector3f out) {
		Vector3f halfExtents = getHalfExtentsWithoutMargin(out);
		Vector3f margin = VECTORS.get();
		margin.set(getMargin(), getMargin(), getMargin());
		halfExtents.add(margin);
		VECTORS.release(margin);
		return out;
	}

	/**
	 * Gets the half extents without margin.
	 *
	 * @param out the out
	 * @return the half extents without margin
	 */
	public Vector3f getHalfExtentsWithoutMargin(final Vector3f out) {
		out.set(implicitShapeDimensions); // changed in Bullet 2.63: assume the scaling and margin are included
		return out;
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.BOX_SHAPE_PROXYTYPE;
	}

	@Override
	public Vector3f localGetSupportingVertex(final Vector3f vec, final Vector3f out) {
		Vector3f halfExtents = getHalfExtentsWithoutMargin(out);

		float margin = getMargin();
		halfExtents.x += margin;
		halfExtents.y += margin;
		halfExtents.z += margin;

		out.set(ScalarUtil.fsel(vec.x, halfExtents.x, -halfExtents.x),
				ScalarUtil.fsel(vec.y, halfExtents.y, -halfExtents.y),
				ScalarUtil.fsel(vec.z, halfExtents.z, -halfExtents.z));
		return out;
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec, final Vector3f out) {
		Vector3f halfExtents = getHalfExtentsWithoutMargin(out);

		out.set(ScalarUtil.fsel(vec.x, halfExtents.x, -halfExtents.x),
				ScalarUtil.fsel(vec.y, halfExtents.y, -halfExtents.y),
				ScalarUtil.fsel(vec.z, halfExtents.z, -halfExtents.z));
		return out;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		Vector3f halfExtents = getHalfExtentsWithoutMargin(VECTORS.get());

		for (int i = 0; i < numVectors; i++) {
			Vector3f vec = vectors[i];
			supportVerticesOut[i].set(ScalarUtil.fsel(vec.x, halfExtents.x, -halfExtents.x),
					ScalarUtil.fsel(vec.y, halfExtents.y, -halfExtents.y),
					ScalarUtil.fsel(vec.z, halfExtents.z, -halfExtents.z));
		}
		VECTORS.release(halfExtents);
	}

	@Override
	public void setMargin(final float margin) {
		// correct the implicitShapeDimensions for the margin
		Vector3f oldMargin = VECTORS.get();
		oldMargin.set(getMargin(), getMargin(), getMargin());
		Vector3f implicitShapeDimensionsWithMargin = VECTORS.get();
		implicitShapeDimensionsWithMargin.add(implicitShapeDimensions, oldMargin);

		super.setMargin(margin);
		Vector3f newMargin = VECTORS.get();
		newMargin.set(getMargin(), getMargin(), getMargin());
		implicitShapeDimensions.sub(implicitShapeDimensionsWithMargin, newMargin);
		VECTORS.release(oldMargin, newMargin, implicitShapeDimensionsWithMargin);
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		Vector3f oldMargin = VECTORS.get();
		oldMargin.set(getMargin(), getMargin(), getMargin());
		Vector3f implicitShapeDimensionsWithMargin = VECTORS.get();
		implicitShapeDimensionsWithMargin.add(implicitShapeDimensions, oldMargin);
		Vector3f unScaledImplicitShapeDimensionsWithMargin = VECTORS.get();
		VectorUtil.div(unScaledImplicitShapeDimensionsWithMargin, implicitShapeDimensionsWithMargin, localScaling);

		super.setLocalScaling( scaling);

		VectorUtil.mul(implicitShapeDimensions, unScaledImplicitShapeDimensionsWithMargin, localScaling);
		implicitShapeDimensions.sub(oldMargin);
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		Vector3f temp = getHalfExtentsWithoutMargin(VECTORS.get());
		AabbUtil2.transformAabb(temp, getMargin(), t, aabbMin, aabbMax);
		VECTORS.release(temp);
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		// btScalar margin = btScalar(0.);
		Vector3f halfExtents = getHalfExtentsWithMargin(VECTORS.get());

		float lx = 2f * halfExtents.x;
		float ly = 2f * halfExtents.y;
		float lz = 2f * halfExtents.z;

		inertia.set(mass / 12f * (ly * ly + lz * lz), mass / 12f * (lx * lx + lz * lz),
				mass / 12f * (lx * lx + ly * ly));
		VECTORS.release(halfExtents);
	}

	@Override
	public void getPlane(final Vector3f planeNormal, final Vector3f planeSupport, final int i) {
		// this plane might not be aligned...
		Vector4f plane = VECTORS4.get();
		getPlaneEquation(plane, i);
		planeNormal.set(plane.x, plane.y, plane.z);
		Vector3f tmp = VECTORS.get();
		tmp.negate(planeNormal);
		localGetSupportingVertex(tmp, planeSupport);
		VECTORS4.release(plane);
		VECTORS.release(tmp);
	}

	@Override
	public int getNumPlanes() {
		return 6;
	}

	@Override
	public int getNumVertices() {
		return 8;
	}

	@Override
	public int getNumEdges() {
		return 12;
	}

	@Override
	public void getVertex(final int i, final Vector3f vtx) {
		Vector3f halfExtents = getHalfExtentsWithoutMargin(VECTORS.get());

		vtx.set(halfExtents.x * (1 - (i & 1)) - halfExtents.x * (i & 1),
				halfExtents.y * (1 - ((i & 2) >> 1)) - halfExtents.y * ((i & 2) >> 1),
				halfExtents.z * (1 - ((i & 4) >> 2)) - halfExtents.z * ((i & 4) >> 2));
		VECTORS.release(halfExtents);

	}

	/**
	 * Gets the plane equation.
	 *
	 * @param plane the plane
	 * @param i the i
	 * @return the plane equation
	 */
	public void getPlaneEquation(final Vector4f plane, final int i) {
		Vector3f halfExtents = getHalfExtentsWithoutMargin(VECTORS.get());

		switch (i) {
			case 0:
				plane.set(1f, 0f, 0f, -halfExtents.x);
				break;
			case 1:
				plane.set(-1f, 0f, 0f, -halfExtents.x);
				break;
			case 2:
				plane.set(0f, 1f, 0f, -halfExtents.y);
				break;
			case 3:
				plane.set(0f, -1f, 0f, -halfExtents.y);
				break;
			case 4:
				plane.set(0f, 0f, 1f, -halfExtents.z);
				break;
			case 5:
				plane.set(0f, 0f, -1f, -halfExtents.z);
				break;
			default:
				assert false;
		}
		VECTORS.release(halfExtents);

	}

	@Override
	public void getEdge(final int i, final Vector3f pa, final Vector3f pb) {
		int edgeVert0 = 0;
		int edgeVert1 = 0;

		switch (i) {
			case 0:
				edgeVert0 = 0;
				edgeVert1 = 1;
				break;
			case 1:
				edgeVert0 = 0;
				edgeVert1 = 2;
				break;
			case 2:
				edgeVert0 = 1;
				edgeVert1 = 3;

				break;
			case 3:
				edgeVert0 = 2;
				edgeVert1 = 3;
				break;
			case 4:
				edgeVert0 = 0;
				edgeVert1 = 4;
				break;
			case 5:
				edgeVert0 = 1;
				edgeVert1 = 5;

				break;
			case 6:
				edgeVert0 = 2;
				edgeVert1 = 6;
				break;
			case 7:
				edgeVert0 = 3;
				edgeVert1 = 7;
				break;
			case 8:
				edgeVert0 = 4;
				edgeVert1 = 5;
				break;
			case 9:
				edgeVert0 = 4;
				edgeVert1 = 6;
				break;
			case 10:
				edgeVert0 = 5;
				edgeVert1 = 7;
				break;
			case 11:
				edgeVert0 = 6;
				edgeVert1 = 7;
				break;
			default:
				assert false;
		}

		getVertex(edgeVert0, pa);
		getVertex(edgeVert1, pb);
	}

	@Override
	public boolean isInside(final Vector3f pt, final float tolerance) {
		Vector3f halfExtents = getHalfExtentsWithoutMargin(VECTORS.get());

		// btScalar minDist = 2*tolerance;

		boolean result = pt.x <= halfExtents.x + tolerance && pt.x >= -halfExtents.x - tolerance
				&& pt.y <= halfExtents.y + tolerance && pt.y >= -halfExtents.y - tolerance
				&& pt.z <= halfExtents.z + tolerance && pt.z >= -halfExtents.z - tolerance;
		VECTORS.release(halfExtents);

		return result;
	}

	@Override
	public String getName() {
		return "Box";
	}

	@Override
	public int getNumPreferredPenetrationDirections() {
		return 6;
	}

	@Override
	public void getPreferredPenetrationDirection(final int index, final Vector3f penetrationVector) {
		switch (index) {
			case 0:
				penetrationVector.set(1f, 0f, 0f);
				break;
			case 1:
				penetrationVector.set(-1f, 0f, 0f);
				break;
			case 2:
				penetrationVector.set(0f, 1f, 0f);
				break;
			case 3:
				penetrationVector.set(0f, -1f, 0f);
				break;
			case 4:
				penetrationVector.set(0f, 0f, 1f);
				break;
			case 5:
				penetrationVector.set(0f, 0f, -1f);
				break;
			default:
				assert false;
		}
	}

}
