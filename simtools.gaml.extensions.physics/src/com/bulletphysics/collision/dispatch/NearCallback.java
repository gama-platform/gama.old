/*******************************************************************************************************
 *
 * NearCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.DispatcherInfo;

/**
 * Callback for overriding collision filtering and more fine-grained control over
 * collision detection.
 * 
 * @see CollisionDispatcher#setNearCallback
 * @see CollisionDispatcher#getNearCallback
 * @author jezek2
 */
public abstract class NearCallback {

	/**
	 * Handle collision.
	 *
	 * @param collisionPair the collision pair
	 * @param dispatcher the dispatcher
	 * @param dispatchInfo the dispatch info
	 */
	public abstract void handleCollision(BroadphasePair collisionPair, CollisionDispatcher dispatcher, DispatcherInfo dispatchInfo);
	
}
