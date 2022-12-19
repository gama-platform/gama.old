/*******************************************************************************************************
 *
 * SphereShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
import com.bulletphysics.linearmath.Transform;

/**
 * SphereShape implements an implicit sphere, centered around a local origin with radius.
 *
 * @author jezek2
 */
public class SphereShape extends ConvexInternalShape {

	/**
	 * Instantiates a new sphere shape.
	 *
	 * @param radius the radius
	 */
	public SphereShape(final float radius) {
		implicitShapeDimensions.x = radius;
		collisionMargin = radius;
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec, final Vector3f out) {
		out.set(0f, 0f, 0f);
		return out;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		for (int i = 0; i < numVectors; i++) {
			supportVerticesOut[i].set(0f, 0f, 0f);
		}
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		Vector3f center = t.origin;
		Vector3f extent = VECTORS.get();
		extent.set(getMargin(), getMargin(), getMargin());
		aabbMin.sub(center, extent);
		aabbMax.add(center, extent);
		VECTORS.release(extent);
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.SPHERE_SHAPE_PROXYTYPE;
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		float elem = 0.4f * mass * getMargin() * getMargin();
		inertia.set(elem, elem, elem);
	}

	@Override
	public String getName() {
		return "SPHERE";
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public float getRadius() {
		return implicitShapeDimensions.x * localScaling.x;
	}

	@Override
	public void setMargin(final float margin) {
		super.setMargin(margin);
	}

	@Override
	public float getMargin() {
		// to improve gjk behaviour, use radius+margin as the full margin, so never get into the penetration case
		// this means, non-uniform scaling is not supported anymore
		return getRadius();
	}

}
