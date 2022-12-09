/*******************************************************************************************************
 *
 * PointCollector.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;

/**
 *
 * @author jezek2
 */
public class PointCollector implements DiscreteCollisionDetectorInterface.Result {

	/** The normal on B in world. */
	public final Vector3f normalOnBInWorld = new Vector3f();
	
	/** The point in world. */
	public final Vector3f pointInWorld = new Vector3f();
	
	/** The distance. */
	public float distance = 1e30f; // negative means penetration

	/** The has result. */
	public boolean hasResult = false;

	@Override
	public void setShapeIdentifiers(final int partId0, final int index0, final int partId1, final int index1) {
		// ??
	}

	public void addContactPoint( final Vector3f normalOnBInWorld, final Vector3f pointInWorld,
			final float depth) {
		if (depth < distance) {
			hasResult = true;
			this.normalOnBInWorld.set(normalOnBInWorld);
			this.pointInWorld.set(pointInWorld);
			// negative means penetration
			distance = depth;
		}
	}

}
