/*******************************************************************************************************
 *
 * ConcaveShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;

/**
 * ConcaveShape class provides an interface for non-moving (static) concave shapes.
 *
 * @author jezek2
 */
public abstract class ConcaveShape implements CollisionShape {

	/** The collision margin. */
	protected float collisionMargin = 0f;

	/**
	 * Process all triangles.
	 *
	 * @param callback the callback
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 */
	public abstract void processAllTriangles( TriangleCallback callback, Vector3f aabbMin,
			Vector3f aabbMax);

	@Override
	public float getMargin() {
		return collisionMargin;
	}

	@Override
	public void setMargin(final float margin) {
		this.collisionMargin = margin;
	}

}
