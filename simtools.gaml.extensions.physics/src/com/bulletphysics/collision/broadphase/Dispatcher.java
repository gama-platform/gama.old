/*******************************************************************************************************
 *
 * Dispatcher.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import java.util.ArrayList;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.PersistentManifold;

/**
 * Dispatcher abstract class can be used in combination with broadphase to dispatch calculations for overlapping pairs.
 * For example for pairwise collision detection, calculating contact points stored in {@link PersistentManifold} or user
 * callbacks (game logic).
 *
 * @author jezek2
 */
public interface Dispatcher {

	/**
	 * Find algorithm.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return the collision algorithm
	 */
	default CollisionAlgorithm findAlgorithm(final CollisionObject body0, final CollisionObject body1) {
		return findAlgorithm(body0, body1, null);
	}

	/**
	 * Find algorithm.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param sharedManifold the shared manifold
	 * @return the collision algorithm
	 */
	CollisionAlgorithm findAlgorithm(CollisionObject body0, CollisionObject body1, PersistentManifold sharedManifold);

	/**
	 * Gets the new manifold.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return the new manifold
	 */
	PersistentManifold getNewManifold(Object body0, Object body1);

	/**
	 * Release manifold.
	 *
	 * @param manifold the manifold
	 */
	void releaseManifold(PersistentManifold manifold);

	// void clearManifold(PersistentManifold manifold);

	/**
	 * Needs collision.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return true, if successful
	 */
	boolean needsCollision(CollisionObject body0, CollisionObject body1);

	/**
	 * Needs response.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return true, if successful
	 */
	boolean needsResponse(CollisionObject body0, CollisionObject body1);

	/**
	 * Dispatch all collision pairs.
	 *
	 * @param pairCache the pair cache
	 * @param dispatchInfo the dispatch info
	 * @param dispatcher the dispatcher
	 */
	void dispatchAllCollisionPairs(OverlappingPairCache pairCache, DispatcherInfo dispatchInfo, Dispatcher dispatcher);

	/**
	 * Gets the num manifolds.
	 *
	 * @return the num manifolds
	 */
	int getNumManifolds();

	/**
	 * Gets the manifold by index internal.
	 *
	 * @param index the index
	 * @return the manifold by index internal
	 */
	PersistentManifold getManifoldByIndexInternal(int index);

	/**
	 * Gets the internal manifold pointer.
	 *
	 * @return the internal manifold pointer
	 */
	ArrayList<PersistentManifold> getInternalManifoldPointer();

	// public abstract Object allocateCollisionAlgorithm(int size);

	/**
	 * Free collision algorithm.
	 *
	 * @param algo the algo
	 */
	void freeCollisionAlgorithm(CollisionAlgorithm algo);

}
